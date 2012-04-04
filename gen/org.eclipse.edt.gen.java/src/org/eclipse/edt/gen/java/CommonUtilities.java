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
package org.eclipse.edt.gen.java;

import java.util.List;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.javart.util.JavaAliaser;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.*;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;

public class CommonUtilities {

	public static String packageName(Part part) {
		return packageName(part.getPackageName());
	}

	public static String packageName(String pkg) {
		if (pkg != null && pkg.length() > 0) {
			return JavaAliaser.packageNameAlias(pkg);
		}

		return pkg;
	}

	/**
	 * Returns the fully-qualified name for the Part's runtime class.
	 * @param part
	 * @return
	 */
	public static String fullClassAlias(Part part) {
		String alias = JavaAliaser.getAlias(part.getName());
		String pkg = part.getPackageName();
		if (pkg.length() > 0) {
			pkg = packageName(pkg);
			return pkg + '.' + alias;
		}
		return alias;
	}

	/**
	 * Returns the unqualified name for the Part's runtime class.
	 * @param part
	 * @return
	 */
	public static String classAlias(Part part) {
		return JavaAliaser.getAlias(part.getName());
	}

	/**
	 * Returns true if the type is an ExternalType with subtype JavaObject or NativeType.
	 */
	public static boolean isJavaExternalType(Type type) {
		return getJavaExternalType(type) != null;
	}

	/**
	 * Returns the Type if it's an ExternalType, with subtype JavaObject or NativeType. If it's something else, returns null.
	 */
	public static ExternalType getJavaExternalType(Type type) {
		if (type instanceof ExternalType) {
			if (type.getAnnotation("eglx.java.JavaObject") != null ||
				type.getAnnotation("eglx.java.RootJavaObject") != null ||
				type.getAnnotation("eglx.lang.NativeType") != null) {
				return (ExternalType) type;
			} else {
				return null;
			}
		} else if (type instanceof Part) {
			Part member = (Part) type;
			if (member instanceof ExternalType) {
				if (member.getAnnotation("eglx.java.JavaObject") != null ||
					member.getAnnotation("eglx.java.RootJavaObject") != null ||
					member.getAnnotation("eglx.lang.NativeType") != null) {
					return (ExternalType) member;
				} else {
					return null;
				}
			}
		} else if (type instanceof ArrayType) {
			while (type instanceof ArrayType) {
				type = ((ArrayType) type).getElementType();
			}
			return getJavaExternalType(type);
		}
		return null;
	}

	/**
	 * @return true if the external type is serializable. To be serializable, it must extend the java.io.Serializable
	 * external type.
	 */
	public static boolean isSerializable(ExternalType et) {
		if (et == null) {
			return false;
		}

		// First see if we're looking at java.io.Serializable.
		Annotation annot = et.getAnnotation("eglx.java.JavaObject");
		if (annot != null) {
			String name = et.getName();
			if (annot.getValue("externalName") != null && ((String) annot.getValue("externalName")).length() > 0) {
				name = (String) annot.getValue("externalName");
			}
			if ("Serializable".equals(name)) {
				String pkg = et.getPackageName();
				if (((String) annot.getValue(IEGLConstants.PROPERTY_PACKAGENAME)).length() > 0) {
					pkg = (String) annot.getValue(IEGLConstants.PROPERTY_PACKAGENAME);
				}
				if ("java.io".equals(pkg)) {
					return true;
				}
			}
		} else {
			annot = et.getAnnotation("eglx.java.RootJavaObject");
			if (annot != null) {
				if ("Serializable".equals(et.getName())) {
					String pkg = et.getPackageName();
					if (((String) annot.getValue(IEGLConstants.PROPERTY_PACKAGENAME)).length() > 0) {
						pkg = (String) annot.getValue(IEGLConstants.PROPERTY_PACKAGENAME);
					}
					if ("java.io".equals(pkg)) {
						return true;
					}
				}
			}
		}

		// Check the super types.
		List<StructPart> extndsAry = et.getSuperTypes();
		if (extndsAry != null) {
			for (StructPart part : extndsAry) {
				if (part instanceof ExternalType && isSerializable((ExternalType) part)) {
					return true;
				}
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
			return "remainder";
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
			return "concatNull";
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
		// we must not use the equals or notequals from java, as the Object versions of these simply compare the 
		// object and not the values. we need to pass equals and notequals to the edt runtime instead 
		// if we are to use egl overflow checking, then don't pass back that we can do the mathematical operations in java
		if (expr.isNullable() || (Boolean) ctx.getParameter(Constants.parameter_checkOverflow)) {
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

	@SuppressWarnings("static-access")
	public static String getNativeNullTypeJavaOperation(BinaryExpression expr, Context ctx) {
		String op = expr.getOperator();
		if (op.equals(expr.Op_EQ))
			return " == ";
		if (op.equals(expr.Op_NE))
			return " != ";
		return "";
	}

	public static String getNativeJavaAssignment(String op) {
		if (op.equals("xor="))
			return "^=";
		if (op.equals("::="))
			return "+=";
		return op;
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
			return !isBoxedOutputTemp(src, ctx);
		else
			return false;
	}

	private static int getJavaAllowedType(String value) {
		if (value.equals("boolean"))
			return 0;
		else if (value.equals("short"))
			return 1;
		else if (value.equals("int"))
			return 2;
		else if (value.equals("long"))
			return 3;
		else if (value.equals("float"))
			return 4;
		else if (value.equals("double"))
			return 5;
		else
			return -1;
	}

	public static void genEzeCopyTo(Expression expr, Context ctx, TabbedWriter out) {
		out.print("org.eclipse.edt.runtime.java.eglx.lang.AnyValue.ezeCopyTo(");
		// if this is the null literal, we need to cast this to prevent the javagen ambiguous errors
		if (expr instanceof NullLiteral)
			out.print("(eglx.lang.AnyValue) ");
	}

	public static boolean isBoxedOutputTemp(Expression expr, Context ctx) {
		return expr instanceof MemberName && isBoxedOutputTemp(((MemberName) expr).getMember(), ctx);
	}

	public static boolean isBoxedOutputTemp(Member member, Context ctx) {
		return ctx.getAttribute(member, org.eclipse.edt.gen.Constants.SubKey_functionArgumentTemporaryVariable) != null
			&& ctx.getAttribute(member, org.eclipse.edt.gen.Constants.SubKey_functionArgumentTemporaryVariable) != ParameterKind.PARM_IN;
	}

	@SuppressWarnings("unchecked")
	public static void processImport(String qualifiedName, Context ctx) {
		// if we didn't get a name, or it doesn't have periods in it, then we don't want to consider it for importing
		if (qualifiedName == null || qualifiedName.indexOf('.') < 0)
			return;
		// check the types list we have already
		List<String> typesImported = (List<String>) ctx.getAttribute(ctx.getClass(), Constants.SubKey_partTypesImported);
		for (String imported : typesImported) {
			if (qualifiedName.equalsIgnoreCase(imported)) {
				// it was already found, so we have done this logic before. Simply return
				return;
			}
		}
		// ignore adding this entry to the list, if it is the part we are currently generating
		Part partBeingGenerated = (Part) ctx.getAttribute(ctx.getClass(), Constants.SubKey_partBeingGenerated);
		if (partBeingGenerated.getFullyQualifiedName().equalsIgnoreCase(qualifiedName))
			return;
		// don't import a type whose unqualified name matches the name of the part currently being generated
		String unqualifiedName = qualifiedName;
		if (unqualifiedName.indexOf('.') >= 0)
			unqualifiedName = unqualifiedName.substring(unqualifiedName.lastIndexOf('.') + 1);
		if (unqualifiedName.equalsIgnoreCase(partBeingGenerated.getName()))
			return;
		// if we get here, then we haven't processed this type before
		for (String imported : typesImported) {
			int dotIndex = imported.lastIndexOf('.');
			if (dotIndex >= 0)
				imported = imported.substring(dotIndex + 1);
			if (unqualifiedName.equalsIgnoreCase(imported)) {
				// we have an unqualified name that we are importing that matches the last node, Simply return
				return;
			}
		}
		typesImported.add(qualifiedName);
	}

	public static void generateSmapExtension(Member field, Context ctx) {
		// if this isn't an internal variable, then add the data to the debug extension buffer
		if (!field.getName().startsWith("eze")) {
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
				ctx.getSmapExtension().append(JavaAliaser.getAlias(field.getName()) + ";");
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
			ctx.getSmapExtension().append("F:" + function.getName() + ";" + JavaAliaser.getAlias(function.getName()) + ";(");
			for (int i = 0; i < function.getParameters().size(); i++) {
				FunctionParameter decl = function.getParameters().get(i);
				if (org.eclipse.edt.gen.CommonUtilities.isBoxedParameterType(decl, ctx) && !decl.isConst())
					ctx.getSmapExtension().append("Lorg/eclipse/edt/javart/AnyBoxedObject;");
				else if (decl.getType() instanceof Delegate)
					ctx.getSmapExtension().append("Lorg/eclipse/edt/javart/Delegate;");
				else
					ctx.getSmapExtension().append(generateJavaTypeSignature(function.getParameters().get(i).getType(), ctx,
						(decl.getParameterKind() == ParameterKind.PARM_IN)));
			}
			ctx.getSmapExtension().append(")" + generateJavaTypeSignature(function.getReturnType(), ctx, false) + "\n");
		}
	}

	public static void generateSmapExtension(DataTable dataTable, Context ctx) {
		// TODO uncomment when tables are supported. this might also need to be updated
		// ctx.getSmapExtension().append(Constants.smap_extensionDataTable + ";" + dataTable.getFullyQualifiedName()
		// + ";" + dataTable.getFullyQualifiedName().replace('.', '_')+ "\n");
	}

	public static void generateSmapExtension(Form form, Context ctx) {
		// TODO uncomment when forms are supported. this might also need to be updated
		// ctx.getSmapExtension().append(Constants.smap_extensionForm + ";" + form.getFullyQualifiedName()
		// + ";" + form.getFullyQualifiedName().replace('.', '_')+ "\n");
	}

	public static void generateSmapExtension(Library library, Context ctx) {
		if (ctx.mapsToNativeType(library))
			ctx.getSmapExtension().append(
				Constants.smap_extensionSystemLibrary + ";" + library.getFullyQualifiedName() + ";" + Constants.LIBRARY_PREFIX
					+ library.getFullyQualifiedName().replace('.', '_') + "\n");
		else
			ctx.getSmapExtension().append(
				Constants.smap_extensionUserLibrary + ";" + library.getFullyQualifiedName() + ";" + Constants.LIBRARY_PREFIX
					+ library.getFullyQualifiedName().replace('.', '_') + "\n");
	}

	public static void generateSmapExtension(ProgramParameter programParameter, Context ctx) {
		ctx.getSmapExtension().append(
			Constants.smap_extensionProgramParameter + ";" + programParameter.getName() + ";" + JavaAliaser.getAlias(programParameter.getName()) + ";");
		ctx.getSmapExtension().append(programParameter.getType().getTypeSignature() + "\n");
	}

	private static String generateJavaTypeSignature(Type type, Context ctx, boolean inParm) {
		String signature = "";
		// if this is an array, we need to handle it specially
		if (type instanceof ArrayType)
			signature += "Ljava/util/List;";
		else {
			// get the java primitive, if it exists
			if (type == null)
				signature += "V";
			else if (ctx.mapsToPrimitiveType(type.getClassifier())) {
				// do we want the java primitive or the object
				// now try to match up against the known primitives
				String value = ctx.getRawPrimitiveMapping(type.getClassifier());
				// if this is an in parm, we need the object versions of the primitives
				if (inParm) {
					if (value.equals("boolean"))
						signature += "Ljava/lang/Boolean;";
					else if (value.equals("double"))
						signature += "Ljava/lang/Double;";
					else if (value.equals("float"))
						signature += "Ljava/lang/Float;";
					else if (value.equals("int"))
						signature += "Ljava/lang/Integer;";
					else if (value.equals("long"))
						signature += "Ljava/lang/Long;";
					else if (value.equals("short"))
						signature += "Ljava/lang/Short;";
					else
						signature += "L" + value.replaceAll("\\.", "/") + ";";
				} else {
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
				}
			} else
				signature += "L" + type.getClassifier().getTypeSignature().replaceAll("\\.", "/") + ";";
		}
		return signature;
	}

	public static Annotation getAnnotation(Context ctx, String key) throws MofObjectNotFoundException, DeserializationException {
		EObject eObject = Environment.getCurrentEnv().find(key);
		if (eObject instanceof StereotypeType && (eObject = ((StereotypeType) eObject).newInstance()) instanceof Annotation) {
			return (Annotation) eObject;
		} else if (eObject instanceof AnnotationType && (eObject = ((AnnotationType) eObject).newInstance()) instanceof Annotation) {
			return (Annotation) eObject;
		}
		return null;
	}

	/**
	 * Returns null if the desired propertyFunction isn't specified or shouldn't be used; otherwise, returns either the
	 * explicit name of the function (if specified) or implicit name if it should be inferred. According to the docs for both
	 * EGLProperty and Property, function names should be inferred if and only if the annotation is present but BOTH
	 * properties are missing.
	 */
	public static String getPropertyFunction(NamedElement field, boolean setter, Context context) {
		String result = null;

		boolean isEGLProperty = true;
		Annotation annotation = field.getAnnotation(Constants.Annotation_EGLProperty);
		if (annotation == null) {
			annotation = field.getAnnotation(Constants.Annotation_Property);
			isEGLProperty = false;
		}

		if (annotation != null) {
			String propertyFunction = setter ? Constants.Annotation_PropertySetter : Constants.Annotation_PropertyGetter;
			String otherPropertyFunction = setter ? Constants.Annotation_PropertyGetter : Constants.Annotation_PropertySetter;

			Object propFn = annotation.getValue(propertyFunction);
			Object otherPropFn = annotation.getValue(otherPropertyFunction);

			// If neither function is specified then we are supposed to infer the function
			// names for @Property and look up the functions for @EGLProperty.
			boolean bothUnspecified = (propFn == null || (propFn instanceof String && ((String) propFn).length() == 0))
				&& (otherPropFn == null || (otherPropFn instanceof String && ((String) otherPropFn).length() == 0));
			if (bothUnspecified) {
				String fieldName = field.getName();
				result = (setter ? Constants.SetterPrefix : Constants.GetterPrefix) + fieldName.substring(0, 1).toUpperCase();
				if (fieldName.length() > 1) {
					result += fieldName.substring(1);
				}

				if (isEGLProperty) {
					// For @EGLProperty we have to take EGL's case-insensitivity into account.
					// We can't simply assume the function for getting field XYZ is named getXYZ.
					// It might be named getxyz. We'll get the function by making a
					// QualifiedFunctionInvocation and using its ability to resolve the function being called.
					QualifiedFunctionInvocation qfi = context.getFactory().createQualifiedFunctionInvocation();
					qfi.setId((String) result);
					qfi.setQualifier(expressionForContainer(((Field) field).getContainer(), context));
					if (setter) {
						MemberName argName = context.getFactory().createMemberName();
						argName.setId(field.getName());
						argName.setMember((Member) field);
						qfi.getArguments().add(argName);
					}

					result = qfi.getTarget().getName();
				}
			} else {
				if (propFn instanceof Name) {
					result = ((Name) propFn).getId();
				} else {
					result = (String) propFn;
				}
			}
		}

		return result;
	}

	private static Expression expressionForContainer(Container container, Context ctx) {
		Expression result = null;
		Object pbg = ctx.getAttribute(ctx.getClass(), Constants.SubKey_partBeingGenerated);
		if (container instanceof Function
			|| (container instanceof Part && pbg instanceof Part && ((Part) container).getFullyQualifiedName().equalsIgnoreCase(
				((Part) pbg).getFullyQualifiedName()))) {
			ThisExpression thisExpr = ctx.getFactory().createThisExpression();
			thisExpr.setThisObject(container);
			result = thisExpr;
		} else {
			TypeName typeExpr = ctx.getFactory().createTypeName();
			typeExpr.setType((Type) container);
			result = typeExpr;
		}

		return result;
	}
	public static String getEnumerationName(Object enm){
		if(enm instanceof EnumerationEntry){
			return ((EnumerationEntry)enm).getName();
		}
		else{
			return "";
		}
	}

}
