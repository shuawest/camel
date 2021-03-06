/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.bean.validator;

import java.lang.annotation.ElementType;
import java.util.Locale;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.validation.Path;
import javax.validation.Path.Node;
import javax.validation.TraversableResolver;

import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.impl.ProcessorEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.hibernate.validator.internal.engine.ValidatorImpl;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorFactoryImpl;
import org.hibernate.validator.internal.engine.resolver.DefaultTraversableResolver;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.junit.Test;

/**
 * @version 
 */
public class BeanValidatorConfigurationTest extends CamelTestSupport {
    
    private MessageInterpolator messageInterpolator;
    private TraversableResolver traversableResolver;
    private ConstraintValidatorFactory constraintValidatorFactory;
    
    @Override
    public void setUp() throws Exception {
        this.messageInterpolator = new MyMessageInterpolator();
        this.traversableResolver = new MyTraversableResolver();
        this.constraintValidatorFactory = new MyConstraintValidatorFactory();
        
        super.setUp();
    }
    
    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        
        registry.bind("myMessageInterpolator", this.messageInterpolator);
        registry.bind("myTraversableResolver", this.traversableResolver);
        registry.bind("myConstraintValidatorFactory", this.constraintValidatorFactory);
        return registry;
    }
    
    @Test
    public void configureWithDefaults() throws Exception {
        ProcessorEndpoint endpoint = context.getEndpoint("bean-validator://x", ProcessorEndpoint.class);
        BeanValidator processor = (BeanValidator) endpoint.getProcessor();

        assertNull(processor.getGroup());
        assertTrue(processor.getValidator() instanceof ValidatorImpl);
        assertTrue(processor.getMessageInterpolator() instanceof ResourceBundleMessageInterpolator);
        assertTrue(processor.getTraversableResolver() instanceof DefaultTraversableResolver);
        assertTrue(processor.getConstraintValidatorFactory() instanceof ConstraintValidatorFactoryImpl);
    }
    
    @Test
    public void configureBeanValidator() throws Exception {
        ProcessorEndpoint endpoint = context.getEndpoint("bean-validator://x"
                + "?group=org.apache.camel.component.bean.validator.OptionalChecks"
                + "&messageInterpolator=#myMessageInterpolator"
                + "&traversableResolver=#myTraversableResolver"
                + "&constraintValidatorFactory=myConstraintValidatorFactory", ProcessorEndpoint.class);
        BeanValidator processor = (BeanValidator) endpoint.getProcessor();

        assertEquals("org.apache.camel.component.bean.validator.OptionalChecks", processor.getGroup().getName());
        assertTrue(processor.getValidator() instanceof ValidatorImpl);
        assertSame(processor.getMessageInterpolator(), this.messageInterpolator);
        assertSame(processor.getTraversableResolver(), this.traversableResolver);
        assertSame(processor.getConstraintValidatorFactory(), this.constraintValidatorFactory);
    }

    class MyMessageInterpolator implements MessageInterpolator {

        public String interpolate(String messageTemplate, Context context) {
            return null;
        }

        public String interpolate(String messageTemplate, Context context, Locale locale) {
            return null;
        }
    }

    class MyTraversableResolver implements TraversableResolver {

        public boolean isCascadable(Object traversableObject, Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject, ElementType elementType) {
            return false;
        }

        public boolean isReachable(Object traversableObject, Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject, ElementType elementType) {
            return false;
        }
    }
    
    class MyConstraintValidatorFactory implements ConstraintValidatorFactory {

        public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
            return null;
        }

        @Override
        public void releaseInstance(ConstraintValidator<?, ?> arg0) {
            // noop
        }
    }
}