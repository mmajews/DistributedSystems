CC		= @gcc
CFLAGS	= -Wall -std=c99

OBJS = main.o
TARGETS = main

all : clean compile

compile:
	gcc -pthread -D_POSIX_C_SOURCE -std=c99 -o main main.c

clean:
	@rm -f *.o $(TARGETS)
