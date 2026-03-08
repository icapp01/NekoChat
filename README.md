<div align="center">

# NekoChat
[![License: GPL-3.0](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Modrinth](https://img.shields.io/modrinth/dt/nekochat?label=downloads&logo=modrinth)](https://modrinth.com/plugin/nekochat)
[![GitHub Repo stars](https://img.shields.io/github/stars/hanamuramiyu/NekoChat?style=social)](https://github.com/hanamuramiyu/NekoChat)

**A modern, feature-packed chat plugin with local/global channels, mentions, private messages, and full Folia support.**

[<kbd> <br>🇺🇸 English (US) [Current] <br> </kbd>](README.md) | [<kbd> <br> 🇯🇵 日本語 (ja-JP) <br> </kbd>](README_ja-JP.md) | [<kbd> <br> 🇨🇳 简体中文 (zh-CN) <br> </kbd>](README_zh-CN.md)

</div>

## ✨ Features

### 🗣️ Advanced Chat System
- **Dual Chat Channels**: Separate **Global** (prefix-based) and **Local** (range-based) chat channels
- **Smart Mentions**: `@username` mentions with visual and sound notifications
- **Private Messaging**: Fully-featured `/msg` and `/reply` commands with sounds
- **Complete Chat Management**: Toggle channels on/off, ignore players, and more

### ⚡ Performance & Compatibility
- **Folia Support**: Full compatibility with Folia servers using region-aware schedulers
- **Modern Formatting**: MiniMessage support for rich text formatting with `<color>` tags
- **Java 21**: Built with the latest Java for optimal performance and security
- **Async Operations**: All heavy operations are handled asynchronously to prevent lag

### 🎨 Customization & Integration
- **Fully Configurable Formats**: Every message format is customizable via MiniMessage
- **Multi-Language Support**: Built-in translations for 9 languages with auto-detection
- **PlaceholderAPI Support**: Use any PlaceholderAPI placeholders in your chat formats
- **LuckPerms Integration**: Automatic prefix/suffix display in chat

### 🛡️ Player Control
- **Ignore System**: Players can ignore unwanted messages from specific users
- **Cooldowns**: Configurable cooldowns for chat, messages, and replies to prevent spam
- **Permission-Based Features**: Fine-tune access to mentions and commands

---

## 🚀 Installation

### System Requirements
- **Java 21** or higher
- **Minecraft 1.21.1** or higher
- **Bukkit/Paper/Purpur/Folia** server

### Installation Steps:
1.  Download the latest `.jar` file from [Releases](https://github.com/hanamuramiyu/NekoChat/releases) or [Modrinth](https://modrinth.com/plugin/nekochat)
2.  Place the `.jar` file into your server's `plugins` folder
3.  Start or restart your server
4.  Configure `plugins/NekoChat/config.yml` and language files in `plugins/NekoChat/lang/` as needed

---

## ⚙️ Configuration

### Basic Setup (`config.yml`)
```yaml
# NekoChat Configuration
language: "en-US" # Default server language
auto-detect-client-language: true # Use player's client language if available

global-prefix: "!" # Messages starting with this go to global chat

chat:
  global:
    enabled: true
  local:
    enabled: true
    range: 100 # Local chat range in blocks
  private:
    enabled: true
    sound: # Sounds for private messages
      enabled: true
      sound: "entity.experience_orb.pickup"
  mention:
    enabled: true
    sound: # Sounds for mentions
      enabled: true
      volume: 0.5
      pitch: 1.2
    require-permission: false # If true, only players with nekochat.mention can be mentioned

# Format settings
formats:
  global-format: "<gold>[G] <luckperms_prefix><username><luckperms_suffix> <gray>>> <message>"
  local-format: "<aqua>[L] <luckperms_prefix><username><luckperms_suffix> <gray>>> <message>"
  private-format-send: "<aqua>[✉] <username_sender> -> <username_recipient> <gray>>> <message>"
  join-format: "<green>[+]<reset> <player>"
  quit-format: "<red>[-]<reset> <player>"
  death-format: "<gray>[☠] <reason>"

cooldowns: # Cooldowns in seconds
  chat: 0
  msg: 1
  reply: 1
```

### Available Placeholders
- `<luckperms_prefix>` / `<luckperms_suffix>` - From LuckPerms
- `<username>` - Player's raw username
- `<player>` - Player's display name
- `<message>` - The chat message
- `<sender>` / `<recipient>` - Display names for PMs
- `<username_sender>` / `<username_recipient>` - Raw names for PMs
- `<reason>` - Death message reason

### Multi-Language Support
NekoChat comes with built-in translations for:
- `en-US`, `en-GB` (English)
- `es-ES`, `es-419` (Spanish)
- `ja-JP` (Japanese)
- `ru-RU` (Russian)
- `uk-UA` (Ukrainian)
- `zh-CN`, `zh-TW` (Chinese)

With `auto-detect-client-language: true`, players will automatically see messages in their client's language!

---

## 🔧 Commands & Permissions

### In-Game Commands
| Command | Description | Permission |
|---------|-------------|------------|
| `/chat help` | Show plugin help | `nekochat.use` |
| `/chat reload` | Reload configuration (admin) | `nekochat.admin` |
| `/msg <player> <message>` | Send a private message | `nekochat.msg` |
| `/reply <message>` | Reply to last PM | `nekochat.msg` |
| `/togglechat [global\|local\|all]` | Enable/disable channels (admin) | `nekochat.admin` |
| `/ignore <player>` | Ignore a player | `nekochat.ignore` |
| `/unignore <player>` | Unignore a player | `nekochat.ignore` |
| `/ignorelist` | List ignored players | `nekochat.ignore` |

### Permission Nodes
- `nekochat.use` - Base permission for chat commands
- `nekochat.admin` - Full administrative access (includes all below)
- `nekochat.mention` - Allows using @mentions (if `require-permission` is true)
- `nekochat.msg` - Allows sending private messages
- `nekochat.ignore` - Allows using the ignore system

---

## 🚨 Important Notes

### ⚠️ Requirements
1. **Java 21 Required**: Minimum Java version is 21
2. **Minecraft 1.21.1+**: Built for modern Minecraft versions
3. **Folia Support**: Fully compatible with both Bukkit/Paper and Folia servers

### 🔌 Soft Dependencies
- **LuckPerms**: Enables `<luckperms_prefix>` and `<luckperms_suffix>` placeholders
- **PlaceholderAPI**: Allows using any PlaceholderAPI placeholders in formats

---

## 🌐 Adding New Languages

1.  Navigate to `plugins/NekoChat/lang/` directory
2.  Copy `en-US.yml` as a template
3.  Rename to your language code (e.g., `fr-FR.yml`, `de-DE.yml`)
4.  Translate all values using **MiniMessage format** (`<color>` tags)
5.  Update `language` setting in `config.yml` or rely on auto-detection

**Example MiniMessage tags:**
- `<red>` - Red text
- `<green>` - Green text  
- `<yellow>` - Yellow text
- `<gray>` - Gray text
- `<gold>` - Gold text
- `<bold>` - **Bold text**
- `<click:open_url:'https://...'>` - Clickable links
- `<hover:show_text:'<green>Hover text'>` - Hover tooltips

---

## 🏗️ Building from Source

```bash
# Clone repository
git clone https://github.com/hanamuramiyu/NekoChat.git
cd NekoChat

# Build plugin
./gradlew build

# Output file: build/libs/NekoChat-1.0.0.jar
```

**Requirements:**
- Java 21 JDK
- Gradle 9.2.0+

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a Pull Request

Please ensure your code follows the existing style and includes appropriate tests.

---

## 🐛 Issue Reporting

Found a bug or have a feature request? Please:
1. Check existing [Issues](https://github.com/hanamuramiyu/NekoChat/issues)
2. Create a new issue with clear description
3. Include server logs and configuration details
4. Specify your server type and version

---

## 📄 License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.

---

<div align="center">

**Made with ❤️ by Hanamura Miyu**

*Bringing your server's chat to life, one message at a time.*

</div>