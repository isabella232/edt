/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
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

import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.eclipse.edt.compiler.internal.core.utils.CharOperation;
import org.eclipse.edt.ide.core.internal.model.EGLModel;
import org.eclipse.edt.ide.core.internal.model.EGLModelResources;
import org.eclipse.edt.ide.core.internal.model.EGLModelStatus;
import org.eclipse.edt.ide.core.internal.model.EGLPathEntry;
import org.eclipse.edt.ide.core.internal.model.EGLProject;
import org.eclipse.edt.ide.core.internal.model.Util;
import com.ibm.icu.util.StringTokenizer;

/**
 * Provides methods for checking EGL-specific conventions such as name syntax.
 * <p>
 * This class provides static methods and constants only; it is not intended to be
 * instantiated or subclassed by clients.
 * </p>
 */
public final class EGLConventions {

	private final static char fgDot= '.';

	/**
	 * Not instantiable.
	 */
	private EGLConventions() {}

	/*
	 * Returns the index of the first argument paths which is strictly enclosing the path to check
	 */
	private static int indexOfEnclosingPath(IPath checkedPath, IPath[] paths, int pathCount) {

		for (int i = 0; i < pathCount; i++){
			if (paths[i].equals(checkedPath)) continue;
			if (paths[i].isPrefixOf(checkedPath)) return i;
		}
		return -1;
	}
	
	/*
	 * Returns the index of the first argument paths which is equal to the path to check
	 */
	private static int indexOfMatchingPath(IPath checkedPath, IPath[] paths, int pathCount) {

		for (int i = 0; i < pathCount; i++){
			if (paths[i].equals(checkedPath)) return i;
		}
		return -1;
	}

	/*
	 * Returns the index of the first argument paths which is strictly nested inside the path to check
	 */
	private static int indexOfNestedPath(IPath checkedPath, IPath[] paths, int pathCount) {

		for (int i = 0; i < pathCount; i++){
			if (checkedPath.equals(paths[i])) continue;
			if (checkedPath.isPrefixOf(paths[i])) return i;
		}
		return -1;
	}

	/**
	 * Returns whether the given package fragment root paths are considered
	 * to overlap.
	 * <p>
	 * Two root paths overlap if one is a prefix of the other, or they point to
	 * the same location. However, a JAR is allowed to be nested in a root.
	 *
	 * @param rootPath1 the first root path
	 * @param rootPath2 the second root path
	 * @return true if the given package fragment root paths are considered to overlap, false otherwise
	 * @deprecated Overlapping roots are allowed in 2.1
	 */
	public static boolean isOverlappingRoots(IPath rootPath1, IPath rootPath2) {
		if (rootPath1 == null || rootPath2 == null) {
			return false;
		}
		String extension1 = rootPath1.getFileExtension();
		String extension2 = rootPath2.getFileExtension();
		String jarExtension = "JAR"; //$NON-NLS-1$
		String zipExtension = "ZIP"; //$NON-NLS-1$
		if (extension1 != null && (extension1.equalsIgnoreCase(jarExtension) || extension1.equalsIgnoreCase(zipExtension))) {
			return false;
		} 
		if (extension2 != null && (extension2.equalsIgnoreCase(jarExtension) || extension2.equalsIgnoreCase(zipExtension))) {
			return false;
		}
		return rootPath1.isPrefixOf(rootPath2) || rootPath2.isPrefixOf(rootPath1);
	}

	/**
	 * Returns the current identifier extracted by the scanner (without unicode
	 * escapes) from the given id.
	 * Returns <code>null</code> if the id was not valid
	 */
	private static synchronized char[] scannedIdentifier(String id) {
		return id.toCharArray();
		/* TODO handle checking id
		if (id == null) {
			return null;
		}
		String trimmed = id.trim();
		if (!trimmed.equals(id)) {
			return null;
		}
		try {
			SCANNER.setSource(id.toCharArray());
			int token = SCANNER.getNextToken();
			char[] currentIdentifier;
			try {
				currentIdentifier = SCANNER.getCurrentIdentifierSource();
			} catch (ArrayIndexOutOfBoundsException e) {
				return null;
			}
			int nextToken= SCANNER.getNextToken();
			if (token == TerminalTokens.TokenNameIdentifier 
				&& nextToken == TerminalTokens.TokenNameEOF
				&& SCANNER.startPosition == SCANNER.source.length) { // to handle case where we had an ArrayIndexOutOfBoundsException 
																     // while reading the last token
				return currentIdentifier;
			} else {
				return null;
			}
		}
		catch (InvalidInputException e) {
			return null;
		}
		*/
	}

	/**
	 * Validate the given compilation unit name.
	 * A compilation unit name must obey the following rules:
	 * <ul>
	 * <li> it must not be null
	 * <li> it must include the <code>".java"</code> suffix
	 * <li> its prefix must be a valid identifier
	 * <li> it must not contain any characters or substrings that are not valid 
	 *		   on the file system on which workspace root is located.
	 * </ul>
	 * </p>
	 * @param name the name of a compilation unit
	 * @return a status object with code <code>IStatus.OK</code> if
	 *		the given name is valid as a compilation unit name, otherwise a status 
	 *		object indicating what is wrong with the name
	 */
	public static IStatus validateEGLFileName(String name) {
		if (name == null) {
			return new Status(IStatus.ERROR, EGLCore.PLUGIN_ID, -1, EGLModelResources.conventionUnitNullName, null);
		}
		if (!Util.isEGLFileName(name)) {
			return new Status(IStatus.ERROR, EGLCore.PLUGIN_ID, -1, EGLModelResources.conventionUnitNotEGLName, null);
		}
		String identifier;
		int index;
		index = name.lastIndexOf('.');
		if (index == -1) {
			return new Status(IStatus.ERROR, EGLCore.PLUGIN_ID, -1, EGLModelResources.conventionUnitNotEGLName, null);
		}
		identifier = name.substring(0, index);
		IStatus status = validateIdentifier(identifier);
		if (!status.isOK()) {
			return status;
		}
		status = ResourcesPlugin.getWorkspace().validateName(name, IResource.FILE);
		if (!status.isOK()) {
			return status;
		}
		return EGLModelStatus.VERIFIED_OK;
	}

	/**
	 * Validate the given field name.
	 * <p>
	 * Syntax of a field name corresponds to VariableDeclaratorId (JLS2 8.3).
	 * For example, <code>"x"</code>.
	 *
	 * @param name the name of a field
	 * @return a status object with code <code>IStatus.OK</code> if
	 *		the given name is valid as a field name, otherwise a status 
	 *		object indicating what is wrong with the name
	 */
	public static IStatus validateFieldName(String name) {
		return validateIdentifier(name);
	}

	/**
	 * Validate the given EGL identifier.
	 * The identifier must not have the same spelling as a EGL keyword,
	 * boolean literal (<code>"true"</code>, <code>"false"</code>), or null literal (<code>"null"</code>).
	 * See section 3.8 of the <em>EGL Language Specification, Second Edition</em> (JLS2).
	 * A valid identifier can act as a simple type name, method name or field name.
	 *
	 * @param id the EGL identifier
	 * @return a status object with code <code>IStatus.OK</code> if
	 *		the given identifier is a valid EGL identifier, otherwise a status 
	 *		object indicating what is wrong with the identifier
	 */
	public static IStatus validateIdentifier(String id) {
		if (scannedIdentifier(id) != null) {
			return EGLModelStatus.VERIFIED_OK;
		} else {
			return new Status(IStatus.ERROR, EGLCore.PLUGIN_ID, -1, EGLModelResources.bind(EGLModelResources.conventionIllegalIdentifier, id), null); //$NON-NLS-1$
		}
	}

	/**
	 * Validate the given import declaration name.
	 * <p>
	 * The name of an import corresponds to a fully qualified type name
	 * or an on-demand package name as defined by ImportDeclaration (JLS2 7.5).
	 * For example, <code>"java.EGLModelResources.*"</code> or <code>"java.EGLModelResources.Hashtable"</code>.
	 *
	 * @param name the import declaration
	 * @return a status object with code <code>IStatus.OK</code> if
	 *		the given name is valid as an import declaration, otherwise a status 
	 *		object indicating what is wrong with the name
	 */
	public static IStatus validateImportDeclaration(String name) {
		if (name == null || name.length() == 0) {
			return new Status(IStatus.ERROR, EGLCore.PLUGIN_ID, -1, EGLModelResources.conventionImportNullImport, null);
		} 
		if (name.charAt(name.length() - 1) == '*') {
			if (name.charAt(name.length() - 2) == '.') {
				return validatePackageName(name.substring(0, name.length() - 2));
			} else {
				return new Status(IStatus.ERROR, EGLCore.PLUGIN_ID, -1, EGLModelResources.conventionImportUnqualifiedImport, null);
			}
		}
		return validatePackageName(name);
	}

	/**
	 * Validate the given EGL type name, either simple or qualified.
	 * For example, <code>"java.lang.Object"</code>, or <code>"Object"</code>.
	 * <p>
	 *
	 * @param name the name of a type
	 * @return a status object with code <code>IStatus.OK</code> if
	 *		the given name is valid as a EGL type name, 
	 *      a status with code <code>IStatus.WARNING</code>
	 *		indicating why the given name is discouraged, 
	 *      otherwise a status object indicating what is wrong with 
	 *      the name
	 */
	public static IStatus validateEGLTypeName(String name) {
		if (name == null) {
			return new Status(IStatus.ERROR, EGLCore.PLUGIN_ID, -1, EGLModelResources.conventionTypeNullName, null);
		}
		String trimmed = name.trim();
		if (!name.equals(trimmed)) {
			return new Status(IStatus.ERROR, EGLCore.PLUGIN_ID, -1, EGLModelResources.conventionTypeNameWithBlanks, null);
		}
		int index = name.lastIndexOf('.');
		char[] scannedID;
		if (index == -1) {
			// simple name
			scannedID = scannedIdentifier(name);
		} else {
			// qualified name
			String pkg = name.substring(0, index).trim();
			IStatus status = validatePackageName(pkg);
			if (!status.isOK()) {
				return status;
			}
			String type = name.substring(index + 1).trim();
			scannedID = scannedIdentifier(type);
		}
	
		if (scannedID != null) {
			IStatus status = ResourcesPlugin.getWorkspace().validateName(new String(scannedID), IResource.FILE);
			if (!status.isOK()) {
				return status;
			}
			if (CharOperation.contains('$', scannedID)) {
				return new Status(IStatus.WARNING, EGLCore.PLUGIN_ID, -1, EGLModelResources.conventionTypeDollarName, null);
			}
			if ((scannedID.length > 0 && Character.isLowerCase(scannedID[0]))) {
				return new Status(IStatus.WARNING, EGLCore.PLUGIN_ID, -1, EGLModelResources.conventionTypeLowercaseName, null);
			}
			return EGLModelStatus.VERIFIED_OK;
		} else {
			return new Status(IStatus.ERROR, EGLCore.PLUGIN_ID, -1, EGLModelResources.bind(EGLModelResources.conventionTypeInvalidName, name), null);
		}
	}

	/**
	 * Validate the given method name.
	 * The special names "&lt;init&gt;" and "&lt;clinit&gt;" are not valid.
	 * <p>
	 * The syntax for a method  name is defined by Identifier
	 * of MethodDeclarator (JLS2 8.4). For example "println".
	 *
	 * @param name the name of a method
	 * @return a status object with code <code>IStatus.OK</code> if
	 *		the given name is valid as a method name, otherwise a status 
	 *		object indicating what is wrong with the name
	 */
	public static IStatus validateMethodName(String name) {

		return validateIdentifier(name);
	}

	/**
	 * Validate the given package name.
	 * <p>
	 * The syntax of a package name corresponds to PackageName as
	 * defined by PackageDeclaration (JLS2 7.4). For example, <code>"java.lang"</code>.
	 * <p>
	 * Note that the given name must be a non-empty package name (that is, attempting to
	 * validate the default package will return an error status.)
	 * Also it must not contain any characters or substrings that are not valid 
	 * on the file system on which workspace root is located.
	 *
	 * @param name the name of a package
	 * @return a status object with code <code>IStatus.OK</code> if
	 *		the given name is valid as a package name, otherwise a status 
	 *		object indicating what is wrong with the name
	 */
	public static IStatus validatePackageName(String name) {

		if (name == null) {
			return new Status(IStatus.ERROR, EGLCore.PLUGIN_ID, -1, EGLModelResources.conventionPackageNullName, null);
		}
		int length;
		if ((length = name.length()) == 0) {
			return new Status(IStatus.ERROR, EGLCore.PLUGIN_ID, -1, EGLModelResources.conventionPackageEmptyName, null);
		}
		if (name.charAt(0) == fgDot || name.charAt(length-1) == fgDot) {
			return new Status(IStatus.ERROR, EGLCore.PLUGIN_ID, -1, EGLModelResources.conventionPackageDotName, null);
		}
		if (CharOperation.isWhitespace(name.charAt(0)) || CharOperation.isWhitespace(name.charAt(name.length() - 1))) {
			return new Status(IStatus.ERROR, EGLCore.PLUGIN_ID, -1, EGLModelResources.conventionPackageNameWithBlanks, null);
		}
		int dot = 0;
		while (dot != -1 && dot < length-1) {
			if ((dot = name.indexOf(fgDot, dot+1)) != -1 && dot < length-1 && name.charAt(dot+1) == fgDot) {
				return new Status(IStatus.ERROR, EGLCore.PLUGIN_ID, -1, EGLModelResources.conventionPackageConsecutiveDotsName, null);
				}
		}
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		StringTokenizer st = new StringTokenizer(name, new String(new char[] {fgDot}));
		boolean firstToken = true;
		while (st.hasMoreTokens()) {
			String typeName = st.nextToken();
			typeName = typeName.trim(); // grammar allows spaces
			char[] scannedID = scannedIdentifier(typeName);
			if (scannedID == null) {
				return new Status(IStatus.ERROR, EGLCore.PLUGIN_ID, -1, EGLModelResources.bind(EGLModelResources.conventionIllegalIdentifier, typeName), null);
			}
			IStatus status = workspace.validateName(new String(scannedID), IResource.FOLDER);
			if (!status.isOK()) {
				return status;
			}
			if (firstToken && scannedID.length > 0 && Character.isUpperCase(scannedID[0])) {
				return new Status(IStatus.WARNING, EGLCore.PLUGIN_ID, -1, EGLModelResources.conventionPackageUppercaseName, null);
			}
			firstToken = false;
		}
		return EGLModelStatus.VERIFIED_OK;
	}
	
		
	/**
	 * Validate a given eglpath and output location for a project, using the following rules:
	 * <ul>
	 *   <li> EGLPath entries cannot collide with each other; that is, all entry paths must be unique.
	 *   <li> The project output location path cannot be null, must be absolute and located inside the project.
	 *   <li> Specific output locations (specified on source entries) can be null, if not they must be located inside the project,
	 *   <li> A project entry cannot refer to itself directly (that is, a project cannot prerequisite itself).
     *   <li> EGLPath entries or output locations cannot coincidate or be nested in each other, except for the following scenarii listed below:
	 *      <ul><li> A source folder can coincidate with its own output location, in which case this output can then contain library archives. 
	 *                     However, a specific output location cannot coincidate with any library or a distinct source folder than the one referring to it. </li> 
	 *              <li> A source/library folder can be nested in any source folder as long as the nested folder is excluded from the enclosing one. </li>
	 * 			<li> An output location can be nested in a source folder, if the source folder coincidates with the project itself. </li>
	 *      </ul>
	 * </ul>
	 * 
	 *  Note that the eglpath entries are not validated automatically. Only bound variables or containers are considered 
	 *  in the checking process (this allows to perform a consistency check on a eglpath which has references to
	 *  yet non existing projects, folders, ...).
	 *  <p>
	 *  This validation is intended to anticipate eglpath issues prior to assigning it to a project. In particular, it will automatically
	 *  be performed during the eglpath setting operation (if validation fails, the eglpath setting will not complete).
	 *  <p>
	 * @param javaProject the given java project
	 * @param eglpath a given eglpath
	 * @param outputLocation a given output location
	 * @return a status object with code <code>IStatus.OK</code> if
	 *		the given eglpath and output location are compatible, otherwise a status 
	 *		object indicating what is wrong with the eglpath or output location
	 * @since 2.0
	 */
	public static IEGLModelStatus validateEGLPath(IEGLProject javaProject, IEGLPathEntry[] rawEGLPath, IPath projectOutputLocation) {
	
		IProject project = javaProject.getProject();
		IPath projectPath= project.getFullPath();
// EGLTODO: Don't need to validate output location?	
		/* validate output location */
		if (projectOutputLocation == null) {
			return new EGLModelStatus(IEGLModelStatusConstants.NULL_PATH);
		}
		if (projectOutputLocation.isAbsolute()) {
			if (!projectPath.isPrefixOf(projectOutputLocation)) {
				return new EGLModelStatus(IEGLModelStatusConstants.PATH_OUTSIDE_PROJECT, javaProject, projectOutputLocation.toString());
			}
		} else {
			return new EGLModelStatus(IEGLModelStatusConstants.RELATIVE_PATH, projectOutputLocation);
		}
	
		boolean hasSource = false;
		boolean hasLibFolder = false;
	

		// tolerate null path, it will be reset to default
		if (rawEGLPath == null) 
			return EGLModelStatus.VERIFIED_OK;
		
		// retrieve resolved eglpath
		IEGLPathEntry[] eglpath; 
		try {
			eglpath = ((EGLProject)javaProject).getResolvedEGLPath(rawEGLPath, null /*output*/, true/*ignore pb*/, false/*no marker*/, null /*no reverse map*/);
		} catch(EGLModelException e){
			return e.getEGLModelStatus();
		}
		int length = eglpath.length; 

		int outputCount = 1;
		IPath[] outputLocations	= new IPath[length+1];
		boolean[] allowNestingInOutputLocations = new boolean[length+1];
		outputLocations[0] = projectOutputLocation;
		
		// retrieve and check output locations
		IPath potentialNestedOutput = null;
		int sourceEntryCount = 0;
		for (int i = 0 ; i < length; i++) {
			IEGLPathEntry resolvedEntry = eglpath[i];
			switch(resolvedEntry.getEntryKind()){
				case IEGLPathEntry.CPE_SOURCE :
					sourceEntryCount++;

// EGLTODO: Exclustion patterns?
//					if (resolvedEntry.getExclusionPatterns() != null && resolvedEntry.getExclusionPatterns().length > 0
//							&& EGLCore.DISABLED.equals(javaProject.getOption(EGLCore.CORE_ENABLE_CLASSPATH_EXCLUSION_PATTERNS, true))) {
//						return new EGLModelStatus(IEGLModelStatusConstants.DISABLED_CP_EXCLUSION_PATTERNS, resolvedEntry.getPath());
//					}
					IPath customOutput; 
					if ((customOutput = resolvedEntry.getOutputLocation()) != null) {

// EGLTODO: Mutliple output locations?						
//						if (EGLCore.DISABLED.equals(javaProject.getOption(EGLCore.CORE_ENABLE_CLASSPATH_MULTIPLE_OUTPUT_LOCATIONS, true))) {
//							return new EGLModelStatus(IEGLModelStatusConstants.DISABLED_CP_MULTIPLE_OUTPUT_LOCATIONS, resolvedEntry.getPath());
//						}
						// ensure custom output is in project
						if (customOutput.isAbsolute()) {
							if (!javaProject.getPath().isPrefixOf(customOutput)) {
								return new EGLModelStatus(IEGLModelStatusConstants.PATH_OUTSIDE_PROJECT, javaProject, customOutput.toString());
							}
						} else {
							return new EGLModelStatus(IEGLModelStatusConstants.RELATIVE_PATH, customOutput);
						}
						
						// ensure custom output doesn't conflict with other outputs
						int index;
						if ((index = indexOfMatchingPath(customOutput, outputLocations, outputCount)) != -1) {
							continue; // already found
						}
						if ((index = indexOfEnclosingPath(customOutput, outputLocations, outputCount)) != -1) {
							if (index == 0) {
								// custom output is nested in project's output: need to check if all source entries have a custom
								// output before complaining
								if (potentialNestedOutput == null) potentialNestedOutput = customOutput;
							} else {
								return new EGLModelStatus(IEGLModelStatusConstants.INVALID_CLASSPATH, EGLModelResources.bind(EGLModelResources.eglpathCannotNestOutputInOutput, customOutput.makeRelative().toString(), outputLocations[index].makeRelative().toString()));
							}
						}
						outputLocations[outputCount++] = resolvedEntry.getOutputLocation();
					}
			}	
		}	
		// allow custom output nesting in project's output if all source entries have a custom output
		if (potentialNestedOutput != null && sourceEntryCount > outputCount-1) {
			return new EGLModelStatus(IEGLModelStatusConstants.INVALID_CLASSPATH, EGLModelResources.bind(EGLModelResources.eglpathCannotNestOutputInOutput, potentialNestedOutput.makeRelative().toString(), outputLocations[0].makeRelative().toString()));
		}
		
		for (int i = 0 ; i < length; i++) {
			IEGLPathEntry resolvedEntry = eglpath[i];
			IPath path = resolvedEntry.getPath();
			int index;
			switch(resolvedEntry.getEntryKind()){
				
				case IEGLPathEntry.CPE_SOURCE :
					hasSource = true;
					if ((index = indexOfMatchingPath(path, outputLocations, outputCount)) != -1){
						allowNestingInOutputLocations[index] = true;
					}
					break;

// EGLTODO: Not supporting library yet - remove?
				case IEGLPathEntry.CPE_LIBRARY:
					hasLibFolder |= !Util.isArchiveFileName(path.lastSegment());
					if ((index = indexOfMatchingPath(path, outputLocations, outputCount)) != -1){
						allowNestingInOutputLocations[index] = true;
					}
					break;
			}
		}
		if (!hasSource && !hasLibFolder) { // if no source and no lib folder, then allowed
			for (int i = 0; i < outputCount; i++) allowNestingInOutputLocations[i] = true;
		}
		
		HashMap<IPath, IEGLPathEntry> pathEntry = new HashMap<IPath, IEGLPathEntry>(length);
		// check all entries
		for (int i = 0 ; i < length; i++) {
			IEGLPathEntry entry = eglpath[i];
			if (entry == null) continue;
			IPath entryPath = entry.getPath();
			int kind = entry.getEntryKind();
	
			IEGLPathEntry oldEntry = pathEntry.put(entryPath, entry);
			// complain if duplicate path
			if (oldEntry != null){
				if((oldEntry.isBinaryProject() && entry.isBinaryProject()) || ((!oldEntry.isBinaryProject() && (!entry.isBinaryProject())))) {
					return new EGLModelStatus(IEGLModelStatusConstants.NAME_COLLISION, EGLModelResources.bind(EGLModelResources.eglpathDuplicateEntryPath, entryPath.makeRelative().toString()));
				}
			} 
			// no further check if entry coincidates with project or output location
			if (entryPath.equals(projectPath)){
				// complain if self-referring project entry
				if (kind == IEGLPathEntry.CPE_PROJECT){
					return new EGLModelStatus(IEGLModelStatusConstants.INVALID_PATH, EGLModelResources.bind(EGLModelResources.eglpathCannotReferToItself, entryPath.makeRelative().toString()));
				}
				// tolerate nesting output in src if src==prj
				continue;
			}
	
			// allow nesting source entries in each other as long as the outer entry excludes the inner one
			if (kind == IEGLPathEntry.CPE_SOURCE 
					|| (kind == IEGLPathEntry.CPE_LIBRARY && !Util.isArchiveFileName(entryPath.lastSegment()))){
				for (int j = 0; j < eglpath.length; j++){
					IEGLPathEntry otherEntry = eglpath[j];
					if (otherEntry == null) continue;
					int otherKind = otherEntry.getEntryKind();
					IPath otherPath = otherEntry.getPath();
					if (entry != otherEntry 
						&& (otherKind == IEGLPathEntry.CPE_SOURCE 
								|| (otherKind == IEGLPathEntry.CPE_LIBRARY 
										&& !Util.isArchiveFileName(otherPath.lastSegment())))){
						char[][] exclusionPatterns;
						if (otherPath.isPrefixOf(entryPath) 
								&& !otherPath.equals(entryPath)
								&& !Util.isExcluded(entryPath.append("*"), exclusionPatterns = ((EGLPathEntry)otherEntry).fullExclusionPatternChars())) { //$NON-NLS-1$
									
							String exclusionPattern = entryPath.removeFirstSegments(otherPath.segmentCount()).segment(0);
							if (Util.isExcluded(entryPath, exclusionPatterns)) {
								return new EGLModelStatus(IEGLModelStatusConstants.INVALID_CLASSPATH, EGLModelResources.bind(EGLModelResources.eglpathMustEndWithSlash, exclusionPattern, entryPath.makeRelative().toString()));
							} else {
								if (otherKind == IEGLPathEntry.CPE_SOURCE) {
									exclusionPattern += '/';
									return new EGLModelStatus(IEGLModelStatusConstants.INVALID_CLASSPATH, EGLModelResources.bind(EGLModelResources.eglpathCannotNestEntryInEntry, new String[] {entryPath.makeRelative().toString(), otherEntry.getPath().makeRelative().toString(), exclusionPattern}));
								} else {
									return new EGLModelStatus(IEGLModelStatusConstants.INVALID_CLASSPATH, EGLModelResources.bind(EGLModelResources.eglpathCannotNestEntryInLibrary, new String[] {entryPath.makeRelative().toString(), otherEntry.getPath().makeRelative().toString()}));
								}
							}
						}
					}
				}
			}
			
			// prevent nesting output location inside entry
			int index;
			if ((index = indexOfNestedPath(entryPath, outputLocations, outputCount)) != -1) {
				return new EGLModelStatus(IEGLModelStatusConstants.INVALID_CLASSPATH, EGLModelResources.bind(EGLModelResources.eglpathCannotNestOutputInEntry, outputLocations[index].makeRelative().toString(), entryPath.makeRelative().toString()));
			}

			// prevent nesting entry inside output location - when distinct from project or a source folder
			if ((index = indexOfEnclosingPath(entryPath, outputLocations, outputCount)) != -1) {
				if (!allowNestingInOutputLocations[index]) {
					// allow nesting in project's output if all source entries have a custom output
					if (index != 0 || sourceEntryCount > outputCount - 1) {
						return new EGLModelStatus(IEGLModelStatusConstants.INVALID_CLASSPATH, EGLModelResources.bind(EGLModelResources.eglpathCannotNestEntryInOutput, entryPath.makeRelative().toString(), outputLocations[index].makeRelative().toString()));
					}
				}
			}
		}
		// ensure that no specific output is coincidating with another source folder (only allowed if matching current source folder)
		// 36465 - for 2.0 backward compatibility, only check specific output locations (the default can still coincidate)
		// perform one separate iteration so as to not take precedence over previously checked scenarii (in particular should
		// diagnose nesting source folder issue before this one, for example, [src]"Project/", [src]"Project/source/" and output="Project/" should
		// first complain about missing exclusion pattern
		for (int i = 0 ; i < length; i++) {
			IEGLPathEntry entry = eglpath[i];
			if (entry == null) continue;
			IPath entryPath = entry.getPath();
			int kind = entry.getEntryKind();

			if (kind == IEGLPathEntry.CPE_SOURCE) {
				IPath output = entry.getOutputLocation();
				if (output == null) continue; // 36465 - for 2.0 backward compatibility, only check specific output locations (the default can still coincidate)
				// if (output == null) output = projectOutputLocation; // if no specific output, still need to check using default output (this line would check default output)
				for (int j = 0; j < length; j++) {
					IEGLPathEntry otherEntry = eglpath[j];
					if (otherEntry == entry) continue;
					switch (otherEntry.getEntryKind()) {
						case IEGLPathEntry.CPE_SOURCE :
							if (otherEntry.getPath().equals(output)) {
								return new EGLModelStatus(IEGLModelStatusConstants.INVALID_CLASSPATH, EGLModelResources.bind(EGLModelResources.eglpathCannotUseDistinctSourceFolderAsOutput, entryPath.makeRelative().toString(), otherEntry.getPath().makeRelative().toString()));
							}
							break;
						case IEGLPathEntry.CPE_LIBRARY :
							if (otherEntry.getPath().equals(output)) {
								return new EGLModelStatus(IEGLModelStatusConstants.INVALID_CLASSPATH, EGLModelResources.bind(EGLModelResources.eglpathCannotUseLibraryAsOutput, entryPath.makeRelative().toString(), otherEntry.getPath().makeRelative().toString()));
							}
					}
				}
			}			
		}
		return EGLModelStatus.VERIFIED_OK;	
	}
	
	/**
	 * Returns a EGL model status describing the problem related to this eglpath entry if any, 
	 * a status object with code <code>IStatus.OK</code> if the entry is fine (that is, if the
	 * given eglpath entry denotes a valid element to be referenced onto a eglpath).
	 * 
	 * @param javaProject the given java project
	 * @param entry the given eglpath entry
	 * @param checkSourceAttachment a flag to determine if source attachement should be checked
	 * @return a java model status describing the problem related to this eglpath entry if any, a status object with code <code>IStatus.OK</code> if the entry is fine
	 * @since 2.0
	 */
	public static IEGLModelStatus validateEGLPathEntry(IEGLProject javaProject, IEGLPathEntry entry, boolean checkSourceAttachment){
		
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();			
		IPath path = entry.getPath();
		switch(entry.getEntryKind()){
// EGLTODO: Containers not supported - remove?	
			// container entry check
			case IEGLPathEntry.CPE_CONTAINER :
				if (path != null && path.segmentCount() >= 1){
					try {
						IEGLPathContainer container = EGLCore.getEGLPathContainer(path, javaProject);
						// container retrieval is performing validation check on container entry kinds.
						if (container == null){
							return new EGLModelStatus(IEGLModelStatusConstants.CP_CONTAINER_PATH_UNBOUND, javaProject, path);
						}
						IEGLPathEntry[] containerEntries = container.getEGLPathEntries();
						if (containerEntries != null){
							for (int i = 0, length = containerEntries.length; i < length; i++){
								IEGLPathEntry containerEntry = containerEntries[i];
								int kind = containerEntry == null ? 0 : containerEntry.getEntryKind();
								if (containerEntry == null
									|| kind == IEGLPathEntry.CPE_SOURCE
									|| kind == IEGLPathEntry.CPE_VARIABLE
									|| kind == IEGLPathEntry.CPE_CONTAINER){
										String description = container.getDescription();
										if (description == null) description = path.makeRelative().toString();
										return new EGLModelStatus(IEGLModelStatusConstants.INVALID_CP_CONTAINER_ENTRY, javaProject, path);
								}
								IEGLModelStatus containerEntryStatus = validateEGLPathEntry(javaProject, containerEntry, checkSourceAttachment);
								if (!containerEntryStatus.isOK()){
									return containerEntryStatus;
								}
							}
						}
					} catch(EGLModelException e){
						return new EGLModelStatus(e);
					}
				} else {
					return new EGLModelStatus(IEGLModelStatusConstants.INVALID_CLASSPATH, EGLModelResources.bind(EGLModelResources.eglpathIllegalContainerPath, path.makeRelative().toString()));
				}
				break;
// EGLTODO Variables not supported - remove?			
			// variable entry check
			case IEGLPathEntry.CPE_VARIABLE :
				if (path != null && path.segmentCount() >= 1){
					entry = EGLCore.getResolvedEGLPathEntry(entry);
					if (entry == null){
						return new EGLModelStatus(IEGLModelStatusConstants.CP_VARIABLE_PATH_UNBOUND, javaProject, path);
					}
					return validateEGLPathEntry(javaProject, entry, checkSourceAttachment);
				} else {
					return new EGLModelStatus(IEGLModelStatusConstants.INVALID_CLASSPATH, EGLModelResources.bind(EGLModelResources.eglpathIllegalVariablePath, path.makeRelative().toString()));
				}

			case IEGLPathEntry.CPE_LIBRARY :
				
				if (path != null && path.isAbsolute() && !path.isEmpty()) {
					if (EGLModel.getTarget(workspaceRoot, path, true) == null){
						return new EGLModelStatus(IEGLModelStatusConstants.INVALID_CLASSPATH, EGLModelResources.bind(EGLModelResources.eglpathIllegalEGLARPath, path.toString()));
					}
				}
				
				//EGLTODO: 
//				
//				if (path != null && path.isAbsolute() && !path.isEmpty()) {
//					IPath sourceAttachment = entry.getSourceAttachmentPath();
//					Object target = EGLModel.getTarget(workspaceRoot, path, true);
//					if (target instanceof IResource){
//						IResource resolvedResource = (IResource) target;
//						switch(resolvedResource.getType()){
//							case IResource.FILE :
//								if (EGLModelResources.isArchiveFileName(resolvedResource.getFileExtension())) {
//									if (checkSourceAttachment 
//										&& sourceAttachment != null
//										&& !sourceAttachment.isEmpty()
//										&& EGLModel.getTarget(workspaceRoot, sourceAttachment, true) == null){
//										return new EGLModelStatus(IEGLModelStatusConstants.INVALID_CLASSPATH, EGLModelResources.bind(EGLModelResources.eglpathunboundSourceAttachment, sourceAttachment.makeRelative().toString(), path.makeRelative().toString()));
//									}
//								}
//								break;
//							case IResource.FOLDER :	// internal binary folder
//								if (checkSourceAttachment 
//									&& sourceAttachment != null 
//									&& !sourceAttachment.isEmpty()
//									&& EGLModel.getTarget(workspaceRoot, sourceAttachment, true) == null){
//									return  new EGLModelStatus(IEGLModelStatusConstants.INVALID_CLASSPATH, EGLModelResources.bind(EGLModelResources.eglpathunboundSourceAttachment, sourceAttachment.makeRelative().toString(), path.makeRelative().toString()));
//								}
//						}
//					} else if (target instanceof File){
//						if (checkSourceAttachment 
//							&& sourceAttachment != null 
//							&& !sourceAttachment.isEmpty()
//							&& EGLModel.getTarget(workspaceRoot, sourceAttachment, true) == null){
//							return  new EGLModelStatus(IEGLModelStatusConstants.INVALID_CLASSPATH, EGLModelResources.bind(EGLModelResources.eglpathunboundSourceAttachment, sourceAttachment.toString(), path.makeRelative().toString()));
//						}
//					} else {
//						return new EGLModelStatus(IEGLModelStatusConstants.INVALID_CLASSPATH, EGLModelResources.bind(EGLModelResources.eglpathunboundLibrary, path.makeRelative().toString()));
//					}
//				} else {
//					return new EGLModelStatus(IEGLModelStatusConstants.INVALID_CLASSPATH, EGLModelResources.bind(EGLModelResources.eglpathillegalLibraryPath, path.makeRelative().toString()));
//				}
				break;

			// project entry check
			case IEGLPathEntry.CPE_PROJECT :
				if (path != null && path.isAbsolute() && !path.isEmpty()) {
					IProject project = workspaceRoot.getProject(path.segment(0));
					try {
						if (!project.exists() || !project.hasNature(EGLCore.NATURE_ID)){
							return new EGLModelStatus(IEGLModelStatusConstants.INVALID_CLASSPATH, EGLModelResources.bind(EGLModelResources.eglpathUnboundProject, path.makeRelative().segment(0).toString()));
						}
						if (!project.isOpen()){
							return new EGLModelStatus(IEGLModelStatusConstants.INVALID_CLASSPATH, EGLModelResources.bind(EGLModelResources.eglpathClosedProject, path.segment(0).toString()));
						}
					} catch (CoreException e){
						return new EGLModelStatus(IEGLModelStatusConstants.INVALID_CLASSPATH, EGLModelResources.bind(EGLModelResources.eglpathUnboundProject, path.segment(0).toString()));
					}
				} else {
					return new EGLModelStatus(IEGLModelStatusConstants.INVALID_CLASSPATH, EGLModelResources.bind(EGLModelResources.eglpathIllegalProjectPath, path.segment(0).toString()));
				}
				break;

			// project source folder
			case IEGLPathEntry.CPE_SOURCE :
// EGLTODO: Exclusion patterns?
//				if (entry.getExclusionPatterns() != null 
//						&& entry.getExclusionPatterns().length > 0
//						&& EGLCore.DISABLED.equals(javaProject.getOption(EGLCore.CORE_ENABLE_CLASSPATH_EXCLUSION_PATTERNS, true))) {
//					return new EGLModelStatus(IEGLModelStatusConstants.DISABLED_CP_EXCLUSION_PATTERNS, path);
//				}
				
// EGLTODO: Multiple output locations?
//				if (entry.getOutputLocation() != null && EGLCore.DISABLED.equals(javaProject.getOption(EGLCore.CORE_ENABLE_CLASSPATH_MULTIPLE_OUTPUT_LOCATIONS, true))) {
//					return new EGLModelStatus(IEGLModelStatusConstants.DISABLED_CP_MULTIPLE_OUTPUT_LOCATIONS, path);
//				}
				if (path != null && path.isAbsolute() && !path.isEmpty()) {
					IPath projectPath= javaProject.getProject().getFullPath();
					if (!projectPath.isPrefixOf(path) || EGLModel.getTarget(workspaceRoot, path, true) == null){
						return new EGLModelStatus(IEGLModelStatusConstants.INVALID_CLASSPATH, EGLModelResources.bind(EGLModelResources.eglpathUnboundSourceFolder, path.makeRelative().toString()));
					}
				} else {
					return new EGLModelStatus(IEGLModelStatusConstants.INVALID_CLASSPATH, EGLModelResources.bind(EGLModelResources.eglpathIllegalSourceFolderPath, path.makeRelative().toString()));
				}
				break;
				
		}
		return EGLModelStatus.VERIFIED_OK;		
	}
}
