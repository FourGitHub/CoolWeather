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