# SpringCloud微服务学习笔记



#  一、RestTemplate实现远程调用

是Spring框架提供的一个工具类，当我们需要远程调用一个HTTP接口时，可以使用RestTemplate这个类来实现。

RestTemplate提供高度封装的接口，提供了多种便捷访问远程HTTP服务的方法，可以让我们非常方便地进行RestAPI调用，能够大大提高客户端的编写效率。

配置类：

```
package com.example.consumer_rest_9001.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ConfigBean {
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
```

![点击并拖拽以移动](data:image/gif;base64,R0lGODlhAQABAPABAP///wAAACH5BAEKAAAALAAAAAABAAEAAAICRAEAOw==)

使用：

```
package com.example.consumer_rest_9001.controller;

import com.example.consumer_rest_9001.entity.GeoCode;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/webgis")
@Slf4j
public class WebGisController {
    private static final String URL = "http://localhost:8081/geocode";
    
    @Resource
    private RestTemplate restTemplate;
    
    @GetMapping("/list")
    public Object list() {
        log.info("调用地理编码服务");
            Object result = restTemplate.getForObject(URL, Object.class);
            log.info("调用成功");
            return result;
    }

    @PostMapping("/save")
    public Object save(@RequestBody GeoCode geoCode){

        log.info("调用保存服务");
       return restTemplate.postForObject(URL,geoCode,Object.class);

    }
    @DeleteMapping("/del/{id}")
    public Object del(@PathVariable int id){
        log.info("调用删除服务");
        restTemplate.delete(URL+"/"+id);
        return true;

    }


}
```

![点击并拖拽以移动](data:image/gif;base64,R0lGODlhAQABAPABAP///wAAACH5BAEKAAAALAAAAAABAAEAAAICRAEAOw==)

# 服务注册中心

微服务架构中的服务非常多，都是采用集群部署。每个服务都要维护一套服务之间的调用关系。就需要一个中间代理来维护调用关系，因此服务注册中心就应运而生。

之前使用RestTemplate进行直连的方式调用

![img](https://i-blog.csdnimg.cn/direct/0e27a600f2f04efa936a30f0acbe3d3c.png)![点击并拖拽以移动](data:image/gif;base64,R0lGODlhAQABAPABAP///wAAACH5BAEKAAAALAAAAAABAAEAAAICRAEAOw==)编辑

使用注册中心，地图服务和地理编码都要注册到注册中心，并且可以订阅注册中心中的所有服务。地图 服务可以通过订阅服务，间接调用地理编码服务

![img](https://i-blog.csdnimg.cn/direct/ee667ce49a4b46109efac1ead63b5979.png)![点击并拖拽以移动](data:image/gif;base64,R0lGODlhAQABAPABAP///wAAACH5BAEKAAAALAAAAAABAAEAAAICRAEAOw==)编辑

**主流的注册中心** Nacos Zookeeper Consul Sofa Etcd Eureka

这里学习的是 **Nacos**

# 二、Nacos注册中心

## 2.1下载安装及启动

使用Nacos组件可以作为微服务架构的注册中心和配置中心，可以简单理解为可以代替Netflix解决方案 中的Eureka组件做服务注册中心和Spring cloud Config做服务配置中心、Spring Cloud Bus组件

**Nacos = Eureka+Config+Bus**

### 版本依赖

![img](https://i-blog.csdnimg.cn/direct/227e120b6a5f45fcadadc9e7fe3a341c.png)![点击并拖拽以移动](data:image/gif;base64,R0lGODlhAQABAPABAP///wAAACH5BAEKAAAALAAAAAABAAEAAAICRAEAOw==)编辑

#### **本文使用版本及下载地址：**

#### **版本依赖说明：**

[版本发布说明-阿里云Spring Cloud Alibaba官网](https://sca.aliyun.com/docs/2023/overview/version-explain/?spm=5176.29160081.0.0.74805c72IYQU0K)

父模块pom：

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>org.example</groupId>
    <artifactId>SpringCloudLearn</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <modules>
        <module>consumer-client-rest</module>
        <module>provider-service-rest</module>
    </modules>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <spring-cloud.version>2023.0.1</spring-cloud.version>
        <spring-cloud-alibaba-version>2023.0.1.0</spring-cloud-alibaba-version>
        <!-- 添加新的版本属性 -->
        <mybatis-plus.version>3.5.5</mybatis-plus.version>
        <mysql.version>8.0.33</mysql.version>
    </properties>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.4</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-alibaba-dependencies</artifactId>
                <version>${spring-cloud-alibaba-version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

        </dependencies>
    </dependencyManagement>

</project>
```

![点击并拖拽以移动](data:image/gif;base64,R0lGODlhAQABAPABAP///wAAACH5BAEKAAAALAAAAAABAAEAAAICRAEAOw==)

子模块：

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">

	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.example</groupId>
		<artifactId>SpringCloudLearn</artifactId>
		<version>1.0-SNAPSHOT</version>
	</parent>


	<groupId>com.example</groupId>
	<artifactId>consumer-client-rest</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>consumer_rest_9001</name>
	<description>consumer_rest_9001</description>
	<url/>
	<licenses>
		<license/>
	</licenses>
	<developers>
		<developer/>
	</developers>
	<scm>
		<connection/>
		<developerConnection/>
		<tag/>
		<url/>
	</scm>
	<properties>
		<java.version>17</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>com.alibaba.cloud</groupId>
			<artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-loadbalancer</artifactId>
		</dependency>

			<!-- 添加热部署依赖 -->
			<dependency>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-devtools</artifactId>
				<scope>runtime</scope>
				<optional>true</optional>
			</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<version>${mysql.version}</version>
		</dependency>

		<dependency>
			<groupId>com.baomidou</groupId>
			<artifactId>mybatis-plus-spring-boot3-starter</artifactId>
			<version>${mybatis-plus.version}</version>
		</dependency>


		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>

	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<configuration>
					<addResources>true</addResources>

					<excludes>
						<exclude>
							<groupId>org.projectlombok</groupId>
							<artifactId>lombok</artifactId>
						</exclude>
					</excludes>
				</configuration>
			</plugin>
		</plugins>
	</build>

</project>
```

![点击并拖拽以移动](data:image/gif;base64,R0lGODlhAQABAPABAP///wAAACH5BAEKAAAALAAAAAABAAEAAAICRAEAOw==)

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.example</groupId>
        <artifactId>SpringCloudLearn</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>

    <!-- 项目的唯一标识符 -->
    <groupId>com.coder</groupId>           <!-- 组织/公司的标识 -->
    <artifactId>provider-service-rest</artifactId>  <!-- 项目/模块的标识 -->
    <version>0.0.1-SNAPSHOT</version>      <!-- 版本号 -->
    <name>provider-service-rest</name>      <!-- 项目显示名称 -->
    <description>provider_rest_8001</description>  <!-- 项目描述 -->
    <url/>
    <licenses>
        <license/>
    </licenses>
    <developers>
        <developer/>
    </developers>
    <scm>
        <connection/>
        <developerConnection/>
        <tag/>
        <url/>
    </scm>
    <properties>
        <java.version>17</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>${mysql.version}</version>
        </dependency>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

![点击并拖拽以移动](data:image/gif;base64,R0lGODlhAQABAPABAP///wAAACH5BAEKAAAALAAAAAABAAEAAAICRAEAOw==)

##### **Nacos：2.3.2**

[发布历史 | Nacos 官网](https://nacos.io/download/release-history/)

#####  **SpringCloud: 2023.0.1**

[Spring Cloud](https://spring.io/projects/spring-cloud)

##### **SpringCloudAlibaba：**2023.0.1.0

[Spring Cloud Alibaba](https://spring.io/projects/spring-cloud-alibaba)

##### **mybatis：3.5.5**

```
<dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>
```

![点击并拖拽以移动](data:image/gif;base64,R0lGODlhAQABAPABAP///wAAACH5BAEKAAAALAAAAAABAAEAAAICRAEAOw==)

### 启动配置：

#### **Nacos鉴权修改：**

[Authorization](https://nacos.io/zh-cn/docs/auth.html)

#### 服务端如何开启鉴权：

```
### If turn on auth system:
nacos.core.auth.system.type=nacos
nacos.core.auth.enabled=true
```

![点击并拖拽以移动](data:image/gif;base64,R0lGODlhAQABAPABAP///wAAACH5BAEKAAAALAAAAAABAAEAAAICRAEAOw==)

#### **密钥修改：**

```
# Nacos 2.x（包括你使用的 2.3.2）
nacos.core.auth.plugin.nacos.token.secret.key=OTNHMTExRjA3MUExRTFGMDcxQTEyMzQ1Njc4OTAxMjM0NTY3ODkwMQ==
# Nacos 1.x
# nacos.core.auth.default.token.secret.key=OTNHMTExRjA3MUExRTFGMDcxQTEyMzQ1Njc4OTAxMjM0NTY3ODkwMQ==
```

![点击并拖拽以移动](data:image/gif;base64,R0lGODlhAQABAPABAP///wAAACH5BAEKAAAALAAAAAABAAEAAAICRAEAOw==)

#### 开启服务身份识别功能：

nacos集群之间的通信

```
### 开启鉴权
nacos.core.auth.enabled=true

### 关闭使用user-agent判断服务端请求并放行鉴权的功能
nacos.core.auth.enable.userAgentAuthWhite=false

### 配置自定义身份识别的key（不可为空）和value（不可为空）
nacos.core.auth.server.identity.key=example
nacos.core.auth.server.identity.value=example
```

![点击并拖拽以移动](data:image/gif;base64,R0lGODlhAQABAPABAP///wAAACH5BAEKAAAALAAAAAABAAEAAAICRAEAOw==)

```
### Since 1.4.1, worked when nacos.core.auth.enabled=true and nacos.core.auth.enable.userAgentAuthWhite=false.
### The two properties is the white list for auth and used by identity the request from other server.
nacos.core.auth.server.identity.key=zyhhsss
nacos.core.auth.server.identity.value=zyhhsss
```

![点击并拖拽以移动](data:image/gif;base64,R0lGODlhAQABAPABAP///wAAACH5BAEKAAAALAAAAAABAAEAAAICRAEAOw==)

### 启动：

**.\startup.cmd -m standalone**

http://localhost:8848/nacos/

#### 添加用户

账号：zyhhsss

密码：zyhhsss

### 

## 2.2 使用nacos服务名进行远程调用

### 将工程做成父子工程

父工程是一个springboot工程

子工程是maven工程

### 在父工程中管理springcloud版本依赖

在父工程的pom文件中加入如下版本和依赖管理

```
xxxxxxxxxx
    <properties>    
        <spring-cloud.version>2022.0.0</spring-cloud.version>
        <spring-cloud-alibaba.version>2022.0.0.0</spring-cloud-alibaba.version>
    </properties>
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-alibaba-dependencies</artifactId>
                <version>${spring-cloud-alibaba.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
```

![点击并拖拽以移动](data:image/gif;base64,R0lGODlhAQABAPABAP///wAAACH5BAEKAAAALAAAAAABAAEAAAICRAEAOw==)

### 在子模块中引入nacos client端依赖

#### 配置启动服务发现

在服务提供端和服务消费端的pom.xml文件中，引入spring-clound-starter-alibaba-nacos-discovery

参考官网生态融合处理[Nacos 融合 Spring Cloud，成为注册配置中心](http://nacos.io/zh-cn/docs/v2/ecology/use-nacos-with-spring-cloud.html)

```
xxxxxxxxxx
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
```

![点击并拖拽以移动](data:image/gif;base64,R0lGODlhAQABAPABAP///wAAACH5BAEKAAAALAAAAAABAAEAAAICRAEAOw==)

#### 消费端引入客户端负载均衡器（LoadBalancer）

需要在消费端加入LoadBalancer依赖，nacos依赖负载均衡器去解析微服务名称，调用对应的服务地址

在pom.xml文件中引入

```
xxxxxxxxxx
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>
```

![点击并拖拽以移动](data:image/gif;base64,R0lGODlhAQABAPABAP///wAAACH5BAEKAAAALAAAAAABAAEAAAICRAEAOw==)

在RestTemplate上加入@LoadBalanced

```
xxxxxxxxxx
@Configuration
public class ConfigBean {
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
```

![点击并拖拽以移动](data:image/gif;base64,R0lGODlhAQABAPABAP///wAAACH5BAEKAAAALAAAAAABAAEAAAICRAEAOw==)

### 配置服务实例名和nacos相关信息

在服务提供端和服务调用端的yml文件中，进行如下配置

consumer

```
spring:
  application:
    #实例名
    name: consumer-service-rest
  #nacos服务器地址 端口 用户名  密码
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        username: nacos
        password: nacos
```

![点击并拖拽以移动](data:image/gif;base64,R0lGODlhAQABAPABAP///wAAACH5BAEKAAAALAAAAAABAAEAAAICRAEAOw==)

provider

```
spring:
  application:
    #实例名
    name: provider-service-rest
  #nacos服务器地址 端口 用户名  密码
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        username: nacos
        password: nacos
```

![点击并拖拽以移动](data:image/gif;base64,R0lGODlhAQABAPABAP///wAAACH5BAEKAAAALAAAAAABAAEAAAICRAEAOw==)

### 在消费端的Controller使用nacos方式调用服务端

原来使用方式

```
private static final String URL="http://localhost:8001/geocode";
```

改成nacos后

```
private static final String URL="http://provider-service-rest/geocode";
```

### 命名规则：

```
<!-- 消费者模块9001 -->
<groupId>com.example</groupId>
<artifactId>consumer_rest_9001</artifactId>
<name>consumer-rest-9001</name>

<!-- 消费者模块9002 -->
<groupId>com.example</groupId>
<artifactId>consumer_nacos_9002</artifactId>
<name>consumer-nacos-9002</name>

<!-- 提供者模块8001 -->
<groupId>com.example</groupId>                  <!-- 保持一致的组织标识 -->
<artifactId>provider_rest_8001</artifactId>    
 <!-- 模块名字与artifactId一致、文件夹名用下划线 -->
<name>provider-rest-8001</name>                 <!-- 显示名用连字符 -->
```

### 命名规则总结

**必须保持一致的：**

- 物理目录名

- artifactId

- 父pom中的module名

**可以独立命名的：**

- pom.xml中的name（显示名称）
- application.yml中的spring.application.name（服务名）

**建议的命名方式：**

目录名/artifactId：provider_rest_8001   (下划线分隔)
 显示名name：provider-service-rest     (连字符分隔)
 服务名：provider-service          (简短服务名)

## **热部署：**

### 1.加入依赖

```
<!-- 添加热部署依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
</dependency>
```

### 2.高级设置

![img](https://i-blog.csdnimg.cn/direct/b37aa24f254141cab568ca37b6a093e0.png)![点击并拖拽以移动](data:image/gif;base64,R0lGODlhAQABAPABAP///wAAACH5BAEKAAAALAAAAAABAAEAAAICRAEAOw==)









