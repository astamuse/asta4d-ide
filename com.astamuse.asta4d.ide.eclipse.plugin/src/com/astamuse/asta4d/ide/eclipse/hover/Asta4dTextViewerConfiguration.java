package com.astamuse.asta4d.ide.eclipse.hover;

import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.jsdt.web.ui.StructuredTextViewerConfigurationJSDT;

import com.astamuse.asta4d.ide.eclipse.internal.SnippetMethodExtractor;

public class Asta4dTextViewerConfiguration extends StructuredTextViewerConfigurationJSDT implements SnippetMethodExtractor {

    @Override
    public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType, int stateMask) {
        ITextHover hover = super.getTextHover(sourceViewer, contentType, stateMask);
        return new Asta4dPriorityMatchHover(hover);
    }

}
