# JTegraNX - TegraRcmSmash GUI
A GUI for [TegraRcmSmash](https://github.com/rajkosto/TegraRcmSmash) written in Java. This program is intended to be an alternate option for injecting a payload if you can't get [TegraRcmGUI](https://github.com/eliboa/TegraRcmGUI) to work properly.

This program is bundled with the latest version of TegraRcmSmash and it is extracted from the .jar file when the program is launched and deleted when the program is closed.

This program uses [usb4java](http://usb4java.org/index.html) to [detect the RCM device](./src/jtegranx/util/TegraRCM.java). Currently there is an issue where usb4java creates a temp directory that only contains libusb4java.dll, in normal circumstances this would be fine but it doesn't delete it when it's done with it and it always creates another one each time libusb is initialized (Thankfully only once each time this program is launched). I'm not sure why it does that and I will try to look for a fix on that.
