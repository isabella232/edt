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
package org.eclipse.edt.compiler.internal.core.validation.statement;

import java.util.Iterator;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.FieldAccessValidationAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.IPartSubTypeAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.VariableBinding;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;


public class LValueValidator {
	private IProblemRequestor problemRequestor;
	private ICompilerOptions compilerOptions;
	private IDataBinding dBinding;
	private Expression lValue;
	private ILValueValidationRules validationRules;
	
	public static interface ILValueValidationRules {
		boolean canAssignToConstantVariables();
		boolean canAssignToReadOnlyVariables();
		boolean canAssignToFunctionReferences();
		boolean canAssignToPCB();
		boolean canAssignToFunctionParmConst();
		boolean shouldRunAccessRules();
		
	}
	
	public static class DefaultLValueValidationRules implements ILValueValidationRules {
		public boolean canAssignToPCB() {
			return false;
		}
		public boolean canAssignToConstantVariables() {
			return false;
		}
		
		public boolean canAssignToReadOnlyVariables() {
			return false;
		}
		
		public boolean canAssignToFunctionReferences() {
			return false;
		}
		
		public boolean canAssignToFunctionParmConst() {
			return false;
		}

		public boolean shouldRunAccessRules() {
			return true;
		}

	}
	
	public LValueValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions, IDataBinding dBinding, Expression lValue) {
		this(problemRequestor, compilerOptions, dBinding, lValue, new DefaultLValueValidationRules());
	}
	
	public LValueValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions, IDataBinding dBinding, Expression lValue, ILValueValidationRules validationRules) {
		this.problemRequestor = problemRequestor;
		this.dBinding = dBinding;
		this.lValue = lValue;
		this.validationRules = validationRules;
		this.compilerOptions = compilerOptions;
	}
	
	private boolean invokeFieldAccessValidators() {
		boolean result = true;

		if (!Binding.isValidBinding(dBinding)) {
			return result;
		}
		Iterator i = dBinding.getAnnotations().iterator();
		while (i.hasNext()) {
			IAnnotationBinding ann = (IAnnotationBinding)i.next();
			if (Binding.isValidBinding(ann) && Binding.isValidBinding(ann.getType()) && ann.getType() instanceof IAnnotationTypeBinding) {
				IAnnotationTypeBinding annType = (IAnnotationTypeBinding)ann.getType();
				IAnnotationTypeBinding validationProxy = annType.getValidationProxy();
				if(validationProxy != null) {
					for(Iterator iter = validationProxy.getFieldAccessAnnotations().iterator(); iter.hasNext();) {
						result = ((FieldAccessValidationAnnotationTypeBinding) iter.next()).validateLValue(lValue, dBinding, problemRequestor, compilerOptions) && result;
					}
				}
			}
		}
		return result;
	}
	
	public boolean validate() {
		boolean result = true;
		
		if (validationRules.shouldRunAccessRules()) {
			//Run field access rules defined by annotations on the field
			result = invokeFieldAccessValidators();
		}
		
		if (!validationRules.canAssignToPCB()) {
			// This could probably be another "field access" check for subtype
			// DLIRecord
			IAnnotationBinding aBinding = dBinding.getAnnotation(new String[] { "egl", "io", "dli" }, "PCB");
			if (aBinding != null) {
				problemRequestor.acceptProblem(lValue, IProblemRequestor.DLI_PCBRECORD_NOT_VALID_AS_LVALUE, new String[] { lValue
						.getCanonicalString() });
			}
		}
		
		if (dBinding.getKind() == IDataBinding.FUNCTION_PARAMETER_BINDING) {
			if(!validationRules.canAssignToFunctionParmConst() && ((FunctionParameterBinding)dBinding).isConst()) {
				boolean settingValueOfConstantArrayElement = false;
				ITypeBinding dBindingType = dBinding.getType();
				ITypeBinding exprType = lValue.resolveTypeBinding();
				if(dBindingType != null && exprType != null) {
					settingValueOfConstantArrayElement = ITypeBinding.ARRAY_TYPE_BINDING == dBindingType.getKind() && exprType != dBindingType;
				}
				
				if(!settingValueOfConstantArrayElement) {
					problemRequestor.acceptProblem(
						lValue,
						IProblemRequestor.CANNOT_MODIFY_CONSTANT,
						new String[] {dBinding.getCaseSensitiveName()});
					result = false;
				}
			}
		}
		
		if(dBinding.getKind() == IDataBinding.CLASS_FIELD_BINDING ||
		   dBinding.getKind() == IDataBinding.LOCAL_VARIABLE_BINDING) {
			if(!validationRules.canAssignToConstantVariables() && ((VariableBinding) dBinding).isConstant()) {
				boolean settingValueOfConstantArrayElement = false;
				ITypeBinding dBindingType = dBinding.getType();
				ITypeBinding exprType = lValue.resolveTypeBinding();
				if(dBindingType != null && exprType != null) {
					settingValueOfConstantArrayElement = ITypeBinding.ARRAY_TYPE_BINDING == dBindingType.getKind() && exprType != dBindingType;
				}
				
				if(!settingValueOfConstantArrayElement) {
					problemRequestor.acceptProblem(
						lValue,
						IProblemRequestor.CANNOT_MODIFY_CONSTANT,
						new String[] {dBinding.getCaseSensitiveName()});
					result = false;
				}
			}
			
			if(!validationRules.canAssignToReadOnlyVariables() && ((VariableBinding)dBinding).isReadOnly()){
				problemRequestor.acceptProblem(
					lValue,
					IProblemRequestor.READONLY_FIELD_CANNOT_BE_ASSIGNED_TO,
					new String[] {dBinding.getCaseSensitiveName()});
				result = false;
			}
		}
		
		if(dBinding.getKind() == IDataBinding.NESTED_FUNCTION_BINDING ||
		   dBinding.getKind() == IDataBinding.TOP_LEVEL_FUNCTION_BINDING) {
			if(!validationRules.canAssignToFunctionReferences()) {
				problemRequestor.acceptProblem(
					lValue,
					IProblemRequestor.FUNCTION_NOT_VALID_AS_LVALUE,
					new String[] {dBinding.getCaseSensitiveName()});
				result = false;
			}
		}
		
		return result;
	}
}
