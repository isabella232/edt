/*******************************************************************************
 * Copyright © 2004, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.packageexplorer;

import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.swt.widgets.Shell;

public class MoveResourceAndFilesFolderOperation extends
		CopyResourceAndFilesFolderOperation {

	/**
	 * @param resourcesToCopy
	 * @param destContainers
	 * @param force
	 * @param shell
	 */
	public MoveResourceAndFilesFolderOperation(IEGLElement[] resourcesToCopy,
			IEGLElement[] destContainers, boolean force, Shell shell) {
		super(resourcesToCopy, destContainers, force, shell);
	}

	/**
	 * @param resourcesToCopy
	 * @param destContainer
	 * @param force
	 * @param shell
	 */
	public MoveResourceAndFilesFolderOperation(IEGLElement[] resourcesToCopy,
			IEGLElement destContainer, boolean force, Shell shell) {
		super(resourcesToCopy, destContainer, force, shell);
	}

	protected boolean isMove() {
		return true;
	}	
}
