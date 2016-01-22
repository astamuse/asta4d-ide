package com.astamuse.asta4d.ide.eclipse.hover;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.text.java.hover.JavaSourceHover;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;

import com.astamuse.asta4d.ide.eclipse.internal.SnippetMethodExtractor;
import com.astamuse.asta4d.ide.eclipse.internal.SnippetMethodNameConvertorFactory;
import com.astamuse.asta4d.ide.eclipse.util.SnippetMethodNameConvertorFactoryImpl;

public class Asta4dHyperLinkHover extends JavaSourceHover implements SnippetMethodExtractor {

    private static class SnippetMethodRegion extends Region {

        MethodInfo methodInfo;

        public SnippetMethodRegion(int offset, int length) {
            super(offset, length);
        }

    }

    @Override
    public SnippetMethodNameConvertorFactory getSnippetMethodnameConvertorFactory() {
        return SnippetMethodNameConvertorFactoryImpl.getInstance();
    }

    @Override
    protected IJavaElement[] getJavaElementsAt(ITextViewer textViewer, IRegion hoverRegion) {
        MethodInfo methodInfo;
        if (hoverRegion instanceof SnippetMethodRegion) {
            methodInfo = ((SnippetMethodRegion) hoverRegion).methodInfo;
        } else {
            methodInfo = detectSnippetMethod(textViewer, hoverRegion);
        }
        // impossible, right?
        if (methodInfo == null) {
            return null;
        }
        return new IJavaElement[] { methodInfo.method };
    }

    @Override
    public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
        SnippetMethodRegion region = new SnippetMethodRegion(offset, 1);
        MethodInfo methodInfo = detectSnippetMethod(textViewer, region);
        if (methodInfo == null) {
            return null;
        } else {
            region.methodInfo = methodInfo;
            return region;
        }
    }

}
