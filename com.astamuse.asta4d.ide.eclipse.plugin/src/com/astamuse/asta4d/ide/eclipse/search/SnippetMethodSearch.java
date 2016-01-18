package com.astamuse.asta4d.ide.eclipse.search;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.search.ui.ISearchQuery;

public class SnippetMethodSearch implements ISearchQuery {

    private SnippetMethodSearchResult searchResult;

    @Override
    public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {
        SnippetMethodSearchResult result = getSearchResult();
        result.removeAll();
        monitor.beginTask("??", 1);
        try {
            // result.addMatch(new Match(element, offset, length));
        } finally {
            monitor.done();
        }
        return Status.OK_STATUS;
    }

    @Override
    public String getLabel() {
        return "snippet method search";
    }

    @Override
    public boolean canRerun() {
        return true;
    }

    @Override
    public boolean canRunInBackground() {
        return true;
    }

    @Override
    public SnippetMethodSearchResult getSearchResult() {
        if (searchResult == null) {
            searchResult = new SnippetMethodSearchResult(this);
        }
        return searchResult;
    }

}
