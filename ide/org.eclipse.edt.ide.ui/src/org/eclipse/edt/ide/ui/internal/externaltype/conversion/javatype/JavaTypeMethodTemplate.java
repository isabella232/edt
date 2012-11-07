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
		boolean isStartWithEze = methodName.toLowerCase().startsWith(JavaTypeConstants.EZE_PREFIX);
		
		if(isEGLKeyWord || isStartWithEze) {
			methodName = JavaTypeConstants.UNDERSTORE_PREFIX + methodName;
		}
		
		out.print(methodName + "(");
		
		Class<?>[] parameterTypes = javaMethod.getParameterTypes();
		for(int i=0; i < parameterTypes.length; i++) {
			out.print("arg" + i + " " + ReflectionUtil.getEGLTypeName(parameterTypes[i])
					+ " " + JavaTypeConstants.EGL_KEYWORD_IN);
			if(i != parameterTypes.length -1) {
				out.print(',');
			}
		}
		out.print(')');
		
		Class<?> returnedType = javaMethod.getReturnType();
		if(!JavaTypeConstants.JAVA_VOID_TYPE.equals(returnedType.getSimpleName())) {
			out.print("  returns(" + ReflectionUtil.getEGLTypeName(returnedType) + ')');
		} 
		
		if(isEGLKeyWord || isStartWithEze) {
			out.print("   {@ExternalName{\"" + javaMethod.getName() + "\"}");
			if(javaMethod.getExceptionTypes().length > 0) {
				out.print("," + JavaTypeConstants.EGL_THROWS_ANNOTATION);
			}
			out.print("}");
		} else if(javaMethod.getExceptionTypes().length > 0) {
			out.print("{" + JavaTypeConstants.EGL_THROWS_ANNOTATION + "}");
		}
		
		out.println(';');
	}
}
