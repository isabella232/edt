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
package org.eclipse.edt.compiler.internal.core.validation.part;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.AddStatement;
import org.eclipse.edt.compiler.core.ast.ArrayType;
import org.eclipse.edt.compiler.core.ast.AssignmentStatement;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.CaseStatement;
import org.eclipse.edt.compiler.core.ast.CloseStatement;
import org.eclipse.edt.compiler.core.ast.Constructor;
import org.eclipse.edt.compiler.core.ast.ContinueStatement;
import org.eclipse.edt.compiler.core.ast.ConverseStatement;
import org.eclipse.edt.compiler.core.ast.DeleteStatement;
import org.eclipse.edt.compiler.core.ast.DisplayStatement;
import org.eclipse.edt.compiler.core.ast.EmptyStatement;
import org.eclipse.edt.compiler.core.ast.ExecuteStatement;
import org.eclipse.edt.compiler.core.ast.ExitStatement;
import org.eclipse.edt.compiler.core.ast.ForEachStatement;
import org.eclipse.edt.compiler.core.ast.ForStatement;
import org.eclipse.edt.compiler.core.ast.ForwardStatement;
import org.eclipse.edt.compiler.core.ast.FreeSQLStatement;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.FunctionInvocationStatement;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.GetByKeyStatement;
import org.eclipse.edt.compiler.core.ast.GetByPositionStatement;
import org.eclipse.edt.compiler.core.ast.GotoStatement;
import org.eclipse.edt.compiler.core.ast.IfStatement;
import org.eclipse.edt.compiler.core.ast.LabelStatement;
import org.eclipse.edt.compiler.core.ast.MoveStatement;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.OpenStatement;
import org.eclipse.edt.compiler.core.ast.OpenUIStatement;
import org.eclipse.edt.compiler.core.ast.PrepareStatement;
import org.eclipse.edt.compiler.core.ast.PrintStatement;
import org.eclipse.edt.compiler.core.ast.ReplaceStatement;
import org.eclipse.edt.compiler.core.ast.ReturnStatement;
import org.eclipse.edt.compiler.core.ast.SetStatement;
import org.eclipse.edt.compiler.core.ast.SetValuesStatement;
import org.eclipse.edt.compiler.core.ast.ShowStatement;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.core.ast.SuperExpression;
import org.eclipse.edt.compiler.core.ast.ThisExpression;
import org.eclipse.edt.compiler.core.ast.ThrowStatement;
import org.eclipse.edt.compiler.core.ast.TopLevelFunction;
import org.eclipse.edt.compiler.core.ast.TransferStatement;
import org.eclipse.edt.compiler.core.ast.TryStatement;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.core.ast.WhileStatement;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.CaseStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.ContinueStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.ConverseStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.DisplayStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.ExitStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.ForStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.ForwardStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.FreeSQLStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.FunctionDataDeclarationValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.GotoStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.IfStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.LabelStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.MoveStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.OpenUIStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.PrintStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.ReturnStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.SetStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.ShowStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.ThrowStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.TransferStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.TryStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.WhileStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.type.TypeValidator;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.Interface;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.edt.mof.egl.StructuredRecord;
import org.eclipse.edt.mof.egl.utils.InternUtil;


//TODO need to check the type is valid for param & returns. used to be in FunctionBindingCompletor.
public class FunctionValidator extends AbstractASTVisitor {
	
	IProblemRequestor problemRequestor;
	private IPartBinding enclosingPart;
	private String functionName;
	private HashMap labelMap = new HashMap();
	private ArrayList gotoList = new ArrayList();
    ICompilerOptions compilerOptions;
    private boolean nextStatementIsUnreachable;
	private IStatementValidInContainerInfo statementValidInContainerInfo;
	private LabelStatement labelPrecedingStatement;
	private Map labeledLoops = new HashMap();
	
	public FunctionValidator(IProblemRequestor problemRequestor, IPartBinding enclosingPart, ICompilerOptions compilerOptions) {
		this.problemRequestor = problemRequestor;
		this.enclosingPart = enclosingPart;
		
		if(enclosingPart != null) {
			this.statementValidInContainerInfo = StatementValidInContainerInfoFactory.INSTANCE.create(enclosingPart);
		}
		this.compilerOptions = compilerOptions;
	}
	
	public FunctionValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		this(problemRequestor, null, compilerOptions);
	}
	
	public boolean visit(TopLevelFunction topLevelFunction) {
		functionName = topLevelFunction.getName().getCanonicalName();
		if(topLevelFunction.getIdentifier() == InternUtil.intern(IEGLConstants.MNEMONIC_MAIN)) {
			problemRequestor.acceptProblem(
				topLevelFunction.getName(),
				IProblemRequestor.FUNCTION_NO_MAIN_FUNCTION_ALLOWED,
				new String[] {IEGLConstants.MNEMONIC_MAIN});
		}
		
		checkFunctionName(topLevelFunction.getName(), false);
		checkNumberOfParms(topLevelFunction.getFunctionParameters(), topLevelFunction.getName(), functionName);
		checkForConstructorCalls(topLevelFunction);
		
		return true;
	}
	
	public boolean visit(NestedFunction nestedFunction) {
		functionName = nestedFunction.getName().getCanonicalName();
		
		checkFunctionName(nestedFunction.getName(), true);
		checkNumberOfParms(nestedFunction.getFunctionParameters(), nestedFunction.getName(), functionName);
		checkForConstructorCalls(nestedFunction);
//		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(nestedFunction); TODO
		
		return true;
	}
	
	public boolean visit(Constructor constructor) {
		functionName = IEGLConstants.KEYWORD_CONSTRUCTOR;
		
		checkNumberOfParms(constructor.getParameters(), constructor, functionName);
		checkForConstructorCalls(constructor);
//		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(constructor); TODO
		
		return true;
	}
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.ReturnsDeclaration returnsDeclaration) {
		Type type = returnsDeclaration.getType();
		if (type != null) {
			TypeValidator.validate(type, problemRequestor, compilerOptions, enclosingPart.getEnvironment().getCompiler());
		}
		return true;
	};
	
	private void checkForConstructorCalls(Node node) {
		node.accept(new AbstractASTVisitor() {
			public boolean visit(org.eclipse.edt.compiler.core.ast.FunctionInvocation functionInvocation) {
				
				//ensure that constructor invocations are the first statement in a constructor
				if (functionInvocation.getTarget() instanceof SuperExpression || functionInvocation.getTarget() instanceof ThisExpression) {
					if (functionInvocation.getParent() instanceof FunctionInvocationStatement) {
						if (functionInvocation.getParent().getParent() instanceof Constructor) {
							Constructor constructor = (Constructor)functionInvocation.getParent().getParent();
							if (constructor.getStmts().get(0) == functionInvocation.getParent()) {
								return true;  // Success! we can exit without an error
							}
						}
					}
					//If we got here, the constructor invocation is in the wrong place. Throw a validation error
					problemRequestor.acceptProblem(
							functionInvocation.getTarget(),
							IProblemRequestor.CONSTRUCTOR_CALL_WRONG_PLACE,
							new String[]{});
				}				
				return true;
			}
		});
	}
	
	
	void checkNumberOfParms(List parms, Node name, String nameString) {
		if (parms.size() > 255) {
        	problemRequestor.acceptProblem(
            		name,
    				IProblemRequestor.FUNCTION_TOO_MANY_PARMS,
    				new String[] {nameString, Integer.toString(parms.size())});        	
		}
	}
	
	public boolean visit(FunctionParameter functionParameter) {
	
        Type parmType = functionParameter.getType();
        
        Member member = functionParameter.getName().resolveMember();
        if(member instanceof org.eclipse.edt.mof.egl.FunctionParameter) {
        	TypeValidator.validate(parmType, problemRequestor, compilerOptions, enclosingPart.getEnvironment().getCompiler());
        	
        	org.eclipse.edt.mof.egl.FunctionParameter memberParam = (org.eclipse.edt.mof.egl.FunctionParameter)member;
        	
	        checkParmTypeNotStaticArray(functionParameter, parmType);
	        
	        org.eclipse.edt.mof.egl.Type type = member.getType();
	        if(type != null) {
	        	checkParmNotEmptyRecord(functionParameter, type);
	        	checkTypeNotServiceOrInterfaceArray(functionParameter, type);
	        	
	        	switch (memberParam.getParameterKind()) {
	        		case PARM_IN:
	        			checkInputParm(functionParameter);
	        			break;
	        		case PARM_OUT:
	        			checkOutputParm(functionParameter);
	        			break;
	        		case PARM_INOUT:
	        			checkInputOutputParm(functionParameter);
	        			break;
	        	}
	        	
	        	//TODO not sure if this is still relevant
//	        	StatementValidator.validateDeclarationForStereotypeContext(fParameterBinding, problemRequestor, functionParameter.getType().getBaseType());
	        }
	        
	    	EGLNameValidator.validate(functionParameter.getName(), EGLNameValidator.PART, problemRequestor, compilerOptions);
        }
        return true;
	}
	
	private void checkFunctionName(Name name, boolean nested) {
		if (nested) {
			EGLNameValidator.validate(name, EGLNameValidator.INTERNALFIELD, problemRequestor, compilerOptions);
		}
		else {
			EGLNameValidator.validate(name, EGLNameValidator.FUNCTION, problemRequestor, compilerOptions);
		}
	}
	
	private void checkParmTypeNotStaticArray(FunctionParameter functionParameter, Type parmType) {

		if (functionParameter.isNullable()) {
			checkParmTypeNotStaticArray(functionParameter, parmType.getBaseType());
		}
		
		if(parmType.isArrayType()) {
			if (((ArrayType) parmType).hasInitialSize()) {
        	problemRequestor.acceptProblem(
        		parmType,
				IProblemRequestor.STATIC_ARRAY_PARAMETER_DEFINITION,
				new String[] {functionParameter.getName().getCanonicalName(), functionName});   
			}
			else {
				checkParmTypeNotStaticArray(functionParameter, ((ArrayType) parmType).getElementType());
			}
        }
			
	}
	
	private void checkParmNotEmptyRecord(FunctionParameter functionParameter, org.eclipse.edt.mof.egl.Type parmType) {
		//TODO remove when record fields are being bound. until then they're always "empty"
		if (true) return;
		
		if (parmType instanceof Part) {
			boolean isEmptyRecord = false;
			
			int partType = BindingUtil.getPartTypeConstant((Part)parmType);
			if (partType == ITypeBinding.FLEXIBLE_RECORD_BINDING) {
				isEmptyRecord = ((Record)parmType).getFields().isEmpty();
			}
			else if (partType == ITypeBinding.FIXED_RECORD_BINDING) {
				isEmptyRecord = ((StructuredRecord)parmType).getStructuredFields().isEmpty();
			}
			
			if(isEmptyRecord) {
	    		problemRequestor.acceptProblem(
	    			functionParameter.getType(),
					IProblemRequestor.RECORD_PARAMETER_WITH_NO_CONTENTS,
					new String[] {functionParameter.getName().getCanonicalName(), functionParameter.getType().getCanonicalName()});
	    	}
		}
	}
	
	private void checkTypeNotServiceOrInterfaceArray(FunctionParameter functionParameter, org.eclipse.edt.mof.egl.Type paramType) {
		if (paramType instanceof org.eclipse.edt.mof.egl.ArrayType) {
			org.eclipse.edt.mof.egl.ArrayType type = (org.eclipse.edt.mof.egl.ArrayType)paramType;
			org.eclipse.edt.mof.egl.Type elementType = type.getElementType();
			if (elementType instanceof Service || elementType instanceof Interface) {
				problemRequestor.acceptProblem(
						functionParameter.getType(),
						IProblemRequestor.SERVICE_OR_INTERFACE_ARRAYS_NOT_SUPPORTED);
			}
		}
	}
	
	private void checkInputParm(FunctionParameter functionParameter) {
		if (functionParameter.isParmConst()) {
			validatePrimitiveConst(functionParameter.getType());
		}
	}
	
	private void checkOutputParm(FunctionParameter functionParameter) {
		if (functionParameter.isParmConst()) {
			problemRequestor.acceptProblem(
            		functionParameter,
					IProblemRequestor.CONST_MODIFIER_NOT_ALLOWED_WITH_IN_MODIFIER,
					new String[] {IEGLConstants.KEYWORD_OUT.toUpperCase(), IEGLConstants.KEYWORD_INTERVAL.toUpperCase()});
		}
	}
	
	private void checkInputOutputParm(FunctionParameter functionParameter) {
		if (functionParameter.isParmConst()) {
			validatePrimitiveConst(functionParameter.getType());
		}
	}
	
	private void preVisitStatement(Statement statement) {
		preVisitStatement(statement, true);
	}
	
	private void preVisitStatement(Statement statement, boolean issueWarningIfUnreachable) {
		if(issueWarningIfUnreachable && nextStatementIsUnreachable) {
			problemRequestor.acceptProblem(
				statement,
				IProblemRequestor.UNREACHABLE_CODE,
				IMarker.SEVERITY_WARNING);
			nextStatementIsUnreachable = false;
		}
		
		labelPrecedingStatement = null;
	}
	
	private void postVisitStatement(Statement statement) {
		if(statement.canIncludeOtherStatements()) {
			for(Iterator iter = statement.getStatementBlocks().iterator(); iter.hasNext();) {
				for(Iterator stmtIter = ((List) iter.next()).iterator(); stmtIter.hasNext();) {
					((Node) stmtIter.next()).accept(this);
				}
				nextStatementIsUnreachable = false;
			}
		}
	}
	
	private boolean checkStatementAllowedInContainer(Statement statement) {
		if(statementValidInContainerInfo != null && !statementValidInContainerInfo.statementIsValidInContainer(statement)) {
			problemRequestor.acceptProblem(statement, statementValidInContainerInfo.getProblemKind(), new String[] {StatementValidator.getName(statement)});
			return false;
		}
		return true;
	}
	
	public boolean visit(AddStatement addStatement) {
		preVisitStatement(addStatement);
		if (checkStatementAllowedInContainer(addStatement)) {
			if (enclosingPart != null && enclosingPart.getEnvironment() != null && enclosingPart.getEnvironment().getCompiler() != null) {
				org.eclipse.edt.compiler.StatementValidator val = enclosingPart.getEnvironment().getCompiler().getValidatorFor(addStatement);
				if (val != null) {
					val.validateStatement(addStatement, problemRequestor, compilerOptions);
				}
			}
		}
		postVisitStatement(addStatement);
		return false;
	}

	public boolean visit(AssignmentStatement assignmentStatement) {
		preVisitStatement(assignmentStatement);
		if (checkStatementAllowedInContainer(assignmentStatement)) {
			//TODO AssignmentStatementValidator blows up right now
//			assignmentStatement.accept(new AssignmentStatementValidator(problemRequestor, compilerOptions, enclosingPart));
		}
		postVisitStatement(assignmentStatement);
		return false;
	}

	public boolean visit(CallStatement callStatement) {
		preVisitStatement(callStatement);
		if (checkStatementAllowedInContainer(callStatement)) {
			if (enclosingPart != null && enclosingPart.getEnvironment() != null && enclosingPart.getEnvironment().getCompiler() != null) {
				org.eclipse.edt.compiler.StatementValidator val = enclosingPart.getEnvironment().getCompiler().getValidatorFor(callStatement);
				if (val != null) {
					val.validateStatement(callStatement, problemRequestor, compilerOptions);
				}
			}
		}
		postVisitStatement(callStatement);
		return false;
	}

	public boolean visit(CaseStatement caseStatement) {
		if(labelPrecedingStatement != null) {
			labeledLoops.put(caseStatement, labelPrecedingStatement);
		}
		preVisitStatement(caseStatement);
		if (checkStatementAllowedInContainer(caseStatement)) {
			caseStatement.accept(new CaseStatementValidator(problemRequestor, compilerOptions));
		}		
		postVisitStatement(caseStatement);
		return false;
	}

	public boolean visit(CloseStatement closeStatement) {
		preVisitStatement(closeStatement);
		if (checkStatementAllowedInContainer(closeStatement)) {
			if (enclosingPart != null && enclosingPart.getEnvironment() != null && enclosingPart.getEnvironment().getCompiler() != null) {
				org.eclipse.edt.compiler.StatementValidator val = enclosingPart.getEnvironment().getCompiler().getValidatorFor(closeStatement);
				if (val != null) {
					val.validateStatement(closeStatement, problemRequestor, compilerOptions);
				}
			}
		}
		postVisitStatement(closeStatement);
		return false;
	}

	public boolean visit(ContinueStatement continueStatement) {
		preVisitStatement(continueStatement);
		if (checkStatementAllowedInContainer(continueStatement)) {
			continueStatement.accept(new ContinueStatementValidator(problemRequestor, labeledLoops));
			nextStatementIsUnreachable = true;
		}
		postVisitStatement(continueStatement);
		return false;
	}

	public boolean visit(ConverseStatement converseStatement) {
		preVisitStatement(converseStatement);
		if (checkStatementAllowedInContainer(converseStatement)) {
			converseStatement.accept(new ConverseStatementValidator(problemRequestor, enclosingPart));
		}
		postVisitStatement(converseStatement);
		return false;
	}

	public boolean visit(DeleteStatement deleteStatement) {
		preVisitStatement(deleteStatement);
		if (checkStatementAllowedInContainer(deleteStatement)) {
			if (enclosingPart != null && enclosingPart.getEnvironment() != null && enclosingPart.getEnvironment().getCompiler() != null) {
				org.eclipse.edt.compiler.StatementValidator val = enclosingPart.getEnvironment().getCompiler().getValidatorFor(deleteStatement);
				if (val != null) {
					val.validateStatement(deleteStatement, problemRequestor, compilerOptions);
				}
			}
		}
		postVisitStatement(deleteStatement);
		return false;
	}

	public boolean visit(DisplayStatement displayStatement) {
		preVisitStatement(displayStatement);
		if (checkStatementAllowedInContainer(displayStatement)) {
			displayStatement.accept(new DisplayStatementValidator(problemRequestor, enclosingPart, compilerOptions));
		}
		postVisitStatement(displayStatement);
		return false;
	}

	public boolean visit(ExecuteStatement executeStatement) {
		preVisitStatement(executeStatement);
		if (checkStatementAllowedInContainer(executeStatement)) {
			if (enclosingPart != null && enclosingPart.getEnvironment() != null && enclosingPart.getEnvironment().getCompiler() != null) {
				org.eclipse.edt.compiler.StatementValidator val = enclosingPart.getEnvironment().getCompiler().getValidatorFor(executeStatement);
				if (val != null) {
					val.validateStatement(executeStatement, problemRequestor, compilerOptions);
				}
			}
		}
		postVisitStatement(executeStatement);
		return false;
	}

	public boolean visit(EmptyStatement emptyStatement) {
		preVisitStatement(emptyStatement, false);
		if (checkStatementAllowedInContainer(emptyStatement)) {
		}
		postVisitStatement(emptyStatement);
		return false;
	}

	public boolean visit(ExitStatement exitStatement) {
		preVisitStatement(exitStatement);
		if (checkStatementAllowedInContainer(exitStatement)) {
			exitStatement.accept(new ExitStatementValidator(problemRequestor, labeledLoops, enclosingPart, compilerOptions));
			nextStatementIsUnreachable = true;
		}
		postVisitStatement(exitStatement);
		return false;
	}

	public boolean visit(ForEachStatement forEachStatement) {
		if(labelPrecedingStatement != null) {
			labeledLoops.put(forEachStatement, labelPrecedingStatement);
		}
		preVisitStatement(forEachStatement);
		if (checkStatementAllowedInContainer(forEachStatement)) {
			if (enclosingPart != null && enclosingPart.getEnvironment() != null && enclosingPart.getEnvironment().getCompiler() != null) {
				org.eclipse.edt.compiler.StatementValidator val = enclosingPart.getEnvironment().getCompiler().getValidatorFor(forEachStatement);
				if (val != null) {
					val.validateStatement(forEachStatement, problemRequestor, compilerOptions);
				}
			}
		}		
		postVisitStatement(forEachStatement);
		return false;
	}

	public boolean visit(ForStatement forStatement) {
		if(labelPrecedingStatement != null) {
			labeledLoops.put(forStatement, labelPrecedingStatement);
		}
		preVisitStatement(forStatement);
		if (checkStatementAllowedInContainer(forStatement)) {
			forStatement.accept(new ForStatementValidator(problemRequestor, compilerOptions));
		}		
		postVisitStatement(forStatement);
		return false;
	}

	public boolean visit(ForwardStatement forwardStatement) {
		preVisitStatement(forwardStatement);
		if (checkStatementAllowedInContainer(forwardStatement)) {
			forwardStatement.accept(new ForwardStatementValidator(problemRequestor, compilerOptions));
		}
		postVisitStatement(forwardStatement);
		return false;
	}

	public boolean visit(FreeSQLStatement freeSQLStatement) {
		preVisitStatement(freeSQLStatement);
		if (checkStatementAllowedInContainer(freeSQLStatement)) {
			freeSQLStatement.accept(new FreeSQLStatementValidator(problemRequestor, compilerOptions));
		}
		postVisitStatement(freeSQLStatement);
		return false;
	}

	public boolean visit(FunctionInvocationStatement functionInvocationStatement) {
		preVisitStatement(functionInvocationStatement);
		if (checkStatementAllowedInContainer(functionInvocationStatement)) {
		}
		postVisitStatement(functionInvocationStatement);
		return false;
	}

	public boolean visit(FunctionDataDeclaration functionDataDeclaration) {
		preVisitStatement(functionDataDeclaration);
		if (checkStatementAllowedInContainer(functionDataDeclaration)) {
			functionDataDeclaration.accept(new FunctionDataDeclarationValidator(problemRequestor, compilerOptions, enclosingPart));
		}
		postVisitStatement(functionDataDeclaration);
		return false;
	}

	public boolean visit(GetByKeyStatement getByKeyStatement) {
		preVisitStatement(getByKeyStatement);
		if (checkStatementAllowedInContainer(getByKeyStatement)) {
			if (enclosingPart != null && enclosingPart.getEnvironment() != null && enclosingPart.getEnvironment().getCompiler() != null) {
				org.eclipse.edt.compiler.StatementValidator val = enclosingPart.getEnvironment().getCompiler().getValidatorFor(getByKeyStatement);
				if (val != null) {
					val.validateStatement(getByKeyStatement, problemRequestor, compilerOptions);
				}
			}
		}
		postVisitStatement(getByKeyStatement);
		return false;
	}

	public boolean visit(GetByPositionStatement getByPositionStatement) {
		preVisitStatement(getByPositionStatement);
		if (checkStatementAllowedInContainer(getByPositionStatement)) {
			if (enclosingPart != null && enclosingPart.getEnvironment() != null && enclosingPart.getEnvironment().getCompiler() != null) {
				org.eclipse.edt.compiler.StatementValidator val = enclosingPart.getEnvironment().getCompiler().getValidatorFor(getByPositionStatement);
				if (val != null) {
					val.validateStatement(getByPositionStatement, problemRequestor, compilerOptions);
				}
			}
		}
		postVisitStatement(getByPositionStatement);
		return false;
	}

	public boolean visit(GotoStatement gotoStatement) {
		preVisitStatement(gotoStatement);
		if (checkStatementAllowedInContainer(gotoStatement)) {
			gotoStatement.accept(new GotoStatementValidator(problemRequestor, compilerOptions));
			gotoList.add(gotoStatement);
		}
		postVisitStatement(gotoStatement);
		return false;
	}

	public boolean visit(IfStatement ifStatement) {
		if(labelPrecedingStatement != null) {
			labeledLoops.put(ifStatement, labelPrecedingStatement);
		}
		preVisitStatement(ifStatement);
		if (checkStatementAllowedInContainer(ifStatement)) {
			ifStatement.accept(new IfStatementValidator(problemRequestor));
		}
		postVisitStatement(ifStatement);
		return false;
	}

	public boolean visit(LabelStatement labelStatement) {
		nextStatementIsUnreachable = false;
		preVisitStatement(labelStatement);
		labelPrecedingStatement = labelStatement;
		if (checkStatementAllowedInContainer(labelStatement)) {
			labelStatement.accept(new LabelStatementValidator(problemRequestor, compilerOptions));
			if (labelMap.containsKey(labelStatement.getLabel())) {
				problemRequestor.acceptProblem(labelStatement, IProblemRequestor.DUPLICATE_LABEL, new String[] {
						labelStatement.getLabel(), this.functionName });
			} else {
				labelMap.put(labelStatement.getLabel(), labelStatement);
			}
		}
		postVisitStatement(labelStatement);
		return false;
	}

	public boolean visit(MoveStatement moveStatement) {
		preVisitStatement(moveStatement);
		if (checkStatementAllowedInContainer(moveStatement)) {
			moveStatement.accept(new MoveStatementValidator(problemRequestor, compilerOptions, enclosingPart));
		}
		postVisitStatement(moveStatement);
		return false;
	}

	public boolean visit(OpenStatement openStatement) {
		preVisitStatement(openStatement);
		if (checkStatementAllowedInContainer(openStatement)) {
			if (enclosingPart != null && enclosingPart.getEnvironment() != null && enclosingPart.getEnvironment().getCompiler() != null) {
				org.eclipse.edt.compiler.StatementValidator val = enclosingPart.getEnvironment().getCompiler().getValidatorFor(openStatement);
				if (val != null) {
					val.validateStatement(openStatement, problemRequestor, compilerOptions);
				}
			}
		}
		postVisitStatement(openStatement);
		return false;
	}

	public boolean visit(OpenUIStatement openUIStatement) {
		preVisitStatement(openUIStatement);
		if (checkStatementAllowedInContainer(openUIStatement)) {
			openUIStatement.accept(new OpenUIStatementValidator(problemRequestor, compilerOptions));
		}
		postVisitStatement(openUIStatement);
		return false;
	}

	public boolean visit(PrepareStatement prepareStatement) {
		preVisitStatement(prepareStatement);
		if (checkStatementAllowedInContainer(prepareStatement)) {
			if (enclosingPart != null && enclosingPart.getEnvironment() != null && enclosingPart.getEnvironment().getCompiler() != null) {
				org.eclipse.edt.compiler.StatementValidator val = enclosingPart.getEnvironment().getCompiler().getValidatorFor(prepareStatement);
				if (val != null) {
					val.validateStatement(prepareStatement, problemRequestor, compilerOptions);
				}
			}
		}
		postVisitStatement(prepareStatement);
		return false;
	}

	public boolean visit(PrintStatement printStatement) {
		preVisitStatement(printStatement);
		if (checkStatementAllowedInContainer(printStatement)) {
			printStatement.accept(new PrintStatementValidator(problemRequestor, enclosingPart));
		}
		postVisitStatement(printStatement);
		return false;
	}

	public boolean visit(ReplaceStatement replaceStatement) {
		preVisitStatement(replaceStatement);
		if (checkStatementAllowedInContainer(replaceStatement)) {
			if (enclosingPart != null && enclosingPart.getEnvironment() != null && enclosingPart.getEnvironment().getCompiler() != null) {
				org.eclipse.edt.compiler.StatementValidator val = enclosingPart.getEnvironment().getCompiler().getValidatorFor(replaceStatement);
				if (val != null) {
					val.validateStatement(replaceStatement, problemRequestor, compilerOptions);
				}
			}
		}
		postVisitStatement(replaceStatement);
		return false;
	}

	public boolean visit(ReturnStatement returnStatement) {
		preVisitStatement(returnStatement);
		if (checkStatementAllowedInContainer(returnStatement)) {
			returnStatement.accept(new ReturnStatementValidator(problemRequestor, compilerOptions));
		}
		postVisitStatement(returnStatement);
		return false;
	}

	public boolean visit(SetStatement setStatement) {
		preVisitStatement(setStatement);
		if (checkStatementAllowedInContainer(setStatement)) {
			setStatement.accept(new SetStatementValidator(problemRequestor, enclosingPart, compilerOptions));
		}
		postVisitStatement(setStatement);
		return false;
	}

	public boolean visit(SetValuesStatement setValuesStatement) {
		preVisitStatement(setValuesStatement);
		if (checkStatementAllowedInContainer(setValuesStatement)) {
		}
		postVisitStatement(setValuesStatement);
		return false;
	}

	public boolean visit(ShowStatement showStatement) {
		preVisitStatement(showStatement);
		if (checkStatementAllowedInContainer(showStatement)) {
			showStatement.accept(new ShowStatementValidator(problemRequestor, enclosingPart, compilerOptions));
		}
		postVisitStatement(showStatement);
		return false;
	}

	public boolean visit(ThrowStatement throwStatement) {
		preVisitStatement(throwStatement);
		if (checkStatementAllowedInContainer(throwStatement)) {
			throwStatement.accept(new ThrowStatementValidator(problemRequestor, enclosingPart));
		}
		postVisitStatement(throwStatement);
		return false;
	}

	public boolean visit(TransferStatement transferStatement) {
		preVisitStatement(transferStatement);
		if (checkStatementAllowedInContainer(transferStatement)) {
			transferStatement.accept(new TransferStatementValidator(problemRequestor, enclosingPart, compilerOptions));
		}
		postVisitStatement(transferStatement);
		return false;
	}

	public boolean visit(TryStatement tryStatement) {
		preVisitStatement(tryStatement);
		if (checkStatementAllowedInContainer(tryStatement)) {
			tryStatement.accept(new TryStatementValidator(problemRequestor, enclosingPart));
		}
		postVisitStatement(tryStatement);
		return false;
	}

	public boolean visit(WhileStatement whileStatement) {
		if(labelPrecedingStatement != null) {
			labeledLoops.put(whileStatement, labelPrecedingStatement);
		}
		preVisitStatement(whileStatement);
		if (checkStatementAllowedInContainer(whileStatement)) {
			whileStatement.accept(new WhileStatementValidator(problemRequestor));
		}		
		postVisitStatement(whileStatement);
		return false;
	}
	
	public void endVisit(TopLevelFunction topLevelFunction) {
		validateGotoLabels();
	}
	
	public void endVisit(NestedFunction nestedFunction) {
		validateGotoLabels();
	}
	
	private void validateGotoLabels(){
		Iterator iter = gotoList.iterator();
		while(iter.hasNext()){
			GotoStatement gotoStatement = (GotoStatement)iter.next();
			if (!labelMap.containsKey(gotoStatement.getLabel())){
				problemRequestor.acceptProblem(gotoStatement,
						IProblemRequestor.GOTO_LABEL_IS_UNDEFINED,
						new String[]{gotoStatement.getLabel(),functionName});
			}
			else {
				if(!inParents(gotoStatement, ((Node) labelMap.get(gotoStatement.getLabel())).getParent())){
					problemRequestor.acceptProblem(gotoStatement,
						IProblemRequestor.GOTO_LABEL_NOT_ACCESSIBLE,
						new String[]{gotoStatement.getLabel(),functionName});
				}
//				Node gotoOnEventBlock = getEnclosingOnEventBlock(gotoStatement);
//				Node labelOnEventBlock = getEnclosingOnEventBlock((Node) labelMap.get(gotoStatement.getLabel()));
//				if(gotoOnEventBlock == null) {
//					if(labelOnEventBlock != null) {
//						problemRequestor.acceptProblem(gotoStatement, IProblemRequestor.GOTO_LABEL_INSIDE_ONEVENT_BLOCK);
//					}
//				}
//				else {					
//					if(labelOnEventBlock != gotoOnEventBlock) {
//						problemRequestor.acceptProblem(gotoStatement, IProblemRequestor.GOTO_LABEL_OUTSIDE_ONEVENT_BLOCK);
//					}
//				}
			}
		}
	}
	

	private boolean inParents(Node child, Node node) {
		while(child != null) {
			if(child == node) {
				return true;
			}
			child = child.getParent();
		}
		return false;
	}

//	private Node getEnclosingOnEventBlock(Node node) {
//		Node parent = node.getParent();
//		while(parent != null && !(parent instanceof OnEventBlock)) {
//			parent = parent.getParent();
//		}
//		return parent instanceof OnEventBlock ? parent : null;
//	}
	
	private void validatePrimitiveConst(Type type){
		//TODO primitives are being removed, and this check will no longer be valid. see bug 377632
//		StatementValidator.validatePrimitiveConstant(type, problemRequestor);
	}
	
}
