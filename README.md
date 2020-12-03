1.在慕课网课程下载源码，解压缩

2.下载sql初始化文件mmall.sql

3.保证在已经安装jdk，maven，tomcat，mysql等的环境并配置好

4.解压缩源码之后，使用eclipse或者idea导入maven项目

5.如下图，mybatis-generator需要的mysql包已经放在了tools包下，可以copy出来，放到某个位置，并修改 db.driverLocation的路径到你放的路径。使用mybatis-generator的时候就ok啦~

6.如下图，打开datasource.properties请修改db.url、db.username、db.password为自己的mysql数据库连接需要的url、username、password

7.打开mmall.properties，修改成自己的ftp服务器地址，账号和密码，支付宝回调的地址可以通过课程中讲的外网穿透进行配置。如果用nginx配置的话，请修改本机host支持域名。然后修改ftp文件服务器的访问前缀。MD5的salt值非常不建议修改。否则账号就登录不进去啦，还需要重置。

8.然后部署tomcat运行就可以了。

9.管理员账号：admin 管理员密码：admin

10.如自己找软件比较麻烦，可以访问http://learning.happymmall.com/env.html

11.如自己配置比较麻烦，可以参考线上配置http://learning.happymmall.com/env.html

12.接口文档：http://git.oschina.net/imooccode/happymmallwiki/wikis/home