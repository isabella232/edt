/*******************************************************************************
 * Copyright © 2005, 2010 IBM Corporation and others.
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

import org.eclipse.edt.compiler.internal.core.utils.InternUtil;

/**
 * @author Dave Murray
 */
public class AmbiguousFunctionBinding extends FunctionBinding {
	private static AmbiguousFunctionBinding INSTANCE = new AmbiguousFunctionBinding();
	
	private AmbiguousFunctionBinding() {
		super(InternUtil.internCaseSensitive(""), null);
	}
	
	public static AmbiguousFunctionBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isValidBinding() {
		return false;
	}
}
