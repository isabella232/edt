/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.refactoring;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.ui.internal.refactoring.reorg.EGLCopyProcessor;
import org.eclipse.edt.ide.ui.internal.refactoring.reorg.NewNameQueries;
import org.eclipse.edt.ide.ui.internal.refactoring.reorg.ReorgQueries;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.participants.CopyRefactoring;
import org.eclipse.swt.widgets.Shell;

public class ReorgCopyStarter {
	
	private final EGLCopyProcessor fCopyProcessor;

	private ReorgCopyStarter(EGLCopyProcessor copyProcessor) {
		Assert.isNotNull(copyProcessor);
		fCopyProcessor= copyProcessor;
	}
	
	public static ReorgCopyStarter create(IEGLElement[] eglElements, IResource[] resources, IEGLElement destination) throws EGLModelException {
		Assert.isNotNull(eglElements);
		Assert.isNotNull(resources);
		Assert.isNotNull(destination);
		EGLCopyProcessor copyProcessor= EGLCopyProcessor.create(resources, eglElements);
		if (copyProcessor == null)
			return null;
		if (! copyProcessor.setDestination(destination).isOK())
			return null;
		return new ReorgCopyStarter(copyProcessor);
	}

	public static ReorgCopyStarter create(IEGLElement[] eglElements, IResource[] resources, IResource destination) throws EGLModelException {
		Assert.isNotNull(eglElements);
		Assert.isNotNull(resources);
		Assert.isNotNull(destination);
		EGLCopyProcessor copyProcessor= EGLCopyProcessor.create(resources, eglElements);
		if (copyProcessor == null)
			return null;
		if (! copyProcessor.setDestination(destination).isOK())
			return null;
		return new ReorgCopyStarter(copyProcessor);
	}
	
	public void run(Shell parent) throws InterruptedException, InvocationTargetException {
		IRunnableContext context= new ProgressMonitorDialog(parent);
		fCopyProcessor.setNewNameQueries(new NewNameQueries(parent));
		fCopyProcessor.setReorgQueries(new ReorgQueries(parent));
		new RefactoringExecutionHelper(new CopyRefactoring(fCopyProcessor), RefactoringCore.getConditionCheckingFailedSeverity(), false, parent, context).perform(false);
	}
}
