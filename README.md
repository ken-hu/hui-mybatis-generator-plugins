# hui-mybatis-plugins
【Mybatis-Plugin】Mybatis-Generator插件开发。根据个人需要

## blog
1. [mybatis-generator-plugins-javaBean/xmlMethod生成工具](https://ithuhui.com/2018/12/21/mybatis-generator-plugins-javaBean-xmlMethod-tool/)
2. [mybatis-generator-plugins-postgresql-批量更新](https://ithuhui.com/2018/12/21/mybatis-generator-plugins-postgresql-batch-update/)
3. [mybatis-generator-plugins-oracle-批量更新](https://ithuhui.com/2018/12/21/mybatis-generator-plugins-oracle-batch-update/)
4. [mybatis-generator-plugins-mysql批量更新](https://ithuhui.com/2018/12/21/mybatis-generator-plugins-mysql-batch-update/)
5. [mybatis-generator-plugins-postgresql-返回主键](https://ithuhui.com/2018/12/21/mybatis-generator-plugins-postgresql-return-key/)
6. [mybatis-generator-plugins-oracle-返回主键](https://ithuhui.com/2018/12/21/mybatis-generator-plugins-oracle-return-key/)
7. [mybatis-generator-plugins-oracle-批量插入](https://ithuhui.com/2018/12/21/mybatis-generator-plugins-oracle-batch-insert/)
8. [mybatis-generator-plugins-mysql/postgresql-批量插入](https://ithuhui.com/2018/12/21/mybatis-generator-plugins-mysql-postgresql-batch-insert/)
9. [mybatis-generator-plugin-批量删除](https://ithuhui.com/2018/12/21/mybatis-generator-batch-delete/)

## 使用说明
需要新增批量操作的时候。有一个简易版插件。
1. 在id上指定database
2. targetRuntime指定Mybatis3
3. 引入插件
4. <generatedKey column="order_id" />判断是否自增，不加这个配置则是自己插入主键（适用于分布式系统）
```xml
    <context id="Mysql" targetRuntime="MyBatis3" >
```

```xml
           <!--批量操作-->
           <plugin type="com.hui.mybatis.plugins.BaseBatchOperatorPlugin"/>
```

```xml
         <generatedKey column="order_id" sqlStatement="JDBC" identity="id" />
```

## 批量新增
1. MYSQL: <br> 
com.hui.mybatis.plugins.BatchInsertPlugin
2. ORACLE: <br>
com.hui.mybatis.plugins.OracleBatchInsertPlugin
3. POSTGRESQL：<br>
com.hui.mybatis.plugins.BatchInsertPlugin

## 批量删除
1. MYSQL: <br>
com.hui.mybatis.plugins.BatchDeletePlugin
2. ORACLE: <br>
com.hui.mybatis.plugins.BatchDeletePlugin
3. POSTGRESQL：<br>
com.hui.mybatis.plugins.BatchDeletePlugin

## 批量更新
1. MYSQL: <br>
com.hui.mybatis.plugins.MysqlBatchUpdatePlugin
2. ORACLE: <br>
com.hui.mybatis.plugins.OracleBatchUpdatePlugin
3. POSTGRESQL：<br>
com.hui.mybatis.plugins.PostgreBatchUpdatePlugin

## 返回PG主键
com.hui.mybatis.plugins.PostGreSQLReturnKeyPlugin

## 返回ORACLE主键
com.hui.mybatis.plugins.OracleReturnKeyPlugin

## 重新生成的时候 覆盖原文件
com.hui.mybatis.plugins.OverWriteXmlPlugin



