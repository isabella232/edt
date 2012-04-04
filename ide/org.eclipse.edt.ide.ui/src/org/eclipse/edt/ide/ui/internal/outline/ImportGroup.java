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
package org.eclipse.edt.ide.ui.internal.outline;

import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.ImportDeclaration;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;

public class ImportGroup {
	// We are forcing ImportGroup to implement IProductionNode so that the code 
	// in OutlineAdapterFactory can be more uniform.

	private IEGLDocument document;

	public ImportGroup(IEGLDocument document) {
		this.document = document;
	}

	public File getFile() {
		return document.getNewModelEGLFile();
	}

	private class ImportGroupVisitor extends DefaultASTVisitor
	{
		int totalLength = 0;
		int smallestOffset=0;
		int largestOffset = -1;
		int lengthOfNodeWithLargestOffset = -1;
		Node nodeWithLargestOffset = null;
		
		public boolean visit(File file) {
			smallestOffset = file.getLength();		//init to be the biggest number
			return true;
		};
		
		public boolean visit(ImportDeclaration importDeclaration) {
			int offset = importDeclaration.getOffset();
			smallestOffset = Math.min(smallestOffset, offset);			
			int currentLength = importDeclaration.getLength();
			totalLength += currentLength;
			if(offset > largestOffset)
			{
				largestOffset = offset;
				nodeWithLargestOffset = importDeclaration;
				lengthOfNodeWithLargestOffset = currentLength;
			}
			return false;
		};
	}
	
	public int getLength() {
		ImportGroupVisitor visitor = visitImports();
		int totalLength = visitor.totalLength;
		totalLength -= visitor.lengthOfNodeWithLargestOffset;
		totalLength += visitor.nodeWithLargestOffset.getLength();
		return totalLength;
	}

	private ImportGroupVisitor visitImports() {
		File file = getFile();
		ImportGroupVisitor visitor = new ImportGroupVisitor();
		file.accept(visitor);
		return visitor;
	}
	
	public int getOffset() {
		ImportGroupVisitor visitor = visitImports();
		return visitor.smallestOffset;
	}	


}
