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

## 安装

该 mod 依赖 malilib, minihud, fabric-api

若出现报错请前往 masa 官网 https://masa.dy.fi/mcmods/client_mods/ 下载最新的 minihud 和 maillib

在以下版本测试通过：

```
# 1.16.x
malilib_version = 0.10.0-dev.21+arne.1
minihud_version = 0.19.0-dev.20200720.162605

# 1.15.x
malilib_version = 0.10.0-dev.21+arne.1
minihud_version = 0.19.0-dev.20200508.032934

# 1.14.x
malilib_version = 0.10.0-dev.20+arne.1
minihud_version = 0.19.0-dev.20200424.001737
```
