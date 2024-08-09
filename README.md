# Commander

A must-have command management system for your minecraft server.<br/>
With **Commander** you can _remove_ or _hide_ commands you don't want,<br/>
_change_; _remove_ or _add_ permissions to existing commands.<br/>
Every message is localized and can be customized.

> [!IMPORTANT]
> The latest major version (4.0.0) changed the way how commands are hidden, unregistered and permissions overriden<br/>
> When updating from any `3.x.x` version _or below_ all your commands and permissions seem to be reset (https://github.com/TheNextLvl-net/commander/issues/16)<br/>
> 
> The easiest way to update is by reapplying all of your changes via the command, just open your old `commands.json` and enter the respective commands again
>
> Because version 4 does not **yet** support wildcards (*) for bulk actions like hiding, unregistering commands or overriding permissions, you have to apply all your changes for each command manually
> 
> Instead of `paper:*` you would have to apply your changes for all commands: `paper:callback`, `paper:mspt`, `paper:paper`, `paper:spark`. 

## Usage

_In order to use `/command` the permission `commander.admin` is required_<br/>
_To bypass the command hiding feature the explicit permission `commander.bypass` is required_

## Commands

To remove a command for everyone, use: `/command unregister [command]`<br/>
To add a removed command back, use: `/command register [command]`<br/>
To hide a command, use: `/command hide [command]`<br/>
To reveal a command again, use: `/command reveal [command]`<br/>
To completely reset a command, use: `/command reset [command]`

## Permissions

- To change the permission of a command, use: `/command permission set [command] [permission]`<br/>
- To completely remove a permission from a command, use: `/command permission unset [command]`<br/>
- To reset the permission of a command to its default value, use: `/command permission reset [command]`<br/>
- To see what the permission of a certain command is, use: `/command permission query [command]`
