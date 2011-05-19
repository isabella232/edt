/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.project.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.swt.widgets.Display;

public class ProjectWizardUtils {

	public static boolean isPlatformCaseSensitive() {
		return Platform.OS_MACOSX.equals(Platform.getOS()) ? false : new
				java.io.File("a").compareTo(new java.io.File("A")) != 0;  //$NON-NLS-1$//$NON-NLS-2$
	}
	
	/**
	 * @param string
	 * @return
	 */
	public static IStatus createErrorStatus(String message) {
		return createErrorStatus(message, null);
	}
	
	/**
	 * @param string
	 * @return
	 */
	public static IStatus createErrorStatus(String message, Throwable exception) {
		return new Status(IStatus.ERROR, EDTUIPlugin.PLUGIN_ID, -1, message, exception);
	}
	
	/**
	 * Try desperately to return a valid Display. Return null if we fail.
	 * 
	 * @return Display
	 */
	public static final Display getDisplay() {
		Display display = Display.getCurrent();
		if (display == null)
			display = Display.getDefault();
		return display;
	}

}
