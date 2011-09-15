/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.edt.gen.deployment.javascript.templates;

import java.util.LinkedHashSet;

import org.eclipse.edt.gen.deployment.javascript.Context;
import org.eclipse.edt.mof.egl.Handler;

public class HandlerTemplate extends JavaScriptTemplate {	
	public void genDependentPart(Handler handler, Context ctx, LinkedHashSet dependentFiles) {
		ctx.invoke(genOutputFileName, handler, ctx, dependentFiles);
		ctx.invoke(genDependentParts, handler, ctx, dependentFiles);
	}
}
