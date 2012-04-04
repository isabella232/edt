/*******************************************************************************
 * Copyright © 2009, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.results;

import org.eclipse.core.runtime.IStatus;

public interface IDeploymentResultsCollector {

	public String getName();
	public boolean hasError();
	public boolean hasWarning();
	public void addMessage(IStatus status);
	public void done();
	public boolean isDone();
}
