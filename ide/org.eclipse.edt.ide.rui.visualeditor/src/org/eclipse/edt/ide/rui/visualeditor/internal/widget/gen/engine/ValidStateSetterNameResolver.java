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
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.GenNode.Context;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.util.NameFinder;


public class ValidStateSetterNameResolver implements IGenVariableResolver {

	public void resolve(GenNode genNode) {
		String template = genNode.getTemplate();
		String fieldName = genNode.getGenModel().getRoot().getInsertDataNode().getFieldName();
		
		int i = 0;
		String tempValidStateSetterName = "handleValidStateChange_" + fieldName;
		String validStateSetterName = tempValidStateSetterName;
		while(NameFinder.getInstance().isFunctionNameExist(validStateSetterName)){
			i++;
			validStateSetterName = tempValidStateSetterName + i; 
		}
		
		genNode.getContext().set(Context.VALID_STATE_SETTER_NAME, validStateSetterName);
		
		String newTemplate = template.replace(IGenVariableResolver.ValidStateSetterName, validStateSetterName);
		genNode.setTemplate(newTemplate);
	}

}
