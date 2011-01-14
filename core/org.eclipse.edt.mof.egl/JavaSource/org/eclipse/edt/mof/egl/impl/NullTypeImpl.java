/*******************************************************************************
 * Copyright © 2010 IBM Corporation and others.
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
import org.eclipse.edt.mof.egl.NullType;
import org.eclipse.edt.mof.egl.Type;

public class NullTypeImpl extends TypeImpl implements NullType {

	@Override
	public Boolean equals(Type eglType) {
		return this == eglType;
	}

	@Override
	public Classifier getClassifier() {
		return null;
	}

	@Override
	public String getTypeSignature() {
		return null;
	}
}