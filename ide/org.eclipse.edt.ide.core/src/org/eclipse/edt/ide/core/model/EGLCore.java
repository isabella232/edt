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
package org.eclipse.edt.ide.core.model;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.EGLNature;
import org.eclipse.edt.ide.core.internal.model.Assert;
import org.eclipse.edt.ide.core.internal.model.BatchOperation;
import org.eclipse.edt.ide.core.internal.model.BufferManager;
import org.eclipse.edt.ide.core.internal.model.EGLModel;
import org.eclipse.edt.ide.core.internal.model.EGLModelManager;
import org.eclipse.edt.ide.core.internal.model.EGLPathEntry;
import org.eclipse.edt.ide.core.internal.model.EGLProject;
import org.eclipse.edt.ide.core.internal.model.Region;
import org.eclipse.edt.ide.core.internal.model.SetEGLPathOperation;
import org.eclipse.edt.ide.core.internal.model.Util;
import org.eclipse.jdt.core.JavaCore;

/**
 * The plug-in runtime class for the EGL model plug-in containing the core
 * (UI-free) support for EGL projects.
 * <p>
 * Like all plug-in runtime classes (subclasses of <code>Plugin</code>), this
 * class is automatically instantiated by the platform when the plug-in gets
 * activated. Clients must not attempt to instantiate plug-in runtime classes
 * directly.
 * </p>
 * <p>
 * The single instance of this class can be accessed from any plug-in declaring
 * the EGL model plug-in as a prerequisite via 
 * <code>EGLCore.getEGLCore()</code>. The EGL model plug-in will be activated
 * automatically if not already active.
 * </p>
 */
public final class EGLCore {

	/**
	 * The plug-in identifier of the EGL core support
	 * (value <code>"com.ibm.etools.egl.internal.model.core"</code>).
	 */
	//TODO EDT Redo?
	public static final String PLUGIN_ID = EDTCoreIDEPlugin.PLUGIN_ID; //$NON-NLS-1$

	/**
	 * The identifier for the EGL builder
	 * (value <code>"com.ibm.etools.egl.internal.model.core.eglbuilder"</code>).
	 */
	public static final String BUILDER_ID = PLUGIN_ID + ".eglbuilder"; //$NON-NLS-1$

	/**
	 * The identifier for the EGL model
	 * (value <code>"com.ibm.etools.egl.internal.model.core.eglmodel"</code>).
	 */
	public static final String MODEL_ID = PLUGIN_ID + ".eglmodel"; //$NON-NLS-1$

	/**
	 * The identifier for the EGL nature
	 * (value <code>"org.eclipse.edt.ide.core.eglnature"</code>).
	 * The presence of this nature on a project indicates that it is 
	 * EGL-capable.
	 *
	 * @see org.eclipse.core.resources.IProject#hasNature(egl.lang.String)
	 */
	public static final String NATURE_ID = EGLNature.EGL_NATURE_ID; //$NON-NLS-1$

	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions
	 */
	public static final String CORE_EGL_BUILD_ORDER = PLUGIN_ID + ".computeEGLBuildOrder"; //$NON-NLS-1$
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions
	 * @since 2.0
	 */
	public static final String CORE_EGL_BUILD_RESOURCE_COPY_FILTER = PLUGIN_ID + ".builder.resourceCopyExclusionFilter"; //$NON-NLS-1$
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions
	 * @since 2.1
	 */
	public static final String CORE_EGL_BUILD_DUPLICATE_RESOURCE = PLUGIN_ID + ".builder.duplicateResourceTask"; //$NON-NLS-1$
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions
	 * @since 2.1
	 */
	public static final String CORE_EGL_BUILD_CLEAN_OUTPUT_FOLDER = PLUGIN_ID + ".builder.cleanOutputFolder"; //$NON-NLS-1$	 	
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions
	 * @since 2.1
	 */
	public static final String CORE_INCOMPLETE_CLASSPATH = PLUGIN_ID + ".incompleteEGLPath"; //$NON-NLS-1$
//	/**
//	 * Possible  configurable option ID.
//	 * @see #getDefaultOptions
//	 * @since 2.1
//	 */
//	public static final String CORE_CIRCULAR_CLASSPATH = PLUGIN_ID + ".circularEGLPath"; //$NON-NLS-1$
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions
	 * @since 2.0
	 */
	public static final String CORE_EGL_BUILD_INVALID_CLASSPATH = PLUGIN_ID + ".builder.invalidEGLPath"; //$NON-NLS-1$
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions
	 * @since 2.0
	 */
	public static final String CORE_ENCODING = PLUGIN_ID + ".encoding"; //$NON-NLS-1$
//	/**
//	 * Possible  configurable option ID.
//	 * @see #getDefaultOptions
//	 * @since 2.1 
//	 */
//	public static final String CORE_ENABLE_CLASSPATH_EXCLUSION_PATTERNS = PLUGIN_ID + ".eglpath.exclusionPatterns"; //$NON-NLS-1$
//	/**
//	 * Possible  configurable option ID.
//	 * @see #getDefaultOptions
//	 * @since 2.1
//	 */
//	public static final String CORE_ENABLE_CLASSPATH_MULTIPLE_OUTPUT_LOCATIONS = PLUGIN_ID + ".eglpath.multipleOutputLocations"; //$NON-NLS-1$
	/**
	 * Default task tag
	 * @since 2.1
	 */
	public static final String DEFAULT_TASK_TAG = "TODO"; //$NON-NLS-1$
	/**
	 * Default task priority
	 * @since 2.1
	 */
	public static final String DEFAULT_TASK_PRIORITY = "NORMAL"; //$NON-NLS-1$
	/**
	 * Possible  configurable option value.
	 * @see #getDefaultOptions
	 * @since 2.0
	 */
	public static final String ABORT = "abort"; //$NON-NLS-1$
	/**
	 * Possible  configurable option value.
	 * @see #getDefaultOptions
	 */
	public static final String ERROR = "error"; //$NON-NLS-1$
	/**
	 * Possible  configurable option value.
	 * @see #getDefaultOptions
	 */
	public static final String WARNING = "warning"; //$NON-NLS-1$
	/**
	 * Possible  configurable option value.
	 * @see #getDefaultOptions
	 */
	public static final String IGNORE = "ignore"; //$NON-NLS-1$
	/**
	 * Possible  configurable option value.
	 * @see #getDefaultOptions
	 */
	public static final String COMPUTE = "compute"; //$NON-NLS-1$
	/**
	 * Possible  configurable option value.
	 * @see #getDefaultOptions
	 * @since 2.0
	 */
	public static final String INSERT = "insert"; //$NON-NLS-1$
	/**
	 * Possible  configurable option value.
	 * @see #getDefaultOptions
	 * @since 2.0
	 */
	public static final String DO_NOT_INSERT = "do not insert"; //$NON-NLS-1$
	/**
	 * Possible  configurable option value.
	 * @see #getDefaultOptions
	 * @since 2.0
	 */
	public static final String PRESERVE_ONE = "preserve one"; //$NON-NLS-1$
	/**
	 * Possible  configurable option value.
	 * @see #getDefaultOptions
	 * @since 2.0
	 */
	public static final String CLEAR_ALL = "clear all"; //$NON-NLS-1$
	/**
	 * Possible  configurable option value.
	 * @see #getDefaultOptions
	 * @since 2.0
	 */
	public static final String NORMAL = "normal"; //$NON-NLS-1$
	/**
	 * Possible  configurable option value.
	 * @see #getDefaultOptions
	 * @since 2.0
	 */
	public static final String COMPACT = "compact"; //$NON-NLS-1$
	/**
	 * Possible  configurable option value.
	 * @see #getDefaultOptions
	 * @since 2.0
	 */
	public static final String TAB = "tab"; //$NON-NLS-1$
	/**
	 * Possible  configurable option value.
	 * @see #getDefaultOptions
	 * @since 2.0
	 */
	public static final String SPACE = "space"; //$NON-NLS-1$
	/**
	 * Possible  configurable option value.
	 * @see #getDefaultOptions
	 * @since 2.0
	 */
	public static final String ENABLED = "enabled"; //$NON-NLS-1$
	/**
	 * Possible  configurable option value.
	 * @see #getDefaultOptions
	 * @since 2.0
	 */
	public static final String DISABLED = "disabled"; //$NON-NLS-1$
	/**
	 * Possible  configurable option value.
	 * @see #getDefaultOptions
	 * @since 2.1
	 */
	public static final String CLEAN = "clean"; //$NON-NLS-1$
	
	/**
	 * Name of the processor extension point.
	 */	
	public static final String PT_PROCESSOR = "processor"; //$NON-NLS-1$

	/**
	 * Name of the processor extension point attribute that holds the class name.
	 */	
	public static final String CLASS = "class"; //$NON-NLS-1$
	
	/**
	 * Name of default EGL Source Folder.
	 */
//  This value is now stored in a preference instead of hardcorded.  Use
//  EDTCoreIDEPlugin.getPlugin().getPreferenceStore().getString( EDTCorePreferenceConstants.EGL_SOURCE_FOLDER ).
//	public static final String DEFAULT_EGL_SOURCE = "eglsource"; //$NON-NLS-1$
	
	/**
	 * Name of default EGL Source Folder.
	 */
	public static final String DEFAULT_JAVA_SOURCE = "javasource"; //$NON-NLS-1$
	
	/**
	 * Name of default EGL JavaScript Folder.
	 */
	public static final String DEFAULT_JAVASCRIPT_SOURCE = "javascript"; //$NON-NLS-1$
		
	/**
	 * Name of default COBOL Source Folder.
	 */
	public static final String DEFAULT_COBOL_SOURCE = "cobolsource"; //$NON-NLS-1$
	
	/**
	 * Name of default folder containing IR files.
	 */
//  This value is now stored in a preference instead of hardcorded.  Use
//  EDTCoreIDEPlugin.getPlugin().getPreferenceStore().getString( EDTCorePreferenceConstants.EGL_OUTPUT_FOLDER ).	
//	public static final String DEFAULT_EGL_BIN = "eglbin"; //$NON-NLS-1$
	
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}	
	/**
	 * Adds the given listener for changes to EGL elements.
	 * Has no effect if an identical listener is already registered.
	 *
	 * This listener will only be notified during the POST_CHANGE resource change notification
	 * and any reconcile operation (POST_RECONCILE).
	 * For finer control of the notification, use <code>addElementChangedListener(IElementChangedListener,int)</code>,
	 * which allows to specify a different eventMask.
	 * 
	 * @see ElementChangedEvent
	 * @param listener the listener
	 */
	public static void addElementChangedListener(IElementChangedListener listener) {
		addElementChangedListener(listener, ElementChangedEvent.POST_CHANGE | ElementChangedEvent.POST_RECONCILE);
	}

	/**
	 * Adds the given listener for changes to EGL elements.
	 * Has no effect if an identical listener is already registered.
	 * After completion of this method, the given listener will be registered for exactly
	 * the specified events.  If they were previously registered for other events, they
	 * will be deregistered.  
	 * <p>
	 * Once registered, a listener starts receiving notification of changes to
	 * egl elements in the model. The listener continues to receive 
	 * notifications until it is replaced or removed. 
	 * </p>
	 * <p>
	 * Listeners can listen for several types of event as defined in <code>ElementChangeEvent</code>.
	 * Clients are free to register for any number of event types however if they register
	 * for more than one, it is their responsibility to ensure they correctly handle the
	 * case where the same egl element change shows up in multiple notifications.  
	 * Clients are guaranteed to receive only the events for which they are registered.
	 * </p>
	 * 
	 * @param listener the listener
	 * @param eventMask the bit-wise OR of all event types of interest to the listener
	 * @see IElementChangedListener
	 * @see ElementChangedEvent
	 * @see #removeElementChangedListener(IElementChangedListener)
	 * @since 2.0
	 */
	public static void addElementChangedListener(IElementChangedListener listener, int eventMask) {
		EGLModelManager.getEGLModelManager().addElementChangedListener(listener, eventMask);
	}

	/**
	 * Configures the given marker attribute map for the given EGL element.
	 * Used for markers, which denote a EGL element rather than a resource.
	 *
	 * @param attributes the mutable marker attribute map (key type: <code>String</code>,
	 *   value type: <code>String</code>)
	 * @param element the EGL element for which the marker needs to be configured
	 */
	public static void addEGLElementMarkerAttributes(Map attributes, IEGLElement element) {
		if (element instanceof IMember)
			element = ((IMember) element).getEGLFile();
		if (attributes != null && element != null)
			attributes.put(EDTCoreIDEPlugin.ATT_HANDLE_ID, element.getHandleIdentifier());
	}

	/**
	 * Returns the EGL model element corresponding to the given handle identifier
	 * generated by <code>IEGLElement.getHandleIdentifier()</code>, or
	 * <code>null</code> if unable to create the associated element.
	 */
	public static IEGLElement create(String handleIdentifier) {
		if (handleIdentifier == null) {
			return null;
		}
		try {
			return EGLModelManager.getEGLModelManager().getHandleFromMemento(handleIdentifier);
		} catch (EGLModelException e) {
			return null;
		}
	}
	/**
	 * Returns the EGL element corresponding to the given file, or
	 * <code>null</code> if unable to associate the given file
	 * with a EGL element.
	 *
	 * <p>The file must be one of:<ul>
	 *	<li>a <code>.egl</code> file - the element returned is the corresponding <code>ICompilationUnit</code></li>
	 *	<li>a <code>.class</code> file - the element returned is the corresponding <code>IClassFile</code></li>
	 *	<li>a <code>.jar</code> file - the element returned is the corresponding <code>IPackageFragmentRoot</code></li>
	 *	</ul>
	 * <p>
	 * Creating a EGL element has the side effect of creating and opening all of the
	 * element's parents if they are not yet open.
	 * 
	 * @param the given file
	 * @return the EGL element corresponding to the given file, or
	 * <code>null</code> if unable to associate the given file
	 * with a EGL element
	 */
	public static IEGLElement create(IFile file) {
		return EGLModelManager.create(file, null);
	}
	/**
	 * Returns the package fragment or package fragment root corresponding to the given folder, or
	 * <code>null</code> if unable to associate the given folder with a EGL element.
	 * <p>
	 * Note that a package fragment root is returned rather than a default package.
	 * <p>
	 * Creating a EGL element has the side effect of creating and opening all of the
	 * element's parents if they are not yet open.
	 * 
	 * @param the given folder
	 * @return the package fragment or package fragment root corresponding to the given folder, or
	 * <code>null</code> if unable to associate the given folder with a EGL element
	 */
	public static IEGLElement create(IFolder folder) {
		return EGLModelManager.create(folder, null);
	}
	/**
	 * Returns the EGL project corresponding to the given project.
	 * <p>
	 * Creating a EGL Project has the side effect of creating and opening all of the
	 * project's parents if they are not yet open.
	 * <p>
	 * Note that no check is done at this time on the existence or the egl nature of this project.
	 * 
	 * @param project the given project
	 * @return the EGL project corresponding to the given project, null if the given project is null
	 */
	public static IEGLProject create(IProject project) {
		if (project == null) {
			return null;
		}
		EGLModel eglModel = EGLModelManager.getEGLModelManager().getEGLModel();
		return eglModel.getEGLProject(project);
	}
	/**
	 * Returns the EGL element corresponding to the given resource, or
	 * <code>null</code> if unable to associate the given resource
	 * with a EGL element.
	 * <p>
	 * The resource must be one of:<ul>
	 *	<li>a project - the element returned is the corresponding <code>IEGLProject</code></li>
	 *	<li>a <code>.egl</code> file - the element returned is the corresponding <code>IEGLFile</code></li>
	 *  <li>a folder - the element returned is the corresponding <code>IPackageFragmentRoot</code>
	 *    	or <code>IPackageFragment</code></li>
	 *  <li>the workspace root resource - the element returned is the <code>IEGLModel</code></li>
	 *	</ul>
	 * <p>
	 * Creating a EGL element has the side effect of creating and opening all of the
	 * element's parents if they are not yet open.
	 * 
	 * @param resource the given resource
	 * @return the EGL element corresponding to the given resource, or
	 * <code>null</code> if unable to associate the given resource
	 * with a EGL element
	 */
	public static IEGLElement create(IResource resource) {
		return EGLModelManager.create(resource, null);
	}
	/**
	 * Returns the EGL model.
	 * 
	 * @param root the given root
	 * @return the EGL model, or <code>null</code> if the root is null
	 */
	public static IEGLModel create(IWorkspaceRoot root) {
		if (root == null) {
			return null;
		}
		return EGLModelManager.getEGLModelManager().getEGLModel();
	}
	/**
	 * Creates and returns a compilation unit element for
	 * the given <code>.egl</code> file. Returns <code>null</code> if unable
	 * to recognize the compilation unit.
	 * 
	 * @param file the given <code>.egl</code> file
	 * @return a compilation unit element for the given <code>.egl</code> file, or <code>null</code> if unable
	 * to recognize the compilation unit
	 */
	public static IEGLFile createEGLFileFrom(IFile file) {
		return EGLModelManager.createEGLFileFrom(file, null);
	}

	/**
	 * Returns a table of all known configurable options with their default values.
	 * These options allow to configure the behaviour of the underlying components.
	 * The client may safely use the result as a template that they can modify and
	 * then pass to <code>setOptions</code>.
	 * 
	 * Helper constants have been defined on JavaCore for each of the option ID and 
	 * their possible constant values.
	 * 
	 * Note: more options might be added in further releases.
	 * <pre>
	 * RECOGNIZED OPTIONS:
	 * COMPILER / Generating Local Variable Debug Attribute
	 *    When generated, this attribute will enable local variable names 
	 *    to be displayed in debugger, only in place where variables are 
	 *    definitely assigned (.class file is then bigger)
	 *     - option id:         "org.eclipse.jdt.core.compiler.debug.localVariable"
	 *     - possible values:   { "generate", "do not generate" }
	 *     - default:           "generate"
	 *
	 * COMPILER / Generating Line Number Debug Attribute 
	 *    When generated, this attribute will enable source code highlighting in debugger 
	 *    (.class file is then bigger).
	 *     - option id:         "org.eclipse.jdt.core.compiler.debug.lineNumber"
	 *     - possible values:   { "generate", "do not generate" }
	 *     - default:           "generate"
	 *    
	 * COMPILER / Generating Source Debug Attribute 
	 *    When generated, this attribute will enable the debugger to present the 
	 *    corresponding source code.
	 *     - option id:         "org.eclipse.jdt.core.compiler.debug.sourceFile"
	 *     - possible values:   { "generate", "do not generate" }
	 *     - default:           "generate"
	 *    
	 * COMPILER / Preserving Unused Local Variables
	 *    Unless requested to preserve unused local variables (that is, never read), the 
	 *    compiler will optimize them out, potentially altering debugging
	 *     - option id:         "org.eclipse.jdt.core.compiler.codegen.unusedLocal"
	 *     - possible values:   { "preserve", "optimize out" }
	 *     - default:           "preserve"
	 * 
	 * COMPILER / Defining Target Java Platform
	 *    For binary compatibility reason, .class files can be tagged to with certain VM versions and later.
	 *    Note that "1.4" target require to toggle compliance mode to "1.4" too.
	 *     - option id:         "org.eclipse.jdt.core.compiler.codegen.targetPlatform"
	 *     - possible values:   { "1.1", "1.2", "1.3", "1.4" }
	 *     - default:           "1.1"
	 *
	 * COMPILER / Reporting Unreachable Code
	 *    Unreachable code can optionally be reported as an error, warning or simply 
	 *    ignored. The bytecode generation will always optimized it out.
	 *     - option id:         "org.eclipse.jdt.core.compiler.problem.unreachableCode"
	 *     - possible values:   { "error", "warning", "ignore" }
	 *     - default:           "error"
	 *
	 * COMPILER / Reporting Invalid Import
	 *    An import statement that cannot be resolved might optionally be reported 
	 *    as an error, as a warning or ignored.
	 *     - option id:         "org.eclipse.jdt.core.compiler.problem.invalidImport"
	 *     - possible values:   { "error", "warning", "ignore" }
	 *     - default:           "error"
	 *
	 * COMPILER / Reporting Attempt to Override Package-Default Method
	 *    A package default method is not visible in a different package, and thus 
	 *    cannot be overridden. When enabling this option, the compiler will signal 
	 *    such scenarii either as an error or a warning.
	 *     - option id:         "org.eclipse.jdt.core.compiler.problem.overridingPackageDefaultMethod"
	 *     - possible values:   { "error", "warning", "ignore" }
	 *     - default:           "warning"
	 *
	 * COMPILER / Reporting Method With Constructor Name
	 *    Naming a method with a constructor name is generally considered poor 
	 *    style programming. When enabling this option, the compiler will signal such 
	 *    scenarii either as an error or a warning.
	 *     - option id:         "org.eclipse.jdt.core.compiler.problem.methodWithConstructorName"
	 *     - possible values:   { "error", "warning", "ignore" }
	 *     - default:           "warning"
	 *
	 * COMPILER / Reporting Deprecation
	 *    When enabled, the compiler will signal use of deprecated API either as an 
	 *    error or a warning.
	 *     - option id:         "org.eclipse.jdt.core.compiler.problem.deprecation"
	 *     - possible values:   { "error", "warning", "ignore" }
	 *     - default:           "warning"
	 *
	 * COMPILER / Reporting Deprecation Inside Deprecated Code
	 *    When enabled, the compiler will signal use of deprecated API inside deprecated code.
	 *    The severity of the problem is controlled with option "org.eclipse.jdt.core.compiler.problem.deprecation".
	 *     - option id:         "org.eclipse.jdt.core.compiler.problem.deprecationInDeprecatedCode"
	 *     - possible values:   { "enabled", "disabled" }
	 *     - default:           "disabled"
	 *
	 * COMPILER / Reporting Hidden Catch Block
	 *    Locally to a try statement, some catch blocks may hide others . For example,
	 *      try {  throw new java.io.CharConversionException();
	 *      } catch (java.io.CharConversionException e) {
	 *      } catch (java.io.IOException e) {}. 
	 *    When enabling this option, the compiler will issue an error or a warning for hidden 
	 *    catch blocks corresponding to checked exceptions
	 *     - option id:         "org.eclipse.jdt.core.compiler.problem.hiddenCatchBlock"
	 *     - possible values:   { "error", "warning", "ignore" }
	 *     - default:           "warning"
	 *
	 * COMPILER / Reporting Unused Local
	 *    When enabled, the compiler will issue an error or a warning for unused local 
	 *    variables (that is, variables never read from)
	 *     - option id:         "org.eclipse.jdt.core.compiler.problem.unusedLocal"
	 *     - possible values:   { "error", "warning", "ignore" }
	 *     - default:           "ignore"
	 *
	 * COMPILER / Reporting Unused Parameter
	 *    When enabled, the compiler will issue an error or a warning for unused method 
	 *    parameters (that is, parameters never read from)
	 *     - option id:         "org.eclipse.jdt.core.compiler.problem.unusedParameter"
	 *     - possible values:   { "error", "warning", "ignore" }
	 *     - default:           "ignore"
	 *
	 * COMPILER / Reporting Unused Parameter if Implementing Abstract Method
	 *    When enabled, the compiler will signal unused parameters in abstract method implementations.
	 *    The severity of the problem is controlled with option "org.eclipse.jdt.core.compiler.problem.unusedParameter".
	 *     - option id:         "org.eclipse.jdt.core.compiler.problem.unusedParameterWhenImplementingAbstract"
	 *     - possible values:   { "enabled", "disabled" }
	 *     - default:           "disabled"
	 *
	 * COMPILER / Reporting Unused Parameter if Overriding Concrete Method
	 *    When enabled, the compiler will signal unused parameters in methods overriding concrete ones.
	 *    The severity of the problem is controlled with option "org.eclipse.jdt.core.compiler.problem.unusedParameter".
	 *     - option id:         "org.eclipse.jdt.core.compiler.problem.unusedParameterWhenOverridingConcrete"
	 *     - possible values:   { "enabled", "disabled" }
	 *     - default:           "disabled"
	 *
	 * COMPILER / Reporting Unused Import
	 *    When enabled, the compiler will issue an error or a warning for unused import 
	 *    reference 
	 *     - option id:         "org.eclipse.jdt.core.compiler.problem.unusedImport"
	 *     - possible values:   { "error", "warning", "ignore" }
	 *     - default:           "warning"
	 *
	 * COMPILER / Reporting Unused Private Members
	 *    When enabled, the compiler will issue an error or a warning whenever a private 
	 *    method or field is declared but never used within the same unit.
	 *     - option id:         "org.eclipse.jdt.core.compiler.problem.unusedPrivateMember"
	 *     - possible values:   { "error", "warning", "ignore" }
	 *     - default:           "ignore"
	 *
	 * COMPILER / Reporting Synthetic Access Emulation
	 *    When enabled, the compiler will issue an error or a warning whenever it emulates 
	 *    access to a non-accessible member of an enclosing type. Such access can have
	 *    performance implications.
	 *     - option id:         "org.eclipse.jdt.core.compiler.problem.syntheticAccessEmulation"
	 *     - possible values:   { "error", "warning", "ignore" }
	 *     - default:           "ignore"
	 *
	 * COMPILER / Reporting Non-Externalized String Literal
	 *    When enabled, the compiler will issue an error or a warning for non externalized 
	 *    String literal (that is, not tagged with //$NON-NLS-<n>$). 
	 *     - option id:         "org.eclipse.jdt.core.compiler.problem.nonExternalizedStringLiteral"
	 *     - possible values:   { "error", "warning", "ignore" }
	 *     - default:           "ignore"
	 * 
	 * COMPILER / Reporting Usage of 'assert' Identifier
	 *    When enabled, the compiler will issue an error or a warning whenever 'assert' is 
	 *    used as an identifier (reserved keyword in 1.4)
	 *     - option id:         "org.eclipse.jdt.core.compiler.problem.assertIdentifier"
	 *     - possible values:   { "error", "warning", "ignore" }
	 *     - default:           "ignore"
	 * 
	 * COMPILER / Reporting Non-Static Reference to a Static Member
	 *    When enabled, the compiler will issue an error or a warning whenever a static field
	 *    or method is accessed with an expression receiver. A reference to a static member should
	 *    be qualified with a type name.
	 *     - option id:         "org.eclipse.jdt.core.compiler.problem.staticAccessReceiver"
	 *     - possible values:   { "error", "warning", "ignore" }
	 *     - default:           "warning"
	 * 
	 * COMPILER / Reporting Assignment with no Effect
	 *    When enabled, the compiler will issue an error or a warning whenever an assignment
	 *    has no effect (e.g 'x = x').
	 *     - option id:         "org.eclipse.jdt.core.compiler.problem.noEffectAssignment"
	 *     - possible values:   { "error", "warning", "ignore" }
	 *     - default:           "warning"
	 * 
	 * COMPILER / Reporting Interface Method not Compatible with non-Inherited Methods
	 *    When enabled, the compiler will issue an error or a warning whenever an interface
	 *    defines a method incompatible with a non-inherited Object method. Until this conflict
	 *    is resolved, such an interface cannot be implemented, For example, 
	 *      interface I { 
	 *         int clone();
	 *      } 
	 *     - option id:         "org.eclipse.jdt.core.compiler.problem.incompatibleNonInheritedInterfaceMethod"
	 *     - possible values:   { "error", "warning", "ignore" }
	 *     - default:           "warning"
	 * 
	 * COMPILER / Reporting Usage of char[] Expressions in String Concatenations
	 *    When enabled, the compiler will issue an error or a warning whenever a char[] expression
	 *    is used in String concatenations (for example, "hello" + new char[]{'w','o','r','l','d'}).
	 *     - option id:         "org.eclipse.jdt.core.compiler.problem.noImplicitStringConversion"
	 *     - possible values:   { "error", "warning", "ignore" }
	 *     - default:           "warning"
	 *
	 * COMPILER / Setting Source Compatibility Mode
	 *    Specify whether source is 1.3 or 1.4 compatible. From 1.4 on, 'assert' is a keyword
	 *    reserved for assertion support. Also note, than when toggling to 1.4 mode, the target VM
	 *   level should be set to "1.4" and the compliance mode should be "1.4".
	 *     - option id:         "org.eclipse.jdt.core.compiler.source"
	 *     - possible values:   { "1.3", "1.4" }
	 *     - default:           "1.3"
	 * 
	 * COMPILER / Setting Compliance Level
	 *    Select the compliance level for the compiler. In "1.3" mode, source and target settings
	 *    should not go beyond "1.3" level.
	 *     - option id:         "org.eclipse.jdt.core.compiler.compliance"
	 *     - possible values:   { "1.3", "1.4" }
	 *     - default:           "1.3"
	 * 
	 * COMPILER / Maximum number of problems reported per compilation unit
	 *    Specify the maximum number of problems reported on each compilation unit.
	 *     - option id:         "org.eclipse.jdt.core.compiler.maxProblemPerUnit"
	 *     - possible values:	"<n>" where <n> is zero or a positive integer (if zero then all problems are reported).
	 *     - default:           "100"
	 * 
	 * COMPILER / Define the Automatic Task Tags
	 *    When the tag list is not empty, the compiler will issue a task marker whenever it encounters
	 *    one of the corresponding tag inside any comment in Java source code.
	 *    Generated task messages will include the tag, and range until the next line separator or comment ending.
	 *    Note that tasks messages are trimmed.
	 *     - option id:         "org.eclipse.jdt.core.compiler.taskTags"
	 *     - possible values:   { "<tag>[,<tag>]*" } where <tag> is a String without any wild-card or leading/trailing spaces 
	 *     - default:           ""
	 * 
	 * COMPILER / Define the Automatic Task Priorities
	 *    In parallel with the Automatic Task Tags, this list defines the priorities (high, normal or low)
	 *    of the task markers issued by the compiler.
	 *    If the default is specified, the priority of each task marker is "NORMAL".
	 *     - option id:         "org.eclipse.jdt.core.compiler.taskPriorities"
	 *     - possible values:   { "<priority>[,<priority>]*" } where <priority> is one of "HIGH", "NORMAL" or "LOW"
	 *     - default:           ""
	 *
	 * BUILDER / Specifying Filters for Resource Copying Control
	 *    Allow to specify some filters to control the resource copy process.
	 *     - option id:         "org.eclipse.jdt.core.builder.resourceCopyExclusionFilter"
	 *     - possible values:   { "<name>[,<name>]* } where <name> is a file name pattern (* and ? wild-cards allowed)
	 *       or the name of a folder which ends with '/'
	 *     - default:           ""
	 * 
	 * BUILDER / Abort if Invalid Classpath
	 *    Allow to toggle the builder to abort if the classpath is invalid
	 *     - option id:         "org.eclipse.jdt.core.builder.invalidClasspath"
	 *     - possible values:   { "abort", "ignore" }
	 *     - default:           "abort"
	 * 
	 * BUILDER / Cleaning Output Folder(s)
	 *    Indicate whether the JavaBuilder is allowed to clean the output folders
	 *    when performing full build operations.
	 *     - option id:         "org.eclipse.jdt.core.builder.cleanOutputFolder"
	 *     - possible values:   { "clean", "ignore" }
	 *     - default:           "clean"
	 * 
	 * BUILDER / Reporting Duplicate Resources
	 *    Indicate the severity of the problem reported when more than one occurrence
	 *    of a resource is to be copied into the output location.
	 *     - option id:         "org.eclipse.jdt.core.builder.duplicateResourceTask"
	 *     - possible values:   { "error", "warning" }
	 *     - default:           "warning"
	 * 
	 * JAVACORE / Computing Project Build Order
	 *    Indicate whether JavaCore should enforce the project build order to be based on
	 *    the classpath prerequisite chain. When requesting to compute, this takes over
	 *    the platform default order (based on project references).
	 *     - option id:         "org.eclipse.jdt.core.computeJavaBuildOrder"
	 *     - possible values:   { "compute", "ignore" }
	 *     - default:           "ignore"	 
	 * 
	 * JAVACORE / Specify Default Source Encoding Format
	 *    Get the encoding format for compiled sources. This setting is read-only, it is equivalent
	 *    to 'ResourcesPlugin.getEncoding()'.
	 *     - option id:         "org.eclipse.jdt.core.encoding"
	 *     - possible values:   { any of the supported encoding name}.
	 *     - default:           <platform default>
	 * 
	 * JAVACORE / Reporting Incomplete Classpath
	 *    Indicate the severity of the problem reported when an entry on the classpath does not exist, 
	 *    is not legite or is not visible (for example, a referenced project is closed).
	 *     - option id:         "org.eclipse.jdt.core.incompleteClasspath"
	 *     - possible values:   { "error", "warning"}
	 *     - default:           "error"
	 * 
	 * JAVACORE / Reporting Classpath Cycle
	 *    Indicate the severity of the problem reported when a project is involved in a cycle.
	 *     - option id:         "org.eclipse.jdt.core.circularClasspath"
	 *     - possible values:   { "error", "warning" }
	 *     - default:           "error"
	 * 
	 * JAVACORE / Enabling Usage of Classpath Exclusion Patterns
	 *    When disabled, no entry on a project classpath can be associated with
	 *    an exclusion pattern.
	 *     - option id:         "org.eclipse.jdt.core.classpath.exclusionPatterns"
	 *     - possible values:   { "enabled", "disabled" }
	 *     - default:           "enabled"
	 * 
	 * JAVACORE / Enabling Usage of Classpath Multiple Output Locations
	 *    When disabled, no entry on a project classpath can be associated with
	 *    a specific output location, preventing thus usage of multiple output locations.
	 *     - option id:         "org.eclipse.jdt.core.classpath.multipleOutputLocations"
	 *     - possible values:   { "enabled", "disabled" }
	 *     - default:           "enabled"
	 * 
	 *	FORMATTER / Inserting New Line Before Opening Brace
	 *    When Insert, a new line is inserted before an opening brace, otherwise nothing
	 *    is inserted
	 *     - option id:         "org.eclipse.jdt.core.formatter.newline.openingBrace"
	 *     - possible values:   { "insert", "do not insert" }
	 *     - default:           "do not insert"
	 * 
	 *	FORMATTER / Inserting New Line Inside Control Statement
	 *    When Insert, a new line is inserted between } and following else, catch, finally
	 *     - option id:         "org.eclipse.jdt.core.formatter.newline.controlStatement"
	 *     - possible values:   { "insert", "do not insert" }
	 *     - default:           "do not insert"
	 * 
	 *	FORMATTER / Clearing Blank Lines
	 *    When Clear all, all blank lines are removed. When Preserve one, only one is kept
	 *    and all others removed.
	 *     - option id:         "org.eclipse.jdt.core.formatter.newline.clearAll"
	 *     - possible values:   { "clear all", "preserve one" }
	 *     - default:           "preserve one"
	 * 
	 *	FORMATTER / Inserting New Line Between Else/If 
	 *    When Insert, a blank line is inserted between an else and an if when they are 
	 *    contiguous. When choosing to not insert, else-if will be kept on the same
	 *    line when possible.
	 *     - option id:         "org.eclipse.jdt.core.formatter.newline.elseIf"
	 *     - possible values:   { "insert", "do not insert" }
	 *     - default:           "do not insert"
	 * 
	 *	FORMATTER / Inserting New Line In Empty Block
	 *    When insert, a line break is inserted between contiguous { and }, if } is not followed
	 *    by a keyword.
	 *     - option id:         "org.eclipse.jdt.core.formatter.newline.emptyBlock"
	 *     - possible values:   { "insert", "do not insert" }
	 *     - default:           "insert"
	 * 
	 *	FORMATTER / Splitting Lines Exceeding Length
	 *    Enable splitting of long lines (exceeding the configurable length). Length of 0 will
	 *    disable line splitting
	 *     - option id:         "org.eclipse.jdt.core.formatter.lineSplit"
	 *     - possible values:	"<n>", where n is zero or a positive integer
	 *     - default:           "80"
	 * 
	 *	FORMATTER / Compacting Assignment
	 *    Assignments can be formatted asymmetrically, for example 'int x= 2;', when Normal, a space
	 *    is inserted before the assignment operator
	 *     - option id:         "org.eclipse.jdt.core.formatter.style.assignment"
	 *     - possible values:   { "compact", "normal" }
	 *     - default:           "normal"
	 * 
	 *	FORMATTER / Defining Indentation Character
	 *    Either choose to indent with tab characters or spaces
	 *     - option id:         "org.eclipse.jdt.core.formatter.tabulation.char"
	 *     - possible values:   { "tab", "space" }
	 *     - default:           "tab"
	 * 
	 *	FORMATTER / Defining Space Indentation Length
	 *    When using spaces, set the amount of space characters to use for each 
	 *    indentation mark.
	 *     - option id:         "org.eclipse.jdt.core.formatter.tabulation.size"
	 *     - possible values:	"<n>", where n is a positive integer
	 *     - default:           "4"
	 * 
	 *	FORMATTER / Inserting space in cast expression
	 *    When Insert, a space is added between the type and the expression in a cast expression.
	 *     - option id:         "org.eclipse.jdt.core.formatter.space.castexpression"
	 *     - possible values:   { "insert", "do not insert" }
	 *     - default:           "insert"
	 * 
	 *	CODEASSIST / Activate Visibility Sensitive Completion
	 *    When active, completion doesn't show that you can not see
	 *    (for example, you can not see private methods of a super class).
	 *     - option id:         "org.eclipse.jdt.core.codeComplete.visibilityCheck"
	 *     - possible values:   { "enabled", "disabled" }
	 *     - default:           "disabled"
	 * 
	 *	CODEASSIST / Automatic Qualification of Implicit Members
	 *    When active, completion automatically qualifies completion on implicit
	 *    field references and message expressions.
	 *     - option id:         "org.eclipse.jdt.core.codeComplete.forceImplicitQualification"
	 *     - possible values:   { "enabled", "disabled" }
	 *     - default:           "disabled"
	 * 
	 *  CODEASSIST / Define the Prefixes for Field Name
	 *    When the prefixes is non empty, completion for field name will begin with
	 *    one of the proposed prefixes.
	 *     - option id:         "org.eclipse.jdt.core.codeComplete.fieldPrefixes"
	 *     - possible values:   { "<prefix>[,<prefix>]*" } where <prefix> is a String without any wild-card 
	 *     - default:           ""
	 * 
	 *  CODEASSIST / Define the Prefixes for Static Field Name
	 *    When the prefixes is non empty, completion for static field name will begin with
	 *    one of the proposed prefixes.
	 *     - option id:         "org.eclipse.jdt.core.codeComplete.staticFieldPrefixes"
	 *     - possible values:   { "<prefix>[,<prefix>]*" } where <prefix> is a String without any wild-card 
	 *     - default:           ""
	 * 
	 *  CODEASSIST / Define the Prefixes for Local Variable Name
	 *    When the prefixes is non empty, completion for local variable name will begin with
	 *    one of the proposed prefixes.
	 *     - option id:         "org.eclipse.jdt.core.codeComplete.localPrefixes"
	 *     - possible values:   { "<prefix>[,<prefix>]*" } where <prefix> is a String without any wild-card 
	 *     - default:           ""
	 * 
	 *  CODEASSIST / Define the Prefixes for Argument Name
	 *    When the prefixes is non empty, completion for argument name will begin with
	 *    one of the proposed prefixes.
	 *     - option id:         "org.eclipse.jdt.core.codeComplete.argumentPrefixes"
	 *     - possible values:   { "<prefix>[,<prefix>]*" } where <prefix> is a String without any wild-card 
	 *     - default:           ""
	 * 
	 *  CODEASSIST / Define the Suffixes for Field Name
	 *    When the suffixes is non empty, completion for field name will end with
	 *    one of the proposed suffixes.
	 *     - option id:         "org.eclipse.jdt.core.codeComplete.fieldSuffixes"
	 *     - possible values:   { "<suffix>[,<suffix>]*" } where <suffix> is a String without any wild-card 
	 *     - default:           ""
	 * 
	 *  CODEASSIST / Define the Suffixes for Static Field Name
	 *    When the suffixes is non empty, completion for static field name will end with
	 *    one of the proposed suffixes.
	 *     - option id:         "org.eclipse.jdt.core.codeComplete.staticFieldSuffixes"
	 *     - possible values:   { "<suffix>[,<suffix>]*" } where <suffix> is a String without any wild-card 
	 *     - default:           ""
	 * 
	 *  CODEASSIST / Define the Suffixes for Local Variable Name
	 *    When the suffixes is non empty, completion for local variable name will end with
	 *    one of the proposed suffixes.
	 *     - option id:         "org.eclipse.jdt.core.codeComplete.localSuffixes"
	 *     - possible values:   { "<suffix>[,<suffix>]*" } where <suffix> is a String without any wild-card 
	 *     - default:           ""
	 * 
	 *  CODEASSIST / Define the Suffixes for Argument Name
	 *    When the suffixes is non empty, completion for argument name will end with
	 *    one of the proposed suffixes.
	 *     - option id:         "org.eclipse.jdt.core.codeComplete.argumentSuffixes"
	 *     - possible values:   { "<suffix>[,<suffix>]*" } where <prefix> is a String without any wild-card 
	 *     - default:           ""
	 * </pre>
	 * 
	 * @return a mutable table containing the default settings of all known options
	 *   (key type: <code>String</code>; value type: <code>String</code>)
	 * @see #setOptions
	 */
	public static Hashtable getDefaultOptions() {

		Hashtable defaultOptions = new Hashtable(10);

		// see #initializeDefaultPluginPreferences() for changing default settings
		Preferences preferences = getPlugin().getPluginPreferences();
		HashSet optionNames = EGLModelManager.OptionNames;

		// get preferences set to their default
		String[] defaultPropertyNames = preferences.defaultPropertyNames();
		for (int i = 0; i < defaultPropertyNames.length; i++) {
			String propertyName = defaultPropertyNames[i];
			if (optionNames.contains(propertyName)) {
				defaultOptions.put(propertyName, preferences.getDefaultString(propertyName));
			}
		}
		// get preferences not set to their default
		String[] propertyNames = preferences.propertyNames();
		for (int i = 0; i < propertyNames.length; i++) {
			String propertyName = propertyNames[i];
			if (optionNames.contains(propertyName)) {
				defaultOptions.put(propertyName, preferences.getDefaultString(propertyName));
			}
		}
		// get encoding through resource plugin
		defaultOptions.put(CORE_ENCODING, ResourcesPlugin.getEncoding());

		return defaultOptions;
	}
	/** 
	 * Answers the project specific value for a given EGLPath container.
	 * In case this container path could not be resolved, then will answer <code>null</code>.
	 * Both the container path and the project context are supposed to be non-null.
	 * <p>
	 * The containerPath is a formed by a first ID segment followed with extra segments, which can be 
	 * used as additional hints for resolution. If no container was ever recorded for this container path 
	 * onto this project (using <code>setEGLPathContainer</code>, then a 
	 * <code>EGLPathContainerInitializer</code> will be activated if any was registered for this container 
	 * ID onto the extension point "com.ibm.etools.egl.internal.model.core.EGLPathContainerInitializer".
	 * <p>
	 * There is no assumption that the returned container must answer the exact same containerPath
	 * when requested <code>IEGLPathContainer#getPath</code>. 
	 * Indeed, the containerPath is just an indication for resolving it to an actual container object.
	 * <p>
	 * EGLPath container values are persisted locally to the workspace, but 
	 * are not preserved from a session to another. It is thus highly recommended to register a 
	 * <code>EGLPathContainerInitializer</code> for each referenced container 
	 * (through the extension point "com.ibm.etools.egl.internal.model.core.EGLPathContainerInitializer").
	 * <p>
	 * @param containerPath the name of the container, which needs to be resolved
	 * @param project a specific project in which the container is being resolved
	 * @return the corresponding EGLPath container or <code>null</code> if unable to find one.
	 * 
	 * @exception EGLModelException if an exception occurred while resolving the container, or if the resolved container
	 *   contains illegal entries (contains CPE_CONTAINER entries or null entries).	 
	 * 
	 * @see EGLPathContainerInitializer
	 * @see IEGLPathContainer
	 * @see #setEGLPathContainer(IPath, IEGLProject[], IEGLPathContainer[], IProgressMonitor)
	 * @since 2.0
	 */
	public static IEGLPathContainer getEGLPathContainer(final IPath containerPath, final IEGLProject project) throws EGLModelException {

		IEGLPathContainer container = EGLModelManager.containerGet(project, containerPath);
		if (container == EGLModelManager.ContainerInitializationInProgress)
			return null; // break cycle

		if (container == null) {
			final EGLPathContainerInitializer initializer = EGLCore.getEGLPathContainerInitializer(containerPath.segment(0));
			if (initializer != null) {
				if (EGLModelManager.CP_RESOLVE_VERBOSE) {
					System.out.println("CPContainer INIT - triggering initialization of: [" + project.getElementName() + "] " + containerPath + " using initializer: " + initializer); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
					new Exception("FAKE exception for dumping current CPContainer ([" + project.getElementName() + "] " + containerPath + ")INIT invocation stack trace").printStackTrace(); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
				}
				EGLModelManager.containerPut(project, containerPath, EGLModelManager.ContainerInitializationInProgress);
				// avoid initialization cycles
				boolean ok = false;
				try {
					// wrap initializer call with Safe runnable in case initializer would be causing some grief
					Platform.run(new ISafeRunnable() {
						public void handleException(Throwable exception) {
							Util.log(exception, "Exception occurred in EGLPath container initializer: " + initializer); //$NON-NLS-1$
						}
						public void run() throws Exception {
							initializer.initialize(containerPath, project);
						}
					});

					// retrieve value (if initialization was successful)
					container = EGLModelManager.containerGet(project, containerPath);
					if (container == EGLModelManager.ContainerInitializationInProgress)
						return null; // break cycle
					ok = true;
				} finally {
					if (!ok)
						EGLModelManager.containerPut(project, containerPath, null); // flush cache
				}
				if (EGLModelManager.CP_RESOLVE_VERBOSE) {
					System.out.print("CPContainer INIT - after resolution: [" + project.getElementName() + "] " + containerPath + " --> "); //$NON-NLS-2$//$NON-NLS-1$//$NON-NLS-3$
					if (container != null) {
						System.out.print("container: " + container.getDescription() + " {"); //$NON-NLS-2$//$NON-NLS-1$
						IEGLPathEntry[] entries = container.getEGLPathEntries();
						if (entries != null) {
							for (int i = 0; i < entries.length; i++) {
								if (i > 0)
									System.out.println(", "); //$NON-NLS-1$
								System.out.println(entries[i]);
							}
						}
						System.out.println("}"); //$NON-NLS-1$
					} else {
						System.out.println("{unbound}"); //$NON-NLS-1$
					}
				}
			} else {
				if (EGLModelManager.CP_RESOLVE_VERBOSE) {
					System.out.println("CPContainer INIT - no initializer found for: " + project.getElementName() + "] " + containerPath); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
		return container;
	}

	/**
	 * Helper method finding the EGLPath container initializer registered for a given EGLPath container ID 
	 * or <code>null</code> if none was found while iterating over the contributions to extension point to
	 * the extension point "com.ibm.etools.egl.internal.model.core.EGLPathContainerInitializer".
	 * <p>
	 * A containerID is the first segment of any container path, used to identify the registered container initializer.
	 * <p>
	 * @param String - a containerID identifying a registered initializer
	 * @return EGLPathContainerInitializer - the registered EGLPath container initializer or <code>null</code> if 
	 * none was found.
	 * @since 2.1
	 */
	public static EGLPathContainerInitializer getEGLPathContainerInitializer(String containerID) {

		Plugin jdtCorePlugin = EGLCore.getPlugin();
		if (jdtCorePlugin == null)
			return null;

		IExtensionPoint extension = jdtCorePlugin.getDescriptor().getExtensionPoint(EGLModelManager.CPCONTAINER_INITIALIZER_EXTPOINT_ID);
		if (extension != null) {
			IExtension[] extensions = extension.getExtensions();
			for (int i = 0; i < extensions.length; i++) {
				IConfigurationElement[] configElements = extensions[i].getConfigurationElements();
				for (int j = 0; j < configElements.length; j++) {
					String initializerID = configElements[j].getAttribute("id"); //$NON-NLS-1$
					if (initializerID != null && initializerID.equals(containerID)) {
						if (EGLModelManager.CP_RESOLVE_VERBOSE) {
							System.out.println("CPContainer INIT - found initializer: " + containerID + " --> " + configElements[j].getAttribute("class")); //$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
						}
						try {
							Object execExt = configElements[j].createExecutableExtension("class"); //$NON-NLS-1$
							if (execExt instanceof EGLPathContainerInitializer) {
								return (EGLPathContainerInitializer) execExt;
							}
						} catch (CoreException e) {
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Returns the path held in the given EGLPath variable.
	 * Returns <node>null</code> if unable to bind.
	 * <p>
	 * EGLPath variable values are persisted locally to the workspace, and 
	 * are preserved from session to session.
	 * <p>
	 * Note that EGLPath variables can be contributed registered initializers for,
	 * using the extension point "com.ibm.etools.egl.internal.model.core.EGLPathVariableInitializer".
	 * If an initializer is registered for a variable, its persisted value will be ignored:
	 * its initializer will thus get the opportunity to rebind the variable differently on
	 * each session.
	 *
	 * @param variableName the name of the EGLPath variable
	 * @return the path, or <code>null</code> if none 
	 * @see #setEGLPathVariable
	 */
	public static IPath getEGLPathVariable(final String variableName) {

		IPath variablePath = EGLModelManager.variableGet(variableName);
		if (variablePath == EGLModelManager.VariableInitializationInProgress)
			return null; // break cycle

		if (variablePath != null) {
			return variablePath;
		}

		// even if persisted value exists, initializer is given priority, only if no initializer is found the persisted value is reused
		final EGLPathVariableInitializer initializer = EGLCore.getEGLPathVariableInitializer(variableName);
		if (initializer != null) {
			if (EGLModelManager.CP_RESOLVE_VERBOSE) {
				System.out.println("CPVariable INIT - triggering initialization of: " + variableName + " using initializer: " + initializer); //$NON-NLS-1$ //$NON-NLS-2$
				new Exception("FAKE exception for dumping current CPVariable (" + variableName + ")INIT invocation stack trace").printStackTrace(); //$NON-NLS-1$//$NON-NLS-2$
			}
			EGLModelManager.variablePut(variableName, EGLModelManager.VariableInitializationInProgress); // avoid initialization cycles
			boolean ok = false;
			try {
				// wrap initializer call with Safe runnable in case initializer would be causing some grief
				Platform.run(new ISafeRunnable() {
					public void handleException(Throwable exception) {
						Util.log(exception, "Exception occurred in EGLPath variable initializer: " + initializer + " while initializing variable: " + variableName); //$NON-NLS-1$ //$NON-NLS-2$
					}
					public void run() throws Exception {
						initializer.initialize(variableName);
					}
				});
				variablePath = (IPath) EGLModelManager.variableGet(variableName); // initializer should have performed side-effect
				if (variablePath == EGLModelManager.VariableInitializationInProgress)
					return null; // break cycle (initializer did not init or reentering call)
				if (EGLModelManager.CP_RESOLVE_VERBOSE) {
					System.out.println("CPVariable INIT - after initialization: " + variableName + " --> " + variablePath); //$NON-NLS-2$//$NON-NLS-1$
				}
				ok = true;
			} finally {
				if (!ok)
					EGLModelManager.variablePut(variableName, null); // flush cache
			}
		} else {
			if (EGLModelManager.CP_RESOLVE_VERBOSE) {
				System.out.println("CPVariable INIT - no initializer found for: " + variableName); //$NON-NLS-1$
			}
		}
		return variablePath;
	}

	/**
	 * Helper method finding the EGLPath variable initializer registered for a given EGLPath variable name 
	 * or <code>null</code> if none was found while iterating over the contributions to extension point to
	 * the extension point "com.ibm.etools.egl.internal.model.core.EGLPathVariableInitializer".
	 * <p>
	 * @param the given variable
	 * @return EGLPathVariableInitializer - the registered EGLPath variable initializer or <code>null</code> if 
	 * none was found.
	 * @since 2.1
	 */
	public static EGLPathVariableInitializer getEGLPathVariableInitializer(String variable) {

		Plugin jdtCorePlugin = EGLCore.getPlugin();
		if (jdtCorePlugin == null)
			return null;

		IExtensionPoint extension = jdtCorePlugin.getDescriptor().getExtensionPoint(EGLModelManager.CPVARIABLE_INITIALIZER_EXTPOINT_ID);
		if (extension != null) {
			IExtension[] extensions = extension.getExtensions();
			for (int i = 0; i < extensions.length; i++) {
				IConfigurationElement[] configElements = extensions[i].getConfigurationElements();
				for (int j = 0; j < configElements.length; j++) {
					try {
						String varAttribute = configElements[j].getAttribute("variable"); //$NON-NLS-1$
						if (variable.equals(varAttribute)) {
							if (EGLModelManager.CP_RESOLVE_VERBOSE) {
								System.out.println("CPVariable INIT - found initializer: " + variable + " --> " + configElements[j].getAttribute("class")); //$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
							}
							Object execExt = configElements[j].createExecutableExtension("class"); //$NON-NLS-1$
							if (execExt instanceof EGLPathVariableInitializer) {
								return (EGLPathVariableInitializer) execExt;
							}
						}
					} catch (CoreException e) {
					}
				}
			}
		}
		return null;
	}

	/**
	 * Returns the names of all known EGLPath variables.
	 * <p>
	 * EGLPath variable values are persisted locally to the workspace, and 
	 * are preserved from session to session.
	 * <p>
	 *
	 * @return the list of EGLPath variable names
	 * @see #setEGLPathVariable
	 */
	public static String[] getEGLPathVariableNames() {
		return EGLModelManager.variableNames();
	}

	/**
	 * Helper method for returning one option value only. Equivalent to <code>(String)JavaCore.getOptions().get(optionName)</code>
	 * Note that it may answer <code>null</code> if this option does not exist.
	 * <p>
	 * For a complete description of the configurable options, see <code>getDefaultOptions</code>.
	 * </p>
	 * 
	 * @param optionName the name of an option
	 * @return the String value of a given option
	 * @see JavaCore#getDefaultOptions
	 * @since 2.0
	 */
	public static String getOption(String optionName) {

		if (CORE_ENCODING.equals(optionName)) {
			return ResourcesPlugin.getEncoding();
		}
		if (EGLModelManager.OptionNames.contains(optionName)) {
			Preferences preferences = getPlugin().getPluginPreferences();
			return preferences.getString(optionName).trim();
		}
		return null;
	}

	/**
	 * Returns the table of the current options. Initially, all options have their default values,
	 * and this method returns a table that includes all known options.
	 * <p>
	 * For a complete description of the configurable options, see <code>getDefaultOptions</code>.
	 * </p>
	 * 
	 * @return table of current settings of all options 
	 *   (key type: <code>String</code>; value type: <code>String</code>)
	 * @see JavaCore#getDefaultOptions
	 */
	public static Hashtable getOptions() {

		Hashtable options = new Hashtable(10);

		// see #initializeDefaultPluginPreferences() for changing default settings
		Plugin plugin = getPlugin();
		if (plugin != null) {
			Preferences preferences = getPlugin().getPluginPreferences();
			HashSet optionNames = EGLModelManager.OptionNames;

			// get preferences set to their default
			String[] defaultPropertyNames = preferences.defaultPropertyNames();
			for (int i = 0; i < defaultPropertyNames.length; i++) {
				String propertyName = defaultPropertyNames[i];
				if (optionNames.contains(propertyName)) {
					options.put(propertyName, preferences.getDefaultString(propertyName));
				}
			}
			// get preferences not set to their default
			String[] propertyNames = preferences.propertyNames();
			for (int i = 0; i < propertyNames.length; i++) {
				String propertyName = propertyNames[i];
				if (optionNames.contains(propertyName)) {
					options.put(propertyName, preferences.getString(propertyName).trim());
				}
			}
			// get encoding through resource plugin
			options.put(CORE_ENCODING, ResourcesPlugin.getEncoding());
		}
		return options;
	}
	/**
	 * Returns the single instance of the EGL core plug-in runtime class.
	 * 
	 * @return the single instance of the EGL core plug-in runtime class
	 */
	public static Plugin getPlugin() {
		return EDTCoreIDEPlugin.getPlugin();
	}

	/**
	 * This is a helper method, which returns the resolved EGLPath entry denoted 
	 * by a given entry (if it is a variable entry). It is obtained by resolving the variable 
	 * reference in the first segment. Returns <node>null</code> if unable to resolve using 
	 * the following algorithm:
	 * <ul>
	 * <li> if variable segment cannot be resolved, returns <code>null</code></li>
	 * <li> finds a project, JAR or binary folder in the workspace at the resolved path location</li>
	 * <li> if none finds an external JAR file or folder outside the workspace at the resolved path location </li>
	 * <li> if none returns <code>null</code></li>
	 * </ul>
	 * <p>
	 * Variable source attachment path and root path are also resolved and recorded in the resulting EGLPath entry.
	 * <p>
	 * NOTE: This helper method does not handle EGLPath containers, for which should rather be used
	 * <code>EGLCore#getEGLPathContainer(IPath, IEGLProject)</code>.
	 * <p>
	 * 
	 * @param entry the given variable entry
	 * @return the resolved library or project EGLPath entry, or <code>null</code>
	 *   if the given variable entry could not be resolved to a valid EGLPath entry
	 */
	public static IEGLPathEntry getResolvedEGLPathEntry(IEGLPathEntry entry) {
		
		return entry;

//		TODO Do not handle variable yet
		/*
		if (entry.getEntryKind() != IEGLPathEntry.CPE_VARIABLE)
			return entry;
		else
			return null;
		
		
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IPath resolvedPath = EGLCore.getResolvedVariablePath(entry.getPath());
		if (resolvedPath == null)
			return null;
		
		Object target = EGLModel.getTarget(workspaceRoot, resolvedPath, false);
		if (target == null)
			return null;
		
		// inside the workspace
		if (target instanceof IResource) {
			IResource resolvedResource = (IResource) target;
			if (resolvedResource != null) {
				switch (resolvedResource.getType()) {
					
					case IResource.PROJECT :  
						// internal project
						return EGLCore.newProjectEntry(resolvedPath, entry.isExported());
						
					case IResource.FILE : 
						if (Util.isArchiveFileName(resolvedResource.getName())) {
							// internal binary archive
							return EGLCore.newLibraryEntry(
									resolvedPath,
									getResolvedVariablePath(entry.getSourceAttachmentPath()),
									getResolvedVariablePath(entry.getSourceAttachmentRootPath()),
									entry.isExported());
						}
						break;
						
					case IResource.FOLDER : 
						// internal binary folder
						return EGLCore.newLibraryEntry(
								resolvedPath,
								getResolvedVariablePath(entry.getSourceAttachmentPath()),
								getResolvedVariablePath(entry.getSourceAttachmentRootPath()),
								entry.isExported());
				}
			}
		}
		// outside the workspace
		if (target instanceof File) {
			File externalFile = (File) target;
			if (externalFile.isFile()) {
				String fileName = externalFile.getName().toLowerCase();
				if (fileName.endsWith(".jar"  //$NON-NLS-1$
					) || fileName.endsWith(".zip"  //$NON-NLS-1$
					)) { // external binary archive
					return EGLCore.newLibraryEntry(
							resolvedPath,
							getResolvedVariablePath(entry.getSourceAttachmentPath()),
							getResolvedVariablePath(entry.getSourceAttachmentRootPath()),
							entry.isExported());
				}
			} else { // external binary folder
				if (resolvedPath.isAbsolute()){
					return EGLCore.newLibraryEntry(
							resolvedPath,
							getResolvedVariablePath(entry.getSourceAttachmentPath()),
							getResolvedVariablePath(entry.getSourceAttachmentRootPath()),
							entry.isExported());
				}
			}
		}
		return null;
		*/
	}

	/**
	 * Resolve a variable path (helper method).
	 * 
	 * @param variablePath the given variable path
	 * @return the resolved variable path or <code>null</code> if none
	 */
	public static IPath getResolvedVariablePath(IPath variablePath) {

		if (variablePath == null)
			return null;
		int count = variablePath.segmentCount();
		if (count == 0)
			return null;

		// lookup variable	
		String variableName = variablePath.segment(0);
		IPath resolvedPath = EGLCore.getEGLPathVariable(variableName);
		if (resolvedPath == null)
			return null;

		// append path suffix
		if (count > 1) {
			resolvedPath = resolvedPath.append(variablePath.removeFirstSegments(1));
		}
		return resolvedPath;
	}

	/**
	 * Answers the shared working copies currently registered for this buffer factory. 
	 * Working copies can be shared by several clients using the same buffer factory,see 
	 * <code>IWorkingCopy.getSharedWorkingCopy</code>.
	 * 
	 * @param factory the given buffer factory
	 * @return the list of shared working copies for a given buffer factory
	 * @see IWorkingCopy
	 * @since 2.0
	 */
	public static IWorkingCopy[] getSharedWorkingCopies(IBufferFactory factory) {

		// if factory is null, default factory must be used
		if (factory == null)
			factory = BufferManager.getDefaultBufferManager().getDefaultBufferFactory();
		Map sharedWorkingCopies = EGLModelManager.getEGLModelManager().sharedWorkingCopies;

		Map perFactoryWorkingCopies = (Map) sharedWorkingCopies.get(factory);
		if (perFactoryWorkingCopies == null)
			return EGLModelManager.NoWorkingCopy;
		Collection copies = perFactoryWorkingCopies.values();
		IWorkingCopy[] result = new IWorkingCopy[copies.size()];
		copies.toArray(result);
		return result;
	}
	/**
	 * Returns whether the given marker references the given EGL element.
	 * Used for markers, which denote a EGL element rather than a resource.
	 *
	 * @param element the element
	 * @param marker the marker
	 * @return <code>true</code> if the marker references the element, false otherwise
	 * @exception CoreException if the <code>IMarker.getAttribute</code> on the marker fails 	 
	 */
	public static boolean isReferencedBy(IEGLElement element, IMarker marker) throws CoreException {

		// only match units or classfiles
		if (element instanceof IMember) {
			IMember member = (IMember) element;
			element = member.getEGLFile();
		}
		if (element == null)
			return false;
		if (marker == null)
			return false;

		String markerHandleId = (String) marker.getAttribute(EDTCoreIDEPlugin.ATT_HANDLE_ID);
		if (markerHandleId == null)
			return false;

		IEGLElement markerElement = EGLCore.create(markerHandleId);
		while (true) {
			if (element.equals(markerElement))
				return true; // external elements may still be equal with different handleIDs.

			break;
		}
		return false;
	}

	/**
	 * Returns whether the given marker delta references the given EGL element.
	 * Used for markers deltas, which denote a EGL element rather than a resource.
	 *
	 * @param element the element
	 * @param markerDelta the marker delta
	 * @return <code>true</code> if the marker delta references the element
	 * @exception CoreException if the  <code>IMarkerDelta.getAttribute</code> on the marker delta fails 	 
	 */
	public static boolean isReferencedBy(IEGLElement element, IMarkerDelta markerDelta) throws CoreException {

		// only match units or classfiles
		if (element instanceof IMember) {
			IMember member = (IMember) element;
			element = member.getEGLFile();
		}
		if (element == null)
			return false;
		if (markerDelta == null)
			return false;

		String markerDeltarHandleId = (String) markerDelta.getAttribute(EDTCoreIDEPlugin.ATT_HANDLE_ID);
		if (markerDeltarHandleId == null)
			return false;

		IEGLElement markerElement = EGLCore.create(markerDeltarHandleId);
		while (true) {
			if (element.equals(markerElement))
				return true; // external elements may still be equal with different handleIDs.

			break;
		}
		return false;
	}

	/**
	 * Creates and returns a new EGLPath entry of kind <code>CPE_CONTAINER</code>
	 * for the given path. The path of the container will be used during resolution so as to map this
	 * container entry to a set of other EGLPath entries the container is acting for.
	 * <p>
	 * A container entry allows to express indirect references to a set of libraries, projects and variable entries,
	 * which can be interpreted differently for each EGL project where it is used.
	 * A EGLPath container entry can be resolved using <code>EGLCore.getResolvedEGLPathContainer</code>,
	 * and updated with <code>EGLCore.EGLPathContainerChanged</code>
	 * <p>
	 * A container is exclusively resolved by a <code>EGLPathContainerInitializer</code> registered onto the
	 * extension point "com.ibm.etools.egl.internal.model.core.EGLPathContainerInitializer".
	 * <p>
	 * A container path must be formed of at least one segment, where: <ul>
	 * <li> the first segment is a unique ID identifying the target container, there must be a container initializer registered
	 * 	onto this ID through the extension point  "com.ibm.etools.egl.internal.model.core.EGLPathContainerInitializer". </li>
	 * <li> the remaining segments will be passed onto the initializer, and can be used as additional
	 * 	hints during the initialization phase. </li>
	 * </ul>
	 * <p>
	 * Example of an EGLPathContainerInitializer for a EGLPath container denoting a default JDK container:
	 * 
	 * containerEntry = EGLCore.newContainerEntry(new Path("MyProvidedJDK/default"));
	 * 
	 * <extension
	 *    point="com.ibm.etools.egl.internal.model.core.EGLPathContainerInitializer">
	 *    <containerInitializer
	 *       id="MyProvidedJDK"
	 *       class="com.example.MyInitializer"/> 
	 * <p>
	 * Note that this operation does not attempt to validate EGLPath containers
	 * or access the resources at the given paths.
	 * <p>
	 * The resulting entry is not exported to dependent projects. This method is equivalent to
	 * <code>newContainerEntry(-,false)</code>.
	 * <p>
	 * @param containerPath the path identifying the container, it must be formed of two
	 * 	segments
	 * @return a new container EGLPath entry
	 * 
	 * @see EGLCore#getEGLPathContainer(IPath, IEGLProject)
	 * @see EGLCore#newContainerEntry(IPath, boolean)
	 * @since 2.0
	 */
	public static IEGLPathEntry newContainerEntry(IPath containerPath) {

		return newContainerEntry(containerPath, false);
	}

	/**
	 * Creates and returns a new EGLPath entry of kind <code>CPE_CONTAINER</code>
	 * for the given path. The path of the container will be used during resolution so as to map this
	 * container entry to a set of other EGLPath entries the container is acting for.
	 * <p>
	 * A container entry allows to express indirect references to a set of libraries, projects and variable entries,
	 * which can be interpreted differently for each EGL project where it is used.
	 * A EGLPath container entry can be resolved using <code>EGLCore.getResolvedEGLPathContainer</code>,
	 * and updated with <code>EGLCore.EGLPathContainerChanged</code>
	 * <p>
	 * A container is exclusively resolved by a <code>EGLPathContainerInitializer</code> registered onto the
	 * extension point "com.ibm.etools.egl.internal.model.core.EGLPathContainerInitializer".
	 * <p>
	 * A container path must be formed of at least one segment, where: <ul>
	 * <li> the first segment is a unique ID identifying the target container, there must be a container initializer registered
	 * 	onto this ID through the extension point  "com.ibm.etools.egl.internal.model.core.EGLPathContainerInitializer". </li>
	 * <li> the remaining segments will be passed onto the initializer, and can be used as additional
	 * 	hints during the initialization phase. </li>
	 * </ul>
	 * <p>
	 * Example of an EGLPathContainerInitializer for a EGLPath container denoting a default JDK container:
	 * 
	 * containerEntry = EGLCore.newContainerEntry(new Path("MyProvidedJDK/default"));
	 * 
	 * <extension
	 *    point="com.ibm.etools.egl.internal.model.core.EGLPathContainerInitializer">
	 *    <containerInitializer
	 *       id="MyProvidedJDK"
	 *       class="com.example.MyInitializer"/> 
	 * <p>
	 * Note that this operation does not attempt to validate EGLPath containers
	 * or access the resources at the given paths.
	 * <p>
	 * @param containerPath the path identifying the container, it must be formed of at least
	 * 	one segment (ID+hints)
	 * @param isExported a boolean indicating whether this entry is contributed to dependent
	 *    projects in addition to the output location
	 * @return a new container EGLPath entry
	 * 
	 * @see EGLCore#getEGLPathContainer(IPath, IEGLProject)
	 * @see EGLCore#setEGLPathContainer(IPath, IEGLProject[], IEGLPathContainer[], IProgressMonitor)
	 * @see EGLCore#newContainerEntry(IPath, boolean)
	 * @since 2.0
	 */
	public static IEGLPathEntry newContainerEntry(IPath containerPath, boolean isExported) {

		if (containerPath == null || containerPath.segmentCount() < 1) {
			Assert.isTrue(false, "Illegal EGLPath container path: \'" + containerPath.makeRelative().toString() + "\', must have at least one segment (containerID+hints)"); //$NON-NLS-1$//$NON-NLS-2$
		}
		return new EGLPathEntry(
			IPackageFragmentRoot.K_SOURCE,
			IEGLPathEntry.CPE_CONTAINER,
			containerPath,
			EGLPathEntry.EXCLUDE_NONE);
	}

	/**
	 * Creates and returns a new non-exported EGLPath entry of kind <code>CPE_LIBRARY</code> for the 
	 * JAR or folder identified by the given absolute path. This specifies that all package fragments 
	 * within the root will have children of type <code>IClassFile</code>.
	 * <p>
	 * A library entry is used to denote a prerequisite JAR or root folder containing binaries.
	 * The target JAR or folder can either be defined internally to the workspace (absolute path relative
	 * to the workspace root) or externally to the workspace (absolute path in the file system).
	 * <p>
	 * e.g. Here are some examples of binary path usage<ul>
	 *	<li><code> "c:/jdk1.2.2/jre/lib/rt.jar" </code> - reference to an external JAR</li>
	 *	<li><code> "/Project/someLib.jar" </code> - reference to an internal JAR </li>
	 *	<li><code> "c:/classes/" </code> - reference to an external binary folder</li>
	 * </ul>
	 * Note that this operation does not attempt to validate or access the 
	 * resources at the given paths.
	 * <p>
	 * The resulting entry is not exported to dependent projects. This method is equivalent to
	 * <code>newLibraryEntry(-,-,-,false)</code>.
	 * <p>
	 * 
	 * @param path the absolute path of the binary archive
	 * @param sourceAttachmentPath the absolute path of the corresponding source archive or folder, 
	 *    or <code>null</code> if none
	 * @param sourceAttachmentRootPath the location of the root within the source archive or folder
	 *    or <code>null</code> if this location should be automatically detected.
	 * @return a new library EGLPath entry
	 * 
	 * @see #newLibraryEntry(IPath, IPath, IPath, boolean)
	 */
	public static IEGLPathEntry newLibraryEntry(IPath path, IPath sourceAttachmentPath, IPath sourceAttachmentRootPath) {

		return newLibraryEntry(path, sourceAttachmentPath, sourceAttachmentRootPath, false);
	}

	/**
	 * Creates and returns a new EGLPath entry of kind <code>CPE_LIBRARY</code> for the JAR or folder
	 * identified by the given absolute path. This specifies that all package fragments within the root 
	 * will have children of type <code>IClassFile</code>.
	 * <p>
	 * A library entry is used to denote a prerequisite JAR or root folder containing binaries.
	 * The target JAR or folder can either be defined internally to the workspace (absolute path relative
	 * to the workspace root) or externally to the workspace (absolute path in the file system).
	 *	<p>
	 * e.g. Here are some examples of binary path usage<ul>
	 *	<li><code> "c:/jdk1.2.2/jre/lib/rt.jar" </code> - reference to an external JAR</li>
	 *	<li><code> "/Project/someLib.jar" </code> - reference to an internal JAR </li>
	 *	<li><code> "c:/classes/" </code> - reference to an external binary folder</li>
	 * </ul>
	 * Note that this operation does not attempt to validate or access the 
	 * resources at the given paths.
	 * <p>
	 * 
	 * @param path the absolute path of the binary archive
	 * @param sourceAttachmentPath the absolute path of the corresponding source archive or folder, 
	 *    or <code>null</code> if none
	 * @param sourceAttachmentRootPath the location of the root within the source archive or folder
	 *    or <code>null</code> if this location should be automatically detected.
	 * @param isExported indicates whether this entry is contributed to dependent
	 * 	  projects in addition to the output location
	 * @return a new library EGLPath entry
	 * @since 2.0
	 */
	public static IEGLPathEntry newLibraryEntry(
		IPath path,
		IPath sourceAttachmentPath,
		IPath sourceAttachmentRootPath,
		boolean isExported) {

		if (!path.isAbsolute())
			Assert.isTrue(false, "Path for IEGLPathEntry must be absolute"); //$NON-NLS-1$

		return new EGLPathEntry(
			IPackageFragmentRoot.K_BINARY,
			IEGLPathEntry.CPE_LIBRARY,
			EGLProject.canonicalizedPath(path),
			EGLPathEntry.EXCLUDE_NONE,
			sourceAttachmentPath,
			sourceAttachmentRootPath,
			null,
		// specific output folder
		isExported);
	}

	/**
	 * Creates and returns a new non-exported EGLPath entry of kind <code>CPE_PROJECT</code>
	 * for the project identified by the given absolute path.
	 * <p>
	 * A project entry is used to denote a prerequisite project on a EGLPath.
	 * The referenced project will be contributed as a whole, either as sources (in the EGL Model, it
	 * contributes all its package fragment roots) or as binaries (when building, it contributes its 
	 * whole output location).
	 * <p>
	 * A project reference allows to indirect through another project, independently from its internal layout. 
	 * <p>
	 * The prerequisite project is referred to using an absolute path relative to the workspace root.
	 * <p>
	 * The resulting entry is not exported to dependent projects. This method is equivalent to
	 * <code>newProjectEntry(_,false)</code>.
	 * <p>
	 * 
	 * @param path the absolute path of the binary archive
	 * @return a new project EGLPath entry
	 * 
	 * @see EGLCore#newProjectEntry(IPath, boolean)
	 */
	public static IEGLPathEntry newProjectEntry(IPath path) {
		return newProjectEntry(path, false);
	}

	/**
	 * Creates and returns a new EGLPath entry of kind <code>CPE_PROJECT</code>
	 * for the project identified by the given absolute path.
	 * <p>
	 * A project entry is used to denote a prerequisite project on a EGLPath.
	 * The referenced project will be contributed as a whole, either as sources (in the EGL Model, it
	 * contributes all its package fragment roots) or as binaries (when building, it contributes its 
	 * whole output location).
	 * <p>
	 * A project reference allows to indirect through another project, independently from its internal layout. 
	 * <p>
	 * The prerequisite project is referred to using an absolute path relative to the workspace root.
	 * <p>
	 * 
	 * @param path the absolute path of the prerequisite project
	 * @param isExported indicates whether this entry is contributed to dependent
	 * 	  projects in addition to the output location
	 * @return a new project EGLPath entry
	 * @since 2.0
	 */
	public static IEGLPathEntry newProjectEntry(IPath path, boolean isExported) {

		if (!path.isAbsolute())
			Assert.isTrue(false, "Path for IEGLPathEntry must be absolute"); //$NON-NLS-1$

		return new EGLPathEntry(
			IPackageFragmentRoot.K_SOURCE,
			IEGLPathEntry.CPE_PROJECT,
			path,
			EGLPathEntry.EXCLUDE_NONE,
			null,
		// source attachment
		null, // source attachment root
		null, // specific output folder
		isExported);
	}

	/**
	 * Returns a new empty region.
	 * 
	 * @return a new empty region
	 */
	public static IRegion newRegion() {
		return new Region();
	}

	/**
	 * Creates and returns a new EGLPath entry of kind <code>CPE_SOURCE</code>
	 * for the project's source folder identified by the given absolute 
	 * workspace-relative path. This specifies that all package fragments
	 * within the root will have children of type <code>ICompilationUnit</code>.
	 * <p>
	 * The source folder is referred to using an absolute path relative to the
	 * workspace root, e.g. <code>/Project/src</code>. A project's source 
	 * folders are located with that project. That is, a source EGLPath
	 * entry specifying the path <code>/P1/src</code> is only usable for
	 * project <code>P1</code>.
	 * </p>
	 * <p>
	 * The source EGLPath entry created by this method includes all source
	 * files below the given workspace-relative path. To selectively exclude
	 * some of these source files, use the factory method 
	 * <code>EGLCore.newSourceEntry(IPath,IPath[])</code> instead.
	 * </p>
	 * <p>
	 * Note that all sources/binaries inside a project are contributed as a whole through
	 * a project entry (see <code>EGLCore.newProjectEntry</code>). Particular
	 * source entries cannot be selectively exported.
	 * </p>
	 * 
	 * @param path the absolute workspace-relative path of a source folder
	 * @return a new source EGLPath entry with not exclusion patterns
	 * 
	 * @see #newSourceEntry(org.eclipse.core.runtime.IPath,org.eclipse.core.runtime.IPath[])
	 */
	public static IEGLPathEntry newSourceEntry(IPath path) {

		return newSourceEntry(path, EGLPathEntry.EXCLUDE_NONE, null /*output location*/
		);
	}

	/**
	 * Creates and returns a new EGLPath entry of kind <code>CPE_SOURCE</code>
	 * for the project's source folder identified by the given absolute 
	 * workspace-relative path but excluding all source files with paths
	 * matching any of the given patterns. This specifies that all package
	 * fragments within the root will have children of type 
	 * <code>ICompilationUnit</code>.
	 * <p>
	 * The source folder is referred to using an absolute path relative to the
	 * workspace root, e.g. <code>/Project/src</code>. A project's source 
	 * folders are located with that project. That is, a source EGLPath
	 * entry specifying the path <code>/P1/src</code> is only usable for
	 * project <code>P1</code>.
	 * </p>
	 * <p>
	 * The source EGLPath entry created by this method includes all source
	 * files below the given workspace-relative path except for those matched
	 * by one (or more) of the given exclusion patterns. Each exclusion pattern
	 * is represented by a relative path, which is interpreted as relative to
	 * the source folder. For example, if the source folder path is 
	 * <code>/Project/src</code> and the exclusion pattern is 
	 * <code>com/xyz/tests/&#42;&#42;</code>, then source files
	 * like <code>/Project/src/com/xyz/Foo.egl</code>
	 * and <code>/Project/src/com/xyz/utils/Bar.egl</code> would be included,
	 * whereas <code>/Project/src/com/xyz/tests/T1.egl</code>
	 * and <code>/Project/src/com/xyz/tests/quick/T2.egl</code> would be
	 * excluded. Exclusion patterns can contain can contain '**', '*' or '?'
	 * wildcards; see <code>IEGLPathEntry.getExclusionPatterns</code>
	 * for the full description of the syntax and semantics of exclusion
	 * patterns.
	 * </p>
	 * If the empty list of exclusion patterns is specified, the source folder
	 * will automatically include all resources located inside the source
	 * folder. In that case, the result is entirely equivalent to using the
	 * factory method <code>EGLCore.newSourceEntry(IPath)</code>. 
	 * </p>
	 * <p>
	 * Note that all sources/binaries inside a project are contributed as a whole through
	 * a project entry (see <code>EGLCore.newProjectEntry</code>). Particular
	 * source entries cannot be selectively exported.
	 * </p>
	 *
	 * @param path the absolute workspace-relative path of a source folder
	 * @param exclusionPatterns the possibly empty list of exclusion patterns
	 *    represented as relative paths
	 * @return a new source EGLPath entry with the given exclusion patterns
	 * @see #newSourceEntry(org.eclipse.core.runtime.IPath)
	 * @see IEGLPathEntry#getExclusionPatterns
	 * 
	 * @since 2.1
	 */
	public static IEGLPathEntry newSourceEntry(IPath path, IPath[] exclusionPatterns) {

		return newSourceEntry(path, exclusionPatterns, null /*output location*/
		);
	}

	/**
	 * Creates and returns a new EGLPath entry of kind <code>CPE_SOURCE</code>
	 * for the project's source folder identified by the given absolute 
	 * workspace-relative path but excluding all source files with paths
	 * matching any of the given patterns, and associated with a specific output location
	 * (that is, ".class" files are not going to the project default output location). 
	 * All package fragments within the root will have children of type 
	 * <code>ICompilationUnit</code>.
	 * <p>
	 * The source folder is referred to using an absolute path relative to the
	 * workspace root, e.g. <code>/Project/src</code>. A project's source 
	 * folders are located with that project. That is, a source EGLPath
	 * entry specifying the path <code>/P1/src</code> is only usable for
	 * project <code>P1</code>.
	 * </p>
	 * <p>
	 * The source EGLPath entry created by this method includes all source
	 * files below the given workspace-relative path except for those matched
	 * by one (or more) of the given exclusion patterns. Each exclusion pattern
	 * is represented by a relative path, which is interpreted as relative to
	 * the source folder. For example, if the source folder path is 
	 * <code>/Project/src</code> and the exclusion pattern is 
	 * <code>com/xyz/tests/&#42;&#42;</code>, then source files
	 * like <code>/Project/src/com/xyz/Foo.egl</code>
	 * and <code>/Project/src/com/xyz/utils/Bar.egl</code> would be included,
	 * whereas <code>/Project/src/com/xyz/tests/T1.egl</code>
	 * and <code>/Project/src/com/xyz/tests/quick/T2.egl</code> would be
	 * excluded. Exclusion patterns can contain can contain '**', '*' or '?'
	 * wildcards; see <code>IEGLPathEntry.getExclusionPatterns</code>
	 * for the full description of the syntax and semantics of exclusion
	 * patterns.
	 * </p>
	 * If the empty list of exclusion patterns is specified, the source folder
	 * will automatically include all resources located inside the source
	 * folder. In that case, the result is entirely equivalent to using the
	 * factory method <code>EGLCore.newSourceEntry(IPath)</code>. 
	 * </p>
	 * <p>
	 * Additionally, a source entry can be associated with a specific output location. 
	 * By doing so, the EGL builder will ensure that the generated ".class" files will 
	 * be issued inside this output location, as opposed to be generated into the 
	 * project default output location (when output location is <code>null</code>). 
	 * Note that multiple source entries may target the same output location.
	 * The output location is referred to using an absolute path relative to the 
	 * workspace root, e.g. <code>"/Project/bin"</code>, it must be located inside 
	 * the same project as the source folder.
	 * </p>
	 * <p>
	 * Also note that all sources/binaries inside a project are contributed as a whole through
	 * a project entry (see <code>EGLCore.newProjectEntry</code>). Particular
	 * source entries cannot be selectively exported.
	 * </p>
	 *
	 * @param path the absolute workspace-relative path of a source folder
	 * @param exclusionPatterns the possibly empty list of exclusion patterns
	 *    represented as relative paths
	 * @param outputLocation the specific output location for this source entry (<code>null</code> if using project default ouput location)
	 * @return a new source EGLPath entry with the given exclusion patterns
	 * @see #newSourceEntry(org.eclipse.core.runtime.IPath)
	 * @see IEGLPathEntry#getExclusionPatterns
	 * @see IEGLPathEntry#getOutputLocation()
	 * 
	 * @since 2.1
	 */
	public static IEGLPathEntry newSourceEntry(IPath path, IPath[] exclusionPatterns, IPath specificOutputLocation) {

		if (!path.isAbsolute())
			Assert.isTrue(false, "Path for IEGLPathEntry must be absolute"); //$NON-NLS-1$
		if (exclusionPatterns == null)
			Assert.isTrue(false, "Exclusion pattern set cannot be null"); //$NON-NLS-1$

		return new EGLPathEntry(
			IPackageFragmentRoot.K_SOURCE,
			IEGLPathEntry.CPE_SOURCE,
			path,
			exclusionPatterns,
			null,
		// source attachment
		null, // source attachment root
		specificOutputLocation, // custom output location
		false);
	}

	/**
	 * Creates and returns a new non-exported EGLPath entry of kind <code>CPE_VARIABLE</code>
	 * for the given path. The first segment of the path is the name of a EGLPath variable.
	 * The trailing segments of the path will be appended to resolved variable path.
	 * <p>
	 * A variable entry allows to express indirect references on a EGLPath to other projects or libraries,
	 * depending on what the EGLPath variable is referring.
	 * <p>
	 *	It is possible to register an automatic initializer (<code>EGLPathVariableInitializer</code>),
	 * which will be invoked through the extension point "com.ibm.etools.egl.internal.model.core.EGLPathVariableInitializer".
	 * After resolution, a EGLPath variable entry may either correspond to a project or a library entry. </li>	 
	 * <p>
	 * e.g. Here are some examples of variable path usage<ul>
	 * <li> "JDTCORE" where variable <code>JDTCORE</code> is 
	 *		bound to "c:/jars/jdtcore.jar". The resolved EGLPath entry is denoting the library "c:\jars\jdtcore.jar"</li>
	 * <li> "JDTCORE" where variable <code>JDTCORE</code> is 
	 *		bound to "/Project_JDTCORE". The resolved EGLPath entry is denoting the project "/Project_JDTCORE"</li>
	 * <li> "PLUGINS/com.example/example.jar" where variable <code>PLUGINS</code>
	 *      is bound to "c:/eclipse/plugins". The resolved EGLPath entry is denoting the library "c:/eclipse/plugins/com.example/example.jar"</li>
	 * </ul>
	 * Note that this operation does not attempt to validate EGLPath variables
	 * or access the resources at the given paths.
	 * <p>
	 * The resulting entry is not exported to dependent projects. This method is equivalent to
	 * <code>newVariableEntry(-,-,-,false)</code>.
	 * <p>
	 * 
	 * @param variablePath the path of the binary archive; first segment is the
	 *   name of a EGLPath variable
	 * @param variableSourceAttachmentPath the path of the corresponding source archive, 
	 *    or <code>null</code> if none; if present, the first segment is the
	 *    name of a EGLPath variable (not necessarily the same variable
	 *    as the one that begins <code>variablePath</code>)
	 * @param sourceAttachmentRootPath the location of the root within the source archive
	 *    or <code>null</code> if <code>archivePath</code> is also <code>null</code>
	 * @return a new library EGLPath entry
	 * 
	 * @see EGLCore#newVariableEntry(IPath, IPath, IPath, boolean)
	 */
	public static IEGLPathEntry newVariableEntry(IPath variablePath, IPath variableSourceAttachmentPath, IPath sourceAttachmentRootPath) {

		return newVariableEntry(variablePath, variableSourceAttachmentPath, sourceAttachmentRootPath, false);
	}

	/**
	 * Creates and returns a new non-exported EGLPath entry of kind <code>CPE_VARIABLE</code>
	 * for the given path. The first segment of the path is the name of a EGLPath variable.
	 * The trailing segments of the path will be appended to resolved variable path.
	 * <p>
	 * A variable entry allows to express indirect references on a EGLPath to other projects or libraries,
	 * depending on what the EGLPath variable is referring.
	 * <p>
	 *	It is possible to register an automatic initializer (<code>EGLPathVariableInitializer</code>),
	 * which will be invoked through the extension point "com.ibm.etools.egl.internal.model.core.EGLPathVariableInitializer".
	 * After resolution, a EGLPath variable entry may either correspond to a project or a library entry. </li>	 
	 * <p>
	 * e.g. Here are some examples of variable path usage<ul>
	 * <li> "JDTCORE" where variable <code>JDTCORE</code> is 
	 *		bound to "c:/jars/jdtcore.jar". The resolved EGLPath entry is denoting the library "c:\jars\jdtcore.jar"</li>
	 * <li> "JDTCORE" where variable <code>JDTCORE</code> is 
	 *		bound to "/Project_JDTCORE". The resolved EGLPath entry is denoting the project "/Project_JDTCORE"</li>
	 * <li> "PLUGINS/com.example/example.jar" where variable <code>PLUGINS</code>
	 *      is bound to "c:/eclipse/plugins". The resolved EGLPath entry is denoting the library "c:/eclipse/plugins/com.example/example.jar"</li>
	 * </ul>
	 * Note that this operation does not attempt to validate EGLPath variables
	 * or access the resources at the given paths.
	 * <p>
	 *
	 * @param variablePath the path of the binary archive; first segment is the
	 *   name of a EGLPath variable
	 * @param variableSourceAttachmentPath the path of the corresponding source archive, 
	 *    or <code>null</code> if none; if present, the first segment is the
	 *    name of a EGLPath variable (not necessarily the same variable
	 *    as the one that begins <code>variablePath</code>)
	 * @param sourceAttachmentRootPath the location of the root within the source archive
	 *    or <code>null</code> if <code>archivePath</code> is also <code>null</code>
	 * @param isExported indicates whether this entry is contributed to dependent
	 * 	  projects in addition to the output location
	 * @return a new variable EGLPath entry
	 * @since 2.0
	 */
	public static IEGLPathEntry newVariableEntry(
		IPath variablePath,
		IPath variableSourceAttachmentPath,
		IPath variableSourceAttachmentRootPath,
		boolean isExported) {

		if (variablePath == null || variablePath.segmentCount() < 1) {
			Assert.isTrue(false, "Illegal EGLPath variable path: \'" + variablePath.makeRelative().toString() + "\', must have at least one segment"); //$NON-NLS-1$//$NON-NLS-2$
		}

		return new EGLPathEntry(
			IPackageFragmentRoot.K_SOURCE,
			IEGLPathEntry.CPE_VARIABLE,
			variablePath,
			EGLPathEntry.EXCLUDE_NONE,
			variableSourceAttachmentPath,
		// source attachment
		variableSourceAttachmentRootPath, // source attachment root			
		null, // specific output folder
		isExported);
	}

	/**
	 * Removed the given EGLPath variable. Does nothing if no value was
	 * set for this EGLPath variable.
	 * <p>
	 * This functionality cannot be used while the resource tree is locked.
	 * <p>
	 * EGLPath variable values are persisted locally to the workspace, and 
	 * are preserved from session to session.
	 * <p>
	 *
	 * @param variableName the name of the EGLPath variable
	 * @see #setEGLPathVariable
	 *
	 * @deprecated - use version with extra IProgressMonitor
	 */
	public static void removeEGLPathVariable(String variableName) {
		removeEGLPathVariable(variableName, null);
	}

	/**
	 * Removed the given EGLPath variable. Does nothing if no value was
	 * set for this EGLPath variable.
	 * <p>
	 * This functionality cannot be used while the resource tree is locked.
	 * <p>
	 * EGLPath variable values are persisted locally to the workspace, and 
	 * are preserved from session to session.
	 * <p>
	 *
	 * @param variableName the name of the EGLPath variable
	 * @param monitor the progress monitor to report progress
	 * @see #setEGLPathVariable
	 */
	public static void removeEGLPathVariable(String variableName, IProgressMonitor monitor) {

		try {
			updateVariableValues(new String[] { variableName }, new IPath[] { null }, monitor);
		} catch (EGLModelException e) {
		}
	}

	/**
	 * Removes the given element changed listener.
	 * Has no affect if an identical listener is not registered.
	 *
	 * @param listener the listener
	 */
	public static void removeElementChangedListener(IElementChangedListener listener) {
		EGLModelManager.getEGLModelManager().removeElementChangedListener(listener);
	}
	/**
	 * Runs the given action as an atomic EGL model operation.
	 * <p>
	 * After running a method that modifies EGL elements,
	 * registered listeners receive after-the-fact notification of
	 * what just transpired, in the form of a element changed event.
	 * This method allows clients to call a number of
	 * methods that modify egl elements and only have element
	 * changed event notifications reported at the end of the entire
	 * batch.
	 * </p>
	 * <p>
	 * If this method is called outside the dynamic scope of another such
	 * call, this method runs the action and then reports a single
	 * element changed event describing the net effect of all changes
	 * done to egl elements by the action.
	 * </p>
	 * <p>
	 * If this method is called in the dynamic scope of another such
	 * call, this method simply runs the action.
	 * </p>
	 *
	 * @param action the action to perform
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @exception CoreException if the operation failed.
	 * @since 2.1
	 */
	public static void run(IWorkspaceRunnable action, IProgressMonitor monitor) throws CoreException {
		run(action, ResourcesPlugin.getWorkspace().getRoot(), monitor);		
	}
	
	public static void run(IWorkspaceRunnable action, ISchedulingRule rule, IProgressMonitor monitor) throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (workspace.isTreeLocked()) {
			new BatchOperation(action).run(monitor);
		} else {
			// use IWorkspace.run(...) to ensure that a build will be done in autobuild mode
			workspace.run(new BatchOperation(action), rule, IWorkspace.AVOID_UPDATE, monitor);
		}
	}
	
	/** 
	 * Bind a container reference path to some actual containers (<code>IEGLPathContainer</code>).
	 * This API must be invoked whenever changes in container need to be reflected onto the EGLModel.
	 * Containers can have distinct values in different projects, therefore this API considers a
	 * set of projects with their respective containers.
	 * <p>
	 * <code>containerPath</code> is the path under which these values can be referenced through
	 * container EGLPath entries (<code>IEGLPathEntry#CPE_CONTAINER</code>). A container path 
	 * is formed by a first ID segment followed with extra segments, which can be used as additional hints
	 * for the resolution. The container ID is used to identify a <code>EGLPathContainerInitializer</code> 
	 * registered on the extension point "com.ibm.etools.egl.internal.model.core.EGLPathContainerInitializer".
	 * <p>
	 * There is no assumption that each individual container value passed in argument 
	 * (<code>respectiveContainers</code>) must answer the exact same path when requested 
	 * <code>IEGLPathContainer#getPath</code>. 
	 * Indeed, the containerPath is just an indication for resolving it to an actual container object. It can be 
	 * delegated to a <code>EGLPathContainerInitializer</code>, which can be activated through the extension
	 * point "com.ibm.etools.egl.internal.model.core.EGLPathContainerInitializer"). 
	 * <p>
	 * In reaction to changing container values, the EGLModel will be updated to reflect the new
	 * state of the updated container. 
	 * <p>
	 * This functionality cannot be used while the resource tree is locked.
	 * <p>
	 * EGLPath container values are persisted locally to the workspace, but 
	 * are not preserved from a session to another. It is thus highly recommended to register a 
	 * <code>EGLPathContainerInitializer</code> for each referenced container 
	 * (through the extension point "com.ibm.etools.egl.internal.model.core.EGLPathContainerInitializer").
	 * <p>
	 * Note: setting a container to <code>null</code> will cause it to be lazily resolved again whenever
	 * its value is required. In particular, this will cause a registered initializer to be invoked
	 * again.
	 * <p>
	 * @param containerPath - the name of the container reference, which is being updated
	 * @param affectedProjects - the set of projects for which this container is being bound
	 * @param respectiveContainers - the set of respective containers for the affected projects
	 * @param monitor a monitor to report progress
	 * 
	 * @see EGLPathContainerInitializer
	 * @see #getEGLPathContainer(IPath, IEGLProject)
	 * @see IEGLPathContainer
	 * @since 2.0
	 */
	public static void setEGLPathContainer(
		final IPath containerPath,
		IEGLProject[] affectedProjects,
		IEGLPathContainer[] respectiveContainers,
		IProgressMonitor monitor)
		throws EGLModelException {

		if (affectedProjects.length != respectiveContainers.length)
			Assert.isTrue(false, "Projects and containers collections should have the same size"); //$NON-NLS-1$

		if (monitor != null && monitor.isCanceled())
			return;

		if (EGLModelManager.CP_RESOLVE_VERBOSE) {
			System.out.println("CPContainer SET  - setting container: [" + containerPath + "] for projects: {" //$NON-NLS-1$ //$NON-NLS-2$
			+ (Util.toString(affectedProjects, new Util.Displayable() {
				public String displayString(Object o) {
					return ((IEGLProject) o).getElementName();
				}
			})) + "} with values: " //$NON-NLS-1$
			+ (Util.toString(respectiveContainers, new Util.Displayable() {
				public String displayString(Object o) {
					return ((IEGLPathContainer) o).getDescription();
				}
			})));
		}

		final int projectLength = affectedProjects.length;
		final IEGLProject[] modifiedProjects;
		System.arraycopy(affectedProjects, 0, modifiedProjects = new IEGLProject[projectLength], 0, projectLength);
		final IEGLPathEntry[][] oldResolvedPaths = new IEGLPathEntry[projectLength][];

		// filter out unmodified project containers
		int remaining = 0;
		for (int i = 0; i < projectLength; i++) {

			if (monitor != null && monitor.isCanceled())
				return;

			IEGLProject affectedProject = affectedProjects[i];
			IEGLPathContainer newContainer = respectiveContainers[i];
			if (newContainer == null)
				newContainer = EGLModelManager.ContainerInitializationInProgress; // 30920 - prevent infinite loop
			boolean found = false;
			if (EGLProject.hasEGLNature(affectedProject.getProject())) {
				IEGLPathEntry[] rawEGLPath = affectedProject.getRawEGLPath();
				for (int j = 0, cpLength = rawEGLPath.length; j < cpLength; j++) {
					IEGLPathEntry entry = rawEGLPath[j];
					if (entry.getEntryKind() == IEGLPathEntry.CPE_CONTAINER && entry.getPath().equals(containerPath)) {
						found = true;
						break;
					}
				}
			}
			if (!found) {
				modifiedProjects[i] = null; // filter out this project - does not reference the container path, or isnt't yet EGL project
				EGLModelManager.containerPut(affectedProject, containerPath, newContainer);
				continue;
			}
			IEGLPathContainer oldContainer = EGLModelManager.containerGet(affectedProject, containerPath);
			if (oldContainer == EGLModelManager.ContainerInitializationInProgress) {
				Map previousContainerValues = (Map) EGLModelManager.PreviousSessionContainers.get(affectedProject);
				if (previousContainerValues != null) {
					IEGLPathContainer previousContainer = (IEGLPathContainer) previousContainerValues.get(containerPath);
					if (previousContainer != null) {
						if (EGLModelManager.CP_RESOLVE_VERBOSE) {
							System.out.println("CPContainer INIT - reentering access to project container: [" + affectedProject.getElementName() + "] " + containerPath + " during its initialization, will see previous value: " + previousContainer.getDescription()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						}
						EGLModelManager.containerPut(affectedProject, containerPath, previousContainer);
					}
					oldContainer = null; //33695 - cannot filter out restored container, must update affected project to reset cached CP
				} else {
					oldContainer = null;
				}
			}
			if (oldContainer != null && oldContainer.equals(respectiveContainers[i])) { // TODO: could improve to only compare entries
				modifiedProjects[i] = null; // filter out this project - container did not change
				continue;
			}
			remaining++;
			oldResolvedPaths[i] = affectedProject.getResolvedEGLPath(true);
			EGLModelManager.containerPut(affectedProject, containerPath, newContainer);
		}

		if (remaining == 0)
			return;

		// trigger model refresh
		try {
			EGLCore.run(new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					for (int i = 0; i < projectLength; i++) {

						if (monitor != null && monitor.isCanceled())
							return;

						EGLProject affectedProject = (EGLProject) modifiedProjects[i];
						if (affectedProject == null)
							continue; // was filtered out

						if (EGLModelManager.CP_RESOLVE_VERBOSE) {
							System.out.println("CPContainer SET  - updating affected project: [" + affectedProject.getElementName() + "] due to setting container: " + containerPath); //$NON-NLS-1$ //$NON-NLS-2$
						}

						// force a refresh of the affected project (will compute deltas)
						affectedProject
							.setRawEGLPath(
								affectedProject.getRawEGLPath(),
								SetEGLPathOperation.ReuseOutputLocation,
								monitor,
								!ResourcesPlugin.getWorkspace().isTreeLocked(),
						// can save resources
						oldResolvedPaths[i], false, // updating - no validation
						false); // updating - no need to save
					}
				}
			}, monitor);
		} catch (CoreException e) {
			if (EGLModelManager.CP_RESOLVE_VERBOSE) {
				System.out.println("CPContainer SET  - FAILED DUE TO EXCEPTION: " + containerPath); //$NON-NLS-1$
				e.printStackTrace();
			}
			if (e instanceof EGLModelException) {
				throw (EGLModelException) e;
			} else {
				throw new EGLModelException(e);
			}
		} finally {
			for (int i = 0; i < projectLength; i++) {
				if (respectiveContainers[i] == null) {
					EGLModelManager.containerPut(affectedProjects[i], containerPath, null); // reset init in progress marker
				}
			}
		}

	}

	/**
	 * Sets the value of the given EGLPath variable.
	 * The path must have at least one segment.
	 * <p>
	 * This functionality cannot be used while the resource tree is locked.
	 * <p>
	 * EGLPath variable values are persisted locally to the workspace, and 
	 * are preserved from session to session.
	 * <p>
	 *
	 * @param variableName the name of the EGLPath variable
	 * @param path the path
	 * @see #getEGLPathVariable
	 *
	 * @deprecated - use API with IProgressMonitor
	 */
	public static void setEGLPathVariable(String variableName, IPath path) throws EGLModelException {

		setEGLPathVariable(variableName, path, null);
	}

	/**
	 * Sets the value of the given EGLPath variable.
	 * The path must not be null.
	 * <p>
	 * This functionality cannot be used while the resource tree is locked.
	 * <p>
	 * EGLPath variable values are persisted locally to the workspace, and 
	 * are preserved from session to session.
	 * <p>
	 * Updating a variable with the same value has no effect.
	 *
	 * @param variableName the name of the EGLPath variable
	 * @param path the path
	 * @param monitor a monitor to report progress
	 * @see #getEGLPathVariable
	 */
	public static void setEGLPathVariable(String variableName, IPath path, IProgressMonitor monitor) throws EGLModelException {

		if (path == null)
			Assert.isTrue(false, "Variable path cannot be null"); //$NON-NLS-1$
		setEGLPathVariables(new String[] { variableName }, new IPath[] { path }, monitor);
	}

	/**
	 * Sets the values of all the given EGLPath variables at once.
	 * Null paths can be used to request corresponding variable removal.
	 * <p>
	 * This functionality cannot be used while the resource tree is locked.
	 * <p>
	 * EGLPath variable values are persisted locally to the workspace, and 
	 * are preserved from session to session.
	 * <p>
	 * Updating a variable with the same value has no effect.
	 * 
	 * @param variableNames an array of names for the updated EGLPath variables
	 * @param paths an array of path updates for the modified EGLPath variables (null
	 *       meaning that the corresponding value will be removed
	 * @param monitor a monitor to report progress
	 * @see #getEGLPathVariable
	 * @since 2.0
	 */
	public static void setEGLPathVariables(String[] variableNames, IPath[] paths, IProgressMonitor monitor) throws EGLModelException {

		if (variableNames.length != paths.length)
			Assert.isTrue(false, "Variable names and paths collections should have the same size"); //$NON-NLS-1$
		//TODO: should check that null cannot be used as variable paths
		updateVariableValues(variableNames, paths, monitor);
	}

	/**
	 * Sets the current table of options. All and only the options explicitly included in the given table 
	 * are remembered; all previous option settings are forgotten, including ones not explicitly
	 * mentioned.
	 * <p>
	 * For a complete description of the configurable options, see <code>getDefaultOptions</code>.
	 * </p>
	 * 
	 * @param newOptions the new options (key type: <code>String</code>; value type: <code>String</code>),
	 *   or <code>null</code> to reset all options to their default values
	 * @see EGLCore#getDefaultOptions
	 */
	public static void setOptions(Hashtable newOptions) {

		// see #initializeDefaultPluginPreferences() for changing default settings
		Preferences preferences = getPlugin().getPluginPreferences();

		if (newOptions == null) {
			newOptions = EGLCore.getDefaultOptions();
		}
		Enumeration keys = newOptions.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			if (!EGLModelManager.OptionNames.contains(key))
				continue; // unrecognized option
			if (key.equals(CORE_ENCODING))
				continue; // skipped, contributed by resource prefs
			String value = (String) newOptions.get(key);
			preferences.setValue(key, value);
		}

		// persist options
		getPlugin().savePluginPreferences();
	}

	/**
	 * Internal updating of a variable values (null path meaning removal), allowing to change multiple variable values at once.
	 */
	private static void updateVariableValues(String[] variableNames, IPath[] variablePaths, IProgressMonitor monitor)
		throws EGLModelException {

		if (monitor != null && monitor.isCanceled())
			return;

		if (EGLModelManager.CP_RESOLVE_VERBOSE) {
			System.out.println("CPVariable SET  - setting variables: {" + Util.toString(variableNames) //$NON-NLS-1$
			+"} with values: " + Util.toString(variablePaths)); //$NON-NLS-1$
		}

		int varLength = variableNames.length;

		// gather EGLPath information for updating
		final HashMap affectedProjects = new HashMap(5);
		EGLModelManager manager = EGLModelManager.getEGLModelManager();
		IEGLModel model = manager.getEGLModel();

		// filter out unmodified variables
		int discardCount = 0;
		for (int i = 0; i < varLength; i++) {
			String variableName = variableNames[i];
			IPath oldPath = (IPath) EGLModelManager.variableGet(variableName); // if reentering will provide previous session value 
			if (oldPath == EGLModelManager.VariableInitializationInProgress) {
				IPath previousPath = (IPath) EGLModelManager.PreviousSessionVariables.get(variableName);
				if (previousPath != null) {
					if (EGLModelManager.CP_RESOLVE_VERBOSE) {
						System.out.println("CPVariable INIT - reentering access to variable: " + variableName + " during its initialization, will see previous value: " + previousPath); //$NON-NLS-1$ //$NON-NLS-2$
					}
					EGLModelManager.variablePut(variableName, previousPath); // replace value so reentering calls are seeing old value
				}
				oldPath = null; //33695 - cannot filter out restored variable, must update affected project to reset cached CP
			}
			if (oldPath != null && oldPath.equals(variablePaths[i])) {
				variableNames[i] = null;
				discardCount++;
			}
		}
		if (discardCount > 0) {
			if (discardCount == varLength)
				return;
			int changedLength = varLength - discardCount;
			String[] changedVariableNames = new String[changedLength];
			IPath[] changedVariablePaths = new IPath[changedLength];
			for (int i = 0, index = 0; i < varLength; i++) {
				if (variableNames[i] != null) {
					changedVariableNames[index] = variableNames[i];
					changedVariablePaths[index] = variablePaths[i];
					index++;
				}
			}
			variableNames = changedVariableNames;
			variablePaths = changedVariablePaths;
			varLength = changedLength;
		}

		if (monitor != null && monitor.isCanceled())
			return;

		if (model != null) {
			IEGLProject[] projects = model.getEGLProjects();
			nextProject : for (int i = 0, projectLength = projects.length; i < projectLength; i++) {
				IEGLProject project = projects[i];

				// check to see if any of the modified variables is present on the EGLPath
				IEGLPathEntry[] EGLPath = project.getRawEGLPath();
				for (int j = 0, cpLength = EGLPath.length; j < cpLength; j++) {

					IEGLPathEntry entry = EGLPath[j];
					for (int k = 0; k < varLength; k++) {

						String variableName = variableNames[k];
						if (entry.getEntryKind() == IEGLPathEntry.CPE_VARIABLE) {

							if (variableName.equals(entry.getPath().segment(0))) {
								affectedProjects.put(project, project.getResolvedEGLPath(true));
								continue nextProject;
							}
							IPath sourcePath, sourceRootPath;
							if (((sourcePath = entry.getSourceAttachmentPath()) != null && variableName.equals(sourcePath.segment(0)))
								|| ((sourceRootPath = entry.getSourceAttachmentRootPath()) != null
									&& variableName.equals(sourceRootPath.segment(0)))) {

								affectedProjects.put(project, project.getResolvedEGLPath(true));
								continue nextProject;
							}
						}
					}
				}
			}
		}
		// update variables
		for (int i = 0; i < varLength; i++) {
			EGLModelManager.variablePut(variableNames[i], variablePaths[i]);
		}
		final String[] dbgVariableNames = variableNames;

		// update affected project EGLPaths
		if (!affectedProjects.isEmpty()) {
			try {
				EGLCore.run(new IWorkspaceRunnable() {
					public void run(IProgressMonitor monitor) throws CoreException {
						// propagate EGLPath change
						Iterator projectsToUpdate = affectedProjects.keySet().iterator();
						while (projectsToUpdate.hasNext()) {

							if (monitor != null && monitor.isCanceled())
								return;

							EGLProject project = (EGLProject) projectsToUpdate.next();

							if (EGLModelManager.CP_RESOLVE_VERBOSE) {
								System.out.println("CPVariable SET  - updating affected project: [" + project.getElementName() + "] due to setting variables: " + Util.toString(dbgVariableNames)); //$NON-NLS-1$ //$NON-NLS-2$
							}

							project.setRawEGLPath(project.getRawEGLPath(), SetEGLPathOperation.ReuseOutputLocation, null,
							// don't call beginTask on the monitor (see http://bugs.eclipse.org/bugs/show_bug.cgi?id=3717)
							!ResourcesPlugin.getWorkspace().isTreeLocked(), // can change resources
							 (IEGLPathEntry[]) affectedProjects.get(project), false, // updating - no validation
							false); // updating - no need to save
						}
					}
				}, monitor);
			} catch (CoreException e) {
				if (EGLModelManager.CP_RESOLVE_VERBOSE) {
					System.out.println("CPVariable SET  - FAILED DUE TO EXCEPTION: " + Util.toString(dbgVariableNames)); //$NON-NLS-1$
					e.printStackTrace();
				}
				if (e instanceof EGLModelException) {
					throw (EGLModelException) e;
				} else {
					throw new EGLModelException(e);
				}
			}
		}
	}
}
