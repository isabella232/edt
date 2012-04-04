/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.editor.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.ide.core.ast.GetNodeAtOffsetVisitor;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.IWorkingCopyCompileRequestor;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompilationResult;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompiler;
import org.eclipse.edt.ide.core.internal.model.EGLFile;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.ui.internal.EGLUI;

public class BoundNodeModelUtility {
	private static BoundNodeModelUtility instance;
	
	private IEGLDocument document;
	private int documentOffset;
	private int length = 0;
	
	/**
	 * @deprecated
	 */
	public static Node getBoundNodeAtOffset(IEGLDocument document, int documentOffset,int length, IFile file) {
		return getInstance().getInternalBoundNodesAtOffset(document, documentOffset, length,file)[0];
	}
	
	private static BoundNodeModelUtility getInstance() {
		if (instance == null)
			instance = new BoundNodeModelUtility();
		return instance;
	}

	private class WorkingCopyCompileRequestor implements IWorkingCopyCompileRequestor {
		private Node boundNode = null;
		private Part boundPart = null; 
		
		public void acceptResult(WorkingCopyCompilationResult result) {
			//find the node associated with the selection in the bound AST
			if (boundNode == null) {
				boundNode = document.getNewModelNodeAtOffset(documentOffset,length, result.getBoundPart());
				boundPart = (Part)result.getBoundPart();
			}
		}

		public Node getBoundNode() {
			return boundNode;
		}

		public Part getBoundPart() {
			return boundPart;
		}
	}

	private Node[] getInternalBoundNodesAtOffset(IEGLDocument document, int documentOffset, int selLength,IFile file) {
		this.document = document;
		this.documentOffset = documentOffset;
		this.length = selLength;
		IPath path = file.getFullPath();
		IWorkingCopyCompileRequestor requestor = new WorkingCopyCompileRequestor();
		WorkingCopyCompiler compiler = WorkingCopyCompiler.getInstance();
		compiler.compileAllParts(file.getProject(), getPackageName(path), file, EGLCore.getSharedWorkingCopies(EGLUI.getBufferFactory()), requestor);
		return new Node[] {
			((WorkingCopyCompileRequestor) requestor).getBoundNode(), 
			((WorkingCopyCompileRequestor) requestor).getBoundPart()};
	}

	private String[] getPackageName(IPath path) {
		String[] packageName = new String[path.segmentCount()-3];
		if (path.segmentCount() > 3) {
			for (int i = 2; i < path.segmentCount()-1; i++) {
				packageName[i-2] = path.segment(i);
			}
		}
		return packageName;
	}
	
	/**
	 * This utility will locate a bound node at the specified offset.  When the bound node is located, it will be returned to the caller through 
	 * the provided requestor.
	 * 
	 * The bound node should NOT be cached.  The value is only good for the life of the requestor call.
	 */
	public static void getBoundNodeAtOffset(final IFile file, final int offset, final IBoundNodeRequestor requestor){
		WorkingCopyCompiler compiler = WorkingCopyCompiler.getInstance();
		compiler.compileAllParts(file.getProject(), ((EGLFile)EGLCore.create(file)).getPackageName(), file, EGLCore.getSharedWorkingCopies(EGLUI.getBufferFactory()), new IWorkingCopyCompileRequestor(){

			public void acceptResult(WorkingCopyCompilationResult result) {
				
				final Node boundPart = result.getBoundPart();
				
				// Make sure that the selected node is within this part
				if((boundPart.getOffset() <= offset) && ((boundPart.getOffset() + boundPart.getLength()) >= offset)){
					
					// Get the selected node within this part
					GetNodeAtOffsetVisitor visitor = new GetNodeAtOffsetVisitor(offset); 
					result.getBoundPart().accept(visitor);
					
					Node selectedNode = visitor.getNode();
					
					requestor.acceptNode(boundPart, selectedNode);
				}
			}
		});
	}
}
