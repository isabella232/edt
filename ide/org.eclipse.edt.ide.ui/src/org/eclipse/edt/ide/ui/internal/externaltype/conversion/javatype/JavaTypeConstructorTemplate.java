/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.externaltype.conversion.javatype;

import java.lang.reflect.Constructor;

import org.eclipse.edt.compiler.internal.sql.SQLConstants;
import org.eclipse.edt.gen.generator.eglsource.EglSourceContext;
import org.eclipse.edt.ide.ui.internal.externaltype.util.ReflectionUtil;
import org.eclipse.edt.mof.codegen.api.AbstractTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;

public class JavaTypeConstructorTemplate extends AbstractTemplate {
	public void genConstructor(Constructor<?> constructor, EglSourceContext ctx, TabbedWriter out){
		StringBuilder builder = new StringBuilder(80);
		builder.append("  constructor" + SQLConstants.LPAREN);
		
		Class<?>[] parameterTypes = constructor.getParameterTypes();
		for(int i=0; i < parameterTypes.length; i++) {
			builder.append("arg" + i + SQLConstants.SPACE + ReflectionUtil.getTypeName(parameterTypes[i]));
			if(i != parameterTypes.length -1) {
				builder.append(SQLConstants.COMMA);
			}
		}
		
		builder.append(SQLConstants.RPAREN + SQLConstants.SEMICOLON);
		out.println(builder.toString());
	}
}