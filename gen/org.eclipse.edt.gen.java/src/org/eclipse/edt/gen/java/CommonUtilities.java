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
package org.eclipse.edt.gen.java;

import java.util.List;

import org.eclipse.edt.compiler.binding.annotationType.EGLIsSystemPartAnnotationTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.core.utils.Aliaser;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class CommonUtilities {

	public static String packageName(Part part) {
		return Aliaser.packageNameAlias(part.getPackageName().split("[.]"), '.');
	}

	public static String packageName(String pkg) {
		if (pkg != null && pkg.length() > 0) {
			return Aliaser.packageNameAlias(pkg);
		}

		return pkg;
	}

	/**
	 * Returns true if the type is an ExternalType, with subtype JavaObject or NativeType, and does not have {
	 * eglIsSystemPart = yes }.
	 */
	public static boolean isUserDefinedExternalType(Type type) {
		return getUserDefinedExternalType(type) != null;
	}

	/**
	 * Returns the Type if it's an ExternalType, with subtype JavaObject or NativeType, and does not have { eglIsSystemPart =
	 * yes }. If it's something else, returns null.
	 */
	public static ExternalType getUserDefinedExternalType(Type type) {
		if (type instanceof Part) {
			Part member = (Part) type;
			if (member instanceof ExternalType) {
				if (member.getAnnotation(EGLIsSystemPartAnnotationTypeBinding.name) == null) {
					return (ExternalType) member;
				} else {
					Annotation annot = member.getAnnotation(IEGLConstants.EXTERNALTYPE_SUBTYPE_JAVAOBJECT);
					if (annot != null) {
						String value = (String) annot.getValue(IEGLConstants.PROPERTY_PACKAGENAME);
						if (value != null && (value.startsWith("java.") || value.startsWith("javax."))) {
							return (ExternalType) member;
						}
					}
				}
			}
		} else if (type instanceof ExternalType) {
			if (type.getAnnotation(EGLIsSystemPartAnnotationTypeBinding.name) == null) {
				return (ExternalType) type;
			} else {
				Annotation annot = type.getAnnotation(IEGLConstants.EXTERNALTYPE_SUBTYPE_JAVAOBJECT);
				if (annot != null) {
					String value = (String) annot.getValue(IEGLConstants.PROPERTY_PACKAGENAME);
					if (value != null && (value.startsWith("java.") || value.startsWith("javax."))) {
						return (ExternalType) type;
					}
				}
			}
		} else if (type instanceof ArrayType) {
			while (type instanceof ArrayType) {
				type = ((ArrayType) type).getElementType();
			}
			return getUserDefinedExternalType(type);
		}
		return null;
	}

	/**
	 * @return true if the external type is serializable. To be serializable, it must extend the built-in
	 * java.io.Serializable external type.
	 */
	public static boolean isSerializable(ExternalType et) {
		if (et == null) {
			return false;
		}

		Annotation annot = et.getAnnotation(IEGLConstants.EXTERNALTYPE_SUBTYPE_JAVAOBJECT);
		if (annot != null) {
			List<StructPart> extndsAry = et.getSuperTypes();
			if (extndsAry != null) {
				for (StructPart part : extndsAry) {
					if (part instanceof ExternalType && (annot = part.getAnnotation(IEGLConstants.EXTERNALTYPE_SUBTYPE_JAVAOBJECT)) != null) {
						if ("java.io".equalsIgnoreCase((String) annot.getValue(IEGLConstants.PROPERTY_PACKAGENAME))
							&& "Serializable".equalsIgnoreCase((String) annot.getValue(IEGLConstants.PROPERTY_JAVANAME))) {
							return true;
						}

						// JavaObject parent is not Serializable. Check parent's parent.
						// IRs are saying Object extends Object - prevent stack overflow.
						if (!isObjectExternalType(part) && isSerializable((ExternalType) part)) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	/**
	 * @return true if m represents the java.lang.Object system external type.
	 */
	private static boolean isObjectExternalType(Part m) {
		if (m instanceof ExternalType) {
			Annotation annot = m.getAnnotation(IEGLConstants.EXTERNALTYPE_SUBTYPE_JAVAOBJECT);
			if (annot != null) {
				String name = (String) annot.getValue(IEGLConstants.PROPERTY_JAVANAME);
				if (name == null || name.length() == 0) {
					name = m.getId();
				}

				if (!"Object".equalsIgnoreCase(name) || !"java.lang".equalsIgnoreCase((String) annot.getValue(IEGLConstants.PROPERTY_PACKAGENAME))) {
					return false;
				}

				return true;
			}
		}

		return false;
	}

	public static boolean isArgumentToBeAltered(Context ctx, FunctionParameter parameter, Expression expression) {
		if (parameter.getParameterKind() == ParameterKind.PARM_IN) {
			// if the parameter is reference then do not make a temporary
			if (TypeUtils.isReferenceType(parameter.getType()))
				return false;
			// if the argument and parameter types mismatch, or if nullable, or not java primitive, then create a
			// temporary
			if (!parameter.getType().equals(expression.getType()) || parameter.isNullable() || expression.isNullable()
				|| !ctx.mapsToPrimitiveType(parameter.getType()))
				return true;
			return false;
		} else
			return isBoxedParameterType(ctx, parameter);
	}

	public static boolean isBoxedParameterType(Context ctx, FunctionParameter parameter) {
		if (parameter.getParameterKind() == ParameterKind.PARM_INOUT) {
			if (TypeUtils.isReferenceType(parameter.getType()))
				return true;
			else if (ctx.mapsToPrimitiveType(parameter.getType()))
				return true;
			else if (parameter.isNullable())
				return true;
		} else if (parameter.getParameterKind() == ParameterKind.PARM_OUT)
			return true;
		return false;
	}
}
