/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.core.validation.annotation;

import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;


/**
 * @author demurray
 */
public class ScreenSizesAnnotationValidator implements IValueValidationRule {

	public void validate(Node errorNode, Node target, IAnnotationBinding annotationBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {		
		boolean valueIsValid = true;
		Object[] value = (Object[]) annotationBinding.getValue();
		for(int i = 0; i < value.length && valueIsValid; i++) {
			Object[] element = (Object[]) value[i];
			
			if(element instanceof Integer[]) {
				if(element.length == 2) {
					Integer[] intAry = (Integer[]) element;
					if(intAry[0].intValue() < 1 && intAry[1].intValue() < 1) {
						valueIsValid = false;
					}
				}
			}
			else {
				valueIsValid = false;
			}
		}
		
		if(!valueIsValid) {
			problemRequestor.acceptProblem(
				errorNode,
				IProblemRequestor.INVALID_FORM_SCREENSIZES_PROPERTY_VALUE,
				new String[] {IEGLConstants.PROPERTY_SCREENSIZES, getCanonicalName(target)});
		}
	}
	
	private String getCanonicalName(Node target) {
		return getEnclosingPart(target).getName().getCanonicalName();
	}
	
	private Part getEnclosingPart(Node node) {
		if(node instanceof Part) {
			return (Part) node;			
		}
		return getEnclosingPart(node.getParent());
	}
}
