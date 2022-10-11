# VLDofusBot

Welcome to VLDofusBot, this bot's goal is to help you automatize tedious tasks in the game.

### Getting started

To get started, you'll first need to install the bot. For this, you'll need :

- gradle version 6.9 (I've noticed issues versions 7+) : [Download](https://gradle.org/releases/)
- Git : [Download](https://git-scm.com/downloads)
- Npcap to use the
  sniffer ([VLDofusBotSniffer](https://github.com/viclew1/VLDofusBotSniffer)) : [Download](https://npcap.com/#download)

To install or work on this project, you'll need to clone all repositories (
using [VLDofusBotModulesBuilder](https://github.com/viclew1/VLDofusBotModulesBuilder)). Open a cmd where you want to
install the bot and type these commands :

- ```git clone https://github.com/viclew1/VLDofusBotModulesBuilder```
- ```cd VLDofusBotModulesBuilder```
- ```git submodule update --init```
- ```git submodule foreach git checkout master```
- ```gradle build```

Then, you can launch the built jar : ```VLDofusBot\build\libs\VLDofusBot-X.jar```

### How to use

TODO