package com.hui.mybatis.backup;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

/**
 * <b><code>OracelPageLimitPlugin</code></b>
 * <p/>
 * Description:
 * <p/>
 * <b>Creation Time:</b> 2018/12/6 23:33.
 *
 * @author HuWeihui
 */
public class OracelPageLimitPlugin extends PluginAdapter {

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass,
                                              IntrospectedTable introspectedTable) {
        // add field, getter, setter for limit clause  
        addLimit(topLevelClass, introspectedTable, "limitClauseStart");
        addLimit(topLevelClass, introspectedTable, "limitClauseCount");
        return super.modelExampleClassGenerated(topLevelClass,
                introspectedTable);
    }

    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
//        XmlElement isParameterPresenteElemen = (XmlElement) element  
//                .getElements().get(element.getElements().size() - 1); 
        XmlElement isStart = new XmlElement("if");
        isStart.addAttribute(new Attribute("test", "limitClauseStart != null and limitClauseStart >= 0"));
        isStart.addElement(new TextElement(
                " select * from (select t_1.*,rownum as row_num from ("));

        element.getElements().add(0, isStart);

        XmlElement isNotNullElement = new XmlElement("if");
        isNotNullElement.addAttribute(new Attribute("test", "limitClauseStart != null and limitClauseStart >= 0"));
        isNotNullElement.addElement(new TextElement(
                " <![CDATA[ ) t_1 where rownum<=#{limitClauseCount,jdbcType=INTEGER} + #{limitClauseStart,jdbcType=INTEGER}) t_2 where t_2.row_num>#{limitClauseStart,jdbcType=INTEGER}]]>"));
        element.getElements().add(element.getElements().size(), isNotNullElement);

        // isParameterPresenteElemen.addElement(isNotNullElement);  
        return super.sqlMapUpdateByExampleWithoutBLOBsElementGenerated(element,
                introspectedTable);
    }

    @Override
    public boolean sqlMapSelectByExampleWithBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
        XmlElement isStart = new XmlElement("if");
        isStart.addAttribute(new Attribute("test", "limitClauseStart != null and limitClauseStart >= 0"));
        isStart.addElement(new TextElement(
                "select * from (select t_1.*,rownum as row_num from ("));
        element.getElements().add(0, isStart);

        XmlElement isNotNullElement = new XmlElement("if");
        isNotNullElement.addAttribute(new Attribute("test", "limitClauseStart != null and limitClauseStart >= 0"));
        isNotNullElement.addElement(new TextElement(
                " <![CDATA[ ) t_1 where rownum<=#{limitClauseCount,jdbcType=INTEGER} + #{limitClauseStart,jdbcType=INTEGER}) t_2 where t_2.row_num>#{limitClauseStart,jdbcType=INTEGER}]]>"));
        element.getElements().add(element.getElements().size(), isNotNullElement);

        return super.sqlMapUpdateByExampleWithoutBLOBsElementGenerated(element,
                introspectedTable);
    }

    private void addLimit(TopLevelClass topLevelClass,
                          IntrospectedTable introspectedTable, String name) {
        CommentGenerator commentGenerator = context.getCommentGenerator();
        Field field = new Field();
        field.setVisibility(JavaVisibility.PROTECTED);
        field.setType(FullyQualifiedJavaType.getIntInstance());
        field.setName(name);
        field.setInitializationString("-1");
        commentGenerator.addFieldComment(field, introspectedTable);
        topLevelClass.addField(field);
        char c = name.charAt(0);
        String camel = Character.toUpperCase(c) + name.substring(1);
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("set" + camel);
        method.addParameter(new Parameter(FullyQualifiedJavaType
                .getIntInstance(), name));
        method.addBodyLine("this." + name + "=" + name + ";");
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setName("get" + camel);
        method.addBodyLine("return " + name + ";");
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
    }

    public boolean validate(List<String> warnings) {
        return true;
    }
}
