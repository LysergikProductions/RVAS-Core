# RVAS-Core v0.1.5
The core plugin behind RVAS (Real Vanilla Anarchy Survival)

The main goal is to give server operators everything they need to get started with their own *anarchy-style* minecraft server

_**Doesn't prevent any vanilla exploits** by default_

#### Features:
- **5-tier Speed Limiter** and other **lag preventing features** (configurable)

- Integrated **Random World-Spawn** that comes ready to use and is easy to configure

- Creative-mode **dupe/illegals limiter** to protect the economy from sneaky ops

- Permanent **Item/Stack signing** with `/sign` for a **healthy economy**

- Bedrock **Floor / Roof Protection** (configurable)

- **Analytics** for *performance* and *commands-use*

- Player **Statistics Tracking** (playtime, pvp-kills, etc)

- **Slow Chat** and **Anti-Spam** chat modes that ops can toggle

- **Illegal Items Management**

- The End **Exit-Portal** auto-repair

- **Anti Furnace-Chunk-Ban**

- **Lag Machine Detection**

- Customizable + randomized MOTDs

##### Commands:
- `/vm` - _1-hour long, democratic global-chat mute_
- `/mute temp | permanent` - _(for ops only)_
- `/admin color | spy | speed` - _show list of fastest moving players, use red ign chat color, etc_
- `/repair exit | portals | roof | floor` - _precisely repair vital game structures_
- `/tps`, `/stats`, `/backup`  _and many more!_

##### @ Server Operators
For full admin functionality, in `core\config.txt`, please enter your uuid and current ign for the intended in-game master account

This account will be **exempt** from all command / ability restrictions issued by rvas-core

##### Dependencies

At this time, only ProtocolLib and ArmorEquipEvent are required for the plugin to run correctly. Use Paper 1.16.5 Build #446 or newer.

##### Building from Source using Eclipse

1. Right-click empty space in the project explorer and use import.. to import this repo from Github
2. Right-click pom.xml, and use "Run as -> Maven build.."
3. Type "package" in the Goals box. This will save the package configuration. You can later run it with "Run as -> Maven build" and select the configuration.
4. A .jar file will be produced in /target. Use the one that isn't the `original-` version.
5. Place this .jar in your plugins directory on your server along with ArmorEquipEvent.jar and ProtocolLib.jar.
