package com.astamuse.asta4d.ide.eclipse.hover;

import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;

public class Asta4dPriorityMatchHover implements ITextHover, ITextHoverExtension, ITextHoverExtension2 {

    private ITextHover originHover;

    private ITextHover matchedHover;

    private Asta4dHyperLinkHover asta4dHover;

    public Asta4dPriorityMatchHover(ITextHover originHover) {
        this.originHover = originHover;
        this.asta4dHover = new Asta4dHyperLinkHover();
        this.matchedHover = null;
    }

    @Override
    public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
        IRegion region = asta4dHover.getHoverRegion(textViewer, offset);
        if (region == null) {
            matchedHover = originHover;
            return originHover.getHoverRegion(textViewer, offset);
        } else {
            matchedHover = asta4dHover;
            return region;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
        return matchedHover.getHoverInfo(textViewer, hoverRegion);
    }

    @Override
    public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
        if (matchedHover instanceof ITextHoverExtension2) {
            return ((ITextHoverExtension2) matchedHover).getHoverInfo2(textViewer, hoverRegion);
        } else {
            return null;
        }
    }

    @Override
    public IInformationControlCreator getHoverControlCreator() {
        if (matchedHover instanceof ITextHoverExtension) {
            return ((ITextHoverExtension) matchedHover).getHoverControlCreator();
        } else {
            return null;
        }
    }

}
