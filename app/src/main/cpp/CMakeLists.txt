cmake_minimum_required(VERSION 3.22.1)
project(kangaroo_mobile LANGUAGES CXX)
# The upstream CPU engine must be ported from x86 SSE assumptions to ARM64
# and validated before it can be linked into this Android target.
add_library(kangaroo_native SHARED native-lib.cpp)
target_compile_features(kangaroo_native PRIVATE cxx_std_17)
find_library(log-lib log)
target_link_libraries(kangaroo_native PRIVATE ${log-lib})
