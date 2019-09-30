# Spring Boot

> 项目主要内容是工作中遇到的一些问题和对于问题的总结，删除了复杂的业务逻辑，提供可读性高的demo，用于学习交流。  

## 热点商品抢购

- 热点商品缓存预热
- 根据商品ID获取商品详情
- 删除热点商品
- 并发修改商品详情  

### 热点商品缓存预热

> 新增热点商品时，先将商品入库，再写缓存，进行缓存预热。 

![缓存预热](https://github.com/oub9527/springboot/blob/master/src/main/resources/images/缓存预热.png)

### ID获取商品详情

> 先从缓存中获取，命中则直接返回，否则，用Redis获取分布式锁，保证同一时间只有一个线程可以获取得到锁资源，获取锁的线程再次从缓存中获取，获取到资源直接返回，这里是为了防止其他线程已经将资源写入缓存，避免多余的数据库请求，获取不到再请求数据库，资源不为空，写缓存，返回结果，资源为空，为了避免缓存穿透，构造一个默认资源，写入缓存，返回空。

![ID获取缓存](https://github.com/oub9527/springboot/blob/master/src/main/resources/images/ID获取缓存.png)

### 更新热点商品

> 更新热点商品需要考虑到双写不一致的问题。最经典的缓存+数据库读写的模式，就是Cache Aside Pattern。
>
> - 读的时候，先读缓存，未命中情况下，读数据库，然后写缓存，返回响应。
> - 更新的时候，**先更新数据库，再删除缓存**。  
>
> 流程：
>
> - 更新数据库，失败直接返回
> - 更新缓存，成功返回，失败后将资源详情发送到消息队列，并返回资源详情
> - 队列消费者消费消息，消费成功更新缓存
>
> **注意点：**
>
> - 删除缓存失败是小概率事件，出现不一致问题主要是在更新数据库成功之后，和删除缓存之前，如果有请求从缓存中获取资源，可能导致数据不一致。
> - 一般来说，如果不允许缓存和数据库偶尔存在的数据不一致情况，则不能用此方案，因为在消费者消费消息之前，缓存中的数据是脏数据，这时候如果直接从缓存中获取资源的话就导致数据不一致。
> - 如果要求缓存和数据库的强一致性，需要将**读请求和写请求串行化**，串到一个内存队列中去，这样可以保证不会出现数据不一致的情况，但是会导致系统的吞吐量大幅度降低。

![更新商品](https://github.com/oub9527/springboot/blob/master/src/main/resources/images/更新商品.png)

## 订单

- 商品创建订单

### 创建订单

> 主要是为了解决热点商品超卖问题。
>
> 流程：
>
> - 获取Redis分布式锁，获取到锁的同时给锁加上超时时间，防止获取到分布式锁的线程意外挂起，导致其他线程无法获取到锁的情况，并且只有获取得到分布式锁的线程才能够创建订单。
> - 先从缓存中获取资源对象，如果对象为空，则请求数据库，满足条件将商品剩余数量减1，并更新商品信息到缓存中，如果数据库不存在该对象，为了避免缓存穿透，也将默认商品信息写入缓存。
> - 如果缓存中资源对象不为空，则更新剩余商品数量减1，并更新缓存，同时发送订单过期信息到延时消息队列。
> - 延时队列消费消息的时候判断订单状态是否已经改变，如果订单状态还是未支付，则失效该笔订单。
>
> **优化：**
>
> - 缓存中需要维护商品数量信息，其实不是很有必要，如果外层能控制得住并发的话，获取锁的线程直接访问数据库就行，这里加缓存是为了避免多余的数据库访问，但是增加了系统的复杂度，需要维护数据库和缓存的数据一致性。
> - 可以使用异步创建订单的方式，增加用户体验，等需要付款查询订单的时候再去判断订单是否生成成功。
> - 获取不到锁的线程可以采用自旋的方式尝试再次获取锁，超出重试次数未获取到就获取失败，但是其实这也无伤大雅，也就是用户再界面多执行几次创建订单的操作。

![创建订单](https://github.com/oub9527/springboot/blob/master/src/main/resources/images/创建订单.png)