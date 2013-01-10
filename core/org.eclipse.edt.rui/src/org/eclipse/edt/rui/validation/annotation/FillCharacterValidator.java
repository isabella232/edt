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

import java.util.Map;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.IAnnotationValidationRule;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.utils.NameUtile;
import org.eclipse.edt.rui.messages.RUIResourceKeys;


public class FillCharacterValidator implements IAnnotationValidationRule {

	@Override
	public void validate(Node errorNode, Node target, Element targetElement, Map<String, Object> allAnnotationsAndFields, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){
		Annotation annotationBinding = (Annotation)allAnnotationsAndFields.get(NameUtile.getAsName(IEGLConstants.PROPERTY_FILLCHARACTER));
		if(annotationBinding != null && annotationBinding.getValue() != null) {
			if(annotationBinding.getValue().toString().length() > 1) {
				problemRequestor.acceptProblem(
						errorNode,
						RUIResourceKeys.INVALID_FILLCHARACTER_PROPERTY_VALUE,
						IMarker.SEVERITY_ERROR,
						new String[] {IEGLConstants.PROPERTY_FILLCHARACTER},
						RUIResourceKeys.getResourceBundleForKeys());
			}
		}
	}
}
