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
package org.eclipse.edt.gen.deployment.javascript.templates;

import java.util.LinkedHashSet;

import org.eclipse.edt.gen.deployment.util.CommonUtilities;
import org.eclipse.edt.gen.javascript.JavaScriptAliaser;
import org.eclipse.edt.mof.egl.Part;

public class EGLClassTemplate extends JavaScriptTemplate {
	public void genOutputFileName(Part part, LinkedHashSet<String> dependentFiles) {
		StringBuilder buf = new StringBuilder(50);
		
		buf.append("\"");
		String pkg = part.getPackageName();
		if (pkg.length() > 0) {
			buf.append(JavaScriptAliaser.packageNameAlias(pkg.split("[.]"), '/'));
			buf.append('/');
		}
		buf.append(JavaScriptAliaser.getAlias(part.getId()));
		buf.append(".js\"");
		CommonUtilities.addToDependentList(dependentFiles, buf.toString());
	}

}
