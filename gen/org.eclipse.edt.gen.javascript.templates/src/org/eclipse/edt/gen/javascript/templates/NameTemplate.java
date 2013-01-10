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
			// First, is this a reference within the setter's definition?
			if (CommonUtilities.isCurrentFunction(ctx, propertyFunction, expr.getNamedElement())) {
				genAsAssignment = false;
				if (expr.getQualifier() != null) {
					ctx.invoke(genExpression, expr.getQualifier(), ctx, out, expr.getQualifier());
					out.print('.');
				}
				else {
					ctx.invoke(genQualifier, expr.getNamedElement(), ctx, out);
				}
				ctx.invoke(genName, expr.getNamedElement(), ctx, out);
				out.print(arg2);
				ctx.invoke(genExpression, arg1, ctx, out);
			} 
			else {
				// Second, are we referencing a setting from within the declaring part?
				if (!isFunctionDeclaredinCurrentPart(ctx, (Field)expr.getNamedElement(), propertyFunction)) {
					genAsAssignment = false;
					
					if ( expr.getQualifier() != null )
					{
						ctx.invoke( genExpression, expr.getQualifier(), ctx, out, expr.getQualifier() );
						out.print( '.' );
					}
					else {
						ctx.invoke(genQualifier, expr.getNamedElement(), ctx, out);
					}
					out.print( propertyFunction );
					//When the LHS memeber field is just processed
					if ((ctx.getAttribute(expr.getNamedElement(), Constants.EXPR_LHS) != null) && (Boolean.TRUE.equals(ctx.getAttribute(expr.getNamedElement(), Constants.EXPR_LHS)))){
						ctx.putAttribute(expr.getNamedElement(), Constants.EXPR_LHS, false);
					}
					out.print( '(' );
					ctx.invoke(genTypeBasedExpression, arg1, ctx, out, expr.getType());
					out.print( ')' );					
				}
			}
		}

		// Default case:  gen the assignment....
		if (genAsAssignment)
		{
			ctx.invoke( genAssignment, expr.getType(), ctx, out, expr, arg1, arg2 );
		}
	}
	
	
	private static boolean isFunctionDeclaredinCurrentPart(Context ctx, Field field, String propertyFunction){
		boolean result = false;
		
		Part currentPart = (Part) ctx.getAttribute(ctx.getClass(), Constants.SubKey_partBeingGenerated);
		Container nameCnr = getCnrForFunction(ctx, field, propertyFunction);
		
		result = (nameCnr != null) && (nameCnr instanceof Part)
			  && (((Part) nameCnr).getFullyQualifiedName().equals(currentPart.getFullyQualifiedName()));
		
		return result;
	}
	
	
	private static Container getCnrForFunction(Context ctx, Field field, String propertyFunction){
		Container result = null;
		
		QualifiedFunctionInvocation qfi = CommonUtilities.getFunctionInvocation(ctx, field, propertyFunction, true);
		if (qfi != null) {
			try {
				result = qfi.getTarget().getContainer();
			}
			catch (Exception e){
				result = null;
			}
		}
		
		return result;
	}
}
