package com.astamuse.asta4d.ide.eclipse.hyperlink;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.astamuse.asta4d.ide.eclipse.property.Asta4dPreference;
import com.astamuse.asta4d.ide.eclipse.property.Asta4dProperties;
import com.astamuse.asta4d.ide.eclipse.util.Introspector;
import com.astamuse.asta4d.ide.eclipse.util.JdtUtils;
import com.astamuse.asta4d.ide.eclipse.util.ResourceUtil;

public class Asta4dHyperLinkDetector extends AbstractHyperlinkDetector {

    @Override
    public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
        IDocument doc = textViewer.getDocument();

        IFile file = ResourceUtil.getFile(doc);
        IProject prj = file.getProject();

        // we only available on java project
        /*
        if (prj instanceof IJavaProject) {
            // it is OK
        } else {
            return null;
        }
        */

        Node currentNode = ResourceUtil.getNodeByOffset(doc, region.getOffset());
        if (currentNode == null) {
            return null;
        }

        if (currentNode.getNodeType() != Node.ELEMENT_NODE) {
            return null;
        }

        // at first try to handle selected attribute value
        Attr currentAttr = getAttrByOffset(currentNode, region.getOffset());
        IDOMAttr attr = (IDOMAttr) currentAttr;
        if (currentAttr != null && region.getOffset() >= attr.getValueRegionStartOffset()) {
            if (isLinkableAttr(currentNode, currentAttr)) {
                IRegion hyperlinkRegion = getHyperlinkRegion(currentAttr);
                IHyperlink hyperLink = createHyperlink(prj, currentAttr.getName(), currentAttr.getNodeValue(), currentNode,
                        currentNode.getParentNode(), doc, textViewer, hyperlinkRegion, region);
                if (hyperLink != null) {
                    return new IHyperlink[] { hyperLink };
                }
            }
        }
        return null;
    }

    /**
     * Returns the attribute from given node at specified offset.
     */
    public final Attr getAttrByOffset(Node node, int offset) {
        if ((node instanceof IndexedRegion) && ((IndexedRegion) node).contains(offset) && (node.hasAttributes())) {
            NamedNodeMap attrs = node.getAttributes();
            // go through each attribute in node and if attribute contains
            // offset, return that attribute
            for (int i = 0; i < attrs.getLength(); ++i) {
                // assumption that if parent node is of type IndexedRegion,
                // then its attributes will also be of type IndexedRegion
                IndexedRegion attRegion = (IndexedRegion) attrs.item(i);
                if (attRegion.contains(offset)) {
                    return (Attr) attrs.item(i);
                }
            }
        }
        return null;
    }

    public boolean isLinkableAttr(Node node, Attr attr) {
        if (node.getNodeName().startsWith("afd:")) {
            return attr.getName().equalsIgnoreCase("render");
        } else {
            return attr.getName().equalsIgnoreCase("afd:render");
        }
    }

    public IRegion getHyperlinkRegion(Node node) {
        if (node != null) {
            switch (node.getNodeType()) {
            case Node.DOCUMENT_TYPE_NODE:
            case Node.TEXT_NODE:
                IDOMNode docNode = (IDOMNode) node;
                return new Region(docNode.getStartOffset(), docNode.getEndOffset() - docNode.getStartOffset());

            case Node.ELEMENT_NODE:
                IDOMElement element = (IDOMElement) node;
                int endOffset;
                if (element.hasEndTag() && element.isClosed()) {
                    endOffset = element.getStartEndOffset();
                } else {
                    endOffset = element.getEndOffset();
                }
                return new Region(element.getStartOffset(), endOffset - element.getStartOffset());

            case Node.ATTRIBUTE_NODE:
                IDOMAttr att = (IDOMAttr) node;
                // do not include quotes in attribute value region
                int regOffset = att.getValueRegionStartOffset();
                int regLength = att.getValueRegionText().length();
                String attValue = att.getValueRegionText();
                if (StringUtils.isQuoted(attValue)) {
                    regOffset += 1;
                    regLength = regLength - 2;
                }
                return new Region(regOffset, regLength);
            }
        }
        return null;
    }

    public IHyperlink createHyperlink(IProject prj, String name, String target, Node node, Node parentNode, IDocument document,
            ITextViewer textViewer, IRegion hyperlinkRegion, IRegion cursor) {
        String[] declareInfo = target.split("::|:");
        String snippetClass, snippetMethod;
        if (declareInfo.length < 2) {
            snippetClass = declareInfo[0];
            snippetMethod = "render";
        } else {
            snippetClass = declareInfo[0];
            snippetMethod = declareInfo[1];
        }
        Asta4dProperties properties = Asta4dPreference.get(prj).loadProperties();
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
