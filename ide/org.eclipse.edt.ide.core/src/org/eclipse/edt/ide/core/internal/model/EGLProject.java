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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.EDTCorePreferenceConstants;
import org.eclipse.edt.ide.core.internal.model.util.EGLProjectFileUtilityLocator;
import org.eclipse.edt.ide.core.internal.model.util.IEGLProjectFileUtility;
import org.eclipse.edt.ide.core.internal.model.util.ObjectVector;
import org.eclipse.edt.ide.core.model.EGLConventions;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLModelMarker;
import org.eclipse.edt.ide.core.model.IEGLModelStatus;
import org.eclipse.edt.ide.core.model.IEGLModelStatusConstants;
import org.eclipse.edt.ide.core.model.IEGLPathContainer;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.model.IPart;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

//import com.ibm.etools.egl.internal.EGLIncompleteBuildPathSetting;
//import com.ibm.etools.egl.internal.EGLVAGCompatibilitySetting;


/**
 * @author twilson created Jul 25, 2003
 */
public class EGLProject extends Openable implements IEGLProject, IProjectNature {

	/**
	 * Whether the underlying file system is case sensitive.
	 */
	protected static final boolean IS_CASE_SENSITIVE = !new File("Temp").equals(new File("temp")); //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * An empty array of strings indicating that a project doesn't have any
	 * prerequesite projects.
	 */
	protected static final String[] NO_PREREQUISITES = new String[0];

	/**
	 * The platform project this <code>IEGLProject</code> is based on
	 */
	protected IProject fProject;

	/**
	 * Name of file containing project EGLPath
	 */
	public static final String EGLPATH_FILENAME = ".eglPath"; //$NON-NLS-1$
	
	/**
	 * Name of file containing custom project preferences
	 */
	public static final String PREF_FILENAME = ".eprefs"; //$NON-NLS-1$
	
	public static final String BUILD_STATE_FILE_EXTENSION = "bs"; //$NON-NLS-1$

	/**
	 * Value of the project's raw EGLPath if the .EGLPath file contains invalid
	 * entries.
	 */
	public static final IEGLPathEntry[] INVALID_EGLPATH = new IEGLPathEntry[0];

	private static final String CUSTOM_DEFAULT_OPTION_VALUE = "#\r\n\r#custom-non-empty-default-value#\r\n\r#"; //$NON-NLS-1$
	/**
	 * Adds a builder to the build spec for the given project.
	 * bInsert2First - means insert the builder at the beginning
	 */
	protected void addToBuildSpec(String builderID, boolean bInsert2First) throws CoreException {

		IProjectDescription description = getProject().getDescription();
		ICommand eglCommand = getCommand(description, builderID);

		if (eglCommand == null) {

			// Add a EGL command to the build spec
			ICommand command = description.newCommand();
			command.setBuilderName(builderID);
			setEGLCommand(description, command, builderID, bInsert2First);
		}
	}
	
	/**
	 * Returns a canonicalized path from the given external path. Note that the
	 * return path contains the same number of segments and it contains a
	 * device only if the given path contained one.
	 * 
	 * @see java.io.File for the definition of a canonicalized path
	 */
	public static IPath canonicalizedPath(IPath externalPath) {

		if (externalPath == null)
			return null;

		if (IS_CASE_SENSITIVE) {
			return externalPath;
		}

		// if not external path, return original path
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (workspace == null)
			return externalPath; // protection during shutdown (30487)
		if (workspace.getRoot().findMember(externalPath) != null) {
			return externalPath;
		}

		IPath canonicalPath = null;
		try {
			canonicalPath = new Path(new File(externalPath.toOSString()).getCanonicalPath());
		} catch (IOException e) {
			// default to original path
			return externalPath;
		}

		IPath result;
		int canonicalLength = canonicalPath.segmentCount();
		if (canonicalLength == 0) {
			// the java.io.File canonicalization failed
			return externalPath;
		} else if (externalPath.isAbsolute()) {
			result = canonicalPath;
		} else {
			// if path is relative, remove the first segments that were added
			// by the java.io.File canonicalization
			// e.g. 'lib/classes.zip' was converted to
			// 'd:/myfolder/lib/classes.zip'
			int externalLength = externalPath.segmentCount();
			if (canonicalLength >= externalLength) {
				result = canonicalPath.removeFirstSegments(canonicalLength - externalLength);
			} else {
				return externalPath;
			}
		}

		// keep device only if it was specified (this is because
		// File.getCanonicalPath() converts '/lib/classed.zip' to
		// 'd:/lib/classes/zip')
		if (externalPath.getDevice() == null) {
			result = result.setDevice(null);
		}
		return result;
	}
	/**
	 * Configure the project with Java nature.
	 */
	public void configure() throws CoreException {

		// register EGL builders. add them to the top of the build order, with the gen builder second
		addToBuildSpec(EDTCoreIDEPlugin.GENERATION_BUILDER_ID, true);
		addToBuildSpec(EDTCoreIDEPlugin.BUILDER_ID, true);
	}
	
	/**
	 * Removes the Java nature from the project.
	 */
	public void deconfigure() throws CoreException {

		// deregister EGL builder
		removeFromBuildSpec(EGLCore.BUILDER_ID);
	}
	/**
	 * Constructor needed for <code>IProject.getNature()</code> and <code>IProject.addNature()</code>.
	 * 
	 * @see #setProject
	 */
	public EGLProject() {
		super(EGL_PROJECT, null, null);
	}
	/**
	 * @param type
	 * @param parent
	 * @param name
	 */
	public EGLProject(IProject project, IEGLElement parent) {
		super(EGL_PROJECT, parent, project.getName());
		fProject = project;
	}

	/**
	 * Internal computation of an expanded eglpath. It will eliminate
	 * duplicates, and produce copies of exported eglpath entries to avoid
	 * possible side-effects ever after.
	 */
	private void computeExpandedEGLPath(
		EGLProject initialProject,
		boolean ignoreUnresolvedVariable,
		boolean generateMarkerOnError,
		HashSet visitedProjects,
		ObjectVector accumulatedEntries)
		throws EGLModelException {

		if (visitedProjects.contains(this)) {
			return; // break cycles if any
		}
		visitedProjects.add(this);

		if (generateMarkerOnError && !this.equals(initialProject)) {
			generateMarkerOnError = false;
		}
		IEGLPathEntry[] immediateEGLPath = getResolvedEGLPath(ignoreUnresolvedVariable, generateMarkerOnError);

		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		for (int i = 0, length = immediateEGLPath.length; i < length; i++) {
			IEGLPathEntry entry = immediateEGLPath[i];

			boolean isInitialProject = this.equals(initialProject);
			//exported eglar of referencing project should not be added
			if (isInitialProject || (entry.getEntryKind() != EGLPathEntry.CPE_LIBRARY && entry.isExported())) {
				accumulatedEntries.add(entry);

				// recurse in project to get all its indirect exports (only
				// consider exported entries from there on)
				if (entry.getEntryKind() == EGLPathEntry.CPE_PROJECT) {
					IResource member = workspaceRoot.findMember(entry.getPath());
					if (member != null && member.getType() == IResource.PROJECT) {
						// double check if bound to project (23977)
						IProject projRsc = (IProject) member;
						if (EGLProject.hasEGLNature(projRsc)) {
							EGLProject project = (EGLProject) EGLCore.create(projRsc);
							project.computeExpandedEGLPath(
								initialProject,
								ignoreUnresolvedVariable,
								generateMarkerOnError,
								visitedProjects,
								accumulatedEntries);
						}
					}
				}
			}
		}
	}

	/**
	 * Returns (local/all) the package fragment roots identified by the given
	 * project's eglpath. Note: this follows project eglpath references to find
	 * required project contributions, eliminating duplicates silently. Only
	 * works with resolved entries
	 */
	public IPackageFragmentRoot[] computePackageFragmentRoots(IEGLPathEntry[] resolvedEGLPath, boolean retrieveExportedRoots)
		throws EGLModelException {

		ObjectVector accumulatedRoots = new ObjectVector();
		computePackageFragmentRoots(resolvedEGLPath, accumulatedRoots, new HashSet(5),
		// rootIDs
		true, // inside original project
		true, // check existency
		retrieveExportedRoots);
		IPackageFragmentRoot[] rootArray = new IPackageFragmentRoot[accumulatedRoots.size()];
		accumulatedRoots.copyInto(rootArray);
		return rootArray;
	}

	/**
	 * Computes the package fragment roots identified by the given entry. Only
	 * works with resolved entry
	 */
	public IPackageFragmentRoot[] computePackageFragmentRoots(IEGLPathEntry resolvedEntry) {
		try {
			return computePackageFragmentRoots(new IEGLPathEntry[] { resolvedEntry }, false // don't
																							// retrieve
																							// exported
																							// roots
			);
		} catch (EGLModelException e) {
			return new IPackageFragmentRoot[] {
			};
		}
	}

	/**
	 * Returns the package fragment roots identified by the given entry. In
	 * case it refers to a project, it will follow its eglpath so as to find
	 * exported roots as well. Only works with resolved entry
	 */
	public void computePackageFragmentRoots(
		IEGLPathEntry resolvedEntry,
		ObjectVector accumulatedRoots,
		HashSet rootIDs,
		boolean insideOriginalProject,
		boolean checkExistency,
		boolean retrieveExportedRoots)
		throws EGLModelException {

		String rootID = ((EGLPathEntry) resolvedEntry).rootID();
		if (rootIDs.contains(rootID))
			return;

		IPath projectPath = getProject().getFullPath();
		IPath entryPath = resolvedEntry.getPath();
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

		switch (resolvedEntry.getEntryKind()) {

			// source folder
			case IEGLPathEntry.CPE_SOURCE :

				if (projectPath.isPrefixOf(entryPath)) {
					if (checkExistency) {
						Object target = EGLModel.getTarget(workspaceRoot, entryPath, checkExistency);
						if (target == null)
							return;

						if (target instanceof IFolder || target instanceof IProject) {
							accumulatedRoots.add(getPackageFragmentRoot((IResource) target));
							rootIDs.add(rootID);
						}
					} else {
						IPackageFragmentRoot root = getFolderPackageFragmentRoot(entryPath);
						if (root != null) {
							accumulatedRoots.add(root);
							rootIDs.add(rootID);
						}
					}
				}
				break;
			case IEGLPathEntry.CPE_LIBRARY :
//TODO			if (referringEntry != null  && !resolvedEntry.isExported())
//					return;
				Object target = EGLModel.getTarget(workspaceRoot, entryPath, checkExistency);
				if (target == null)
					return;
				IPackageFragmentRoot root = null;
				if (target instanceof IResource){
					// internal target
					root = getPackageFragmentRoot((IResource) target);
					if(root instanceof EglarPackageFragmentRoot) {
						((EglarPackageFragmentRoot)root).setBinaryProject(resolvedEntry.isBinaryProject());
					}
				} else if (target instanceof File) {
					// external target
					if (EGLModel.isFile(target)) {
						root = new EglarPackageFragmentRoot(entryPath, this);
						((EglarPackageFragmentRoot)root).setBinaryProject(resolvedEntry.isBinaryProject());
					} else if (((File) target).isDirectory()) {
//						TODO IPackageFragmentRoot root = new ExternalPackageFragmentRoot(entryPath, this);
					}
				}
				if (root != null) {
					accumulatedRoots.add(root);
					rootIDs.add(rootID);
				}
				break;
				// recurse into required project
			case IEGLPathEntry.CPE_PROJECT :

				if (!retrieveExportedRoots)
					return;
				if (!insideOriginalProject && !resolvedEntry.isExported())
					return;

				IResource member = workspaceRoot.findMember(entryPath);
				if (member != null && member.getType() == IResource.PROJECT) {
					// double check if bound to project (23977)
					IProject requiredProjectRsc = (IProject) member;
					if (EGLProject.hasEGLNature(requiredProjectRsc)) {
						// special builder binary output
						rootIDs.add(rootID);
						EGLProject requiredProject = (EGLProject) EGLCore.create(requiredProjectRsc);
						requiredProject.computePackageFragmentRoots(
							requiredProject.getResolvedEGLPath(true),
							accumulatedRoots,
							rootIDs,
							false,
							checkExistency,
							retrieveExportedRoots);
					}
					break;
				}
		}
	}

	/**
	 * Returns (local/all) the package fragment roots identified by the given
	 * project's eglpath. Note: this follows project eglpath references to find
	 * required project contributions, eliminating duplicates silently. Only
	 * works with resolved entries
	 */
	public void computePackageFragmentRoots(
		IEGLPathEntry[] resolvedEGLPath,
		ObjectVector accumulatedRoots,
		HashSet rootIDs,
		boolean insideOriginalProject,
		boolean checkExistency,
		boolean retrieveExportedRoots)
		throws EGLModelException {

		if (insideOriginalProject) {
			rootIDs.add(rootID());
		}
		for (int i = 0, length = resolvedEGLPath.length; i < length; i++) {
			computePackageFragmentRoots(
				resolvedEGLPath[i],
				accumulatedRoots,
				rootIDs,
				insideOriginalProject,
				checkExistency,
				retrieveExportedRoots);
		}
	}
	/**
	 * Record a new marker denoting a eglpath problem
	 */
	IMarker createEGLPathProblemMarker(IEGLModelStatus status) {

		IMarker marker = null;
		int severity;
		String[] arguments = new String[0];
		boolean isCycleProblem = false, isEGLPathFileFormatProblem = false;
		switch (status.getCode()) {

			case IEGLModelStatusConstants.EGLPATH_CYCLE :
				isCycleProblem = true;
				severity = IMarker.SEVERITY_ERROR;
				break;

			case IEGLModelStatusConstants.INVALID_EGLPATH_FILE_FORMAT :
				isEGLPathFileFormatProblem = true;
				severity = IMarker.SEVERITY_ERROR;
				break;

			default :
				IPath path = status.getPath();
				if (path != null){
					arguments = new String[] { path.toString()};
				}
//TODO EDT Support incomplete build path setting				
//				switch(EGLIncompleteBuildPathSetting.getIncompleteBuildPathSetting()){
//					case EGLIncompleteBuildPathSetting._INCOMPLETE_BUILD_PATH_ERROR: 
//						severity = IMarker.SEVERITY_ERROR;
//						break;
//					case EGLIncompleteBuildPathSetting._INCOMPLETE_BUILD_PATH_WARNING:
//						severity = IMarker.SEVERITY_WARNING;
//						break;
//					default:
						severity = IMarker.SEVERITY_ERROR;
//						break;
//				}
				
				/*
				 * TODO handle configurable options later if
				 * (EGLCore.ERROR.equals(getOption(EGLCore.CORE_INCOMPLETE_EGLPATH,
				 * true))) { severity = IMarker.SEVERITY_ERROR; } else {
				 * severity = IMarker.SEVERITY_WARNING; }
				 */
				break;
		}

		try {
			marker = getProject().createMarker(IEGLModelMarker.BUILDPATH_PROBLEM_MARKER);
			marker
				.setAttributes(
					new String[] {
						IMarker.MESSAGE,
						IMarker.SEVERITY,
						IMarker.LOCATION,
						IEGLModelMarker.CYCLE_DETECTED,
						IEGLModelMarker.EGLPATH_FILE_FORMAT,
						IEGLModelMarker.ID,
						IEGLModelMarker.ARGUMENTS,
						},
					new Object[] { status.getMessage(), new Integer(severity), EGLModelResources.eglpathBuildPath,
				//$NON-NLS-1$
				isCycleProblem ? "true" : "false", //$NON-NLS-1$ //$NON-NLS-2$
				isEGLPathFileFormatProblem ? "true" : "false", //$NON-NLS-1$ //$NON-NLS-2$
				new Integer(status.getCode()), Util.getProblemArgumentsForMarker(arguments), });
		} catch (CoreException e) {
		}
		return marker;
	}
	/**
	 * Record a new marker denoting a eglpath problem
	 */
	IMarker createEGLBDProblemMarker(IEGLModelStatus status) {
		return null;
	}
	/**
	 * Returns a new element info for this element.
	 */
	protected OpenableElementInfo createElementInfo() {

		return new EGLProjectElementInfo();
	}

	/*
	 * Returns whether the given resource is accessible through the children or
	 * the non-EGL resources of this project. Returns true if the resource is
	 * not in the project. Assumes that the resource is a folder or a file.
	 */
	public boolean contains(IResource resource) {

		IEGLPathEntry[] eglpath;
		IPath output;
		try {
			eglpath = getResolvedEGLPath(true);
			output = getOutputLocation();
		} catch (EGLModelException e) {
			return false;
		}

		IPath fullPath = resource.getFullPath();
		IPath innerMostOutput = output.isPrefixOf(fullPath) ? output : null;
		IEGLPathEntry innerMostEntry = null;
		for (int j = 0, cpLength = eglpath.length; j < cpLength; j++) {
			IEGLPathEntry entry = eglpath[j];

			IPath entryPath = entry.getPath();
			if ((innerMostEntry == null || innerMostEntry.getPath().isPrefixOf(entryPath)) && entryPath.isPrefixOf(fullPath)) {
				innerMostEntry = entry;
			}
			IPath entryOutput = eglpath[j].getOutputLocation();
			if (entryOutput != null && entryOutput.isPrefixOf(fullPath)) {
				innerMostOutput = entryOutput;
			}
		}
		if (innerMostEntry != null) {
			// special case prj==src and nested output location
			if (innerMostOutput != null
				&& innerMostOutput.segmentCount() > 1 // output isn't project
				&& innerMostEntry.getPath().segmentCount() == 1) {
				// 1 segment must be project name
				return false;
			}
			if (resource instanceof IFolder) {
				// folders are always included in src/lib entries
				return true;
			}
		}
		if (innerMostOutput != null) {
			return false;
		}
		return true;
	}

	/**
	 * Returns true if given name exists in receiver's namespace
	 * 
	 * @param packageName
	 * @return
	 */
	public boolean containsPackage(String packageName) {
		try {
			return getNameLookup().findPackageFragments(packageName, false) != null;
		} catch (EGLModelException e) {
			return false;
		}
	}
	/**
	 * Returns true if this handle represents the same EGL project as the given
	 * handle. Two handles represent the same project if they are identical or
	 * if they represent a project with the same underlying resource and
	 * occurrence counts.
	 * 
	 * @see EGLElement#equals
	 */
	public boolean equals(Object o) {

		if (this == o)
			return true;

		if (!(o instanceof EGLProject))
			return false;

		EGLProject other = (EGLProject) o;
		return getProject().equals(other.getProject()) && fOccurrenceCount == other.fOccurrenceCount;
	}

	public boolean exists() {
		if (!hasEGLNature(fProject))
			return false;
		return super.exists();
	}
	/*
	 * Returns the cycle marker associated with this project or null if none.
	 */
	public IMarker getCycleMarker() {
		try {
			IProject project = getProject();
			if (project.exists()) {
				IMarker[] markers = project.findMarkers(IEGLModelMarker.BUILDPATH_PROBLEM_MARKER, false, IResource.DEPTH_ZERO);
				for (int i = 0, length = markers.length; i < length; i++) {
					IMarker marker = markers[i];
					String cycleAttr = (String) marker.getAttribute(IEGLModelMarker.CYCLE_DETECTED);
					if (cycleAttr != null && cycleAttr.equals("true")) { //$NON-NLS-1$
						return marker;
					}
				}
			}
		} catch (CoreException e) {
		}
		return null;
	}

	/**
	 * Reads and decode an XML classpath string
	 */
	protected IEGLPathEntry[] decodeEGLPath(String xmlPath, boolean createMarker, boolean logProblems) {

		ArrayList paths = new ArrayList();
		IEGLPathEntry defaultOutput = null;
		try {
			if (xmlPath == null)
				return null;
			StringReader reader = new StringReader(xmlPath);
			Element cpElement;

			try {
				DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				cpElement = parser.parse(new InputSource(reader)).getDocumentElement();
			} catch (SAXException e) {
				throw new IOException(EGLModelResources.fileBadFormat);
			} catch (ParserConfigurationException e) {
				throw new IOException(EGLModelResources.fileBadFormat);
			} finally {
				reader.close();
			}

			if (!cpElement.getNodeName().equalsIgnoreCase("eglpath")) { //$NON-NLS-1$
				throw new IOException(EGLModelResources.fileBadFormat);
			}
			NodeList list = cpElement.getElementsByTagName("eglpathentry"); //$NON-NLS-1$
			int length = list.getLength();

			for (int i = 0; i < length; ++i) {
				Node node = list.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					IEGLPathEntry entry = EGLPathEntry.elementDecode((Element) node, getProject().getFullPath(), getElementName());
					if (entry != null) {
						if (entry.getContentKind() == EGLPathEntry.K_OUTPUT) {
							defaultOutput = entry; // separate output
						} else {
							paths.add(entry);
						}
					}
				}
			}
		} catch (IOException e) {
			// bad format
			if (createMarker && this.getProject().isAccessible()) {
				this.createEGLPathProblemMarker(new EGLModelStatus(IEGLModelStatusConstants.INVALID_EGLPATH_FILE_FORMAT, EGLModelResources.bind(EGLModelResources.eglpathXmlFormatError, this.getElementName(), e.getMessage())));
			}
			if (logProblems) {
				Util.log(e, "Exception while retrieving " + this.getPath() //$NON-NLS-1$
				+"/.eglpath, will mark eglpath as invalid"); //$NON-NLS-1$
			}
			return INVALID_EGLPATH;
		} catch (Exception e) {
			// failed creating CP entries from file
			if (createMarker && this.getProject().isAccessible()) {
				this.createEGLPathProblemMarker(new EGLModelStatus(IEGLModelStatusConstants.INVALID_EGLPATH_FILE_FORMAT, EGLModelResources.bind(EGLModelResources.eglpathIllegalEntryInEGLpathFile, this.getElementName(), e.getMessage())));
			}
			if (logProblems) {
				Util.log(e, "Exception while retrieving " + this.getPath() //$NON-NLS-1$
				+"/.eglpath, will mark eglpath as invalid"); //$NON-NLS-1$
			}
			return INVALID_EGLPATH;
		}
		int pathSize = paths.size();
		if (pathSize > 0 || defaultOutput != null) {
			IEGLPathEntry[] entries = new IEGLPathEntry[pathSize + (defaultOutput == null ? 0 : 1)];
			paths.toArray(entries);
			if (defaultOutput != null)
				entries[pathSize] = defaultOutput;
			// ensure output is last item
			return entries;
		} else {
			return null;
		}
	}

	/**
	 * Returns a default egl path. This is the root of the project
	 */
	protected IEGLPathEntry[] defaultEGLPath() throws EGLModelException {

		return new IEGLPathEntry[] { EGLCore.newSourceEntry(getProject().getFullPath())};
	}

	/**
	 * Returns a default output location. This is the project bin folder
	 */
	protected IPath defaultOutputLocation() throws EGLModelException {
		return getProject().getFullPath().append(
				EDTCoreIDEPlugin.getPlugin().getPreferenceStore().getString( 
				EDTCorePreferenceConstants.EGL_OUTPUT_FOLDER ));
	}

	/**
	 * Returns the XML String encoding of the class path.
	 */
	protected String encodeEGLPath(IEGLPathEntry[] eglpath, IPath outputLocation, boolean indent) throws EGLModelException {

		try {
			ByteArrayOutputStream s = new ByteArrayOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(s, "UTF-8"); //$NON-NLS-1$
			XMLWriter xmlWriter = new XMLWriter(writer);
			
			xmlWriter.startTag("eglpath", indent); //$NON-NLS-1$
			for (int i = 0; i < eglpath.length; ++i) {
				((EGLPathEntry)eglpath[i]).elementEncode(xmlWriter, getProject().getFullPath(), indent, true);
			}
	
			if (outputLocation != null) {
				outputLocation = outputLocation.removeFirstSegments(1);
				outputLocation = outputLocation.makeRelative();
				HashMap parameters = new HashMap();
				parameters.put("kind", EGLPathEntry.kindToString(EGLPathEntry.K_OUTPUT));//$NON-NLS-1$
				parameters.put("path", String.valueOf(outputLocation));//$NON-NLS-1$
				xmlWriter.printTag("eglpathentry", parameters, indent, true, true);//$NON-NLS-1$
			}
	
			xmlWriter.endTag("eglpath", indent);//$NON-NLS-1$
			writer.flush();
			writer.close();
			return s.toString("UTF8");//$NON-NLS-1$
		} catch (IOException e) {
			throw new EGLModelException(e, IEGLModelStatusConstants.IO_EXCEPTION);
		}
	}

	/**
	 * Remove all markers denoting eglpath problems
	 */
	protected void flushEGLPathProblemMarkers(boolean flushCycleMarkers, boolean flushEGLPathFormatMarkers) {
		try {
			IProject project = getProject();
			if (project.exists()) {
				IMarker[] markers = project.findMarkers(IEGLModelMarker.BUILDPATH_PROBLEM_MARKER, false, IResource.DEPTH_ZERO);
				for (int i = 0, length = markers.length; i < length; i++) {
					IMarker marker = markers[i];
					if (flushCycleMarkers && flushEGLPathFormatMarkers) {
						marker.delete();
					} else {
						String cycleAttr = (String) marker.getAttribute(IEGLModelMarker.CYCLE_DETECTED);
						String eglpathFileFormatAttr = (String) marker.getAttribute(IEGLModelMarker.EGLPATH_FILE_FORMAT);
						if ((flushCycleMarkers == (cycleAttr != null && cycleAttr.equals("true"))) //$NON-NLS-1$
						&& (flushEGLPathFormatMarkers == (eglpathFileFormatAttr != null && eglpathFileFormatAttr.equals("true")))) { //$NON-NLS-1$
							marker.delete();
						}
					}
				}
			}
		} catch (CoreException e) {
		}
	}
	/**
	 * @see Openable
	 */
	protected boolean generateInfos(OpenableElementInfo info, IProgressMonitor pm, Map newElements, IResource underlyingResource)
		throws EGLModelException {

		boolean validInfo = false;
		try {
			if (getProject().isOpen()) {
				// put the info now, because computing the roots requires it
				EGLModelManager.getEGLModelManager().putInfo(this, info);

				// compute the pkg fragment roots
				updatePackageFragmentRoots();

				// remember the timestamps of external libraries the first time
				// they are looked up
				IEGLPathEntry[] resolvedEGLPath = getResolvedEGLPath(true /*
																		   * ignore
																		   * unresolved
																		   * variable
																		   */
				);
				for (int i = 0, length = resolvedEGLPath.length; i < length; i++) {
					IEGLPathEntry entry = resolvedEGLPath[i];
					if (entry.getEntryKind() == IEGLPathEntry.CPE_LIBRARY) {
						IPath path = entry.getPath();
						Object target = EGLModel.getTarget(ResourcesPlugin.getWorkspace().getRoot(), path, true);
						if (target instanceof java.io.File) {
							Map externalTimeStamps = EGLModelManager.getEGLModelManager().deltaProcessor.externalTimeStamps;
							if (externalTimeStamps.get(path) == null) {
								long timestamp = DeltaProcessor.getTimeStamp((java.io.File) target);
								externalTimeStamps.put(path, new Long(timestamp));
							}
						}
					}
				}

				// only valid if reaches here
				validInfo = true;
			}
		} finally {
			if (!validInfo)
				EGLModelManager.getEGLModelManager().removeInfo(this);
		}
		return validInfo;
	}

	/**
	 * @see IEGLProject
	 */
	public IEGLElement findElement(IPath path) throws EGLModelException {

		if (path == null || path.isAbsolute()) {
			throw new EGLModelException(new EGLModelStatus(IEGLModelStatusConstants.INVALID_PATH, path));
		}
		try {

			String extension = path.getFileExtension();
			if (extension == null) {
				String packageName = path.toString().replace(IPath.SEPARATOR, '.');

				IPackageFragment[] pkgFragments = getNameLookup().findPackageFragments(packageName, false);
				if (pkgFragments == null) {
					return null;

				} else {
					// try to return one that is a child of this project
					for (int i = 0, length = pkgFragments.length; i < length; i++) {

						IPackageFragment pkgFragment = pkgFragments[i];
						if (this.equals(pkgFragment.getParent().getParent())) {
							return pkgFragment;
						}
					}
					// default to the first one
					return pkgFragments[0];
				}
			} else if (extension.equalsIgnoreCase("egl") //$NON-NLS-1$
			|| extension.equalsIgnoreCase("eglbld")) { //$NON-NLS-1$
				IPath packagePath = path.removeLastSegments(1);
				String packageName = packagePath.toString().replace(IPath.SEPARATOR, '.');
				String typeName = path.lastSegment();
				typeName = typeName.substring(0, typeName.length() - extension.length() - 1);
				String qualifiedName = null;
				if (packageName.length() > 0) {
					qualifiedName = packageName + "." + typeName; //$NON-NLS-1$
				} else {
					qualifiedName = typeName;
				}
				IPart part = getNameLookup().findPart(qualifiedName, false, NameLookup.ACCEPT_PARTS);
				if (part != null) {
					return part.getParent();
				} else {
					return null;
				}
			} else {
				// unsupported extension
				return null;
			}
		} catch (EGLModelException e) {
			if (e.getStatus().getCode() == IEGLModelStatusConstants.ELEMENT_DOES_NOT_EXIST) {
				return null;
			} else {
				throw e;
			}
		}
	}

	/**
	 * @see IEGLProject
	 */
	public IPackageFragment findPackageFragment(IPath path) throws EGLModelException {

		return findPackageFragment0(EGLProject.canonicalizedPath(path));
	}

	/**
	 * non path canonicalizing version
	 */
	public IPackageFragment findPackageFragment0(IPath path) throws EGLModelException {

		return getNameLookup().findPackageFragment(path);
	}

	/**
	 * @see IEGLProject
	 */
	public IPackageFragmentRoot findPackageFragmentRoot(IPath path) throws EGLModelException {

		return findPackageFragmentRoot0(EGLProject.canonicalizedPath(path));
	}

	/**
	 * no path canonicalization
	 */
	public IPackageFragmentRoot findPackageFragmentRoot0(IPath path) throws EGLModelException {

		IPackageFragmentRoot[] allRoots = this.getAllPackageFragmentRoots();
		if (!path.isAbsolute()) {
			throw new IllegalArgumentException(EGLModelResources.pathMustBeAbsolute);
		}
		for (int i = 0; i < allRoots.length; i++) {
			IPackageFragmentRoot eglpathRoot = allRoots[i];
			if (eglpathRoot.getPath().equals(path)) {
				return eglpathRoot;
			}
		}
		return null;
	}
	/**
	 * @see IEGLProject
	 */
	public IPackageFragmentRoot[] findPackageFragmentRoots(IEGLPathEntry entry) {
		try {
			IEGLPathEntry[] eglpath = this.getRawEGLPath();
			for (int i = 0, length = eglpath.length; i < length; i++) {
				if (eglpath[i].equals(entry)) {
					// entry may need to be resolved
					return computePackageFragmentRoots(getResolvedEGLPath(
						new IEGLPathEntry[] { entry },
						null,
						true,
						false,
						null /* no reverse map */
					), false); // don't retrieve exported roots
				}
			}
		} catch (EGLModelException e) {
		}
		return new IPackageFragmentRoot[] {
		};
	}
	/**
	 * @see IEGLProject#findPart(String)
	 */
	public IPart findPart(String fullyQualifiedName) throws EGLModelException {
		IPart type = this.getNameLookup().findPart(fullyQualifiedName, false, NameLookup.ACCEPT_PARTS);
		if (type == null) {
			// try to find enclosing type
			int lastDot = fullyQualifiedName.lastIndexOf('.');
			if (lastDot == -1)
				return null;
			type = this.findPart(fullyQualifiedName.substring(0, lastDot));
			if (type != null) {
				type = type.getPart(fullyQualifiedName.substring(lastDot + 1));
				if (!type.exists()) {
					return null;
				}
			}
		}
		return type;
	}

	/**
	 * @see IEGLProject#findPart(String, String)
	 */
	public IPart findPart(String packageName, String typeQualifiedName) throws EGLModelException {
		return this.getNameLookup().findPart(typeQualifiedName, packageName, false, NameLookup.ACCEPT_PARTS);
	}

	/**
	 * Returns the eglpath entry that refers to the given path or <code>null</code>
	 * if there is no reference to the path.
	 */
	public IEGLPathEntry getEGLPathEntryFor(IPath path) throws EGLModelException {

		IEGLPathEntry[] entries = getExpandedEGLPath(true);
		for (int i = 0; i < entries.length; i++) {
			if (entries[i].getPath().equals(path)) {
				return entries[i];
			}
		}
		return null;
	}
	/**
	 * Find the specific EGL command amongst the build spec of a given
	 * description
	 */
	private ICommand getCommand(IProjectDescription description, String builderID) throws CoreException {

		ICommand[] commands = description.getBuildSpec();
		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(builderID)) {
				return commands[i];
			}
		}
		return null;
	}
	
	/**
	 * Convenience method that returns the specific type of info for a EGL
	 * project.
	 */
	protected EGLProjectElementInfo getEGLProjectElementInfo() throws EGLModelException {

		return (EGLProjectElementInfo) getElementInfo();
	}
	/**
	 * This is a helper method returning the expanded eglpath for the project,
	 * as a list of eglpath entries, where all eglpath variable entries have
	 * been resolved and substituted with their final target entries. All
	 * project exports have been appended to project entries.
	 */
	public IEGLPathEntry[] getExpandedEGLPath(boolean ignoreUnresolvedVariable) throws EGLModelException {

		return getExpandedEGLPath(ignoreUnresolvedVariable, false);
	}

	/**
	 * Internal variant which can create marker on project for invalid entries,
	 * it will also perform eglpath expansion in presence of project
	 * prerequisites exporting their entries.
	 */
	public IEGLPathEntry[] getExpandedEGLPath(boolean ignoreUnresolvedVariable, boolean generateMarkerOnError) throws EGLModelException {

		ObjectVector accumulatedEntries = new ObjectVector();
		this.computeExpandedEGLPath(this, ignoreUnresolvedVariable, generateMarkerOnError, new HashSet(5), accumulatedEntries);

		IEGLPathEntry[] expandedPath = new IEGLPathEntry[accumulatedEntries.size()];
		accumulatedEntries.copyInto(expandedPath);

		return expandedPath;
	}

	/**
	 * Returns the <code>char</code> that marks the start of this handles
	 * contribution to a memento.
	 */
	protected char getHandleMementoDelimiter() {

		return EGLM_EGLPROJECT;
	}
	/**
	 * @see IEGLProject
	 */
	public NameLookup getNameLookup() throws EGLModelException {

		EGLProjectElementInfo info = getEGLProjectElementInfo();
		// lock on the project info to avoid race condition
		synchronized (info) {
			NameLookup nameLookup;
			if ((nameLookup = info.getNameLookup()) == null) {
				info.setNameLookup(nameLookup = new NameLookup(this));
			}
			return nameLookup;
		}
	}
	/**
	 * @see IEGLProject
	 */
	public IPackageFragmentRoot[] getAllPackageFragmentRoots() throws EGLModelException {

		return computePackageFragmentRoots(getResolvedEGLPath(true), true);
	}

	/**
	 * Returns an array of non-java resources contained in the receiver.
	 */
	public Object[] getNonEGLResources() throws EGLModelException {

		return ((EGLProjectElementInfo) getElementInfo()).getNonEGLResources(this);
	}

	/*
	 * (non-EGLdoc)
	 * 
	 * @see com.ibm.etools.egl.internal.model.core.IEGLProject#getOption(java.lang.String,
	 *      boolean)
	 */
	public String getOption(String optionName, boolean inheritEGLCoreOptions) {
		// EGLTODO Fix when we have a builder preference page.
		//		if (EGLModelManager.OptionNames.contains(optionName)){
		//			
		//			Preferences preferences = getPreferences();
		//			if (preferences == null || preferences.isDefault(optionName)) {
		//				return inheritEGLCoreOptions ? EGLCore.getOption(optionName) : null;
		//			}
		//			return preferences.getString(optionName).trim();
		//		}
		if (EGLCore.CORE_ENCODING.equals(optionName)) {
			return EGLCore.getOption(optionName);
		}
		
		return null;
	}

	/*
	 * (non-EGLdoc)
	 * 
	 * @see com.ibm.etools.egl.internal.model.core.IEGLProject#getOptions(boolean)
	 */
	public Map getOptions(boolean inheritEGLCoreOptions) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see IEGLProject
	 */
	public IPath getOutputLocation() throws EGLModelException {
		return getOutputLocation(true);
	}

	public IPath getOutputLocation(Boolean returnDefaultIfNull) throws EGLModelException {
		EGLModelManager.PerProjectInfo perProjectInfo = EGLModelManager.getEGLModelManager().getPerProjectInfoCheckExistence(fProject);
		IPath outputLocation = perProjectInfo.outputLocation;
		if (outputLocation != null)
			return outputLocation;

		// force to read eglpath - will position output location as well
		this.getRawEGLPath();
		outputLocation = perProjectInfo.outputLocation;
		if (outputLocation == null && returnDefaultIfNull) {
			return defaultOutputLocation();
		}
		return outputLocation;
	}

	/**
	 * @return A handle to the package fragment root identified by the given
	 *         path. This method is handle-only and the element may or may not
	 *         exist. Returns <code>null</code> if unable to generate a
	 *         handle from the path (for example, an absolute path that has
	 *         less than 1 segment. The path may be relative or absolute.
	 */
	public IPackageFragmentRoot getPackageFragmentRoot(IPath path) {
		if (!path.isAbsolute()) {
			path = getPath().append(path);
		}
		int segmentCount = path.segmentCount();
		switch (segmentCount) {
			case 0 :
				return null;
			case 1 :
				// default root
				return getPackageFragmentRoot(getProject());
			default :
				// a path ending with .jar/.zip is still ambiguous and could
				// still resolve to a source/lib folder
				// thus will try to guess based on existing resource				
				if (Util.isArchiveFileName(path.lastSegment())) {
					IResource resource = getProject().getWorkspace().getRoot().findMember(path);
					if (resource != null && resource.getType() == IResource.FOLDER) {
						return getPackageFragmentRoot(resource);
					}
					return getPackageFragmentRoot0(path);
				} else if(Util.isEGLARFileName(path.lastSegment())) {	//handle .eglar case
					IResource resource = getProject().getWorkspace().getRoot().findMember(path);
					if (resource != null && resource.getType() == IResource.FILE) {
						return getPackageFragmentRoot(resource);
					}
					return getPackageFragmentRoot0(path);
				} else {
					return getPackageFragmentRoot(getProject().getWorkspace().getRoot().getFolder(path));
				}
		}
	}

	/**
	 * The path is known to match a source/library folder entry.
	 */
	public IPackageFragmentRoot getFolderPackageFragmentRoot(IPath path) {
		if (path.segmentCount() == 1) { // default project root
			return getPackageFragmentRoot(getProject());
		}
		
//		if ( path.toOSString().endsWith(FileInEglar.EGLAR_EXTENSION) ) {
//			return new EglarPackageFragmentRoot(path, this);
//		} else {
			return getPackageFragmentRoot(getProject().getWorkspace().getRoot().getFolder(path));
//		}
	}

	/**
	 * @see IEGLProject
	 */
	public IPackageFragmentRoot getPackageFragmentRoot(IResource resource) {

		switch (resource.getType()) {
			case IResource.FILE :
				if (Util.isEGLFileName(resource.getName())) {
					return new PackageFragmentRoot(resource, this, resource.getName());
				} else if(Util.isEGLARFileName(resource.getName())) { 
					return new EglarPackageFragmentRoot(resource, this, resource.getName());
				}else {
					return null;
				}
			case IResource.FOLDER :
				return new PackageFragmentRoot(resource, this, resource.getName());
			case IResource.PROJECT :
				return new PackageFragmentRoot(resource, this, ""); //$NON-NLS-1$
			default :
				return null;
		}
	}

	/**
	 * @see IEGLProject
	 */
	public IPackageFragmentRoot getPackageFragmentRoot(String jarPath) {

		return getPackageFragmentRoot0(EGLProject.canonicalizedPath(new Path(jarPath)));
	}

	/**
	 * no path canonicalization
	 */
	public IPackageFragmentRoot getPackageFragmentRoot0(IPath eglPath) {
		if(Util.isEGLARFileName(eglPath.lastSegment())) {
			return new EglarPackageFragmentRoot(eglPath,this);
		}
		return new PackageFragmentRoot(null, this, "jarPackageFragment"); //$NON-NLS-1$
	}

	/**
	 * @see IEGLProject
	 */
	public IPackageFragmentRoot[] getPackageFragmentRoots() throws EGLModelException {
		//TODO The returned value does not include eglar entry...
		Object[] children;
		int length;
		IPackageFragmentRoot[] roots;

		System.arraycopy(children = getChildren(), 0, roots = new IPackageFragmentRoot[length = children.length], 0, length);

		return roots;
	}

	/**
	 * Returns the package fragment root prefixed by the given path, or an
	 * empty collection if there are no such elements in the model.
	 */
	protected IPackageFragmentRoot[] getPackageFragmentRoots(IPath path) throws EGLModelException {
		IPackageFragmentRoot[] roots = getAllPackageFragmentRoots();
		ArrayList matches = new ArrayList();

		for (int i = 0; i < roots.length; ++i) {
			if (path.isPrefixOf(roots[i].getPath())) {
				matches.add(roots[i]);
			}
		}
		IPackageFragmentRoot[] copy = new IPackageFragmentRoot[matches.size()];
		matches.toArray(copy);
		return copy;
	}

	/**
	 * @see IEGLProject
	 */
	public IPackageFragment[] getPackageFragments() throws EGLModelException {

		IPackageFragmentRoot[] roots = getPackageFragmentRoots();
		return getPackageFragmentsInRoots(roots);
	}

	/**
	 * Returns all the package fragments found in the specified package
	 * fragment roots.
	 */
	public IPackageFragment[] getPackageFragmentsInRoots(IPackageFragmentRoot[] roots) {

		ArrayList frags = new ArrayList();
		for (int i = 0; i < roots.length; i++) {
			IPackageFragmentRoot root = roots[i];
			try {
				IEGLElement[] rootFragments = root.getChildren();
				for (int j = 0; j < rootFragments.length; j++) {
					frags.add(rootFragments[j]);
				}
			} catch (EGLModelException e) {
				// do nothing
			}
		}
		IPackageFragment[] fragments = new IPackageFragment[frags.size()];
		frags.toArray(fragments);
		return fragments;
	}

	/*
	 * @see IEGLElement
	 */
	public IPath getPath() {
		return this.getProject().getFullPath();
	}

	/**
	 * @see IEGLProject
	 */
	public IProject getProject() {

		return fProject;
	}

	/**
	 * Returns the project custom preference pool. Project preferences may
	 * include custom encoding.
	 */

	public Preferences getPreferences() {
		IProject project = getProject();
		if (!EGLProject.hasEGLNature(project))
			return null;
		EGLModelManager.PerProjectInfo perProjectInfo = EGLModelManager.getEGLModelManager().getPerProjectInfo(project, true);
		Preferences preferences = perProjectInfo.preferences;
		if (preferences != null)
			return preferences;
		preferences = loadPreferences();
		if (preferences == null)
			preferences = new Preferences();
		perProjectInfo.preferences = preferences;
		return preferences;
	}
	/*
	 * (non-EGLdoc)
	 * 
	 * @see com.ibm.etools.egl.internal.model.core.IEGLProject#getRawEGLPath()
	 */
	public IEGLPathEntry[] getRawEGLPath() throws EGLModelException {
		EGLModelManager.PerProjectInfo perProjectInfo = EGLModelManager.getEGLModelManager().getPerProjectInfoCheckExistence(fProject);
		IEGLPathEntry[] eglpath = perProjectInfo.eglpath;
		if (eglpath != null)
			return eglpath;
		eglpath = this.readEGLPathFile(false /* don't create markers */
		, true /* log problems */
		);

		// extract out the output location
		IPath outputLocation = null;
		if (eglpath != null && eglpath.length > 0) {
			IEGLPathEntry entry = eglpath[eglpath.length - 1];
			if (entry.getContentKind() == EGLPathEntry.K_OUTPUT) {
				outputLocation = entry.getPath();
				IEGLPathEntry[] copy = new IEGLPathEntry[eglpath.length - 1];
				System.arraycopy(eglpath, 0, copy, 0, copy.length);
				eglpath = copy;
			}
		}
		if (eglpath == null) {
			return defaultEGLPath();
		}

		perProjectInfo.eglpath = eglpath;
		perProjectInfo.outputLocation = outputLocation;
		return eglpath;
	}

	/**
	 * @see IEGLProject
	 */
	public IEGLPathEntry[] getResolvedEGLPath(boolean ignoreUnresolvedEntry) throws EGLModelException {

		return this.getResolvedEGLPath(ignoreUnresolvedEntry, false);
		// generateMarkerOnError
	}

	/**
	 * Internal variant which can create marker on project for invalid entries
	 * and caches the resolved eglpath on perProjectInfo
	 */
	public IEGLPathEntry[] getResolvedEGLPath(boolean ignoreUnresolvedEntry, boolean generateMarkerOnError) throws EGLModelException {

		EGLModelManager manager = EGLModelManager.getEGLModelManager();
		EGLModelManager.PerProjectInfo perProjectInfo = manager.getPerProjectInfoCheckExistence(fProject);

		// reuse cache if not needing to refresh markers or checking bound
		// variables
		if (ignoreUnresolvedEntry && !generateMarkerOnError && perProjectInfo != null) {
			// resolved path is cached on its info
			IEGLPathEntry[] infoPath = perProjectInfo.lastResolvedEGLPath;
			if (infoPath != null)
				return infoPath;
		}
		Map reverseMap = perProjectInfo == null ? null : new HashMap(5);
		IEGLPathEntry[] resolvedPath =
			getResolvedEGLPath(
				getRawEGLPath(),
				generateMarkerOnError ? getOutputLocation() : null,
				ignoreUnresolvedEntry,
				generateMarkerOnError,
				reverseMap);

		if (perProjectInfo != null) {
			if (perProjectInfo.eglpath == null // .eglpath file could not be
											   // read
				&& generateMarkerOnError
				&& EGLProject.hasEGLNature(fProject)) {
				this.createEGLPathProblemMarker(new EGLModelStatus(IEGLModelStatusConstants.INVALID_EGLPATH_FILE_FORMAT, EGLModelResources.bind(EGLModelResources.eglpathCannotReadEGLpathFile, this.getElementName())));
			}

			perProjectInfo.lastResolvedEGLPath = resolvedPath;
			perProjectInfo.resolvedPathToRawEntries = reverseMap;
		}
		return resolvedPath;
	}

	/**
	 * Internal variant which can process any arbitrary eglpath
	 */
	public IEGLPathEntry[] getResolvedEGLPath(IEGLPathEntry[] eglpathEntries, IPath projectOutputLocation,
	// only set if needing full eglpath validation (and markers)
	boolean ignoreUnresolvedEntry,
	// if unresolved entries are met, should it trigger initializations
	boolean generateMarkerOnError, Map reverseMap) // can be null if not
												   // interested in reverse
												   // mapping
	throws EGLModelException {

		IEGLModelStatus status;
		if (generateMarkerOnError) {
			flushEGLPathProblemMarkers(false, false);
		}

		int length = eglpathEntries.length;
		ArrayList resolvedEntries = new ArrayList();

		for (int i = 0; i < length; i++) {

			IEGLPathEntry rawEntry = eglpathEntries[i];
			IPath resolvedPath;
			status = null;

			/* validation if needed */
			if (generateMarkerOnError || !ignoreUnresolvedEntry) {
				status = EGLConventions.validateEGLPathEntry(this, rawEntry, false);
				if (generateMarkerOnError && !status.isOK())
					createEGLPathProblemMarker(status);
			}

			switch (rawEntry.getEntryKind()) {

				case IEGLPathEntry.CPE_VARIABLE :

					IEGLPathEntry resolvedEntry = EGLCore.getResolvedEGLPathEntry(rawEntry);
					if (resolvedEntry == null) {
						if (!ignoreUnresolvedEntry)
							throw new EGLModelException(status);
					} else {
						if (reverseMap != null && reverseMap.get(resolvedPath = resolvedEntry.getPath()) == null)
							reverseMap.put(resolvedPath, rawEntry);
						resolvedEntries.add(resolvedEntry);
					}
					break;

				case IEGLPathEntry.CPE_CONTAINER :

					IEGLPathContainer container = EGLCore.getEGLPathContainer(rawEntry.getPath(), this);
					if (container == null) {
						if (!ignoreUnresolvedEntry)
							throw new EGLModelException(status);
						break;
					}

					IEGLPathEntry[] containerEntries = container.getEGLPathEntries();
					if (containerEntries == null)
						break;

					// container was bound
					for (int j = 0, containerLength = containerEntries.length; j < containerLength; j++) {
						IEGLPathEntry cEntry = containerEntries[j];

						if (generateMarkerOnError) {
							IEGLModelStatus containerStatus = EGLConventions.validateEGLPathEntry(this, cEntry, false);
							if (!containerStatus.isOK())
								createEGLPathProblemMarker(containerStatus);
						}
						// if container is exported, then its nested entries
						// must in turn be exported (21749)
						if (rawEntry.isExported()) {
							cEntry =
								new EGLPathEntry(
									cEntry.getContentKind(),
									cEntry.getEntryKind(),
									cEntry.getPath(),
									cEntry.getExclusionPatterns());
							// duplicate container entry for tagging it as
							// exported
						}
						if (reverseMap != null && reverseMap.get(resolvedPath = cEntry.getPath()) == null)
							reverseMap.put(resolvedPath, rawEntry);
						resolvedEntries.add(cEntry);
					}
					break;
				case IEGLPathEntry.CPE_LIBRARY:
					// resolve ".." in library path
					resolvedEntry = ((EGLPathEntry) rawEntry).resolvedDotDot();
					if (resolvedEntry == null) {
						if (!ignoreUnresolvedEntry)
							throw new EGLModelException(status);
					} else {
						if (reverseMap != null && reverseMap.get(resolvedPath = resolvedEntry.getPath()) == null)
							reverseMap.put(resolvedPath, rawEntry);
						resolvedEntries.add(resolvedEntry);
					}
//					if (resolveChainedLibraries && result.rawReverseMap.get(resolvedEntry.getPath()) == null) {
//						// resolve Class-Path: in manifest
//						EGLPathEntry[] extraEntries = ((EGLPathEntry) resolvedEntry).resolvedChainedLibraries();
//						for (int k = 0, length2 = extraEntries.length; k < length2; k++) {
//							if (!rawLibrariesPath.contains(extraEntries[k].getPath())) {
//								addToResult(rawEntry, extraEntries[k], result, resolvedEntries, externalFoldersManager, referencedEntriesMap, true);
//							}
//						}
//					}
	
//					addToResult(rawEntry, resolvedEntry, result, resolvedEntries, externalFoldersManager, referencedEntriesMap, false);
					break;
				default:

					if (reverseMap != null && reverseMap.get(resolvedPath = rawEntry.getPath()) == null)
						reverseMap.put(resolvedPath, rawEntry);
					resolvedEntries.add(rawEntry);

			}
		}

		IEGLPathEntry[] resolvedPath = new IEGLPathEntry[resolvedEntries.size()];
		resolvedEntries.toArray(resolvedPath);

		if (generateMarkerOnError && projectOutputLocation != null) {
			status = EGLConventions.validateEGLPath(this, resolvedPath, projectOutputLocation);
			if (!status.isOK())
				createEGLPathProblemMarker(status);
		}
		return resolvedPath;
	}

	/*
	 * @see IEGLElement
	 */
	public IResource getResource() {
		return this.getProject();
	}

	/**
	 * Retrieve a shared property on a project. If the property is not defined,
	 * answers null. Note that it is orthogonal to IResource persistent
	 * properties, and client code has to decide which form of storage to use
	 * appropriately. Shared properties produce real resource files which can
	 * be shared through a VCM onto a server. Persistent properties are not
	 * shareable.
	 * 
	 * @see EGLProject#setSharedProperty(String, String)
	 */
	public String getSharedProperty(String key) throws CoreException {

		String property = null;
		IFile rscFile = getProject().getFile(key);
		if (rscFile.exists()) {
			try {
				property = new String(Util.getResourceContentsAsByteArray(rscFile), "UTF-8");
			} catch (UnsupportedEncodingException e) {
			}
		}else {
			File file = rscFile.getLocation().toFile();
			if (file != null && file.exists()) {
				byte[] bytes;
				try {
					BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
					try{
						bytes = org.eclipse.edt.ide.core.internal.model.util.Util.getInputStreamAsByteArray(inputStream, (int)file.length());
					}finally{
						inputStream.close();
					}
				} catch (IOException e) {
					return null;
				}
				try {
					property = new String(bytes, "UTF-8"); // .eglpath always encoded with UTF-8
				} catch (UnsupportedEncodingException e) {
					Util.log(e, "Could not read .eglpath with UTF-8 encoding"); //$NON-NLS-1$
					// fallback to default
					property = new String(bytes);
				}
			}
		}
		return property;
	}

	/*
	 * (non-EGLdoc)
	 * 
	 * @see com.ibm.etools.egl.internal.model.core.IEGLProject#hasBuildState()
	 */
	public boolean hasBuildState() {
		// TODO Check build state
		return false;
	}

	/**
	 * @see IEGLProject
	 */
	public boolean hasEGLPathCycle(IEGLPathEntry[] preferredEGLPath) {
		HashSet cycleParticipants = new HashSet();
		updateCycleParticipants(
			preferredEGLPath,
			new ArrayList(2),
			cycleParticipants,
			ResourcesPlugin.getWorkspace().getRoot(),
			new HashSet(2));
		return !cycleParticipants.isEmpty();
	}

	public boolean hasCycleMarker() {
		return this.getCycleMarker() != null;
	}

	public int hashCode() {
		return fProject.hashCode();
	}

	/**
	 * Answers true if the project potentially contains any source. A project
	 * which has no source is immutable.
	 */
	public boolean hasSource() {

		// look if any source folder on the classpath
		// no need for resolved path given source folder cannot be abstracted
		IEGLPathEntry[] entries;
		try {
			entries = this.getRawEGLPath();
		} catch (EGLModelException e) {
			return true; // unsure
		}
		for (int i = 0, max = entries.length; i < max; i++) {
			if (entries[i].getEntryKind() == IEGLPathEntry.CPE_SOURCE) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Returns true if the given project is accessible and it has a egl nature,
	 * otherwise false.
	 */
	public static boolean hasEGLNature(IProject project) {
		try {
			return project.hasNature(EGLCore.NATURE_ID);
		} catch (CoreException e) {
			// project does not exist or is not open
		}
		return false;
	}
	
	/**
	 * Returns true if the given project is accessible and it has a rui nature,
	 * otherwise false.
	 */
	public static boolean hasRUINature(IProject project) {
		try {
			return project.hasNature(RUINature.RUI_NATURE_ID);
		} catch (CoreException e) {
			// project does not exist or is not open
		}
		return false;
	}
	
	/**
	 * Returns true if the given project is accessible and it has a CE nature,
	 * otherwise false.
	 */
	public static boolean hasCENature(IProject project) {
		try {
			return project.hasNature(CENature.CE_NATURE_ID);
		} catch (CoreException e) {
			// project does not exist or is not open
		}
		return false;
	}

	/**
	 * Compare current eglpath with given one to see if any different. Note
	 * that the argument classpath contains its binary output.
	 */
	public boolean isEGLPathEqualsTo(IEGLPathEntry[] newEGLPath, IPath newOutputLocation, IEGLPathEntry[] otherEGLPathWithOutput)
		throws EGLModelException {

		if (otherEGLPathWithOutput != null && otherEGLPathWithOutput.length > 0) {

			int length = otherEGLPathWithOutput.length;
			if (length == newEGLPath.length + 1) {
				// output is amongst file entries (last one)

				// compare classpath entries
				for (int i = 0; i < length - 1; i++) {
					if (!otherEGLPathWithOutput[i].equals(newEGLPath[i]))
						return false;
				}
				// compare binary outputs
				IEGLPathEntry output = otherEGLPathWithOutput[length - 1];
				if (output.getContentKind() == EGLPathEntry.K_OUTPUT && output.getPath().equals(newOutputLocation))
					return true;
			}
		}
		return false;
	}

	/*
	 * @see IEGLProject
	 */
	public boolean isOnEGLPath(IEGLElement element) {
		IPath path = element.getPath();
		switch (element.getElementType()) {
			case IEGLElement.PACKAGE_FRAGMENT_ROOT :
				if (!((IPackageFragmentRoot) element).isArchive()) {
					// ensure that folders are only excluded if all of their
					// children are excluded
					path = path.append("*"); //$NON-NLS-1$
				}
				break;
			case IEGLElement.PACKAGE_FRAGMENT :
				if (!((IPackageFragmentRoot) element.getParent()).isArchive()) {
					// ensure that folders are only excluded if all of their
					// children are excluded
					path = path.append("*"); //$NON-NLS-1$
				}
				break;
		}
		return this.isOnEGLPath(path);
	}
	private boolean isOnEGLPath(IPath path) {
		IEGLPathEntry[] classpath;
		try {
			classpath = this.getResolvedEGLPath(true /*
													  * ignore unresolved
													  * variable
													  */
			);
		} catch (EGLModelException e) {
			return false; // not a EGL project
		}
		for (int i = 0; i < classpath.length; i++) {
			IEGLPathEntry entry = classpath[i];
			if (entry.getPath().isPrefixOf(path) && !Util.isExcluded(path, ((EGLPathEntry) entry).fullExclusionPatternChars())) {
				return true;
			}
		}
		return false;
	}
	/*
	 * @see IEGLProject
	 */
	public boolean isOnEGLPath(IResource resource) {
		IPath path = resource.getFullPath();

		// ensure that folders are only excluded if all of their children are
		// excluded
		if (resource.getType() == IResource.FOLDER) {
			path = path.append("*"); //$NON-NLS-1$
		}

		return this.isOnEGLPath(path);
	}

	/*
	 * load preferences from a shareable format (VCM-wise)
	 */
	public Preferences loadPreferences() {

		Preferences preferences = new Preferences();

		//		File prefFile =
		// getProject().getLocation().append(PREF_FILENAME).toFile();
		IPath projectMetaLocation = getProject().getPluginWorkingLocation(EDTCoreIDEPlugin.getPlugin().getDescriptor());
		if (projectMetaLocation != null) {
			File prefFile = projectMetaLocation.append(PREF_FILENAME).toFile();
			if (prefFile.exists()) { // load preferences from file
				InputStream in = null;
				try {
					in = new BufferedInputStream(new FileInputStream(prefFile));
					preferences.load(in);
					return preferences;
				} catch (IOException e) { // problems loading preference store
					// - quietly ignore
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) { // ignore problems with close
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Open project if resource isn't closed
	 */
	protected void openWhenClosed(IProgressMonitor pm) throws EGLModelException {

		if (!this.fProject.isOpen()) {
			throw newNotPresentException();
		} else {
			super.openWhenClosed(pm);
		}
	}

	/**
	 * Reads the .eglpath file from disk and returns the list of entries it
	 * contains (including output location entry) Returns null if .eglpath is
	 * not present. Returns INVALID_EGLPATH if it has a format problem.
	 */
	protected IEGLPathEntry[] readEGLPathFile(boolean createMarker, boolean logProblems) {

		try {
			String xmlPath = getSharedProperty(EGLPATH_FILENAME);
			if (xmlPath == null)
				return null;
			return decodeEGLPath(xmlPath, createMarker, logProblems);
		} catch (CoreException e) {
			// file does not exist (or not accessible)
			if (createMarker && this.getProject().isAccessible()) {
				this.createEGLPathProblemMarker(new EGLModelStatus(IEGLModelStatusConstants.INVALID_EGLPATH_FILE_FORMAT, EGLModelResources.bind(EGLModelResources.eglpathCannotReadEGLpathFile, this.getElementName())));
			}
			if (logProblems) {
				Util.log(e, "Exception while retrieving " + this.getPath() //$NON-NLS-1$
				+"/.eglpath, will revert to default eglpath"); //$NON-NLS-1$
			}
		}
		return null;
	}

	/**
	 * Removes the given builder from the build spec for the given project.
	 */
	protected void removeFromBuildSpec(String builderID) throws CoreException {

		IProjectDescription description = getProject().getDescription();
		ICommand[] commands = description.getBuildSpec();
		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(builderID)) {
				ICommand[] newCommands = new ICommand[commands.length - 1];
				System.arraycopy(commands, 0, newCommands, 0, i);
				System.arraycopy(commands, i + 1, newCommands, i, commands.length - i - 1);
				description.setBuildSpec(newCommands);
				getProject().setDescription(description, null);
				return;
			}
		}
	}

	public String[] projectPrerequisites(IEGLPathEntry[] entries) throws EGLModelException {

		ArrayList prerequisites = new ArrayList();
		// need resolution
		entries = getResolvedEGLPath(entries, null, true, false, null /*
																	   * no
																	   * reverse
																	   * map
																	   */
		);
		for (int i = 0, length = entries.length; i < length; i++) {
			IEGLPathEntry entry = entries[i];
			if (entry.getEntryKind() == IEGLPathEntry.CPE_PROJECT) {
				prerequisites.add(entry.getPath().lastSegment());
			}
		}
		int size = prerequisites.size();
		if (size == 0) {
			return NO_PREREQUISITES;
		} else {
			String[] result = new String[size];
			prerequisites.toArray(result);
			return result;
		}
	}
	/**
	 * @see EGLElement#rootedAt(IEGLProject)
	 */
	public IEGLElement rootedAt(IEGLProject project) {
		return project;

	}

	/**
	 * Answers an ID which is used to distinguish project/entries during
	 * package fragment root computations
	 */
	public String rootID() {
		return "[PRJ]" + this.getProject().getFullPath(); //$NON-NLS-1$
	}

	/**
	 * Update the EGL command in the build spec (replace existing one if
	 * present, add one first if none).
	 */
	private void setEGLCommand(IProjectDescription description, ICommand newCommand, String builderID, boolean bInsert2First) throws CoreException {

		ICommand[] oldCommands = description.getBuildSpec();
		ICommand oldEGLCommand = getCommand(description, builderID);
		ICommand[] newCommands;

		if (oldEGLCommand == null) {
			// Add a EGL build spec before other builders (1FWJK7I)
			newCommands = new ICommand[oldCommands.length + 1];
			
			if(bInsert2First){
				System.arraycopy(oldCommands, 0, newCommands, 1, oldCommands.length);				
				newCommands[0] = newCommand;
			}
			else
			{
				System.arraycopy(oldCommands, 0, newCommands, 0, oldCommands.length);
				newCommands[newCommands.length-1] = newCommand;
			}
		} else {
			for (int i = 0, max = oldCommands.length; i < max; i++) {
				if (oldCommands[i] == oldEGLCommand) {
					oldCommands[i] = newCommand;
					break;
				}
			}
			newCommands = oldCommands;
		}

		// Commit the spec change into the project
		description.setBuildSpec(newCommands);
		getProject().setDescription(description, null);
	}

	/**
	 * Saves the eglpath in a shareable format (VCM-wise) only when necessary,
	 * that is, if it is semantically different from the existing one in file.
	 * Will never write an identical one.
	 * 
	 * @return Return whether the .eglpath file was modified.
	 */
	public boolean saveEGLPath(IEGLPathEntry[] newEGLPath, IPath newOutputLocation) throws EGLModelException {

		if (!getProject().exists())
			return false;

		IEGLPathEntry[] fileEntries = readEGLPathFile(false /*
															 * don't create
															 * markers
															 */
		, false /* don't log problems */
		);
		if (fileEntries != null && isEGLPathEqualsTo(newEGLPath, newOutputLocation, fileEntries)) {
			// no need to save it, it is the same
			return false;
		}

		// actual file saving
		try {
			setSharedProperty(EGLPATH_FILENAME, encodeEGLPath(newEGLPath, newOutputLocation, true));
			return true;
		} catch (CoreException e) {
			throw new EGLModelException(e);
		}
	}

	/**
	 * Save project custom preferences to shareable file (.jprefs)
	 */
	private void savePreferences(Preferences preferences) {

		IProject project = getProject();
		if (!EGLProject.hasEGLNature(project))
			return; // ignore

		if (preferences == null || (!preferences.needsSaving() && preferences.propertyNames().length != 0)) {
			// nothing to save
			return;
		}

		// preferences need to be saved
		// the preferences file is located in the plug-in's state area
		// at a well-known name (.eprefs)
		//		File prefFile =
		// getProject().getLocation().append(PREF_FILENAME).toFile();
		File prefFile = project.getPluginWorkingLocation(EDTCoreIDEPlugin.getPlugin().getDescriptor()).append(PREF_FILENAME).toFile();
		if (preferences.propertyNames().length == 0) {
			// there are no preference settings
			// rather than write an empty file, just delete any existing file
			if (prefFile.exists()) {
				prefFile.delete(); // don't worry if delete unsuccessful
			}
			return;
		}

		// write file, overwriting an existing one
		OutputStream out = null;
		try {
			// do it as carefully as we know how so that we don't lose/mangle
			// the setting in times of stress
			out = new BufferedOutputStream(new FileOutputStream(prefFile));
			preferences.store(out, null);
		} catch (IOException e) { // problems saving preference store - quietly
			// ignore
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) { // ignore problems with close
				}
			}
		}
	}

	/*
	 * (non-EGLdoc)
	 * 
	 * @see com.ibm.etools.egl.internal.model.core.IEGLProject#setOptions(java.util.Map)
	 */
	public void setOptions(Map newOptions) {
		Preferences preferences;
		setPreferences(preferences = new Preferences());
		// always reset (26255)
		if (newOptions != null) {
			Iterator keys = newOptions.keySet().iterator();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				if (!EGLModelManager.OptionNames.contains(key))
					continue; // unrecognized option
				// no filtering for encoding (custom encoding for project is
				// allowed)
				String value = (String) newOptions.get(key);
				preferences.setDefault(key, CUSTOM_DEFAULT_OPTION_VALUE);
				// empty string isn't the default (26251)
				preferences.setValue(key, value);
			}
		}

		// persist options
		savePreferences(preferences);

	}

	/*
	 * (non-EGLdoc)
	 * 
	 * @see com.ibm.etools.egl.internal.model.core.IEGLProject#setOutputLocation(org.eclipse.core.runtime.IPath,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setOutputLocation(IPath path, IProgressMonitor monitor) throws EGLModelException {

		if (path == null) {
			throw new IllegalArgumentException(EGLModelResources.pathNullPath);
		}
		if (path.equals(getOutputLocation())) {
			return;
		}
		this.setRawEGLPath(SetEGLPathOperation.ReuseEGLPath, path, monitor);
	}

	/**
	 * @see IEGLProject
	 */
	public void setRawEGLPath(IEGLPathEntry[] entries, IPath outputLocation, IProgressMonitor monitor) throws EGLModelException {

		setRawEGLPath(entries, outputLocation, monitor, true,
		// canChangeResource (as per API contract)
		getResolvedEGLPath(true), // ignoreUnresolvedVariable
		true, // needValidation
		true); // need to save
	}

	public void setRawEGLPath(
		IEGLPathEntry[] newEntries,
		IPath newOutputLocation,
		IProgressMonitor monitor,
		boolean canChangeResource,
		IEGLPathEntry[] oldResolvedPath,
		boolean needValidation,
		boolean needSave)
		throws EGLModelException {

		EGLModelManager manager = EGLModelManager.getEGLModelManager();
		try {
			IEGLPathEntry[] newRawPath = newEntries;
			if (newRawPath == null) { //are we already with the default
				// eglpath
				newRawPath = defaultEGLPath();
			}
			SetEGLPathOperation op =
				new SetEGLPathOperation(this, oldResolvedPath, newRawPath, newOutputLocation, canChangeResource, needValidation, needSave);
			op.runOperation(monitor);

		} catch (EGLModelException e) {
			manager.flush();
			throw e;
		}
	}

	/**
	 * @see IEGLProject
	 */
	public void setRawEGLPath(IEGLPathEntry[] entries, IProgressMonitor monitor) throws EGLModelException {

		setRawEGLPath(entries, SetEGLPathOperation.ReuseOutputLocation, monitor, true,
		// canChangeResource (as per API contract)
		getResolvedEGLPath(true), // ignoreUnresolvedVariable
		true, // needValidation
		true); // need to save
	}

	/**
	 * Record a shared persistent property onto a project. Note that it is
	 * orthogonal to IResource persistent properties, and client code has to
	 * decide which form of storage to use appropriately. Shared properties
	 * produce real resource files which can be shared through a VCM onto a
	 * server. Persistent properties are not shareable.
	 * 
	 * shared properties end up in resource files, and thus cannot be modified
	 * during delta notifications (a CoreException would then be thrown).
	 * 
	 * @see EGLProject#getSharedProperty(String key)
	 */
	public void setSharedProperty(String key, String value) throws CoreException {

		IFile rscFile = getProject().getFile(key);
		byte[] bytes = null;
		try{
			bytes = value.getBytes("UTF-8");//$NON-NLS-1$
		}catch (UnsupportedEncodingException e) {
			Util.log(e, "Could not write .eglpath with UTF-8 encoding "); //$NON-NLS-1$
			// fallback to default
			bytes = value.getBytes();
		}
		InputStream inputStream = new ByteArrayInputStream(bytes);
		// update the resource content
		if (rscFile.exists()) {
			if (rscFile.isReadOnly()) {
				// provide opportunity to checkout read-only .eglpath file
				ResourcesPlugin.getWorkspace().validateEdit(new IFile[] { rscFile }, null);
			}
			rscFile.setContents(inputStream, IResource.FORCE, null);
		} else {
			rscFile.create(inputStream, IResource.FORCE, null);
		}
		rscFile.setCharset("UTF-8");
	}

	/**
	 * NOTE: <code>null</code> specifies default eglpath, and an empty array
	 * specifies an empty eglpath.
	 * 
	 * @exception NotPresentException
	 *                if this project does not exist.
	 */
	public void setRawEGLPath0(IEGLPathEntry[] rawEntries) throws EGLModelException {

		EGLModelManager.PerProjectInfo info = EGLModelManager.getEGLModelManager().getPerProjectInfoCheckExistence(fProject);

		synchronized (info) {
			if (rawEntries != null) {
				info.eglpath = rawEntries;
			}

			// clear cache of resolved eglpath
			info.lastResolvedEGLPath = null;
			info.resolvedPathToRawEntries = null;
		}
	}

	/**
	 * If a cycle is detected, then cycleParticipants contains all the paths of
	 * projects involved in this cycle (directly and indirectly), no cycle if
	 * the set is empty (and started empty)
	 */
	public void updateCycleParticipants(
		IEGLPathEntry[] preferredEGLPath,
		ArrayList prereqChain,
		HashSet cycleParticipants,
		IWorkspaceRoot workspaceRoot,
		HashSet traversed) {

		IPath path = this.getPath();
		prereqChain.add(path);
		traversed.add(path);
		try {
			IEGLPathEntry[] eglpath = preferredEGLPath == null ? getResolvedEGLPath(true) : preferredEGLPath;
			for (int i = 0, length = eglpath.length; i < length; i++) {
				IEGLPathEntry entry = eglpath[i];

				if (entry.getEntryKind() == IEGLPathEntry.CPE_PROJECT) {
					IPath prereqProjectPath = entry.getPath();
					int index = cycleParticipants.contains(prereqProjectPath) ? 0 : prereqChain.indexOf(prereqProjectPath);
					if (index >= 0) { // refer to cycle, or in cycle itself
						for (int size = prereqChain.size(); index < size; index++) {
							cycleParticipants.add(prereqChain.get(index));
						}
					} else {
						if (!traversed.contains(prereqProjectPath)) {
							IResource member = workspaceRoot.findMember(prereqProjectPath);
							if (member != null && member.getType() == IResource.PROJECT) {
								EGLProject project = (EGLProject) EGLCore.create((IProject) member);
								project.updateCycleParticipants(null, prereqChain, cycleParticipants, workspaceRoot, traversed);
							}
						}
					}
				}
			}
		} catch (EGLModelException e) {
		}
		prereqChain.remove(path);
	}
	/*
	 * (non-EGLdoc)
	 * 
	 * @see com.ibm.etools.egl.internal.model.core.IParent#hasChildren()
	 */
	public boolean hasChildren() throws EGLModelException {
		// TODO Auto-generated method stub
		return false;
	}
	/**
	 * Reset the collection of package fragment roots (local ones) - only if
	 * opened. Need to check *all* package fragment roots in order to reset
	 * NameLookup
	 */
	public void updatePackageFragmentRoots() {

		if (this.isOpen()) {
			try {
				EGLProjectElementInfo info = getEGLProjectElementInfo();

				IEGLPathEntry[] eglpath = getResolvedEGLPath(true);
				NameLookup lookup = info.getNameLookup();
				if (lookup != null) {
					IPackageFragmentRoot[] oldRoots = lookup.fPackageFragmentRoots;
					IPackageFragmentRoot[] newRoots = computePackageFragmentRoots(eglpath, true);
					checkIdentical : { // compare all pkg fragment root lists
						if (oldRoots.length == newRoots.length) {
							for (int i = 0, length = oldRoots.length; i < length; i++) {
								if (!oldRoots[i].equals(newRoots[i])) {
									break checkIdentical;
								}
							}
							return; // no need to update
						}
					}
					info.setNameLookup(null);
					// discard name lookup (hold onto roots)
				}
				info.setNonEGLResources(null);
				info.setChildren(computePackageFragmentRoots(eglpath, false));

			} catch (EGLModelException e) {
				try {
					close(); // could not do better
				} catch (EGLModelException ex) {
				}
			}
		}
	}
	public static void updateAllCycleMarkers() throws EGLModelException {
		EGLModelManager manager = EGLModelManager.getEGLModelManager();
		IEGLProject[] projects = manager.getEGLModel().getEGLProjects();
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

		HashSet cycleParticipants = new HashSet();
		HashSet traversed = new HashSet();
		int length = projects.length;

		// compute cycle participants
		ArrayList prereqChain = new ArrayList();
		for (int i = 0; i < length; i++) {
			EGLProject project = (EGLProject) projects[i];
			if (!traversed.contains(project.getPath())) {
				prereqChain.clear();
				project.updateCycleParticipants(null, prereqChain, cycleParticipants, workspaceRoot, traversed);
			}
		}
		//System.out.println("updateAllCycleMarkers: " +
		// (System.currentTimeMillis() - start) + " ms");

		for (int i = 0; i < length; i++) {
			EGLProject project = (EGLProject) projects[i];

			if (cycleParticipants.contains(project.getPath())) {
				IMarker cycleMarker = project.getCycleMarker();
				int circularCPSeverity = IMarker.SEVERITY_ERROR;

				if (cycleMarker != null) {
					// update existing cycle marker if needed
					try {
						int existingSeverity = ((Integer) cycleMarker.getAttribute(IMarker.SEVERITY)).intValue();
						if (existingSeverity != circularCPSeverity) {
							cycleMarker.setAttribute(IMarker.SEVERITY, circularCPSeverity);
						}
					} catch (CoreException e) {
						throw new EGLModelException(e);
					}
				} else {
					// create new marker
					project.createEGLPathProblemMarker(new EGLModelStatus(IEGLModelStatusConstants.EGLPATH_CYCLE, project));
				}
			} else {
				project.flushEGLPathProblemMarkers(true, false);
			}
		}
	}

	/*
	 * Set cached preferences, no preference file is saved, only info is
	 * updated
	 */
	public void setPreferences(Preferences preferences) {
		IProject project = getProject();
		if (!EGLProject.hasEGLNature(project))
			return; // ignore
		EGLModelManager.PerProjectInfo perProjectInfo = EGLModelManager.getEGLModelManager().getPerProjectInfo(project, true);
		perProjectInfo.preferences = preferences;
	}

	/**
	 * @param project
	 */
	public void setProject(IProject project) {
		fProject = project;
	}

	public IPath getBuildStateLocation()
	{
		return getProject().getFullPath().append(".buildstate"); //$NON-NLS-1$
	}
	
	public class EGLPartWrapper {
		public static final String NO_VALUE_SET = ""; //$NON-NLS-1$
		public String partName = NO_VALUE_SET;
		public String partPath = NO_VALUE_SET;

		public EGLPartWrapper(String partName, String partPath) {
			super();
			this.partName = partName;
			this.partPath = partPath;
		}
	}
	
	public boolean isReadOnly() {

    	//Check for the .readonly file 
    	if (getProject().findMember(".readonly") != null) {
    		return true;
    	}
    	
    	return isBinary();	
   	}

	public EGLModelManager.PerProjectInfo getPerProjectInfo() throws EGLModelException {
		return EGLModelManager.getEGLModelManager().getPerProjectInfoCheckExistence(this.fProject);
	}
	
	/*
	 * Resets this project's caches
	 */
	public void resetCaches() {
		//TODO Rocky, looks like EGL doesn't not have the cache...
//		EGLProjectElementInfo info = (EGLProjectElementInfo) EGLModelManager.getEGLModelManager().peekAtInfo(this);
//		if (info != null){
//			info.resetCaches();
//		}
	}

	public String[] getRequiredProjectNames()
			throws EGLModelException {
		return this.projectPrerequisites(getResolvedEGLPath(true));
	}

	public boolean isBinary() {
    	//Check for the binary project flag in the .eglproject
 		IEGLProjectFileUtility util = EGLProjectFileUtilityLocator.INSTANCE.getUtil();
		if (util != null) {
			 if (util.isBinaryProject(getProject())) {
				 return true;
			 }
		}

		//Check to see if there is an output entry in the .eglpath
		try {
			EGLModelManager.PerProjectInfo perProjectInfo = EGLModelManager.getEGLModelManager().getPerProjectInfoCheckExistence(getProject());
			
			//First check to see if the output location is already in the info
			if (perProjectInfo.outputLocation != null)  {
				return false;
			}				
			
			//The .eglpath file may not have been read yet, force it to read
			getRawEGLPath();
			
			//At this point, if there is a output directory specified, it would have been read and set in the info
			return (perProjectInfo.outputLocation == null);
		} catch (EGLModelException e) {
		}

    	return false;
	}
	
	@Override
	public IEGLProject[] getReferencingProjects() throws EGLModelException {
		IProject[] projects = fProject.getWorkspace().getRoot().getProjects(IContainer.INCLUDE_HIDDEN);
		List<IEGLProject> result = new ArrayList<IEGLProject>(projects.length);
		
		for (IProject project : projects) {
			if (!project.isAccessible() || (project.equals(fProject))) {
				 continue;
			}
			
			IEGLProject eglProject = EGLCore.create(project);
			String[] referencedProjectNames = eglProject.getRequiredProjectNames();
			if(referencedProjectNames == null || (referencedProjectNames.length == 0)) {
				continue;
			}
			for(String eglProjectName : referencedProjectNames) {
				if(fProject.getName().equals(eglProjectName)) {
					result.add(eglProject);
					break;
				}
			}
		}
		return result.toArray(new IEGLProject[result.size()]);
	}
}
