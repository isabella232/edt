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
package org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.engine;

import java.util.List;

import org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.ComposeGenNode;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.GenNode;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.GenNode.Context;


public class FormFieldsResolver implements IGenVariableResolver {

	public void resolve(GenNode genNode) {
		if(genNode instanceof ComposeGenNode){
			ComposeGenNode composeGenNode = (ComposeGenNode)genNode;
			String template = composeGenNode.getTemplate();
			StringBuffer sbFormFields = new StringBuffer();
			List<GenNode> children = composeGenNode.getChildren();
			for(int i=0; i<children.size(); i++){
				GenNode child = children.get(i);
				if(!child.equals(genNode)){
					Object oFormFieldName = child.getContext().get(Context.FORM_FIELD_NAME);
					if(oFormFieldName != null){
						String formFieldName = (String)oFormFieldName;
						sbFormFields.append(formFieldName).append(",");
					}
				}
			}
		
			String formFields = sbFormFields.toString();
			if(!formFields.isEmpty()){
				formFields = formFields.substring(0, formFields.lastIndexOf(","));
			}
			
			String newTemplate = template.replace(IGenVariableResolver.FormFields, formFields);
			genNode.setTemplate(newTemplate);  
		}
	}

}
