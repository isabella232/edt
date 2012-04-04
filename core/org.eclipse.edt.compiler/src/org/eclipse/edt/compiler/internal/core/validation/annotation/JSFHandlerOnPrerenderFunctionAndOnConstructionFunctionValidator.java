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

import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.Binding;
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
 * @author svihovec
 *
 */
public class JSFHandlerOnPrerenderFunctionAndOnConstructionFunctionValidator implements IAnnotationValidationRule {
	
	public void validate(Node errorNode, Node target, ITypeBinding targetTypeBinding, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		IAnnotationBinding onPrerenderFunctionABinding = (IAnnotationBinding) allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_ONPRERENDERFUNCTION));
		IAnnotationBinding onPostrenderFunctionABinding = (IAnnotationBinding) allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_ONPOSTRENDERFUNCTION));
		IAnnotationBinding onConstructionFunctionABinding = (IAnnotationBinding) allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_ONCONSTRUCTIONFUNCTION));
		
		validateAgainstOnConstructionFunction(onPrerenderFunctionABinding, onConstructionFunctionABinding, errorNode, problemRequestor, compilerOptions);
		validateAgainstOnConstructionFunction(onPostrenderFunctionABinding, onConstructionFunctionABinding, errorNode, problemRequestor, compilerOptions);
	}

	private void validateAgainstOnConstructionFunction(IAnnotationBinding onPreorPostrenderFunctionABinding, IAnnotationBinding onConstructionFunctionABinding, Node errorNode, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if(onPreorPostrenderFunctionABinding != null && onConstructionFunctionABinding != null) {
			Object onPreOrPostrenderFunctionValue = onPreorPostrenderFunctionABinding.getValue();
			Object onConstructionFunctionValue = onConstructionFunctionABinding.getValue();
			
			if(onPreOrPostrenderFunctionValue instanceof IFunctionBinding && Binding.isValidBinding((IBinding) onPreOrPostrenderFunctionValue) &&
			   onConstructionFunctionValue instanceof IFunctionBinding && Binding.isValidBinding((IBinding) onConstructionFunctionValue)) {
				IFunctionBinding onPreOrPostrenderFunction = (IFunctionBinding) onPreOrPostrenderFunctionValue;
				IFunctionBinding onConstructionFunction = (IFunctionBinding) onConstructionFunctionValue;
				
				List onPreOrPostrenderFunctionParameters = onPreOrPostrenderFunction.getParameters();
				List onConstructionFunctionParameters = onConstructionFunction.getParameters();
				
				if(onPreOrPostrenderFunctionParameters.size() != 0 && onConstructionFunctionParameters.size() != 0) {
					if(onPreOrPostrenderFunctionParameters.size() != onConstructionFunctionParameters.size()) {
						problemRequestor.acceptProblem(
							errorNode,
							IProblemRequestor.ONPRERENDERFUNCTION_ONCONSTRUCTION_FUNCTION_PARAMETER_MISMATCH_NUMBER,
							new String[] {
								onPreOrPostrenderFunction.getCaseSensitiveName(),
								Integer.toString(onPreOrPostrenderFunctionParameters.size()),
								onConstructionFunction.getCaseSensitiveName(),
								Integer.toString(onConstructionFunctionParameters.size())
							});
					}
					else {
						for(int i = 0; i < onPreOrPostrenderFunctionParameters.size(); i++) {
							ITypeBinding nextOnPrerenderParm = ((IDataBinding) onPreOrPostrenderFunctionParameters.get(i)).getType(); 
							ITypeBinding nextOnConstructionParm = ((IDataBinding) onConstructionFunctionParameters.get(i)).getType();
							if(!TypeCompatibilityUtil.typesAreIdentical(nextOnPrerenderParm, nextOnConstructionParm, compilerOptions)) {
								problemRequestor.acceptProblem(
									errorNode,
									IProblemRequestor.ONPRERENDERFUNCTION_ONCONSTRUCTION_FUNCTION_PARAMETER_MISMATCH_TYPE,
									new String[] {
										Integer.toString(i+1),
										onPreOrPostrenderFunction.getCaseSensitiveName(),										
										StatementValidator.getTypeString(nextOnPrerenderParm),
										onConstructionFunction.getCaseSensitiveName(),
										StatementValidator.getTypeString(nextOnConstructionParm)
									});
							}
						}
					}
				}
			}
		}
	}
}
