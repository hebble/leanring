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
     */
}
