package com.astamuse.asta4d.ide.eclipse.hover;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;

import com.astamuse.asta4d.ide.eclipse.internal.SnippetMethodExtractor;

public class Asta4dHyperLinkHover implements SnippetMethodExtractor, ITextHover {

    @Override
    public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
        MethodInfo methodInfo = detectSnippetMethod(textViewer, hoverRegion);
        if (methodInfo == null) {
            return null;
        }
        return methodInfo.method.getElementName();
    }

    @Override
    public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
        IRegion region = new Region(offset, 1);
        MethodInfo methodInfo = detectSnippetMethod(textViewer, region);
        if (methodInfo == null) {
            return null;
        }
        IRegion hyperlinkRegion = getHyperlinkRegion(methodInfo.currentAttr);
        return hyperlinkRegion;
    }

}
