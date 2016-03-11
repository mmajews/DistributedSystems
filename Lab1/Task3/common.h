#define MAX_MESSAGE_LENGTH 12
#define MAX_NICK_LENGTH 12
#define TIME_LENGTH 25
#define INTERNET 666
#define LOCAL_S 667
#include <sys/un.h>
struct msg
{
    char buf[MAX_MESSAGE_LENGTH];
    char nick[MAX_NICK_LENGTH];
    char time[TIME_LENGTH];
    long controlSum;
    int sock;
};

struct clientProfile
{
    struct sockaddr_in inter;
    char nick[MAX_NICK_LENGTH ];
    long lastActivity;
    int active;
    int type;
    int sock;
};

long getHashFromStruct(struct msg *toBeHashed) {
    long hash = 0;
    hash += toBeHashed->sock;
    for (int i = 0; i < MAX_NICK_LENGTH; i++) {
        hash += toBeHashed->nick[i];
    }
    for (int i = 0; i < MAX_MESSAGE_LENGTH; i++) {
        hash += toBeHashed->buf[i];
    }
    for (int i = 0; i < TIME_LENGTH; i++) {
        hash += toBeHashed->time[i];
    }
    return hash;
}