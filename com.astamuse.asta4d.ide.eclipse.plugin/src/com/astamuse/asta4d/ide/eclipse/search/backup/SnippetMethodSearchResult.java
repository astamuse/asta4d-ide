package com.astamuse.asta4d.ide.eclipse.search.backup;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.IEditorMatchAdapter;
import org.eclipse.search.ui.text.IFileMatchAdapter;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

public class SnippetMethodSearchResult extends AbstractTextSearchResult implements IFileMatchAdapter, IEditorMatchAdapter {

    private final ISearchQuery query;

    public SnippetMethodSearchResult(ISearchQuery query) {
        this.query = query;
    }

    @Override
    public String getLabel() {
        return MessageFormat.format("{0} {1} found", query.getLabel(), getMatchCount());
    }

    @Override
    public String getTooltip() {
        return getLabel();
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ISearchQuery getQuery() {
        return query;
    }

    @Override
    public boolean isShownInEditor(Match match, IEditorPart editor) {
        IFile editorFile = getFile(editor);
        if (editorFile == null) {
            return false;
        }
        IFile file = getFile(match.getElement());
        return editorFile.equals(file);
    }

    @Override
    public IEditorMatchAdapter getEditorMatchAdapter() {
        return this;
    }

    @Override
    public IFileMatchAdapter getFileMatchAdapter() {
        return this;
    }

    @Override
    public Match[] computeContainedMatches(AbstractTextSearchResult result, IEditorPart editor) {
        IFile file = getFile(editor);
        return getMatches(file);
    }

    @Override
    public Match[] computeContainedMatches(AbstractTextSearchResult result, IFile file) {
        return getMatches(file);
    }

    @Override
    public IFile getFile(Object element) {
        if (element instanceof IJavaElement) {
            IJavaElement javaElement = (IJavaElement) element;
            return (IFile) javaElement.getResource();
        }
        if (element instanceof IEditorPart) {
            IEditorPart editor = (IEditorPart) element;
            IEditorInput input = editor.getEditorInput();
            if (input instanceof IFileEditorInput) {
                return ((IFileEditorInput) input).getFile();
            }
        }
        if (element instanceof IFile) {
            return (IFile) element;
        }
        return null;
    }

    protected Match[] getMatches(IFile file) {
        /*
        IJavaElement element = JavaCore.create(file);
        Set<Match> set = new HashSet<Match>();
        collectMatches(set, element);
        return set.toArray(new Match[set.size()]);
        */
        return new Match[0];
    }

}
