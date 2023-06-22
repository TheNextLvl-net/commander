# Commander
A must-have command management system for your minecraft server.<br/>
With **Commander** you can _remove_ commands you don't want,<br/>
_change_; _remove_ or _add_ permissions to existing commands.<br/>
Also, Commander improves the **no permission** and **unknown command** messages.

## Usage

_In order to use `/command` the permission `commander.admin` is required_

### Permissions

- To _change_ the _permission_ of a command, use: `/command permission set [command] [permission]`<br/>
- To completely _remove_ a _permission_ from a command, use: `/command permission set [command] null`<br/>
- To _reset_ the _permission_ of a command to its default value, use: `/command permission reset [command]`<br/>
- To _see_ what the _permission_ of a certain command is, use: `/command permission query [command]`

### Commands

To _delete_ a _command_ for everyone, use: `/command unregister [command]`<br/>
To _get_ a deleted command _back_, use: `/command register [command]`<br/>