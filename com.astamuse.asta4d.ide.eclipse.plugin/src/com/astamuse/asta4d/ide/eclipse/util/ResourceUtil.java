package com.astamuse.asta4d.ide.eclipse.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.w3c.dom.Node;

public class ResourceUtil {
    public static final IFile getFile(IDocument document) {
        IFile resource = null;
        String baselocation = null;
        if (document != null) {
            IStructuredModel model = null;
            try {
                model = org.eclipse.wst.sse.core.StructuredModelManager.getModelManager().getExistingModelForRead(document);
                if (model != null) {
                    baselocation = model.getBaseLocation();
                }
            } finally {
                if (model != null) {
                    model.releaseFromRead();
                }
            }
        }
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        if (baselocation != null) {
            IPath path = new Path(baselocation);
            if (root.exists(path) && path.segmentCount() > 1) {
                resource = root.getFile(path);
            }
        }
        return resource;
    }

    /**
     * Returns the node from given document at specified offset.
     * 
     * @param offset
     *            the offset with given document
     * @return Node either element, doctype, text, or null
     */
    public static final Node getNodeByOffset(IDocument document, int offset) {
        // get the node at offset (returns either: element, doctype, text)
        IndexedRegion inode = null;
        IStructuredModel sModel = null;
        try {
            sModel = org.eclipse.wst.sse.core.StructuredModelManager.getModelManager().getExistingModelForRead(document);
            if (sModel == null && document instanceof IStructuredDocument) {
                sModel = org.eclipse.wst.sse.core.StructuredModelManager.getModelManager().getModelForRead((IStructuredDocument) document);
            }
            inode = sModel.getIndexedRegion(offset);
            if (inode == null) {
                inode = sModel.getIndexedRegion(offset - 1);
            }
        } finally {
            if (sModel != null) {
                sModel.releaseFromRead();
            }
        }

        if (inode instanceof Node) {
            return (Node) inode;
        }
        return null;
    }
}
