package com.hui.mybatis.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

/**
 * <b><code>OracleReturnKeyPlugin</code></b>
 * <p/>
 * Description:
 * <p/>
 * <b>Creation Time:</b> 2018/12/19 1:27.
 *
 * @author HuWeihui
 */
public class OracleReturnKeyPlugin  extends PluginAdapter {
    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    /**
     * 新增SqlMapper.xml里面PostGreSql返回主键基础的insert方法
     * @param element
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean sqlMapInsertElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        XmlElement selectKey = addSelectKey(introspectedTable);
        element.addElement(0, selectKey);
        return super.sqlMapInsertElementGenerated(element, introspectedTable);
    }

    /**
     * 新增SqlMapper.xml里面PostGreSql返回主键基础的insertSelective方法
     * @param element
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        XmlElement selectKey = addSelectKey(introspectedTable);
        element.addElement(0, selectKey);
        return super.sqlMapInsertSelectiveElementGenerated(element, introspectedTable);
    }


    /**
     * 返回主键方法   ==> 一般sql如下面
     * <selectKey keyProperty="id" order="AFTER" resultType="java.lang.Integer">
     *       SELECT currval('t_hui_order_id_seq')
     * </selectKey>
     * @param introspectedTable
     * @return
     */
    private XmlElement addSelectKey(IntrospectedTable introspectedTable){
        String resultType = introspectedTable.getPrimaryKeyColumns().get(0).getFullyQualifiedJavaType().getFullyQualifiedName();
        String keyProperty = introspectedTable.getPrimaryKeyColumns().get(0).getActualColumnName();
        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
        String sql = "SELECT " + tableName + "_SEQUENCE.nextval from dual')";

        XmlElement selectKey = new XmlElement("selectKey");
        TextElement selectKeySQL = new TextElement(sql);
        selectKey.addAttribute(new Attribute("keyProperty",keyProperty));
        selectKey.addAttribute(new Attribute("order","BEFORE"));
        selectKey.addAttribute(new Attribute("resultType", resultType));
        selectKey.addElement(selectKeySQL);
        return selectKey;
    }
}
