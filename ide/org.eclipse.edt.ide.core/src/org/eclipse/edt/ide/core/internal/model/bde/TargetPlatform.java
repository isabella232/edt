/*******************************************************************************
 * Copyright © 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.model.bde;

import java.net.URL;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;

public class TargetPlatform {
	/**
	 * Returns the location of the default target platform, namely the location 
	 * of the host (running) instance of Eclipse.
	 *  
	 * @return the location of the default target platform
	 */
	public static String getDefaultLocation() {
		Location location = Platform.getInstallLocation();
		if (location != null) {
			URL url = Platform.getInstallLocation().getURL();
			IPath path = new Path(url.getFile()).removeTrailingSeparator();
			return path.toOSString();
		}
		return ""; //$NON-NLS-1$
	}
}
