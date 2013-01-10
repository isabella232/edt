/*******************************************************************************
 * Copyright © 2004, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.search;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;

public class EGLSearchResultTableContentProvider extends EGLSearchResultContentProvider implements IStructuredContentProvider
{
    private TableViewer fTableViewer;   
    
    public EGLSearchResultTableContentProvider(TableViewer viewer)
    {
        fTableViewer = viewer;
    }
    
    /* (non-Javadoc)
     * @see com.ibm.etools.egl.internal.ui.search.EGLSearchResultContentProvider#elementsChanged(java.lang.Object[])
     */
    public void elementsChanged(Object[] updatedElements)
    {
    	int addCount= 0;
    	int removeCount= 0;
    	for (int i= 0; i < updatedElements.length; i++) {
    		if (fResult.getMatchCount(updatedElements[i]) > 0) {
    			if (fTableViewer.testFindItem(updatedElements[i]) != null)
    				fTableViewer.refresh(updatedElements[i]);
    			else
    				fTableViewer.add(updatedElements[i]);
    			addCount++;
    		} else {
    			fTableViewer.remove(updatedElements[i]);
    			removeCount++;
    		}
    	}
    }
    /* (non-Javadoc)
     * @see com.ibm.etools.egl.internal.ui.search.EGLSearchResultContentProvider#clear()
     */
    public void clear()
    {
        fTableViewer.refresh();
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement)
    {
    	if (inputElement instanceof EGLSearchResult)
    		return ((EGLSearchResult)inputElement).getElements();
    	return EMPTY_ARR;
    }
}

