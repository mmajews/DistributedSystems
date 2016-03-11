#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <unistd.h>
#include <sys/un.h>
#include <sys/select.h>
#include <sys/time.h>
#include <signal.h>
#include "common.h"
#include <errno.h>

#define MAXIMUM_NUMBER_OF_CLIENTS 100
#define MAXIMUM_SECONDS_OF_INACTIVITY 100
fd_set fds;
struct clientProfile clients[MAXIMUM_NUMBER_OF_CLIENTS];
int clientsConnected = 0;

int checkIfRegistered(char *nick);

int checkIfActive(char *nick);

int getNumber(char *nick);

int socketInternet;

void disactivateUsers();

void sendToAll(struct msg msg1);

void setTimeStamp(int index);

void handleInternet(struct msg msg1, struct sockaddr_in other);

void intHandler(int signo) {
    printf("Bye!\n");
    close(socketInternet);
    exit(EXIT_SUCCESS);
}

void atexit_function(void) {
    intHandler(1);
    exit(EXIT_SUCCESS);
}

int main(int argc, char *argv[]) {
    atexit(atexit_function);
    if (signal(SIGINT, intHandler) == SIG_ERR) {
        perror("signal():");
        exit(EXIT_FAILURE);
    }

    for (int i = 0; i < MAXIMUM_NUMBER_OF_CLIENTS; i++) {
        memset(&clients[i], '0', sizeof(clients[i]));
        clients[i].lastActivity = -1;
    }

    struct sockaddr_in selfInternet, other;

    //DEFINING INTERNET SERVER
    int len = sizeof(struct sockaddr_in);
    int n, port;
    if (argc < 3) {
        printf("Bad arguments\n");
        printf("Arguments to provide: \n1.Port\n2.IP Adress\n");
        return 1;
    }

    if ((socketInternet = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP)) == -1) {
        perror("socket");
        return 1;
    }

    port = atoi(argv[1]);
    memset((char *) &selfInternet, 0, sizeof(struct sockaddr_in));
    selfInternet.sin_family = AF_INET;
    selfInternet.sin_port = htons(port);
    selfInternet.sin_addr.s_addr = inet_addr(argv[2]);//htonl(INADDR_ANY);
    if (bind(socketInternet, (struct sockaddr *) &selfInternet, sizeof(selfInternet)) == -1) {
        perror("bind()");
        return 1;
    }

    //defining FD_SET
    FD_ZERO(&fds);
    FD_SET(socketInternet, &fds);
    struct msg msg1;
    memset(msg1.buf, '\0', sizeof(msg1.buf));
    memset(msg1.nick, '\0', sizeof(msg1.nick));

    printf("Server is up and running!\n");
    //RECEIVING
    while (1) {
        FD_SET(socketInternet, &fds);
        select(socketInternet + 1, &fds, NULL, NULL, NULL);
        if (FD_ISSET(socketInternet, &fds)) {
            int n_int = (int) recvfrom(socketInternet, (void *) &msg1, sizeof(struct msg), MSG_DONTWAIT,
                                       (struct sockaddr *) &other, &len);
            if (n_int < 0 && errno != EAGAIN && errno != EWOULDBLOCK) {
                perror("recvfrom():");
            }
            else if (n_int > 0) {
                handleInternet(msg1, other);
            }
        }

        FD_ZERO(&fds);
        disactivateUsers();
    }
}

void handleInternet(struct msg msg1, struct sockaddr_in other) {
    long calculatedHash = getHashFromStruct(&msg1);
    if (calculatedHash != msg1.controlSum) {
        printf("Calculated: %ld, in message: %ld \n", calculatedHash, msg1.controlSum);
        printf("Calculated control sum is not correct. Corrupted message. Aborting...\n");
        return;
    }

    if (checkIfRegistered(msg1.nick) == 0) {
        printf("User %s not registered!\n", msg1.nick);
        if (strncmp(msg1.buf, "%R%", 3) != 0) {
            return;
        }
        printf("Registering user %s!\n", msg1.nick);

        strcpy(clients[clientsConnected].nick, msg1.nick);
        clients[clientsConnected].active = 1;
        clients[clientsConnected].inter = other;
        clients[clientsConnected].type = INTERNET;
        setTimeStamp(clientsConnected);
        clientsConnected++;
    }
    else {
        int index = getNumber(msg1.nick);
        if (checkIfActive(msg1.nick)) {
            printf("%s\n", msg1.buf);
            setTimeStamp(index);
            sendToAll(msg1);
        }
        else {
            if (strncmp(msg1.buf, "%R%", 3) == 0) {
                clients[index].active = 1;
                setTimeStamp(index);
            }
        }
    }
}

int checkIfRegistered(char *nick) {
    for (int i = 0; i < clientsConnected; i++) {
        if (strncmp(clients[i].nick, nick, strlen(nick)) == 0) {
            return 1;
        }
    }
    return 0;
}

int checkIfActive(char *nick) {
    for (int i = 0; i < clientsConnected; i++) {
        if (strcmp(clients[i].nick, nick) == 0) {
            if (clients[i].active == 1) {
                return 1;
                break;
            }
        }
    }
    return 0;
}

int getNumber(char *nick) {
    for (int i = 0; i < clientsConnected; i++) {
        if (strcmp(clients[i].nick, nick) == 0) {
            return i;
        }
    }
    return -1;
}

void sendToAll(struct msg msg1) {
    printf("Sending to all..\n");
    int rc;
    for (int i = 0; i < clientsConnected; i++) {
        if (clients[i].active == 0) {
            continue;
        }
        rc = (int) sendto(socketInternet, (void *) &msg1, sizeof(struct msg), 0,
                          (struct sockaddr *) &clients[i].inter, sizeof(struct sockaddr_in));
        if (rc < 0) {
            perror("sendto():");
        }
    }
}

void setTimeStamp(int index) {
    struct timeval tv;
    if (gettimeofday(&tv, NULL) == -1) {
        perror("gettimeofday():");
        return;
    }
    clients[index].lastActivity = tv.tv_sec;
}

void disactivateUsers() {
    struct timeval tv;
    if (gettimeofday(&tv, NULL) == -1) {
        perror("gettimeofday():");
        return;
    }
    long currentSec = tv.tv_sec;
    for (int i = 0; i < clientsConnected; i++) {
        if (currentSec - clients[i].lastActivity > MAXIMUM_SECONDS_OF_INACTIVITY) {
            clients[i].active = 0;
        }
    }
}