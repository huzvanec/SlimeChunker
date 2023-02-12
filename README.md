# SlimeChunker
## What is SlimeChunker?
SlimeChunker is a simple and lightweight plugin which makes slime chunks easy to locate without needing to know the seed.

## Supported platforms
Spigot based minecraft servers <br>
Bukkit is **not** and will **not** be supported!

## How to use it?
### Player
Display your chunks using ```F3 + G```. Then use ```/slimechunk``` to determine, if the chunk you are standing in is a slime chunk or not. The response will be shown in chat. <br>
**Example** <br>
![example](https://skladu.jeme.cz/slimechunker/slimechunker.gif)
### Admin
**Permissions** <br>
First, you need to grant ```slimechunker.slimechunk``` permission to players so they can use the ```/slimechunk``` command. You can do this using a permission plugin such as [Luck Perms](https://luckperms.net/). <br>
**Config** <br>
In your plugins folder you will find a folder named ```SlimeChunker``` and inside a file named ```config.yml```. There, you can customize three types of messages: <br> <br>
```Yes slimechunk``` - A message shown when the chunk is a slime chunk <br>
```No slimechunk``` - A message shown when the chunk is not a slime chunk <br>
```Wrong dimesion``` - A message shown when the command is ran in a dimesion, that doesn't have slime chunks (The Nether or The End) <br> <br>
To reload the config, restart your server, or use ```/slimechunker reload```