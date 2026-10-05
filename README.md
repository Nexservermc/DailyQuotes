# DailyQuotes · 每日一言

> 一款轻量的 Paper 服务端趣味插件：**玩家进服的每日一言 Title + 整点报时 + 入服公告**，全部支持 MiniMessage 富文本。

[![Platform](https://img.shields.io/badge/Platform-Paper-blue)](https://papermc.io)
[![API](https://img.shields.io/badge/API-1.21%2B-green)](#适用环境)
[![Java](https://img.shields.io/badge/Java-21-orange)](#适用环境)
[![Version](https://img.shields.io/badge/Version-1.2.0-brightgreen)](#更新日志)

---

## 简介

DailyQuotes（每日一言）会在玩家进服时，随机从名言库中挑选一句名言，以 **Title** 的形式展示在玩家屏幕中央：主标题显示名言句子，副标题显示作者。除此之外，它还自带**整点报时**和**入服公告**两个小功能，让你的服务器多一点温度。

名言库内置 125 条中外名句（先秦诸子、唐诗宋词、劝学惜时、世界名言等），全部可在 `config.yml` 中自由增删改。所有消息均支持 Bukkit 传统颜色码、十六进制颜色与 MiniMessage，并可调用 PlaceholderAPI 占位符。

---

## 功能特性

| 功能 | 说明 |
| --- | --- |
| 🎬 每日一言 | 玩家进服时以 Title 随机展示名言；自动按 `——` 拆分「句子」与「作者」，无作者的名言不会显示空副标题 |
| 🕐 整点报时 | 每小时整点向全服播报一次（同一小时只发一次，不会重复），支持 `{hour}`/`{minute}`/`{time}` 变量 |
| 📢 入服公告 | 玩家进服时按配置逐行发送公告，适合放规则、群号、网址等信息 |
| 🎨 富文本 | 原生支持 `&a` 传统颜色码、`&#FFAA00` 十六进制、`&x&F&F&A&A&0&0`、`<gradient:gold:yellow>` 等 MiniMessage 标签 |
| 🔌 PlaceholderAPI | 已安装则自动启用（软依赖，不装也不会报错），可在任意消息中使用 PAPI 占位符 |
| ⚡ 轻量 | 无需任何前置依赖，单 jar 即插即用；控制台启动横幅自动转 ANSI 彩色输出 |
| 🔄 热重载 | `/dailyquotes reload` 即时重载配置，无需重启服务器 |

---

## 适用环境

| 项 | 要求 |
| --- | --- |
| 服务端 | [Paper](https://papermc.io/downloads/paper) 1.21+（`api-version: 1.21`） |
| Java | **Java 21** 或更高 |
| 软依赖 | PlaceholderAPI（可选） |

> 因为使用了 Adventure 的 Title API，本插件面向 Paper 系服务端；不建议在纯 Spigot / CraftBukkit 上使用。

---

## 安装

1. 下载 `DailyQuotes.jar`，放入服务端的 `plugins/` 目录；
2. （可选）安装 [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)；
3. 重启服务器（或 `/reload confirm`）；
4. 进服即可看到每日一言，控制台打印彩色启动横幅。

---

## 配置说明

配置文件：`plugins/DailyQuotes/config.yml`

### 1. 每日一言 `daily-message`

```yaml
daily-message:
  enabled: true
  # 主标题显示名言句子，副标题显示作者
  title: "<gold><bold>%quote_sentence%</bold></gold>"
  subtitle: "<gray>—— %quote_author%</gray>"
  fade-in: 10     # 淡入（单位：tick，20 tick = 1 秒）
  stay: 80        # 停留
  fade-out: 20    # 淡出
  quotes:
    - "千里之行，始于足下。"
    - "长风破浪会有时。——李白"
    - "知识就是力量。——培根"
```

标题 / 副标题可用占位符：

| 占位符 | 含义 |
| --- | --- |
| `%quote%` / `{quote}` | 完整名言（句子 + 作者） |
| `%quote_sentence%` / `{quote_sentence}` | 名言句子部分 |
| `%quote_author%` / `{quote_author}` | 名言作者部分（**无作者的名言不会显示副标题**） |

名言书写格式：`句子——作者`（支持 `——`、`—`、`--` 三种分隔符）；不写作者则副标题自动留空。

### 2. 整点报时 `hourly-broadcast`

```yaml
hourly-broadcast:
  enabled: true
  # 可用变量：{hour}、{minute}、{time}，也支持 %hour%、%minute%、%time%
  message: "<gold>叮咚！现在是 <yellow>{hour}</yellow> 点整！愿你拥有美好的一小时。</gold>"
```

### 3. 入服公告 `join-announcement`

```yaml
join-announcement:
  enabled: true
  messages:
    - "<gradient:gold:yellow>━━━━━━━━━━━━━━━━━━━━━━━━</gradient>"
    - "<gold>欢迎来到服务器！</gold> <gray>祝你游戏愉快，%player_name%！</gray>"
    - "<gradient:gold:yellow>━━━━━━━━━━━━━━━━━━━━━━━━</gradient>"
```

> `%player_name%` 属于 PlaceholderAPI 占位符；未安装 PAPI 时会被原样显示为文本，此时请改用支持内置变量的写法，或直接安装 PAPI。

### 颜色格式支持

```yaml
# 1. Bukkit 传统颜色码
message: "&a绿色文本 &l加粗"

# 2. 十六进制颜色（两种写法均可）
message: "&#FFAA00橙色文本"
message: "<#FFAA00>橙色文本"

# 3. MiniMessage
message: "<gradient:gold:yellow>渐变文字</gradient>"
message: "<bold><color:#00FFAA>加粗自定义色</color></bold>"
```

---

## 命令与权限

| 命令 | 说明 | 权限 |
| --- | --- | --- |
| `/dailyquotes reload`（别名 `/dq`） | 重载 `config.yml` 配置 | `dailyquotes.admin`（默认 OP） |

---


