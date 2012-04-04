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
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
public class OnValueChangeFunctionForPageItemFieldValidator extends DefaultFieldContentAnnotationValidationRule {

	public void validate(Node errorNode, Node container, IDataBinding containerBinding, String canonicalContainerName, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		IAnnotationBinding annotationBinding = (IAnnotationBinding) allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_ONVALUECHANGEFUNCTION));
		if(annotationBinding != null) {
			Object value = annotationBinding.getValue();
			if(IBinding.NOT_FOUND_BINDING != value && value instanceof IFunctionBinding) {
				ITypeBinding containerType = containerBinding.getType();
				if(containerType != null && IBinding.NOT_FOUND_BINDING != containerType) {
				   	IFunctionBinding fBinding = (IFunctionBinding) value;
		        	if(fBinding.getParameters().size() == 1) {
		        		ITypeBinding parameterType = ((IDataBinding) fBinding.getParameters().get(0)).getType();
						if(!TypeCompatibilityUtil.isMoveCompatible(parameterType, containerType, null, compilerOptions)) {
		        			problemRequestor.acceptProblem(
	    	        			errorNode,
	    	    				IProblemRequestor.ONVALUECHANGEFUNCTION_PARAMETER_TYPE_INVALID,
	    	    				new String[] {
	    	                		fBinding.getCaseSensitiveName(),
	    	                		StatementValidator.getTypeString(containerType),
	    	                		containerBinding.getCaseSensitiveName(),
	    	                		StatementValidator.getTypeString(parameterType)    	                		
	    	                	});
		        		}
		        	}
		        	else {
		        		problemRequestor.acceptProblem(
		        			errorNode,
		    				IProblemRequestor.ONVALUECHANGEFUNCTION_NOT_ONE_PARAMETER,
		    				new String[] {
		                		fBinding.getCaseSensitiveName(),
		                		StatementValidator.getTypeString(containerType),
		                		containerBinding.getCaseSensitiveName(),
		                		Integer.toString(fBinding.getParameters().size())
		                	});
		        	}
				}
			}
		}
	}
}
