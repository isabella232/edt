/*******************************************************************************
 * Copyright © 2004, 2012 IBM Corporation and others.
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.edt.ide.core.internal.model.EglarPackageFragment;
import org.eclipse.edt.ide.core.internal.model.EglarPackageFragmentRoot;
import org.eclipse.edt.ide.core.model.IClassFile;
import org.eclipse.edt.ide.core.model.IFunction;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;

public class EGLSearchResultTreeContentProvider
        extends
            EGLSearchResultContentProvider implements ITreeContentProvider
{
    private AbstractTreeViewer fTreeViewer;
    private Map fChildrenMap;
    
    EGLSearchResultTreeContentProvider(AbstractTreeViewer viewer) {
    	fTreeViewer= viewer;
    }
    
    
    /* (non-Javadoc)
     * @see com.ibm.etools.egl.internal.ui.search.EGLSearchResultContentProvider#elementsChanged(java.lang.Object[])
     */
    public synchronized void elementsChanged(Object[] updatedElements) {
    	for (int i= 0; i < updatedElements.length; i++) {
    		if (fResult.getMatchCount(updatedElements[i]) > 0)
    			insert(updatedElements[i], true);
    		else
    			remove(updatedElements[i], true);
    	}
    }
    
    /* (non-Javadoc)
     * @see com.ibm.etools.egl.internal.ui.search.EGLSearchResultContentProvider#clear()
     */
    public void clear() {
    	initialize(fResult);
    	fTreeViewer.refresh();
    }  
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren(Object parentElement) {
    	Set children= (Set) fChildrenMap.get(parentElement);
    	if (children == null)
    		return EMPTY_ARR;
    	return children.toArray();
    }    
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent(Object element) {
    	if (element instanceof IProject)
    		return null;
    	if (element instanceof IResource) {
    		IResource resource = (IResource) element;
    		return resource.getParent();
    	}
    	if(element instanceof IFunction) {
    		IFunction function = (IFunction)element;
			return function.getParent();
		}
    	if (element instanceof IPart) {
    		IPart part = (IPart) element;
			return part.getPackageFragment();
		}
    	if (element instanceof IClassFile) {
			return ((IClassFile) element).getParent();
		}
    	if (element instanceof EglarPackageFragment){
    		return ((EglarPackageFragment)element).getPackageFragmentRoot();
    	}
    	if (element instanceof EglarPackageFragmentRoot){
    		return ((EglarPackageFragmentRoot)element).getEGLProject().getProject();
    	}
    	
    	return null;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren(Object element) {
    	return getChildren(element).length > 0;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement) {
    	return getChildren(inputElement);
    }


    protected synchronized void initialize(EGLSearchResult result) {
    	super.initialize(result);
    	fChildrenMap= new HashMap();
    	if (result != null) {
    		Object[] elements= result.getElements();
    		for (int i= 0; i < elements.length; i++) {
    			insert(elements[i], false);
    		}
    	}
    }

    protected void insert(Object child, boolean refreshViewer) {
    	Object parent= getParent(child);
    	while (parent != null) {
    		if (insertChild(parent, child)) {
    			if (refreshViewer)
    				fTreeViewer.add(parent, child);
    		} else {
    			if (refreshViewer)
    				fTreeViewer.refresh(parent);
    			return;
    		}
    		child= parent;
    		parent= getParent(child);
    	}
    	if (insertChild(fResult, child)) {
    		if (refreshViewer)
    			fTreeViewer.add(fResult, child);
    	}
    }

    /**
     * returns true if the child already was a child of parent.
     * 
     * @param parent
     * @param child
     * @return
     */
    private boolean insertChild(Object parent, Object child) {
    	Set children= (Set) fChildrenMap.get(parent);
    	if (children == null) {
    		children= new HashSet();
    		fChildrenMap.put(parent, children);
    	}
    	return children.add(child);
    }

    protected void remove(Object element, boolean refreshViewer) {
    	// precondition here:  fResult.getMatchCount(child) <= 0

    	if (hasChildren(element)) {
    		if (refreshViewer)
    			fTreeViewer.refresh(element);
    	} else {
    		if (fResult.getMatchCount(element) == 0) {
    			fChildrenMap.remove(element);
    			Object parent= getParent(element);
    			if (parent != null) {
    				removeFromSiblings(element, parent);
    				remove(parent, refreshViewer);
    			} else {
    				removeFromSiblings(element, fResult);
    				if (refreshViewer)
    					fTreeViewer.refresh();
    			}
    		} else {
    			if (refreshViewer) {
    				fTreeViewer.refresh(element);
    			}
    		}
    	}
    }

    private void removeFromSiblings(Object element, Object parent) {
    	Set siblings= (Set) fChildrenMap.get(parent);
    	if (siblings != null) {
    		siblings.remove(element);
    	}
    }    
}
