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

import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Delegate;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.jface.text.IRegion;

public class DelegateOutlineAdapter extends AbstractOutlineAdapter {

	public DelegateOutlineAdapter(EGLEditor editor) {
		super(editor);
		nodeIcon = PluginImages.DESC_OBJS_DELEGATE;
	}
	/* (non-Javadoc)
	* @see org.eclipse.edt.ide.ui.internal.outline.IOutlineAdapter#getText(java.lang.Object)
	*/
	public String getText(Object element) {
		Delegate item = (Delegate) element;
		Name funcName = item.getName();
		StringBuffer buffer = new StringBuffer();
		buffer.append(funcName.getCanonicalName());
		buffer.append('(');
		//FunctionParameter[] parameters = (IEGLFunctionParameter[]) function.getParameters().toArray(new IEGLFunctionParameter[0]);
		final ArrayList parameters = new ArrayList();
		item.accept(new DefaultASTVisitor(){
			public boolean visit(Delegate delegate) { return true;};
			public boolean visit(FunctionParameter functionParameter) {
				parameters.add(functionParameter);
				return false;
			};
		});
		
		int len = parameters.size();
		if (len > 0) {
			for (int i = 0; i < len; i++) {
				if (i != 0) {
					buffer.append(", "); //$NON-NLS-1$
				}
				FunctionParameter param = (FunctionParameter)(parameters.get(i));
				buffer.append(formatType(param.getType()));
			}
		}
		buffer.append(')');
		return buffer.toString();

	}

	public IRegion getHighlightRange(Object element) {
		Delegate node = (Delegate) element;
		return getPartNameHighlightRange(node);
	}
}
