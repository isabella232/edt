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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.AnnotationValidationRule;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.FixedPrecisionType;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.eglx.rui.messages.RUIResourceKeys;
import org.eclipse.edt.mof.utils.NameUtile;


public class IsBooleanAnnotationValidator extends AnnotationValidationRule {
	
	protected AnnotationType annotationType;
	protected String canonicalAnnotationName;
	
	public IsBooleanAnnotationValidator() {
		super(NameUtile.getAsCaseSensitiveName("isboolean"));
	}
	
	private static final List<Type> acceptedTypes = new ArrayList<Type>();
	static{
		acceptedTypes.add(TypeUtils.Type_CHAR);
		acceptedTypes.add(TypeUtils.Type_BIN);
		acceptedTypes.add(TypeUtils.Type_NUM);
		acceptedTypes.add(TypeUtils.Type_NUMC);
		acceptedTypes.add(TypeUtils.Type_DECIMAL);
		acceptedTypes.add(TypeUtils.Type_PACF);
		acceptedTypes.add(TypeUtils.Type_INT);
		acceptedTypes.add(TypeUtils.Type_BIGINT);
		acceptedTypes.add(TypeUtils.Type_SMALLINT);
		acceptedTypes.add(TypeUtils.Type_FLOAT);
		acceptedTypes.add(TypeUtils.Type_MONEY);
	}
	
	@Override
	public void validate(Node errorNode, Node target, Element targetElement, Map<String, Object> allAnnotationsAndFields, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
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
		
		Classifier classifier = type.getClassifier();
		if (classifier == null) {
			return;
		}
		
		for (Type nextType : acceptedTypes) {
			// Compare with the classifier in case it's parameterized.
			if (nextType != null && classifier.equals(nextType)) { // not all the 'accepted' types are currently supported in the language, so their TypeUtils constants are null. Skip them.
				if (type instanceof FixedPrecisionType && ((FixedPrecisionType)type).getLength() != 0 && ((FixedPrecisionType)type).getLength() == ((FixedPrecisionType)type).getDecimals()) {
					problemRequestor.acceptProblem(errorNode,
							RUIResourceKeys.PROPERTY_REQUIRES_NONDECIMAL_DIGITS,
							IMarker.SEVERITY_ERROR,
							new String[]{IEGLConstants.PROPERTY_ISBOOLEAN, BindingUtil.getShortTypeString(type, true)},
							RUIResourceKeys.getResourceBundleForKeys());
				}
				return;
			}
		}
			
		problemRequestor.acceptProblem(errorNode,
				RUIResourceKeys.PROPERTY_INVALID_FOR_TYPE,
				IMarker.SEVERITY_ERROR,
				new String[]{IEGLConstants.PROPERTY_ISBOOLEAN, BindingUtil.getShortTypeString(type, true)},
				RUIResourceKeys.getResourceBundleForKeys());
	}
}
