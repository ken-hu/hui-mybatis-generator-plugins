package com.hui.mybatis.plugins;

import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;

import java.util.List;

/**
 * <b><code>OverWriteXmlPlugin</code></b>
 * <p/>
 * Description: 执行generator的时候覆盖旧的XML插件
 * <p/>
 * <b>Creation Time:</b> 2018/12/10 0:22.
 *
 * @author HuWeihui
 */
public class OverWriteXmlPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
        //设置不在源文件追加新内容
        sqlMap.setMergeable(false);
        return super.sqlMapGenerated(sqlMap, introspectedTable);
    }
}
