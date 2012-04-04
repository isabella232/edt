/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/

package org.eclipse.edt.ide.ui.internal.quickfix.proposals;

import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.swt.graphics.Image;

public abstract class AbstractMethodCorrectionProposal extends ASTRewriteCorrectionProposal {

	public AbstractMethodCorrectionProposal(String label, IEGLFile targetEGL, int relevance, Image image, IEGLDocument document) {
		super(label, targetEGL, relevance, image, document);

	}

}
