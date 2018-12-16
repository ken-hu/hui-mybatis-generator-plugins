package com.hui.mybatis.plugins;

import com.hui.mybatis.tools.MethodGeneratorTool;
import com.hui.mybatis.tools.SqlMapperGeneratorTool;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.util.List;
import java.util.Set;

/**
 * <b><code>PostgreBatchUpdatePlugin</code></b>
 * <p/>
 * Description:
 * <p/>
 * <b>Creation Time:</b> 2018/12/15 1:12.
 *
 * @author HuWeihui
 */
public class PostgreBatchUpdatePlugin extends PluginAdapter {

    private final static String BATCH_UPDATE = "batchUpdate";

    private final static String PARAMETER_NAME = "recordList";

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (introspectedTable.getTargetRuntime().equals(IntrospectedTable.TargetRuntime.MYBATIS3)) {
            addMethod(interfaze, introspectedTable);
        }
        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        if (introspectedTable.getTargetRuntime().equals(IntrospectedTable.TargetRuntime.MYBATIS3)) {
            addSqlMapper(document, introspectedTable);
        }
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    private void addMethod(Interface interfaze, IntrospectedTable introspectedTable){
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

    private void addSqlMapper(Document document, IntrospectedTable introspectedTable){
        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
        List<IntrospectedColumn> columnList = introspectedTable.getAllColumns();

        XmlElement updateElement = SqlMapperGeneratorTool.baseElementGenerator(SqlMapperGeneratorTool.UPDATE,
                BATCH_UPDATE, FullyQualifiedJavaType.getNewListInstance());

        XmlElement foreachElement = SqlMapperGeneratorTool.baseForeachElementGenerator(PARAMETER_NAME, "item", "index", ",");
        foreachElement.addAttribute(new Attribute("open","("));
        foreachElement.addAttribute(new Attribute("close",")"));

        String baseSql = String.format("update %s", tableName);

        updateElement.addElement(new TextElement(baseSql));

        XmlElement setElement = new XmlElement("set");


        StringBuilder columnInfo = new StringBuilder();
        StringBuilder valuesInfo = new StringBuilder();

        StringBuilder columnInfoTotal = new StringBuilder();
        String key = "id";
        String keyJava = "id";
        for (int i = 0; i < columnList.size(); i++) {

            IntrospectedColumn introspectedColumn = columnList.get(i);
            if (introspectedColumn.isIdentity()) {
                key = introspectedColumn.getActualColumnName();
                keyJava = MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, "item.");
                continue;
            }
            columnInfo.append(introspectedColumn.getActualColumnName());
            columnInfoTotal.append(introspectedColumn.getActualColumnName());
            valuesInfo.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, "item."));
            if (i != (columnList.size() - 1)) {
                valuesInfo.append(",");
                columnInfo.append(",");
                columnInfoTotal.append(",");
            }
            String setSql = String.format(" %s = %s" ,columnInfo,"temp."+columnInfo);

            setElement.addElement(new TextElement(setSql));

            columnInfo.delete(0, valuesInfo.length());
        }


        foreachElement.addElement(new TextElement(valuesInfo.toString()));

        updateElement.addElement(setElement);

        updateElement.addElement(new TextElement("from (values"));

        updateElement.addElement(foreachElement);

        updateElement.addElement(new TextElement(String.format(") as temp (%s) where %s.%s=temp.%s;",columnInfoTotal,tableName,key,key)));

        //3.parent Add
        document.getRootElement().addElement(updateElement);
    }
}
