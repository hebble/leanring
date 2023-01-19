package com.learning.study.maven;

public class MavenLearning {
    /**
     1.maven常见命令之 -pl -am -amd
        详见maven常见命令
         参数	全称	说明
         -pl	--projects 选项后可跟随{groupId}:{artifactId}或者所选模块的相对路径(多个模块以逗号分隔)
         -am	--also-make	表示同时处理选定模块所依赖的模块
         -amd	--also-make-dependents 表示同时处理依赖选定模块的模块
         -N	    --Non-recursive	表示不递归子模块
         -rf	--resume-from 表示从指定模块开始继续处理

         mvn clean compile -pl common,gather-web -am

     2.阿里maven仓库
        如springboot构建很慢，或者打包的时候下载依赖很慢，可在pom文件中添加如下配置。可以加快构建速度如springboot构建很慢，或者打包的时候下载依赖很慢，可在pom文件中添加如下配置。可以加快构建速度
            <repositories>
                <repository>
                    <id>alimaven</id>
                    <url>https://maven.aliyun.com/repository/public</url>
                </repository>
            </repositories>

            <pluginRepositories>
                <pluginRepository>
                    <id>alimaven</id>
                    <url>https://maven.aliyun.com/repository/public</url>
                </pluginRepository>
            </pluginRepositories>

     3.查找maven仓库
        https://mvnrepository.com/
     */
}
