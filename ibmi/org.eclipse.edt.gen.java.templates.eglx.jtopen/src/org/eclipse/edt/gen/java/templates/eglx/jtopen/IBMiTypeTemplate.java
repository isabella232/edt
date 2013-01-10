/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.java.templates.eglx.jtopen;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.egl.Type;

public class IBMiTypeTemplate extends JavaTemplate {
	public void preGen(Type type, Context ctx) {
		// Make sure IBMi runtime container is added to the build path.
		ctx.requireRuntimeContainer(Constants.IBMI_RUNTIME_CONTAINER_ID);
		
		// process anything else the superclass needs to do
		ctx.invokeSuper(this, preGen, type, ctx);
	}
}
