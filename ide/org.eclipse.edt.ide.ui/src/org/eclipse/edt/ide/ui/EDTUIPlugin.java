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
package org.eclipse.edt.ide.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.edt.ide.ui.internal.ResourceAdapterFactory;
import org.eclipse.edt.ide.ui.internal.editor.DocumentProvider;
import org.eclipse.edt.ide.ui.internal.editor.ProblemMarkerManager;
import org.eclipse.edt.ide.ui.internal.editor.folding.FoldingStructureProviderRegistry;
import org.eclipse.edt.ide.ui.internal.templates.CoreContextType;
import org.eclipse.edt.ide.ui.internal.templates.EGLTemplateStore;
import org.eclipse.edt.ide.ui.internal.viewsupport.ImageDescriptorRegistry;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

/**
 * The activator class controls the plug-in life cycle
 */
public class EDTUIPlugin extends AbstractUIPlugin {

	private static final String TEMPLATES_KEY= "org.eclipse.edt.ide.ui.text.custom_templates"; //$NON-NLS-1$
	
	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.edt.ide.ui";
	public static final String EGL_EDITOR_ID = "org.eclipse.edt.ide.ui.EGLEditor"; //$NON-NLS-1$
	public static final int INTERNAL_ERROR= 10001;

	// The shared instance
	private static EDTUIPlugin plugin;

	public static final String CLASS = "class"; //$NON-NLS-1$

	private ProblemMarkerManager fProblemMarkerManager;
	private DocumentProvider fEGLDocumentProvider;
	private ImageDescriptorRegistry fImageDescriptorRegistry;
	private FoldingStructureProviderRegistry fFoldingStructureProviderRegistry;
	private EGLTemplateStore fDefaultTemplateStore;
	private TemplateStore fTemplateStore;
	private ContextTypeRegistry fContextTypeRegistry;	
	private IPropertyChangeListener propertyChangeListener; 
	private ResourceAdapterFactory fResourceAdapterFactory; 
	private FormColors formColors;
	
	
	/**
	 * The constructor
	 */
	public EDTUIPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		registerAdapters();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		unregisterAdapters();
	}
	
	private void registerAdapters() {
		fResourceAdapterFactory= new ResourceAdapterFactory();
		IAdapterManager manager= Platform.getAdapterManager();		
		manager.registerAdapters(fResourceAdapterFactory, IResource.class);
	}
	
	private void unregisterAdapters() {
		IAdapterManager manager= Platform.getAdapterManager();
		manager.unregisterAdapters(fResourceAdapterFactory);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static EDTUIPlugin getDefault() {
		return plugin;
	}
	
	public static String getPluginId() {
		return PLUGIN_ID;
	}
	
	/**
	 * Get the message logger.
	 */
	public Logger getLogger()
	{
		Logger logger = null;
		try
		{
		LogManager.getLogManager().readConfiguration();

		
		//Create a logger named 'HyadesLoggingJava14Sample':
		logger = Logger.getLogger(PLUGIN_ID);

		//Set the logger to log all messages:
		logger.setLevel(Level.ALL);
		}
		catch (Throwable t) {
			System.out.println("ERROR - EGL UI Plugin - getting logger unsuccessfully!"); //$NON-NLS-1$
			System.out.println("REASON: " + t.getMessage()); //$NON-NLS-1$
		}
		return logger;
	}
	
	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}
	
	public static void log(Throwable e) {
		// TODO Use real NLS strings
		log(new Status(IStatus.ERROR, getPluginId(), INTERNAL_ERROR, "EDTUIPlugin.internal_error", e)); //$NON-NLS-1$
	}
	
	public static void logErrorMessage(String message) {
		log(new Status(IStatus.ERROR, getPluginId(), INTERNAL_ERROR, message, null));
	}
	
	public synchronized DocumentProvider getEGLDocumentProvider() {
		if (fEGLDocumentProvider == null)
			fEGLDocumentProvider = new DocumentProvider();
		return fEGLDocumentProvider;
	}
	
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}
	
	public static ImageDescriptorRegistry getImageDescriptorRegistry() {
		return getDefault().internalGetImageDescriptorRegistry();
	}
	
	private synchronized ImageDescriptorRegistry internalGetImageDescriptorRegistry() {
		if (fImageDescriptorRegistry == null)
			fImageDescriptorRegistry= new ImageDescriptorRegistry();
		return fImageDescriptorRegistry;
	}
	
	public static IWorkbenchPage getActivePage() {
		return getDefault().internalGetActivePage();
	}
	
	private IWorkbenchPage internalGetActivePage() {
		IWorkbenchWindow window = getWorkbench().getActiveWorkbenchWindow();
		if (window == null)
			return null;
		return getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}
	
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}
	
	public static Shell getActiveWorkbenchShell() {
		return getActiveWorkbenchWindow().getShell();
	}
	
	/**
	 * Returns an array of all editors that have an unsaved content. If the identical content is 
	 * presented in more than one editor, only one of those editor parts is part of the result.
	 * 
	 * @return an array of all dirty editor parts.
	 */
	public static IEditorPart[] getDirtyEditors() {
		Set inputs= new HashSet();
		List result= new ArrayList(0);
		IWorkbench workbench= getDefault().getWorkbench();
		IWorkbenchWindow[] windows= workbench.getWorkbenchWindows();
		for (int i= 0; i < windows.length; i++) {
			IWorkbenchPage[] pages= windows[i].getPages();
			for (int x= 0; x < pages.length; x++) {
				IEditorPart[] editors= pages[x].getDirtyEditors();
				for (int z= 0; z < editors.length; z++) {
					IEditorPart ep= editors[z];
					IEditorInput input= ep.getEditorInput();
					if (!inputs.contains(input)) {
						inputs.add(input);
						result.add(ep);
					}
				}
			}
		}
		return (IEditorPart[])result.toArray(new IEditorPart[result.size()]);
	}
	
	public synchronized ProblemMarkerManager getProblemMarkerManager() {
		if (fProblemMarkerManager == null)
			fProblemMarkerManager= new ProblemMarkerManager();
		return fProblemMarkerManager;
	}
	
	/**
	 * Returns the registry of the extensions to the <code>org.eclipse.jdt.ui.javaFoldingStructureProvider</code>
	 * extension point.
	 * 
	 * @return the registry of contributed <code>IJavaFoldingStructureProvider</code>
	 * @since 3.0
	 */
	public synchronized FoldingStructureProviderRegistry getFoldingStructureProviderRegistry() {
		if (fFoldingStructureProviderRegistry == null)
			fFoldingStructureProviderRegistry= new FoldingStructureProviderRegistry();
		return fFoldingStructureProviderRegistry;
	}
	
	/**
	 * Returns the template context type registry for the java plugin.
	 * 
	 * @return the template context type registry for the java plugin
	 * @since 3.0
	 */
	public ContextTypeRegistry getTemplateContextRegistry() {
		if (fContextTypeRegistry == null) {
			fContextTypeRegistry= new ContributionContextTypeRegistry();
			
			//EGLComponent
			fContextTypeRegistry.addContextType(new CoreContextType());
//			fContextTypeRegistry.addContextType(new RUIContextType());
		}

		return fContextTypeRegistry;
	}
	
	/**
	 * Returns the template store for the java editor templates.
	 * 
	 * @return the template store for the java editor templates
	 * @since 3.0
	 */
	public TemplateStore getTemplateStore() {
		if (fTemplateStore == null) {
			fTemplateStore= new EGLTemplateStore(getTemplateContextRegistry(), getPreferenceStore(), TEMPLATES_KEY);
			fDefaultTemplateStore= new EGLTemplateStore(getTemplateContextRegistry(), getPreferenceStore(), TEMPLATES_KEY);

			try {
				fTemplateStore.load();
				fDefaultTemplateStore.load();
			} catch (IOException e) {
				log(e);
			}
			
			//Remove old listener if it exists
			if (propertyChangeListener != null)
				getPreferenceStore().removePropertyChangeListener(propertyChangeListener);
			
			//Add listener to reload templates otherwise aftertemplate import the custom templates
			//do not show until the workspace is closed and reopened.
			propertyChangeListener = new IPropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent event) {
					if (event.getProperty().equals(TEMPLATES_KEY)) {
						try {
							fTemplateStore.load();
							fDefaultTemplateStore.load();
						} catch (IOException e) {
							log(e);
						}
					}
				}
			};
			
			getPreferenceStore().addPropertyChangeListener(propertyChangeListener);
}
		return fTemplateStore;
	}
	
	public TemplateStore getDefaultTemplateStore() {
		return fDefaultTemplateStore;
	}
	
	/**
	 * Return the UI plug-in preferences, which is the same as the UI 
	 * preference store.
	 * 
	 * @return UI plug-in preferences
	 */
	public IEclipsePreferences getUIPluginPreferences() {
		return new InstanceScope().getNode( PLUGIN_ID );
	}
	
	public void saveUIPluginPreferences() {
		IEclipsePreferences prefs =  getUIPluginPreferences();
		try {
		    prefs.flush();
		} catch(BackingStoreException e) {
		   log( e );
		}
	}
	
	public FormColors getFormColors(Display display) {
		if (formColors == null) {
			formColors = new FormColors(display);
			formColors.markShared();
		}
		return formColors;
	}
	
}
