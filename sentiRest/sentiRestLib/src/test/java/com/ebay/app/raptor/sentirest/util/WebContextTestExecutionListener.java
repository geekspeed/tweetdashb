package com.ebay.app.raptor.sentirest.util;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.web.context.WebApplicationContext;

public class WebContextTestExecutionListener extends AbstractTestExecutionListener{
    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {

        if (testContext.getApplicationContext() instanceof GenericApplicationContext) {
   

            GenericApplicationContext context = (GenericApplicationContext) testContext.getApplicationContext();
            ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
            Scope requestScope = (Scope) new SimpleThreadScope();
           beanFactory.registerScope(WebApplicationContext.SCOPE_REQUEST, requestScope);
            Scope sessionScope = new SimpleThreadScope();
            beanFactory.registerScope(WebApplicationContext.SCOPE_SESSION, sessionScope);
        }
    }

}
