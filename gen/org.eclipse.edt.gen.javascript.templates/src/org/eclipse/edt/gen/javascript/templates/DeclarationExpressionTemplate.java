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

import org.eclipse.edt.gen.CommonUtilities;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AssignmentStatement;
import org.eclipse.edt.mof.egl.DeclarationExpression;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.NewExpression;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class DeclarationExpressionTemplate extends JavaScriptTemplate {

	public void genDeclarationExpression(DeclarationExpression expr, Context ctx, TabbedWriter out) {
		for (Field field : expr.getFields()) {
			for (Annotation annot : CommonUtilities.getAnnotations(field, ctx)){
				ctx.invoke(preGen, annot.getEClass(), ctx, annot, field);
			}
			ctx.put( "generating declaration of " + field + field.hashCode(), Boolean.TRUE );
			genFieldDeclaration(expr, ctx, out, field);
			if (field.getInitializerStatements() != null) {
				genInitializerStatements(field, ctx, out);
			}
			ctx.remove( "generating declaration of " + field + field.hashCode() );
		}
	}
	
	public void genFieldDeclaration(DeclarationExpression expr, Context ctx, TabbedWriter out, Field field) {
		out.print("var ");
		ctx.invoke(genName, field, ctx, out);

		if (field.getInitializerStatements() != null && field.getInitializerStatements().getStatements().size() == 1
				&& field.getInitializerStatements().getStatements().get(0) instanceof AssignmentStatement
				&& ((AssignmentStatement) field.getInitializerStatements().getStatements().get(0)).getAssignment().getLHS() instanceof MemberName
				&& ((MemberName) ((AssignmentStatement) field.getInitializerStatements().getStatements().get(0)).getAssignment().getLHS()).getMember().equals(field)) {
			if (TypeUtils.isReferenceType(expr.getType()) || ctx.mapsToPrimitiveType(expr.getType())) {
				out.println(";");
			} else {
				out.print(" = ");
				// we need to run the temporary variables separately,
				// otherwise we might not get wraps
				Expression rhs = ((AssignmentStatement) field.getInitializerStatements().getStatements().get(0)).getAssignment().getRHS();
				// avoid initialization when the initializer is going to
				// create a new value
				if (rhs instanceof NewExpression && rhs.getType().equals(field.getType())) {
					ctx.invoke(genExpression, rhs, ctx, out);
					out.println(";");
				} else {
					out.print("null");
					out.println(";");

				}
			}
		} else {
			out.print(" = ");
			// this logic will not combine, because it isn't safe to
			ctx.invoke(genInitialization, field, ctx, out);
			out.println(";");
			// now check for any statements to be processed

		}
	}
	
	public void genInitializerStatements(Field field, Context ctx, TabbedWriter out){
		ctx.invoke(genStatementNoBraces, field.getInitializerStatements(), ctx, out);
	}
}
