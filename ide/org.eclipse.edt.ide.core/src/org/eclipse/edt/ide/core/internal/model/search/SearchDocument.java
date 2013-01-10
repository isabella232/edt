/*******************************************************************************
 * Copyright © 2010, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.model.search;

import org.eclipse.edt.ide.core.internal.model.index.IDocument;
import org.eclipse.edt.ide.core.internal.model.index.IIndex;



public abstract class SearchDocument implements IDocument{

//	private IIndex index;
//	private SourceElementParser parser;
	private String documentPath;
	private SearchParticipant participant;

	/**
	 * Creates a new search document. The given document path is a string that uniquely identifies the document.
	 * Most of the time it is a workspace-relative path, but it can also be a file system path, or a path inside a zip file.
	 *
	 * @param documentPath the path to the document,
	 * or <code>null</code> if none
	 * @param participant the participant that creates the search document
	 */
	protected SearchDocument(String documentPath, SearchParticipant participant) {
		this.documentPath = documentPath;
		this.participant = participant;
	}

	/**
	 * Adds the given index entry (category and key) coming from this
	 * document to the index. This method must be called from
	 * {@link SearchParticipant#indexDocument(SearchDocument document, org.eclipse.core.runtime.IPath indexPath)}.
	 *
	 * @param category the category of the index entry
	 * @param key the key of the index entry
	 */
	public void addIndexEntry(char[] category, char[] key) {
//		if (this.index != null)
//			this.index.addIndexEntry(category, key, getContainerRelativePath());
	}

	/**
	 * @nooverride This method is not intended to be re-implemented or extended by clients.
	 * @noreference This method is not intended to be referenced by clients.
	 */
//	public SourceElementParser getParser() {
//		return this.parser;
//	}
	
	/**
	 * Returns the participant that created this document.
	 *
	 * @return the participant that created this document
	 */
	public final SearchParticipant getParticipant() {
		return this.participant;
	}

	/**
	 * Returns the path to the original document to publicly mention in index
	 * or search results. This path is a string that uniquely identifies the document.
	 * Most of the time it is a workspace-relative path, but it can also be a file system path,
	 * or a path inside a zip file.
	 *
	 * @return the path to the document
	 */
	public final String getPath() {
		return this.documentPath;
	}
	/**
	 * Removes all index entries from the index for the given document.
	 * This method must be called from
	 * {@link SearchParticipant#indexDocument(SearchDocument document, org.eclipse.core.runtime.IPath indexPath)}.
	 */
	public void removeAllIndexEntries() {
//		if (this.index != null)
//			this.index.remove(getContainerRelativePath());
	}
	
	/**
	 * @nooverride This method is not intended to be re-implemented or extended by clients.
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public void setIndex(IIndex indexToSet) {
//		this.index = indexToSet;
	}
	
	/**
	 * @nooverride This method is not intended to be re-implemented or extended by clients.
	 * @noreference This method is not intended to be referenced by clients.
	 */
//	public void setParser(SourceElementParser sourceElementParser) {
//		this.parser = sourceElementParser;
//	}

}
