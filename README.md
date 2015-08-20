# PushLocal-Android
Push Android notifications to your Windows PC over a WiFi network

## Want to help?

### Prerequisites

#### Genymotion Android Emulator

  - [Download](https://www.genymotion.com/#!/download) and install For an Android Emulator with WiFi capabilities
    -  If using Windows 10, install the [latest version](https://www.virtualbox.org/wiki/Testbuilds) of VirtualBox seperately to avoid project-breaking bugs
    -  In VirtualBox, configure your device to run the second network adapter as a bridge to your networking card
  
### Project Setup

* Once the repository is pulled, run `gradlew` to init the project
* run `gradlew <ide>` to compile the project for a certain IDE
  * for example, `gradlew idea` for Intellij IDEA
* `gradlew assemble` to test the build
* Finally, open the project with the generated IDE extension file
