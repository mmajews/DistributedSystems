/* Sample TCP client */

#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <strings.h>
#include <unistd.h>
#include <sys/stat.h>

int BUFLEN = 10000;
char *separator = "%%%";

off_t fsize(const char *filename) {
    struct stat st;

    if (stat(filename, &st) == 0)
        return st.st_size;

    return -1;
}

char *subString(const char *input, int offset, int len, char *dest) {
    int input_len = (int) strlen(input);

    if (offset + len > input_len) {
        return NULL;
    }

    strncpy(dest, input + offset, (size_t) len);
    return dest;
}


int main(int argc, char **argv) {
    int sock_fd;
    struct sockaddr_in serv_addr;


    if (argc != 4) {
        printf("usage: %s <IP address> <TCP port> <FilePath>\n", argv[0]);
        exit(EXIT_FAILURE);
    }

    char *filePath = argv[3];
    BUFLEN = (int) fsize(filePath);
    char filePathWithSeparator[strlen(filePath) + 3];
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
        }
        fclose(fp);
    }

    char *finalVersion = strcat(toSend, fileContent);
//    printf("%s", finalVersion);
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

    int sizeToBeSend = (int) strlen(finalVersion);
    printf("Size of buffer to be send: %d\n", sizeToBeSend);
    write(sock_fd, &sizeToBeSend, sizeof(sizeToBeSend));

    // send buffer
    int toEmit = 1;
    int emitted = 0;
    for (int i = 0; i < sizeToBeSend; i += toEmit) {
        printf("Left to send: %d \n", sizeToBeSend - i);
        emitted = (int) write(fd, finalVersion + i, (size_t) toEmit);
        printf("Emitted: %d \n", emitted);
    }

    printf("Transfer completed.\n");
    return EXIT_SUCCESS;
}

