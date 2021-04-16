# RVAS-Core v0.1.3
The core plugin behind RVAS (Real Vanilla Anarchy Survival)

The main goal is to give server operators everything they need to get started with their own *anarchy-style* minecraft server

##### Features:
- 5-tier speed limiter (configurable)
- Built-in, simple, and configurable random respawning
- Operator accounts *cannot* introduce illegal items / disrupt the economy (configurable)
- Doesn't prevent any vanilla exploits by default
- Bedrock floor / roof protection (configurable)
- Tracks various player statistics (playtime, pvp-kills, etc)
- Configure various ways to deal with illegal stacks / items
- Customizable MOTDs

##### Commands:
- /vm - 1-hour long, democratic temporary global-chat mute
- /mute temp | permanent - (for ops only)
- /world - various op-only fun-things for events (i.e. summon lightning around every player)
- and more!

##### @ Server Operators
For full admin functionality, in `core\config.txt`, please enter your uuid and current ign for the intended in-game master account

This account will be **exempt** from all command / ability restrictions issued by rvas-core