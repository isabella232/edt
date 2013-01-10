/*******************************************************************************
 * Copyright © 2010, 2013 IBM Corporation and others.
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.model.IPart;


public class SourceMapper {
	
	/**
	 * Specifies the location of the package fragment roots within
	 * the zip (empty specifies the default root). <code>null</code> is
	 * not a valid root path.
	 */
	protected ArrayList rootPaths;

//	/**
//	 * The binary type source is being mapped for
//	 */
//	protected BinaryType binaryType;

	/**
	 * The location of the zip file containing source, or the location of the source folder (not including package folder)
	 */
	protected IPath sourcePath;
	/**
	 * Specifies the location of the package fragment root within
	 * the zip (empty specifies the default root). <code>null</code> is
	 * not a valid root path.
	 */
	protected String rootPath = ""; //$NON-NLS-1$

	/**
	 * Table that maps a binary method to its parameter names.
	 * Keys are the method handles, entries are <code>char[][]</code>.
	 */
	protected HashMap parameterNames;

	/**
	 * Table that maps a binary element to its <code>SourceRange</code>s.
	 * Keys are the element handles, entries are <code>SourceRange[]</code> which
	 * is a two element array; the first being source range, the second
	 * being name range.
	 */
	protected HashMap sourceRanges;

	/*
	 * A map from IJavaElement to String[]
	 */
	protected HashMap categories;


	/**
	 * The unknown source range {-1, 0}
	 */
	public static final SourceRange UNKNOWN_RANGE = new SourceRange(-1, 0);

	/**
	 * The position within the source of the start of the
	 * current member element, or -1 if we are outside a member.
	 */
	protected int[] memberDeclarationStart;
	/**
	 * The <code>SourceRange</code> of the name of the current member element.
	 */
	protected SourceRange[] memberNameRange;
	/**
	 * The name of the current member element.
	 */
	protected String[] memberName;

	/**
	 * The parameter names for the current member method element.
	 */
	protected char[][][] methodParameterNames;

	/**
	 * The parameter types for the current member method element.
	 */
	protected char[][][] methodParameterTypes;


//	/**
//	 * The element searched for
//	 */
//	protected IJavaElement searchedElement;

	/**
	 * imports references
	 */
	private HashMap importsTable;
	private HashMap importsCounterTable;

	/**
	 * Enclosing type information
	 */
//	IType[] types;
	int[] typeDeclarationStarts;
	SourceRange[] typeNameRanges;
	int[] typeModifiers;
	int typeDepth;

	/**
	 *  Anonymous counter in case we want to map the source of an anonymous class.
	 */
	int anonymousCounter;
	int anonymousClassName;

	/**
	 *Options to be used
	 */
	String encoding;
	Map options;

	/**
	 * Use to handle root paths inference
	 */
	private boolean areRootPathsComputed;
	
	public static final int FROM_ZIP = 1;
	public static final int FROM_SOURCE = 2;
	
	private int sourceFrom = FROM_ZIP;	//default: get source from zip file
	
	public SourceMapper() {
		this.areRootPathsComputed = false;
	}
	
	public SourceMapper(IPath sourcePath, String rootPath, Map options, int sourceFrom){
		this(sourcePath, rootPath, options);
		this.sourceFrom = sourceFrom;
	}

	/**
	 * Creates a <code>SourceMapper</code> that locates source in the zip file
	 * at the given location in the specified package fragment root.
	 */
	public SourceMapper(IPath sourcePath, String rootPath, Map options) {
		this.areRootPathsComputed = false;
		this.options = options;
		try {
			this.encoding = ResourcesPlugin.getWorkspace().getRoot().getDefaultCharset();
		} catch (CoreException e) {
			// use no encoding
		}
		if (rootPath != null) {
			this.rootPaths = new ArrayList();
			this.rootPaths.add(rootPath);
		}
		this.sourcePath = sourcePath;
		this.sourceRanges = new HashMap();
		this.parameterNames = new HashMap();
		this.importsTable = new HashMap();
		this.importsCounterTable = new HashMap();
	}
	

	
//	/**
//	 * Locates and returns source code for the given (binary) type, in this
//	 * SourceMapper's ZIP file, or returns <code>null</code> if source
//	 * code cannot be found.
//	 */
//	public char[] findSource(IPart part, IBinaryType info) {
//		if (!part.isBinary()) {
//			return null;
//		}
//		String simpleSourceFileName = ((BinaryPart) part).getSourceFileName(info);
//		if (simpleSourceFileName == null) {
//			return null;
//		}
//		return findSource(type, simpleSourceFileName);
//	}
	
//	abstract public char[] findSource(IPart part, String simpleSourceFileName);
	
	public int getSourceFrom() {
		return sourceFrom;
	}
	
	public char[] findSource(String fullName) {
		char[] source = null;
		Object target = EGLModel.getTarget(ResourcesPlugin.getWorkspace().getRoot(), this.sourcePath, true);
		//for non-archive source
		if (target instanceof IContainer) {	//source is in workspace folder (non-archive)
			IResource res = ((IContainer)target).findMember(fullName);
			if (res instanceof IFile) {
				try {
					source = org.eclipse.edt.ide.core.internal.model.Util.getResourceContentsAsCharArray((IFile)res);
				} catch (EGLModelException e) {
					e.printStackTrace();
				}
			}
		} else if (target instanceof IFile && !isZipFile(target)){ //source is in non-external file (non-archive)
			try {
				source = org.eclipse.edt.ide.core.internal.model.Util.getResourceContentsAsCharArray((IFile)target);
			} catch (EGLModelException e) {
				e.printStackTrace();
			}
			
		} else if (target instanceof java.io.File && !isZipFile(target)){	//source is in extrernal file (non-archive)
			InputStream stream = null;
			try {
				stream = new BufferedInputStream(new FileInputStream((File)target));
				source = org.eclipse.edt.ide.core.internal.model.util.Util.getInputStreamAsCharArray(stream, -1, null);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {	//for archive source file
			// try to get the entry
			ZipEntry entry = null;
			ZipFile zip = null;
			EGLModelManager manager = EGLModelManager.getEGLModelManager();
			try {
				zip = manager.getZipFile(this.sourcePath);
				entry = zip.getEntry(fullName);
				if (entry != null) {
					// now read the source code
					source = readSource(entry, zip);
				}
			} catch (CoreException e) {
				return null;
			} catch (Exception e) {
				return null;
			} finally {
				manager.closeZipFile(zip); // handle null case
			}
		}
		if(source == null){
			//generate the frame from ir
			
		}
		return source;
	}
	
	/**
	 * Locates and returns source code for the given (binary) type, in this
	 * SourceMapper's ZIP file, or returns <code>null</code> if source
	 * code cannot be found.
	 * The given simpleSourceFileName is the .java file name (without the enclosing
	 * folder) used to create the given type (e.g. "A.java" for x/y/A$Inner.class)
	 */
	public char[] findSource(IPart part, String simpleSourceFileName, String[] caseSensitivePackageName) {
//		long time = 0;
//		if (VERBOSE) {
//			time = System.currentTimeMillis();
//		}
		
		String name;
		
		if  (caseSensitivePackageName == null) {
			PackageFragment pkgFrag = (PackageFragment) part.getPackageFragment();
			name = org.eclipse.edt.compiler.internal.core.utils.CharOperation.concatWith(pkgFrag.names, simpleSourceFileName, '/');
		}
		else {
			name = org.eclipse.edt.compiler.internal.core.utils.CharOperation.concatWith(caseSensitivePackageName, simpleSourceFileName, '/');
		}

		char[] source = null;
		
		if(this.sourcePath == null){	//no source
			return source;
		}

		EGLModelManager eglModelManager = EGLModelManager.getEGLModelManager();
		try {
			eglModelManager.cacheZipFiles(this); // Cache any zip files we open during this operation

			if (this.rootPath != null) {
				source = getSourceForRootPath(this.rootPath, name);
			}
	
			if (source == null) {
//				computeAllRootPaths(part);
				if (this.rootPaths != null) {
					loop: for (Iterator iterator = this.rootPaths.iterator(); iterator.hasNext(); ) {
						String currentRootPath = (String) iterator.next();
						if (!currentRootPath.equals(this.rootPath)) {
							source = getSourceForRootPath(currentRootPath, name);
							if (source != null) {
								// remember right root path
								this.rootPath = currentRootPath;
								break loop;
							}
						}
					}
				}
			}
		} finally {
			eglModelManager.flushZipFiles(this); // clean up cached zip files.
		}
//		if (VERBOSE) {
//			System.out.println("spent " + (System.currentTimeMillis() - time) + "ms for " + type.getElementName()); //$NON-NLS-1$ //$NON-NLS-2$
//		}
		return source;
	}
	
	private char[] getSourceForRootPath(String currentRootPath, String name) {
		String newFullName;
		if (!currentRootPath.equals(IPackageFragmentRoot.DEFAULT_PACKAGEROOT_PATH)) {
			if (currentRootPath.endsWith("/")) { //$NON-NLS-1$
				newFullName = currentRootPath + name;
			} else {
				newFullName = currentRootPath + '/' + name;
			}
		} else {
			newFullName = name;
		}
		return this.findSource(newFullName);
	}
	
	private char[] readSource(ZipEntry entry, ZipFile zip) {
		try {
			byte[] bytes = Util.getZipEntryByteContent(entry, zip);
			if (bytes != null) {
				return org.eclipse.edt.ide.core.internal.model.util.Util.bytesToChar(bytes, this.encoding);
			}
		} catch (IOException e) {
			// ignore
		}
		return null;
	}
	
	private boolean isZipFile(Object target){
		String fileName = null;
		if(target instanceof java.io.File){
			File file = (File)target;
			if(file.isFile()){
				fileName = file.getName();
			}
		} else if(target instanceof IFile){
			IFile file = (IFile)target;
			fileName = file.getName();
		}
		int index = fileName.lastIndexOf(".");
		if(index == -1){
			return false;
		}
		else{
			String ext = fileName.substring(index);
			//TODO
			if(ext.equalsIgnoreCase(".zip")){
//			if(ext.equalsIgnoreCase(".zip") || ext.equalsIgnoreCase(".eglar")){
				return true;
			}
		}
		return false;
	}

	public IPath getSourcePath() {
		return sourcePath;
	}

	
}
