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
package org.eclipse.edt.ide.core.internal.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IBuffer;
import org.eclipse.edt.ide.core.model.IBufferFactory;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLModelStatusConstants;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IFunction;
import org.eclipse.edt.ide.core.model.IImportContainer;
import org.eclipse.edt.ide.core.model.IImportDeclaration;
import org.eclipse.edt.ide.core.model.IMember;
import org.eclipse.edt.ide.core.model.IOpenable;
import org.eclipse.edt.ide.core.model.IPackageDeclaration;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.model.IParent;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.IProblemRequestor;
import org.eclipse.edt.ide.core.model.ISourceManipulation;
import org.eclipse.edt.ide.core.model.ISourceRange;
import org.eclipse.edt.ide.core.model.ISourceReference;
import org.eclipse.edt.ide.core.model.IWorkingCopy;
import org.eclipse.edt.ide.core.model.Signature;

import org.eclipse.edt.compiler.internal.core.utils.CharOperation;
import org.eclipse.edt.ide.core.internal.model.index.IDocument;
/**
 * @see IEGLFile
 */

public class EGLFile extends Openable implements IEGLFile, IDocument {
	
	public static boolean SHARED_WC_VERBOSE = false;
	

/**
 * Constructs a handle to a egl file with the given name in the
 * specified package.
 *
 * @exception IllegalArgumentException if the name of the egl file
 * does not end with ".egl"
 */
protected EGLFile(IPackageFragment parent, String name) {
	super(EGL_FILE, parent, name);
	if (!Util.isEGLFileName(name)) {
		throw new IllegalArgumentException(EGLModelResources.conventionUnitNotEGLName);
	}
}
/**
 * Accepts the given visitor onto the parsed tree of this egl file, after
 * having runned the name resolution.
 * The visitor's corresponding <code>visit</code> method is called with the
 * corresponding parse tree. If the visitor returns <code>true</code>, this method
 * visits this parse node's members.
 *
 * @param visitor the visitor
 * @exception EGLModelException if this method fails. Reasons include:
 * <ul>
 * <li> This element does not exist.</li>
 * <li> The visitor failed with this exception.</li>
 * </ul>
 */
/* TODO Do parse tree visiting later
public void accept(IAbstractSyntaxTreeVisitor visitor) throws EGLModelException {
	EGLFileVisitor.visit(this, visitor);
} 
*/

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

/**
 * @see ICodeAssist#codeComplete(int, ICompletionRequestor)
 */
/*
public void codeComplete(int offset, ICompletionRequestor requestor) throws EGLModelException {
	codeComplete(this, isWorkingCopy() ? (com.ibm.etools.egl.internal.model.internal.compiler.env.IEGLFile) getOriginalElement() : this, offset, requestor);
}
*/
/**
 * @see ICodeAssist#codeSelect(int, int)
 */
/*
public IEGLElement[] codeSelect(int offset, int length) throws EGLModelException {
	return super.codeSelect(this, offset, length);
}
*/
/**
 * @see IWorkingCopy#commit(boolean, IProgressMonitor)
 */

public void commit(boolean force, IProgressMonitor monitor) throws EGLModelException {
	throw new EGLModelException(new EGLModelStatus(IEGLModelStatusConstants.INVALID_ELEMENT_TYPES, this));
}


/**
 * @see ISourceManipulation#copy(IEGLElement, IEGLElement, String, boolean, IProgressMonitor)
 */
public void copy(IEGLElement container, IEGLElement sibling, String rename, boolean force, IProgressMonitor monitor) throws EGLModelException {
	if (container == null) {
		throw new IllegalArgumentException(EGLModelResources.operationNullContainer);
	}
	IEGLElement[] elements = new IEGLElement[] {this};
	IEGLElement[] containers = new IEGLElement[] {container};
	String[] renamings = null;
	if (rename != null) {
		renamings = new String[] {rename};
	}
	getEGLModel().copy(elements, containers, null, renamings, force, monitor);
}
/**
 * Returns a new element info for this element.
 */
protected OpenableElementInfo createElementInfo() {
	return new EGLFileElementInfo();
}
/**
 * @see IEGLFile#createImport(String, IEGLElement, IProgressMonitor)
 */
public IImportDeclaration createImport(String name, IEGLElement sibling, IProgressMonitor monitor) throws EGLModelException {
	return null;
//	CreateImportOperation op = new CreateImportOperation(name, this);
//	if (sibling != null) {
//		op.createBefore(sibling);
//	}
//	runOperation(op, monitor);
//	return getImport(name);
}
/**
 * @see IEGLFile#createPackageDeclaration(String, IProgressMonitor)
 */
public IPackageDeclaration createPackageDeclaration(String name, IProgressMonitor monitor) throws EGLModelException {
	return null;
//	CreatePackageDeclarationOperation op= new CreatePackageDeclarationOperation(name, this);
//	runOperation(op, monitor);
//	return getPackageDeclaration(name);
}
/**
 * @see IEGLFile#createPart(String, IEGLElement, boolean, IProgressMonitor)
 */
// TODO Handle internal part creation later
public IPart createPart(String content, IEGLElement sibling, boolean force, IProgressMonitor monitor) throws EGLModelException {
	return null;
	/*
	if (!exists()) {
		//autogenerate this egl file
		IPackageFragment pkg = (IPackageFragment) getParent();
		String source = ""; //$NON-NLS-1$
		if (pkg.getElementName().length() > 0) {
			//not the default package...add the package declaration
			source = "package " + pkg.getElementName() + ";"  + org.eclipse.jdt.internal.compiler.util.Util.LINE_SEPARATOR + org.eclipse.jdt.internal.compiler.util.Util.LINE_SEPARATOR; //$NON-NLS-1$ //$NON-NLS-2$
		}
		CreateEGLFileOperation op = new CreateEGLFileOperation(pkg, fName, source, force);
		runOperation(op, monitor);
	}
	CreatePartOperation op = new CreatePartOperation(this, content, force);
	if (sibling != null) {
		op.createBefore(sibling);
	}
	runOperation(op, monitor);
	return (IPart) op.getResultElements()[0];
	*/
}

/**
 * @see ISourceManipulation#delete(boolean, IProgressMonitor)
 */
public void delete(boolean force, IProgressMonitor monitor) throws EGLModelException {
	IEGLElement[] elements= new IEGLElement[] {this};
	getEGLModel().delete(elements, force, monitor);
}
/**
 * This is not a working copy, do nothing.
 *
 * @see IWorkingCopy#destroy()
 */
public void destroy() {
}


/**
 * Returns true if this handle represents the same EGL element
 * as the given handle.
 *
 * <p>EGL files must also check working copy state;
 *
 * @see Object#equals(java.lang.Object)
 */
public boolean equals(Object o) {
	return super.equals(o) && !((IEGLFile)o).isWorkingCopy();
}
/**
 * @see IWorkingCopy#findElements(IEGLElement)
 */
public IEGLElement[] findElements(IEGLElement element) {
	ArrayList children = new ArrayList();
	while (element != null && element.getElementType() != IEGLElement.EGL_FILE) {
		children.add(element);
		element = element.getParent();
	}
	if (element == null) return null;
	IEGLElement currentElement = this;
	for (int i = children.size()-1; i >= 0; i--) {
		IEGLElement child = (IEGLElement)children.get(i);
		switch (child.getElementType()) {
			case IEGLElement.PACKAGE_DECLARATION:
				currentElement = ((IEGLFile)currentElement).getPackageDeclaration(child.getElementName());
				break;
			case IEGLElement.IMPORT_CONTAINER:
				currentElement = ((IEGLFile)currentElement).getImportContainer();
				break;
			case IEGLElement.IMPORT_DECLARATION:
				currentElement = ((IImportContainer)currentElement).getImport(child.getElementName());
				break;
			case IEGLElement.PART:
				if (currentElement.getElementType() == IEGLElement.EGL_FILE) {
					currentElement = ((IEGLFile)currentElement).getPart(child.getElementName());
				} else {
					currentElement = ((IPart)currentElement).getPart(child.getElementName());
				}
				break;
			case IEGLElement.FIELD:
				currentElement = ((IPart)currentElement).getField(child.getElementName());
				break;
			case IEGLElement.FUNCTION:
				return ((IPart)currentElement).findFunctions((IFunction)child);
		}
		
	}
	if (currentElement != null && currentElement.exists()) {
		return new IEGLElement[] {currentElement};
	} else {
		return null;
	}
}
/**
 * @see IWorkingCopy#findPrimaryPart()
 */
public IPart findPrimaryPart() {
	String typeName = Signature.getQualifier(this.getElementName());
	IPart primaryPart= this.getPart(typeName);
	if (primaryPart.exists()) {
		return primaryPart;
	}
	return null;
}

/**
 * @see IWorkingCopy#findSharedWorkingCopy(IBufferFactory)
 */
public IEGLElement findSharedWorkingCopy(IBufferFactory factory) {

	// if factory is null, default factory must be used
	if (factory == null) factory = this.getBufferManager().getDefaultBufferFactory();

	// In order to be shared, working copies have to denote the same egl file 
	// AND use the same buffer factory.
	// Assuming there is a little set of buffer factories, then use a 2 level Map cache.
	Map sharedWorkingCopies = EGLModelManager.getEGLModelManager().sharedWorkingCopies;
	
	Map perFactoryWorkingCopies = (Map) sharedWorkingCopies.get(factory);
	if (perFactoryWorkingCopies == null) return null;
	return (WorkingCopy)perFactoryWorkingCopies.get(this);
}

protected boolean generateInfos(OpenableElementInfo info, IProgressMonitor pm, Map newElements, IResource underlyingResource) throws EGLModelException {

		// put the info now, because getting the contents requires it
		EGLModelManager.getEGLModelManager().putInfo(this, info);
		EGLFileElementInfo unitInfo = (EGLFileElementInfo) info;

		// generate structure
		
		EGLFileStructureRequestor requestor = new EGLFileStructureRequestor(this, unitInfo, newElements);
		SourceElementParser parser = new SourceElementParser(requestor);
		parser.parseEGLFile(this);
		if (isWorkingCopy()) {
			EGLFile original = (EGLFile) getOriginalElement();
			// might be IResource.NULL_STAMP if original does not exist
			unitInfo.fTimestamp = ((IFile) original.getResource()).getModificationStamp();
		}
		
		return unitInfo.isStructureKnown();
	
}

/**
 * @see IEGLFile#getAllParts()
 */
public IPart[] getAllParts() throws EGLModelException {
	return getParts();
	/*
	IEGLElement[] types = getParts();
	int i;
	ArrayList allParts = new ArrayList(types.length);
	ArrayList typesToTraverse = new ArrayList(types.length);
	for (i = 0; i < types.length; i++) {
		typesToTraverse.add(types[i]);
	}
	while (!typesToTraverse.isEmpty()) {
		IPart type = (IPart) typesToTraverse.get(0);
		typesToTraverse.remove(type);
		allParts.add(type);
		types = type.getParts();
		for (i = 0; i < types.length; i++) {
			typesToTraverse.add(types[i]);
		}
	} 
	IPart[] arrayOfAllParts = new IPart[allParts.size()];
	allParts.toArray(arrayOfAllParts);
	return arrayOfAllParts;
	*/
}
/**
 * @see IMember#getEGLFile()
 */
public IEGLFile getEGLFile() {
	return this;
}
/**
 * @see org.eclipse.edt.ide.core.model.model.internal.compiler.env.IEGLFile#getContents()
 */
public char[] getContents() {
	try {
		IBuffer buffer = this.getBuffer();
		return buffer == null ? null : buffer.getCharacters();
	} catch (EGLModelException e) {
		return CharOperation.NO_CHAR;
	}
}
/**
 * A egl file has a corresponding resource unless it is contained
 * in a jar.
 *
 * @see IEGLElement#getCorrespondingResource()
 */
public IResource getCorrespondingResource() throws EGLModelException {
	IPackageFragmentRoot root= (IPackageFragmentRoot)getParent().getParent();
	if (root.isArchive()) {
		return null;
	} else {
		return getUnderlyingResource();
	}
}
/**
 * @see IEGLFile#getElementAt(int)
 */
public IEGLElement getElementAt(int position) throws EGLModelException {

	IEGLElement e= getSourceElementAt(position);
	if (e == this) {
		return null;
	} else {
		return e;
	}
}
public char[] getFileName(){
	return getElementName().toCharArray();
}
/**
 * @see EGLElement#getHandleMementoDelimiter()
 */
protected char getHandleMementoDelimiter() {
	return EGLElement.EGLM_EGLFILE;
}
/**
 * @see IEGLFile#getImport(String)
 */
public IImportDeclaration getImport(String name) {
	return new ImportDeclaration(getImportContainer(), name);
}
/**
 * @see IEGLFile#getImportContainer()
 */
public IImportContainer getImportContainer() {
	return new ImportContainer(this);
}


/**
 * @see IEGLFile#getImports()
 */
public IImportDeclaration[] getImports() throws EGLModelException {
	IImportContainer container= getImportContainer();
	if (container.exists()) {
		IEGLElement[] elements= container.getChildren();
		IImportDeclaration[] imprts= new IImportDeclaration[elements.length];
		System.arraycopy(elements, 0, imprts, 0, elements.length);
		return imprts;
	} else if (!exists()) {
			throw newNotPresentException();
	} else {
		return new IImportDeclaration[0];
	}

}
/**
 * @see org.eclipse.edt.ide.core.model.model.internal.compiler.env.IEGLFile#getMainPartName()
 */
public char[] getMainPartName(){
	String name= getElementName();
	//remove the .java
	name= name.substring(0, name.length() - 5);
	return name.toCharArray();
}
/**
 * Returns <code>null</code>, this is not a working copy.
 *
 * @see IWorkingCopy#getOriginal(IEGLElement)
 */
public IEGLElement getOriginal(IEGLElement workingCopyElement) {
	return null;
}
/**
 * Returns <code>null</code>, this is not a working copy.
 *
 * @see IWorkingCopy#getOriginalElement()
 */
public IEGLElement getOriginalElement() {
	return null;
}
/**
 * @see IEGLFile#getPackageDeclaration(String)
 */
public IPackageDeclaration getPackageDeclaration(String name) {
	return new PackageDeclaration(this, name);
}
/**
 * @see IEGLFile#getPackageDeclarations()
 */
public IPackageDeclaration[] getPackageDeclarations() throws EGLModelException {
	ArrayList list = getChildrenOfType(PACKAGE_DECLARATION);
	IPackageDeclaration[] array= new IPackageDeclaration[list.size()];
	list.toArray(array);
	return array;
}
/**
 * @see org.eclipse.edt.ide.core.model.model.internal.compiler.env.IEGLFile#getPackageName()
 */
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
/**
 * @see IEGLElement#getPath()
 */
public IPath getPath() {
	PackageFragmentRoot root = this.getPackageFragmentRoot();
	if (root.isArchive()) {
		return root.getPath();
	} else {
		return this.getParent().getPath().append(this.getElementName());
	}
}
/**
 * @see IEGLElement#getResource()
 */
public IResource getResource() {
	PackageFragmentRoot root = this.getPackageFragmentRoot();
	if (root.isArchive()) {
		return root.getResource();
	} else {
		return ((IContainer)this.getParent().getResource()).getFile(new Path(this.getElementName()));
	}
}

/**
 * @see ISourceReference#getSource()
 */
public String getSource() throws EGLModelException {
	IBuffer buffer = getBuffer();
	if (buffer == null) return ""; //$NON-NLS-1$
	return buffer.getContents();
}
/**
 * @see ISourceReference#getSourceRange()
 */
public ISourceRange getSourceRange() throws EGLModelException {
	return ((EGLFileElementInfo) getElementInfo()).getSourceRange();
}
/**
 * @see IEGLFile#getPart(String)
 */
public IPart getPart(String name) {
	return new SourcePart(this, name);
}
/**
 * @see IEGLFile#getParts()
 */
public IPart[] getParts() throws EGLModelException {
	ArrayList list = getChildrenOfType(PART);
	IPart[] array= new IPart[list.size()];
	list.toArray(array);
	return array;
}
/*
public IResource getUnderlyingResource() throws EGLModelException {
	if (FIX_BUG25184) {
		return super.getUnderlyingResource();
	} else {
		return getResource();
	}
}
*/
/**
 * @see IWorkingCopy#getSharedWorkingCopy(IProgressMonitor, IBufferFactory, IProblemRequestor)
 */

public IEGLElement getSharedWorkingCopy(IProgressMonitor pm, IBufferFactory factory, IProblemRequestor problemRequestor) throws EGLModelException {
	
	// if factory is null, default factory must be used
	if (factory == null) factory = this.getBufferManager().getDefaultBufferFactory();

	EGLModelManager manager = EGLModelManager.getEGLModelManager();
	
	// In order to be shared, working copies have to denote the same egl file 
	// AND use the same buffer factory.
	// Assuming there is a little set of buffer factories, then use a 2 level Map cache.
	Map sharedWorkingCopies = manager.sharedWorkingCopies;
	
	Map perFactoryWorkingCopies = (Map) sharedWorkingCopies.get(factory);
	if (perFactoryWorkingCopies == null){
		perFactoryWorkingCopies = new HashMap();
		sharedWorkingCopies.put(factory, perFactoryWorkingCopies);
	}
	WorkingCopy workingCopy = (WorkingCopy)perFactoryWorkingCopies.get(this);
	if (workingCopy != null) {
		workingCopy.useCount++;

		if (SHARED_WC_VERBOSE) {
			System.out.println("Incrementing use count of shared working copy " + workingCopy.toStringWithAncestors()); //$NON-NLS-1$
		}

		return workingCopy;
	} else {
		CreateWorkingCopyOperation op = new CreateWorkingCopyOperation(this, perFactoryWorkingCopies, factory, problemRequestor);
		op.runOperation(pm);
		return op.getResultElements()[0];
	}
}

/**
 * @see IWorkingCopy#getWorkingCopy()
 */
public IEGLElement getWorkingCopy() throws EGLModelException {
	return this.getWorkingCopy(null, null, null);
}

/**
 * @see IWorkingCopy#getWorkingCopy(IProgressMonitor, IBufferFactory, IProblemRequestor)
 */
public IEGLElement getWorkingCopy(IProgressMonitor pm, IBufferFactory factory, IProblemRequestor problemRequestor) throws EGLModelException {
	CreateWorkingCopyOperation op = new CreateWorkingCopyOperation(this, null, factory, problemRequestor);
	op.runOperation(pm);
	return op.getResultElements()[0];
}

/**
 * @see Openable#hasBuffer()
 */
protected boolean hasBuffer() {
	return true;
}
/**
 * If I am not open, return true to avoid parsing.
 *
 * @see IParent#hasChildren()
 */
public boolean hasChildren() throws EGLModelException {
	if (isOpen()) {
		return getChildren().length > 0;
	} else {
		return true;
	}
}
/**
 * Returns false, this is not a working copy.
 *
 * @see IWorkingCopy#isBasedOn(IResource)
 */
public boolean isBasedOn(IResource resource) {
	return false;
}
/**
 * @see IOpenable#isConsistent()
 */
public boolean isConsistent() throws EGLModelException {
	return EGLModelManager.getEGLModelManager().getElementsOutOfSynchWithBuffers().get(this) == null;
}
/**
 * @see Openable#isSourceElement()
 */
protected boolean isSourceElement() {
	return true;
}
/**
 * @see IWorkingCopy#isWorkingCopy()
 */
public boolean isWorkingCopy() {
	return false;
}
/**
 * @see IOpenable#makeConsistent(IProgressMonitor)
 */
public void makeConsistent(IProgressMonitor monitor) throws EGLModelException {
	if (!isConsistent()) { // TODO: this code isn't synchronized with regular opening of a working copy
		// create a new info and make it the current info
		OpenableElementInfo info = createElementInfo();
		buildStructure(info, monitor);
	}
}

/**
 * @see ISourceManipulation#move(IEGLElement, IEGLElement, String, boolean, IProgressMonitor)
 */
public void move(IEGLElement container, IEGLElement sibling, String rename, boolean force, IProgressMonitor monitor) throws EGLModelException {
	if (container == null) {
		throw new IllegalArgumentException(EGLModelResources.operationNullContainer);
	}
	IEGLElement[] elements= new IEGLElement[] {this};
	IEGLElement[] containers= new IEGLElement[] {container};
	
	String[] renamings= null;
	if (rename != null) {
		renamings= new String[] {rename};
	}
	getEGLModel().move(elements, containers, null, renamings, force, monitor);
}

/**
 * @see Openable#openBuffer(IProgressMonitor)
 */
protected IBuffer openBuffer(IProgressMonitor pm, Object info) throws EGLModelException {

	// create buffer -  egl files only use default buffer factory
	BufferManager bufManager = getBufferManager();
	IBuffer buffer = getBufferFactory().createBuffer(this);
	if (buffer == null) return null;
	
	// set the buffer source
	if (buffer.getCharacters() == null){
		IFile file = (IFile)this.getResource();
		if (file == null || !file.exists()) throw newNotPresentException();
		buffer.setContents(Util.getResourceContentsAsCharArray(file));
	}

	// add buffer to buffer cache
	bufManager.addBuffer(buffer);
			
	// listen to buffer changes
	buffer.addBufferChangedListener(this);
	
	return buffer;
}
protected void openParent(IProgressMonitor pm) throws EGLModelException {
	try {
		super.openParent(pm);
	} catch(EGLModelException e){
		// allow parent to not exist for egl files defined outside eglpath
		if (!e.isDoesNotExist()){ 
			throw e;
		}
	}
}

protected boolean parentExists() {
	return true; // tolerate units outside eglpath
}


/**
 * @see IWorkingCopy#reconcile()
 */
public IMarker[] reconcile() throws EGLModelException {
	// Reconciling is not supported on non working copies
	return null;
}

/**
 * @see IWorkingCopy#reconcile(boolean, IProgressMonitor)
 */
public void reconcile(
	boolean forceProblemDetection,
	IProgressMonitor monitor)
	throws EGLModelException {
	// Reconciling is not supported on non working copies
}

/**
 * @see ISourceManipulation#rename(String, boolean, IProgressMonitor)
 */
public void rename(String name, boolean force, IProgressMonitor monitor) throws EGLModelException {
	if (name == null) {
		throw new IllegalArgumentException(EGLModelResources.operationNullName);
	}
	IEGLElement[] elements= new IEGLElement[] {this};
	IEGLElement[] dests= new IEGLElement[] {this.getParent()};
	String[] renamings= new String[] {name};
	getEGLModel().rename(elements, dests, renamings, force, monitor);

}
/**
 * Does nothing - this is not a working copy.
 *
 * @see IWorkingCopy#restore()
 */
public void restore () throws EGLModelException {
}
/**
 * @see ICodeAssist#codeComplete(int, ICodeCompletionRequestor)
 * @deprecated - use codeComplete(int, ICompletionRequestor)
 */
/*
public void codeComplete(int offset, final ICodeCompletionRequestor requestor) throws EGLModelException {
	
	if (requestor == null){
		codeComplete(offset, (ICompletionRequestor)null);
		return;
	}
	codeComplete(
		offset,
		new ICompletionRequestor(){
			public void acceptAnonymousPart(char[] superPartPackageName,char[] superPartName,char[][] parameterPackageNames,char[][] parameterPartNames,char[][] parameterNames,char[] completionName,int modifiers,int completionStart,int completionEnd, int relevance){
			}
			public void acceptClass(char[] packageName, char[] className, char[] completionName, int modifiers, int completionStart, int completionEnd, int relevance) {
				requestor.acceptClass(packageName, className, completionName, modifiers, completionStart, completionEnd);
			}
			public void acceptError(IProblem error) {
				if (true) return; // was disabled in 1.0

				try {
					IMarker marker = ResourcesPlugin.getWorkspace().getRoot().createMarker(IEGLModelMarker.TRANSIENT_PROBLEM);
					marker.setAttribute(IEGLModelMarker.ID, error.getID());
					marker.setAttribute(IMarker.CHAR_START, error.getSourceStart());
					marker.setAttribute(IMarker.CHAR_END, error.getSourceEnd() + 1);
					marker.setAttribute(IMarker.LINE_NUMBER, error.getSourceLineNumber());
					marker.setAttribute(IMarker.MESSAGE, error.getMessage());
					marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
					requestor.acceptError(marker);
				} catch(CoreException e){
				}
			}
			public void acceptField(char[] declaringPartPackageName, char[] declaringPartName, char[] name, char[] typePackageName, char[] typeName, char[] completionName, int modifiers, int completionStart, int completionEnd, int relevance) {
				requestor.acceptField(declaringPartPackageName, declaringPartName, name, typePackageName, typeName, completionName, modifiers, completionStart, completionEnd);
			}
			public void acceptInterface(char[] packageName,char[] interfaceName,char[] completionName,int modifiers,int completionStart,int completionEnd, int relevance) {
				requestor.acceptInterface(packageName, interfaceName, completionName, modifiers, completionStart, completionEnd);
			}
			public void acceptKeyword(char[] keywordName,int completionStart,int completionEnd, int relevance){
				requestor.acceptKeyword(keywordName, completionStart, completionEnd);
			}
			public void acceptLabel(char[] labelName,int completionStart,int completionEnd, int relevance){
				requestor.acceptLabel(labelName, completionStart, completionEnd);
			}
			public void acceptLocalVariable(char[] name,char[] typePackageName,char[] typeName,int modifiers,int completionStart,int completionEnd, int relevance){
				// ignore
			}
			public void acceptMethod(char[] declaringPartPackageName,char[] declaringPartName,char[] selector,char[][] parameterPackageNames,char[][] parameterPartNames,char[][] parameterNames,char[] returnPartPackageName,char[] returnPartName,char[] completionName,int modifiers,int completionStart,int completionEnd, int relevance){
				// skip parameter names
				requestor.acceptMethod(declaringPartPackageName, declaringPartName, selector, parameterPackageNames, parameterPartNames, returnPartPackageName, returnPartName, completionName, modifiers, completionStart, completionEnd);
			}
			public void acceptMethodDeclaration(char[] declaringPartPackageName,char[] declaringPartName,char[] selector,char[][] parameterPackageNames,char[][] parameterPartNames,char[][] parameterNames,char[] returnPartPackageName,char[] returnPartName,char[] completionName,int modifiers,int completionStart,int completionEnd, int relevance){
				// ignore
			}
			public void acceptModifier(char[] modifierName,int completionStart,int completionEnd, int relevance){
				requestor.acceptModifier(modifierName, completionStart, completionEnd);
			}
			public void acceptPackage(char[] packageName,char[] completionName,int completionStart,int completionEnd, int relevance){
				requestor.acceptPackage(packageName, completionName, completionStart, completionEnd);
			}
			public void acceptPart(char[] packageName,char[] typeName,char[] completionName,int completionStart,int completionEnd, int relevance){
				requestor.acceptPart(packageName, typeName, completionName, completionStart, completionEnd);
			}
			public void acceptVariableName(char[] typePackageName,char[] typeName,char[] name,char[] completionName,int completionStart,int completionEnd, int relevance){
				// ignore
			}
		});
}
*/
/**
 * @see EGLElement#rootedAt(IEGLProject)
 */
public IEGLElement rootedAt(IEGLProject project) {
	return
		new EGLFile(
			(IPackageFragment)((EGLElement)fParent).rootedAt(project), 
			fName);
}
public String sourceLocator() {
	return getResource().getProjectRelativePath().toString();
}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.core.index.IDocument#getByteContent()
	 */
	public byte[] getByteContent() throws IOException {
		// EGLFiles are not currently in binary form
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.core.index.IDocument#getCharContent()
	 */
	public char[] getCharContent() throws IOException {
		return getContents();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.core.index.IDocument#getEncoding()
	 */
	public String getEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.core.index.IDocument#getName()
	 */
	public String getName() {
		return getFileName().toString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.core.index.IDocument#getStringContent()
	 */
	public String getStringContent() throws IOException {
		try {
			IBuffer buffer = this.getBuffer();
			return buffer == null ? null : buffer.getContents();
		} catch (EGLModelException e) {
			return ""; //$NON-NLS-1$
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.core.index.IDocument#getType()
	 */
	public String getType() {
		// TODO get type from file extension
		return "egl"; //$NON-NLS-1$
	}

}
