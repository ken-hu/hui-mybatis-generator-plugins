package com.hui.mybatis.plugins;

import com.hui.mybatis.tools.MethodGeneratorTool;
import com.hui.mybatis.tools.SqlMapperGeneratorTool;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.util.List;
import java.util.Set;

/**
 * <b><code>BatchInsertPlugin</code></b>
 * <p/>
 * Description: 批量insert 和 insertSelective插件开发（Mybatis模式的时候）
 * <p/>
 * <b>Creation Time:</b> 2018/12/6 22:59.
 *
 * @author HuWeihui
 */
public class BatchInsertPlugin extends PluginAdapter {

    private final static String BATCH_INSERT = "batchInsert";

    private final static String PARAMETER_NAME = "recordList";

    @Override
    public boolean validate(List<String> list) {
        return true;
    }


    /**
     * java代码Mapper生成
     *
     * @param interfaze
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean clientGenerated(Interface interfaze,
                                   TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (introspectedTable.getTargetRuntime() == IntrospectedTable.TargetRuntime.MYBATIS3) {
            //生成batchInsert 和 batchInsertSelective的java方法
            addMethod(interfaze, introspectedTable);
        }
        return super.clientGenerated(interfaze, topLevelClass,
                introspectedTable);
    }

    /**
     * sqlMapper生成
     *
     * @param document
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean sqlMapDocumentGenerated(Document document,
                                           IntrospectedTable introspectedTable) {
        if (introspectedTable.getTargetRuntime().equals(IntrospectedTable.TargetRuntime.MYBATIS3)) {
            //生成batchInsert 和 batchInsertSelective的java方法
            addSqlMapper(document, introspectedTable);
        }
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    /**
     * batchInsert和batchInsertSelective的SQL生成
     *
     * @param document
     * @param introspectedTable
     */
    private void addSqlMapper(Document document, IntrospectedTable introspectedTable) {
        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
        List<IntrospectedColumn> columnList = introspectedTable.getAllColumns();

        //1.Batchinsert
        XmlElement insertElement = SqlMapperGeneratorTool.baseElementGenerator(SqlMapperGeneratorTool.INSERT,
                BATCH_INSERT, FullyQualifiedJavaType.getNewListInstance());

        XmlElement foreachElement = SqlMapperGeneratorTool.baseForeachElementGenerator(PARAMETER_NAME, "item", "index", ",");

        foreachElement.addElement(new TextElement("("));

        StringBuilder columnInfo = new StringBuilder();
        StringBuilder valuesInfo = new StringBuilder();

        for (int i = 0; i < columnList.size(); i++) {

            IntrospectedColumn introspectedColumn = columnList.get(i);
            if (introspectedColumn.isIdentity()) {
                continue;
            }
            columnInfo.append(introspectedColumn.getActualColumnName());
            valuesInfo.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, "item."));
            if (i != (columnList.size() - 1)) {
                columnInfo.append(",");
                valuesInfo.append(",");
            }
            foreachElement.addElement(new TextElement(valuesInfo.toString()));
            valuesInfo.delete(0, valuesInfo.length());
        }
        foreachElement.addElement(new TextElement(")"));

        String baseSql = String.format("insert into %s (%s) values ", tableName, columnInfo);

        insertElement.addElement(new TextElement(baseSql));

        insertElement.addElement(foreachElement);


        //3.parent Add
        document.getRootElement().addElement(insertElement);
    }

    /**
     * java的Mapper类生成
     *
     * @param interfaze
     * @param introspectedTable
     */
    private void addMethod(Interface interfaze, IntrospectedTable introspectedTable) {
        //获取基本需要导入的类型
        Set<FullyQualifiedJavaType> importedTypes = MethodGeneratorTool.importedBaseTypesGenerator(introspectedTable);

        //List<Entity>
        FullyQualifiedJavaType listParameterType = FullyQualifiedJavaType.getNewListInstance();
        listParameterType.addTypeArgument(introspectedTable.getRules().calculateAllFieldsClass());

        //1.batchInsert
        Method insertMethod = MethodGeneratorTool.methodGenerator(BATCH_INSERT,
                JavaVisibility.PUBLIC,
                FullyQualifiedJavaType.getIntInstance(),
                new Parameter(listParameterType, PARAMETER_NAME, "@Param(\"" + PARAMETER_NAME + "\")"));

        CommentGenerator commentGenerator = context.getCommentGenerator();
        commentGenerator.addGeneralMethodComment(insertMethod, introspectedTable);

        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(insertMethod);
    }
}
