/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.internal.core.utils.CharOperation;
import org.eclipse.edt.compiler.tools.EGL2IR;
import org.eclipse.edt.ide.core.internal.model.index.IDocument;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IBuffer;
import org.eclipse.edt.ide.core.model.IBufferFactory;
import org.eclipse.edt.ide.core.model.IClassFile;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLModelStatusConstants;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageDeclaration;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.IProblemRequestor;
import org.eclipse.edt.ide.core.model.ISourceRange;

public class ClassFile extends Openable implements IClassFile, IDocument {
	protected BinaryPart binaryPart = null;
	protected String name;
	
	public static boolean SHARED_WC_VERBOSE = false;
	
	private boolean sourceFileSearchRequired = true;
	/**
	 * Constructs a handle to a ir file with the given name in the specified
	 * package.
	 * 
	 * @param parent
	 * @param name
	 * 
	 * @exception IllegalArgumentException if the name of the ir file does not end with ".ir"
	 */
	protected ClassFile(IEGLElement parent, String nameWithoutExtension) {
		super(CLASS_FILE, parent, nameWithoutExtension);
		this.name = nameWithoutExtension;
//		if (!Util.isEGLIRFileName(name)) {
//			throw new IllegalArgumentException(EGLModelResources.conventionUnitNotIRName);
//		}
	}

	@Override
	protected void buildStructure(OpenableElementInfo info, IProgressMonitor monitor) throws EGLModelException {
		// Note: This method seems to be exactly the same as the one it overrides in Openable

		if (monitor != null && monitor.isCanceled()) return;

		// remove existing (old) infos
		removeInfo();

		HashMap newElements = new HashMap(11);
		info.setIsStructureKnown(generateInfos(info, monitor, newElements, getResource()));
		EGLModelManager.getEGLModelManager().getElementsOutOfSynchWithBuffers().remove(this);
		for (Iterator iter = newElements.keySet().iterator(); iter.hasNext();) {
			IEGLElement key = (IEGLElement) iter.next();
			Object value = newElements.get(key);
			EGLModelManager.getEGLModelManager().putInfo(key, value);
		}
		// add the info for this at the end, to ensure that a getInfo cannot reply null in case the LRU cache needs
		// to be flushed. Might lead to performance issues.
		// see PR 1G2K5S7: ITPJCORE:ALL - NPE when accessing source for a binary type
		EGLModelManager.getEGLModelManager().putInfo(this, info);	
	}

	@Override
	protected boolean generateInfos(OpenableElementInfo info, IProgressMonitor pm, Map newElements, IResource underlyingResource)
			throws EGLModelException {
		// put the info now, because getting the contents requires it
		EGLModelManager.getEGLModelManager().putInfo(this, info);
		ClassFileElementInfo unitInfo = (ClassFileElementInfo) info;

		// generate structure
		IRFileStructureRequestor requestor = new IRFileStructureRequestor(this, unitInfo, newElements);
		BinaryElementParser parser = new BinaryElementParser(requestor,underlyingResource.getProject());
		parser.parseDocument(this, true);
//		if (isWorkingCopy()) {
//			EGLFile original = (EGLFile) getOriginalElement();
//			// might be IResource.NULL_STAMP if original does not exist
//			unitInfo.fTimestamp = ((IFile) original.getResource()).getModificationStamp();
//		}
		return unitInfo.isStructureKnown();
	}

	@Override
	protected char getHandleMementoDelimiter() {
		return 0;
	}

	@Override
	public IPath getPath() {
		PackageFragmentRoot root = this.getPackageFragmentRoot();
		if (root.isArchive()) {
			return root.getPath();
		} else {
			return this.getParent().getPath().append(this.getElementName());
		}
	}

	@Override
	public IResource getResource() {
		PackageFragmentRoot root = this.getPackageFragmentRoot();
		if (root.isArchive()) {
			return root.getResource();
		} else {
			return ((IContainer)this.getParent().getResource()).getFile(new Path(this.getElementName()));
		}
	}

	public IEGLElement rootedAt(IEGLProject project) {
		return null;
	}

	public IPart[] getAllParts() throws EGLModelException {
		return getParts();
	}

	public byte[] getBytes() throws EGLModelException {
		EGLElement pkg = (EGLElement) getParent();
		if (pkg instanceof EglarPackageFragment) {
			EglarPackageFragmentRoot root = (EglarPackageFragmentRoot) pkg.getParent();
			ZipFile zip = null;
			try {
				zip = root.getJar();
				String entryName = CharOperation.concatWith(((PackageFragment) pkg).names, getElementName(), '/');
				ZipEntry ze = zip.getEntry(entryName);
				if (ze != null) {
					return org.eclipse.edt.ide.core.internal.model.util.Util.getZipEntryByteContent(ze, zip);
				}
				throw new EGLModelException(new EGLModelStatus(IEGLModelStatusConstants.ELEMENT_DOES_NOT_EXIST, this));
			} catch (IOException ioe) {
				throw new EGLModelException(ioe, IEGLModelStatusConstants.IO_EXCEPTION);
			} catch (CoreException e) {
				if (e instanceof EGLModelException) {
					throw (EGLModelException)e;
				} else {
					throw new EGLModelException(e);
				}
			} finally {
				EGLModelManager.getEGLModelManager().closeZipFile(zip);
			}
		} else {
			IFile file = (IFile) getResource();
			return Util.getResourceContentsAsByteArray(file);
		}
	}

	public IEGLElement getElementAt(int position) throws EGLModelException {
		return null;
	}

	public IPackageDeclaration getPackageDeclaration(String name) {
		return new PackageDeclaration(this, name);
	}

	public IPackageDeclaration[] getPackageDeclarations() throws EGLModelException {
		ArrayList list = getChildrenOfType(PACKAGE_DECLARATION);
		IPackageDeclaration[] array= new IPackageDeclaration[list.size()];
		list.toArray(array);
		return array;
	}

	public String[] getPackageName() {
		IPackageFragment packageFragment = (IPackageFragment)getAncestor(IEGLElement.PACKAGE_FRAGMENT);
		String[] packageName;
		if(packageFragment.isDefaultPackage()){
			packageName = new String[0];
		}else{
			packageName = packageFragment.getElementName().split("\\.");
		}
		
		return packageName;
	}

	public IPart getPart(String name) {
		this.binaryPart = new BinaryPart(this, name);
		return binaryPart;
	}

	public IPart[] getParts() throws EGLModelException {
		ArrayList<IPart> list = getChildrenOfType(PART);
		IPart[] array= new IPart[list.size()];
		list.toArray(array);
		return array;
	}
	public String getElementName() {
		return this.name + EGL2IR.EGLXML;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.edt.ide.core.model.IClassFile#isClass()
	 */
	public boolean isClass() throws EGLModelException {
		return true;
	}

	public String getSource() throws EGLModelException {
		IBuffer buffer = getBuffer();
		if (buffer == null) return ""; //$NON-NLS-1$
		return buffer.getContents();
	}

	public ISourceRange getSourceRange() throws EGLModelException {
		return ((EGLFileElementInfo) getElementInfo()).getSourceRange();
	}

	public void copy(IEGLElement container, IEGLElement sibling, String rename, boolean replace, IProgressMonitor monitor) throws EGLModelException {
		throw new EGLModelException(new EGLModelStatus(IEGLModelStatusConstants.READ_ONLY, this));
	}

	public void delete(boolean force, IProgressMonitor monitor) throws EGLModelException {
		throw new EGLModelException(new EGLModelStatus(IEGLModelStatusConstants.READ_ONLY, this));
	}

	public void move(IEGLElement container, IEGLElement sibling, String rename, boolean replace, IProgressMonitor monitor) throws EGLModelException {
		throw new EGLModelException(new EGLModelStatus(IEGLModelStatusConstants.READ_ONLY, this));
	}

	public void rename(String name, boolean replace, IProgressMonitor monitor) throws EGLModelException {
		throw new EGLModelException(new EGLModelStatus(IEGLModelStatusConstants.READ_ONLY, this));
	}

	public void commit(boolean force, IProgressMonitor monitor) throws EGLModelException {
		throw new EGLModelException(new EGLModelStatus(IEGLModelStatusConstants.READ_ONLY, this));
	}

	public void destroy() {
	}

	public IEGLElement[] findElements(IEGLElement element) {

		return null;
	}

	public IPart findPrimaryPart() {

		return null;
	}

	public IEGLElement findSharedWorkingCopy(IBufferFactory bufferFactory) {

		return null;
	}

	public IEGLElement getOriginal(IEGLElement workingCopyElement) {

		return null;
	}

	public IEGLElement getOriginalElement() {

		return null;
	}

	public IEGLElement getSharedWorkingCopy(IProgressMonitor monitor, IBufferFactory factory, IProblemRequestor problemRequestor)
			throws EGLModelException {
		//TODO
		return null;
//		// if factory is null, default factory must be used
//		if (factory == null) factory = this.getBufferManager().getDefaultBufferFactory();
//
//		EGLModelManager manager = EGLModelManager.getEGLModelManager();
//		
//		// In order to be shared, working copies have to denote the same egl file 
//		// AND use the same buffer factory.
//		// Assuming there is a little set of buffer factories, then use a 2 level Map cache.
//		Map sharedWorkingCopies = manager.sharedWorkingCopies;
//		
//		Map perFactoryWorkingCopies = (Map) sharedWorkingCopies.get(factory);
//		if (perFactoryWorkingCopies == null){
//			perFactoryWorkingCopies = new HashMap();
//			sharedWorkingCopies.put(factory, perFactoryWorkingCopies);
//		}
//		WorkingCopy workingCopy = (WorkingCopy)perFactoryWorkingCopies.get(this);
//		if (workingCopy != null) {
//			workingCopy.useCount++;
//
//			if (SHARED_WC_VERBOSE) {
//				System.out.println("Incrementing use count of shared working copy " + workingCopy.toStringWithAncestors()); //$NON-NLS-1$
//			}
//
//			return workingCopy;
//		} else {
//			CreateWorkingCopyOperation op = new CreateWorkingCopyOperation(this, perFactoryWorkingCopies, factory, problemRequestor);
//			op.runOperation(monitor);
//			return op.getResultElements()[0];
//		}
	}

	public IEGLElement getWorkingCopy() throws EGLModelException {

		return null;
	}

	public IEGLElement getWorkingCopy(IProgressMonitor monitor, IBufferFactory factory, IProblemRequestor problemRequestor) throws EGLModelException {

		return null;
	}

	public boolean isBasedOn(IResource resource) {

		return false;
	}

	public boolean isWorkingCopy() {
		return false;
	}

	public IMarker[] reconcile() throws EGLModelException {

		return null;
	}

	public void reconcile(boolean forceProblemDetection, IProgressMonitor monitor) throws EGLModelException {

	}

	public void restore() throws EGLModelException {

	}

	public byte[] getByteContent() throws IOException {
		try {
			return getBytes();
		} catch (EGLModelException e) {
			return null;
		}
	}

	public char[] getCharContent() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	

	public String getStringContent() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	public IPart getPart() {
		try {
			IPart[] parts = getAllParts();
			if(parts.length > 0) {
				return parts[0];
			}
		} catch (EGLModelException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getTypeName() {
		// Internal class file name doesn't contain ".class" file extension
//		int lastDollar = this.name.lastIndexOf('$');
		return this.name;
//		return lastDollar > -1 ? Util.localTypeName(this.name, lastDollar, this.name.length()) : this.name;
	}

	public String getName() {
		return getFileName().toString();
	}
	
	public char[] getFileName(){
		return getElementName().toCharArray();
	}
	protected OpenableElementInfo createElementInfo() {
		return new ClassFileElementInfo();
	}
	
	public IClassFile getClassFile() {
		return this;
	}
	
	protected boolean hasBuffer() {
		return true;
	}
	
	protected IBuffer openBuffer(IProgressMonitor pm, Object info) {
		// Check the cache for the top-level type first
//		IType outerMostEnclosingType = getOuterMostEnclosingType();
		IBuffer buffer = getBufferManager().getBuffer(this);
		if (buffer == null) {
			SourceMapper mapper = getSourceMapper();
			//info: ClassFileElementInfo
			ClassFileElementInfo classFileInfo = info instanceof ClassFileElementInfo? (ClassFileElementInfo)info : null;
			if (mapper != null) {
				buffer = mapSource(mapper, classFileInfo, this.getClassFile());
			}
		}
		return buffer;
	}
	
	/** Loads the buffer via SourceMapper, and maps it in SourceMapper */
	private IBuffer mapSource(SourceMapper mapper, ClassFileElementInfo info, IClassFile bufferOwner) {
		char[] contents = mapper.findSource(getPart(), info.getEglFileName(), info.getCaseSensitivePackageName());
		// create buffer
		BufferManager bufManager = getBufferManager();
		IBuffer buffer = bufManager.createBuffer(bufferOwner);
		if (buffer == null)
			return null;
		bufManager.addBuffer(buffer);
				
		if (contents != null) {
			// set the buffer source
			if (buffer.getCharacters() == null){
				buffer.setContents(contents);
			}
		} else {
			if (buffer.getCharacters() == null){
				String result = EGLModelResources.eglarNoSourceAttachmentContent;
				buffer.setContents(result.toCharArray());
			}
		}
		
		buffer.addBufferChangedListener(this);
		return buffer;
	}
	
	public SourceMapper getSourceMapper(){
		/*
		 * case 1: if the binary project is imported into workspace, and the eglar file is in this imported binary project,
		 * code enters here when:
		 *   1) have decided this classfile is under BP but no corresponding source file found in the source folder. Enter
		 *      case #3 directly
		 *   2) need to decide if this classfile is under BP (if is BP but no source file available, enter case #3 eventually)
		 * case 2: if project A refers binary project B, and binary project B is used in Target Platform and remains external,
		 * corresponding eglar file of B will be attached as a library of A. To open an ir in project B's eglar, first look 
		 * for external source folder for project B. if B has external source folder and corresponding source file found for 
		 * the ir, then create SourceMapper using the external source folder location; otherwise, goto case #3
		 * case 3: source folder (either internal or external) not available, or no corresponding source file found in the source
		 * folder, then use the source attachment location to create SourceMapper, which should be the same as its parent's
		 * SourceMapper (eventually, this should be the same as the eglar SourceMapper)
		 * 
		 */
		//case 1:
		if(sourceFileSearchRequired && ResourcesPlugin.getWorkspace().getRoot().findMember(this.getPath()) != null){//non-external eglar file
			IPath projPath = this.getEGLProject().getProject().getLocation();	//absolute location in file system
			if(org.eclipse.edt.ide.core.internal.model.util.Util.isBinaryProject(new File(projPath.toString()))){//is binary project
				String[] eglSourceFolders = org.eclipse.edt.ide.core.internal.model.util.Util.getEGLSourceFolders(new File(this.getEGLProject().getProject().getLocation().toString()));
				for(String eglSourceFolder: eglSourceFolders){
					IResource sourceFolder = this.getEGLProject().getProject().findMember(eglSourceFolder);
					if(sourceFolder != null && sourceFolder.exists() && sourceFolder.getType() == IResource.FOLDER){
						String pkgPath;
						try {
							pkgPath = this.getPackageDeclarations()[0].getElementName();
							pkgPath = pkgPath.replace(".", File.separator);
							IResource fullPkgFolder = ((IFolder)sourceFolder).findMember(pkgPath);
							if(fullPkgFolder != null && fullPkgFolder.exists() && fullPkgFolder.getType() == IResource.FOLDER){	//package matches
								ClassFileElementInfo elementInfo = ((ClassFileElementInfo)this.getElementInfo());
								String srcName = elementInfo.getEglFileName();
								IResource sourceFile = ((IFolder)fullPkgFolder).findMember(srcName);
								if(sourceFile == null) {
									IPath path = new Path(srcName);
									srcName = path.lastSegment();
									sourceFile = ((IFolder)fullPkgFolder).findMember(srcName);
								}
								if(sourceFile != null && sourceFile.exists() && sourceFile.getType() == IResource.FILE){	//egl source matches, use the source
									return new SourceMapper(
											sourceFile.getFullPath(),
											null,
											getEGLProject().getOptions(true));
								}
							}
						} catch (EGLModelException e) {
							e.printStackTrace();
						}
						
					}
				}
			}
		}
		//case 2:
		if(ResourcesPlugin.getWorkspace().getRoot().findMember(this.getPath()) == null){	//external eglar file
			String eglarPath = this.getPath().toString();
			IEGLProject eglProj = this.getEGLProject();
			boolean isInBinaryProj = false;
			try {
				IEGLPathEntry[] pathEntries = eglProj.getResolvedEGLPath(true);				
				for(IEGLPathEntry entry: pathEntries){
					//if the entry path represents a binary project, and the entry path
					//is the one we want to open, then look for the binary project's source folder
					if(entry.isBinaryProject() && entry.getPath().equals(this.getPath())){
						isInBinaryProj = true;
						break;
					}
				}
			} catch (EGLModelException e1) {
				e1.printStackTrace();
			}
			if(isInBinaryProj){	//is in binary project
				if(org.eclipse.edt.ide.core.internal.model.Util.isEGLARFileName(eglarPath)){
					int index = eglarPath.lastIndexOf("/");
					if(index == -1){
						index = eglarPath.lastIndexOf(File.separator);
					}
					if(index != -1){
						String projRootPath = eglarPath.substring(0, index);
						String[] eglSourceFolders = org.eclipse.edt.ide.core.internal.model.util.Util.getEGLSourceFolders(new File(projRootPath));
						for(String eglSourceFolder: eglSourceFolders){
							String sourcePath = projRootPath + File.separator + eglSourceFolder;
							//try to find the source file
							try {
								String pkgPath = this.getPackageDeclarations()[0].getElementName();
								index = pkgPath.indexOf(".");
								while(index != -1){
									sourcePath += File.separator + pkgPath.substring(0, index);
									pkgPath = pkgPath.substring(index + 1);
									index = pkgPath.indexOf(".");
								}
								sourcePath += File.separator + pkgPath;
								//the ir file name is not always equal to the source egl file, as one egl file can relate to
								//several ir files (depends on how many Parts in the egl file)
								String srcName = ((ClassFileElementInfo)this.getElementInfo()).getEglFileName();
								if(pkgPath.trim().length() > 0)
									sourcePath += File.separator;
								sourcePath += srcName; 
								return new SourceMapper(
										new Path(sourcePath),
										null,
										getEGLProject().getOptions(true));
							} catch (EGLModelException e) {
								e.printStackTrace();
							}
						}
					}
				}	
			}
		}
		//case 3:
		return super.getSourceMapper();
	}
	
	//find current ClassFile's corresponding eclipse file from source folder
	public IFile getFileInSourceFolder(){
		String[] eglSourceFolders = org.eclipse.edt.ide.core.internal.model.util.Util.getEGLSourceFolders(new File(this.getEGLProject().getProject().getLocation().toString()));
		for(String eglSourceFolder: eglSourceFolders){
			IResource sourceFolder = this.getEGLProject().getProject().findMember(eglSourceFolder);
			if(sourceFolder != null && sourceFolder.exists() && sourceFolder.getType() == IResource.FOLDER){	//source folder exists
				String pkgPath = "";
				IResource fullPkgFolder = null;
				try {
					ClassFileElementInfo elementInfo = ((ClassFileElementInfo)this.getElementInfo());
					String[] pkgs = elementInfo.getCaseSensitivePackageName();
					if(pkgs != null && pkgs.length > 0){
						for(int i=0; i<pkgs.length-1; i++){
							pkgPath += pkgs[i];
							pkgPath += File.separator;
						}
						pkgPath += pkgs[pkgs.length - 1];
						fullPkgFolder = ((IFolder)sourceFolder).findMember(pkgPath);
					}
					else{
						fullPkgFolder = sourceFolder;
					}
					
					if(fullPkgFolder != null && fullPkgFolder.exists() && fullPkgFolder.getType() == IResource.FOLDER){	//package matches
						String srcName = elementInfo.getEglFileName();	//srcName should represent the source file name eliminating package name
						int index = srcName.lastIndexOf("/");
						if(index != -1){
							srcName = srcName.substring(index);
						}
						IResource sourceFile = ((IFolder)fullPkgFolder).findMember(srcName);
						if(sourceFile != null && sourceFile.exists() && sourceFile.getType() == IResource.FILE){	//egl source matches
							return (IFile)sourceFile;
						}
					}
				} catch (EGLModelException e) {
					e.printStackTrace();
				}
				
			}
		}
		
		return null;
	}

	public void setSourceFileSearchRequired(boolean sourceFileSearchRequired) {
		this.sourceFileSearchRequired = sourceFileSearchRequired;
	}

}
