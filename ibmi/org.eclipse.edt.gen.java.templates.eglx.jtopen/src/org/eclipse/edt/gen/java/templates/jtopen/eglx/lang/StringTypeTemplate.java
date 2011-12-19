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
package org.eclipse.edt.gen.java.templates.jtopen.eglx.lang;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.eglx.jtopen.Constants;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Type;

public class StringTypeTemplate extends org.eclipse.edt.gen.java.templates.eglx.lang.StringTypeTemplate  implements Constants{
	public void genLength(Type type, Context ctx, TabbedWriter out, Annotation typeAnnot) {
		if(typeAnnot != null){
			out.print(((Integer)typeAnnot.getValue(subKey_length)).toString());
		}
	}
}