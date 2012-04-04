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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;

import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.ide.core.ast.rewrite.ASTRewrite;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.rui.internal.Activator;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLFile;

/**
 * Insert Widget Reference and update the Z order of the declared widgets
 */
public class SetPropertyValueOperation {

	private IEGLDocument currentDocument;
	private IFile currentFile;
	private int charactersChanged = 0;
	private List imports = new ArrayList();

	public SetPropertyValueOperation(IEGLDocument currentDocument, IFile currentFile){
		this.currentDocument = currentDocument;		
		this.currentFile = currentFile;
	}
	public int setPropertyValue(final String propertyName, final String propertyValueTemplate, final String propertyType, final int widgetOffset, final int widgetLength){
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
					//handle ${typeName:<fullyQualifiedTypeName>}
					final String propertyValue = DocumentUtil.handleTypeNameVaraibles(currentFile, currentDocument, propertyValueTemplate, imports);

					widgetReference.accept(new DefaultASTVisitor(){
						public boolean visit(final NewExpression newExpression){
							// This widget is anonymous
							try{
								if(newExpression.hasSettingsBlock()){
									SettingsBlock settingsBlockOpt = newExpression.getSettingsBlock();
									AssignmentLocator assignmentLocator = new AssignmentLocator(InternUtil.intern(propertyName));
									settingsBlockOpt.accept(assignmentLocator);
									Assignment setting = assignmentLocator.getAssignment();
									if(setting != null){
										// Found a setting to replace
										ASTRewrite rewrite = ASTRewrite.create(fileAST);
										rewrite.setText(setting, propertyName + " = " + propertyValue);
										rewrite.rewriteAST(currentDocument).apply(currentDocument);
										charactersChanged = propertyName.length() + propertyValue.length() + 3 - setting.getLength();
									}else{
										// Add the setting to the settings block
										ASTRewrite rewrite = ASTRewrite.create(fileAST);
										rewrite.addSimpleSetting(newExpression, propertyName, propertyValue);
										rewrite.rewriteAST(currentDocument).apply(currentDocument);
										charactersChanged = propertyName.length() + propertyValue.length() + 5;
									}
								}else{
									// Add the settings block and the setting
									ASTRewrite rewrite = ASTRewrite.create(fileAST);
									rewrite.addSimpleSetting(newExpression, propertyName, propertyValue);
									rewrite.rewriteAST(currentDocument).apply(currentDocument);
									charactersChanged = propertyName.length() + propertyValue.length() + 7;
								}
							}catch(BadLocationException e){
								Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Set Property: Error processing new expression", e));
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
												AssignmentLocator assignmentLocator = new AssignmentLocator(InternUtil.intern(propertyName));
												fieldSettingsBlock.accept(assignmentLocator);
												setting = assignmentLocator.getAssignment();
											}
											if(setting == null && initializerSettingsBlock != null){
												AssignmentLocator assignmentLocator = new AssignmentLocator(InternUtil.intern(propertyName));
												initializerSettingsBlock.accept(assignmentLocator);
												setting = assignmentLocator.getAssignment();
											}
											
											if(setting != null){
												// Found a setting to replace
												ASTRewrite rewrite = ASTRewrite.create(fileAST);
												rewrite.setText(setting, propertyName + " = " + propertyValue);
												rewrite.rewriteAST(currentDocument).apply(currentDocument);
												charactersChanged = propertyName.length() + propertyValue.length() + 3 - setting.getLength();
											}else{
												// See if we have a settings block to add the setting too
												if(fieldSettingsBlock != null || initializerSettingsBlock != null){
													// try to add to the initializer block first
													if(initializerSettingsBlock != null){
														// Add the setting to the initialzer settings block
														ASTRewrite rewrite = ASTRewrite.create(fileAST);
														rewrite.addSimpleSetting(initializer, propertyName, propertyValue);
														rewrite.rewriteAST(currentDocument).apply(currentDocument);
														charactersChanged = propertyName.length() + propertyValue.length() + 5;
													}else{
														// Add the setting to the class field settings block
														ASTRewrite rewrite = ASTRewrite.create(fileAST);
														rewrite.addSimpleSetting(classDataDeclaration, propertyName, propertyValue);
														rewrite.rewriteAST(currentDocument).apply(currentDocument);
														charactersChanged = propertyName.length() + propertyValue.length() + 7;
													}
												}else{
													// We have to create a settings block and add the setting
													// Try to add the settings block to an initializer first
													if(initializer != null){
														// Add the settings block and the setting
														ASTRewrite rewrite = ASTRewrite.create(fileAST);
														rewrite.addSimpleSetting(initializer, propertyName, propertyValue);
														rewrite.rewriteAST(currentDocument).apply(currentDocument);
														charactersChanged = propertyName.length() + propertyValue.length() + 5;
													}else{
														// Add the settings block and the setting
														ASTRewrite rewrite = ASTRewrite.create(fileAST);
														rewrite.addSimpleSetting(classDataDeclaration, propertyName, propertyValue);
														rewrite.rewriteAST(currentDocument).apply(currentDocument);
														charactersChanged = propertyName.length() + propertyValue.length() + 7;
													}
												}
											}
										}catch(BadLocationException e){
											Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Set Property: Error processing class field expression", e));
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
												AssignmentLocator assignmentLocator = new AssignmentLocator(InternUtil.intern(propertyName));
												fieldSettingsBlock.accept(assignmentLocator);
												setting = assignmentLocator.getAssignment();
											}
											if(setting == null && initializerSettingsBlock != null){
												AssignmentLocator assignmentLocator = new AssignmentLocator(InternUtil.intern(propertyName));
												initializerSettingsBlock.accept(assignmentLocator);
												setting = assignmentLocator.getAssignment();
											}
											
											if(setting != null){
												// Found a setting to replace
												ASTRewrite rewrite = ASTRewrite.create(fileAST);
												rewrite.setText(setting, propertyName + " = " + propertyValue);
												rewrite.rewriteAST(currentDocument).apply(currentDocument);
												charactersChanged = propertyName.length() + propertyValue.length() + 3 - setting.getLength();
											}else{
												// See if we have a settings block to add the setting too
												if(fieldSettingsBlock != null || initializerSettingsBlock != null){
													// try to add to the initializer block first
													if(initializerSettingsBlock != null){
														// Add the setting to the initialzer settings block
														ASTRewrite rewrite = ASTRewrite.create(fileAST);
														rewrite.addSimpleSetting(initializer, propertyName, propertyValue);
														rewrite.rewriteAST(currentDocument).apply(currentDocument);
														charactersChanged = propertyName.length() + propertyValue.length() + 5;
													}else{
														// Add the setting to the class field settings block
														ASTRewrite rewrite = ASTRewrite.create(fileAST);
														rewrite.addSimpleSetting(functionDataDeclaration, propertyName, propertyValue);
														rewrite.rewriteAST(currentDocument).apply(currentDocument);
														charactersChanged = propertyName.length() + propertyValue.length() + 7;
													}
												}else{
													// We have to create a settings block and add the setting
													// Try to add the settings block to an initializer first
													if(initializer != null){
														// Add the settings block and the setting
														ASTRewrite rewrite = ASTRewrite.create(fileAST);
														rewrite.addSimpleSetting(initializer, propertyName, propertyValue);
														rewrite.rewriteAST(currentDocument).apply(currentDocument);
														charactersChanged = propertyName.length() + propertyValue.length() + 5;
													}else{
														// Add the settings block and the setting
														ASTRewrite rewrite = ASTRewrite.create(fileAST);
														rewrite.addSimpleSetting(functionDataDeclaration, propertyName, propertyValue);
														rewrite.rewriteAST(currentDocument).apply(currentDocument);
														charactersChanged = propertyName.length() + propertyValue.length() + 7;
													}
												}
											}
										}catch(BadLocationException e){
											Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Set Property: Error processing function field expression", e));
										}
										return false;
									}
								});
							}
							return false;
						}
					});

					DocumentUtil.addQualifiedImports(currentFile, currentDocument, imports);
				}
			}catch(Exception e){
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Set Property: Error setting property", e));
			}finally{
				sharedWorkingCopy.destroy();					
			}
		}catch(EGLModelException e){
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Set Property: Error creating working copy", e));
		}
		return charactersChanged;
	}

}
