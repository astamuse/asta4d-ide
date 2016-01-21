package com.astamuse.asta4d.ide.eclipse.property;

import java.util.Arrays;

public class Asta4dProperties implements Cloneable {

    private String namespace;

    private boolean snippetPrefixesEnabled;

    private String[] snippetPrefixes;

    private boolean snippetConvertFuncEnabled;

    private String snippetConvertFunc;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public boolean isSnippetPrefixesEnabled() {
        return snippetPrefixesEnabled;
    }

    public void setSnippetPrefixesEnabled(boolean snippetPrefixesEnabled) {
        this.snippetPrefixesEnabled = snippetPrefixesEnabled;
    }

    public String[] getSnippetPrefixes() {
        return snippetPrefixes;
    }

    public void setSnippetPrefixes(String[] snippetPrefixes) {
        this.snippetPrefixes = snippetPrefixes;
    }

    public boolean isSnippetConvertFuncEnabled() {
        return snippetConvertFuncEnabled;
    }

    public void setSnippetConvertFuncEnabled(boolean snippetConvertFuncEnabled) {
        this.snippetConvertFuncEnabled = snippetConvertFuncEnabled;
    }

    public String getSnippetConvertFunc() {
        return snippetConvertFunc;
    }

    public void setSnippetConvertFunc(String snippetConvertFunc) {
        this.snippetConvertFunc = snippetConvertFunc;
    }

    public Asta4dProperties clone() {
        try {
            return (Asta4dProperties) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
        result = prime * result + ((snippetConvertFunc == null) ? 0 : snippetConvertFunc.hashCode());
        result = prime * result + (snippetConvertFuncEnabled ? 1231 : 1237);
        result = prime * result + Arrays.hashCode(snippetPrefixes);
        result = prime * result + (snippetPrefixesEnabled ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Asta4dProperties other = (Asta4dProperties) obj;
        if (namespace == null) {
            if (other.namespace != null)
                return false;
        } else if (!namespace.equals(other.namespace))
            return false;
        if (snippetConvertFunc == null) {
            if (other.snippetConvertFunc != null)
                return false;
        } else if (!snippetConvertFunc.equals(other.snippetConvertFunc))
            return false;
        if (snippetConvertFuncEnabled != other.snippetConvertFuncEnabled)
            return false;
        if (!Arrays.equals(snippetPrefixes, other.snippetPrefixes))
            return false;
        if (snippetPrefixesEnabled != other.snippetPrefixesEnabled)
            return false;
        return true;
    }

}
