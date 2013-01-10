/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/

package org.eclipse.edt.ide.ui.internal.quickfix.proposals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Status;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.AssignmentStatement;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.CallbackTarget;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.FunctionInvocation;
import org.eclipse.edt.compiler.core.ast.FunctionInvocationStatement;
import org.eclipse.edt.compiler.core.ast.ImportDeclaration;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.PackageDeclaration;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.ide.core.ast.rewrite.ASTRewrite;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.IWorkingCopyCompileRequestor;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompilationResult;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.quickfix.IInvocationContext;
import org.eclipse.edt.ide.ui.wizards.EGLFileConfiguration;
import org.eclipse.edt.ide.ui.wizards.ExtractInterfaceConfiguration;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Delegate;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.swt.graphics.Image;

public class AddFunctionProposal extends AbstractMethodCorrectionProposal {

	final public static String RETARGNAME = "retResult";
	final public static String EXPARGNAME = "exp";
	final public static String EXPPARAMNAME = "AnyException";	
	private File fFile;
	private Part fPart;
	private List <String> fNeedImport = new LinkedList<String>();
	private String fFunctionText;
	
	public AddFunctionProposal(String label, IInvocationContext context, int relevance, Image image, String aFunctionText, List<String> needImports) {
		super(label, context.getEGLFile(), relevance, image, context.getDocument());
		fFile = context.getFileAST();
		fPart = context.getPart();
		fNeedImport = needImports;
		fFunctionText = aFunctionText;
	}

	@Override
	protected ASTRewrite getRewrite() {
		try{
			ASTRewrite rewrite = ASTRewrite.create(fFile);
			for (String aImportStr : fNeedImport) {
					rewrite.addImport(fFile, aImportStr, false);	
			}
			rewrite.addFunction(fPart, fFunctionText);

			return(rewrite);
		}catch(Exception e){
			EDTUIPlugin.log(new Status(Status.ERROR, EDTUIPlugin.PLUGIN_ID, "Insert Function: Error inserting function", e));
		}
		
		return null;
	}

	private static void appendArgument(String argName, String typeName, boolean isFirst, StringBuffer strbuf, boolean isNullable) {
		if (!isFirst){
			strbuf.append(", ");
		}
		strbuf.append(argName);
		strbuf.append(' ');
		strbuf.append(typeName);
		if (isNullable) {
			strbuf.append('?');
		}
		strbuf.append(' ');
		strbuf.append(IEGLConstants.KEYWORD_IN);
	}
	
	private static boolean isAlreadyImported(List currentImports, String qualifier, String strImport){
		for (Iterator it = currentImports.iterator(); it.hasNext(); ) {
			ImportDeclaration importDecl = (ImportDeclaration) it.next();
			String simport = importDecl.getName().getCanonicalName();
			if (importDecl.isOnDemand() && simport.equalsIgnoreCase(qualifier) || simport.equalsIgnoreCase(strImport)) {
				return (true);
			} 
		}
		
		return(false);
	}
	
	public static boolean getFunctionText(IInvocationContext context,final StringBuffer functionName, final StringBuffer functionTextBuffer, final List<String> needImports){
		final int errorOffset = context.getSelectionOffset();
		final List<String> currImports = new ArrayList<String>();
		final String[] currPkg = new String[1];
		final String newLineDelimiter = TextUtilities.getDefaultLineDelimiter(context.getDocument());
		getImportsAndPkg(currImports, context.getFileAST(), currPkg);//Get the current import part and pkg

		EGLFileConfiguration.processBoundPart(context.getEGLFile(), context.getPart().getName().getCanonicalName(), new IWorkingCopyCompileRequestor() {
			@Override
			public void acceptResult(WorkingCopyCompilationResult result) {
				Node boundPart = result.getBoundPart();
				if (boundPart == null) {
					return;
				}
				
				boundPart.accept(new AbstractASTVisitor(){
					public boolean visit(CallStatement callStatement) {
						int callEndOffset = callStatement.getOffset() + callStatement.getLength();
						if(callStatement.getOffset()<= errorOffset && errorOffset<= callEndOffset ){
							Expression serviceFunctionExpr = callStatement.getInvocationTarget();
							CallbackTarget callbackTgt = null;
							CallbackTarget errCallbackTgt = null;
							
							if (callStatement.getCallSynchronizationValues() != null) {
								callbackTgt = callStatement.getCallSynchronizationValues().getReturnTo();
								errCallbackTgt = callStatement.getCallSynchronizationValues().getOnException();
							}

							Expression callbackExpr = null, errCallbackExpr =null;

							if(callbackTgt != null){
								callbackExpr = callbackTgt.getExpression();
							}
							if(errCallbackTgt != null){
								errCallbackExpr = errCallbackTgt.getExpression();
							}
							
							if(callbackExpr != null && callbackExpr.resolveMember() == null && errorOffset >= callbackExpr.getOffset() && errorOffset <= callbackExpr.getOffset() + callbackExpr.getLength()){
								 createCallbackFunction( serviceFunctionExpr, callbackExpr, currImports, currPkg[0], functionTextBuffer, needImports, newLineDelimiter, functionName);	
							}else if(errCallbackExpr != null && errCallbackExpr.resolveMember() == null && errorOffset >= errCallbackExpr.getOffset() && errorOffset <= errCallbackExpr.getOffset() + errCallbackExpr.getLength()){
								createErrCallbackFunction(serviceFunctionExpr, errCallbackExpr, newLineDelimiter, functionTextBuffer, functionName);
							}
						}
											
						return false;
					}
					
					public boolean visit(Assignment assignment) {
						if(assignment.getOffset() <= errorOffset && errorOffset <= assignment.getOffset() + assignment.getLength()){
							createDelegateFunction(assignment, functionTextBuffer, needImports, currImports, functionName, errorOffset, currPkg[0], newLineDelimiter);
						}
						
						return false;
					}
					
					public boolean visit(AssignmentStatement assStmt){
						if(assStmt.getOffset() <= errorOffset && errorOffset <= assStmt.getOffset() + assStmt.getLength()){
							createDelegateFunction(assStmt.getAssignment(), functionTextBuffer, needImports, currImports, functionName, errorOffset, currPkg[0], newLineDelimiter);
						}
						return false;
					}
					
					public boolean visit(FunctionDataDeclaration functionDataDeclaration) {
						return checkInitializer(functionDataDeclaration.getNames().get(0), functionDataDeclaration.getInitializer());
					};
					
					public boolean visit(ClassDataDeclaration classDataDeclartaion) {
						return checkInitializer(classDataDeclartaion.getNames().get(0), classDataDeclartaion.getInitializer());
					};
					
					private boolean checkInitializer(Expression lhs, Expression initializer) {
						if (initializer == null) {
							return true;
						}
						
						if(initializer.getOffset() <= errorOffset && errorOffset <= initializer.getOffset() + initializer.getLength()){
							createDelegateFunction(lhs, initializer, functionTextBuffer, needImports, currImports, functionName, errorOffset, currPkg[0], newLineDelimiter);
						}
						return functionTextBuffer.length() > 0;
					}
					
					public boolean visit(FunctionInvocationStatement functionInvoke){
						if(functionInvoke.getOffset() <= errorOffset && errorOffset<= functionInvoke.getOffset() + functionInvoke.getLength()){
							FunctionInvocation functionInvocation = functionInvoke.getFunctionInvocation();
							Member member = functionInvocation.getTarget().resolveMember();
							
							List<FunctionParameter> paraDefList = null;
							if (member instanceof FunctionMember) {
								paraDefList = ((FunctionMember)member).getParameters();
							}
							else if (functionInvocation.getTarget().resolveType() instanceof Delegate) {
								paraDefList = ((Delegate)functionInvocation.getTarget().resolveType()).getParameters();
							}
							
							if (paraDefList != null) {
								Iterator<Expression> realIter = functionInvocation.getArguments().iterator();
								
								FunctionParameter errorParaBinding = null;
								Type typeBinding = null;
								String name = null;
								
								for (Iterator<FunctionParameter> iterator = paraDefList.iterator(); iterator.hasNext();) {
									Expression aSimpleName = realIter.next();
									if(aSimpleName.getOffset() <= errorOffset && errorOffset<= aSimpleName.getOffset() + aSimpleName.getLength()){
										name = aSimpleName.getCanonicalString();
										errorParaBinding = iterator.next();
										typeBinding = errorParaBinding.getType();
										break;
									}
									
									iterator.next();
								}
								
								if (typeBinding instanceof Delegate){
									getDelegateFunctionString((Delegate)typeBinding, name, functionName, functionTextBuffer, newLineDelimiter, needImports, currImports, currPkg[0]);
								}
							}
						}

						return false;
					}
				});
			}
		});

		return(functionTextBuffer.toString().trim().length() > 1 );
	}
	
	protected static void createDelegateFunction(Assignment assignment,
			StringBuffer functionTextBuffer, List<String> needImports, 
			List<String> currImports, StringBuffer bfFunctionName, int errorOffset, String currPkg, String newLine) {
		createDelegateFunction(assignment.getLeftHandSide(), assignment.getRightHandSide(), functionTextBuffer, needImports, currImports, bfFunctionName, errorOffset, currPkg, newLine);
	}
	protected static void createDelegateFunction(Expression lhs, Expression rhs,
			StringBuffer functionTextBuffer, List<String> needImports, 
			List<String> currImports, StringBuffer bfFunctionName, int errorOffset, String currPkg, String newLine) {
		Type leftSideBinding = lhs.resolveType();
		Delegate delegateBinding = null;
		String functionName = "";
		
		if(rhs instanceof ArrayLiteral){
			List<Expression> expressions = ((ArrayLiteral)rhs).getExpressions();
			for (Expression expression : expressions) {
				if(expression instanceof SimpleName){
					if(((SimpleName)expression).getOffset() <= errorOffset && errorOffset <=((SimpleName)expression).getLength() + ((SimpleName)expression).getOffset()){
						functionName = ((SimpleName)expression).getCanonicalName().trim();
					}
				}
			}
		}else if(rhs instanceof SimpleName){
			SimpleName simpleName = (SimpleName)rhs;
			functionName = simpleName.getCanonicalName().trim();
		}
		
		if(leftSideBinding instanceof Delegate){
			delegateBinding = (Delegate)leftSideBinding;
		}else if(leftSideBinding instanceof ArrayType){
			Type rootType = leftSideBinding;
			while (rootType instanceof ArrayType) {
				rootType = ((ArrayType)rootType).getElementType();
			}
			if(rootType instanceof Delegate){
				delegateBinding = (Delegate)rootType;
			}
		}
		
		if (delegateBinding != null) {
			getDelegateFunctionString(delegateBinding, functionName, bfFunctionName, functionTextBuffer, newLine, needImports, currImports, currPkg );
		}
	}
	
	private static void createCallbackFunction( Expression serviceExpr, Expression callbackExpr, final List<String> currImports, final String currFilePkg, 
			StringBuffer functionTextBuffer, final List<String> needImports, final String newLineDelimiter, final StringBuffer functionNameBuffer) {
			if (callbackExpr.isName()) {
				Name callbackExprName = (Name) callbackExpr;
				getCallbackFunctionString(serviceExpr, callbackExprName, new ICallbackArgumentPrinter() {
					public void printArgs(Expression serviceExpr, StringBuffer strbuf) {
						createCallbackFunctionArguments(serviceExpr, strbuf, currImports, currFilePkg, needImports, newLineDelimiter);
					}
				}, functionTextBuffer, newLineDelimiter, functionNameBuffer) ;
		}
	}
	
	private static void createErrCallbackFunction(Expression serviceExpr,
		Expression errCallbackExpr, final String newLineDelimiter, 
		StringBuffer functionTextBuffer, StringBuffer functionNameBuffer) {
		if (errCallbackExpr.isName()) {
			Name callbackExprName = (Name) errCallbackExpr;
			getCallbackFunctionString(serviceExpr, callbackExprName, new ICallbackArgumentPrinter() {
				public void printArgs(Expression serviceExpr, StringBuffer strbuf) {
					appendArgument(EXPARGNAME, EXPPARAMNAME, true, strbuf, false);
				}
			},functionTextBuffer, newLineDelimiter, functionNameBuffer);
		}
	}
	
	private static void createCallbackFunctionArguments(Expression serviceExpr,StringBuffer strbuf, List currImports,
			String currFilePkg, List<String> needImports, String newLineDelimiter) {
		Member serviceExprBinding = serviceExpr.resolveMember();
		if (serviceExprBinding instanceof FunctionMember) {
			Set argNames = new HashSet();
			Set insertedImports = new HashSet();
			
			FunctionMember svrFuncBinding = (FunctionMember) serviceExprBinding;
			List<FunctionParameter> params = svrFuncBinding.getParameters();
			boolean isFirst = true;
			for (Iterator<FunctionParameter> it = params.iterator(); it.hasNext();) {
				FunctionParameter paramBinding = it.next();
				switch (paramBinding.getParameterKind()) {
					case PARM_OUT:
					case PARM_INOUT:
						String argName = paramBinding.getCaseSensitiveName();
						argNames.add(argName);
						
						appendArguments(argName, paramBinding.getType(), isFirst, strbuf, currImports,currFilePkg, insertedImports, needImports, newLineDelimiter, paramBinding.isNullable());
						isFirst = false;
						break;
				}
			}

			Type retType = svrFuncBinding.getReturnType();
			if (retType != null) {
				// get the unique argument name for the return argument
				int n = 1;
				String retArgNm = RETARGNAME;
				while (argNames.contains(retArgNm)) {
					retArgNm = RETARGNAME + Integer.toString(n);
					n++;
				}
				appendArguments(retArgNm, retType, isFirst, strbuf, currImports, currFilePkg,  insertedImports, needImports, newLineDelimiter, svrFuncBinding.isNullable());
				isFirst = false;
			}
		}
	}
	
	private static void appendArguments(String argName, Type paramTypeBinding, boolean isFirst, StringBuffer strbuf,
			List currImports, String currFilePkg, Set insertedImports, List<String>needImport, String newLineDelimiter, boolean isNullable) {
		
		appendArgument(argName, BindingUtil.getShortTypeString(paramTypeBinding, true), isFirst, strbuf, isNullable);
		
		Type rootType = paramTypeBinding;
		while (rootType instanceof ArrayType) {
			rootType = ((ArrayType)rootType).getElementType();
		}
		if (rootType instanceof org.eclipse.edt.mof.egl.Part) {
			String qualifier = ExtractInterfaceConfiguration.getReferenceTypeParamQualifier(paramTypeBinding,currFilePkg);
			
			if (!qualifier.equalsIgnoreCase(currFilePkg)) {
				// build the import string
				String strImport = qualifier + (qualifier.length() > 0 ? "." : "");
				strImport += getSimpleName(rootType, false);

				if(!isAlreadyImported(currImports, qualifier, strImport) && !insertedImports.contains(strImport) && strImport.contains(".")){
					needImport.add(strImport);
				}
			}
		}
	}
	
	private interface ICallbackArgumentPrinter{
		public void printArgs(Expression serviceExpr, StringBuffer strbuf);
	}

	private static void getImportsAndPkg(final List imports, File file, final String[] fCurrPkg){
		file.accept(new DefaultASTVisitor(){
			public boolean visit(File file) {return true;}
			public boolean visit(PackageDeclaration packageDeclaration) {
				fCurrPkg[0] = packageDeclaration.getName().getCanonicalString();
				return true;
			}
			public boolean visit(ImportDeclaration importDeclaration) {
				imports.add(importDeclaration);
				return true;
			}			
		});	
	}
	
	private static void getCallbackFunctionString(Expression serviceExpr,Name callbckFuncName, ICallbackArgumentPrinter callbackArgPrinter,
		StringBuffer callBackFuncStr, String newLineDelimiter,final StringBuffer functionNameBuffer) {
		callBackFuncStr.append(IEGLConstants.KEYWORD_FUNCTION);
		callBackFuncStr.append(" ");
		String callbackFuncName = callbckFuncName.getCaseSensitiveIdentifier();
		callBackFuncStr.append(callbackFuncName);
		callBackFuncStr.append('(');
		callbackArgPrinter.printArgs(serviceExpr, callBackFuncStr);
		callBackFuncStr.append(')');
		
		functionNameBuffer.append(callBackFuncStr.toString());//Get the function name
		callBackFuncStr.append(newLineDelimiter);
		callBackFuncStr.append("\t//Auto-generated method stub");
		callBackFuncStr.append(newLineDelimiter);
		callBackFuncStr.append(IEGLConstants.KEYWORD_END);
		callBackFuncStr.append(newLineDelimiter);
	}	
	
	private static void getDelegateFunctionString(Delegate delegateBinding, String functionName, StringBuffer bfFunctionName, StringBuffer delegateFunctionStr,
			String newLineDelimeter, List<String> needImports, List<String> currImiports, String currFilePkg){
		
		delegateFunctionStr.append(IEGLConstants.KEYWORD_FUNCTION);
		delegateFunctionStr.append(" ");
		delegateFunctionStr.append(functionName);
		delegateFunctionStr.append("(");
		
		List<FunctionParameter> parameters = delegateBinding.getParameters();
		for (int parameterIndex = 0; parameterIndex < parameters.size(); parameterIndex++) {
			FunctionParameter functionParameterBinding = parameters.get(parameterIndex);
			String name = functionParameterBinding.getCaseSensitiveName();
			Type paraTypeBinding = functionParameterBinding.getType();
			String type = getSimpleName(paraTypeBinding, true);
			String qualifier = ExtractInterfaceConfiguration.getReferenceTypeParamQualifier(paraTypeBinding,currFilePkg);
			if (!qualifier.equalsIgnoreCase(currFilePkg)) {
				// build the import string
				Type rootType = paraTypeBinding;
				while (rootType instanceof ArrayType) {
					rootType = ((ArrayType)rootType).getElementType();
				}
				
				String strImport = qualifier + (qualifier.length() > 0 ? "." : "");
				strImport += getSimpleName(rootType, false);
				if(!isAlreadyImported(currImiports, qualifier, strImport) && !needImports.contains(strImport) && strImport.contains(".")){
					needImports.add(strImport);
				}
			}
			
			String decorate = null;
			switch (functionParameterBinding.getParameterKind()) {
				case PARM_IN:
					decorate = IEGLConstants.KEYWORD_IN;
					break;
				case PARM_OUT:
					decorate = IEGLConstants.KEYWORD_OUT;
					break;
				case PARM_INOUT:
				default:
					decorate = IEGLConstants.KEYWORD_INOUT;
					break;
			}
				
			delegateFunctionStr.append(name).append(" ").append(type);
			if (functionParameterBinding.isNullable()) {
				delegateFunctionStr.append('?');
			}
			if(decorate != null){
				delegateFunctionStr.append(" ").append(decorate);
			}
			
			if(parameterIndex+1 < parameters.size()){
				delegateFunctionStr.append(", ");
			}
		}
		
		delegateFunctionStr.append(") ");
		
		//returns
		Type returnsTypeBinding = delegateBinding.getReturnType();
		if(returnsTypeBinding != null){
			String qualifier = ExtractInterfaceConfiguration.getReferenceTypeParamQualifier(returnsTypeBinding,currFilePkg);
			if (!qualifier.equalsIgnoreCase(currFilePkg)) {
				// build the import string
				Type rootType = returnsTypeBinding;
				while (rootType instanceof ArrayType) {
					rootType = ((ArrayType)rootType).getElementType();
				}
				
				String strImport = qualifier + (qualifier.length() > 0 ? "." : "");
				strImport += getSimpleName(rootType, false);
				if(!isAlreadyImported(currImiports, qualifier, strImport) && !needImports.contains(strImport) && strImport.contains(".")){
					needImports.add(strImport);
				}
			}
			String type = getSimpleName(returnsTypeBinding, true);
			String nullable = null;
			if(delegateBinding.isNullable()){
				nullable = "?";
			}else{
				nullable = "";
			}
			
			delegateFunctionStr.append( IEGLConstants.KEYWORD_RETURNS + "(").append(type).append(nullable).append(")");
		}
		bfFunctionName.append(delegateFunctionStr.toString());
		
		delegateFunctionStr.append(newLineDelimeter);
		delegateFunctionStr.append("\t//Auto-generated method stub");
		delegateFunctionStr.append(newLineDelimeter).append(IEGLConstants.KEYWORD_END);
	}
	
	private static String getSimpleName(Type type, boolean includeParams) {
		return BindingUtil.getShortTypeString(type, includeParams);
	}
}
