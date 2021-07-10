# Building instructions
Cloning JTegraNX includes everything you need to build it, all you need is Maven.
## Building from source
1. Clone JTegraNX.
2. Open a command prompt or terminal window and navigate to where JTegraNX was cloned.
3. Run `mvn -B -DskipTests clean package`.

## Bulding the natives
You only need to build the natives if you've made changes to their code.

### Windows natives
The Windows natives are made from a Visual Studio 2019 project linked with [libusbK 3.0.7.0](https://sourceforge.net/projects/libusbk/files/libusbK-release/3.0.7.0/).
1. Create a new Visual Studio Solution (Dynamic Link Library) and name it JTegraNX.
2. Open the project's properties -> C/C++ -> Precompiled headers, and set "Precompiled Header" to "Not Using Precompiled Headers" and clear the "Precompiled Header File" field.
3. In C/C++ -> General -> Additional Include Directories, add the include directories for JNI ((JDK Path)\include and (JDK Path\include\win32)) and libusbK (default is C:\libusbK-dev-kit\includes) and apply.
4. Switch platform to x64.
5. In Linker -> General -> Additional Library Directories, add C:\libusbK-dev-kit\bin\dll\amd64 if that's where you installed the libusbK-dev-kit and apply.
6. Switch platform to Win32.
7. In Linker -> General -> Additional Library Directories, add C:\libusbK-dev-kit\bin\lib\x86 if that's where you installed the libusbK-dev-kit and apply.
8. Switch platform to All Platforms.
9. In Linker -> Input -> Additional Dependencies, add SetupAPI.lib and libusbK.lib.
10. Delete "framework.h", "pch.h", "dllmain.ccp", and "pch.cpp" from the project.
11. Copy the Native's source files from this repository to the directory of the Visual Studio Project.
12. Copy libusbK.dll from the libusbK-dev-kit to the directory of the Visual Studio Project.
13. In the Solution Explorer, right click on Header Files and select Add -> Existing Item... and add all the header files.
14. In the Solution Explorer, right click on Source Files and select Add -> Existing Item... and add all the source files.
15. Build the Release configuration of both platforms.
16. Copy the compiled x86 native to `src\main\resources\native\windows` and rename it to "JTegraNX_x86.dll".
17. Copy the compiled x64 native to `src\main\resources\native\windows` and rename it to "JTegraNX_x64.dll".

### Linux natives
1. Edit the makefile to have the proper paths for JNI.
2. Run `make`.
3. Copy the compiled x86 native to `src\main\resources\native\linux` and rename it to "JTegraNX_x86.so".
4. Copy the compiled x64 native to `src\main\resources\native\linux` and rename it to "JTegraNX_x64.so".

Mac OS X doesn't need a native since fusee-gelee works out of the box with it.
