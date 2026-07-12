[![Banner](https://raw.githubusercontent.com/TheNextLvl-net/commander/refs/heads/main/assets/banner.png)](https://thenextlvl.net/docs/commander)

<p align="center" style="text-align: center;">
  <a href="https://thenextlvl.net/discord"><img src="https://tr7zw.github.io/uikit/social_buttons_icon/Discord-Button-64.png"></img></a>
  <a href="https://modrinth.com/project/USLuwMUi"><img src="https://tr7zw.github.io/uikit/social_buttons_icon/Modrinth-Button-64.png"></img></a>
  <a href="https://github.com/TheNextLvl-net/commander"><img src="https://tr7zw.github.io/uikit/social_buttons_icon/Github-Button-64.png"></img></a>
</p>

# A Powerful Command Management Tool

A must-have command management system for your minecraft server.<br/>
With **Commander** you can _remove_ or _hide_ commands you don't want,<br/>
_change_; _remove_ or _add_ permissions to existing commands.<br/>
Every message is localized and can be customized.

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

The management command on Velocity is: `/commandv`

---

[![Usage](https://faststats.dev/embed/default:b027d374-827c-4e6f-9a59-18c91ad0b854:servers-and-players?w=1012)](https://faststats.dev/project/commander)
