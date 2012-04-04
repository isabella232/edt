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

import org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.GenNode;

public class ColumnResolver implements IGenVariableResolver {

	public void resolve(GenNode genNode) {
		if(!(genNode.equals(genNode.getGenModel().getRoot()))){
			String template = genNode.getTemplate();
			
			int column = 1;
			while(template.contains(IGenVariableResolver.Column)){
				int index = template.indexOf(IGenVariableResolver.Column);
				String startPart = template.substring(0, index);
				String endPart = template.substring(index+8, template.length());
				template = startPart + Integer.toString(column) + endPart;
				column ++;
			}
			
			String newTemplate = template;
			genNode.setTemplate(newTemplate);  
		}
	}

}
