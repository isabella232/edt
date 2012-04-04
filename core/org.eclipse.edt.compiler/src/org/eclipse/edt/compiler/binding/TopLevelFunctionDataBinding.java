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
package org.eclipse.edt.compiler.binding;

import java.util.Iterator;

public class TopLevelFunctionDataBinding extends DataBinding {
	
	public TopLevelFunctionDataBinding(String caseSensitiveInternedName, IPartBinding declarer, IFunctionBinding functionBinding) {
		super(caseSensitiveInternedName, declarer, functionBinding);
	}

	public int getKind() {
		return TOP_LEVEL_FUNCTION_BINDING;
	}

	public IDataBinding getFunctionWithItemsNullableSignature(boolean I4GLItemsNullableEnabled) {
		IDataBinding result = null;
		if(I4GLItemsNullableEnabled) {
			IFunctionBinding myFunction = (IFunctionBinding) getType();
			TopLevelFunctionBinding functionBinding = new TopLevelFunctionBinding(myFunction.getPackageName(), myFunction.getName());
			
			ITypeBinding returnType = myFunction.getReturnType();
			if(returnType != null && ITypeBinding.PRIMITIVE_TYPE_BINDING == returnType.getBaseType().getKind()) {
				functionBinding.setReturnType(returnType.getNullableInstance());
			}
			else {
				functionBinding.setReturnType(returnType);
			}
			
			for(Iterator iter = myFunction.getParameters().iterator(); iter.hasNext();) {
				FunctionParameterBinding parmBinding = (FunctionParameterBinding) iter.next();
				FunctionParameterBinding newParmBinding = new FunctionParameterBinding(parmBinding.getName(), parmBinding.getDeclaringPart(), parmBinding.getType(), myFunction);
				
				if(parmBinding.isInput()) {
					newParmBinding.setInput(true);
				}
				if(parmBinding.isOutput()) {
					newParmBinding.setOutput(true);
				}
				if(parmBinding.isSQLNullable()) {
					newParmBinding.setSqlNullable(true);
				}
				if(parmBinding.isField()) {
					newParmBinding.setField(true);
				}
				
				newParmBinding.setConst(parmBinding.isConst());

				ITypeBinding type = newParmBinding.getType();
				if(type != null && ITypeBinding.PRIMITIVE_TYPE_BINDING == type.getBaseType().getKind()) {
					newParmBinding.setType(type.getNullableInstance());
				}
				
				functionBinding.addParameter(newParmBinding);
			}
			
			result = new TopLevelFunctionDataBinding(getName(), functionBinding, functionBinding);
		}
		else {
			result = this;
		}
		return result;
	}
	
	public boolean isStaticPartDataBinding() {
		return true;
	}
}
