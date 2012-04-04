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
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.deployment.Activator;


public class SystemOutDeploymentResultsCollector implements IDeploymentResultsCollector {
	private String name;
	private boolean hasError;
	private boolean hasWarning;
	
	public SystemOutDeploymentResultsCollector(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public boolean hasError() {
		return hasError;
	}

	public boolean hasWarning() {
		return hasWarning;
	}

	public void addMessage(IStatus status) {

		String prefix = "";
		switch (status.getSeverity()) {
		case (IStatus.CANCEL):
			prefix = "Cancled: ";
			hasError = true;
			break;

		case (IStatus.ERROR):
			prefix = "Error: ";
			hasError = true;
			break;

		case (IStatus.INFO):
			prefix = "Info: ";
			break;

		case (IStatus.WARNING):
			prefix = "Warning: ";
			hasWarning = true;
			break;

		case (IStatus.OK):
			prefix = "Success: ";
			break;

		}
		System.out.println(prefix + status.getMessage());
	}

	public void done() {
		addMessage(new Status(IStatus.INFO, Activator.PLUGIN_ID, "Complete"));
	}

	public boolean isDone() {
		// TODO Auto-generated method stub
		return false;
	}

}
