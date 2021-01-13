# Masa Gadget

给 Masa 全家桶添加了一些 Feature

## 功能

功能如下

### 优化搜索框

如今 masa 全家桶比较方便的汉化方式为资源包汉化, malilib 创建的搜索框默认情况下不支持搜索汉化后的文本。安装此 mod 后 masa 全家桶可以搜索汉化后的文本。

同时搜索时支持大小写混用，搜索 modified 可以搜索到修改过的配置项

### 配置显示界面优化

malilib 创建的配置界面没有考虑到汉化的情况，在加载汉化资源包后可能会出现对齐异常，文本显示不全的问题。安装 mod 后优化了对齐的代码，让文本能显示正常。

### MiniHUD 支持 安装了 bbor 的服务端

minihud 的查看结构和史莱姆区块渲染需要服务端安装了 carpet 或者 servux 才能正常使用（史莱姆区块可以手动输入种子），在安装此 mod 后，若是服务器未安装上述 2个 mod 但是安装了 bbor， 该 mod 可以自动从服务器获取 bbor 的数据并导入 minihud，从而让 minihud 在多人游戏中也能渲染结构和史莱姆区块。 

### 多人游戏容器预览

tweakeroo 的容器预览功能在多人游戏中是不可用的，在安装此 mod 后可以让该功能在多人游戏中使用

现已兼容箱子矿车，漏斗矿车

需要服务端安装 [plusls-carpet-addition](https://github.com/plusls/plusls-carpet-addition) 并开启 pcaSyncProtocol

### 灵魂出窍支持容器预览

tweakeroo 的灵魂出窍默认情况下是不支持容器预览的，在安装此 mod 后可以在灵魂出窍的情况下预览容器的内容

### 容器预览支持玩家存储

容器预览可以显示玩家背包和末影箱（服务器不一定可用）

### 多人游戏蜜蜂数量预览

MiniHUD 支持查看蜂箱内的蜜蜂数量，但是在多人游戏会失效。在安装此 mod 后，按下容器预览的快捷键即可同步蜜蜂数量

需要服务端安装 plusls-carpet-addition 并开启 pcaSyncProtocol


### 兼容 multiconnect

最新版本的 Masa Gadget 已经兼容了由 EarthComputer 编写的 multiconnect mod, Masa Gadget 支持跨版本使用，即从高版本客户端连接到低版本服务器

出于维护方便的考虑不再更新 1.14，1.15 的 Masa Gadget

### 投影 mod 多人游戏轻松放置修复

投影 mod 的轻松放置功能在多人游戏中存在 bug，无法正确识别部分物品的方向，现已修复

## 安装

该 mod 依赖 fabric-api >= 0.28.0

若出现报错请前往 masa 官网 https://masa.dy.fi/mcmods/client_mods/ 下载最新的 minihud 和 maillib

已经测试的版本：

```
malilib_version >= 0.10.0-dev.21+arne.2
minihud_version >= 0.19.0-dev.20201103.184029
tweakeroo_version >= 0.10.0-dev.20201103.184154
```
