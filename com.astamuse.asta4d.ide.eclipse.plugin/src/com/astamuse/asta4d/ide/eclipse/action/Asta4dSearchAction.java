package com.astamuse.asta4d.ide.eclipse.action;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.actions.SelectionConverter;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import com.astamuse.asta4d.ide.eclipse.search.SnippetMethodSearch;

public class Asta4dSearchAction implements IEditorActionDelegate {

    private Shell shell;

    private JavaEditor editor;

    @Override
    public void run(IAction action) {
        System.out.println("I am here");
        if (editor == null) {
            return;// do nothing
        }

        ISelection selection = editor.getSite().getSelectionProvider().getSelection();

        if (!(selection instanceof ITextSelection)) {
            return;
        }

        try {
            IJavaElement[] elements = SelectionConverter.codeResolve(editor, true);
            if (elements.length > 0) {
                IJavaElement element = elements[0];
                ISearchQuery query = new SnippetMethodSearch();
                NewSearchUI.activateSearchResultView();
                NewSearchUI.runQueryInBackground(query);
            }
        } catch (JavaModelException e) {
            e.printStackTrace(System.err);
        }

        System.out.println("I got selection");
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {

    }

    @Override
    public void setActiveEditor(IAction action, IEditorPart targetEditor) {
        this.shell = targetEditor.getSite().getShell();
        if (targetEditor instanceof JavaEditor) {
            this.editor = (JavaEditor) targetEditor;
        } else {
            this.editor = null;
        }
    }

}
