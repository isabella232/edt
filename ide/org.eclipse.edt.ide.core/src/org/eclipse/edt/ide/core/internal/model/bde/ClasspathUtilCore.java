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

import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.ide.core.model.bde.IPluginBase;
import org.eclipse.edt.ide.core.model.bde.IPluginModelBase;
import org.eclipse.osgi.service.resolver.BundleDescription;


public class ClasspathUtilCore {

	public static void addLibraries(IPluginModelBase model, ArrayList result) {
		if (new File(model.getInstallLocation()).isFile()) {
			addJARdPlugin(model, result);
		} 
	}

	private static void addJARdPlugin(IPluginModelBase model, ArrayList result) {

		IPath sourcePath = getSourceAnnotation(model, "."); //$NON-NLS-1$
		if (sourcePath == null)
			sourcePath = new Path(model.getInstallLocation());

	}


	public static boolean hasExtensibleAPI(IPluginModelBase model) {
		IPluginBase pluginBase = model.getPluginBase();
		return false;
	}


	public static boolean isPatchFragment(IPluginModelBase model) {
		IPluginBase pluginBase = model.getPluginBase();
		return false;
	}


	public static boolean hasBundleStructure(IPluginModelBase model) {
		return false;
	}

	public static boolean containsVariables(String name) {
		return name.indexOf("$os$") != -1 //$NON-NLS-1$
				|| name.indexOf("$ws$") != -1 //$NON-NLS-1$
				|| name.indexOf("$nl$") != -1 //$NON-NLS-1$
				|| name.indexOf("$arch$") != -1; //$NON-NLS-1$
	}

	public static String expandLibraryName(String source) {
		if (source == null || source.length() == 0)
			return ""; //$NON-NLS-1$
		if (source.indexOf("$ws$") != -1) //$NON-NLS-1$
			source = source.replaceAll("\\$ws\\$", //$NON-NLS-1$
					"ws" + IPath.SEPARATOR ); //+ TargetPlatform.getWS()); //$NON-NLS-1$
		if (source.indexOf("$os$") != -1) //$NON-NLS-1$
			source = source.replaceAll("\\$os\\$", //$NON-NLS-1$
					"os" + IPath.SEPARATOR ); //+ TargetPlatform.getOS()); //$NON-NLS-1$
		if (source.indexOf("$nl$") != -1) //$NON-NLS-1$
			source = source.replaceAll("\\$nl\\$", //$NON-NLS-1$
					"nl" + IPath.SEPARATOR ); //+ TargetPlatform.getNL()); //$NON-NLS-1$
		if (source.indexOf("$arch$") != -1) //$NON-NLS-1$
			source = source.replaceAll("\\$arch\\$", //$NON-NLS-1$
					"arch" + IPath.SEPARATOR );//+ TargetPlatform.getOSArch()); //$NON-NLS-1$
		return source;
	}

	public static IPath getSourceAnnotation(IPluginModelBase model, String libraryName) {
		return null;
	}

	public static String getSourceZipName(String libraryName) {
		int dot = libraryName.lastIndexOf('.');
		return (dot != -1) ? libraryName.substring(0, dot) + "src.zip" : libraryName; //$NON-NLS-1$
	}


	public static IPath getPath(IPluginModelBase model, String libraryName) {
		return null;
	}

	public static String getFilename(IPluginModelBase model) {
		return new Path(model.getInstallLocation()).lastSegment();
	}

}
