package com.astamuse.asta4d.ide.eclipse.util;

import java.util.ArrayList;
import java.util.List;

import com.astamuse.asta4d.ide.eclipse.internal.SnippetMethodNameConvertor;
import com.astamuse.asta4d.ide.eclipse.internal.SnippetMethodNameConvertorFactory;
import com.astamuse.asta4d.ide.eclipse.property.Asta4dProperties;

public class SnippetMethodNameConvertorFactoryImpl implements SnippetMethodNameConvertorFactory {

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
        return null;
    }
}
