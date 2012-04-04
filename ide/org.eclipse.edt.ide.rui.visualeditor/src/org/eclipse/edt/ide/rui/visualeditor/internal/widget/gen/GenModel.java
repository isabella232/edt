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
package org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen;




public class GenModel {
	private ComposeGenNode root;
	
	public Templates getTemplate(){
		return TemplateComposer.getInstance().compose(this);
	}

	public ComposeGenNode getRoot(){
		return root;
	}
	
	public void setRoot(ComposeGenNode root) {
		this.root = root;
		this.root.setGenModel(this);
	}

	public String getName(){
		return root.getInsertDataNode().getWidgetName();
	}
}
