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
package org.eclipse.edt.ide.ui.internal.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

public class PartitionScanner extends RuleBasedPartitionScanner implements IPartitions {

	/**
	 * Creates the partitioner and sets up the appropriate rules.
	 * We only have two partions -- SQL and the rest.  We add
	 * the rules necessary for the SQL section and anything that
	 * doesn't meet those rules automatically defaults to the 
	 * IDocument.DEFAULT_CONTENT_TYPE - which uses our default
	 * CodeScanner.
	 */
	public PartitionScanner() {
		super();

		IToken sqlPartition = new Token(SQL_CONTENT_TYPE);
		IToken sqlConditionPartition = new Token(SQL_CONDITION_CONTENT_TYPE);
		// BBL - An OTI developer said we didn't need this partition, but we
		// ran into severe performance problems with his alternative and also
		// found that our multi-line comments weren't being color coded correctly.  
		IToken defaultPartition = new Token(IDocument.DEFAULT_CONTENT_TYPE);
		IToken multilineCommentPartition = new Token(EGL_MULTI_LINE_COMMENT);
		IToken singlelineCommentPartition = new Token(EGL_SINGLE_LINE_COMMENT);

		List rules = new ArrayList();

		// These rules are necessary so we don't look inside these things for the sql/end pair
		// Add rule for single line comments.
		rules.add(new EndOfLineRule(CodeConstants.EGL_SINGLE_LINE_COMMENT, singlelineCommentPartition)); //$NON-NLS-1$ //$NON-NLS-2$
		
		// Add rules for multi-line comments.
		rules.add(new MultiLineRule(CodeConstants.EGL_MULTI_LINE_COMMENT_START, CodeConstants.EGL_MULTI_LINE_COMMENT_END, multilineCommentPartition)); //$NON-NLS-1$ //$NON-NLS-2$

		// Add rule for strings and character constants.
		rules.add(new SingleLineRule(CodeConstants.EGL_STRING_COMMENT, CodeConstants.EGL_STRING_COMMENT, defaultPartition, '\\')); //$NON-NLS-1$ //$NON-NLS-2$

		// Add rule for sql section.  Have to use our own special multi line rule (handle blank, tab, or new line following
		// to be case insensitive and to ensure that there is some form of whitespace or
		// EOF following the tokens. 
		rules.add(new EGLMultiLineRule(CodeConstants.EGL_SQL_PARTITION_START, CodeConstants.EGL_SQL_PARTITION_END, sqlPartition));
		rules.add(new EGLMultiLineRule(CodeConstants.EGL_SQL_CONDITION_PARTITION_START, CodeConstants.EGL_SQL_PARTITION_END, sqlConditionPartition));

		IPredicateRule[] result = new IPredicateRule[rules.size()];
		rules.toArray(result);
		setPredicateRules(result);
	}

	public IDocument getDocument() {
		return fDocument;
	}

	public int getOffset() {
		return fOffset;
	}

}
