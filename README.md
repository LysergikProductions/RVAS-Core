<a href="https://minecraft-mp.com/server-s287184" target="_blank"><img src="https://minecraft-mp.com/banner-287184.png" border="0"></a>

# RVAS-Core v0.3
The core plugin behind RVAS (Real Vanilla Anarchy Survival)

The main goal is to give server operators everything they need to get started with their own *anarchy-style* minecraft server

_**Doesn't prevent any vanilla exploits** by default_

#### Notable Features:
- **5-tier Speed Limiter** and other **lag preventing features** (configurable)

- Integrated **Random World-Spawn** that comes ready to use and is easy to configure

- Creative-mode **dupe/illegals limiter** to protect the economy from sneaky ops

- Permanent **Item/Stack signing** with `/sign` for a **healthy economy**

- Bedrock Floor / Roof and The End **exit portal Protection** (configurable)

- **Analytics** for *performance* and *commands-use*

- Player **Statistics Tracking** (playtime, pvp-kills, etc)

- **Slow Chat** and **Anti-Spam** toggleable chat-modes

- **Illegal Items Management**

- **Lag-Machine Detection** and **Anti-Chunk-Ban**

- Customizable + randomized MOTDs

- Customizable Themes!

##### Commands:
- `/vm` | `/ignore` | `/tjm` - _For players to self-govern their chat experience_
- `/admin` | `/repair` - _Powerful tools for ops_
- `/stats` - _Display various player stats_
- `/backup`,  _and many more!_

##### @ Server Operators
For full admin functionality, in `core\config.txt`, please enter your uuid and current ign for the intended in-game master account

This account will be **exempt** from all command / ability restrictions issued by rvas-core

##### Dependencies

- **Paper/Spigot 1.16.5** build #446 *(or newer)*
- [ProtocolLib](https://github.com/dmulloy2/ProtocolLib/releases/tag/4.6.0)
- [ArmorEquipEvent](https://github.com/Arnuh/ArmorEquipEvent/releases)

##### Building from Source using Eclipse

1. Right-click empty space in the project explorer and use import.. to import this repo from Github
2. Right-click pom.xml, and use "Run as -> Maven build.."
3. Type "package" in the Goals box. This will save the package configuration. You can later run it with "Run as -> Maven build" and select the configuration.
4. A .jar file will be produced in /target. Use the one that isn't the `original-` version.
5. Place this .jar in your plugins directory on your server along with ArmorEquipEvent.jar and ProtocolLib.jar.
