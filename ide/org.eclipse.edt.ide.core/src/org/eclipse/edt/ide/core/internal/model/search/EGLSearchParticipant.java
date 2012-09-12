/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
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

import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.internal.model.index.IDocument;



public class EGLSearchParticipant extends SearchParticipant {

	private ThreadLocal indexSelector = new ThreadLocal();

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.search.SearchParticipant#beginSearching()
	 */
	public void beginSearching() {
		super.beginSearching();
		this.indexSelector.set(null);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.search.SearchParticipant#doneSearching()
	 */
	public void doneSearching() {
		this.indexSelector.set(null);
		super.doneSearching();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.search.SearchParticipant#getName()
	 */
	public String getDescription() {
		return "EGL"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.search.SearchParticipant#getDocument(String)
	 */
	public SearchDocument getDocument(String documentPath) {
		return new EGLSearchDocument(documentPath, this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.search.SearchParticipant#indexDocument(SearchDocument)
	 */
	public void indexDocument(IDocument document, IPath indexPath) {
		// TODO must verify that the document + indexPath match, when this is not called from scheduleDocumentIndexing
//		document.removeAllIndexEntries(); // in case the document was already indexed

//		String documentPath = document.getPath();
//		if (org.eclipse.jdt.internal.core.util.Util.isJavaLikeFileName(documentPath)) {
//			new SourceIndexer(document).indexDocument();
//		} else 
			
//		if (com.ibm.etools.egl.model.internal.core.Util.isEGLIRFileName(documentPath)) {
//			new BinaryIndexer().indexDocument(document);
//		}
	}

	/* (non-Javadoc)
	 * @see SearchParticipant#locateMatches(SearchDocument[], SearchPattern, IJavaSearchScope, SearchRequestor, IProgressMonitor)
	 */
//	public void locateMatches(SearchDocument[] indexMatches, SearchPattern pattern,
//			IJavaSearchScope scope, SearchRequestor requestor, IProgressMonitor monitor) throws CoreException {
//
//		MatchLocator matchLocator =
//			new MatchLocator(
//				pattern,
//				requestor,
//				scope,
//				monitor
//		);
//
//		/* eliminating false matches and locating them */
//		if (monitor != null && monitor.isCanceled()) throw new OperationCanceledException();
//		matchLocator.locateMatches(indexMatches);
//	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.search.SearchParticipant#selectIndexes(org.eclipse.jdt.core.search.SearchQuery, org.eclipse.jdt.core.search.SearchContext)
	 */
//	public IPath[] selectIndexes(SearchPattern pattern, IJavaSearchScope scope) {
//
//		IndexSelector selector = (IndexSelector) this.indexSelector.get();
//		if (selector == null) {
//			selector = new IndexSelector(scope, pattern);
//			this.indexSelector.set(selector);
//		}
//		return selector.getIndexLocations();
//	}

}
