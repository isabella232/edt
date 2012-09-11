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
package org.eclipse.edt.ide.ui.internal.refactoring.rename;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.core.builder.DefaultProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.DefaultCompilerOptions;
import org.eclipse.edt.compiler.internal.core.utils.CharOperation;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.ide.core.internal.model.SourcePart;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.core.search.IEGLSearchResultCollector;
import org.eclipse.edt.ide.core.search.SearchEngine;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.refactoring.Checks;
import org.eclipse.edt.ide.ui.internal.refactoring.RefactoringScopeFactory;
import org.eclipse.edt.ide.ui.internal.refactoring.changes.RenameResourceChange;
import org.eclipse.edt.ide.ui.internal.refactoring.changes.TextChangeCompatibility;
import org.eclipse.edt.ide.ui.internal.refactoring.tagging.IReferenceUpdating;
import org.eclipse.edt.ide.ui.internal.refactoring.util.TextChangeManager;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.GroupCategory;
import org.eclipse.ltk.core.refactoring.GroupCategorySet;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.ParticipantManager;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.RenameArguments;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;
import org.eclipse.text.edits.ReplaceEdit;

import com.ibm.icu.text.MessageFormat;

public class RenamePartProcessor extends RenameProcessor implements IReferenceUpdating {
	
	private static final GroupCategorySet CATEGORY_PART_RENAME= new GroupCategorySet(new GroupCategory("com.ibm.etools.egl.internal.ui..refactoring.rename.renamePart.part", UINlsStrings.RenamePartProcessor_changeCategory_type, UINlsStrings.RenamePartProcessor_changeCategory_type_description)); //$NON-NLS-1$
	
	private IPart part;
	private boolean fUpdateReferences;
	private List fReferences;
	private boolean willUpdateEGLFileName;
	private TextChangeManager fChangeManager;

	public RenamePartProcessor(IPart part) {
		this.part = part;
		willUpdateEGLFileName = part instanceof SourcePart && ((SourcePart) part).isGeneratable();
	}

	protected RefactoringStatus doCheckFinalConditions(IProgressMonitor pm, CheckConditionsContext context) throws CoreException, OperationCanceledException {
		RefactoringStatus result= new RefactoringStatus();
		
		int referenceSearchTicks= fUpdateReferences ? 15 : 0;
		int createChangeTicks = 5;
		
		try{
			pm.beginTask("", 12 + referenceSearchTicks + createChangeTicks); //$NON-NLS-1$
			pm.setTaskName(UINlsStrings.RefactoringProcessor_precondition_checking);
			
			if(pm.isCanceled()) {
				throw new OperationCanceledException();
			}
			
			fChangeManager= new TextChangeManager(true);
			
			result.merge(checkTypesInPackage());
			pm.worked(1);
			
			result.merge(checkRenameMainFunction());
			pm.worked(1);
			
			if(willUpdateEGLFileName) {
				result.merge(Checks.checkFileNewName(part.getEGLFile(), getNewElementName()));
				pm.worked(1);
			}
			
			if(result.hasFatalError()) {
				return result;
			}
			
			if (fUpdateReferences) {
				pm.setTaskName(UINlsStrings.RefactoringProcessor_searching_references);
				result.merge(initializeReferences(new SubProgressMonitor(pm, referenceSearchTicks)));
			} else {
				fReferences = Collections.EMPTY_LIST;
			}
			
			createChanges(new SubProgressMonitor(pm, createChangeTicks));
		}
		finally {
			pm.done();
		}
		
		return result;
	}
	
	public RefactoringStatus initializeReferences(final IProgressMonitor monitor) throws EGLModelException {
		RefactoringStatus result= new RefactoringStatus();
		
		fReferences = new ArrayList();
		
		String searchString;
		IPackageFragment packageFragment = (IPackageFragment)part.getAncestor(IEGLElement.PACKAGE_FRAGMENT);
		if(packageFragment.isDefaultPackage()) {
			searchString = part.getElementName();
		}
		else {				
			searchString = new String(CharOperation.concat(packageFragment.getElementName().toCharArray(), part.getElementName().toCharArray(), '.'));
		}
		
		new SearchEngine().search(ResourcesPlugin.getWorkspace(), SearchEngine.createSearchPattern(searchString, getSearchFor(part), IEGLSearchConstants.REFERENCES, false), RefactoringScopeFactory.create(part), true,true,new IEGLSearchResultCollector() {
			public void aboutToStart() {
			}

			public void accept(IResource resource, int start, int end, IEGLElement enclosingElement, int accuracy) throws CoreException {
				if (IEGLSearchResultCollector.EXACT_MATCH == accuracy){
					fReferences.add(new Object[] {resource, new Integer(start), new Integer(end), enclosingElement, new Integer(accuracy)});
				}
			}

			public void done() {
			}

			public IProgressMonitor getProgressMonitor() {
				return monitor;
			}

			public void accept(IEGLElement element, int start, int end,
					IResource resource, int accuracy) throws CoreException {
				// TODO Auto-generated method stub
				
			}
			
		});
		return result;
	}

	private int getSearchFor(IPart part) {
		int result = IEGLSearchConstants.ALL_ELEMENTS;
		if(part instanceof SourcePart) {
			SourcePart sPart = ((SourcePart) part);
			if (sPart.isDelegate()) {
				result = IEGLSearchConstants.DELEGATE_PART;
			}
			else if (sPart.isExternalType()) {
				result = IEGLSearchConstants.EXTERNALTYPE_PART;
			}
			else if (sPart.isHandler()) {
				result = IEGLSearchConstants.HANDLER_PART;
			}
			else if (sPart.isInterface()) {
				result = IEGLSearchConstants.INTERFACE_PART;
			}
			else if (sPart.isLibrary()) {
				result = IEGLSearchConstants.LIBRARY_PART;
			}
			else if (sPart.isProgram()) {
				result = IEGLSearchConstants.PROGRAM_PART;
			}			
			else if (sPart.isRecord()) {
				result = IEGLSearchConstants.RECORD_PART;
			}
			else if (sPart.isService()) {
				result = IEGLSearchConstants.SERVICE_PART;
			}
			else if (sPart.isEnumeration()) {
				result = IEGLSearchConstants.ENUMERATION_PART;
			}
			else if (sPart.isClass()) {
				result = IEGLSearchConstants.CLASS_PART;
			}
			else if (sPart.isFunction()) {
				result = IEGLSearchConstants.ALL_FUNCTIONS;
			}
		}
		return result;
	}

	private void createChanges(IProgressMonitor pm) throws CoreException {
		try {
			pm.beginTask("", 3); //$NON-NLS-1$
			pm.setTaskName(UINlsStrings.RefactoringProcessor_creating_changes); 
			
			if (fUpdateReferences)
				addReferenceUpdates(fChangeManager, new SubProgressMonitor(pm, 3));
			pm.worked(1);
			
			addPartDeclarationUpdate(fChangeManager);
			pm.worked(1);
		}
		finally {
			pm.done();
		}	
	}

	private void addRenameEGLFileUpdate(CompositeChange compositeChange) {
		if(willUpdateEGLFileName) {
			IEGLFile file = part.getEGLFile();
			String filename = file.getElementName();
			String ext = null;
			int lastIndexOfDot = filename.lastIndexOf('.');
			if(lastIndexOfDot != -1) {
				ext = filename.substring(lastIndexOfDot+1);
				filename = filename.substring(0, lastIndexOfDot);				
			}
			if(filename.equals(part.getElementName())) {
				StringBuffer newFilename = new StringBuffer(getNewElementName());
				if(ext != null) {
					newFilename.append('.');
					newFilename.append(ext);
				}
				compositeChange.add(new RenameResourceChange(null, file.getResource(), newFilename.toString(), null));
			}
		}		
	}

	private void addPartDeclarationUpdate(TextChangeManager manager) throws EGLModelException {
		String name= UINlsStrings.RenamePartRefactoring_update;
		int partNameLength= part.getElementName().length();
		IFile file = (IFile) part.getResource();
		TextChangeCompatibility.addTextEdit(manager.get(part.getEGLFile()), name, new ReplaceEdit(part.getNameRange().getOffset(), partNameLength, getNewElementName()));
	}

	private void addReferenceUpdates(TextChangeManager manager, IProgressMonitor pm) {
		pm.beginTask("", fReferences.size()); //$NON-NLS-1$
		String name= UINlsStrings.RenamePartRefactoring_update_reference;
		for (Iterator iter = fReferences.iterator(); iter.hasNext();) {
			Object[] nextRef = (Object[]) iter.next();
			if(nextRef[0] instanceof IFile) {
				int start = ((Integer) nextRef[1]).intValue();
				int end = ((Integer) nextRef[2]).intValue();
				IEGLElement enclosingElement = (IEGLElement) nextRef[3];
				String newElementName = getNewElementName();
				
				start = start + end-start - part.getElementName().length(); // reference may be qualified
				
				TextChangeCompatibility.addTextEdit(manager.get(getEGLFile(enclosingElement)), name, new ReplaceEdit(start, part.getElementName().length(), newElementName), CATEGORY_PART_RENAME);
			}
			pm.worked(1);
		}
	}

	private IEGLFile getEGLFile(IEGLElement element) {
		while(!(element instanceof IEGLFile)) {
			element = element.getParent();
		}
		return (IEGLFile) element;
	}

	protected String[] getAffectedProjectNatures() throws CoreException {
		return new String[]{"com.ibm.etools.egl.model.eglnature"}; //$NON-NLS-1$
	}

	protected IFile[] getChangedFiles() throws CoreException {
		List result= new ArrayList();
		result.addAll(Arrays.asList(toFiles(fChangeManager.getAllCompilationUnits())));
		if (willUpdateEGLFileName)
			result.add((IFile) part.getEGLFile().getResource());
		return (IFile[]) result.toArray(new IFile[result.size()]);
	}

	private Object[] toFiles(IEGLFile[] allCompilationUnits) {
		List result = new ArrayList();
		for(int i = 0; i < allCompilationUnits.length; i++) {
			result.add((IFile) allCompilationUnits[i].getResource());
		}
		return (IFile[]) result.toArray(new IFile[result.size()]);
	}

	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		return new RefactoringStatus();
	}

	public Change createChange(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
		try {
			CompositeChange result = new CompositeChange(UINlsStrings.RenamePartProcessor_change_name);
			monitor.beginTask(UINlsStrings.RenamePartRefactoring_creating_change, 4);
			result.addAll(fChangeManager.getAllChanges());
			if (willUpdateEGLFileName) {
				addRenameEGLFileUpdate(result);
			}
			monitor.worked(1);
			return result;
		} finally {
			fChangeManager= null;
		}
	}

	public Object[] getElements() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getIdentifier() {
		return this.getClass().getName();
	}

	public String getProcessorName() {
		return UINlsStrings.RenamePartRefactoring_name;
	}

	public boolean isApplicable() throws CoreException {
		// TODO Auto-generated method stub
		return false;
	}

	public RefactoringParticipant[] loadParticipants(RefactoringStatus status, SharableParticipants shared) throws CoreException {
		return ParticipantManager.loadRenameParticipants(status, 
			this, part, 
			new RenameArguments(getNewElementName(), getUpdateReferences()), getAffectedProjectNatures(), shared);
}
	
	public RefactoringStatus checkNewElementName(String newName) throws CoreException {
		final RefactoringStatus[] result = new RefactoringStatus[] {new RefactoringStatus()};
		EGLNameValidator.validate(newName, EGLNameValidator.PART, new DefaultProblemRequestor() {
			@Override
			public void acceptProblem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts, ResourceBundle bundle) {
				if(result[0].isOK()) {
					result[0] = RefactoringStatus.createFatalErrorStatus(getMessageFromBundle(problemKind, inserts, bundle));
				}
			}
		}, 
		DefaultCompilerOptions.getInstance());
		return result[0];
	}

	public String getCurrentElementName() {
		return part.getElementName();
	}

	public boolean canEnableUpdateReferences() {
		return true;
	}

	public boolean getUpdateReferences() {
		return fUpdateReferences;
	}

	public void setUpdateReferences(boolean update) {
		fUpdateReferences = update;
	}
	
	private RefactoringStatus checkTypesInPackage() throws CoreException {
		IPart existingPart = findPartInPackage(part.getPackageFragment(), getNewElementName());
		if (existingPart == null || ! existingPart.exists())
			return null;
		String msg= MessageFormat.format(
			UINlsStrings.RenamePartRefactoring_exists, 
			new String[]{getNewElementName(), part.getPackageFragment().getElementName()});
		return RefactoringStatus.createErrorStatus(msg);
	}
	
	private RefactoringStatus checkRenameMainFunction() throws CoreException {
		IEGLElement parent = part.getParent();
		if(IPart.FUNCTION == part.getElementType() && IEGLConstants.MNEMONIC_MAIN.equalsIgnoreCase(part.getElementName()) && parent instanceof SourcePart && ((SourcePart) parent).isProgram()) {
			String msg= MessageFormat.format(
				UINlsStrings.RenamePartRefactoring_rename_main_function, 
				new String[]{parent.getElementName()});
			return RefactoringStatus.createErrorStatus(msg);
		}
		return null;
	}
	
	public static IPart findPartInPackage(IPackageFragment pack, String name) throws EGLModelException {
		Assert.isTrue(pack.exists());
		Assert.isTrue(!pack.isReadOnly());
		
		/* ICompilationUnit.getType expects simple name*/  
		if (name.indexOf(".") != -1) //$NON-NLS-1$
			name= name.substring(0, name.indexOf(".")); //$NON-NLS-1$
		IEGLFile[] cus= pack.getEGLFiles();
		for (int i= 0; i < cus.length; i++){
			if (cus[i].getPart(name).exists())
				return cus[i].getPart(name);
		}
		return null;
	}
}
