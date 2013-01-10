/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
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

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.core.ast.Type;
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
public class SetEventValueOperation {

	private IEGLDocument currentDocument;
	private IFile currentFile;

	public SetEventValueOperation(IEGLDocument currentDocument, IFile currentFile){
		this.currentDocument = currentDocument;		
		this.currentFile = currentFile;
	}
	public void setEventValue(final String eventName, final String eventValue, final int widgetOffset, final int widgetLength){
		
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
										// Found a setting to replace
										ASTRewrite rewrite = ASTRewrite.create(fileAST);
										rewrite.setText(setting, eventName + " ::= " + eventValue);
										rewrite.rewriteAST(currentDocument).apply(currentDocument);
									}else{
										addEvent(eventName, eventValue, fileAST, settingsBlockOpt);
									}
								}else{
									// Add the settings block and the setting
									ASTRewrite rewrite = ASTRewrite.create(fileAST);
									rewrite.setText(newExpression, newExpression.getCanonicalString() + "{ " + eventName + " ::= " + eventValue + " }");
									rewrite.rewriteAST(currentDocument).apply(currentDocument);
								}
							}catch(BadLocationException e){
								Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Set Event: Error processing new expression", e));
							}
							return false;
						}
						public boolean visit(final SimpleName simpleName){
							Node parentNode = simpleName.getParent();
							if(parentNode != null){
								parentNode.accept(new DefaultASTVisitor(){
									public boolean visit(ClassDataDeclaration classDataDeclaration){
										try{
											SettingsBlock fieldSettingsBlock = null;
											SettingsBlock initializerSettingsBlock = null;
											NewExpression initializer = null;
											
											// Find this class fields settings block and its initializer if it has one
											// If it has an initializer, get its settings block as well
											if(classDataDeclaration.hasSettingsBlock()){
												fieldSettingsBlock = classDataDeclaration.getSettingsBlockOpt();
											}
											if(classDataDeclaration.hasInitializer()){
												Expression theInitializer = classDataDeclaration.getInitializer();
												InitializerLocator locator = new InitializerLocator();
												theInitializer.accept(locator);
												initializer = locator.getInitializer();
												if(initializer != null && initializer.hasSettingsBlock()){
													initializerSettingsBlock = initializer.getSettingsBlock();
												}
											}
											
											// Attempt to locate the setting in each of the settings block we found previously
											// If we find it in one block, we do not look in the other
											Assignment setting = null;
											if(fieldSettingsBlock != null){
												AssignmentLocator assignmentLocator = new AssignmentLocator(NameUtile.getAsName(eventName));
												fieldSettingsBlock.accept(assignmentLocator);
												setting = assignmentLocator.getAssignment();
											}
											if(setting == null && initializerSettingsBlock != null){
												AssignmentLocator assignmentLocator = new AssignmentLocator(NameUtile.getAsName(eventName));
												initializerSettingsBlock.accept(assignmentLocator);
												setting = assignmentLocator.getAssignment();
											}
											
											if(setting != null){
												// Found a setting to replace
												ASTRewrite rewrite = ASTRewrite.create(fileAST);
												rewrite.setText(setting, eventName + " ::= " + eventValue);
												rewrite.rewriteAST(currentDocument).apply(currentDocument);
											}else{
												// See if we have a settings block to add the setting too
												if(fieldSettingsBlock != null || initializerSettingsBlock != null){
													// try to add to the initializer block first
													if(initializerSettingsBlock != null){
														// Add the setting to the initialzer settings block
														addEvent(eventName, eventValue,	fileAST, initializerSettingsBlock);
													}else{
														// Add the setting to the class field settings block
														addEvent(eventName, eventValue,	fileAST, fieldSettingsBlock);
													}
												}else{
													// We have to create a settings block and add the setting
													// Try to add the settings block to an initializer first
													if(initializer != null){
														// Add the settings block and the setting
														ASTRewrite rewrite = ASTRewrite.create(fileAST);
														Type type = initializer.getType();
														rewrite.setText(type, type.getCanonicalName() + "{ " + eventName + " ::= " + eventValue + " }");
														rewrite.rewriteAST(currentDocument).apply(currentDocument);
													}else{
														// Add the settings block and the setting
														ASTRewrite rewrite = ASTRewrite.create(fileAST);
														Type type = classDataDeclaration.getType();
														rewrite.setText(type, type.getCanonicalName() + "{ " + eventName + " ::= " + eventValue + " }");
														rewrite.rewriteAST(currentDocument).apply(currentDocument);
													}
												}
											}
										}catch(BadLocationException e){
											Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Set Event: Error processing class field expression", e));
										}
										return false;
									}
									
									public boolean visit(FunctionDataDeclaration functionDataDeclaration){
										try{
											SettingsBlock fieldSettingsBlock = null;
											SettingsBlock initializerSettingsBlock = null;
											NewExpression initializer = null;
											
											// Find this class fields settings block and its initializer if it has one
											// If it has an initializer, get its settings block as well
											if(functionDataDeclaration.hasSettingsBlock()){
												fieldSettingsBlock = functionDataDeclaration.getSettingsBlockOpt();
											}
											if(functionDataDeclaration.hasInitializer()){
												Expression theInitializer = functionDataDeclaration.getInitializer();
												InitializerLocator locator = new InitializerLocator();
												theInitializer.accept(locator);
												initializer = locator.getInitializer();
												if(initializer != null && initializer.hasSettingsBlock()){
													initializerSettingsBlock = initializer.getSettingsBlock();
												}
											}
											
											// Attempt to locate the setting in each of the settings block we found previously
											// If we find it in one block, we do not look in the other
											Assignment setting = null;
											if(fieldSettingsBlock != null){
												AssignmentLocator assignmentLocator = new AssignmentLocator(NameUtile.getAsName(eventName));
												fieldSettingsBlock.accept(assignmentLocator);
												setting = assignmentLocator.getAssignment();
											}
											if(setting == null && initializerSettingsBlock != null){
												AssignmentLocator assignmentLocator = new AssignmentLocator(NameUtile.getAsName(eventName));
												initializerSettingsBlock.accept(assignmentLocator);
												setting = assignmentLocator.getAssignment();
											}
											
											if(setting != null){
												// Found a setting to replace
												ASTRewrite rewrite = ASTRewrite.create(fileAST);
												rewrite.setText(setting, eventName + " ::= " + eventValue);
												rewrite.rewriteAST(currentDocument).apply(currentDocument);
											}else{
												// See if we have a settings block to add the setting too
												if(fieldSettingsBlock != null || initializerSettingsBlock != null){
													// try to add to the initializer block first
													if(initializerSettingsBlock != null){
														// Add the setting to the initialzer settings block
														addEvent(eventName, eventValue,	fileAST, initializerSettingsBlock);
													}else{
														// Add the setting to the class field settings block
														addEvent(eventName, eventValue,	fileAST, fieldSettingsBlock);
													}
												}else{
													// We have to create a settings block and add the setting
													// Try to add the settings block to an initializer first
													if(initializer != null){
														// Add the settings block and the setting
														ASTRewrite rewrite = ASTRewrite.create(fileAST);
														Type type = initializer.getType();
														rewrite.setText(type, type.getCanonicalName() + "{ " + eventName + " ::= " + eventValue + " }");
														rewrite.rewriteAST(currentDocument).apply(currentDocument);
													}else{
														// Add the settings block and the setting
														ASTRewrite rewrite = ASTRewrite.create(fileAST);
														Type type = functionDataDeclaration.getType();
														rewrite.setText(type, type.getCanonicalName() + "{ " + eventName + " ::= " + eventValue + " }");
														rewrite.rewriteAST(currentDocument).apply(currentDocument);
													}
												}
											}
										}catch(BadLocationException e){
											Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Set Event: Error processing function field expression", e));
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
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Set Event: Error setting event", e));
			}finally{
				sharedWorkingCopy.destroy();					
			}
		}catch(EGLModelException e){
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Set Event: Error creating working copy", e));
		}
	}
	private void addEvent(final String eventName, final String eventValue,
			final org.eclipse.edt.compiler.core.ast.File fileAST,
			SettingsBlock settingsBlockOpt) throws BadLocationException {
		List settings = settingsBlockOpt.getSettings();
		if(settings.size() > 0){
			Node lastSetting = (Node)settings.get(settings.size() - 1);
			currentDocument.replace(lastSetting.getOffset() + lastSetting.getLength(), 0,  ", " + eventName + " ::= " + eventValue);
		}else{
			ASTRewrite rewrite = ASTRewrite.create(fileAST);
			rewrite.setText(settingsBlockOpt, "{ " + eventName + " ::= " + eventValue + " }");
			rewrite.rewriteAST(currentDocument).apply(currentDocument);
		}
	}
}
