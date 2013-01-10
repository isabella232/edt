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
package org.eclipse.edt.ide.ui.internal.dialogs;

import java.util.ArrayList;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

import org.eclipse.edt.ide.ui.internal.packageexplorer.EGLElementContentProvider;
import org.eclipse.edt.ide.ui.internal.packageexplorer.EGLElementLabelProvider;
import org.eclipse.edt.ide.ui.internal.packageexplorer.EGLElementSorter;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.edt.ide.ui.internal.wizards.TypedElementSelectionValidator;
import org.eclipse.edt.ide.ui.internal.wizards.TypedViewerFilter;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLModel;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;

public class MoveDialog extends ElementTreeSelectionDialog {
	
	Class[] acceptedClasses;
	TypedElementSelectionValidator validator;
	ViewerFilter filter;
	IEGLElement finitElement;
	
	/**
	 * @param parent
	 * @param labelProvider
	 * @param contentProvider
	 */
	public MoveDialog(Shell parent, IEGLElement initElement) {
		super(parent, new EGLElementLabelProvider(EGLElementLabelProvider.SHOW_DEFAULT), new EGLElementContentProvider());
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IUIHelpConstants.EGL_MOVE_DIALOG);
		
		finitElement = initElement;
		initAcceptedClasses(initElement);
		initFiltersValidators();
		
		setValidator(validator);
		setSorter(new EGLElementSorter());
		setTitle(NewWizardMessages.EGLMoveDialogTitle);
		setMessage(NewWizardMessages.EGLMoveDialogMessage + "  "); //$NON-NLS-1$
		addFilter(filter);	
		setInput(EGLCore.create(ResourcesPlugin.getWorkspace().getRoot()));
		setInitialSelection(initElement.getParent());	
	}
	
	protected TreeViewer createTreeViewer(Composite parent) {
		TreeViewer viewer = super.createTreeViewer(parent);

		//expand the tree to show the initial selection(the parent of the initElement)
		ArrayList elemlist = new ArrayList();
		IEGLElement elem = finitElement.getParent();
		while(!(elem instanceof IEGLModel))
		{
			elemlist.add(0, elem);
			elem = elem.getParent();
		}
		viewer.setExpandedElements(elemlist.toArray());	
		
		return viewer;
	}

	private void initAcceptedClasses(IEGLElement element){
		if(element instanceof IPackageFragmentRoot){
			acceptedClasses = new Class[] { IEGLProject.class };
		}
		else if(element instanceof IPackageFragment){
			acceptedClasses = new Class[] { IEGLProject.class, IPackageFragmentRoot.class };
		}
		else if(element instanceof IEGLFile){
			acceptedClasses = new Class[] { IEGLProject.class, IPackageFragmentRoot.class, IPackageFragment.class, IFolder.class };
		}
	}
	
	private void initFiltersValidators(){		
		validator= new TypedElementSelectionValidator(acceptedClasses, false) {		
			public IStatus validate(Object[] selection) {
				int nSelected= selection.length;
				String pluginId = EDTUIPlugin.getDefault().getDescriptor().getUniqueIdentifier();
				
				if (nSelected == 0) 
					return new Status(IStatus.ERROR, pluginId, IStatus.ERROR, UINlsStrings.MoveDialog_validateMsg_NoSelection, null);  //$NON-NLS-1$
				if(nSelected > 1)
					return new Status(IStatus.ERROR, pluginId, IStatus.ERROR, UINlsStrings.MoveDialog_validateMsg_MultiDestination, null);
				
				Object element = selection[0];
				try
				{
					if(element instanceof IEGLElement)
					{
						//should not try to move the element to the same container
						IEGLElement selectedElem = (IEGLElement)element;
						if(selectedElem.equals(finitElement.getParent()))
							return new Status(IStatus.ERROR, pluginId, IStatus.ERROR, UINlsStrings.MoveDialog_validateMsg_SameParent, null);						
					}
					//EGLProject no longer is the src folder by default
					if((element instanceof IEGLProject) && ((finitElement instanceof IEGLFile) || (finitElement instanceof IPackageFragment)))
					{
						IEGLProject eproject= (IEGLProject)element;
						IPath path= eproject.getProject().getFullPath();
						if(eproject.findPackageFragmentRoot(path) == null)
							return new Status(IStatus.ERROR, pluginId, IStatus.ERROR, "", null);						 //$NON-NLS-1$
					}
					if(element instanceof IPackageFragmentRoot)
					{
						if(((IPackageFragmentRoot)element).getKind() != IPackageFragmentRoot.K_SOURCE)
							return new Status(IStatus.ERROR, pluginId, IStatus.ERROR, "", null); //$NON-NLS-1$
					}
					return super.validate(selection);
				}
				catch(EGLModelException e)
				{
					EGLLogger.log(this, e);
				}
				return new Status(IStatus.ERROR, pluginId, IStatus.ERROR, "", null); //$NON-NLS-1$
			}
		};
		
		filter= new TypedViewerFilter(acceptedClasses, null) {
			public boolean select(Viewer viewer, Object parent, Object element) {
/*				if (element instanceof IPackageFragmentRoot) {
					try {
						return (((IPackageFragmentRoot)element).getKind() == IPackageFragmentRoot.K_SOURCE);
					} catch (EGLModelException e) {
						EGLLogger.log(this, e);
						return false;
					}
				}
*/				return super.select(viewer, parent, element);
			}
		};		
	}
}
