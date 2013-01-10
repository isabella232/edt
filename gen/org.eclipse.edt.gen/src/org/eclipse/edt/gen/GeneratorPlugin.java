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
package org.eclipse.edt.gen;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class GeneratorPlugin extends Plugin {
	
	public static String PLUGIN_ID = "org.eclipse.edt.gen"; //$NON-NLS-1$
	
	private static GeneratorPlugin plugin;
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}
	
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}
	
	public static GeneratorPlugin getPlugin() {
		return plugin;
	}
}
