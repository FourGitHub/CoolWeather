# CoolWeather
首先，这是一款天气预报软件，仿《第一行代码》(第二版)中酷欧天气，无广告，服务免费。
和书中不同的是，针对天气信息接口部分做了修改，不同于书中的接口，我使用的是和风天气提供的免费接口，返回数据更丰富，但是API KEY 使用的是作者的，因为我没有做认证开发者步骤，普通的API KEY ，只能获取3天的预报。
换了接口之后，对返回的JSON数据进行了重新解析,以及界面进行了重新设置，反正就是为了向用户展示尽可能多的有用的信息。
基本功能：
1. 遍历全国各省市县的功能
2. 查询全国各省市县的天气信息
3. 可切换城市
4. 提供短时预报(由于是免费接口，所以只能每3个小时的短时预报)
5. 提供七天预报
6. 应用背景每日刷新
7. 提供一些生活建议，和一些生活指数
8. 后台服务每2小时更新一次天气
9. 在别人的github上搞了一个日出日落的动画，提高逼格
10. 尽力将APP设计为Material Design风格，Toolbar、CardView、SwipeRefreahLayout、DrawerLayout

1、简介
Spring Boot 是一个框架，用于简化Spring应用初始搭建和开发过程 --- 致力于快速应用开发
编码变得简单：采用Java config方式，提供大量注解，提高开发效率
配置变得简单：所有spring boot的项目都只有一个配置文件：application.properties/application.yml
部署变得简单：spring boot内置了三种servlet容器：tomcat，jetty，undertow
监控简单：spring boot提供了actuator包，可以使用它来对你的应用进行监控

spring boot可以很方便的提供restful 风格的api


2、默认生成的Spring Boot项目
XxxApplication.java
    SpringBoot程序入口，已经自动生成好了，只需要添加我们自己的逻辑
resources(资源文件夹)
    static：保存所有的静态资源； js css images；
    templates：保存所有的模板视图页面；
    application.properties：Spring Boot应用的配置文件；可以修改一些默认设置

3、SpringBoot中Tomcat配置
SpringBoot将应用打成Jar包，默认运行在内嵌的Tomcat(Sevelet容器)
在application.properties做属性配置，通用的Servlet容器配置都以"server"作为前缀，Tomcat特有配置都以"server.tomcat"作为前缀

4、Spring Boot 框架分层设计
controller层/控制层
    controller层的功能为请求和响应控制
    controller层负责前后端交互，接受前端请求，调用service层，接收service层返回的数据，最后返回具体的页面和数据到客户端

service层/业务层
    service层的作用为完成功能设计,可细分为接口层和实现层
    service层调用dao层接口，接收dao层返回的数据，完成项目的基本功能设计

dao层/数据持久层/mapper层
    dao层的作用为访问数据库，向数据库发送sql语句，完成数据的增删改查任务

model层/实体层/entity层/pojo层
    一般数据库一张表对应一个实体类，类属性同表字段一一对应


5、常用注解 
controller层/控制层
    @RequestMapping
    用来处理请求地址映射，可用于类或方法上，如果不注明请求方式，默认拦截get和post请求

    @Controller
    @RestController = @Controller + @ResponseBody
    前后端之间的交互，无非两种方式：
    整体页面提交，比如form提交 —— @Controller就是处理整体页面刷新提交的注解，返回一个视图
    局部刷新/异步刷新，ajax提交 —— @RestController就是处理ajax提交的注解，一般返回json格式

    SpringBoot 会将 @Controller 注解的类当做 URL处理器/请求处理器，在创建 Tomcat Server 时，会将请求处理器传递进去，注册到 Servlet 的请求处理器中；XxxController.java 就是这样被自动装配进 Tomcat 的

    @ResponseBody
    用于将Controller的方法返回对象，通过适当的HttpMessageConverter转换为指定格式后(json)，写入Response对象的body数据区

    @Autowired
    自动装配，bean工厂

service层/业务层
    @Service
    标记业务层的组件，将业务逻辑处理的类都会加上@Service注解交给spring容器

    @Resource
    @Resource和@Autowired一样都可以用来装配bean，都可以用于标注字段或者方法；两个之前的区别就是匹配方式不同，@Resource默认按照名称方式进行bean匹配，@Autowired默认按照类型方式进行bean匹配

dao层/数据持久层/mapper层
    @Repository
    注解类作为DAO对象，管理操作数据库的对象


总得来看，@Component, @Service, @Controller, @Repository是Spring注解，注解后可以被Spring框架所扫描并注入到Spring容器来进行管理；@Component是通用注解，后三个注解是这个注解的拓展，具有了特定的功能。通过这些注解的分层管理，就能将请求处理，义务逻辑处理，数据库操作处理分离出来。所以在正常开发中，如果能用@Service, @Controller, @Repository其中一个标注这个类的定位的时候，就不要用@Component来标注


SpringBoot结合前端有很多种方法，比如在static里面直接加入css或js，又或者引入webjars，以jar包的形式加入项目(pom文件引入webjars的jar)


理解servlet容器

1、web早起发展
web技术主要用于浏览静态页面
随着时间发展，用户已经不满足于仅浏览静态页面，还需要一些交互操作，获取一些动态结果
GCI程序出现(CGI程序编写困难，响应时间较长，以进程方式运行导致性能受限)

2. Servlet
Java Servlet（Java服务器小程序）是一个基于Java技术的Web组件，运行在服务器端，它由Servlet容器所管理，用于生成动态的内容
编写一个Servlet，实际上就是按照Servlet规范编写一个Java类
每一个Servlet都是一个拥有能响应HTTP请求的特定元素的Java类
Servlet没有main方法，不能独立运行，它需要被部署到Servlet容器中，由容器来实例化和调用 Servlet的方法

3. Servlet容器
HTTP Request -> Forward Request -> HTTP Response
Servlet容器是Web服务器的一部分，主要作用是将请求转发给相应的servlet进行处理，请求处理完后，将动态生成的结果返回至正确的地址
Servlet容器负责servlet的创建、执行和销毁(生命周期)

4. Servlet容器和Web服务器如何处理一个请求？
Tomcat是一个开源免费的Servlet容器(运行Servlet的环境)
1）客户端（通常都是浏览器）访问Web服务器，发送HTTP请求
2）Web服务器接收到请求后，传递给Servlet容器
3）Servlet容器将请求转发给相应的servlet进行处理，向其传递表示请求和响应的对象(如果找不到相应servlet，就加载servlet实例，并创建Servlet实例)
4）Servlet实例使用请求对象得到客户端的请求信息，进行相应的处理
5）Servlet实例将处理结果通过响应对象发送回客户端，容器负责确保响应正确送出，同时将控制返回给Web服务器


WebJars
WebJars是将web前端资源（js，css等）打成jar包文件，然后借助Maven工具，以jar包形式对web前端资源进行统一依赖管理，保证这些Web资源版本唯一性。WebJars的jar包部署在Maven中央仓库上。

一般情况下，我们是将这些Web资源拷贝到Java Web项目的webapp相应目录下进行管理,这种通过人工方式管理可能会产生版本误差，拷贝版本错误，漏拷等现象，导致前端页面无法正确展示等莫名其妙的错误。

如何引用这些以jar包形式被管理的Web资源

原理
/webjars/** 的请求会被SpringBoot拦截然后到jar包下classpath:/META-INF/resources/webjars/ 寻找


Thymeleaf
是一个用于渲染XML/XHTML/HTML5内容的 模板引擎
模板引擎是为了使用户界面与业务数据分离而产生的，它将特定格式的模板和数据通过模板引擎渲染就会生成一个标准的HTML文档
默认的模板映射路径是：src/main/resources/templates


特色
Thymeleaf 在有网络和无网络的环境下皆可运行，即它可以让美工在浏览器查看页面的静态效果，也可以让程序员在服务器查看带数据的动态页面效果。这是由于它支持 html 原型，然后在 html 标签里增加额外的属性来达到模板+数据的展示方式。浏览器解释 html 时会忽略未定义的标签属性，所以 thymeleaf 的模板可以静态地运行；当有数据返回到页面时，Thymeleaf 标签会动态地替换掉静态内容，使页面动态显示

