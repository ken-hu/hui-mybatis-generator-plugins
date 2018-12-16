# hui-mybatis-plugins
【Mybatis-Plugin】Mybatis的插件开发。根据个人需要
## 使用方法
需要新增批量操作的时候。有一个简易版插件。
1. 在id上指定database
2. targetRuntime指定Mybatis3
3. 引入插件
4. 指定table的主键（这里主要是为了分布式系统用的主键是非数据库自动生成的。主键指定一下）
```xml
    <context id="Mysql" targetRuntime="MyBatis3" defaultModelType="flat">
```

```xml
           <!--批量操作-->
           <plugin type="com.hui.mybatis.plugins.BaseBatchOperatorPlugin"/>
```

```xml
          <generatedKey column="order_id"/>
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

##重新生成的时候 覆盖原文件
com.hui.mybatis.plugins.OverWriteXmlPlugin



