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
package org.eclipse.edt.mof.egl.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.MofSerializable;
import org.eclipse.edt.mof.egl.AmbiguousReferenceException;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.BoxingExpression;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Constructor;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.DanglingReference;
import org.eclipse.edt.mof.egl.DelegateInvocation;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.FixedPrecisionType;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionInvocation;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.FunctionPart;
import org.eclipse.edt.mof.egl.FunctionPartInvocation;
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.LogicAndDataPart;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.NamedElement;
import org.eclipse.edt.mof.egl.NewExpression;
import org.eclipse.edt.mof.egl.NoSuchMemberError;
import org.eclipse.edt.mof.egl.NullLiteral;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.ParameterizableType;
import org.eclipse.edt.mof.egl.ParameterizedType;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartName;
import org.eclipse.edt.mof.egl.PatternType;
import org.eclipse.edt.mof.egl.QualifiedFunctionInvocation;
import org.eclipse.edt.mof.egl.SequenceType;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.StructuredContainer;
import org.eclipse.edt.mof.egl.StructuredField;
import org.eclipse.edt.mof.egl.SubType;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypeName;
import org.eclipse.edt.mof.impl.AbstractVisitor;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.edt.mof.utils.EList;



public class IRUtils {

	private static IrFactory factory = IrFactory.INSTANCE;
	public static String OVERLOADED_FUNCTION = "EZE_OVERLOADED_FUNCTION";
	private static String EGL_SCHEMA = Type.EGL_KeyScheme + Type.KeySchemeDelimiter;
	
	
	public static class FileNameResolver extends AbstractVisitor {
	
		private String packageName;
		private String fileName;
		
		FileNameResolver(EObject obj) {
			disallowRevisit();
			obj.accept(this);
		}

		private String getFilename() {
						
			return fileName;
		}

		private String getPackage() {
			
			return packageName;
		}
		
		public boolean visit(Object obj) {
			return false;
		}
		
		public boolean visit(Classifier classifier) {
			fileName = classifier.getFileName();
			
			if (fileName != null) {
				int i = fileName.lastIndexOf("/");
				if (i >= 0) {
					fileName = fileName.substring(i + 1);
				}
			}
			
			packageName = classifier.getPackageName();
			return false;
		}
		
		public boolean visit(Statement stmt) {
			
			if (stmt.getContainer() != null) {
				stmt.getContainer().accept(this);
			}
			return false;
		}
		
		public boolean visit(Member mbr) {
			if (mbr.getContainer() != null) {
				mbr.getContainer().accept(this);
			}
			return false;
		}
		
	}
	
	public class TopLevelFunctionResolver extends AbstractVisitor {
		private LogicAndDataPart context;
		private Function currentFunction;
		private Set<Function> addedFunctions = new HashSet<Function>();
		
		TopLevelFunctionResolver() {
			disallowRevisit();
		}
		
		public void resolveInContext(Function func, LogicAndDataPart part) {
			context = part;
			func.accept(this);
			currentFunction = null;
			context = null;
		}
		
		public void resolveAddedFunctions(LogicAndDataPart part) {
			List<Function> newFunctions = new ArrayList<Function>();
			newFunctions.addAll(addedFunctions);
			addedFunctions = new HashSet<Function>();
			for (Function func : newFunctions) {
				part.addMember(func);
			}
			for (Function func : newFunctions) {
				resolveInContext(func, part);
			}
			if (!addedFunctions.isEmpty()) {
				resolveAddedFunctions(part);
			}
		}
		
		public boolean visit(Member mbr) {
			return false;
		}
		
		public boolean visit(Part part) {
			return false;
		}

		public boolean visit(Function func) {
			if (currentFunction == null) {
				currentFunction = func;
				for (Statement stmt : func.getStatementBlock().getStatements()) {
					stmt.accept(this);
				}
			}
			return false;
		}
		
		public boolean visit(DanglingReference ref) {
			try {
				IRUtils.resolveDanglingReference(ref, context);
			} catch (NoSuchFieldError e) {
				throw new RuntimeException(e);
			} catch (AmbiguousReferenceException e) {
				throw new RuntimeException(e);
			}
			return false;

		}
		
		public boolean visit(FunctionPartInvocation expr) {
			Function f = null;
			for (Function added : addedFunctions) {
				if (added.getName().equalsIgnoreCase(expr.getId())) {
					f = added;
					break;
				}
			}
			if (f == null){
				f = IRUtils.resolveFunctionPartReference(expr, context);
				if (f != null) addedFunctions.add(f);
				for (Expression parm : expr.getArguments()) {
					parm.accept(this);
				}
			}
			return false;
		}

	}
	
	public static class PartsReferencedResolver extends AbstractVisitor {
		Part root;
		Set<Part> referencedParts;
		
		PartsReferencedResolver() {
			disallowRevisit();
			referencedParts = new HashSet<Part>();
		}
		
		public boolean visit(Part part) {
			if (part == root) {
				return true;
			}
			referencedParts.add(part);
			return false;
		}
		
		public boolean visit(TypeName name) {
			name.getType().accept(this);
			return false;
		}
		
		public boolean visit(NewExpression newExpr) {
			newExpr.getType().accept(this);
			return true;
		}
		
		//This visit is here because sometimes a MemberAccess or a QualifiedFunctionInvocation has already resoloved
		//the referenced member. When this happens, we do not want to start visiting the member if it is contained in
		//another part
		public boolean visit(Member member) {
			if (member.getContainer() instanceof Part && member.getContainer() != root) {
				member.getContainer().accept(this);
				return false;
			}
			return true;
		}
		
		public Set<Part> getReferencedPartsFor(Part part) {
			root = part;
			root.accept(this);
			return referencedParts;
		}
		
	}
	
	/**
	 * Use this method only to retrieve MOF types that are guaranteed to be there.
	 * Typically used within Expressions to return referenced types where the
	 * context where the expression exists has already resolved all type references
	 * @param typeSignature
	 * @return
	 */
	public static MofSerializable getType(String typeSignature) {
		try {
			return (MofSerializable)Environment.getCurrentEnv().find(typeSignature);
		}
		catch (Exception ex) {
			// Should not get here
			throw new RuntimeException(ex);
		}
	}
	public static Type getEGLType(String typeSignature) {
		String mofKey = typeSignature;
		if(!typeSignature.startsWith(Type.EGL_KeyScheme + Type.KeySchemeDelimiter)){
			mofKey = Type.EGL_KeyScheme + Type.KeySchemeDelimiter + mofKey;
		}
		return (Type)getType(mofKey);
	}
	
	public static StructPart getEGLPrimitiveType(String primSignature) {
		return (StructPart) getEGLType(primSignature);
	}
	
	public static FixedPrecisionType getEGLPrimitiveType(String primSignature, int length, int decimals) {
		String sig = primSignature + "(" + length + Type.PrimArgDelimiter + decimals + ")";
		return (FixedPrecisionType)getEGLType(sig);
	}
	
	public static SequenceType getEGLPrimitiveType(String primSignature, int length) {
		String sig = primSignature + "(" + length + ")";
		return (SequenceType)getEGLType(sig);
	}
	
	public static PatternType getEGLPrimitiveType(String primSignature, String pattern) {
		String sig = primSignature + "(" + pattern + ")";
		return (PatternType)getEGLType(sig);
	}
	
	/**
	 * Annotate functions that have same signature and also have inout or out
	 * parameters of primitive types or reference types.  This annotation is
	 * used to do aliasing in any generated code that needs to be able to.
	 * @param part
	 */
	public static void markOverloadedFunctions(LogicAndDataPart part) {
		List<Function> overloaded = new ArrayList<Function>();
		for (Function func : part.getFunctions()) {
			for (Function possible : part.getFunctions()) {
				if (func != possible && !overloaded.contains(func) && func.getName().equalsIgnoreCase(possible.getName())) {
					overloaded.add(func);
				}
			}
		}
		List<Function> parmLengthSame = new ArrayList<Function>();
		for (Function func : overloaded) {
			for (Function possible : overloaded) {
				if (func != possible && !parmLengthSame.contains(func) && func.getParameters().size() == possible.getParameters().size()) {
					parmLengthSame.add(func);
				}
			}
		}
		for (Function func : parmLengthSame) {
			boolean shouldMark = false;
			for (Function possible : parmLengthSame) {
				shouldMark = false;
				if (func != possible) {
					int j = 0;
					for (FunctionParameter parm : possible.getParameters()) {
						shouldMark = (parm.getParameterKind() == ParameterKind.PARM_INOUT || parm.getParameterKind() == ParameterKind.PARM_OUT);
						if (shouldMark && j < func.getParameters().size()) {
							FunctionParameter p = func.getParameters().get(j);
							shouldMark = (p.getParameterKind() == ParameterKind.PARM_INOUT || p.getParameterKind() == ParameterKind.PARM_OUT);
						}
						if (shouldMark) break;
						j++;
					}
					if (shouldMark) break;
				}
			}
			if (shouldMark) {
				Annotation ann = factory.createAnnotation(OVERLOADED_FUNCTION);
				func.addAnnotation(ann);
			}
		}
	}
	
	public static boolean isOverloadedFunction(Function func) {
		return func.getAnnotation(OVERLOADED_FUNCTION) != null;
	}
	
	public static void makeCompatible(BinaryExpression expr) {
		Type type1 = expr.getLHS().getType();
		Type type2 = expr.getRHS().getType();
		if (type1 != null && type2 != null)
			makeCompatible(expr, type1, type2);

	}
	public static void makeCompatible(BinaryExpression expr, Type type1, Type type2) {

		if (isNull(type1) || isNull(type2)) {
			return;
		}
	
		Operation op = expr.getOperation();
		Expression asExpr;
		Type parmType1 = op.getParameters().get(0).getType();
		Type parmType2 = op.getParameters().get(1).getType();
		// Operation invocations never have ParameterizedType(s) as parameter types
		// so always use the classifier instead of the type directly
		
		
		if (type1 != null && !type1.getClassifier().equals(parmType1)) {
			asExpr = makeExprCompatibleToType(expr.getLHS(), parmType1);
			expr.setLHS(asExpr);
		}
		if (type2 != null && !type2.getClassifier().equals(parmType2)) {
			asExpr = makeExprCompatibleToType(expr.getRHS(), parmType2);
			expr.setRHS(asExpr);
		}

	}
	
	public static void makeCompatible(Assignment expr) {	
		Type type = expr.getLHS().getType();
		Expression asExpr = makeExprCompatibleToType(expr.getRHS(), type);
		expr.setRHS(asExpr);			
	}
	
	public static void makeCompatible(Assignment expr, Type type) {
		Expression asExpr = makeExprCompatibleToType(expr.getRHS(), type);
		expr.setRHS(asExpr);
	}

	public static void makeCompatible(InvocationExpression expr) {
		Expression asExpr;
		int i = 0;
		for (Expression arg : expr.getArguments()) {
			asExpr = makeExprCompatibleToType(arg, expr.getParameterTypeForArg(i));
			expr.getArguments().set(i, asExpr);
			i++;
		}
	}
		
	private static boolean isList(Classifier clazz) {
		if (clazz != null) {
			return (clazz.getMofSerializationKey().equalsIgnoreCase(MofConversion.Type_EGLList));
		}
		return false;
	}

	private static boolean isString(NamedElement clazz) {
		if (clazz instanceof Classifier) {
			return (((Classifier)clazz).getMofSerializationKey().equalsIgnoreCase(MofConversion.Type_EGLString));
		}
		return false;
	}

		
	private static boolean isNull(Type type) {
		if (type == null || type.getMofSerializationKey() == null) {
			return false;
		}
			
			return type.getMofSerializationKey().equalsIgnoreCase(MofConversion.Type_EGLNullType);
	}

	private static boolean isAny(Classifier clazz) {
		if (clazz != null) {
			return (clazz.getMofSerializationKey().equalsIgnoreCase(MofConversion.Type_EGLAny));
		}
		return false;
	}


	public static Expression makeExprCompatibleToType(Expression expr, Type type) {
		Type exprType = expr.getType();
		if (expr instanceof Name) {
			// Check to see if this is a FunctionMember reference
			NamedElement mbr = ((Name)expr).getNamedElement();
			if (mbr instanceof Function) return expr;
		}
		
		if (expr instanceof NullLiteral) {
			return expr;
		}
		
		if (isNull(type)) {
			return expr;
		}
		
		if (exprType.equals(type) || exprType.getClassifier().equals(type)) {
			return expr;
		}
		
		if (isList(type.getClassifier()) && (exprType.equals(type.getClassifier()) || exprType.getClassifier().equals(type.getClassifier()))) {
			return expr;
		}
		
		if (exprType.getClassifier().equals(type.getClassifier())) {
			if (exprType instanceof SequenceType && type instanceof SequenceType) {
				if (((SequenceType)exprType).getLength() < ((SequenceType)type).getLength()) {
					return expr;
				}
			}
			else if (exprType instanceof FixedPrecisionType && type instanceof FixedPrecisionType) {
				FixedPrecisionType fpExpr = (FixedPrecisionType)exprType;
				FixedPrecisionType fpType = (FixedPrecisionType)type;
				
				if (fpExpr.getLength() <= fpType.getLength() 
						&& fpExpr.getDecimals() <= fpType.getDecimals()) {
					return expr;
				}
			}
		}
				
		if (TypeUtils.isReferenceType(exprType) 
				&& exprType instanceof SubType 
				&& type instanceof StructPart 
				&& ((SubType)exprType).isSubtypeOf((StructPart)type)) 
			return createAsExpression(expr, type);
		
		if (TypeUtils.isReferenceType(type) && TypeUtils.isValueType(exprType)) {
			 //Conversions from value types to Number, Decimal, TimeStamp, String do not need to be boxed
			if (!(type instanceof ParameterizableType) && !(type == IRUtils.getEGLPrimitiveType(MofConversion.Type_Number))) {
				BoxingExpression box = factory.createBoxingExpression();
				box.setExpr(expr);
				return createAsExpression(box, type);
			}
			
		}
		return createAsExpression(expr, type);
	}
	
	public static AsExpression createAsExpression(Expression objExpr, Type target) {
		AsExpression expr = factory.createAsExpression();
		expr.setObjectExpr(objExpr);
		expr.setEType(target);
		expr.setConversionOperation(getConversionOperation(objExpr, target));
		
		Annotation annot = objExpr.getAnnotation("EGL_Location");
		if (annot != null) {
			expr.addAnnotation(annot);
		}
		return expr;
	}
	
	public static AsExpression createAsExpression(Expression objExpr, ParameterizableType target) {
		ParameterizedType type = (ParameterizedType)target.getParameterizedType().newInstance();
		type.setParameterizableType(target);
		AsExpression expr = createAsExpression(objExpr, type);
		return expr;
	}
		
	/**
	 * Returns an Integer value representing the direction of conversion between a left hand side
	 * type and a right hand side type.
	 * - 0: No conversion necessary
	 * - 1: Left hand side converted to right hand side type
	 * - -1: Right hand side converted to left hand side
	 * - 2: Both sides converted to Decimal
	 * - null: Invalid conversion
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	public static Integer conversionDirection(NamedElement lhsne, NamedElement rhsne, String operator) {
		// Check for NullType
		if (lhsne == null || rhsne == null) return 0;
		
		if (lhsne.equals(rhsne)) return 0;

		if (lhsne instanceof Type && rhsne instanceof Type) {
			Type lhs = (Type) lhsne;
			Type rhs = (Type) rhsne;
			if (lhs.equals(rhs) || lhs.getClassifier().equals(rhs.getClassifier())) return 0;
	
			// If Text and Numeric types are involved both will be converted to Decimal
			// Special case for + operator which is overloaded for both String and Number;
			// if the Number is on the left then it is a numeric operation otherwise
			// it is a concatenation operation
			if (operator.equals("+") && TypeUtils.isNumericType(lhs) && TypeUtils.isTextType(rhs)) {
				return 2;
			}
			if (operator.equals("+") && TypeUtils.isTextType(lhs)) {
				return -1;
			}
			if (isValidWidenConversion(lhs, rhs))
				return 1;
			else if (isValidWidenConversion(rhs, lhs))
				return -1;
			else if (isValidNarrowConversion(lhs, rhs))
				return 1;
			else if (isValidNarrowConversion(rhs, lhs))
				return -1;
			else 
				return null;
		}
		return null;
	}
	
	public static boolean isValidWidenConversion(Type source,  Type target) {
		Classifier c1 = source.getClassifier();
		Classifier c2 = target.getClassifier();
		if (c1 == c2) return true;
		if (c1 instanceof StructPart && c2 instanceof StructPart) {
			StructPart src = (StructPart)c1;
			StructPart trg = (StructPart)c2;
			Operation op = TypeUtils.getBestFitWidenConversionOp(src, trg);
			return op != null || src.isSubtypeOf(trg);
		}
		return false;
	}
	
	public static boolean isValidNarrowConversion(Type source,  Type target) {
		Classifier c1 = source.getClassifier();
		Classifier c2 = target.getClassifier();
		if (c1 == c2) return true;
		if (c1 instanceof StructPart && c2 instanceof StructPart) {
			StructPart src = (StructPart)c1;
			StructPart trg = (StructPart)c2;
			Operation op = TypeUtils.getBestFitNarrowConversionOp(src, trg);
			return op != null;
		}
		return false;
	}

	public static StructPart getCommonSupertype(Type type1, Type type2) {
		StructPart class1 = (StructPart)type1.getClassifier();
		StructPart class2 = (StructPart)type2.getClassifier();
		StructPart result = null;
		if (class1.equals(class2) || class2.isSubtypeOf(class1)) {
			result = class1;
		}
		else {
			for (StructPart superType : class1.getSuperTypes()) {
				if (isValidWidenConversion(type2, superType)) {
					result = superType;
					break;
				}
			}
			if (result == null) {
				for (StructPart superType : class1.getSuperTypes()) {
					result = getCommonSupertype(superType, type2);
					if (result != null) break;
				}
				if (result == null) {
					result = getCommonSupertype(type2, type1);
				}
			}
			if (result == null) {
				result = (StructPart)TypeUtils.Type_ANY;
			}
		}
		return result;
	}
	
	public static Operation getConversionOperation(Expression expr, Type trg) {
		if (expr.getType() != null && expr.getType().getClassifier() instanceof StructPart && trg.getClassifier() instanceof StructPart) {
			
			//Do not attempt to find a conversion operation if the 2 types are the same
			if ((StructPart)expr.getType().getClassifier() != (StructPart)trg.getClassifier()) {
				return getConversionOperation((StructPart)expr.getType().getClassifier(), (StructPart)trg.getClassifier());
			}
		}
		return null;
	}
	
	public static Operation getConversionOperation(StructPart src, StructPart trg) {
		Operation op = TypeUtils.getBestFitWidenConversionOp(src, trg);
		if (op != null) return op;
		op = TypeUtils.getBestFitNarrowConversionOp(src, trg);
		
		return op;
		

	}

	//TODO this needs to be made more sophisticated in the future. The code will  need to be
	//able to resolve the correct operation based on all arguments to the various Access expressions
	public static Operation getMyOperation(Classifier classifier, String opSymbol) {
		if (!(classifier instanceof StructPart)) return null;
		
		StructPart clazz = (StructPart) classifier;
		for (Operation op : clazz.getOperations()) {
			if (op.getOpSymbol().equals(opSymbol))  {
				return op;
			}
		}  
		return null;
	}
	
	private static Operation getNoConversionBinaryOperation(NamedElement lhs, NamedElement rhs, StructPart clazz, String opSymbol) {
		// First check if there is an explicit operation 
		// independent of conversion of either side to the other.
		// This is to handle the cases where binary operations are implemented
		// that do not force a conversion of one type to the other

		if (!lhs.equals(rhs)) {
			
			//first check in the clazz
			Operation result = primGetNoConversionBinaryOperation(lhs, rhs, clazz, opSymbol);
			if (result != null) {
				return result;
			}
			
			if (clazz != lhs && lhs instanceof StructPart) {
				result = primGetNoConversionBinaryOperation(lhs, rhs, (StructPart)lhs, opSymbol);
				if (result != null) {
					return result;
				}
				
			}

			if (clazz != rhs && rhs instanceof StructPart) {
				result = primGetNoConversionBinaryOperation(lhs, rhs, (StructPart)rhs, opSymbol);
				if (result != null) {
					return result;
				}				
			}
		}
		return null;
	}

	private static Operation primGetNoConversionBinaryOperation(NamedElement lhs, NamedElement rhs, StructPart clazz, String opSymbol) {
		List<Operation> ops = TypeUtils.getBestFitOperation(clazz, opSymbol, lhs, rhs);
		// Filter out an operation that has the same parameter types for each parameter
		if (ops.size() > 0) {
			for (Operation operation : ops) {
				if (!(operation.getParameters().get(0).getType().equals(operation.getParameters().get(1).getType()))) {
					return operation;
				}
			}
		}
		
		return null;
	}

	public static Operation getBinaryOperation(NamedElement lhs, NamedElement rhs, String opSymbol) {
				
		Operation result = primGetBinaryOperation(lhs, rhs, opSymbol);
		if (result != null) {
			return result;
		}
				
		result = checkForTextConcatenation(lhs, opSymbol);
		if (result != null) {
			return result;
		}

		result = checkForTextConcatenation(rhs, opSymbol);
		if (result != null) {
			return result;
		}
		
		//Special case for reference types..if lhs is subType of rhs or rhs, is subtype of lhs, use the default operation
		//defined in ANY
		if (lhs instanceof StructPart && rhs instanceof StructPart && TypeUtils.isReferenceType((StructPart)lhs) && TypeUtils.isReferenceType((StructPart)rhs)) {
			StructPart lStruct = (StructPart)lhs;
			StructPart rStruct = (StructPart)rhs;
			
			if (lStruct.isSubtypeOf(rStruct) || rStruct.isSubtypeOf(lStruct)) {
				StructPart any = (StructPart)IRUtils.getEGLType(MofConversion.Type_EGLAny);
				if (lStruct.isSubtypeOf(any)) {
					result = primGetBinaryOperation(any, any, opSymbol);
				}
			}
		}

		return result;		
	}
	
	private static Operation checkForTextConcatenation(NamedElement clazz, String opSymbol) {
		if (opSymbol.equals("+") || opSymbol.equals("::") || opSymbol.equals("?:")) {
			if (isString(clazz)) {
				return TypeUtils.getBinaryOperation((StructPart)clazz, opSymbol, false);
			}
		}
		return null;
	}

	private static Operation primGetBinaryOperation(NamedElement lhs, NamedElement rhs, String opSymbol) {
				
		if (!(lhs instanceof StructPart)) {
			Operation result = null;
			if (rhs instanceof StructPart) {
				result = getNoConversionBinaryOperation(lhs, rhs, (StructPart)rhs, opSymbol);
				if (result != null) {
					return result;
				}
			}
		
			if (lhs instanceof SubType) {
				SubType lSubType = (SubType)lhs;
				NamedElement rSubType = rhs;

				if (!(rhs instanceof StructPart) && rhs instanceof SubType && ((SubType)rhs).getSuperTypes().size() > 0)  {
					rSubType = ((SubType)rhs).getSuperTypes().get(0);
				}
				
				if (lSubType.getSuperTypes().size() > 0) {
					return getBinaryOperation(lSubType.getSuperTypes().get(0), rSubType, opSymbol);
				}
			}
			
			return null;
		}
		
		
		StructPart clazz = null;
		Integer direction = conversionDirection(lhs, rhs, opSymbol);
		
		// Now check for operation based on one type being converted to the other
		// to determine where to look for the operation
		Operation conOp = null;
		if (direction == null || direction == 0) {
			clazz = (StructPart)lhs;
		}
		else if (direction == -1) {
			conOp = getConversionOperation((StructPart)rhs, (StructPart)lhs);
			if (conOp == null) {				
				return getNoConversionBinaryOperation(lhs, rhs, (StructPart)lhs, opSymbol);
			}
			clazz = (StructPart)conOp.getType().getClassifier();
		}
		else if (direction == 1) {
			conOp = getConversionOperation((StructPart)lhs, (StructPart)rhs);
			if (conOp == null) return null;
			clazz = (StructPart)conOp.getType().getClassifier(); 
		}
		else if (direction == 2) {
			clazz = (StructPart)TypeUtils.Type_DECIMAL;
		}
		
		//Check for explicit operation
		Operation result = getNoConversionBinaryOperation(lhs, rhs, clazz, opSymbol);
		if (result != null) {
			return result;
		}
		
		result = TypeUtils.getBinaryOperation(clazz, opSymbol, (direction != null && direction == 0) );
		if (result == null && direction != null) {
			// If there was no operation then reverse the lookup
			if (direction == -1) {
				conOp = getConversionOperation((StructPart)lhs, (StructPart)rhs);
				if (conOp == null) return null;
				clazz = (StructPart)conOp.getType().getClassifier();
			}
			else if (direction == 1) {
				conOp = getConversionOperation((StructPart)rhs, (StructPart)lhs);
				if (conOp == null) return null;
				clazz = (StructPart)conOp.getType().getClassifier(); 
			}		
			result = TypeUtils.getBinaryOperation(clazz, opSymbol, false);
		}
		return result;	
	}
	
	public static Operation getUnaryOperation(Classifier src, String opSymbol) {
		if (!(src instanceof StructPart)) return null;
		for (Operation op : ((StructPart)src).getOperations()) {
			if (op.getOpSymbol().equals(opSymbol)) {
				if (op.getParameters().size() == 1) return op;
			}
		}
		return null;
	}
	
	public static String concatWithSeparator(String[] fragments, String separator) {
		StringBuffer result = new StringBuffer();
		for (int i=0; i<fragments.length; i++) {
			result.append(fragments[i]);
			if (i < fragments.length-1) {
				result.append(separator);
			}
		}
		return result.toString();
	}
	
	public void resolveTopLevelFunctionsAndDanglingReferences(final LogicAndDataPart part) {
		TopLevelFunctionResolver resolver = new TopLevelFunctionResolver();
		for (Function func : part.getFunctions()) {
			resolver.resolveInContext(func, part);
		}
		resolver.resolveAddedFunctions(part);
	}
	
	/**
	 * This method is used to resolve unqualified references in the
	 * context of the @param part for TopLevelFunctions that have such 
	 * references and where the annotation "IncludeReferencedFunctions"
	 * has been set on the @param part.
	 * @param name
	 * @param part
	 * @return
	 * @throws NoSuchMemberError
	 * @throws AmbiguousReferenceException
	 */
	public static void resolveDanglingReference(DanglingReference dangling, LogicAndDataPart part) throws NoSuchFieldError, AmbiguousReferenceException {
		Field field = part.getField(dangling.getId());
		if (field != null) {
			dangling.setMember(field);
		}
		else {
			EObject qualifier = null;
			List<Field> fields = new EList<Field>();
			// search the types of the fields in part
			for (Field f : part.getFields()) {
				Container container;
				if (f.getType() instanceof LogicAndDataPart || f.getType() instanceof StructuredContainer) {
					qualifier = f;
					container = (Container)f.getType();
					if (container instanceof StructPart) {
						List<StructuredField> list = ((StructPart)container).getStructuredFields(dangling.getId());
						if (!list.isEmpty())
							fields.addAll(list);
					}
					else{
						Field fi = ((LogicAndDataPart)container).getField(dangling.getId());
						if (fi != null) fields.add(fi);
					}
				}
			}
			for (Part usedPart : part.getUsedParts()) {
				if (usedPart instanceof LogicAndDataPart) {
					for (Field f : ((LogicAndDataPart)usedPart).getFields()) {
						if (f.getName().equalsIgnoreCase(dangling.getId())) {
							fields.add(f);
						}
					}
				}
				if (usedPart instanceof StructPart) {
					List<StructuredField> list = ((StructPart)usedPart).getStructuredFields(dangling.getId());
					if (!list.isEmpty())
						fields.addAll(list);
				}
 			}
			if (fields.size() > 1) {
				throw new AmbiguousReferenceException(dangling.getId());
			}
			if (fields.size() == 0) {
				throw new NoSuchFieldError(dangling.getId());
			}
			else {
				if (qualifier instanceof Field) {
					MemberName expr = factory.createMemberName();
					expr.setId(((Field)qualifier).getId());
					expr.setMember((Field)qualifier);
					dangling.setQualifier(expr);
				}
				else if (qualifier instanceof Part) {
					PartName expr = factory.createPartName();
					expr.setId(((Part)qualifier).getId());
					expr.setPackageName(((Part)qualifier).getPackageName());
					dangling.setQualifier(expr);
				}
			}
		}
	}
	
	public static Function resolveFunctionPartReference(FunctionPartInvocation expr, LogicAndDataPart part) {
		Function func = part.getFunction(expr.getId());
		if (func == null) { 
			FunctionPart funcPart = expr.getFunctionPart();
			if (funcPart != null) {
				func = (Function)funcPart.getFunction().clone();
				expr.setTarget(func);
				return func;
			}
		}
		else {
			expr.setTarget(func);
		}
		return null;
	}

	public static Set<Part> getReferencedPartsFor(Part part) {
		return (new PartsReferencedResolver()).getReferencedPartsFor(part);
	}
	
	public static boolean hasSideEffects(Expression expr) {
		return (new CheckSideEffects()).checkSideEffect(expr);
	}

	public static class CheckSideEffects extends AbstractVisitor {
		boolean has = false;
		public boolean checkSideEffect(Expression expr) {
			disallowRevisit();
			setReturnData(false);
			expr.accept(this);
			return (Boolean)getReturnData();
		}
		public boolean visit(EObject obj) {
			return false;
		}
		public boolean visit(Expression expr) {
			if (has) return false;
			return true;
		}
		public boolean visit(NewExpression expr) {
			has = true;
			setReturnData(has);
			return true;
		}
		public boolean visit(Assignment expr) {
			has = true;
			setReturnData(has);
			return true;
		}
		public boolean visit(FunctionInvocation expr) {
			has = true;
			setReturnData(has);
			return false;
		}
		public boolean visit(DelegateInvocation expr) {
			has = true;
			setReturnData(has);
			return false;
		}
		public boolean visit(QualifiedFunctionInvocation expr) {
			has = true;
			setReturnData(has);
			return false;
		}
	}
	
	public static Constructor resolveConstructorReference(EGLClass clazz, List<Expression> arguments) {
		Constructor result = null;
		List<Constructor> possibles = new ArrayList<Constructor>();
		for (Constructor mbr : clazz.getConstructors()) {
			if (mbr.getParameters().size() == arguments.size()) {
				boolean exact = true;
				for (int i=0; i<arguments.size(); i++) {
					if (!mbr.getParameters().get(i).getType().equals(arguments.get(i).getType())) {
						exact = false;
					}
				}
				if (exact) return mbr;
				boolean compat = false;
				for (int i=0; i<arguments.size(); i++) {
					compat = isCompatibleWith(arguments.get(i), mbr.getParameters().get(i).getType());
				}
				if (compat) {
					possibles.add(mbr);
				}
			}
		}
		if (possibles.size() == 0) {
			// Return a default constructor
			result = IrFactory.INSTANCE.createConstructor();
			result.setIsAbstract(true);
			result.setName(clazz.getName());
			result.setType(clazz);
		}
		else if (possibles.size() == 1) {
			result = possibles.get(0);
		}
		else {
			// TODO find best fit based on 
			result = possibles.get(0);
		}
		return result;
	}
	
	public static boolean isCompatibleWith(Expression expr, Type type) {
		
		NamedElement exprKind;
		if (expr instanceof Name && ((Name)expr).getNamedElement() instanceof Function) {
			exprKind = (Function) ((Name)expr).getNamedElement();
		}
		else {			
			exprKind = (Classifier)expr.getType().getClassifier();
		}
		
		return TypeUtils.areCompatible(type.getClassifier(), exprKind);
		
	}


	public static String getFileName(EObject obj) {
		FileNameResolver resolver = new FileNameResolver(obj);
		return resolver.getFilename();
	}
	
	public static String getQualifiedFileName(EObject obj) {
		FileNameResolver resolver = new FileNameResolver(obj);
		String fileName = resolver.getFilename();
		String packageName = resolver.getPackage();
		
		if (fileName == null || packageName == null) {
			return null;
		}
		
		if (packageName.length() == 0) {
			return fileName;
		}
		
		return packageName.replace('.', '/') + "/" + fileName;
		
	}

	public static boolean isSystemPart( String partName, IEnvironment sysIREnv ) {
		String key = makeEGLKey(partName);
		if(sysIREnv.get(key)!=null){
			return true;
		}else{
			return false;
		}
	}
	
	public static String makeEGLKey(String key) {
		if (key != null && !key.startsWith(EGL_SCHEMA)) {
			key = EGL_SCHEMA + key;
		}
		return key.toUpperCase().toLowerCase();
	}

}
