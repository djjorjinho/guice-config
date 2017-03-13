package com.dll.config;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.junit.Test;

import javax.inject.Named;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ConfigurationModuleTest {

    @Test
    public void testYamlConfig() {
        System.setProperty("app.bool", "false");
        Injector injector = Guice.createInjector(new ConfigurationModule("application.yml", null, false, false));
        TestSubject subject = injector.getInstance(TestSubject.class);
        assertEquals(subject.bool, true);
    }

    @Test
    public void testTypesafeConfig() {
        System.setProperty("app.bool", "true");
        Injector injector = Guice.createInjector(new ConfigurationModule());
        TestSubject subject = injector.getInstance(TestSubject.class);
        assertEquals(subject.bool, true);
        assertEquals(subject.number.intValue(), 123);
    }

    public static class TestSubject {
        @Inject(optional=true) @Named("app.ignore")
        public String ignore;

        @Inject @Named("app.text")
        public String text;

        @Inject @Named("app.number")
        public Integer number;

        @Inject @Named("app.bool")
        public Boolean bool;

        @Inject @Named("app.list")
        public List<Number> list;

        @Inject @Named("app.boolList")
        public List<Boolean> booleanList;

        @Inject @Named("app.boolList.0")
        public Boolean boolZero;
    }

}