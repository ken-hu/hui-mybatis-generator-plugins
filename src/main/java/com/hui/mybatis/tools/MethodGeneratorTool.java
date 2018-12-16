package com.hui.mybatis.tools;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;

import java.util.Set;
import java.util.TreeSet;

/**
 * <b><code>MethodGeneratorTool</code></b>
 * <p/>
 * Description: 方法生成器
 * <p/>
 * <b>Creation Time:</b> 2018/12/14 0:50.
 *
 * @author HuWeihui
 */
public class MethodGeneratorTool {

    /**
     * java方法生成构造器.
     *
     * @param methodName     the method name
     * @param visibility     the visibility
     * @param returnJavaType the return java type
     * @param parameters     the parameters
     * @author HuWeihui
     * @since hui_project v1
     */
    public static Method methodGenerator(String methodName,
                                       JavaVisibility visibility,
                                       FullyQualifiedJavaType returnJavaType,
                                       Parameter... parameters) {
        Method method = new Method();
        method.setName(methodName);
        method.setVisibility(visibility);
        for (Parameter parameter : parameters) {
            method.addParameter(parameter);
        }
        return method;
    }

    /**
     * 导入基础的java类型
     *
     * @param introspectedTable the introspected table
     * @return the set
     * @author HuWeihui
     * @since hui_project v1
     */
    public static Set<FullyQualifiedJavaType> importedBaseTypesGenerator(IntrospectedTable introspectedTable){
        //获取实体类类型
        FullyQualifiedJavaType parameterType = introspectedTable.getRules().calculateAllFieldsClass();
        //@Param需要导入的类型
        FullyQualifiedJavaType paramType = new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param");
        //Integer类型
        FullyQualifiedJavaType intInstance = FullyQualifiedJavaType.getIntInstance();
        //List<Entity>
        FullyQualifiedJavaType listParameterType = FullyQualifiedJavaType.getNewListInstance();

        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        importedTypes.add(parameterType);
        importedTypes.add(intInstance);
        importedTypes.add(paramType);
        importedTypes.add(listParameterType);
        return importedTypes;
    }


}
