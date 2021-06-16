![Png](https://img.shields.io/badge/Platform-Windows%20x86--x64-green)
![Png](https://img.shields.io/badge/Latest%20release-1.6.4-green)
![GitHub](https://img.shields.io/badge/License-GPL--2.0%20or%20later-green)
![GitHub All Releases](https://img.shields.io/github/downloads/dylwedma11748/JTegraNX/total)
![GitHub repo size](https://img.shields.io/github/repo-size/dylwedma11748/JTegraNX)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fdylwedma11748%2FJTegraNX.svg?type=small)](https://app.fossa.com/projects/git%2Bgithub.com%2Fdylwedma11748%2FJTegraNX?ref=badge_small)
###
![Png](src/ui/images/banner.png)
# JTegraNX - Another RCM payload injector

In order to run this program, you need Java 8 or higher installed on your computer. You can download Java [here](https://www.java.com/en/).

JTegraNX will not run on Java 11 or higher due to JavaFX not being included. Anyone who wishes to use Java 11 or higher with JTegraNX will need to build from source.

JTegraNX is currently only supported on Windows, but I plan to add support for Linux and macOS.

## Preview
![Png](Preview.png)

## Features
- RCM payload injection
- Auto-injection
- RCM status indicator with transparent images
- Config system (similar to favorites in [TegraRcmGUI](https://github.com/eliboa/TegraRcmGUI))
- Bundled payloads
- Tray icon with functionality
- SD card preparation
- APX driver detection/installation
- Portable mode

## Usage
1. Choose payload or config
2. Inject

## Using configs
The config system allows you to save what you entered in the "Payload Path" field and load it again in another session.

### Loading a saved config:
Simply click on "Load Config" to reveal the config list, then select the config you wish to use.

### Saving a config:
Simple click on "Save Config", input a name for the config and hit enter.

## Custom Settings
You can now customize JTegraNX's settings to your liking.
- You can now toggle auto-checking for JTegraNX updates.
- You can now toggle auto-checking for payload updates.
- You can now choose which payloads you want to include with JTegraNX.
- You can now toggle the tray icon being enabled or disabled.

## SD Card preparation
This new feature with JTegraNX will download all the basic requirements for getting your Switch ready for CFW and copy them to the specified output path.

## APX driver detection/installation
JTegraNX can detect if the APX driver is missing or incorrect, and if this is the case, you have the option to install it from there.

## Portable mode
JTegraNX's old behavior for handling data files has been re-implemented as a secondary option. Portable mode make it to where the "Payloads" directory and the main config file are created in the working directory of where the JAR is executed. You can switch between standard and portable mode using the Settings menu.

## Bundled Payloads
For any bundled payloads, JTegraNX will download them automatically, place them in the "Payloads" directory, and optionaly check for updates on them each time the program is launched. If updates are enabled and an update for a payload is found, it will be taken care of.

### Currently bundled payloads:
- [TegraExplorer](https://github.com/suchmememanyskill/TegraExplorer)
- [Lockpick_RCM](https://github.com/shchmue/Lockpick_RCM)
- [fusee-primary](https://github.com/Atmosphere-NX/Atmosphere/releases)
- [Hekate](https://github.com/CTCaer/hekate)

## Bulding from source
JTegraNX in it's current state is made of two projects, a Netbeans project that has all the Java bits and a Visual Studio 2019 solution that handles the native code.

Creating the Netbeans project isn't that complicated.
1. Create a new Netbeans Java project and name it JTegraNX.
2. Download [usb4java](https://github.com/usb4java/usb4java/releases), [Apache Commons IO](https://commons.apache.org/proper/commons-io/), and [GitHandler](https://github.com/dylwedma11748/GitHandler/releases).
3. Extract the usb4java and Apache Commons Lang archives.
4. On Netbeans, Projects -> JTegraNX -> Libraries, right-click and select "Add JAR/Folder" and add "usb4java-version.jar", "commons-lang3-version.jar", the usb4java native JAR that corresponds with your OS architecture (Located where you extracted the usb4java archive), "commons-io-version.jar" (Located where you extracted the Apache Commons IO archive), and GitHandler.jar (Located wherever you saved GitHandler when you downloaded it).
5. Add the Java source files from this repository.
6. Now would be a good time to follow the instructions for the Visual Studio Solution.
7. Copy libusbK.dll from the libusbK-dev-kit to the directory of the Netbeans project.
8. Do a test run to make sure everything is running correctly.
9. If everything is working properly build the project.
10. Download [jarsplice](http://www.java2s.com/Code/Jar/j/Downloadjarsplice040jar.htm) and use it to combine the main JAR file, the library JAR files, the compiled Native and libusbK.dll into one executable JAR file.

To make changes to the UI, open MainUI.fxml using [Scene Builder](https://gluonhq.com/products/scene-builder/).

As for the Native, this is where things may get complicated.
The libusbk-dev-kit is required for building the native. Download it [here](https://sourceforge.net/projects/libusbk/files/libusbK-release/3.0.7.0/).

1. Create a new Visual Studio Solution (Dynamic Link Library) and name it JTegraNX.
2. Open the project's properties -> C/C++ -> Precompiled headers, and set "Precompiled Header" to "Not Using Precompiled Headers" and clear the "Precompiled Header File" field.
3. In C/C++ -> General -> Additional Include Directories, add the include directories for JNI ((JDK Path)\include and (JDK Path\include\win32)) and libusbK (default is C:\libusbK-dev-kit\includes) and apply.
4. Switch platform to x64.
5. In Linker -> General -> Additional Library Directories, add C:\libusbK-dev-kit\bin\dll\amd64 if that's where you installed the libusbK-dev-kit and apply.
6. Switch platform to Win32.
7. In Linker -> General -> Additional Library Directories, add C:\libusbK-dev-kit\bin\lib\x86 if that's where you installed the libusbK-dev-kit and apply.
8. Switch platform to All Platforms.
9. In Configuration Properties -> General -> Output Directory, set the output directory to where the Netbeans project has been created, (Example: C:\Users\user\Documents\NetBeansProjects\JTegraNX) and apply. This step is optional but makes it to where you won't have to copy the compiled native to the Netbeans project directory every time you build it.
10. In Linker -> Input -> Additional Dependencies, add SetupAPI.lib and libusbK.lib.
11. Delete "framework.h", "pch.h", "dllmain.ccp", and "pch.cpp" from the project.
12. Copy the Native's soruce files from this repository to the directory of the Visual Studio Project.
13. Copy libusbK.dll from the libusbK-dev-kit to the directory of the Visual Studio Project.
14. In the Soluition Explorer, right click on Header Files and select Add -> Exisiting Item... and add all the header files.
15. In the Soluition Explorer, right click on Source Files and select Add -> Exisiting Item... and add all the source files.
16. Build the Release configuration corresponding to your system's OS architecture.

# Credits
- [suchmememanyskill](https://github.com/suchmememanyskill) for [TegraExplorer](https://github.com/suchmememanyskill/TegraExplorer).
- [rajkosto](https://github.com/rajkosto) for [memloader](https://github.com/rajkosto/memloader) and [TegraRcmSmash](https://github.com/rajkosto/TegraRcmSmash) (No longer being used in v1.6+).
- [shchmue](https://github.com/shchmue) for [Lockpick_RCM](https://github.com/shchmue/Lockpick_RCM).
- [eliboa](https://github.com/eliboa) for the images from [TegraRcmGUI](https://github.com/eliboa/TegraRcmGUI).
- The creators of Atmosphère for [fusee-primary](https://github.com/Atmosphere-NX/Atmosphere/releases).
- [CTCaer](https://github.com/CTCaer) for [Hekate](https://github.com/CTCaer/hekate).
