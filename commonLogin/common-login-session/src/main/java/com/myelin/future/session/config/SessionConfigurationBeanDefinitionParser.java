package com.myelin.future.session.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.w3c.dom.Element;

/**
 * Created by gabriel on 14-11-27.
 */
public class SessionConfigurationBeanDefinitionParser implements BeanDefinitionParser {

    public static String BASE_LOGIN_ADAPTER_PATH = "classpath:adapter/adapter.xml";

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {

        XmlReaderContext context = parserContext.getReaderContext();
        ResourceLoader resLoader = context.getResourceLoader();
        XmlBeanDefinitionReader reader = context.getReader();

        Resource resource = resLoader.getResource(BASE_LOGIN_ADAPTER_PATH);
        reader.loadBeanDefinitions(resource);
        /**
         * 获取配置参数
         */
        String loginadapter = element.getAttribute("login-adapter");
        if (StringUtils.isNotBlank(loginadapter)) {
            String[] loginTypes = loginadapter.split(",");
            for (String loginEntry : loginTypes) {
                String resPath = "classpath:adapter/" + loginEntry + "_login.xml";
                Resource loginRes = resLoader.getResource(resPath);
                reader.loadBeanDefinitions(loginRes);
            }
        }
        return null;
    }
}
