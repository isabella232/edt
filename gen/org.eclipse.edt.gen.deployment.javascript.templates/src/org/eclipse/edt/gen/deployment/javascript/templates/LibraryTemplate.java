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

import org.eclipse.edt.gen.deployment.javascript.Context;
import org.eclipse.edt.gen.deployment.util.CommonUtilities;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Library;


public class LibraryTemplate extends JavaScriptTemplate {
	public void genDependentPart(Library library, Context ctx, TabbedWriter out, Boolean addComma) {
		ctx.invoke(genOutputFileName, library, ctx, out, addComma);
		ctx.invoke(genDependentParts, library, ctx, out, Boolean.TRUE);
	}
	
	public void genPropFiles(Library library, TabbedWriter out, LinkedHashSet propFiles){
		if(CommonUtilities.isRUIPropertiesLibrary(library)){
			propFiles.add(CommonUtilities.getPropertiesFile( library ));
		}
	}
	
}
