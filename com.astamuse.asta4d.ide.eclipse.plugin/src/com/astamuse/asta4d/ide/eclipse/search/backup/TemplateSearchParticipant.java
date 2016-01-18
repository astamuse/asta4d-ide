package com.astamuse.asta4d.ide.eclipse.search.backup;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchDocument;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;

public class TemplateSearchParticipant extends SearchParticipant {

    @Override
    public SearchDocument getDocument(String documentPath) {
        return null;
    }

    public String getDescription() {
        return "Template Files";
    }

    @Override
    public void indexDocument(SearchDocument document, IPath indexLocation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void locateMatches(SearchDocument[] documents, SearchPattern pattern, IJavaSearchScope scope, SearchRequestor requestor,
            IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        SearchEngine.getDefaultSearchParticipant().locateMatches(documents, pattern, scope, requestor, monitor);
    }

    @Override
    public IPath[] selectIndexes(SearchPattern query, IJavaSearchScope scope) {
        // TODO Auto-generated method stub
        return null;
    }

}
