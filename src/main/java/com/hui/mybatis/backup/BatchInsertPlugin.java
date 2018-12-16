package com.hui.mybatis.backup;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.config.GeneratedKey;

import java.util.*;

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

    private final static String BATCH_INSERTSELECTIVE = "batchInsertSelective";

    public boolean validate(List<String> list) {
        return true;
    }


    /**
     * java代码Mapper生成
     * @param interfaze
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean clientGenerated(Interface interfaze,
                                   TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        if (introspectedTable.getTargetRuntime() == IntrospectedTable.TargetRuntime.MYBATIS3) {
            addBatchInsertMethod(interfaze, introspectedTable);
        }

        return super.clientGenerated(interfaze, topLevelClass,
                introspectedTable);
    }

    /**
     * sqlMapper生成
     * @param document
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean sqlMapDocumentGenerated(Document document,
                                           IntrospectedTable introspectedTable) {
        if (introspectedTable.getTargetRuntime().equals(IntrospectedTable.TargetRuntime.MYBATIS3)) {
            addBatchInsertSqlMap(document.getRootElement(), introspectedTable);
        }
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    /**
     * batchInsert方法生成
     * @param parentElement
     * @param introspectedTable
     */
    private void addBatchInsertSqlMap(XmlElement parentElement, IntrospectedTable introspectedTable) {
        /*
        <insert id="batchInsert" parameterType="com.hui.base.springboot.model.Order">
    insert into t_hui_order (order_id,
      order_name, product_id, buy_quantity
      )
    values <foreach collection="list" item="item" index="index" separator="," > (#{item.orderId,jdbcType=VARCHAR},
      #{item.orderName,jdbcType=VARCHAR}, #{item.productId,jdbcType=VARCHAR}, #{item.buyQuantity,jdbcType=INTEGER}
      )</foreach>
  </insert>
  */
        XmlElement answer = new XmlElement("insert");

        answer.addAttribute(new Attribute("id", BATCH_INSERT));

        FullyQualifiedJavaType parameterType;
        parameterType = introspectedTable.getRules().calculateAllFieldsClass();

        answer.addAttribute(new Attribute("parameterType", parameterType.getFullyQualifiedName()));

        context.getCommentGenerator().addComment(answer);

        GeneratedKey gk = introspectedTable.getGeneratedKey();
        if (gk != null) {
            IntrospectedColumn introspectedColumn = introspectedTable
                    .getColumn(gk.getColumn());
            if (introspectedColumn != null) {
                if (gk.isJdbcStandard()) {
                    answer.addAttribute(new Attribute(
                            "useGeneratedKeys", "true"));
                    answer.addAttribute(new Attribute(
                            "keyProperty", introspectedColumn.getJavaProperty()));
                } else {
                    answer.addElement(getSelectKey(introspectedColumn, gk));
                }
            }
        }

        StringBuilder insertClause = new StringBuilder();
        StringBuilder valuesClause = new StringBuilder();

        insertClause.append("insert into ");
        insertClause.append(introspectedTable
                .getFullyQualifiedTableNameAtRuntime());
        insertClause.append(" (");

        valuesClause.append("values <foreach collection=\"list\" item=\"item\" index=\"index\" separator=\",\" > (");


        List<String> valuesClauses = new ArrayList<String>();
        Iterator<IntrospectedColumn> iter = introspectedTable.getAllColumns()
                .iterator();
        while (iter.hasNext()) {
            IntrospectedColumn introspectedColumn = iter.next();
            if (introspectedColumn.isIdentity()) {
                // cannot set values on identity fields
                continue;
            }

            insertClause.append(MyBatis3FormattingUtilities
                    .getEscapedColumnName(introspectedColumn));

            // 批量插入,如果是sequence字段,则插入不需要item.前缀
            if (introspectedColumn.isSequenceColumn()) {
                valuesClause.append(MyBatis3FormattingUtilities
                        .getParameterClause(introspectedColumn));
            } else {
                valuesClause.append(MyBatis3FormattingUtilities
                        .getParameterClause(introspectedColumn, "item."));
            }
            if (iter.hasNext()) {
                insertClause.append(", ");
                valuesClause.append(", ");
            }

            if (valuesClause.length() > 80) {
                answer.addElement(new TextElement(insertClause.toString()));
                insertClause.setLength(0);
                OutputUtilities.xmlIndent(insertClause, 1);

                valuesClauses.add(valuesClause.toString());
                valuesClause.setLength(0);
                OutputUtilities.xmlIndent(valuesClause, 1);
            }
        }

        insertClause.append(')');
        answer.addElement(new TextElement(insertClause.toString()));

        valuesClause.append(")</foreach>");
        valuesClauses.add(valuesClause.toString());

        for (String clause : valuesClauses) {
            answer.addElement(new TextElement(clause));
        }

        parentElement.addElement(answer);
    }

    protected XmlElement getSelectKey(IntrospectedColumn introspectedColumn,
                                      GeneratedKey generatedKey) {
        String identityColumnType = introspectedColumn
                .getFullyQualifiedJavaType().getFullyQualifiedName();

        XmlElement answer = new XmlElement("selectKey");
        answer.addAttribute(new Attribute("resultType", identityColumnType));
        answer.addAttribute(new Attribute(
                "keyProperty", introspectedColumn.getJavaProperty()));
        answer.addAttribute(new Attribute("order",
                generatedKey.getMyBatis3Order()));

        answer.addElement(new TextElement(generatedKey
                .getRuntimeSqlStatement()));

        return answer;
    }


    private void addBatchInsertMethod(Interface interfaze, IntrospectedTable introspectedTable) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        Method method = new Method();

        method.setReturnType(new FullyQualifiedJavaType("void"));
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName(BATCH_INSERT);

        //获取实体类类型
        FullyQualifiedJavaType parameterType = introspectedTable.getRules().calculateAllFieldsClass();

        importedTypes.add(parameterType);

        FullyQualifiedJavaType listParamType = new FullyQualifiedJavaType("List<" + parameterType + ">");

        method.addParameter(new Parameter(listParamType, "recordLst"));

        context.getCommentGenerator().addGeneralMethodComment(method,
                introspectedTable);

        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(method);
    }
}
