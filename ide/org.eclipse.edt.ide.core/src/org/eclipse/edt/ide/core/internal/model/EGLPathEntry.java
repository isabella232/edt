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

import java.util.LinkedHashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.internal.core.utils.CharOperation;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.w3c.dom.Element;

/**
 * @see IEGLPathEntry
 */
public class EGLPathEntry implements IEGLPathEntry {

	/**
	 * Describes the kind of eglpath entry - one of 
	 * CPE_PROJECT, CPE_LIBRARY, CPE_SOURCE, CPE_EGLAR ,CPE_VARIABLE or CPE_CONTAINER
	 */
	public int entryKind;

	/**
	 * Describes the kind of package fragment roots found on
	 * this eglpath entry - either K_BINARY or K_SOURCE or
	 * K_OUTPUT.
	 */
	public int contentKind;

	/**
	 * The meaning of the path of a eglpath entry depends on its entry kind:<ul>
	 *	<li>Source code in the current project (<code>CPE_SOURCE</code>) -  
	 *      The path associated with this entry is the absolute path to the root folder. </li>
	 *	<li>A binary library in the current project (<code>CPE_LIBRARY</code>) - the path
	 *		associated with this entry is the absolute path to the JAR (or root folder), and 
	 *		in case it refers to an external JAR, then there is no associated resource in 
	 *		the workbench.
	 *	<li>A required project (<code>CPE_PROJECT</code>) - the path of the entry denotes the
	 *		path to the corresponding project resource.</li>
	 *  <li>A variable entry (<code>CPE_VARIABLE</code>) - the first segment of the path 
	 *      is the name of a eglpath variable. If this eglpath variable
	 *		is bound to the path <it>P</it>, the path of the corresponding eglpath entry
	 *		is computed by appending to <it>P</it> the segments of the returned
	 *		path without the variable.</li>
	 *  <li> A container entry (<code>CPE_CONTAINER</code>) - the first segment of the path is denoting
	 *     the unique container identifier (for which a <code>EGLPathContainerInitializer</code> could be
	 * 	registered), and the remaining segments are used as additional hints for resolving the container entry to
	 * 	an actual <code>IEGLPathContainer</code>.</li>
	 */
	public IPath path;

	/**
	 * Patterns allowing to exclude portions of the resource tree denoted by this entry path.
	 */
	public IPath[] exclusionPatterns;
	private char[][] fullCharExclusionPatterns;
	private final static char[][] UNINIT_PATTERNS = new char[][] { "Non-initialized yet".toCharArray() }; //$NON-NLS-1$

	private String rootID;
	
	/**
	 * Default exclusion pattern set
	 */
	public final static IPath[] EXCLUDE_NONE = {};
				
	/**
	 * Describes the path to the source archive associated with this
	 * eglpath entry, or <code>null</code> if this eglpath entry has no
	 * source attachment.
	 * <p>
	 * Only library and variable eglpath entries may have source attachments.
	 * For library eglpath entries, the result path (if present) locates a source
	 * archive. For variable eglpath entries, the result path (if present) has
	 * an analogous form and meaning as the variable path, namely the first segment 
	 * is the name of a eglpath variable.
	 */
	public IPath sourceAttachmentPath;

	/**
	 * Describes the path within the source archive where package fragments
	 * are located. An empty path indicates that packages are located at
	 * the root of the source archive. Returns a non-<code>null</code> value
	 * if and only if <code>getSourceAttachmentPath</code> returns 
	 * a non-<code>null</code> value.
	 */
	public IPath sourceAttachmentRootPath;

	/**
	 * Specific output location (for this source entry)
	 */
	public IPath specificOutputLocation;
	
	/**
	 * A constant indicating an output location.
	 */
	public static final int K_OUTPUT = 10;
	public static final String DOT_DOT = "..";
	
	/**
	 * The export flag
	 */
	public boolean isExported;

	private boolean isBinaryProject; 
	
	/**
	 * Creates a class path entry of the specified kind with the given path.
	 */
	public EGLPathEntry(
		int contentKind,
		int entryKind,
		IPath path,
		//IPath[] inclusionPatterns, // EGLMIGRATION: Inclusion Pattern support?
		IPath[] exclusionPatterns) {

		this.contentKind = contentKind;
		this.entryKind = entryKind;
		this.path = path;
		this.exclusionPatterns = exclusionPatterns;
	    if (exclusionPatterns.length > 0) {
			this.fullCharExclusionPatterns = UNINIT_PATTERNS;
	    } else {
			this.fullCharExclusionPatterns = null; // empty exclusion pattern means nothing is excluded
	    }
	}
	/**
	 * Creates a class path entry of the specified kind with the given path.
	 */
	public EGLPathEntry(
		int contentKind,
		int entryKind,
		IPath path,
		//IPath[] inclusionPatterns, // EGLMIGRATION: Inclusion Pattern support?
		IPath[] exclusionPatterns,
		IPath sourceAttachmentPath,
		IPath sourceAttachmentRootPath,
		IPath specificOutputLocation,
		boolean isExported) {

		this.contentKind = contentKind;
		this.entryKind = entryKind;
		this.path = path;
		this.exclusionPatterns = exclusionPatterns;
	    if (exclusionPatterns.length > 0) {
			this.fullCharExclusionPatterns = UNINIT_PATTERNS;
	    } else {
			this.fullCharExclusionPatterns = null; // empty exclusion pattern means nothing is excluded
	    }
		this.sourceAttachmentPath = sourceAttachmentPath;
		this.sourceAttachmentRootPath = sourceAttachmentRootPath;
		this.specificOutputLocation = specificOutputLocation;
		this.isExported = isExported;
	}
	
	/*
	 * Returns a char based representation of the exclusions patterns full path.
	 */
	public char[][] fullExclusionPatternChars() {

		if (this.fullCharExclusionPatterns == UNINIT_PATTERNS) {
			int length = this.exclusionPatterns.length;
			this.fullCharExclusionPatterns = new char[length][];
			IPath prefixPath = path.removeTrailingSeparator();
			for (int i = 0; i < length; i++) {
				this.fullCharExclusionPatterns[i] = 
					prefixPath.append(this.exclusionPatterns[i]).toString().toCharArray();
			}
		}
		return this.fullCharExclusionPatterns;
	}
	
	/**
	 * Returns the XML encoding of the class path.
	 */
	public void elementEncode(XMLWriter writer, IPath projectPath, boolean indent, boolean newLine) {
		LinkedHashMap parameters = new LinkedHashMap();
		
		String kindString = "";
		if(!this.isBinaryProject()) {
			kindString = EGLPathEntry.kindToString(this.entryKind);
			if(this.contentKind == K_OUTPUT){
				kindString = "output";
			}
		} else {
			kindString = EGLPathEntry.kindToString(IEGLPathEntry.CPE_PROJECT);
		}
		parameters.put("kind", kindString);//$NON-NLS-1$
		
		IPath xmlPath = this.path;
		if (this.entryKind != IEGLPathEntry.CPE_VARIABLE && this.entryKind != IEGLPathEntry.CPE_CONTAINER) {
			// translate to project relative from absolute (unless a device path)
			if (xmlPath.isAbsolute()) {
				if (projectPath != null && projectPath.isPrefixOf(xmlPath)) {
					if (xmlPath.segment(0).equals(projectPath.segment(0))) {
						xmlPath = xmlPath.removeFirstSegments(1);
						xmlPath = xmlPath.makeRelative();
					} else {
						xmlPath = xmlPath.makeAbsolute();
					}
				}
			}
		}
		if(isBinaryProject() && Util.isEGLARFileName(path.toOSString())) {
			int binaryProjectNameIndex = path.segmentCount() -2;
			xmlPath = new Path(IPath.SEPARATOR + path.segments()[binaryProjectNameIndex]);
		}
		parameters.put("path", String.valueOf(xmlPath));//$NON-NLS-1$
		
		if (this.sourceAttachmentPath != null) {
			xmlPath = this.sourceAttachmentPath;
			// translate to project relative from absolute 
			if (this.entryKind != IEGLPathEntry.CPE_VARIABLE && projectPath != null && projectPath.isPrefixOf(xmlPath)) {
				if (xmlPath.segment(0).equals(projectPath.segment(0))) {
					xmlPath = xmlPath.removeFirstSegments(1);
					xmlPath = xmlPath.makeRelative();
				}
			}
			parameters.put("sourcepath", String.valueOf(xmlPath));//$NON-NLS-1$
		}
		if (this.sourceAttachmentRootPath != null) {
			parameters.put("rootpath", String.valueOf(this.sourceAttachmentRootPath));//$NON-NLS-1$
		}
		if (this.isExported) {
			parameters.put("exported", "true");//$NON-NLS-1$//$NON-NLS-2$
		}
		// EGLTODO: Add support for inclusion patterns?
//		if (this.inclusionPatterns != null && this.inclusionPatterns.length > 0) {
//			StringBuffer includeRule = new StringBuffer(10);
//			for (int i = 0, max = this.inclusionPatterns.length; i < max; i++){
//				if (i > 0) includeRule.append('|');
//				includeRule.append(this.inclusionPatterns[i]);
//			}
//			parameters.put("including", String.valueOf(includeRule));//$NON-NLS-1$
//		}
		if (this.exclusionPatterns != null && this.exclusionPatterns.length > 0) {
			StringBuffer excludeRule = new StringBuffer(10);
			for (int i = 0, max = this.exclusionPatterns.length; i < max; i++){
				if (i > 0) excludeRule.append('|');
				excludeRule.append(this.exclusionPatterns[i]);
			}
			parameters.put("excluding", String.valueOf(excludeRule));//$NON-NLS-1$
		}
		
		if (this.specificOutputLocation != null) {
			IPath outputLocation = this.specificOutputLocation.removeFirstSegments(1);
			outputLocation = outputLocation.makeRelative();
			parameters.put("output", String.valueOf(outputLocation));//$NON-NLS-1$
		}

		writer.printTag("eglpathentry", parameters, indent, newLine, true);//$NON-NLS-1$
	}
	
	public static IEGLPathEntry elementDecode(Element element, IPath projectPath, String projectName) {
		
		String kindAttr = element.getAttribute("kind"); //$NON-NLS-1$
		String pathAttr = element.getAttribute("path"); //$NON-NLS-1$

		// ensure path is absolute
		IPath path = new Path(pathAttr); 		
		int kind = kindFromString(kindAttr);
		if (kind != IEGLPathEntry.CPE_VARIABLE && kind != IEGLPathEntry.CPE_CONTAINER && !path.isAbsolute()) {
			path = projectPath.append(path);
		}
		// source attachment info (optional)
		IPath sourceAttachmentPath = 
			element.hasAttribute("sourcepath")	//$NON-NLS-1$
			? new Path(element.getAttribute("sourcepath")) //$NON-NLS-1$
			: null;
		if (kind != IEGLPathEntry.CPE_VARIABLE && sourceAttachmentPath != null && !sourceAttachmentPath.isAbsolute()) {
			sourceAttachmentPath = projectPath.append(sourceAttachmentPath);
		}
		IPath sourceAttachmentRootPath = 
			element.hasAttribute("rootpath") //$NON-NLS-1$
			? new Path(element.getAttribute("rootpath")) //$NON-NLS-1$
			: null;
			
		// exported flag (optional)
		boolean isExported = element.getAttribute("exported").equals("true"); //$NON-NLS-1$ //$NON-NLS-2$

// EGLMIGRATION: Inclusion Pattern support?
		// inclusion patterns (optional)
//		String inclusion = element.getAttribute("including"); //$NON-NLS-1$ 
//		IPath[] inclusionPatterns = INCLUDE_ALL;
//		if (!inclusion.equals("")) { //$NON-NLS-1$ 
//			char[][] patterns = CharOperation.splitOn('|', inclusion.toCharArray());
//			int patternCount;
//			if ((patternCount  = patterns.length) > 0) {
//				inclusionPatterns = new IPath[patternCount];
//				for (int j = 0; j < patterns.length; j++){
//					inclusionPatterns[j] = new Path(new String(patterns[j]));
//				}
//			}
//		}

		// exclusion patterns (optional)
		String exclusion = element.getAttribute("excluding"); //$NON-NLS-1$ 
		IPath[] exclusionPatterns = EXCLUDE_NONE;
		if (!exclusion.equals("")) { //$NON-NLS-1$ 
			char[][] patterns = CharOperation.splitOn('|', exclusion.toCharArray());
			int patternCount;
			if ((patternCount  = patterns.length) > 0) {
				exclusionPatterns = new IPath[patternCount];
				for (int j = 0; j < patterns.length; j++){
					exclusionPatterns[j] = new Path(new String(patterns[j]));
				}
			}
		}

		// custom output location
		IPath outputLocation = element.hasAttribute("output") ? projectPath.append(element.getAttribute("output")) : null; //$NON-NLS-1$ //$NON-NLS-2$
		
		// recreate the CP entry
		switch (kind) {

			case IEGLPathEntry.CPE_PROJECT :
				return EGLCore.newProjectEntry(path, isExported);
				
			case IEGLPathEntry.CPE_LIBRARY :
				if (!path.isAbsolute()) {
					return null;
				}
				return EGLCore.newLibraryEntry(path, sourceAttachmentPath,
						sourceAttachmentRootPath, isExported);
				
			case IEGLPathEntry.CPE_SOURCE :
				// must be an entry in this project or specify another project
				String projSegment = path.segment(0);
				if (projSegment != null && projSegment.equals(projectName)) { // this project
					// EGLMIGRATION: Inclusion Pattern support?
					//return EGLCore.newSourceEntry(path, inclusionPatterns, exclusionPatterns, outputLocation);
					return EGLCore.newSourceEntry(path, exclusionPatterns, outputLocation);
				} else { // another project
					/*Is able to find the project in the workspace?
					If yes, then return the project entry in the workspace;
					If no, then try to find the project in the target platform,
					and create the entry as a library entry which reference to an EGLAR! 
					*/
//					if(isProjectExistedInWS(projSegment) ) {
						return EGLCore.newProjectEntry(path, isExported);
//					} 
					//if we get here, assume its going to be in the workspace
					//One possible is that workspace cannot find the project, assume the 
					//project is not imported to the workspace.
//					return EGLCore.newSourceEntry(path, exclusionPatterns, outputLocation);

				}

			case IEGLPathEntry.CPE_VARIABLE :
				return EGLCore.newVariableEntry(
						path,
						sourceAttachmentPath,
						sourceAttachmentRootPath, 
						isExported);
				
			case IEGLPathEntry.CPE_CONTAINER :
				return EGLCore.newContainerEntry(
						path,
						isExported);
				
			case EGLPathEntry.K_OUTPUT :
				if (!path.isAbsolute()) return null;
				return new EGLPathEntry(
						EGLPathEntry.K_OUTPUT,
						IEGLPathEntry.CPE_LIBRARY,
						path,
						//EGLPathEntry.INCLUDE_ALL, // EGLMIGRATION: Inclusion Pattern support?
						EGLPathEntry.EXCLUDE_NONE, 
						null, // source attachment
						null, // source attachment root
						null, // custom output location
						false);
			default :
				throw new Assert.AssertionFailedException(EGLModelResources.bind(EGLModelResources.eglpathUnknownKind, kindAttr));
		}
	}
	
	/**
	 * Check if the project is existed in the workspace.
	 * @param projSegment
	 * @return
	 */
	private static boolean isProjectExistedInWS(String projSegment) {
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = workspaceRoot.getProjects();
		for(IProject project : projects) {
			if(projSegment.equals(project.getName())) {
				return true;
			}
		}
		return false;
	}
		
	/**
	 * Returns true if the given object is a eglpath entry
	 * with equivalent attributes.
	 */
	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (object instanceof IEGLPathEntry) {
			IEGLPathEntry otherEntry = (IEGLPathEntry) object;

			if (this.contentKind != otherEntry.getContentKind())
				return false;

			if (this.entryKind != otherEntry.getEntryKind())
				return false;

			if (this.isExported != otherEntry.isExported())
				return false;

			if(this.path == null) {
				if(otherEntry.getPath() != null) {
					return false;
				}
			} else {
				if(otherEntry.getPath() == null) {
					return false;
				}
				if (!this.path.toOSString().equalsIgnoreCase((otherEntry.getPath().toOSString())))
					return false;
			}

			IPath[] otherExcludes = otherEntry.getExclusionPatterns();
			if (this.exclusionPatterns != otherExcludes){
				int excludeLength = this.exclusionPatterns.length;
				if (otherExcludes.length != excludeLength) 
					return false;
				for (int i = 0; i < excludeLength; i++) {
					// compare toStrings instead of IPaths 
					// since IPath.equals is specified to ignore trailing separators
					if (!this.exclusionPatterns[i].toString().equals(otherExcludes[i].toString()))
						return false;
				}
			}
			
			IPath otherPath = otherEntry.getOutputLocation();
			if (this.specificOutputLocation == null) {
				if (otherPath != null)
					return false;
			} else {
				if (!this.specificOutputLocation.equals(otherPath))
					return false;
			}
			
			otherPath = otherEntry.getSourceAttachmentPath();
			if (this.sourceAttachmentPath == null) {
				if (otherPath != null)
					return false;
			} else {
				if (!this.sourceAttachmentPath.equals(otherPath))
					return false;
			}

			return true;
		} else {
			return false;
		}
	}

	/**
	 * @see IEGLPathEntry
	 */
	public int getContentKind() {
		return this.contentKind;
	}

	/**
	 * @see IEGLPathEntry
	 */
	public int getEntryKind() {
		return this.entryKind;
	}

	/**
	 * @see IEGLPathEntry#getExclusionPatterns()
	 */
	public IPath[] getExclusionPatterns() {
		return this.exclusionPatterns;
	}

	/**
	 * @see IEGLPathEntry#getOutputLocation()
	 */
	public IPath getOutputLocation() {
		return this.specificOutputLocation;
	}

	/**
	 * @see IEGLPathEntry
	 */
	public IPath getPath() {
		return this.path;
	}

	/**
	 * @see IEGLPathEntry
	 */
	public IPath getSourceAttachmentPath() {
		return this.sourceAttachmentPath;
	}

	/**
	 * @see IEGLPathEntry
	 */
	public IPath getSourceAttachmentRootPath() {
		return this.sourceAttachmentRootPath;
	}

	/**
	 * Returns the hash code for this eglpath entry
	 */
	public int hashCode() {
		return this.path.hashCode();
	}

	/**
	 * @see IEGLPathEntry#isExported()
	 */
	public boolean isExported() {
		return this.isExported;
	}

	/**
	 * Returns the kind of a <code>PackageFragmentRoot</code> from its <code>String</code> form.
	 */
	static int kindFromString(String kindStr) {

		if (kindStr.equalsIgnoreCase("prj")) //$NON-NLS-1$
			return IEGLPathEntry.CPE_PROJECT;
		if (kindStr.equalsIgnoreCase("var")) //$NON-NLS-1$
			return IEGLPathEntry.CPE_VARIABLE;
		if (kindStr.equalsIgnoreCase("con")) //$NON-NLS-1$
			return IEGLPathEntry.CPE_CONTAINER;
		if (kindStr.equalsIgnoreCase("src")) //$NON-NLS-1$
			return IEGLPathEntry.CPE_SOURCE;
		if (kindStr.equalsIgnoreCase("lib")) //$NON-NLS-1$
			return IEGLPathEntry.CPE_LIBRARY;
		if (kindStr.equalsIgnoreCase("output")) //$NON-NLS-1$
			return EGLPathEntry.K_OUTPUT;
		return -1;
	}

	/**
	 * Returns a <code>String</code> for the kind of a class path entry.
	 */
	static String kindToString(int kind) {

		switch (kind) {
			case IEGLPathEntry.CPE_PROJECT :
				return "src"; // backward compatibility //$NON-NLS-1$
			case IEGLPathEntry.CPE_SOURCE :
				return "src"; //$NON-NLS-1$
			case IEGLPathEntry.CPE_LIBRARY :
				return "lib"; //$NON-NLS-1$
			case IEGLPathEntry.CPE_VARIABLE :
				return "var"; //$NON-NLS-1$
			case IEGLPathEntry.CPE_CONTAINER :
				return "con"; //$NON-NLS-1$
			case EGLPathEntry.K_OUTPUT :
				return "output"; //$NON-NLS-1$
			default :
				return "unknown"; //$NON-NLS-1$
		}
	}

	/**
	 * Returns a printable representation of this eglpath entry.
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getPath().toString());
		buffer.append('[');
		switch (getEntryKind()) {
			case IEGLPathEntry.CPE_LIBRARY :
				buffer.append("CPE_LIBRARY"); //$NON-NLS-1$
				break;
			case IEGLPathEntry.CPE_PROJECT :
				buffer.append("CPE_PROJECT"); //$NON-NLS-1$
				break;
			case IEGLPathEntry.CPE_SOURCE :
				buffer.append("CPE_SOURCE"); //$NON-NLS-1$
				break;
			case IEGLPathEntry.CPE_VARIABLE :
				buffer.append("CPE_VARIABLE"); //$NON-NLS-1$
				break;
			case IEGLPathEntry.CPE_CONTAINER :
				buffer.append("CPE_CONTAINER"); //$NON-NLS-1$
				break;
		}
		buffer.append("]["); //$NON-NLS-1$
		switch (getContentKind()) {
			case IPackageFragmentRoot.K_BINARY :
				buffer.append("K_BINARY"); //$NON-NLS-1$
				break;
			case IPackageFragmentRoot.K_SOURCE :
				buffer.append("K_SOURCE"); //$NON-NLS-1$
				break;
			case EGLPathEntry.K_OUTPUT :
				buffer.append("K_OUTPUT"); //$NON-NLS-1$
				break;
		}
		buffer.append(']');
		if (getSourceAttachmentPath() != null) {
			buffer.append("[sourcePath:"); //$NON-NLS-1$
			buffer.append(getSourceAttachmentPath());
			buffer.append(']');
		}
		if (getSourceAttachmentRootPath() != null) {
			buffer.append("[rootPath:"); //$NON-NLS-1$
			buffer.append(getSourceAttachmentRootPath());
			buffer.append(']');
		}
		buffer.append("[isExported:"); //$NON-NLS-1$
		buffer.append(this.isExported);
		buffer.append(']');
		IPath[] patterns = getExclusionPatterns();
		int length;
		if ((length = patterns.length) > 0) {
			buffer.append("[excluding:"); //$NON-NLS-1$
			for (int i = 0; i < length; i++) {
				buffer.append(patterns[i]);
				if (i != length-1) {
					buffer.append('|');
				}
			}
			buffer.append(']');
		}
		if (getOutputLocation() != null) {
			buffer.append("[output:"); //$NON-NLS-1$
			buffer.append(getOutputLocation());
			buffer.append(']');
		}
		return buffer.toString();
	}
	public EGLPathEntry resolvedDotDot() {
		IPath resolvedPath = resolveDotDot(this.path);
		if (resolvedPath == this.path)
			return this;
		return new EGLPathEntry(
							getContentKind(),
							getEntryKind(),
							resolvedPath,
//							this.inclusionPatterns,
							this.exclusionPatterns,
							getSourceAttachmentPath(),
							getSourceAttachmentRootPath(),
							getOutputLocation(),
							this.isExported);
	}	
	/*
	 * Resolves the ".." in the given path. Returns the given path if it contains no ".." segment.
	 */
	public static IPath resolveDotDot(IPath path) {
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IPath newPath = null;
		IPath workspaceLocation = null;
		for (int i = 0, length = path.segmentCount(); i < length; i++) {
			String segment = path.segment(i);
			if (DOT_DOT.equals(segment)) {
				if (newPath == null) {
					if (i == 0) {
						workspaceLocation = workspaceRoot.getLocation();
						newPath = workspaceLocation;
					} else {
						newPath = path.removeFirstSegments(i);
					}
				} else {
					if (newPath.segmentCount() > 0) {
						newPath = newPath.removeLastSegments(1);
					} else {
						workspaceLocation = workspaceRoot.getLocation();
						newPath = workspaceLocation;
					}
				}
			} else if (newPath != null) {
				if (newPath.equals(workspaceLocation) && workspaceRoot.getProject(segment).isAccessible()) {
					newPath = new Path(segment).makeAbsolute();
				} else {
					newPath = newPath.append(segment);
				}
			}
		}
		if (newPath == null)
			return path;
		return newPath;
	}	
	/**
	 * Answers an ID which is used to distinguish entries during package
	 * fragment root computations
	 */
	public String rootID(){

		if (this.rootID == null) {
			switch(this.entryKind){
				case IEGLPathEntry.CPE_LIBRARY :
					this.rootID = "[LIB]"+this.path;  //$NON-NLS-1$
					break;
				case IEGLPathEntry.CPE_PROJECT :
					this.rootID = "[PRJ]"+this.path;  //$NON-NLS-1$
					break;
				case IEGLPathEntry.CPE_SOURCE :
					this.rootID = "[SRC]"+this.path;  //$NON-NLS-1$
					break;
				case IEGLPathEntry.CPE_VARIABLE :
					this.rootID = "[VAR]"+this.path;  //$NON-NLS-1$
					break;
				case IEGLPathEntry.CPE_CONTAINER :
					this.rootID = "[CON]"+this.path;  //$NON-NLS-1$
					break;
				default :
					this.rootID = "";  //$NON-NLS-1$
					break;
			}
		}
		return this.rootID;
	}
	public boolean isBinaryProject() {
		return isBinaryProject;
	}
	
	public void setBinaryProject(boolean isBinaryProject) {
		this.isBinaryProject = isBinaryProject;
	}
	
}
