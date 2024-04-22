# Commander
A must-have command management system for your minecraft server.<br/>
With **Commander** you can _remove_ or _hide_ commands you don't want,<br/>
_change_; _remove_ or _add_ permissions to existing commands.<br/>
Also, Commander improves the **no permission** and **unknown command** messages.

![image](https://github.com/TheNextLvl-net/commander/assets/54660361/3cdb5f22-e6f7-4835-b051-e647d3afa18b)

## Usage

_In order to use `/command` the permission `commander.admin` is required_<br/>
_To bypass the command hiding feature the explicit permission `commander.bypass` is required_

### Permissions

- To change the permission of a command, use: `/command permission set [command] [permission]`<br/>
- To completely remove a permission from a command, use: `/command permission set [command] null`<br/>
- To reset the permission of a command to its default value, use: `/command permission reset [command]`<br/>
- To see what the permission of a certain command is, use: `/command permission query [command]`

### Commands

To remove a command for everyone, use: `/command unregister [command]`<br/>
To add a removed command back, use: `/command register [command]`<br/>
To hide a command, use: `/command hide [command]`<br/>
To reveal a command again, use: `/command reveal [command]`<br/>
To completely reset a command, use: `/command reset [command]`
