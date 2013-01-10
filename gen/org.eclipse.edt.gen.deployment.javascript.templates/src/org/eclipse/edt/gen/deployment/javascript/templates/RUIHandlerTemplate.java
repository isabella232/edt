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
package org.eclipse.edt.gen.deployment.javascript.templates;

import java.util.HashMap;
import java.util.List;

import org.eclipse.edt.gen.deployment.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Handler;

public class RUIHandlerTemplate extends RUITemplate {
	
	public void genDeploymentHTML(Handler handler, Context ctx, TabbedWriter out, List egldds, HashMap eglParameters, String userMsgLocale, String runtimeMsgLocale) {		
		genHTML(handler, ctx, out, egldds, eglParameters, userMsgLocale, runtimeMsgLocale);
	}
	
	private void genHTML(Handler handler, Context ctx, TabbedWriter out, List egldds, HashMap eglParameters, String userMsgLocale, String runtimeMsgLocale){
		genHTML(false, handler, ctx, out, egldds, eglParameters, userMsgLocale, runtimeMsgLocale, false, false, false);
	}
	
}
