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
package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.*;

public class DeclarationExpressionTemplate extends JavaTemplate {

	public void genDeclarationExpression(DeclarationExpression expr, Context ctx, TabbedWriter out) {
		for (Field field : expr.getFields()) {
			// write out the debug extension data
			CommonUtilities.generateSmapExtension(field, ctx);
			// process the field
			ctx.invoke(genRuntimeTypeName, field, ctx, out, TypeNameKind.JavaPrimitive);
			out.print(" ");
			ctx.invoke(genName, field, ctx, out);
			out.print(" = ");
			ctx.invoke(genInitialization, field, ctx, out);
			out.println(";");
			// as this is an expression that also creates a new line with the above println method, it throws off the
			// smap ending line number by 1. We need to issue a call to correct this
			ctx.setSmapLastJavaLineNumber(out.getLineNumber() - 1);
			// if the initializer statements are not against the currect field, then we need to do the initialization in
			// addition to the statements
			if (field.getInitializerStatements() != null
				&& field.getInitializerStatements().getStatements().size() == 1
				&& field.getInitializerStatements().getStatements().get(0) instanceof AssignmentStatement
				&& ((AssignmentStatement) field.getInitializerStatements().getStatements().get(0)).getAssignment().getLHS() instanceof MemberName
				&& ((MemberName) ((AssignmentStatement) field.getInitializerStatements().getStatements().get(0)).getAssignment().getLHS())
					.getMember().equals(field)) {
				// we need to run the temporary variables separately, otherwise we might not get wraps
				Expression rhs = ((AssignmentStatement) field.getInitializerStatements().getStatements().get(0)).getAssignment().getRHS();
				// Skip initialization when the initializer is going to create a new value anyway.
				if ( !( rhs instanceof NewExpression && rhs.getType().equals( field.getType() ) ) )
				{
					ctx.invoke(genInitializeStatement, field, ctx, out, true);
				}
			} else {
				// now check for any statements to be processed
				if (field.getInitializerStatements() != null)
					ctx.invoke(genStatementNoBraces, field.getInitializerStatements(), ctx, out);
			}
		}
	}
}
