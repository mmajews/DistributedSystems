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