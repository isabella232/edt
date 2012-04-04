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
public class AmbiguousDataBinding extends DataBinding {
	
	private static final AmbiguousDataBinding INSTANCE = new AmbiguousDataBinding();

	protected AmbiguousDataBinding() {
		super(InternUtil.internCaseSensitive(""), null, null);
	}	

	public int getKind() {
		return AMBIGUOUS_BINDING;
	}
	
	public static AmbiguousDataBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isValidBinding() {
		return false;
	}
}
