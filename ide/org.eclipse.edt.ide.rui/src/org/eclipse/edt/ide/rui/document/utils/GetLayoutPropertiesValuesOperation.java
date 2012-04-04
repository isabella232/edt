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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Status;

import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.rui.internal.Activator;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLFile;

public class GetLayoutPropertiesValuesOperation {
	
	private IEGLDocument currentDocument;
	private IFile currentFile;
	private String layoutPropertyName = "layoutData";
	private Expression layoutData; 
	private List assignments; 
	
	public List getAllProperties() {
		return assignments;
	}
	
	public GetLayoutPropertiesValuesOperation(IEGLDocument currentDocument, IFile currentFile){
		this.currentDocument = currentDocument;		
		this.currentFile = currentFile;
	}
	public void visitProperties(final int widgetOffset, final int widgetLength){
		if(widgetOffset <= 0 || widgetLength <= 0){ return ; }; // Guard against invalid widget locations
		
		try{
			IEGLFile modelFile = (IEGLFile)EGLCore.create(currentFile);
			IEGLFile sharedWorkingCopy = (IEGLFile)modelFile.getSharedWorkingCopy(null, EGLUI.getBufferFactory(), null);
			sharedWorkingCopy.reconcile(false, null);
		
			try{
				final Node widgetReference = DocumentUtil.getWidgetNode(currentDocument,widgetOffset, widgetLength);		
				
				if(widgetReference != null){
					widgetReference.accept(new DefaultASTVisitor(){
						public boolean visit(final NewExpression newExpression){
							// This widget is anonymous
							if(newExpression.hasSettingsBlock()){
								AssignmentLocator locator = new AssignmentLocator(InternUtil.intern(layoutPropertyName));
								newExpression.getSettingsBlock().accept(locator);
								if(locator.getAssignment() != null){
									//processExpression(locator.getAssignment().getRightHandSide());
									layoutData = locator.getAssignment().getRightHandSide();
								}
							}
							return false;
						}
						public boolean visit(final SimpleName simpleName){
							Node parentNode = simpleName.getParent();
							if(parentNode != null){
								parentNode.accept(new DefaultASTVisitor(){
									public boolean visit(ClassDataDeclaration classDataDeclaration){
										if(classDataDeclaration.hasSettingsBlock()){
											AssignmentLocator locator = new AssignmentLocator(InternUtil.intern(layoutPropertyName));
											classDataDeclaration.getSettingsBlockOpt().accept(locator);
											if(locator.getAssignment() != null){
												layoutData = locator.getAssignment().getRightHandSide();
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
				
				// Be defensive
				if(layoutData != null){
					layoutData.accept(new DefaultASTVisitor(){
						public boolean visit(final NewExpression newExpression){
							// This widget is anonymous
							processNewExpression(newExpression);
							return false;
						}
						public boolean visit(final SimpleName simpleName){
							Node parentNode = simpleName.getParent();
							if(parentNode != null){
								parentNode.accept(new DefaultASTVisitor(){
									public boolean visit(ClassDataDeclaration classDataDeclaration){
										boolean foundSetting = false;
										if(classDataDeclaration.hasSettingsBlock()){
											AssignmentsLocator locator = new AssignmentsLocator();
											classDataDeclaration.getSettingsBlockOpt().accept(locator);
											assignments = locator.getAssignments();
										}
										
										if(!foundSetting && classDataDeclaration.hasInitializer()){
											Expression initializer = classDataDeclaration.getInitializer();
											initializer.accept(new DefaultASTVisitor(){
												public boolean visit(NewExpression newExpression) {
													processNewExpression(newExpression);
													return false;
												}
											});
										}
										return false;
									}
									public boolean visit(FunctionDataDeclaration functionDataDeclaration){
										boolean foundSetting = false;
										if(functionDataDeclaration.hasSettingsBlock()){
											AssignmentsLocator locator = new AssignmentsLocator();
											functionDataDeclaration.getSettingsBlockOpt().accept(locator);
											assignments = locator.getAssignments();
										}	
										
										if(!foundSetting && functionDataDeclaration.hasInitializer()){
											Expression initializer = functionDataDeclaration.getInitializer();
											initializer.accept(new DefaultASTVisitor(){
												public boolean visit(NewExpression newExpression) {
													processNewExpression(newExpression);
													return false;
												}
											});
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
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Get Property Value: Error accessing property value", e));
			}finally{
				sharedWorkingCopy.destroy();					
			}
		}catch(EGLModelException e){
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Get Property Value: Error creating working copy", e));
		}
	}
	
	private void processNewExpression(final NewExpression newExpression) {
		if(newExpression.hasSettingsBlock()){
			AssignmentsLocator locator = new AssignmentsLocator();
			newExpression.getSettingsBlock().accept(locator);
			assignments = locator.getAssignments();
		}
	}
}
