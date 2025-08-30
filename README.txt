Minecraft Packet Logger

Apacket logging mod for Minecraft 1.8.9 (Forge) that captures all client-to-server (Cxx) and server-to-client (Sxx) packets in real-time. Logs are saved in JSON format for easy analysis, including:

- Packet class name
- Direction (Client -> Server / Server -> Client)
- Timestamp of each packet
- New Json per session

Features
- Real time packet logging to JSON files
- Toggle logging on/off with `&toggle`
- Check current logging status with `&status`
- Open logs folder with `&packetfolder`
- Works with vanilla and Forge mod packets

Usage
Type commands in chat:
- `&toggle` — Enable/disable logging
- `&status` — Display current logging status and active log file
- `&packetfolder` — Open the folder containing log files
- `&help` — Show available commands

Installation
- Drop the compiled `.jar` into your Minecraft 1.8.9 `mods` folder
- Launch Minecraft with Forge 1.8.9

Notes
- Logs are stored in `C:\Users\<YourUser>\MinecraftPacketLoggerLogs` by default
- Works with both vanilla and modded servers
