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
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.QualifiedFunctionInvocation;

public class NameTemplate extends JavaScriptTemplate {

	public void genAssignment(Name expr, Context ctx, TabbedWriter out, Expression arg1, String arg2) {
		String propertyFunction = CommonUtilities.getPropertyFunction(
			expr.getNamedElement(), true, ctx );
		
		boolean genAsAssignment = true;
		if ( propertyFunction != null )
		{
			Part currentPart = (Part) ctx.getAttribute(ctx.getClass(), Constants.SubKey_partBeingGenerated);
			QualifiedFunctionInvocation qfi = CommonUtilities.getFunctionInvocation(ctx, (Field)expr.getNamedElement(), propertyFunction, true);
			Container nameCnr = null;
			if (qfi != null) {
				try {
					nameCnr = qfi.getTarget().getContainer();
				}
				catch (Exception e){
					nameCnr = null;
				}
			}
			
			boolean nameContainedInPart = (nameCnr != null) && (nameCnr instanceof Part)
										  && (((Part) nameCnr).getFullyQualifiedName().equals(currentPart.getFullyQualifiedName()));

			if ((propertyFunction != null) && (nameCnr != null) && (!nameContainedInPart)) {
				genAsAssignment = false;
				
				if ( expr.getQualifier() != null )
				{
					ctx.invoke( genExpression, expr.getQualifier(), ctx, out );
					out.print( '.' );
				}
				else {
					ctx.invoke(genQualifier, expr.getNamedElement(), ctx, out);
				}
				out.print( propertyFunction );
				out.print( '(' );
				ctx.invoke( genExpression, arg1, ctx, out );
				out.print( ')' );
			}
		}


		if (genAsAssignment)
		{
			ctx.invoke( genAssignment, expr.getType(), ctx, out, expr, arg1, arg2 );
		}
	}
}
