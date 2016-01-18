package com.astamuse.asta4d.ide.eclipse.search;

import org.eclipse.jdt.internal.ui.refactoring.nls.search.FileEntry;
import org.eclipse.jdt.internal.ui.search.TextSearchLabelProvider;
import org.eclipse.jdt.internal.ui.search.TextSearchTableContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;

public class SnippetMethodSearchResultPage extends AbstractTextSearchViewPage {

    public SnippetMethodSearchResultPage() {
        super(AbstractTextSearchViewPage.FLAG_LAYOUT_FLAT);
    }

    @Override
    protected void elementsChanged(Object[] objects) {

    }

    @Override
    protected void clear() {

    }

    @Override
    protected void configureTreeViewer(TreeViewer viewer) {

    }

    /*
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#configureTableViewer(org.eclipse.jface.viewers.TableViewer)
     */
    @Override
    protected void configureTableViewer(TableViewer viewer) {
        viewer.setComparator(new ViewerComparator() {
            @Override
            public int category(Object element) {
                if (element instanceof FileEntry) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
        TextSearchLabelProvider tp = new TextSearchLabelProvider(this) {
        };
        // viewer.setLabelProvider(new ColoringLabelProvider
        TextSearchTableContentProvider fContentProvider = new TextSearchTableContentProvider();
        viewer.setContentProvider(fContentProvider);
    }

}
