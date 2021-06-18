# Compiler
CC=g++
# Flags
CFLAGS=-O2
MKDIR_P = mkdir -p
APP_NAME = JTegraNX.so

all: x86 amd64

x86:
	$(MKDIR_P) ./x86
	$(CC) ${CFLAGS} -m32 -c -fPIC -I"/home/dylan/jdk1.8.0_111/include" -I"/home/dylan/jdk1.8.0_111/include/linux" native.cpp -o native_x86.o
	$(CC) ${CFLAGS} -m32 -shared -fPIC -o ./x86/${APP_NAME} native_x86.o -lc

amd64:
	$(MKDIR_P) ./amd64
	$(CC) ${CFLAGS} -m64 -c -fPIC -I"/home/dylan/jdk1.8.0_111/include" -I"/home/dylan/jdk1.8.0_111/include/linux" native.cpp -o native_amd64.o
	$(CC) ${CFLAGS} -m64 -shared -fPIC -o ./amd64/${APP_NAME} native_amd64.o -lc

clean:
	rm -rf \
		native_amd64.o \
		native_x86.o \
		./x86 \
		./amd64 
