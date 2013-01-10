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

import org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.GenNode;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.GenNode.Context;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.util.NameFinder;


public class ControllerNameResolver implements IGenVariableResolver {

	public void resolve(GenNode genNode) {
		String template = genNode.getTemplate();
		String widgetName = ResolverUtil.alaisWidgetName(genNode.getInsertDataNode().getWidgetName());
		
		int i = 0;
		String tempControllerName = widgetName + "_controller";
		String controllerName = tempControllerName;
		while(NameFinder.getInstance().isFieldNameExist(controllerName)){
			i++;
			controllerName = tempControllerName + i; 
		}
		
		genNode.getContext().set(Context.CONTROLLER_NODE, controllerName);
		
		String newTemplate = template.replace(IGenVariableResolver.ControllerName, controllerName);
		genNode.setTemplate(newTemplate);
	}

}
