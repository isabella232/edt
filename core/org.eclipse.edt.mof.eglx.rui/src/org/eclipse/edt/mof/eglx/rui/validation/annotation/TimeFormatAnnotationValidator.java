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
package org.eclipse.edt.mof.eglx.rui.validation.annotation;

import java.util.Map;

import org.eclipse.edt.compiler.binding.AnnotationValidationRule;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.FixedPrecisionType;
import org.eclipse.edt.mof.egl.ParameterizedType;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.eglx.rui.messages.RUIResourceKeys;
import org.eclipse.edt.mof.utils.NameUtile;


public class TimeFormatAnnotationValidator extends AnnotationValidationRule {
	
	public TimeFormatAnnotationValidator() {
		super(NameUtile.getAsCaseSensitiveName("timeformat"));
	}
	
	public void validate(Node errorNode, Node target, Element targetElement, Map<String, Object> allAnnotationsAndFields, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){
		Type type = null;
		if (targetElement instanceof Type) {
			type = (Type)targetElement;
		}
		else if (targetElement instanceof TypedElement) {
			type = ((TypedElement)targetElement).getType();
		}
		
		if (type == null) {
			return;
		}
		

		// Numerics cannot have decimals.
		if (type instanceof FixedPrecisionType) {
			if (((FixedPrecisionType)type).getDecimals() > 0) {
				problemRequestor.acceptProblem(errorNode,
						RUIResourceKeys.PROPERTY_INVALID_FOR_DECIMALS,
						IMarker.SEVERITY_ERROR,
						new String[]{IEGLConstants.PROPERTY_TIMEFORMAT},
						RUIResourceKeys.getResourceBundleForKeys());
			}
			return;
		}
		
		if (type instanceof ParameterizedType) {
			type = ((ParameterizedType)type).getParameterizableType();
		}
		
		// string, time, and numerics are valid. everything else is invalid.
		if (TypeUtils.Type_STRING.equals(type) || TypeUtils.Type_TIME.equals(type) || TypeUtils.isNumericType(type)) {
			return;
		}
		
		problemRequestor.acceptProblem(errorNode,
				RUIResourceKeys.PROPERTY_INVALID_FOR_TYPE,
				IMarker.SEVERITY_ERROR,
				new String[]{IEGLConstants.PROPERTY_TIMEFORMAT, BindingUtil.getShortTypeString(type, false)},
				RUIResourceKeys.getResourceBundleForKeys());
	}
	
}
