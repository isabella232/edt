/*******************************************************************************
 * Copyright © 2005, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.compiler;

import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;

/**
 * @author winghong
 */
public class Compiler extends org.eclipse.edt.compiler.internal.core.builder.Compiler{
	
	private static final Compiler INSTANCE = new Compiler();
	
	private Compiler(){}
	
	public static Compiler getInstance(){
		return INSTANCE;
	}
	
	protected void logPartBinderException(RuntimeException e) {
		EDTCoreIDEPlugin.getPlugin().log("Part Binder Failure", e);  //$NON-NLS-1$        
    }
	
	protected void logValidationException(RuntimeException e) {
		EDTCoreIDEPlugin.getPlugin().log("Part Validation Failure", e);  //$NON-NLS-1
	}
	
	public void logIRCreationException(RuntimeException e) {
		EDTCoreIDEPlugin.getPlugin().log("IR Creation Failure", e);  //$NON-NLS-1
	}
}
