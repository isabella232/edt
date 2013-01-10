/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
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

import org.eclipse.core.resources.IFile;
import org.eclipse.edt.ide.core.internal.model.SourceElementParser;
import org.eclipse.edt.ide.core.internal.model.index.IDocument;


/**
 * A SourceIndexer indexes egl files using an egl parser. The following items are indexed:
 * Declarations of:
 * - Parts<br>
 * - Functions in Parts;<br>
 * - Fields;<br>
 * References to:
 * - Functions (with number of arguments); <br>
 * - Fields;<br>
 * - Parts.
 */
public class SourceIndexer extends AbstractIndexer {
	
	public static final String[] FILE_TYPES= new String[] {"egl"}; //$NON-NLS-1$
//	protected DefaultProblemFactory problemFactory= new DefaultProblemFactory(Locale.getDefault());
	IFile resourceFile;
	
SourceIndexer(IFile resourceFile)	{
	this.resourceFile = resourceFile;
}

/**
 * Returns the file types the <code>IIndexer</code> handles.
 */

public String[] getFileTypes(){
	return FILE_TYPES;
}
protected void indexFile(IDocument document) throws IOException {

	// Add the name of the file to the index
	output.addDocument(document);

	// Create a new Parser
	SourceIndexerRequestor requestor = new SourceIndexerRequestor(this, document);
	SourceElementParser parser = new SourceElementParser(requestor); 

	// Launch the parser
	char[] source = null;
	char[] name = null;
	try {
		source = document.getCharContent();
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
/**
 * Sets the document types the <code>IIndexer</code> handles.
 */

public void setFileTypes(String[] fileTypes){}
}
