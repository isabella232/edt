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

import org.eclipse.edt.gen.Constants;
import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.JavaAliaser;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.*;

public class NamedElementTemplate extends JavaTemplate {

	public Annotation getPropertyAnnotation(NamedElement expr) {
		return expr.getAnnotation(Constants.Annotation_EGLProperty);
	}

	public void genAccessor(NamedElement element, Context ctx, TabbedWriter out) {
		Annotation property = getPropertyAnnotation(element);
		if (property != null) {
			// obtain the name of the function
			String functionName;
			if (property.getValue("getMethod") != null)
				functionName = (String) property.getValue("getMethod");
			else {
				functionName = "get" + element.getName().substring(0, 1).toUpperCase();
				if (element.getName().length() > 1)
					functionName = functionName + element.getName().substring(1);
			}
			// if the function name matches the name of the current function, then this is the getter and we simply output
			// the name of the variable, instead of creating an infinite loop of calls to the same function
			if (functionName.equals(ctx.getCurrentFunction()))
				genName(element, ctx, out);
			else
				out.print(functionName + "()");
		} else
			genName(element, ctx, out);
	}

	public void genName(NamedElement element, Context ctx, TabbedWriter out) {
		if ( element instanceof Member && ((Member)element).getContainer() instanceof ExternalType )
		{
			Member member = (Member)element;
			if ( CommonUtilities.isJavaExternalType( (ExternalType)member.getContainer() ) 
					&& member.getAnnotation( "eglx.lang.ExternalName" ) != null )
			{
				out.print( (String)member.getAnnotation( "eglx.lang.ExternalName" ).getValue() );
				return;
			}
		}

		out.print(JavaAliaser.getAlias(element.getName()));
	}
}
