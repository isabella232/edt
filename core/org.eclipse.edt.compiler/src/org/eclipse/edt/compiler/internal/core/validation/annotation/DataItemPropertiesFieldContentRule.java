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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.FieldContentValidationAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.annotationType.EGLDataItemPropertyProblemAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.annotationType.EGLDataItemPropertyProblemsAnnotationTypeBinding;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;


public class DataItemPropertiesFieldContentRule extends FieldContentValidationAnnotationTypeBinding {
	
	private Object[] memberAnnotations;
	private IAnnotationTypeBinding enclosingSubtype;

	public DataItemPropertiesFieldContentRule(IAnnotationTypeBinding enclosingSubtype, Object[] memberAnnotations) {
		super("DataItemPropertiesFieldContentRule");
		
		this.enclosingSubtype = enclosingSubtype;
		this.memberAnnotations = memberAnnotations;
	}

	public void validate(Node errorNode, Node target, IDataBinding containerBinding, String canonicalContainerName, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		Map problemsMap = buildProblemsMap(containerBinding);
		String valFunctionName = InternUtil.intern("egl.ui.ValidatorFunction");
		
		for(int i = 0; i < memberAnnotations.length; i++) {
			IAnnotationBinding overridenAnnotation = (IAnnotationBinding) allAnnotations.get(((ITypeBinding) memberAnnotations[i]).getName());
			if(overridenAnnotation != null) {
				Object value = overridenAnnotation.getValue();
				if(value != IBinding.NOT_FOUND_BINDING && value != null) {
					continue;
				}
			}
			
			String[] error = (String[]) problemsMap.get(((ITypeBinding) memberAnnotations[i]).getPackageQualifiedName());
			if(error != null) {
				
				String errorAnnName = InternUtil.intern(((ITypeBinding) memberAnnotations[i]).getPackageQualifiedName());
				if (errorAnnName == valFunctionName && Binding.isValidBinding(containerBinding) && containerBinding.getKind() == IDataBinding.FORM_FIELD) {
					continue;
				}
					
				problemRequestor.acceptProblem(
					errorNode,
					IProblemRequestor.DATAITEM_CONTEXT_SPECIFIC_PROBLEM,
					new String[] {
						error[0],
						canonicalContainerName,
						enclosingSubtype.getCaseSensitiveName(),
						error[1]
					});
			}
		}
	}

	private Map buildProblemsMap(IDataBinding containerBinding) {
		Map result = new HashMap();
		IAnnotationBinding aBinding = containerBinding.getAnnotation(EGLDataItemPropertyProblemsAnnotationTypeBinding.getInstance());
		if(aBinding != null) {
			IAnnotationBinding[] aBindings = (IAnnotationBinding[]) aBinding.getValue();
			for(int i = 0; i < aBindings.length; i++) {
				String qualifiedDataItemName = getStringValue((IAnnotationBinding) aBindings[i].findData(EGLDataItemPropertyProblemAnnotationTypeBinding.QUALIFIED_DATAITEM_NAME));
				String annotationName = getStringValue((IAnnotationBinding) aBindings[i].findData(EGLDataItemPropertyProblemAnnotationTypeBinding.ANNOTATION_NAME));
				String errorMsg = getStringValue((IAnnotationBinding) aBindings[i].findData(EGLDataItemPropertyProblemAnnotationTypeBinding.ERROR_MSG));
				
				result.put(annotationName, new String[] {qualifiedDataItemName, errorMsg});				
			}
		}
		return result;
	}

	private String getStringValue(IAnnotationBinding binding) {
		return (String) binding.getValue();
	}
}
