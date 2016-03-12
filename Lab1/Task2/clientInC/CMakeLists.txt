cmake_minimum_required(VERSION 3.3)
project(clientInC)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall -Werror  -std=gnu99")

set(SOURCE_FILES main.c)
add_executable(clientInC ${SOURCE_FILES})