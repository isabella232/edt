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
import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.Type;

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

	@SuppressWarnings("static-access")
	public static String getNativeRuntimeOperationName(BinaryExpression expr) throws GenerationException {
		// safety check to make sure the operation has been defined properly
		if (expr.getOperation() == null || expr.getOperation().getName() == null)
			throw new GenerationException();
		// process the operator
		String op = expr.getOperator();
		if (op.equals(expr.Op_PLUS))
			return "plus";
		if (op.equals(expr.Op_MINUS))
			return "minus";
		if (op.equals(expr.Op_DIVIDE))
			return "divide";
		if (op.equals(expr.Op_MULTIPLY))
			return "multiply";
		if (op.equals(expr.Op_MODULO))
			return "modulo";
		if (op.equals(expr.Op_EQ))
			return "equals";
		if (op.equals(expr.Op_NE))
			return "notEquals";
		if (op.equals(expr.Op_LT))
			return "compareTo";
		if (op.equals(expr.Op_GT))
			return "compareTo";
		if (op.equals(expr.Op_LE))
			return "compareTo";
		if (op.equals(expr.Op_GE))
			return "compareTo";
		if (op.equals(expr.Op_AND))
			return "and";
		if (op.equals(expr.Op_OR))
			return "or";
		if (op.equals(expr.Op_XOR))
			return "xor";
		if (op.equals(expr.Op_CONCAT))
			return "concat";
		if (op.equals(expr.Op_NULLCONCAT))
			return "concat";
		if (op.equals(expr.Op_BITAND))
			return "bitand";
		if (op.equals(expr.Op_BITOR))
			return "bitor";
		if (op.equals(expr.Op_POWER))
			return "power";
		if (op.equals(expr.Op_IN))
			return "in";
		if (op.equals(expr.Op_MATCHES))
			return "matches";
		if (op.equals(expr.Op_LIKE))
			return "like";
		return "UnknownOp";
	}

	@SuppressWarnings("static-access")
	public static String getNativeRuntimeComparisionOperation(BinaryExpression expr) {
		String op = expr.getOperator();
		if (op.equals(expr.Op_LT))
			return " < 0";
		if (op.equals(expr.Op_GT))
			return " > 0";
		if (op.equals(expr.Op_LE))
			return " <= 0";
		if (op.equals(expr.Op_GE))
			return " >= 0";
		return "";
	}

	@SuppressWarnings("static-access")
	public static String getNativeJavaOperation(BinaryExpression expr, Context ctx) {
		String op = expr.getOperator();
		// if we are to use egl overflow checking, then don't pass back that we can do the mathematical operations in java
		if (expr.isNullable() || (Boolean) ctx.getParameter(Constants.parameter_checkOverflow)) {
			if (op.equals(expr.Op_EQ))
				return " == ";
			if (op.equals(expr.Op_NE))
				return " != ";
			if (op.equals(expr.Op_LT))
				return " < ";
			if (op.equals(expr.Op_GT))
				return " > ";
			if (op.equals(expr.Op_LE))
				return " <= ";
			if (op.equals(expr.Op_GE))
				return " >= ";
			if (op.equals(expr.Op_AND))
				return " && ";
			if (op.equals(expr.Op_OR))
				return " || ";
			if (op.equals(expr.Op_XOR))
				return " ^ ";
			if (op.equals(expr.Op_CONCAT))
				return " + ";
			if (op.equals(expr.Op_BITAND))
				return " & ";
			if (op.equals(expr.Op_BITOR))
				return " | ";
			return "";
		}
		// these are the defaults for all other types
		// division is intentionally left off as all division must be done through the egl runtime
		if (op.equals(expr.Op_PLUS))
			return " + ";
		if (op.equals(expr.Op_MINUS))
			return " - ";
		if (op.equals(expr.Op_MULTIPLY))
			return " * ";
		if (op.equals(expr.Op_MODULO))
			return " % ";
		if (op.equals(expr.Op_EQ))
			return " == ";
		if (op.equals(expr.Op_NE))
			return " != ";
		if (op.equals(expr.Op_LT))
			return " < ";
		if (op.equals(expr.Op_GT))
			return " > ";
		if (op.equals(expr.Op_LE))
			return " <= ";
		if (op.equals(expr.Op_GE))
			return " >= ";
		if (op.equals(expr.Op_AND))
			return " && ";
		if (op.equals(expr.Op_OR))
			return " || ";
		if (op.equals(expr.Op_XOR))
			return " ^ ";
		if (op.equals(expr.Op_CONCAT))
			return " + ";
		if (op.equals(expr.Op_BITAND))
			return " & ";
		if (op.equals(expr.Op_BITOR))
			return " | ";
		return "";
	}

	public static boolean isHandledByJavaWithoutCast(Expression src, AsExpression tgt, Context ctx) {
		// nullables will never be handled by java natives
		if (src.isNullable() || tgt.isNullable())
			return false;
		if (!ctx.mapsToPrimitiveType(src.getType()) || !ctx.mapsToPrimitiveType(tgt.getType()))
			return false;
		String srcString = ctx.getPrimitiveMapping(src.getType());
		String tgtString = ctx.getPrimitiveMapping(tgt.getType());
		// check see to see it is safe to allow java to handle this conversion
		int srcIndex = getJavaAllowedType(srcString);
		int tgtIndex = getJavaAllowedType(tgtString);
		if (srcIndex >= 0 && tgtIndex >= 0 && srcIndex == tgtIndex)
			return true;
		else
			return false;
	}

	public static boolean isHandledByJavaWithCast(Expression src, AsExpression tgt, Context ctx) {
		// nullables will never be handled by java natives
		if (src.isNullable() || tgt.isNullable())
			return false;
		if (!ctx.mapsToPrimitiveType(src.getType()) || !ctx.mapsToPrimitiveType(tgt.getType()))
			return false;
		String srcString = ctx.getPrimitiveMapping(src.getType());
		String tgtString = ctx.getPrimitiveMapping(tgt.getType());
		// check see to see it is safe to allow java to handle this conversion
		int srcIndex = getJavaAllowedType(srcString);
		int tgtIndex = getJavaAllowedType(tgtString);
		if (srcIndex >= 0 && tgtIndex >= 0 && srcIndex != tgtIndex)
			return true;
		else
			return false;
	}

	private static int getJavaAllowedType(String value) {
		if (value.equals("short"))
			return 0;
		else if (value.equals("int"))
			return 1;
		else if (value.equals("long"))
			return 2;
		else if (value.equals("float"))
			return 3;
		else if (value.equals("double"))
			return 4;
		else
			return -1;
	}

	@SuppressWarnings("unchecked")
	public static void processImport(String qualifiedName, Context ctx) {
		// if we didn't get a name, or it doesn't have periods in it, then we don't want to consider it for importing
		if (qualifiedName == null || qualifiedName.indexOf('.') < 0)
			return;
		// check the types list we have already
		List<String> typesImported = (List<String>) ctx.getAttribute(ctx.getClass(), Constants.Annotation_partTypesImported);
		for (String imported : typesImported) {
			if (qualifiedName.equalsIgnoreCase(imported)) {
				// it was already found, so we have done this logic before. Simply return
				return;
			}
		}
		// if we get here, then we haven't processed this type before
		String unqualifiedName = qualifiedName;
		if (unqualifiedName.indexOf('.') >= 0)
			unqualifiedName = unqualifiedName.substring(unqualifiedName.lastIndexOf('.') + 1);
		for (String imported : typesImported) {
			if (imported.indexOf('.') >= 0)
				imported = imported.substring(imported.lastIndexOf('.') + 1);
			if (unqualifiedName.equalsIgnoreCase(imported)) {
				// we have an unqualified name that we are importing that matches the last node, Simply return
				return;
			}
		}
		typesImported.add(qualifiedName);
	}

	public static void generateSmapExtension(Member field, Context ctx) {
		// if this isn't a temporary variable, then add the data to the debug extension buffer
		if (!field.getName().startsWith(org.eclipse.edt.gen.Constants.temporaryVariablePrefix)) {
			// get the line number. If it is not found, then we can't write the debug extension
			Annotation annotation = field.getAnnotation(IEGLConstants.EGL_LOCATION);
			if (annotation != null && annotation.getValue(IEGLConstants.EGL_PARTLINE) != null) {
				ctx.getSmapExtension().append("" + ((Integer) annotation.getValue(IEGLConstants.EGL_PARTLINE)).intValue());
				if (ctx.getCurrentFile() != null) {
					if (ctx.getSmapFiles().indexOf(ctx.getCurrentFile()) < 0)
						ctx.getSmapFiles().add(ctx.getCurrentFile());
					ctx.getSmapExtension().append("#" + (ctx.getSmapFiles().indexOf(ctx.getCurrentFile()) + 1) + ";");
				} else
					ctx.getSmapExtension().append("#1" + ";");
				ctx.getSmapExtension().append(field.getName() + ";");
				ctx.getSmapExtension().append(field.getName() + ";");
				ctx.getSmapExtension().append(field.getType().getTypeSignature() + "\n");
			}
		}
	}

	public static void generateSmapExtension(Function function, Context ctx) {
		// get the line number. If it is not found, then we can't write the debug extension
		Annotation annotation = function.getAnnotation(IEGLConstants.EGL_LOCATION);
		if (annotation != null && annotation.getValue(IEGLConstants.EGL_PARTLINE) != null) {
			ctx.getSmapExtension().append("" + ((Integer) annotation.getValue(IEGLConstants.EGL_PARTLINE)).intValue());
			if (ctx.getCurrentFile() != null) {
				if (ctx.getSmapFiles().indexOf(ctx.getCurrentFile()) < 0)
					ctx.getSmapFiles().add(ctx.getCurrentFile());
				ctx.getSmapExtension().append("#" + (ctx.getSmapFiles().indexOf(ctx.getCurrentFile()) + 1) + ";");
			} else
				ctx.getSmapExtension().append("#1" + ";");
			// for the main function, we need to alter the values
			if (function.getName().equalsIgnoreCase("main"))
				ctx.getSmapExtension().append("F:" + "main;(Ljava/util/List;)V\n");
			else {
				ctx.getSmapExtension().append("F:" + function.getName() + ";(");
				for (int i = 0; i < function.getParameters().size(); i++) {
					FunctionParameter decl = function.getParameters().get(i);
					if (org.eclipse.edt.gen.CommonUtilities.isBoxedParameterType(decl, ctx))
						ctx.getSmapExtension().append("Lorg/eclipse/edt/javart/AnyBoxedObject;");
					else
						ctx.getSmapExtension().append(generateJavaTypeSignature(function.getParameters().get(i).getType(), ctx));
				}
				ctx.getSmapExtension().append(")" + generateJavaTypeSignature(function.getReturnType(), ctx) + "\n");
			}
		}
	}

	private static String generateJavaTypeSignature(Type type, Context ctx) {
		String signature = "";
		// if this is an array, we need to handle it specially
		if (type instanceof ArrayType)
			signature += "L" + ctx.getRawNativeInterfaceMapping(type.getClassifier()).replaceAll("\\.", "/") + ";";
		else {
			// get the java primitive, if it exists
			if (type == null)
				signature += "V";
			else if (ctx.mapsToPrimitiveType(type.getClassifier())) {
				// do we want the java primitive or the object
				// now try to match up against the known primitives
				String value = ctx.getRawPrimitiveMapping(type.getClassifier());
				if (value.equals("boolean"))
					signature += "Z";
				else if (value.equals("byte"))
					signature += "B";
				else if (value.equals("char"))
					signature += "C";
				else if (value.equals("double"))
					signature += "D";
				else if (value.equals("float"))
					signature += "F";
				else if (value.equals("int"))
					signature += "I";
				else if (value.equals("long"))
					signature += "J";
				else if (value.equals("short"))
					signature += "S";
				else
					signature += "L" + value.replaceAll("\\.", "/") + ";";
			} else
				signature += "L" + type.getClassifier().getTypeSignature().replaceAll("\\.", "/") + ";";
		}
		return signature;
	}
}
