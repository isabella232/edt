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

import org.eclipse.edt.compiler.binding.AnnotationValidationRule;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.EnumerationEntry;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.utils.NameUtile;
import org.eclipse.edt.rui.messages.RUIResourceKeys;


public class SignAnnotationValidator extends AnnotationValidationRule {
	
	public SignAnnotationValidator() {
		super(NameUtile.getAsCaseSensitiveName("sign"));
	}
	
	@Override
	public void validate(Node errorNode, Node target, Element targetElement, Map<String, Object> allAnnotationsAndFields, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		Annotation annotationBinding = (Annotation)allAnnotationsAndFields.get(NameUtile.getAsName(IEGLConstants.PROPERTY_SIGN));
		if (annotationBinding.getValue() instanceof EnumerationEntry) {
			EnumerationEntry value = (EnumerationEntry) annotationBinding.getValue();
			
			// Float and Smallfloat cannot use 'trailing'
			if (NameUtile.equals(value.getName(), NameUtile.getAsName("trailing"))) {
				Type targetType = null;
				if (targetElement instanceof Type) {
					targetType = (Type)targetElement;
				}
				else if (targetElement instanceof TypedElement) {
					targetType = ((TypedElement)targetElement).getType();
				}
				
				if (targetType != null && (targetType.equals(TypeUtils.Type_FLOAT) || targetType.equals(TypeUtils.Type_SMALLFLOAT))) {
					problemRequestor.acceptProblem(errorNode,
							RUIResourceKeys.INVALID_PROPERTY_VALUE_FOR_ITEM_TYPE,
							IMarker.SEVERITY_ERROR,
							new String[]{"trailing", IEGLConstants.PROPERTY_SIGN, BindingUtil.getShortTypeString(targetType, false)},
							RUIResourceKeys.getResourceBundleForKeys());
				}
			}
		}
	}
}
