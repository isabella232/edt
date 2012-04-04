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
package org.eclipse.edt.compiler.internal.core.validation.statement;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.AmbiguousDataBinding;
import org.eclipse.edt.compiler.binding.ArrayDictionaryBinding;
import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.DictionaryBinding;
import org.eclipse.edt.compiler.binding.FixedRecordBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.FormBinding;
import org.eclipse.edt.compiler.binding.FormFieldBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.MultiplyOccuringItemTypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.ArrayAccess;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FieldAccess;
import org.eclipse.edt.compiler.core.ast.ForMoveModifier;
import org.eclipse.edt.compiler.core.ast.IntegerLiteral;
import org.eclipse.edt.compiler.core.ast.MoveModifier;
import org.eclipse.edt.compiler.core.ast.MoveStatement;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.ParenthesizedExpression;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.SubstringAccess;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
public class MoveStatementValidator extends DefaultASTVisitor {

	private IProblemRequestor problemRequestor;
	private MoveStatement moveStmt;
    private ICompilerOptions compilerOptions;
	private IPartBinding enclosingPart;

	public MoveStatementValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions, IPartBinding enclosingPart) {
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
		this.enclosingPart = enclosingPart;
	}
	
	public boolean visit(MoveStatement moveStatement) {
		 
		if (true) {
			problemRequestor.acceptProblem(moveStatement,
					IProblemRequestor.STATEMENT_NOT_SUPPORTED,
					new String[] {"MOVE"});
			return false;
		}
		
		this.moveStmt = moveStatement;
		Expression sourceExpr = moveStatement.getSource();
		Expression targetExpr = moveStatement.getTarget();
		
		if (targetExpr instanceof SubstringAccess) {
			problemRequestor.acceptProblem(targetExpr,
					IProblemRequestor.SUBSTRING_IMMUTABLE,
					new String[] {});
		}

		
		ITypeBinding sourceType = sourceExpr.resolveTypeBinding();
		ITypeBinding targetType = targetExpr.resolveTypeBinding();
		
		if(!Binding.isValidBinding(sourceType)|| !Binding.isValidBinding(targetType)) {
			return false;
		}
		
		MoveModifier modifier = moveStatement.getMoveModifierOpt();
		if(modifier != null && modifier.isFor()) {
			checkForCount(((ForMoveModifier) modifier).getExpression());
		}
		
		boolean expressionsValid =
			checkTargetOrSourceExpression(sourceExpr, sourceType, IProblemRequestor.MOVE_STATEMENT_INVALID_SOURCE_TYPE) &&
			checkTargetOrSourceExpression(targetExpr, targetType, IProblemRequestor.MOVE_STATEMENT_INVALID_TARGET_TYPE);
		if(!expressionsValid) {
			return false;
		}
		
		if(modifier == null) {
			checkExpressionsForDefaultModifier(sourceExpr, targetExpr, sourceType, targetType);
		}
		else if(modifier.isByName()) {
			checkExpressionsForByName(sourceExpr, targetExpr, sourceType, targetType);
		}
		else if(modifier.isByPosition()) {
			problemRequestor.acceptProblem(
					moveStatement,
					IProblemRequestor.MOVE_MODIFIER_INVALID,
					new String[] {"ByPosition"});
			checkExpressionsForByPosition(sourceExpr, targetExpr, sourceType, targetType);
		}
		else if(modifier.isFor()) {
			checkExpressionsForFor(sourceExpr, targetExpr, sourceType, targetType);
		}
		else if(modifier.isForAll()) {
			checkExpressionsForForAll(sourceExpr, targetExpr, sourceType, targetType);
		}
		else if(modifier.isWithV60Compat()) {
			problemRequestor.acceptProblem(
					moveStatement,
					IProblemRequestor.MOVE_MODIFIER_INVALID,
					new String[] {"WithV60Compat"});
			if(isContainer(sourceExpr) && isContainer(targetExpr) && !isStructureItem(sourceExpr) && !isStructureItem(targetExpr)) {
				checkExpressionsForByName(sourceExpr, targetExpr, sourceType, targetType);
			}
			else {
				checkExpressionsForDefaultModifier(sourceExpr, targetExpr, sourceType, targetType);
			}
		}
		
		return false;
	}
	
	private boolean checkTargetOrSourceExpression(Expression expr, ITypeBinding exprType, int problemKind) {
		if(ITypeBinding.FIXED_RECORD_BINDING != exprType.getKind() &&
	       ITypeBinding.PRIMITIVE_TYPE_BINDING != exprType.getKind() &&
		   ITypeBinding.MULTIPLY_OCCURING_ITEM != exprType.getKind() &&
		   ITypeBinding.ENUMERATION_BINDING != exprType.getKind() &&
		   ITypeBinding.EXTERNALTYPE_BINDING != exprType.getKind() &&
		   ITypeBinding.FUNCTION_BINDING != exprType.getKind() &&
		   ITypeBinding.DELEGATE_BINDING != exprType.getKind() && 
		   exprType != ArrayDictionaryBinding.INSTANCE &&
		   exprType != DictionaryBinding.INSTANCE &&
		   !exprType.isDynamic() &&
		   !isRecordOrForm(exprType) &&
		   ITypeBinding.NIL_BINDING != exprType.getKind()) {
			if(ITypeBinding.ARRAY_TYPE_BINDING == exprType.getKind()) {
				return checkTargetOrSourceExpression(expr, ((ArrayTypeBinding) exprType).getBaseType(), problemKind);
			}
			boolean isValid = false;
						
			if(!isValid) {
				problemRequestor.acceptProblem(
					expr,
					problemKind,
					new String[] {expr.getCanonicalString()});
				return false;
			}
		}
		return true;
	}
	
	private void checkForCount(Expression forCountExpr) {
		boolean countIsValid = false;
		
		final boolean[] isValidExpr = new boolean[] {false};
		forCountExpr.accept(new AbstractASTExpressionVisitor() {
			public void endVisit(IntegerLiteral integerLiteral) {
				isValidExpr[0] = true;
			}
			public void endVisitName(Name name) {
				isValidExpr[0] = true;
			}
			public void endVisit(ArrayAccess arrayAccess) {
				isValidExpr[0] = true;
			}
			public void endVisit(FieldAccess fieldAccess) {
				isValidExpr[0] = true;
			}
		});
		
		if(isValidExpr[0]) {		
			ITypeBinding forCountTBinding = forCountExpr.resolveTypeBinding();
			if(forCountTBinding == null) {
				countIsValid = true;
			}
			else if(ITypeBinding.PRIMITIVE_TYPE_BINDING == forCountTBinding.getKind()) {
				PrimitiveTypeBinding primTBinding = (PrimitiveTypeBinding) forCountTBinding;
				if(Primitive.isNumericType(primTBinding.getPrimitive()) &&
				   primTBinding.getDecimals() == 0) {
					countIsValid = true;
				}
			}
		}
		
		if(!countIsValid) {
			problemRequestor.acceptProblem(
				forCountExpr,
				IProblemRequestor.MOVE_FOR_COUNT_NOT_INTEGER,
				new String[] {forCountExpr.getCanonicalString()});
		}
	}
	
	private void checkExpressionsForDefaultModifier(Expression sourceExpr, final Expression targetExpr, ITypeBinding sourceType, ITypeBinding targetType) {
		targetExpr.accept(new AbstractASTExpressionVisitor() {
			boolean firstVisit = true;
			public boolean visit(ArrayAccess arrayAccess) {
				if(firstVisit) {
					if(arrayAccess.getArray().resolveTypeBinding() == ArrayDictionaryBinding.INSTANCE) {
						problemRequestor.acceptProblem(
							arrayAccess,
							IProblemRequestor.CANNOT_ASSIGN_TO_ARRAY_DICTIONARY_ELEMENTS);
					}
				}
				firstVisit = false;
				return true;
			}
			public boolean visit(FieldAccess fieldAccess) {
				firstVisit = false;
				return true;
			}
			public boolean visit(SubstringAccess substringAccess) {
				firstVisit = false;
				return true;
			}
		});
		
		if(!TypeCompatibilityUtil.isMoveCompatible(targetType, sourceType, sourceExpr, compilerOptions) &&
		   !sourceType.isDynamic() &&
		   !TypeCompatibilityUtil.areCompatibleArrayTypes(targetType, sourceType, compilerOptions) && 
		   !TypeCompatibilityUtil.areCompatibleExceptions(sourceType, targetType, compilerOptions)) {
			problemRequestor.acceptProblem(
				targetExpr,
				IProblemRequestor.ASSIGNMENT_STATEMENT_TYPE_MISMATCH,
				new String[] {
					sourceType.getCaseSensitiveName(),
					targetType.getCaseSensitiveName(),
					IEGLConstants.KEYWORD_MOVE.toUpperCase()});
		}
		else if(sourceType.getAnnotation(new String[] {"egl", "io", "dli"}, "PSBRecord") != null &&
				targetType.getAnnotation(new String[] {"egl", "io", "dli"}, "PSBRecord") != null) {
			problemRequestor.acceptProblem(
				targetExpr,
				IProblemRequestor.ASSIGNMENT_STATEMENT_TYPE_MISMATCH,
				new String[] {
					"PSBRecord",
					"PSBRecord",
					IEGLConstants.KEYWORD_MOVE.toUpperCase()});
		}
		
		IDataBinding targetDBinding = targetExpr.resolveDataBinding();
		if(targetDBinding != null) {
			new LValueValidator(problemRequestor, compilerOptions, targetDBinding, targetExpr).validate();
		}
		
		IDataBinding sourceDBinding = sourceExpr.resolveDataBinding();
		if(sourceDBinding != null) {
			new RValueValidator(problemRequestor, compilerOptions, sourceDBinding, sourceExpr).validate();
		}
		
		
		if (Binding.isValidBinding(sourceType)) {			
			if (!sourceType.isReference()) {
				problemRequestor.acceptProblem(
						sourceExpr,
						IProblemRequestor.MOVE_MUST_BE_REFERENCE,
						new String[] {});
			}
			
			if (Binding.isValidBinding(sourceType.getBaseType()) && sourceType.getBaseType().getKind() == ITypeBinding.EXTERNALTYPE_BINDING) {
				problemRequestor.acceptProblem(
						sourceExpr,
						IProblemRequestor.MOVE_EXTERNALTYPE,
						new String[] {});
			}
		}
		else {
			if (Binding.isValidBinding(targetType)) {
				if (!targetType.isReference()) {
					problemRequestor.acceptProblem(
							targetExpr,
							IProblemRequestor.MOVE_MUST_BE_REFERENCE,
							new String[] {});
				}
				
				if (Binding.isValidBinding(targetType.getBaseType()) && targetType.getBaseType().getKind() == ITypeBinding.EXTERNALTYPE_BINDING) {
					problemRequestor.acceptProblem(
							targetExpr,
							IProblemRequestor.MOVE_EXTERNALTYPE,
							new String[] {});
				}
			}
		}
		
		

	}
		
	private boolean checkExpressionsForByNameOrByPosition(Expression sourceExpr, Expression targetExpr, ITypeBinding sourceType, ITypeBinding targetType) {
		if(ITypeBinding.ARRAY_TYPE_BINDING == sourceType.getKind() && ITypeBinding.FLEXIBLE_RECORD_BINDING == ((ArrayTypeBinding) sourceType).getElementType().getKind() &&
		   ITypeBinding.ARRAY_TYPE_BINDING == targetType.getKind() && ITypeBinding.FLEXIBLE_RECORD_BINDING == ((ArrayTypeBinding) targetType).getElementType().getKind()) {
			problemRequestor.acceptProblem(
				moveStmt,
				IProblemRequestor.FLEXIBLE_RECORD_ARRAYS_MOVED_BYNAME_OR_BYPOSITION);
			return false;
		}
		else {
			boolean expressionsValid = true;
			if(!isContainer(sourceExpr)) {
				problemRequestor.acceptProblem(
					sourceExpr,
					IProblemRequestor.NON_CONTAINER_MOVE_OPERAND_MOVED_BY_NAME_OR_POSITION,
					new String[] {sourceExpr.getCanonicalString()});
				expressionsValid = false;
			}
			if(!isContainer(targetExpr)) {
				problemRequestor.acceptProblem(
					targetExpr,
					IProblemRequestor.NON_CONTAINER_MOVE_OPERAND_MOVED_BY_NAME_OR_POSITION,
					new String[] {targetExpr.getCanonicalString()});
				expressionsValid = false;
			}
			if(!expressionsValid) {
				return false;
			}
		}
		return true;
	}
	
	private void checkExpressionsForByName(Expression sourceExpr, Expression targetExpr, ITypeBinding sourceType, ITypeBinding targetType) {
		if(!checkExpressionsForByNameOrByPosition(sourceExpr, targetExpr, sourceType, targetType)) {
			return;
		}
		
		if(sourceType.getAnnotation(new String[] {"egl", "io", "dli"}, "PSBRecord") != null &&
		   targetType.getAnnotation(new String[] {"egl", "io", "dli"}, "PSBRecord") != null) {
			problemRequestor.acceptProblem(
				sourceExpr,
				IProblemRequestor.ASSIGNMENT_STATEMENT_TYPE_MISMATCH,
				new String[] {
					"PSBRecord",
					"PSBRecord",
					IEGLConstants.KEYWORD_MOVE.toUpperCase()
				});
			return;
		}
		
		Map sourceNamesToDataBindings = getNamesToDataBindingsMap(sourceExpr);
		Map targetNamesToDataBindings = getNamesToDataBindingsMap(targetExpr);
		
		boolean shouldReturn = false;
		
		if(!checkNamesUnique(sourceNamesToDataBindings, sourceExpr, IProblemRequestor.MOVE_STATEMENT_NONUNIQUE_BYNAME_SOURCE)) {
			shouldReturn = true;
		}
		if(!checkNamesUnique(targetNamesToDataBindings, targetExpr, IProblemRequestor.MOVE_STATEMENT_NONUNIQUE_BYNAME_TARGET)) {
			shouldReturn = true;
		}		
		
		if(shouldReturn) { return; }
		
		if(!checkNoMultiplyOccuringStructureItems(sourceNamesToDataBindings, sourceExpr, IProblemRequestor.MOVE_STATEMENT_MULTIDIMENSIONAL_BYNAME_OR_BYPOSITION_SOURCE)) {
			shouldReturn = true;
		}
		if(!checkNoMultiplyOccuringStructureItems(targetNamesToDataBindings, targetExpr, IProblemRequestor.MOVE_STATEMENT_MULTIDIMENSIONAL_BYNAME_OR_BYPOSITION_TARGET)) {
			shouldReturn = true;
		}
		
		if(shouldReturn) { return; }
		
		if(sourceNamesToDataBindings.isEmpty()) {
			problemRequestor.acceptProblem(
				sourceExpr,
				IProblemRequestor.MOVE_STATEMENT_BYNAME_BYPOSITION_ITEM_HAS_NO_SUBSTRUCTURE,
				IMarker.SEVERITY_WARNING,
				new String[] {sourceExpr.getCanonicalString()});
		}
		
		for(Iterator sourceNamesIter = sourceNamesToDataBindings.keySet().iterator(); sourceNamesIter.hasNext();) {
			String nextSourceName = (String) sourceNamesIter.next();
			IDataBinding matchingTargetBinding = (IDataBinding) targetNamesToDataBindings.get(nextSourceName);
			if(matchingTargetBinding != null) {
				ITypeBinding nextSourceType = ((IDataBinding) sourceNamesToDataBindings.get(nextSourceName)).getType();
				if(!TypeCompatibilityUtil.isMoveCompatible(matchingTargetBinding.getType(), nextSourceType, null, compilerOptions)) {
					problemRequestor.acceptProblem(
						targetExpr,
						IProblemRequestor.MOVE_STATEMENT_INCOMPATIBLE_TYPES,
						new String[] {
							nextSourceName,
							nextSourceType.getName(),
							matchingTargetBinding.getType().getName()
						});
				}
			}
		}
	}
	
	private void checkExpressionsForByPosition(Expression sourceExpr, Expression targetExpr, ITypeBinding sourceType, ITypeBinding targetType) {
		if(!checkExpressionsForByNameOrByPosition(sourceExpr, targetExpr, sourceType, targetType)) {
			return;
		}
		
		if(ITypeBinding.FORM_BINDING == sourceType.getKind()) {
			problemRequestor.acceptProblem(
				sourceExpr,
				IProblemRequestor.BYPOSITION_USED_WITH_FLEXIBLE_RECORD_AND_FORM);
			return;
		}
		if(ITypeBinding.FORM_BINDING == targetType.getKind()) {
			problemRequestor.acceptProblem(
				targetExpr,
				IProblemRequestor.BYPOSITION_USED_WITH_FLEXIBLE_RECORD_AND_FORM);
			return;
		}
		
		List sourceDataBindings = getTopLevelDataBindings(sourceExpr);
		List targetDataBindings = getTopLevelDataBindings(targetExpr);
		
		if(sourceDataBindings.isEmpty()) {
			problemRequestor.acceptProblem(
				sourceExpr,
				IProblemRequestor.MOVE_STATEMENT_BYNAME_BYPOSITION_ITEM_HAS_NO_SUBSTRUCTURE,
				IMarker.SEVERITY_WARNING,
				new String[] {sourceExpr.getCanonicalString()});
		}
		
		Iterator targetBindingsIter = targetDataBindings.iterator();
		for(Iterator sourceBindingsIter = sourceDataBindings.iterator(); sourceBindingsIter.hasNext();) {
			IDataBinding nextSourceBinding = (IDataBinding) sourceBindingsIter.next();
			if(targetBindingsIter.hasNext()) {
				IDataBinding nextTargetBinding = (IDataBinding) targetBindingsIter.next(); 
				if(InternUtil.intern("*") != nextSourceBinding.getName()) {
					if(!TypeCompatibilityUtil.isMoveCompatible(nextTargetBinding.getType(), nextSourceBinding.getType(), null, compilerOptions)) {
						problemRequestor.acceptProblem(
							targetExpr,
							IProblemRequestor.MOVE_BY_POSITION_INCOMPATIBLE_TYPES,
							new String[] {
								nextSourceBinding.getCaseSensitiveName(),
								nextTargetBinding.getCaseSensitiveName(),
								nextSourceBinding.getType().getCaseSensitiveName(),
								nextTargetBinding.getType().getCaseSensitiveName()
							});
					}
				}
			}
		}
	}
	
	private boolean checkExpressionsForForOrForAll(Expression sourceExpr, Expression targetExpr, ITypeBinding sourceType, ITypeBinding targetType) {
		boolean targetIsValid = false;
		int problemKind = IProblemRequestor.MOVE_STATEMENT_TARGET_WRONG_TYPE_FOR_SCALAR_SOURCE;
		
		IDataBinding targetDBinding = targetExpr.resolveDataBinding();
		if(targetDBinding != null && targetDBinding != IBinding.NOT_FOUND_BINDING) {
			if(IDataBinding.STRUCTURE_ITEM_BINDING == targetDBinding.getKind()) {
				StructureItemBinding strItemBinding = (StructureItemBinding) targetDBinding;
				if(ITypeBinding.DATATABLE_BINDING == strItemBinding.getEnclosingStructureBinding().getKind()) {
					targetIsValid = TypeCompatibilityUtil.isMoveCompatible(targetDBinding.getType(), sourceType, sourceExpr, compilerOptions);
				}
				else if(strItemBinding.isMultiplyOccuring()) {
					targetIsValid = TypeCompatibilityUtil.isMoveCompatible(targetDBinding.getType(), sourceType, sourceExpr, compilerOptions);
				}
			}
		}
		
		if(ITypeBinding.ARRAY_TYPE_BINDING == sourceType.getKind() && isRecord(((ArrayTypeBinding) sourceType).getElementType())) {
			problemKind = IProblemRequestor.MOVE_STATEMENT_TARGET_WRONG_TYPE_FOR_CONTAINER_SOURCE;
			targetIsValid = ITypeBinding.ARRAY_TYPE_BINDING == targetType.getKind() && isRecord(((ArrayTypeBinding) targetType).getElementType());			 
		}
		else if(ITypeBinding.ARRAY_TYPE_BINDING == sourceType.getKind()) {
			problemKind = IProblemRequestor.MOVE_STATEMENT_TARGET_WRONG_TYPE_FOR_ARRAY_SOURCE;
			if(ITypeBinding.ARRAY_TYPE_BINDING == targetType.getKind()) {
				targetIsValid = TypeCompatibilityUtil.isMoveCompatible(
					((ArrayTypeBinding) targetType).getElementType(),
					((ArrayTypeBinding) sourceType).getElementType(), 
					null,
                	compilerOptions);
			}
			else if(ITypeBinding.MULTIPLY_OCCURING_ITEM == targetType.getKind()) {
				targetIsValid = TypeCompatibilityUtil.isMoveCompatible(                	
                	((MultiplyOccuringItemTypeBinding) targetType).getBaseType(),
                	((ArrayTypeBinding) sourceType).getElementType(),
                	null,
                	compilerOptions);
			}
			else if(targetDBinding != null && targetDBinding != IBinding.NOT_FOUND_BINDING) {
				switch(targetDBinding.getKind()) {
				case IDataBinding.FORM_FIELD:
					targetIsValid = ((FormFieldBinding) targetDBinding).isMultiplyOccuring() &&
						TypeCompatibilityUtil.isMoveCompatible(
							targetType,
							((ArrayTypeBinding) sourceType).getElementType(),
							null,
		                	compilerOptions);
					break;
				case IDataBinding.STRUCTURE_ITEM_BINDING:
					targetIsValid = false;
					break;
				default:
					targetIsValid = false;
				}
			}
			else {
				targetIsValid = false;
			}
		}
		else if(ITypeBinding.ARRAY_TYPE_BINDING == targetType.getKind()) {
			if(ITypeBinding.MULTIPLY_OCCURING_ITEM == sourceType.getKind()) {
				targetIsValid = TypeCompatibilityUtil.isMoveCompatible(((ArrayTypeBinding) targetType).getElementType(), sourceType.getBaseType(), null, compilerOptions);
			}
			else {
				targetIsValid = TypeCompatibilityUtil.isMoveCompatible(((ArrayTypeBinding) targetType).getElementType(), sourceType, null, compilerOptions);
			}
		}
		else if(ITypeBinding.MULTIPLY_OCCURING_ITEM == targetType.getKind()) {
			if(ITypeBinding.MULTIPLY_OCCURING_ITEM == sourceType.getKind()) {
				targetIsValid = TypeCompatibilityUtil.isMoveCompatible(targetType.getBaseType(), sourceType.getBaseType(), null, compilerOptions);
			}
			else if(ITypeBinding.ARRAY_TYPE_BINDING == sourceType.getKind()) {
				targetIsValid = TypeCompatibilityUtil.isMoveCompatible(
					targetType.getBaseType(),
					((ArrayTypeBinding) sourceType).getElementType(),
					null,
					compilerOptions);
			}
			else {
				targetIsValid = TypeCompatibilityUtil.isMoveCompatible(targetType.getBaseType(), sourceType, null, compilerOptions);
			}
		}
		if(!targetIsValid) {
			problemRequestor.acceptProblem(
				targetExpr,
				problemKind,
				new String[] {targetExpr.getCanonicalString()});
		}
		return true;
	}
	
	private void checkExpressionsForFor(Expression sourceExpr, Expression targetExpr, ITypeBinding sourceType, ITypeBinding targetType) {
		if(!checkExpressionsForForOrForAll(sourceExpr, targetExpr, sourceType, targetType)) {
			return;
		}
	}
	
	private void checkExpressionsForForAll(Expression sourceExpr, Expression targetExpr, ITypeBinding sourceType, ITypeBinding targetType) {
		if(!checkExpressionsForForOrForAll(sourceExpr, targetExpr, sourceType, targetType)) {
			return;
		}
	}
	
	private boolean checkNamesUnique(Map namesToDataBindingsMap, Expression sourceOrTargetExpression, int problemKind) {
		for(Iterator iter = namesToDataBindingsMap.keySet().iterator(); iter.hasNext();) {
			String next = (String) iter.next();
			if(!next.equals("*")) {
				if(AmbiguousDataBinding.getInstance() == namesToDataBindingsMap.get(next)) {
					problemRequestor.acceptProblem(
						sourceOrTargetExpression,
						problemKind,
						new String[] {sourceOrTargetExpression.getCanonicalString()});
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean checkNoMultiplyOccuringStructureItems(Map namesToDataBindingsMap, Expression sourceOrTargetExpression, int problemKind) {
		for(Iterator iter = namesToDataBindingsMap.values().iterator(); iter.hasNext();) {
			IDataBinding nextDBinding = (IDataBinding) iter.next();
			if(IDataBinding.STRUCTURE_ITEM_BINDING == nextDBinding.getKind()) {
				StructureItemBinding strItemBinding = (StructureItemBinding) nextDBinding;
				if(strItemBinding.hasOccurs() &&
				   strItemBinding.getParentItem() != null &&
				   strItemBinding.getParentItem().isMultiplyOccuring()) {
					problemRequestor.acceptProblem(
						sourceOrTargetExpression,
						problemKind,
						new String[] {sourceOrTargetExpression.getCanonicalString()});
					return false;
				}
			}
		}
		return true;
	}
	
	private static boolean isContainer(Expression expr) {
		IDataBinding dBinding = expr.resolveDataBinding();
		if(dBinding != null && dBinding != IBinding.NOT_FOUND_BINDING) {
			if(IDataBinding.STRUCTURE_ITEM_BINDING == dBinding.getKind()) {
				return true;
			}
		}
		ITypeBinding tBinding = expr.resolveTypeBinding();
		if(ITypeBinding.ARRAY_TYPE_BINDING == tBinding.getKind()) {
			return isFixedRecordOrForm(((ArrayTypeBinding) tBinding).getBaseType());
		}
		return isRecordOrForm(expr.resolveTypeBinding());
	}
	
	private static boolean isStructureItem(Expression expr) {
		IDataBinding dBinding = expr.resolveDataBinding();
		if(dBinding != null && dBinding != IBinding.NOT_FOUND_BINDING) {
			if(IDataBinding.STRUCTURE_ITEM_BINDING == dBinding.getKind()) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean isRecord(ITypeBinding type) {
		return ITypeBinding.FIXED_RECORD_BINDING == type.getKind() ||
		       ITypeBinding.FLEXIBLE_RECORD_BINDING == type.getKind();
	}
	
	private static boolean isRecordOrForm(ITypeBinding type) {
		return ITypeBinding.FLEXIBLE_RECORD_BINDING == type.getKind() ||
		       isFixedRecordOrForm(type);
	}
	
	private static boolean isFixedRecordOrForm(ITypeBinding type) {
		return ITypeBinding.FIXED_RECORD_BINDING == type.getKind() ||
			   ITypeBinding.FORM_BINDING == type.getKind();
	}
	
	private static Map getNamesToDataBindingsMap(Expression expr) {
		IDataBinding dBinding = expr.resolveDataBinding();
		if(dBinding != null && dBinding != IBinding.NOT_FOUND_BINDING && IDataBinding.STRUCTURE_ITEM_BINDING == dBinding.getKind()) {
			return ((StructureItemBinding) dBinding).getSimpleNamesToDataBindingsMap();
		}
		return getNamesToDataBindingsMap(expr.resolveTypeBinding());
	}
	
	private static Map getNamesToDataBindingsMap(ITypeBinding type) {
		if(ITypeBinding.FORM_BINDING == type.getKind() ||
		   ITypeBinding.FIXED_RECORD_BINDING == type.getKind()) {
			return type.getSimpleNamesToDataBindingsMap();
		}
		if(ITypeBinding.FLEXIBLE_RECORD_BINDING == type.getKind()) {
			Map result = new HashMap();
			for(Iterator iter = ((FlexibleRecordBinding) type).getDeclaredFields().iterator(); iter.hasNext();) {
				IDataBinding next = (IDataBinding) iter.next();
				result.put(next.getName(), next);
			}
			return result;
		}
		if(ITypeBinding.ARRAY_TYPE_BINDING == type.getKind()) {
			return getNamesToDataBindingsMap(((ArrayTypeBinding) type).getBaseType());
		}
		return Collections.EMPTY_MAP;
	}
	
	private static List getTopLevelDataBindings(Expression expr) {
		IDataBinding dBinding = expr.resolveDataBinding();
		if(dBinding != null && dBinding != IBinding.NOT_FOUND_BINDING && IDataBinding.STRUCTURE_ITEM_BINDING == dBinding.getKind()) {
			return ((StructureItemBinding) dBinding).getChildren();
		}
		return getTopLevelDataBindings(expr.resolveTypeBinding());
	}
	
	private static List getTopLevelDataBindings(ITypeBinding type) {
		if(ITypeBinding.FORM_BINDING == type.getKind()) {
			return ((FormBinding) type).getFields();
		}
		else if(ITypeBinding.FIXED_RECORD_BINDING == type.getKind()) {
			return ((FixedRecordBinding) type).getStructureItems();
		}
		if(ITypeBinding.FLEXIBLE_RECORD_BINDING == type.getKind()) {
			return ((FlexibleRecordBinding) type).getDeclaredFields();
		}
		if(ITypeBinding.ARRAY_TYPE_BINDING == type.getKind()) {
			return getTopLevelDataBindings(((ArrayTypeBinding) type).getBaseType());
		}
		return Collections.EMPTY_LIST;
	}
	
	private String getCanonicalStringNoSubscripts(Expression expr) {
		final String[] result = new String[] {null};
		expr.accept(new AbstractASTExpressionVisitor() {
			public boolean visitExpression(Expression expression) {
				result[0] = expression.getCanonicalString();
				return false;
			}
			
			public boolean visit(ParenthesizedExpression parenthesizedExpression) {
				return true;
			}
			
			public boolean visit(ArrayAccess arrayAccess) {
				arrayAccess.getArray().accept(this);
				return false;
			}
			
			public boolean visit(SubstringAccess substringAccess) {
				substringAccess.getPrimary().accept(this);
				return false;
			}
		});
		return result[0];
	}
}
