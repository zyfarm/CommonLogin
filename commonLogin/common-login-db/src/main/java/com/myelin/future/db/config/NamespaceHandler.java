package com.myelin.future.db.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Created by gabriel on 14-8-18.
 */
public class NamespaceHandler extends NamespaceHandlerSupport{
    @Override
    public void init() {
        registerBeanDefinitionParser("db-config", new DbConfigurationBeanDefinitionParser());
    }
}
