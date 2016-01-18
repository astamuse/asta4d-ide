package com.astamuse.asta4d.ide.eclipse.search.backup;

import org.eclipse.jdt.core.search.SearchDocument;
import org.eclipse.jdt.core.search.SearchParticipant;

public class TemplateSearchDocument extends SearchDocument {

    protected TemplateSearchDocument(String documentPath, SearchParticipant participant) {
        super(documentPath, participant);
    }

    @Override
    public byte[] getByteContents() {
        return null;
    }

    @Override
    public char[] getCharContents() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getEncoding() {
        // TODO Auto-generated method stub
        return null;
    }

}
