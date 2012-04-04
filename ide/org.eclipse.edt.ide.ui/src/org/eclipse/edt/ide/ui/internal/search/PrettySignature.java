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
package org.eclipse.edt.ide.ui.internal.search;

import org.eclipse.edt.ide.core.internal.model.util.EGLModelUtil;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IFunction;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.Signature;

public class PrettySignature {

	public static String getSignature(IEGLElement element) {
		if (element == null)
			return null;
		else
			switch (element.getElementType()) {
				case IEGLElement.FUNCTION:
					return getFunctionSignature((IFunction)element);
				case IEGLElement.PART:
					return EGLModelUtil.getFullyQualifiedName((IPart)element);
				default:
					return element.getElementName();
			}
	}
	
	public static String getFunctionSignature(IFunction method) {
		StringBuffer buffer= new StringBuffer();
		buffer.append(EGLModelUtil.getFullyQualifiedName(method.getDeclaringPart()));
		buffer.append('.');
		buffer.append(getUnqualifiedFunctionSignature(method));
		
		return buffer.toString();
	}

	public static String getUnqualifiedTypeSignature(IPart type) {
		return type.getElementName();
	}
	
	public static String getUnqualifiedFunctionSignature(IFunction method) {
		StringBuffer buffer= new StringBuffer();
		buffer.append(method.getElementName());
		buffer.append('(');
		
		String[] types= method.getParameterTypes();
		if (types.length > 0)
			buffer.append(Signature.toString(types[0]));
		for (int i= 1; i < types.length; i++) {
			buffer.append(", "); //$NON-NLS-1$
			buffer.append(Signature.toString(types[i]));
		}
		
		buffer.append(')');
		
		return buffer.toString();
	}	
}
