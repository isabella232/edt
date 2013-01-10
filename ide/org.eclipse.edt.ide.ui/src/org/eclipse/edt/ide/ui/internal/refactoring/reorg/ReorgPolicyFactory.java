/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.refactoring.reorg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.mapping.IResourceChangeDescriptionFactory;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.ImportDeclaration;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.PackageDeclaration;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.ide.core.ast.GetNodeAtOffsetVisitor;
import org.eclipse.edt.ide.core.ast.rewrite.ASTRewrite;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.IWorkingCopyCompileRequestor;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompilationResult;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompiler;
import org.eclipse.edt.ide.core.internal.model.IEGLDocumentAdapter;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IBuffer;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLModel;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IField;
import org.eclipse.edt.ide.core.model.IFunction;
import org.eclipse.edt.ide.core.model.IImportContainer;
import org.eclipse.edt.ide.core.model.IImportDeclaration;
import org.eclipse.edt.ide.core.model.IMember;
import org.eclipse.edt.ide.core.model.IOpenable;
import org.eclipse.edt.ide.core.model.IPackageDeclaration;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.ISourceReference;
import org.eclipse.edt.ide.core.model.IWorkingCopy;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.ui.internal.Strings;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.codemanipulation.OrganizeImportsOperation;
import org.eclipse.edt.ide.ui.internal.codemanipulation.OrganizeImportsOperation.OrganizedImportSection;
import org.eclipse.edt.ide.ui.internal.codemanipulation.OrganizeImportsVisitor;
import org.eclipse.edt.ide.ui.internal.editor.DocumentAdapter;
import org.eclipse.edt.ide.ui.internal.refactoring.Checks;
import org.eclipse.edt.ide.ui.internal.refactoring.changes.CopyEGLFileChange;
import org.eclipse.edt.ide.ui.internal.refactoring.changes.CopyResourceChange;
import org.eclipse.edt.ide.ui.internal.refactoring.changes.CreateCopyOfEGLFileChange;
import org.eclipse.edt.ide.ui.internal.refactoring.changes.EGLFileChange;
import org.eclipse.edt.ide.ui.internal.refactoring.changes.MoveEGLFileChange;
import org.eclipse.edt.ide.ui.internal.refactoring.changes.TextChangeCompatibility;
import org.eclipse.edt.ide.ui.internal.refactoring.rename.RenamePartProcessor;
import org.eclipse.edt.ide.ui.internal.refactoring.reorg.IReorgPolicy.IEGLCopyPolicy;
import org.eclipse.edt.ide.ui.internal.refactoring.reorg.IReorgPolicy.IEGLMovePolicy;
import org.eclipse.edt.ide.ui.internal.refactoring.util.TextChangeManager;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.NullChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.CopyArguments;
import org.eclipse.ltk.core.refactoring.participants.MoveArguments;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;
import org.eclipse.ltk.core.refactoring.participants.ReorgExecutionLog;
import org.eclipse.ltk.core.refactoring.participants.ResourceChangeChecker;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;
import org.eclipse.ltk.core.refactoring.participants.ValidateEditChecker;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.TextEdit;

import com.ibm.icu.text.MessageFormat;

public class ReorgPolicyFactory {
	private ReorgPolicyFactory() {
		//private
	}
	
	public static IEGLCopyPolicy createCopyPolicy(IResource[] resources, IEGLElement[] eglElements) throws EGLModelException{
		return (IEGLCopyPolicy)createReorgPolicy(true, resources, eglElements);
	}
	
	public static IEGLMovePolicy createMovePolicy(IResource[] resources, IEGLElement[] eglElements) throws EGLModelException{
		return (IEGLMovePolicy)createReorgPolicy(false, resources, eglElements);
	}
	
	private static IReorgPolicy createReorgPolicy(boolean copy, IResource[] selectedResources, IEGLElement[] selectedEGLElements) throws EGLModelException{
		final IReorgPolicy NO;
		if (copy) {
			NO= new NoCopyPolicy();
		}
		else
			NO= new NoMovePolicy();

		ActualSelectionComputer selectionComputer= new ActualSelectionComputer(selectedEGLElements, selectedResources);
		IResource[] resources= selectionComputer.getActualResourcesToReorg();
		IEGLElement[] eglElements= selectionComputer.getActualEGLElementsToReorg();
	
		if (isNothingToReorg(resources, eglElements) || 
			containsNull(resources) ||
			containsNull(eglElements) ||
			ReorgUtils.isArchiveMember(eglElements) ||
			ReorgUtils.hasElementsOfType(eglElements, IEGLElement.EGL_PROJECT) ||
			ReorgUtils.hasElementsOfType(eglElements, IEGLElement.EGL_MODEL) ||
			ReorgUtils.hasElementsOfType(resources, IResource.PROJECT | IResource.ROOT) ||
			! haveCommonParent(resources, eglElements))
			return NO;
			
//		if (EGLReorgUtils.hasElementsOfType(javaElements, IEGLElement.PACKAGE_FRAGMENT)){
//			if (resources.length != 0 || EGLReorgUtils.hasElementsNotOfType(javaElements, IEGLElement.PACKAGE_FRAGMENT))
//				return NO;
//			if (copy) {
//				//return new CopyPackagesPolicy(ArrayTypeConverter.toPackageArray(javaElements));
//				return null;
//			}
//			else
//				return new MovePackagesPolicy(ArrayTypeConverter.toPackageArray(javaElements));
//		}
		
//		if (EGLReorgUtils.hasElementsOfType(javaElements, IEGLElement.PACKAGE_FRAGMENT_ROOT)){
//			if (resources.length != 0 || EGLReorgUtils.hasElementsNotOfType(javaElements, IEGLElement.PACKAGE_FRAGMENT_ROOT))
//				return NO;
//			if (copy) {
//				return new CopyPackageFragmentRootsPolicy(ArrayTypeConverter.toPackageFragmentRootArray(javaElements));
//				return null;
//			}
//			else
//				return new MovePackageFragmentRootsPolicy(ArrayTypeConverter.toPackageFragmentRootArray(javaElements));
//		}
		
		if (ReorgUtils.hasElementsOfType(eglElements, IEGLElement.EGL_FILE)){
			if (ReorgUtils.hasElementsNotOfType(eglElements, IEGLElement.EGL_FILE))
				return NO;
			if (ReorgUtils.hasElementsNotOfType(resources, IResource.FILE | IResource.FOLDER))
				return NO;
			if (copy) {
				return new CopyEGLFilesPolicy(ReorgUtils.getFiles(resources), ReorgUtils.getFolders(resources), ArrayTypeConverter.toEGLFileArray(eglElements));
			}
			else
				return new MoveEGLFilesPolicy(ReorgUtils.getFiles(resources), ReorgUtils.getFolders(resources), ArrayTypeConverter.toEGLFileArray(eglElements));
		}
		
		if (hasElementsSmallerThanCuOrClassFile(eglElements)){
			//assertions guaranteed by common parent
			Assert.isTrue(resources.length == 0);
			Assert.isTrue(! ReorgUtils.hasElementsOfType(eglElements, IEGLElement.EGL_FILE));
			Assert.isTrue(! ReorgUtils.hasElementsOfType(eglElements, IEGLElement.CLASS_FILE));
			Assert.isTrue(! hasElementsLargerThanCuOrClassFile(eglElements));
			if (copy) {
				//return new CopySubCuElementsPolicy(javaElements);
				return NO;
			}
			else
				return new MovePartsPolicy(eglElements);
		}
		return NO;
	}
		
	private static boolean containsNull(Object[] objects) {
		for (int i= 0; i < objects.length; i++) {
			if (objects[i]==null) return  true;
		}
		return false;
	}

	private static boolean hasElementsSmallerThanCuOrClassFile(IEGLElement[] eglElements) {
		for (int i= 0; i < eglElements.length; i++) {
			if (ReorgUtils.isInsideEGLFile(eglElements[i]))
				return true;
			if (ReorgUtils.isInsideIRFile(eglElements[i]))
				return true;
		}
		return false;
	}

	private static boolean hasElementsLargerThanCuOrClassFile(IEGLElement[] eglElements) {
		for (int i= 0; i < eglElements.length; i++) {
			if (! ReorgUtils.isInsideEGLFile(eglElements[i]) &&
				! ReorgUtils.isInsideIRFile(eglElements[i]))
				return true;
		}
		return false;
	}

	private static boolean haveCommonParent(IResource[] resources, IEGLElement[] eglElements) {
		return new ParentChecker(resources, eglElements).haveCommonParent();
	}

	private static boolean isNothingToReorg(IResource[] resources, IEGLElement[] eglElements) {
		return resources.length + eglElements.length == 0;
	}

	private static abstract class ReorgPolicy implements IReorgPolicy {
		//invariant: only 1 of these can ever be not null
		private IResource fResourceDestination;
		private IEGLElement fEGLElementDestination;
				
		public final RefactoringStatus setDestination(IResource destination) throws EGLModelException {
			Assert.isNotNull(destination);
			resetDestinations();
			fResourceDestination= destination;
			return verifyDestination(destination);
		}
		public RefactoringStatus setDestination(IEGLElement destination) throws EGLModelException {
			Assert.isNotNull(destination);
			resetDestinations();
			fEGLElementDestination= destination;
			return verifyDestination(destination);
		}
		protected abstract RefactoringStatus verifyDestination(IEGLElement destination) throws EGLModelException;
		protected abstract RefactoringStatus verifyDestination(IResource destination) throws EGLModelException;

		public boolean canChildrenBeDestinations(IEGLElement eglElement) {
			return true;
		}
		public boolean canChildrenBeDestinations(IResource resource) {
			return true;
		}
		public boolean canElementBeDestination(IEGLElement eglElement) {
			return true;
		}
		public boolean canElementBeDestination(IResource resource) {
			return true;
		}
		
		private void resetDestinations() {
			fEGLElementDestination= null;
			fResourceDestination= null;
		}
		public final IResource getResourceDestination(){
			return fResourceDestination;
		}
		public final IEGLElement getEGLElementDestination(){
			return fEGLElementDestination;
		}
		public IFile[] getAllModifiedFiles() {
			return new IFile[0];
		}
		protected RefactoringModifications getModifications() throws CoreException {
			return null;
		}
		public final RefactoringParticipant[] loadParticipants(RefactoringStatus status, RefactoringProcessor processor, String[] natures, SharableParticipants shared) throws CoreException {
			RefactoringModifications modifications= getModifications();
			if (modifications != null) {
				return modifications.loadParticipants(status, processor, natures, shared);
			} else {
				return new RefactoringParticipant[0];
			}
		}
		public RefactoringStatus checkFinalConditions(IProgressMonitor pm, CheckConditionsContext context, IReorgQueries reorgQueries) throws CoreException{
			Assert.isNotNull(reorgQueries);
			ResourceChangeChecker checker= (ResourceChangeChecker) context.getChecker(ResourceChangeChecker.class);
			IFile[] allModifiedFiles= getAllModifiedFiles();
			RefactoringModifications modifications= getModifications();
			IResourceChangeDescriptionFactory deltaFactory= checker.getDeltaFactory();
			for (int i= 0; i < allModifiedFiles.length; i++) {
				deltaFactory.change(allModifiedFiles[i]);
			}
			if (modifications != null) {
				modifications.buildDelta(deltaFactory);
				modifications.buildValidateEdits((ValidateEditChecker)context.getChecker(ValidateEditChecker.class));
			}
			return new RefactoringStatus();
		}
		public boolean hasAllInputSet() {
			return fEGLElementDestination != null || fResourceDestination != null;
		}
		public boolean canUpdateReferences() {
			return false;
		}
		public boolean getUpdateReferences() {
			Assert.isTrue(false);//should not be called if canUpdateReferences is not overridden and returns false
			return false;
		}
		public void setUpdateReferences(boolean update) {
			Assert.isTrue(false);//should not be called if canUpdateReferences is not overridden and returns false
		}
		public boolean canEnableQualifiedNameUpdating() {
			return false;
		}
		public boolean canUpdateQualifiedNames() {
			Assert.isTrue(false);//should not be called if canEnableQualifiedNameUpdating is not overridden and returns false
			return false;
		}
		public String getFilePatterns() {
			Assert.isTrue(false);//should not be called if canEnableQualifiedNameUpdating is not overridden and returns false
			return null;
		}
		public boolean getUpdateQualifiedNames() {
			Assert.isTrue(false);//should not be called if canEnableQualifiedNameUpdating is not overridden and returns false
			return false;
		}
		public void setFilePatterns(String patterns) {
			Assert.isTrue(false);//should not be called if canEnableQualifiedNameUpdating is not overridden and returns false
		}
		public void setUpdateQualifiedNames(boolean update) {
			Assert.isTrue(false);//should not be called if canEnableQualifiedNameUpdating is not overridden and returns false
		}
		public boolean canEnable() throws EGLModelException {
			IResource[] resources= getResources();
			for (int i= 0; i < resources.length; i++) {
				IResource resource= resources[i];
				if (! resource.exists() || resource.isPhantom() || ! resource.isAccessible())
					return false;
			}
			
			IEGLElement[] eglElements= getEGLElements();
			for (int i= 0; i < eglElements.length; i++) {
				IEGLElement element= eglElements[i];
				if (!element.exists()) return false;
			}
			return true;
		}
	}

	private static abstract class FilesFoldersAndCusReorgPolicy extends ReorgPolicy{

		private IEGLFile[] fCus;
		private IFolder[] fFolders;
		private IFile[] fFiles;
		
		protected static class DocumentAndRewrite {
			IEGLDocument document;
			ASTRewrite rewrite;
			public DocumentAndRewrite(IEGLDocument document) {
				super();
				this.document = document;
			}
			public IEGLDocument getDocument() {
				return document;
			}
			public ASTRewrite getRewrite() {
				if (rewrite == null) {
					rewrite = ASTRewrite.create(document.getNewModelEGLFile());
				}
				return rewrite;
			}
			public void setRewrite(ASTRewrite rw) {
				rewrite = rw;
			}
		}
		
		public FilesFoldersAndCusReorgPolicy(IFile[] files, IFolder[] folders, IEGLFile[] cus){
			fFiles= files;
			fFolders= folders;
			fCus= cus;
		}

		protected RefactoringStatus verifyDestination(IEGLElement eglElement) throws EGLModelException {
			Assert.isNotNull(eglElement);
			if (! eglElement.exists())
				return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_doesnotexist0); 
			if (eglElement instanceof IEGLModel)
				return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_jmodel); 
	
			if (eglElement.isReadOnly())
				return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_readonly); 
	
			if (! eglElement.isStructureKnown())
				return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_structure); 
	
			if (eglElement instanceof IOpenable){
				IOpenable openable= (IOpenable)eglElement;
				if (! openable.isConsistent())
					return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_inconsistent); 
			}				
	
			if (eglElement instanceof IPackageFragmentRoot){
				IPackageFragmentRoot root= (IPackageFragmentRoot)eglElement;
				if (root.isArchive())
					return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_archive); 
				if (root.isExternal())
					return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_external); 
			}
			
			if (ReorgUtils.isInsideEGLFile(eglElement)) {
				return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_cannot); 
			}
			
			IContainer destinationAsContainer= getDestinationAsContainer();
			if (destinationAsContainer == null || isChildOfOrEqualToAnyFolder(destinationAsContainer))
				return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_not_this_resource); 
			
			if (containsLinkedResources() && !ReorgUtils.canBeDestinationForLinkedResources(eglElement))
				return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_linked); 
			return new RefactoringStatus();
		}

		protected RefactoringStatus verifyDestination(IResource resource) throws EGLModelException {
			Assert.isNotNull(resource);
			if (! resource.exists() || resource.isPhantom())
				return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_phantom);			 
			if (!resource.isAccessible())
				return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_inaccessible); 
			Assert.isTrue(resource.getType() != IResource.ROOT);
					
			if (isChildOfOrEqualToAnyFolder(resource))
				return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_not_this_resource); 
						
			if (containsLinkedResources() && !ReorgUtils.canBeDestinationForLinkedResources(resource))
				return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_linked); 
	
			return new RefactoringStatus();
		}

		private boolean isChildOfOrEqualToAnyFolder(IResource resource) {
			for (int i= 0; i < fFolders.length; i++) {
				IFolder folder= fFolders[i];
				if (folder.equals(resource) || ParentChecker.isDescendantOf(resource, folder))
					return true;
			}
			return false;
		}
	
		public boolean canChildrenBeDestinations(IEGLElement eglElement) {
			switch (eglElement.getElementType()) {
				case IEGLElement.EGL_MODEL :
				case IEGLElement.EGL_PROJECT :
				case IEGLElement.PACKAGE_FRAGMENT_ROOT :
					return true;
				default :
					return false;
			}
		}
		public boolean canChildrenBeDestinations(IResource resource) {
			return resource instanceof IContainer;
		}
		public boolean canElementBeDestination(IEGLElement eglElement) {
			switch (eglElement.getElementType()) {
				case IEGLElement.PACKAGE_FRAGMENT :
					return true;
				default :
					return false;
			}
		}
		public boolean canElementBeDestination(IResource resource) {
			return resource instanceof IContainer;
		}
		
		private static IContainer getAsContainer(IResource resDest){
			if (resDest instanceof IContainer)
				return (IContainer)resDest;
			if (resDest instanceof IFile)
				return ((IFile)resDest).getParent();
			return null;				
		}
				
		protected final IContainer getDestinationAsContainer(){
			IResource resDest= getResourceDestination();
			if (resDest != null)
				return getAsContainer(resDest);
			IEGLElement jelDest= getEGLElementDestination();
			Assert.isNotNull(jelDest);				
			return getAsContainer(ReorgUtils.getResource(jelDest));
		}
		
		protected final IEGLElement getDestinationContainerAsEGLElement() {
			if (getEGLElementDestination() != null)
				return getEGLElementDestination();
			IContainer destinationAsContainer= getDestinationAsContainer();
			if (destinationAsContainer == null)
				return null;
			IEGLElement je= EGLCore.create(destinationAsContainer);
			if (je != null && je.exists())
				return je;
			return null;
		}
		
		protected final IPackageFragment getDestinationAsPackageFragment() throws EGLModelException {
			IPackageFragment EGLAsPackage= getEGLDestinationAsPackageFragment(getEGLElementDestination());
			if (EGLAsPackage != null)
				return EGLAsPackage;
			return getResourceDestinationAsPackageFragment(getResourceDestination());
		}
				
		private static IPackageFragment getEGLDestinationAsPackageFragment(IEGLElement eglDest) throws EGLModelException{
			if( eglDest == null || ! eglDest.exists())
				return null;					
			if (eglDest instanceof IPackageFragment)
				return (IPackageFragment) eglDest;
			if (eglDest instanceof IPackageFragmentRoot)
				return ((IPackageFragmentRoot) eglDest).getPackageFragment(""); //$NON-NLS-1$
			if (eglDest instanceof IEGLProject) {
				try {
					IPackageFragmentRoot root= ReorgUtils.getCorrespondingPackageFragmentRoot((IEGLProject)eglDest);
					if (root != null)
						return root.getPackageFragment("");  //$NON-NLS-1$
				} catch (EGLModelException e) {
					// fall through
				}
			}
			return (IPackageFragment) eglDest.getAncestor(IEGLElement.PACKAGE_FRAGMENT);
		}
				
		private static IPackageFragment getResourceDestinationAsPackageFragment(IResource resource) throws EGLModelException{
			if (resource instanceof IFile)
				return getEGLDestinationAsPackageFragment(EGLCore.create(resource.getParent()));
			return null;	
		}

		public final IEGLElement[] getEGLElements(){
			return fCus;
		}

		public final IResource[] getResources() {
			return ReorgUtils.union(fFiles, fFolders);
		}

		protected boolean containsLinkedResources() {
			return 	ReorgUtils.containsLinkedResources(fFiles) || 
					ReorgUtils.containsLinkedResources(fFolders) || 
					ReorgUtils.containsLinkedResources(fCus);
		}

		protected final IFolder[] getFolders(){
			return fFolders;
		}
		protected final IFile[] getFiles(){
			return fFiles;
		}
		protected final IEGLFile[] getCus(){
			return fCus;
		}
		public RefactoringStatus checkFinalConditions(IProgressMonitor pm, CheckConditionsContext context, IReorgQueries reorgQueries) throws CoreException {
			RefactoringStatus status= super.checkFinalConditions(pm, context, reorgQueries);
			confirmOverwritting(reorgQueries);
			return status;
		}

		private void confirmOverwritting(IReorgQueries reorgQueries) throws EGLModelException {
			OverwriteHelper oh= new OverwriteHelper();
			oh.setFiles(fFiles);
			oh.setFolders(fFolders);
			oh.setCus(fCus);
			IPackageFragment destPack= getDestinationAsPackageFragment();
			if (destPack != null) {
				oh.confirmOverwritting(reorgQueries, destPack);
			} else {
				IContainer destinationAsContainer= getDestinationAsContainer();
				if (destinationAsContainer != null)
					oh.confirmOverwritting(reorgQueries, destinationAsContainer);
			}	
			fFiles= oh.getFilesWithoutUnconfirmedOnes();
			fFolders= oh.getFoldersWithoutUnconfirmedOnes();
			fCus= oh.getCusWithoutUnconfirmedOnes();
		}
		
		private HashMap documentandRewriteMap = new HashMap();
		
		protected DocumentAndRewrite getDocumentAndRewrite(IEGLFile file) {
			DocumentAndRewrite dnr = (DocumentAndRewrite)documentandRewriteMap.get(file);
			if (dnr == null) {
				IEGLDocument doc = getDocument(file);
				if (doc != null) {
					dnr = new DocumentAndRewrite(getDocument(file));
					documentandRewriteMap.put(file, dnr);
				}
			}
			return dnr;
		}
		
		protected void createUpdatePackageChange(IEGLFile cu, IPackageFragment pack, TextChangeManager changeManager) {

			DocumentAndRewrite dnr = getDocumentAndRewrite(cu);
			if (dnr == null) {
				return;
			}
			
			File ast = dnr.getDocument().getNewModelEGLFile();
			if (ast == null) {
				return;
			}

			boolean defaultPkg = pack.getElementName().length() == 0;
			PackageDeclaration pkgNode = getPackageNode(ast);

			if (defaultPkg) {
				if (pkgNode == null) {
					// moving from one default package to another...no update
					// necessary
					return;
				} else {
					// must delete the package node
					dnr.getRewrite().removeNode(pkgNode);
				}
			} else {

				if (pkgNode == null) {
					// must add a new package node
					dnr.getRewrite().addPackage(ast, pack.getElementName());
				} 
				else {

					if (pkgNode.getName().getCanonicalName().equals(pack.getElementName())) {
						// moving to a like named package...no update necessary
						return;
					}

					else {
						// must change the existing package statement
						dnr.getRewrite().rename(pkgNode.getName(), pack.getElementName());
					}
				}
			}

			TextEdit edit = dnr.getRewrite().rewriteAST(dnr.getDocument());
			TextChangeCompatibility.addTextEdit(changeManager.get(cu), UINlsStrings.MoveRefactoring_updatePackage, edit);
			dnr.setRewrite(null);
			return;

		}

		protected IEGLDocument getDocument(IEGLFile cu) {
			try {
				IEGLFile workingCopy = ((IEGLFile) cu.getWorkingCopy(null, EGLUI.getBufferFactory(), null));
				IBuffer buffer = workingCopy.getBuffer();
				if (buffer instanceof DocumentAdapter) {
					return (IEGLDocument)((DocumentAdapter) buffer).getDocument();
				}
			} catch (EGLModelException e) {
				e.printStackTrace();
				EDTUIPlugin.log(e);
				return null;
			}
			return null;

		}

		protected static PackageDeclaration getPackageNode(File fileAst) {
			if (fileAst.hasPackageDeclaration()) {
				return fileAst.getPackageDeclaration();
			}
			return null;
		}
	}

	private static abstract class PartReorgPolicy extends ReorgPolicy{
		private final IEGLElement[] fEGLElements;
		protected IEGLDocument sourceEGLDocument;
		protected IEGLDocument destinationEGLDocument;
				
		PartReorgPolicy(IEGLElement[] eglElements){
			Assert.isNotNull(eglElements);
			fEGLElements= eglElements;
			
			try {	
				IEGLFile sourceCu = getSourceCu();
				IEGLFile sharedWorkingCopy = (IEGLFile) sourceCu.getWorkingCopy(null, EGLUI.getBufferFactory(), null);
				IBuffer buffer = sharedWorkingCopy.getBuffer();
				
				if (buffer instanceof IEGLDocumentAdapter) {
					IEGLDocumentAdapter adapter = (IEGLDocumentAdapter) buffer;
					sourceEGLDocument = (IEGLDocument) adapter.getDocument();
				}
			} catch (EGLModelException e) {
				throw new RuntimeException(e);
			}
		}
		
		public RefactoringStatus setDestination(IEGLElement destination) throws EGLModelException {
			RefactoringStatus status = super.setDestination(destination);
			
			IEGLFile destinationFile;
			if (destination instanceof IEGLFile)
				destinationFile = (IEGLFile) destination;
			else
				destinationFile = (IEGLFile) destination.getAncestor(IEGLElement.EGL_FILE);
			if(destinationFile != null) {
				IEGLFile workingCopy = (IEGLFile) destinationFile.getWorkingCopy(null, EGLUI.getBufferFactory(), null);
				IBuffer buffer = workingCopy.getBuffer();
				if(buffer instanceof IEGLDocumentAdapter) {
					IEGLDocumentAdapter adapter = (IEGLDocumentAdapter) buffer;
					destinationEGLDocument = (IEGLDocument) adapter.getDocument();
				}
			}
			
			return status;
		}

		protected final RefactoringStatus verifyDestination(IResource destination) throws EGLModelException {
			return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_no_resource); 
		}

		protected final IEGLFile getSourceCu() {
			//all have a common parent, so all must be in the same cu
			//we checked before that the array in not null and not empty
			return (IEGLFile) fEGLElements[0].getAncestor(IEGLElement.EGL_FILE);
		}

		public final IEGLElement[] getEGLElements() {
			return fEGLElements;
		}
		public final IResource[] getResources() {
			return new IResource[0];
		}
	
		protected final IEGLFile getDestinationCu() {
			return getDestinationCu(getEGLElementDestination());
		}

		protected static final IEGLFile getDestinationCu(IEGLElement destination) {
			if (destination instanceof IEGLFile)
				return (IEGLFile) destination;
			return (IEGLFile) destination.getAncestor(IEGLElement.EGL_FILE);
		}

		protected static EGLFileChange createEGLEGLFileChange(IEGLFile cu, IEGLDocument eglDocument, ASTRewrite rewrite) throws CoreException {
			EGLFileChange eglFileChange = new EGLFileChange(cu.getElementName(), cu);
			eglFileChange.setEdit(rewrite.rewriteAST(eglDocument));
			if (eglFileChange != null) {
				if (cu.isWorkingCopy())
					eglFileChange.setSaveMode(TextFileChange.LEAVE_DIRTY);
			}
			return eglFileChange;
		}

		protected void copyToDestination(IEGLElement element, ASTRewrite targetRewriter, File sourceCuNode, File targetCuNode) throws CoreException {
//			final ASTRewrite rewrite= targetRewriter.getASTRewrite();
//			switch(element.getElementType()){
//				case IEGLElement.FIELD: 
//					copyMemberToDestination((IMember) element, targetRewriter, sourceCuNode, targetCuNode, createNewFieldDeclarationNode(((IField)element), rewrite, sourceCuNode));
//					break;
//				case IEGLElement.IMPORT_CONTAINER:
//					copyImportsToDestination((IImportContainer)element, rewrite, sourceCuNode, targetCuNode);
//					break;
//				case IEGLElement.IMPORT_DECLARATION:
//					copyImportToDestination((IImportDeclaration)element, rewrite, sourceCuNode, targetCuNode);
//					break;
//				case IEGLElement.INITIALIZER:
//					copyInitializerToDestination((IInitializer)element, targetRewriter, sourceCuNode, targetCuNode);
//					break;
//				case IEGLElement.METHOD: 
//					copyMethodToDestination((IMethod)element, targetRewriter, sourceCuNode, targetCuNode);
//					break;
//				case IEGLElement.PACKAGE_DECLARATION:
//					copyPackageDeclarationToDestination((IPackageDeclaration)element, rewrite, sourceCuNode, targetCuNode);
//					break;
//				case IEGLElement.TYPE :
//					copyTypeToDestination((IPart) element, targetRewriter, sourceCuNode, targetCuNode);
//					break;
//
//				default: Assert.isTrue(false); 
//			}
		}

		private ClassDataDeclaration createNewFieldDeclarationNode(IField field, ASTRewrite rewrite, File sourceCuNode) throws CoreException {
//			AST targetAst= rewrite.getAST();
//			ITextFileBuffer buffer= null;
//			BodyDeclaration newDeclaration= null;
//			IEGLFile unit= field.getEGLFile();
//			try {
//				buffer= RefactoringFileBuffers.acquire(unit);
//				IDocument document= buffer.getDocument();
//				BodyDeclaration bodyDeclaration= ASTNodeSearchUtil.getFieldOrEnumConstantDeclaration(field, sourceCuNode);
//				if (bodyDeclaration instanceof FieldDeclaration) {
//					FieldDeclaration fieldDeclaration= (FieldDeclaration) bodyDeclaration;
//					if (fieldDeclaration.fragments().size() == 1)
//						return (FieldDeclaration) ASTNode.copySubtree(targetAst, fieldDeclaration);
//					VariableDeclarationFragment originalFragment= ASTNodeSearchUtil.getFieldDeclarationFragmentNode(field, sourceCuNode);
//					VariableDeclarationFragment copiedFragment= (VariableDeclarationFragment) ASTNode.copySubtree(targetAst, originalFragment);
//					newDeclaration= targetAst.newFieldDeclaration(copiedFragment);
//					((FieldDeclaration) newDeclaration).setType((Type) ASTNode.copySubtree(targetAst, fieldDeclaration.getType()));
//				} else if (bodyDeclaration instanceof EnumConstantDeclaration) {
//					EnumConstantDeclaration constantDeclaration= (EnumConstantDeclaration) bodyDeclaration;
//					EnumConstantDeclaration newConstDeclaration= targetAst.newEnumConstantDeclaration();
//					newConstDeclaration.setName((SimpleName) ASTNode.copySubtree(targetAst, constantDeclaration.getName()));
//					AnonymousClassDeclaration anonymousDeclaration= constantDeclaration.getAnonymousClassDeclaration();
//					if (anonymousDeclaration != null)
//						newConstDeclaration.setAnonymousClassDeclaration((AnonymousClassDeclaration) rewrite.createStringPlaceholder(document.get(anonymousDeclaration.getStartPosition(), anonymousDeclaration.getLength()), ASTNode.ANONYMOUS_CLASS_DECLARATION));
//					newDeclaration= newConstDeclaration;
//				} else
//					Assert.isTrue(false);
//				if (newDeclaration != null) {
//					newDeclaration.modifiers().addAll(ASTNodeFactory.newModifiers(targetAst, bodyDeclaration.getModifiers()));
//					Javadoc javadoc= bodyDeclaration.getJavadoc();
//					if (javadoc != null)
//						newDeclaration.setJavadoc((Javadoc) rewrite.createStringPlaceholder(document.get(javadoc.getStartPosition(), javadoc.getLength()), ASTNode.JAVADOC));
//				}
//			} catch (BadLocationException exception) {
//				JavaPlugin.log(exception);
//			} finally {
//				if (buffer != null)
//					RefactoringFileBuffers.release(unit);
//			}
//			return newDeclaration;
			return null;
		}

		private void copyImportsToDestination(IImportContainer container, ASTRewrite rewrite, Node sourceCuNode, Node destinationCuNode) throws EGLModelException {
			//there's no special AST node for the container - we copy all imports
//			IEGLElement[] importDeclarations= container.getChildren();
//			for (int i= 0; i < importDeclarations.length; i++) {
//				Assert.isTrue(importDeclarations[i] instanceof IImportDeclaration);//promised in API
//				IImportDeclaration importDeclaration= (IImportDeclaration)importDeclarations[i];
//				copyImportToDestination(importDeclaration, rewrite, sourceCuNode, destinationCuNode);
//			}
		}

		private void copyImportToDestination(IImportDeclaration declaration, ASTRewrite targetRewrite, Node sourceCuNode, Node destinationCuNode) throws EGLModelException {
//			ImportDeclaration sourceNode= ASTNodeSearchUtil.getImportDeclarationNode(declaration, sourceCuNode);
//			ImportDeclaration copiedNode= (ImportDeclaration) ASTNode.copySubtree(targetRewrite.getAST(), sourceNode);
//			targetRewrite.getListRewrite(destinationCuNode, CompilationUnit.IMPORTS_PROPERTY).insertLast(copiedNode, null);
		}

		private void copyPackageDeclarationToDestination(IPackageDeclaration declaration, ASTRewrite targetRewrite, File sourceCuNode, File destinationCuNode) throws EGLModelException {
//			if (destinationCuNode.getPackage() != null)
//				return;
//			PackageDeclaration sourceNode= ASTNodeSearchUtil.getPackageDeclarationNode(declaration, sourceCuNode);
//			PackageDeclaration copiedNode= (PackageDeclaration) ASTNode.copySubtree(targetRewrite.getAST(), sourceNode);
//			targetRewrite.set(destinationCuNode, CompilationUnit.PACKAGE_PROPERTY, copiedNode, null);
		}

		private void copyTypeToDestination(IPart type, ASTRewrite targetRewriter, File sourceCuNode, File targetCuNode) throws EGLModelException {
//			AbstractTypeDeclaration newType= (AbstractTypeDeclaration) targetRewriter.getASTRewrite().createStringPlaceholder(getUnindentedSource(type), ASTNode.TYPE_DECLARATION);
//			IPart enclosingType= getEnclosingType(getEGLElementDestination());
//			if (enclosingType != null) {
//				copyMemberToDestination(type, targetRewriter, sourceCuNode, targetCuNode, newType);
//			} else {
//				targetRewriter.getASTRewrite().getListRewrite(targetCuNode, CompilationUnit.TYPES_PROPERTY).insertLast(newType, null);
//			}
		}

		private void copyMethodToDestination(IFunction method, ASTRewrite targetRewriter, File sourceCuNode, File targetCuNode) throws EGLModelException {
//			BodyDeclaration newMethod= (BodyDeclaration) targetRewriter.getASTRewrite().createStringPlaceholder(getUnindentedSource(method), ASTNode.METHOD_DECLARATION);
//			copyMemberToDestination(method, targetRewriter, sourceCuNode, targetCuNode, newMethod);
		}

		private void copyMemberToDestination(IMember member, ASTRewrite targetRewriter, File sourceCuNode, File targetCuNode, Node newMember) throws EGLModelException {
//			IEGLElement javaElementDestination= getEGLElementDestination();
//			ASTNode nodeDestination;
//			ASTNode destinationContainer;
//			switch (javaElementDestination.getElementType()) {
//				case IEGLElement.INITIALIZER:
//					nodeDestination= ASTNodeSearchUtil.getInitializerNode((IInitializer) javaElementDestination, targetCuNode);
//					destinationContainer= nodeDestination.getParent();
//					break;
//				case IEGLElement.FIELD:
//					nodeDestination= ASTNodeSearchUtil.getFieldOrEnumConstantDeclaration((IField) javaElementDestination, targetCuNode);
//					destinationContainer= nodeDestination.getParent();
//					break;
//				case IEGLElement.METHOD:
//					nodeDestination= ASTNodeSearchUtil.getMethodOrAnnotationTypeMemberDeclarationNode((IMethod) javaElementDestination, targetCuNode);
//					destinationContainer= nodeDestination.getParent();
//					break;
//				case IEGLElement.TYPE:
//					nodeDestination= null;
//					IPart typeDestination= (IPart) javaElementDestination;
//					if (typeDestination.isAnonymous())
//						destinationContainer= ASTNodeSearchUtil.getClassInstanceCreationNode(typeDestination, targetCuNode).getAnonymousClassDeclaration();
//					else
//						destinationContainer= ASTNodeSearchUtil.getAbstractTypeDeclarationNode(typeDestination, targetCuNode);
//					break;
//				default:
//					nodeDestination= null;
//					destinationContainer= null;
//			}
//			if (!(member instanceof IInitializer)) {
//				BodyDeclaration decl= ASTNodeSearchUtil.getBodyDeclarationNode(member, sourceCuNode);
//				if (decl != null)
//					ImportRewriteUtil.addImports(targetRewriter, decl, new HashMap(), new HashMap(), false);
//			}
//			if (destinationContainer != null) {
//				ListRewrite listRewrite;
//				if (destinationContainer instanceof AbstractTypeDeclaration) {
//					if (newMember instanceof EnumConstantDeclaration && destinationContainer instanceof EnumDeclaration)
//						listRewrite= targetRewriter.getASTRewrite().getListRewrite(destinationContainer, EnumDeclaration.ENUM_CONSTANTS_PROPERTY);
//					else
//						listRewrite= targetRewriter.getASTRewrite().getListRewrite(destinationContainer, ((AbstractTypeDeclaration) destinationContainer).getBodyDeclarationsProperty());
//				} else
//					listRewrite= targetRewriter.getASTRewrite().getListRewrite(destinationContainer, AnonymousClassDeclaration.BODY_DECLARATIONS_PROPERTY);
//
//				if (nodeDestination != null) {
//					final List list= listRewrite.getOriginalList();
//					final int index= list.indexOf(nodeDestination);
//					if (index > 0 && index < list.size() - 1) {
//						listRewrite.insertBefore(newMember, (ASTNode) list.get(index), null);
//					} else
//						listRewrite.insertLast(newMember, null);
//				} else
//					listRewrite.insertAt(newMember, ASTNodes.getInsertionIndex(newMember, listRewrite.getRewrittenList()), null);
//				return; // could insert into/after destination
//			}
//			// fall-back / default:
//			final AbstractTypeDeclaration declaration= ASTNodeSearchUtil.getAbstractTypeDeclarationNode(getDestinationAsType(), targetCuNode);
//			targetRewriter.getASTRewrite().getListRewrite(declaration, declaration.getBodyDeclarationsProperty()).insertLast(newMember, null);
		}

		private static String getUnindentedSource(ISourceReference sourceReference) throws EGLModelException {
			Assert.isTrue(sourceReference instanceof IEGLElement);
			String[] lines= Strings.convertIntoLines(sourceReference.getSource());
			final IEGLProject project= ((IEGLElement) sourceReference).getEGLProject();
			Strings.trimIndentation(lines, JavaCore.create(project.getProject()), false);
			return Strings.concatenate(lines, ReorgUtils.getLineDelimiterUsed((IEGLElement) sourceReference));
		}

		private IPart getDestinationAsType() throws EGLModelException {
			IEGLElement destination= getEGLElementDestination();
			IPart enclosingType= getEnclosingType(destination);
			if (enclosingType != null)
				return enclosingType;
			IEGLFile enclosingCu= getEnclosingCu(destination);
			Assert.isNotNull(enclosingCu);
			IPart mainType= ReorgUtils.getMainPart(enclosingCu);
			Assert.isNotNull(mainType);
			return mainType;
		}

		private static IEGLFile getEnclosingCu(IEGLElement destination) {
			if (destination instanceof IEGLFile)
				return (IEGLFile) destination;
			return (IEGLFile)destination.getAncestor(IEGLElement.EGL_FILE);
		}

		private static IPart getEnclosingType(IEGLElement destination) {
			if (destination instanceof IPart)
				return (IPart) destination;
			return (IPart)destination.getAncestor(IEGLElement.PART);
		}
		
		public boolean canEnable() throws EGLModelException {
			if (! super.canEnable()) return false;
			for (int i= 0; i < fEGLElements.length; i++) {
				if (fEGLElements[i] instanceof IMember){
					IMember member= (IMember) fEGLElements[i];
					//we can copy some binary members, but not all
					if (member.getSourceRange() == null)
						return false;
				}
			}
			return true;
		}	
		
		protected RefactoringStatus verifyDestination(IEGLElement destination) throws EGLModelException {
			return recursiveVerifyDestination(destination);
		}
		
		private RefactoringStatus recursiveVerifyDestination(IEGLElement destination) throws EGLModelException {
			Assert.isNotNull(destination);
			if (!destination.exists())
				return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_doesnotexist1); 
			if (destination instanceof IEGLModel)
				return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_jmodel); 
			if (! (destination instanceof IEGLFile) && ! ReorgUtils.isInsideEGLFile(destination))
				return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_cannot); 

			IEGLFile destinationCu= getDestinationCu(destination);
			Assert.isNotNull(destinationCu);
			if (destinationCu.isReadOnly())//the resource read-onliness is handled by validateEdit
				return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_cannot_modify); 
				
			switch(destination.getElementType()){
				case IEGLElement.EGL_FILE:
					int[] types0= new int[]{IEGLElement.FIELD, IEGLElement.INITIALIZER, IEGLElement.FUNCTION};
					if (ReorgUtils.hasElementsOfType(getEGLElements(), types0))
						return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_cannot);				 
					break;
				case IEGLElement.PACKAGE_DECLARATION:
					return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_package_decl); 
				
				case IEGLElement.IMPORT_CONTAINER:
					if (ReorgUtils.hasElementsNotOfType(getEGLElements(), IEGLElement.IMPORT_DECLARATION))
						return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_cannot); 
					break;
					
				case IEGLElement.IMPORT_DECLARATION:
					if (ReorgUtils.hasElementsNotOfType(getEGLElements(), IEGLElement.IMPORT_DECLARATION))
						return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_cannot); 
					break;
				
				case IEGLElement.FIELD://fall thru
				case IEGLElement.INITIALIZER://fall thru
				case IEGLElement.FUNCTION://fall thru
					return recursiveVerifyDestination(destination.getParent());
				
				case IEGLElement.PART:
					int[] types1= new int[]{IEGLElement.IMPORT_DECLARATION, IEGLElement.IMPORT_CONTAINER, IEGLElement.PACKAGE_DECLARATION};
					if (ReorgUtils.hasElementsOfType(getEGLElements(), types1))
						return recursiveVerifyDestination(destination.getParent());
					break;
			}
				
			return new RefactoringStatus();
		}
		
		public boolean canChildrenBeDestinations(IResource resource) {
			return false;
		}
		public boolean canElementBeDestination(IResource resource) {
			return false;
		}
		
		public boolean canElementBeDestination(IEGLElement eglElement) {
			return IEGLElement.EGL_FILE == eglElement.getElementType();
		}
		
		public boolean canChildrenBeDestinations(IEGLElement eglElement) {
			switch (eglElement.getElementType()) {
			case IEGLElement.EGL_MODEL :
			case IEGLElement.EGL_PROJECT :
			case IEGLElement.PACKAGE_FRAGMENT_ROOT :
			case IEGLElement.PACKAGE_FRAGMENT :
				return true;
			default :
				return false;
			}
		}
		
		public RefactoringStatus checkFinalConditions(IProgressMonitor pm, CheckConditionsContext context, IReorgQueries reorgQueries) throws CoreException {
			RefactoringStatus status = super.checkFinalConditions(pm, context, reorgQueries);
			
			if(status.isOK()) {
				IEGLFile destinationCu = getDestinationCu();
				IPackageFragment pkg = (IPackageFragment) destinationCu.getAncestor(IEGLElement.PACKAGE_FRAGMENT);
				IEGLElement[] elements = getEGLElements();
				if(elements.length != 0) {
					IPackageFragment sourcePkg = (IPackageFragment) elements[0].getAncestor(IEGLElement.PACKAGE_FRAGMENT);
					if(!pkg.getElementName().equals(sourcePkg.getElementName())) {						
						for(int i = 0; i < elements.length; i++) {
							if(RenamePartProcessor.findPartInPackage(pkg, elements[i].getElementName()) != null) {
								String msg= MessageFormat.format(
									UINlsStrings.RenamePartRefactoring_exists, 
									new String[]{elements[i].getElementName(), pkg.getElementName()});
								status.merge(RefactoringStatus.createErrorStatus(msg));
	}
						}
					}
				}
			}

			return status;
		}
				
	}

	private static class MoveEGLFilesPolicy extends FilesFoldersAndCusReorgPolicy implements IEGLMovePolicy {

		private boolean fUpdateReferences;

		private String fFilePatterns;

		private TextChangeManager fChangeManager;

		private MoveModifications fModifications;
		
		private IPart[] fParts;

		MoveEGLFilesPolicy(IFile[] files, IFolder[] folders, IEGLFile[] cus) {
			super(files, folders, cus);
			fUpdateReferences = true;
		}

		protected RefactoringModifications getModifications() throws CoreException {
			if (fModifications != null)
				return fModifications;

			fModifications = new MoveModifications();
			IPackageFragment pack = getDestinationAsPackageFragment();
			IContainer container = getDestinationAsContainer();
			Object unitDestination = null;
			if (pack != null)
				unitDestination = pack;
			else
				unitDestination = container;

			// don't use fUpdateReferences directly since it is only valid if
			// canUpdateReferences is true
			boolean updateReferenes = canUpdateReferences() && getUpdateReferences();
			if (unitDestination != null) {
				IEGLFile[] units = getCus();
				for (int i = 0; i < units.length; i++) {
					fModifications.move(units[i], new MoveArguments(unitDestination, updateReferenes));
				}
			}
			if (container != null) {
				IFile[] files = getFiles();
				for (int i = 0; i < files.length; i++) {
					fModifications.move(files[i], new MoveArguments(container, updateReferenes));
				}
				IFolder[] folders = getFolders();
				for (int i = 0; i < folders.length; i++) {
					fModifications.move(folders[i], new MoveArguments(container, updateReferenes));
				}
			}
			return fModifications;
		}

		protected RefactoringStatus verifyDestination(IEGLElement destination) throws EGLModelException {
			RefactoringStatus superStatus = super.verifyDestination(destination);
			if (superStatus.hasFatalError())
				return superStatus;

			Object commonParent = new ParentChecker(getResources(), getEGLElements()).getCommonParent();
			if (destination.equals(commonParent))
				return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_parent);
			IContainer destinationAsContainer = getDestinationAsContainer();
			if (destinationAsContainer != null && destinationAsContainer.equals(commonParent))
				return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_parent);
			IPackageFragment destinationAsPackage = getDestinationAsPackageFragment();
			if (destinationAsPackage != null && destinationAsPackage.equals(commonParent))
				return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_parent);

			return superStatus;
		}

		protected RefactoringStatus verifyDestination(IResource destination) throws EGLModelException {
			RefactoringStatus superStatus = super.verifyDestination(destination);
			if (superStatus.hasFatalError())
				return superStatus;

			Object commonParent = getCommonParent();
			if (destination.equals(commonParent))
				return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_parent);
			IContainer destinationAsContainer = getDestinationAsContainer();
			if (destinationAsContainer != null && destinationAsContainer.equals(commonParent))
				return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_parent);
			IEGLElement destinationContainerAsPackage = getDestinationContainerAsEGLElement();
			if (destinationContainerAsPackage != null && destinationContainerAsPackage.equals(commonParent))
				return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_parent);

			return superStatus;
		}

		private Object getCommonParent() {
			return new ParentChecker(getResources(), getEGLElements()).getCommonParent();
		}

		public Change createChange(IProgressMonitor pm) throws EGLModelException {
			if (!fUpdateReferences) {
				if (fChangeManager == null) {
					fChangeManager = createChangeManager(new SubProgressMonitor(pm, 1), new RefactoringStatus());
				}
				CompositeChange composite = new CompositeChange(UINlsStrings.ReorgPolicy_move);
				CompositeChange fileMove =  createSimpleMoveChange(pm, fChangeManager);
				composite.merge(new CompositeChange(UINlsStrings.MoveRefactoring_reorganize_elements, fChangeManager.getAllChanges()));
				composite.merge (fileMove);
				return composite;
			} else {
				return createReferenceUpdatingMoveChange(pm);
			}
		}
		
		public Change postCreateChange(Change[] participantChanges, IProgressMonitor pm) throws CoreException {
			return null;
		}

		private Change createReferenceUpdatingMoveChange(IProgressMonitor pm) throws EGLModelException {
			pm.beginTask("", 2); //$NON-NLS-1$
			try {
				CompositeChange composite = new CompositeChange(UINlsStrings.ReorgPolicy_move);
				composite.markAsSynthetic();

				if (fChangeManager == null) {
					fChangeManager = createChangeManager(new SubProgressMonitor(pm, 1), new RefactoringStatus());
				}
				createReferenceChanges(pm, fChangeManager);
				createAddImportsChanges(pm, fChangeManager);
				RefactoringStatus status = Checks.validateModifiedFiles(getAllModifiedFiles(), null);
				if (status.hasFatalError())
					fChangeManager = new TextChangeManager();
				

				CompositeChange fileMove = createSimpleMoveChange(new SubProgressMonitor(pm, 1), fChangeManager);
				composite.merge(new CompositeChange(UINlsStrings.MoveRefactoring_reorganize_elements, fChangeManager.getAllChanges()));

				composite.merge(fileMove);
				return composite;
			} finally {
				pm.done();
			}
		}
		
		private void createAddImportsChanges(IProgressMonitor pm, TextChangeManager changeManager) throws EGLModelException {
			IEGLFile[] files = getCus();

			for (int i = 0; i < files.length; i++) {
				DocumentAndRewrite dnr = getDocumentAndRewrite(files[i]);
				if (dnr != null) {
					ASTRewrite rewrite = dnr.getRewrite();
					OrganizeImportsOperation.OrganizedImportSection importSection = createImportSection(files[i]);
					if(importSection != null) {
						IPart[] parts = files[i].getParts();
						for (int j = 0; j < parts.length; j++) {
							addImportsToReferencedTypes(parts[j].getElementName(), files[i], importSection, pm);
						}
						importSection.addImportsToASTRewrite(rewrite, dnr.getDocument().getNewModelEGLFile());
					}
					removeImportsForNewPackage(files[i]);
					TextEdit edit = rewrite.rewriteAST(dnr.getDocument());
					TextChangeCompatibility
							.addTextEdit(changeManager.get(files[i]), UINlsStrings.MoveRefactoring_updateImports, edit);
					dnr.setRewrite(null);
				}
			}
		}
		
		private void addImportsToReferencedTypes(String partName, final IEGLFile sourceCu, final OrganizedImportSection importSection, IProgressMonitor pm) throws EGLModelException {
			IPackageFragment packageFragment = (IPackageFragment)sourceCu.getAncestor(IEGLElement.PACKAGE_FRAGMENT);
			String packageName = packageFragment.isDefaultPackage() ? "" : packageFragment.getElementName();
			
			WorkingCopyCompiler.getInstance().compilePart(
					sourceCu.getEGLProject().getProject(),
				packageName,
				(IFile) sourceCu.getResource(),
				new IWorkingCopy[] {(IWorkingCopy) sourceCu.getWorkingCopy(pm, EGLUI.getBufferFactory(), null)},
				partName,
				new IWorkingCopyCompileRequestor() {
					public void acceptResult(WorkingCopyCompilationResult result) {
						Part boundPart = (Part)result.getBoundPart();
						OrganizeImportsVisitor organizeImportsVisitor = new OrganizeImportsVisitor(importSection, new HashMap(), new HashSet(), null, sourceCu.getEGLProject().getProject());
						organizeImportsVisitor.setCurrentPartName(boundPart.getName());
						boundPart.accept(organizeImportsVisitor);
						removeReferencesToNewPackage(boundPart, sourceCu);
					}
			});
		}
		
		private void removeReferencesToNewPackage(Part boundPart, IEGLFile sourceCU) {
			//prh
			final DocumentAndRewrite dnr = getDocumentAndRewrite(sourceCU);
			if (dnr == null) {
				return;
			}
			String temp = null;
			try {
				temp = getDestinationAsPackageFragment().getElementName();
			} catch (EGLModelException e) {
				return;
			}
			if (temp.length() == 0) {
				return;
			}
			final String newPkg = temp;
			
			boundPart.accept(new AbstractASTVisitor() {
				public boolean visit(org.eclipse.edt.compiler.core.ast.QualifiedName qualifiedName) {
					if (newPkg.equalsIgnoreCase(qualifiedName.getQualifier().getCanonicalName())) {
						Object qualBinding = qualifiedName.getQualifier().resolveElement();
						if (!(qualBinding instanceof IPackageBinding)) {
							return false;
						}
						
						Object nameBinding = qualifiedName.resolveElement();
						if (nameBinding == null || !(nameBinding instanceof IPackageBinding)) {
							GetNodeAtOffsetVisitor visitor = new GetNodeAtOffsetVisitor(qualifiedName.getOffset(), qualifiedName.getLength());
							dnr.getDocument().getNewModelEGLFile().accept(visitor);
							Node node = visitor.getNode();
							if (node instanceof QualifiedName) {
								QualifiedName foundName = (QualifiedName) node;
								dnr.getRewrite().rename(foundName, qualifiedName.getCaseSensitiveIdentifier());
							}
						}
						return false;
					}
					return true;
				}
			});
		}
				
		private void removeImportsForNewPackage(IEGLFile sourceCu) {
			final DocumentAndRewrite dnr = getDocumentAndRewrite(sourceCu);
			if (dnr == null) {
				return;
			}
			String newPkg = null;
			try {
				IPackageFragment destinationAsPackageFragment = getDestinationAsPackageFragment();
				if(destinationAsPackageFragment != null) {
					newPkg = destinationAsPackageFragment.getElementName();
				}
			} catch (EGLModelException e) {
				return;
			}
			
			if(newPkg != null) {
				Iterator i = dnr.getDocument().getNewModelEGLFile().getImportDeclarations().iterator();
				while (i.hasNext()) {
					String pkgName = "";
					ImportDeclaration importDeclaration = (ImportDeclaration) i.next();
					if (importDeclaration.isOnDemand()) {
						pkgName = importDeclaration.getName().getCanonicalName();
					} else {
						int index = importDeclaration.getName().getCanonicalName().lastIndexOf(".");
						if (index > -1) {
							pkgName = importDeclaration.getName().getCanonicalName().substring(0, index);
						}
					}
					if (pkgName.equalsIgnoreCase(newPkg)) {
						dnr.getRewrite().removeNode(importDeclaration);
					}
				}
			}
		}

				
		private OrganizedImportSection createImportSection(IEGLFile eglFile) throws EGLModelException {
			OrganizedImportSection importSection = null;
			
			IPackageFragment destinationAsPackageFragment = getDestinationAsPackageFragment();
			if(destinationAsPackageFragment != null) {
				importSection = new OrganizeImportsOperation.OrganizedImportSection(destinationAsPackageFragment.getElementName());
	
				DocumentAndRewrite dnr = getDocumentAndRewrite(eglFile);
				if (dnr == null) {
					return importSection;
				}
				
				File file = dnr.getDocument().getNewModelEGLFile();
							
				for(Iterator iter = file.getImportDeclarations().iterator(); iter.hasNext();) {
					ImportDeclaration decl = (ImportDeclaration) iter.next();
					String canonicalName = decl.getName().getCanonicalName();
					if(decl.isOnDemand()) {
						importSection.ignoreImport(canonicalName, "*");
					}
					else {
						int lastDot = canonicalName.lastIndexOf('.');
						if(lastDot != -1) {
							importSection.ignoreImport(canonicalName.substring(0, lastDot), canonicalName.substring(lastDot+1));
						}
						else {
							importSection.ignoreImport("", canonicalName);
						}
					}
				}
				
				IPart[] parts = getParts();
				
				for (int i = 0; i < parts.length; i++) {
					IPackageDeclaration[] packageDeclarations = parts[i].getEGLFile().getPackageDeclarations();
					String sourcePackageName = packageDeclarations.length == 0 ? "" : packageDeclarations[0].getElementName();
					importSection.ignoreImport(sourcePackageName, parts[i].getElementName());
				}
			}
			
			return importSection;
		}
		
		private IPart[] getParts() {
			if (fParts == null) {
				List list = new ArrayList();
				IEGLFile[] files = getCus();
				for (int i = 0; i < files.length; i++) {
					try {
						addAll(files[i].getParts(), list);
					} catch (EGLModelException e) {
						e.printStackTrace();
						EDTUIPlugin.log(e);
						return null;
					}					
				}
				fParts = (IPart[]) list.toArray(new IPart[list.size()]);
			}
			return fParts;
		}
		
		private void addAll(Object[] arr, List list) {
			if (arr == null) {
				return;
			}
			for (int i = 0; i < arr.length; i++) {
				list.add(arr[i]);
			}
		}

		
		private void createReferenceChanges(IProgressMonitor pm, TextChangeManager changeManager) throws EGLModelException {

			MoveReferenceUpdater updater = new MoveReferenceUpdater(changeManager, pm, getDestinationAsPackageFragment(), getCus());
			updater.run();
		}

		
		private TextChangeManager createChangeManager(IProgressMonitor pm, RefactoringStatus status) throws EGLModelException {
			pm.beginTask("", 1);//$NON-NLS-1$
			try {
				if (!fUpdateReferences)
					return new TextChangeManager();

				IPackageFragment packageDest = getDestinationAsPackageFragment();
				if (packageDest != null) {
					MoveEGLFileUpdateCreator creator = new MoveEGLFileUpdateCreator(packageDest);
					return creator.createChangeManager(new SubProgressMonitor(pm, 1), status);
				} else
					return new TextChangeManager();
			} finally {
				pm.done();
			}
		}

		private CompositeChange createSimpleMoveChange(IProgressMonitor pm, TextChangeManager changeManager) throws EGLModelException {
			CompositeChange result = new CompositeChange(UINlsStrings.ReorgPolicy_move);
			result.markAsSynthetic();
			IEGLFile[] cus = getCus();
			if (pm.isCanceled())
				throw new OperationCanceledException();
			for (int i = 0; i < cus.length; i++) {
				Change c = createChange(cus[i], changeManager);
				if (c != null) {
					result.add(c);
				}
				pm.worked(1);
			}
			pm.done();
			return result;
		}

		private Change createChange(IEGLFile cu, TextChangeManager changeManager) throws EGLModelException {
			IPackageFragment pack = getDestinationAsPackageFragment();
			if (pack != null) {
				createUpdatePackageChange(cu, pack, changeManager);
				return moveCuToPackage(cu, pack);
			}
			IContainer container = getDestinationAsContainer();
			
			if (container == null) {
				return new NullChange();
			}
			
			return null;
		}

		private static Change moveCuToPackage(IEGLFile cu, IPackageFragment dest) {
			return new MoveEGLFileChange(cu, dest);
		}

		private void handlePart(IPart type, IPackageFragment destination, IProgressMonitor pm) {
			// QualifiedNameFinder.process(fQualifiedNameSearchResult,
			// type.getFullyQualifiedName(), destination.getElementName() + "."
			// + type.getTypeQualifiedName(), //$NON-NLS-1$
			// fFilePatterns, type.getEGLProject().getProject(), pm);
		}

		public RefactoringStatus checkFinalConditions(IProgressMonitor pm, CheckConditionsContext context, IReorgQueries reorgQueries)
				throws CoreException {
			try {
				pm.beginTask("", 3); //$NON-NLS-1$
				RefactoringStatus result = new RefactoringStatus();
				confirmMovingReadOnly(reorgQueries);
				fChangeManager = createChangeManager(new SubProgressMonitor(pm, 2), result);
				result.merge(super.checkFinalConditions(new SubProgressMonitor(pm, 1), context, reorgQueries));
				return result;
			} catch (EGLModelException e) {
				throw e;
			} catch (CoreException e) {
				throw new EGLModelException(e);
			} finally {
				pm.done();
			}
		}

		private void confirmMovingReadOnly(IReorgQueries reorgQueries) throws CoreException {
			if (!ReadOnlyResourceFinder.confirmMoveOfReadOnlyElements(getEGLElements(), getResources(), reorgQueries))
				throw new OperationCanceledException(); // saying 'no' to this
			// one is like
			// cancelling the whole
			// operation
		}

		public IFile[] getAllModifiedFiles() {
			Set result = new HashSet();
			result.addAll(Arrays.asList(ReorgUtils.getFiles(fChangeManager.getAllCompilationUnits())));
			try {
				if (getDestinationAsPackageFragment() != null && getUpdateReferences()) {
					result.addAll(Arrays.asList(ReorgUtils.getFiles(getCus())));
				}
			} catch (EGLModelException e) {
			}
			return (IFile[]) result.toArray(new IFile[result.size()]);
		}

		public boolean hasAllInputSet() {
			return super.hasAllInputSet() && !canUpdateReferences() && !canUpdateQualifiedNames();
		}

		public boolean canUpdateReferences() {
			if (getCus().length == 0)
				return false;

//			try {
//				IPackageFragment pack = getDestinationAsPackageFragment();
//				if (pack != null && pack.isDefaultPackage())
//					return false;
//			} catch (EGLModelException e) {
//				return false;
//			}
//
//			Object commonParent = getCommonParent();
//			if (EGLReorgUtils.isDefaultPackage(commonParent))
//				return false;
			return true;
		}

		public boolean getUpdateReferences() {
			return fUpdateReferences;
		}

		public void setUpdateReferences(boolean update) {
			fUpdateReferences = update;
		}

		public String getFilePatterns() {
			return fFilePatterns;
		}

		public void setFilePatterns(String patterns) {
			Assert.isNotNull(patterns);
			fFilePatterns = patterns;
		}

		public ICreateTargetQuery getCreateTargetQuery(ICreateTargetQueries createQueries) {
			return createQueries.createNewPackageQuery();
		}

		public boolean isTextualMove() {
			return false;
		}
	}
	
	private static class MovePartsPolicy extends PartReorgPolicy implements IEGLMovePolicy{
		private boolean fUpdateReferences;
		private TextChangeManager fChangeManager;
		
		MovePartsPolicy(IEGLElement[] javaElements){
			super(javaElements);
			
			fUpdateReferences = true;
		}
		protected RefactoringStatus verifyDestination(IEGLElement destination) throws EGLModelException {
			IEGLElement[] elements= getEGLElements();
			for (int i= 0; i < elements.length; i++) {
				IEGLElement parent= destination.getParent();
				while (parent != null) {
					if (parent.equals(elements[i]))
						return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_cannot);
					parent= parent.getParent();
				}
			}

			RefactoringStatus superStatus= super.verifyDestination(destination);
			if (superStatus.hasFatalError())
				return superStatus;
				
			Object commonParent= new ParentChecker(new IResource[0], getEGLElements()).getCommonParent();
			if (destination.equals(commonParent) || Arrays.asList(getEGLElements()).contains(destination))
				return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_element2parent); 
			return superStatus;
		}

		public Change createChange(IProgressMonitor pm) throws EGLModelException {
			pm.beginTask("", 3); //$NON-NLS-1$
			
			if (fChangeManager == null) {
				fChangeManager = new TextChangeManager();
			}
			
			try {
				File sourceFile = sourceEGLDocument.getNewModelEGLFile();
				ASTRewrite sourceRewrite = ASTRewrite.create(sourceFile);
				IEGLElement[] elements = getEGLElements();
				for(int i = 0; i < elements.length; i++) {
					if(IEGLElement.PART == elements[i].getElementType()) {
						sourceRewrite.removeNode(sourceEGLDocument.getNewModelNodeAtOffset(((IPart) elements[i]).getSourceRange().getOffset()));
					}
				}
				
				if (pm.isCanceled())
					throw new OperationCanceledException();
				
				IEGLFile sourceCu = getSourceCu();
				EGLFileChange sourceFileChange = createEGLEGLFileChange(sourceCu, sourceEGLDocument, sourceRewrite);
				fChangeManager.manage(sourceCu, sourceFileChange);
				
				File destinationFile = destinationEGLDocument.getNewModelEGLFile();
				ASTRewrite destinationRewrite = ASTRewrite.create(destinationFile);
				removeAddContentsCommentFromDestinationFile(destinationEGLDocument, destinationRewrite);
				String[] deletions = getDeletedSections(sourceFileChange.getEdit(), sourceEGLDocument);
				for(int i = 0; i < deletions.length; i++) {
					destinationRewrite.addPart(destinationFile, deletions[i]);
				}
				
				if (pm.isCanceled())
					throw new OperationCanceledException();
				
				OrganizeImportsOperation.OrganizedImportSection importSection = createImportSection(destinationFile);
				for(int i = 0; i < elements.length; i++) {
					if(IEGLElement.PART == elements[i].getElementType()) {
						addImportsToReferencedTypes(elements[i].getElementName(), sourceCu, importSection, pm);
					}
				}
				importSection.addImportsToASTRewrite(destinationRewrite, destinationFile);
				IEGLFile destinationCu = getDestinationCu();
				TextChange destinationCuChange = createEGLEGLFileChange(destinationCu, destinationEGLDocument, destinationRewrite);
				fChangeManager.manage(destinationCu, destinationCuChange);
				
				if (pm.isCanceled())
					throw new OperationCanceledException();
				
				if(fUpdateReferences) {
					createReferenceChanges(pm, fChangeManager);
				}
				
				CompositeChange result= new CompositeChange(UINlsStrings.ReorgPolicy_move_members); 
				result.markAsSynthetic();
				result.merge(new CompositeChange(UINlsStrings.MoveRefactoring_reorganize_elements, fChangeManager.getAllChanges()));
				return result;
				
			} catch (CoreException e) {
				throw new EGLModelException(e);
			} finally {
				pm.done();
			}
		}
		
		private void addImportsToReferencedTypes(String partName, final IEGLFile sourceCu, final OrganizedImportSection importSection, IProgressMonitor pm) throws EGLModelException {
			IPackageFragment packageFragment = (IPackageFragment)sourceCu.getAncestor(IEGLElement.PACKAGE_FRAGMENT);
			String packageName = packageFragment.isDefaultPackage() ? "" : packageFragment.getElementName();
			
			WorkingCopyCompiler.getInstance().compilePart(
				sourceCu.getEGLProject().getProject(),
				packageName,
				(IFile) sourceCu.getResource(),
				new IWorkingCopy[] {(IWorkingCopy) sourceCu.getWorkingCopy(pm, EGLUI.getBufferFactory(), null)},
				partName,
				new IWorkingCopyCompileRequestor() {
					public void acceptResult(WorkingCopyCompilationResult result) {
						Part boundPart = (Part)result.getBoundPart();
						OrganizeImportsVisitor organizeImportsVisitor = new OrganizeImportsVisitor(importSection, new HashMap(), new HashSet(), null, sourceCu.getEGLProject().getProject());
						organizeImportsVisitor.setCurrentPartName(boundPart.getName());
						boundPart.accept(organizeImportsVisitor);												
					}
			});
		}
		
		private OrganizedImportSection createImportSection(File destinationFile) throws EGLModelException {
			final String destinationPackageName = destinationFile.hasPackageDeclaration() ? destinationFile.getPackageDeclaration().getName().getCanonicalName() : "";
			OrganizedImportSection importSection = new OrganizeImportsOperation.OrganizedImportSection(destinationPackageName);
			
			for(Iterator iter = destinationFile.getImportDeclarations().iterator(); iter.hasNext();) {
				ImportDeclaration decl = (ImportDeclaration) iter.next();
				String canonicalName = decl.getName().getCanonicalName();
				if(decl.isOnDemand()) {
					importSection.ignoreImport(canonicalName, "*");
				}
				else {
					int lastDot = canonicalName.lastIndexOf('.');
					if(lastDot != -1) {
						importSection.ignoreImport(canonicalName.substring(0, lastDot), canonicalName.substring(lastDot+1));
					}
					else {
						importSection.ignoreImport("", canonicalName);
					}
				}
			}
			
			IEGLElement[] elements = getEGLElements();
			IPackageDeclaration[] packageDeclarations = getSourceCu().getPackageDeclarations();
			String sourcePackageName = packageDeclarations.length == 0 ? "" : packageDeclarations[0].getElementName();
			for(int i = 0; i < elements.length; i++) {
				importSection.ignoreImport(sourcePackageName, elements[i].getElementName());
			}
			
			return importSection;
		}

				
		private void removeAddContentsCommentFromDestinationFile(IEGLDocument destinationEGLDocument, ASTRewrite destinationRewrite) {
			int numberOfLines = destinationEGLDocument.getNumberOfLines();
			if(numberOfLines == 1 || numberOfLines == 3) {
				String addContentsString = NewWizardMessages.NewEGLFileWizardPageFilecontents;
				int i = destinationEGLDocument.get().indexOf(addContentsString);
				if(i != -1) {
					try {
						int startOffset = numberOfLines == 1 ? 0 : destinationEGLDocument.getLineLength(0);
						int length = numberOfLines == 1 ? addContentsString.length() : i - startOffset + addContentsString.length();
						destinationRewrite.removeText(startOffset, length);
					} catch (BadLocationException e) {
						throw new RuntimeException(e);
					}					
				}
			}
			
		}
		
		private String[] getDeletedSections(TextEdit edit, IEGLDocument eglDocument) {
			List result = new ArrayList();
			TextEdit[] children = edit.getChildren();
			for(int i = 0; i < children.length; i++) {
				if(children[i] instanceof DeleteEdit) {
					try {
						result.add(eglDocument.get(children[i].getOffset(), children[i].getLength()).trim());
					} catch (BadLocationException e) {
						throw new RuntimeException(e);
					}
				}
			}
			return (String[]) result.toArray(new String[0]);
		}
		
		private void createReferenceChanges(IProgressMonitor pm, TextChangeManager changeManager) throws EGLModelException {
			MoveReferenceUpdater updater = new MoveReferenceUpdater(changeManager, pm, (IPackageFragment) getDestinationCu().getAncestor(IEGLElement.PACKAGE_FRAGMENT), getMovingParts());
			updater.run();
		}
		
		private IPart[] getMovingParts() {
			List parts = new ArrayList();
			IEGLElement[] elements = getEGLElements();
			for(int i = 0; i < elements.length; i++) {
				if(IEGLElement.PART == elements[i].getElementType()) {
					parts.add(elements[i]);
				}
			}
			return (IPart[]) parts.toArray(new IPart[0]);
		}
		
		public Change postCreateChange(Change[] participantChanges, IProgressMonitor pm) throws CoreException {
			return null;
		}

		public boolean canEnable() throws EGLModelException {
			return super.canEnable() && getSourceCu() != null; //can move only elements from cus
		}

		public IFile[] getAllModifiedFiles() {
			return ReorgUtils.getFiles(new IResource[]{ReorgUtils.getResource(getSourceCu()), ReorgUtils.getResource(getDestinationCu())});
		}
		
		public ICreateTargetQuery getCreateTargetQuery(ICreateTargetQueries createQueries) {
			return createQueries.createNewEGLFileQuery();
		}
		public boolean isTextualMove() {
			return true;
		}
		
		public boolean canUpdateReferences(){
			return true;							
		}
		public boolean getUpdateReferences() {
			return fUpdateReferences;
		}
		public void setUpdateReferences(boolean update) {
			fUpdateReferences= update;
		}
	}
		
	private static class NoMovePolicy extends ReorgPolicy implements IEGLMovePolicy{
		protected RefactoringStatus verifyDestination(IResource resource) throws EGLModelException {
			return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_noMoving); 
		}
		protected RefactoringStatus verifyDestination(IEGLElement javaElement) throws EGLModelException {
			return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_noMoving); 
		}
		public Change createChange(IProgressMonitor pm) {
			return new NullChange();
		}
		public Change postCreateChange(Change[] participantChanges, IProgressMonitor pm) throws CoreException {
			return null;
		}
		public boolean canEnable() throws EGLModelException {
			return false;
		}
		public IResource[] getResources() {
			return new IResource[0];
		}
		public IEGLElement[] getEGLElements() {
			return new IEGLElement[0];
		}
		public ICreateTargetQuery getCreateTargetQuery(ICreateTargetQueries createQueries) {
			return null;
		}
		public boolean isTextualMove() {
			return true;
		}
	}
	
	private static class NoCopyPolicy extends ReorgPolicy implements IEGLCopyPolicy{
		public boolean canEnable() throws EGLModelException {
			return false;
		}
		public ReorgExecutionLog getReorgExecutionLog() {
			return null;
		}
		protected RefactoringStatus verifyDestination(IResource resource) throws EGLModelException {
			return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_noCopying); 
		}
		protected RefactoringStatus verifyDestination(IEGLElement javaElement) throws EGLModelException {
			return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgPolicyFactory_noCopying); 
		}
		public Change createChange(IProgressMonitor pm, INewNameQueries copyQueries) {
			return new NullChange();
		}
		public IResource[] getResources() {
			return new IResource[0];
		}
		public IEGLElement[] getEGLElements() {
			return new IEGLElement[0];
		}
	}
	
	private static class NewNameProposer{
		private final Set fAutoGeneratedNewNames= new HashSet(2);
			
		public String createNewName(IEGLFile cu, IPackageFragment destination){
			if (isNewNameOk(destination, cu.getElementName()))
				return null;
			if (! ReorgUtils.isParentInWorkspaceOrOnDisk(cu, destination))
				return null;
			int i= 1;
			while (true){
				String newName;
				if (i == 1)
					newName= MessageFormat.format(UINlsStrings.CopyRefactoring_cu_copyOf1, 
								new String[] {cu.getElementName()});
				else	
					newName= MessageFormat.format(UINlsStrings.CopyRefactoring_cu_copyOfMore, 
								new String[]{String.valueOf(i), cu.getElementName()});
				if (isNewNameOk(destination, newName) && ! fAutoGeneratedNewNames.contains(newName)){
					fAutoGeneratedNewNames.add(newName);
					return removeTrailingEGL(newName);
				}
				i++;
			}
		}
		private static String removeTrailingEGL(String name) {
			int index = name.indexOf(".egl");
			if (index == -1)
				return name;
			return name.substring(0, index);
		}
		public String createNewName(IResource res, IContainer destination){
			if (isNewNameOk(destination, res.getName()))
				return null;
			if (! ReorgUtils.isParentInWorkspaceOrOnDisk(res, destination))
				return null;
			int i= 1;
			while (true){
				String newName;
				if (i == 1)
					newName= MessageFormat.format(UINlsStrings.CopyRefactoring_resource_copyOf1, 
							new String[] {res.getName()});
				else
					newName= MessageFormat.format(UINlsStrings.CopyRefactoring_resource_copyOfMore, 
								new String[]{String.valueOf(i), res.getName()});
				if (isNewNameOk(destination, newName) && ! fAutoGeneratedNewNames.contains(newName)){
					fAutoGeneratedNewNames.add(newName);
					return newName;
				}
				i++;
			}	
		}
	
		public String createNewName(IPackageFragment pack, IPackageFragmentRoot destination){
			if (isNewNameOk(destination, pack.getElementName()))
				return null;
			if (! ReorgUtils.isParentInWorkspaceOrOnDisk(pack, destination))
				return null;
			int i= 1;
			while (true){
				String newName;
				if (i == 1)
					newName= MessageFormat.format(UINlsStrings.CopyRefactoring_package_copyOf1, 
							new String[] {pack.getElementName()});
				else
					newName= MessageFormat.format(UINlsStrings.CopyRefactoring_package_copyOfMore, 
								new String[]{String.valueOf(i), pack.getElementName()});
				if (isNewNameOk(destination, newName) && ! fAutoGeneratedNewNames.contains(newName)){
					fAutoGeneratedNewNames.add(newName);
					return newName;
				}
				i++;
			}	
		}
		private static boolean isNewNameOk(IPackageFragment dest, String newName) {
			return ! dest.getEGLFile(newName).exists();
		}
	
		private static boolean isNewNameOk(IContainer container, String newName) {
			return container.findMember(newName) == null;
		}

		private static boolean isNewNameOk(IPackageFragmentRoot root, String newName) {
			return ! root.getPackageFragment(newName).exists() ;
		}
	}
	
	private static class CopyEGLFilesPolicy extends FilesFoldersAndCusReorgPolicy implements IEGLCopyPolicy {
		private CopyModifications fModifications;
		private ReorgExecutionLog fReorgExecutionLog;
		
		CopyEGLFilesPolicy(IFile[] files, IFolder[] folders, IEGLFile[] cus){
			super(files, folders, cus);
		}
		public ReorgExecutionLog getReorgExecutionLog() {
			return fReorgExecutionLog;
		}
		protected RefactoringModifications getModifications() throws CoreException {
			if (fModifications != null)
				return fModifications;
			fModifications= new CopyModifications();
			fReorgExecutionLog= new ReorgExecutionLog();
			CopyArguments jArgs= new CopyArguments(getDestination(), fReorgExecutionLog);
			CopyArguments rArgs= new CopyArguments(getDestinationAsContainer(), fReorgExecutionLog);
			IEGLFile[] cus= getCus();
			for (int i= 0; i < cus.length; i++) {
				fModifications.copy(cus[i], jArgs, rArgs);
			}
			IResource[] resources= ReorgUtils.union(getFiles(), getFolders());
			for (int i= 0; i < resources.length; i++) {
				fModifications.copy(resources[i], rArgs);
			}
			return fModifications;
		}
		private Object getDestination() throws EGLModelException {
			Object result= getDestinationAsPackageFragment();
			if (result != null)
				return result;
			return getDestinationAsContainer();
		}
		public Change createChange(IProgressMonitor pm, INewNameQueries copyQueries) throws EGLModelException {
			IFile[] file= getFiles();
			IFolder[] folders= getFolders();
			IEGLFile[] cus= getCus();
			pm.beginTask("", cus.length + file.length + folders.length); //$NON-NLS-1$
			NewNameProposer nameProposer= new NewNameProposer();
			CompositeChange composite= new CompositeChange(UINlsStrings.ReorgPolicy_copy); 
			composite.markAsSynthetic();
			for (int i= 0; i < cus.length; i++) {
				composite.add(createChange(cus[i], nameProposer, copyQueries));
				pm.worked(1);
			}
			if (pm.isCanceled())
				throw new OperationCanceledException();
			for (int i= 0; i < file.length; i++) {
				composite.add(createChange(file[i], nameProposer, copyQueries));
				pm.worked(1);
			}
			if (pm.isCanceled())
				throw new OperationCanceledException();
			for (int i= 0; i < folders.length; i++) {
				composite.add(createChange(folders[i], nameProposer, copyQueries));
				pm.worked(1);
			}
			pm.done();
			return composite;
		}

		private Change createChange(IEGLFile unit, NewNameProposer nameProposer, INewNameQueries copyQueries) throws EGLModelException {
			IPackageFragment pack= getDestinationAsPackageFragment();
			if (pack != null)
				return copyCuToPackage(unit, pack, nameProposer, copyQueries);
			IContainer container= getDestinationAsContainer();
			return copyFileToContainer(unit, container, nameProposer, copyQueries);
		}
	
		private static Change copyFileToContainer(IEGLFile cu, IContainer dest, NewNameProposer nameProposer, INewNameQueries copyQueries) {
			IResource resource= ReorgUtils.getResource(cu);
			return createCopyResourceChange(resource, nameProposer, copyQueries, dest);
		}

		private Change createChange(IResource resource, NewNameProposer nameProposer, INewNameQueries copyQueries) {
			IContainer dest= getDestinationAsContainer();
			return createCopyResourceChange(resource, nameProposer, copyQueries, dest);
		}
			
		private static Change createCopyResourceChange(IResource resource, NewNameProposer nameProposer, INewNameQueries copyQueries, IContainer destination) {
			if (resource == null || destination == null)
				return new NullChange();
			INewNameQuery nameQuery;
			String name= nameProposer.createNewName(resource, destination);
			if (name == null)
				nameQuery= copyQueries.createNullQuery();
			else
				nameQuery= copyQueries.createNewResourceNameQuery(resource, name);
			return new CopyResourceChange(resource, destination, nameQuery);
		}

		private Change copyCuToPackage(IEGLFile cu, IPackageFragment dest, NewNameProposer nameProposer, INewNameQueries copyQueries) throws EGLModelException {
			IResource res= ReorgUtils.getResource(cu);
			if (res != null && res.isLinked()){
				if (dest.getResource() instanceof IContainer)
					return copyFileToContainer(cu, (IContainer)dest.getResource(), nameProposer, copyQueries);
			}
			
			String newName= nameProposer.createNewName(cu, dest);
			Change simpleCopy= new CopyEGLFileChange(cu, dest, copyQueries.createStaticQuery(newName));
			if (newName == null || newName.equals(cu.getElementName())) {
				return simpleCopy;
			}
		
			try {
				IPath newPath= cu.getResource().getParent().getFullPath().append(newName);				
				INewNameQuery nameQuery= copyQueries.createNewEGLFileNameQuery(cu, newName);
				return new CreateCopyOfEGLFileChange(newPath, cu.getSource(), cu, nameQuery);
			} catch(CoreException e) {
				return simpleCopy; //fallback - no ui here
			}
		}
	}
	
	private static class ActualSelectionComputer {
		private final IResource[] fResources;
		private final IEGLElement[] fEGLElements;

		public ActualSelectionComputer(IEGLElement[]  eglElements, IResource[] resources) {
			fEGLElements= eglElements;
			fResources= resources;
		}
		
		public IEGLElement[] getActualEGLElementsToReorg() throws EGLModelException {
			List result= new ArrayList();
			for (int i= 0; i < fEGLElements.length; i++) {
				IEGLElement element= fEGLElements[i];
				if (element == null)
					continue;
				if (ReorgUtils.isDeletedFromEditor(element))
					continue;
//TODO: the following code is from Java... if the part you select is the only one in a cu, it moves the entire cu. I'm not sure we want to do this in EGL
//				if (element instanceof IPart) {
//					IPart type= (IPart)element;
//					IEGLFile cu= type.getEGLFile();
//					if (cu != null && type.getDeclaringPart() == null && cu.exists() && cu.getParts().length == 1 && ! result.contains(cu))
//						result.add(cu);
//					else if (! result.contains(type))
//						result.add(type);
//				} else
				if (! result.contains(element)){
					result.add(element);
				}
			}
			return (IEGLElement[]) result.toArray(new IEGLElement[result.size()]);
		}
		
		public IResource[] getActualResourcesToReorg() {
			Set eglElementSet= new HashSet(Arrays.asList(fEGLElements));	
			List result= new ArrayList();
			for (int i= 0; i < fResources.length; i++) {
				if (fResources[i] == null)
					continue;
				IEGLElement element= EGLCore.create(fResources[i]);
				if (element == null || ! element.exists() || ! eglElementSet.contains(element))
					if (! result.contains(fResources[i]))
							result.add(fResources[i]);
			}
			return (IResource[]) result.toArray(new IResource[result.size()]);

		}
	}
}
