#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <arpa/inet.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>
#include <unistd.h>
#include <pthread.h>
#include <sys/select.h>
#include <signal.h>
#include "common.h"

int socketInternet;
int selfSocket;
struct sockaddr_in server;
struct sockaddr_un serverLocal;
fd_set fds;

char *getCurrentTime(char * output);

void receiver(void *ptr);

void writer(void *ptr);

char *nick;

void intHandler(int signo) {
    printf("Bye!");
    close(selfSocket);
    exit(EXIT_SUCCESS);
}

void atexit_function(void) {
    intHandler(1);
}

int main(int argc, char *argv[]) {
    signal(SIGINT, intHandler);
    atexit(atexit_function);
    int len = sizeof(struct sockaddr_in);
    char buf[MAX_MESSAGE_LENGTH];
    struct hostent *host;
    int n, port;

    /*
     * 1-nick
     * 2-host
     * 3-port
     */

    if (argc < 4) {
        printf("Wrong number of parameters!\n");
        printf("Correct parameters: \n1.Nick\n2.Host\n3.Port\n");
    }

    nick = argv[1];
    printf("Your nick: %s\n", nick);

    host = gethostbyname(argv[2]);
    if (host == NULL) {
        perror("gethostbyname");
        return 1;
    }

    port = atoi(argv[3]);

    /* initialize socket */
    if ((socketInternet = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP)) == -1) {
        perror("socket");
        return 1;
    }

    /* initialize server addr */
    memset((char *) &server, 0, sizeof(struct sockaddr_in));
    server.sin_family = AF_INET;
    server.sin_port = htons(port);
    server.sin_addr = *((struct in_addr *) host->h_addr_list[0]);

    pthread_t receiverThread;
    pthread_t writerThread;

    pthread_create(&receiverThread, NULL, (void *) receiver, NULL);
    pthread_create(&writerThread, NULL, (void *) writer, NULL);
    pthread_join(receiverThread, NULL);
    pthread_join(writerThread, NULL);

    close(socketInternet);
    return 0;
}

void writer(void *ptr) {
    struct msg msg1;
    memset(msg1.buf, '\0', sizeof(msg1.buf));
    memset(msg1.nick, '\0', sizeof(msg1.nick));
    strcpy(msg1.nick, nick);
    msg1.sock = selfSocket;

    //Register User
    strcpy(msg1.buf, "%R%");
    if (sendto(socketInternet, (void *) &msg1, sizeof(struct msg), 0, (struct sockaddr *) &server,
               sizeof(struct sockaddr_in)) == -1) {
        perror("sendto()");
        pthread_exit((void *) EXIT_FAILURE);
    }

    while (1) {
        fgets(msg1.buf, 256, stdin);
        char output[TIME_LENGTH];
        getCurrentTime(output);
        strcpy(msg1.time, output);
        printf("Current time %s",output);
        if (sendto(socketInternet, (void *) &msg1, sizeof(struct msg), 0, (struct sockaddr *) &server,
                   sizeof(struct sockaddr_in)) == -1) {
            perror("sendto()");
            pthread_exit((void *) EXIT_FAILURE);
        }

    }
}

void receiver(void *ptr) {
    struct msg msg1;
    memset(msg1.buf, '\0', sizeof(msg1.buf));
    memset(msg1.nick, '\0', sizeof(msg1.nick));

    FD_ZERO(&fds);
    FD_SET(socketInternet, &fds);
    while (1) {
        select(socketInternet + 1, &fds, NULL, NULL, NULL);
        recvfrom(socketInternet, (void *) &msg1, sizeof(struct msg), 0, NULL, 0);
        if (strcmp(msg1.nick, nick) == 0) {
            continue;
        }
        printf("Nick:%s\tMessage:%s\n", msg1.nick, msg1.buf);
    }
}


char *getCurrentTime(char *output) {
    memset(output, '\0', sizeof(output));
    time_t rawtime;
    struct tm *timeinfo;

    time(&rawtime);
    timeinfo = localtime(&rawtime);

    sprintf(output, "[%d %d %d %d:%d:%d]", timeinfo->tm_mday, timeinfo->tm_mon + 1, timeinfo->tm_year + 1900,
            timeinfo->tm_hour, timeinfo->tm_min, timeinfo->tm_sec);
    return output;
}
