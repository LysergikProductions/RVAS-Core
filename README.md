# RVAS-Core v0.1.4
The core plugin behind RVAS (Real Vanilla Anarchy Survival)

The main goal is to give server operators everything they need to get started with their own *anarchy-style* minecraft server

**Doesn't prevent any *vanilla* exploits** by default

#### Features:
- 5-tier **speed limiter** and other lag preventing features (configurable)
- Built-in, simple, and configurable **random respawning**
- Economy-saving peace-of-mind **creative-mode dupe protection**
- Bedrock **floor / roof protection** (configurable)
- **Tracks various player statistics** (playtime, pvp-kills, etc)
- Configure various ways to **deal with illegal stacks / items**
- Operator accounts *cannot* introduce illegal items / disrupt the economy (configurable)
- **Customizable MOTDs**

##### Commands:
- /vm - 1-hour long, democratic temporary global-chat mute
- /mute temp | permanent - (for ops only)
- /admin speeds - show list of how fast the fastest moving players are going
- /repair exit/portals/roof/floor - precisely repair vital game structures
- /tps, /stats, and many more!

##### @ Server Operators
For full admin functionality, in `core\config.txt`, please enter your uuid and current ign for the intended in-game master account

This account will be **exempt** from all command / ability restrictions issued by rvas-core

##### Dependencies

At this time, only ProtocolLib and ArmorEquipEvent are required for the plugin to run correctly. Use Paper 1.16.5 Build #446 or newer.

##### Building from Source

1. Clone this repo.
2. Use Eclipse to import the repo in a workspace.
3. Right click pom.xml, and use "Run as -> Maven build.."
4. Type "package" in the Goals box. This will save the package configuration. You can later run it with "Run as -> Maven build" and select the configuration.
5. A .jar file will be produced in /target. Use the one that isn't the `original-` version.
6. Place this .jar in your plugins directory on your server along with ArmorEquipEvent and ProtocolLib.
