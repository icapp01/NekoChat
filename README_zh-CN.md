<div align="center">

# NekoChat
[![License: GPL-3.0](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Modrinth](https://img.shields.io/modrinth/dt/nekochat?label=downloads&logo=modrinth)](https://modrinth.com/plugin/nekochat)
[![GitHub Repo stars](https://img.shields.io/github/stars/hanamuramiyu/NekoChat?style=social)](https://github.com/hanamuramiyu/NekoChat)

**一款现代、功能丰富的聊天插件，支持本地/全局频道、@提及、私信，并完全兼容Folia。**

[<kbd> <br> 🇺🇸 English (US) <br> </kbd>](README.md) | [<kbd> <br> 🇯🇵 日本語 (ja-JP) <br> </kbd>](README_ja-JP.md) | [<kbd> <br>🇨🇳 简体中文 (zh-CN) [Current] <br> </kbd>](README_zh-CN.md)

</div>

## ✨ 功能

### 🗣️ 高级聊天系统
- **双聊天频道**: 独立的**全局**聊天（基于前缀）和**本地**聊天（基于范围）
- **智能提及**: `@用户名`提及，带有视觉和声音通知
- **私信系统**: 功能完备的`/msg`和`/reply`命令，支持提示音
- **完整的聊天管理**: 开关频道、忽略玩家等

### ⚡ 性能与兼容性
- **Folia支持**: 使用区域感知调度器，与Folia服务器完全兼容
- **现代格式化**: 支持使用`<color>`标签进行富文本格式化的MiniMessage
- **Java 21**: 使用最新Java构建，提供最佳性能和安全性
- **异步操作**: 所有繁重操作均异步处理，防止卡顿

### 🎨 定制与集成
- **完全可配置的格式**: 所有消息格式均可通过MiniMessage自定义
- **多语言支持**: 内置9种语言翻译，支持自动检测
- **PlaceholderAPI支持**: 在聊天格式中使用任何PlaceholderAPI变量
- **LuckPerms集成**: 在聊天中自动显示前缀/后缀

### 🛡️ 玩家控制
- **忽略系统**: 玩家可以忽略来自特定用户的不需要的消息
- **冷却时间**: 可为聊天、私信和回复设置冷却时间以防止刷屏
- **基于权限的功能**: 精细控制对提及和命令的访问

---

## 🚀 安装

### 系统要求
- **Java 21** 或更高版本
- **Minecraft 1.21.1** 或更高版本
- **Bukkit/Paper/Purpur/Folia** 服务器

### 安装步骤:
1.  从[发布页面](https://github.com/hanamuramiyu/NekoChat/releases)或[Modrinth](https://modrinth.com/plugin/nekochat)下载最新的`.jar`文件
2.  将`.jar`文件放入服务器的`plugins`文件夹
3.  启动或重启服务器
4.  根据需要配置`plugins/NekoChat/config.yml`和`plugins/NekoChat/lang/`中的语言文件

---

## ⚙️ 配置

### 基本设置 (`config.yml`)
```yaml
# NekoChat 配置
language: "en-US" # 默认服务器语言
auto-detect-client-language: true # 如果可能，自动使用玩家的客户端语言

global-prefix: "!" # 以此前缀开头的消息将发送到全局聊天

chat:
  global:
    enabled: true
  local:
    enabled: true
    range: 100 # 本地聊天范围（方块数）
  private:
    enabled: true
    sound: # 私信提示音
      enabled: true
      sound: "entity.experience_orb.pickup"
  mention:
    enabled: true
    sound: # 提及提示音
      enabled: true
      volume: 0.5
      pitch: 1.2
    require-permission: false # 如果为true，只有拥有nekochat.mention权限的玩家才能被@提及

# 格式设置
formats:
  global-format: "<gold>[G] <luckperms_prefix><username><luckperms_suffix> <gray>>> <message>"
  local-format: "<aqua>[L] <luckperms_prefix><username><luckperms_suffix> <gray>>> <message>"
  private-format-send: "<aqua>[✉] <username_sender> -> <username_recipient> <gray>>> <message>"
  join-format: "<green>[+]<reset> <player>"
  quit-format: "<red>[-]<reset> <player>"
  death-format: "<gray>[☠] <reason>"

cooldowns: # 冷却时间（秒）
  chat: 0
  msg: 1
  reply: 1
```

### 可用变量
- `<luckperms_prefix>` / `<luckperms_suffix>` - 来自LuckPerms
- `<username>` - 玩家的原始用户名
- `<player>` - 玩家的显示名称
- `<message>` - 聊天消息
- `<sender>` / `<recipient>` - 私信中的显示名称
- `<username_sender>` / `<username_recipient>` - 私信中的原始用户名
- `<reason>` - 死亡消息原因

### 多语言支持
NekoChat 内置了以下语言的翻译：
- `en-US`, `en-GB` (英语)
- `es-ES`, `es-419` (西班牙语)
- `ja-JP` (日语)
- `ru-RU` (俄语)
- `uk-UA` (乌克兰语)
- `zh-CN`, `zh-TW` (中文)

设置`auto-detect-client-language: true`后，玩家将自动看到其客户端语言的消息！

---

## 🔧 命令与权限

### 游戏内命令
| 命令 | 描述 | 权限 |
|---------|-------------|------------|
| `/chat help` | 显示插件帮助 | `nekochat.use` |
| `/chat reload` | 重新加载配置（管理员） | `nekochat.admin` |
| `/msg <player> <message>` | 发送私信 | `nekochat.msg` |
| `/reply <message>` | 回复上一条私信 | `nekochat.msg` |
| `/togglechat [global\|local\|all]` | 启用/禁用频道（管理员） | `nekochat.admin` |
| `/ignore <player>` | 忽略一名玩家 | `nekochat.ignore` |
| `/unignore <player>` | 取消忽略一名玩家 | `nekochat.ignore` |
| `/ignorelist` | 列出被忽略的玩家 | `nekochat.ignore` |

### 权限节点
- `nekochat.use` - 使用聊天命令的基础权限
- `nekochat.admin` - 完全管理权限（包含以下所有权限）
- `nekochat.mention` - 允许使用@提及（如果`require-permission`为true）
- `nekochat.msg` - 允许发送私信
- `nekochat.ignore` - 允许使用忽略系统

---

## 🚨 重要注意事项

### ⚠️ 要求
1. **需要Java 21**: 最低Java版本为21
2. **Minecraft 1.21.1+**: 为现代Minecraft版本构建
3. **Folia支持**: 与Bukkit/Paper和Folia服务器完全兼容

### 🔌 软依赖
- **LuckPerms**: 启用`<luckperms_prefix>`和`<luckperms_suffix>`变量
- **PlaceholderAPI**: 允许在格式中使用任何PlaceholderAPI变量

---

## 🌐 添加新语言

1.  导航到`plugins/NekoChat/lang/`目录
2.  复制`en-US.yml`作为模板
3.  重命名为您的语言代码（例如`fr-FR.yml`、`de-DE.yml`）
4.  使用**MiniMessage格式**（`<color>`标签）翻译所有值
5.  更新`config.yml`中的`language`设置或依赖自动检测

**MiniMessage标签示例:**
- `<red>` - 红色文本
- `<green>` - 绿色文本
- `<yellow>` - 黄色文本
- `<gray>` - 灰色文本
- `<gold>` - 金色文本
- `<bold>` - **粗体文本**
- `<click:open_url:'https://...'>` - 可点击链接
- `<hover:show_text:'<green>悬停文本'>` - 悬停提示

---

## 🏗️ 从源码构建

```bash
# 克隆仓库
git clone https://github.com/hanamuramiyu/NekoChat.git
cd NekoChat

# 构建插件
./gradlew build

# 输出文件: build/libs/NekoChat-1.0.0.jar
```

**要求:**
- Java 21 JDK
- Gradle 9.2.0+

---

## 🤝 贡献

欢迎贡献！请遵循以下步骤:
1. Fork仓库
2. 创建功能分支
3. 进行更改
4. 提交Pull Request

请确保代码遵循现有风格并包含适当的测试。

---

## 🐛 问题报告

发现错误或有功能请求？请:
1. 检查现有[Issues](https://github.com/hanamuramiyu/NekoChat/issues)
2. 创建描述清晰的新issue
3. 包含服务器日志和配置详情
4. 指定服务器类型和版本

---

## 📄 许可证

该项目根据GNU通用公共许可证v3.0授权 - 详情请参阅[LICENSE](LICENSE)文件。

---

<div align="center">

**由 Hanamura Miyu 倾心制作 ❤️**

*为您的服务器聊天注入活力，每一条消息都精彩纷呈。*

</div>
```