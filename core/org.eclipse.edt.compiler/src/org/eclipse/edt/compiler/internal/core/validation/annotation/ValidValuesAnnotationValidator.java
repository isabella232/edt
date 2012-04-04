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
public class ValidValuesAnnotationValidator implements IAnnotationValidationRule {
	
	protected IAnnotationTypeBinding annotationType;
	protected String canonicalAnnotationName;
	
	public void validate(final Node errorNode, Node target, ITypeBinding targetTypeBinding, Map allAnnotations, final IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){
		final IAnnotationBinding annotationBinding = (IAnnotationBinding)allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_VALIDVALUES));		
		Object value = annotationBinding.getValue();
		
		Primitive prim = null;
		String typename = "";
		if (targetTypeBinding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING){
			prim = ((PrimitiveTypeBinding)targetTypeBinding).getPrimitive();
			if (Primitive.isNumericType(prim) || Primitive.isIntegerType(prim)){
				typename = IEGLConstants.MNEMONIC_NUMERIC;
			}else if (Primitive.isStringType(prim)){
				typename = Primitive.STRING.getName();
			}else return;
		}
		
		if (value instanceof IValidValuesElement[] && prim != null){
			IValidValuesElement[] array = (IValidValuesElement[])value;
			validateValues(array,prim,typename,targetTypeBinding,problemRequestor,errorNode);
		}


	}

	private void validateValues(IValidValuesElement[] array,Primitive prim,String typename,ITypeBinding targetTypeBinding,IProblemRequestor problemRequestor,Node errorNode){
		for (int i = 0; i < array.length; i++){
			IValidValuesElement element = array[i];
			if (element.isRange()){
				validateValues(element.getRangeElements(),prim,typename,targetTypeBinding,problemRequestor,errorNode);
				continue;
			}
			if ((element.isFloat() && !Primitive.isFloatType(prim)&& !Primitive.isIntegerType(prim) && prim != Primitive.MONEY && prim != Primitive.DECIMAL && prim != Primitive.NUM && prim != Primitive.NUMC && prim != Primitive.PACF && prim != Primitive.BIN)||
				(!element.isFloat()&& !element.isInt() && Primitive.isFloatType(prim)) ||
				(element.isInt() && !Primitive.isNumericType(prim))||
				(!element.isInt() && Primitive.isNumericType(prim)&& !Primitive.isFloatType(prim) && prim != Primitive.MONEY && prim != Primitive.DECIMAL && prim != Primitive.NUM && prim != Primitive.NUMC && prim != Primitive.PACF && prim != Primitive.BIN)||
				(element.isString() && !Primitive.isStringType(prim))||
				(!element.isString() && Primitive.isStringType(prim))){
				problemRequestor.acceptProblem(errorNode,
						IProblemRequestor.PROPERTY_VALIDVALUES_INVALID_VALUE_TYPE,
						new String[] {targetTypeBinding.getName(),
						typename});		
				break;
			}
		}

	}
}
