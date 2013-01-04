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
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.*;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class TypeTemplate extends JavaScriptTemplate {

	public void preGen(Type type, Context ctx) {
		// types may override this validation for specific checking
	}

	public Boolean isAssignmentArrayMatchingWanted(Type type, Context ctx) {
		// types can override this to cause type matching of array literals to be ignored.
		return true;
	}

	public Boolean isAssignmentBreakupWanted(Type type, Context ctx, Assignment expr) {
		// types can override this to cause an compound assignment expression to be broken up 
		// the arg contains the operation being asked about
		if (expr.getOperator() .equals("**=") || expr.getOperator() .equals("?:=") || expr.getOperator() .equals("::="))
			return true;
		else
			return false;
	}

	public Boolean isListReorganizationWanted(Type type, Context ctx, Expression expr) {
		// types can override this to cause list reorganization to be done
		return true;  //TODO JS gen will require changes before this can be used
	}

	public Boolean isMathLibDecimalBoxingWanted(Type type, Context ctx) {
		return true;
	}
	
	public Boolean isStringLibFormatBoxingWanted(Type type, Context ctx) {
		return true;
	}

	public void genInstantiation(Type type, Context ctx, TabbedWriter out) {
		out.print("new ");
		ctx.invoke(genRuntimeTypeName, type, ctx, out, TypeNameKind.JavascriptImplementation);
		out.print("(");
		ctx.invoke(genConstructorOptions, type, ctx, out);
		out.print(")");
	}

	public void genInstantiation(Type type, Context ctx, TabbedWriter out, Field arg) {
		ctx.invoke(genInstantiation, type, ctx, out);
	}

	public void genInvocation(Type type, Context ctx, TabbedWriter out, InvocationExpression arg) {
		ctx.invoke(genInvocation, arg, ctx, out);
	}

	public void genDefaultValue(Type type, Context ctx, TabbedWriter out, TypedElement arg) {
		if (arg.isNullable())
			out.print("null");
		else
			ctx.invoke(genDefaultValue, type, ctx, out);
	}

	public void genDefaultValue(Type type, Context ctx, TabbedWriter out, Expression arg) {
		if (arg.isNullable())
			out.print("null");
		else
			ctx.invoke(genDefaultValue, type, ctx, out);
	}

	public void genDefaultValue(Type type, Context ctx, TabbedWriter out) {
		if (TypeUtils.isReferenceType(type))
			ctx.invoke(genInstantiation, type, ctx, out); //out.print("null");
		else
			out.print("\"Invalid default value\"");
	}

	public void genRuntimeTypeName(Type type, Context ctx, TabbedWriter out) {
		ctx.invoke(genRuntimeTypeName, type, ctx, out, TypeNameKind.JavascriptPrimitive);
	}

	public void genRuntimeTypeName(Type type, Context ctx, TabbedWriter out, TypeNameKind arg) {
		// are we looking for the default (java primitive) or specifically java primitive, if it exists
		if (arg == TypeNameKind.JavascriptPrimitive) {
			if (ctx.mapsToPrimitiveType(type.getClassifier())) {
				out.print(ctx.getPrimitiveMapping(type.getClassifier()));
				return;
			}
		}
		// are we looking for the java object
		if (arg == TypeNameKind.JavascriptObject) {
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
		if (arg == TypeNameKind.JavascriptImplementation) {
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
		if (arg == TypeNameKind.EGLImplementation) {
			if (ctx.mapsToNativeType(type.getClassifier())) {
				out.print(ctx.getNativeImplementationMapping(type.getClassifier()));
				return;
			}
		}
		// select the proper default to use. we have run out of options
		if (arg == TypeNameKind.JavascriptImplementation)
			out.print(ctx.getNativeImplementationMapping(type.getClassifier()));
		else
			// must be an egl interface name we want
			out.print(ctx.getNativeInterfaceMapping(type.getClassifier()));
	}

	public void genConstructorOptions(Type type, Context ctx, TabbedWriter out) {
		// no default
	}

	public void genTypeDependentOptions(Type type, Context ctx, TabbedWriter out, AsExpression arg) {
		ctx.invoke(genTypeDependentOptions, type, ctx, out);
	}

	public void genTypeDependentOptions(Type type, Context ctx, TabbedWriter out) {
		// no default
	}
	
	public void genTypeDependentPatterns(Type type, Context ctx, TabbedWriter out) {
		// no default
	}
	
	public void genInitializeStatement(Type type, Context ctx, TabbedWriter out, Field arg) {
		if (arg.getInitializerStatements() != null) {
			ctx.invoke(genStatementNoBraces, arg.getInitializerStatements(), ctx, out);
		}
	}


	public void genAssignment(Type type, Context ctx, TabbedWriter out, Expression arg1, Expression arg2, String arg3) {
		// if the lhs is non-nullable but the rhs is nullable, we have a special case
		if (!arg1.isNullable() && arg2.isNullable()) {
			ctx.invoke(genExpression, arg1, ctx, out);
			out.print(arg3);
			out.print("egl.checkNull(");
			assignmentSource(arg1, arg2, ctx, out);
			out.print(")");
		} else {
			ctx.invoke(genExpression, arg1, ctx, out);
			out.print(arg3);
			ctx.putAttribute(arg2, Constants.SubKey_recordToAnyAssignment, true);
			assignmentSource(arg1, arg2, ctx, out);
			ctx.putAttribute(arg2, Constants.SubKey_recordToAnyAssignment, false);
		}
	}
	
	public static void assignmentSource( Expression lhs, Expression rhs, Context ctx, TabbedWriter out )
	{
		// Generate something special for the RHS of an assignment when:
		//  1) assigning bytes(x) to bytes(y), and x < y
		//  2) assigning bytes to bytes(x)
		//  3) assigning 'any as bytes(x)' to bytes(x)
		//  4) assigning 'any as bytes' to bytes
		//  5) assigning a function to a delegate
		Type lhsType = lhs.getType();
		Type rhsType = rhs.getType();
		if ( rhsType != null && TypeUtils.getTypeKind( rhsType ) == TypeUtils.TypeKind_BYTES
				&& lhsType != null && TypeUtils.getTypeKind( lhsType ) == TypeUtils.TypeKind_BYTES )
		{
			if ( ( lhsType instanceof SequenceType && rhsType instanceof SequenceType
						&& ((SequenceType)rhsType).getLength() < ((SequenceType)lhsType).getLength() )
					|| ( rhs instanceof AsExpression && ((AsExpression)rhs).getObjectExpr().getType() instanceof ParameterizableType
							&& TypeUtils.getTypeKind( ((AsExpression)rhs).getObjectExpr().getType() ) == TypeUtils.TypeKind_BYTES ) )
			{
				assignShorterBytesToLongerBytes( rhs, rhsType, lhs, lhsType, ctx, out );
			}
			else if ( rhs instanceof AsExpression 
					&& TypeUtils.getTypeKind( ((AsExpression)rhs).getObjectExpr().getType() ) == TypeUtils.TypeKind_ANY )
			{
				int lhsLength = lhsType instanceof SequenceType ? ((SequenceType)lhsType).getLength() : 0;

				ctx.invoke( genRuntimeTypeName, rhsType, ctx, out, TypeNameKind.EGLImplementation );
				out.print( ".ezeAssignFromAny(" );
				ctx.invoke( genTypeBasedExpression, rhs, ctx, out, lhs.getType() );
				out.print( ", " );
				if ( lhsLength > 0 )
				{
					if ( (lhs instanceof MemberName || lhs instanceof MemberAccess) 
							&& ctx.get( "generating declaration of " + CommonUtilities.getMember( lhs ) + CommonUtilities.getMember( lhs ).hashCode() ) != null )
					{
						ctx.invoke( genRuntimeTypeName, rhsType, ctx, out, TypeNameKind.EGLImplementation );
						out.print( ".ezeNew(" );
						out.print( lhsLength );
						out.print( ')' );
					}
					else
					{
						ctx.invoke( genExpression, lhs, ctx, out );
					}
					out.print( ", " );
					out.print( lhsLength );
				}
				else
				{
					out.print( "null" );
				}
				out.print( ')' );
			}
			else
			{
				ctx.invoke( genTypeBasedExpression, rhs, ctx, out, lhs.getType() );
			}
		}
		else if ( lhsType instanceof Delegate 
				&& (rhs instanceof MemberName || rhs instanceof MemberAccess)
				&& CommonUtilities.getMember( rhs ) instanceof Function )
		{
			String functionSig = ((Function)CommonUtilities.getMember( rhs )).getSignature();
			ctx.put( "Delegate_signature_for_function_" + functionSig, lhsType );
			ctx.invoke( genTypeBasedExpression, rhs, ctx, out, lhs.getType() );
			ctx.remove( "Delegate_signature_for_function_" + functionSig );
		}
		else
		{
			ctx.invoke( genTypeBasedExpression, rhs, ctx, out, lhs.getType() );
		}
	}
	
	private static void assignShorterBytesToLongerBytes( Expression rhs, Type rhsType, Expression lhs, Type lhsType, Context ctx, TabbedWriter out )
	{
		int lhsLength = ((SequenceType)lhsType).getLength();
		if ( rhs instanceof BytesLiteral )
		{
			StringBuilder value = new StringBuilder( ((BytesLiteral)rhs).getValue() );
			while ( value.length() / 2 < lhsLength )
			{
				value.append( "00" );
			}
			BytesLiteral newRHS = ctx.getFactory().createBytesLiteral();
			newRHS.setValue( value.toString() );
			ctx.invoke( genExpression, newRHS, ctx, out );
		}
		else
		{
			ctx.invoke( genRuntimeTypeName, rhsType, ctx, out, TypeNameKind.EGLImplementation );
			out.print( ".ezeAssignToLonger(" );
			if ( (lhs instanceof MemberName || lhs instanceof MemberAccess) 
					&& ctx.get( "generating declaration of " + CommonUtilities.getMember( lhs ) + CommonUtilities.getMember( lhs ).hashCode() ) != null )
			{
				ctx.invoke( genRuntimeTypeName, rhsType, ctx, out, TypeNameKind.EGLImplementation );
				out.print( "egl.eglx.lang.EBytes.ezeNew(" );
				out.print( lhsLength );
				out.print( ')' );
			}
			else
			{
				ctx.invoke( genExpression, lhs, ctx, out );
			}
			out.print( ", " );
			out.print( lhsLength );
			out.print( ", " );
			ctx.invoke( genTypeBasedExpression, rhs, ctx, out, lhs.getType() );
			out.print( ')' );
		}
	}

	public void genConversionOperation(Type type, Context ctx, TabbedWriter out, AsExpression arg) {
		if (arg.getConversionOperation() != null) {
			Operation op = arg.getConversionOperation();
			out.print("egl.eglx.lang.convert(");			
			out.print(ctx.getNativeImplementationMapping((Classifier) op.getContainer()) + '.');
			out.print(CommonUtilities.getOpName(ctx, op));
			out.print(", [");
			Expression objectExpr = arg.getObjectExpr();
			if (objectExpr instanceof BoxingExpression){
				objectExpr = ((BoxingExpression)objectExpr).getExpr();
			}
			
			Member member = CommonUtilities.getMember(objectExpr);
			ParameterKind paramKind = member == null ? null : (ParameterKind)ctx.getAttribute(member, org.eclipse.edt.gen.Constants.SubKey_functionArgumentTemporaryVariable);
			if (paramKind == ParameterKind.PARM_OUT) {
				out.print("egl.unboxAny(");
			}
			ctx.invoke(genExpression, objectExpr, ctx, out);
			if(objectExpr.getType() instanceof Record && ctx.getAttribute(arg, "function parameter is const in") == null){
				out.print(".eze$$clone()");
			}
			if (paramKind == ParameterKind.PARM_OUT) {
				out.print(')');
			}
			ctx.invoke(genTypeDependentPatterns, arg.getObjectExpr().getType(), ctx, out);
			
			String typeSignature = arg.getObjectExpr().getType().getClassifier().getTypeSignature();			
			if ( "asAny".equalsIgnoreCase(op.getCaseSensitiveName()) ) {
				out.print(",\"");
				ctx.put( Constants.SubKey_isaSignature, "true" );
				ctx.invoke(genSignature, arg.getObjectExpr().getType(), ctx, out, arg);
				ctx.remove( Constants.SubKey_isaSignature );
				out.print("\"");
			} else if ( ((ctx.getPrimitiveMapping(typeSignature) == null) && (!"eglx.lang.ENumber".equalsIgnoreCase(typeSignature))) || ("eglx.lang.EDecimal".equalsIgnoreCase(typeSignature))) {
				//TODO shouldn't have to special case ENumber and Decimal
				out.print(",\"");
				ctx.invoke(genSignature, arg.getObjectExpr().getType(), ctx, out, arg);
				out.print("\"");
			} 
			ctx.invoke(genTypeDependentOptions, arg.getEType(), ctx, out, arg);
			out.print("])");
		} else if (ctx.mapsToPrimitiveType(arg.getEType())) {
			ctx.invoke(genRuntimeTypeName, arg.getEType(), ctx, out, TypeNameKind.EGLImplementation);
			out.print(".ezeCast(");
			if (arg.getObjectExpr().getType() != TypeUtils.Type_ANY) {
				BoxingExpression boxingExpr = IrFactory.INSTANCE.createBoxingExpression();
				boxingExpr.setExpr(arg.getObjectExpr());
				ctx.invoke(genExpression, boxingExpr, ctx, out);
			}
			else {
				ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
			}
			out.print(", ");
			out.print(arg.getObjectExpr().isNullable());
			ctx.invoke(genTypeDependentOptions, arg.getEType(), ctx, out, arg);
			out.print(")");
		}else {
			if( arg.getType() != TypeUtils.Type_ANY )
				out.print(eglnamespace + "eglx.lang.EAny.ezeCast("); // TODO sbg need to dynamically get class name
			if (arg.getObjectExpr().getType() != TypeUtils.Type_ANY) {
				BoxingExpression boxingExpr = IrFactory.INSTANCE.createBoxingExpression();
				boxingExpr.setExpr(arg.getObjectExpr());
				ctx.invoke(genExpression, boxingExpr, ctx, out);
			}
			else {
				ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
			}
			if( arg.getType() != TypeUtils.Type_ANY ){
				out.print(", ");
				ctx.invoke(genRuntimeTypeName, arg.getEType(), ctx, out, TypeNameKind.JavascriptImplementation);
				out.print(")");
			}			
		}
	}

	
	public void genTypeBasedAssignment(Type type, Context ctx, TabbedWriter out, Assignment arg) {
		String operator = "=";
		if (arg.getOperator() != null && arg.getOperator().length() > 0) {
			operator = arg.getOperator();
		}
		// For compound assignments like lhs += rhs, we unravel them into lhs = lhs + rhs in JavaScript
		if ((operator != null) && (operator.length() > 1) && (operator.endsWith("="))) { 
			String op = operator.substring(0,operator.length()-1);
			BinaryExpression binExpr = IrFactory.INSTANCE.createBinaryExpression();
			binExpr.setLHS(arg.getLHS());
			binExpr.setOperator(op);
			binExpr.setRHS(arg.getRHS());
			ctx.invoke(genAssignment, arg.getLHS(), ctx, out, binExpr, " = ");
		}
		else {
			ctx.invoke(genAssignment, arg.getLHS(), ctx, out, arg.getRHS(), " " + CommonUtilities.getNativeJavaScriptAssignment(operator) + " ");
		}
	}

	public void genReturnStatement(Type type, Context ctx, TabbedWriter out, ReturnStatement arg) {
		ctx.invoke(genReturnStatement, arg, ctx, out);
	}

	public void genBinaryExpression(Type type, Context ctx, TabbedWriter out, BinaryExpression arg) throws GenerationException {
		// if either side of this expression is nullable, or if there is no direct java operation, we need to use the runtime
		if ((arg.getLHS().isNullable() || arg.getRHS().isNullable()) || (arg.getLHS() instanceof ArrayAccess || arg.getRHS() instanceof ArrayAccess)
				|| CommonUtilities.getNativeJavaScriptOperation(arg, ctx).length() == 0) {
			out.print(ctx.getNativeImplementationMapping((Type) arg.getOperation().getContainer()) + '.');
			out.print(CommonUtilities.getNativeRuntimeOperationName(arg));
			out.print("(");
			ctx.invoke(genExpression, arg.getLHS(), ctx, out, arg.getOperation().getParameters().get(0));
			out.print(", ");
			ctx.invoke(genExpression, arg.getRHS(), ctx, out, arg.getOperation().getParameters().get(1));
			out.print(")" + CommonUtilities.getNativeRuntimeComparisionOperation(arg));
		} else {
			ctx.invoke(genExpression, arg.getLHS(), ctx, out);
			out.print(CommonUtilities.getNativeJavaScriptOperation(arg, ctx));
			ctx.invoke(genExpression, arg.getRHS(), ctx, out);
		}
	}

	public void genUnaryExpression(Type type, Context ctx, TabbedWriter out, UnaryExpression arg) throws GenerationException {
		if (arg.getExpression().isNullable()) {
			out.print(ctx.getNativeImplementationMapping((Type) arg.getOperation().getContainer()) + ".");
			out.print(CommonUtilities.getNativeRuntimeOperationName(arg));
			out.print("(");
			ctx.invoke(genExpression, arg.getExpression(), ctx, out, arg.getOperation().getParameters().get(0));
			out.print(")");
		} else 
		// we only need to check for minus sign and if found, we need to change it to -()
		if (arg.getOperator().equals("-") || arg.getOperator().equals("!") || arg.getOperator().equals("~")) {
			out.print(arg.getOperator() + "(");
			ctx.invoke(genExpression, arg.getExpression(), ctx, out);
			out.print(")");
		} else
			ctx.invoke(genExpression, arg.getExpression(), ctx, out);
	}
	
	
	public void genIsaExpression(Type type, Context ctx, TabbedWriter out, IsAExpression arg) {
		Expression lhs = arg.getObjectExpr();
		Type lhsType = lhs.getType();
		Type isaType = arg.getEType();
		String lhsTypeSig = lhsType.getTypeSignature();
		String isaTypeSig = isaType.getTypeSignature();

		
		// ISA is usually simple, but there are some things we have to check
		// specially: loose types, ExternalTypes, Any, and AnyException.  Also 
		// beware that Widget (an ET) is compatible with RUIWidget (a kind of handler).
		//TODO expressiongenerator has the impl. for these special caess
		
		if ( (lhsTypeSig.equals( "eglx.lang.AnyException" ) && isaType instanceof NamedElement) ||
				(isaType instanceof ExternalType) )
		{
			out.print("(");
			ctx.invoke(genExpression, lhs, ctx, out);
			out.print( " instanceof " );
			ctx.invoke(genRuntimeTypeName, isaType, ctx, out, TypeNameKind.JavascriptObject);
			out.print(")");
		}
		else if (lhsTypeSig.equalsIgnoreCase(isaTypeSig)) {
			if (lhs.isNullable()) {
				out.print("(");
				ctx.invoke(genExpression, lhs, ctx, out);
				out.print(" == null ? false : true)");
			} else
				out.print("true");
		}
		else if (lhsType.getClassifier() != null && lhsType.getClassifier().getTypeSignature().equalsIgnoreCase(isaTypeSig)) {
			if (lhs.isNullable()) {
				out.print("(");
				ctx.invoke(genExpression, lhs, ctx, out);
				out.print(" == null ? false : true)");
			} else
				out.print("true");
		} 
		else if (lhsType.getClassifier() != null && isaType.getClassifier() != null
			&& lhsType.getClassifier().getTypeSignature().equalsIgnoreCase(isaType.getClassifier().getTypeSignature())) {
			out.print("false");
		}
		else if ( lhs instanceof IntegerLiteral ) {
			out.print( lhsTypeSig.equalsIgnoreCase(isaTypeSig) ? "true" : "false" );
		} else if (ctx.mapsToPrimitiveType(lhs.getType())) {
			out.print("egl.isa(");
			ctx.invoke(genExpression, lhs, ctx, out);
			out.print(", ");
			out.print("\"");
			ctx.put( Constants.SubKey_isaSignature, "true" );
			ctx.invoke(genSignature, isaType, ctx, out);
			ctx.remove( Constants.SubKey_isaSignature );
			out.print("\", \"");
			ctx.invoke(genSignature, lhsType, ctx, out);
			out.print("\"");
			out.print(")");
		}
		else {
			out.print("egl.isa(");
			ctx.invoke(genExpression, lhs, ctx, out);
			out.print(", ");
			out.print("\"");
			ctx.put( Constants.SubKey_isaSignature, "true" );
			ctx.invoke(genSignature, isaType, ctx, out);
			ctx.remove( Constants.SubKey_isaSignature );
			out.print("\"");
			out.print(")");
		}
	}

	public void genSignature(Type type, Context ctx, TabbedWriter out, TypedElement arg) {
		/* In EDT, nullable is a characteristic of the field, not the type, so this is no
		 * longer appropriate.....
		  if (arg.isNullable())
			out.print("?");
		 */
		ctx.invoke(genSignature, type, ctx, out);
	}

	public void genSignature(Type type, Context ctx, TabbedWriter out, Expression arg) {
		/* In EDT, nullable is a characteristic of the field, not the type, so this is no
		 * longer appropriate.....
		  if (arg.isNullable())
			out.print("?");
		 */
		ctx.invoke(genSignature, type, ctx, out);
	}

	public void genSignature(Type type, Context ctx, TabbedWriter out) {
		if ( ctx.get( Constants.SubKey_isaSignature ) != null ) {
			out.print( "T" );
			out.print(type.getTypeSignature().replaceAll("\\.", "/"));
			out.print( ";" );
		} else {
			out.print(type.getTypeSignature());
		}
	}

	public void genContainerBasedAccessor(Type type, Context ctx, TabbedWriter out, Function arg) {
		// no default
	}

	public void genContainerBasedAssignment(Type type, Context ctx, TabbedWriter out, Assignment arg1, Field arg2) {
		ctx.invoke(genAssignment, arg1, ctx, out);
	}

	public void genContainerBasedArrayAccess(Type type, Context ctx, TabbedWriter out, ArrayAccess arg1, Field arg2) {
		ctx.invoke(genArrayAccess, arg1, ctx, out);
	}

	public void genContainerBasedMemberAccess(Type type, Context ctx, TabbedWriter out, MemberAccess arg1, Member arg2) {
		ctx.invoke(genMemberAccess, arg1, ctx, out);
	}

	public void genContainerBasedMemberName(Type type, Context ctx, TabbedWriter out, MemberName arg1, Member arg2) {
		ctx.invoke(genMemberName, arg1, ctx, out);
	}

	public void genContainerBasedNewExpression(Type type, Context ctx, TabbedWriter out, Expression arg) {
		ctx.invoke(genNewExpression, arg, ctx, out);
	}

	public void genContainerBasedInvocation(Type type, Context ctx, TabbedWriter out, Expression arg) {
		ctx.invoke(genInvocation, arg, ctx, out);
	}

	public void genFieldInfoTypeName(Type type, Context ctx, TabbedWriter out, TypeNameKind arg) {
		ctx.invoke(genRuntimeTypeName, type, ctx, out, arg);
	}
	public void genCloneMethod(Type type, Context ctx, TabbedWriter out) {
	}
	public Boolean supportsConversion(Type type, Context ctx) {
		return Boolean.TRUE;
	}
	public void genServiceInvocationInParam(Type type, Context ctx, TabbedWriter out, Expression arg){
		ctx.invoke(genExpression, arg, ctx, out);
	}
	public void genServiceCallbackArgType(Type type, Context ctx, TabbedWriter out){
		ctx.invoke(genRuntimeTypeName, type, ctx, out, TypeNameKind.EGLImplementation);
	}
}
