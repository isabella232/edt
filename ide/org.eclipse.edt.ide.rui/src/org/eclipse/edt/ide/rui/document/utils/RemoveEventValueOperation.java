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
package org.eclipse.edt.ide.rui.document.utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.ide.core.ast.rewrite.ASTRewrite;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.rui.internal.Activator;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.mof.utils.NameUtile;
import org.eclipse.jface.text.BadLocationException;

/**
 * Insert Widget Reference and update the Z order of the declared widgets
 */
public class RemoveEventValueOperation {

	private IEGLDocument currentDocument;
	private IFile currentFile;

	public RemoveEventValueOperation(IEGLDocument currentDocument, IFile currentFile){
		this.currentDocument = currentDocument;		
		this.currentFile = currentFile;
	}
	public void removeEventValue(final String eventName, final int widgetOffset, final int widgetLength){
		
		try{
			IEGLFile modelFile = (IEGLFile)EGLCore.create(currentFile);
			IEGLFile sharedWorkingCopy = (IEGLFile)modelFile.getSharedWorkingCopy(null, EGLUI.getBufferFactory(), null);
			sharedWorkingCopy.open(null);
			sharedWorkingCopy.reconcile(false, null);
		
			try{
				final org.eclipse.edt.compiler.core.ast.File fileAST = currentDocument.getNewModelEGLFile();
				final Node widgetReference = DocumentUtil.getWidgetNode(currentDocument,widgetOffset, widgetLength);
				//TODO: How do we want to handle invalid offsets?
				if(widgetReference != null){
					widgetReference.accept(new DefaultASTVisitor(){
						public boolean visit(final NewExpression newExpression){
							// This widget is anonymous
							try{
								if(newExpression.hasSettingsBlock()){
									SettingsBlock settingsBlockOpt = newExpression.getSettingsBlock();
									AssignmentLocator assignmentLocator = new AssignmentLocator(NameUtile.getAsName(eventName));
									settingsBlockOpt.accept(assignmentLocator);
									Assignment setting = assignmentLocator.getAssignment();
									if(setting != null){
										ASTRewrite rewrite = ASTRewrite.create(fileAST);
										rewrite.removeSetting(newExpression, setting);
										rewrite.rewriteAST(currentDocument).apply(currentDocument);
									}
								}
							}catch(BadLocationException e){
								Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Remove Event: Error processing new expression", e));
							}
							return false;
						}
						public boolean visit(final SimpleName simpleName){
							Node parentNode = simpleName.getParent();
							if(parentNode != null){
								parentNode.accept(new DefaultASTVisitor(){
									public boolean visit(ClassDataDeclaration classDataDeclaration){
										try{
											if(classDataDeclaration.hasSettingsBlock()){
												SettingsBlock settingsBlockOpt = classDataDeclaration.getSettingsBlockOpt();
												AssignmentLocator assignmentLocator = new AssignmentLocator(NameUtile.getAsName(eventName));
												settingsBlockOpt.accept(assignmentLocator);
												Assignment setting = assignmentLocator.getAssignment();
												if(setting != null){
													ASTRewrite rewrite = ASTRewrite.create(fileAST);
													rewrite.removeSetting(classDataDeclaration, setting);
													rewrite.rewriteAST(currentDocument).apply(currentDocument);
												}
											}
										}catch(BadLocationException e){
											Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Remove Event: Error processing class field expression", e));
										}
										return false;
									}
									public boolean visit(FunctionDataDeclaration functionDataDeclaration){
										try{
											if(functionDataDeclaration.hasSettingsBlock()){
												SettingsBlock settingsBlockOpt = functionDataDeclaration.getSettingsBlockOpt();
												AssignmentLocator assignmentLocator = new AssignmentLocator(NameUtile.getAsName(eventName));
												settingsBlockOpt.accept(assignmentLocator);
												Assignment setting = assignmentLocator.getAssignment();
												if(setting != null){
													ASTRewrite rewrite = ASTRewrite.create(fileAST);
													rewrite.removeSetting(functionDataDeclaration, setting);
													rewrite.rewriteAST(currentDocument).apply(currentDocument);
												}
											}
										}catch(BadLocationException e){
											Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Remove Event: Error processing function field expression", e));
										}
										return false;
									}
								});
							}
							return false;
						}	
					});					
				}
			}catch(Exception e){
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Remove Event: Error removing event", e));
			}finally{
				sharedWorkingCopy.destroy();					
			}
		}catch(EGLModelException e){
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Remove Event: Error creating working copy", e));
		}
	}
}
