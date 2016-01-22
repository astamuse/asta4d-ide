package com.astamuse.asta4d.ide.eclipse.hyperlink;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.w3c.dom.Node;

import com.astamuse.asta4d.ide.eclipse.internal.SnippetMethodExtractor;
import com.astamuse.asta4d.ide.eclipse.internal.SnippetMethodNameConvertorFactory;
import com.astamuse.asta4d.ide.eclipse.property.Asta4dProperties;
import com.astamuse.asta4d.ide.eclipse.util.Introspector;
import com.astamuse.asta4d.ide.eclipse.util.JdtUtils;
import com.astamuse.asta4d.ide.eclipse.util.SnippetMethodNameConvertorFactoryImpl;

public class Asta4dHyperLinkDetector extends AbstractHyperlinkDetector implements SnippetMethodExtractor {

    private IStatusLineManager statusLineManager = null;

    @Override
    public SnippetMethodNameConvertorFactory getSnippetMethodnameConvertorFactory() {
        return SnippetMethodNameConvertorFactoryImpl.getInstance();
    }

    @Override
    public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {

        if (statusLineManager == null) {
            IWorkbench wb = PlatformUI.getWorkbench();
            IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
            IWorkbenchPage page = win.getActivePage();
            IActionBars actionBars = page.getActiveEditor().getEditorSite().getActionBars();
            statusLineManager = actionBars.getStatusLineManager();
        }

        MethodInfo methodInfo = detectSnippetMethod(textViewer, region);
        if (methodInfo == null) {
            return null;
        }

        if (!methodInfo.isSnippetMethodExists()) {
            statusLineManager.setErrorMessage("Snippet method does not exist");
            UIJob job = new UIJob("") {
                @Override
                public IStatus runInUIThread(IProgressMonitor monitor) {
                    statusLineManager.setErrorMessage(null);
                    return Status.OK_STATUS;
                }
            };
            job.setPriority(UIJob.DECORATE);// lowest priority
            job.schedule(10 * 1000);// wait 10s
        }

        // we always return a valid link to show the link lookandfeel
        IRegion hyperlinkRegion = getHyperlinkRegion(methodInfo.currentAttr);
        IHyperlink hyperLink = new JavaElementHyperlink(hyperlinkRegion, methodInfo.method);
        return new IHyperlink[] { hyperLink };

    }

    public IHyperlink createHyperlink(IProject prj, String name, String target, Node node, Node parentNode, IDocument document,
            ITextViewer textViewer, IRegion hyperlinkRegion, IRegion cursor, Asta4dProperties properties) {
        String[] declareInfo = target.split("::|:");
        String snippetClass, snippetMethod;
        if (declareInfo.length < 2) {
            snippetClass = declareInfo[0];
            snippetMethod = "render";
        } else {
            snippetClass = declareInfo[0];
            snippetMethod = declareInfo[1];
        }

        for (String prefix : properties.getSnippetPrefixes()) {
            String searchName = prefix + snippetClass;
            IType type = JdtUtils.getJavaType(prj, searchName);
            if (type == null) {
                continue;
            }
            try {
                IMethod method = Introspector.findMethod(type, snippetMethod);
                if (method != null) {
                    return new JavaElementHyperlink(hyperlinkRegion, method);
                }
            } catch (JavaModelException e) {
            }
        }
        return null;
    }

}
