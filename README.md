# Masa Gadget

给 Masa 全家桶添加了一些 Feature

## 功能

功能如下

### 优化搜索框

如今 masa 全家桶比较方便的汉化方式为资源包汉化, malilib 创建的搜索框默认情况下不支持搜索汉化后的文本。安装此 mod 后 masa 全家桶可以搜索汉化后的文本。

### 配置显示界面优化

malilib 创建的配置界面没有考虑到汉化的情况，在加载汉化资源包后可能会出现对齐异常，文本显示不全的问题。安装 mod 后优化了对齐的代码，让文本能显示正常。

### MiniHUD 支持 安装了 bbor 的服务端

minihud 的查看结构和史莱姆区块渲染需要服务端安装了 carpet 或者 servux 才能正常使用（史莱姆区块可以手动输入种子），在安装此 mod 后，若是服务器未安装上述 2个 mod 但是安装了 bbor， 该 mod 可以自动从服务器获取 bbor 的数据并导入 minihud，从而让 minihud 在多人游戏中也能渲染结构和史莱姆区块。 

### 多人游戏容器预览

tweakeroo 的容器预览功能在多人游戏中是不可用的，在安装此 mod 后可以让该功能在多人游戏中使用（需要服务端也安装 MasaGadget）

### 灵魂出窍支持容器预览

tweakeroo 的灵魂出窍默认情况下是不支持容器预览的，在安装此 mod 后可以在灵魂出窍的情况下预览容器的内容

### 多人游戏空潜影盒堆叠修复

carpet 的空潜影盒堆叠只能让空潜影盒在地面上堆叠，无法在容器内和背包内操作堆叠，服务端安装 mod 后会修复这个行为

只有玩家操作才能让潜影盒堆叠，例如手动堆叠，shift + 左键到容器，从地上拾起均会自动堆叠

但是不会影响投掷器和漏斗的行为，也不会影响比较器的输出

### 多人游戏蜜蜂数量预览

MiniHUD 支持查看蜂箱内的蜜蜂数量，但是在多人游戏会失效。服务端安装 mod 后需在客户端开启容器预览，按下容器预览的快捷键即可同步蜜蜂数量

有时候数量显示会有 bug，这是 MiniHUD 的问题，按 2 下 H 即可解决

数量显示不是即时同步的，按一下同步一次

## 安装

该 mod 依赖 malilib, minihud, fabric-api

若出现报错请前往 masa 官网 https://masa.dy.fi/mcmods/client_mods/ 下载最新的 minihud 和 maillib

在以下版本测试通过：

```
# 1.16.x
malilib_version = 0.10.0-dev.21+arne.1
minihud_version = 0.19.0-dev.20200928.220110
tweakeroo_version = 0.10.0-dev.20201001.000406

# 1.15.x
malilib_version = 0.10.0-dev.21+arne.1
minihud_version = 0.19.0-dev.20200508.032934

# 1.14.x
malilib_version = 0.10.0-dev.20+arne.1
minihud_version = 0.19.0-dev.20200424.001737
```
