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
package org.eclipse.edt.compiler.binding.annotationType;

import java.util.Map;

import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.DefaultFieldContentAnnotationValidationRule;
import org.eclipse.edt.compiler.internal.core.validation.annotation.IFieldContentAnnotationValidationRule;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class DelimiterAnnotationTypeBinding extends DefaultFieldContentAnnotationValidationRule {
	public void validate(Node errorNode, Node container, IDataBinding containerBinding, String canonicalContainerName, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if(allAnnotations.containsKey(InternUtil.intern(IEGLConstants.PROPERTY_DELIMITER))){
			
//			int minimumInputLength = ((Integer)((IAnnotationBinding)allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_MINIMUMINPUT))).getValue()).intValue();
//				
//			int definedLength = getFieldLength(container);
//			int maxLengthAllowed = definedLength;
//			
//			//decimal point
//			if(hasDecimals(container)){
//				maxLengthAllowed += 1;
//			}
//			
//			// sign
//			if(allAnnotations.containsKey(InternUtil.intern(IEGLConstants.PROPERTY_SIGN))){
//				// if yes
//				maxLengthAllowed += 1;
//			}
//
//			//currency
//			if(allAnnotations.containsKey(InternUtil.intern(IEGLConstants.PROPERTY_CURRENCY))){
//				// if yes
//				maxLengthAllowed += 1;
//			}
//			
//			// TODO CurrencyValue?
//	
//    		// numeric separator
//			if(allAnnotations.containsKey(InternUtil.intern(IEGLConstants.PROPERTY_NUMERICSEPARATOR))){
//				// if yes
//				maxLengthAllowed += 1;
//			}
//
//			if(minimumInputLength > maxLengthAllowed) {
//				problemRequestor.acceptProblem(errorNode, 
//												IProblemRequestor.PROPERTY_MINIMUM_INPUT_MUST_BE_LESS_THAN_PRIMITIVE_LENGTH,
//												new String[]{String.valueOf(definedLength), String.valueOf(maxLengthAllowed), "bob"});
//										
//			}
		}
		
	}

}
