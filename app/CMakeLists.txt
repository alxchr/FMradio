cmake_minimum_required(VERSION 3.4.1)
add_library( # Specifies the name of the library.
             serial-port

             # Sets the library as a shared library.
             SHARED
             # Provides a relative path to your source file(s).

         src/main/jni/SerialPort.c)

find_library( log-lib
        log )

target_link_libraries(serial-port
        ${log-lib})
