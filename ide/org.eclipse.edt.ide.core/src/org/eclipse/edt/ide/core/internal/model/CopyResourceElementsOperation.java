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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IBuffer;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLElementDelta;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLModelStatus;
import org.eclipse.edt.ide.core.model.IEGLModelStatusConstants;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageDeclaration;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.model.ISourceRange;


/**
 * @author mattclem
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
/**
 * This operation copies/moves/renames a collection of resources from their current
 * container to a new container, optionally renaming the
 * elements.
 * <p>Notes:<ul>
 *    <li>If there is already an resource with the same name in
 *    the new container, the operation either overwrites or aborts,
 *    depending on the collision policy setting. The default setting is
 *	  abort.
 *
 *    <li>When a compilation unit is copied to a new package, the
 *    package declaration in the compilation unit is automatically updated.
 *
 *    <li>The collection of elements being copied must all share the
 *    same type of container.
 *
 *    <li>This operation can be used to copy and rename elements within
 *    the same container. 
 *
 *    <li>This operation only copies compilation units and package fragments.
 *    It does not copy package fragment roots - a platform operation must be used for that.
 * </ul>
 *
 */
public class CopyResourceElementsOperation extends MultiOperation {
	/**
	 * A collection of renamed compilation units.  These cus do
	 * not need to be saved as they no longer exist.
	 */
	protected ArrayList fRenamedCompilationUnits = null;
	/**
	 * Table specifying deltas for elements being 
	 * copied/moved/renamed. Keyed by elements' project(s), and
	 * values are the corresponding deltas.
	 */
	protected Map fDeltasPerProject= new HashMap(1);
//	/**
//	 * The <code>DOMFactory</code> used to manipulate the source code of
//	 * <code>ICompilationUnit</code>.
//	 */
//	protected DOMFactory fFactory;
	/**
	 * The list of new resources created during this operation.
	 */
	protected ArrayList fCreatedElements;
	/**
	 * When executed, this operation will copy the given resources to the
	 * given containers.  The resources and destination containers must be in
	 * the correct order. If there is > 1 destination, the number of destinations
	 * must be the same as the number of resources being copied/moved.
	 */
	public CopyResourceElementsOperation(IEGLElement[] resourcesToCopy, IEGLElement[] destContainers, boolean force) {
		super(resourcesToCopy, destContainers, force);
//		fFactory = new DOMFactory();
	}
	/**
	 * When executed, this operation will copy the given resources to the
	 * given container.
	 */
	public CopyResourceElementsOperation(IEGLElement[] resourcesToCopy, IEGLElement destContainer, boolean force) {
		this(resourcesToCopy, new IEGLElement[]{destContainer}, force);
	}
	/**
	 * Returns the children of <code>source</code> which are affected by this operation.
	 * If <code>source</code> is a <code>K_SOURCE</code>, these are the <code>.java</code>
	 * files, if it is a <code>K_BINARY</code>, they are the <code>.class</code> files.
	 */
	protected IResource[] collectResourcesOfInterest(IPackageFragment source) throws EGLModelException {
		IEGLElement[] children = source.getChildren();
		int childOfInterest = IEGLElement.EGL_FILE;
		if (source.getKind() == IPackageFragmentRoot.K_BINARY) {
			childOfInterest = IEGLElement.CLASS_FILE;
		}
		ArrayList correctKindChildren = new ArrayList(children.length);
		for (int i = 0; i < children.length; i++) {
			IEGLElement child = children[i];
			if (child.getElementType() == childOfInterest) {
				correctKindChildren.add(child.getResource());
			}
		}
		// Gather non-java resources
		Object[] nonEGLResources = source.getNonEGLResources();
		int actualNonEGLResourceCount = 0;
		for (int i = 0, max = nonEGLResources.length; i < max; i++){
			if (nonEGLResources[i] instanceof IResource) actualNonEGLResourceCount++;
		}
		IResource[] actualNonEGLResources = new IResource[actualNonEGLResourceCount];
		for (int i = 0, max = nonEGLResources.length, index = 0; i < max; i++){
			if (nonEGLResources[i] instanceof IResource) actualNonEGLResources[index++] = (IResource)nonEGLResources[i];
		}
		
		if (actualNonEGLResourceCount != 0) {
			int correctKindChildrenSize = correctKindChildren.size();
			IResource[] result = new IResource[correctKindChildrenSize + actualNonEGLResourceCount];
			correctKindChildren.toArray(result);
			System.arraycopy(actualNonEGLResources, 0, result, correctKindChildrenSize, actualNonEGLResourceCount);
			return result;
		} else {
			IResource[] result = new IResource[correctKindChildren.size()];
			correctKindChildren.toArray(result);
			return result;
		}
	}
	/**
	 * Creates any destination package fragment(s) which do not exists yet.
	 */
	@SuppressWarnings("deprecation")
	protected void createNeededPackageFragments(IContainer sourceFolder, IPackageFragmentRoot root, String newFragName, boolean moveFolder) throws EGLModelException {
		IContainer parentFolder = (IContainer) root.getResource();
		EGLElementDelta projectDelta = null;
		String[] names = Util.getTrimmedSimpleNames(newFragName);
		StringBuffer sideEffectPackageName = new StringBuffer();
		char[][] exclusionsPatterns = ((PackageFragmentRoot)root).fullExclusionPatternChars();
		for (int i = 0; i < names.length; i++) {
			String subFolderName = names[i];
			sideEffectPackageName.append(subFolderName);
			IResource subFolder = parentFolder.findMember(subFolderName);
			if (subFolder == null) {
				// create deepest folder only if not a move (folder will be moved in processPackageFragmentResource)
				if (!(moveFolder && i == names.length-1)) {
					createFolder(parentFolder, subFolderName, fForce);
				}
				parentFolder = parentFolder.getFolder(new Path(subFolderName));
				sourceFolder = sourceFolder.getFolder(new Path(subFolderName));
				if (sourceFolder.isReadOnly()) {
					parentFolder.setReadOnly(true);
				}
				IPackageFragment sideEffectPackage = root.getPackageFragment(sideEffectPackageName.toString());
				if (i < names.length - 1 // all but the last one are side effect packages
						&& !Util.isExcluded(parentFolder, exclusionsPatterns)) { 
					if (projectDelta == null) {
						projectDelta = getDeltaFor(root.getEGLProject());
					}
					projectDelta.added(sideEffectPackage);
				}
				fCreatedElements.add(sideEffectPackage);
			} else {
				parentFolder = (IContainer) subFolder;
			}
			sideEffectPackageName.append('.');
		}
	}
	/**
	 * Returns the <code>JavaElementDelta</code> for <code>javaProject</code>,
	 * creating it and putting it in <code>fDeltasPerProject</code> if
	 * it does not exist yet.
	 */
	private EGLElementDelta getDeltaFor(IEGLProject eglProject) {
		EGLElementDelta delta = (EGLElementDelta) fDeltasPerProject.get(eglProject);
		if (delta == null) {
			delta = new EGLElementDelta(eglProject);
			fDeltasPerProject.put(eglProject, delta);
		}
		return delta;
	}
	/**
	 * @see MultiOperation
	 */
	protected String getMainTaskName() {
		return EGLModelResources.operationCopyResourceProgress;
	}
	/**
	 * Sets the deltas to register the changes resulting from this operation
	 * for this source element and its destination.
	 * If the operation is a cross project operation<ul>
	 * <li>On a copy, the delta should be rooted in the dest project
	 * <li>On a move, two deltas are generated<ul>
	 * 			<li>one rooted in the source project
	 *			<li>one rooted in the destination project</ul></ul>
	 * If the operation is rooted in a single project, the delta is rooted in that project
	 * 	 
	 */
	protected void prepareDeltas(IEGLElement sourceElement, IEGLElement destinationElement, boolean isMove) {
		if (Util.isExcluded(sourceElement) || Util.isExcluded(destinationElement)) return;
		IEGLProject destProject = destinationElement.getEGLProject();
		if (isMove) {
			IEGLProject sourceProject = sourceElement.getEGLProject();
			getDeltaFor(sourceProject).movedFrom(sourceElement, destinationElement);
			getDeltaFor(destProject).movedTo(destinationElement, sourceElement);
		} else {
			getDeltaFor(destProject).added(destinationElement);
		}
	}
	/**
	 * Copies/moves a compilation unit with the name <code>newCUName</code>
	 * to the destination package.<br>
	 * The package statement in the compilation unit is updated if necessary.
	 * The main type of the compilation unit is renamed if necessary.
	 *
	 * @exception JavaModelException if the operation is unable to
	 * complete
	 */
	private void processCompilationUnitResource(IEGLFile source, IPackageFragment dest) throws EGLModelException {
		String newCUName = getNewNameFor(source);
		String destName = (newCUName != null) ? newCUName : source.getElementName();
		String newContent = updatedContent(source, dest); // null if unchanged
	
		// copy resource
		IFile sourceResource = (IFile)(source.isWorkingCopy() ? source.getOriginalElement() : source).getResource();
		IContainer destFolder = (IContainer)dest.getResource(); // can be an IFolder or an IProject
		IFile destFile = destFolder.getFile(new Path(destName));
		if (!destFile.equals(sourceResource)) {
			try {
				if (destFile.exists()) {
					if (fForce) {
						// we can remove it
						deleteResource(destFile, IResource.KEEP_HISTORY);
					} else {
						// abort
						throw new EGLModelException(new EGLModelStatus(
							IEGLModelStatusConstants.NAME_COLLISION, 
							EGLModelResources.bind(EGLModelResources.statusNameCollision, destFile.getFullPath().toString())));
					}
				}
				int flags = fForce ? IResource.FORCE : IResource.NONE;
				if (this.isMove()) {
					flags |= IResource.KEEP_HISTORY;
					sourceResource.move(destFile.getFullPath(), flags, getSubProgressMonitor(1));
				} else {
					if (newContent != null) flags |= IResource.KEEP_HISTORY;
					sourceResource.copy(destFile.getFullPath(), flags, getSubProgressMonitor(1));
				}
				this.setAttribute(HAS_MODIFIED_RESOURCE_ATTR, TRUE); 
			} catch (EGLModelException e) {
				throw e;
			} catch (CoreException e) {
				throw new EGLModelException(e);
			}
	
			// update new resource content
			try {
				if (newContent != null){
					String encoding = source.getEGLProject().getOption(EGLCore.CORE_ENCODING, true);
					destFile.setContents(
						new ByteArrayInputStream(encoding == null ? newContent.getBytes() : newContent.getBytes(encoding)), 
						fForce ? IResource.FORCE | IResource.KEEP_HISTORY : IResource.KEEP_HISTORY,
						getSubProgressMonitor(1));
				}
			} catch(IOException e) {
				throw new EGLModelException(e, IEGLModelStatusConstants.IO_EXCEPTION);
			} catch (CoreException e) {
				throw new EGLModelException(e);
			}
		
			// register the correct change deltas
			IEGLFile destCU = dest.getEGLFile(destName);
			prepareDeltas(source, destCU, isMove());
			if (newCUName != null) {
				//the main type has been renamed
				String oldName = source.getElementName();
				oldName = oldName.substring(0, oldName.length() - 5);
				String newName = newCUName;
				newName = newName.substring(0, newName.length() - 5);
				prepareDeltas(source.getPart(oldName), destCU.getPart(newName), isMove());
			}
		} else {
			if (!fForce) {
				throw new EGLModelException(new EGLModelStatus(
					IEGLModelStatusConstants.NAME_COLLISION, 
					EGLModelResources.bind(EGLModelResources.statusNameCollision, destFile.getFullPath().toString())));
			}
			// update new resource content
			// in case we do a saveas on the same resource we have to simply update the contents
			// see http://dev.eclipse.org/bugs/show_bug.cgi?id=9351
//			try {
//				if (newContent != null){
//					String encoding = source.getJavaProject().getOption(JavaCore.CORE_ENCODING, true);
//					destFile.setContents(
//						new ByteArrayInputStream(encoding == null ? newContent.getBytes() : newContent.getBytes(encoding)), 
//						fForce ? IResource.FORCE | IResource.KEEP_HISTORY : IResource.KEEP_HISTORY, 
//						getSubProgressMonitor(1));
//				}
//			} catch(IOException e) {
//				throw new JavaModelException(e, IJavaModelStatusConstants.IO_EXCEPTION);
//			} catch (CoreException e) {
//				throw new JavaModelException(e);
//			}
		}
	}
	/**
	 * Process all of the changed deltas generated by this operation.
	 */
	protected void processDeltas() {
		for (Iterator deltas = this.fDeltasPerProject.values().iterator(); deltas.hasNext();){
			addDelta((IEGLElementDelta) deltas.next());
		}
	}
	/**
	 * @see MultiOperation
	 * This method delegates to <code>processCompilationUnitResource</code> or
	 * <code>processPackageFragmentResource</code>, depending on the type of
	 * <code>element</code>.
	 */
	protected void processElement(IEGLElement element) throws EGLModelException {
		IEGLElement dest = getDestinationParent(element);
		switch (element.getElementType()) {
			case IEGLElement.EGL_FILE :
				processCompilationUnitResource((IEGLFile) element, (IPackageFragment) dest);
				fCreatedElements.add(((IPackageFragment) dest).getEGLFile(element.getElementName()));
				break;
			case IEGLElement.PACKAGE_FRAGMENT :
				processPackageFragmentResource((IPackageFragment) element, (IPackageFragmentRoot) dest, getNewNameFor(element));
				break;
			default :
				throw new EGLModelException(new EGLModelStatus(IEGLModelStatusConstants.INVALID_ELEMENT_TYPES, element));
		}
	}
	/**
	 * @see MultiOperation
	 * Overridden to allow special processing of <code>JavaElementDelta</code>s
	 * and <code>fResultElements</code>.
	 */
	protected void processElements() throws EGLModelException {
		fCreatedElements = new ArrayList(fElementsToProcess.length);
		try {
			super.processElements();
		} catch (EGLModelException jme) {
			throw jme;
		} finally {
			fResultElements = new IEGLElement[fCreatedElements.size()];
			fCreatedElements.toArray(fResultElements);
			processDeltas();
		}
	}
	/**
	 * Copies/moves a package fragment with the name <code>newName</code>
	 * to the destination package.<br>
	 *
	 * @exception JavaModelException if the operation is unable to
	 * complete
	 */
	private void processPackageFragmentResource(IPackageFragment source, IPackageFragmentRoot root, String newName) throws EGLModelException {
		try {
			String newFragName = (newName == null) ? source.getElementName() : newName;
			IPackageFragment newFrag = root.getPackageFragment(newFragName);
			IResource[] resources = collectResourcesOfInterest(source);
			
			// if isMove() can we move the folder itself ? (see http://bugs.eclipse.org/bugs/show_bug.cgi?id=22458)
			boolean shouldMoveFolder = isMove() && !newFrag.getResource().exists(); // if new pkg fragment exists, it is an override
			IFolder srcFolder = (IFolder)source.getResource();
			IPath destPath = newFrag.getPath();
			if (shouldMoveFolder) {
				// check if destination is not included in source
				if (srcFolder.getFullPath().isPrefixOf(destPath)) {
					shouldMoveFolder = false;
				} else {
					// check if there are no sub-packages
					IResource[] members = srcFolder.members();
					for (int i = 0; i < members.length; i++) {
						if ( members[i] instanceof IFolder) {
							shouldMoveFolder = false;
							break;
						}
					}
				}	
			}
			createNeededPackageFragments((IContainer) source.getParent().getResource(), root, newFragName, shouldMoveFolder);
	
			// Process resources
			if (shouldMoveFolder) {
				// move underlying resource
				srcFolder.move(destPath, fForce, true /* keep history */, getSubProgressMonitor(1));
				this.setAttribute(HAS_MODIFIED_RESOURCE_ATTR, TRUE); 
			} else {
				// process the leaf resources
				if (resources.length > 0) {
					if (isRename()) {
						if (! destPath.equals(source.getPath())) {
							moveResources(resources, destPath);
						}
					} else if (isMove()) {
						// we need to delete this resource if this operation wants to override existing resources
						for (int i = 0, max = resources.length; i < max; i++) {
							IResource destinationResource = ResourcesPlugin.getWorkspace().getRoot().findMember(destPath.append(resources[i].getName()));
							if (destinationResource != null) {
								if (fForce) {
									deleteResource(destinationResource, IResource.KEEP_HISTORY);
								} else {
									throw new EGLModelException(new EGLModelStatus(
										IEGLModelStatusConstants.NAME_COLLISION, 
										EGLModelResources.bind(EGLModelResources.statusNameCollision, destinationResource.getFullPath().toString())));
								}
							}
						}
						moveResources(resources, destPath);
					} else {
						// we need to delete this resource if this operation wants to override existing resources
						for (int i = 0, max = resources.length; i < max; i++) {
							IResource destinationResource = ResourcesPlugin.getWorkspace().getRoot().findMember(destPath.append(resources[i].getName()));
							if (destinationResource != null) {
								if (fForce) {
									// we need to delete this resource if this operation wants to override existing resources
									deleteResource(destinationResource, IResource.KEEP_HISTORY);
								} else {
									throw new EGLModelException(new EGLModelStatus(
										IEGLModelStatusConstants.NAME_COLLISION, 
										EGLModelResources.bind(EGLModelResources.statusNameCollision, destinationResource.getFullPath().toString())));
								}
							}
						}
						copyResources(resources, destPath);
					}
				}
			}
	
			// Discard empty old package (if still empty after the rename)
			boolean isEmpty = true;
			if (isMove()) {
				// delete remaining files in this package (.class file in the case where Proj=src=bin)
				if (srcFolder.exists()) {
					IResource[] remaingFiles = srcFolder.members();
					for (int i = 0, length = remaingFiles.length; i < length; i++) {
						IResource file = remaingFiles[i];
						if (file instanceof IFile) {
							this.deleteResource(file, IResource.FORCE | IResource.KEEP_HISTORY);
						} else {
							isEmpty = false;
						}
					}
				}
				if (isEmpty) {
					IResource rootResource;
					// check if source is included in destination
					if (destPath.isPrefixOf(srcFolder.getFullPath())) {
						rootResource = newFrag.getResource();
					} else {
						rootResource =  source.getParent().getResource();
					}
					
					// delete recursively empty folders
					deleteEmptyPackageFragment(source, false, rootResource);
				}
			}
	
			// Update package statement in compilation unit if needed
//			if (!newFrag.getElementName().equals(source.getElementName())) { // if package has been renamed, update the compilation units
//				for (int i = 0; i < resources.length; i++) {
//					if (resources[i].getName().endsWith(".java")) { //$NON-NLS-1$
//						// we only consider potential compilation units
//						ICompilationUnit cu = newFrag.getCompilationUnit(resources[i].getName());
//						IDOMCompilationUnit domCU = fFactory.createCompilationUnit(cu.getSource(), cu.getElementName());
//						if (domCU != null) {
//							updatePackageStatement(domCU, newFragName);
//							IBuffer buffer = cu.getBuffer();
//							if (buffer == null) continue;
//							String bufferContents = buffer.getContents();
//							if (bufferContents == null) continue;
//							String domCUContents = domCU.getContents();
//							String cuContents = null;
//							if (domCUContents != null) {
//								cuContents = org.eclipse.jdt.internal.core.Util.normalizeCRs(domCU.getContents(), bufferContents);
//							} else {
//								// See PR http://dev.eclipse.org/bugs/show_bug.cgi?id=11285
//								cuContents = bufferContents;//$NON-NLS-1$
//							}
//							buffer.setContents(cuContents);
//							cu.save(null, false);
//						}
//					}
//				}
//			}
	
			//register the correct change deltas
			prepareDeltas(source, newFrag, isMove() && isEmpty);
//		} catch (DOMException dom) {
//			throw new JavaModelException(dom, IJavaModelStatusConstants.DOM_EXCEPTION);
		} catch (EGLModelException e) {
			throw e;
		} catch (CoreException ce) {
			throw new EGLModelException(ce);
		}
	}
	/**
	 * Updates the content of <code>cu</code>, modifying the type name and/or package
	 * declaration as necessary.
	 *
	 * @return the new source
	 */
	private String updatedContent(IEGLFile cu, IPackageFragment dest) throws EGLModelException {
		String currPackageName = cu.getParent().getElementName();
		String destPackageName = dest.getElementName();
		if (currPackageName.equals(destPackageName)) {
			return null; //nothing to change
		} else {
			IBuffer buffer = cu.getBuffer();
			if (buffer == null) return null;
			char[] contents = buffer.getCharacters();
			if (contents == null) return null;
			StringBuffer sb = new StringBuffer(buffer.getContents());
			updatePackageStatement(cu, sb, destPackageName);
			return sb.toString();
		}
	}
	/**
	 * Makes sure that <code>cu</code> declares to be in the <code>pkgName</code> package.
	 */
	private void updatePackageStatement(IEGLFile cu, StringBuffer buffer, String pkgName) throws EGLModelException {
		boolean defaultPackage = pkgName.equals(IPackageFragment.DEFAULT_PACKAGE_NAME);
		boolean seenPackageNode = false;
		
		for(IPackageDeclaration packageDeclaration : cu.getPackageDeclarations()) {
			ISourceRange sourceRange = packageDeclaration.getSourceRange();
			if (!defaultPackage) {
				buffer.replace(sourceRange.getOffset(), sourceRange.getOffset()+sourceRange.getLength(), "package " + pkgName + ";"); //$NON-NLS-1$ //$NON-NLS-2$				
			} else {
				buffer.replace(sourceRange.getOffset(), sourceRange.getOffset()+sourceRange.getLength(), "");
			}
			seenPackageNode = true;
			break;
		}
		if (!seenPackageNode && !defaultPackage) {
			//the cu was in a default package...no package declaration
			//create the new package declaration as the first child of the cu
			buffer.replace(0, 0, "package " + pkgName + ";" + System.getProperty("line.separator")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$			
		}
	}
		/**
		 * Renames the main type in <code>cu</code>.
		 */
//		private void updateTypeName(ICompilationUnit cu, IDOMCompilationUnit domCU, String oldName, String newName) throws JavaModelException {
//			if (newName != null) {
//				if (fRenamedCompilationUnits == null) {
//					fRenamedCompilationUnits= new ArrayList(1);
//				}
//				fRenamedCompilationUnits.add(cu);
//				String oldTypeName= oldName.substring(0, oldName.length() - 5);
//				String newTypeName= newName.substring(0, newName.length() - 5);
//				// update main type name
//				IType[] types = cu.getTypes();
//				for (int i = 0, max = types.length; i < max; i++) {
//					IType currentType = types[i];
//					if (currentType.getElementName().equals(oldTypeName)) {
//						IDOMNode typeNode = ((JavaElement) currentType).findNode(domCU);
//						if (typeNode != null) {
//							typeNode.setName(newTypeName);
//						}
//					}
//				}
//			}
//		}
	/**
	 * Possible failures:
	 * <ul>
	 *  <li>NO_ELEMENTS_TO_PROCESS - no elements supplied to the operation
	 *	<li>INDEX_OUT_OF_BOUNDS - the number of renamings supplied to the operation
	 *		does not match the number of elements that were supplied.
	 * </ul>
	 */
	protected IEGLModelStatus verify() {
		IEGLModelStatus status = super.verify();
		if (!status.isOK()) {
			return status;
		}
	
		if (fRenamingsList != null && fRenamingsList.length != fElementsToProcess.length) {
			return new EGLModelStatus(IEGLModelStatusConstants.INDEX_OUT_OF_BOUNDS);
		}
		return EGLModelStatus.VERIFIED_OK;
	}
	/**
	 * @see MultiOperation
	 */
	protected void verify(IEGLElement element) throws EGLModelException {
		if (element == null || !element.exists())
			error(IEGLModelStatusConstants.ELEMENT_DOES_NOT_EXIST, element);
			
		if (element.isReadOnly() && (isRename() || isMove()))
			error(IEGLModelStatusConstants.READ_ONLY, element);

		IResource resource = element.getResource();
		if (resource instanceof IFolder) {
			if (resource.isLinked()) {
				error(EGLModelStatus.INVALID_RESOURCE, element);
			}
		}
	
		int elementType = element.getElementType();
	
		if (elementType == IEGLElement.EGL_FILE) {
			if (isMove() && ((IEGLFile) element).isWorkingCopy())
				error(IEGLModelStatusConstants.INVALID_ELEMENT_TYPES, element);
		} else if (elementType != IEGLElement.PACKAGE_FRAGMENT) {
			error(IEGLModelStatusConstants.INVALID_ELEMENT_TYPES, element);
		}
		
		EGLElement dest = (EGLElement) getDestinationParent(element);
		verifyDestination(element, dest);
		if (fRenamings != null) {
			verifyRenaming(element);
		}
}
}
