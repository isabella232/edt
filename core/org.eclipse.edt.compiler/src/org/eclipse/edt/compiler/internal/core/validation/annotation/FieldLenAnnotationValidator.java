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

import org.eclipse.edt.compiler.binding.AnnotationValidationAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.ConstantFormField;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;



/**
 * @author 
 */
public class FieldLenAnnotationValidator extends AnnotationValidationAnnotationTypeBinding {
	
	public FieldLenAnnotationValidator() {
		super(InternUtil.internCaseSensitive("fieldlen"));
	}
	
	public void validate(final Node errorNode, Node target, ITypeBinding targetTypeBinding, Map allAnnotations, final IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){

		if (target instanceof ConstantFormField) {
			IBinding binding = ((ConstantFormField)target).resolveBinding();
			String[] pkgName = new String[] {"egl", "ui"};
			IAnnotationBinding fieldLenAnn = binding.getAnnotation(pkgName, IEGLConstants.PROPERTY_FIELDLEN);
			IAnnotationBinding valueAnn = binding.getAnnotation(pkgName, IEGLConstants.PROPERTY_VALUE);
			if (fieldLenAnn != null && valueAnn != null && fieldLenAnn.getValue() instanceof Integer && valueAnn.getValue() instanceof String) {
				int fieldLen = ((Integer)fieldLenAnn.getValue()).intValue();
				String value = (String) valueAnn.getValue();
				int byteLen = value.getBytes().length;
				
				if (fieldLen < byteLen) {
					problemRequestor.acceptProblem(
							errorNode,
							IProblemRequestor.FIELDLEN_LESS_THAN_VALUE_LENGTH,
							IMarker.SEVERITY_WARNING,
							new String[] {Integer.toString(fieldLen), Integer.toString(byteLen)});
				}
			}
		}
		
		
//		final IAnnotationBinding annotationBinding = (IAnnotationBinding)allAnnotations.get(SignAnnotationTypeBinding.name);
//		Object value = annotationBinding.getValue();
		
		
//		if (value == SignKind.TRAILING){
//			if (targetTypeBinding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING && ((PrimitiveTypeBinding)targetTypeBinding).getPrimitive() == Primitive.SMALLFLOAT){
//				problemRequestor.acceptProblem(errorNode,
//						IProblemRequestor.INVALID_PROPERTY_VALUE_FOR_ITEM_TYPE,
//						new String[] 
//						{SignKind.TRAILING.getName(),
//						IEGLConstants.PROPERTY_SIGN,
//						targetTypeBinding.getName()});
//			}
//		}

	}
}
