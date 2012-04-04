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

import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.rui.internal.Activator;
import org.eclipse.edt.ide.rui.utils.Util;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLFile;

/**
 * Insert Widget Reference and update the Z order of the declared widgets
 */
public class InsertWidgetReferenceOperation {

	private IEGLDocument currentDocument;
	private IFile currentFile;
	private int totalCharactersAdded;

	public InsertWidgetReferenceOperation(IEGLDocument currentDocument, IFile currentFile){
		this.currentDocument = currentDocument;		
		this.currentFile = currentFile;
	}
	
	public int insertWidgetReference(final String insertText, final int containerOffset, final int containerLength, final int containerIndex){
		
		try{
			final IEGLFile modelFile = (IEGLFile)EGLCore.create(currentFile);
			IEGLFile sharedWorkingCopy = (IEGLFile)modelFile.getSharedWorkingCopy(null, EGLUI.getBufferFactory(), null);
			sharedWorkingCopy.open(null);
			sharedWorkingCopy.reconcile(false, null);
			try{
				final Node container = DocumentUtil.getWidgetNode(currentDocument,containerOffset, containerLength);
				container.accept(new DefaultASTVisitor(){
					public boolean visit(Handler handler){
						String typeName = InternUtil.intern(new String(handler.getSubType().getCanonicalName()));
						if( typeName == Util.RUIHANDLER ){
							EGLRUIHandlerUpdateStrategy updateStrategy = new EGLRUIHandlerUpdateStrategy(handler, currentDocument);
							totalCharactersAdded = updateStrategy.updateHandler(insertText, containerIndex);
						} else if ( typeName == Util.RUIWIDGET ) {
							EGLRUIWidgetUpdateStrategy updateStrategy = new EGLRUIWidgetUpdateStrategy(handler, currentDocument);
							totalCharactersAdded = updateStrategy.insertTargetWidget(insertText);
						}
						return false;
					}
					public boolean visit(final NewExpression newExpression){
						EGLContainerUpdateStrategy updateStrategy = new EGLContainerUpdateStrategy(newExpression, currentDocument);
						totalCharactersAdded = updateStrategy.updateContainer(insertText, containerIndex);
						return false;
					}
					public boolean visit(final SimpleName simpleName){
						EGLContainerUpdateStrategy updateStrategy = new EGLContainerUpdateStrategy(simpleName, currentDocument);
						totalCharactersAdded = updateStrategy.updateContainer(insertText, containerIndex);
						return false;
					}
				});
			}catch(Exception e){
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Insert Widget Reference: Error inserting widget reference", e));
			}finally{
				sharedWorkingCopy.destroy();					
			}
		}catch(EGLModelException e){
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Insert Widget Reference: Error creating working copy", e));
		}
		return totalCharactersAdded;
	}
}
