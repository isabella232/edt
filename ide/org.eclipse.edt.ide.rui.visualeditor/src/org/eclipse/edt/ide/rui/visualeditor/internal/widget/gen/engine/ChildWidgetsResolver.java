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
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.GenNode.Context;


public class ChildWidgetsResolver implements IGenVariableResolver {

	public void resolve(GenNode genNode) {
		if(genNode instanceof ComposeGenNode){
			ComposeGenNode composeGenNode = (ComposeGenNode)genNode;
			String template = composeGenNode.getTemplate();
			StringBuffer sbChildWidgets = new StringBuffer();
			List<GenNode> children = composeGenNode.getChildren();
			for(int i=0; i<children.size(); i++){
				GenNode child = children.get(i);
				if(!child.equals(genNode)){
					Object oNameLabelName = child.getContext().get(Context.NAME_LABEL_NAME);
					if(oNameLabelName != null){
						String labelName = (String)oNameLabelName;
						sbChildWidgets.append(labelName).append(",");
					}
					
					String widgetName = child.getInsertDataNode().getWidgetName();
					sbChildWidgets.append(widgetName).append(",");
					
					if(child.getInsertDataNode().getModel().isAddErrorMessage() && child.getInsertDataNode().getDataTemplate().isGenController()){
						Object oErrorLabelName = child.getContext().get(Context.ERROR_LABEL_NAME);
						if(oErrorLabelName != null){
							String errorLabelName = (String)oErrorLabelName;
							sbChildWidgets.append(errorLabelName).append(",");
						}
					}
				}
			}
		
			String childWidgets = sbChildWidgets.toString();
			if(!childWidgets.isEmpty()){
				childWidgets = childWidgets.substring(0, childWidgets.lastIndexOf(","));
			}
			
			String newTemplate = template.replace(IGenVariableResolver.ChildWidgets, childWidgets);
			genNode.setTemplate(newTemplate);  
		}
	}

}
