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

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Dave Murray
 */
public class NilBinding extends TypeBinding {
	
	public static NilBinding INSTANCE = new NilBinding();
	
	private NilBinding() {
		super(InternUtil.internCaseSensitive(IEGLConstants.KEYWORD_NULL));
	}
	
	public int getKind() {
		return NIL_BINDING;
	}

	public ITypeBinding getBaseType() {
		return this;
	}
	
	public boolean isReference() {
		return true;
	}

	@Override
	public ITypeBinding primGetNullableInstance() {
		NilBinding nullable = new NilBinding();
		nullable.setNullable(true);
		return nullable;
	}
}
