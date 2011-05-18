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
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ArrayAccess;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;
import org.eclipse.edt.mof.egl.UnaryExpression;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class TypeTemplate extends JavaScriptTemplate {

	public void validate(Type type, Context ctx, Object... args) {
		// types may override this validation for specific checking
	}

	public void genConversionOperation(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genConversionOperation, type, ctx, out, args)) {
			if (((AsExpression) args[0]).getConversionOperation() != null)
				ctx.gen("gen" + CommonUtilities.getEglNameForTypeCamelCase(((AsExpression) args[0]).getConversionOperation().getParameters().get(0).getType())
					+ "Conversion", ((AsExpression) args[0]).getConversionOperation().getReturnType(), ctx, out, args);
			else if (TypeUtils.Type_ANY.equals(((AsExpression) args[0]).getObjectExpr().getType())) {
				out.print("egl.convertAnyToType(");
				ctx.gen(genExpression, ((AsExpression) args[0]).getObjectExpr(), ctx, out, args);
				out.print(", ");
				out.print("\"");
				ctx.gen(genSignature, ((AsExpression) args[0]).getEType(), ctx, out, ((AsExpression) args[0]).getObjectExpr());
				out.print("\"");
				out.print(")");
			} else
				ctx.gen(genExpression, ((AsExpression) args[0]).getObjectExpr(), ctx, out, args);
		}
	}

	public void genInstantiation(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genInstantiation, type, ctx, out, args)) {
			out.print("new ");
			ctx.gen(genRuntimeTypeName, type, ctx, out, TypeNameKind.JavascriptImplementation);
			out.print("(");
			ctx.gen(genConstructorOptions, type, ctx, out, org.eclipse.edt.gen.CommonUtilities.genWithoutTypeList(args));
			out.print(")");
		}
	}

	public void genInvocation(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genInvocation, type, ctx, out, args))
			ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, org.eclipse.edt.gen.CommonUtilities.genWithoutTypeList(args));
	}

	public void genDefaultValue(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genDefaultValue, type, ctx, out, args)) {
			if (args.length > 0 && args[0] instanceof TypedElement && ((TypedElement) args[0]).isNullable())
				out.print("null");
			else if (args.length > 0 && args[0] instanceof Expression && ((Expression) args[0]).isNullable())
				out.print("null");
			else if (TypeUtils.isReferenceType(type))
				out.print("null");
			else
				out.print("\"Invalid default value\"");
		}
	}

	public void genRuntimeTypeName(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genRuntimeTypeName, type, ctx, out, args)) {
			// are we looking for the default (java primitive) or specifically java primitive, if it exists
			if (args.length == 0 || args[0] == null || args[0] == TypeNameKind.JavascriptPrimitive) {
				if (ctx.mapsToPrimitiveType(type.getClassifier())) {
					out.print(ctx.getPrimitiveMapping(type.getClassifier()));
					return;
				}
			}
			// are we looking for the java object
			if (args[0] == TypeNameKind.JavascriptObject) {
				if (ctx.mapsToPrimitiveType(type.getClassifier())) {
					String item = ctx.getPrimitiveMapping(type.getClassifier());
					if (ctx.getPrimitiveMapping(item) == null)
						out.print(item);
					else
						out.print(ctx.getPrimitiveMapping(item));
					return;
				}
			}
			// we couldn't resolve the java types, so we have to check for the java implementation name
			if (args[0] == TypeNameKind.JavascriptImplementation) {
				if (ctx.mapsToPrimitiveType(type.getClassifier())) {
					String item = ctx.getPrimitiveMapping(type.getClassifier());
					if (ctx.getPrimitiveMapping(item) == null)
						out.print(item);
					else
						out.print(ctx.getPrimitiveMapping(item));
					return;
				}
			}
			// type an egl implementation name
			if (args[0] == TypeNameKind.EGLImplementation) {
				if (ctx.mapsToNativeType(type.getClassifier())) {
					out.print(ctx.getNativeImplementationMapping(type.getClassifier()));
					return;
				}
			}
			// select the proper default to use. we have run out of options
			if (args[0] == TypeNameKind.JavascriptImplementation)
				out.print(ctx.getNativeImplementationMapping(type.getClassifier()));
			else
				// must be an egl interface name we want
				out.print(ctx.getNativeInterfaceMapping(type.getClassifier()));
		}
	}

	public void genConstructorOptions(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genConstructorOptions, type, ctx, out, args)) {
			// no default
		}
	}

	public void genTypeDependentOptions(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genTypeDependentOptions, type, ctx, out, args)) {
			// no default
		}
	}

	public void genAssignment(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genAssignment, type, ctx, out, args)) {
			// if the lhs is non-nullable but the rhs is nullable, we have a special case
			if (!((Expression) args[0]).isNullable() && ((Expression) args[1]).isNullable()) {
				out.print("(function(x){ return x != null ? (x) : ");
				ctx.gen(genDefaultValue, ((Expression) args[0]).getType(), ctx, out, args);
				out.print("; }");
				out.print("(");
				ctx.gen(genExpression, (Expression) args[1], ctx, out, args);
				out.print(")");
			} else {
				ctx.gen(genExpression, (Expression) args[0], ctx, out, org.eclipse.edt.gen.CommonUtilities.genWithoutTypeList(args));
				out.print(" = ");
				ctx.gen(genExpression, (Expression) args[1], ctx, out, org.eclipse.edt.gen.CommonUtilities.genWithoutTypeList(args));
			}
		}
	}

	public void genSubstringAssignment(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genSubstringAssignment, type, ctx, out, args)) {
			// no default
		}
	}

	public void genSubstringAccess(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genSubstringAccess, type, ctx, out, args)) {
			// no default
		}
	}

	public void genBinaryExpression(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genBinaryExpression, type, ctx, out, args)) {
			// if either side of this expression is nullable, or if there is no direct java operation, we need to use the
			// runtime
			if ((((BinaryExpression) args[0]).getLHS().isNullable() || ((BinaryExpression) args[0]).getRHS().isNullable())
				|| CommonUtilities.getNativeJavaOperation((BinaryExpression) args[0], ctx).length() == 0) {
				out.print(ctx.getNativeImplementationMapping((Type) ((BinaryExpression) args[0]).getOperation().getContainer()) + '.');
				out.print(CommonUtilities.getNativeRuntimeOperationName((BinaryExpression) args[0]));
				out.print("(ezeProgram, ");
				ctx.gen(genExpression, ((BinaryExpression) args[0]).getLHS(), ctx, out, org.eclipse.edt.gen.CommonUtilities.genWithoutTypeList(args));
				out.print(", ");
				ctx.gen(genExpression, ((BinaryExpression) args[0]).getRHS(), ctx, out, org.eclipse.edt.gen.CommonUtilities.genWithoutTypeList(args));
				out.print(")" + CommonUtilities.getNativeRuntimeComparisionOperation((BinaryExpression) args[0]));
			} else {
				ctx.gen(genExpression, ((BinaryExpression) args[0]).getLHS(), ctx, out, org.eclipse.edt.gen.CommonUtilities.genWithoutTypeList(args));
				out.print(CommonUtilities.getNativeJavaOperation((BinaryExpression) args[0], ctx));
				ctx.gen(genExpression, ((BinaryExpression) args[0]).getRHS(), ctx, out, org.eclipse.edt.gen.CommonUtilities.genWithoutTypeList(args));
			}
		}
	}

	public void genUnaryExpression(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genUnaryExpression, type, ctx, out, args)) {
			// we only need to check for minus sign and if found, we need to change it to -()
			if (((UnaryExpression) args[0]).getOperator().equals("-"))
				out.print(((UnaryExpression) args[0]).getOperator() + "(");
			ctx.gen(genExpression, ((UnaryExpression) args[0]).getExpression(), ctx, out, org.eclipse.edt.gen.CommonUtilities.genWithoutTypeList(args));
			// we only need to check for minus sign and if found, we need to change it to -()
			if (((UnaryExpression) args[0]).getOperator().equals("-"))
				out.print(")");
		}
	}

	public void genSignature(Type type, Context ctx, TabbedWriter out, Object... args) {
		// TODO sbg In RBD, the type may be null which has a runtime signature of "V;" -- do we need to handle that, and if
		// so, how?

		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genSignature, type, ctx, out, args))
			out.print(type.getTypeSignature());
	}

	public void genContainerBasedAssignment(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genContainerBasedAssignment, type, ctx, out, args))
			ctx.gen(genAssignment, (Assignment) args[0], ctx, out, args);
	}

	public void genContainerBasedAccessor(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genContainerBasedAccessor, type, ctx, out, args)) {
			// no default
		}
	}

	public void genContainerBasedArrayAccess(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genContainerBasedArrayAccess, type, ctx, out, args))
			ctx.gen(genArrayAccess, (ArrayAccess) args[0], ctx, out, args);
	}

	public void genContainerBasedMemberAccess(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genContainerBasedMemberAccess, type, ctx, out, args))
			ctx.gen(genMemberAccess, (MemberAccess) args[0], ctx, out, args);
	}

	public void genContainerBasedMemberName(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genContainerBasedMemberName, type, ctx, out, args))
			ctx.gen(genMemberName, (MemberName) args[0], ctx, out, args);
	}
}
