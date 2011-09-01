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
package org.eclipse.edt.gen.java.templates.egl.lang;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.*;

public class DictionaryTypeTemplate extends JavaTemplate 
{
	private static final String FIELD_KEY = "org.eclipse.edt.gen.java.templates.egl.lang.DictionaryTypeTemplate.FIELD_KEY";
	
	public void genConstructorOptions( Type type, Context ctx, TabbedWriter out ) 
	{
		Field field = (Field)ctx.remove( FIELD_KEY );
		if ( field != null )
		{
			Annotation caseSense = field.getAnnotation( "eglx.lang.CaseSensitive" ); 
			Annotation ordering = field.getAnnotation( "eglx.lang.Ordering" );
			if ( caseSense != null || ordering != null )
			{
				boolean caseSenseValue = caseSense != null ? (Boolean)caseSense.getValue() : false;
				String orderingValue = ordering != null ? ordering.getValue().toString() : "none";
				out.print( caseSenseValue );
				out.print( ", eglx.lang.OrderingKind." );
				out.print( orderingValue );
			}
		}
	}
	
	public void genAssignment( Type type, Context ctx, TabbedWriter out, Expression lhs, Expression rhs, String operator )
	{
		// If the target is a Field and the source is a NewExpression, save the Field
		// so we can use its annotations for arguments to the constructor.
		Field field = null;
		if ( lhs instanceof ArrayAccess && ((Name)((ArrayAccess)lhs).getArray()).getNamedElement() instanceof Field )
		{
			field = (Field)((Name)((ArrayAccess)lhs).getArray()).getNamedElement();
		}
		else if ( lhs instanceof Name && ((Name)lhs).getNamedElement() instanceof Field )
		{
			field = (Field)((Name)lhs).getNamedElement();
		}
		
		if ( field != null && rhs instanceof NewExpression )
		{
			ctx.put( FIELD_KEY, field );
		}
		ctx.invokeSuper( this, genAssignment, type, ctx, out, lhs, rhs, operator );
	}
}
