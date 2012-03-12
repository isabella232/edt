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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.compiler.internal.sql.SQLConstants;
import org.eclipse.edt.gen.generator.eglsource.EglSourceContext;
import org.eclipse.edt.ide.ui.internal.externaltype.util.ReflectionUtil;
import org.eclipse.edt.mof.codegen.api.AbstractTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;

public class JavaTypeMethodTemplate extends AbstractTemplate {
	public void genMethod(Method javaMethod, EglSourceContext ctx, TabbedWriter out){
		if(Modifier.isStatic(javaMethod.getModifiers())) {
			out.print("   static function ");
		} else {
			out.print("   function ");
		}
		String methodName = javaMethod.getName();
		boolean isEGLKeyWord = EGLNameValidator.isKeyword(methodName);
		
		if(isEGLKeyWord) {
			methodName = JavaTypeConstants.UNDERSTORE_PREFIX + methodName;
		}
		
		out.print(methodName + SQLConstants.LPAREN);
		
		Class<?>[] parameterTypes = javaMethod.getParameterTypes();
		for(int i=0; i < parameterTypes.length; i++) {
			out.print("arg" + i + SQLConstants.SPACE + ReflectionUtil.getEGLTypeName(parameterTypes[i])
					+ SQLConstants.SPACE + JavaTypeConstants.EGL_KEYWORD_IN);
			if(i != parameterTypes.length -1) {
				out.print(SQLConstants.COMMA);
			}
		}
		out.print(SQLConstants.RPAREN);
		
		Class<?> returnedType = javaMethod.getReturnType();
		if(!JavaTypeConstants.JAVA_VOID_TYPE.equals(returnedType.getSimpleName())) {
			out.print("  returns" + SQLConstants.LPAREN + ReflectionUtil.getEGLTypeName(returnedType) +
					SQLConstants.RPAREN);
		} 
		
		if(isEGLKeyWord) {
			out.print("   {externalName = " + SQLConstants.DOUBLE_QUOTE + javaMethod.getName()
					  + SQLConstants.DOUBLE_QUOTE + "}" );
		}
		
		out.println(SQLConstants.SEMICOLON);
	}
}
