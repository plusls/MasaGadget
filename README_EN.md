# Masa Gadget

[![License](https://img.shields.io/github/license/plusls/MasaGadget?style=flat-square)](https://github.com/plusls/MasaGadget/blob/main/LICENSE)
[![Issues](https://img.shields.io/github/issues/plusls/MasaGadget?style=flat-square)](https://github.com/plusls/MasaGadget/issues)
[![Pull Requests](https://img.shields.io/github/issues-pr/plusls/MasaGadget?style=flat-square)](https://github.com/plusls/MasaGadget/pulls)
[![Java CI with gradle](https://img.shields.io/github/workflow/status/plusls/MasaGadget/build?label=Build&style=flat-square)](https://github.com/plusls/MasaGadget/actions/workflows/build.yml)
[![Publish Release](https://img.shields.io/github/workflow/status/plusls/MasaGadget/Publish%20Release?label=Publish%20Release&style=flat-square)](https://github.com/plusls/MasaGadget/actions/workflows/publish.yml)
[![Release](https://img.shields.io/github/v/release/plusls/MasaGadget?include_prereleases&style=flat-square)](https://github.com/plusls/MasaGadget/releases)
[![Github Release Downloads](https://img.shields.io/github/downloads/plusls/MasaGadget/total?label=Github%20Release%20Downloads&style=flat-square)](https://github.com/plusls/MasaGadget/releases)

[中文](./README.md)

Added some features to the Masa collection of mods.

The default hotkey to open the in-game config GUI is **G + C**.

## Dependencies

| Dependency             | Type     | Download                                                                                                                                            |
| ---------------------- | -------- | --------------------------------------------------------------------------------------------------------------------------------------------------- |
| Fabric-API             | Required | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/fabric-api) &#124; [Github](https://github.com/FabricMC/fabric)                           |
| Litematica             | Optional | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/litematica) &#124; [Masa WebSite](https://masa.dy.fi/mcmods/client_mods/?mod=litematica)  |
| MaliLib                | Required | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/malilib) &#124; [Masa WebSite](https://masa.dy.fi/mcmods/client_mods/?mod=malilib)        |
| MiniHUD                | Optional | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/minihud) &#124; [Masa WebSite](https://masa.dy.fi/mcmods/client_mods/?mod=minihud)        |
| Plusls-Carpet-Addition | Optional | [Github](https://github.com/plusls/plusls-carpet-addition)                                                                                          |
| Tweakeroo              | Optional | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/tweakeroo) &#124; [Masa WebSite](https://masa.dy.fi/mcmods/client_mods/?mod=tweakeroo)    |


## Feature

### Generic

- openConfigGui

    - A hotkey to open the in-game Config GUI.

- debug

    - Display debug message.

### Litematica

- fixAccurateProtocol

    - Fix accurate protocol support.

- nudgeSelectionSupportFreeCamera

    - Nudge Selection Support Free Camera.

- saveInventoryToSchematicInServer

    - Use pcaSyncProtocol to sync inventory data to local and save to schematic.

- useRelativePath

    - Save and load schematic use relative path

### Malilib

- fixConfigWidgetWidth

    - Use the length of getTranslatedGuiDisplayName as widget width.

- fixGetInventoryType

    - Fix AbstractFurnaceBlock inventory type.

- optimizeConfigWidgetSearch

    - Make search support uppercase, translate text, and can use **modified** to search modified config.

### MiniHUD

- compactBborProtocol

    - Parse bbor protocol and send data to minihud.

- pcaSyncProtocolSyncBeehive

    - Press **inventoryPreview** hotkey to use PCA sync protocol to sync Beehive.

### Tweakeroo

- autoSyncTradeOfferList

    - Auto sync the trade offer list of villager

- inventoryPreviewSupportFreeCamera

    - Player can use inventory preview on free camera.

- inventoryPreviewSupportPlayer

    - Player can use inventory preview to show the inventory of player and ender chest inventory.

- inventoryPreviewSupportSelect

    - When player press the inventory preview hotkey, player can use the mouse wheel to select items (player can view
      the enchantment information), and when player select the shulker box in the chest, player can press the mouse
      scroll to preview the items in the box.

- inventoryPreviewSupportTradeOfferList

    - Inventory preview will preview the trade offer list of merchant

- pcaSyncProtocol

    - Use pcaSyncProtocol to sync block entity data and entity data from server, such as chest, villager.

- renderTradeEnchantedBook

    - Render trade enchanted book text on villager.
    - good trader：
    - ![good trader](./docs/img/good_trader.png)
    - mid trader：
    - ![mid trader](./docs/img/mid_trader.png)
    - bad trader：
    - ![bad trader](./docs/img/bad_trader.png)