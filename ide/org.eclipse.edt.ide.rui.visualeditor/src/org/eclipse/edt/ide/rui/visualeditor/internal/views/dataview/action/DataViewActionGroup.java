/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.views.dataview.action;

import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvEditor;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.actions.ActionGroup;


public class DataViewActionGroup extends ActionGroup {
	private TreeViewer treeViewer;
    private EvEditor evEditor;
	
	public DataViewActionGroup(TreeViewer treeViewer, EvEditor evEditor){
		this.treeViewer = treeViewer;
		this.evEditor = evEditor;
	}
	
	public void fillContextMenu(IMenuManager mgr){
		MenuManager menuManager = (MenuManager)mgr;
		MenuManager newSubMenuManager = new MenuManager(Messages.NL_PDV_Context_Menu_New_Sub_Menu);
		newSubMenuManager.add(new NewEGLVariableAction(evEditor));
		menuManager.add(newSubMenuManager);

		Tree tree = treeViewer.getTree();
		menuManager.createContextMenu(tree);
		
		tree.setMenu(menuManager.getMenu());
	}
}
