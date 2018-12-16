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
 * <b><code>MysqlBatchUpdatePlugin</code></b>
 * <p/>
 * Description:
 * <p/>
 * <b>Creation Time:</b> 2018/12/10 0:48.
 *
 * @author HuWeihui
 */
public class MysqlBatchUpdatePlugin extends PluginAdapter {

    private final static String BATCH_UPDATE = "batchUpdate";

    private final static String PARAMETER_NAME = "recordList";


    @Override
    public boolean validate(List<String> list) {
        return true;
    }


    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        if (introspectedTable.getTargetRuntime().equals(IntrospectedTable.TargetRuntime.MYBATIS3)) {
            addUpdateMethod(interfaze, introspectedTable);
        }
        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        if (introspectedTable.getTargetRuntime().equals(IntrospectedTable.TargetRuntime.MYBATIS3)) {
            addUpdateSqlMapper(document, introspectedTable);
        }
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    public void addUpdateSqlMapper(Document document, IntrospectedTable introspectedTable) {
        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
        List<IntrospectedColumn> columnList = introspectedTable.getAllColumns();

        XmlElement updateXmlElement = SqlMapperGeneratorTool.baseElementGenerator(SqlMapperGeneratorTool.UPDATE,
                BATCH_UPDATE,
                FullyQualifiedJavaType.getNewListInstance());


        String sql = String.format("update %s ", tableName);

        updateXmlElement.addElement(new TextElement(sql));

        XmlElement trimElement = SqlMapperGeneratorTool.baseTrimElement("set", null, ",");

        String key = "id";

        String keyJava = "id";

        for (int i = 0; i < columnList.size(); i++) {
            String valuesInfo = "";

            IntrospectedColumn introspectedColumn = columnList.get(i);

            String columnName = introspectedColumn.getActualColumnName();

            String columnJavaTypeName = introspectedColumn.getJavaProperty("item.");

            String parameterClause = MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, "item.");


            if (introspectedColumn.isIdentity()) {
                key = columnName;
                keyJava = parameterClause;
                continue;
            }

            String ifSql = String.format("when %s then %s", keyJava, parameterClause);
            XmlElement ifElement = SqlMapperGeneratorTool.baseIfJudgeElementGen(columnJavaTypeName, ifSql, false);

            String ifNullSql = String.format("when %s then %s", keyJava, tableName + "." + columnName);
            XmlElement ifNullElement = SqlMapperGeneratorTool.baseIfJudgeElementGen(columnJavaTypeName, ifNullSql, true);


            XmlElement foreachElement = SqlMapperGeneratorTool.baseForeachElementGenerator(PARAMETER_NAME, "item", "index", null);
            foreachElement.addElement(ifElement);
            foreachElement.addElement(ifNullElement);

            XmlElement caseTrimElement = SqlMapperGeneratorTool.baseTrimElement(columnName + " =case " + key, "end,", null);
            caseTrimElement.addElement(foreachElement);

            trimElement.addElement(caseTrimElement);
        }

        updateXmlElement.addElement(trimElement);

        document.getRootElement().addElement(updateXmlElement);
    }

    public void addUpdateMethod(Interface interfaze, IntrospectedTable introspectedTable) {
        Set<FullyQualifiedJavaType> importedTypes = MethodGeneratorTool.importedBaseTypesGenerator(introspectedTable);

        //List包住实体类
        FullyQualifiedJavaType listParameterType = FullyQualifiedJavaType.getNewListInstance();
        listParameterType.addTypeArgument(introspectedTable.getRules().calculateAllFieldsClass());

        Method updateMethod = MethodGeneratorTool.methodGenerator(BATCH_UPDATE,
                JavaVisibility.PUBLIC,
                FullyQualifiedJavaType.getIntInstance(),
                new Parameter(listParameterType, PARAMETER_NAME, "@Param(\"" + PARAMETER_NAME + "\")"));

        CommentGenerator commentGenerator = context.getCommentGenerator();
        commentGenerator.addGeneralMethodComment(updateMethod, introspectedTable);

        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(updateMethod);
    }

}
