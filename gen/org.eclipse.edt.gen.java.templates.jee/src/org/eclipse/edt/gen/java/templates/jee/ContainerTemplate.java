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
package org.eclipse.edt.gen.java.templates.jee;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.jee.Constants;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.Member;

public class ContainerTemplate extends org.eclipse.edt.gen.java.templates.ContainerTemplate implements Constants {

	public void genAnnotations(Container object, Context ctx, TabbedWriter out, Member member) {}

	public void genXmlTransient(Container object, Context ctx, TabbedWriter out) {}
}
