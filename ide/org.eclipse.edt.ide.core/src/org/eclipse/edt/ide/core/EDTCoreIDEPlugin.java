/*******************************************************************************
 * Copyright © 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/

package org.eclipse.edt.ide.core;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.core.internal.binding.BinaryFileManager;
import org.eclipse.edt.ide.core.internal.builder.ASTManager;
import org.eclipse.edt.ide.core.internal.builder.ResourceChangeProcessor;
import org.eclipse.edt.ide.core.internal.lookup.ExternalProjectManager;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyFileInfoManager;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyResourceChangeProcessor;
import org.eclipse.edt.ide.core.internal.model.EGLModelManager;
import org.eclipse.edt.ide.core.internal.model.bde.BDEPreferencesManager;
import org.eclipse.edt.ide.core.internal.model.bde.PluginModelManager;
import org.eclipse.edt.ide.core.internal.model.bde.target.ITargetPlatformService;
import org.eclipse.edt.ide.core.internal.model.bde.target.TargetPlatformService;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IMember;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * <p>
 * Like all plug-in runtime classes (subclasses of <code>Plugin</code>), this
 * class is automatically instantiated by the platform when the plug-in gets
 * activated. Clients must not attempt to instantiate plug-in runtime classes
 * directly.
 * </p>
 * <p>
 */
public class EDTCoreIDEPlugin extends AbstractUIPlugin implements ISaveParticipant, IExecutableExtension {

	/***
	 * eglCorePlugin - This plugin.
	 */
	private static EDTCoreIDEPlugin eglCorePlugin;
	private BundleContext fBundleContext;
	
	/**
	 * Target platform service.
	 */
	private ServiceRegistration fTargetPlatformService;
	
	/**
	 * Bundle project service.
	 */
	private ServiceRegistration fBundleProjectService;
	
	private static BDEPreferencesManager fPreferenceManager;
	
	private PluginModelManager fModelManager;
	
	/***
	 * imageServicesManager - the manager used to load/save images and delegate them.
	 */
//	private static ImageServicesManager imageServicesManager =
//		ImageServicesManager.getServicesManager();
//
//	private static final ImageManager imageManager = new ImageManager();
//	private static final ValidationResourceChangeManager validationResourceChangeManager =
//		new ValidationResourceChangeManager();
//
	/**
	 * The EGL Core plugin ID.
	 */
	public static String PLUGIN_ID = "org.eclipse.edt.ide.core"; //$NON-NLS-1$
	
	public static final String BUILDER_ID = PLUGIN_ID + ".EDTBuilder"; //$NON-NLS-1$
	
	/**
	 * Name of the handle id attribute in a EGL marker.
	 */
	public static final String ATT_HANDLE_ID = "org.eclipse.edt.ide.core.internal.model.EGLModelManager.handleId"; //$NON-NLS-1$
	
	public static String EGL_UTILITIES = "org.eclipse.edt.ide.core.internal.utilities"; //$NON-NLS-1$
	
	/**
	 * Constants for extension points.
	 */
	public static final String PT_COMPILERS = "compilers"; //$NON-NLS-1$
	public static final String PT_GENERATORS = "generators"; //$NON-NLS-1$
	public static final String CLASS = "class"; //$NON-NLS-1$
	public static final String ID = "id"; //$NON-NLS-1$
	public static final String NAME = "name"; //$NON-NLS-1$
	public static final String COMPILER = "compiler"; //$NON-NLS-1$
	public static final String PREFERENCE_PAGE_ID = "preferencePageId"; //$NON-NLS-1$
	public static final String VERSION = "version"; //$NON-NLS-1$
	public static final String DESCRIPTION = "description"; //$NON-NLS-1$
	public static final String PROVIDER = "provider"; //$NON-NLS-1$
	public static final String PARENT_GEN_ID = "parentGeneratorId"; //$NON-NLS-1$
	
	/**
	 * ID used as a qualified for all EDTRuntimeContainers.
	 */
	public static final String EDT_CONTAINER_ID = "org.eclipse.edt.ide.core.EDT_CONTAINER";
	
	/**
	 * The various compilers available.
	 */
	private ICompiler[] compilers;
	
	/**
	 * The various generators available.
	 */
	private IGenerator[] generators;
	
	private final Object compilersAndGeneratorsSynchObj = new Object();
	
	private ResourceChangeProcessor resourceChangeProcessor = new ResourceChangeProcessor();

//	/**
//	 * This class will operate on state changes that took place while our plugin
//	 * was not loaded.
//	 */
//	private class LastStateChangeManager extends ImageManager {
//		/**
//		 * @see IResourceChangeListener#resourceChanged(IResourceChangeEvent)
//		 */
//		public void resourceChanged(IResourceChangeEvent event) {
//
//			// clear the built projects (there shouldn't be any)
//			clearBuiltProjects();
//
//			// add egl nature to the new projects in this event (if there are any)
//			addEGLNatureToProject(event);
//
//			// process the deltas in this event
//			
//			// Validate all files affected by any deleted files
//			ValidationResourceChangeManager valManager = new ValidationResourceChangeManager();
//			valManager.processRemovedFiles(event);
//			
//			// update the image
//			runIncrementalBuild(event);
//			
//			// validate any changed files
//			ValidationBuilder.runIncrementalBuild(IncrementalProjectBuilder.INCREMENTAL_BUILD, null, event.getDelta());
//			ValidationBuilder.initialize();
//		}
//
//	}

	/**
	 * Creates the EGL core plug-in.
	 */
	public EDTCoreIDEPlugin() {
		super();
		eglCorePlugin = this;
	}
	/**
	 * @see ISaveParticipant
	 */
	public void doneSaving(ISaveContext context) {
	}
//	/**
//	 * Insert the method's description here.
//	 * Creation date: (10/15/2001 1:01:23 PM)
//	 * @param message java.lang.String
//	 */
//	public static void fixit(String message) {
//		Logger.log(EGLCorePlugin.class, "EGLCorePlugin:" + message);  //$NON-NLS-1$
//	}
//	/**
//	 * Get the image services manager from the plugin.
//	 * Creation date: (8/20/2001 5:05:05 PM)
//	 * @return org.eclipse.edt.core.ImageServicesManager
//	 */
//	public static ImageServicesManager getImageServicesManager() {
//		return imageServicesManager;
//	}
	/**
	 * Returns the single instance of the EGL core plug-in runtime class.
	 */
	public static EDTCoreIDEPlugin getPlugin() {
		return eglCorePlugin;
	}
	
	/**
	 * Returns the singleton instance of if the {@link BDEPreferencesManager} for this bundle
	 * @return the preference manager for this bundle
	 * 
	 */
	public synchronized BDEPreferencesManager getPreferencesManager() {
		if (fPreferenceManager == null) {
			fPreferenceManager = new BDEPreferencesManager(PLUGIN_ID);
		}
		return fPreferenceManager;
	}

	/**
	 * @see ISaveParticipant
	 */
	public void prepareToSave(ISaveContext context) throws CoreException {
	}
	/**
	 * @see ISaveParticipant
	 */
	public void rollback(ISaveContext context) {
	}
//	/**
//	 * @see ISaveParticipant
//	 */
	public void saving(ISaveContext context) throws CoreException {
//		if (context.getKind() == ISaveContext.FULL_SAVE) {
//			// save the image to disk
//			imageServicesManager.saveImage();
//		}

		// ask the worbench to save state for us while we are gone
//		context.needDelta();
	}
//
//	/* (non-Javadoc)
//	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
//	 */
//	public void start(BundleContext context) throws Exception {
//		super.start(context);
//		try {
//			IWorkspace workspace = ResourcesPlugin.getWorkspace();
//			ISavedState lastState = workspace.addSaveParticipant(this, this);
//
//			// update our state based on changes that took place while we were gone
//			if (lastState != null) {
//				lastState.processResourceChangeEvents(new LastStateChangeManager());
//			}
//
//			workspace.addResourceChangeListener(
//				imageManager,
//				IResourceChangeEvent.POST_CHANGE | IResourceChangeEvent.PRE_BUILD | IResourceChangeEvent.POST_BUILD);
//			workspace.addResourceChangeListener(
//				validationResourceChangeManager,
//				IResourceChangeEvent.POST_CHANGE
//					| IResourceChangeEvent.POST_BUILD
//					| IResourceChangeEvent.PRE_BUILD);
//
//		} catch (CoreException ce) {
//			Logger.log("EGLCorePlugin.startup() - CoreException", ce); //$NON-NLS-1$
//		}
//	}
//	/* (non-Javadoc)
//	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
//	 */
//	public void stop(BundleContext context) throws Exception {
//		IWorkspace workspace = ResourcesPlugin.getWorkspace();
//		workspace.removeSaveParticipant(this);
//		workspace.removeResourceChangeListener(imageManager);
//		workspace.removeResourceChangeListener(validationResourceChangeManager);
//		super.stop(context);
//	}
//	
	/**
	 * Get the message logger.
	 */
	public Logger getLogger()
	{
		Logger logger = null;
		try
		{
			LogManager.getLogManager().readConfiguration();
			
			logger = Logger.getLogger(PLUGIN_ID);
	
			//Set the logger to log all messages:
			logger.setLevel(Level.ALL);
		}
		catch (Throwable t) {
			System.out.println("ERROR - EGL Core Plugin - getting logger"); //$NON-NLS-1$
			System.out.println("REASON: " + t.getMessage()); //$NON-NLS-1$
		}
		return logger;
	}
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		fBundleContext = context;

//		fTargetPlatformService = context.registerService(ITargetPlatformService.class.getName(), TargetPlatformService.getDefault(), new Hashtable());
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(resourceChangeProcessor, IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE
				| IResourceChangeEvent.PRE_BUILD);

		workspace.addResourceChangeListener(ASTManager.getInstance(), IResourceChangeEvent.PRE_BUILD);
		
		workspace.addResourceChangeListener(BinaryFileManager.getInstance(), IResourceChangeEvent.PRE_BUILD | IResourceChangeEvent.POST_BUILD);

		workspace.addResourceChangeListener(WorkingCopyResourceChangeProcessor.getInstance(), IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE
				| IResourceChangeEvent.POST_CHANGE);
		
		EGLModelManager manager = EGLModelManager.getEGLModelManager();
		workspace.addResourceChangeListener(
			manager.deltaProcessor,
			IResourceChangeEvent.PRE_BUILD
				| IResourceChangeEvent.POST_BUILD
				| IResourceChangeEvent.POST_CHANGE
				| IResourceChangeEvent.PRE_DELETE
				| IResourceChangeEvent.PRE_CLOSE);
		
		// request state folder creation (workaround 19885)
		EGLCore.getPlugin().getStateLocation();

		ISavedState lastState = workspace.addSaveParticipant(getPlugin(), new ISaveParticipant() {

			public void doneSaving(ISaveContext context) {
			}

			public void prepareToSave(ISaveContext context) throws CoreException {
			}

			public void rollback(ISaveContext context) {
			}

			public void saving(ISaveContext context) throws CoreException {
				//	ask the worbench to save state for us while we are gone
				context.needDelta();
			}
		});
		
		// update our state based on changes that took place while we were gone
		if(lastState == null || !WorkingCopyFileInfoManager.getInstance().hasValidState()){
			WorkingCopyResourceChangeProcessor.getInstance().initializeWorkingCopyIndex();
		}else{
			lastState.processResourceChangeEvents(new IResourceChangeListener() {
				public void resourceChanged(IResourceChangeEvent event) {
					WorkingCopyResourceChangeProcessor.getInstance().processPostChange(event.getDelta());
				}
			});
		}
		
		//Initialize the external project manager
		ExternalProjectManager.getInstance();
		
		startIndexing();
		
		if (PlatformUI.isWorkbenchRunning()) {
			new InitializeSystemPartsJob().schedule(); // Do this as the last line of start() to avoid classloader deadlock
		}
	}

	public BundleContext getBundleContext() {
		return fBundleContext;
	}
	
	/**
	 * Returns a service with the specified name or <code>null</code> if none.
	 * 
	 * @param serviceName name of service
	 * @return service object or <code>null</code> if none
	 */
	public Object acquireService(String serviceName) {
		ServiceReference reference = fBundleContext.getServiceReference(serviceName);
		if (reference == null)
			return null;
		Object service = fBundleContext.getService(reference);
		if (service != null) {
			fBundleContext.ungetService(reference);
		}
		return service;
	}
	
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		
		ITargetPlatformService tps = (ITargetPlatformService) acquireService(ITargetPlatformService.class.getName());
		if (tps instanceof TargetPlatformService) {
			//((TargetPlatformService) tps).cleanOrphanedTargetDefinitionProfiles();
		}

		if (fPreferenceManager != null) {
			fPreferenceManager.savePluginPreferences();
		}

		if (fModelManager != null) {
			fModelManager.shutdown();
			fModelManager = null;
		}
		if (fTargetPlatformService != null) {
			fTargetPlatformService.unregister();
			fTargetPlatformService = null;
		}
		if (fBundleProjectService != null) {
			fBundleProjectService.unregister();
			fBundleProjectService = null;
		}
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.removeResourceChangeListener(ASTManager.getInstance());
		workspace.removeResourceChangeListener(resourceChangeProcessor);
		workspace.removeResourceChangeListener(WorkingCopyResourceChangeProcessor.getInstance());
		
		fBundleContext = null;
	}
	
	public PluginModelManager getModelManager() {
		if (fModelManager == null) {
			fModelManager = new PluginModelManager();
		}
		return fModelManager;
	}


	public boolean areModelsInitialized() {
		return fModelManager != null && fModelManager.isInitialized();
	}
	
	public void log(String msg, Exception e) {
		getLog().log(new Status(IStatus.INFO, PLUGIN_ID, IStatus.OK, msg, e));
	}
	
	public static void log(IStatus status) {
		if (status != null)
			ResourcesPlugin.getPlugin().getLog().log(status);
	}

	public static void log(Throwable e) {
		if (e instanceof InvocationTargetException)
			e = ((InvocationTargetException) e).getTargetException();
		IStatus status = null;
		if (e instanceof CoreException) {
			status = ((CoreException) e).getStatus();
		} else if (e.getMessage() != null) {
			status = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, e.getMessage(), e);
		}
		log(status);
	}

	public static void logErrorMessage(String message) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, message, null));
	}

	public static void logException(Throwable e) {
		logException(e, null);
	}	
	
	public static void logException(Throwable e, String message) {
		if (e instanceof InvocationTargetException) {
			e = ((InvocationTargetException) e).getTargetException();
		}
		IStatus status = null;
		if (e instanceof CoreException)
			status = ((CoreException) e).getStatus();
		else {
			if (message == null)
				message = e.getMessage();
			if (message == null)
				message = e.toString();
			status = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, message, e);
		}
		log(status);
	}
	
	/**
	 * Initializes the default preferences settings for this plug-in.
	 */
	protected void initializeDefaultPluginPreferences() {

//		Preferences preferences = getPluginPreferences();
//		HashSet optionNames = EGLModelManager.OptionNames;
		// TODO Set up proper preferences
		/*
		// Builder settings
		preferences.setDefault(CORE_EGL_BUILD_RESOURCE_COPY_FILTER, ""); //$NON-NLS-1$
		optionNames.add(CORE_EGL_BUILD_RESOURCE_COPY_FILTER);
		
		preferences.setDefault(CORE_EGL_BUILD_INVALID_EGLPATH, ABORT); 
		optionNames.add(CORE_EGL_BUILD_INVALID_EGLPATH);
		
		preferences.setDefault(CORE_EGL_BUILD_DUPLICATE_RESOURCE, WARNING); 
		optionNames.add(CORE_EGL_BUILD_DUPLICATE_RESOURCE);
		
		preferences.setDefault(CORE_EGL_BUILD_CLEAN_OUTPUT_FOLDER, CLEAN); 
		optionNames.add(CORE_EGL_BUILD_CLEAN_OUTPUT_FOLDER);
		
		// EGLCore settings
		preferences.setDefault(CORE_EGL_BUILD_ORDER, IGNORE); 
		optionNames.add(CORE_EGL_BUILD_ORDER);
		
		preferences.setDefault(CORE_CIRCULAR_EGLPATH, ERROR); 
		optionNames.add(CORE_CIRCULAR_EGLPath);
		
		preferences.setDefault(CORE_INCOMPLETE_EGLPATH, ERROR); 
		optionNames.add(CORE_INCOMPLETE_EGLPath);
		
		preferences.setDefault(CORE_ENABLE_EGLPATH_EXCLUSION_PATTERNS, ENABLED); 
		optionNames.add(CORE_ENABLE_EGLPath_EXCLUSION_PATTERNS);
		
		preferences.setDefault(CORE_ENABLE_EGLPATH_MULTIPLE_OUTPUT_LOCATIONS, ENABLED); 
		optionNames.add(CORE_ENABLE_EGLPath_MULTIPLE_OUTPUT_LOCATIONS);
		
		// encoding setting comes from resource plug-in
		optionNames.add(CORE_ENCODING);
		
		// Formatter settings
		preferences.setDefault(FORMATTER_NEWLINE_OPENING_BRACE, DO_NOT_INSERT); 
		optionNames.add(FORMATTER_NEWLINE_OPENING_BRACE);
		
		preferences.setDefault(FORMATTER_NEWLINE_CONTROL, DO_NOT_INSERT);
		optionNames.add(FORMATTER_NEWLINE_CONTROL);
		
		preferences.setDefault(FORMATTER_CLEAR_BLANK_LINES, PRESERVE_ONE); 
		optionNames.add(FORMATTER_CLEAR_BLANK_LINES);
		
		preferences.setDefault(FORMATTER_NEWLINE_ELSE_IF, DO_NOT_INSERT);
		optionNames.add(FORMATTER_NEWLINE_ELSE_IF);
		
		preferences.setDefault(FORMATTER_NEWLINE_EMPTY_BLOCK, INSERT); 
		optionNames.add(FORMATTER_NEWLINE_EMPTY_BLOCK);
		
		preferences.setDefault(FORMATTER_LINE_SPLIT, "80"); //$NON-NLS-1$
		optionNames.add(FORMATTER_LINE_SPLIT);
		
		preferences.setDefault(FORMATTER_COMPACT_ASSIGNMENT, NORMAL); 
		optionNames.add(FORMATTER_COMPACT_ASSIGNMENT);
		
		preferences.setDefault(FORMATTER_TAB_CHAR, TAB); 
		optionNames.add(FORMATTER_TAB_CHAR);
		
		preferences.setDefault(FORMATTER_TAB_SIZE, "4"); //$NON-NLS-1$ 
		optionNames.add(FORMATTER_TAB_SIZE);
		
		preferences.setDefault(FORMATTER_SPACE_CASTEXPRESSION, INSERT); //$NON-NLS-1$ 
		optionNames.add(FORMATTER_SPACE_CASTEXPRESSION);
		
		// CodeAssist settings
		preferences.setDefault(CODEASSIST_VISIBILITY_CHECK, DISABLED); //$NON-NLS-1$
		optionNames.add(CODEASSIST_VISIBILITY_CHECK);
		
		preferences.setDefault(CODEASSIST_IMPLICIT_QUALIFICATION, DISABLED); //$NON-NLS-1$
		optionNames.add(CODEASSIST_IMPLICIT_QUALIFICATION);
		
		preferences.setDefault(CODEASSIST_FIELD_PREFIXES, ""); //$NON-NLS-1$
		optionNames.add(CODEASSIST_FIELD_PREFIXES);
		
		preferences.setDefault(CODEASSIST_STATIC_FIELD_PREFIXES, ""); //$NON-NLS-1$
		optionNames.add(CODEASSIST_STATIC_FIELD_PREFIXES);
		
		preferences.setDefault(CODEASSIST_LOCAL_PREFIXES, ""); //$NON-NLS-1$
		optionNames.add(CODEASSIST_LOCAL_PREFIXES);
		
		preferences.setDefault(CODEASSIST_ARGUMENT_PREFIXES, ""); //$NON-NLS-1$
		optionNames.add(CODEASSIST_ARGUMENT_PREFIXES);
		
		preferences.setDefault(CODEASSIST_FIELD_SUFFIXES, ""); //$NON-NLS-1$
		optionNames.add(CODEASSIST_FIELD_SUFFIXES);
		
		preferences.setDefault(CODEASSIST_STATIC_FIELD_SUFFIXES, ""); //$NON-NLS-1$
		optionNames.add(CODEASSIST_STATIC_FIELD_SUFFIXES);
		
		preferences.setDefault(CODEASSIST_LOCAL_SUFFIXES, ""); //$NON-NLS-1$
		optionNames.add(CODEASSIST_LOCAL_SUFFIXES);
		
		preferences.setDefault(CODEASSIST_ARGUMENT_SUFFIXES, ""); //$NON-NLS-1$
		optionNames.add(CODEASSIST_ARGUMENT_SUFFIXES);
		*/
	}
	
	/**
	 * Shutdown the EGLCore plug-in.
	 * <p>
	 * De-registers the EGLModelManager as a resource changed listener and save participant.
	 * <p>
	 * @see org.eclipse.core.runtime.Plugin#shutdown()
	 */
	public void shutdown() {

//		savePluginPreferences();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.removeResourceChangeListener(EGLModelManager.getEGLModelManager().deltaProcessor);
		workspace.removeSaveParticipant(getPlugin());

		((EGLModelManager) EGLModelManager.getEGLModelManager()).shutdown();
	}
	
	public URL getInstallURL() {
		try {
			return FileLocator.resolve(getPlugin().getBundle().getEntry("/")); //$NON-NLS-1$
		} catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * Initiate the background indexing process.
	 * This should be deferred after the plugin activation.
	 */
	private void startIndexing() {

		EGLModelManager.getEGLModelManager().getIndexManager().reset();
	}
	
	/**
	 * Configures the given marker for the given EGL element.
	 * Used for markers, which denote a EGL element rather than a resource.
	 *
	 * @param marker the marker to be configured
	 * @param element the EGL element for which the marker needs to be configured
	 * @exception CoreException if the <code>IMarker.setAttribute</code> on the marker fails
	 */
	public void configureEGLElementMarker(IMarker marker, IEGLElement element) throws CoreException {
		if (element instanceof IMember)
			element = ((IMember) element).getEGLFile();
		if (marker != null && element != null)
			marker.setAttribute(ATT_HANDLE_ID, element.getHandleIdentifier());
	}
	
	/* (non-EGLdoc)
	 * Method declared on IExecutableExtension.
	 * Record any necessary initialization data from the plugin.
	 */
	public void setInitializationData(IConfigurationElement cfig, String propertyName, Object data) throws CoreException {
	}
	
	private void setupCompilersAndGenerators() {
		// First the compilers
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(PLUGIN_ID + "." + PT_COMPILERS); //$NON-NLS-1$
		if (elements != null) {
			List<ICompiler> compilers = new ArrayList();
			for (int i = 0; i < elements.length; i++) {
				try {
					Object command = elements[i].createExecutableExtension(CLASS);
					if (command instanceof ICompiler) {
						ICompiler compiler = (ICompiler)command;
						// An ID is required.
						String id = elements[i].getAttribute(ID);
						if (id != null && id.length() != 0) {
							compiler.setId(id);
							compiler.setName(elements[i].getAttribute(NAME));
							compiler.setPreferencePageId(elements[i].getAttribute(PREFERENCE_PAGE_ID));
							compiler.setVersion(elements[i].getAttribute(VERSION));
							compilers.add(compiler);
						}
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
			this.compilers = compilers.toArray(new ICompiler[compilers.size()]);
		}
		else {
			this.compilers = new ICompiler[0];
		}
		
		// Now the generators
		elements = Platform.getExtensionRegistry().getConfigurationElementsFor(PLUGIN_ID + "." + PT_GENERATORS); //$NON-NLS-1$
		if (elements != null) {
			List<IGenerator> gens = new ArrayList();
			for (int i = 0; i < elements.length; i++) {
				try {
					Object command = elements[i].createExecutableExtension(CLASS);
					if (command instanceof IGenerator) {
						IGenerator gen = (IGenerator)command;
						
						// An ID is required.
						String id = elements[i].getAttribute(ID);
						if (id != null && id.length() != 0) {
							// As well as a compiler.
							ICompiler compiler = findCompiler(elements[i].getAttribute(COMPILER));
							if (compiler != null) {
								gen.setId(id);
								gen.setName(elements[i].getAttribute(NAME));
								gen.setCompiler(compiler);
								gen.setVersion(elements[i].getAttribute(VERSION));
								gen.setDescription(elements[i].getAttribute(DESCRIPTION));
								gen.setParentGeneratorId(elements[i].getAttribute(PARENT_GEN_ID));
								gen.setProvider(elements[i].getAttribute(PROVIDER));
								gens.add(gen);
								compiler.addGenerator(gen);
							}
						}
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
			generators = gens.toArray(new IGenerator[gens.size()]);
		}
		else {
			generators = new IGenerator[0];
		}
	}
	
	/**
	 * This method assumes the compilers have been initialized already.
	 */
	private ICompiler findCompiler(String id) {
		if (id == null || id.length() == 0 || compilers == null) {
			return null;
		}
		
		for (int i = 0; i < compilers.length; i++) {
			if (compilers[i].getId().equals(id)) {
				return compilers[i];
			}
		}
		
		return null;
	}
	
	/**
	 * @return a non-null array of IGenerators.
	 */
	public IGenerator[] getGenerators() {
		synchronized(compilersAndGeneratorsSynchObj) {
			if (generators == null) {
				setupCompilersAndGenerators();
			}
		}
		return generators;
	}
	
	/**
	 * @return a non-null array of ICompilers.
	 */
	public ICompiler[] getCompilers() {
		synchronized(compilersAndGeneratorsSynchObj) {
			if (compilers == null) {
				setupCompilersAndGenerators();
			}
		}
		return compilers;
	}
}
