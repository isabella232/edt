/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.refactoring.reorg;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.ui.internal.refactoring.util.TextChangeManager;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

public class MoveEGLFileUpdateCreator {
	
	public MoveEGLFileUpdateCreator(IPackageFragment pack){
		Assert.isNotNull(pack);
	}
	
	public TextChangeManager createChangeManager(IProgressMonitor pm, RefactoringStatus status) throws EGLModelException{
		pm.beginTask("", 5); //$NON-NLS-1$
		try{
			return new TextChangeManager();
		} finally{
			pm.done();
		}
	}
}
