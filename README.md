# JTegraNX - Another TegraRcmSmash GUI
A GUI for [TegraRcmSmash](https://github.com/rajkosto/TegraRcmSmash) written in Java. This program is intended to be an alternate option for injecting a payload if you can't get [TegraRcmGUI](https://github.com/eliboa/TegraRcmGUI) to work properly.

This program is bundled with the latest version of TegraRcmSmash and it is extracted from the .jar file when the program is launched and deleted when the program is closed.

This program uses [usb4java](http://usb4java.org/index.html) to [detect the RCM device](./src/jtegranx/util/TegraRCM.java). Currently there is an issue where usb4java creates a temp directory that only contains libusb4java.dll, in normal circumstances this would be fine but it doesn't delete it when it's done with it and it always creates another one each time libusb is initialized (Thankfully only once each time this program is launched). I'm not sure why it does that and I will try to look for a fix on that.

Because this program was written in Java, you will need to have it installed on your computer, you can download Java [here](https://www.java.com/en/).

This program is only supported on Windows Operating Systems, sorry for any inconvenience.

## Functions
- Payload injection (obviously)
- Custom arguments
- Config system (similar to favorites in [TegraRcmGUI](https://github.com/eliboa/TegraRcmGUI))
- SD card mounting (Select "Load Config" to find it)

## Usage
1. Choose payload or config
2. Inject

## Using configs
The new config system allows you to save what you entered in the "Payload Path" field and the "Arguments" field and load it again in another session.

### Loading a config:
Simply click on "Load Config" and JTegraNX will search the "configs" directory for any valid config files. Select the config you wish to use and click on "Load".

### Saving a config:
Simple click on "Save Config" and enter a name for the config, it will be saved in the "configs" directory.

## Bundled Payloads
For any bundled payloads, JTegraNX will download them automatically, place them in the "payloads" directory, generate a config for them, and check for updates on them each time the program is launched. If an update for a payload is found, it will be taken care of.

Currently bundled payloads:
- [TegraExplorer](https://github.com/suchmememanyskill/TegraExplorer)

# Credits
- suchmememanyskill for allowing me to include [TegraExplorer](https://github.com/suchmememanyskill/TegraExplorer).
