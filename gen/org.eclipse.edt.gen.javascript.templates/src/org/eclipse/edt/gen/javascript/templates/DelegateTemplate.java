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
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.Delegate;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.MemberName;

public class DelegateTemplate extends JavaScriptTemplate {

	public void preGenClassBody(Delegate part, Context ctx) {}

	public void genPart(Delegate part, Context ctx, TabbedWriter out) {}

	public void genClassBody(Delegate part, Context ctx, TabbedWriter out) {}

	public void genClassHeader(Delegate part, Context ctx, TabbedWriter out) {}

	public void genRuntimeTypeName(Delegate part, Context ctx, TabbedWriter out, TypeNameKind arg) {
		out.print("egl.eglx.lang.AnyDelegate");
	}
	
	public void genDefaultValue(Delegate part, Context ctx, TabbedWriter out) {
		out.print("null");
	}
	
	public Boolean supportsConversion(Delegate part, Context ctx) {
		return Boolean.FALSE;
	}
	
	public void genConversionOperation(Delegate part, Context ctx, TabbedWriter out, AsExpression arg) {
		Expression expr = arg.getObjectExpr();
		if(expr instanceof MemberName && ((MemberName)expr).getMember() != null &&
				((MemberName)expr).getMember() instanceof Function){
			ctx.invoke(genCallbackAccesor, expr, ctx, out, null);
		}else{
			ctx.invokeSuper(this, genConversionOperation, part, ctx, out, arg);
		}		
	}

}
