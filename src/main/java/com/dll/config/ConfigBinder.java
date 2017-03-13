package com.dll.config;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;

import java.util.List;

import static com.google.inject.name.Names.named;
import static java.util.stream.Collectors.toList;

public class ConfigBinder {

    public static void bind(Binder binder, Config config) {
        config.entrySet().forEach( entry -> bind(binder, entry.getValue(), entry.getKey()));
    }

    private static void bind(Binder binder, ConfigValue obj, String bindingPath) {
        switch (obj.valueType()) {
            case OBJECT :
                ConfigObject cfgObj = (ConfigObject) obj;
                binder.bind(Key.get(Config.class, named(bindingPath))).toInstance(cfgObj.toConfig());
                cfgObj.entrySet().forEach(entry -> bind(binder, entry.getValue(), bindingPath + '.' + entry.getKey()));
            break;
            case LIST:
                ConfigList cfgList = (ConfigList) obj;
                List<Object> unwrapped = cfgList.unwrapped();
                Object o = unwrapped.stream().findFirst().orElse(null);
                if (o instanceof Number) {
                    binder.bind(Key.get(new TypeLiteral<List<Number>>() {}, named(bindingPath)))
                            .toInstance(unwrapped.stream().map(i -> (Number) i).collect(toList()));
                } else if (o instanceof Boolean) {
                    binder.bind(Key.get(new TypeLiteral<List<Boolean>>() {}, named(bindingPath)))
                            .toInstance(unwrapped.stream().map(i -> (Boolean) i).collect(toList()));
                } else {
                    binder.bind(Key.get(new TypeLiteral<List<String>>() {}, named(bindingPath)))
                            .toInstance(unwrapped.stream().map(Object::toString).collect(toList()));
                }
                for (int i = 0; i < cfgList.size(); i++) {
                    bind(binder, cfgList.get(i), bindingPath + '.' + i);
                }
            break;
            case NUMBER:
                binder.bindConstant()
                    .annotatedWith(named(bindingPath))
                    .to(obj.unwrapped().toString());
            break;
            case BOOLEAN:
                binder.bindConstant()
                    .annotatedWith(named(bindingPath))
                    .to((Boolean) obj.unwrapped());
            break;
            case STRING:
                binder.bindConstant()
                    .annotatedWith(named(bindingPath))
                    .to(obj.unwrapped().toString());
            break;
            case NULL:
                // ignore
            break;
            default:
        }
    }

}
