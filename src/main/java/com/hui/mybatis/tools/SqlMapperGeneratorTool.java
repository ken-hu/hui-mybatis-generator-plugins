package com.hui.mybatis.tools;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;


/**
 * <b><code>SqlMapperGeneratorTool</code></b>
 * <p/>
 * Description:
 * <p/>
 * <b>Creation Time:</b> 2018/12/15 18:40.
 *
 * @author HuWeihui
 */
public class SqlMapperGeneratorTool {

    public static final String INSERT = "insert";

    public static final String DELETE = "delete";

    public static final String UPDATE = "update";

    public static final String SELECT = "select";


    public static XmlElement baseElementGenerator(String sqlElementType, String sqlMapperId,FullyQualifiedJavaType parameterType){

        XmlElement baseElement = new XmlElement(sqlElementType);

        baseElement.addAttribute(new Attribute("id", sqlMapperId));

        baseElement.addAttribute(new Attribute("parameterType", parameterType.getFullyQualifiedName()));

        return baseElement;
    }


    public static XmlElement baseForeachElementGenerator(String collectionName,String itemName,String indexName ,String separatorName){
        XmlElement foreachElement = new XmlElement("foreach");
        if (null!=collectionName){
            foreachElement.addAttribute(new Attribute("collection", collectionName));
        }
        if (null!=itemName){
            foreachElement.addAttribute(new Attribute("item", itemName));
        }
        if (null!=indexName){
            foreachElement.addAttribute(new Attribute("index", indexName));
        }
        if (null!=separatorName){
            foreachElement.addAttribute(new Attribute("separator", separatorName));
        }
        return foreachElement;
    }

    public static XmlElement baseIfJudgeElementGen(String columnJavaTypeName,String sql ,boolean judgeNull){
        String colmunJudge = "";
        if (judgeNull){
            colmunJudge = columnJavaTypeName + " ==null ";
        }else {
            colmunJudge = columnJavaTypeName + " !=null ";
        }
        XmlElement ifElement = new XmlElement("if");
        ifElement.addAttribute(new Attribute("test", colmunJudge));
        ifElement.addElement(new TextElement(sql));
        return ifElement;
    }

    public static XmlElement baseTrimElement(String prefix,String suffix,String suffixOverrides){
        XmlElement trimElement = new XmlElement("trim");
        if (null != prefix){
            trimElement.addAttribute(new Attribute("prefix", prefix));
        }
        if (null != suffix){
            trimElement.addAttribute(new Attribute("suffix", suffix));
        }
        if (null!=suffixOverrides){
            trimElement.addAttribute(new Attribute("suffixOverrides", suffixOverrides));
        }
        return trimElement;
    }
}
