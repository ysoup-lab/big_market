# 抽奖API接口实现

## **流程设计**

大营销的系统架构设计中，有一个 trigger 模块，专门用于提供触发操作。这里我们把 HTTP 调用、RPC（Dubbo）调用、定时任务、MQ监听等动作，都称为触发操作。触发表示通过一种调用方式，调用到领域的服务上。

在大营销系统中，会给大家提供出 HTTP 接口，也会在后续提供 RPC 接口。RPC 就像 Dubbo 这样的框架，它的调用方式是需要对外提供接口描述性Jar，调用方拿到 Jar 包，就像本地调用接口一样，使用 RPC 框架，远程的调用到你的服务上。

那么因为为了让 HTTP 接口、RPC 接口，都能在一个标准下开发，所以本节会增加一个 big-market-api 模块，定义出接口信息和出入参对象。以便于分别可以实现本节所需的 HTTP 接口和后续所需的 RPC 接口。【注意；一般在大厂中，我们只需要定义 RPC 接口即可，因为 HTTP\小程序\APP 的接口，都是通过网关来调用的。网关会把 HTTP 请求转换为对应的 RPC 接口。

![img](https://article-images.zsxq.com/FkXmHnNYnMl8agoyWiUuxzcx1YE3)

1. 定义 big-market-api 模块，由 big-market-trigger 实现出3个接口；装配策略接口（调用后将抽奖策略装配到缓存）、查询奖品列表、随机抽奖接口。
2. 在 big-market-domain 的抽奖策略新增加 IRaffleAward 策略奖品接口，并调整 IRaffleStock 抽奖库存接口，都直接由子类实现。IRaffleStrategy 抽奖策略接口由抽象类定义抽奖过程【这部分是前面章节实现的】，并有子类实现。

## **功能实现**

### **1. 工程结构**

![img](https://article-images.zsxq.com/FurGUrQ1JtErr6HYvdW-B8-M2CUG)

定义 IRaffleService 接口，由 trigger 模块下的 http 层 RaffleController 实现接口。其中包含策略装配接口、查询抽奖奖品列表配置、随机抽奖接口接口层的实现，直接调用到 domain 领域层。也就是我们前面所实现的抽奖策略领域服务。【本节会对抽奖策略领域服务新增接口，做到单一职责的设计】

### **2. 抽奖策略**

![img](https://article-images.zsxq.com/FvX6S-xyd2V9NzbVU0wKVe34ldX-)

1. 新增加 IRaffleAward 策略奖品接口，查询奖品信息。让 DefaultRaffleStrategy 子类实现。【注意奖品查询会用到之前的接口，并做了新的字段的增加查询】
2. 调整 IRaffleStock 库存的处理接口，由子类实现。因为这两个接口，都不需要做抽象类的处理。

#### 

