# VLDofusBotSniffer

VLDofusBotSniffer is the sniffer part of the bot. The goal is to mimic the Dofus network and class hierarchy. Every message sent by the server to the client is converted to a Kotlin object, then stored in an EventStore.

### How to use

To use the sniffer, you must have an instance of Dofus running on your computer. Then,
use a `DofusMessageReceiver`. It will split received messages in different `DofusMessageCharacterReceiver` and every successfully parsed message will be stored in an `EventStore` from which you can retrieve last parsed messages.
