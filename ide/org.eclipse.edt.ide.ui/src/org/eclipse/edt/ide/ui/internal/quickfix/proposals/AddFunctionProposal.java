/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.edt.ide.ui.internal.quickfix.proposals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Status;
import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.DelegateBinding;
import org.eclipse.edt.compiler.binding.FunctionBinding;
import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.AssignmentStatement;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.CallbackTarget;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.ImportDeclaration;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.PackageDeclaration;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.ide.core.ast.rewrite.ASTRewrite;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.quickfix.IInvocationContext;
import org.eclipse.edt.ide.ui.wizards.EGLFileConfiguration;
import org.eclipse.edt.ide.ui.wizards.ExtractInterfaceConfiguration;
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

	private static void appendArgument(String argName, String typeName, boolean isFirst, StringBuffer strbuf) {
		if (!isFirst){
			strbuf.append(", ");
		}
		strbuf.append(argName);
		strbuf.append(' ');
		strbuf.append(typeName);
		strbuf.append(' ');
		strbuf.append(IEGLConstants.KEYWORD_IN);
	}
	
	private void appendArguments(String argName, ITypeBinding paramTypeBinding, boolean isFirst, StringBuffer strbuf,
			List currImports, String currFilePkg, Set insertedImports, String newLineDelimiter, List<String> needImport) {
		
		StringBuffer strBufTypeName = new StringBuffer();
		ExtractInterfaceConfiguration.getSimpleTypeString(paramTypeBinding,strBufTypeName);
		appendArgument(argName, strBufTypeName.toString(), isFirst, strbuf);

		if (paramTypeBinding.getBaseType().isPartBinding()) {
			String qualifier = ExtractInterfaceConfiguration.getReferenceTypeParamQualifier(paramTypeBinding,currFilePkg);
			if (!qualifier.equalsIgnoreCase(currFilePkg)) {
				// build the import string
				String strImport = qualifier + (qualifier.length() > 0 ? "." : "");
				strImport += paramTypeBinding.getBaseType().getCaseSensitiveName();

				if(!isAlreadyImported(currImports, qualifier, strImport) && !insertedImports.contains(strImport) && strImport.contains(".")){
					needImport.add(strImport);
				}
			}
		}
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

		Part boundPart = EGLFileConfiguration.getBoundPart(context.getEGLFile(), context.getPart().getName().getCanonicalName());
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
					
					if(callbackExpr != null && !callbackExpr.resolveDataBinding().isValidBinding() && errorOffset >= callbackExpr.getOffset() && errorOffset <= callbackExpr.getOffset() + callbackExpr.getLength()){
						 createCallbackFunction( serviceFunctionExpr, callbackExpr, currImports, currPkg[0], functionTextBuffer, needImports, newLineDelimiter, functionName);	
					}else if(errCallbackExpr != null && !errCallbackExpr.resolveDataBinding().isValidBinding() && errorOffset >= errCallbackExpr.getOffset() && errorOffset <= errCallbackExpr.getOffset() + errCallbackExpr.getLength()){
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
		});

		return(functionTextBuffer.toString().trim().length() > 1 );
	}
	
	protected static void createDelegateFunction(Assignment assignment,
			StringBuffer functionTextBuffer, List<String> needImports, 
			List<String> currImports, StringBuffer bfFunctionName, int errorOffset, String currPkg, String newLine) {

		ITypeBinding leftSideBinding = assignment.getLeftHandSide().resolveTypeBinding();
		DelegateBinding delegateBinding = null;
		String functionName = "";
		
			Expression rightEP =  assignment.getRightHandSide();
			if(rightEP instanceof ArrayLiteral){
				List<Expression> expressions = ((ArrayLiteral)rightEP).getExpressions();
				for (Expression expression : expressions) {
					if(expression instanceof SimpleName){
						if(((SimpleName)expression).getOffset() <= errorOffset && errorOffset <=((SimpleName)expression).getLength() + ((SimpleName)expression).getOffset()){
							functionName = ((SimpleName)expression).getCanonicalName().trim();
						}
					}
				}
			}else if(rightEP instanceof SimpleName){
				SimpleName simpleName = (SimpleName)rightEP;
				functionName = simpleName.getCanonicalName().trim();
			}
			
			if(leftSideBinding instanceof DelegateBinding){
				delegateBinding = (DelegateBinding)leftSideBinding;
			}else if(leftSideBinding instanceof ArrayTypeBinding){
				ArrayTypeBinding arrayTypeBinding = (ArrayTypeBinding)leftSideBinding;
				ITypeBinding elementTypeBinding = arrayTypeBinding.getElementType();
				if(elementTypeBinding instanceof DelegateBinding){
					delegateBinding = (DelegateBinding)elementTypeBinding;
				}
			}
			
			getDelegateFunctionString(delegateBinding, functionName, bfFunctionName, functionTextBuffer, newLine, needImports, currImports, currPkg );
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
					appendArgument(EXPARGNAME, EXPPARAMNAME, true, strbuf);
				}
			},functionTextBuffer, newLineDelimiter, functionNameBuffer);
		}
	}
	
	private static void createCallbackFunctionArguments(Expression serviceExpr,StringBuffer strbuf, List currImports,
			String currFilePkg, List<String> needImports, String newLineDelimiter) {
		ITypeBinding serviceExprBinding = serviceExpr.resolveTypeBinding();
		if (serviceExprBinding != null && serviceExprBinding.getKind() == ITypeBinding.FUNCTION_BINDING) {
			Set argNames = new HashSet();
			Set insertedImports = new HashSet();
			
			FunctionBinding svrFuncBinding = (FunctionBinding) serviceExprBinding;
			List params = svrFuncBinding.getParameters();
			boolean isFirst = true;
			for (Iterator it = params.iterator(); it.hasNext();) {
				Object obj = it.next();
				if (obj instanceof FunctionParameterBinding) {
					FunctionParameterBinding paramBinding = (FunctionParameterBinding) obj;
					if (paramBinding.isOutput() || paramBinding.isInputOutput()) {
						String argName = paramBinding.getCaseSensitiveName();
						argNames.add(argName);
						
						appendArguments(argName, paramBinding.getType(), isFirst, strbuf, currImports,currFilePkg, insertedImports, needImports, newLineDelimiter);
						isFirst = false;
					}
				}
			}

			ITypeBinding retType = svrFuncBinding.getReturnType();
			if (Binding.isValidBinding(retType)) {
				// get the unique argument name for the return argument
				int n = 1;
				String retArgNm = AddFunctionProposal.RETARGNAME;
				while (argNames.contains(retArgNm)) {
					retArgNm = AddFunctionProposal.RETARGNAME + Integer.toString(n);
					n++;
				}
				appendArguments(retArgNm, retType, isFirst, strbuf, currImports, currFilePkg,  insertedImports, needImports, newLineDelimiter);
				isFirst = false;
			}
		}
	}
	
	private static void appendArguments(String argName, ITypeBinding paramTypeBinding, boolean isFirst, StringBuffer strbuf,
			List currImports, String currFilePkg, Set insertedImports, List<String>needImport, String newLineDelimiter) {
		
		StringBuffer strBufTypeName = new StringBuffer();
		ExtractInterfaceConfiguration.getSimpleTypeString(paramTypeBinding,strBufTypeName);
		appendArgument(argName, strBufTypeName.toString(), isFirst, strbuf);

		if (paramTypeBinding.getBaseType().isPartBinding()) {
			String qualifier = ExtractInterfaceConfiguration.getReferenceTypeParamQualifier(paramTypeBinding,currFilePkg);
			
			if (!qualifier.equalsIgnoreCase(currFilePkg)) {
				// build the import string
				String strImport = qualifier + (qualifier.length() > 0 ? "." : "");
				strImport += paramTypeBinding.getBaseType().getCaseSensitiveName();

				if(!isAlreadyImported(currImports, qualifier, strImport) && !insertedImports.contains(strImport) && strImport.length() > 0){
					needImport.add(strImport);
				}
			}
		}
	}
	
	private interface ICallbackArgumentPrinter{
		public void printArgs(Expression serviceExpr, StringBuffer strbuf);
	}

	private static Node getContainerFunction(Node callstmt) {
		Node parent = callstmt.getParent();
		while (parent != null && !(parent instanceof NestedFunction)) {
			parent = getContainerFunction(parent);
		}
		
		return parent;
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
	
	private static void getDelegateFunctionString(DelegateBinding delegateBinding, String functionName, StringBuffer bfFunctionName, StringBuffer delegateFunctionStr,
			String newLineDelimeter, List<String> needImports, List<String> currImiports, String currFilePkg){
		
		delegateFunctionStr.append(IEGLConstants.KEYWORD_FUNCTION);
		delegateFunctionStr.append(" ");
		delegateFunctionStr.append(functionName);
		delegateFunctionStr.append("(");
		
		List parameters = delegateBinding.getParemeters();
		for (int parameterIndex = 0; parameterIndex < parameters.size(); parameterIndex++) {
			Object object = parameters.get(parameterIndex);
			if(object instanceof FunctionParameterBinding){
				FunctionParameterBinding functionParameterBinding = (FunctionParameterBinding)object;
				String name = functionParameterBinding.getCaseSensitiveName();
				ITypeBinding paraTypeBinding = functionParameterBinding.getType();
				String type = paraTypeBinding.getCaseSensitiveName();
				String qualifier = ExtractInterfaceConfiguration.getReferenceTypeParamQualifier(paraTypeBinding,currFilePkg);
				if (!qualifier.equalsIgnoreCase(currFilePkg)) {
					// build the import string
					String strImport = qualifier + (qualifier.length() > 0 ? "." : "");
					strImport += paraTypeBinding.getBaseType().getCaseSensitiveName();
					if(!isAlreadyImported(currImiports, qualifier, strImport) && !needImports.contains(strImport) && strImport.contains(".")){
						needImports.add(strImport);
					}
				}
				
				String decorate = null;
				if(functionParameterBinding.isInput()){
					decorate = IEGLConstants.KEYWORD_IN;
				}else if(functionParameterBinding.isOutput()){
					decorate = IEGLConstants.KEYWORD_OUT;
				}else if(functionParameterBinding.isInputOutput()){
					decorate = IEGLConstants.KEYWORD_INOUT;
				}
				delegateFunctionStr.append(name).append(" ").append(type);
				if(decorate != null){
					delegateFunctionStr.append(" ").append(decorate);
				}
				
				if(parameterIndex+1 < parameters.size()){
					delegateFunctionStr.append(", ");
				}
			}
		}
		
		delegateFunctionStr.append(") ");
		
		//returns
		ITypeBinding returnsTypeBinding = delegateBinding.getReturnType();
		if(returnsTypeBinding != null){
			String qualifier = ExtractInterfaceConfiguration.getReferenceTypeParamQualifier(returnsTypeBinding,currFilePkg);
			if (!qualifier.equalsIgnoreCase(currFilePkg)) {
				// build the import string
				String strImport = qualifier + (qualifier.length() > 0 ? "." : "");
				strImport += returnsTypeBinding.getBaseType().getCaseSensitiveName();
				if(!isAlreadyImported(currImiports, qualifier, strImport) && !needImports.contains(strImport) && strImport.contains(".")){
					needImports.add(strImport);
				}
			}
			String type = returnsTypeBinding.getCaseSensitiveName();
			String nullable = null;
			if(returnsTypeBinding.isNullable()){
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
	
}
