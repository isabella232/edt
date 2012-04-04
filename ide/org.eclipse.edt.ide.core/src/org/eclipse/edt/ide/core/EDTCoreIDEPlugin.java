/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
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
import org.eclipse.core.resources.IProject;
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
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.edt.ide.core.internal.builder.ASTManager;
import org.eclipse.edt.ide.core.internal.builder.ProjectSettingsListenerManager;
import org.eclipse.edt.ide.core.internal.builder.ResourceChangeProcessor;
import org.eclipse.edt.ide.core.internal.generation.GenerationBuildManager;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyFileInfoManager;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyResourceChangeProcessor;
import org.eclipse.edt.ide.core.internal.model.EGLModelManager;
import org.eclipse.edt.ide.core.internal.model.EGLProject;
import org.eclipse.edt.ide.core.internal.refactor.SettingUpdateResourceChangeListener;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IMember;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.prefs.BackingStoreException;

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
	
	private IPropertyChangeListener propertyChangeListener = new PreferenceListener();
	
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
	
	public static final String GENERATION_BUILDER_ID = PLUGIN_ID + ".EDTGenBuilder"; //$NON-NLS-1$
	
	/**
	 * ID for generation error markers.
	 */
	public static final String GENERATION_PROBLEM = PLUGIN_ID + ".generationProblem"; //$NON-NLS-1$
	
	/**
	 * Name of the handle id attribute in a EGL marker.
	 */
	public static final String ATT_HANDLE_ID = "org.eclipse.edt.ide.core.internal.model.EGLModelManager.handleId"; //$NON-NLS-1$
	
	public static String EGL_UTILITIES = "org.eclipse.edt.ide.core.internal.utilities"; //$NON-NLS-1$
	
	/**
	 * Constants for extension points.
	 */
	public static final String PT_GENERATIONCONTRIBUTORS = "GenerationContributors"; //$NON-NLS-1$
	public static final String PT_COMPILERS = "compilers"; //$NON-NLS-1$
	public static final String PT_GENERATORS = "generators"; //$NON-NLS-1$
	public static final String CLASS = "class"; //$NON-NLS-1$
	public static final String ID = "id"; //$NON-NLS-1$
	public static final String NAME = "name"; //$NON-NLS-1$
	public static final String COMPILER = "compiler"; //$NON-NLS-1$
	public static final String REQUIRES = "requires"; //$NON-NLS-1$
	public static final String PREFERENCE_PAGE_ID = "preferencePageId"; //$NON-NLS-1$
	public static final String VERSION = "version"; //$NON-NLS-1$
	public static final String DESCRIPTION = "description"; //$NON-NLS-1$
	public static final String PROVIDER = "provider"; //$NON-NLS-1$
	public static final String PARENT_GEN_ID = "parentGeneratorId"; //$NON-NLS-1$
	public static final String LANGUAGE = "language"; //$NON-NLS-1$
	public static final String ENABLED_WITH = "enabledWith"; //$NON-NLS-1$

	public static final String BUNDLEID = "bundleId";
	public static final String BUNDLEROOT = "bundleRoot";
	public static final String VERSIONRANGE = "versionRange";
	public static final String SOURCEBUNDLEID = "sourceBundleId";
	public static final String SOURCEBUNDLEROOT = "sourceBundleRoot";
	public static final String JAVADOCLOCATION = "javadocLocation";
	public static final String RUNTIMECONTAINERENTRY = "runtimeContainerEntry";
	public static final String RUNTIMECONTAINER = "runtimeContainer";


	/**
	 * Control flag for features 
	 */
	public static final boolean SUPPORT_SOAP = false;
	
	
	/**
	 * ID used as a qualified for all EDTRuntimeContainers.
	 */
	public static final String EDT_CONTAINER_ID = "org.eclipse.edt.ide.core.EDT_CONTAINER";
	
	/**
	 * The various compilers available.
	 */
	private IIDECompiler[] compilers;
	
	/**
	 * The various generators available.
	 */
	private IGenerator[] generators;
	
	private final Object compilersAndGeneratorsSynchObj = new Object();
	
	private ResourceChangeProcessor resourceChangeProcessor = new ResourceChangeProcessor();
	
	private class PreferenceListener implements IPropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			if (EDTCorePreferenceConstants.COMPILER_ID.equals(event.getProperty())) {
				// Touch all projects using the default compiler so that autobuild is triggered.
				IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
				for (IProject project : projects) {
					if (project.isAccessible() && EGLProject.hasEGLNature(project) && ProjectSettingsUtility.getCompilerId(project) == null) {
						try {
							project.touch(null);
						}
						catch (CoreException e) {
							log(e);
						}
					}
				}
			}
			//TODO this next check is still incomplete. We need to also verify that the modified gen settings were for the default compiler.
			// If you change a non-default compiler's settings, we should not regenerate!
			else if (EDTCorePreferenceConstants.GENERATOR_IDS.equals(event.getProperty())
					|| EDTCorePreferenceConstants.BUILD_FLAG.equals(event.getProperty())) {
				IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
				for (IProject project : projects) {
					if (project.isAccessible() && EGLProject.hasEGLNature(project)){
						String compilerId = ProjectSettingsUtility.getCompilerId(project);
						if(compilerId == null) {
							// No compiler means the project cannot be overriding any generation settings.
							// Since the workspace generation settings have changed, force a regenerate.
							try {
								GenerationBuildManager.getInstance().setProjectState(project, false);
								project.touch(null);
							}
							catch (CoreException e) {
								log(e);
							}
						}
						else if (compilerId.equals(EDTCoreIDEPlugin.getPlugin().getPreferenceStore().getString(EDTCorePreferenceConstants.COMPILER_ID))) {
							// If the project itself has generatorIds then that means nothing in the project could possibly be using the workspace
							// generator settings and we can skip regenerating.
							if (ProjectSettingsUtility.getGeneratorIds(project) == null) {
								//TODO right now we regenerate the entire project, but that hurts performance. It could be that some packages or files are overriding
								// the workspace generation settings. These files would not need to be regenerated. A full solution would be to walk the source
								// folders in the project and touch all IRs that correspond to .egl files that do not override the generation settings.
								// See ProjectSettingsListenerManager.GeneratorPreferenceListener for how to do this.
								try {
									GenerationBuildManager.getInstance().setProjectState(project, false);
									project.touch(null);
								}
								catch (CoreException e) {
									log(e);
								}
							}
						}
					}
				}
			}
		}
	}

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

		getPreferenceStore().addPropertyChangeListener(propertyChangeListener);
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		EGLModelManager manager = EGLModelManager.getEGLModelManager();
		workspace.addResourceChangeListener(
			manager.deltaProcessor,
			IResourceChangeEvent.PRE_BUILD
				| IResourceChangeEvent.POST_BUILD
				| IResourceChangeEvent.POST_CHANGE
				| IResourceChangeEvent.PRE_DELETE
				| IResourceChangeEvent.PRE_CLOSE);

		workspace.addResourceChangeListener(resourceChangeProcessor, IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE
				| IResourceChangeEvent.PRE_BUILD | IResourceChangeEvent.POST_CHANGE);

		workspace.addResourceChangeListener(ASTManager.getInstance(), IResourceChangeEvent.PRE_BUILD);
		
		workspace.addResourceChangeListener(WorkingCopyResourceChangeProcessor.getInstance(), IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE
				| IResourceChangeEvent.POST_CHANGE);

		workspace.addResourceChangeListener(SettingUpdateResourceChangeListener.getInstance(), IResourceChangeEvent.PRE_DELETE | IResourceChangeEvent.POST_CHANGE);

		ProjectSettingsListenerManager.getInstance(); // Make sure existing projects register preference listeners
		
		// request state folder creation (workaround 19885)
		EGLCore.getPlugin().getStateLocation();

		ISavedState lastState = workspace.addSaveParticipant(PLUGIN_ID, new ISaveParticipant() {

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
		
		startIndexing();
		
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
		
		getPreferenceStore().removePropertyChangeListener(propertyChangeListener);
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.removeResourceChangeListener(ASTManager.getInstance());
		workspace.removeResourceChangeListener(resourceChangeProcessor);
		workspace.removeResourceChangeListener(WorkingCopyResourceChangeProcessor.getInstance());
		workspace.removeResourceChangeListener(EGLModelManager.getEGLModelManager().deltaProcessor);
		workspace.removeSaveParticipant(PLUGIN_ID);

		((EGLModelManager) EGLModelManager.getEGLModelManager()).shutdown();
		
		fBundleContext = null;
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
			List<IIDECompiler> compilers = new ArrayList();
			for (int i = 0; i < elements.length; i++) {
				try {
					Object command = elements[i].createExecutableExtension(CLASS);
					if (command instanceof IIDECompiler) {
						IIDECompiler compiler = (IIDECompiler)command;
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
			this.compilers = compilers.toArray(new IIDECompiler[compilers.size()]);
		}
		else {
			this.compilers = new IIDECompiler[0];
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
							IIDECompiler compiler = findCompiler(elements[i].getAttribute(COMPILER));
							if (compiler != null) {
								gen.setId(id);
								gen.setName(elements[i].getAttribute(NAME));
								gen.setCompiler(compiler);
								gen.setVersion(elements[i].getAttribute(VERSION));
								gen.setDescription(elements[i].getAttribute(DESCRIPTION));
								gen.setParentGeneratorId(elements[i].getAttribute(PARENT_GEN_ID));
								gen.setLanguage(elements[i].getAttribute(LANGUAGE));
								gen.setProvider(elements[i].getAttribute(PROVIDER));
								gen.setEnabledWith(elements[i].getAttribute(ENABLED_WITH));
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
	private IIDECompiler findCompiler(String id) {
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
	public IIDECompiler[] getCompilers() {
		synchronized(compilersAndGeneratorsSynchObj) {
			if (compilers == null) {
				setupCompilersAndGenerators();
			}
		}
		return compilers;
	}
	
	/**
	 * Return the Core plug-in preferences, which is the same as the Core
	 * preference store.
	 * 
	 * @return Core plug-in preferences
	 */
	@SuppressWarnings("deprecation")
	public IEclipsePreferences getCorePluginPreferences() {
		// Must use deprecated API to support Eclipse 3.6
		return new InstanceScope().getNode( PLUGIN_ID );
	}
	
	public void saveCorePluginPreferences() {
		IEclipsePreferences prefs =  getCorePluginPreferences();
		try {
		    prefs.flush();
		} catch(BackingStoreException e) {
		   log( e );
		}
	}
}
