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
package org.eclipse.edt.ide.core.model.document;

import java.io.Reader;

import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.jface.text.IDocument;

/**
 * @author svihovec
 *
 * This interface defines all of the methods that are provided by the Model.
 * 
 * This model is used to represent a EGL Documents.  The model maintains all of the nodes
 * that make up this document as well as the textual information in this document.  All of
 * the node and textual information for this document are versioned so that the model
 * can be set to a previous version at any time.
 * 
 * If the model is set to a previous version, and new changes are made to the document,
 * any version that came after the current version will be inaccessible.
 */
public interface IModel extends IDocument {
	// The following handles model change notification
	void addModelChangeListener(IEGLModelChangeListener listener);
	void removeModelChangeListener(IEGLModelChangeListener listener);
	
	Reader getReader(int startOffset);

	public long getLastUpdateTime();

	/*------------ Node access ----------------------------*/

	/*------------ text access and manipulation -----------*/

	Node getNewModelNodeAtOffset(int offset,int length);
	Node getNewModelNodeAtOffset(int offset);
	Node getNewModelNodeAtOffset(int offset,int length, Node node);
	Node getNewModelNodeAtOffset(int offset, Node node);
	
	File getNewModelEGLFile();
	
	
	/*------------ versioning ------------*/
	
	/**
	 * Get the global version tree for this model.
 	 */
//	public IGlobalVersionTree getGlobalVersionTree();
}
