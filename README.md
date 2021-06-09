#### sun-parent ####
jar包版本管理

#### sun-common ####
公共工具类

#### sun-language ####
多语言专用基础数据处理包

#### sun-bean ####
基础业务实现包

#### sun-manager ####
地图管理器专用接口包

#### sun-auth ####
授权码系统

#### sun-web ####
地图平台整合包，启动入口

---


### 配置文件：

配置文件的入口在：`\trunk\sun-web\src\main\resources\application.properties`

直接修改里面的`spring.profiles.active`的值来配置使用的是哪一个配置文件,比如值为`dev`的时候:使用的就是：`application-dev.properties`文件的配置。

`logback-spring.xml`是日志处理和输出相关的配置。

### 定时器：

在`\trunk\sun-web\src\main\java\com\it\sun\PlayRunApplication.java`中添加了`@EnableScheduling`注解，即开始定时器功能，不需要的可以去掉这个注解。
在`\trunk\sun-bean\src\main\java\com\it\sun\task\ScheduledTasks.java`文件中有定时器的使用案例。

### 异步方法调用：

在`\trunk\sun-web\src\main\java\com\it\sun\PlayRunApplication.java`中添加了`@EnableAsync`注解，即开启了异步调用的功能，不需要的可以注释掉。（一般用在多任务且不相关的多个方法的调用上）
在`\trunk\sun-bean\src\main\java\com\it\sun\task\AnsyncTask.java`文件中定义了异步方法。
在`\trunk\sun-web\src\main\java\com\it\sun\controller\SampleController.java`中的`AnsyncPlay()`方法中有调用异步方法的案例。

### Quartz调度

在`\trunk\sun-common\src\main\java\com\it\common\task\quartz\QuartzManager.java`中定义了Quartz调度任务的添加删除启动等方法【最下面的main方法给出了调用案列】
在`\trunk\sun-bean\src\main\java\com\it\sun\task\quartz\MyJob.java`中写了一个被Quartz调用的执行方法案列



## mybatis-generator 是代码生成器

先配置 generatorConfig.xml 里面的链接信息和需要生产的类的信息

配置：<jdbcConnection> 标签 中的 数据库连接信息,地址，账号，密码

配置：<table> 标签中的 tableName：表名   domainObjectName：生成的java类名

然后执行脚本命令，语法在readme.txt文件中


---