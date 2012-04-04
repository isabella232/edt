/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.outline;

import java.util.ArrayList;

import org.eclipse.edt.compiler.core.ast.ConstantFormField;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedForm;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.TopLevelForm;
import org.eclipse.edt.compiler.core.ast.VariableFormField;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

public class FormOutlineAdapter extends AbstractOutlineAdapter {

	public FormOutlineAdapter(EGLEditor editor) {
		super(editor);
		nodeIcon = PluginImages.DESC_OBJS_FORM;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.outline.IOutlineAdapter#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		final ArrayList formFields = new ArrayList();
		Node form = (Node) parentElement;		
		form.accept(new DefaultASTVisitor(){
			public boolean visit(NestedForm nestedForm) {return true;};
			public boolean visit(TopLevelForm topLevelForm) {return true;};
			public boolean visit(VariableFormField variableFormField) {
				formFields.add(variableFormField);
				return false;
			};
			public boolean visit(ConstantFormField constantFormField) {
				formFields.add(constantFormField);
				return false;
			};
		});
		return formFields.toArray();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.outline.IOutlineAdapter#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		Name type = null;
		Name formName = null;
		if(element instanceof TopLevelForm)
		{			
			TopLevelForm form = (TopLevelForm)element;
			formName = form.getName();
			type = form.getSubType();
		}
		else if(element instanceof NestedForm)
		{
			NestedForm nestForm = (NestedForm)element;
			formName = nestForm.getName();
			type = nestForm.getSubType();
		}
		
		return formName.getCanonicalString() + ((type == null) ? "" : " : " + type.getCanonicalString()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public IRegion getHighlightRange(Object element) {
		Name formName = null;
		if(element instanceof TopLevelForm)
			formName = ((TopLevelForm)element).getName();
		else if(element instanceof NestedForm)
			formName = ((NestedForm)element).getName();
		else 
			return new Region(0, 0);
		
		return new Region(formName.getOffset(), formName.getLength());
	}

}
