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
package org.eclipse.edt.mof.egl.impl;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EEnum;
import org.eclipse.edt.mof.egl.*;
import org.eclipse.edt.mof.impl.EFactoryImpl;

public abstract class IrFactoryBase extends EFactoryImpl implements IrFactory {
	@Override
	public EEnum getAccessKindEEnum() {
		return (EEnum)getTypeNamed(AccessKind);
	}
	
	@Override
	public EClass getAddStatementEClass() {
		return (EClass)getTypeNamed(AddStatement);
	}
	
	@Override
	public EClass getAnnotationEClass() {
		return (EClass)getTypeNamed(Annotation);
	}
	
	@Override
	public EClass getAnnotationTypeEClass() {
		return (EClass)getTypeNamed(AnnotationType);
	}
	
	@Override
	public EClass getArrayAccessEClass() {
		return (EClass)getTypeNamed(ArrayAccess);
	}
	
	@Override
	public EClass getArrayElementMemberAccessEClass() {
		return (EClass)getTypeNamed(ArrayElementMemberAccess);
	}
	
	@Override
	public EClass getArrayLiteralEClass() {
		return (EClass)getTypeNamed(ArrayLiteral);
	}
	
	@Override
	public EClass getArrayTypeEClass() {
		return (EClass)getTypeNamed(ArrayType);
	}
	
	@Override
	public EClass getAsExpressionEClass() {
		return (EClass)getTypeNamed(AsExpression);
	}
	
	@Override
	public EClass getAssignmentEClass() {
		return (EClass)getTypeNamed(Assignment);
	}
	
	@Override
	public EClass getAssignmentStatementEClass() {
		return (EClass)getTypeNamed(AssignmentStatement);
	}
	
	@Override
	public EClass getBinaryExpressionEClass() {
		return (EClass)getTypeNamed(BinaryExpression);
	}
	
	@Override
	public EClass getBoxingExpressionEClass() {
		return (EClass)getTypeNamed(BoxingExpression);
	}

	@Override
	public EClass getBooleanLiteralEClass() {
		return (EClass)getTypeNamed(BooleanLiteral);
	}
	
	@Override
	public EClass getBytesLiteralEClass() {
		return (EClass)getTypeNamed(BytesLiteral);
	}
	
	@Override
	public EClass getBuiltInOperationEClass() {
		return (EClass)getTypeNamed(BuiltInOperation);
	}
	
	@Override
	public EClass getBuiltInOperationExpressionEClass() {
		return (EClass)getTypeNamed(BuiltInOperationExpression);
	}
	
	@Override
	public EClass getCallStatementEClass() {
		return (EClass)getTypeNamed(CallStatement);
	}
	
	@Override
	public EClass getCaseStatementEClass() {
		return (EClass)getTypeNamed(CaseStatement);
	}
	
	@Override
	public EClass getCharLiteralEClass() {
		return (EClass)getTypeNamed(CharLiteral);
	}
	
	@Override
	public EClass getClassifierEClass() {
		return (EClass)getTypeNamed(Classifier);
	}
	
	@Override
	public EClass getCloseStatementEClass() {
		return (EClass)getTypeNamed(CloseStatement);
	}
	
	@Override
	public EClass getConditionalStatementEClass() {
		return (EClass)getTypeNamed(ConditionalStatement);
	}
	
	@Override
	public EClass getConstantFieldEClass() {
		return (EClass)getTypeNamed(ConstantField);
	}
	
	@Override
	public EClass getConstantFormFieldEClass() {
		return (EClass)getTypeNamed(ConstantFormField);
	}
	
	@Override
	public EClass getConstructorEClass() {
		return (EClass)getTypeNamed(Constructor);
	}
	
	@Override
	public EClass getContainerEClass() {
		return (EClass)getTypeNamed(Container);
	}
	
	@Override
	public EClass getContinueStatementEClass() {
		return (EClass)getTypeNamed(ContinueStatement);
	}
	
	@Override
	public EClass getConverseStatementEClass() {
		return (EClass)getTypeNamed(ConverseStatement);
	}
	
	@Override
	public EClass getConversionOperationEClass() {
		return (EClass)getTypeNamed(ConversionOperation);
	}
	
	@Override
	public EClass getConvertExpressionEClass() {
		return (EClass)getTypeNamed(ConvertExpression);
	}
	
	@Override
	public EClass getConvertStatementEClass() {
		return (EClass)getTypeNamed(ConvertStatement);
	}
	
	@Override
	public EClass getDanglingReferenceEClass() {
		return (EClass)getTypeNamed(DanglingReference);
	}
	
	@Override
	public EClass getDataItemEClass() {
		return (EClass)getTypeNamed(DataItem);
	}
	
	@Override
	public EClass getDataTableEClass() {
		return (EClass)getTypeNamed(DataTable);
	}
	
	@Override
	public EClass getDataTypeEClass() {
		return (EClass)getTypeNamed(DataType);
	}
	
	@Override
	public EClass getDBCharLiteralEClass() {
		return (EClass)getTypeNamed(DBCharLiteral);
	}
	
	@Override
	public EClass getDecimalLiteralEClass() {
		return (EClass)getTypeNamed(DecimalLiteral);
	}
	
	@Override
	public EClass getDeclarationExpressionEClass() {
		return (EClass)getTypeNamed(DeclarationExpression);
	}
	
	@Override
	public EClass getDelegateEClass() {
		return (EClass)getTypeNamed(Delegate);
	}
	
	@Override
	public EClass getDeleteStatementEClass() {
		return (EClass)getTypeNamed(DeleteStatement);
	}
	
	@Override
	public EEnum getDirectionKindEEnum() {
		return (EEnum)getTypeNamed(DirectionKind);
	}
	
	@Override
	public EClass getDisplayStatementEClass() {
		return (EClass)getTypeNamed(DisplayStatement);
	}
	
	@Override
	public EClass getDynamicAccessEClass() {
		return (EClass)getTypeNamed(DynamicAccess);
	}
	
	@Override
	public EClass getEClassProxyEClass() {
		return (EClass)getTypeNamed(EClassProxy);
	}
	
	@Override
	public EClass getEGLClassEClass() {
		return (EClass)getTypeNamed(EGLClass);
	}
	
	@Override
	public EClass getElementEClass() {
		return (EClass)getTypeNamed(Element);
	}
	
	@Override
	public EClass getElementAnnotationsEClass() {
		return (EClass)getTypeNamed(ElementAnnotations);
	}
	
	@Override
	public EEnum getElementKindEEnum() {
		return (EEnum)getTypeNamed(ElementKind);
	}
	
	@Override
	public EClass getEmptyStatementEClass() {
		return (EClass)getTypeNamed(EmptyStatement);
	}
	
	@Override
	public EClass getEnumerationEClass() {
		return (EClass)getTypeNamed(Enumeration);
	}
	
	@Override
	public EClass getEnumerationEntryEClass() {
		return (EClass)getTypeNamed(EnumerationEntry);
	}
	
	@Override
	public EEnum getETypeKindEEnum() {
		return (EEnum)getTypeNamed(ETypeKind);
	}
	
	@Override
	public EClass getExceptionBlockEClass() {
		return (EClass)getTypeNamed(ExceptionBlock);
	}
	
	@Override
	public EClass getExecuteStatementEClass() {
		return (EClass)getTypeNamed(ExecuteStatement);
	}
	
	@Override
	public EClass getExitStatementEClass() {
		return (EClass)getTypeNamed(ExitStatement);
	}
	
	@Override
	public EClass getExpressionEClass() {
		return (EClass)getTypeNamed(Expression);
	}
	
	@Override
	public EClass getExpressionStatementEClass() {
		return (EClass)getTypeNamed(ExpressionStatement);
	}
	
	@Override
	public EClass getExternalTypeEClass() {
		return (EClass)getTypeNamed(ExternalType);
	}
	
	@Override
	public EClass getFieldEClass() {
		return (EClass)getTypeNamed(Field);
	}
	
	@Override
	public EClass getFixedPrecisionTypeEClass() {
		return (EClass)getTypeNamed(FixedPrecisionType);
	}
	
	@Override
	public EClass getFloatingPointLiteralEClass() {
		return (EClass)getTypeNamed(FloatingPointLiteral);
	}
	
	@Override
	public EClass getForEachStatementEClass() {
		return (EClass)getTypeNamed(ForEachStatement);
	}
	
	@Override
	public EClass getFormEClass() {
		return (EClass)getTypeNamed(Form);
	}
	
	@Override
	public EClass getFormFieldEClass() {
		return (EClass)getTypeNamed(FormField);
	}
	
	@Override
	public EClass getFormGroupEClass() {
		return (EClass)getTypeNamed(FormGroup);
	}
	
	@Override
	public EClass getForStatementEClass() {
		return (EClass)getTypeNamed(ForStatement);
	}
	
	@Override
	public EClass getForwardStatementEClass() {
		return (EClass)getTypeNamed(ForwardStatement);
	}
	
	@Override
	public EClass getFreeSqlStatementEClass() {
		return (EClass)getTypeNamed(FreeSqlStatement);
	}
	
	@Override
	public EClass getFunctionEClass() {
		return (EClass)getTypeNamed(Function);
	}
	
	@Override
	public EClass getFunctionInvocationEClass() {
		return (EClass)getTypeNamed(FunctionInvocation);
	}
	
	@Override
	public EClass getDelegateInvocationEClass() {
		return (EClass)getTypeNamed(DelegateInvocation);
	}
	
	@Override
	public EClass getFunctionMemberEClass() {
		return (EClass)getTypeNamed(FunctionMember);
	}
	
	@Override
	public EClass getFunctionParameterEClass() {
		return (EClass)getTypeNamed(FunctionParameter);
	}
	
	@Override
	public EClass getFunctionPartEClass() {
		return (EClass)getTypeNamed(FunctionPart);
	}
	
	@Override
	public EClass getFunctionPartInvocationEClass() {
		return (EClass)getTypeNamed(FunctionPartInvocation);
	}
	
	@Override
	public EClass getFunctionReturnFieldEClass() {
		return (EClass)getTypeNamed(FunctionReturnField);
	}
	
	@Override
	public EClass getFunctionStatementEClass() {
		return (EClass)getTypeNamed(FunctionStatement);
	}
	
	@Override
	public EClass getGenericTypeEClass() {
		return (EClass)getTypeNamed(GenericType);
	}
	
	@Override
	public EClass getGetByKeyStatementEClass() {
		return (EClass)getTypeNamed(GetByKeyStatement);
	}
	
	@Override
	public EEnum getGetByPositionKindEEnum() {
		return (EEnum)getTypeNamed(GetByPositionKind);
	}
	
	@Override
	public EClass getGetByPositionStatementEClass() {
		return (EClass)getTypeNamed(GetByPositionStatement);
	}
	
	@Override
	public EClass getGoToStatementEClass() {
		return (EClass)getTypeNamed(GoToStatement);
	}
	
	@Override
	public EClass getHandlerEClass() {
		return (EClass)getTypeNamed(Handler);
	}
	
	@Override
	public EClass getHexLiteralEClass() {
		return (EClass)getTypeNamed(HexLiteral);
	}
	
	@Override
	public EClass getIfStatementEClass() {
		return (EClass)getTypeNamed(IfStatement);
	}
	
	@Override
	public EClass getInExpressionEClass() {
		return (EClass)getTypeNamed(InExpression);
	}
	
	@Override
	public EClass getIntegerLiteralEClass() {
		return (EClass)getTypeNamed(IntegerLiteral);
	}
	
	@Override
	public EClass getInterfaceEClass() {
		return (EClass)getTypeNamed(Interface);
	}
	
	@Override
	public EClass getIntervalTypeEClass() {
		return (EClass)getTypeNamed(IntervalType);
	}
	
	@Override
	public EClass getInvalidNameEClass() {
		return (EClass)getTypeNamed(InvalidName);
	}
	
	@Override
	public EClass getInvocationExpressionEClass() {
		return (EClass)getTypeNamed(InvocationExpression);
	}
	
	@Override
	public EClass getIOStatementEClass() {
		return (EClass)getTypeNamed(IOStatement);
	}
	
	@Override
	public EClass getIsAExpressionEClass() {
		return (EClass)getTypeNamed(IsAExpression);
	}
	
	@Override
	public EClass getIsNotExpressionEClass() {
		return (EClass)getTypeNamed(IsNotExpression);
	}
	
	@Override
	public EClass getLabelStatementEClass() {
		return (EClass)getTypeNamed(LabelStatement);
	}
	
	@Override
	public EClass getLHSExprEClass() {
		return (EClass)getTypeNamed(LHSExpr);
	}
	
	@Override
	public EClass getLibraryEClass() {
		return (EClass)getTypeNamed(Library);
	}
	
	@Override
	public EClass getLiteralEClass() {
		return (EClass)getTypeNamed(Literal);
	}
	
	@Override
	public EClass getLocalVariableDeclarationStatementEClass() {
		return (EClass)getTypeNamed(LocalVariableDeclarationStatement);
	}
	
	@Override
	public EClass getLogicAndDataPartEClass() {
		return (EClass)getTypeNamed(LogicAndDataPart);
	}
	
	@Override
	public EClass getLoopStatementEClass() {
		return (EClass)getTypeNamed(LoopStatement);
	}
	
	@Override
	public EClass getMBCharLiteralEClass() {
		return (EClass)getTypeNamed(MBCharLiteral);
	}
	
	@Override
	public EClass getMemberEClass() {
		return (EClass)getTypeNamed(Member);
	}
	
	@Override
	public EClass getMemberAccessEClass() {
		return (EClass)getTypeNamed(MemberAccess);
	}
	
	@Override
	public EClass getMemberNameEClass() {
		return (EClass)getTypeNamed(MemberName);
	}
	
	@Override
	public EClass getMoveStatementEClass() {
		return (EClass)getTypeNamed(MoveStatement);
	}
	
	@Override
	public EClass getNameEClass() {
		return (EClass)getTypeNamed(Name);
	}
	
	@Override
	public EClass getNamedElementEClass() {
		return (EClass)getTypeNamed(NamedElement);
	}
	
	@Override
	public EClass getNewExpressionEClass() {
		return (EClass)getTypeNamed(NewExpression);
	}
	
	@Override
	public EClass getNullLiteralEClass() {
		return (EClass)getTypeNamed(NullLiteral);
	}
	
	@Override
	public EClass getNullTypeEClass() {
		return (EClass)getTypeNamed(NullType);
	}
	
	@Override
	public EClass getNumericLiteralEClass() {
		return (EClass)getTypeNamed(NumericLiteral);
	}

	@Override
	public EClass getObjectExpressionEClass() {
		return (EClass)getTypeNamed(ObjectExpression);
	}

	@Override
	public EClass getObjectExpressionEntryEClass() {
		return (EClass)getTypeNamed(ObjectExpressionEntry);
	}

	@Override
	public EClass getOpenStatementEClass() {
		return (EClass)getTypeNamed(OpenStatement);
	}
	
	@Override
	public EClass getOpenUIStatementEClass() {
		return (EClass)getTypeNamed(OpenUIStatement);
	}
	
	@Override
	public EClass getOperationEClass() {
		return (EClass)getTypeNamed(Operation);
	}
	
	@Override
	public EClass getParameterEClass() {
		return (EClass)getTypeNamed(Parameter);
	}
	
	@Override
	public EClass getParameterizableTypeEClass() {
		return (EClass)getTypeNamed(ParameterizableType);
	}
	
	@Override
	public EClass getParameterizedTypeEClass() {
		return (EClass)getTypeNamed(ParameterizedType);
	}
	
	@Override
	public EEnum getParameterKindEEnum() {
		return (EEnum)getTypeNamed(ParameterKind);
	}
	
	@Override
	public EClass getPartEClass() {
		return (EClass)getTypeNamed(Part);
	}
	
	@Override
	public EClass getPartNameEClass() {
		return (EClass)getTypeNamed(PartName);
	}

	@Override
	public EClass getTypeNameEClass() {
		return (EClass)getTypeNamed(TypeName);
	}
	
	@Override
	public EClass getPatternTypeEClass() {
		return (EClass)getTypeNamed(PatternType);
	}
	
	@Override
	public EClass getPrepareStatementEClass() {
		return (EClass)getTypeNamed(PrepareStatement);
	}
	
	@Override
	public EClass getPrimitiveTypeLiteralEClass() {
		return (EClass)getTypeNamed(PrimitiveTypeLiteral);
	}
	
	@Override
	public EClass getPrintStatementEClass() {
		return (EClass)getTypeNamed(PrintStatement);
	}
	
	@Override
	public EClass getProgramEClass() {
		return (EClass)getTypeNamed(Program);
	}
	
	@Override
	public EClass getProgramParameterEClass() {
		return (EClass)getTypeNamed(ProgramParameter);
	}
	
	@Override
	public EClass getQualifiedFunctionInvocationEClass() {
		return (EClass)getTypeNamed(QualifiedFunctionInvocation);
	}
	
	@Override
	public EClass getRecordEClass() {
		return (EClass)getTypeNamed(Record);
	}
	
	@Override
	public EClass getReplaceStatementEClass() {
		return (EClass)getTypeNamed(ReplaceStatement);
	}
	
	@Override
	public EClass getReturnStatementEClass() {
		return (EClass)getTypeNamed(ReturnStatement);
	}
	
	@Override
	public EClass getSequenceTypeEClass() {
		return (EClass)getTypeNamed(SequenceType);
	}
	
	@Override
	public EClass getServiceEClass() {
		return (EClass)getTypeNamed(Service);
	}
	
	@Override
	public EClass getSetStatementEClass() {
		return (EClass)getTypeNamed(SetStatement);
	}
	
	@Override
	public EClass getSetValuesExpressionEClass() {
		return (EClass)getTypeNamed(SetValuesExpression);
	}

	@Override
	public EClass getSetValuesStatementEClass() {
		return (EClass)getTypeNamed(SetValuesStatement);
	}
	
	@Override
	public EClass getShowStatementEClass() {
		return (EClass)getTypeNamed(ShowStatement);
	}
	
	@Override
	public EClass getSizeInBytesExpressionEClass() {
		return (EClass)getTypeNamed(SizeInBytesExpression);
	}
	
	@Override
	public EClass getSizeOfExpressionEClass() {
		return (EClass)getTypeNamed(SizeOfExpression);
	}
	
	@Override
	public EClass getStatementEClass() {
		return (EClass)getTypeNamed(Statement);
	}
	
	@Override
	public EClass getStatementBlockEClass() {
		return (EClass)getTypeNamed(StatementBlock);
	}
	
	@Override
	public EClass getStereotypeEClass() {
		return (EClass)getTypeNamed(Stereotype);
	}
	
	@Override
	public EClass getStereotypeTypeEClass() {
		return (EClass)getTypeNamed(StereotypeType);
	}
	
	@Override
	public EClass getStringLiteralEClass() {
		return (EClass)getTypeNamed(StringLiteral);
	}
	
	@Override
	public EClass getStructPartEClass() {
		return (EClass)getTypeNamed(StructPart);
	}
	
	@Override
	public EClass getStructuredContainerEClass() {
		return (EClass)getTypeNamed(StructuredContainer);
	}
	
	@Override
	public EClass getStructuredFieldEClass() {
		return (EClass)getTypeNamed(StructuredField);
	}
	
	@Override
	public EClass getStructuredRecordEClass() {
		return (EClass)getTypeNamed(StructuredRecord);
	}
	
	@Override
	public EClass getSubstringAccessEClass() {
		return (EClass)getTypeNamed(SubstringAccess);
	}
	
	@Override
	public EClass getSystemFunctionArgumentMnemonicNameEClass() {
		return (EClass)getTypeNamed(SystemFunctionArgumentMnemonicName);
	}
	
	@Override
	public EClass getTernaryExpressionEClass() {
		return (EClass)getTypeNamed(TernaryExpression);
	}

	@Override
	public EClass getTextTypeLiteralEClass() {
		return (EClass)getTypeNamed(TextTypeLiteral);
	}
	
	@Override
	public EClass getThisExpressionEClass() {
		return (EClass)getTypeNamed(ThisExpression);
	}
	
	@Override
	public EClass getThrowStatementEClass() {
		return (EClass)getTypeNamed(ThrowStatement);
	}
	
	@Override
	public EClass getTimestampTypeEClass() {
		return (EClass)getTypeNamed(TimestampType);
	}
	
	@Override
	public EClass getTopLevelFunctionNameEClass() {
		return (EClass)getTypeNamed(TopLevelFunctionName);
	}
	
	@Override
	public EClass getTransferStatementEClass() {
		return (EClass)getTypeNamed(TransferStatement);
	}
	
	@Override
	public EClass getTryStatementEClass() {
		return (EClass)getTypeNamed(TryStatement);
	}
	
	@Override
	public EClass getTypeEClass() {
		return (EClass)getTypeNamed(Type);
	}
	
	@Override
	public EClass getTypedElementEClass() {
		return (EClass)getTypeNamed(TypedElement);
	}
	
	@Override
	public EClass getTypeExpressionEClass() {
		return (EClass)getTypeNamed(TypeExpression);
	}
	
	@Override
	public EClass getTypeParameterEClass() {
		return (EClass)getTypeNamed(TypeParameter);
	}
	
	@Override
	public EClass getUnaryExpressionEClass() {
		return (EClass)getTypeNamed(UnaryExpression);
	}
	
	@Override
	public EClass getVariableFormFieldEClass() {
		return (EClass)getTypeNamed(VariableFormField);
	}
	
	@Override
	public EClass getWhenClauseEClass() {
		return (EClass)getTypeNamed(WhenClause);
	}
	
	@Override
	public EClass getWhileStatementEClass() {
		return (EClass)getTypeNamed(WhileStatement);
	}
	
	@Override
	public AddStatement createAddStatement() {
		return (AddStatement)getAddStatementEClass().newInstance();
	}
	
	@Override
	public Annotation createAnnotation() {
		return (Annotation)getAnnotationEClass().newInstance();
	}
	
	@Override
	public AnnotationType createAnnotationType() {
		return (AnnotationType)getAnnotationTypeEClass().newInstance();
	}
	
	@Override
	public ArrayAccess createArrayAccess() {
		return (ArrayAccess)getArrayAccessEClass().newInstance();
	}
	
	@Override
	public ArrayElementMemberAccess createArrayElementMemberAccess() {
		return (ArrayElementMemberAccess)getArrayElementMemberAccessEClass().newInstance();
	}
	
	@Override
	public ArrayLiteral createArrayLiteral() {
		return (ArrayLiteral)getArrayLiteralEClass().newInstance();
	}
	
	@Override
	public ArrayType createArrayType() {
		return (ArrayType)getArrayTypeEClass().newInstance();
	}
	
	@Override
	public AsExpression createAsExpression() {
		return (AsExpression)getAsExpressionEClass().newInstance();
	}
	
	@Override
	public Assignment createAssignment() {
		return (Assignment)getAssignmentEClass().newInstance();
	}
	
	@Override
	public AssignmentStatement createAssignmentStatement() {
		return (AssignmentStatement)getAssignmentStatementEClass().newInstance();
	}
	
	@Override
	public BinaryExpression createBinaryExpression() {
		return (BinaryExpression)getBinaryExpressionEClass().newInstance();
	}
	
	@Override
	public BooleanLiteral createBooleanLiteral() {
		return (BooleanLiteral)getBooleanLiteralEClass().newInstance();
	}
	
	@Override
	public BytesLiteral createBytesLiteral() {
		return (BytesLiteral)getBytesLiteralEClass().newInstance();
	}
	
	@Override
	public BoxingExpression createBoxingExpression() {
		return (BoxingExpression)getBoxingExpressionEClass().newInstance();
	}

	@Override
	public BuiltInOperation createBuiltInOperation() {
		return (BuiltInOperation)getBuiltInOperationEClass().newInstance();
	}
	
	@Override
	public BuiltInOperationExpression createBuiltInOperationExpression() {
		return (BuiltInOperationExpression)getBuiltInOperationExpressionEClass().newInstance();
	}
	
	@Override
	public CallStatement createCallStatement() {
		return (CallStatement)getCallStatementEClass().newInstance();
	}
	
	@Override
	public CaseStatement createCaseStatement() {
		return (CaseStatement)getCaseStatementEClass().newInstance();
	}
	
	@Override
	public CharLiteral createCharLiteral() {
		return (CharLiteral)getCharLiteralEClass().newInstance();
	}
	
	@Override
	public CloseStatement createCloseStatement() {
		return (CloseStatement)getCloseStatementEClass().newInstance();
	}
	
	@Override
	public ConstantField createConstantField() {
		return (ConstantField)getConstantFieldEClass().newInstance();
	}
	
	@Override
	public ConstantFormField createConstantFormField() {
		return (ConstantFormField)getConstantFormFieldEClass().newInstance();
	}
	
	@Override
	public Constructor createConstructor() {
		return (Constructor)getConstructorEClass().newInstance();
	}
	
	@Override
	public ContinueStatement createContinueStatement() {
		return (ContinueStatement)getContinueStatementEClass().newInstance();
	}
	
	@Override
	public ConverseStatement createConverseStatement() {
		return (ConverseStatement)getConverseStatementEClass().newInstance();
	}
	
	@Override
	public ConversionOperation createConversionOperation() {
		return (ConversionOperation)getConversionOperationEClass().newInstance();
	}
	
	@Override
	public ConvertExpression createConvertExpression() {
		return (ConvertExpression)getConvertExpressionEClass().newInstance();
	}
	
	@Override
	public ConvertStatement createConvertStatement() {
		return (ConvertStatement)getConvertStatementEClass().newInstance();
	}
	
	@Override
	public DanglingReference createDanglingReference() {
		return (DanglingReference)getDanglingReferenceEClass().newInstance();
	}
	
	@Override
	public DataItem createDataItem() {
		return (DataItem)getDataItemEClass().newInstance();
	}
	
	@Override
	public DataTable createDataTable() {
		return (DataTable)getDataTableEClass().newInstance();
	}
	
	@Override
	public DataType createDataType() {
		return (DataType)getDataTypeEClass().newInstance();
	}
	
	@Override
	public DBCharLiteral createDBCharLiteral() {
		return (DBCharLiteral)getDBCharLiteralEClass().newInstance();
	}
	
	@Override
	public DecimalLiteral createDecimalLiteral() {
		return (DecimalLiteral)getDecimalLiteralEClass().newInstance();
	}
	
	@Override
	public DeclarationExpression createDeclarationExpression() {
		return (DeclarationExpression)getDeclarationExpressionEClass().newInstance();
	}
	
	@Override
	public Delegate createDelegate() {
		return (Delegate)getDelegateEClass().newInstance();
	}
	
	@Override
	public DeleteStatement createDeleteStatement() {
		return (DeleteStatement)getDeleteStatementEClass().newInstance();
	}
	
	@Override
	public DisplayStatement createDisplayStatement() {
		return (DisplayStatement)getDisplayStatementEClass().newInstance();
	}
	
	@Override
	public DynamicAccess createDynamicAccess() {
		return (DynamicAccess)getDynamicAccessEClass().newInstance();
	}
	
	@Override
	public EClassProxy createEClassProxy() {
		return (EClassProxy)getEClassProxyEClass().newInstance();
	}
	
	@Override
	public EGLClass createEGLClass() {
		return (EGLClass)getEGLClassEClass().newInstance();
	}
	
	@Override
	public ElementAnnotations createElementAnnotations() {
		return (ElementAnnotations)getElementAnnotationsEClass().newInstance();
	}
	
	@Override
	public EmptyStatement createEmptyStatement() {
		return (EmptyStatement)getEmptyStatementEClass().newInstance();
	}
	
	@Override
	public Enumeration createEnumeration() {
		return (Enumeration)getEnumerationEClass().newInstance();
	}
	
	@Override
	public EnumerationEntry createEnumerationEntry() {
		return (EnumerationEntry)getEnumerationEntryEClass().newInstance();
	}
	
	@Override
	public ExceptionBlock createExceptionBlock() {
		return (ExceptionBlock)getExceptionBlockEClass().newInstance();
	}
	
	@Override
	public ExecuteStatement createExecuteStatement() {
		return (ExecuteStatement)getExecuteStatementEClass().newInstance();
	}
	
	@Override
	public ExitStatement createExitStatement() {
		return (ExitStatement)getExitStatementEClass().newInstance();
	}
	
	@Override
	public ExpressionStatement createExpressionStatement() {
		return (ExpressionStatement)getExpressionStatementEClass().newInstance();
	}
	
	@Override
	public ExternalType createExternalType() {
		return (ExternalType)getExternalTypeEClass().newInstance();
	}
	
	@Override
	public Field createField() {
		return (Field)getFieldEClass().newInstance();
	}
	
	@Override
	public FixedPrecisionType createFixedPrecisionType() {
		return (FixedPrecisionType)getFixedPrecisionTypeEClass().newInstance();
	}
	
	@Override
	public FloatingPointLiteral createFloatingPointLiteral() {
		return (FloatingPointLiteral)getFloatingPointLiteralEClass().newInstance();
	}
	
	@Override
	public ForEachStatement createForEachStatement() {
		return (ForEachStatement)getForEachStatementEClass().newInstance();
	}
	
	@Override
	public Form createForm() {
		return (Form)getFormEClass().newInstance();
	}
	
	@Override
	public FormField createFormField() {
		return (FormField)getFormFieldEClass().newInstance();
	}
	
	@Override
	public FormGroup createFormGroup() {
		return (FormGroup)getFormGroupEClass().newInstance();
	}
	
	@Override
	public ForStatement createForStatement() {
		return (ForStatement)getForStatementEClass().newInstance();
	}
	
	@Override
	public ForwardStatement createForwardStatement() {
		return (ForwardStatement)getForwardStatementEClass().newInstance();
	}
	
	@Override
	public FreeSqlStatement createFreeSqlStatement() {
		return (FreeSqlStatement)getFreeSqlStatementEClass().newInstance();
	}
	
	@Override
	public Function createFunction() {
		return (Function)getFunctionEClass().newInstance();
	}
	
	@Override
	public FunctionInvocation createFunctionInvocation() {
		return (FunctionInvocation)getFunctionInvocationEClass().newInstance();
	}
	
	@Override
	public DelegateInvocation createDelegateInvocation() {
		return (DelegateInvocation)getDelegateInvocationEClass().newInstance();
	}

	
	@Override
	public FunctionMember createFunctionMember() {
		return (FunctionMember)getFunctionMemberEClass().newInstance();
	}
	
	@Override
	public FunctionParameter createFunctionParameter() {
		return (FunctionParameter)getFunctionParameterEClass().newInstance();
	}
	
	@Override
	public FunctionPart createFunctionPart() {
		return (FunctionPart)getFunctionPartEClass().newInstance();
	}
	
	@Override
	public FunctionPartInvocation createFunctionPartInvocation() {
		return (FunctionPartInvocation)getFunctionPartInvocationEClass().newInstance();
	}
	
	@Override
	public FunctionReturnField createFunctionReturnField() {
		return (FunctionReturnField)getFunctionReturnFieldEClass().newInstance();
	}
	
	@Override
	public FunctionStatement createFunctionStatement() {
		return (FunctionStatement)getFunctionStatementEClass().newInstance();
	}
	
	@Override
	public GenericType createGenericType() {
		return (GenericType)getGenericTypeEClass().newInstance();
	}
	
	@Override
	public GetByKeyStatement createGetByKeyStatement() {
		return (GetByKeyStatement)getGetByKeyStatementEClass().newInstance();
	}
	
	@Override
	public GetByPositionStatement createGetByPositionStatement() {
		return (GetByPositionStatement)getGetByPositionStatementEClass().newInstance();
	}
	
	@Override
	public GoToStatement createGoToStatement() {
		return (GoToStatement)getGoToStatementEClass().newInstance();
	}
	
	@Override
	public Handler createHandler() {
		return (Handler)getHandlerEClass().newInstance();
	}
	
	@Override
	public HexLiteral createHexLiteral() {
		return (HexLiteral)getHexLiteralEClass().newInstance();
	}
	
	@Override
	public IfStatement createIfStatement() {
		return (IfStatement)getIfStatementEClass().newInstance();
	}
		
	@Override
	public IntegerLiteral createIntegerLiteral() {
		return (IntegerLiteral)getIntegerLiteralEClass().newInstance();
	}
	
	@Override
	public Interface createInterface() {
		return (Interface)getInterfaceEClass().newInstance();
	}
	
	@Override
	public IntervalType createIntervalType() {
		return (IntervalType)getIntervalTypeEClass().newInstance();
	}
	
	@Override
	public InvalidName createInvalidName() {
		return (InvalidName)getInvalidNameEClass().newInstance();
	}
	
	@Override
	public IsAExpression createIsAExpression() {
		return (IsAExpression)getIsAExpressionEClass().newInstance();
	}
	
	@Override
	public IsNotExpression createIsNotExpression() {
		return (IsNotExpression)getIsNotExpressionEClass().newInstance();
	}
	
	@Override
	public LabelStatement createLabelStatement() {
		return (LabelStatement)getLabelStatementEClass().newInstance();
	}
	
	@Override
	public Library createLibrary() {
		return (Library)getLibraryEClass().newInstance();
	}
	
	@Override
	public LocalVariableDeclarationStatement createLocalVariableDeclarationStatement() {
		return (LocalVariableDeclarationStatement)getLocalVariableDeclarationStatementEClass().newInstance();
	}
	
	@Override
	public MBCharLiteral createMBCharLiteral() {
		return (MBCharLiteral)getMBCharLiteralEClass().newInstance();
	}
	
	@Override
	public MemberAccess createMemberAccess() {
		return (MemberAccess)getMemberAccessEClass().newInstance();
	}
	
	@Override
	public MemberName createMemberName() {
		return (MemberName)getMemberNameEClass().newInstance();
	}
	
	@Override
	public MoveStatement createMoveStatement() {
		return (MoveStatement)getMoveStatementEClass().newInstance();
	}
	
	@Override
	public NewExpression createNewExpression() {
		return (NewExpression)getNewExpressionEClass().newInstance();
	}
	
	@Override
	public NullLiteral createNullLiteral() {
		return (NullLiteral)getNullLiteralEClass().newInstance();
	}
	
	@Override
	public ObjectExpression createObjectExpression() {
		return (ObjectExpression)getObjectExpressionEClass().newInstance();
	}
	
	@Override
	public ObjectExpressionEntry createObjectExpressionEntry() {
		return (ObjectExpressionEntry)getObjectExpressionEntryEClass().newInstance();
	}

	
	@Override
	public OpenStatement createOpenStatement() {
		return (OpenStatement)getOpenStatementEClass().newInstance();
	}
	
	@Override
	public OpenUIStatement createOpenUIStatement() {
		return (OpenUIStatement)getOpenUIStatementEClass().newInstance();
	}
	
	@Override
	public Operation createOperation() {
		return (Operation)getOperationEClass().newInstance();
	}
	
	@Override
	public Parameter createParameter() {
		return (Parameter)getParameterEClass().newInstance();
	}
	
	@Override
	public ParameterizableType createParameterizableType() {
		return (ParameterizableType)getParameterizableTypeEClass().newInstance();
	}
	
	@Override
	public PartName createPartName() {
		return (PartName)getPartNameEClass().newInstance();
	}
	
	@Override
	public TypeName createTypeName() {
		return (TypeName)getTypeNameEClass().newInstance();
	}

	@Override
	public PrepareStatement createPrepareStatement() {
		return (PrepareStatement)getPrepareStatementEClass().newInstance();
	}
	
	@Override
	public PrimitiveTypeLiteral createPrimitiveTypeLiteral() {
		return (PrimitiveTypeLiteral)getPrimitiveTypeLiteralEClass().newInstance();
	}
	
	@Override
	public PrintStatement createPrintStatement() {
		return (PrintStatement)getPrintStatementEClass().newInstance();
	}
	
	@Override
	public Program createProgram() {
		return (Program)getProgramEClass().newInstance();
	}
	
	@Override
	public ProgramParameter createProgramParameter() {
		return (ProgramParameter)getProgramParameterEClass().newInstance();
	}
	
	@Override
	public QualifiedFunctionInvocation createQualifiedFunctionInvocation() {
		return (QualifiedFunctionInvocation)getQualifiedFunctionInvocationEClass().newInstance();
	}
	
	@Override
	public Record createRecord() {
		return (Record)getRecordEClass().newInstance();
	}
	
	@Override
	public ReplaceStatement createReplaceStatement() {
		return (ReplaceStatement)getReplaceStatementEClass().newInstance();
	}
	
	@Override
	public ReturnStatement createReturnStatement() {
		return (ReturnStatement)getReturnStatementEClass().newInstance();
	}
	
	@Override
	public SequenceType createSequenceType() {
		return (SequenceType)getSequenceTypeEClass().newInstance();
	}
	
	@Override
	public Service createService() {
		return (Service)getServiceEClass().newInstance();
	}
	
	@Override
	public SetStatement createSetStatement() {
		return (SetStatement)getSetStatementEClass().newInstance();
	}
	
	@Override
	public SetValuesExpression createSetValuesExpression() {
		return (SetValuesExpression)getSetValuesExpressionEClass().newInstance();
	}

	@Override
	public SetValuesStatement createSetValuesStatement() {
		return (SetValuesStatement)getSetValuesStatementEClass().newInstance();
	}

	@Override
	public ShowStatement createShowStatement() {
		return (ShowStatement)getShowStatementEClass().newInstance();
	}
	
	@Override
	public SizeInBytesExpression createSizeInBytesExpression() {
		return (SizeInBytesExpression)getSizeInBytesExpressionEClass().newInstance();
	}
	
	@Override
	public SizeOfExpression createSizeOfExpression() {
		return (SizeOfExpression)getSizeOfExpressionEClass().newInstance();
	}
	
	@Override
	public StatementBlock createStatementBlock() {
		return (StatementBlock)getStatementBlockEClass().newInstance();
	}
	
	@Override
	public Stereotype createStereotype() {
		return (Stereotype)getStereotypeEClass().newInstance();
	}
	
	@Override
	public StereotypeType createStereotypeType() {
		return (StereotypeType)getStereotypeTypeEClass().newInstance();
	}
	
	@Override
	public StringLiteral createStringLiteral() {
		return (StringLiteral)getStringLiteralEClass().newInstance();
	}
	
	@Override
	public StructPart createStructPart() {
		return (StructPart)getStructPartEClass().newInstance();
	}
	
	@Override
	public StructuredField createStructuredField() {
		return (StructuredField)getStructuredFieldEClass().newInstance();
	}
	
	@Override
	public StructuredRecord createStructuredRecord() {
		return (StructuredRecord)getStructuredRecordEClass().newInstance();
	}
	
	@Override
	public SubstringAccess createSubstringAccess() {
		return (SubstringAccess)getSubstringAccessEClass().newInstance();
	}
	
	@Override
	public SystemFunctionArgumentMnemonicName createSystemFunctionArgumentMnemonicName() {
		return (SystemFunctionArgumentMnemonicName)getSystemFunctionArgumentMnemonicNameEClass().newInstance();
	}
	
	@Override
	public TernaryExpression createTernaryExpression() {
		return (TernaryExpression)getTernaryExpressionEClass().newInstance();
	}

	@Override
	public TextTypeLiteral createTextTypeLiteral() {
		return (TextTypeLiteral)getTextTypeLiteralEClass().newInstance();
	}
	
	@Override
	public ThisExpression createThisExpression() {
		return (ThisExpression)getThisExpressionEClass().newInstance();
	}
	
	@Override
	public ThrowStatement createThrowStatement() {
		return (ThrowStatement)getThrowStatementEClass().newInstance();
	}
	
	@Override
	public TimestampType createTimestampType() {
		return (TimestampType)getTimestampTypeEClass().newInstance();
	}
	
	@Override
	public TopLevelFunctionName createTopLevelFunctionName() {
		return (TopLevelFunctionName)getTopLevelFunctionNameEClass().newInstance();
	}
	
	@Override
	public TransferStatement createTransferStatement() {
		return (TransferStatement)getTransferStatementEClass().newInstance();
	}
	
	@Override
	public TryStatement createTryStatement() {
		return (TryStatement)getTryStatementEClass().newInstance();
	}
	
	@Override
	public TypeParameter createTypeParameter() {
		return (TypeParameter)getTypeParameterEClass().newInstance();
	}
	
	@Override
	public UnaryExpression createUnaryExpression() {
		return (UnaryExpression)getUnaryExpressionEClass().newInstance();
	}
	
	@Override
	public VariableFormField createVariableFormField() {
		return (VariableFormField)getVariableFormFieldEClass().newInstance();
	}
	
	@Override
	public WhenClause createWhenClause() {
		return (WhenClause)getWhenClauseEClass().newInstance();
	}
	
	@Override
	public WhileStatement createWhileStatement() {
		return (WhileStatement)getWhileStatementEClass().newInstance();
	}
	
}
