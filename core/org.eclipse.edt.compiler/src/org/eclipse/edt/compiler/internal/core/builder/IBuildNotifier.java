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
package org.eclipse.edt.compiler.internal.core.builder;

/**
 * @author cduval
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface IBuildNotifier {
	public abstract boolean isAborted();

	public abstract void setAborted(boolean aborted);

	public abstract void compiled();

	public abstract void begin();

	/**
	 * Check whether the build has been canceled.
	 */
	public abstract void checkCancel();

	public abstract void done();

	/**
	 * Sets the amount of progress to report for compiling each compilation unit.
	 */
	public abstract void setProgressPerEGLPart(float progress);

	public abstract void subTask(String message);

	public abstract void updateProgress(float percentComplete);

	public abstract void updateProgressDelta(float percentWorked);
	
	public abstract IBuildNotifier createSubNotifier(float percentFromRemaining);
}
