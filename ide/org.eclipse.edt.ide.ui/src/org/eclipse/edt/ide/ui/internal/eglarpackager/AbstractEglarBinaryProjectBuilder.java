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
package org.eclipse.edt.ide.ui.internal.eglarpackager;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.edt.ide.ui.EDTUIPlugin;

public abstract class AbstractEglarBinaryProjectBuilder implements IEglarBinaryProjectBuilder {
	public static final int INTERNAL_ERROR= 10001;
	private MultiStatus fStatus;

	/**
	 * {@inheritDoc}
	 */
	public void open(EglarPackageData eglarPackage, Shell shell, MultiStatus status) throws CoreException {
		fStatus= status;
	}

	protected final void addInfo(String message, Throwable error) {
		fStatus.add(new Status(IStatus.INFO, EDTUIPlugin.getPluginId(), INTERNAL_ERROR, message, error));
	}

	protected final void addWarning(String message, Throwable error) {
		fStatus.add(new Status(IStatus.WARNING, EDTUIPlugin.getPluginId(), INTERNAL_ERROR, message, error));
	}

	protected final void addError(String message, Throwable error) {
		fStatus.add(new Status(IStatus.ERROR, EDTUIPlugin.getPluginId(), INTERNAL_ERROR, message, error));
	}

	protected final void addToStatus(CoreException ex) {
		IStatus status= ex.getStatus();
		String message= ex.getLocalizedMessage();
		if (message == null || message.length() < 1) {
			message= EglarPackagerMessages.EglarFileExportOperation_coreErrorDuringExport;
			status= new Status(status.getSeverity(), status.getPlugin(), status.getCode(), message, ex);
		}
		fStatus.add(status);
	}
	
	public void writeFileFromString(String fileContent, IPath destinationPath) throws CoreException {
	}
}
