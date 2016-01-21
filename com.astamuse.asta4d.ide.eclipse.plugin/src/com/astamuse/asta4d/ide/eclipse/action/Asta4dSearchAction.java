package com.astamuse.asta4d.ide.eclipse.action;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.ui.actions.SelectionConverter;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.search.internal.ui.text.FileSearchQuery;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import com.astamuse.asta4d.ide.eclipse.internal.SnippetMethodNameConvertorFactory;
import com.astamuse.asta4d.ide.eclipse.property.Asta4dPreference;
import com.astamuse.asta4d.ide.eclipse.property.Asta4dProperties;
import com.astamuse.asta4d.ide.eclipse.util.SnippetMethodNameConvertorFactoryImpl;

public class Asta4dSearchAction implements IEditorActionDelegate {

    private static class SearchPattern {
        String regex;
        String labelName;
    }

    private Shell shell;

    private JavaEditor editor;

    private SnippetMethodNameConvertorFactory snippetMethodNameConvertorFactory = new SnippetMethodNameConvertorFactoryImpl();

    @Override
    public void run(IAction action) {
        // System.out.println("I am here");
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
                if (element.getElementType() != IJavaElement.METHOD) {
                    MessageDialog.openError(shell, "Wrong search", "Can only search for method.");
                    return;
                }
                IMethod method = (IMethod) element;
                if (!isRenderMethod(method)) {
                    return;
                }

                IEditorInput input = editor.getEditorInput();
                if (input instanceof IFileEditorInput) {
                    // OK
                } else {
                    return;
                }

                IFileEditorInput fileInput = (IFileEditorInput) input;
                IFile file = fileInput.getFile();
                IProject project = file.getProject();

                IResource[] roots = new IResource[] { project };
                String[] fileNamePattern = new String[] { "*.html", "*.htm" };
                FileTextSearchScope scope = FileTextSearchScope.newSearchScope(roots, fileNamePattern, false);

                SearchPattern searchPattern = createSearchPattern(project, method);
                FileSearchQuery fsq = new FileSearchQuery(searchPattern.regex, true, false, scope) {
                    @Override
                    public String getLabel() {
                        return String.format("Searching snippet method [%s] references in [%s]", searchPattern.labelName,
                                project.getName());
                    }

                    @Override
                    public String getResultLabel(int nMatches) {
                        return String.format("Search snippet method [%s] references - %d matches in [%s]", searchPattern.labelName,
                                nMatches, project.getName());
                    }

                };

                NewSearchUI.runQueryInBackground(fsq);
            }
        } catch (JavaModelException e) {
            e.printStackTrace(System.err);
        }

        // System.out.println("I got selection");
    }

    private SearchPattern createSearchPattern(IProject project, IMethod method) throws JavaModelException {

        Asta4dPreference pref = Asta4dPreference.get(project);
        Asta4dProperties props = pref.loadProperties();

        String holdingCls = method.getDeclaringType().getFullyQualifiedName();
        String methodName = method.getElementName();

        String[] searchNames = snippetMethodNameConvertorFactory.getConvertor(props).convert(holdingCls, false);

        List<String> patterns = new LinkedList<>();
        for (String searchName : searchNames) {
            searchName = StringUtils.replace(searchName, ".", "\\.");
            patterns.add(searchName + ":{1,2}" + methodName);
            if (methodName.equals("render")) {
                patterns.add(searchName);
            }
        }

        String searchDeclaring = StringUtils.join(patterns, ")|(");
        if (patterns.size() > 1) {
            searchDeclaring = "(" + searchDeclaring + ")";
        }

        String pattern = "render=[\"'](" + searchDeclaring + ")[\"']";

        System.out.println(pattern);

        SearchPattern sp = new SearchPattern();
        sp.regex = pattern;
        sp.labelName = holdingCls + "#" + methodName;

        return sp;
    }

    @SuppressWarnings("unused")
    private boolean isRenderMethod(IMethod method) {
        // copied from jdt SearchPattern
        // but it does not work correctly, so we alway return true
        if (true) {
            return true;
        }
        try {
            String returnSignature = null;

            returnSignature = method.getReturnType();
            char[] signature = returnSignature.toCharArray();
            char[] returnErasure = Signature.toCharArray(Signature.getTypeErasure(signature));
            CharOperation.replace(returnErasure, '$', '.');
            String returnQualification = new String(returnErasure);
            return returnQualification.equals("com.astamuse.asta4d.render.Renderer");
        } catch (JavaModelException e) {
            e.printStackTrace(System.err);
            return false;
        }
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {

    }

    @Override
    public void setActiveEditor(IAction action, IEditorPart targetEditor) {
        if (targetEditor == null) {
            return;
        }
        this.shell = targetEditor.getSite().getShell();
        if (targetEditor instanceof JavaEditor) {
            this.editor = (JavaEditor) targetEditor;
        } else {
            this.editor = null;
        }
    }

}
