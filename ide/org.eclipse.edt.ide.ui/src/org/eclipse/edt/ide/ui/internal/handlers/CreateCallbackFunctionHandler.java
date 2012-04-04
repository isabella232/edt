/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.handlers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.FunctionBinding;
import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.CallbackTarget;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.ImportDeclaration;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.ide.core.internal.model.EGLFile;
import org.eclipse.edt.ide.core.internal.model.document.EGLDocument;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.edt.ide.ui.wizards.EGLFileConfiguration;
import org.eclipse.edt.ide.ui.wizards.ExtractInterfaceConfiguration;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.eclipse.ui.part.FileEditorInput;

public class CreateCallbackFunctionHandler extends EGLHandler {
	private ITextSelection fCurrSelection;
	private Part part;
	private EGLDocument document;
	private String fCurrPkg = "";
	private EGLEditor editor;
	
	private static String RETARGNAME = "retResult";
	private static String EXPARGNAME = "exp";
	private static String EXPPARAMNAME = "AnyException";		//this is a system part, implicit imports egl.core
	
	//serves as function pointer
	private interface ICallbackArgumentPrinter{
		public void printArgs(Expression serviceExpr, StringBuffer strbuf);
	}

	public void run() {
		update();
		
		//if in the run(), means the action is enabled, which means the cursor is inside a callStatement that has callback functions
		IFile currentFile = ((FileEditorInput)(editor.getEditorInput())).getFile();
		IEGLFile eglFile = (EGLFile)EGLCore.create(currentFile);
		
		final int[] importInsertionOffset = new int[]{0};
		final List currImports = new ArrayList();
		final File fileAST = document.getNewModelEGLFile();
		fileAST.accept(new DefaultASTVisitor(){
			public boolean visit(File file) {return true;}
			
			public boolean visit(org.eclipse.edt.compiler.core.ast.PackageDeclaration packageDeclaration) {
				fCurrPkg = packageDeclaration.getName().getCanonicalString();
				importInsertionOffset[0] = packageDeclaration.getOffset() + packageDeclaration.getLength();
				return true;
			}
			
			public boolean visit(org.eclipse.edt.compiler.core.ast.ImportDeclaration importDeclaration) {
				currImports.add(importDeclaration);
				importInsertionOffset[0] = importDeclaration.getOffset() + importDeclaration.getLength();
				return true;
			}			
		});		

		final String newLine = TextUtilities.getDefaultLineDelimiter(document);
		
		//bind the part 
		Part boundPart = EGLFileConfiguration.getBoundPart(eglFile, part.getName().getCanonicalName());
		boundPart.accept(new AbstractASTVisitor(){
			
			public boolean visit(CallStatement callStatement) {
				TextEdit compositeEdit = new MultiTextEdit();
				
				int cursorOffset = fCurrSelection.getOffset();
				int endingOffset = callStatement.getOffset() + callStatement.getLength();
				//only care about the call statement that the user currently has the cursor on
				if(callStatement.getOffset()<= cursorOffset && cursorOffset<=endingOffset){
				
					Node ContainerFunction = getContainerFunction(callStatement);					
					int insertionOffset = ContainerFunction != null ? ContainerFunction.getOffset() + ContainerFunction.getLength() : 0;
					
					Expression serviceFunctionExpr = callStatement.getInvocationTarget();
					
					Expression callbackExpr = null, errCallbackExpr =null;
					
					CallbackTarget callbackTgt = null;
					CallbackTarget errCallbackTgt = null;
					if (callStatement.getCallSynchronizationValues() != null) {
						callbackTgt = callStatement.getCallSynchronizationValues().getReturnTo();
						errCallbackTgt = callStatement.getCallSynchronizationValues().getOnException();
					}

					if(callbackTgt != null)
						callbackExpr = callbackTgt.getExpression();				
					if(errCallbackTgt != null)
						errCallbackExpr = errCallbackTgt.getExpression();
					
					boolean bCallbackCreated = false;
					if(callbackExpr != null)
						bCallbackCreated = createCallbackFunction(compositeEdit, insertionOffset, serviceFunctionExpr, callbackExpr, newLine, currImports, fCurrPkg, importInsertionOffset[0]);				
					
					boolean bErrCallbackCreated = false;
					if(errCallbackExpr != null)
						bErrCallbackCreated = createErrCallbackFunction(compositeEdit, insertionOffset, serviceFunctionExpr, errCallbackExpr, newLine);
					
					if(!bCallbackCreated && !bErrCallbackCreated){
						//if none of the call back is created, means the function already existed
						String errMsg = "";
						if(callbackExpr != null && errCallbackExpr==null){
							errMsg = UINlsStrings.bind("A callback function with name ''{0}'' already existed.", callbackExpr.getCanonicalString());
						}
						else if(errCallbackExpr != null && callbackExpr==null){
							errMsg = UINlsStrings.bind("An error callback function with name ''{0}'' already existed.", errCallbackExpr.getCanonicalString());
						}
						else if(callbackExpr != null  && errCallbackExpr != null){
							errMsg = UINlsStrings.bind("A callback function with name ''{0}'' and an error callback function with name ''{1}'' already existed.", 
										callbackExpr.getCanonicalString(),
										errCallbackExpr.getCanonicalString());
						}
						MessageDialog.openInformation(editor.getSite().getShell(), "Create callback functions", errMsg);
					}
					else{
						try {
							
							if(compositeEdit.hasChildren()){
								compositeEdit.apply(document);
								String statusBarMsg = "";
								
								if(bCallbackCreated && bErrCallbackCreated)
									statusBarMsg = UINlsStrings.bind("Callback function ''{0}'' and error callback function ''{1}'' are created", 
													callbackExpr.getCanonicalString(), errCallbackExpr.getCanonicalString());
								else if(bCallbackCreated)
									statusBarMsg = UINlsStrings.bind("Callback function ''{0}'' created", callbackExpr.getCanonicalString());
								else if(bErrCallbackCreated)
									statusBarMsg = UINlsStrings.bind("Error callback function ''{0}'' created", errCallbackExpr.getCanonicalString());
								
								setStatusBarMessage(statusBarMsg);	
							}
						} catch (MalformedTreeException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (BadLocationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}						
					}
					
				}
				return false;
			}
		});
		
	}

	public void update() {
		//disable this action by default
		setEnabled(false);
				
		fCurrSelection = (ITextSelection)(editor.getSelectionProvider().getSelection());
		IDocument doc = editor.getDocumentProvider().getDocument(editor.getEditorInput());
		if(doc instanceof EGLDocument){
			document = (EGLDocument) doc;
			part = document.getNewModelPartAtOffset(fCurrSelection.getOffset());		
			if(part != null){
				part.accept(new AbstractASTVisitor(){
					public boolean visit(CallStatement callStatement) {
						int cursorOffset = fCurrSelection.getOffset();
						int endingOffset = callStatement.getOffset() + callStatement.getLength();
						//only care about the call statement that the user currently has the cursor on
						if(callStatement.getOffset()<= cursorOffset && cursorOffset<=endingOffset){
							if (callStatement.getCallSynchronizationValues() != null) {
								if(callStatement.getCallSynchronizationValues().getReturnTo() != null || callStatement.getCallSynchronizationValues().getOnException() != null){
									//only enable this action, if cursor is in call statement and has callback or error call back
									setEnabled(true);
								}
							}
						}					
						return false;
					};
				});
			}
		}
	}
	
	private Node getContainerFunction(Node callstmt){
		Node parent = callstmt.getParent();
		while(parent != null && !(parent instanceof NestedFunction)){
			parent = getContainerFunction(parent);
		}
		return parent;
	}

	/**
	 * create the function string
	 * @param serviceExpr
	 * @param callbckFuncName
	 * @param callbackArgPrinter
	 * @return
	 */
	private String getCallbackFunctionString(Expression serviceExpr, Name callbckFuncName, ICallbackArgumentPrinter callbackArgPrinter, String newLine){
		StringBuffer callBackFuncStr = new StringBuffer();
		
		callBackFuncStr.append(newLine);
		callBackFuncStr.append(newLine);
		callBackFuncStr.append("\t");		//indent
		callBackFuncStr.append(IEGLConstants.KEYWORD_FUNCTION);
		callBackFuncStr.append(" ");
		
		String callbackFuncName = callbckFuncName.getCaseSensitiveIdentifier();
		callBackFuncStr.append(callbackFuncName);
		callBackFuncStr.append('(');
		
		callbackArgPrinter.printArgs(serviceExpr, callBackFuncStr);
		
		callBackFuncStr.append(')');
		callBackFuncStr.append(newLine);
		callBackFuncStr.append("\t");		//indent
		callBackFuncStr.append(IEGLConstants.KEYWORD_END);
		return callBackFuncStr.toString();
	}
				
	private boolean createErrCallbackFunction(TextEdit textEdit,
												int insertOffset,
												Expression serviceExpr,
												Expression errCallbackExpr,
												String newLine) 
	{
		boolean createdFunction = false;
		//try to resolve the error call back function name
		IDataBinding dataBinding = errCallbackExpr.resolveDataBinding();
		if(!Binding.isValidBinding(dataBinding)){
			if(errCallbackExpr.isName()){
				Name callbackExprName = (Name)errCallbackExpr;
				String funcStr = getCallbackFunctionString(serviceExpr, callbackExprName, new ICallbackArgumentPrinter(){
					public void printArgs(Expression serviceExpr, StringBuffer strbuf) {
						appendArgument(EXPARGNAME, EXPPARAMNAME, true, strbuf);								
					}							
				}, newLine);
				
				textEdit.addChild(new InsertEdit(insertOffset, funcStr));
				createdFunction = true;						
			}
		}
		else
			createdFunction = false;		//the error call back function already existed
		
		return createdFunction;
	}

	private boolean createCallbackFunction(final TextEdit textEdit,
										int insertOffset,
										Expression serviceExpr,
										Expression callbackExpr,
										final String newLine,
										final List currImports, 
										final String currFilePkg,
										final int newImportInsertOffset) 
	{
		boolean createdFunction = false;
		//try to resolve the call back function name
		IDataBinding dataBinding = callbackExpr.resolveDataBinding();								
		if(!Binding.isValidBinding(dataBinding)){  //only try to create a call back function when the function name doesn't exist					
			if(callbackExpr.isName()){
				
				Name callbackExprName = (Name)callbackExpr;
				String funcStr = getCallbackFunctionString(serviceExpr, callbackExprName, new ICallbackArgumentPrinter(){
					public void printArgs(Expression serviceExpr, StringBuffer strbuf) {								
						createCallbackFunctionArguments(serviceExpr, strbuf, newLine, currImports, currFilePkg, newImportInsertOffset, textEdit);						
					}
				}, newLine);
				
				textEdit.addChild(new InsertEdit(insertOffset, funcStr));
				createdFunction = true;
			}
		}
		else
			createdFunction = false;//the callback name is already existed
		
		return createdFunction;
	}
	
	private void createCallbackFunctionArguments(Expression serviceExpr, StringBuffer strbuf, String newLine,
												 List currImports, String currFilePkg, int newImportInsertOffset, TextEdit textEdit) {
		//now figure out all the parameters for the callback function from the service function
		ITypeBinding serviceExprBinding = serviceExpr.resolveTypeBinding();
		if(serviceExprBinding != null && serviceExprBinding.getKind() == ITypeBinding.FUNCTION_BINDING){
			Set argNames = new HashSet();
			Set insertedImports = new HashSet();
			
			FunctionBinding svrFuncBinding = (FunctionBinding)serviceExprBinding;
			List params = svrFuncBinding.getParameters();
			boolean isFirst = true;
			for(Iterator it = params.iterator(); it.hasNext();){
				Object obj = it.next();
				if(obj instanceof FunctionParameterBinding){
					FunctionParameterBinding paramBinding = (FunctionParameterBinding)obj;
																
					if(paramBinding.isOutput() || paramBinding.isInputOutput()){												
						String argName = paramBinding.getCaseSensitiveName();
						argNames.add(argName);
						
						appendArgument(argName, paramBinding.getType(), isFirst, strbuf, newLine,
								currImports, currFilePkg, newImportInsertOffset, textEdit, insertedImports);
						isFirst = false;
					}
				}
			}
				
			ITypeBinding retType = svrFuncBinding.getReturnType();
			if(Binding.isValidBinding(retType)){										
				//get the unique argument name for the return argument
				int n=1;
				String retArgNm = RETARGNAME;
				while(argNames.contains(retArgNm)){
					retArgNm = RETARGNAME + Integer.toString(n);
					n++;
				}
				appendArgument(retArgNm, retType, isFirst, strbuf, newLine,
						currImports, currFilePkg, newImportInsertOffset, textEdit, insertedImports);
				isFirst = false;
			}
		}
	}										
	
	private void appendArgument(String argName, ITypeBinding paramTypeBinding, boolean isFirst, StringBuffer strbuf, 
								String newLine, List currImports, String currFilePkg, int newImportInsertOffset, TextEdit textEdit, Set insertedImports){
		StringBuffer strBufTypeName = new StringBuffer();
		ExtractInterfaceConfiguration.getSimpleTypeString(paramTypeBinding, strBufTypeName);
		appendArgument(argName, strBufTypeName.toString(), isFirst, strbuf);
		
		if(paramTypeBinding.getBaseType().isPartBinding()){
			//we need to consider adding import if the qualifer differs from the current file package 
			//and if the part is not already in the current imports			
			String qualifier = ExtractInterfaceConfiguration.getReferenceTypeParamQualifier(paramTypeBinding, currFilePkg);
			if(!qualifier.equalsIgnoreCase(currFilePkg)){
				//build the import string
				String strImport = qualifier + (qualifier.length()>0 ? "." : "");
				strImport += paramTypeBinding.getBaseType().getCaseSensitiveName();
				
				boolean existed = false;
				for(Iterator it=currImports.iterator(); it.hasNext() && !existed;){
					ImportDeclaration importDecl = (ImportDeclaration)it.next();
					String simport = importDecl.getName().getCanonicalName();
					if(importDecl.isOnDemand()){
						if(simport.equalsIgnoreCase(qualifier))
							existed = true;
					}
					else{
						if(simport.equalsIgnoreCase(strImport))
							existed = true;
					}						
				}
				
				//test to see if we've already inserted this one
				if(!existed)
					existed = insertedImports.contains(strImport);
				
				if(!existed && strImport.length()>0){
					insertedImports.add(strImport);
					
					StringBuffer importStr = new StringBuffer();
					importStr.append(newLine);
					importStr.append(IEGLConstants.KEYWORD_IMPORT);
					importStr.append(' ');
					importStr.append(strImport);
					importStr.append(';');
					
					//let's add the import
					textEdit.addChild(new InsertEdit(newImportInsertOffset, importStr.toString()));					
				}
			}
			
		}
	}
	
	private void appendArgument(String argName, String typeName, boolean isFirst, StringBuffer strbuf){
		if(!isFirst)
			strbuf.append(", ");
		
		strbuf.append(argName);
		strbuf.append(' ');
		strbuf.append(typeName);
		strbuf.append(' ');
		strbuf.append(IEGLConstants.KEYWORD_IN);				
	}
	
	protected void setStatusBarMessage(String message) {
		IEditorActionBarContributor contributor= editor.getEditorSite().getActionBarContributor();
		if (contributor instanceof EditorActionBarContributor) {
			IStatusLineManager manager= ((EditorActionBarContributor) contributor).getActionBars().getStatusLineManager();
			manager.setMessage(message);
		}
	}
	
}
