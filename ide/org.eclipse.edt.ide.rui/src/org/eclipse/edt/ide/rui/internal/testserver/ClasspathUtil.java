/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.internal.testserver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.osgi.framework.Bundle;

public class ClasspathUtil {
	
	private ClasspathUtil() {
		// No instances.
	}
	
	//TODO add all java projects on the EGL build path to the java build path, in case the user didn't add them to the java build path of the project
	public static List<String> buildClasspath(IProject project, ILaunchConfiguration config) throws CoreException {
		List<String> list = config.getAttribute( IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, new ArrayList<String>(10) );
		
		String entry = getClasspathEntry("org.mortbay.jetty.server");
		if (entry != null) {
			list.add(entry);
		}
		
		entry = getClasspathEntry("org.mortbay.jetty.util");
		if (entry != null) {
			list.add(entry);
		}
		
		entry = getClasspathEntry("org.eclipse.equinox.http.jetty");
		if (entry != null) {
			list.add(entry);
		}
		
		entry = getClasspathEntry("javax.servlet");
		if (entry != null) {
			list.add(entry);
		}
		
		entry = getClasspathEntry("org.eclipse.edt.ide.rui");
		if (entry != null) {
			list.add(entry);
		}
		
		entry = getClasspathEntry("org.eclipse.edt.runtime.java");
		if (entry != null) {
			list.add(entry);
		}
		
		list.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?><runtimeClasspathEntry id=\"org.eclipse.jdt.launching.classpathentry.defaultClasspath\">\n" + 
				"<memento exportedEntriesOnly=\"false\" project=\"" + project.getName() + "\"/>\n" + "</runtimeClasspathEntry>");
		return list;
	}
	
	public static String getClasspathEntry(String pluginName) {
		Bundle bundle = Platform.getBundle(pluginName);
		if (bundle != null) {
			try {
				File file = FileLocator.getBundleFile( bundle );
				String path = file.getAbsolutePath();
				if (file.isDirectory()) {
					if (!path.endsWith( File.separator )) {
						path += File.separator;
					}
					path += "bin";
				}
				
				return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><runtimeClasspathEntry externalArchive=\"" + path + "\" path=\"3\" type=\"2\"/>";
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		System.err.println("Could not retrieve path for " + pluginName + ". This may prevent the server from starting correctly!");
		return null;
	}
}
