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

import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class AsExpressionTemplate extends JavascriptTemplate {

	// All conversions are governed by the Operation associated with the AsExpression argument asExpr. This operation has
	// come from the EGL definition that contained the operation. The conversion code generation is implemented in the
	// TypeTemplate subclass that has been mapped to from the EGL type that contained the operation, i.e. the Operation's
	// Container. In this way each particular type template implements exactly what was defined by the EGL type for that
	// template.
	public void genExpression(AsExpression asExpr, Context ctx, TabbedWriter out, Object... args) {
		Operation conOp = asExpr.getConversionOperation();
		if (conOp != null && needsConversion(conOp))
			ctx.gen(
				"gen" + CommonUtilities.getEglNameForTypeCamelCase(conOp.getReturnType()) + "From"
					+ CommonUtilities.getEglNameForTypeCamelCase(conOp.getParameters().get(0).getType()) + "Conversion", (Type) conOp.getContainer(), ctx, out,
				asExpr);
		else if (TypeUtils.Type_ANY.equals(asExpr.getObjectExpr().getType())) {
			out.print("egl.convertAnyToType(");
			ctx.gen(genExpression, asExpr.getObjectExpr(), ctx, out);
			out.print(", ");
			out.print(quoted(asExpr.getEType().getTypeSignature()));
			out.print(")");
		} else
			ctx.gen(genExpression, asExpr.getObjectExpr(), ctx, out, args);
	}

	public boolean needsConversion(Operation conOp) {
		Type fromType = conOp.getParameters().get(0).getType();
		Type toType = conOp.getReturnType();
		// always do conversions if parameterized types are involved
		if (toType.equals(TypeUtils.Type_DECIMAL)
			|| toType.equals(TypeUtils.Type_TIMESTAMP)
			|| (TypeUtils.isTextType(toType) && !CommonUtilities.getEglNameForTypeCamelCase(toType)
				.equals(CommonUtilities.getEglNameForTypeCamelCase(fromType))))
			return true;
		if (conOp.isWidenConversion()) {
			if (TypeUtils.isNumericType(fromType)) {
				int kind = TypeUtils.getTypeKind(toType);
				return kind == TypeUtils.TypeKind_DECIMAL || kind == TypeUtils.TypeKind_BIGINT || kind == TypeUtils.TypeKind_DATE
					|| TypeUtils.isTextType(toType);
			}
			if (TypeUtils.isTextType(toType)
				&& !CommonUtilities.getEglNameForTypeCamelCase(toType).equals(CommonUtilities.getEglNameForTypeCamelCase(fromType)))
				return true;
		} else {
			if (TypeUtils.isNumericType(fromType))
				return !(fromType.equals(TypeUtils.Type_INT) && toType.equals(TypeUtils.Type_SMALLINT));
			else if (fromType.equals(TypeUtils.Type_TIMESTAMP))
				return !toType.equals(TypeUtils.Type_TIMESTAMP);
		}
		return false;
	}
}
