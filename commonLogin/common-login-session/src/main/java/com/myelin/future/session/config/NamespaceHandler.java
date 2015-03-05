package com.myelin.future.session.config;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Created by gabriel on 14-11-27.
 */
public class NamespaceHandler extends NamespaceHandlerSupport{
    @Override
    public void init() {
        registerBeanDefinitionParser("session-config", new SessionConfigurationBeanDefinitionParser());
    }
}
