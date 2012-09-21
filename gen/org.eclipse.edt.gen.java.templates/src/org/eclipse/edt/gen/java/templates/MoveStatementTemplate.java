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
package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ArrayAccess;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.LogicAndDataPart;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.MoveStatement;
import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class MoveStatementTemplate extends JavaTemplate 
{
	public void genStatementEnd( MoveStatement stmt, Context ctx, TabbedWriter out )
	{
		// The other methods will generate the final semicolon if necessary.
	}

	public void genStatementBody( MoveStatement stmt, Context ctx, TabbedWriter out )
	{
		Expression targetExpr = stmt.getTargetExpr();
		Expression sourceExpr = stmt.getSourceExpr();
		
		if ( stmt.getModifier() == MoveStatement.MOVE_DEFAULT )
		{
			ctx.invoke( genExpression, targetExpr, ctx, out );
			out.print( " = (" );
			ctx.invoke( genRuntimeTypeName, sourceExpr.getType(), ctx, out );
			out.print( ") org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeDeepCopy(" );
			ctx.invoke( genExpression, sourceExpr, ctx, out );
			out.println( ");" );
		}
		else
		{
			boolean needTemp = !(targetExpr instanceof Name) || !(sourceExpr instanceof Name);
			if ( needTemp )
			{
				out.println( '{' );
				if ( !(targetExpr instanceof Name) )
				{
					// Save the target in a temp variable.
					String temp = ctx.nextTempName();
					
					ctx.invoke( genRuntimeTypeName, targetExpr.getType(), ctx, out );
					out.print( ' ' );				
					out.print( temp );
					out.print( " = " );				
					ctx.invoke( genExpression, targetExpr, ctx, out );
					out.println( ';' );
					// Create a Name to be the new target.
					targetExpr = nameForTemp( temp, ctx, targetExpr.getType() );
				}
				
				if ( !(sourceExpr instanceof Name) )
				{
					// Save the source in a temp variable.
					String temp = ctx.nextTempName();
					
					ctx.invoke( genRuntimeTypeName, sourceExpr.getType(), ctx, out );
					out.print( ' ' );				
					out.print( temp );
					out.print( " = " );				
					ctx.invoke( genExpression, sourceExpr, ctx, out );
					out.println( ';' );
					// Create a Name to be the new source.
					sourceExpr = nameForTemp( temp, ctx, sourceExpr.getType() );
				}
			}
			
			switch ( stmt.getModifier() )
			{
				case MoveStatement.MOVE_BYNAME:
					LogicAndDataPart targetPart = (LogicAndDataPart)targetExpr.getType();
					LogicAndDataPart sourcePart = (LogicAndDataPart)sourceExpr.getType();
					for ( Field sourceField : sourcePart.getFields() )
					{
						Field targetField = targetPart.getField( sourceField.getCaseSensitiveName() );
						if ( targetField != null )
						{
							Assignment assign = ctx.getFactory().createAssignment();
							assign.setOperator( "=" );
							
							MemberName targetName = ctx.getFactory().createMemberName();
							targetName.setId( targetField.getCaseSensitiveName() );
							targetName.setMember( (Member)targetField );
							MemberAccess access = (MemberAccess)targetName.addQualifier( targetExpr );
							assign.setLHS( access );
							
							MemberName sourceName = ctx.getFactory().createMemberName();
							sourceName.setId( sourceField.getCaseSensitiveName() );
							sourceName.setMember( (Member)sourceField );
							access = (MemberAccess)sourceName.addQualifier( sourceExpr );
							assign.setRHS( access );
							
							ctx.invoke( genExpression, assign, ctx, out );
							out.println( ';' );
						}
					}
					break;

				case MoveStatement.MOVE_FOR:
				case MoveStatement.MOVE_FORALL:
					String counter = ctx.nextTempName();
					out.print( "for ( int " + counter + " = " );

					Expression forCount = stmt.getModifierExpr();
					if ( forCount != null )
					{
						out.print( "(int)Math.min(Math.min(" );
						ctx.invoke( genExpression, sourceExpr, ctx, out );
						out.print( ".getSize(), " );
						ctx.invoke( genExpression, targetExpr, ctx, out );
						out.print( ".getSize())," );
						ctx.invoke( genExpression, forCount, ctx, out );
						out.print( ')' );
					}
					else
					{
						out.print( "Math.min(" );
						ctx.invoke( genExpression, sourceExpr, ctx, out );
						out.print( ".getSize(), " );
						ctx.invoke( genExpression, targetExpr, ctx, out );
						out.print( ".getSize())" );
					}					
					out.println( "; " + counter + " > 0; " + counter + "-- )" );
					out.println( '{' );
					Assignment assign = ctx.getFactory().createAssignment();
					assign.setOperator( "=" );
					
					Expression index = nameForTemp( counter, ctx, TypeUtils.Type_INT );
					
					ArrayAccess targetAccess = ctx.getFactory().createArrayAccess();
					targetAccess.setArray( targetExpr );
					targetAccess.setIndex( index );
					assign.setLHS( targetAccess );
					
					ArrayAccess sourceAccess = ctx.getFactory().createArrayAccess();
					sourceAccess.setArray( sourceExpr );
					sourceAccess.setIndex( index );
					assign.setRHS( sourceAccess );

					ctx.invoke( genExpression, assign, ctx, out );
					out.println( ';' );
					out.println( '}' );
					break;
			}

			if ( needTemp )
			{
				out.println( '}' );
			}
		}
	}
	
	private Name nameForTemp( String id, Context ctx, Type type )
	{
		Field field = ctx.getFactory().createField();
		field.setIsImplicit( true );
		field.setContainer( ctx.getCurrentFunction() );
		field.setType( type );
		field.setName( id );
		
		MemberName name = ctx.getFactory().createMemberName();
		name.setId( id );
		name.setMember( field );
		
		return name;
	}
}
