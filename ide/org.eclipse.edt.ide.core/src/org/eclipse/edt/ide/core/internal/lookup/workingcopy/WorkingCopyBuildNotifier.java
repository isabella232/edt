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
package org.eclipse.edt.ide.core.internal.lookup.workingcopy;

import org.eclipse.edt.compiler.internal.core.builder.CancelledException;
import org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier;

public class WorkingCopyBuildNotifier implements IBuildNotifier {

	private static final WorkingCopyBuildNotifier INSTANCE = new WorkingCopyBuildNotifier();
	private boolean cancelled;
	
	private WorkingCopyBuildNotifier(){}
	
	public static WorkingCopyBuildNotifier getInstance(){
		return INSTANCE;
	}
	
	public boolean isAborted() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setAborted(boolean aborted) {
		// TODO Auto-generated method stub

	}

	public void compiled() {
		// TODO Auto-generated method stub

	}

	public void begin() {
		// TODO Auto-generated method stub

	}

	public void checkCancel() {
		if(cancelled){
			throw new CancelledException();
		}
	}

	public void done() {
		// TODO Auto-generated method stub

	}

	public void setProgressPerEGLPart(float progress) {
		// TODO Auto-generated method stub

	}

	public void subTask(String message) {
		// TODO Auto-generated method stub

	}

	public void updateProgress(float percentComplete) {
		// TODO Auto-generated method stub

	}

	public void updateProgressDelta(float percentWorked) {
		// TODO Auto-generated method stub

	}
	
	public void setCanceled(boolean canceled){
		this.cancelled = canceled;
	}

	@Override
	public IBuildNotifier createSubNotifier(float percentFromParent) {
		// TODO Auto-generated method stub
		return null;
	}
}
