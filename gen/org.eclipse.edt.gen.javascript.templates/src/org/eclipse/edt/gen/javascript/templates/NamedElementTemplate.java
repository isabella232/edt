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

import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.JavaScriptAliaser;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.NamedElement;

public class NamedElementTemplate extends JavaScriptTemplate {

	public void genAccessor(NamedElement element, Context ctx, TabbedWriter out) {
		String propertyFunction = CommonUtilities.getPropertyFunction( element, false, ctx );

		if (((ctx.getAttribute(element, Constants.EXPR_LHS) == null) || (Boolean.FALSE.equals(ctx.getAttribute(element, Constants.EXPR_LHS))))
			&& (propertyFunction != null) && !CommonUtilities.isCurrentFunction(ctx, propertyFunction, element)) {
			out.print( propertyFunction );
			out.print( "()" );
		}
		else {			
			genName(element, ctx, out);
			//When the LHS memeber field is processed
			if ((ctx.getAttribute(element, Constants.EXPR_LHS) != null) && (Boolean.TRUE.equals(ctx.getAttribute(element, Constants.EXPR_LHS)))){
				ctx.putAttribute(element, Constants.EXPR_LHS, false);
			}
		}
	}

	public void genName(NamedElement element, Context ctx, TabbedWriter out) {
		if(Boolean.TRUE.equals(ctx.getAttribute(element, Constants.SubKey_isInList)) && element.getCaseSensitiveName().equalsIgnoreCase("sort")){
			out.print("ezekw$$sort");
		}else{
			out.print(JavaScriptAliaser.getAlias(element.getCaseSensitiveName()));
		}		
	}

	public void genQualifier(NamedElement element, Context ctx, TabbedWriter out) {}
}
