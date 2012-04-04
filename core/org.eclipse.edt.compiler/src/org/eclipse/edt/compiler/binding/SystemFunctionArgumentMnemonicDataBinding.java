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

import org.eclipse.edt.mof.egl.utils.InternUtil;

/**
 * @author Dave Murray
 */
public class SystemFunctionArgumentMnemonicDataBinding extends DataBinding {
	
	public static final SystemFunctionArgumentMnemonicDataBinding AUTOCOMMIT = new SystemFunctionArgumentMnemonicDataBinding("AUTOCOMMIT");
	public static final SystemFunctionArgumentMnemonicDataBinding AUTOMATIC = new SystemFunctionArgumentMnemonicDataBinding("AUTOMATIC");
	public static final SystemFunctionArgumentMnemonicDataBinding CONDITIONAL = new SystemFunctionArgumentMnemonicDataBinding("CONDITIONAL");
	public static final SystemFunctionArgumentMnemonicDataBinding EXPLICIT = new SystemFunctionArgumentMnemonicDataBinding("EXPLICIT");
	public static final SystemFunctionArgumentMnemonicDataBinding NOAUTOCOMMIT = new SystemFunctionArgumentMnemonicDataBinding("NOAUTOCOMMIT");
	public static final SystemFunctionArgumentMnemonicDataBinding NOCOMMIT = new SystemFunctionArgumentMnemonicDataBinding("NOCOMMIT");		
	public static final SystemFunctionArgumentMnemonicDataBinding READCOMMITTED = new SystemFunctionArgumentMnemonicDataBinding("READCOMMITTED");
	public static final SystemFunctionArgumentMnemonicDataBinding READUNCOMMITTED = new SystemFunctionArgumentMnemonicDataBinding("READUNCOMMITTED");	
	public static final SystemFunctionArgumentMnemonicDataBinding REPEATABLEREAD = new SystemFunctionArgumentMnemonicDataBinding("REPEATABLEREAD");
	public static final SystemFunctionArgumentMnemonicDataBinding SERIALIZABLETRANSACTION = new SystemFunctionArgumentMnemonicDataBinding("SERIALIZABLETRANSACTION");	
	public static final SystemFunctionArgumentMnemonicDataBinding TWOPHASE = new SystemFunctionArgumentMnemonicDataBinding("TWOPHASE");
	public static final SystemFunctionArgumentMnemonicDataBinding TYPE1 = new SystemFunctionArgumentMnemonicDataBinding("TYPE1");
	public static final SystemFunctionArgumentMnemonicDataBinding TYPE2 = new SystemFunctionArgumentMnemonicDataBinding("TYPE2");	
	
	private SystemFunctionArgumentMnemonicDataBinding(String name) {
		super(InternUtil.internCaseSensitive(name), null, null);
	}

	public int getKind() {
		return SYSTEM_FUNCTION_ARGUMENT_MNEMONIC;
	}
}
