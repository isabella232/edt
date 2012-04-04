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
import org.eclipse.edt.compiler.internal.core.lookup.SystemEnvironmentPackageNames;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Dave Murray
 */
public class ArrayDictionaryBinding extends PartBinding {
	
	public static ArrayDictionaryBinding INSTANCE = new ArrayDictionaryBinding();
	
	private ArrayDictionaryBinding() {
		super(InternUtil.intern(SystemEnvironmentPackageNames.EGLX_LANG), InternUtil.internCaseSensitive(IEGLConstants.MIXED_ARRAYDICTIONARY_STRING));
	}
	
	private ArrayDictionaryBinding(ArrayDictionaryBinding old) {
		super(old);
	}

	public int getKind() {
		return ARRAYDICTIONARY_BINDING;
	}
	
	public boolean isDynamicallyAccessible() {
		return true;
	}

	public void clear() {
		super.clear();
	}
	
	public boolean isValid() {
		return true;
	}
	
	public boolean isStructurallyEqual(IPartBinding anotherPartBinding) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
	
	public boolean isDeclarablePart() {
		return true;
	}
	
	public boolean isReference() {
		return true;
	}
	
	public boolean isSystemPart() {
		return true;
	}

	@Override
	public ITypeBinding primGetNullableInstance() {
		ArrayDictionaryBinding nullable = new ArrayDictionaryBinding(this);
		nullable.setNullable(true);
		return nullable;
	}
}
