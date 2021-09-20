cmake_minimum_required(VERSION 3.16.3)
project (internship_counter)

set(CMAKE_CXX_STANDARD 17)

include_directories(src)
add_executable(counter_test src/test.cpp)
TARGET_LINK_LIBRARIES(counter_test pthread)