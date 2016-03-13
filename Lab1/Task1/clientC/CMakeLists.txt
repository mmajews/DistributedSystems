cmake_minimum_required(VERSION 3.3)
project(clientC)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES main.c)
add_executable(clientC ${SOURCE_FILES})