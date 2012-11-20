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

import org.eclipse.edt.compiler.ASTValidator;
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
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.DeleteStatement;
import org.eclipse.edt.compiler.core.ast.ExecuteStatement;
import org.eclipse.edt.compiler.core.ast.ExitStatement;
import org.eclipse.edt.compiler.core.ast.ForEachStatement;
import org.eclipse.edt.compiler.core.ast.ForStatement;
import org.eclipse.edt.compiler.core.ast.ForwardStatement;
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
import org.eclipse.edt.compiler.core.ast.PrepareStatement;
import org.eclipse.edt.compiler.core.ast.ReplaceStatement;
import org.eclipse.edt.compiler.core.ast.ReturnStatement;
import org.eclipse.edt.compiler.core.ast.SetStatement;
import org.eclipse.edt.compiler.core.ast.SetValuesStatement;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.core.ast.SuperExpression;
import org.eclipse.edt.compiler.core.ast.ThisExpression;
import org.eclipse.edt.compiler.core.ast.ThrowStatement;
import org.eclipse.edt.compiler.core.ast.TryStatement;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.core.ast.WhileStatement;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.AssignmentStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.CaseStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.ContinueStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.ExitStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.ForStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.FunctionDataDeclarationValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.GotoStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.IfStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.LabelStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.MoveStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.ReturnStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.SetStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.ThrowStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.TryStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.WhileStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.type.TypeValidator;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.utils.NameUtile;

public class FunctionValidator extends AbstractASTVisitor {
	
	IProblemRequestor problemRequestor;
	private IPartBinding enclosingPart;
	private String functionName;
	private HashMap labelMap = new HashMap();
	private ArrayList gotoList = new ArrayList();
    ICompilerOptions compilerOptions;
    private boolean nextStatementIsUnreachable;
	private LabelStatement labelPrecedingStatement;
	private Map labeledLoops = new HashMap();
	
	public FunctionValidator(IProblemRequestor problemRequestor, IPartBinding enclosingPart, ICompilerOptions compilerOptions) {
		this.problemRequestor = problemRequestor;
		this.enclosingPart = enclosingPart;
		this.compilerOptions = compilerOptions;
	}
	
	public FunctionValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		this(problemRequestor, null, compilerOptions);
	}
	
	@Override
	public boolean visit(NestedFunction nestedFunction) {
		functionName = nestedFunction.getName().getCanonicalName();
		
		checkFunctionName(nestedFunction.getName(), true);
		checkNumberOfParms(nestedFunction.getFunctionParameters(), nestedFunction.getName(), functionName);
		checkForConstructorCalls(nestedFunction);
		checkAbstract(nestedFunction);
		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(nestedFunction);
		
		return true;
	}
	
	@Override
	public boolean visit(Constructor constructor) {
		functionName = IEGLConstants.KEYWORD_CONSTRUCTOR;
		
		checkNumberOfParms(constructor.getParameters(), constructor, functionName);
		checkForConstructorCalls(constructor);
		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(constructor);
		
		return true;
	}
	
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.ReturnsDeclaration returnsDeclaration) {
		Type type = returnsDeclaration.getType();
		TypeValidator.validate(type, enclosingPart, problemRequestor, compilerOptions);
		TypeValidator.validateTypeDeclaration(type, enclosingPart, problemRequestor);
		return true;
	};
	
	private void checkForConstructorCalls(Node node) {
		node.accept(new AbstractASTVisitor() {
			@Override
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
	
	private void checkAbstract(NestedFunction func) {
		if (func.isAbstract()) {
			FunctionMember irFunc = (FunctionMember)func.getName().resolveMember();
			if (func.getParent() instanceof org.eclipse.edt.compiler.core.ast.Part && !((org.eclipse.edt.compiler.core.ast.Part)func.getParent()).isAbstract()) {
				problemRequestor.acceptProblem(
						func,
						IProblemRequestor.ABSTRACT_FUNCTION_IN_CONCRETE_PART,
						new String[]{func.getName().getCaseSensitiveIdentifier() + "(" + FunctionContainerValidator.getTypeNamesList(irFunc.getParameters()) + ")"});
			}
			
			if (func.isPrivate()) {
				problemRequestor.acceptProblem(
						func,
						IProblemRequestor.ABSTRACT_FUNCTION_IS_PRIVATE,
						new String[]{func.getName().getCaseSensitiveIdentifier() + "(" + FunctionContainerValidator.getTypeNamesList(irFunc.getParameters()) + ")"});
			}
			
			if (func.isStatic()) {
				problemRequestor.acceptProblem(
						func,
						IProblemRequestor.ABSTRACT_FUNCTION_IS_STATIC,
						new String[]{func.getName().getCaseSensitiveIdentifier() + "(" + FunctionContainerValidator.getTypeNamesList(irFunc.getParameters()) + ")"});
			}
		}
	}
	
	void checkNumberOfParms(List parms, Node name, String nameString) {
		if (parms.size() > 255) {
        	problemRequestor.acceptProblem(
            		name,
    				IProblemRequestor.FUNCTION_TOO_MANY_PARMS,
    				new String[] {nameString, Integer.toString(parms.size())});        	
		}
	}
	
	@Override
	public boolean visit(FunctionParameter functionParameter) {
        
		Type parmType = functionParameter.getType();
		
        Member member = functionParameter.getName().resolveMember();
        if (member instanceof org.eclipse.edt.mof.egl.FunctionParameter) {
        	TypeValidator.validate(parmType, enclosingPart, problemRequestor, compilerOptions);
        	
	        checkParmTypeNotStaticArray(functionParameter, parmType);
	        
	        org.eclipse.edt.mof.egl.Type type = member.getType();
	        if (type != null) {
	        	checkParmNotEmptyRecord(functionParameter, type);
	        	
	        	switch (((org.eclipse.edt.mof.egl.FunctionParameter)member).getParameterKind()) {
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
		if (parmType.isArrayType()) {
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
	
	private boolean hasSupertypeNotAnyRecord(StructPart part) {

		StructPart anyRec = (StructPart)BindingUtil.findPart(NameUtile.getAsName(MofConversion.EGLX_lang_package), NameUtile.getAsName("AnyRecord"));

		for (StructPart superType : part.getSuperTypes()) {
			if (!superType.equals(anyRec)) {
				return true;
			}
		}
		return false;
	}
	
	
	private void checkParmNotEmptyRecord(FunctionParameter functionParameter, org.eclipse.edt.mof.egl.Type parmType) {
		if (parmType instanceof Part) {
			boolean isEmptyRecord = false;
			
			int partType = BindingUtil.getPartTypeConstant((Part)parmType);
			if (partType == ITypeBinding.FLEXIBLE_RECORD_BINDING) {
				isEmptyRecord = ((Record)parmType).getFields().isEmpty() && !hasSupertypeNotAnyRecord((Record)parmType);
			}
			
			if (isEmptyRecord) {
	    		problemRequestor.acceptProblem(
	    			functionParameter.getType(),
					IProblemRequestor.RECORD_PARAMETER_WITH_NO_CONTENTS,
					new String[] {functionParameter.getName().getCanonicalName(), functionParameter.getType().getCanonicalName()});
	    	}
		}
	}
	
	private void checkInputParm(FunctionParameter functionParameter) {
	}
	
	private void checkOutputParm(FunctionParameter functionParameter) {
		if (functionParameter.isParmConst()) {
			problemRequestor.acceptProblem(
            		functionParameter,
					IProblemRequestor.CONST_MODIFIER_NOT_ALLOWED_WITH_OUT_MODIFIER,
					new String[] {IEGLConstants.KEYWORD_OUT.toUpperCase(), IEGLConstants.KEYWORD_INTERVAL.toUpperCase()});
		}
	}
	
	private void checkInputOutputParm(FunctionParameter functionParameter) {
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
	
	@Override
	public boolean visit(AddStatement addStatement) {
		preVisitStatement(addStatement);
		validateStatement(addStatement);
		postVisitStatement(addStatement);
		return false;
	}
	
	@Override
	public boolean visit(AssignmentStatement assignmentStatement) {
		preVisitStatement(assignmentStatement);
		assignmentStatement.accept(new AssignmentStatementValidator(problemRequestor, compilerOptions, enclosingPart));
		postVisitStatement(assignmentStatement);
		return false;
	}
	
	@Override
	public boolean visit(CallStatement callStatement) {
		preVisitStatement(callStatement);
		validateStatement(callStatement);
		postVisitStatement(callStatement);
		return false;
	}
	
	@Override
	public boolean visit(CaseStatement caseStatement) {
		if(labelPrecedingStatement != null) {
			labeledLoops.put(caseStatement, labelPrecedingStatement);
		}
		preVisitStatement(caseStatement);
		caseStatement.accept(new CaseStatementValidator(problemRequestor, compilerOptions));
		postVisitStatement(caseStatement);
		return false;
	}
	
	@Override
	public boolean visit(CloseStatement closeStatement) {
		preVisitStatement(closeStatement);
		validateStatement(closeStatement);
		postVisitStatement(closeStatement);
		return false;
	}
	
	@Override
	public boolean visit(ContinueStatement continueStatement) {
		preVisitStatement(continueStatement);
		continueStatement.accept(new ContinueStatementValidator(problemRequestor, labeledLoops));
		nextStatementIsUnreachable = true;
		postVisitStatement(continueStatement);
		return false;
	}
	
	@Override
	public boolean visit(DeleteStatement deleteStatement) {
		preVisitStatement(deleteStatement);
		validateStatement(deleteStatement);
		postVisitStatement(deleteStatement);
		return false;
	}
	
	@Override
	public boolean visit(ExecuteStatement executeStatement) {
		preVisitStatement(executeStatement);
		validateStatement(executeStatement);
		postVisitStatement(executeStatement);
		return false;
	}
	
	@Override
	public boolean visit(ExitStatement exitStatement) {
		preVisitStatement(exitStatement);
		exitStatement.accept(new ExitStatementValidator(problemRequestor, labeledLoops, enclosingPart, compilerOptions));
		nextStatementIsUnreachable = true;
		postVisitStatement(exitStatement);
		return false;
	}
	
	@Override
	public boolean visit(ForEachStatement forEachStatement) {
		if(labelPrecedingStatement != null) {
			labeledLoops.put(forEachStatement, labelPrecedingStatement);
		}
		preVisitStatement(forEachStatement);
		validateStatement(forEachStatement);
		postVisitStatement(forEachStatement);
		return false;
	}
	
	@Override
	public boolean visit(ForStatement forStatement) {
		if(labelPrecedingStatement != null) {
			labeledLoops.put(forStatement, labelPrecedingStatement);
		}
		preVisitStatement(forStatement);
		forStatement.accept(new ForStatementValidator(problemRequestor, compilerOptions));
		postVisitStatement(forStatement);
		return false;
	}
	
	@Override
	public boolean visit(FunctionInvocationStatement functionInvocationStatement) {
		preVisitStatement(functionInvocationStatement);
		postVisitStatement(functionInvocationStatement);
		return false;
	}
	
	@Override
	public boolean visit(FunctionDataDeclaration functionDataDeclaration) {
		preVisitStatement(functionDataDeclaration);
		functionDataDeclaration.accept(new FunctionDataDeclarationValidator(problemRequestor, compilerOptions, enclosingPart));
		postVisitStatement(functionDataDeclaration);
		return false;
	}
	
	@Override
	public boolean visit(GetByKeyStatement getByKeyStatement) {
		preVisitStatement(getByKeyStatement);
		validateStatement(getByKeyStatement);
		postVisitStatement(getByKeyStatement);
		return false;
	}
	
	@Override
	public boolean visit(GetByPositionStatement getByPositionStatement) {
		preVisitStatement(getByPositionStatement);
		validateStatement(getByPositionStatement);
		postVisitStatement(getByPositionStatement);
		return false;
	}
	
	@Override
	public boolean visit(GotoStatement gotoStatement) {
		preVisitStatement(gotoStatement);
		gotoStatement.accept(new GotoStatementValidator(problemRequestor, compilerOptions));
		gotoList.add(gotoStatement);
		postVisitStatement(gotoStatement);
		return false;
	}
	
	@Override
	public boolean visit(IfStatement ifStatement) {
		if(labelPrecedingStatement != null) {
			labeledLoops.put(ifStatement, labelPrecedingStatement);
		}
		preVisitStatement(ifStatement);
		ifStatement.accept(new IfStatementValidator(problemRequestor));
		postVisitStatement(ifStatement);
		return false;
	}
	
	@Override
	public boolean visit(LabelStatement labelStatement) {
		nextStatementIsUnreachable = false;
		preVisitStatement(labelStatement);
		labelPrecedingStatement = labelStatement;
		
		labelStatement.accept(new LabelStatementValidator(problemRequestor, compilerOptions));
		if (labelMap.containsKey(labelStatement.getLabel())) {
			problemRequestor.acceptProblem(labelStatement, IProblemRequestor.DUPLICATE_LABEL, new String[] {
					labelStatement.getLabel(), this.functionName });
		} else {
			labelMap.put(labelStatement.getLabel(), labelStatement);
		}
		
		postVisitStatement(labelStatement);
		return false;
	}
	
	@Override
	public boolean visit(MoveStatement moveStatement) {
		preVisitStatement(moveStatement);
		moveStatement.accept(new MoveStatementValidator(problemRequestor, compilerOptions, enclosingPart));
		postVisitStatement(moveStatement);
		return false;
	}
	
	@Override
	public boolean visit(OpenStatement openStatement) {
		preVisitStatement(openStatement);
		validateStatement(openStatement);
		postVisitStatement(openStatement);
		return false;
	}
	
	@Override
	public boolean visit(PrepareStatement prepareStatement) {
		preVisitStatement(prepareStatement);
		validateStatement(prepareStatement);
		postVisitStatement(prepareStatement);
		return false;
	}
	
	@Override
	public boolean visit(ReplaceStatement replaceStatement) {
		preVisitStatement(replaceStatement);
		validateStatement(replaceStatement);
		postVisitStatement(replaceStatement);
		return false;
	}
	
	@Override
	public boolean visit(ReturnStatement returnStatement) {
		preVisitStatement(returnStatement);
		returnStatement.accept(new ReturnStatementValidator(problemRequestor, compilerOptions));
		postVisitStatement(returnStatement);
		return false;
	}
	
	@Override
	public boolean visit(SetStatement setStatement) {
		preVisitStatement(setStatement);
		setStatement.accept(new SetStatementValidator(problemRequestor, enclosingPart, compilerOptions));
		postVisitStatement(setStatement);
		return false;
	}
	
	@Override
	public boolean visit(SetValuesStatement setValuesStatement) {
		preVisitStatement(setValuesStatement);
		postVisitStatement(setValuesStatement);
		return false;
	}
	
	@Override
	public boolean visit(ThrowStatement throwStatement) {
		preVisitStatement(throwStatement);
		throwStatement.accept(new ThrowStatementValidator(problemRequestor, enclosingPart));
		postVisitStatement(throwStatement);
		return false;
	}
	
	@Override
	public boolean visit(TryStatement tryStatement) {
		preVisitStatement(tryStatement);
		tryStatement.accept(new TryStatementValidator(problemRequestor, enclosingPart));
		postVisitStatement(tryStatement);
		return false;
	}
	
	@Override
	public boolean visit(WhileStatement whileStatement) {
		if(labelPrecedingStatement != null) {
			labeledLoops.put(whileStatement, labelPrecedingStatement);
		}
		preVisitStatement(whileStatement);
		whileStatement.accept(new WhileStatementValidator(problemRequestor));
		postVisitStatement(whileStatement);
		return false;
	}
	
	@Override
	public void endVisit(NestedFunction nestedFunction) {
		validateGotoLabels();
	}
	
	private void validateStatement(Statement stmt) {
		List<ASTValidator> validators = enclosingPart.getEnvironment().getCompiler().getValidatorsFor(stmt);
		if (validators != null && validators.size() > 0) {
			for (ASTValidator validator : validators) {
				validator.validate(stmt, enclosingPart, problemRequestor, compilerOptions);
			}
		}
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

	public static String getName(Statement statement) {
		final String[] result = new String[] {null};
		statement.accept(new DefaultASTVisitor() {
			@Override
			public void endVisit(AddStatement addStatement) {
				result[0] = IEGLConstants.KEYWORD_ADD;
			}
			@Override
			public void endVisit(CallStatement callStatement) {
				result[0] = IEGLConstants.KEYWORD_CALL;
			}
			@Override
			public void endVisit(CaseStatement caseStatement) {
				result[0] = IEGLConstants.KEYWORD_CASE;
			}
			@Override
			public void endVisit(CloseStatement closeStatement) {
				result[0] = IEGLConstants.KEYWORD_CLOSE;
			}
			@Override
			public void endVisit(ContinueStatement continueStatement) {
				result[0] = IEGLConstants.KEYWORD_CONTINUE;
			}
			@Override
			public void endVisit(DeleteStatement deleteStatement) {
				result[0] = IEGLConstants.KEYWORD_DELETE;
			}
			@Override
			public void endVisit(ExecuteStatement executeStatement) {
				result[0] = IEGLConstants.KEYWORD_EXECUTE;
			}
			@Override
			public void endVisit(ExitStatement exitStatement) {
				result[0] = IEGLConstants.KEYWORD_EXIT;
			}
			@Override
			public void endVisit(ForEachStatement forEachStatement) {
				result[0] = IEGLConstants.KEYWORD_FOREACH;
			}
			@Override
			public void endVisit(ForStatement forStatement) {
				result[0] = IEGLConstants.KEYWORD_FOR;
			}
			@Override
			public void endVisit(ForwardStatement forwardStatement) {
				result[0] = IEGLConstants.KEYWORD_FORWARD;
			}
			@Override
			public void endVisit(GetByKeyStatement getByKeyStatement) {
				result[0] = IEGLConstants.KEYWORD_GET;
			}
			@Override
			public void endVisit(GetByPositionStatement getByPositionStatement) {
				result[0] = IEGLConstants.KEYWORD_GET;
			}
			@Override
			public void endVisit(GotoStatement gotoStatement) {
				result[0] = IEGLConstants.KEYWORD_GOTO;
			}
			@Override
			public void endVisit(IfStatement ifStatement) {
				result[0] = IEGLConstants.KEYWORD_IF;
			}
			@Override
			public void endVisit(MoveStatement moveStatement) {
				result[0] = IEGLConstants.KEYWORD_MOVE;
			}
			@Override
			public void endVisit(OpenStatement openStatement) {
				result[0] = IEGLConstants.KEYWORD_OPEN;
			}
			@Override
			public void endVisit(ReplaceStatement replaceStatement) {
				result[0] = IEGLConstants.KEYWORD_REPLACE;
			}
			@Override
			public void endVisit(ThrowStatement throwStatement) {
				result[0] = IEGLConstants.KEYWORD_THROW;
			}
			@Override
			public void endVisit(TryStatement tryStatement) {
				result[0] = IEGLConstants.KEYWORD_TRY;
			}
			@Override
			public void endVisit(WhileStatement whileStatement) {
				result[0] = IEGLConstants.KEYWORD_WHILE;
			}
			@Override
		    public void endVisit(ReturnStatement returnStatement) {
		        result[0] = IEGLConstants.KEYWORD_RETURN;
		    }
		    @Override
		    public void endVisit(SetStatement setStatement) {
		        result[0] = IEGLConstants.KEYWORD_SET;
		    }
		    @Override
		    public void endVisit(PrepareStatement prepareStatement) {
		        result[0] = IEGLConstants.KEYWORD_PREPARE;
		    }
		});
		
		if(result[0] == null) {
			throw new RuntimeException("Must define visit() for class " + statement.getClass() + " in " + FunctionValidator.class.getSimpleName() + "::getName()");
		}
		
		return result[0];
	}
}
