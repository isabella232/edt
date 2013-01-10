/*******************************************************************************
 * Copyright © 2009, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
// ---------------------------------------------------------------------------
// Change History                                                                                              
// ---------------------------------------------------------------------------
// Flag  Reason   	Who  Date       Description of changes
// ----  --------- 	---  ---------- ------------------------------------------
// $bd1  61709      HBA  08Nov2010  [TVT] Wrong display for file name
// ---------------------------------------------------------------------------

package org.eclipse.edt.ide.ui.internal.property.pages;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.edt.ide.ui.internal.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPageExtension;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;



/**
 * Class that gives access to dialogs used by the Java build path page to configure classpath entries
 * and properties of classpath entries.
 * Static methods are provided to show dialogs for:
 * <ul>
 *  <li> configuration of source attachments</li>
 *  <li> configuration of Javadoc locations</li>
 *  <li> configuration and selection of classpath variable entries</li>
 *  <li> configuration and selection of classpath container entries</li>
 *  <li> configuration and selection of JAR and external JAR entries</li>
 *  <li> selection of class and source folders</li>
 * </ul>
 * <p>
 * This class is not intended to be instantiated or subclassed by clients.
 * </p>
 * @since 3.0
 * 
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @noextend This class is not intended to be subclassed by clients.
 */
public final class BuildPathDialogAccess {

	private BuildPathDialogAccess() {
		// do not instantiate
	}
	
	/**
	 * Shows the UI for configuring source attachments. <code>null</code> is returned
	 * if the user cancels the dialog. The dialog does not apply any changes.
	 * 
	 * @param shell The parent shell for the dialog
	 * @param initialEntry The entry to edit. The kind of the classpath entry must be either
	 * <code>IEGLPathEntry.CPE_LIBRARY</code> or <code>IEGLPathEntry.CPE_VARIABLE</code>.
	 * @return Returns the resulting classpath entry containing a potentially modified source attachment path and
	 * source attachment root. The resulting entry can be used to replace the original entry on the classpath.
	 * Note that the dialog does not make any changes on the passed entry nor on the classpath that
	 * contains it.
	 */
	public static IEGLPathEntry configureSourceAttachment(Shell shell, IEGLPathEntry initialEntry) {
		if (initialEntry == null) {
			throw new IllegalArgumentException();
		}
		int entryKind= initialEntry.getEntryKind();
		if (entryKind != IEGLPathEntry.CPE_LIBRARY && entryKind != IEGLPathEntry.CPE_VARIABLE) {
			throw new IllegalArgumentException();
		}
		
		SourceAttachmentDialog dialog=  new SourceAttachmentDialog(shell, initialEntry);
		if (dialog.open() == Window.OK) {
			return dialog.getResult();
		}
		return null;
	}
			
	/**
	 * Shows the UI for configuring a variable classpath entry. See {@link IEGLPathEntry#CPE_VARIABLE} for
	 * details about variable classpath entries.
	 * The dialog returns the configured classpath entry path or <code>null</code> if the dialog has
	 * been canceled. The dialog does not apply any changes.
	 * 
	 * @param shell The parent shell for the dialog.
	 * @param initialEntryPath The initial variable classpath variable path or <code>null</code> to use
	 * an empty path. 
	 * @param existingPaths An array of paths that are already on the classpath and therefore should not be
	 * selected again.
	 * @return Returns the configures classpath entry path or <code>null</code> if the dialog has
	 * been canceled.
	 */
	public static IPath configureVariableEntry(Shell shell, IPath initialEntryPath, IPath[] existingPaths) {
		if (existingPaths == null) {
			throw new IllegalArgumentException();
		}
		
//		EditVariableEntryDialog dialog= new EditVariableEntryDialog(shell, initialEntryPath, existingPaths);
//		if (dialog.open() == Window.OK) {
//			return dialog.getPath();
//		}
		return null;
	}
	
	/**
	 * Shows the UI for selecting new variable classpath entries. See {@link IEGLPathEntry#CPE_VARIABLE} for
	 * details about variable classpath entries.
	 * The dialog returns an array of the selected variable entries or <code>null</code> if the dialog has
	 * been canceled. The dialog does not apply any changes.
	 * 
	 * @param shell The parent shell for the dialog.
	 * @param existingPaths An array of paths that are already on the classpath and therefore should not be
	 * selected again.
	 * @return Returns an non empty array of the selected variable entries or <code>null</code> if the dialog has
	 * been canceled.
	 */
	public static IPath[] chooseVariableEntries(Shell shell, IPath[] existingPaths) {
		if (existingPaths == null) {
			throw new IllegalArgumentException();
		}
//		NewVariableEntryDialog dialog= new NewVariableEntryDialog(shell);
//		if (dialog.open() == Window.OK) {
//			return dialog.getResult();
//		}
		return null;
	}
	
	/**
	 * Shows the UI to configure a classpath container classpath entry. See {@link IEGLPathEntry#CPE_CONTAINER} for
	 * details about container classpath entries.
	 * The dialog returns the configured classpath entry or <code>null</code> if the dialog has
	 * been canceled. The dialog does not apply any changes.
	 * 
	 * @param shell The parent shell for the dialog.
	 * @param initialEntry The initial classpath container entry.
	 * @param project The project the entry belongs to. The project does not have to exist and can also be <code>null</code>.
	 * @param currentClasspath The class path entries currently selected to be set as the projects classpath. This can also
	 * include the entry to be edited. The dialog uses these entries as information only (e.g. to avoid duplicate entries); The user still can make changes after the
	 * the classpath container dialog has been closed. See {@link IClasspathContainerPageExtension} for
	 * more information.
	 * @return Returns the configured classpath container entry or <code>null</code> if the dialog has
	 * been canceled by the user.
	 */
	public static IEGLPathEntry configureContainerEntry(Shell shell, IEGLPathEntry initialEntry, IEGLProject project, IEGLPathEntry[] currentClasspath) {
		if (initialEntry == null || currentClasspath == null) {
			throw new IllegalArgumentException();
		}
		
//		ClasspathContainerWizard wizard= new ClasspathContainerWizard(initialEntry, project, currentClasspath);
//		if (ClasspathContainerWizard.openWizard(shell, wizard) == Window.OK) {
//			IEGLPathEntry[] created= wizard.getNewEntries();
//			if (created != null && created.length == 1) {
//				return created[0];
//			}
//		}
		return null;
	}
	
	/**
	 * Shows the UI to choose new classpath container classpath entries. See {@link IEGLPathEntry#CPE_CONTAINER} for
	 * details about container classpath entries.
	 * The dialog returns the selected classpath entries or <code>null</code> if the dialog has
	 * been canceled. The dialog does not apply any changes.
	 * 
	 * @param shell The parent shell for the dialog.
	 * @param project The project the entry belongs to. The project does not have to exist and
	 * can also be <code>null</code>.
	 * @param currentClasspath The class path entries currently selected to be set as the projects classpath. This can also
	 * include the entry to be edited. The dialog uses these entries as information only; The user still can make changes after the
	 * the classpath container dialog has been closed. See {@link IClasspathContainerPageExtension} for
	 * more information.
	 * @return Returns the selected classpath container entries or <code>null</code> if the dialog has
	 * been canceled by the user.
	 */
	public static IEGLPathEntry[] chooseContainerEntries(Shell shell, IEGLProject project, IEGLPathEntry[] currentClasspath) {
		if (currentClasspath == null) {
			throw new IllegalArgumentException();
		}
		
//		ClasspathContainerWizard wizard= new ClasspathContainerWizard((IEGLPathEntry) null, project, currentClasspath);
//		if (ClasspathContainerWizard.openWizard(shell, wizard) == Window.OK) {
//			return wizard.getNewEntries();
//		}
		return null;
	}
	
	
	/**
	 * Shows the UI to configure a JAR or ZIP archive located in the workspace.
	 * The dialog returns the configured classpath entry path or <code>null</code> if the dialog has
	 * been canceled. The dialog does not apply any changes.
	 * 
	 * @param shell The parent shell for the dialog.
	 * @param initialEntry The path of the initial archive entry 
	 * @param usedEntries An array of paths that are already on the classpath and therefore should not be
	 * selected again.
	 * @return Returns the configured JAR path or <code>null</code> if the dialog has
	 * been canceled by the user.
	 */
	public static IPath configureJAREntry(Shell shell, IPath initialEntry, IPath[] usedEntries) {
		if (initialEntry == null || usedEntries == null) {
			throw new IllegalArgumentException();
		}
		
		Class[] acceptedClasses= new Class[] { IFile.class };
		TypedElementSelectionValidator validator= new TypedElementSelectionValidator(acceptedClasses, false);
		
		ArrayList usedJars= new ArrayList(usedEntries.length);
		IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();
		for (int i= 0; i < usedEntries.length; i++) {
			IPath curr= usedEntries[i];
			if (!curr.equals(initialEntry)) {
				IResource resource= root.findMember(usedEntries[i]);
				if (resource instanceof IFile) {
					usedJars.add(resource);
				}
			}
		}
		
		IResource existing= root.findMember(initialEntry);
		
		FilteredElementTreeSelectionDialog dialog= new FilteredElementTreeSelectionDialog(shell, new WorkbenchLabelProvider(), new WorkbenchContentProvider());
		dialog.setValidator(validator);
		dialog.setTitle(NewWizardMessages.BuildPathDialogAccess_EGLARArchiveDialog_edit_title); 
		dialog.setMessage(NewWizardMessages.BuildPathDialogAccess_EGLARArchiveDialog_edit_description); 
		dialog.setInitialFilter(ArchiveFileFilter.EGLAR_FILTER_STRING);
		dialog.addFilter(new ArchiveFileFilter(usedJars, true, true));
		dialog.setInput(root);
		dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
		dialog.setInitialSelection(existing);

		if (dialog.open() == Window.OK) {
			IResource element= (IResource) dialog.getFirstResult();
			return element.getFullPath();
		}
		return null;
	}
	
	/**
	 * Shows the UI to select new JAR or ZIP archive entries located in the workspace.
	 * The dialog returns the selected entries or <code>null</code> if the dialog has
	 * been canceled. The dialog does not apply any changes.
	 * 
	 * @param shell The parent shell for the dialog.
	 * @param initialSelection The path of the element (container or archive) to initially select or <code>null</code> to not select an entry. 
	 * @param usedEntries An array of paths that are already on the classpath and therefore should not be
	 * selected again.
	 * @return Returns the new JAR paths or <code>null</code> if the dialog has
	 * been canceled by the user.
	 */
	public static IPath[] chooseJAREntries(Shell shell, IPath initialSelection, IPath[] usedEntries) {
		if (usedEntries == null) {
			throw new IllegalArgumentException();
		}
		
		Class[] acceptedClasses= new Class[] { IFile.class };
		TypedElementSelectionValidator validator= new TypedElementSelectionValidator(acceptedClasses, true);
		ArrayList usedJars= new ArrayList(usedEntries.length);
		IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();
		for (int i= 0; i < usedEntries.length; i++) {
			IResource resource= root.findMember(usedEntries[i]);
			if (resource instanceof IFile) {
				usedJars.add(resource);
			}
		}
		IResource focus= initialSelection != null ? root.findMember(initialSelection) : null;
		
		FilteredElementTreeSelectionDialog dialog= new FilteredElementTreeSelectionDialog(shell, new WorkbenchLabelProvider(), new WorkbenchContentProvider());
		dialog.setHelpAvailable(false);
		dialog.setValidator(validator);
		dialog.setTitle(NewWizardMessages.BuildPathDialogAccess_EGLARArchiveDialog_new_title); 
		dialog.setMessage(NewWizardMessages.BuildPathDialogAccess_EGLARArchiveDialog_new_description); 
		//@bd1a Start
		int orientation = org.eclipse.jface.window.Window.getDefaultOrientation();
		if (orientation == SWT.RIGHT_TO_LEFT){
			dialog.setInitialFilter(TextProcessor.process(ArchiveFileFilter.EGLAR_FILTER_STRING,".")  );
		}else{ 
		//@bd1a End
			dialog.setInitialFilter(ArchiveFileFilter.EGLAR_FILTER_STRING);
		} //@bd1a
		dialog.addFilter(new ArchiveFileFilter(usedJars, true, true));
		dialog.setInput(root);
		dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
		dialog.setInitialSelection(focus);

		if (dialog.open() == Window.OK) {
			Object[] elements= dialog.getResult();
			IPath[] res= new IPath[elements.length];
			for (int i= 0; i < res.length; i++) {
				IResource elem= (IResource)elements[i];
				res[i]= elem.getFullPath();
			}
			return res;
		}
		return null;
	}
	
	/**
	 * Shows the UI to configure an external JAR or ZIP archive.
	 * The dialog returns the configured or <code>null</code> if the dialog has
	 * been canceled. The dialog does not apply any changes.
	 * 
	 * @param shell The parent shell for the dialog.
	 * @param initialEntry The path of the initial archive entry.
	 * @return Returns the configured external JAR path or <code>null</code> if the dialog has
	 * been canceled by the user.
	 */
	public static IPath configureExternalJAREntry(Shell shell, IPath initialEntry) {
		if (initialEntry == null) {
			throw new IllegalArgumentException();
		}
		
		String lastUsedPath= initialEntry.removeLastSegments(1).toOSString();
		
		FileDialog dialog= new FileDialog(shell, SWT.SINGLE);
		dialog.setText(NewWizardMessages.BuildPathDialogAccess_ExtEGLARArchiveDialog_edit_title); 
		dialog.setFilterExtensions(ArchiveFileFilter.EGLAR_FILTER_EXTENSIONS);
		dialog.setFilterPath(lastUsedPath);
		dialog.setFileName(initialEntry.lastSegment());
		
		String res= dialog.open();
		if (res == null) {
			return null;
		}
		EDTUIPlugin.getDefault().getDialogSettings().put(IEGLUIConstants.DIALOGSTORE_LASTEXTEGLAR, dialog.getFilterPath());

		return Path.fromOSString(res).makeAbsolute();	
	}
	
	/**
	 * Shows the UI to select new external JAR or ZIP archive entries.
	 * The dialog returns the selected entry paths or <code>null</code> if the dialog has
	 * been canceled. The dialog does not apply any changes.
	 * 
	 * @param shell The parent shell for the dialog.
	 * @return Returns the new external JAR paths or <code>null</code> if the dialog has
	 * been canceled by the user.
	 */
	public static IPath[] chooseExternalJAREntries(Shell shell) {
		String lastUsedPath= EDTUIPlugin.getDefault().getDialogSettings().get(IEGLUIConstants.DIALOGSTORE_LASTEXTEGLAR);
		if (lastUsedPath == null) {
			lastUsedPath= ""; //$NON-NLS-1$
		}
		FileDialog dialog= new FileDialog(shell, SWT.MULTI);
		dialog.setText(NewWizardMessages.BuildPathDialogAccess_ExtEGLARArchiveDialog_new_title); 
		dialog.setFilterExtensions(ArchiveFileFilter.ALL_ARCHIVES_FILTER_EXTENSIONS);
		dialog.setFilterPath(lastUsedPath);
		
		String res= dialog.open();
		if (res == null) {
			return null;
		}
		String[] fileNames= dialog.getFileNames();
		int nChosen= fileNames.length;
			
		IPath filterPath= Path.fromOSString(dialog.getFilterPath());
		IPath[] elems= new IPath[nChosen];
		for (int i= 0; i < nChosen; i++) {
			elems[i]= filterPath.append(fileNames[i]).makeAbsolute();	
		}
		EDTUIPlugin.getDefault().getDialogSettings().put(IEGLUIConstants.DIALOGSTORE_LASTEXTEGLAR, dialog.getFilterPath());
		
		return elems;
	}
					
}
