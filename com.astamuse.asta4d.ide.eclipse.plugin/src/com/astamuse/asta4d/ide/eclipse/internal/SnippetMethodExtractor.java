package com.astamuse.asta4d.ide.eclipse.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
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

public interface SnippetMethodExtractor {

    public static class MethodInfo {
        public IMethod method;
        public Attr currentAttr;

        public MethodInfo(IMethod method, Attr currentAttr) {
            super();
            this.method = method;
            this.currentAttr = currentAttr;
        }

    }

    public SnippetMethodNameConvertorFactory getSnippetMethodnameConvertorFactory();

    default MethodInfo detectSnippetMethod(ITextViewer textViewer, IRegion region) {
        IDocument doc = textViewer.getDocument();

        IFile file = ResourceUtil.getFile(doc);
        IProject prj = file.getProject();

        Node currentNode = ResourceUtil.getNodeByOffset(doc, region.getOffset());
        if (currentNode == null) {
            return null;
        }

        if (currentNode.getNodeType() != Node.ELEMENT_NODE) {
            return null;
        }

        Asta4dProperties properties = Asta4dPreference.get(prj).loadProperties();

        // at first try to handle selected attribute value
        Attr currentAttr = getAttrByOffset(currentNode, region.getOffset());
        IDOMAttr attr = (IDOMAttr) currentAttr;
        if (currentAttr != null && region.getOffset() >= attr.getValueRegionStartOffset()) {
            if (isLinkableAttr(currentNode, currentAttr, properties)) {
                IMethod method = extractMethod(prj, currentAttr.getNodeValue(), properties);
                return new MethodInfo(method, currentAttr);
            }
        }
        return null;
    }

    default boolean isLinkableAttr(Node node, Attr attr, Asta4dProperties properties) {
        String ns = properties.getNamespace().toLowerCase();
        if (node.getNodeName().toLowerCase().startsWith(ns)) {
            return attr.getName().equalsIgnoreCase("render");
        } else {
            return attr.getName().equalsIgnoreCase(ns + ":render");
        }
    }

    /**
     * Returns the attribute from given node at specified offset.
     */
    default Attr getAttrByOffset(Node node, int offset) {
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

    default IRegion getHyperlinkRegion(Node node) {
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

    default IMethod extractMethod(IProject prj, String target, Asta4dProperties properties) {
        String[] declareInfo = target.split("::|:");
        String snippetClass, snippetMethod;
        if (declareInfo.length < 2) {
            snippetClass = declareInfo[0];
            snippetMethod = "render";
        } else {
            snippetClass = declareInfo[0];
            snippetMethod = declareInfo[1];
        }

        String[] searchClasses = getSnippetMethodnameConvertorFactory().getConvertor(properties).convert(snippetClass, true);

        for (String cls : searchClasses) {
            IType type = JdtUtils.getJavaType(prj, cls);
            if (type == null) {
                continue;
            }
            try {
                IMethod method = Introspector.findMethod(type, snippetMethod);
                if (method != null) {
                    return method;
                }
            } catch (JavaModelException e) {
            }
        }
        return null;
    }
}
