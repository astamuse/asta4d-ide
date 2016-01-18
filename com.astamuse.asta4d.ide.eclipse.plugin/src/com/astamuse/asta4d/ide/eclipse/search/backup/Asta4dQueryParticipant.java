package com.astamuse.asta4d.ide.eclipse.search.backup;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.ui.search.ElementQuerySpecification;
import org.eclipse.jdt.ui.search.IMatchPresentation;
import org.eclipse.jdt.ui.search.IQueryParticipant;
import org.eclipse.jdt.ui.search.ISearchRequestor;
import org.eclipse.jdt.ui.search.QuerySpecification;

public class Asta4dQueryParticipant implements IQueryParticipant {

    @Override
    public void search(ISearchRequestor requestor, QuerySpecification querySpecification, IProgressMonitor monitor) throws CoreException {
        if (querySpecification instanceof ElementQuerySpecification) {
            // element search (eg. from global find references in Java file)
            ElementQuerySpecification elementQuery = (ElementQuerySpecification) querySpecification;
            IJavaElement element = elementQuery.getElement();
            if (element.getElementType() != IJavaElement.METHOD) {
                return;// we only search on method
            }

            /*
            IMethod method = (IMethod) element;
            
            IType declaringClass = method.getDeclaringType();
            System.out.println(declaringClass.getFullyQualifiedName());
            */

            SearchPattern pattern = null;

        }
    }

    @Override
    public int estimateTicks(QuerySpecification specification) {
        return 0;
    }

    @Override
    public IMatchPresentation getUIParticipant() {
        // TODO Auto-generated method stub
        return null;
    }

}
