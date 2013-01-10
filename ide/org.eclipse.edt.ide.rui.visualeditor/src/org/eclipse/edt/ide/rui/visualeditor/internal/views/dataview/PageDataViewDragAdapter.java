/*******************************************************************************
 * Copyright © 2010, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.views.dataview;

import org.eclipse.edt.ide.rui.visualeditor.internal.views.dataview.model.HandlerPageDataNode;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.TreeDragSourceEffect;
import org.eclipse.swt.widgets.Tree;


public class PageDataViewDragAdapter extends TreeDragSourceEffect {
	private ISelectionProvider selectionProvider;  
	  
    public PageDataViewDragAdapter(ISelectionProvider selectionProvider, Tree tree) {
    	super( tree );
        this.selectionProvider = selectionProvider;  
    }  
    
    public void dragStart(DragSourceEvent event){
		event.image = null;
    	ISelection selection = selectionProvider.getSelection();
    	if(selection instanceof TreeSelection){
    		TreeSelection treeSelection = (TreeSelection)selection;
    		Object object = treeSelection.getFirstElement();
    		if(object instanceof HandlerPageDataNode){
    			event.doit = false;
    		}
    	}
    }
  
    public void dragSetData(DragSourceEvent event) { 
    	ISelection selection = selectionProvider.getSelection();
    	if(selection instanceof TreeSelection){
    		TreeSelection treeSelection = (TreeSelection)selection;
    		event.data = treeSelection.getFirstElement();
    	}
    }  
    
    
}
