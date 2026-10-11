# AGENTS.md —— 本项目 AI 编码规范

## 一、总则（必须严格遵守，适用于所有前端代码）
### 前端
1. 命名：组件/类型大驼峰（UserCard），变量/函数/文件小驼峰（userName, getUser）
2. 类型：接口数据必须定义 interface 并标注类型，禁止使用 any
3. 函数：入参和返回值都要写类型；逻辑抽成有名字的函数，不在模板里堆表达式
4. 组件：一个组件只做一件事，超过两屏就拆；通用 UI 用 Element Plus
5. 数据：接口调用收进 src/api/，store 用 Pinia，禁止在组件里散落裸请求
6. 禁止魔法数字：写 60 * 1000 而不是 60000，常量用 const 命名
7. 注释：复杂逻辑加一句"为什么这样做"，不写废话注释
8. 不要一次性输出超大代码，分步生成，每步可运行验证

### 后端（Java / Spring Boot）
- 分层：Controller / Service / Mapper 职责分离，Controller 只接收与返回，业务逻辑放 Service，不写"上帝类"
- 命名：类大驼峰（UserController），方法/变量小驼峰（getUser），常量全大写（MAX_SIZE）；类和包名用英文，不用拼音
- 返回统一用 Result 包装（code / message / data），不裸返回、不把异常当返回值
- 入参校验用 @Validated + 注解（@NotNull / @NotBlank / @Size），不手写一堆 if 判空
- 禁止魔法数字：状态值/大小写常量，不写裸数字；状态码用枚举/常量命名
- 日志用 Logger（@Slf4j），不用 System.out.println 调试
- 异常：抛给全局异常处理器统一处理，不吞异常、不在 catch 里静默
- 数据库操作走 Mapper，不用裸 JDBC；SQL 参数用占位符防注入；字段按需返回 DTO，不把整个实体无脑返回
- 不生成超大文件，分步生成、每步可运行验证

## 二、分领域细则（生成对应代码时参考）
### Vue / 组件
- 组件文件放 src/components/，页面放 src/views/
- props 用 defineProps 声明并写类型；事件用 defineEmits 声明
- 一个组件只负责一件事，超过两屏就拆子组件；插槽用于定制内容，别滥用

### 路由
- 路由地址小写+短横线（/user-profile）
- 每个页面一个 src/views/xxx.vue，路由集中在 src/router/index.ts
- 页面跳转用 router-link / router.push，不用 <a>；带参数用 query 或 params

### 状态管理（Pinia）
- 全局共享状态用 Pinia，不用 props 层层传
- store 收进 src/stores/

### 样式
- 骨架用 el-row/el-col，局部排列用 flex
- 样式一律写在 <style scoped>
- 间距用 gap / margin，不用绝对定位硬怼

### 请求（axios）
- 所有接口收进 src/api/，axios 实例集中在 src/api/http.ts
- baseURL 用相对路径 /api，配合 Vite 代理，不写死 localhost
- 错误处理统一在拦截器，页面只写成功逻辑；拿到数据先打印再上页面

### 表单 / 组件库
- 通用组件（按钮/表格/表单/弹窗）用 Element Plus，不自写
- 表单校验统一用 Element Plus 校验规则

### 日期 / 图表
- 日期一律用 dayjs，不用原生 Date 拼字符串
- ECharts 图表统一初始化，组件销毁时 dispose 释放