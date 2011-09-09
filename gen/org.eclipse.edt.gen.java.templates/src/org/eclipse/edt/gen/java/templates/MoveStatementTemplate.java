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

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.*;

public class MoveStatementTemplate extends JavaTemplate 
{
	public void genStatementBody( MoveStatement stmt, Context ctx, TabbedWriter out )
	{
		Expression source = stmt.getSourceExpr();
		LHSExpr target = stmt.getTargetExpr();
		Expression modifier = stmt.getModifierExpr();
		
		if ( modifier == null )
		{
			String tempName = ctx.nextTempName();
			ctx.invoke( genRuntimeTypeName, source.getType(), ctx, out );
			out.print( ' ' );
			out.print( tempName );
			out.print( " = (" );
			ctx.invoke( genRuntimeTypeName, source.getType(), ctx, out );
			out.print( ')' );
			ctx.invoke( genExpression, source, ctx, out );
			out.println( ".clone();" );
			ctx.invoke( genExpression, target, ctx, out );
			out.print( " = " );
			out.print( tempName );
		}
		//TODO else...moveByName, moveFor, moveForAll
	}
}
