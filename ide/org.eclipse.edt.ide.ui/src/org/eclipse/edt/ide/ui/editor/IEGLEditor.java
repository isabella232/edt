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
package org.eclipse.edt.ide.ui.editor;

import org.eclipse.jface.text.source.ISourceViewer;

public interface IEGLEditor {
	
	/**
	 * @param offset  The editor offset of the line.
	 * @return the line number for the offset into the file.
	 */
	public int getLineAtOffset(int offset);
	
	/**
	 * @return the source viewer.
	 */
	public ISourceViewer getViewer();
}
