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
package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.CallStatement;

public class CallStatementTemplate extends JavaTemplate {

	public void genStatementBody(CallStatement stmt, Context ctx, TabbedWriter out) {
		out.print("ezeProgram._runUnit().getCallers().localCall(");
		ctx.invoke(genExpression, stmt.getInvocationTarget(), ctx, out);
		out.print(", new com.ibm.javart.JavartSerializable[] {");
		ctx.foreach(stmt.getArguments(), ',', genExpression, ctx, out);
		out.print("}, null, \"x\", ezeProgram )");
	}
}
