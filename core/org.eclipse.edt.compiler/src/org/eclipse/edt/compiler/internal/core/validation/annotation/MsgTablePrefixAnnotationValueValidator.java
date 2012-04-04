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
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;


/**
 * @author svihovec
 *
 */
public class MsgTablePrefixAnnotationValueValidator implements IValueValidationRule {

	public void validate(Node errorNode, Node target, IAnnotationBinding annotationBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if (annotationBinding.getValue() != null && annotationBinding.getValue() != IBinding.NOT_FOUND_BINDING) {
			if (annotationBinding.getValue() instanceof String){
				String value = (String)annotationBinding.getValue();
				String unqualifiedValue = value;
				int index = value.lastIndexOf(".");
				if (index > -1) {
				    unqualifiedValue = value.substring(index + 1);
				}
				if(unqualifiedValue.length() < 3 || unqualifiedValue.length() > 4) {
					problemRequestor.acceptProblem(errorNode,
							IProblemRequestor.PROPERTY_MSGTABLEPREFIX_INVALID_LENGTH,
							new String[] {unqualifiedValue,IEGLConstants.PROPERTY_MSGTABLEPREFIX,
										"3","4"}); //$NON-NLS-1$ //$NON-NLS-2$
  				
				}
			}
				
		}
	}
}
