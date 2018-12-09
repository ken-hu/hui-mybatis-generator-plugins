package com.hui.mybatis.backup;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.List;

/**
 * <b><code>SelectPageKeyPlugin</code></b>
 * <p/>
 * Description:
 * <p/>
 * <b>Creation Time:</b> 2018/12/6 23:36.
 *
 * @author HuWeihui
 */
public class SelectPageKeyPlugin extends PluginAdapter {

    private static final String IS_GEN_PAGINATION = "generate.pagination";


    public boolean validate(List<String> list) {
        return true;
    }


    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        boolean super_result = super.clientGenerated(interfaze, topLevelClass, introspectedTable);

        // 生成方法
        Method newMethod = new Method("selectByPage");
        // 设置方法类型
        newMethod.setVisibility(JavaVisibility.PUBLIC);

        // 设置方法返回值类型
        FullyQualifiedJavaType returnType = new FullyQualifiedJavaType("List<" + introspectedTable.getTableConfiguration().getDomainObjectName() + ">");
        newMethod.setReturnType(returnType);

        // 设置方法参数
        FullyQualifiedJavaType offsetJavaType = new FullyQualifiedJavaType("Long");
        Parameter offsetParameter = new Parameter(offsetJavaType, "offset");
        offsetParameter.addAnnotation("@Param(\"offset\")");
        newMethod.addParameter(0, offsetParameter);

        FullyQualifiedJavaType limitJavaType = new FullyQualifiedJavaType("Long");
        Parameter limitParameter = new Parameter(limitJavaType, "limit");
        limitParameter.addAnnotation("@Param(\"limit\")");
        newMethod.addParameter(1, limitParameter);

        // 添加相应的包
        interfaze.addImportedType(new FullyQualifiedJavaType(("org.apache.ibatis.annotations.Param")));
        interfaze.addImportedType(new FullyQualifiedJavaType("java.util.List"));
        interfaze.addMethod(newMethod);

        return true;

    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
//        if (isGeneratePagination(introspectedTable)) {
        XmlElement select = new XmlElement("select");
        select.addAttribute(new Attribute("id", "selectByPage"));
        select.addAttribute(new Attribute("resultMap", "BaseResultMap"));
        select.addElement(new TextElement("SELECT <include refid=\"Base_Column_List\" /> FROM " + introspectedTable.getFullyQualifiedTableNameAtRuntime() + " LIMIT #{offset},#{limit}"));
        XmlElement parentElement = document.getRootElement();
        parentElement.addElement(select);
        return super.sqlMapDocumentGenerated(document, introspectedTable);
//        }

//        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }


    /**
     * 判断参数generate.pagination.是否生成
     */
    private boolean isGeneratePagination(IntrospectedTable introspectedTable) {
        String is_generate_pagination = introspectedTable.getTableConfigurationProperty(IS_GEN_PAGINATION);
        if (StringUtility.stringHasValue(is_generate_pagination)) {
            return Boolean.valueOf(is_generate_pagination);
        } else {
            return false;
        }
    }
}
