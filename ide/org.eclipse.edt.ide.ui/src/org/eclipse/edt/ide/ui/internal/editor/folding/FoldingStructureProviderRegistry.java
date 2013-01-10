/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.editor.folding;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.EDTUIPreferenceConstants;
import org.eclipse.edt.ide.ui.editor.IFoldingStructureProvider;

public class FoldingStructureProviderRegistry {

	private static final String EXTENSION_POINT= "eglFoldingStructureProviders"; //$NON-NLS-1$

	/** The map of descriptors, indexed by their identifiers. */
	private Map fDescriptors;

	/**
	 * Creates a new instance.
	 */
	public FoldingStructureProviderRegistry() {
	}

	/**
	 * Returns an array of <code>IJavaFoldingProviderDescriptor</code> describing
	 * all extension to the <code>foldingProviders</code> extension point.
	 *
	 * @return the list of extensions to the
	 *         <code>quickDiffReferenceProvider</code> extension point.
	 */
	public FoldingStructureProviderDescriptor[] getFoldingProviderDescriptors() {
		synchronized (this) {
			ensureRegistered();
			return (FoldingStructureProviderDescriptor[]) fDescriptors.values().toArray(new FoldingStructureProviderDescriptor[fDescriptors.size()]);
		}
	}

	/**
	 * Returns the folding provider with identifier <code>id</code> or
	 * <code>null</code> if no such provider is registered.
	 *
	 * @param id the identifier for which a provider is wanted
	 * @return the corresponding provider, or <code>null</code> if none can be
	 *         found
	 */
	public FoldingStructureProviderDescriptor getFoldingProviderDescriptor(String id) {
		synchronized (this) {
			ensureRegistered();
			return (FoldingStructureProviderDescriptor) fDescriptors.get(id);
		}
	}

	/**
	 * Instantiates and returns the provider that is currently configured in the
	 * preferences.
	 *
	 * @return the current provider according to the preferences
	 */
	public IFoldingStructureProvider getCurrentFoldingProvider() {
		String id= EDTUIPlugin.getDefault().getPreferenceStore().getString(EDTUIPreferenceConstants.EDITOR_FOLDING_PROVIDER);
		FoldingStructureProviderDescriptor desc= getFoldingProviderDescriptor(id);
		if (desc != null) {
			try {
				return desc.createProvider();
			} catch (CoreException e) {
				EDTUIPlugin.log(e);
			}
		}
		return null;
	}

	/**
	 * Ensures that the extensions are read and stored in
	 * <code>fDescriptors</code>.
	 */
	private void ensureRegistered() {
		if (fDescriptors == null)
			reloadExtensions();
	}

	/**
	 * Reads all extensions.
	 * <p>
	 * This method can be called more than once in
	 * order to reload from a changed extension registry.
	 * </p>
	 */
	public void reloadExtensions() {
		IExtensionRegistry registry= Platform.getExtensionRegistry();
		Map map= new HashMap();

		IConfigurationElement[] elements= registry.getConfigurationElementsFor(EDTUIPlugin.getPluginId(), EXTENSION_POINT);
		for (int i= 0; i < elements.length; i++) {
			FoldingStructureProviderDescriptor desc= new FoldingStructureProviderDescriptor(elements[i]);
			map.put(desc.getId(), desc);
		}

		synchronized(this) {
			fDescriptors= Collections.unmodifiableMap(map);
		}
	}
	
}
