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

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;

import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.ide.core.ast.rewrite.ASTRewrite;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.rui.internal.Activator;
import org.eclipse.edt.mof.utils.NameUtile;

public class EGLContainerUpdateStrategy {
	
	private static final String CHILDREN_PROPERTY_NAME = NameUtile.getAsName("children");
	private int theCharactersAdded = 0;
	
	private class ChildrenArrayVisitor extends DefaultASTVisitor{
		private String insertText;
		private int index;

		public ChildrenArrayVisitor(String insertText, int index){
			this.insertText = insertText;
			this.index = index;
		}
		
		public boolean visit(final Assignment assignment){
			assignment.getRightHandSide().accept(new DefaultASTVisitor(){
				public boolean visit(ArrayLiteral array){
					try{
						if(array.getExpressions().size() == 0){
							String childrenString = CHILDREN_PROPERTY_NAME + " = [ " + insertText + " ]";
							ASTRewrite rewrite = ASTRewrite.create(fileAST);
							rewrite.setText(assignment, childrenString);
							rewrite.rewriteAST(currentDocument).apply(currentDocument);
							theCharactersAdded =  childrenString.length() - assignment.getLength();
						}else{
							Node insertNode;
							String nodeText;
							if(index < array.getExpressions().size()){
								// insert into beginning or middle
								insertNode = (Node)array.getExpressions().get(index);
								nodeText = currentDocument.get(insertNode.getOffset(), insertNode.getLength());
								nodeText = insertText + ", " + nodeText;	
							}else{
								// insert at end
								insertNode = (Node)array.getExpressions().get(index - 1);
								nodeText = currentDocument.get(insertNode.getOffset(), insertNode.getLength());
								nodeText = nodeText + ", " + insertText;
							}														
							ASTRewrite rewrite = ASTRewrite.create(fileAST);
							rewrite.setText(insertNode, nodeText);
							rewrite.rewriteAST(currentDocument).apply(currentDocument);
							theCharactersAdded = insertText.length() + 2;
						}
					}catch(BadLocationException e){
						Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Container Update: Error updating Array Literal of container", e));
					}
					return false;
				}
			});
			return false;
		}			
	}
	
	private org.eclipse.edt.compiler.core.ast.File fileAST;
	private Node containerNode;
	private IEGLDocument currentDocument;
	
	public EGLContainerUpdateStrategy(Node containerNode, IEGLDocument document){
		this.containerNode = containerNode;
		this.currentDocument = document;
		this.fileAST = document.getNewModelEGLFile();	
	}
	
	public int updateContainer(final String insertText, final int containerIndex){
		if(containerNode != null){
			containerNode.accept(new DefaultASTVisitor(){
				public boolean visit(final NewExpression newExpression){
					// This widget is anonymous
					try{
						if(newExpression.hasSettingsBlock()){
							SettingsBlock settingsBlockOpt = newExpression.getSettingsBlock();
							AssignmentLocator assignmentLocator = new AssignmentLocator(NameUtile.getAsName(CHILDREN_PROPERTY_NAME));
							settingsBlockOpt.accept(assignmentLocator);
							Assignment setting = assignmentLocator.getAssignment();
							if(setting != null){
								ChildrenArrayVisitor visitor = new ChildrenArrayVisitor(insertText, containerIndex);
								setting.accept(visitor);
							}else{
								// Add the setting to the settings block
								ASTRewrite rewrite = ASTRewrite.create(fileAST);
								rewrite.addSimpleSetting(newExpression, CHILDREN_PROPERTY_NAME, "[ " + insertText + " ]");
								rewrite.rewriteAST(currentDocument).apply(currentDocument);
								theCharactersAdded = CHILDREN_PROPERTY_NAME.length() + insertText.length() + 11;
							}
						}else{
							// Add the settings block and the setting
							ASTRewrite rewrite = ASTRewrite.create(fileAST);
							rewrite.addSimpleSetting(newExpression, CHILDREN_PROPERTY_NAME, "[ " + insertText + " ]");
							rewrite.rewriteAST(currentDocument).apply(currentDocument);
							theCharactersAdded = CHILDREN_PROPERTY_NAME.length() + insertText.length() + 10;
						}
					}catch(BadLocationException e){
						Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Container Update: Error updating New Expression container", e));
					}
					return false;
				}
				public boolean visit(final SimpleName simpleName){
					// The widget has been declared as a top level field - find the field and update it
					Node parentNode = simpleName.getParent();
					if(parentNode != null){
						parentNode.accept(new DefaultASTVisitor(){
							public boolean visit(ClassDataDeclaration classDataDeclaration){
								try{
									if(classDataDeclaration.hasSettingsBlock()){
										SettingsBlock settingsBlockOpt = classDataDeclaration.getSettingsBlockOpt();
										AssignmentLocator assignmentLocator = new AssignmentLocator(CHILDREN_PROPERTY_NAME);
										settingsBlockOpt.accept(assignmentLocator);
										Assignment setting = assignmentLocator.getAssignment();
										if(setting != null){
											ChildrenArrayVisitor visitor = new ChildrenArrayVisitor(insertText, containerIndex);
											setting.accept(visitor);
										}else{
											// Add the setting to the settings block
											ASTRewrite rewrite = ASTRewrite.create(fileAST);
											rewrite.addSimpleSetting(classDataDeclaration, CHILDREN_PROPERTY_NAME, "[ " + insertText + " ]");
											rewrite.rewriteAST(currentDocument).apply(currentDocument);
											theCharactersAdded = CHILDREN_PROPERTY_NAME.length() + insertText.length() + 11;
										}
									}else{
										// Add the settings block and the setting
										ASTRewrite rewrite = ASTRewrite.create(fileAST);
										rewrite.addSimpleSetting(classDataDeclaration, CHILDREN_PROPERTY_NAME, "[ " + insertText + " ]");
										rewrite.rewriteAST(currentDocument).apply(currentDocument);
										theCharactersAdded = CHILDREN_PROPERTY_NAME.length() + insertText.length() + 10;
									}
								}catch(BadLocationException e){
									Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Container Update: Error updating Class Field container", e));
								}
								return false;
							}
							public boolean visit(FunctionDataDeclaration functionDataDeclaration){
								try{
									if(functionDataDeclaration.hasSettingsBlock()){
										SettingsBlock settingsBlockOpt = functionDataDeclaration.getSettingsBlockOpt();
										AssignmentLocator assignmentLocator = new AssignmentLocator(CHILDREN_PROPERTY_NAME);
										settingsBlockOpt.accept(assignmentLocator);
										Assignment setting = assignmentLocator.getAssignment();
										if(setting != null){
											ChildrenArrayVisitor visitor = new ChildrenArrayVisitor(insertText, containerIndex);
											setting.accept(visitor);
										}else{
											// Add the setting to the settings block
											ASTRewrite rewrite = ASTRewrite.create(fileAST);
											rewrite.addSimpleSetting(functionDataDeclaration, CHILDREN_PROPERTY_NAME, "[ " + insertText + " ]");
											rewrite.rewriteAST(currentDocument).apply(currentDocument);
											theCharactersAdded = CHILDREN_PROPERTY_NAME.length() + insertText.length() + 11;
										}
									}else{
										// Add the settings block and the setting
										ASTRewrite rewrite = ASTRewrite.create(fileAST);
										rewrite.addSimpleSetting(functionDataDeclaration, CHILDREN_PROPERTY_NAME, "[ " + insertText + " ]");
										rewrite.rewriteAST(currentDocument).apply(currentDocument);
										theCharactersAdded = CHILDREN_PROPERTY_NAME.length() + insertText.length() + 10;
									}
								}catch(BadLocationException e){
									Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Container Update: Error updating Function Field container", e));
								}
								return false;
							}
						});
					}
					return false;
				}					
			});
		}
		return theCharactersAdded;
	}
}
