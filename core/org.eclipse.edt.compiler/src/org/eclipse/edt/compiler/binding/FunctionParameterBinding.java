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

/**
 * @author Dave Murray
 */
public class FunctionParameterBinding extends DataBinding {
	
	private boolean isSqlNullable;
	private boolean isField;
	private boolean isInput;
	private boolean isOutput;
	private boolean isConst;
	
	private IFunctionBinding fBinding;
	
    public FunctionParameterBinding(String caseSensitiveInternedName, IPartBinding declarer, ITypeBinding typeBinding, IFunctionBinding functionBinding) {
        super(caseSensitiveInternedName, declarer, typeBinding);
        this.fBinding = functionBinding;
    }
	
	public int getKind() {
		return FUNCTION_PARAMETER_BINDING;
	}
	
	public boolean isSQLNullable() {
		return isSqlNullable;
	}
	
	public boolean isConst() {
		return isConst;
	}
	
	public void setConst(boolean b) {
		isConst = b;
	}
	
	public void setSqlNullable(boolean b) {
		isSqlNullable = b;
	}
	
	public boolean isField() {
		return isField;
	}
	
	public void setField(boolean b) {
		isField = b;
	}
	
	/**
	 * @return true if this parameter was specified with the 'in' modifier, false
	 *         otherwise
	 */
	public boolean isInput() {
		return isInput;
	}
	
	public void setInput(boolean b) {
		isInput = b;
	}
	
	/**
	 * @return true if this parameter was specified with the 'out' modifier, false
	 *         otherwise
	 */
	public boolean isOutput() {
		return isOutput;
	}
	
	public void setOutput(boolean b) {
		isOutput = b;
	}
	
	/**
	 * @return true if this parameter was specified with the 'inout' modifier
	 *         or no modifier, false otherwise
	 */
	public boolean isInputOutput() {
		return !isInput && !isOutput;
	}
	
	public IFunctionBinding getFunctionBinding() {
		return fBinding;
	}
}
