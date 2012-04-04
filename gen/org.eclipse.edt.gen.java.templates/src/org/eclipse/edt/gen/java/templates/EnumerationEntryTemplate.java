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
package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.javart.util.JavaAliaser;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.EnumerationEntry;

public class EnumerationEntryTemplate extends JavaTemplate {

	public void genName(EnumerationEntry element, Context ctx, TabbedWriter out) {
		out.print(JavaAliaser.getAlias(element.getName()));
	}
}
