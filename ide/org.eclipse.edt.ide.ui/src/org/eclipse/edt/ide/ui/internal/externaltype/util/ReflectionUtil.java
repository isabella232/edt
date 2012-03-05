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
package org.eclipse.edt.ide.ui.internal.externaltype.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.compiler.internal.sql.SQLConstants;
import org.eclipse.edt.ide.ui.internal.externaltype.conversion.javatype.JavaTypeConstants;

public class ReflectionUtil {
	
	public static Class<?> getClass(ClassLoader urlLoader,String fullyQualifiedName) {
		try {
			return Class.forName(fullyQualifiedName,true,urlLoader);
		} catch (Throwable ee) {
			ee.printStackTrace();
		} 
		
		return null;
	}
	
	public static Set<Class<?>> getAllSuperTypes(Class<?> clazz) {
		Set<Class<?>> superTypes = new HashSet<Class<?>>();
		
		Class<?> declaringClass = clazz;
		while(!declaringClass.equals(Object.class)) {
			Class<?> superClass = declaringClass.getSuperclass();
			if(!superClass.equals(Object.class)) {
				superTypes.add(superClass);
			}
			
			//try interfaces
			Class<?>[] interfaces = declaringClass.getInterfaces();
			for(Class<?> interfaceClass : interfaces) {
				superTypes.addAll(getImplementedInterfaces(interfaceClass));
			}
			
			declaringClass = declaringClass.getSuperclass();
		}
		
		return superTypes;
	}
	
	public static Set<Class<?>> getImplementedInterfaces(Class<?> clazz) {
        Set<Class<?>> interfaces = new HashSet<Class<?>>();
        
        if (clazz.isInterface())
            interfaces.add(clazz);

        while (clazz != null) {
            for (Class<?> iface : clazz.getInterfaces())
                  interfaces.addAll(getImplementedInterfaces(iface));
            clazz = clazz.getSuperclass();
        } 

        return interfaces;
    }
	
	public static Set<Class<?>> getFieldReferencedTypes(List<Field> fields) {
		Set<Class<?>> referenced = new HashSet<Class<?>>();
		for(Field field : fields) {
			if(!isBuiltinEglType(field.getType())) {
				referenced.add(field.getType());
			}
		}
		return referenced;
	}
	
	public static Set<Class<?>> getConReferencedTypes(List<Constructor<?>> constructors) {
		Set<Class<?>> referenced = new HashSet<Class<?>>();
		
		for(Constructor<?> constructor : constructors) {
			Class<?>[] paraTypes = constructor.getParameterTypes();
			for(Class<?> paraType : paraTypes) {
				paraType = getComponentClass(paraType);
				if(!isBuiltinEglType(paraType)) {
					referenced.add(paraType);
				}
			}
		}
		
		return referenced;
	}
	
	public static Set<Class<?>> getMethodReferencedTypes(List<Method> methods) {
		Set<Class<?>> referenced = new HashSet<Class<?>>();
		
		for(Method method : methods) {
			Class<?>[] paraTypes = method.getParameterTypes();
			for(Class<?> paraType : paraTypes) {
				paraType = getComponentClass(paraType);
				if(!isBuiltinEglType(paraType) && !Object.class.equals(paraType)) {
					referenced.add(paraType);
				}
			}
			
			Class<?> returnType = getComponentClass(method.getReturnType());
			if(!Void.TYPE.equals(returnType) && !Object.class.equals(returnType)
					&& !isBuiltinEglType(returnType)) {
				referenced.add(returnType);
			}
		}
		
		return referenced;
	}
	
	public static String getMethodLabel(java.lang.reflect.Method method) {
		StringBuilder buffer = new StringBuilder(60);
		buffer.append(method.getName());
		buffer.append(SQLConstants.LPAREN);
		
		Class<?>[] paraTypes = method.getParameterTypes();
		for(int i=0; i<paraTypes.length; i++) {
			buffer.append(paraTypes[i].getSimpleName());
			if(i < paraTypes.length-1){
				buffer.append(SQLConstants.COMMA);
			}
		}
		buffer.append(SQLConstants.RPAREN);
		return buffer.toString();
	}
	
	public static String getConstructorLabel(java.lang.reflect.Constructor<?> con) {
		StringBuilder buffer = new StringBuilder(60);
		buffer.append(con.getDeclaringClass().getSimpleName());
		buffer.append(SQLConstants.LPAREN);
		
		Class<?>[] paraTypes = con.getParameterTypes();
		for(int i=0; i<paraTypes.length; i++) {
			buffer.append(paraTypes[i].getSimpleName());
			if(i < paraTypes.length-1){
				buffer.append(SQLConstants.COMMA);
			}
		}
		buffer.append(SQLConstants.RPAREN);
		
		return buffer.toString();
	}
	
	public static String getFieldLabel(java.lang.reflect.Field field) {
		StringBuilder buffer = new StringBuilder(60);
		if(Modifier.isStatic(field.getModifiers())) {
			buffer.append("static ");
		}
		
		boolean isEGLKeyWord = EGLNameValidator.isKeyword(field.getName());
		if(isEGLKeyWord) {
			buffer.append(JavaTypeConstants.UNDERSTORE_PREFIX);
		}
		buffer.append(field.getName() + SQLConstants.SPACE + getTypeName(field.getType()));
		
		return buffer.toString();
	}
	
	public static String getTypeName(Class<?> paraType) {
		String typeName;
		int dim = 0;
		while(paraType.isArray()) {
			dim++;
			paraType = paraType.getComponentType();
		}
		
		typeName = JavaTypeConstants.JavaToEglMapping.get(paraType.getName());
		if(typeName == null)
			typeName = paraType.getSimpleName();
		
		boolean isEGLPart =JavaTypeConstants.EglPartNames.contains(typeName.toLowerCase(Locale.ENGLISH));
		if(isEGLPart) {
			typeName = JavaTypeConstants.UNDERSTORE_PREFIX + typeName;
		}
		while(dim > 0) {
			typeName = typeName + SQLConstants.LEFT_BRACKET + SQLConstants.RIGHT_BRACKET;
			dim--;
		}
		
		return typeName;
	}
	
	public static boolean isBuiltinEglType(Class<?> paraType) {
		String typeName;
		paraType = getComponentClass(paraType);
		typeName = JavaTypeConstants.JavaToEglMapping.get(paraType.getName());
		
		return (typeName != null);
	}
	
	public static Class<?> getComponentClass(Class<?> paraType) {
		Class<?> clazz = paraType;
		
		while(clazz.isArray())
			clazz = clazz.getComponentType();
		return clazz;
	}
}
