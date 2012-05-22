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
package org.eclipse.edt.mof.egl.impl;

import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.InternUtil;


public abstract class TypeImpl extends ElementImpl implements Type {
	
	@Override
	public abstract Classifier getClassifier();
	
	@Override
	public abstract Boolean equals(Type eglType);
	
	@Override
	public abstract String getTypeSignature();
	
	@Override
	public String getMofSerializationKey() {
		return InternUtil.intern(EGL_KeyScheme + KeySchemeDelimiter + getTypeSignature().toUpperCase().toLowerCase());
	}
	
	@Override
	public boolean isNativeType() {
		// Default implementation
		return true;
	}
}
