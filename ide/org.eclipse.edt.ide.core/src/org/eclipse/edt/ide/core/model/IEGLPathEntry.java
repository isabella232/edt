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

import org.eclipse.core.runtime.IPath;

/** 
 * An entry on a EGL project EGLPath identifying one or more package fragment
 * roots. A EGLPath entry has a content kind (either source, 
 * <code>K_SOURCE</code>, or binary, <code>K_BINARY</code>), which is inherited
 * by each package fragment root and package fragment associated with the entry.
 * <p>
 * A EGLPath entry can refer to any of the following:<ul>
 * 
 *	<li>Source code in the current project. In this case, the entry identifies a
 *		root folder in the current project containing package fragments and
 *		<code>.EGL</code> source files. The root folder itself represents a default
 *		package, subfolders represent package fragments, and <code>.EGL</code> files
 *		represent compilation units. All compilation units will be compiled when
 * 		the project is built. The EGLPath entry must specify the
 *		absolute path to the root folder. Entries of this kind are 
 *		associated with the <code>CPE_SOURCE</code> constant.
 *      Source EGLPath entries can carry patterns to exclude selected files.
 *      Excluded <code>.EGL</code> source files do not appear as compilation
 *      units and are not compiled when the project is built.
 *  </li>
 * 
 *	<li>A required project. In this case the entry identifies another project in
 *		the workspace. The required project is used as a binary library when compiling
 *		(that is, the builder looks in the output location of the required project
 *		for required <code>.class</code> files when building). When performing other
 *		"development" operations - such as code assist, code resolve, type hierarchy
 *		creation, etc. - the source code of the project is referred to. Thus, development
 *		is performed against a required project's source code, and compilation is 
 *		performed against a required project's last built state.  The
 *		EGLPath entry must specify the absolute path to the
 *		project. Entries of this kind are  associated with the <code>CPE_PROJECT</code>
 *		constant. 
 * 		Note: referencing a required project with a EGLPath entry refers to the source 
 *     code or associated <code>.class</code> files located in its output location. 
 *     It will also automatically include any other libraries or projects that the required project's EGLPath 
 *     refers to, iff the corresponding EGLPath entries are tagged as being exported 
 *     (<code>IEGLPathEntry#isExported</code>). 
 *    Unless exporting some EGLPath entries, EGLPaths are not chained by default - 
 *    each project must specify its own EGLPath in its entirety.</li>
 * 
 *  <li> A path beginning in a EGLPath variable defined globally to the workspace.
 *		Entries of this kind are  associated with the <code>CPE_VARIABLE</code> constant.  
 *      EGLPath variables are created using <code>EGLCore#setEGLPathVariable</code>,
 * 		and gets resolved, to either a project or library entry, using
 *      <code>EGLCore#getResolvedEGLPathVariable</code>.
 *		It is also possible to register an automatic initializer (<code>EGLPathVariableInitializer</code>),
 * 	which will be invoked through the extension point "org.eclipse.jdt.core.EGLPathVariableInitializer".
 * 	After resolution, a EGLPath variable entry may either correspond to a project or a library entry. </li>
 * 
 *  <li> A named EGLPath container identified by its container path.
 *     A EGLPath container provides a way to indirectly reference a set of EGLPath entries through
 *     a EGLPath entry of kind <code>CPE_CONTAINER</code>. Typically, a EGLPath container can
 *     be used to describe a complex library composed of multiple JARs, projects or EGLPath variables,
 *     considering also that containers can be mapped differently on each project. Several projects can
 *     reference the same generic container path, but have each of them actually bound to a different
 *     container object.
 *     The container path is a formed by a first ID segment followed with extra segments, 
 *     which can be used as additional hints for resolving this container reference. If no container was ever 
 *     recorded for this container path onto this project (using <code>setEGLPathContainer</code>, 
 * 	then a <code>EGLPathContainerInitializer</code> will be activated if any was registered for this 
 * 	container ID onto the extension point "org.eclipse.jdt.core.EGLPathContainerInitializer".
 * 	A EGLPath container entry can be resolved explicitly using <code>EGLCore#getEGLPathContainer</code>
 * 	and the resulting container entries can contain any non-container entry. In particular, it may contain variable
 *     entries, which in turn needs to be resolved before being directly used. 
 * 	<br> Also note that the container resolution APIs include an IEGLProject argument, so as to allow the same
 * 	container path to be interpreted in different ways for different projects. </li>
 * </ul>
 * </p>
 * The result of <code>IEGLProject#getResolvedEGLPath</code> will have all entries of type
 * <code>CPE_VARIABLE</code> and <code>CPE_CONTAINER</code> resolved to a set of 
 * <code>CPE_SOURCE</code>, <code>CPE_LIBRARY</code> or <code>CPE_PROJECT</code>
 * EGLPath entries.
 * <p>
 * Any EGLPath entry other than a source folder (kind <code>CPE_SOURCE</code>) can
 * be marked as being exported. Exported entries are automatically contributed to
 * dependent projects, along with the project's default output folder, which is
 * implicitly exported, and any auxiliary output folders specified on source
 * EGLPath entries. The project's output folder(s) are always listed first,
 * followed by the any exported entries.
 * <p>
 * This interface is not intended to be implemented by clients.
 * EGLPath entries can be created via methods on <code>EGLCore</code>.
 * </p>
 *
 * @see EGLCore#newLibraryEntry
 * @see EGLCore#newProjectEntry
 * @see EGLCore#newSourceEntry
 * @see EGLCore#newVariableEntry
 * @see EGLCore#newContainerEntry
 * @see EGLPathVariableInitializer
 * @see EGLPathContainerInitializer
 */
public interface IEGLPathEntry {

	
	/**
	 * Entry kind constant describing a eglpath entry identifying a
	 * library. A library is a folder or JAR containing package
	 * fragments consisting of pre-compiled binaries.
	 */
	int CPE_LIBRARY = 1;

	/**
	 * Entry kind constant describing a EGLPath entry identifying a
	 * required project.
	 */
	int CPE_PROJECT = 2;

	/**
	 * Entry kind constant describing a EGLPath entry identifying a
	 * folder containing package fragments with source code
	 * to be compiled.
	 */
	int CPE_SOURCE = 3;

	/**
	 * Entry kind constant describing a EGLPath entry defined using
	 * a path that begins with a EGLPath variable reference.
	 */
	int CPE_VARIABLE = 4;

	/**
	 * Entry kind constant describing a EGLPath entry representing
	 * a name EGLPath container.
	 * 
	 * @since 2.0
	 */
	int CPE_CONTAINER = 5;

	
	/**
	 * Returns the kind of files found in the package fragments identified by this
	 * eglpath entry.
	 *
	 * @return <code>IPackageFragmentRoot.K_SOURCE</code> for files containing
	 *   source code, and <code>IPackageFragmentRoot.K_BINARY</code> for binary
	 *   class files.
	 *   There is no specified value for an entry denoting a variable (<code>CPE_VARIABLE</code>)
	 *   or a eglpath container (<code>CPE_CONTAINER</code>).
	 */
	int getContentKind();
	/**
	 * Returns the kind of this EGLPath entry.
	 *
	 * @return one of:
	 * <ul>
	 * <li><code>CPE_SOURCE</code> - this entry describes a source root in
	 		its project
	 * <li><code>CPE_PROJECT</code> - this entry describes another project
	 *
	 * <li><code>CPE_VARIABLE</code> - this entry describes a project or library
	 *  	indirectly via a EGLPath variable in the first segment of the path
	 * *
	 * <li><code>CPE_CONTAINER</code> - this entry describes set of entries
	 *  	referenced indirectly via a EGLPath container
	 * </ul>
	 */
	int getEntryKind();

	/**
	 * Returns the set of patterns used to exclude resources associated with
	 * this source entry.
	 * <p>
	 * Exclusion patterns allow specified portions of the resource tree rooted
	 * at this source entry's path to be filtered out. If no exclusion patterns
	 * are specified, this source entry includes all relevent files. Each path
	 * specified must be a relative path, and will be interpreted relative
	 * to this source entry's path. File patterns are case-sensitive. A file
	 * matched by one or more of these patterns is excluded from the 
	 * corresponding package fragment root.
	 * </p>
	 * <p>
	 * Note that there is no need to supply a pattern to exclude ".class" files
	 * because a source entry filters these out automatically.
	 * </p>
	 * <p>
	 * The pattern mechanism is similar to Ant's. Each pattern is represented as
	 * a relative path. The path segments can be regular file or folder names or simple patterns
	 * involving standard wildcard characters.
	 * </p>
	 * <p>
	 * '*' matches 0 or more characters within a segment. So
	 * <code>*.EGL</code> matches <code>.EGL</code>, <code>a.EGL</code>
	 * and <code>Foo.EGL</code>, but not <code>Foo.properties</code>
	 * (does not end with <code>.EGL</code>).
	 * </p>
	 * <p>
	 * '?' matches 1 character within a segment. So <code>?.EGL</code> 
	 * matches <code>a.EGL</code>, <code>A.EGL</code>, 
	 * but not <code>.EGL</code> or <code>xyz.EGL</code> (neither have
	 * just one character before <code>.EGL</code>).
	 * </p>
	 * <p>
	 * Combinations of *'s and ?'s are allowed.
	 * </p>
	 * <p>
	 * The special pattern '**' matches zero or more segments. A path 
	 * like <code>tests/</code> that ends in a trailing separator is interpreted
	 * as <code>tests/&#42;&#42;</code>, and would match all files under the 
	 * the folder named <code>tests</code>.
	 * </p>
	 * <p>
	 * Examples:
	 * <ul>
	 * <li>
	 * <code>tests/&#42;&#42;</code> (or simply <code>tests/</code>) 
	 * matches all files under a root folder
	 * named <code>tests</code>. This includes <code>tests/Foo.EGL</code>
	 * and <code>tests/com/example/Foo.EGL</code>, but not 
	 * <code>com/example/tests/Foo.EGL</code> (not under a root folder named
	 * <code>tests</code>).
	 * </li>
	 * <li>
	 * <code>tests/&#42;</code> matches all files directly below a root 
	 * folder named <code>tests</code>. This includes <code>tests/Foo.EGL</code>
	 * and <code>tests/FooHelp.EGL</code>
	 * but not <code>tests/com/example/Foo.EGL</code> (not directly under
	 * a folder named <code>tests</code>) or 
	 * <code>com/Foo.EGL</code> (not under a folder named <code>tests</code>).
	 * </li>
	 * <li>
	 * <code>&#42;&#42;/tests/&#42;&#42;</code> matches all files under any
	 * folder named <code>tests</code>. This includes <code>tests/Foo.EGL</code>,
	 * <code>com/examples/tests/Foo.EGL</code>, and 
	 * <code>com/examples/tests/unit/Foo.EGL</code>, but not 
	 * <code>com/example/Foo.EGL</code> (not under a folder named
	 * <code>tests</code>).
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @return the possibly empty list of resource exclusion patterns 
	 *   associated with this source entry, and <code>null</code> for other
	 *   kinds of EGLPath entries
	 * @since 2.1
	 */
	IPath[] getExclusionPatterns();
	
	/**
	 * Returns the full path to the specific location where the builder writes 
	 * <code>.class</code> files generated for this source entry 
	 * (entry kind <code>CPE_SOURCE</code>).
	 * <p>
	 * Source entries can optionally be associated with a specific output location.
	 * If none is provided, the source entry will be implicitly associated with its project
	 * default output location (see <code>IEGLProject#getOutputLocation</code>).
	 * </p><p>
	 * NOTE: A specific output location cannot coincidate with another source/library entry.
	 * </p>
	 * 
	 * @return the full path to the specific location where the builder writes 
	 * <code>.class</code> files for this source entry, or <code>null</code>
	 * if using default output folder
	 * @since 2.1
	 */
	IPath getOutputLocation();
	
	/**
	 * Returns the path of this EGLPath entry.
	 *
	 * The meaning of the path of a EGLPath entry depends on its entry kind:<ul>
	 *	<li>Source code in the current project (<code>CPE_SOURCE</code>) -  
	 *      The path associated with this entry is the absolute path to the root folder. </li>
	 *	<li>A binary library in the current project (<code>CPE_LIBRARY</code>) - the path
	 *		associated with this entry is the absolute path to the JAR (or root folder), and 
	 *		in case it refers to an external JAR, then there is no associated resource in 
	 *		the workbench.
	 *	<li>A required project (<code>CPE_PROJECT</code>) - the path of the entry denotes the
	 *		path to the corresponding project resource.</li>
	 *  <li>A variable entry (<code>CPE_VARIABLE</code>) - the first segment of the path 
	 *      is the name of a EGLPath variable. If this EGLPath variable
	 *		is bound to the path <it>P</it>, the path of the corresponding EGLPath entry
	 *		is computed by appending to <it>P</it> the segments of the returned
	 *		path without the variable.</li>
	 *  <li> A container entry (<code>CPE_CONTAINER</code>) - the path of the entry
	 * 	is the name of the EGLPath container, which can be bound indirectly to a set of EGLPath 
	 * 	entries after resolution. The containerPath is a formed by a first ID segment followed with 
	 *     extra segments that can be used as additional hints for resolving this container 
	 * 	reference (also see <code>IEGLPathContainer</code>).
	 * </li>
	 * </ul>
	 *
	 * @return the path of this EGLPath entry
	 */
	IPath getPath();

	/**
	 * Returns the path to the source archive or folder associated with this
	 * eglpath entry, or <code>null</code> if this eglpath entry has no
	 * source attachment.
	 * <p>
	 * Only library and variable eglpath entries may have source attachments.
	 * For library eglpath entries, the result path (if present) locates a source
	 * archive or folder. This archive or folder can be located in a project of the 
	 * workspace or outside thr workspace. For variable eglpath entries, the 
	 * result path (if present) has an analogous form and meaning as the 
	 * variable path, namely the first segment is the name of a eglpath variable.
	 * </p>
	 *
	 * @return the path to the source archive or folder, or <code>null</code> if none
	 */
	IPath getSourceAttachmentPath();

	/**
	 * Returns the path within the source archive or folder where package fragments
	 * are located. An empty path indicates that packages are located at
	 * the root of the source archive or folder. Returns a non-<code>null</code> value
	 * if and only if <code>getSourceAttachmentPath</code> returns 
	 * a non-<code>null</code> value.
	 *
	 * @return the path within the source archive or folder, or <code>null</code> if
	 *    not applicable
	 */
	IPath getSourceAttachmentRootPath();
	/**
	 * Returns whether this entry is exported to dependent projects.
	 * Always returns <code>false</code> for source entries (kind
	 * <code>CPE_SOURCE</code>), which cannot be exported.
	 * 
	 * @return <code>true</code> if exported, and <code>false</code> otherwise
	 * @since 2.0
	 */
	boolean isExported();
	
	/**
	 * Return if this entry is created from binary project. 
	 * @return
	 */
	boolean isBinaryProject();
	
	void setBinaryProject(boolean isBinaryProject);
	
}
