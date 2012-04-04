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

import java.util.List;

import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.mof.egl.utils.InternUtil;

public class EGLContainerLocatorStrategy {
	
	private static final String CHILDREN_PROPERTY_NAME = InternUtil.intern("children");
	
	private class ChildrenArrayVisitor extends DefaultASTVisitor{
		private int index;
		private Node theNode;

		public ChildrenArrayVisitor(int index){
			this.index = index;
		}
		
		public boolean visit(final Assignment assignment){
			assignment.getRightHandSide().accept(new DefaultASTVisitor(){
				public boolean visit(ArrayLiteral arrayLiteral){
					List expressions = arrayLiteral.getExpressions();
					theNode = (Node)expressions.get(index);
					return false;
				}
			});
			return false;
		}			
	}
	
	private Node containerNode;
	private Node result;
	
	public EGLContainerLocatorStrategy(Node containerNode){
		this.containerNode = containerNode;
	}
	
	public Node locateIndex(final int containerIndex){
		if(containerNode != null){
			containerNode.accept(new DefaultASTVisitor(){
				public boolean visit(final NewExpression newExpression){
					// This widget is anonymous
					if(newExpression.hasSettingsBlock()){
						SettingsBlock settingsBlockOpt = newExpression.getSettingsBlock();
						AssignmentLocator assignmentLocator = new AssignmentLocator(InternUtil.intern(CHILDREN_PROPERTY_NAME));
						settingsBlockOpt.accept(assignmentLocator);
						Assignment setting = assignmentLocator.getAssignment();
						if(setting != null){
							ChildrenArrayVisitor visitor = new ChildrenArrayVisitor(containerIndex);
							setting.accept(visitor);
							result = visitor.theNode;
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
										result = visitor.theNode;
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
										result = visitor.theNode;
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
		return result;
	}
}
