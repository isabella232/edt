/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Type;

public class FieldTemplate extends org.eclipse.edt.gen.java.templates.FieldTemplate implements org.eclipse.edt.gen.java.templates.eglx.jtopen.Constants {

	public void preGen(Field field, Context ctx) {
		super.preGen(field, ctx);
		if (field.getContainer() instanceof Type) {
			ctx.invoke(preGenAS400Annotation, field.getType(), ctx, field);
		}
	}

}
