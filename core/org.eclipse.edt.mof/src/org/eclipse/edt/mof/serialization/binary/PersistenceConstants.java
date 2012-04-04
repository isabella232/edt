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
package org.eclipse.edt.mof.serialization.binary;

public interface PersistenceConstants {
	
	public static final long Magic_Number = 0xCAFEDEADL;
	

	public static final int MajorVersion = 9;
	public static final int MinorVersion = 10000;
	
	//Parts
	public static final int Program = 1;
	public static final int Record = 2;
	public static final int StructuredRecord = 3;
	public static final int ClassRecord = 4;
	public static final int Interface = 6;
	public static final int Handler = 7;
	public static final int DataItem = 9;
	public static final int FunctionPart = 10;
	public static final int Library = 11;
	public static final int Service = 12;
	public static final int FormGroup = 14;
	public static final int DataTable = 15;
	public static final int Delegate = 16;
	public static final int ExternalType = 17;
	public static final int Enumeration = 18;

	public static final int Function = 25;
	public static final int Constructor = 26;
	public static final int Form = 27;

	
	
	//Fields
	public static final int Field = 100;
	public static final int ConstantField = 101;
	public static final int FunctionReturnField = 102;
	public static final int FunctionParameterField = 103;
	public static final int ProgramParameterField = 104;
	public static final int StructuredField = 105;
	public static final int AmbiguousFillerItemStructuredField = 106;
	public static final int EnumerationEntry = 107;
	public static final int ConstantFormField = 108;
	public static final int VariableFormField = 109;

	
	//Statements
	public static final int StatementBlock = 200;
	public static final int AssignmentStatement = 201;	
    public static final int IfStatementStatement = 202;
    public static final int WhileStatementStatement = 203;
    public static final int FunctionStatement = 204;
    public static final int ReturnStatement = 205;
    public static final int SetValuesStatement = 206;
    public static final int LocalVariableDeclarationStatement = 207;
    public static final int ExitStatement = 208;
    public static final int EmptyStatement = 209;
    public static final int ForStatement= 210;
    public static final int ForEachStatement = 211;
    public static final int CallStatement = 212;
    public static final int ContinueStatement = 213;
    public static final int ForwardStatement = 214;
    public static final int MoveStatement = 215;
    public static final int SetStatement = 216;
    public static final int CaseStatement = 217;    
    public static final int OpenUIStatement = 218; 
    public static final int TransferStatement = 219; 
    public static final int LabelStatement = 220; 
    public static final int GotoStatement = 221; 
    public static final int TryStatement = 222; 
    public static final int AddStatement = 223; 
    public static final int ReplaceStatement = 224; 
    public static final int DeleteStatement = 226; 
    public static final int OpenStatement = 227; 
    public static final int GetByPositionStatement = 228; 
    public static final int GetByKeyStatement = 229; 
    public static final int ExecuteStatement = 230; 
    public static final int PrepareStatement = 231; 
    public static final int ThrowStatement = 232; 
    public static final int CloseStatement = 233; 
    public static final int FreeSqlStatement = 234; 
    public static final int ConvertStatement = 235;
    public static final int DeepCopyStatement = 236;
    public static final int ConverseStatement = 237;
    public static final int DisplayStatement = 238;
    public static final int PrintStatement = 239;
    
    public static final int DLIAddStatement = 240;
    public static final int DLIDeleteStatement = 241;
    public static final int DLIGetByKeyStatement = 242;
    public static final int DLIGetByPositionStatement = 243;
    public static final int DLIReplaceStatement = 244;

    public static final int ShowStatement = 245;

	
	//Expressions
	public static final int BinaryExpression = 300;
	public static final int Assignment = 301;
	public static final int Name = 302;
	public static final int InvalidName = 303;
	public static final int NameType = 304;
	public static final int SystemFunctionArgumentMnemonicName = 305;
	public static final int TopLevelFuncionName = 306;
	public static final int ArrayAccess = 307;
	public static final int ArrayElementFieldAccess = 308;
	public static final int AsExpression = 309;
	public static final int ConvertExpression = 310;
	public static final int DeclarationExpression = 311;
	public static final int DynamicAccess = 312;
	public static final int FieldAccess = 313;
	public static final int FunctionInvocation = 314;
	public static final int IsAExpression = 315;
	public static final int NewExpression = 316;
	public static final int SetValuesExpression = 317;
	public static final int SizeInBytesExpression = 318;
	public static final int SizeOfExpression = 319;
	public static final int SubstringAccess = 320;
	public static final int UnaryExpression = 321;
	public static final int EzeFunctionInvocation = 322;
	public static final int FillerFieldAccess = 323;
	public static final int InExpression = 324;
	public static final int EmbeddedPartNameType = 325;
	
	//Literals
	public static final int ArrayLiteral = 350;
	public static final int BooleanLiteral = 351;
	public static final int HexLiteral = 352;
	public static final int DecimalLiteral = 353;
	public static final int FloatingPointLiteral = 354;
	public static final int IntegerLiteral = 355;
	public static final int CharLiteral = 356;
	public static final int DBCharLiteral = 357;
	public static final int MBCharLiteral = 358;
	public static final int StringLiteral = 359;
	public static final int NullLiteral = 360;
	
	//Types
	public static final int BaseType = 601;
	public static final int ArrayType = 602;
	public static final int ArrayDictionary = 603;
	public static final int Dictionary = 604;
	public static final int ForeignLanguageType = 605;
	public static final int NilType = 606;
	public static final int ReflectType = 607;
	public static final int SystemFunctionParameterMnemonicType = 608;
	public static final int SystemFunctionParameterSpecialType = 609;
	
	//Objects
	public static final int EObjectReference = 648;
	public static final int EObject = 649;
	public static final int String = 650;
	public static final int BigInteger = 651;
	public static final int Long = 652;
	public static final int Integer = 653;
	public static final int Float = 654;
	public static final int Double = 655;
	public static final int BigDecimal = 656;
	public static final int SystemEnumerationDataBinding = 657;
	public static final int SystemEnumerationDataBindingArray = 658;
	public static final int BooleanTrue = 659;
	public static final int BooleanFalse = 660;
	public static final int AnnotationArray = 661;
	public static final int IntegerArray = 662;
	public static final int IntegerArrayArray = 663;
	public static final int StringArray = 664;
	public static final int StringArrayArray = 665;
	public static final int ObjectArray = 666;
	public static final int FloatValidValuesElement = 667;
	public static final int IntegerValidValuesElement = 668;
	public static final int RangeValidValuesElement = 669;
	public static final int StringValidValuesElement = 670;
	public static final int List = 671;
	public static final int SlotArray = 672;
	public static final int Slot = 673;
	public static final int NullableSlot = 674;
	
	
	//Misc
	public static final int PartReference = 700;
	public static final int PartInfo = 701;
	public static final int Annotation = 702;
	public static final int EventBlock = 703;
	public static final int OnExceptionBlock = 704;
	public static final int SqlClause = 705;
	public static final int SqlInputHostVariableToken = 706;
	public static final int SqlOutputHostVariableToken = 707;
	public static final int SqlSelectNameToken = 708;
	public static final int SqlStringToken = 709;
	public static final int SqlTableNameHostVariableToken = 710;
	public static final int SqlWhereCurrentOfToken = 711;
	public static final int UsageInformation = 712;
	public static final int WhenClause = 713;
	public static final int StatementIdentifier = 714;
	public static final int ExpressionIdentifier = 715;
	public static final int DataItemCopiedAnnotation = 716;
	public static final int ElementAnnotation = 717;
	
	public static final int DLICall = 720;
	public static final int DLIStatement = 721;
	public static final int SEGMENTSEARCHARGUMENT = 722;
	public static final int DLIVALUECONDITION = 723;
	public static final int DLIBOOLEANOPERATOREXPRESSION = 724;
	public static final int DLICONDITIONALEXPRESSION = 725;
	
	
	public static final int Null = 998;
	public static final int PoolEntry = 999;


	public static final int TypeReference = 997;




	


	
}
