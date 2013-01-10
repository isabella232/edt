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

import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.rui.internal.Activator;
import org.eclipse.edt.ide.rui.utils.Util;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.mof.utils.NameUtile;

/**
 * Remove the reference to the widget at the specified offset.  If the widget is named, this operation will not remove
 * the widget definition.
 */
public class DeleteWidgetReferenceOperation {

	private IEGLDocument currentDocument;
	private IFile currentFile;
	private int numCharactersRemoved = 0;
	private String deletedChildName;
	
	public DeleteWidgetReferenceOperation(IEGLDocument currentDocument, IFile currentFile){
		this.currentDocument = currentDocument;
		this.currentFile = currentFile;
	}
	public int deleteWidgetReference(final int containerOffset, final int containerLength, final int containerIndex){
		
		try{
			IEGLFile modelFile = (IEGLFile)EGLCore.create(currentFile);
			IEGLFile sharedWorkingCopy = (IEGLFile)modelFile.getSharedWorkingCopy(null, EGLUI.getBufferFactory(), null);
			sharedWorkingCopy.open(null);
			sharedWorkingCopy.reconcile(false, null);
		
			try{
				final Node container = DocumentUtil.getWidgetNode( currentDocument, containerOffset, containerLength);
				
				container.accept(new DefaultASTVisitor(){
					public boolean visit(Handler handler){
						String typeName = NameUtile.getAsName(new String(handler.getSubType().getCanonicalName()));
						if(NameUtile.equals(typeName, Util.RUIHANDLER)){
							EGLRUIHandlerDeleteStrategy strategy = new EGLRUIHandlerDeleteStrategy(handler, currentDocument);
							numCharactersRemoved = strategy.deleteFromHandler(containerIndex);
							deletedChildName = strategy.getDeletedWidgetName();
						} else if ( NameUtile.equals(typeName, Util.RUIWIDGET) ) {
							EGLRUIWidgetDeleteStrategy strategy = new EGLRUIWidgetDeleteStrategy(handler, currentDocument);
							strategy.deleteTargetWidget();
							deletedChildName = strategy.getDeletedWidgetName();
						}
						return false;
					}
					public boolean visit(final NewExpression newExpression){
						EGLContainerDeleteStrategy strategy = new EGLContainerDeleteStrategy(container, currentDocument);
						numCharactersRemoved = strategy.deleteFromContainer(containerIndex);
						deletedChildName = strategy.getDeletedWidgetName();
						return false;
					}
					public boolean visit(final SimpleName simpleName){
						EGLContainerDeleteStrategy strategy = new EGLContainerDeleteStrategy(container, currentDocument);
						numCharactersRemoved = strategy.deleteFromContainer(containerIndex);
						deletedChildName = strategy.getDeletedWidgetName();
						return false;
					}
				});
			}catch(Exception e){
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Delete Widget Reference: Error deleting reference", e));
			}finally{
				sharedWorkingCopy.destroy();					
			}
		}catch(EGLModelException e){
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Delete Widget Reference: Error creating working copy", e));
		}
		
		return numCharactersRemoved;
	}
	
	public String getDeletedWidgetName() {
		return deletedChildName;
	}
}
