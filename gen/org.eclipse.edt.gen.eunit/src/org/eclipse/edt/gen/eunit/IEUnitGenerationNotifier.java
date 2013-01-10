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
package org.eclipse.edt.gen.eunit;

/**
 * This interface is used for updating the progress while generating the test drivers.
 * Client maybe implement this interface in Eclipse or SDK mode to update the test driver
 * generation progress.
 * 
 */
public interface IEUnitGenerationNotifier {
	
	/**
	 * Check if user has aborted the test driver generation process or not.
	 * @return true if user abort the generation process, and false if not aborted.
	 */
	public abstract boolean isAborted();

	/**
	 * Abort the test driver generation progress.
	 * @param aborted True to abort the generation process.
	 */
	public abstract void setAborted(boolean aborted);

	/**
	 * Begin a new test driver generation progress bar.
	 */
	public abstract void begin();
	
	/**
	 * Begin a new test driver generation progress with specified generated count.
	 * @param totalWork
	 */
	public abstract void begin(int totalWork);

	/**
	 * 
	 */
	public abstract void done();

	/**
	 * Set the current task name in the generation progress. 
	 * @param message
	 */
	public abstract void setTaskName(String message);

	/**
	 * Set the progress number.
	 * @param number
	 */
	public abstract void updateProgress(int number);
}
