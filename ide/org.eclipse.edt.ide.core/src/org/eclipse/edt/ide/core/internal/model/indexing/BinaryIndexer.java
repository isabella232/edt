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
package org.eclipse.edt.ide.core.internal.model.indexing;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.edt.ide.core.internal.model.BinaryElementParser;
import org.eclipse.edt.ide.core.internal.model.index.IDocument;


public class BinaryIndexer extends AbstractIndexer {
	
	public static final String[] FILE_TYPES = new String[] { "eglxml", "eglar" }; //$NON-NLS-1$
	private IProject project;
	
	public BinaryIndexer() {
		this.project = null;
	}
	
	public BinaryIndexer(IProject project) {
		this.project = project;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.edt.ide.core.internal.model.indexing.AbstractIndexer#getFileTypes()
	 */
	public String[] getFileTypes() {
		return FILE_TYPES;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.edt.ide.core.internal.model.indexing.AbstractIndexer#indexFile(com.ibm.etools.egl.model.internal.core.index.IDocument)
	 */
	protected void indexFile(IDocument document) throws IOException {
		if (document == null) {
			return;
		}
		output.addDocument(document);
		// Create a new Parser
		BinaryIndexerRequestor requestor = new BinaryIndexerRequestor(this, document);
		BinaryElementParser parser = new BinaryElementParser(requestor,this.project); 

		// Launch the parser
		byte[] source = null;
		char[] name = null;
		try {
			source = document.getByteContent();
			name = document.getName().toCharArray();
		} catch(Exception e){
		}
		if (source == null || name == null) return; // could not retrieve document info (e.g. resource was discarded)
		try {
			parser.parseDocument(document, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.edt.ide.core.internal.model.index.IIndexer#setFileTypes(java.lang.String[])
	 */
	public void setFileTypes(String[] fileTypes) {
	}
}
