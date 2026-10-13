下面把你给出的命令整理成一份 Redis 常用命令速查表。Redis 命令**大小写不敏感**，但 key 区分大小写；`--` 在 redis-cli 里只是注释。

| string      | 二进制安全、原子计数 | 验证码、Token、计数器  |
| ----------- | -------------------- | ---------------------- |
| hash        | 对象字段级读写       | 用户信息、购物车       |
| list        | 有序可重复，头尾快   | 队列、栈、最新列表     |
| set         | 去重、集合运算       | 关注、去重、抽奖、标签 |
| zset        | 有序去重，按分数排   | 排行榜、延迟任务       |
| geo         | 经纬度、半径查询     | 附近的人/门店          |
| hyperloglog | 基数统计、省内存     | UV、独立访客           |
| bitmap      | 位操作、极省内存     | 签到、在线状态、布隆   |
| stream      | 日志型消息队列       | 简单异步消息、日志     |
## 1. 连接与数据库

| 命令 | 说明 |
|---|---|
| `SELECT index` | 切换数据库，默认 `0`，如 `SELECT 1`、`SELECT 0` |
| `KEYS pattern` | 查找匹配 key，如 `KEYS *`。生产环境慎用，会阻塞 |
| `SCAN cursor MATCH pattern COUNT count` | 渐进式遍历 key，如 `SCAN 0 MATCH user:* COUNT 100`。返回新游标，游标为 0 表示结束 |
| `EXISTS key [key ...]` | 判断 key 是否存在，返回存在个数 |
| `TYPE key` | 返回 key 类型：`string/list/set/hash/zset/stream/none` |
| `TTL key` | 剩余过期时间，秒。`-1` 永不过期，`-2` key 不存在 |
| `EXPIRE key seconds` | 设置过期时间，单位秒 |
| `PERSIST key` | 移除过期时间，变为永久 |
| `RENAME key newkey` | 重命名 key，目标存在会覆盖 |
| `UNLINK key [key ...]` | 异步删除 key，适合大 key，非阻塞 |
| `DEL key [key ...]` | 同步删除 key，你列表里未出现，但常用 |

## 2. 字符串 String

| 命令 | 说明 |
|---|---|
| `SET key value` | 设置字符串值 |
| `SET key value EX seconds` | 设置值并指定过期秒数，如 `SET a 10 EX 200`、`SET token abc EX 60` |
| `SET key value NX` | 仅当 key 不存在时设置，如 `SET code 888 NX` |
| `SET key value XX` | 仅当 key 存在时设置，如 `SET vip 1 XX` |
| `SET key value GET` | 设置新值并返回旧值，如 `SET name mysql GET` |
| `GET key` | 获取值 |
| `APPEND key value` | 追加内容，返回追加后长度，如 `APPEND name 01` |
| `STRLEN key` | 返回字符串长度 |
| `INCR key` | 整数自增 1，key 不存在按 0 处理 |
| `DECR key` | 整数自减 1 |
| `INCRBY key increment` | 整数增加指定值，如 `INCRBY num 10` |
| `DECRBY key decrement` | 整数减少指定值，如 `DECRBY num 5` |
| `MSET key value [key value ...]` | 批量设置，如 `MSET a 1 b 2 c 3` |
| `MGET key [key ...]` | 批量获取，如 `MGET a b c` |
| `MSETNX key value [key value ...]` | 批量设置，仅当所有 key 都不存在才成功，原子操作 |
| `SETRANGE key offset value` | 从偏移量开始覆盖，如 `SETRANGE name 0 Tom`。长度不足会补 `\x00` |

## 3. 列表 List

| 命令 | 说明 |
|---|---|
| `LPUSH key element [element ...]` | 从左侧插入，返回列表长度 |
| `RPUSH key element [element ...]` | 从右侧插入 |
| `LPOP key [count]` | 从左侧弹出，`count` 为 Redis 6.2+ 支持 |
| `RPOP key [count]` | 从右侧弹出 |
| `LLEN key` | 返回列表长度 |
| `LRANGE key start stop` | 范围查询，`0 -1` 表示全部 |
| `LINDEX key index` | 按下标获取元素，支持负数 |
| `LINSERT key BEFORE|AFTER pivot element` | 在基准元素前/后插入，如 `LINSERT apple BEFORE 1 0` |
| `LREM key count element` | 删除元素。`count > 0` 从头删，`< 0` 从尾删，`0` 删全部 |
| `LSET key index element` | 设置指定下标的值，索引必须存在 |

## 4. 集合 Set

| 命令 | 说明 |
|---|---|
| `SADD key member [member ...]` | 添加成员，返回新增数量 |
| `SMEMBERS key` | 返回所有成员，大集合慎用 |
| `SISMEMBER key member` | 判断成员是否存在，返回 1/0 |
| `SCARD key` | 返回集合基数，即成员数量 |
| `SREM key member [member ...]` | 删除成员 |
| `SPOP key [count]` | 随机弹出并删除成员 |
| `SRANDMEMBER key [count]` | 随机返回成员，但不删除。正数不重复，负数可重复 |
| `SMOVE source destination member` | 原子地把成员从 source 移到 destination |
| `SINTER key [key ...]` | 交集 |
| `SUNION key [key ...]` | 并集 |
| `SDIFF key [key ...]` | 差集 |

## 5. 你原列表里需要特别注意的写法

1. `set name mysql get name` 不是标准 Redis 语法。  
   正确写法是：`SET name mysql GET`  
   表示设置 `name=mysql`，并返回旧值。

2. `set count 0 NX Incrby count 10` 不是一条命令。  
   应拆成两条：
   ```redis
   SET count 0 NX
   INCRBY count 10
   ```
   意思是：如果 `count` 不存在，先设为 0，然后自增 10。

3. `KEYS *` 生产环境慎用，建议用：
   ```redis
   SCAN 0 MATCH * COUNT 100
   ```

4. `UNLINK` 是异步删除，适合删除大 key；`DEL` 是同步删除。

5. `TTL` 返回值：
    - `-1`：存在但未设置过期
    - `-2`：key 不存在
    - 正数：剩余秒数

6. `MSETNX` 如果任意一个 key 已存在，则全部不设置，返回 0。

7. `LPOP/RPOP` 的 `count` 参数需要 Redis 6.2+；`SET ... GET` 也需要 Redis 6.2+。

## 6. 一句话总结

你列出的命令主要覆盖了 Redis 的：

- 通用键管理：`SELECT/KEYS/SCAN/EXISTS/TYPE/TTL/EXPIRE/PERSIST/RENAME/UNLINK`
- 字符串：`SET/GET/APPEND/STRLEN/INCR/DECR/INCRBY/DECRBY/MSET/MGET/MSETNX/SETRANGE`
- 列表：`LPUSH/RPUSH/LPOP/RPOP/LLEN/LRANGE/LINDEX/LINSERT/LREM/LSET`
- 集合：`SADD/SMEMBERS/SISMEMBER/SCARD/SREM/SPOP/SRANDMEMBER/SMOVE/SINTER/SUNION/SDIFF`

这套命令基本可以覆盖 Redis 日常开发中字符串、列表、集合的常用操作。