package com.astamuse.asta4d.ide.eclipse.search.backup;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jface.text.IDocument;
import org.eclipse.search.internal.ui.text.FileSearchResult;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.text.Match;
import org.eclipse.wst.html.core.internal.encoding.HTMLDocumentLoader;
import org.eclipse.wst.sse.core.internal.provisional.document.IEncodedDocument;
import org.w3c.dom.Node;

import com.astamuse.asta4d.ide.eclipse.Activator;
import com.astamuse.asta4d.ide.eclipse.util.ResourceUtil;

public class SnippetMethodSearch implements ISearchQuery {

    private FileSearchResult fileResult;

    private SnippetMethodSearchResult searchResult;

    private IMethod method;

    private IContainer searchScope;

    public SnippetMethodSearch(IMethod method, IContainer searchScope) {
        this.method = method;
        this.searchScope = searchScope;
    }

    @Override
    public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {
        SnippetMethodSearchResult result = getSearchResult();
        result.removeAll();
        monitor.beginTask("searching snippet method reference...", 1);
        try {
            // result.addMatch(new Match(element, offset, length));
            performSearch(searchScope);
        } catch (Exception ex) {
            return new Status(Status.ERROR, Activator.PLUGIN_ID, 1, "", ex);
        } finally {
            monitor.done();
        }
        return Status.OK_STATUS;

    }

    private void performSearch(IResource resource) throws CoreException, IOException {
        // recursive search in sub
        if (resource instanceof IContainer) {
            for (IResource res : ((IContainer) resource).members()) {
                performSearch(res);
            }
            return;
        }

        IFile file = null;
        if (resource instanceof IFile) {
            file = (IFile) resource;
        } else {
            return;
        }

        String ext = file.getFileExtension();
        if (ext.equalsIgnoreCase("htm") || ext.equals("html")) {
            // OK
        } else {
            return;
        }

        HTMLDocumentLoader loader = new HTMLDocumentLoader();
        IEncodedDocument doc = loader.createNewStructuredDocument();
        Charset charset = Charset.forName(file.getCharset(true));
        loader.reload(doc, new InputStreamReader(file.getContents(), charset));

        String text = doc.get();

        int offset = text.indexOf("render");

        if (offset >= 0) {
            Match match = new Match(file, 0, 6);
            searchResult.addMatch(match);
        }

        // searchInDocument(doc);

    }

    private void searchInDocument(IDocument doc) {
        Node root = ResourceUtil.getNodeByOffset(doc, 2);
        searchInNode(root);
    }

    private void searchInNode(Node node) {
        if (node == null) {
            return;
        }
        System.out.println(node.getNodeName());
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
