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


public class FormManagerNameResolver implements IGenVariableResolver {
	
	public void resolve(GenNode genNode){
		String template = genNode.getTemplate();
		Object oFormManagerName = genNode.getContext().get(Context.FORM_FIELD_MANAGER_NAME);
		String formManagerName = "";
		if(oFormManagerName == null){
			String widgetName = ResolverUtil.alaisWidgetName(genNode.getInsertDataNode().getWidgetName());
			
			int i = 0;
			String tempFormManagerName = widgetName + "_form";
			formManagerName = tempFormManagerName;
			while(NameFinder.getInstance().isFieldNameExist(formManagerName)){
				i++;
				formManagerName = tempFormManagerName + i; 
			}
			
			genNode.getContext().set(Context.FORM_FIELD_MANAGER_NAME, formManagerName);
		}else{
			formManagerName = (String)oFormManagerName;
		}
		
		String newTemplate = template.replace(IGenVariableResolver.FormManagerName, formManagerName);
		genNode.setTemplate(newTemplate);
	}
}
