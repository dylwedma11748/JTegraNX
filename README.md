![Png](https://img.shields.io/badge/Platforms-Windows%20x86--x64%20Linux%20x86--x64%20Mac%20OS%20X%20x86--x64-green)
![Png](https://img.shields.io/badge/Latest%20release-1.6.9-green)
![GitHub](https://img.shields.io/badge/License-GPL--2.0%20or%20later-green)
![GitHub All Releases](https://img.shields.io/github/downloads/dylwedma11748/JTegraNX/total)
![GitHub repo size](https://img.shields.io/github/repo-size/dylwedma11748/JTegraNX)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fdylwedma11748%2FJTegraNX.svg?type=small)](https://app.fossa.com/projects/git%2Bgithub.com%2Fdylwedma11748%2FJTegraNX?ref=badge_small)
###
![Png](src/main/resources/images/banner.png)
# JTegraNX - Another RCM payload injector

In order to run this program, you need Java 8 or higher installed on your computer. You can download Java [here](https://www.java.com/en/).

JTegraNX is supported on Windows, Linux, and Mac OS X.

## Preview
![image](https://user-images.githubusercontent.com/32218999/125573024-25bb1802-c9df-40fe-9419-1857b755e5a9.png)

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
- Command line mode
- GPT restoration

## Usage
Normal mode:
- Launch JTegraNX.jar normally to use the UI mode. Then select a payload and inject or use the other features if needed.

Command line mode:
- Launch JTegraNX.jar from the command line like this:
  - `java -jar JTegraNX.jar -cml`

## Linux support
Linux support has been added in v1.6.6, to use JTegraNX on Linux some preperation may be required.
- JTegraNX on Linux must either be run as root or with udev rules configured. To configure your udev rules, follow [this guide](https://nh-server.github.io/switch-guide/extras/adding_udev/).
- Payload injection on Linux requires the RCM device connecting to a port handled by xhci_hcd or a port with a patched EHCI driver.
  - My way of getting past this was creating an Ubuntu 21.04 VM in VMware with USB 3.1 enabled.

## Mac OS X support
Mac OS X support has been added in v1.6.7, some things to take note on though.
- While JTegraNX's core functionality worked in my testing on a VM, I ran into some issues. You may or may not encounter these.
  - If you try to use the browse payload function or prepare SD card function and it fails, open JTegraNX using the terminal and check for errors. If you recieve this error or something similar to it:
    - `java[2748:110752] +[NSXPCSharedListener endpointForReply:withListenerName:]: an error occurred while attempting to obtain endpoint for listener 'com.apple.view-bridge': Connection interrupted`
    - This error occures when trying to open the FileChooser or DirectoryChooser respectively, I don't know what's causing it but I do know that it's an issue with Mac OS X, not JTegraNX. I've seen on a thread that someone encountered the same kind of issue while using OBS Studio on Mac OS X. For me it could be that I was running Catalina in a VM but I'm not sure because I don't own a Mac.
  - When a notification gets sent to the tray, it doesn't show JTegraNX's icon. While a minor issue, I'd perfer it did.

- JTegraNX on Mac OS X doesn't require any prior set up and *should* work out of the box. No drivers, no patches, nothing. Just download and go.

## Command line mode
Command line mode is a new feature added in v1.6.7. This mode has most of JTegraNX's functionality stuffed into the command line.
- To access command line mode, run JTegraNX.jar like this:
  - `java -jar JTegraNX.jar -cml`

These features are not usable in command line mode:
- Device listeners (JTegraNX's inject function already checks for the device anyway).
  - Becuase of this, Auto-inject won't work either.
- Changing configuration mode from standard to portable or vice versa.
- Browsing for payloads using a dialog (it *is* a command line).
- Restarting JTegraNX after an update.
  - This is due to how the restart function works.
    - It gets the running JAR file's path and executes it using `Runime.getRuntime().exec()`. In theory, I could launch command line mode in Runtime but I would then need to simultaneously have the Input, Output, and maybe even Error streams being monitored and used, but doing that while running a JVM inside a JVM would be very tedious. So I opted out of it.
 - Config system (Other than setting updates or bundled payloads).
 - Tray icon (again, it's a command line).

## Using configs
The config system allows you to save what you entered in the "Payload Path" field and load it again in another session.

### Loading a saved config:
Simply click on "Load Config" to reveal the config list, then select the config you wish to use.

### Saving a config:
Simply click on "Save Config", input a name for the config, and hit enter.

## Custom Settings
You can now customize JTegraNX's settings to your liking.
- You can now toggle auto-checking for JTegraNX updates.
- You can now toggle auto-checking for payload updates.
- You can now choose which payloads you want to include with JTegraNX.
- You can now toggle the tray icon being enabled or disabled.

## SD Card preparation
This new feature with JTegraNX will download all the basic requirements for getting your Switch ready for CFW and copy them to the specified output path.

## GPT restoration
This new feature added in v1.6.9 will inject [gptrestore](https://github.com/rajkosto/gptrestore), which is now bundled with JTegraNX as a standard tool instead of a bundled payload.

## APX driver detection/installation
JTegraNX can detect if the APX driver is missing or incorrect, and if this is the case, you have the option to install it from there.

## Portable mode
JTegraNX's old behavior for handling data files has been re-implemented as a secondary option. Portable mode make it to where the "Payloads" directory and the main config file are created in the working directory of where the JAR is executed. You can switch between standard and portable mode using the Settings menu.

## Bundled Payloads
For any bundled payloads, JTegraNX will download them automatically, place them in the "Payloads" directory, and optionally check for updates on them each time the program is launched. If updates are enabled and an update for a payload is found, it will be taken care of.

### Currently bundled payloads:
- [fusee-primary](https://github.com/Atmosphere-NX/Atmosphere/releases)
- [Hekate](https://github.com/CTCaer/hekate)
- [Lockpick_RCM](https://github.com/shchmue/Lockpick_RCM)
- [TegraExplorer](https://github.com/suchmememanyskill/TegraExplorer)

# Credits
- [suchmememanyskill](https://github.com/suchmememanyskill) for [TegraExplorer](https://github.com/suchmememanyskill/TegraExplorer).
- [rajkosto](https://github.com/rajkosto) for [gptrestore](https://github.com/rajkosto/gptrestore).
- [shchmue](https://github.com/shchmue) for [Lockpick_RCM](https://github.com/shchmue/Lockpick_RCM).
- [eliboa](https://github.com/eliboa) for the images from [TegraRcmGUI](https://github.com/eliboa/TegraRcmGUI).
- The creators of Atmosphère for [fusee-primary](https://github.com/Atmosphere-NX/Atmosphere/releases).
- [CTCaer](https://github.com/CTCaer) for [Hekate](https://github.com/CTCaer/hekate).

## Donate
[![Donations](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/donate?business=2N3E52994D2KG&no_recurring=0&currency_code=USD)
