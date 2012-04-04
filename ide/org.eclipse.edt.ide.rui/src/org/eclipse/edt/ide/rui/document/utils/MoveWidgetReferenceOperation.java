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
import org.eclipse.jface.text.BadLocationException;

import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.rui.internal.Activator;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLFile;

//TODO Handle moving an anonymous reference out of all boxes - need to give it a field name and remove 'new'
/**
 * Handle moving a widget reference (name of a widget, or an anonymous widget).  
 * The move can be:
 *  - from one box to another 
 *  	- Move the widget reference text.  The insert reference operation will update the Z order if necessary. 
 */
public class MoveWidgetReferenceOperation {

	private class MoveNodeOperation extends DefaultASTVisitor{
		
		private int newContainerOffset;
		private int newContainerLength;
		private int newContainerIndex;
		private int oldContainerOffset;
		private int oldContainerLength;
		private int oldContainerIndex;
		
		public MoveNodeOperation(int oldContainerOffset, int oldContainerLength,  int oldContainerIndex, int newContainerOffset, int newContainerLength, int newContainerIndex){
			this.oldContainerOffset = oldContainerOffset;
			this.oldContainerLength = oldContainerLength;
			this.oldContainerIndex = oldContainerIndex;
			this.newContainerOffset = newContainerOffset;
			this.newContainerLength = newContainerLength;
			this.newContainerIndex = newContainerIndex;
		}
		
		public boolean visit(NewExpression newExpression) {
			// Just need to move the widget text
			try{
				String widgetText = currentDocument.get(newExpression.getOffset(), newExpression.getLength());
				DeleteWidgetReferenceOperation deleteOp = new DeleteWidgetReferenceOperation(currentDocument, currentFile);
				charactersChanged[0] = deleteOp.deleteWidgetReference(oldContainerOffset, oldContainerLength, oldContainerIndex);
				
				// By removing the widget reference, we may have moved up the offset of the new container
				// Check to see if we need to adjust the offset of the new container by the number of 
				// characters we removed
				int theOffset = newContainerOffset;
				if(newContainerOffset > newExpression.getOffset()){
					theOffset = newContainerOffset - charactersChanged[0];			
				}
				
				charactersChanged[1] = new InsertWidgetReferenceOperation(currentDocument, currentFile).insertWidgetReference(widgetText, theOffset, newContainerLength, newContainerIndex);
			}catch(BadLocationException e){
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Move Widget Reference: Error getting text for New Expression", e));
			}
			return false;
		}
		public boolean visit(SimpleName simpleName){
			// Need to move the reference to the widget
			String widgetText = simpleName.getIdentifier();
			DeleteWidgetReferenceOperation deleteOp = new DeleteWidgetReferenceOperation(currentDocument, currentFile);
			charactersChanged[0] = deleteOp.deleteWidgetReference(oldContainerOffset, oldContainerLength, oldContainerIndex);
			
			// By removing the widget reference, we may have moved up the offset of the new container
			// Check to see if we need to adjust the offset of the new container by the number of 
			// characters we removed
			int theOffset = newContainerOffset;
			if(newContainerOffset > simpleName.getOffset()){
				theOffset = newContainerOffset - charactersChanged[0];			
			}
			
			charactersChanged[1] = new InsertWidgetReferenceOperation(currentDocument, currentFile).insertWidgetReference(widgetText, theOffset, newContainerLength, newContainerIndex);
			return false;
		}
		public boolean visit(QualifiedName qualifiedName){
			// Need to move the reference to the widget
			String widgetText = qualifiedName.getCanonicalName();
			DeleteWidgetReferenceOperation deleteOp = new DeleteWidgetReferenceOperation(currentDocument, currentFile);
			charactersChanged[0] = deleteOp.deleteWidgetReference(oldContainerOffset, oldContainerLength, oldContainerIndex);
			
			// By removing the widget reference, we may have moved up the offset of the new container
			// Check to see if we need to adjust the offset of the new container by the number of 
			// characters we removed
			int theOffset = newContainerOffset;
			if(newContainerOffset > qualifiedName.getOffset()){
				theOffset = newContainerOffset - charactersChanged[0];			
			}
			
			charactersChanged[1] = new InsertWidgetReferenceOperation(currentDocument, currentFile).insertWidgetReference(widgetText, theOffset, newContainerLength, newContainerIndex);
			return false;
		}
	}
	
	private IEGLDocument currentDocument;
	private IFile currentFile;
	private int[] charactersChanged = new int[2];
	
	public MoveWidgetReferenceOperation(IEGLDocument currentDocument, IFile currentFile){
		this.currentDocument = currentDocument;		
		this.currentFile = currentFile;
	}
	
	public int[] moveWidget(final int oldContainerOffset, final int oldContainerLength, final int oldContainerIndex, final int newContainerOffset, final int newContainerLength, final int newContainerIndex){
		try{
			IEGLFile modelFile = (IEGLFile)EGLCore.create(currentFile);
			IEGLFile sharedWorkingCopy = (IEGLFile)modelFile.getSharedWorkingCopy(null, EGLUI.getBufferFactory(), null);
			sharedWorkingCopy.open(null);
			sharedWorkingCopy.reconcile(false, null);
		
			try{
				final Node oldContainer = DocumentUtil.getWidgetNode(currentDocument,oldContainerOffset, oldContainerLength);	
				final Node newContainer = DocumentUtil.getWidgetNode(currentDocument,newContainerOffset, newContainerLength);
				final int newIndex = calculateNewIndex(oldContainer, newContainer, oldContainerIndex, newContainerIndex);
				oldContainer.accept(new DefaultASTVisitor(){
					public boolean visit(Handler handler){
						// Assume this is a RUIHandler
						EGLRUIHandlerLocatorStrategy strategy = new EGLRUIHandlerLocatorStrategy(handler);
						final Node nodeToMove = strategy.locateIndex(oldContainerIndex);
						MoveNodeOperation moveOp = new MoveNodeOperation(oldContainerOffset, oldContainerLength, oldContainerIndex, newContainerOffset, newContainerIndex, newIndex);
						nodeToMove.accept(moveOp);
						return false;
					}
					public boolean visit(final NewExpression newExpression){
						EGLContainerLocatorStrategy strategy = new EGLContainerLocatorStrategy(oldContainer);
						final Node nodeToMove = strategy.locateIndex(oldContainerIndex);
						MoveNodeOperation moveOp = new MoveNodeOperation(oldContainerOffset, oldContainerLength, oldContainerIndex, newContainerOffset, newContainerIndex, newIndex);
						nodeToMove.accept(moveOp);
						return false;
					}
					public boolean visit(final SimpleName simpleName){
						EGLContainerLocatorStrategy strategy = new EGLContainerLocatorStrategy(oldContainer);
						final Node nodeToMove = strategy.locateIndex(oldContainerIndex);
						MoveNodeOperation moveOp = new MoveNodeOperation(oldContainerOffset, oldContainerLength, oldContainerIndex, newContainerOffset, newContainerIndex, newIndex);
						nodeToMove.accept(moveOp);
						return false;
					}
				});
			}catch(Exception e){
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Move Widget Reference: Error moving reference", e));
			}finally{
				sharedWorkingCopy.destroy();					
			}
		}catch(EGLModelException e){
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Move Widget Reference: Error creating working copy", e));
		}
		return charactersChanged;
	}
	
	// If we are moving within the same container, and we are moving a widget further down in the list, remove one index for the widget that is being moved
	private int calculateNewIndex(Node oldContainer, Node newContainer, int oldIndex, int newIndex){
		int result = newIndex;
		if(oldContainer == newContainer){
			if(newIndex > oldIndex){
				result = newIndex - 1;
			}
		}
		return result;
	}
}
