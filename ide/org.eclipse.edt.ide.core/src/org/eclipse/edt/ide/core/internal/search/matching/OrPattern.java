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
package org.eclipse.edt.ide.core.internal.search.matching;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.ide.core.internal.model.index.IEntryResult;
import org.eclipse.edt.ide.core.internal.model.index.impl.IndexInput;
import org.eclipse.edt.ide.core.internal.search.IIndexSearchRequestor;
import org.eclipse.edt.ide.core.internal.search.IInfoConstants;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IFunction;
import org.eclipse.edt.ide.core.model.IMember;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.mof.egl.FunctionPart;

public class OrPattern extends SearchPattern {

	public SearchPattern leftPattern;
	public SearchPattern rightPattern;
	
	public OrPattern(SearchPattern leftPattern, SearchPattern rightPattern) {
		super(-1, false); // values ignored for a OrPattern

		this.leftPattern = leftPattern;
		this.rightPattern = rightPattern;

		this.matchMode = Math.min(leftPattern.matchMode, rightPattern.matchMode);
		this.isCaseSensitive = leftPattern.isCaseSensitive || rightPattern.isCaseSensitive;
		this.needsResolve = leftPattern.needsResolve || rightPattern.needsResolve;
	}
	/**
	 * see SearchPattern.decodedIndexEntry
	 */
	protected void decodeIndexEntry(IEntryResult entry) {

		// will never be directly invoked on a composite pattern
	}
	/**
	 * see SearchPattern.feedIndexRequestor
	 */
	public void feedIndexRequestor(
		IIndexSearchRequestor requestor,
		int detailLevel,
		int[] references,
		IndexInput input,
		IEGLSearchScope scope)
		throws IOException {
		// will never be directly invoked on a composite pattern
	}
	/**
	 * see SearchPattern.findMatches
	 */
	public void findIndexMatches(
		IndexInput input,
		IIndexSearchRequestor requestor,
		int detailLevel,
		IProgressMonitor progressMonitor,
		IEGLSearchScope scope)
		throws IOException {

		if (progressMonitor != null && progressMonitor.isCanceled())
			throw new OperationCanceledException();

		IIndexSearchRequestor orCombiner;
		if (detailLevel == IInfoConstants.NameInfo) {
			orCombiner = new OrNameCombiner(requestor);
		} else {
			orCombiner = new OrPathCombiner(requestor);
		}
		leftPattern.findIndexMatches(input, orCombiner, detailLevel, progressMonitor, scope);
		if (progressMonitor != null && progressMonitor.isCanceled())
			throw new OperationCanceledException();
		rightPattern.findIndexMatches(input, orCombiner, detailLevel, progressMonitor, scope);
	}
	/**
	 * see SearchPattern.indexEntryPrefix
	 */
	public char[] indexEntryPrefix() {

		// will never be directly invoked on a composite pattern
		return null;
	}
	/**
	 * @see SearchPattern#matchContainer()
	 */
	protected int matchContainer() {
		return leftPattern.matchContainer() | rightPattern.matchContainer();
	}
	/**
	 * @see SearchPattern#matchIndexEntry
	 */
	protected boolean matchIndexEntry() {

		return this.leftPattern.matchIndexEntry() || this.rightPattern.matchIndexEntry();
	}
	/**
	 * @see SearchPattern#matchReportReference
	 */
	protected void matchReportReference(Node reference, IEGLElement element, int accuracy, MatchLocator2 locator) throws CoreException {
		int leftLevel = this.leftPattern.matchLevel(reference, true);
		if (leftLevel == ACCURATE_MATCH || leftLevel == INACCURATE_MATCH) {
			this.leftPattern.matchReportReference(reference, element, accuracy, locator);
		} else {
			this.rightPattern.matchReportReference(reference, element, accuracy, locator);
		}
	}
	
	public String toString() {
		return this.leftPattern.toString() + "\n| " + this.rightPattern.toString(); //$NON-NLS-1$
	}

	/**
	 * @see SearchPattern#matchLevel(AstNode, boolean)
	 */
	public int matchLevel(Node node, boolean resolve) {
		switch (this.leftPattern.matchLevel(node, resolve)) {
			case IMPOSSIBLE_MATCH :
				return this.rightPattern.matchLevel(node, resolve);
			case POSSIBLE_MATCH :
				return POSSIBLE_MATCH;
			case INACCURATE_MATCH :
				int rightLevel = this.rightPattern.matchLevel(node, resolve);
				if (rightLevel != IMPOSSIBLE_MATCH) {
					return rightLevel;
				} else {
					return INACCURATE_MATCH;
				}
			case ACCURATE_MATCH :
				return ACCURATE_MATCH;
			default :
				return IMPOSSIBLE_MATCH;
		}
	}
	
	@Override
	public int matchLevel(IMember member, boolean resolve) {
		switch (this.leftPattern.matchLevel(member, resolve)) {
		case IMPOSSIBLE_MATCH:
			return this.rightPattern.matchLevel(member, resolve);
		case POSSIBLE_MATCH:
			return POSSIBLE_MATCH;
		case INACCURATE_MATCH:
			int rightLevel = this.rightPattern.matchLevel(member, resolve);
			if (rightLevel != IMPOSSIBLE_MATCH) {
				return rightLevel;
			} else {
				return INACCURATE_MATCH;
			}
		case ACCURATE_MATCH:
			return ACCURATE_MATCH;
		default:
			return IMPOSSIBLE_MATCH;
		}
	}
	
	public int matchesPartType(Name node ,org.eclipse.edt.mof.egl.Part partBinding,boolean forceQualification){
		int leftlevel = this.leftPattern.matchesPartType(node,partBinding,forceQualification);
		int rightlevel = this.rightPattern.matchesPartType(node,partBinding,forceQualification);
		return compareLevels(leftlevel,rightlevel);
	}
	
	public int matchesFunctionPartType(Name node,FunctionPart partBinding){
		int leftlevel = this.leftPattern.matchesFunctionPartType(node,partBinding);
		int rightlevel = this.rightPattern.matchesFunctionPartType(node,partBinding);
		return compareLevels(leftlevel,rightlevel);
	}
	
	public int matchesFunctionPartType(IFunction function){
		int leftlevel = this.leftPattern.matchesFunctionPartType(function);
		int rightlevel = this.rightPattern.matchesFunctionPartType(function);
		return compareLevels(leftlevel,rightlevel);
	}
	
	public int matchesPart(Part node){
		int leftlevel = this.leftPattern.matchesPart(node);
		int rightlevel = this.rightPattern.matchesPart(node);
		return compareLevels(leftlevel,rightlevel);
	}
	
	@Override
	public int matchesPart(IPart part) {
		int leftlevel = this.leftPattern.matchesPart(part);
		int rightlevel = this.rightPattern.matchesPart(part);
		return compareLevels(leftlevel,rightlevel);
	}
	
	protected int compareLevels(int leftlevel,int rightlevel){
		if (leftlevel == ACCURATE_MATCH || rightlevel == ACCURATE_MATCH){
			return ACCURATE_MATCH;
		}else if (leftlevel == INACCURATE_MATCH || rightlevel == INACCURATE_MATCH){
			return INACCURATE_MATCH;
		}else return IMPOSSIBLE_MATCH;		
	}
	
	@Override
	public int getPatternType(){
		return this.leftPattern.getPatternType() | this.rightPattern.getPatternType();
	}
}
