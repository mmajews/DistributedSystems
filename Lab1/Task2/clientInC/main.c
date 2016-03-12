/* Sample TCP client */

#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <strings.h>

#define BUFLEN 10000
char *separator = "%%%";

int main(int argc, char **argv) {
    int sock_fd;
    int len;
    struct sockaddr_in serv_addr;
    char sendline[] = "PING";
    char recvline[BUFLEN];

    if (argc != 4) {
        printf("usage: %s <IP address> <TCP port> <FilePath>\n", argv[0]);
        exit(EXIT_FAILURE);
    }

    char *filePath = argv[3];
    char filePathWithSeparator[sizeof(filePath) + 3];
    strcpy(filePathWithSeparator, filePath);
    strcat(filePathWithSeparator, separator);
    char fileContent[BUFLEN + 1];
    char toSend[BUFLEN + 1 + sizeof(filePathWithSeparator)];
    strcpy(toSend, filePathWithSeparator);

    FILE *fp = fopen(filePath, "r");
    int fd = fileno(fp);
    strcpy(fileContent, filePath);
    if (fp != NULL) {
        size_t newLen = fread(fileContent, sizeof(char), BUFLEN, fp);
        if (newLen == 0) {
            fputs("Error reading file", stderr);
        } else {
            fileContent[newLen++] = '\0';
        }
        fclose(fp);
    }

    char *finalVersion = strcat(toSend, fileContent);
    printf("%s", finalVersion);

    // create the socket (add missing arguments)
    sock_fd = socket((AF_INET), (SOCK_STREAM), (0));
    if (!sock_fd) {
        perror("socket");
        exit(EXIT_FAILURE);
    }

    bzero(&serv_addr, sizeof(serv_addr));
    // fill in the socket family, address and port
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_addr.s_addr = inet_addr(argv[1]);
    serv_addr.sin_port = htons((uint16_t) atoi(argv[2]));

    // establish the connection (SYN, SYN+ANK, ACK) with "connect" procedure
    connect(sock_fd, (struct sockaddr *) &serv_addr, sizeof(serv_addr));

    // send sendline buffer with the "send" system call and assign number of sent bytes to len
    len = (int) send(sock_fd, finalVersion, strlen(finalVersion), 0);
    printf("sent bytes: %d\n", len);

    // receive data to recvline buffer with the "recv" system call and assign number of received bytes to len
    len = (int) recv(sock_fd, recvline, BUFLEN, 0);
    printf("received bytes: %d\n", len);
    recvline[len] = 0;
    printf("received: %s\n", recvline);

    return EXIT_SUCCESS;
}

