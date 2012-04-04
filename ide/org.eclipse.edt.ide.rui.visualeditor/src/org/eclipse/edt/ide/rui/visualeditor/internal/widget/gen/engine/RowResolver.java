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
package org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.engine;

import java.util.List;

import org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.ComposeGenNode;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.GenNode;


public class RowResolver implements IGenVariableResolver {

	public void resolve(GenNode genNode) {
		if(!(genNode.equals(genNode.getGenModel().getRoot()))){
			ComposeGenNode root = genNode.getGenModel().getRoot();
			String template = genNode.getTemplate();
			
			List<GenNode> children = root.getChildren();
			int row = 1;
			for(int i=0; i<children.size(); i++){
				GenNode child = children.get(i);
				if(child.getInsertDataNode().getBindingName().equalsIgnoreCase(genNode.getInsertDataNode().getBindingName())){
					row = i+1;
				}
			}
			
			String newTemplate = template.replace(IGenVariableResolver.Row, Integer.toString(row));
			genNode.setTemplate(newTemplate);  
		}
	}

}
