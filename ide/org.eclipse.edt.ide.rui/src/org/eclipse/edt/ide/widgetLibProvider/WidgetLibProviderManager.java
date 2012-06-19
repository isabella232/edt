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
import org.eclipse.edt.ide.ui.internal.EGLLogger;


public class WidgetLibProviderManager {
	public static final String EXTENSIONPOINT_WIDGET_LIBRARY_PROVIDER = Activator.PLUGIN_ID + ".widgetLibraryProvider"; //$NON-NLS-1$
	public static final String PROVIDER = "provider"; //$NON-NLS-1$
	public static final String PROVIDER_CONTAINER = "providerContainer"; //$NON-NLS-1$
	
	private static WidgetLibProviderManager manager;
	private static IConfigurationElement[] rootElements;

	static {
		manager = new WidgetLibProviderManager();
		rootElements = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSIONPOINT_WIDGET_LIBRARY_PROVIDER);
	}

	public static WidgetLibProviderManager getInstance() {
		return manager;
	}

	private WidgetLibProviderManager() {
		super();
	}
	
	public IWidgetLibProvider[] getProviders(String widgetLibraryContainerID){
		List<IWidgetLibProvider> ret = new ArrayList<IWidgetLibProvider>();
		IConfigurationElement[] providerRefs = getProviderRefs(widgetLibraryContainerID);
		for(int i = 0; i < providerRefs.length; i++){
			IWidgetLibProvider iwp = getIWidgetLibProvider(providerRefs[i].getAttribute("id"));
			if(iwp != null){
				String sSelected = providerRefs[i].getAttribute(IWidgetLibProvider.PROVIDER_REF_SELECTED);
				if (sSelected!=null) { 
					boolean selected = Boolean.parseBoolean(sSelected);
					iwp.setIsSelect(selected);
				}		

				String sIsMandatory = providerRefs[i].getAttribute(IWidgetLibProvider.PROVIDER_REF_IS_MANDATORY);
				if (sIsMandatory!=null) { 
					boolean isMandatory = Boolean.parseBoolean(sIsMandatory);
					iwp.setIsMandatory(isMandatory);
				}		
				
				ret.add(iwp);
			}
		}

		return ret.toArray(new IWidgetLibProvider[ret.size()]);
	}
	
	public List<String> getProviders(String widgetLibraryContainerID, boolean isSelected){
		List<String> ret = new ArrayList<String>();
		IConfigurationElement[] providerRefs = getProviderRefs(widgetLibraryContainerID);
		for(int i = 0; i < providerRefs.length; i++){
				String sSelected = providerRefs[i].getAttribute(IWidgetLibProvider.PROVIDER_REF_SELECTED);
				if (sSelected!=null) { 
					boolean selected = Boolean.parseBoolean(sSelected);
					if(isSelected != selected){
						continue;
					}
				}
				
				ret.add(providerRefs[i].getAttribute("id"));
		}

		return ret;
	}
	
	public IConfigurationElement[] getProviderRefs(String widgetLibraryContainerID){
		
		for (int i = 0; i < rootElements.length; i++) {
			try {
				if (rootElements[i].getName().equals(PROVIDER_CONTAINER) &&  (rootElements[i].getAttribute("id").equals(widgetLibraryContainerID))) {
					IConfigurationElement[] providerRefs = rootElements[i].getChildren();
					return providerRefs;
				}
			}catch (Exception ex){
				EGLLogger.log(this, ex);
			}
		}
		EGLLogger.log(this, "Can not find the widget provider container by id: " + widgetLibraryContainerID);
		return null;
	}
	
	public WidgetLibProvider getIWidgetLibProvider(String providerID){

		for (int i = 0; i < rootElements.length; i++) {
			try {
				if (rootElements[i].getName().equals(PROVIDER) &&  (rootElements[i].getAttribute("id").equals(providerID))) {
					WidgetLibProvider provider = new WidgetLibProvider();
					provider.init(rootElements[i]);
					return provider;
				}
			} catch (Exception ex) {
				EGLLogger.log(this, ex);
			}
		}

		EGLLogger.log(this, "Can not find the widget provider by id: " + providerID);
		return null;
	}
	

}
