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

import java.util.Map;

import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.IValidValuesElement;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;



/**
 * @author 
 */
public class ValidValuesForPageItemAnnotationValidator extends DefaultFieldContentAnnotationValidationRule {
	
	protected IAnnotationTypeBinding annotationType;
	protected String canonicalAnnotationName;

	public void validate(Node errorNode, Node container, IDataBinding containerBinding, String canonicalContainerName, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		final IAnnotationBinding annotationBinding = (IAnnotationBinding)allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_VALIDVALUES));
		if (annotationBinding == null || annotationBinding.getValue() == null ){
			return;
		}
		
		Object value = annotationBinding.getValue();
	
		if (value instanceof IValidValuesElement[] ){
			IValidValuesElement[] array = (IValidValuesElement[])value;
			for (int i = 0; i < array.length; i++){
				IValidValuesElement element = array[i];
				if (element.isRange()){
			 		IAnnotationBinding typeaheadannotationBinding = (IAnnotationBinding) allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_TYPEAHEAD));
					if(typeaheadannotationBinding != null && typeaheadannotationBinding.getValue() != null) {
						org.eclipse.edt.compiler.core.Boolean typeaheadvalue = (org.eclipse.edt.compiler.core.Boolean)typeaheadannotationBinding.getValue();
						if (typeaheadvalue.booleanValue() ){
							problemRequestor.acceptProblem(
									errorNode,
									IProblemRequestor.VALIDVALUES_RANGE_WITH_TYPEAHEAD);
						}
					}
				}
			}

		}

		

	}
	


}
