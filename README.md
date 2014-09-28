AsyncDbLogger
=============
这是一个简单的异步记录数据库日志的类库（log4j本身的数据库日志太蠢了），没有任何多余的功能，非常mini

项目本身是maven项目，调用方式在test目录中有示例

添加项目引用可以用如下maven的方式实现
	<repositories>
		<repository>
			<id>My Repository</id>
			<name>My Repository</name>
			<url>http://114.215.158.105:8080/nexus/content/repositories/releases/</url>
		</repository>
	</repositories>
	
<dependency>
  <groupId>com.ajita</groupId>
  <artifactId>AsyncDbLogger</artifactId>
  <version>0.1</version>
</dependency>
