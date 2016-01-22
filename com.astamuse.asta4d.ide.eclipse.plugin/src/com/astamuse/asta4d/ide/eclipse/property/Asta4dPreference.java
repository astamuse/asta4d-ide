package com.astamuse.asta4d.ide.eclipse.property;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.osgi.service.prefs.BackingStoreException;

import com.astamuse.asta4d.ide.eclipse.Activator;

public class Asta4dPreference {

    private static final String DEFAULT_NAMESPACE = "afd";

    private static final String DEFAULT_PREFIX_ENABLED = "true";

    private static final String DEFAULT_PREFIX = "com.astamuse.asta4d.sample.snippet.";

    private static final String DEFAULT_FUNC_ENABLED = "false";

    //@formatter:off
    public static final String DEFAULT_FUNC = ""
                                    + "function convert(name, nameToCls){ \n"
                                    + "  return [name];\n"
                                    + "}\n";
    //@formatter:on
    private IEclipsePreferences pref;

    private Asta4dPreference(IEclipsePreferences pref) {
        this.pref = pref;
    }

    public static Asta4dPreference get(IProject prj) {
        IScopeContext context = new ProjectScope(prj);
        IEclipsePreferences node = context.getNode(Activator.PLUGIN_ID);
        return new Asta4dPreference(node);
    }

    public Asta4dProperties loadProperties() {
        Asta4dProperties properties = new Asta4dProperties();
        properties.setNamespace(getStoredNamespace());
        properties.setSnippetPrefixesEnabled(getStoredPrefixEnabled());
        properties.setSnippetPrefixes(getStoredPrefixes());
        properties.setSnippetConvertFuncEnabled(getStoredFuncEnabled());
        properties.setSnippetConvertFunc(getStoredFunc());

        return properties;
    }

    public void storeProperties(Asta4dProperties properties) {
        storeNamespace(properties.getNamespace());
        storePrefixEnabled(properties.isSnippetPrefixesEnabled());
        storePrefixes(properties.getSnippetPrefixes());
        storeFuncEnabled(properties.isSnippetConvertFuncEnabled());
        storeFunc(properties.getSnippetConvertFunc());
        try {
            this.pref.flush();
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
    }

    private String getStoredNamespace() {
        return this.pref.get("template.namespace", DEFAULT_NAMESPACE);
    }

    private void storeNamespace(String namespace) {
        this.pref.put("template.namespace", namespace);
    }

    private boolean getStoredPrefixEnabled() {
        return Boolean.parseBoolean(this.pref.get("snippet.prefixes.enabled", DEFAULT_PREFIX_ENABLED));
    }

    private void storePrefixEnabled(boolean enabled) {
        this.pref.put("snippet.prefixes.enabled", String.valueOf(enabled));
    }

    private String[] getStoredPrefixes() {
        String prefixes = this.pref.get("snippet.prefixes", DEFAULT_PREFIX);
        return prefixes.split(",");
    }

    private void storePrefixes(String[] prefixes) {
        String str = StringUtils.join(prefixes, ",");
        this.pref.put("snippet.prefixes", str);
    }

    private boolean getStoredFuncEnabled() {
        return Boolean.parseBoolean(this.pref.get("snippet.func.enabled", DEFAULT_FUNC_ENABLED));
    }

    private void storeFuncEnabled(boolean enabled) {
        this.pref.put("snippet.func.enabled", String.valueOf(enabled));
    }

    private String getStoredFunc() {
        return this.pref.get("snippet.func", DEFAULT_FUNC);
    }

    private void storeFunc(String func) {
        this.pref.put("snippet.func", func);
    }

}
