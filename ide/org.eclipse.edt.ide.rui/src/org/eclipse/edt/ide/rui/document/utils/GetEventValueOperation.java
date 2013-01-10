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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Delegate;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.FunctionInvocation;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.rui.internal.Activator;
import org.eclipse.edt.ide.rui.server.EventValue;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.mof.utils.NameUtile;

/**
 * Get the value of a property
 */
public class GetEventValueOperation {

	private class EventValueVisitor extends DefaultASTVisitor{
		
		private String result = "";
		
		public EventValueVisitor(){
		}
		
		public String getResult(){
			return result;
		}
		
		public void processExpression(Expression expression){
			expression.accept(new DefaultASTVisitor(){	
				public boolean visit(Delegate delegate) {
					result = delegate.getPartTypeName();
					return false;
				}
				public boolean visit(FunctionInvocation functionInvocation){
					result = functionInvocation.getCanonicalString();
					return false;
				}
				public boolean visit(SimpleName simpleName){
					result = simpleName.getCanonicalString();
					return false;
				}
				public boolean visit(QualifiedName qualifiedName){
					result = qualifiedName.getCanonicalString();
					return false;
				}
			});
		}
	}
	
	private IEGLDocument currentDocument;
	private IFile currentFile;
	private EventValue eventValue = null;
	
	public GetEventValueOperation(IEGLDocument currentDocument, IFile currentFile){
		this.currentDocument = currentDocument;		
		this.currentFile = currentFile;
	}
	
	// Event values are always editable
	public EventValue getEventValue(final String eventName, final int widgetOffset, final int widgetLength){
		if(widgetOffset <= 0 || widgetLength <= 0){ return null; }; // Guard against invalid widget locations
		
		try{
			IEGLFile modelFile = (IEGLFile)EGLCore.create(currentFile);
			IEGLFile sharedWorkingCopy = (IEGLFile)modelFile.getSharedWorkingCopy(null, EGLUI.getBufferFactory(), null);
			sharedWorkingCopy.reconcile(false, null);
		
			try{
				final Node widgetReference = DocumentUtil.getWidgetNode(currentDocument, widgetOffset, widgetLength);		
				if(widgetReference != null){
					widgetReference.accept(new DefaultASTVisitor(){
						public boolean visit(final NewExpression newExpression){
							// This widget is anonymous
							processNewExpression(eventName, newExpression);
							return false;
						}
						public boolean visit(final SimpleName simpleName){
							Node parentNode = simpleName.getParent();
							if(parentNode != null){
								parentNode.accept(new DefaultASTVisitor(){
									public boolean visit(ClassDataDeclaration classDataDeclaration){
										boolean foundEvent = false;
										if(classDataDeclaration.hasSettingsBlock()){
											AssignmentLocator locator = new AssignmentLocator(NameUtile.getAsName(eventName));
											classDataDeclaration.getSettingsBlockOpt().accept(locator);
											if(locator.getAssignment() != null){
												foundEvent = true;
												EventValueVisitor visitor = new EventValueVisitor();
												visitor.processExpression(locator.getAssignment().getRightHandSide());
												eventValue = new EventValue(visitor.getResult(), true);
											}
										}	
										if(!foundEvent && classDataDeclaration.hasInitializer()){
											Expression initializer = classDataDeclaration.getInitializer();
											initializer.accept(new DefaultASTVisitor(){
												public boolean visit(NewExpression newExpression) {
													processNewExpression(eventName, newExpression);
													return false;
												}
											});
										}
										return false;
									}
									public boolean visit(FunctionDataDeclaration functionDataDeclaration){
										boolean foundEvent = false;
										if(functionDataDeclaration.hasSettingsBlock()){
											AssignmentLocator locator = new AssignmentLocator(NameUtile.getAsName(eventName));
											functionDataDeclaration.getSettingsBlockOpt().accept(locator);
											if(locator.getAssignment() != null){
												foundEvent = true;
												EventValueVisitor visitor = new EventValueVisitor();
												visitor.processExpression(locator.getAssignment().getRightHandSide());
												eventValue = new EventValue(visitor.getResult(), true);
											}
										}
										if(!foundEvent && functionDataDeclaration.hasInitializer()){
											Expression initializer = functionDataDeclaration.getInitializer();
											initializer.accept(new DefaultASTVisitor(){
												public boolean visit(NewExpression newExpression) {
													processNewExpression(eventName, newExpression);
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
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Get Event Value: Error accessing event value", e));
			}finally{
				sharedWorkingCopy.destroy();					
			}
		}catch(EGLModelException e){
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Get Event Value: Error creating working copy", e));
		}
		
		return eventValue;
	}

	private void processNewExpression(final String eventName, final NewExpression newExpression) {
		if(newExpression.hasSettingsBlock()){
			AssignmentLocator locator = new AssignmentLocator(NameUtile.getAsName(eventName));
			newExpression.getSettingsBlock().accept(locator);
			if(locator.getAssignment() != null){
				//processExpression(locator.getAssignment().getRightHandSide());
				EventValueVisitor visitor = new EventValueVisitor();
				visitor.processExpression(locator.getAssignment().getRightHandSide());
				eventValue = new EventValue(visitor.getResult(), true);
			}
		}
	}
}
