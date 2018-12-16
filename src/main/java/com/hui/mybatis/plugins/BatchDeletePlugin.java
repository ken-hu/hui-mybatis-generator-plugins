package com.hui.mybatis.plugins;

import com.hui.mybatis.tools.MethodGeneratorTool;
import com.hui.mybatis.tools.SqlMapperGeneratorTool;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;
import java.util.Set;

/**
 * <b><code>BatchDeletePlugin</code></b>
 * <p/>
 * Description:
 * <p/>
 * <b>Creation Time:</b> 2018/12/10 0:48.
 *
 * @author HuWeihui
 */
public class BatchDeletePlugin extends PluginAdapter {

    private final static String BATCH_DELETE = "batchDelete";

    private final static String PARAMETER_NAME = "ids";

    @Override
    public boolean validate(List<String> list) {
        return true;
    }


    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (introspectedTable.getTargetRuntime().equals(IntrospectedTable.TargetRuntime.MYBATIS3)) {
            addBatchDeleteMethod(interfaze, introspectedTable);
        }
        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        if (introspectedTable.getTargetRuntime().equals(IntrospectedTable.TargetRuntime.MYBATIS3)){
            addBatchDeleteSqlMap(document.getRootElement(), introspectedTable);
        }
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    /**
     * 批量删除java方法生成
     * @param interfaze
     * @param introspectedTable
     */
    private void addBatchDeleteMethod(Interface interfaze ,IntrospectedTable introspectedTable){
        Set<FullyQualifiedJavaType> importedTypes = MethodGeneratorTool.importedBaseTypesGenerator(introspectedTable);

        Method batchDeleteMethod = MethodGeneratorTool.methodGenerator(BATCH_DELETE,
                JavaVisibility.PUBLIC,
                FullyQualifiedJavaType.getIntInstance(),
                new Parameter(new FullyQualifiedJavaType("Integer[]"), PARAMETER_NAME, "@Param(\""+PARAMETER_NAME+"\")"));

        context.getCommentGenerator().addGeneralMethodComment(batchDeleteMethod,introspectedTable);
        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(batchDeleteMethod);
    }


    /**
     * 批量删除的xml方法生成
     * @param parentElement
     * @param introspectedTable
     */
    private void addBatchDeleteSqlMap(XmlElement parentElement, IntrospectedTable introspectedTable){
        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();

        String key = introspectedTable.getPrimaryKeyColumns().get(0).getActualColumnName();

        String baseSql = String.format("delete from %s where %s in (",tableName,key);

        XmlElement deleteElement = SqlMapperGeneratorTool.baseElementGenerator(SqlMapperGeneratorTool.DELETE, BATCH_DELETE, FullyQualifiedJavaType.getIntInstance());

        XmlElement foreachElement = SqlMapperGeneratorTool.baseForeachElementGenerator(PARAMETER_NAME,"item","index",null);

        deleteElement.addElement(new TextElement(baseSql));

        foreachElement.addAttribute(new Attribute("separator", ","));

        foreachElement.addElement(new TextElement("#{item}"));

        deleteElement.addElement(foreachElement);

        deleteElement.addElement(new TextElement(")"));

        parentElement.addElement(deleteElement);
    }
}
