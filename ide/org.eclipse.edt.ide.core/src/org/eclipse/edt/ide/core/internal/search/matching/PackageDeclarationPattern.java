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
package org.eclipse.edt.ide.core.internal.search.matching;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.ide.core.internal.model.index.IEntryResult;
import org.eclipse.edt.ide.core.internal.model.index.impl.IndexInput;
import org.eclipse.edt.ide.core.internal.search.IIndexSearchRequestor;
import org.eclipse.edt.ide.core.model.IMember;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;

public class PackageDeclarationPattern extends SearchPattern {
	char[] pkgName;
public PackageDeclarationPattern(char[] pkgName, int matchMode, boolean isCaseSensitive) {
	super(matchMode, isCaseSensitive);
	this.pkgName = pkgName;
}
/**
 * @see SearchPattern#decodeIndexEntry
 */
protected void decodeIndexEntry(IEntryResult entryResult) {
	// not used
}
/**
 * @see SearchPattern#feedIndexRequestor
 */
public void feedIndexRequestor(IIndexSearchRequestor requestor, int detailLevel, int[] references, IndexInput input, IEGLSearchScope scope) throws java.io.IOException {
	// not used
}
/**
 * see SearchPattern#findMatches
 */
public void findIndexMatches(IndexInput input, IIndexSearchRequestor requestor, int detailLevel, IProgressMonitor progressMonitor, IEGLSearchScope scope) throws IOException {
	// package declarations are not indexed
}
/**
 * @see SearchPattern#indexEntryPrefix
 */
public char[] indexEntryPrefix() {
	// not used
	return null;
}
/**
 * @see SearchPattern#matchContainer
 */
protected int matchContainer() {
	// used only in the case of a OrPattern
	return 0;
}
/**
 * @see SearchPattern#matchIndexEntry
 */
protected boolean matchIndexEntry() {
	// used only in the case of a OrPattern
	return true;
}
public String toString(){
	StringBuffer buffer = new StringBuffer(20);
	buffer.append("PackageDeclarationPattern: <"); //$NON-NLS-1$
	if (this.pkgName != null) buffer.append(this.pkgName);
	buffer.append(">, "); //$NON-NLS-1$
	switch(matchMode){
		case EXACT_MATCH : 
			buffer.append("exact match, "); //$NON-NLS-1$
			break;
		case PREFIX_MATCH :
			buffer.append("prefix match, "); //$NON-NLS-1$
			break;
		case PATTERN_MATCH :
			buffer.append("pattern match, "); //$NON-NLS-1$
			break;
	}
	if (isCaseSensitive)
		buffer.append("case sensitive"); //$NON-NLS-1$
	else
		buffer.append("case insensitive"); //$NON-NLS-1$
	return buffer.toString();
}

/**
 * 
 */
public int matchLevel(Node node, boolean resolve) {
	// used only in the case of a OrPattern
	return ACCURATE_MATCH;
}

	@Override
	public int matchLevel(IMember member, boolean resolve) {
		// used only in the case of a OrPattern
		return ACCURATE_MATCH;
	}
	@Override
	public int getPatternType() {
		return SearchPattern.DECLARATION;
	}
}
