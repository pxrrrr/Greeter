# Greeter Plugin

A basic Hytale server plugin for player join/leave messages.

**Note:** This plugin has a terrible structure and is purely for testing purposes and proof of concept. Not intended for production use.

## Features

- Custom welcome messages for first-time players
- Join/leave broadcast messages
- Configurable via `config.json`
- Tracks known players to distinguish new vs returning players

## Commands

| Command         | Description                                                |
|-----------------|------------------------------------------------------------|
| /greeter        | Shows help for all greeter commands                        |
| /greeter reload | Reloads the config from disk                               |
| /greeter status | Shows current settings (join/leave messages enabled, etc.) |

## Building

```bash
mvn clean package
```

The compiled JAR will be in `target/`.
