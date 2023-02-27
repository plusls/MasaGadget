# Masa Gadget

[![License](https://img.shields.io/github/license/plusls/MasaGadget?style=flat-square)](https://github.com/plusls/MasaGadget/blob/main/LICENSE)
[![Issues](https://img.shields.io/github/issues/plusls/MasaGadget?style=flat-square)](https://github.com/plusls/MasaGadget/issues)
[![Pull Requests](https://img.shields.io/github/issues-pr/plusls/MasaGadget?style=flat-square)](https://github.com/plusls/MasaGadget/pulls)
[![CI](https://img.shields.io/github/actions/workflow/status/plusls/MasaGadget/build.yml?label=Build&style=flat-square)](https://github.com/plusls/MasaGadget/actions/workflows/build.yml)
[![Publish Release](https://img.shields.io/github/actions/workflow/status/plusls/MasaGadget/publish.yml?label=Publish%20Release&style=flat-square)](https://github.com/plusls/MasaGadget/actions/workflows/publish.yml)
[![Release](https://img.shields.io/github/v/release/plusls/MasaGadget?include_prereleases&style=flat-square)](https://github.com/plusls/MasaGadget/releases)
[![Github Release Downloads](https://img.shields.io/github/downloads/plusls/MasaGadget/total?label=Github%20Release%20Downloads&style=flat-square)](https://github.com/plusls/MasaGadget/releases)

[中文](./README.md)

Added some features to the Masa collection of mods.

The default hotkey to open the in-game config GUI is **G + C**.

## Dependencies

| Dependency             | Type     | Download                                                                                                                                           |
|------------------------|----------|----------------------------------------------------------------------------------------------------------------------------------------------------|
| Fabric-API             | Required | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/fabric-api) &#124; [Github](https://github.com/FabricMC/fabric)                          |
| Litematica             | Optional | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/litematica) &#124; [Masa WebSite](https://masa.dy.fi/mcmods/client_mods/?mod=litematica) |
| Magiclib               | Required | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/magiclib) &#124; [Github](https://github.com/Hendrix-Shen/Magiclib)                      |
| MaliLib                | Required | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/malilib) &#124; [Masa WebSite](https://masa.dy.fi/mcmods/client_mods/?mod=malilib)       |
| MiniHUD                | Optional | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/minihud) &#124; [Masa WebSite](https://masa.dy.fi/mcmods/client_mods/?mod=minihud)       |
| Plusls-Carpet-Addition | Optional | [Github](https://github.com/plusls/plusls-carpet-addition)                                                                                         |
| Tweakeroo              | Optional | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/tweakeroo) &#124; [Masa WebSite](https://masa.dy.fi/mcmods/client_mods/?mod=tweakeroo)   |

## Feature
## Generic
## autoSyncEntityData
Auto use pcaSyncProtocol to sync entity data

- Category: `Generic`
- Type: `boolean`
- Default value: `true`
## cacheContainerMenu
Cache container menu, minecraft will cache inventory data from server when open inventory

- Category: `Generic`
- Type: `boolean with hotkey`
- Default value: `true`, `no hotkey`
## debug
Display debug message

- Category: `Generic`
- Type: `boolean`
- Default value: `false`
## openConfigGui
A hotkey to open the in-game Config GUI

- Category: `Generic`
- Type: `hotkey`
- Default value: `G,C`
## renderNextRestockTime
Render next restock time on villager.

- Category: `Generic`
- Type: `boolean with hotkey`
- Default value: `true`, `no hotkey`
- Dependencies:
  - And (All conditions need to be satisfied):
    - tweakeroo: *

## renderTradeEnchantedBook
Render trade enchanted book text on villager.



- Category: `Generic`
- Type: `boolean with hotkey`
- Default value: `true`, `no hotkey`
- Dependencies:
  - And (All conditions need to be satisfied):
    - tweakeroo: *

good trader:

![good trader](./docs/img/good_trader.png)

mid trader:

![mid trader](./docs/img/mid_trader.png)

bad trader:

![bad trader](./docs/img/bad_trader.png)

## renderZombieVillagerConvertTime
Render zombie villager convert time on zombie villager.

- Category: `Generic`
- Type: `boolean with hotkey`
- Default value: `true`, `no hotkey`
- Dependencies:
  - And (All conditions need to be satisfied):
    - tweakeroo: *

## searchMobSpawnPoint
Search mob spawn point. Need set §6Despawn Sphere§r in minihud,

and light check depends on §6lightLevelThresholdSafe§r in minihud

- Category: `Generic`
- Type: `hotkey`
- Default value: `no hotkey`
- Dependencies:
  - And (All conditions need to be satisfied):
    - minihud: *

## searchMobSpawnPointBlackList
Don't search blocks in this list.

- Category: `Generic`
- Type: `string list`
- Default value: `[]`
- Dependencies:
  - And (All conditions need to be satisfied):
    - minihud: *

## syncAllEntityData
Use pcaSyncProtocol to sync all entity data.

- Category: `Generic`
- Type: `hotkey`
- Default value: `no hotkey`
## Litematica
## betterEasyPlaceMode
Easy place mode can open inventory (such as chest, hopper, etc.),

and can use beacon.

- Category: `Litematica`
- Type: `boolean with hotkey`
- Default value: `false`, `no hotkey`
- Dependencies:
  - And (All conditions need to be satisfied):
    - litematica: *

## disableLitematicaEasyPlaceFailTip
Disable easyPlace failure that annoying prompt window.

- Category: `Litematica`
- Type: `boolean with hotkey`
- Default value: `false`, `no hotkey`
- Dependencies:
  - And (All conditions need to be satisfied):
    - litematica: *

## fixAccurateProtocol
Fix accurate protocol support

- Category: `Litematica`
- Type: `boolean with hotkey`
- Default value: `false`, `no hotkey`
- Dependencies:
  - And (All conditions need to be satisfied):
    - litematica: *

## nudgeSelectionSupportFreeCamera
Nudge Selection Support Free Camera

- Category: `Litematica`
- Type: `boolean`
- Default value: `true`
- Dependencies:
  - And (All conditions need to be satisfied):
    - litematica: *
    - tweakeroo: *
    - minecraft: <=1.15.2

## saveInventoryToSchematicInServer
Use pcaSyncProtocol to sync inventory data to local and save to schematic

- Category: `Litematica`
- Type: `boolean`
- Default value: `false`
- Dependencies:
  - And (All conditions need to be satisfied):
    - litematica: *

## useRelativePath
Save and load schematic use relative path

- Category: `Litematica`
- Type: `boolean`
- Default value: `false`
- Dependencies:
  - And (All conditions need to be satisfied):
    - litematica: *
## Malilib
## backportI18nSupport
Backport masa mod i18n support from 1.18.x

- Category: `Malilib`
- Type: `boolean`
- Default value: `true`
- Dependencies:
  - And (All conditions need to be satisfied):
    - minecraft: <=1.17.1

## fastSwitchMasaConfigGui
Render drop down list widget, player can switch other masa's mod config gui quickly.

- Category: `Malilib`
- Type: `boolean with hotkey`
- Default value: `true`, `no hotkey`
- Dependencies:
  - And (All conditions need to be satisfied):
    - modmenu: *

## favoritesSupport
Users can favorite and filter their frequently used options.

- Category: `Malilib`
- Type: `boolean with hotkey`
- Default value: `false`, `no hotkey`
## fixConfigWidgetWidth
Use the length of getTranslatedGuiDisplayName as widget width

- Category: `Malilib`
- Type: `boolean`
- Default value: `true`
- Dependencies:
  - And (All conditions need to be satisfied):
    - minecraft: <=1.17.1

## fixGetInventoryType
Fix AbstractFurnaceBlock inventory type

- Category: `Malilib`
- Type: `boolean`
- Default value: `true`
- Dependencies:
  - And (All conditions need to be satisfied):
    - minecraft: <=1.17.1

## fixSearchbarHotkeyInput
Fix searchbar input when press hotkey to open config gui.

- Category: `Malilib`
- Type: `boolean`
- Default value: `true`
## optimizeConfigWidgetSearch
Make search support uppercase, translate text, and can use §6modified§r to search modified config

- Category: `Malilib`
- Type: `boolean`
- Default value: `true`
- Dependencies:
  - And (All conditions need to be satisfied):
    - minecraft: <=1.17.1

## showOriginalConfigName
Show original config name when config gui display name no the same as original config name.

- Category: `Malilib`
- Type: `boolean with hotkey`
- Default value: `false`, `no hotkey`
## showOriginalConfigNameScale
Original config name font scale.

- Category: `Malilib`
- Type: `double`
- Default value: `0.65`
- Min value: `0.0`
- Max value: `2.0`
## Minihud
## minihudI18n
Minihud display text support i18n translate.

- Category: `Minihud`
- Type: `boolean with hotkey`
- Default value: `true`, `no hotkey`
- Dependencies:
  - And (All conditions need to be satisfied):
    - minihud: *

## pcaSyncProtocolSyncBeehive
Press §6inventoryPreview§r hotkey to use PCA sync protocol to sync Beehive

- Category: `Minihud`
- Type: `boolean`
- Default value: `true`
- Dependencies:
  - And (All conditions need to be satisfied):
    - minihud: *
    - tweakeroo: *
    - minecraft: >1.14.4
## Tweakeroo
## inventoryPreviewSupportComparator
Inventory preview will render the output of comparator.

- Category: `Tweakeroo`
- Type: `boolean`
- Default value: `true`
- Dependencies:
  - And (All conditions need to be satisfied):
    - tweakeroo: *

## inventoryPreviewSupportPlayer
Player can use inventory preview to show the inventory of player and ender chest inventory

- Category: `Tweakeroo`
- Type: `boolean`
- Default value: `true`
- Dependencies:
  - And (All conditions need to be satisfied):
    - tweakeroo: *

## inventoryPreviewSupportSelect
When player press the inventory preview hotkey,

player can use the mouse wheel to select items (player can view the enchantment information), and when player select the shulker box in the chest, player can press the mouse scroll to preview the items in the box

- Category: `Tweakeroo`
- Type: `boolean with hotkey`
- Default value: `true`, `no hotkey`
- Dependencies:
  - And (All conditions need to be satisfied):
    - tweakeroo: *

## inventoryPreviewSupportShulkerBoxItemEntity
Inventory preview support shulker box item entity.

- Category: `Tweakeroo`
- Type: `boolean`
- Default value: `true`
- Dependencies:
  - And (All conditions need to be satisfied):
    - tweakeroo: *

## inventoryPreviewSupportTradeOfferList
Inventory preview will preview the trade offer list of merchant

- Category: `Tweakeroo`
- Type: `boolean`
- Default value: `true`
- Dependencies:
  - And (All conditions need to be satisfied):
    - tweakeroo: *

## inventoryPreviewSyncData
When toggle inventory preview, use pcaSyncProtocol to sync block entity data and entity data from server, such as chest, villager

- Category: `Tweakeroo`
- Type: `boolean with hotkey`
- Default value: `true`, `no hotkey`
- Dependencies:
  - And (All conditions need to be satisfied):
    - tweakeroo: *

## inventoryPreviewSyncDataClientOnly
Open inventory to sync inventory data when use inventory preview

- Category: `Tweakeroo`
- Type: `boolean with hotkey`
- Default value: `false`, `no hotkey`
- Dependencies:
  - And (All conditions need to be satisfied):
    - tweakeroo: *

## inventoryPreviewUseCache
Inventory preview get the hit result from cache, to reduce lag when render

- Category: `Tweakeroo`
- Type: `boolean`
- Default value: `true`
- Dependencies:
  - And (All conditions need to be satisfied):
    - tweakeroo: *

## restockWithCrafting
Automatically craft required items in the 2x2 crafting grid

when Tweakeroo can't find the item. This feature requires ItemScroller。

- Category: `Tweakeroo`
- Type: `boolean with hotkey`
- Default value: `true`, `no hotkey`
- Dependencies:
  - And (All conditions need to be satisfied):
    - tweakeroo: *
    - itemscroller: *

## restockWithCraftingRecipes
Recipe indexes in ItemScroller recipe storage. Note: only 2x2 recipes are supported.

- Category: `Tweakeroo`
- Type: `string list`
- Default value: `[]`
- Dependencies:
  - And (All conditions need to be satisfied):
    - tweakeroo: *
    - itemscroller: *

## Development

### Support

Current main development for Minecraft version: 1.19.3

And use `preprocess` to be compatible with all versions.

**Note: We only accept the following versions of issues. Please note that this information is time-sensitive and any version of the issue not listed here will be closed**

- Minecraft 1.14.4
- Minecraft 1.15.2
- Minecraft 1.16.5
- Minecraft 1.17.1
- Minecraft 1.18.2
- Minecraft 1.19.2
- Minecraft 1.19.3

### Mappings

We are using the **Mojang official** mappings to de-obfuscate Minecraft and insert patches.

### Document

The English doc and the Chinese doc are aligned line by line.

## License

This project is available under the LGPLv3 license. Feel free to learn from it and incorporate it in your own projects.
