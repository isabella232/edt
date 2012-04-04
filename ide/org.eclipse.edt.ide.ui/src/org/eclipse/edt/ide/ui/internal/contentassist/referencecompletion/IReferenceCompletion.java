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
package org.eclipse.edt.ide.ui.internal.contentassist.referencecompletion;

import java.util.List;

import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

public interface IReferenceCompletion {
	public List computeCompletionProposals(
			ParseStack parseStack,
			String prefix,
			ITextViewer viewer,
			int documentOffset,
			IEditorPart editor);
}
