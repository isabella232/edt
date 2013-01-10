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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.core.internal.model.util.PerThreadObject;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLElementDelta;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLModel;
import org.eclipse.edt.ide.core.model.IEGLModelStatus;
import org.eclipse.edt.ide.core.model.IEGLModelStatusConstants;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.model.IWorkingCopy;


/**
 * Defines behavior common to all EGL Model operations
 */
public abstract class EGLModelOperation implements IWorkspaceRunnable, IProgressMonitor {
	protected interface IPostAction {
		/*
		 * Returns the id of this action.
		 * @see EGLModelOperation#postAction
		 */
		String getID();
		/*
		 * Run this action.
		 */
		void run() throws EGLModelException;
	}
	/*
	 * Constants controlling the insertion mode of an action.
	 * @see EGLModelOperation#postAction
	 */
	protected static final int APPEND = 1; // insert at the end
	protected static final int REMOVEALL_APPEND = 2; // remove all existing ones with same ID, and add new one at the end
	protected static final int KEEP_EXISTING = 3; // do not insert if already existing with same ID
	
	/*
	 * Whether tracing post actions is enabled.
	 */
	protected static boolean POST_ACTION_VERBOSE;

	/*
	 * A list of IPostActions.
	 */
	protected IPostAction[] actions;
	protected int actionsStart = 0;
	protected int actionsEnd = -1;
	/*
	 * A HashMap of attributes that can be used by operations
	 */
	protected HashMap attributes;

	public static final String HAS_MODIFIED_RESOURCE_ATTR = "hasModifiedResource"; //$NON-NLS-1$
	public static final String TRUE = "true"; //$NON-NLS-1$
	//public static final String FALSE = "false"; //$NON-NLS-1$
		
	/**
	 * The elements this operation operates on,
	 * or <code>null</code> if this operation
	 * does not operate on specific elements.
	 */
	protected IEGLElement[] fElementsToProcess;
	/**
	 * The parent elements this operation operates with
	 * or <code>null</code> if this operation
	 * does not operate with specific parent elements.
	 */
	protected IEGLElement[] fParentElements;
	/**
	 * An empty collection of <code>IEGLElement</code>s - the common
	 * empty result if no elements are created, or if this
	 * operation is not actually executed.
	 */
	protected static IEGLElement[] fgEmptyResult= new IEGLElement[] {};


	/**
	 * The elements created by this operation - empty
	 * until the operation actually creates elements.
	 */
	protected IEGLElement[] fResultElements= fgEmptyResult;

	/**
	 * The progress monitor passed into this operation
	 */
	protected IProgressMonitor fMonitor= null;
	/**
	 * A flag indicating whether this operation is nested.
	 */
	protected boolean fNested = false;
	/**
	 * Conflict resolution policy - by default do not force (fail on a conflict).
	 */
	protected boolean fForce= false;

	/*
	 * A per thread stack of java model operations (PerThreadObject of ArrayList).
	 */
	protected static PerThreadObject operationStacks = new PerThreadObject();
	protected EGLModelOperation() {
	}
	/**
	 * A common constructor for all EGL Model operations.
	 */
	protected EGLModelOperation(IEGLElement[] elements) {
		fElementsToProcess = elements;
	}
	/**
	 * Common constructor for all EGL Model operations.
	 */
	protected EGLModelOperation(IEGLElement[] elementsToProcess, IEGLElement[] parentElements) {
		fElementsToProcess = elementsToProcess;
		fParentElements= parentElements;
	}
	/**
	 * A common constructor for all EGL Model operations.
	 */
	protected EGLModelOperation(IEGLElement[] elementsToProcess, IEGLElement[] parentElements, boolean force) {
		fElementsToProcess = elementsToProcess;
		fParentElements= parentElements;
		fForce= force;
	}
	/**
	 * A common constructor for all EGL Model operations.
	 */
	protected EGLModelOperation(IEGLElement[] elements, boolean force) {
		fElementsToProcess = elements;
		fForce= force;
	}
	
	/**
	 * Common constructor for all EGL Model operations.
	 */
	protected EGLModelOperation(IEGLElement element) {
		fElementsToProcess = new IEGLElement[]{element};
	}
	/**
	 * A common constructor for all EGL Model operations.
	 */
	protected EGLModelOperation(IEGLElement element, boolean force) {
		fElementsToProcess = new IEGLElement[]{element};
		fForce= force;
	}
	
	/*
	 * Registers the given action at the end of the list of actions to run.
	 */
	protected void addAction(IPostAction action) {
		int length = this.actions.length;
		if (length == ++this.actionsEnd) {
			System.arraycopy(this.actions, 0, this.actions = new IPostAction[length*2], 0, length);
		}
		this.actions[this.actionsEnd] = action;
	}
	/*
	 * Registers the given delta with the EGL Model Manager.
	 */
	protected void addDelta(IEGLElementDelta delta) {
		EGLModelManager.getEGLModelManager().registerEGLModelDelta(delta);
	}
	/*
	 * Registers the given reconcile delta with the EGL Model Manager.
	 */
	protected void addReconcileDelta(IWorkingCopy workingCopy, IEGLElementDelta delta) {
		HashMap reconcileDeltas = EGLModelManager.getEGLModelManager().reconcileDeltas;
		EGLElementDelta previousDelta = (EGLElementDelta)reconcileDeltas.get(workingCopy);
		if (previousDelta != null) {
			IEGLElementDelta[] children = delta.getAffectedChildren();
			for (int i = 0, length = children.length; i < length; i++) {
				EGLElementDelta child = (EGLElementDelta)children[i];
				previousDelta.insertDeltaTree(child.getElement(), child);
			}
		} else {
			reconcileDeltas.put(workingCopy, delta);
		}
	}
	/*
	 * Deregister the reconcile delta for the given working copy
	 */
	protected void removeReconcileDelta(IWorkingCopy workingCopy) {
		EGLModelManager.getEGLModelManager().reconcileDeltas.remove(workingCopy);		
	}
	/**
	 * @see IProgressMonitor
	 */
	public void beginTask(String name, int totalWork) {
		if (fMonitor != null) {
			fMonitor.beginTask(name, totalWork);
		}
	}
	/**
	 * Checks with the progress monitor to see whether this operation
	 * should be canceled. An operation should regularly call this method
	 * during its operation so that the user can cancel it.
	 *
	 * @exception OperationCanceledException if cancelling the operation has been requested
	 * @see IProgressMonitor#isCanceled
	 */
	protected void checkCanceled() {
		if (isCanceled()) {
			throw new OperationCanceledException(EGLModelResources.operationCancelled);
		}
	}
	/**
	 * Common code used to verify the elements this operation is processing.
	 * @see EGLModelOperation#verify()
	 */
	protected IEGLModelStatus commonVerify() {
		if (fElementsToProcess == null || fElementsToProcess.length == 0) {
			return new EGLModelStatus(IEGLModelStatusConstants.NO_ELEMENTS_TO_PROCESS);
		}
		for (int i = 0; i < fElementsToProcess.length; i++) {
			if (fElementsToProcess[i] == null) {
				return new EGLModelStatus(IEGLModelStatusConstants.NO_ELEMENTS_TO_PROCESS);
			}
		}
		return EGLModelStatus.VERIFIED_OK;
	}
	/**
	 * Convenience method to copy resources
	 */
	protected void copyResources(IResource[] resources, IPath destinationPath) throws EGLModelException {
		IProgressMonitor subProgressMonitor = getSubProgressMonitor(resources.length);
		IWorkspace workspace = resources[0].getWorkspace();
		try {
			workspace.copy(resources, destinationPath, false, subProgressMonitor);
			this.setAttribute(HAS_MODIFIED_RESOURCE_ATTR, TRUE); 
		} catch (CoreException e) {
			throw new EGLModelException(e);
		}
	}
	/**
	 * Convenience method to create a file
	 */
	protected void createFile(IContainer folder, String name, InputStream contents, boolean force) throws EGLModelException {
		IFile file= folder.getFile(new Path(name));
		try {
			file.create(
				contents, 
				force ? IResource.FORCE | IResource.KEEP_HISTORY : IResource.KEEP_HISTORY, 
				getSubProgressMonitor(1));
				this.setAttribute(HAS_MODIFIED_RESOURCE_ATTR, TRUE); 
		} catch (CoreException e) {
			throw new EGLModelException(e);
		}
	}
	/**
	 * Convenience method to create a folder
	 */
	protected void createFolder(IContainer parentFolder, String name, boolean force) throws EGLModelException {
		IFolder folder= parentFolder.getFolder(new Path(name));
		try {
			// we should use true to create the file locally. Only VCM should use tru/false
			folder.create(
				force ? IResource.FORCE | IResource.KEEP_HISTORY : IResource.KEEP_HISTORY,
				true, // local
				getSubProgressMonitor(1));
				this.setAttribute(HAS_MODIFIED_RESOURCE_ATTR, TRUE); 
		} catch (CoreException e) {
			throw new EGLModelException(e);
		}
	}
	/**
	 * Convenience method to delete an empty package fragment
	 */
	protected void deleteEmptyPackageFragment(
		IPackageFragment fragment,
		boolean force,
		IResource rootResource)
		throws EGLModelException {
	
		IContainer resource = (IContainer) fragment.getResource();
	
		try {
			resource.delete(
				force ? IResource.FORCE | IResource.KEEP_HISTORY : IResource.KEEP_HISTORY, 
				getSubProgressMonitor(1));
			this.setAttribute(HAS_MODIFIED_RESOURCE_ATTR, TRUE); 
			while (resource instanceof IFolder) {
				// deleting a package: delete the parent if it is empty (eg. deleting x.y where folder x doesn't have resources but y)
				// without deleting the package fragment root
				resource = resource.getParent();
				if (!resource.equals(rootResource) && resource.members().length == 0) {
					resource.delete(
						force ? IResource.FORCE | IResource.KEEP_HISTORY : IResource.KEEP_HISTORY, 
						getSubProgressMonitor(1));
					this.setAttribute(HAS_MODIFIED_RESOURCE_ATTR, TRUE); 
				}
			}
		} catch (CoreException e) {
			throw new EGLModelException(e);
		}
	}
	/**
	 * Convenience method to delete a resource
	 */
	protected void deleteResource(IResource resource,int flags) throws EGLModelException {
		try {
			resource.delete(flags, getSubProgressMonitor(1));
			this.setAttribute(HAS_MODIFIED_RESOURCE_ATTR, TRUE); 
		} catch (CoreException e) {
			throw new EGLModelException(e);
		}
	}
	/**
	 * Convenience method to delete resources
	 */
	protected void deleteResources(IResource[] resources, boolean force) throws EGLModelException {
		if (resources == null || resources.length == 0) return;
		IProgressMonitor subProgressMonitor = getSubProgressMonitor(resources.length);
		IWorkspace workspace = resources[0].getWorkspace();
		try {
			workspace.delete(
				resources,
				force ? IResource.FORCE | IResource.KEEP_HISTORY : IResource.KEEP_HISTORY, 
				subProgressMonitor);
				this.setAttribute(HAS_MODIFIED_RESOURCE_ATTR, TRUE); 
		} catch (CoreException e) {
			throw new EGLModelException(e);
		}
	}
	/**
	 * @see IProgressMonitor
	 */
	public void done() {
		if (fMonitor != null) {
			fMonitor.done();
		}
	}
	/*
	 * Returns whether the given path is equals to one of the given other paths.
	 */
	protected boolean equalsOneOf(IPath path, IPath[] otherPaths) {
		for (int i = 0, length = otherPaths.length; i < length; i++) {
			if (path.equals(otherPaths[i])) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Verifies the operation can proceed and executes the operation.
	 * Subclasses should override <code>#verify</code> and
	 * <code>executeOperation</code> to implement the specific operation behavior.
	 *
	 * @exception EGLModelException The operation has failed.
	 */
	protected void execute() throws EGLModelException {
		IEGLModelStatus status= verify();
		if (status.isOK()) {
			// if first time here, computes the root infos before executing the operation
			DeltaProcessor deltaProcessor = EGLModelManager.getEGLModelManager().deltaProcessor;
			if (deltaProcessor.roots == null) {
				deltaProcessor.initializeRoots();
			}

			executeOperation();
		} else {
			throw new EGLModelException(status);
		}
	}
	/**
	 * Convenience method to run an operation within this operation
	 */
	public void executeNestedOperation(EGLModelOperation operation, int subWorkAmount) throws EGLModelException {
		IProgressMonitor subProgressMonitor = getSubProgressMonitor(subWorkAmount);
		// fix for 1FW7IKC, part (1)
		try {
			operation.setNested(true);
			operation.run(subProgressMonitor);
		} catch (CoreException ce) {
			if (ce instanceof EGLModelException) {
				throw (EGLModelException)ce;
			} else {
				// translate the core exception to a java model exception
				if (ce.getStatus().getCode() == IResourceStatus.OPERATION_FAILED) {
					Throwable e = ce.getStatus().getException();
					if (e instanceof EGLModelException) {
						throw (EGLModelException) e;
					}
				}
				throw new EGLModelException(ce);
			}
		}
	}
	/**
	 * Performs the operation specific behavior. Subclasses must override.
	 */
	protected abstract void executeOperation() throws EGLModelException;
	/*
	 * Returns the attribute registered at the given key with the top level operation.
	 * Returns null if no such attribute is found.
	 */
	protected Object getAttribute(Object key) {
		ArrayList stack = this.getCurrentOperationStack();
		if (stack.size() == 0) return null;
		EGLModelOperation topLevelOp = (EGLModelOperation)stack.get(0);
		if (topLevelOp.attributes == null) {
			return null;
		} else {
			return topLevelOp.attributes.get(key);
		}
	}
	/**
	 * Returns the compilation unit the given element is contained in,
	 * or the element itself (if it is a compilation unit),
	 * otherwise <code>null</code>.
	 */
	protected IEGLFile getEGLFileFor(IEGLElement element) {
	
		return ((EGLElement)element).getEGLFile();
	}
	/*
	 * Returns the stack of operations running in the current thread.
	 * Returns an empty stack if no operations are currently running in this thread. 
	 */
	protected ArrayList getCurrentOperationStack() {
		ArrayList stack = (ArrayList)operationStacks.getCurrent();
		if (stack == null) {
			stack = new ArrayList();
			operationStacks.setCurrent(stack);
		}
		return stack;
	}
	/**
	 * Returns the elements to which this operation applies,
	 * or <code>null</code> if not applicable.
	 */
	protected IEGLElement[] getElementsToProcess() {
		return fElementsToProcess;
	}
	/**
	 * Returns the element to which this operation applies,
	 * or <code>null</code> if not applicable.
	 */
	protected IEGLElement getElementToProcess() {
		if (fElementsToProcess == null || fElementsToProcess.length == 0) {
			return null;
		}
		return fElementsToProcess[0];
	}
	/**
	 * Returns the EGL Model this operation is operating in.
	 */
	public IEGLModel getEGLModel() {
		if (fElementsToProcess == null || fElementsToProcess.length == 0) {
			return getParentElement().getEGLModel();
		} else {
			return fElementsToProcess[0].getEGLModel();
		}
	}
	protected IPath[] getNestedFolders(IPackageFragmentRoot root) throws EGLModelException {
		IPath rootPath = root.getPath();
		IEGLPathEntry[] classpath = root.getEGLProject().getRawEGLPath();
		int length = classpath.length;
		IPath[] result = new IPath[length];
		int index = 0;
		for (int i = 0; i < length; i++) {
			IPath path = classpath[i].getPath();
			if (rootPath.isPrefixOf(path) && !rootPath.equals(path)) {
				result[index++] = path;
			}
		}
		if (index < length) {
			System.arraycopy(result, 0, result = new IPath[index], 0, index);
		}
		return result;
	}
	/**
	 * Returns the parent element to which this operation applies,
	 * or <code>null</code> if not applicable.
	 */
	protected IEGLElement getParentElement() {
		if (fParentElements == null || fParentElements.length == 0) {
			return null;
		}
		return fParentElements[0];
	}
	/**
	 * Returns the parent elements to which this operation applies,
	 * or <code>null</code> if not applicable.
	 */
	protected IEGLElement[] getParentElements() {
		return fParentElements;
	}
	/**
	 * Returns the elements created by this operation.
	 */
	public IEGLElement[] getResultElements() {
		return fResultElements;
	}
	/**
	 * Creates and returns a subprogress monitor if appropriate.
	 */
	protected IProgressMonitor getSubProgressMonitor(int workAmount) {
		IProgressMonitor sub = null;
		if (fMonitor != null) {
			sub = new SubProgressMonitor(fMonitor, workAmount, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
		}
		return sub;
	}

	/**
	 * Returns whether this operation has performed any resource modifications.
	 * Returns false if this operation has not been executed yet.
	 */
	public boolean hasModifiedResource() {
		return !this.isReadOnly() && this.getAttribute(HAS_MODIFIED_RESOURCE_ATTR) == TRUE; 
	}
	public void internalWorked(double work) {
		if (fMonitor != null) {
			fMonitor.internalWorked(work);
		}
	}
	/**
	 * @see IProgressMonitor
	 */
	public boolean isCanceled() {
		if (fMonitor != null) {
			return fMonitor.isCanceled();
		}
		return false;
	}
	/**
	 * Returns <code>true</code> if this operation performs no resource modifications,
	 * otherwise <code>false</code>. Subclasses must override.
	 */
	public boolean isReadOnly() {
		return false;
	}
	/*
	 * Returns whether this operation is the first operation to run in the current thread.
	 */
	protected boolean isTopLevelOperation() {
		ArrayList stack;
		return 
			(stack = this.getCurrentOperationStack()).size() > 0
			&& stack.get(0) == this;
	}
	/*
	 * Returns the index of the first registered action with the given id, starting from a given position.
	 * Returns -1 if not found.
	 */
	protected int firstActionWithID(String id, int start) {
		for (int i = start; i <= this.actionsEnd; i++) {
			if (this.actions[i].getID().equals(id)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Convenience method to move resources
	 */
	protected void moveResources(IResource[] resources, IPath destinationPath) throws EGLModelException {
		IProgressMonitor subProgressMonitor = null;
		if (fMonitor != null) {
			subProgressMonitor = new SubProgressMonitor(fMonitor, resources.length, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
		}
		IWorkspace workspace = resources[0].getWorkspace();
		try {
			workspace.move(resources, destinationPath, false, subProgressMonitor);
			this.setAttribute(HAS_MODIFIED_RESOURCE_ATTR, TRUE); 
		} catch (CoreException e) {
			throw new EGLModelException(e);
		}
	}
	/**
	 * Creates and returns a new <code>IEGLElementDelta</code>
	 * on the EGL Model.
	 */
	public EGLElementDelta newEGLElementDelta() {
		return new EGLElementDelta(getEGLModel());
	}
	/*
	 * Removes the last pushed operation from the stack of running operations.
	 * Returns the poped operation or null if the stack was empty.
	 */
	protected EGLModelOperation popOperation() {
		ArrayList stack = getCurrentOperationStack();
		int size = stack.size();
		if (size > 0) {
			if (size == 1) { // top level operation 
				operationStacks.setCurrent(null); // release reference (see http://bugs.eclipse.org/bugs/show_bug.cgi?id=33927)
			}
			return (EGLModelOperation)stack.remove(size-1);
		} else {
			return null;
		}
	}
	/*
	 * Registers the given action to be run when the outer most java model operation has finished.
	 * The insertion mode controls whether:
	 * - the action should discard all existing actions with the same id, and be queued at the end (REMOVEALL_APPEND),
	 * - the action should be ignored if there is already an action with the same id (KEEP_EXISTING),
	 * - the action should be queued at the end without looking at existing actions (APPEND)
	 */
	protected void postAction(IPostAction action, int insertionMode) {
		if (POST_ACTION_VERBOSE) {
			System.out.print("(" + Thread.currentThread() + ") [EGLModelOperation.postAction(IPostAction, int)] Posting action " + action.getID()); //$NON-NLS-1$ //$NON-NLS-2$
			switch(insertionMode) {
				case REMOVEALL_APPEND:
					System.out.println(" (REMOVEALL_APPEND)"); //$NON-NLS-1$
					break;
				case KEEP_EXISTING:
					System.out.println(" (KEEP_EXISTING)"); //$NON-NLS-1$
					break;
				case APPEND:
					System.out.println(" (APPEND)"); //$NON-NLS-1$
					break;
			}
		}
		
		EGLModelOperation topLevelOp = (EGLModelOperation)getCurrentOperationStack().get(0);
		IPostAction[] postActions = topLevelOp.actions;
		if (postActions == null) {
			topLevelOp.actions = postActions = new IPostAction[1];
			postActions[0] = action;
			topLevelOp.actionsEnd = 0;
		} else {
			String id = action.getID();
			switch (insertionMode) {
				case REMOVEALL_APPEND :
					int index = this.actionsStart-1;
					while ((index = topLevelOp.firstActionWithID(id, index+1)) >= 0) {
						// remove action[index]
						System.arraycopy(postActions, index+1, postActions, index, topLevelOp.actionsEnd - index);
						postActions[topLevelOp.actionsEnd--] = null;
					}
					topLevelOp.addAction(action);
					break;
				case KEEP_EXISTING:
					if (topLevelOp.firstActionWithID(id, 0) < 0) {
						topLevelOp.addAction(action);
					}
					break;
				case APPEND:
					topLevelOp.addAction(action);
					break;
			}
		}
	}
	/*
	 * Returns whether the given path is the prefix of one of the given other paths.
	 */
	protected boolean prefixesOneOf(IPath path, IPath[] otherPaths) {
		for (int i = 0, length = otherPaths.length; i < length; i++) {
			if (path.isPrefixOf(otherPaths[i])) {
				return true;
			}
		}
		return false;
	}	
	/*
	 * Pushes the given operation on the stack of operations currently running in this thread.
	 */
	protected void pushOperation(EGLModelOperation operation) {
		getCurrentOperationStack().add(operation);
	}
	
	/**
	 * Main entry point for EGL Model operations.  Executes this operation
	 * and registers any deltas created.
	 *
	 * @see IWorkspaceRunnable
	 * @exception CoreException if the operation fails
	 */
	public void run(IProgressMonitor monitor) throws CoreException {
		EGLModelManager manager = EGLModelManager.getEGLModelManager();
		int previousDeltaCount = manager.eglModelDeltas.size();
		try {
			fMonitor = monitor;
			pushOperation(this);
			try {
				this.execute();
			} finally {
				if (this.isTopLevelOperation()) {
					this.runPostActions();
				}
			}
		} finally {
			try {
				// update EGLModel using deltas that were recorded during this operation
				for (int i = previousDeltaCount, size = manager.eglModelDeltas.size(); i < size; i++) {
					manager.updateEGLModel((IEGLElementDelta)manager.eglModelDeltas.get(i));
				}
				
				// fire only iff:
				// - the operation is a top level operation
				// - the operation did produce some delta(s)
				// - but the operation has not modified any resource
				if (this.isTopLevelOperation()) {
					if ((manager.eglModelDeltas.size() > previousDeltaCount || !manager.reconcileDeltas.isEmpty()) 
							&& !this.hasModifiedResource()) {
						manager.fire(null, EGLModelManager.DEFAULT_CHANGE_EVENT);
					} // else deltas are fired while processing the resource delta
				}
			} finally {
				popOperation();
			}
		}
	}
	/**
	 * Returns the scheduling rule for this operation (i.e. the resource that needs to be locked 
	 * while this operation is running.
	 * Subclasses can override.
	 */
	protected ISchedulingRule getSchedulingRule() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}
	/**
	 * Main entry point for EGL Model operations. Runs a EGL Model Operation as an IWorkspaceRunnable
	 * if not read-only.
	 */
	public void runOperation(IProgressMonitor monitor) throws EGLModelException {
		IEGLModelStatus status= verify();
		if (!status.isOK()) {
			throw new EGLModelException(status);
		}
		try {
			if (isReadOnly()) {
				run(monitor);
			} else {
				// Use IWorkspace.run(...) to ensure that a build will be done in autobuild mode.
				// Note that if the tree is locked, this will throw a CoreException, but this is ok
				// as this operation is modifying the tree (not read-only) and a CoreException will be thrown anyway.
				ResourcesPlugin.getWorkspace().run(this, getSchedulingRule(), IWorkspace.AVOID_UPDATE, monitor);
			}
		} catch (CoreException ce) {
			if (ce instanceof EGLModelException) {
				throw (EGLModelException)ce;
			} else {
				if (ce.getStatus().getCode() == IResourceStatus.OPERATION_FAILED) {
					Throwable e= ce.getStatus().getException();
					if (e instanceof EGLModelException) {
						throw (EGLModelException) e;
					}
				}
				throw new EGLModelException(ce);
			}
		}
	}
	protected void runPostActions() throws EGLModelException {
		while (this.actionsStart <= this.actionsEnd) {
			IPostAction postAction = this.actions[this.actionsStart++];
			if (POST_ACTION_VERBOSE) {
				System.out.println("(" + Thread.currentThread() + ") [EGLModelOperation.runPostActions()] Running action " + postAction.getID()); //$NON-NLS-1$ //$NON-NLS-2$
			}
			postAction.run();
		}
	}
	/*
	 * Registers the given attribute at the given key with the top level operation.
	 */
	protected void setAttribute(Object key, Object attribute) {
		EGLModelOperation topLevelOp = (EGLModelOperation)this.getCurrentOperationStack().get(0);
		if (topLevelOp.attributes == null) {
			topLevelOp.attributes = new HashMap();
		}
		topLevelOp.attributes.put(key, attribute);
	}
	/**
	 * @see IProgressMonitor
	 */
	public void setCanceled(boolean b) {
		if (fMonitor != null) {
			fMonitor.setCanceled(b);
		}
	}
	/**
	 * Sets whether this operation is nested or not.
	 * @see CreateElementInCUOperation#checkCanceled
	 */
	protected void setNested(boolean nested) {
		fNested = nested;
	}
	/**
	 * @see IProgressMonitor
	 */
	public void setTaskName(String name) {
		if (fMonitor != null) {
			fMonitor.setTaskName(name);
		}
	}
	/**
	 * @see IProgressMonitor
	 */
	public void subTask(String name) {
		if (fMonitor != null) {
			fMonitor.subTask(name);
		}
	}
	/**
	 * Returns a status indicating if there is any known reason
	 * this operation will fail.  Operations are verified before they
	 * are run.
	 *
	 * Subclasses must override if they have any conditions to verify
	 * before this operation executes.
	 *
	 * @see IEGLModelStatus
	 */
	protected IEGLModelStatus verify() {
		return commonVerify();
	}
	
	/**
	 * @see IProgressMonitor
	 */
	public void worked(int work) {
		if (fMonitor != null) {
			fMonitor.worked(work);
			checkCanceled();
		}
	}
}
