package com.astamuse.asta4d.ide.eclipse.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.astamuse.asta4d.ide.eclipse.internal.SnippetMethodNameConvertor;
import com.astamuse.asta4d.ide.eclipse.internal.SnippetMethodNameConvertorFactory;
import com.astamuse.asta4d.ide.eclipse.property.Asta4dProperties;

public class SnippetMethodNameConvertorFactoryImpl implements SnippetMethodNameConvertorFactory {

    private static final SnippetMethodNameConvertorFactoryImpl instance = new SnippetMethodNameConvertorFactoryImpl();

    private SnippetMethodNameConvertorFactoryImpl() {

    }

    public static final SnippetMethodNameConvertorFactoryImpl getInstance() {
        return instance;
    }

    private Map<String, SnippetMethodNameConvertor> functionMap = new HashMap<>();

    public SnippetMethodNameConvertor getConvertor(Asta4dProperties properties) {
        if (properties.isSnippetPrefixesEnabled()) {
            return createPrefixConvertor(properties.getSnippetPrefixes());
        } else {
            return createFuncConvertor(properties.getSnippetConvertFunc());
        }
    }

    private SnippetMethodNameConvertor createPrefixConvertor(String[] prefixes) {
        return new SnippetMethodNameConvertor() {
            @Override
            public String[] convert(String name, boolean nameToClass) {
                List<String> list = new ArrayList<>(prefixes.length + 1);
                if (nameToClass) {
                    for (String prefix : prefixes) {
                        list.add(prefix + name);
                    }
                    list.add(name);
                } else {
                    String declareName;
                    for (String prefix : prefixes) {
                        if (name.startsWith(prefix)) {
                            declareName = name.substring(prefix.length());
                            list.add(declareName);
                        }
                    }
                    list.add(name);
                }
                return list.toArray(new String[list.size()]);
            }
        };
    }

    private SnippetMethodNameConvertor createFuncConvertor(String func) {
        try {

            SnippetMethodNameConvertor cachedConvertor = functionMap.get(func);
            if (cachedConvertor == null) {
                ScriptEngineManager engineManager = new ScriptEngineManager();
                ScriptEngine engine = engineManager.getEngineByName("nashorn");
                //@formatter:off
                String wrapperFunc = "function __wrapper__(name, nameToCls){\n"
                        + "  var result = convert(name, nameToCls); \n"
                        + "  return Java.to(result, \"java.lang.String[]\");"
                        + "}";
                //@formatter:on
                engine.eval(wrapperFunc);
                engine.eval(func);
                Invocable invocable = (Invocable) engine;
                cachedConvertor = new SnippetMethodNameConvertor() {
                    @Override
                    public String[] convert(String name, boolean nameToClass) {
                        try {
                            Object result = invocable.invokeFunction("__wrapper__", name, nameToClass);
                            return (String[]) result;
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    }
                };

                // simply do not cache too many
                if (functionMap.size() > 5) {
                    functionMap.clear();
                }

                functionMap.put(func, cachedConvertor);
            }
            return cachedConvertor;
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }
}
