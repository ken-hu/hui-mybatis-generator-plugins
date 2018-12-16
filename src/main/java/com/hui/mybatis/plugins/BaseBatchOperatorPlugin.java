package com.hui.mybatis.plugins;

import com.hui.mybatis.tools.BaseGenTool;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Document;

import java.util.List;

/**
 * <b><code>BaseBatchOperatorPlugin</code></b>
 * <p/>
 * Description:
 * <p/>
 * <b>Creation Time:</b> 2018/12/16 16:15.
 *
 * @author HuWeihui
 */
public class BaseBatchOperatorPlugin extends PluginAdapter {

    private static final String MYSQL = "MYSQL";
    private static final String ORACLE = "ORACLE";
    private static final String POSTGRESQL = "POSTGRESQL";


    private BatchInsertPlugin batchInsertPlugin = new BatchInsertPlugin();

    private OracleBatchInsertPlugin oracleBatchInsertPlugin = new OracleBatchInsertPlugin();

    private BatchDeletePlugin batchDeletePlugin = new BatchDeletePlugin();

    private MysqlBatchUpdatePlugin mysqlBatchUpdatePlugin = new MysqlBatchUpdatePlugin();

    private OracleBatchUpdatePlugin oracleBatchUpdatePlugin = new OracleBatchUpdatePlugin();

    private PostgreBatchUpdatePlugin postgreBatchUpdatePlugin = new PostgreBatchUpdatePlugin();

    @Override
    public boolean validate(List<String> list) {
        return true;
    }


    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (BaseGenTool.isMybatisMode(introspectedTable)) {
            addMethod(interfaze, topLevelClass, introspectedTable);
        }
        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        if (BaseGenTool.isMybatisMode(introspectedTable)) {
            addSqlMapper(document, introspectedTable);
        }
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }


    public void addMethod(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String contextIdUp = context.getId().toUpperCase();
        switch (contextIdUp) {
            case MYSQL:
                batchInsertPlugin.setContext(context);
                batchDeletePlugin.setContext(context);
                mysqlBatchUpdatePlugin.setContext(context);

                batchInsertPlugin.clientGenerated(interfaze, topLevelClass, introspectedTable);
                batchDeletePlugin.clientGenerated(interfaze, topLevelClass, introspectedTable);
                mysqlBatchUpdatePlugin.clientGenerated(interfaze, topLevelClass, introspectedTable);
                break;
            case ORACLE:
                oracleBatchInsertPlugin.setContext(context);
                batchDeletePlugin.setContext(context);
                oracleBatchUpdatePlugin.setContext(context);

                oracleBatchInsertPlugin.clientGenerated(interfaze, topLevelClass, introspectedTable);
                batchDeletePlugin.clientGenerated(interfaze, topLevelClass, introspectedTable);
                oracleBatchUpdatePlugin.clientGenerated(interfaze, topLevelClass, introspectedTable);
                break;
            case POSTGRESQL:
                batchInsertPlugin.setContext(context);
                batchDeletePlugin.setContext(context);
                postgreBatchUpdatePlugin.setContext(context);

                batchInsertPlugin.clientGenerated(interfaze, topLevelClass, introspectedTable);
                batchDeletePlugin.clientGenerated(interfaze, topLevelClass, introspectedTable);
                postgreBatchUpdatePlugin.clientGenerated(interfaze, topLevelClass, introspectedTable);
                break;
            default:
                batchInsertPlugin.setContext(context);
                batchDeletePlugin.setContext(context);
                mysqlBatchUpdatePlugin.setContext(context);

                batchInsertPlugin.clientGenerated(interfaze, topLevelClass, introspectedTable);
                batchDeletePlugin.clientGenerated(interfaze, topLevelClass, introspectedTable);
                mysqlBatchUpdatePlugin.clientGenerated(interfaze, topLevelClass, introspectedTable);
                break;
        }
    }


    public void addSqlMapper(Document document, IntrospectedTable introspectedTable) {
        String contextIdUp = context.getId().toUpperCase();
        switch (contextIdUp) {
            case MYSQL:
                batchInsertPlugin.setContext(context);
                batchDeletePlugin.setContext(context);
                mysqlBatchUpdatePlugin.setContext(context);

                batchInsertPlugin.sqlMapDocumentGenerated(document, introspectedTable);
                batchDeletePlugin.sqlMapDocumentGenerated(document, introspectedTable);
                mysqlBatchUpdatePlugin.sqlMapDocumentGenerated(document, introspectedTable);
                break;
            case ORACLE:
                oracleBatchInsertPlugin.setContext(context);
                batchDeletePlugin.setContext(context);
                oracleBatchUpdatePlugin.setContext(context);

                oracleBatchInsertPlugin.sqlMapDocumentGenerated(document, introspectedTable);
                batchDeletePlugin.sqlMapDocumentGenerated(document, introspectedTable);
                oracleBatchUpdatePlugin.sqlMapDocumentGenerated(document, introspectedTable);
                break;
            case POSTGRESQL:
                batchInsertPlugin.setContext(context);
                batchDeletePlugin.setContext(context);
                postgreBatchUpdatePlugin.setContext(context);

                batchInsertPlugin.sqlMapDocumentGenerated(document, introspectedTable);
                batchDeletePlugin.sqlMapDocumentGenerated(document, introspectedTable);
                postgreBatchUpdatePlugin.sqlMapDocumentGenerated(document, introspectedTable);
                break;
            default:
                batchInsertPlugin.setContext(context);
                batchDeletePlugin.setContext(context);
                mysqlBatchUpdatePlugin.setContext(context);

                batchInsertPlugin.sqlMapDocumentGenerated(document, introspectedTable);
                batchDeletePlugin.sqlMapDocumentGenerated(document, introspectedTable);
                mysqlBatchUpdatePlugin.sqlMapDocumentGenerated(document, introspectedTable);
                break;
        }
    }

}
