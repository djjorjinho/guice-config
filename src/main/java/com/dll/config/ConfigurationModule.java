package com.dll.config;

import com.google.inject.AbstractModule;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigParseOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

import static com.typesafe.config.ConfigFactory.*;

public class ConfigurationModule extends AbstractModule {

    private final String path;
    private final String overridePath;
    private final boolean sysProps;
    private final boolean envProps;

    public ConfigurationModule() {
        this("application.conf", "application-override.conf", true, true);
    }

    public ConfigurationModule(String path, String overridePath, boolean sysProps, boolean envProps) {
        this.path = path;
        this.overridePath = overridePath;
        this.sysProps = sysProps;
        this.envProps = envProps;
    }

    @Override
    protected void configure() {
        ConfigBinder.bind(binder(), config());
    }

    protected Config config() {
        Config config = ConfigFactory.empty();
        config = sysProps ? config.withFallback(systemConfig()) : config;
        config = envProps ? config.withFallback(environmentConfig()) : config;
        config = config.withFallback(overrideConfig());
        config = config.withFallback(appConfig());
        config = config.withFallback(referenceConfig());
        return config.resolve();
    }

    private Config appConfig() {
        if (path.endsWith(".yaml") || path.endsWith(".yml")) {
            InputStream stream = getClass()
                    .getResourceAsStream(!path.startsWith("/") ? "/" + path : path);
            Map<String, Object> map = new Yaml().loadAs(stream, Map.class);
            return parseMap(map);
        }
        return parseResources(path, ConfigParseOptions.defaults());
    }

    private Config overrideConfig() {
        if (overridePath == null) {
            return empty();
        }
        if (overridePath.endsWith(".yaml") || overridePath.endsWith(".yml")) {
            InputStream inputStream = getClass()
                    .getResourceAsStream(!overridePath.startsWith("/") ? "/" + overridePath : overridePath);
            if (inputStream == null) {
                return empty();
            }
            Map<String, Object> map = new Yaml().loadAs(inputStream, Map.class);
            return parseMap(map);
        }
        return parseResources(overridePath, ConfigParseOptions.defaults().setAllowMissing(true));
    }

    private Config systemConfig() {
        return systemProperties();
    }

    private Config environmentConfig() {
        return systemEnvironment();
    }

    private Config referenceConfig() {
        return defaultReference();
    }
}
