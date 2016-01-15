package com.astamuse.asta4d.ide.eclipse.property;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.osgi.service.prefs.BackingStoreException;

import com.astamuse.asta4d.ide.eclipse.Activator;

public class Asta4dPreference {

    private static final String DEFAULT_PREFIX = "com.astamuse.asta4d.sample.snippet.";

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
        properties.setSnippetPrefixes(getStoredPrefixes());
        return properties;
    }

    public void storeProperties(Asta4dProperties properties) {
        storePrefixes(properties.getSnippetPrefixes());
        try {
            this.pref.flush();
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
    }

    private String[] getStoredPrefixes() {
        String prefixes = this.pref.get("snippet.prefixes", DEFAULT_PREFIX);
        return prefixes.split(",");
    }

    private void storePrefixes(String[] prefixes) {
        String str = StringUtils.join(prefixes, ",");
        this.pref.put("snippet.prefixes", str);
    }
}
