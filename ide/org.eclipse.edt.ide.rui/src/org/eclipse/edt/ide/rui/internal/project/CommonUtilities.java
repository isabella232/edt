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
package org.eclipse.edt.ide.rui.internal.project;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;

public class CommonUtilities {
	public static URL getWidgetProjectURL(String resourcePluginName, String libraryResourceFolder, String projectName ) throws IOException {
		URL url = FileLocator.resolve(Platform.getBundle(resourcePluginName).getEntry(libraryResourceFolder + projectName + ".zip"));
		return url;
	}
}
