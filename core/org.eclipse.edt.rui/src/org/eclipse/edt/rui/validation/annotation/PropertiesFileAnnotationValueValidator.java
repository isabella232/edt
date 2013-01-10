/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.rui.validation.annotation;

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.IValueValidationRule;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.rui.messages.RUIResourceKeys;


public class PropertiesFileAnnotationValueValidator implements IValueValidationRule {
	
	@Override
	public void validate(Node errorNode, Node target, Annotation annotationBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if (annotationBinding.getValue() != null) {
			String value = (String)annotationBinding.getValue();
			if (value.indexOf('-') != -1) {
				problemRequestor.acceptProblem(
						errorNode,
						RUIResourceKeys.PROPERTIESFILE_NAME_CANNOT_CONTAIN_DASH,
						IMarker.SEVERITY_ERROR,
						new String[] {},
						RUIResourceKeys.getResourceBundleForKeys());
			}			
		}
	}	
}
