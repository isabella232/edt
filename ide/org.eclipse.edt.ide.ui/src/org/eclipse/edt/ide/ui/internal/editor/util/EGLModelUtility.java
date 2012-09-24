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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.ErrorCorrectingParser;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Lexer;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.ide.core.internal.model.BufferManager;
import org.eclipse.edt.ide.core.model.IBuffer;
import org.eclipse.edt.ide.core.model.IBufferFactory;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.ui.internal.editor.DocumentAdapter;
import org.eclipse.jface.text.IDocument;

public class EGLModelUtility {
	
	/*
	 * return the production node for the nested part in eglPart which matches part naame
	 * This currently only works for functions in programs, page handlers, and libraries.
	 * It can easily be modified for forms in form groups if necessary
	 */
	public static Node getNestedPartNode(IEGLDocument document, final int documentOffset) {
		File fileAST = document.getNewModelEGLFile();
		final Node[] newNestedNode = new Node[]{null};
		fileAST.accept(new AbstractASTVisitor(){
			public boolean visit(org.eclipse.edt.compiler.core.ast.NestedFunction nestedFunction) {
				visitNestedPart(nestedFunction);
				return false;
			};
			
			private void visitNestedPart(Node nestedNode)
			{
				if(documentOffset >= nestedNode.getOffset() && documentOffset <= nestedNode.getOffset() + nestedNode.getLength())
					newNestedNode[0] = nestedNode;
			}
		});
		
		return newNestedNode[0];
	}
	
	/*
	 * Get the top level part associated with the cursor position
	 * If not in a top level part null is returned
	 */
	public static Part getPartNode(IEGLDocument document, int documentOffset) {
		Part eglPart = null;
		List eglParts = document.getNewModelEGLFile().getParts();
		Iterator iter = eglParts.iterator();
		while (iter.hasNext()) {
			eglPart = (Part) iter.next();
			int offset = eglPart.getOffset(); 
			int length = eglPart.getLength();
			if (documentOffset < offset)
				return null;
			if (documentOffset >= offset && documentOffset <= offset + length){
				return eglPart;
			}
		}
		return null;
	}
	
	/**
	 * this is the equvalent replacement for the old EGLCore.getEGLFile() T-model to pgm model conversion
	 * 
	 * The AST Root node return is from saved file version, not live info
	 * 
	 * @param element
	 * @return the File root node of the T-modle IEGLElement
	 * @throws CoreException 
	 * @throws  
	 * @throws  
	 */
	public static File getEGLFileAST(IEGLFile eglfile, IBufferFactory factory) throws Exception
	{
		IEGLFile workingCopy = null;
		try{
			if(factory == null)
				factory = BufferManager.getDefaultBufferManager().getDefaultBufferFactory(); 
			workingCopy = (IEGLFile)(eglfile.getSharedWorkingCopy(null, factory, null));	
			IBuffer buf = workingCopy.getBuffer();
			if(buf.hasUnsavedChanges())
			{
				//use the saved file
				IFile file = (IFile)(eglfile.getUnderlyingResource());
				ErrorCorrectingParser newParser;			
				int whitespacemask = ErrorCorrectingParser.RETURN_BLOCK_COMMENT | ErrorCorrectingParser.RETURN_LINE_COMMENT | ErrorCorrectingParser.RETURN_LINEBREAKS;
				Reader reader = new BufferedReader(new InputStreamReader(file.getContents(true), file.getCharset()));
	       		newParser = new ErrorCorrectingParser(new Lexer(reader), whitespacemask);		
				File fileRootNode = (File)(newParser.parse().value);
				return fileRootNode;
			}
			else //use the cached ast tree from document
			{
				if(buf instanceof DocumentAdapter)
				{
					IDocument doc = ((DocumentAdapter)buf).getDocument();
					if(doc instanceof IEGLDocument)
					{
						IEGLDocument egldoc = (IEGLDocument)doc;
						return egldoc.getNewModelEGLFile();
					}
				}
			}
		}
		finally{
			if(workingCopy != null)
				workingCopy.destroy();		//release the workingCopy to decrement the count			
		}
		return null;
	}
	
}
