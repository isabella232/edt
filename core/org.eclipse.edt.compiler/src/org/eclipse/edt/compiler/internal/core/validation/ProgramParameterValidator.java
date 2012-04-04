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
package org.eclipse.edt.compiler.internal.core.validation;

import java.util.Iterator;

import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;


public class ProgramParameterValidator {

	private IProblemRequestor problemRequestor;

	public ProgramParameterValidator(IProblemRequestor problemRequestor) {
		this.problemRequestor = problemRequestor;
	}

	public void validate(ITypeBinding tBinding, Node nodeForErrors) {
		if(!typeIsValid(tBinding)) {
			problemRequestor.acceptProblem(nodeForErrors,
				IProblemRequestor.PROGRAM_PARAMETER_HAS_INCORRECT_TYPE,
				new String[]{StatementValidator.getTypeString(tBinding)});
		}
	}

	private boolean typeIsValid(ITypeBinding tBinding) {
		if(ITypeBinding.PRIMITIVE_TYPE_BINDING == tBinding.getKind()) {
			switch(((PrimitiveTypeBinding) tBinding).getPrimitive().getType()) {
			case Primitive.BLOB_PRIMITIVE:
			case Primitive.CLOB_PRIMITIVE:
			case Primitive.ANY_PRIMITIVE:
				return false;
			}
		}
		else if(ITypeBinding.ARRAY_TYPE_BINDING == tBinding.getKind()) {
			return typeIsValid(((ArrayTypeBinding) tBinding).getElementType());
		}
		else if(ITypeBinding.FLEXIBLE_RECORD_BINDING == tBinding.getKind()) {		
			for(Iterator iter = ((FlexibleRecordBinding) tBinding).getDeclaredFields().iterator(); iter.hasNext();) {
				IDataBinding dBinding = (IDataBinding) iter.next();
				if(dBinding.getType() != null && !typeIsValid(dBinding.getType())) {
					return false;
				}
			}
		}
		else if(ITypeBinding.FIXED_RECORD_BINDING != tBinding.getKind() &&
				ITypeBinding.FORM_BINDING != tBinding.getKind()) {
			return false;
		}
		
		if(tBinding.isReference()) {
			return false;					
		}
		else if (tBinding.isNullable()) {
			return false;
		}
		else if(tBinding.isDynamic()) {
			return false;
		}
		
		return true;
	}
}
