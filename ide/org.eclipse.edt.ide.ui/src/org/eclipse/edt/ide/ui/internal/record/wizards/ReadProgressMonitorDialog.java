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
package org.eclipse.edt.ide.ui.internal.record.wizards;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

public class ReadProgressMonitorDialog extends ProgressMonitorDialog {
	public static final int SUCCESS = 0;
	public static final int CANCELED = 1;
	public static final int FAILED = 2;
	
	class Status {
		int status = SUCCESS;
	}

	private final File file;
	private final URL url;
	private final StringBuffer input = new StringBuffer();
	private final StringBuffer error = new StringBuffer();
	private final Status status = new Status();
	
	public ReadProgressMonitorDialog(Shell parent, File file) {
		super(parent);
		this.file = file;
		url = null;
	}

	public ReadProgressMonitorDialog(Shell parent, URL url) {
		super(parent);
		this.url = url;
		file = null;
	}

	public void run() throws InvocationTargetException, InterruptedException {
		status.status = SUCCESS;
		super.run(true, true, new IRunnableWithProgress() { 
				public void run(IProgressMonitor monitor) throws InvocationTargetException {
					if (url != null) { 
						try {
							input.append(readFromStream(url.openStream(), monitor));
						} catch (Exception ex) {
							error(ex.toString());
						}
					} else {
						try {
							input.append(readFromStream(new FileInputStream(file), monitor));
						} catch (Exception ex) {
							error(ex.getMessage());
						}
					}
				}
			});

	}
	
	private void error(String message) {
		error.append(message);
		status.status = FAILED;
	}
	
	private String readFromStream(InputStream is, IProgressMonitor monitor) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(is));

		String inputLine;
		
		String results = "";

		while ((inputLine = in.readLine()) != null) {
			if (monitor.isCanceled()) {
				status.status = CANCELED;
				break;
			}
			results = results + inputLine + "\n";
		}
			
		in.close();
		
		return results;
	}
	
	public boolean isStatusSuccess() {
		return status.status == SUCCESS;
	}

	public boolean isStatusCanceled() {
		return status.status == CANCELED;
	}

	public String getInput() {
		return input.toString();
	}
	
	public String getError() {
		return error.toString();
	}
}
