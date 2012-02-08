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
package org.eclipse.edt.gen.javascriptdev.templates;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascriptdev.Constants;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.PartName;

public class MemberAccessTemplate extends org.eclipse.edt.gen.javascript.templates.MemberAccessTemplate {
	

	public void genSetLocalFunctionVariable(MemberAccess access, Context ctx, TabbedWriter out) {
		// No need to update - it's still the same object
	}
	
	@Override
	public void genMemberAccess(MemberAccess expr, Context ctx, TabbedWriter out) {
		super.genMemberAccess(expr, ctx, out);
		Object veEdit = ctx.getParameter(Constants.PARAMETER_VE_ENABLE_EDITING);
		if (veEdit != null && veEdit == Boolean.TRUE && CommonUtilities.isRUIWidget(expr.getMember().getType()) && expr.getQualifier() != null && 
				((expr.getQualifier() instanceof MemberName && !(((MemberName)expr.getQualifier()).getMember() instanceof FunctionParameter)) ||
						((expr.getQualifier() instanceof PartName) && ((PartName)expr.getQualifier()).getPart() instanceof Library))){
			List references = (List)ctx.get( Constants.REFERENCES_WIDGETS );
			if ( references == null ) {
				references = new ArrayList();
				ctx.put( Constants.REFERENCES_WIDGETS, references );
			}
			references.add( expr );
		}
	}
	
	public void genReferencedWidgets(MemberAccess expr, Context ctx, TabbedWriter out) {
		String idName = null;
		if ( expr.getQualifier() instanceof MemberName ) {
			idName = ((MemberName)expr.getQualifier()).getId();
		} else if ( expr.getQualifier() instanceof PartName ) {
			idName = ((PartName)expr.getQualifier()).getId();
		}
		if ( idName != null ) {
			out.print("(function(x){ if ( x !== null && x !== undefined ) egl.setWidgetMoveable( x.");
			ctx.invoke(genAccessor, expr.getMember(), ctx, out);
			out.print(", \"");
			out.print( idName );
			out.print( ".");
			ctx.invoke(genAccessor, expr.getMember(), ctx, out);
			out.print( "\" );}( ");
			ctx.invoke(genExpression, expr.getQualifier(), ctx, out);
			out.println(" )); ");
		}
	}
}
