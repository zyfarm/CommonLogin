package com.myelin.future.db.config;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.w3c.dom.Element;

/**
 * Created by gabriel on 14-8-18.
 */
public class DbConfigurationBeanDefinitionParser implements BeanDefinitionParser {


    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {

        /**
         * 获取配置参数
         */
        String orm = element.getAttribute("orm");
        String dbhost = element.getAttribute("db-host");
        String dbname = element.getAttribute("db-name");
        String dbport = element.getAttribute("db-port");

        /**
         * 加载必须的配置
         */
        XmlReaderContext context = parserContext.getReaderContext();
        ResourceLoader resLoader = context.getResourceLoader();
        XmlBeanDefinitionReader reader = context.getReader();


        Resource resource = resLoader.getResource("classpath:db/db.xml");
        reader.loadBeanDefinitions(resource);
        AbstractBeanDefinition definition = (AbstractBeanDefinition) reader.getRegistry().getBeanDefinition("dataSource");
        MutablePropertyValues propertyValues = definition.getPropertyValues();
        definition = (AbstractBeanDefinition) reader.getRegistry().getBeanDefinition("dataSource");
        propertyValues = definition.getPropertyValues();
        PropertyValue propertyValue = propertyValues.getPropertyValue("url");
        String dbConfig = dbport + "/" + dbname;
        PropertyValue pv = new PropertyValue("url", "jdbc:mysql://" + dbhost + ":" + dbConfig + "?useUnicode=true&amp;characterEncoding=UTF-8&amp;autoReconnect=true&amp;autoReconnectForPools=true&amp;zeroDateTimeBehavior=convertToNull");
        propertyValues.removePropertyValue(propertyValue);


        propertyValues.addPropertyValue(pv);
        definition.setPropertyValues(propertyValues);


        /**
         * 根据配置,加载不同的bean
         */
        if (orm.equals("mybatis")) {
            Resource resource1 = resLoader.getResource("classpath:mybatis/db-mybatis.xml");
            Resource resource2 = resLoader.getResource("classpath:mybatis/db-mybatis-config.xml");
            reader.loadBeanDefinitions(resource1);
            reader.loadBeanDefinitions(resource2);
        } else if (orm.equals("jpa")) {
            Resource resource1 = resLoader.getResource("classpath:jpa/db-jpa.xml");
            reader.loadBeanDefinitions(resource1);
        } else {
            Resource resource1 = resLoader.getResource("classpath:ibatis/db-ibatis.xml");
            reader.loadBeanDefinitions(resource1);
        }

        return null;
    }
}