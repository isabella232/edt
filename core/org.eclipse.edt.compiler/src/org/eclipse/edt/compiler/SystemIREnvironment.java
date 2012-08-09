/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler;

import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.lookup.EglLookupDelegate;
import org.eclipse.edt.mof.impl.Bootstrap;
import org.eclipse.edt.mof.serialization.Environment;

public class SystemIREnvironment extends Environment {

	
	public SystemIREnvironment() {
		super();
		Bootstrap.initialize(this);
	}

	@Override
	public void reset() {
		super.reset();
		registerLookupDelegate(Type.EGL_KeyScheme, new EglLookupDelegate());
	}
		
}
