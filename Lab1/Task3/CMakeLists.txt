cmake_minimum_required(VERSION 3.2)
project(zad01)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall -Werror  -std=gnu99 -pthread")

set(SOURCE_FILES client.c common.h server.c)
add_executable(zad02 ${SOURCE_FILES})