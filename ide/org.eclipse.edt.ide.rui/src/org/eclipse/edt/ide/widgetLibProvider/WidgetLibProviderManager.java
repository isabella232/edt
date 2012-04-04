/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.widgetLibProvider;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.ide.rui.internal.Activator;


public class WidgetLibProviderManager {
	public static final String EXTENSIONPOINT_WIDGET_LIBRARY_PROVIDER = Activator.PLUGIN_ID + ".widgetLibraryProvider"; //$NON-NLS-1$
	public static final String PROVIDER = "provider"; //$NON-NLS-1$
	
	private static WidgetLibProviderManager manager;

	static {
		manager = new WidgetLibProviderManager();
	}

	public static WidgetLibProviderManager getInstance() {
		return manager;
	}

	private WidgetLibProviderManager() {
		super();
	}
	
	public IWidgetLibProvider[] getProviders(){
		List<IWidgetLibProvider> ret = new ArrayList<IWidgetLibProvider>();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(EXTENSIONPOINT_WIDGET_LIBRARY_PROVIDER);
		
		for (int i = 0; i < elements.length; i++) {
			try {
				if (elements[i].getName().equals(PROVIDER) ) {
					WidgetLibProvider provider = new WidgetLibProvider();
					provider.init(elements[i]);
					ret.add(provider);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return ret.toArray(new IWidgetLibProvider[ret.size()]);
	}
}
