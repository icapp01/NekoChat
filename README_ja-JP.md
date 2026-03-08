<div align="center">

# NekoChat
[![License: GPL-3.0](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Modrinth](https://img.shields.io/modrinth/dt/nekochat?label=downloads&logo=modrinth)](https://modrinth.com/plugin/nekochat)
[![GitHub Repo stars](https://img.shields.io/github/stars/hanamuramiyu/NekoChat?style=social)](https://github.com/hanamuramiyu/NekoChat)

**ローカル/グローバルチャンネル、メンション、プライベートメッセージ、そして完全なFoliaサポートを備えた、モダンで機能満載のチャットプラグイン。**

[<kbd> <br> 🇺🇸 English (US) <br> </kbd>](README.md) | [<kbd> <br>🇯🇵 日本語 (ja-JP) [Current] <br> </kbd>](README_ja-JP.md) | [<kbd> <br> 🇨🇳 简体中文 (zh-CN) <br> </kbd>](README_zh-CN.md)

</div>

## ✨ 機能

### 🗣️ 高度なチャットシステム
- **デュアルチャットチャンネル**: 接頭辞ベースの**グローバル**チャットと距離制限のある**ローカル**チャットを分離
- **スマートメンション**: `@ユーザー名`でメンション、視覚・聴覚通知付き
- **プライベートメッセージ**: サウンド付きの完全な機能を持つ`/msg`と`/reply`コマンド
- **完全なチャット管理**: チャンネルのオン/オフ切り替え、プレイヤーの無視など

### ⚡ パフォーマンスと互換性
- **Foliaサポート**: リージョン対応スケジューラを使用したFoliaサーバーとの完全互換性
- **モダンな書式設定**: `<color>`タグを使用したリッチテキスト書式設定のMiniMessage対応
- **Java 21**: 最新のJavaで構築され、最適なパフォーマンスとセキュリティを提供
- **非同期処理**: すべての重い処理はラグを防ぐために非同期で処理されます

### 🎨 カスタマイズと連携
- **完全に設定可能なフォーマット**: すべてのメッセージフォーマットはMiniMessageでカスタマイズ可能
- **多言語サポート**: 9言語の内蔵翻訳と自動検出機能
- **PlaceholderAPIサポート**: チャットフォーマットで任意のPlaceholderAPIプレースホルダーを使用可能
- **LuckPerms連携**: チャットでの接頭辞/接尾辞の自動表示

### 🛡️ プレイヤーコントロール
- **無視システム**: プレイヤーは特定のユーザーからの不要なメッセージを無視可能
- **クールダウン**: スパム防止のためのチャット、メッセージ、返信に設定可能なクールダウン
- **権限ベースの機能**: メンションやコマンドへのアクセスを細かく制御

---

## 🚀 インストール

### システム要件
- **Java 21** 以上
- **Minecraft 1.21.1** 以上
- **Bukkit/Paper/Purpur/Folia** サーバー

### インストール手順:
1.  [リリースページ](https://github.com/hanamuramiyu/NekoChat/releases) または [Modrinth](https://modrinth.com/plugin/nekochat) から最新の `.jar` ファイルをダウンロード
2.  サーバーの `plugins` フォルダに `.jar` ファイルを配置
3.  サーバーを起動または再起動
4.  `plugins/NekoChat/config.yml` と `plugins/NekoChat/lang/` 内の言語ファイルを必要に応じて設定

---

## ⚙️ 設定

### 基本設定 (`config.yml`)
```yaml
# NekoChat 設定
language: "en-US" # デフォルトサーバー言語
auto-detect-client-language: true # 可能な場合、プレイヤーのクライアント言語を使用

global-prefix: "!" # この接頭辞で始まるメッセージはグローバルチャットへ

chat:
  global:
    enabled: true
  local:
    enabled: true
    range: 100 # ローカルチャットの範囲（ブロック数）
  private:
    enabled: true
    sound: # プライベートメッセージのサウンド
      enabled: true
      sound: "entity.experience_orb.pickup"
  mention:
    enabled: true
    sound: # メンションのサウンド
      enabled: true
      volume: 0.5
      pitch: 1.2
    require-permission: false # trueの場合、メンションにはnekochat.mention権限が必要

# フォーマット設定
formats:
  global-format: "<gold>[G] <luckperms_prefix><username><luckperms_suffix> <gray>>> <message>"
  local-format: "<aqua>[L] <luckperms_prefix><username><luckperms_suffix> <gray>>> <message>"
  private-format-send: "<aqua>[✉] <username_sender> -> <username_recipient> <gray>>> <message>"
  join-format: "<green>[+]<reset> <player>"
  quit-format: "<red>[-]<reset> <player>"
  death-format: "<gray>[☠] <reason>"

cooldowns: # クールダウン（秒）
  chat: 0
  msg: 1
  reply: 1
```

### 利用可能なプレースホルダー
- `<luckperms_prefix>` / `<luckperms_suffix>` - LuckPermsから
- `<username>` - プレイヤーの生のユーザー名
- `<player>` - プレイヤーの表示名
- `<message>` - チャットメッセージ
- `<sender>` / `<recipient>` - PM用の表示名
- `<username_sender>` / `<username_recipient>` - PM用の生のユーザー名
- `<reason>` - 死亡メッセージの理由

### 多言語サポート
NekoChatには以下の言語の内蔵翻訳があります:
- `en-US`, `en-GB` (英語)
- `es-ES`, `es-419` (スペイン語)
- `ja-JP` (日本語)
- `ru-RU` (ロシア語)
- `uk-UA` (ウクライナ語)
- `zh-CN`, `zh-TW` (中国語)

`auto-detect-client-language: true`を設定すると、プレイヤーは自動的にクライアント言語でメッセージを表示します！

---

## 🔧 コマンドと権限

### ゲーム内コマンド
| コマンド | 説明 | 権限 |
|---------|-------------|------------|
| `/chat help` | プラグインヘルプを表示 | `nekochat.use` |
| `/chat reload` | 設定を再読み込み（管理者） | `nekochat.admin` |
| `/msg <player> <message>` | プライベートメッセージを送信 | `nekochat.msg` |
| `/reply <message>` | 最後のPMに返信 | `nekochat.msg` |
| `/togglechat [global\|local\|all]` | チャンネルを有効/無効化（管理者） | `nekochat.admin` |
| `/ignore <player>` | プレイヤーを無視 | `nekochat.ignore` |
| `/unignore <player>` | プレイヤーの無視を解除 | `nekochat.ignore` |
| `/ignorelist` | 無視しているプレイヤーを一覧表示 | `nekochat.ignore` |

### 権限ノード
- `nekochat.use` - チャットコマンドの基本権限
- `nekochat.admin` - 完全な管理アクセス（以下のすべてを含む）
- `nekochat.mention` - @メンションの使用を許可（`require-permission`がtrueの場合）
- `nekochat.msg` - プライベートメッセージの送信を許可
- `nekochat.ignore` - 無視システムの使用を許可

---

## 🚨 重要な注意点

### ⚠️ 要件
1. **Java 21必須**: 最低Javaバージョンは21です
2. **Minecraft 1.21.1以上**: 現代のMinecraftバージョン向けに構築
3. **Foliaサポート**: Bukkit/PaperとFoliaサーバーの両方と完全互換

### 🔌 ソフト依存
- **LuckPerms**: `<luckperms_prefix>` と `<luckperms_suffix>` プレースホルダーを有効化
- **PlaceholderAPI**: フォーマットで任意のPlaceholderAPIプレースホルダーを使用可能に

---

## 🌐 新しい言語の追加

1.  `plugins/NekoChat/lang/` ディレクトリに移動
2.  テンプレートとして `en-US.yml` をコピー
3.  言語コードにリネーム（例: `fr-FR.yml`, `de-DE.yml`）
4.  **MiniMessage形式** (`<color>`タグ) を使用してすべての値を翻訳
5.  `config.yml` の `language` 設定を更新するか、自動検出に依存

**MiniMessageタグの例:**
- `<red>` - 赤いテキスト
- `<green>` - 緑のテキスト
- `<yellow>` - 黄色のテキスト
- `<gray>` - 灰色のテキスト
- `<gold>` - 金色のテキスト
- `<bold>` - **太字テキスト**
- `<click:open_url:'https://...'>` - クリック可能なリンク
- `<hover:show_text:'<green>ホバーテキスト'>` - ホバーツールチップ

---

## 🏗️ ソースからのビルド

```bash
# リポジトリをクローン
git clone https://github.com/hanamuramiyu/NekoChat.git
cd NekoChat

# プラグインをビルド
./gradlew build

# 出力ファイル: build/libs/NekoChat-1.0.0.jar
```

**要件:**
- Java 21 JDK
- Gradle 9.2.0+

---

## 🤝 貢献

貢献を歓迎します！以下の手順に従ってください:
1. リポジトリをフォーク
2. 機能ブランチを作成
3. 変更を加える
4. プルリクエストを提出

コードが既存のスタイルに従い、適切なテストを含んでいることを確認してください。

---

## 🐛 問題の報告

バグを見つけた場合や機能リクエストがある場合は:
1. 既存の [Issues](https://github.com/hanamuramiyu/NekoChat/issues) を確認
2. 明確な説明を含む新しいissueを作成
3. サーバーログと設定詳細を含める
4. サーバータイプとバージョンを指定

---

## 📄 ライセンス

このプロジェクトはGNU General Public License v3.0の下で公開されています - 詳細については[LICENSE](LICENSE)ファイルを参照してください。

---

<div align="center">

**Hanamura Miyu によって ❤️ を込めて作られました**

*あなたのサーバーのチャットに、メッセージごとに命を吹き込みます。*

</div>