# Masa Gadget
[![License](https://img.shields.io/github/license/plusls/MasaGadget?style=flat-square)](https://github.com/plusls/MasaGadget/blob/main/LICENSE)
[![Issues](https://img.shields.io/github/issues/plusls/MasaGadget?style=flat-square)](https://github.com/plusls/MasaGadget/issues)
[![Pull Requests](https://img.shields.io/github/issues-pr/plusls/MasaGadget?style=flat-square)](https://github.com/plusls/MasaGadget/pulls)
[![Java CI with gradle](https://img.shields.io/github/workflow/status/plusls/MasaGadget/build?label=Build&style=flat-square)](https://github.com/plusls/MasaGadget/actions/workflows/build.yml)
[![Publish Release](https://img.shields.io/github/workflow/status/plusls/MasaGadget/Publish%20Release?label=Publish%20Release&style=flat-square)](https://github.com/plusls/MasaGadget/actions/workflows/publish.yml)
[![Release](https://img.shields.io/github/v/release/plusls/MasaGadget?include_prereleases&style=flat-square)](https://github.com/plusls/MasaGadget/releases)
[![Github Release Downloads](https://img.shields.io/github/downloads/plusls/MasaGadget/total?label=Github%20Release%20Downloads&style=flat-square)](https://github.com/plusls/MasaGadget/releases)

[English](./README_EN.md)

为 Masa 系列模组添加了一些特性。

默认使用 **G + C** 打开设置界面。

## 依赖项

| 依赖                   | 类型 | 下载                                                                                                                                                |
| ---------------------- | ---- | --------------------------------------------------------------------------------------------------------------------------------------------------- |
| Fabric-API             | 必须 | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/fabric-api) &#124; [Github](https://github.com/FabricMC/fabric)                           |
| Litematica             | 可选 | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/litematica) &#124; [Masa WebSite](https://masa.dy.fi/mcmods/client_mods/?mod=litematica)  |
| MaliLib                | 必须 | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/malilib) &#124; [Masa WebSite](https://masa.dy.fi/mcmods/client_mods/?mod=malilib)        |
| MiniHUD                | 可选 | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/minihud) &#124; [Masa WebSite](https://masa.dy.fi/mcmods/client_mods/?mod=minihud)        |
| Plusls-Carpet-Addition | 可选 | [Github](https://github.com/plusls/plusls-carpet-addition)                                                                                          |
| Tweakeroo              | 可选 | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/tweakeroo) &#124; [Masa WebSite](https://masa.dy.fi/mcmods/client_mods/?mod=tweakeroo)    |


## 特性

### 全局

- 打开设置界面(openConfigGui)

    - 打开设置界面的快捷键。

- 调试模式(debug)

    - 开启后将会打印调试日志。

### Litematica

- 修复精准放置协议(fixAccurateProtocol)

    - Litematica 本身的精准放置协议实现的有问题，修复后可以在服务器中使用轻松放置来摆放中继器，活塞，侦测器，楼梯等方块.

- 移动投影时支持灵魂出窍(nudgeSelectionSupportFreeCamera)

    - 在开启 Free Camera 时，移动投影的方向会取决于 Camera 的方向。

- 服务器中保存投影保留容器数据(saveInventoryToSchematicInServer)

    - 保存投影时会使用 PCA 同步协议来同步容器中的数据到本地。

### Malilib

- 修复 Masa 配置文本的宽度(fixConfigWidgetWidth)

    - 在使用汉化包后现在的界面会根据汉化后的文本长度自动调节。

- 修复容器类型(fixGetInventoryType)

    - 修复对烟熏炉之类的熔炉变种的容器类型的判断。

- Masa 搜索优化(optimizeConfigWidgetSearch)

    - 在 Masa 家的搜索栏中可以使用中文搜索，还能使用 **modified** 来搜索修改过的配置项

### MiniHUD

- 兼容 BBOR 协议(compactBborProtocol)

    - 解析 BBOR 的协议并将结构数据发送给 MiniHUD，可以在未安装 servux 的服务器上使用 MiniHUD 的查看结构功能。

- PCA同步协议同步蜂巢数据(pcaSyncProtocolSyncBeehive)

    - 按下 **容器预览** 的快捷键将会使用 PCA 同步协议来 同步蜂巢，蜂箱数据。

### Tweakeroo

- 容器预览支持灵魂出窍(inventoryPreviewSupportFreeCamera)

    - 玩家可以在开启灵魂出窍的情况下使用容器预览。

- 容器预览支持预览玩家(inventoryPreviewSupportPlayer)

    - 对着玩家使用容器预览时，会显示出玩家背包和末影箱的内容。

- 容器预览支持选中格子(inventoryPreviewSupportSelect)

    - 按下容器预览快捷键时可以使用鼠标滚轮来选中物品（可以查看附魔信息），在选中箱子中的潜影盒时可以按下鼠标中键来预览盒子内的物品。

- PCA 同步协议(pcaSyncProtocol)

    - 使用 PCA 同步协议来从服务器同步方块实体信息和实体信息，比如箱子内的物品，村民背包。

## 许可

此项目在 CC0许可证 下可用。 从中学习，并将其融入到您自己的项目中。