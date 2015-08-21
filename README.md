# PushLocal-Android
Push Android notifications to your Windows PC over a WiFi network

## Want to help?

### Prerequisites

#### Genymotion Android Emulator

  - [Download](https://www.genymotion.com/#!/download) and install For an Android Emulator with WiFi capabilities
    -  If using Windows 10, install the [latest version](https://www.virtualbox.org/wiki/Testbuilds) (+ the ExtPack) of VirtualBox seperately to avoid project-breaking bugs
    -  In VirtualBox, configure your device to run the second network adapter as a bridge to your networking card
  
### Project Setup

* Once the repository is pulled, run `gradlew` to init the project
* `gradlew assemble` to test the build
* Finally, import the repository as a Gradle project in your IDE
