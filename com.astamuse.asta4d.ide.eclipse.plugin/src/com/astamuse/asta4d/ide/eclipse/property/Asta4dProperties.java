package com.astamuse.asta4d.ide.eclipse.property;

import java.util.Arrays;

public class Asta4dProperties implements Cloneable {

    private String namespace;

    private String[] snippetPrefixes;

    public String[] getSnippetPrefixes() {
        return snippetPrefixes;
    }

    public void setSnippetPrefixes(String[] snippetPrefixes) {
        this.snippetPrefixes = snippetPrefixes;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
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
        result = prime * result + Arrays.hashCode(snippetPrefixes);
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
        if (!Arrays.equals(snippetPrefixes, other.snippetPrefixes))
            return false;
        return true;
    }

}
