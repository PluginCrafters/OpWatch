![Alt Text](https://imgur.com/SRL3b7L.png)
![Alt Text](https://imgur.com/5lulL0z.png)
Do you want to ensure that only certain players have op or certain permissions on your Minecraft server? 
Then this is the plugin for you!

With this plugin, you can easily create a whitelist of players who are allowed to have op or certain permissions on 
your server. You can configure the whitelist and blocked permissions in separate YAML files that are easy to edit.

Whenever someone tries to give op or blocked permissions to a player who is not on the whitelist, the plugin steps in 
to save the day! It checks if the player is allowed to have op or the permission and blocks the command if they're not 
on the list. Plus, it checks periodically (you can set the time) among all online players to see if anyone with 
op or the permission is not on the whitelist. If so, OpWatch automatically removes the op and either kicks or bans 
them (you choose!).

The plugin also saves the IPs of players who have op or permissions. If a player's IP changes, OpWatch prompts them 
to authenticate with a Google Authenticator 2FA code for added security.

This plugin is fully configurable and translatable. It works with Minecraft versions 1.8 to 1.19.4 and can save the IP 
in either an H2 or MySQL database. Plus, it's compatible with AuthMe and LuckPerms.

Try it out today and enjoy peace of mind knowing that only the right players have op or permissions on your server!

# IMPORTANT
By default, the IP change check for players with op is disabled. This option should ONLY be enabled on the server on which players enter the server for the first time (e.g. the Lobby).

To use this option, all players who have op (or any permissions configured), must use the Google Authenticator application or similar.

![Alt Text](https://imgur.com/SpBqBM6.png)
* **/opwatch reassign (player)** (Give player another QR Code)
* **/opwatch help** (Shows all commands in game)
* **/opwatch reload** (Reloads all the files.)

![Alt Text](https://imgur.com/GmIRxmO.png)
* **opwatch.help** (Allows players to use help command)
* **opwatch.reload** (Allows players to use reload command)
* **opwatch.reassign** (Allows player to perform */opwatch reassign* command.)
* **opwatch.updatechecker** (Allows users to receive notifications when the plugin is updated)

![Alt Text](https://imgur.com/jRS4DPF.png)

![Alt Text](https://imgur.com/ns03Y4n.png)
![Alt Text](https://imgur.com/3pWVwpC.png)
![Alt Text](https://imgur.com/CMn9iqj.png)
![Alt Text](https://imgur.com/7oOgAsD.png)

![Alt Text](https://imgur.com/baW2OzV.png)
## * **[Download](https://www.spigotmc.org/resources/opwatch.108739/)**
## * **[Wiki](https://github.com/PluginCrafters/OpWatch/wiki)**
## * **[Discord](https://discord.gg/cvagVTztZZ) server for help**
## * **Drawings by [melinesa13](https://twitter.com/melinesa13)**
## * **[Donate](https://www.paypal.com/paypalme/RosenM00?locale.x=es_XC)**