/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.refactoring.rename;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.edt.compiler.internal.core.builder.DefaultProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.DefaultCompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.ide.core.internal.model.SourcePart;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.refactoring.Checks;
import org.eclipse.edt.ide.ui.internal.refactoring.changes.RenameResourceChange;
import org.eclipse.edt.ide.ui.internal.refactoring.tagging.IReferenceUpdating;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;

public class RenameEGLFileProcessor extends RenameProcessor implements IReferenceUpdating {
	
	private boolean fUpdateReferences;
	private IEGLFile eglFile;
	private Boolean willRenameGeneratablePart;
	private RenamePartProcessor renameGeneratablePartProcessor;
	private CompositeChange compositeChange;

	public RenameEGLFileProcessor(IEGLFile eglFile) {
		this.eglFile = eglFile;
		this.compositeChange = new CompositeChange(getProcessorName());
	}

	protected RefactoringStatus doCheckFinalConditions(IProgressMonitor pm, CheckConditionsContext context) throws CoreException, OperationCanceledException {
		if(willRenameGeneratablePart() && fUpdateReferences) {
			return renameGeneratablePartProcessor.doCheckFinalConditions(pm, context);
		}
		
		this.compositeChange = new CompositeChange(getProcessorName());
		RefactoringStatus result= new RefactoringStatus();
		try{
			pm.beginTask("", 1); //$NON-NLS-1$
			pm.setTaskName(UINlsStrings.RefactoringProcessor_precondition_checking);
			
			result.merge(Checks.checkFileNewName(eglFile, getNewElementName()));
			pm.worked(1);
			
			if(pm.isCanceled()) {
				throw new OperationCanceledException();
			}
			
			createChanges(new SubProgressMonitor(pm, 1));
		}
		finally {
			pm.done();
		}
		
		return result;
	}
	
	private void createChanges(IProgressMonitor pm) throws CoreException {
		if(willRenameGeneratablePart() && fUpdateReferences) {
			renameGeneratablePartProcessor.createChange(pm);
			return;
		}
		
		try {
			String filename = eglFile.getElementName();
			String ext = null;
			int lastIndexOfDot = filename.lastIndexOf('.');
			if(lastIndexOfDot != -1) {
				ext = filename.substring(lastIndexOfDot+1);
				filename = filename.substring(0, lastIndexOfDot);				
			}
			StringBuffer newFilename = new StringBuffer(getNewElementName());
			if(ext != null) {
				newFilename.append('.');
				newFilename.append(ext);
			}
			compositeChange.add(new RenameResourceChange(null, eglFile.getResource(), newFilename.toString(), null));
		}
		finally {
			pm.done();
		}	
	}

	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		if(willRenameGeneratablePart()) {
			return renameGeneratablePartProcessor.checkInitialConditions(pm);
		}
		return new RefactoringStatus();
	}

	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		if(willRenameGeneratablePart() && fUpdateReferences) {
			return renameGeneratablePartProcessor.createChange(pm);
		}
		
		return compositeChange;
	}

	public String getProcessorName() {
		return UINlsStrings.RenameEGLFileRefactoring_name;
	}

	public boolean isApplicable() throws CoreException {
		// TODO Auto-generated method stub
		return false;
	}

	public RefactoringParticipant[] loadParticipants(RefactoringStatus status,
			SharableParticipants sharedParticipants) throws CoreException {
		if(willRenameGeneratablePart()) {
			return renameGeneratablePartProcessor.loadParticipants(status, sharedParticipants);
		}
		return new RefactoringParticipant[0];
	}

	public boolean canEnableUpdateReferences() {
		if(willRenameGeneratablePart()) {
			return renameGeneratablePartProcessor.canEnableUpdateReferences();
		}
		return false;
	}

	public boolean getUpdateReferences() {
		if(willRenameGeneratablePart()) {
			return renameGeneratablePartProcessor.getUpdateReferences();
		}
		return fUpdateReferences;
	}

	public void setUpdateReferences(boolean update) {
		fUpdateReferences = update;
		if(willRenameGeneratablePart()) {
			renameGeneratablePartProcessor.setUpdateReferences(update);
			return;
		}		
	}

	protected String[] getAffectedProjectNatures() throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	protected IFile[] getChangedFiles() throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object[] getElements() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	public RenamePartProcessor getRenamePartProcessor() {
		return willRenameGeneratablePart() ? renameGeneratablePartProcessor : null;
	}
	
	public void setNewElementName(String newName) {
		super.setNewElementName(newName);
		if(willRenameGeneratablePart()) {
			renameGeneratablePartProcessor.setNewElementName(newName);
		}
	}
	
	public String getCurrentElementName() {
		return getSimpleEGLFileName();	
	}
	
	private String getSimpleEGLFileName() {
		return removeFileNameExtension(eglFile.getElementName());
	}
	
	/**
	 * Removes the extension (whatever comes after the last '.') from the given file name.
	 */
	private static String removeFileNameExtension(String fileName) {
		if (fileName.lastIndexOf(".") == -1) //$NON-NLS-1$
			return fileName;
		return fileName.substring(0, fileName.lastIndexOf(".")); //$NON-NLS-1$
	}
	
	public RefactoringStatus checkNewElementName(String newName) throws CoreException {
		if(willRenameGeneratablePart() && fUpdateReferences) {
			return renameGeneratablePartProcessor.checkNewElementName(newName);
		}
		else {
			final RefactoringStatus[] result = new RefactoringStatus[] {new RefactoringStatus()};
			EGLNameValidator.validate(newName, EGLNameValidator.FILENAME, new DefaultProblemRequestor() {
				public void acceptProblem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts) {
					if(result[0].isOK()) {
						result[0] = RefactoringStatus.createFatalErrorStatus(getMessageFromBundle(problemKind, inserts));
					}
				}
			}, 
			DefaultCompilerOptions.getInstance());
			return result[0];
		}
	}
	
	private boolean willRenameGeneratablePart() {
		if(willRenameGeneratablePart == null) {
			try {
				IPart generatablePartWithSameName = getGeneratablePartWithSameName();
				if(generatablePartWithSameName == null) {
					willRenameGeneratablePart = Boolean.FALSE;
				}
				else {
					willRenameGeneratablePart = Boolean.TRUE;
					renameGeneratablePartProcessor = new RenamePartProcessor(generatablePartWithSameName);
				}
			}
			catch(EGLModelException e) {
				EDTUIPlugin.log(e);
				willRenameGeneratablePart = Boolean.FALSE;
			}
		}
		return willRenameGeneratablePart.booleanValue();
	}

	private IPart getGeneratablePartWithSameName() throws EGLModelException {
		IPart[] parts = eglFile.getParts();
		String filename = eglFile.getElementName();
		int lastIndexOfDot = filename.lastIndexOf('.');
		if(lastIndexOfDot != -1) {
			filename = filename.substring(0, lastIndexOfDot);				
		}		
		for(int i = 0; i < parts.length; i++) {
			if(filename.equals(parts[i].getElementName()) &&
			   parts[i] instanceof SourcePart &&
			   ((SourcePart) parts[i]).isGeneratable()) {
				return parts[i];
			}
		}
		return null;
	}
}
