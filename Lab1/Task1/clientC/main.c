#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <string.h>
#include <strings.h>
#include <arpa/inet.h>
#include <unistd.h>
int sock_fd;
int main(int argc, char **argv) {
    if (argc < 3) {
        printf("Wrong number of parameters \n 1.IP address\2 2.Port");
        printf("Exiting program...\n");
    }

    char *ipAddress = argv[1];
    int port = atoi(argv[2]);
    printf("Selected ip: %s:%d\n", ipAddress, port);

    printf("Select type of request: \n");
    printf("1.Integer 1 byte\n2.Integer 2 byte\n3.Integer 4 byte\n4.Integer 8 byte\n");
    int selectedOption = 0;
    scanf("%d", &selectedOption);

    struct sockaddr_in server_address;
    sock_fd = socket(AF_INET, SOCK_STREAM, 0);
    if (!sock_fd) {
        printf("Cannot create socket. Aborting..\n");
        return -1;
    }
    bzero(&server_address, sizeof(server_address));
    server_address.sin_family = AF_INET;
    server_address.sin_addr.s_addr = inet_addr(ipAddress);
    server_address.sin_port = htons((uint16_t) port);
    connect(sock_fd, (struct sockaddr *) &server_address, sizeof(server_address));


    if (selectedOption == 1) {
        write(sock_fd, &selectedOption, sizeof(selectedOption));
        uint8_t number = 0;
        printf("Enter number: ");
        int nNumber= 0;

        scanf("%d",&nNumber);
        number = (uint8_t) nNumber;
        if(number>255 || number < 0){
            printf("Number does not fit in 1 byte. Aborting..\n");
            return -1;
        }
        printf("Number to send %d\n",number);
        int len = (int) write(sock_fd, &number, sizeof(number));
    }
    else if (selectedOption == 2){
        write(sock_fd, &selectedOption, sizeof(selectedOption));
        uint16_t number = 0;
        printf("Enter number: ");
        scanf("%hu",&number);
        printf("Number to send: %hu\n",number);
        int len = (int) write(sock_fd, &number, sizeof(number));
    }
    else if (selectedOption == 3){
        int bytesToSend = 4;
        write(sock_fd, &bytesToSend, sizeof(bytesToSend));
        uint32_t number = 0;
        printf("Enter number: ");
        scanf("%u",&number);
        printf("Number to send %d\n",number);
        int len = (int) write(sock_fd, &number, sizeof(number));
    }
    else if(selectedOption == 4){
        int bytesToSend = 8;
        write(sock_fd, &bytesToSend, sizeof(bytesToSend));
        uint64_t number = 0;
        printf("Selected size: %lu \n",  sizeof(number));
        printf("Enter number: ");
        scanf("%lu",&number);
        printf("Number to send %lu\n", number);
        int len = (int) write(sock_fd, &number, sizeof(number));
    }



    printf("Sending completed!\nWaiting for response...\n");
    char response[1];
    recvfrom(sock_fd, &response, sizeof(response), 0, NULL, 0);
    printf("Nth digit of PI is %s\n",response);
    return 0;
}
