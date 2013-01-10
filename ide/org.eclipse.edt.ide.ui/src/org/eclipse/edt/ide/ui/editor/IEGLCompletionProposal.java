/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.editor;

import org.eclipse.jface.text.contentassist.ICompletionProposal;


public interface IEGLCompletionProposal extends ICompletionProposal {

	/**
	 * Returns the relevance of this completion proposal.
	 * <p>
	 * The relevance is used to determine if this proposal is more
	 * relevant than another proposal.</p>
	 *
	 * @return the relevance of this completion proposal in the range of [0, 100]
	 */
	int getRelevance();

}
