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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.ide.core.model.EGLConventions;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLModelStatus;
import org.eclipse.edt.ide.core.model.IEGLModelStatusConstants;
import org.eclipse.edt.ide.core.model.IPackageFragment;


/**
 * This class is used to perform operations on multiple <code>IEGLElement</code>.
 * It is responible for running each operation in turn, collecting
 * the errors and merging the corresponding <code>EGLElementDelta</code>s.
 * <p>
 * If several errors occured, they are collected in a multi-status
 * <code>EGLModelStatus</code>. Otherwise, a simple <code>EGLModelStatus</code>
 * is thrown.
 */
public abstract class MultiOperation extends EGLModelOperation {
	/**
	 * The list of renamings supplied to the operation
	 */
	protected String[] fRenamingsList= null;
	/**
	 * Table specifying the new parent for elements being 
	 * copied/moved/renamed.
	 * Keyed by elements being processed, and
	 * values are the corresponding destination parent.
	 */
	protected Map fParentElements;
	/**
	 * Table specifying insertion positions for elements being 
	 * copied/moved/renamed. Keyed by elements being processed, and
	 * values are the corresponding insertion point.
	 * @see processElements(IProgressMonitor)
	 */
	protected Map fInsertBeforeElements= new HashMap(1);
	/**
	 * This table presents the data in <code>fRenamingList</code> in a more
	 * convenient way.
	 */
	protected Map fRenamings;
	/**
	 * Creates a new <code>MultiOperation</code>.
	 */
	protected MultiOperation(IEGLElement[] elementsToProcess, IEGLElement[] parentElements, boolean force) {
		super(elementsToProcess, parentElements, force);
		fParentElements = new HashMap(elementsToProcess.length);
		if (elementsToProcess.length == parentElements.length) {
			for (int i = 0; i < elementsToProcess.length; i++) {
				fParentElements.put(elementsToProcess[i], parentElements[i]);
			}
		} else { //same destination for all elements to be moved/copied/renamed
			for (int i = 0; i < elementsToProcess.length; i++) {
				fParentElements.put(elementsToProcess[i], parentElements[0]);
			}
		}
	
	}
	/**
	 * Creates a new <code>MultiOperation</code> on <code>elementsToProcess</code>.
	 */
	protected MultiOperation(IEGLElement[] elementsToProcess, boolean force) {
		super(elementsToProcess, force);
	}
	/**
	 * Convenience method to create a <code>EGLModelException</code>
	 * embending a <code>EGLModelStatus</code>.
	 */
	protected void error(int code, IEGLElement element) throws EGLModelException {
		throw new EGLModelException(new EGLModelStatus(code, element));
	}
	/**
	 * Executes the operation.
	 *
	 * @exception EGLModelException if one or several errors occured during the operation.
	 * If multiple errors occured, the corresponding <code>EGLModelStatus</code> is a
	 * multi-status. Otherwise, it is a simple one.
	 */
	protected void executeOperation() throws EGLModelException {
		processElements();
	}
	/**
	 * Returns the parent of the element being copied/moved/renamed.
	 */
	protected IEGLElement getDestinationParent(IEGLElement child) {
		return (IEGLElement)fParentElements.get(child);
	}
	/**
	 * Returns the name to be used by the progress monitor.
	 */
	protected abstract String getMainTaskName();
	/**
	 * Returns the new name for <code>element</code>, or <code>null</code>
	 * if there are no renamings specified.
	 */
	protected String getNewNameFor(IEGLElement element) {
		if (fRenamings != null)
			return (String) fRenamings.get(element);
		else
			return null;
	}
	/**
	 * Sets up the renamings hashtable - keys are the elements and
	 * values are the new name.
	 */
	private void initializeRenamings() {
		if (fRenamingsList != null && fRenamingsList.length == fElementsToProcess.length) {
			fRenamings = new HashMap(fRenamingsList.length);
			for (int i = 0; i < fRenamingsList.length; i++) {
				if (fRenamingsList[i] != null) {
					fRenamings.put(fElementsToProcess[i], fRenamingsList[i]);
				}
			}
		}
	}
	/**
	 * Returns <code>true</code> if this operation represents a move or rename, <code>false</code>
	 * if this operation represents a copy.<br>
	 * Note: a rename is just a move within the same parent with a name change.
	 */
	protected boolean isMove() {
		return false;
	}
	/**
	 * Returns <code>true</code> if this operation represents a rename, <code>false</code>
	 * if this operation represents a copy or move.
	 */
	protected boolean isRename() {
		return false;
	}
	
	/**
	 * Subclasses must implement this method to process a given <code>IEGLElement</code>.
	 */
	protected abstract void processElement(IEGLElement element) throws EGLModelException;
	/**
	 * Processes all the <code>IEGLElement</code>s in turn, collecting errors
	 * and updating the progress monitor.
	 *
	 * @exception EGLModelException if one or several operation(s) was unable to
	 * be completed.
	 */
	protected void processElements() throws EGLModelException {
		beginTask(getMainTaskName(), fElementsToProcess.length);
		IEGLModelStatus[] errors = new IEGLModelStatus[3];
		int errorsCounter = 0;
		for (int i = 0; i < fElementsToProcess.length; i++) {
			try {
				verify(fElementsToProcess[i]);
				processElement(fElementsToProcess[i]);
			} catch (EGLModelException jme) {
				if (errorsCounter == errors.length) {
					// resize
					System.arraycopy(errors, 0, (errors = new IEGLModelStatus[errorsCounter*2]), 0, errorsCounter);
				}
				errors[errorsCounter++] = jme.getEGLModelStatus();
			} finally {
				worked(1);
			}
		}
		done();
		if (errorsCounter == 1) {
			throw new EGLModelException(errors[0]);
		} else if (errorsCounter > 1) {
			if (errorsCounter != errors.length) {
				// resize
				System.arraycopy(errors, 0, (errors = new IEGLModelStatus[errorsCounter]), 0, errorsCounter);
			}
			throw new EGLModelException(EGLModelStatus.newMultiStatus(errors));
		}
	}
	/**
	 * Sets the insertion position in the new container for the modified element. The element
	 * being modified will be inserted before the specified new sibling. The given sibling
	 * must be a child of the destination container specified for the modified element.
	 * The default is <code>null</code>, which indicates that the element is to be
	 * inserted at the end of the container.
	 */
	public void setInsertBefore(IEGLElement modifiedElement, IEGLElement newSibling) {
		fInsertBeforeElements.put(modifiedElement, newSibling);
	}
	/**
	 * Sets the new names to use for each element being copied. The renamings
	 * correspond to the elements being processed, and the number of
	 * renamings must match the number of elements being processed.
	 * A <code>null</code> entry in the list indicates that an element
	 * is not to be renamed.
	 *
	 * <p>Note that some renamings may not be used.  If both a parent
	 * and a child have been selected for copy/move, only the parent
	 * is changed.  Therefore, if a new name is specified for the child,
	 * the child's name will not be changed.
	 */
	public void setRenamings(String[] renamings) {
		fRenamingsList = renamings;
		initializeRenamings();
	}
	/**
	 * This method is called for each <code>IEGLElement</code> before
	 * <code>processElement</code>. It should check that this <code>element</code>
	 * can be processed.
	 */
	protected abstract void verify(IEGLElement element) throws EGLModelException;
	/**
	 * Verifies that the <code>destination</code> specified for the <code>element</code> is valid for the types of the
	 * <code>element</code> and <code>destination</code>.
	 */
	protected void verifyDestination(IEGLElement element, IEGLElement destination) throws EGLModelException {
		if (destination == null || !destination.exists())
			error(IEGLModelStatusConstants.ELEMENT_DOES_NOT_EXIST, destination);
		
		int destType = destination.getElementType();
		switch (element.getElementType()) {
			case IEGLElement.PACKAGE_DECLARATION :
			case IEGLElement.IMPORT_DECLARATION :
				if (destType != IEGLElement.EGL_FILE)
					error(IEGLModelStatusConstants.INVALID_DESTINATION, element);
				break;
			case IEGLElement.PART :
				if (destType != IEGLElement.EGL_FILE && destType != IEGLElement.PART)
					error(IEGLModelStatusConstants.INVALID_DESTINATION, element);
				break;
			case IEGLElement.FUNCTION :
			case IEGLElement.FIELD :
			case IEGLElement.INITIALIZER :
				if (destType != IEGLElement.PART )
					error(IEGLModelStatusConstants.INVALID_DESTINATION, element);
				break;
			case IEGLElement.EGL_FILE :
				if (destType != IEGLElement.PACKAGE_FRAGMENT)
					error(IEGLModelStatusConstants.INVALID_DESTINATION, element);
				break;
			case IEGLElement.PACKAGE_FRAGMENT :
				IPackageFragment fragment = (IPackageFragment) element;
				IEGLElement parent = fragment.getParent();
				if (parent.isReadOnly())
					error(IEGLModelStatusConstants.READ_ONLY, element);
				else if (destType != IEGLElement.PACKAGE_FRAGMENT_ROOT)
					error(IEGLModelStatusConstants.INVALID_DESTINATION, element);
				break;
			default :
				error(IEGLModelStatusConstants.INVALID_ELEMENT_TYPES, element);
		}
	}
	/**
	 * Verify that the new name specified for <code>element</code> is
	 * valid for that type of EGL element.
	 */
	protected void verifyRenaming(IEGLElement element) throws EGLModelException {
		String newName = getNewNameFor(element);
		boolean isValid = true;
	
		switch (element.getElementType()) {
			case IEGLElement.PACKAGE_FRAGMENT :
				if (element.getElementName().equals(IPackageFragment.DEFAULT_PACKAGE_NAME)) {
					// don't allow renaming of default package (see PR #1G47GUM)
					throw new EGLModelException(new EGLModelStatus(IEGLModelStatusConstants.NAME_COLLISION, element));
				}
				isValid = EGLConventions.validatePackageName(newName).getSeverity() != IStatus.ERROR;
				break;
			case IEGLElement.EGL_FILE :
				isValid = EGLConventions.validateEGLFileName(newName).getSeverity() != IStatus.ERROR;
				break;
			case IEGLElement.INITIALIZER :
				isValid = false; //cannot rename initializers
				break;
			default :
				isValid = EGLConventions.validateIdentifier(newName).getSeverity() != IStatus.ERROR;
				break;
		}
	
		if (!isValid) {
			throw new EGLModelException(new EGLModelStatus(IEGLModelStatusConstants.INVALID_NAME, element, newName));
		}
	}
	/**
	 * Verifies that the positioning sibling specified for the <code>element</code> is exists and
	 * its parent is the destination container of this <code>element</code>.
	 */
	protected void verifySibling(IEGLElement element, IEGLElement destination) throws EGLModelException {
		IEGLElement insertBeforeElement = (IEGLElement) fInsertBeforeElements.get(element);
		if (insertBeforeElement != null) {
			if (!insertBeforeElement.exists() || !insertBeforeElement.getParent().equals(destination)) {
				error(IEGLModelStatusConstants.INVALID_SIBLING, insertBeforeElement);
			}
		}
	}
}
