/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.refactoring.changes;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.refactoring.reorg.INewNameQuery;
import org.eclipse.edt.ide.ui.internal.refactoring.reorg.ReorgUtils;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.ReorgExecutionLog;

import com.ibm.icu.text.MessageFormat;

abstract class ResourceReorgChange extends Change {
	
	protected static final int NONE= 0;
	protected static final int READ_ONLY= 1 << 0;
	protected static final int DIRTY= 1 << 1;
	private static final int SAVE= 1 << 2;
	protected static final int SAVE_IF_DIRTY= SAVE | DIRTY;
	
	private long fModificationStamp;
	private boolean fReadOnly;
	
	private final IPath fResourcePath;
	private final boolean fIsFile;
	private final IPath fDestinationPath;
	private final boolean fIsDestinationProject;
	private final INewNameQuery fNewNameQuery;
	
	ResourceReorgChange(IResource res, IContainer dest, INewNameQuery nameQuery){
		Assert.isTrue(res instanceof IFile || res instanceof IFolder);
		fIsFile= (res instanceof IFile);
		fResourcePath= getResourcePath(res);
	
		Assert.isTrue(dest instanceof IProject || dest instanceof IFolder);
		fIsDestinationProject= (dest instanceof IProject);
		fDestinationPath= getResourcePath(dest);
		fNewNameQuery= nameQuery;
	}
	
	protected abstract Change doPerformReorg(IPath path, IProgressMonitor pm) throws CoreException;
	
	/* non java-doc
	 * @see IChange#perform(ChangeContext, IProgressMonitor)
	 */
	public final Change perform(IProgressMonitor pm) throws CoreException {
		try{
			pm.beginTask(getName(), 2);
			
			String newName= getNewResourceName();
			IResource resource= getResource();
			boolean performReorg= deleteIfAlreadyExists(new SubProgressMonitor(pm, 1), newName);
			if (!performReorg)
				return null;
			final Change result= doPerformReorg(getDestinationPath(newName), new SubProgressMonitor(pm, 1));
			markAsExecuted(resource);
			return result;
		} finally {
			pm.done();
		}
	}

	protected IPath getDestinationPath(String newName) {
		return getDestination().getFullPath().append(newName);
	}

	/**
	 * returns false if source and destination are the same (in workspace or on disk)
	 * in such case, no action should be performed
	 */
	private boolean deleteIfAlreadyExists(IProgressMonitor pm, String newName) throws CoreException {
		pm.beginTask("", 1); //$NON-NLS-1$
		IResource current= getDestination().findMember(newName);
		if (current == null)
			return true;
		if (! current.exists())
			return true;

		IResource resource= getResource();
		Assert.isNotNull(resource);
			
		if (ReorgUtils.areEqualInWorkspaceOrOnDisk(resource, current))
			return false;
		
		if (current instanceof IFile)
			((IFile)current).delete(false, true, new SubProgressMonitor(pm, 1));
		else if (current instanceof IFolder)
			((IFolder)current).delete(false, true, new SubProgressMonitor(pm, 1));
		else 
			Assert.isTrue(false);
			
		return true;	
	}
	

	private String getNewResourceName(){
		if (fNewNameQuery == null)
			return getResource().getName();
		String name= fNewNameQuery.getNewName();
		if (name == null)
			return getResource().getName();
		return name;
	}
	
	/* non java-doc
	 * @see IChange#getModifiedLanguageElement()
	 */
	public Object getModifiedElement() {
		return getResource();
	}

	private IFile getFile(){
		return getFile(fResourcePath);
	}
	
	private IFolder getFolder(){
		return getFolder(fResourcePath);
	}
	
	protected IResource getResource(){
		if (fIsFile)
			return getFile();
		else
			return getFolder();
	}
	
	IContainer getDestination(){
		if (fIsDestinationProject)
			return getProject(fDestinationPath);
		else
			return getFolder(fDestinationPath);	
	}

	protected int getReorgFlags() {
		return IResource.KEEP_HISTORY | IResource.SHALLOW;
	}
	
	private void markAsExecuted(IResource resource) {
		ReorgExecutionLog log= (ReorgExecutionLog)getAdapter(ReorgExecutionLog.class);
		if (log != null) {
			log.markAsProcessed(resource);
		}
	}
	
	static IPath getResourcePath(IResource resource){
		return resource.getFullPath().removeFirstSegments(ResourcesPlugin.getWorkspace().getRoot().getFullPath().segmentCount());
	}
	
	static IFile getFile(IPath path){
		return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
	}
	
	static IFolder getFolder(IPath path){
		return ResourcesPlugin.getWorkspace().getRoot().getFolder(path);
	}
	
	static IProject getProject(IPath path){
		return (IProject)ResourcesPlugin.getWorkspace().getRoot().findMember(path);
	}
	
	public abstract String getName();
	public abstract Object getAdapter(Class clazz);
	
	private static class ValidationState {
		private IResource fResource;
		private int fKind;
		private boolean fDirty;
		private boolean fReadOnly;
		private long fModificationStamp;
		private ITextFileBuffer fTextFileBuffer;
		public static final int RESOURCE= 1;
		public static final int DOCUMENT= 2;
		public ValidationState(IResource resource) {
			fResource= resource;
			if (resource instanceof IFile) {
				initializeFile((IFile)resource);
			} else {
				initializeResource(resource);
			}
		}
		public void checkDirty(RefactoringStatus status, long stampToMatch, IProgressMonitor pm) throws CoreException {
			if (fDirty) {
				if (fKind == DOCUMENT && fTextFileBuffer != null && stampToMatch == fModificationStamp) {
					fTextFileBuffer.commit(pm, false);
				} else {
					status.addFatalError(MessageFormat.format(
						UINlsStrings.Change_is_unsaved, new String[] {fResource.getFullPath().toString()})); 
				}
			}
		}
		public void checkDirty(RefactoringStatus status) {
			if (fDirty) {
				status.addFatalError(MessageFormat.format(
					UINlsStrings.Change_is_unsaved, new String[] {fResource.getFullPath().toString()})); 
			}
		}
		public void checkReadOnly(RefactoringStatus status) {
			if (fReadOnly) {
				status.addFatalError(MessageFormat.format(
						UINlsStrings.Change_is_read_only, new String[] {fResource.getFullPath().toString()})); 
			}
		}
		public void checkSameReadOnly(RefactoringStatus status, boolean valueToMatch) {
			if (fReadOnly != valueToMatch) {
				status.addFatalError(MessageFormat.format(
					UINlsStrings.Change_same_read_only,
					new String[] {fResource.getFullPath().toString()}));
			}
		}
		public void checkModificationStamp(RefactoringStatus status, long stampToMatch) {
			if (fKind == DOCUMENT) {
				if (stampToMatch != IDocumentExtension4.UNKNOWN_MODIFICATION_STAMP && fModificationStamp != stampToMatch) {
					status.addFatalError(MessageFormat.format(
						UINlsStrings.Change_has_modifications, new String[] {fResource.getFullPath().toString()})); 
				}
			} else {
				if (stampToMatch != IResource.NULL_STAMP && fModificationStamp != stampToMatch) {
					status.addFatalError(MessageFormat.format(
						UINlsStrings.Change_has_modifications, new String[] {fResource.getFullPath().toString()})); 
					
				}
			}
		}
		private void initializeFile(IFile file) {
			fTextFileBuffer= getBuffer(file);
			if (fTextFileBuffer == null) {
				initializeResource(file);
			} else {
				IDocument document= fTextFileBuffer.getDocument();
				fDirty= fTextFileBuffer.isDirty();
				fReadOnly= isReadOnly(file);
				if (document instanceof IDocumentExtension4) {
					fKind= DOCUMENT;
					fModificationStamp= ((IDocumentExtension4)document).getModificationStamp();
				} else {
					fKind= RESOURCE;
					fModificationStamp= file.getModificationStamp();
				}
			}
			
		}
		private void initializeResource(IResource resource) {
			fKind= RESOURCE;
			fDirty= false;
			fReadOnly= isReadOnly(resource);
			fModificationStamp= resource.getModificationStamp();
		}
	}
	
	protected final RefactoringStatus isValid(IProgressMonitor pm, int flags) throws CoreException {
		pm.beginTask("", 2); //$NON-NLS-1$
		try {
			RefactoringStatus result= new RefactoringStatus();
			Object modifiedElement= getModifiedElement();
			checkExistence(result, modifiedElement);
			if (result.hasFatalError())
				return result;
			if (flags == NONE)
				return result;
			IResource resource= getResource(modifiedElement);
			if (resource != null) {
				ValidationState state= new ValidationState(resource);
				state.checkModificationStamp(result, fModificationStamp);
				if (result.hasFatalError())
					return result;
				state.checkSameReadOnly(result, fReadOnly);
				if (result.hasFatalError())
					return result;
				if ((flags & READ_ONLY) != 0) {
					state.checkReadOnly(result);
					if (result.hasFatalError())
						return result;
				}
				if ((flags & DIRTY) != 0) {
					if ((flags & SAVE) != 0) {
						state.checkDirty(result, fModificationStamp, new SubProgressMonitor(pm, 1));
					} else {
						state.checkDirty(result);
					}
				}
			}
			return result;
		} finally {
			pm.done();
		}
	}
	
	protected static void checkExistence(RefactoringStatus status, Object element) {
		if (element == null) {
			status.addFatalError(UINlsStrings.DynamicValidationStateChange_workspace_changed); 
			
		} else if (element instanceof IResource && !((IResource)element).exists()) {
			status.addFatalError(MessageFormat.format(
				UINlsStrings.Change_does_not_exist, new String[] {((IResource)element).getFullPath().toString()})); 
		} else if (element instanceof IEGLElement && !((IEGLElement)element).exists()) {
			status.addFatalError(MessageFormat.format(
					UINlsStrings.Change_does_not_exist, new String[] {((IEGLElement)element).getElementName()})); 
		}
	}

	protected final RefactoringStatus isValid(int flags) throws CoreException {
		return isValid(new NullProgressMonitor(), flags);
	}
	
	private static ITextFileBuffer getBuffer(IFile file) {
		ITextFileBufferManager manager= FileBuffers.getTextFileBufferManager();
		return manager.getTextFileBuffer(file.getFullPath());
	}
	
	private static IResource getResource(Object element) {
		if (element instanceof IResource) {
			return (IResource)element;
		}
		if (element instanceof IEGLFile) {
			return ((IEGLFile)element).getResource();
		}
		if (element instanceof IEGLElement) {
			return ((IEGLElement)element).getResource();
		}
		if (element instanceof IAdaptable) {
			return (IResource)((IAdaptable)element).getAdapter(IResource.class);
		}
		return null;
	}
	
	public static boolean isReadOnly(IResource resource) {
		ResourceAttributes resourceAttributes = resource.getResourceAttributes();
		if (resourceAttributes == null)  // not supported on this platform for this resource 
			return false;
		return resourceAttributes.isReadOnly();
	}
	
	public long getModificationStamp(IResource resource) {
		if (!(resource instanceof IFile))
			return resource.getModificationStamp();
		IFile file= (IFile)resource;
		ITextFileBuffer buffer= getBuffer(file);
		if (buffer == null) {
			return file.getModificationStamp();
		} else {
			IDocument document= buffer.getDocument();
			if (document instanceof IDocumentExtension4) {
				return ((IDocumentExtension4)document).getModificationStamp();
			} else {
				return file.getModificationStamp();
			}
		}
	}
	
	public void initializeValidationData(IProgressMonitor pm) {
		IResource resource= getResource(getModifiedElement());
		if (resource != null) {
			fModificationStamp= getModificationStamp(resource);
			fReadOnly= isReadOnly(resource);
		}
	}
}
