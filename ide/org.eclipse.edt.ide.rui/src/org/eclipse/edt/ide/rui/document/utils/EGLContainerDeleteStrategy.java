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
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.rui.internal.Activator;
import org.eclipse.edt.mof.utils.NameUtile;

public class EGLContainerDeleteStrategy {
	
	private static final String CHILDREN_PROPERTY_NAME = NameUtile.getAsName("children");
	
	private class ChildrenArrayVisitor extends DefaultASTVisitor{
		private int index;
		private int theCharactersRemoved;
		private String deletedChildName;
		private List<String> deletedChildNames;
		private ChildrenRecursiveVisitor childrenRecursiveVisitor;
		
		public ChildrenArrayVisitor(int index){
			this.index = index;
			deletedChildNames = new ArrayList<String>();
		}
		
		public boolean visit(final Assignment assignment){
			assignment.getRightHandSide().accept(new DefaultASTVisitor(){
				public boolean visit(ArrayLiteral arrayLiteral){
					List expressions = arrayLiteral.getExpressions();
					Node referenceNode = (Node)expressions.get(index);
					int referenceOffset = referenceNode.getOffset();
					int referenceLength = referenceNode.getLength();
					if ( referenceNode instanceof SimpleName ) {
						deletedChildName = ((SimpleName)referenceNode).getCanonicalName();
					}else if( referenceNode instanceof NewExpression ){
						NewExpression newExpression = (NewExpression)referenceNode;
						if(newExpression.hasSettingsBlock()){
							SettingsBlock settingsBlockOpt = newExpression.getSettingsBlock();
							AssignmentLocator assignmentLocator = new AssignmentLocator(NameUtile.getAsName(CHILDREN_PROPERTY_NAME));
							settingsBlockOpt.accept(assignmentLocator);
							Assignment assignment = assignmentLocator.getAssignment();
							if(assignment != null){
								childrenRecursiveVisitor = new ChildrenRecursiveVisitor();
								assignment.getRightHandSide().accept(childrenRecursiveVisitor);
								StringBuffer sbDeletedChildNames = new StringBuffer();
								for(int i=0; i<deletedChildNames.size(); i++){
									sbDeletedChildNames.append(deletedChildNames.get(i)).append(DeleteWidgetDefinitionOperation.DELETE_WIDGET_NAME_SAPERATOR);
								}
								deletedChildName = sbDeletedChildNames.toString();
							}
						}
					}
					if(expressions.size() > 1){
						if(index == expressions.size() - 1){						
							// Removing the last element, need to remove a trailing comma from the previous element
							Node previousWidget = (Node)expressions.get(index - 1);
							try{
								theCharactersRemoved = (referenceOffset + referenceLength) - (previousWidget.getOffset() + previousWidget.getLength());
								currentDocument.replace(previousWidget.getOffset() + previousWidget.getLength(), theCharactersRemoved, "");
							}catch(BadLocationException e){
								Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Error deleting last widget reference", e));
							}
						}else{
							// Remove the element and a trailing comma
							Node nextWidget = (Node)expressions.get(index + 1);
							try{
								// Nodes do not offer a way to get their complete text, so we need to figure it out ourselves
								String nextWidgetText = currentDocument.get(referenceOffset, (nextWidget.getOffset() + nextWidget.getLength()) - referenceOffset);
								nextWidgetText = nextWidgetText.substring(nextWidgetText.indexOf(",", referenceLength) + 1, nextWidgetText.length()).trim();
								theCharactersRemoved = ((nextWidget.getOffset() + nextWidget.getLength()) - referenceOffset) - nextWidgetText.length();
								currentDocument.replace(referenceOffset, (nextWidget.getOffset() + nextWidget.getLength()) - referenceOffset, nextWidgetText);
							}catch(BadLocationException e){
								Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Error Deleting item from Container", e));
							}
						}
					}else{
						// Just remove the node
						try{
							theCharactersRemoved = referenceLength;
							currentDocument.replace(referenceOffset, referenceLength, "");
						}catch(BadLocationException e){
							Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Error Deleting item from Container", e));
						}
					}
					return false;
				}
			});
			return false;
		}	
	
		private class ChildrenRecursiveVisitor extends DefaultASTVisitor{
			public boolean visit(final ArrayLiteral arrayLiteral){
				List expressions = arrayLiteral.getExpressions();
				for(int i=0; i<expressions.size(); i++){
					Node childWidget = (Node)expressions.get(i);
					if ( childWidget instanceof SimpleName ) {
						deletedChildNames.add(((SimpleName)childWidget).getCanonicalName());
					}else if( childWidget instanceof NewExpression ){
						NewExpression newExpression = (NewExpression)childWidget;
						if(newExpression.hasSettingsBlock()){
							SettingsBlock settingsBlockOpt = newExpression.getSettingsBlock();
							AssignmentLocator assignmentLocator = new AssignmentLocator(NameUtile.getAsName(CHILDREN_PROPERTY_NAME));
							settingsBlockOpt.accept(assignmentLocator);
							Assignment assignment = assignmentLocator.getAssignment();
							if(assignment != null){
								assignment.getRightHandSide().accept(childrenRecursiveVisitor);
							}
						}
					}
				}
				return false;
			}
		}
	}
	
	private Node containerNode;
	private IEGLDocument currentDocument;
	private int totalCharactersRemoved;
	private String deletedChildName;
	
	public EGLContainerDeleteStrategy(Node containerNode, IEGLDocument document){
		this.containerNode = containerNode;
		this.currentDocument = document;
	}
	
	public int deleteFromContainer(final int containerIndex){
		if(containerNode != null){
			containerNode.accept(new DefaultASTVisitor(){
				public boolean visit(final NewExpression newExpression){
					// This widget is anonymous
					if(newExpression.hasSettingsBlock()){
						SettingsBlock settingsBlockOpt = newExpression.getSettingsBlock();
						AssignmentLocator assignmentLocator = new AssignmentLocator(NameUtile.getAsName(CHILDREN_PROPERTY_NAME));
						settingsBlockOpt.accept(assignmentLocator);
						Assignment setting = assignmentLocator.getAssignment();
						if(setting != null){
							ChildrenArrayVisitor visitor = new ChildrenArrayVisitor(containerIndex);
							setting.accept(visitor);
							totalCharactersRemoved = visitor.theCharactersRemoved;
							deletedChildName = visitor.deletedChildName;
						}
					}
					return false;
				}
				public boolean visit(final SimpleName simpleName){
					// The widget has been declared as a top level field - find the field and update it
					Node parentNode = simpleName.getParent();
					if(parentNode != null){
						parentNode.accept(new DefaultASTVisitor(){
							public boolean visit(ClassDataDeclaration classDataDeclaration){
								if(classDataDeclaration.hasSettingsBlock()){
									SettingsBlock settingsBlockOpt = classDataDeclaration.getSettingsBlockOpt();
									AssignmentLocator assignmentLocator = new AssignmentLocator(CHILDREN_PROPERTY_NAME);
									settingsBlockOpt.accept(assignmentLocator);
									Assignment setting = assignmentLocator.getAssignment();
									if(setting != null){
										ChildrenArrayVisitor visitor = new ChildrenArrayVisitor(containerIndex);
										setting.accept(visitor);
										totalCharactersRemoved = visitor.theCharactersRemoved;
										deletedChildName = visitor.deletedChildName;
									}
								}
								return false;
							}
							public boolean visit(FunctionDataDeclaration functionDataDeclaration){
								if(functionDataDeclaration.hasSettingsBlock()){
									SettingsBlock settingsBlockOpt = functionDataDeclaration.getSettingsBlockOpt();
									AssignmentLocator assignmentLocator = new AssignmentLocator(CHILDREN_PROPERTY_NAME);
									settingsBlockOpt.accept(assignmentLocator);
									Assignment setting = assignmentLocator.getAssignment();
									if(setting != null){
										ChildrenArrayVisitor visitor = new ChildrenArrayVisitor(containerIndex);
										setting.accept(visitor);
										totalCharactersRemoved = visitor.theCharactersRemoved;
										deletedChildName = visitor.deletedChildName;
									}
								}
								return false;
							}
						});
					}
					return false;
				}					
			});
		}
		return totalCharactersRemoved;
	}
	
	public String getDeletedWidgetName() {
		return deletedChildName;
	}
}
