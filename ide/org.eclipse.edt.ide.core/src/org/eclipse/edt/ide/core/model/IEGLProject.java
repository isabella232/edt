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
package org.eclipse.edt.ide.core.model;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
// TODO Define exactly what an EGL Project has
/**
 * A EGL project represents a view of a project resource in terms of EGL 
 * elements such as package fragments, types, methods and fields.
 * A project may contain several package roots, which contain package fragments. 
 * A package root corresponds to an underlying folder or JAR.
 * <p>
 * Each EGL project has a EGLPath, defining which folders contain source code and
 * where required libraries are located. Each EGL project also has an output location,
 * defining where the builder writes <code>.class</code> files. A project that
 * references packages in another project can access the packages by including
 * the required project in a EGLPath entry. The EGL model will present the
 * source elements in the required project; when building, the compiler will use
 * the corresponding generated class files from the required project's output
 * location(s)). The EGLPath format is a sequence of EGLPath entries
 * describing the location and contents of package fragment roots.
 * </p>
 * EGL project elements need to be opened before they can be navigated or manipulated.
 * The children of a EGL project are the package fragment roots that are 
 * defined by the EGLPath and contained in this project (in other words, it
 * does not include package fragment roots for other projects).
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients. An instance
 * of one of these handles can be created via 
 * <code>EGLCore.create(project)</code>.
 * </p>
 *
 * @see EGLCore#create(org.eclipse.core.resources.IProject)
 * @see IEGLPathEntry
 */
public interface IEGLProject extends IParent, IEGLElement, IOpenable {

	/**
	 * Returns the first existing package fragment on this project's eglpath
	 * whose path matches the given (absolute) path, or <code>null</code> if none
	 * exist.
	 * The path can be:
	 * 	- internal to the workbench: "/Project/src"
	 *  - external to the workbench: "c:/jdk/classes.zip/java/lang"
	 * @param path the given absolute path
	 * @exception EGLModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource
	 * @return the first existing package fragment on this project's eglpath
	 * whose path matches the given (absolute) path, or <code>null</code> if none
	 * exist
	 */
	IPackageFragment findPackageFragment(IPath path) throws EGLModelException;

	/**
	 * Returns the existing package fragment root on this project's eglpath
	 * whose path matches the given (absolute) path, or <code>null</code> if
	 * one does not exist.
	 * The path can be:
	 *	- internal to the workbench: "/Compiler/src"
	 *	- external to the workbench: "c:/jdk/classes.zip"
	 * @param path the given absolute path
	 * @exception EGLModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource
	 * @return the existing package fragment root on this project's eglpath
	 * whose path matches the given (absolute) path, or <code>null</code> if
	 * one does not exist
	 */
	IPackageFragmentRoot findPackageFragmentRoot(IPath path)
		throws EGLModelException;
	/**
	 * Returns the existing package fragment roots identified by the given entry.
	 * Note that a eglpath entry that refers to another project may
	 * have more than one root (if that project has more than on root
	 * containing source), and eglpath entries within the current
	 * project identify a single root.
	 * <p>
	 * If the eglpath entry denotes a variable, it will be resolved and return
	 * the roots of the target entry (empty if not resolvable).
	 * <p>
	 * If the eglpath entry denotes a container, it will be resolved and return
	 * the roots corresponding to the set of container entries (empty if not resolvable).
	 * 
	 * @param entry the given entry
	 * @return the existing package fragment roots identified by the given entry
	 * @see IEGLPathContainer
	 * @since 2.1
	 */
	IPackageFragmentRoot[] findPackageFragmentRoots(IEGLPathEntry entry);
	/**
	 * Returns the <code>IEGLElement</code> corresponding to the given
	 * EGLPath-relative path, or <code>null</code> if no such 
	 * <code>IEGLElement</code> is found. The result is one of an
	 * <code>IEGLFile</code>, <code>IPackageFragment</code>.
	 * <p>
	 * When looking for a package fragment, there might be several potential
	 * matches; only one of them is returned.
	 *
	 * <p>For example, the path "egl/lang/DataUtils.EGL", would result in the
	 * <code>IEGLFile</code>. The path "EGL/lang" would result in the
	 * <code>IPackageFragment</code> for "EGL.lang".
	 * @param path the given EGLPath-relative path
	 * @exception EGLModelException if the given path is <code>null</code>
	 *  or absolute
	 * @return the <code>IEGLElement</code> corresponding to the given
	 * EGLPath-relative path, or <code>null</code> if no such 
	 * <code>IEGLElement</code> is found
	 */
	IEGLElement findElement(IPath path) throws EGLModelException;

	/**
	 * Returns the existing package fragment root on this project's EGLPath
	 * whose path matches the given (absolute) path, or <code>null</code> if
	 * one does not exist.
	 * The path can be:
	 *	- internal to the workbench: "/Compiler/src"
	 *	- external to the workbench: "c:/sys001/Program1.egl"
	 * @param path the given absolute path
	 * @exception EGLModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource
	 * @return the existing package fragment root on this project's EGLPath
	 * whose path matches the given (absolute) path, or <code>null</code> if
	 * one does not exist
	 */
	IPackageFragmentRoot getPackageFragmentRoot(IPath path)
		throws EGLModelException;
	/**
	 * Returns the first type found following this project's classpath 
	 * with the given fully qualified name or <code>null</code> if none is found.
	 * The fully qualified name is a dot-separated name. For example,
	 * a class B defined as a member type of a class A in package x.y should have a 
	 * the fully qualified name "x.y.A.B".
	 * 
	 * Note that in order to be found, a type name (or its toplevel enclosing
	 * type name) must match its corresponding compilation unit name. As a 
	 * consequence, secondary types cannot be found using this functionality.
	 * Secondary types can however be explicitely accessed through their enclosing
	 * unit or found by the <code>SearchEngine</code>.
	 * 
	 * This method only searches the current project (source folders and archives).
	 *  
	 * @param fullyQualifiedName the given fully qualified name
	 * @exception EGLModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource
	 * @return the first type found following this project's classpath 
	 * with the given fully qualified name or <code>null</code> if none is found
	 * @see IPart#getFullyQualifiedName(char)
	 * @since 2.0
	 */
	IPart findPart(String fullyQualifiedName) throws EGLModelException;
	/**
	 * Returns the first type found following this project's classpath 
	 * with the given package name and type qualified name
	 * or <code>null</code> if none is found.
	 * The package name is a dot-separated name.
	 * The type qualified name is also a dot-separated name. For example,
	 * a class B defined as a member type of a class A should have the 
	 * type qualified name "A.B".
	 * 
	 * Note that in order to be found, a type name (or its toplevel enclosing
	 * type name) must match its corresponding compilation unit name. As a 
	 * consequence, secondary types cannot be found using this functionality.
	 * Secondary types can however be explicitely accessed through their enclosing
	 * unit or found by the <code>SearchEngine</code>.
	 * 
	 * This method only searches the current project (source folders and archives).
	 * 
	 * @param packageName the given package name
	 * @param typeQualifiedName the given type qualified name
	 * @exception EGLModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource
	 * @return the first type found following this project's classpath 
	 * with the given package name and type qualified name
	 * or <code>null</code> if none is found
	 * @see IPart#getTypeQualifiedName(char)

	 * @since 2.0
	 */
	IPart findPart(String packageName, String typeQualifiedName) throws EGLModelException;

	/**
	 * Returns all of the existing package fragment roots that exist
	 * on the EGLPath, in the order they are defined by the EGLPath.
	 *
	 * @return all of the existing package fragment roots that exist
	 * on the EGLPath
	 * @exception EGLModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource
	 */
	IPackageFragmentRoot[] getAllPackageFragmentRoots() throws EGLModelException;

	/**
	 * Returns an array of non-EGL resources directly contained in this project.
	 * It does not transitively answer non-EGL resources contained in folders;
	 * these would have to be explicitly iterated over.
	 * <p>
	 * Non-EGL resources includes other files and folders located in the
	 * project not accounted for by any of it source or binary package fragment
	 * roots. If the project is a source folder itself, resources excluded from the
	 * corresponding source EGLPath entry by one or more exclusion patterns
	 * are considered non-EGL resources and will appear in the result
	 * (possibly in a folder)
	 * </p>
	 * 
	 * @return an array of non-EGL resources directly contained in this project
	 * @exception EGLModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource
	 */
	Object[] getNonEGLResources() throws EGLModelException;

	/**
	 * Helper method for returning one option value only. Equivalent to <code>(String)this.getOptions(inheritEGLCoreOptions).get(optionName)</code>
	 * Note that it may answer <code>null</code> if this option does not exist, or if there is no custom value for it.
	 * <p>
	 * For a complete description of the configurable options, see <code>EGLCore#getDefaultOptions</code>.
	 * </p>
	 * 
	 * @param optionName the name of an option
	 * @param inheritEGLCoreOptions - boolean indicating whether EGLCore options should be inherited as well
	 * @return the String value of a given option
	 * @see EGLCore#getDefaultOptions
	 * @since 2.1
	 */
	String getOption(String optionName, boolean inheritEGLCoreOptions);
	
	/**
	 * Returns the table of the current custom options for this project. Projects remember their custom options,
	 * in other words, only the options different from the the EGLCore global options for the workspace.
	 * A boolean argument allows to directly merge the project options with global ones from <code>EGLCore</code>.
	 * <p>
	 * For a complete description of the configurable options, see <code>EGLCore#getDefaultOptions</code>.
	 * </p>
	 * 
	 * @param inheritEGLCoreOptions - boolean indicating whether EGLCore options should be inherited as well
	 * @return table of current settings of all options 
	 *   (key type: <code>String</code>; value type: <code>String</code>)
	 * @see EGLCore#getDefaultOptions
	 * @since 2.1
	 */
	Map getOptions(boolean inheritEGLCoreOptions);

	/**
	 * Returns the default output location for this project as a workspace-
	 * relative absolute path.
	 * <p>
	 * The default output location is where class files are ordinarily generated
	 * (and resource files, copied). Each source EGLPath entry can also
	 * specify an output location for the generated class files (and copied
	 * resource files) corresponding to compilation units under that source
	 * folder. This makes it possible to arrange generated class files for
	 * different source folders in different output folders, and not
	 * necessarily the default output folder. This means that the generated
	 * class files for the project may end up scattered across several folders,
	 * rather than all in the default output folder (which is more standard).
	 * </p>
	 * 
	 * @return the workspace-relative absolute path of the default output folder
	 * @exception EGLModelException if this element does not exist
	 * @see #setOutputLocation
	 * @see IEGLPathEntry#getOutputLocation
	 */
	IPath getOutputLocation() throws EGLModelException;

	/**
	 * Returns a package fragment root for the JAR at the specified file system path.
	 * This is a handle-only method.  The underlying <code>EGL.io.File</code>
	 * may or may not exist. No resource is associated with this local JAR
	 * package fragment root.
	 * 
	 * @param jarPath the jars's file system path
	 * @return a package fragment root for the JAR at the specified file system path
	 */
	IPackageFragmentRoot getPackageFragmentRoot(String jarPath);

	/**
	 * Returns a package fragment root for the given resource, which
	 * must either be a folder representing the top of a package hierarchy,
	 * or a <code>.jar</code> or <code>.zip</code> file.
	 * This is a handle-only method.  The underlying resource may or may not exist. 
	 * 
	 * @param resource the given resource
	 * @return a package fragment root for the given resource, which
	 * must either be a folder representing the top of a package hierarchy,
	 * or a <code>.jar</code> or <code>.zip</code> file
	 */
	IPackageFragmentRoot getPackageFragmentRoot(IResource resource);

	/**
	 * Returns all of the  package fragment roots contained in this
	 * project, identified on this project's resolved EGLPath. The result
	 * does not include package fragment roots in other projects referenced
	 * on this project's EGLPath.
	 *
	 * <p>NOTE: This is equivalent to <code>getChildren()</code>.
	 *
	 * @return all of the  package fragment roots contained in this
	 * project, identified on this project's resolved EGLPath
	 * @exception EGLModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource
	 */
	IPackageFragmentRoot[] getPackageFragmentRoots() throws EGLModelException;

	/**
	 * Returns all package fragments in all package fragment roots contained
	 * in this project. This is a convenience method.
	 *
	 * Note that the package fragment roots corresponds to the resolved
	 * EGLPath of the project.
	 *
	 * @return all package fragments in all package fragment roots contained
	 * in this project
	 * @exception EGLModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource
	 */
	IPackageFragment[] getPackageFragments() throws EGLModelException;

	/**
	 * Returns the <code>IProject</code> on which this <code>IEGLProject</code>
	 * was created. This is handle-only method.
	 * 
	 * @return the <code>IProject</code> on which this <code>IEGLProject</code>
	 * was created
	 */
	IProject getProject();

	/**
	 * Returns the raw EGLPath for the project, as a list of EGLPath entries. This corresponds to the exact set
	 * of entries which were assigned using <code>setRawEGLPath</code>, in particular such a EGLPath may contain
	 * EGLPath variable entries. EGLPath variable entries can be resolved individually (see <code>EGLCore#getEGLPathVariable</code>),
	 * or the full EGLPath can be resolved at once using the helper method <code>getResolvedEGLPath</code>.
	 * <p>
	 * A EGLPath variable provides an indirection level for better sharing a EGLPath. As an example, it allows
	 * a EGLPath to no longer refer directly to external JARs located in some user specific location. The EGLPath
	 * can simply refer to some variables defining the proper locations of these external JARs.
	 *  <p>
	 * Note that in case the project isn't yet opened, the EGLPath will directly be read from the associated <tt>.EGLPath</tt> file.
	 * <p>
	 * 
	 * @return the raw EGLPath for the project, as a list of EGLPath entries
	 * @exception EGLModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource
	 * @see IEGLPathEntry
	 */
	IEGLPathEntry[] getRawEGLPath() throws EGLModelException;

	/**
	 * Returns the names of the projects that are directly required by this
	 * project. A project is required if it is in its EGLPath.
	 * <p>
	 * The project names are returned in the order they appear on the EGLPath.
	 *
	 * @return the names of the projects that are directly required by this
	 * project in EGLPath order
	 * @exception EGLModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource
	 */
	String[] getRequiredProjectNames() throws EGLModelException;

	/**
	 * This is a helper method returning the resolved EGLPath for the project
	 * as a list of simple (non-variable, non-container) EGLPath entries.
	 * All EGLPath variable and EGLPath container entries in the project's
	 * raw EGLPath will be replaced by the simple EGLPath entries they
	 * resolve to.
	 * <p>
	 * The resulting resolved EGLPath is accurate for the given point in time.
	 * If the project's raw EGLPath is later modified, or if EGLPath
	 * variables are changed, the resolved EGLPath can become out of date.
	 * Because of this, hanging on resolved EGLPath is not recommended.
	 * </p>
	 * 
	 * @param ignoreUnresolvedEntry indicates how to handle unresolvable
	 * variables and containers; <code>true</code> indicates that missing
	 * variables and unresolvable EGLPath containers should be silently
	 * ignored, and that the resulting list should consist only of the
	 * entries that could be successfully resolved; <code>false</code> indicates
	 * that a <code>EGLModelException</code> should be thrown for the first
	 * unresolved variable or container
	 * @return the resolved EGLPath for the project as a list of simple 
	 * EGLPath entries, where all EGLPath variable and container entries
	 * have been resolved and substituted with their final target entries
	 * @exception EGLModelException in one of the corresponding situation:
	 * <ul>
	 *    <li>this element does not exist</li>
	 *    <li>an exception occurs while accessing its corresponding resource</li>
	 *    <li>a EGLPath variable or EGLPath container was not resolvable
	 *    and <code>ignoreUnresolvedEntry</code> is <code>false</code>.</li>
	 * </ul>
	 * @see IEGLPathEntry
	 */
	IEGLPathEntry[] getResolvedEGLPath(boolean ignoreUnresolvedEntry)
	     throws EGLModelException;

	/**
	 * Returns whether this project has been built at least once and thus whether it has a build state.
	 * @return true if this project has been built at least once, false otherwise
	 */
	boolean hasBuildState();

	/**
	 * Returns whether setting this project's EGLPath to the given EGLPath entries
	 * would result in a cycle.
	 *
	 * If the set of entries contains some variables, those are resolved in order to determine
	 * cycles.
	 * 
	 * @param entries the given EGLPath entries
	 * @return true if the given EGLPath entries would result in a cycle, false otherwise
	 */
	boolean hasEGLPathCycle(IEGLPathEntry[] entries);
	/**
	 * Returns whether the given element is on the EGLPath of this project, 
	 * that is, referenced from a EGLPath entry and not explicitly excluded
	 * using an exclusion pattern. 
	 * 
	 * @param element the given element
	 * @return <code>true</code> if the given element is on the EGLPath of
	 * this project, <code>false</code> otherwise
	 * @see IEGLPathEntry#getExclusionPatterns
	 * @since 2.0
	 */
	boolean isOnEGLPath(IEGLElement element);
	/**
	 * Returns whether the given resource is on the EGLPath of this project,
	 * that is, referenced from a EGLPath entry and not explicitly excluded
	 * using an exclusion pattern.
	 * 
	 * @param element the given element
	 * @return <code>true</code> if the given resource is on the EGLPath of
	 * this project, <code>false</code> otherwise
	 * @see IEGLPathEntry#getExclusionPatterns
	 * @since 2.1
	 */
	boolean isOnEGLPath(IResource resource);


	/**
	 * Sets the project custom options. All and only the options explicitly included in the given table 
	 * are remembered; all previous option settings are forgotten, including ones not explicitly
	 * mentioned.
	 * <p>
	 * For a complete description of the configurable options, see <code>EGLCore#getDefaultOptions</code>.
	 * </p>
	 * 
	 * @param newOptions the new options (key type: <code>String</code>; value type: <code>String</code>),
	 *   or <code>null</code> to flush all custom options (clients will automatically get the global EGLCore options).
	 * @see EGLCore#getDefaultOptions
	 * @since 2.1
	 */
	void setOptions(Map newOptions);

	/**
	 * Sets the default output location of this project to the location
	 * described by the given workspace-relative absolute path.
	 * <p>
	 * The default output location is where class files are ordinarily generated
	 * (and resource files, copied). Each source EGLPath entries can also
	 * specify an output location for the generated class files (and copied
	 * resource files) corresponding to compilation units under that source
	 * folder. This makes it possible to arrange that generated class files for
	 * different source folders to end up in different output folders, and not
	 * necessarily the default output folder. This means that the generated
	 * class files for the project may end up scattered across several folders,
	 * rather than all in the default output folder (which is more standard).
	 * </p>
	 * 
	 * @param path the workspace-relative absolute path of the default output
	 * folder
	 * @param monitor the progress monitor
	 * 
	 * @exception EGLModelException if the EGLPath could not be set. Reasons include:
	 * <ul>
	 *  <li> This EGL element does not exist (ELEMENT_DOES_NOT_EXIST)</li>
	 *  <li> The path refers to a location not contained in this project (<code>PATH_OUTSIDE_PROJECT</code>)
	 *  <li> The path is not an absolute path (<code>RELATIVE_PATH</code>)
	 *  <li> The path is nested inside a package fragment root of this project (<code>INVALID_PATH</code>)
	 *  <li> The output location is being modified during resource change event notification (CORE_EXCEPTION)	 
	 * </ul>
	 * @see #getOutputLocation
     * @see IEGLPathEntry#getOutputLocation
	 */
	void setOutputLocation(IPath path, IProgressMonitor monitor)
		throws EGLModelException;

	/**
	 * Sets the EGLPath of this project using a list of EGLPath entries. In particular such a EGLPath may contain
	 * EGLPath variable entries. EGLPath variable entries can be resolved individually (see <code>EGLCore#getEGLPathVariable</code>),
	 * or the full EGLPath can be resolved at once using the helper method <code>getResolvedEGLPath</code>.
	 * <p>
	 * A EGLPath variable provides an indirection level for better sharing a EGLPath. As an example, it allows
	 * a EGLPath to no longer refer directly to external JARs located in some user specific location. The EGLPath
	 * can simply refer to some variables defining the proper locations of these external JARs.
	 * <p>
	 * Setting the EGLPath to <code>null</code> specifies a default EGLPath
	 * (the project root). Setting the EGLPath to an empty array specifies an
	 * empty EGLPath.
	 * <p>
	 * If a cycle is detected while setting this EGLPath, an error marker will be added
	 * to the project closing the cycle.
	 * To avoid this problem, use <code>hasEGLPathCycle(IEGLPathEntry[] entries)</code>
	 * before setting the EGLPath.
	 *
	 * @param entries a list of EGLPath entries
	 * @param monitor the given progress monitor
	 * @exception EGLModelException if the EGLPath could not be set. Reasons include:
	 * <ul>
	 * <li> This EGL element does not exist (ELEMENT_DOES_NOT_EXIST)</li>
	 * <li> The EGLPath is being modified during resource change event notification (CORE_EXCEPTION)
	 * <li> The EGLPath failed the validation check as defined by <code>EGLConventions#validateEGLPath</code>
	 * </ul>
	 * @see IEGLPathEntry
	 */
	void setRawEGLPath(IEGLPathEntry[] entries, IProgressMonitor monitor)
		throws EGLModelException;
		
	/**
	 * Sets the both the EGLPath of this project and its default output
	 * location at once. The EGLPath is defined using a list of EGLPath
	 * entries. In particular, such a EGLPath may contain EGLPath variable
	 * entries. EGLPath variable entries can be resolved individually (see
	 * <code>EGLCore#getEGLPathVariable</code>), or the full EGLPath can be
	 * resolved at once using the helper method
	 * <code>getResolvedEGLPath</code>.
	 * <p>
	 * A EGLPath variable provides an indirection level for better sharing a
	 * EGLPath. As an example, it allows a EGLPath to no longer refer
	 * directly to external JARs located in some user specific location. The
	 * EGLPath can simply refer to some variables defining the proper
	 * locations of these external JARs.
	 * </p>
	 * <p>
	 * Setting the EGLPath to <code>null</code> specifies a default EGLPath
	 * (the project root). Setting the EGLPath to an empty array specifies an
	 * empty EGLPath.
	 * </p>
	 * <p>
	 * If a cycle is detected while setting this EGLPath, an error marker will
	 * be added to the project closing the cycle. To avoid this problem, use
	 * <code>hasEGLPathCycle(IEGLPathEntry[] entries)</code> before setting
	 * the EGLPath.
	 * </p>
	 * 
	 * @param entries a list of EGLPath entries
	 * @param monitor the progress monitor
	 * @param outputLocation the default output location
	 * @exception EGLModelException if the EGLPath could not be set. Reasons
	 * include:
	 * <ul>
	 * <li> This EGL element does not exist (ELEMENT_DOES_NOT_EXIST)</li>
	 * <li> Two or more entries specify source roots with the same or overlapping paths (NAME_COLLISION)
	 * <li> A entry of kind <code>CPE_PROJECT</code> refers to this project (INVALID_PATH)
	 *  <li>This EGL element does not exist (ELEMENT_DOES_NOT_EXIST)</li>
	 *	<li>The output location path refers to a location not contained in this project (<code>PATH_OUTSIDE_PROJECT</code>)
	 *	<li>The output location path is not an absolute path (<code>RELATIVE_PATH</code>)
	 *  <li>The output location path is nested inside a package fragment root of this project (<code>INVALID_PATH</code>)
	 * <li> The EGLPath is being modified during resource change event notification (CORE_EXCEPTION)
	 * </ul>
	 * @see IEGLPathEntry
	 * @since 2.0
	 */
	void setRawEGLPath(IEGLPathEntry[] entries, IPath outputLocation, IProgressMonitor monitor)
		throws EGLModelException;
	
	boolean isReadOnly();
	
	boolean isBinary();
	
	/**
	 * Returns whether this project is readonly.
	 * A project is considered readonly if it contains a file named ".readonly" in the root directory
	 * or if the .eglpath file does not specify an output directory
	 * 
	 * @return <code>true</code> if the this project is readonly, <code>false</code> otherwise
	 */
	public IPath getBuildStateLocation();
	
	/**
	 * This is a helper method returning the expanded eglpath for the project,
	 * as a list of eglpath entries, where all eglpath variable entries have
	 * been resolved and substituted with their final target entries. All
	 * project exports have been appended to project entries.
	 * 
	 * @param ignoreUnresolvedVariable
	 * @return
	 * @throws EGLModelException
	 */
	public IEGLPathEntry[] getExpandedEGLPath(boolean ignoreUnresolvedVariable) throws EGLModelException;
	
	/**
	 * Returns the list of all open projects which reference
	 * this project. This project may or may not exist. Returns
	 * an empty array if there are no referencing projects.
	 *
	 * @return a list of open projects referencing this project
	 */
	public IEGLProject[] getReferencingProjects() throws EGLModelException;
}
