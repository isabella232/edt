/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.eunit.templates;

import org.eclipse.edt.gen.eunit.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.EGLClass;

public class EGLClassTemplate extends EUnitTemplate {

	public void genClassBody(EGLClass part, Context ctx, TabbedWriter out) {}

	public void genClassHeader(EGLClass part, Context ctx, TabbedWriter out) {}
}
