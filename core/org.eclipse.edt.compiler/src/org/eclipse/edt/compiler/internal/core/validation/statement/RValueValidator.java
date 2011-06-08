/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.core.validation.statement;

import java.util.Iterator;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.FieldAccessValidationAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.IPartSubTypeAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;


public class RValueValidator {
	private IProblemRequestor problemRequestor;
	private ICompilerOptions compilerOptions;
	private IDataBinding dBinding;
	private Expression nodeForErrors;
	
	public RValueValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions, IDataBinding dBinding, Expression nodeForErrors) {
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
		this.dBinding = dBinding;
		this.nodeForErrors = nodeForErrors;
	}
	
	public boolean validate() {
		boolean result = true;
		
		if(!checkFunctionDelegation()) {
			result = false;
		}
		
		if(!checkFieldAccessRulesFromSubtype()) {
			result = false;
		}
		
		return result;
	}

	private boolean checkFieldAccessRulesFromSubtype() {
		boolean result = true;
		
		//Run field access rules defined by subtype of part that field belongs to
		IPartBinding declaringPart = dBinding.getDeclaringPart();
		if(Binding.isValidBinding(declaringPart)) {
			IPartSubTypeAnnotationTypeBinding declaringSubtype = declaringPart.getSubType();
			if(Binding.isValidBinding(declaringSubtype)) {
				IAnnotationTypeBinding validationProxy = declaringSubtype.getValidationProxy();
				if(validationProxy != null) {
					for(Iterator iter = validationProxy.getFieldAccessAnnotations().iterator(); iter.hasNext();) {
						result = ((FieldAccessValidationAnnotationTypeBinding) iter.next()).validateRValue(nodeForErrors, dBinding, problemRequestor, compilerOptions) && result;
					}
				}
			}
		}
		
		return result;
	}

	private boolean checkFunctionDelegation() {
		boolean result = true;
		
		if(IDataBinding.NESTED_FUNCTION_BINDING == dBinding.getKind() ||
		   IDataBinding.TOP_LEVEL_FUNCTION_BINDING == dBinding.getKind()) {
			if(((IFunctionBinding) dBinding.getType()).hasConverse()) {
				problemRequestor.acceptProblem(
					nodeForErrors,
					IProblemRequestor.FUNCTION_WITH_CONVERSE_CANNOT_BE_DELEGATED,
					new String[] {
						dBinding.getCaseSensitiveName()	
					});
				result = false;
			}
		}
		
		if(IDataBinding.NESTED_FUNCTION_BINDING == dBinding.getKind()) {
			if(dBinding.getDeclaringPart().isSystemPart()) {
				problemRequestor.acceptProblem(
					nodeForErrors,
					IProblemRequestor.SYSTEM_FUNCTION_CANNOT_BE_DELEGATED,
					new String[] {
						dBinding.getCaseSensitiveName()	
					});
				result = false;
			}
			
			if(ITypeBinding.PROGRAM_BINDING == dBinding.getDeclaringPart().getKind() &&
			   InternUtil.intern("main") == dBinding.getName()) {
				problemRequestor.acceptProblem(
					nodeForErrors,
					IProblemRequestor.MAIN_FUNCTION_CANNOT_BE_ASSIGNED_TO_DELEGATE,
					new String[] {
						dBinding.getCaseSensitiveName()	
					});
				result = false;
			}
		}
		
		return result;
	}
}
