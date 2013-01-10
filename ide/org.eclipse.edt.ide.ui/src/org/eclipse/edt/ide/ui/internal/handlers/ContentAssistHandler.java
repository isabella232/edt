/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.handlers;

import org.eclipse.jface.text.source.ISourceViewer;

public class ContentAssistHandler extends EGLHandler {

	public void run() {
		doTextOperation(ISourceViewer.CONTENTASSIST_PROPOSALS);
	}

}
