# RVAS-Core v0.1.3
The core plugin behind RVAS (Real Vanilla Anarchy Survival)

The main goal is to give server operators everything they need to get started with their own *anarchy-style* minecraft server

##### Features:
- 5-tier speed limiter (configurable)
- Operator accounts *cannot* introduce illegal items / disrupt the economy (configurable)
- Players can vote to temporarily mute each other for 1 hour (configurable, majority required by default)
- Doesn't prevent any vanilla exploits by default
- Bedrock floor / roof protection (configurable)
- Tracks various player statistics (playtime, pvp-kills, and more!)
- Can permanently mute accounts that are just bots (or for other reasons)
- Can enforce survival mode in various ways if configured to
- Customizable MOTDs

##### @ Server Operators
For full admin functionality, in `core\config.txt`, please enter your uuid and current ign for the intended in-game master account

This account will be **exempt** from all command / ability restrictions issued by rvas-core