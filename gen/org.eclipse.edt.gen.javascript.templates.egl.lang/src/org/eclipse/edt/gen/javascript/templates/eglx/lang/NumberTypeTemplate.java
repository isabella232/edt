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
package org.eclipse.edt.gen.javascript.templates.eglx.lang;

import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.BoxingExpression;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.FixedPrecisionType;
import org.eclipse.edt.mof.egl.IntegerLiteral;
import org.eclipse.edt.mof.egl.IsAExpression;
import org.eclipse.edt.mof.egl.ParameterizableType;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class NumberTypeTemplate extends JavaScriptTemplate {
	
	/* WARNING:   Template methods in this class should use an if/else convention to ensure
	 * that they only process the Number type, otherwise, they should defer to ctx.invokeSuper.
	 */

	// this method gets invoked when there is a specific fixed precision needed
	public void genDefaultValue(FixedPrecisionType type, Context ctx, TabbedWriter out) {
		if (type.getTypeSignature().equalsIgnoreCase("eglx.lang.ENumber")) {
			processDefaultValue(type, ctx, out);
		}
		else {
			ctx.invokeSuper(this, genDefaultValue, type, ctx, out);
		}
	}

	// this method gets invoked when there is a generic (unknown) fixed precision needed
	public void genDefaultValue(Type type, Context ctx, TabbedWriter out) {
		if (type.getTypeSignature().equalsIgnoreCase("eglx.lang.ENumber")) {
			processDefaultValue(type, ctx, out);
		}
		else {
			ctx.invokeSuper(this, genDefaultValue, type, ctx, out);
		}
	}

	public void processDefaultValue(Type type, Context ctx, TabbedWriter out) {
		out.print(Constants.JSRT_EGL_NAMESPACE + ctx.getNativeMapping("eglx.lang.ENumber") + ".ZERO");
	}

	public Boolean isMathLibDecimalBoxingWanted(Type type, Context ctx) {
		return !type.getTypeSignature().equalsIgnoreCase("eglx.lang.ENumber");
	}

	// this method gets invoked when there is a specific fixed precision needed
	public void genSignature(FixedPrecisionType type, Context ctx, TabbedWriter out) {
		if (type.getTypeSignature().equalsIgnoreCase("eglx.lang.ENumber")) {
			String signature = "N" + type.getLength() + ":" + type.getDecimals() + ";";
			out.print(signature);
		}
		else {
			ctx.invokeSuper(this, genSignature, type, ctx, out);
		}
	}

	// this method gets invoked when there is a generic (unknown) fixed precision needed
	public void genSignature(ParameterizableType type, Context ctx, TabbedWriter out) {
		if (type.getTypeSignature().equalsIgnoreCase("eglx.lang.ENumber")) {
			String signature = "N;";
			out.print(signature);
		}
		else {
			ctx.invokeSuper(this, genSignature, type, ctx, out);
		}
	}

	protected boolean needsConversion(Type fromType, Type toType) {
		boolean result = true;
		if (TypeUtils.isNumericType(fromType) && !CommonUtilities.needsConversion(fromType, toType))
			result = CommonUtilities.isJavaScriptBigDecimal(toType);
		return result;
	}
	

	public void genConversionOperation(Type type, Context ctx, TabbedWriter out, AsExpression arg) {
		if (type.getTypeSignature().equalsIgnoreCase("eglx.lang.ENumber") && (arg.getConversionOperation() != null)) {
			
			Type toType = arg.getEType();
			Type fromType = arg.getObjectExpr().getType();
			
			out.print(ctx.getNativeImplementationMapping(toType) + '.');
			out.print(CommonUtilities.getOpName(ctx, arg.getConversionOperation()));
			out.print("(");
			Expression objectExpr = arg.getObjectExpr();
			if (objectExpr instanceof BoxingExpression){
				objectExpr = ((BoxingExpression)objectExpr).getExpr();
			}
			ctx.invoke(genExpression, objectExpr, ctx, out);
			ctx.invoke(genTypeDependentOptions, arg.getEType(), ctx, out, arg);
			out.print(")");
//						BoxingExpression boxingExpr = IrFactory.INSTANCE.createBoxingExpression();
//						boxingExpr.setExpr(arg.getObjectExpr());
//						ctx.invoke(genExpression, boxingExpr, ctx, out);
		} 
		else {
		// we need to invoke the logic in type template to call back to the other conversion situations
		ctx.invokeSuper(this, genConversionOperation, type, ctx, out, arg);
		}
	}

	public void genTypeDependentOptions(ParameterizableType type, Context ctx, TabbedWriter out, AsExpression arg) {
		if (type.getTypeSignature().equalsIgnoreCase("eglx.lang.ENumber")) {
			out.print(", ");
			// if we get here, then we have been given an integer literal, to be represented as a FixedPrecisionType. So, we must
			// set the dependend options to be a list of nines
			if (arg.getObjectExpr() instanceof IntegerLiteral) {
				String value = ((IntegerLiteral) arg.getObjectExpr()).getValue();
				if (value.startsWith("-"))
					value = value.substring(1);
				if (value.length() > 4)
					out.print("egl.javascript.BigDecimal.prototype.NINES[8]");
				else
					out.print("egl.javascript.BigDecimal.prototype.NINES[3]");
			} else
				out.print("egl.javascript.BigDecimal.prototype.NINES[8]");
		}
		else
			ctx.invokeSuper(this, genTypeDependentOptions, type, ctx, out, arg);
	}

	public void genTypeDependentOptions(ParameterizableType type, Context ctx, TabbedWriter out) {
		if (type.getTypeSignature().equalsIgnoreCase("eglx.lang.ENumber")) {
			out.print(", ");
			out.print("egl.javascript.BigDecimal.prototype.NINES[8]");
		}
		else
			ctx.invokeSuper(this, genTypeDependentOptions, type, ctx, out);
	}

	public void genBinaryExpression(Type type, Context ctx, TabbedWriter out, BinaryExpression arg) {
		if (type.getTypeSignature().equalsIgnoreCase("eglx.lang.ENumber")) {
			out.print(getNativeStringPrefixOperation(arg));
			ctx.invoke(genExpression, arg.getLHS(), ctx, out, arg.getOperation().getParameters().get(0));
			out.print(getNativeStringOperation(arg));
			ctx.invoke(genExpression, arg.getRHS(), ctx, out, arg.getOperation().getParameters().get(1));
			out.print(getNativeStringPostfixOperation(arg));
		}
		else
			ctx.invokeSuper(this, genBinaryExpression, type, ctx, out, arg);
	}
	
	public void genIsaExpression(Type type, Context ctx, TabbedWriter out, IsAExpression arg) {
		if (type.getTypeSignature().equalsIgnoreCase("eglx.lang.ENumber")) {
			if (TypeUtils.Type_Number.equals(type.getTypeSignature())) {
				out.print("egl.is(");
				ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
				if (arg.getObjectExpr().getType().getTypeSignature().equalsIgnoreCase("eglx.lang.ENumber")) {
					out.print(".eze$$value");
				}
				out.print(", \"NUMERIC\",egl.isnumeric)");
			}
			else {
				ctx.invokeSuper(this, genIsaExpression, type, ctx, out, arg);
			}
		}
		else
			ctx.invokeSuper(this, genIsaExpression, type, ctx, out, arg);

	}

	@SuppressWarnings("static-access")
	protected String getNativeStringPrefixOperation(BinaryExpression expr) {
		String op = expr.getOperator();
		if (op.equals(expr.Op_PLUS))
			return "egl.addEGLNumber(";
		if (op.equals(expr.Op_MINUS))
			return "egl.subtractEGLNumber(";
		if (op.equals(expr.Op_MULTIPLY))
			return "egl.multiplyEGLNumber(";
		if (op.equals(expr.Op_DIVIDE))
			return "egl.divideEGLNumber(";		
		if (op.equals(expr.Op_MODULO))
			return "egl.remainderEGLNumber(";
		if (op.equals(expr.Op_EQ))
			return "egl.compareEGLNumbers(";
		if (op.equals(expr.Op_NE))
			return "egl.compareEGLNumbers(";
		if (op.equals(expr.Op_LT))
			return "egl.compareEGLNumbers(";
		if (op.equals(expr.Op_GT))
			return "egl.compareEGLNumbers(";
		if (op.equals(expr.Op_LE))
			return "egl.compareEGLNumbers(";
		if (op.equals(expr.Op_GE))
			return "egl.compareEGLNumbers(";
		if (op.equals(expr.Op_POWER))
			return "egl.powEGLNumber(";
		return "(";
	}

	@SuppressWarnings("static-access")
	protected String getNativeStringOperation(BinaryExpression expr) {
		String op = expr.getOperator();
		// these are the defaults for what can be handled by the java string class
		if (op.equals(expr.Op_PLUS))
			return ", " + eglNumberTypeArg(expr.getLHS()) + ", ";
		if (op.equals(expr.Op_MINUS))
			return ", " + eglNumberTypeArg(expr.getLHS()) + ", ";
		if (op.equals(expr.Op_MULTIPLY))
			return ", " + eglNumberTypeArg(expr.getLHS()) + ", ";
		if (op.equals(expr.Op_DIVIDE))
			return ", " + eglNumberTypeArg(expr.getLHS()) + ", ";
		if (op.equals(expr.Op_EQ))
			return ", " + eglNumberTypeArg(expr.getLHS()) + ", ";
		if (op.equals(expr.Op_NE))
			return ", " + eglNumberTypeArg(expr.getLHS()) + ", ";
		if (op.equals(expr.Op_LT))
			return ", " + eglNumberTypeArg(expr.getLHS()) + ", ";
		if (op.equals(expr.Op_GT))
			return ", " + eglNumberTypeArg(expr.getLHS()) + ", ";
		if (op.equals(expr.Op_LE))
			return ", " + eglNumberTypeArg(expr.getLHS()) + ", ";
		if (op.equals(expr.Op_GE))
			return ", " + eglNumberTypeArg(expr.getLHS()) + ", ";
		if (op.equals(expr.Op_AND))
			return " && ";
		if (op.equals(expr.Op_OR))
			return " || ";
		if (op.equals(expr.Op_CONCAT))
			return " + ";
		if (op.equals(expr.Op_MODULO))
			return ", " + eglNumberTypeArg(expr.getLHS()) + ", ";
		if (op.equals(expr.Op_POWER))
			return ", " + eglNumberTypeArg(expr.getLHS().getType()) + ", ";
		return "";
	}

	@SuppressWarnings("static-access")
	protected String getNativeStringPostfixOperation(BinaryExpression expr) {
		String op = expr.getOperator();
		if (op.equals(expr.Op_PLUS))
			return ", " + eglNumberTypeArg(expr.getRHS()) + ")";
		if (op.equals(expr.Op_MINUS))
			return ", " + eglNumberTypeArg(expr.getRHS()) + ")";
		if (op.equals(expr.Op_MULTIPLY))
			return ", " + eglNumberTypeArg(expr.getRHS()) + ")";
		if (op.equals(expr.Op_DIVIDE))
			return ", " + eglNumberTypeArg(expr.getRHS()) + ")";
		if (op.equals(expr.Op_EQ))
			return ", " + eglNumberTypeArg(expr.getRHS()) + ") == 0";
		if (op.equals(expr.Op_NE))
			return ", " + eglNumberTypeArg(expr.getRHS()) + ") !=0";
		if (op.equals(expr.Op_LT))
			return ", " + eglNumberTypeArg(expr.getRHS()) + ") < 0";
		if (op.equals(expr.Op_GT))
			return ", " + eglNumberTypeArg(expr.getRHS()) + ") > 0";
		if (op.equals(expr.Op_LE))
			return ", " + eglNumberTypeArg(expr.getRHS()) + ") <= 0";
		if (op.equals(expr.Op_GE))
			return ", " + eglNumberTypeArg(expr.getRHS()) + ") >= 0";
		if (op.equals(expr.Op_MODULO))
			return ", " + eglNumberTypeArg(expr.getRHS()) + ")";
		if (op.equals(expr.Op_POWER))
			return ", " + eglNumberTypeArg(expr.getRHS()) + ")";
		return ")";
	}

	/**
	 * Returns the value that'll identify the kind of argument passed to the egl.*EGLNumber functions.
	 * <UL>
	 * <LI>-1 means we must inspect the value to learn its type
	 * <LI>0 means it's a JavaScript Number and not an EGL float or smallfloat
	 * <LI>1 means it's an EGL BigDecimal
	 * <LI>2 means it's an EGL float or smallfloat
	 * </UL>
	 */
	private static String eglNumberTypeArg( Expression expr )
	{
//		Expression e = (expr instanceof AsExpression) ? ((AsExpression)expr).getObjectExpr() : expr;
//		return eglNumberTypeArg(e.getType());
		return eglNumberTypeArg(expr.getType());
	}	
	
	/**
	 * Returns the value that'll identify the kind of argument passed to the egl.*EGLNumber functions.
	 * <UL>
	 * <LI>-1 means we must inspect the value to learn its type
	 * <LI>0 means it's a JavaScript Number and not an EGL float or smallfloat
	 * <LI>1 means it's an EGL BigDecimal
	 * <LI>2 means it's an EGL float or smallfloat
	 * </UL>
	 */
	private static String eglNumberTypeArg( Type type )
	{

		int kind = TypeUtils.getTypeKind(type);
		if ( (type.getTypeSignature().equalsIgnoreCase("eglx.lang.ENumber")) || isCharacterType( type ) )
		{
			return "-1";
		}
		else if ( isBigDecimalType( type ) )
		{
			return "1";
		}
		else if ( kind == TypeUtils.TypeKind_FLOAT || kind == TypeUtils.TypeKind_SMALLFLOAT )
		{
			return "2";
		}
		else
		{
			// int, smallint, null, date
			return "0";
		}
	}
	
	private static boolean isCharacterType( Type type )
	{
		switch ( TypeUtils.getTypeKind(type) )
		{
			case TypeUtils.TypeKind_CHAR:
			case TypeUtils.TypeKind_DBCHAR:
			case TypeUtils.TypeKind_MBCHAR:
			case TypeUtils.TypeKind_UNICODE:
			case TypeUtils.TypeKind_LIMITEDSTRING:
			case TypeUtils.TypeKind_STRING:
				return true;
				
			default:
				return false;
		}
	}
	
	/**
	 * @param type
	 * @return True if so
	 */
	private static boolean isFixedLengthNumericType( Type type )
	{
		switch ( TypeUtils.getTypeKind(type) )
		{
			case TypeUtils.TypeKind_MONEY:
			case TypeUtils.TypeKind_NUM:
			case TypeUtils.TypeKind_NUMC:
			case TypeUtils.TypeKind_BIN:
			case TypeUtils.TypeKind_DECIMAL:
			case TypeUtils.TypeKind_PACF:
				return true;
				
			default:
				return false;
		}
	}
	
	/**
	 * checks if the type is to use the egl.javascript.BigDecimal class
	 * in the generated JavaScript code
	 * 
	 * @param type
	 * @return True if so
	 */
	private static boolean isBigDecimalType( Type type )
	{
		return ( TypeUtils.getTypeKind(type) == TypeUtils.TypeKind_BIGINT
				|| isFixedLengthNumericType( type ) );
	}
}
