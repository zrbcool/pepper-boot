```bash
.
├── pepper-boot-project
│   ├── pepper-boot //包含各个start，boot-x的公共抽象代码
│   ├── pepper-boot-dependencies //将所有的依赖统一抽入到这个pom中，便于使用
│   ├── pepper-boot-extensions 
│   │   ├── pepper-boot-jedis
│   │   │   ├── pepper-boot-jedis //包含自封装的boot-x相关的工具类
│   │   │   ├── pepper-boot-jedis-starter //通过spring.factories文件配置进入spring-boot管理，主要编写各种AutoConfiguration
│   │   │   │   ├── pom.xml
│   │   │   │   └── src
│   │   │   │       └── main
│   │   │   │           ├── java
│   │   │   │           │   └── top.zrbcool.pepper.boot.jedis.starter.JedisAutoConfiguration.java
│   │   │   │           └── resources
│   │   │   │               └── META-INF
│   │   │   │                   └── spring.factories
│   │   │   └── pom.xml
│   ├── pepper-boot-parent //父pom
└── README.md
```

