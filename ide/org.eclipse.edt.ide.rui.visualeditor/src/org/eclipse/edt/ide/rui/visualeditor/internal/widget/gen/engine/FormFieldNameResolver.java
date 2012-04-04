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


public class FormFieldNameResolver implements IGenVariableResolver {

	public void resolve(GenNode genNode) {
		String template = genNode.getTemplate();
		String widgetName = ResolverUtil.alaisWidgetName(genNode.getInsertDataNode().getWidgetName());

		int i = 0;
		String tempFormFieldName = widgetName + "_formField";
		String formFieldName = tempFormFieldName;
		while (NameFinder.getInstance().isFieldNameExist(formFieldName)) {
			i++;
			formFieldName = tempFormFieldName + i;
		}

		if (!(genNode.equals(genNode.getGenModel().getRoot())) && genNode.getInsertDataNode().getDataTemplate().isGenController()) {
			genNode.getContext().set(Context.FORM_FIELD_NAME, formFieldName);
		}

		String newTemplate = template.replace(IGenVariableResolver.FormFieldName, formFieldName);
		genNode.setTemplate(newTemplate);
	}

}
