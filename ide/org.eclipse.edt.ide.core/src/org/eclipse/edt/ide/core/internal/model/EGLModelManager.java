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
package org.eclipse.edt.ide.core.internal.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.compiler.tools.IRUtils;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.model.EGLConventions;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.ElementChangedEvent;
import org.eclipse.edt.ide.core.model.IClassFile;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLElementDelta;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLModel;
import org.eclipse.edt.ide.core.model.IEGLPathContainer;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IElementChangedListener;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.model.IWorkingCopy;

import org.eclipse.edt.ide.core.internal.model.indexing.AbstractSearchScope;
import org.eclipse.edt.ide.core.internal.model.indexing.IndexManager;

/**
 * The <code>EGLModelManager</code> manages instances of <code>IEGLModel</code>.
 * <code>IElementChangedListener</code> s register with the <code>EGLModelManager</code>,
 * and receive <code>ElementChangedEvent</code> s for all <code>IEGLModel</code>s.
 * <p>
 * The single instance of <code>EGLModelManager</code> is available from the
 * static method <code>EGLModelManager.getEGLModelManager()</code>.
 */
public class EGLModelManager {

	/**
	 * Unique handle onto the EGLModel
	 */
	final EGLModel eglModel = new EGLModel();

	/**
	 * EGLPath variables pool
	 */
	public static HashMap Variables = new HashMap(5);
	public static HashMap PreviousSessionVariables = new HashMap(5);
	public static HashSet OptionNames = new HashSet(20);
	
	/*
	 * Map from a package fragment root's path to a source attachment property (source path + ATTACHMENT_PROPERTY_DELIMITER + source root path)
	 */
	public Map rootPathToAttachments = new Hashtable();
	
	public final static String CP_VARIABLE_PREFERENCES_PREFIX = EGLCore.PLUGIN_ID + ".eglpathVariable."; //$NON-NLS-1$
	public final static String CP_CONTAINER_PREFERENCES_PREFIX = EGLCore.PLUGIN_ID + ".eglpathContainer."; //$NON-NLS-1$
	public final static String CP_ENTRY_IGNORE = "##<cp entry ignore>##"; //$NON-NLS-1$

	/**
	 * EGLPath containers pool
	 */
	public static HashMap Containers = new HashMap(5);
	public static HashMap PreviousSessionContainers = new HashMap(5);
	/**
	 * A cache of opened zip files per thread.
	 * (for a given thread, the object value is a HashMap from IPath to java.io.ZipFile)
	 */
	private ThreadLocal zipFiles = new ThreadLocal();
	/**
	 * Name of the extension point for contributing eglpath variable
	 * initializers
	 */
	public static final String CPVARIABLE_INITIALIZER_EXTPOINT_ID = "eglpathVariableInitializer"; //$NON-NLS-1$

	/**
	 * Name of the extension point for contributing eglpath container
	 * initializers
	 */
	public static final String CPCONTAINER_INITIALIZER_EXTPOINT_ID = "eglpathContainerInitializer"; //$NON-NLS-1$

	/**
	 * Name of the extension point for contributing a source code formatter
	 */
	public static final String FORMATTER_EXTPOINT_ID = "codeFormatter"; //$NON-NLS-1$
	/**
	 * Special value used for recognizing ongoing initialization and breaking
	 * initialization cycles
	 */
	public final static IPath VariableInitializationInProgress = new Path("Variable Initialization In Progress"); //$NON-NLS-1$
	public final static IEGLPathContainer ContainerInitializationInProgress = new IEGLPathContainer() {
		public IEGLPathEntry[] getEGLPathEntries() {
			return null;
		}
		public String getDescription() {
			return "Container Initialization In Progress"; //$NON-NLS-1$
		} //$NON-NLS-1$
		public int getKind() {
			return 0;
		}
		public IPath getPath() {
			return null;
		}
		public String toString() {
			return getDescription();
		}
	};
	/**
	 *  
	 */
	public final static IWorkingCopy[] NoWorkingCopy = new IWorkingCopy[0];
	/**
	 * Table from WorkingCopyOwner to a table of ICompilationUnit (working copy handle) to PerWorkingCopyInfo.
	 * NOTE: this object itself is used as a lock to synchronize creation/removal of per working copy infos
	 */
	protected Map perWorkingCopyInfos = new HashMap(5);
	/**
	 * Returns whether the given full path (for a package) conflicts with the
	 * output location of the given project.
	 */
	public static boolean conflictsWithOutputLocation(IPath folderPath, EGLProject project) {
		try {
			IPath outputLocation = project.getOutputLocation();
			if (outputLocation == null) {
				// in doubt, there is a conflict
				return true;
			}
			if (outputLocation.isPrefixOf(folderPath)) {
				// only allow nesting in project's output if there is a
				// corresponding source folder
				// or if the project's output is not used (in other words, if
				// all source folders have their custom output)
				IEGLPathEntry[] classpath = project.getResolvedEGLPath(true);
				boolean isOutputUsed = false;
				for (int i = 0, length = classpath.length; i < length; i++) {
					IEGLPathEntry entry = classpath[i];
					if (entry.getEntryKind() == IEGLPathEntry.CPE_SOURCE) {
						if (entry.getPath().equals(outputLocation)) {
							return false;
						}
						if (entry.getOutputLocation() == null) {
							isOutputUsed = true;
						}
					}
				}
				return isOutputUsed;
			}
			return false;
		} catch (EGLModelException e) {
			// in doubt, there is a conflict
			return true;
		}
	}

	public static IEGLPathContainer containerGet(IEGLProject project, IPath containerPath) {
		Map projectContainers = (Map) Containers.get(project);
		if (projectContainers == null) {
			return null;
		}
		IEGLPathContainer container = (IEGLPathContainer) projectContainers.get(containerPath);
		return container;
	}

	public static void containerPut(IEGLProject project, IPath containerPath, IEGLPathContainer container) {

		Map projectContainers = (Map) Containers.get(project);
		if (projectContainers == null) {
			projectContainers = new HashMap(1);
			Containers.put(project, projectContainers);
		}

		if (container == null) {
			projectContainers.remove(containerPath);
			Map previousContainers = (Map) PreviousSessionContainers.get(project);
			if (previousContainers != null) {
				previousContainers.remove(containerPath);
			}
		} else {
			projectContainers.put(containerPath, container);
		}

		// do not write out intermediate initialization value
		if (container == EGLModelManager.ContainerInitializationInProgress) {
			return;
		}
		Preferences preferences = EDTCoreIDEPlugin.getPlugin().getPluginPreferences();
		String containerKey = CP_CONTAINER_PREFERENCES_PREFIX + project.getElementName() + "|" + containerPath; //$NON-NLS-1$
		String containerString = CP_ENTRY_IGNORE;
		try {
			if (container != null) {
				containerString = ((EGLProject) project).encodeEGLPath(container.getEGLPathEntries(), null, false);
			}
		} catch (EGLModelException e) {
		}
		preferences.setDefault(containerKey, CP_ENTRY_IGNORE); // use this
															   // default to
															   // get rid of
															   // removed ones
		preferences.setValue(containerKey, containerString);
		EDTCoreIDEPlugin.getPlugin().savePluginPreferences();
	}

	/**
	 * Returns the EGL element corresponding to the given resource, or <code>null</code>
	 * if unable to associate the given resource with a EGL element.
	 * <p>
	 * The resource must be one of:
	 * <ul>
	 * <li>a project - the element returned is the corresponding <code>IEGLProject</code>
	 * </li>
	 * <li>a <code>.egl</code> file - the element returned is the
	 * corresponding <code>IEGLFile</code></li>
	 * <li>a <code>.class</code> file - the element returned is the
	 * corresponding <code>IClassFile</code></li>
	 * <li>a <code>.jar</code> file - the element returned is the
	 * corresponding <code>IPackageFragmentRoot</code></li>
	 * <li>a folder - the element returned is the corresponding <code>IPackageFragmentRoot</code>
	 * or <code>IPackageFragment</code></li>
	 * <li>the workspace root resource - the element returned is the <code>IEGLModel</code>
	 * </li>
	 * </ul>
	 * <p>
	 * Creating a EGL element has the side effect of creating and opening all
	 * of the element's parents if they are not yet open.
	 */
	public static IEGLElement create(IResource resource, IEGLProject project) {
		if (resource == null) {
			return null;
		}
		int type = resource.getType();
		switch (type) {
			case IResource.PROJECT :
				return EGLCore.create((IProject) resource);
			case IResource.FILE :
				return create((IFile) resource, project);
			case IResource.FOLDER :
				return create((IFolder) resource, project);
			case IResource.ROOT :
				return EGLCore.create((IWorkspaceRoot) resource);
			default :
				return null;
		}
	}

	/**
	 * Returns the EGL element corresponding to the given file, its project
	 * being the given project. Returns <code>null</code> if unable to
	 * associate the given file with a EGL element.
	 * 
	 * <p>
	 * The file must be one of:
	 * <ul>
	 * <li>a <code>.egl</code> file - the element returned is the
	 * corresponding <code>IEGLFile</code></li>
	 * <li>a <code>.class</code> file - the element returned is the
	 * corresponding <code>IClassFile</code></li>
	 * <li>a <code>.jar</code> file - the element returned is the
	 * corresponding <code>IPackageFragmentRoot</code></li>
	 * </ul>
	 * <p>
	 * Creating a EGL element has the side effect of creating and opening all
	 * of the element's parents if they are not yet open.
	 */
	public static IEGLElement create(IFile file, IEGLProject project) {
		if (file == null) {
			return null;
		}
		if (project == null) {
			project = EGLCore.create(file.getProject());
		}

		if (file.getFileExtension() != null) {
			String name = file.getName();
			if (Util.isValidEGLFileName(name))
				return createEGLFileFrom(file, project);
			else if(IRUtils.isEGLIRFileName(name)) {
				return createClassFileFrom(file, project);
			}
			return createJarPackageFragmentRootFrom(file, project);
		}
		return null;
	}
	/**
	 * Creates and returns a class file element for the given <code>.class</code> file,
	 * its project being the given project. Returns <code>null</code> if unable
	 * to recognize the class file.
	 */
	public static IClassFile createClassFileFrom(IFile file, IEGLProject project ) {
		if (file == null) {
			return null;
		}
		if (project == null) {
			project = EGLCore.create(file.getProject());
		}
		IPackageFragment pkg = (IPackageFragment) determineIfOnEGLPath(file, project);
		if (pkg == null) {
			// fix for 1FVS7WE
			// not on classpath - make the root its folder, and a default package
			PackageFragmentRoot root = (PackageFragmentRoot) project.getPackageFragmentRoot(file.getParent());
			pkg = root.getPackageFragment(new String[0]);
		}
		return pkg.getClassFile(file.getName());
	}
	/**
	 * Creates and returns a handle for the given JAR file, its project being the given project.
	 * The Java model associated with the JAR's project may be
	 * created as a side effect.
	 * Returns <code>null</code> if unable to create a JAR package fragment root.
	 * (for example, if the JAR file represents a non-Java resource)
	 */
	public static IPackageFragmentRoot createJarPackageFragmentRootFrom(IFile file, IEGLProject project) {
		if (file == null) {
			return null;
		}
		if (project == null) {
			project = EGLCore.create(file.getProject());
		}

		// Create a jar package fragment root only if on the classpath
		IPath resourcePath = file.getFullPath();
		try {
			IEGLPathEntry entry = ((EGLProject)project).getEGLPathEntryFor(resourcePath);
			if (entry != null) {
				IPackageFragmentRoot root = project.getPackageFragmentRoot(file);
				if(root instanceof EglarPackageFragmentRoot) {
					((EglarPackageFragmentRoot)root).setBinaryProject(entry.isBinaryProject());
				}
				return root;
			}
		} catch (EGLModelException e) {
			// project doesn't exist: return null
		}
		return null;
	}	
	/**
	 * Returns the package fragment or package fragment root corresponding to
	 * the given folder, its parent or great parent being the given project. or
	 * <code>null</code> if unable to associate the given folder with a EGL
	 * element.
	 * <p>
	 * Note that a package fragment root is returned rather than a default
	 * package.
	 * <p>
	 * Creating a EGL element has the side effect of creating and opening all
	 * of the element's parents if they are not yet open.
	 */
	public static IEGLElement create(IFolder folder, IEGLProject project) {
		if (folder == null) {
			return null;
		}
		if (project == null) {
			project = EGLCore.create(folder.getProject());
		}
		IEGLElement element = determineIfOnEGLPath(folder, project);
		if (conflictsWithOutputLocation(folder.getFullPath(), (EGLProject) project)
			|| (folder.getName().indexOf('.') >= 0 && !(element instanceof IPackageFragmentRoot))) {
			return null; // only package fragment roots are allowed with dot
						 // names
		} else {
			return element;
		}
	}

	/**
	 * Creates and returns a compilation unit element for the given <code>.egl</code>
	 * file, its project being the given project. Returns <code>null</code>
	 * if unable to recognize the compilation unit.
	 */
	public static IEGLFile createEGLFileFrom(IFile file, IEGLProject project) {

		if (file == null)
			return null;

		if (project == null) {
			project = EGLCore.create(file.getProject());
		}
		IPackageFragment pkg = (IPackageFragment) determineIfOnEGLPath(file, project);
		if (pkg == null) {
			// not on classpath - make the root its folder, and a default
			// package
			IPackageFragmentRoot root = project.getPackageFragmentRoot(file.getParent());
			pkg = root.getPackageFragment(IPackageFragment.DEFAULT_PACKAGE_NAME);

		}
		return pkg.getEGLFile(file.getName());
	}
	/**
	 * Returns the package fragment root represented by the resource, or the
	 * package fragment the given resource is located in, or <code>null</code>
	 * if the given resource is not on the classpath of the given project.
	 */
	public static IEGLElement determineIfOnEGLPath(IResource resource, IEGLProject project) {

		IPath resourcePath = resource.getFullPath();
		try {
				IEGLPathEntry[] entries =
					Util.isEGLFileName(resourcePath.lastSegment())
						? project.getRawEGLPath() // JAVA file can only live
												  // inside SRC folder (on the
												  // raw path)
	: ((EGLProject) project).getResolvedEGLPath(true);

			for (int i = 0; i < entries.length; i++) {
				IEGLPathEntry entry = entries[i];
				if (entry.getEntryKind() == IEGLPathEntry.CPE_PROJECT)
					continue;
				IPath rootPath = entry.getPath();
				if (rootPath.equals(resourcePath)) {
					return project.getPackageFragmentRoot(resource);
				} else if (
					rootPath.isPrefixOf(resourcePath) && !Util.isExcluded(resource, ((EGLPathEntry) entry).fullExclusionPatternChars())) {
					// given we have a resource child of the root, it cannot be
					// a JAR pkg root
					IPackageFragmentRoot root = ((EGLProject) project).getFolderPackageFragmentRoot(rootPath);
					if (root == null)
						return null;
					IPath pkgPath = resourcePath.removeFirstSegments(rootPath.segmentCount());
					if (resource.getType() == IResource.FILE) {
						// if the resource is a file, then remove the last
						// segment which
						// is the file name in the package
						pkgPath = pkgPath.removeLastSegments(1);

						// don't check validity of package name (see
						// http://bugs.eclipse.org/bugs/show_bug.cgi?id=26706)
						String pkgName = org.eclipse.edt.ide.core.internal.utils.Util.pathToQualifiedName(pkgPath);
						return root.getPackageFragment(pkgName);
					} else {
						String pkgName = Util.packageName(pkgPath);
						if (pkgName == null || EGLConventions.validatePackageName(pkgName).getSeverity() == IStatus.ERROR) {
							return null;
						}
						return root.getPackageFragment(pkgName);
					}
				}
			}
		} catch (EGLModelException npe) {
			return null;
		}
		return null;
	}

	/**
	 * The singleton manager
	 */
	private final static EGLModelManager Manager = new EGLModelManager();

	/**
	 * Infos cache.
	 */
	protected EGLModelCache cache = new EGLModelCache();

	/**
	 * Set of elements which are out of sync with their buffers.
	 */
	protected Map elementsOutOfSynchWithBuffers = new HashMap(11);

	/**
	 * Turns delta firing on/off. By default it is on.
	 */
	private boolean isFiring = true;

	/**
	 * Queue of deltas created explicily by the EGL Model that have yet to be
	 * fired.
	 */
	ArrayList eglModelDeltas = new ArrayList();
	/**
	 * Cached deltas that will be used by incremental builder
	 */
	//ArrayList cachedModelDeltas = new ArrayList();
	/**
	 * Queue of reconcile deltas on working copies that have yet to be fired.
	 * This is a table form IWorkingCopy to IEGLElementDelta
	 */
	HashMap reconcileDeltas = new HashMap();

	/**
	 * Collection of listeners for EGL element deltas
	 */
	private IElementChangedListener[] elementChangedListeners = new IElementChangedListener[5];
	private int[] elementChangedListenerMasks = new int[5];
	private int elementChangedListenerCount = 0;
	public int currentChangeEventType = ElementChangedEvent.PRE_AUTO_BUILD;
	public static final int DEFAULT_CHANGE_EVENT = 0; // must not collide with

	private static final boolean ZIP_ACCESS_VERBOSE = false;
													  // ElementChangedEvent
													  // event masks

	/**
	 * Used to convert <code>IResourceDelta</code> s into <code>IEGLElementDelta</code>s.
	 */
	public final DeltaProcessor deltaProcessor = new DeltaProcessor(this);
	/**
	 * Used to update the EGLModel for <code>IEGLElementDelta</code>s.
	 */
	private final ModelUpdater modelUpdater = new ModelUpdater();
	/**
	 * Workaround for bug 15168 circular errors not reported This is a cache of
	 * the projects before any project addition/deletion has started.
	 */
	public IEGLProject[] eglProjectsCache;

	/**
	 * Table from IProject to PerProjectInfo. NOTE: this object itself is used
	 * as a lock to synchronize creation/removal of per project infos
	 */
	protected Map perProjectInfo = new HashMap(5);
	/**
	 * A map from ICompilationUnit to IWorkingCopy of the shared working
	 * copies.
	 */
	public Map sharedWorkingCopies = new HashMap();

	/**
	 * A weak set of the known scopes.
	 */
	protected WeakHashMap scopes = new WeakHashMap();

	public static class PerProjectInfo {
		public IProject project;
		public IEGLPathEntry[] eglpath;
		public IEGLPathEntry[] lastResolvedEGLPath;
		public Map resolvedPathToRawEntries; // reverse map from resolved path
											 // to raw entries
		public IPath outputLocation;
		public Preferences preferences;
		public PerProjectInfo(IProject project) {

			this.project = project;
		}
	}
	public static boolean VERBOSE = false;
	public static boolean CP_RESOLVE_VERBOSE = false;

	private class WorkbenchState {
		public static final long UNKNOWN_UPDATE_TIME = -1;

		private long upateTime = UNKNOWN_UPDATE_TIME;

		/**
		 * @return Returns the upateTime.
		 */
		public long getUpateTime() {
			return upateTime;
		}

		/**
		 * @param upateTime
		 *            The upateTime to set.
		 */
		public void setUpateTime(long upateTime) {
			this.upateTime = upateTime;
		}
	}

	private WorkbenchState wbState;

	private EGLModelManager() {
	}
	/**
	 * addElementChangedListener method comment. Need to clone defensively the
	 * listener information, in case some listener is reacting to some
	 * notification iteration by adding/changing/removing any of the other (for
	 * example, if it deregisters itself).
	 */
	public void addElementChangedListener(IElementChangedListener listener, int eventMask) {
		for (int i = 0; i < this.elementChangedListenerCount; i++) {
			if (this.elementChangedListeners[i].equals(listener)) {

				// only clone the masks, since we could be in the middle of
				// notifications and one listener decide to change
				// any event mask of another listeners (yet not notified).
				int cloneLength = this.elementChangedListenerMasks.length;
				System.arraycopy(
					this.elementChangedListenerMasks,
					0,
					this.elementChangedListenerMasks = new int[cloneLength],
					0,
					cloneLength);
				this.elementChangedListenerMasks[i] = eventMask; // could be
																 // different
				return;
			}
		}
		// may need to grow, no need to clone, since iterators will have cached
		// original arrays and max boundary and we only add to the end.
		int length;
		if ((length = this.elementChangedListeners.length) == this.elementChangedListenerCount) {
			System.arraycopy(
				this.elementChangedListeners,
				0,
				this.elementChangedListeners = new IElementChangedListener[length * 2],
				0,
				length);
			System.arraycopy(this.elementChangedListenerMasks, 0, this.elementChangedListenerMasks = new int[length * 2], 0, length);
		}
		this.elementChangedListeners[this.elementChangedListenerCount] = listener;
		this.elementChangedListenerMasks[this.elementChangedListenerCount] = eventMask;
		this.elementChangedListenerCount++;
	}

	/**
	 * Fire EGL Model delta, flushing them after the fact after post_change
	 * notification. If the firing mode has been turned off, this has no
	 * effect.
	 */
	public void fire(IEGLElementDelta customDelta, int eventType) {

		if (!this.isFiring)
			return;

		if (DeltaProcessor.VERBOSE && (eventType == DEFAULT_CHANGE_EVENT || eventType == ElementChangedEvent.PRE_AUTO_BUILD)) {
			System.out.println("-----------------------------------------------------------------------------------------------------------------------"); //$NON-NLS-1$
		}

		IEGLElementDelta deltaToNotify;
		if (customDelta == null) {
			deltaToNotify = this.mergeDeltas(this.eglModelDeltas);
		} else {
			deltaToNotify = customDelta;
		}

		// Refresh internal scopes
		if (deltaToNotify != null) {
			Iterator scopes = this.scopes.keySet().iterator();
			while (scopes.hasNext()) {
				AbstractSearchScope scope = (AbstractSearchScope) scopes.next();
				scope.processDelta(deltaToNotify);
			}
		}

		// Notification

		// Important: if any listener reacts to notification by updating the
		// listeners list or mask, these lists will
		// be duplicated, so it is necessary to remember original lists in a
		// variable (since field values may change under us)
		IElementChangedListener[] listeners = this.elementChangedListeners;
		int[] listenerMask = this.elementChangedListenerMasks;
		int listenerCount = this.elementChangedListenerCount;

		switch (eventType) {
			case DEFAULT_CHANGE_EVENT :
				firePreAutoBuildDelta(deltaToNotify, listeners, listenerMask, listenerCount);
				firePostChangeDelta(deltaToNotify, listeners, listenerMask, listenerCount);
				fireReconcileDelta(listeners, listenerMask, listenerCount);
				break;
			case ElementChangedEvent.PRE_AUTO_BUILD :
				firePreAutoBuildDelta(deltaToNotify, listeners, listenerMask, listenerCount);
				break;
			case ElementChangedEvent.POST_CHANGE :
				firePostChangeDelta(deltaToNotify, listeners, listenerMask, listenerCount);
				fireReconcileDelta(listeners, listenerMask, listenerCount);
				break;
		}

	}

	private void firePreAutoBuildDelta(
		IEGLElementDelta deltaToNotify,
		IElementChangedListener[] listeners,
		int[] listenerMask,
		int listenerCount) {

		if (DeltaProcessor.VERBOSE) {
			System.out.println("FIRING PRE_AUTO_BUILD Delta [" + Thread.currentThread() + "]:"); //$NON-NLS-1$//$NON-NLS-2$
			System.out.println(deltaToNotify == null ? "<NONE>" : deltaToNotify.toString()); //$NON-NLS-1$
		}
		if (deltaToNotify != null) {
			notifyListeners(deltaToNotify, ElementChangedEvent.PRE_AUTO_BUILD, listeners, listenerMask, listenerCount);
		}
	}

	private void firePostChangeDelta(
		IEGLElementDelta deltaToNotify,
		IElementChangedListener[] listeners,
		int[] listenerMask,
		int listenerCount) {

		// post change deltas
		if (DeltaProcessor.VERBOSE) {
			System.out.println("FIRING POST_CHANGE Delta [" + Thread.currentThread() + "]:"); //$NON-NLS-1$//$NON-NLS-2$
			System.out.println(deltaToNotify == null ? "<NONE>" : deltaToNotify.toString()); //$NON-NLS-1$
		}
		if (deltaToNotify != null) {
			// flush now so as to keep listener reactions to post their own
			// deltas for subsequent iteration
			this.flush();

			notifyListeners(deltaToNotify, ElementChangedEvent.POST_CHANGE, listeners, listenerMask, listenerCount);
		}
	}
	private void fireReconcileDelta(IElementChangedListener[] listeners, int[] listenerMask, int listenerCount) {

		IEGLElementDelta deltaToNotify = mergeDeltas(this.reconcileDeltas.values());
		if (DeltaProcessor.VERBOSE) {
			System.out.println("FIRING POST_RECONCILE Delta [" + Thread.currentThread() + "]:"); //$NON-NLS-1$//$NON-NLS-2$
			System.out.println(deltaToNotify == null ? "<NONE>" : deltaToNotify.toString()); //$NON-NLS-1$
		}
		if (deltaToNotify != null) {
			// flush now so as to keep listener reactions to post their own
			// deltas for subsequent iteration
			this.reconcileDeltas = new HashMap();

			notifyListeners(deltaToNotify, ElementChangedEvent.POST_RECONCILE, listeners, listenerMask, listenerCount);
		}
	}

	public void notifyListeners(
		IEGLElementDelta deltaToNotify,
		int eventType,
		IElementChangedListener[] listeners,
		int[] listenerMask,
		int listenerCount) {
		final ElementChangedEvent extraEvent = new ElementChangedEvent(deltaToNotify, eventType);
		for (int i = 0; i < listenerCount; i++) {
			if ((listenerMask[i] & eventType) != 0) {
				final IElementChangedListener listener = listeners[i];
				long start = -1;
				if (DeltaProcessor.VERBOSE) {
					System.out.print("Listener #" + (i + 1) + "=" + listener.toString()); //$NON-NLS-1$//$NON-NLS-2$
					start = System.currentTimeMillis();
				}
				// wrap callbacks with Safe runnable for subsequent listeners
				// to be called when some are causing grief
				Platform.run(new ISafeRunnable() {
					public void handleException(Throwable exception) {
						Util.log(exception, "Exception occurred in listener of EGL element change notification"); //$NON-NLS-1$
					}
					public void run() throws Exception {
						listener.elementChanged(extraEvent);
					}
				});
				if (DeltaProcessor.VERBOSE) {
					System.out.println(" -> " + (System.currentTimeMillis() - start) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
	}

	/**
	 * Flushes all deltas without firing them.
	 */
	protected void flush() {
		this.eglModelDeltas = new ArrayList();
	}

	public ArrayList getEGLModelDeltas() {
		return this.eglModelDeltas;
	}
	/**
	 * Returns the set of elements which are out of synch with their buffers.
	 */
	protected Map getElementsOutOfSynchWithBuffers() {
		return this.elementsOutOfSynchWithBuffers;
	}
	/**
	 * Returns the <code>IEGLElement</code> represented by the <code>String</code>
	 * memento.
	 */
	public IEGLElement getHandleFromMemento(String memento) throws EGLModelException {
		if (memento == null) {
			return null;
		}
		EGLModel model = (EGLModel) getEGLModel();
		if (memento.equals("")) { // workspace memento //$NON-NLS-1$
			return model;
		}
		int modelEnd = memento.indexOf(EGLElement.EGLM_EGLPROJECT);
		if (modelEnd == -1) {
			return null;
		}
		boolean returnProject = false;
		int projectEnd = memento.indexOf(EGLElement.EGLM_PACKAGEFRAGMENTROOT, modelEnd);
		if (projectEnd == -1) {
			projectEnd = memento.length();
			returnProject = true;
		}
		String projectName = memento.substring(modelEnd + 1, projectEnd);
		EGLProject proj = (EGLProject) model.getEGLProject(projectName);
		if (returnProject) {
			return proj;
		}
		int rootEnd = memento.indexOf(EGLElement.EGLM_PACKAGEFRAGMENT, projectEnd + 1);
		if (rootEnd == -1) {
			return model.getHandleFromMementoForRoot(memento, proj, projectEnd, memento.length());
		}
		IPackageFragmentRoot root = model.getHandleFromMementoForRoot(memento, proj, projectEnd, rootEnd);
		if (root == null)
			return null;

		int end = memento.indexOf(EGLElement.EGLM_EGLFILE, rootEnd);
		if (end == -1) {
			end = memento.indexOf(EGLElement.EGLM_CLASSFILE, rootEnd);
			if (end != -1) {
				//deal with class file and binary members
				// return model.getHandleFromMementoForBinaryMembers(memento, root,
				// rootEnd, end);
				
				// For now we only support the IClassFile, nothing that it contains.
				IPackageFragment frag;
				if (rootEnd == end - 1) {
					frag = root.getPackageFragment(IPackageFragment.DEFAULT_PACKAGE_NAME);
				} else {
					frag = root.getPackageFragment(memento.substring(rootEnd + 1, end));
				}
				return frag.getClassFile(memento.substring(end + 1));
			}
			
			if (rootEnd + 1 == memento.length()) {
				return root.getPackageFragment(IPackageFragment.DEFAULT_PACKAGE_NAME);
			} else {
				return root.getPackageFragment(memento.substring(rootEnd + 1));
			}
		}

		//deal with compilation units and source members
		return model.getHandleFromMementoForSourceMembers(memento, root, rootEnd, end);
	}
	public IndexManager getIndexManager() {
		return this.deltaProcessor.indexManager;
	}

	/**
	 * Returns the info for the element.
	 */
	public synchronized Object getInfo(IEGLElement element) {
		return this.cache.getInfo(element);
	}

	/*
	 * Returns the per-project info for the given project. If specified, create
	 * the info if the info doesn't exist.
	 */
	public PerProjectInfo getPerProjectInfo(IProject project, boolean create) {
		synchronized (perProjectInfo) { // use the perProjectInfo collection as
										// its own lock
			PerProjectInfo info = (PerProjectInfo) perProjectInfo.get(project);
			if (info == null && create) {
				info = new PerProjectInfo(project);
				perProjectInfo.put(project, info);
			}
			return info;
		}
	}

	/*
	 * Returns the per-project info for the given project. If the info doesn't
	 * exist, check for the project existence and create the info. @throws
	 * EGLModelException if the project doesn't exist.
	 */
	public PerProjectInfo getPerProjectInfoCheckExistence(IProject project) throws EGLModelException {
		EGLModelManager.PerProjectInfo info = getPerProjectInfo(project, false /*
																			    * don't
																			    * create
																			    * info
																			    */
		);
		if (info == null) {
			if (!EGLProject.hasEGLNature(project)) {
				throw ((EGLProject) EGLCore.create(project)).newNotPresentException();
			}
			info = getPerProjectInfo(project, true /* create info */
			);
		}
		return info;
	}

	/**
	 * Returns the handle to the active EGL Model.
	 */
	public final EGLModel getEGLModel() {
		return eglModel;
	}

	/**
	 * Returns the singleton EGLModelManager
	 */
	public final static EGLModelManager getEGLModelManager() {
		return Manager;
	}

	/**
	 * Merged all awaiting deltas.
	 */
	public IEGLElementDelta mergeDeltas(Collection deltas) {
		if (deltas.size() == 0)
			return null;
		if (deltas.size() == 1)
			return (IEGLElementDelta) deltas.iterator().next();

		if (DeltaProcessor.VERBOSE) {
			System.out.println("MERGING " + deltas.size() + " DELTAS [" + Thread.currentThread() + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		Iterator iterator = deltas.iterator();
		IEGLElement eglModel = this.getEGLModel();
		EGLElementDelta rootDelta = new EGLElementDelta(eglModel);
		boolean insertedTree = false;
		while (iterator.hasNext()) {
			EGLElementDelta delta = (EGLElementDelta) iterator.next();
			if (DeltaProcessor.VERBOSE) {
				System.out.println(delta.toString());
			}
			IEGLElement element = delta.getElement();
			if (eglModel.equals(element)) {
				IEGLElementDelta[] children = delta.getAffectedChildren();
				for (int j = 0; j < children.length; j++) {
					EGLElementDelta projectDelta = (EGLElementDelta) children[j];
					rootDelta.insertDeltaTree(projectDelta.getElement(), projectDelta);
					insertedTree = true;
				}
				IResourceDelta[] resourceDeltas = delta.getResourceDeltas();
				if (resourceDeltas != null) {
					for (int i = 0, length = resourceDeltas.length; i < length; i++) {
						rootDelta.addResourceDelta(resourceDeltas[i]);
						insertedTree = true;
					}
				}
			} else {
				rootDelta.insertDeltaTree(element, delta);
				insertedTree = true;
			}
		}
		if (insertedTree) {
			return rootDelta;
		} else {
			return null;
		}
	}

	/**
	 * Returns the info for this element without disturbing the cache ordering.
	 */
	protected synchronized Object peekAtInfo(IEGLElement element) {
		return this.cache.peekAtInfo(element);
	}

	protected synchronized void putInfo(IEGLElement element, Object info) {
		this.cache.putInfo(element, info);
	}

	/**
	 * Registers the given delta with this manager.
	 */
	protected void registerEGLModelDelta(IEGLElementDelta delta) {
		this.eglModelDeltas.add(delta);
		//this.cachedModelDeltas.add(delta);
	}

	/**
	 * Remembers the given scope in a weak set (so no need to remove it: it
	 * will be removed by the garbage collector)
	 */
	public void rememberScope(AbstractSearchScope scope) {
		// NB: The value has to be null so as to not create a strong reference
		// on the scope
		this.scopes.put(scope, null);
	}

	/**
	 * removeElementChangedListener method comment.
	 */
	public void removeElementChangedListener(IElementChangedListener listener) {

		for (int i = 0; i < this.elementChangedListenerCount; i++) {

			if (this.elementChangedListeners[i].equals(listener)) {

				// need to clone defensively since we might be in the middle of
				// listener notifications (#fire)
				int length = this.elementChangedListeners.length;
				IElementChangedListener[] newListeners = new IElementChangedListener[length];
				System.arraycopy(this.elementChangedListeners, 0, newListeners, 0, i);
				int[] newMasks = new int[length];
				System.arraycopy(this.elementChangedListenerMasks, 0, newMasks, 0, i);

				// copy trailing listeners
				int trailingLength = this.elementChangedListenerCount - i - 1;
				if (trailingLength > 0) {
					System.arraycopy(this.elementChangedListeners, i + 1, newListeners, i, trailingLength);
					System.arraycopy(this.elementChangedListenerMasks, i + 1, newMasks, i, trailingLength);
				}

				// update manager listener state (#fire need to iterate over
				// original listeners through a local variable to hold onto
				// the original ones)
				this.elementChangedListeners = newListeners;
				this.elementChangedListenerMasks = newMasks;
				this.elementChangedListenerCount--;
				return;
			}
		}
	}

	protected synchronized void removeInfo(IEGLElement element) {
		this.cache.removeInfo(element);
	}

	public void removePerProjectInfo(EGLProject javaProject) {
		synchronized (perProjectInfo) { // use the perProjectInfo collection as
										// its own lock
			IProject project = javaProject.getProject();
			PerProjectInfo info = (PerProjectInfo) perProjectInfo.get(project);
			if (info != null) {
				perProjectInfo.remove(project);
			}
		}
	}

	public void shutdown() {
		if (this.deltaProcessor.indexManager != null) { // no more indexing
			this.deltaProcessor.indexManager.shutdown();
		}
		try {
			IEGLModel model = this.getEGLModel();
			if (model != null) {
				model.close();
			}
		} catch (EGLModelException e) {
		}
	}

	/**
	 * Turns the firing mode to on. That is, deltas that are/have been
	 * registered will be fired.
	 */
	public void startDeltas() {
		this.isFiring = true;
	}

	/**
	 * Turns the firing mode to off. That is, deltas that are/have been
	 * registered will not be fired until deltas are started again.
	 */
	public void stopDeltas() {
		this.isFiring = false;
	}

	/**
	 * Update EGL Model given some delta
	 */
	public void updateEGLModel(IEGLElementDelta customDelta) {

		if (customDelta == null) {
			for (int i = 0, length = this.eglModelDeltas.size(); i < length; i++) {
				IEGLElementDelta delta = (IEGLElementDelta) this.eglModelDeltas.get(i);
				this.modelUpdater.processEGLDelta(delta);
			}
		} else {
			this.modelUpdater.processEGLDelta(customDelta);
		}
	}

	public static IPath variableGet(String variableName) {
		return (IPath) Variables.get(variableName);
	}

	public static String[] variableNames() {
		int length = Variables.size();
		String[] result = new String[length];
		Iterator vars = Variables.keySet().iterator();
		int index = 0;
		while (vars.hasNext()) {
			result[index++] = (String) vars.next();
		}
		return result;
	}

	public static void variablePut(String variableName, IPath variablePath) {

		// update cache - do not only rely on listener refresh
		if (variablePath == null) {
			Variables.remove(variableName);
			PreviousSessionVariables.remove(variableName);
		} else {
			Variables.put(variableName, variablePath);
		}

		// do not write out intermediate initialization value
		if (variablePath == EGLModelManager.VariableInitializationInProgress) {
			return;
		}
		Preferences preferences = EDTCoreIDEPlugin.getPlugin().getPluginPreferences();
		String variableKey = CP_VARIABLE_PREFERENCES_PREFIX + variableName;
		String variableString = variablePath == null ? CP_ENTRY_IGNORE : variablePath.toString();
		preferences.setDefault(variableKey, CP_ENTRY_IGNORE); // use this
															  // default to get
															  // rid of removed
															  // ones
		preferences.setValue(variableKey, variableString);
		EDTCoreIDEPlugin.getPlugin().savePluginPreferences();
	}

//	/**
//	 * @return
//	 */
//	public ArrayList getCachedModelDeltas() {
//		return cachedModelDeltas;
//	}
//	public IEGLElementDelta getDeltaFor(IEGLElement element) {
//		IEGLElementDelta delta = null;
//		for (Iterator iter = cachedModelDeltas.iterator(); iter.hasNext();) {
//			delta = ((EGLElementDelta) iter.next()).getDeltaFor(element);
//			if (delta != null)
//				break;
//		}
//		return delta;
//	}
//	public List getDeltasFor(IEGLElement element) {
//		List deltas = new ArrayList();
//		for (Iterator iter = cachedModelDeltas.iterator(); iter.hasNext();) {
//			EGLElementDelta delta = ((EGLElementDelta) iter.next()).getDeltaFor(element);
//			if (delta != null) {
//				deltas.add(delta);
//			}
//		}
//		return deltas;
//	}
//	public void flushCachedModelDeltas() {
//		cachedModelDeltas = new ArrayList();
//	}

	/**
	 * Get the timestamp recorded the last time a .egl file was saved.
	 * 
	 * @return
	 */
	public long getLastUpdateTime() {
		wbState = getWorkbenchState();
		return wbState.getUpateTime();
	}

	/**
	 * @return
	 */
	private WorkbenchState readWorkbenchState() throws CoreException {
		WorkbenchState newWBState = new WorkbenchState();

		File file = getWorkbenchSerializationFile();
		if (file != null && file.exists()) {
			try {
				DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
				try {
					String pluginID = in.readUTF();
					if (!pluginID.equals(EGLCore.PLUGIN_ID))
						throw new IOException(EGLModelResources.buildWrongFileFormat);
					String kind = in.readUTF();
					if (!kind.equals("STATE")) //$NON-NLS-1$
						throw new IOException(EGLModelResources.buildWrongFileFormat);
					newWBState.setUpateTime(in.readLong());
				} finally {
					in.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new CoreException(new Status(IStatus.ERROR, EGLCore.PLUGIN_ID, Platform.PLUGIN_ERROR, "Error reading last workbench build state", e)); //$NON-NLS-1$
			}
		}
		return newWBState;
	}

	/**
	 * Returns the File to use for saving and restoring the last known EGL save
	 * time.
	 */
	private File getWorkbenchSerializationFile() {
		IPath path = EDTCoreIDEPlugin.getPlugin().getStateLocation();
		return path.append("state.dat").toFile(); //$NON-NLS-1$
	}

	/**
	 * Save the last known EGL update time.
	 * 
	 * @param timeStamp
	 */
	public void setLastUpdateTime(long timeStamp) {
		
		wbState = getWorkbenchState();
		
		// set the update time
		wbState.setUpateTime(timeStamp);

		// always write out the state after an update
		try {
			writeWorkbenchState();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private WorkbenchState getWorkbenchState() {
		// Check to see if we have retrieved the workbench state yet
		if(wbState == null)
		{
			try {
				wbState = readWorkbenchState();
			} catch (CoreException e) {
				wbState = new WorkbenchState();
			}
		}
		
		return wbState;
	}

	/**
	 * 
	 */
	private void writeWorkbenchState() throws CoreException{
		File file = getWorkbenchSerializationFile();
		if (file == null)
			return;
		try {
			DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			try {
				out.writeUTF(EGLCore.PLUGIN_ID);
				out.writeUTF("STATE"); //$NON-NLS-1$
				out.writeLong(wbState.getUpateTime());
			} finally {
				out.close();
			}
		} catch (RuntimeException e) {
			try {
				file.delete();
			} catch (SecurityException se) {
			}
			throw new CoreException(new Status(IStatus.ERROR, EGLCore.PLUGIN_ID, Platform.PLUGIN_ERROR, "Error saving EGL workbench state", e)); //$NON-NLS-1$
		} catch (IOException e) {
			try {
				file.delete();
			} catch (SecurityException se) {
			}
			throw new CoreException(new Status(IStatus.ERROR, EGLCore.PLUGIN_ID, Platform.PLUGIN_ERROR, "Error saving EGL workbench state", e)); //$NON-NLS-1$
		}		
	}
	
	public synchronized void flushEGLModelCache() {
		cache = new EGLModelCache();
	}
	
	/**
	 * Returns the open ZipFile at the given path. If the ZipFile
	 * does not yet exist, it is created, opened, and added to the cache
	 * of open ZipFiles.
	 *
	 * The path must be a file system path if representing an external
	 * zip/jar, or it must be an absolute workspace relative path if
	 * representing a zip/jar inside the workspace.
	 *
	 * @exception CoreException If unable to create/open the ZipFile
	 */
	public ZipFile getZipFile(IPath path) throws Exception{

		ZipCache zipCache;
		ZipFile zipFile;
		if ((zipCache = (ZipCache)this.zipFiles.get()) != null
				&& (zipFile = zipCache.getCache(path)) != null) {
			return zipFile;
		}
		File localFile = null;
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource file = root.findMember(path);
		if (file != null) {
			// internal resource
			URI location;
			if (file.getType() != IResource.FILE || (location = file.getLocationURI()) == null) {
				//TODO throw new CoreException(new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, Messages.bind(Messages.file_notFound, path.toString()), null));
				throw new Exception("");
			}
			localFile = Util.toLocalFile(location, null/*no progress availaible*/);
			if (localFile == null) {
//TODO			throw new CoreException(new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, Messages.bind(Messages.file_notFound, path.toString()), null));
				throw new Exception("");
			}
		} else {
			// external resource -> it is ok to use toFile()
			localFile= path.toFile();
		}

		try {
			if (ZIP_ACCESS_VERBOSE) {
				System.out.println("(" + Thread.currentThread() + ") [EGLModelManager.getZipFile(IPath)] Creating ZipFile on " + localFile ); //$NON-NLS-1$ //$NON-NLS-2$
			}
			zipFile = new ZipFile(localFile);
			if (zipCache != null) {
				zipCache.setCache(path, zipFile);
			}
			return zipFile;
		} catch (IOException e) {
//TODO			throw new CoreException(new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, Messages.status_IOException, e));
			throw new Exception(e.getMessage());
		}
	}
	
	/**
	 * Define a zip cache object.
	 */
	static class ZipCache {
		private Map map;
		Object owner;

		ZipCache(Object owner) {
			this.map = new HashMap();
			this.owner = owner;
		}

		public void flush() {
			Thread currentThread = Thread.currentThread();
			Iterator iterator = this.map.values().iterator();
			while (iterator.hasNext()) {
				try {
					ZipFile zipFile = (ZipFile)iterator.next();
					if (EGLModelManager.ZIP_ACCESS_VERBOSE) {
						System.out.println("(" + currentThread + ") [EGLModelManager.flushZipFiles()] Closing ZipFile on " +zipFile.getName()); //$NON-NLS-1$//$NON-NLS-2$
					}
					zipFile.close();
				} catch (IOException e) {
					// problem occured closing zip file: cannot do much more
				}
			}
		}

		public ZipFile getCache(IPath path) {
			return (ZipFile) this.map.get(path);
		}

		public void setCache(IPath path, ZipFile zipFile) {
			this.map.put(path, zipFile);
		}
	}
	
	public void closeZipFile(ZipFile zipFile) {
		if (zipFile == null) return;
		if (this.zipFiles.get() != null) {
			return; // zip file will be closed by call to flushZipFiles
		}
		try {
			if (EGLModelManager.ZIP_ACCESS_VERBOSE) {
				System.out.println("(" + Thread.currentThread() + ") [EGLModelManager.closeZipFile(ZipFile)] Closing ZipFile on " +zipFile.getName()); //$NON-NLS-1$	//$NON-NLS-2$
			}
			zipFile.close();
		} catch (IOException e) {
			// problem occured closing zip file: cannot do much more
		}
	}
	
	/*
	 * Resets the cache that holds on binary type in jar files
	 */
	protected synchronized void resetJarTypeCache() {
		this.cache.resetJarTypeCache();
	}
		
	/**
	 * Starts caching ZipFiles.
	 * Ignores if there are already clients.
	 */
	public void cacheZipFiles(Object owner) {
		ZipCache zipCache = (ZipCache) this.zipFiles.get();
		if (zipCache != null) {
			return;
		}
		// the owner will be responsible for flushing the cache
		this.zipFiles.set(new ZipCache(owner));
	}
	
	/**
	 * Flushes ZipFiles cache if there are no more clients.
	 */
	public void flushZipFiles(Object owner) {
		ZipCache zipCache = (ZipCache)this.zipFiles.get();
		if (zipCache == null) {
			return;
		}
		// the owner will be responsible for flushing the cache
		// we want to check object identity to make sure this is the owner that created the cache
		if (zipCache.owner == owner) {
			this.zipFiles.set(null);
			zipCache.flush();
		}
	}
}
