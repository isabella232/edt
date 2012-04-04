/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.eglarpackager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLModel;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.StandardEGLElementContentProvider;
import org.eclipse.edt.ide.ui.internal.packageexplorer.EGLElementLabelProvider;
import org.eclipse.edt.ide.ui.internal.util.SWTUtil;
import org.eclipse.jdt.ui.ProblemsLabelDecorator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;


/**
 *	Page 1 of the EGLAR Package wizard
 */
public class EglarPackageWizardPage extends AbstractEglarDestinationWizardPage {
	protected EglarPackageData fEglarPackage;
	private IStructuredSelection fInitialSelection;
	private CheckboxTreeAndListGroup fInputGroup;

	// widgets
	private Button	fExportEGLSrcFilesCheckbox;
	
	protected Button  fExportEGLWithErrorsCheckbox;
	protected Button  fExportEGLWithWarningCheckbox;

	// dialog store id constants
	private static final String PAGE_NAME= "EglarPackageWizardPage"; //$NON-NLS-1$

	private static final String STORE_EXPORT_IR_FILES= PAGE_NAME + ".EXPORT_IR_FILES"; //$NON-NLS-1$
	private static final String STORE_COMPRESS= PAGE_NAME + ".COMPRESS"; //$NON-NLS-1$

	// other constants
	private static final int SIZING_SELECTION_WIDGET_WIDTH = 480;
	private static final int SIZING_SELECTION_WIDGET_HEIGHT = 150;
	
    private final static String SELECT_ALL_TITLE = EglarPackagerMessages.EglarPackageWizardPage_selectAll;

    private final static String DESELECT_ALL_TITLE = EglarPackagerMessages.EglarPackageWizardPage_deselectAll;
    
	public EglarPackageWizardPage(EglarPackageData eglarPackage, IStructuredSelection selection) {
		this(PAGE_NAME, eglarPackage, selection);
	}
	
	public EglarPackageWizardPage(String pageName, EglarPackageData eglarPackage, IStructuredSelection selection) {
		super(pageName, selection, eglarPackage);
		setTitle(EglarPackagerMessages.EglarPackageWizardPage_title);
		setDescription(EglarPackagerMessages.EglarPackageWizardPage_description);
		fEglarPackage= eglarPackage;
		fInitialSelection= selection;
	}
	
	public Button getExportEGLSrcFilesCheckbox() {
		return fExportEGLSrcFilesCheckbox;
	}
	
	/**
	 * Method declared on IDialogPage.
	 */
	@Override
	public void createControl(final Composite parent) {

		initializeDialogUnits(parent);

		Composite composite= new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(
			new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));

		createPlainLabel(composite, EglarPackagerMessages.EglarPackageWizardPage_whatToExport_label);
		createInputGroup(composite);
		createSelectionButtonsGroup(composite);
		createExportTypeGroup(composite);

		new Label(composite, SWT.NONE); // vertical spacer


		createPlainLabel(composite, EglarPackagerMessages.EglarPackageWizardPage_whereToExport_label);
		createDestinationGroup(composite);

		createOptionsGroup(composite);

		restoreResourceSpecificationWidgetValues(); // superclass API defines this hook
		restoreWidgetValues();
		if (fInitialSelection != null)
			BusyIndicator.showWhile(parent.getDisplay(), new Runnable() {
				public void run() {
					setupBasedOnInitialSelections();
				}
			});

		setControl(composite);
		update();
		giveFocusToDestination();

		Dialog.applyDialogFont(composite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IUIHelpConstants.EGLAR_EXPORT_WIZARD);
	}
	
	   /**
     * Creates the buttons for selecting specific types or selecting all or none of the
     * elements.
     *
     * @param parent the parent control
     */
    protected final void createSelectionButtonsGroup(Composite parent) {

        Font font = parent.getFont();

        // top level group
        Composite buttonComposite = new Composite(parent, SWT.NONE);
        buttonComposite.setFont(parent.getFont());

        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        layout.makeColumnsEqualWidth = true;
        buttonComposite.setLayout(layout);
        buttonComposite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL));

        Button selectButton = createButton(buttonComposite,
                IDialogConstants.SELECT_ALL_ID, SELECT_ALL_TITLE, false);

        SelectionListener listener = new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            	fInputGroup.setAllSelections(true);
                updateWidgetEnablements();
            }
        };
        selectButton.addSelectionListener(listener);
        selectButton.setFont(font);
        setButtonLayoutData(selectButton);

        Button deselectButton = createButton(buttonComposite,
                IDialogConstants.DESELECT_ALL_ID, DESELECT_ALL_TITLE, false);

        listener = new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            	fInputGroup.setAllSelections(false);
                updateWidgetEnablements();
            }
        };
        deselectButton.addSelectionListener(listener);
        deselectButton.setFont(font);
        setButtonLayoutData(deselectButton);

    }

	/**
	 *	Create the export options specification widgets.
	 *
	 *	@param parent org.eclipse.swt.widgets.Composite
	 */
	protected void createOptionsGroup(Composite parent) {

	}

	/**
	 * Returns an iterator over this page's collection of currently-specified
	 * elements to be exported. This is the primary element selection facility
	 * accessor for subclasses.
	 *
	 * @return an iterator over the collection of elements currently selected for export
	 */
	protected Iterator getSelectedResourcesIterator() {
		return fInputGroup.getAllCheckedListItems();
	}

	/**
	 * Persists resource specification control setting that are to be restored
	 * in the next instance of this page. Subclasses wishing to persist
	 * settings for their controls should extend the hook method
	 * <code>internalSaveWidgetValues</code>.
	 */
	public final void saveWidgetValues() {
		super.saveWidgetValues();
		// update directory names history
		IDialogSettings settings= getDialogSettings();
		if (settings != null) {
			settings.put(STORE_EXPORT_IR_FILES, fEglarPackage.areEGLIRFilesExported());

			// options
			settings.put(STORE_COMPRESS, fEglarPackage.isCompressed());
		}
		// Allow subclasses to save values
		internalSaveWidgetValues();
	}

	/**
	 * Hook method for subclasses to persist their settings.
	 */
	protected void internalSaveWidgetValues() {
	}

	/**
	 *	Hook method for restoring widget values to the values that they held
	 *	last time this wizard was used to completion.
	 */
	protected void restoreWidgetValues() {
		if (!((EglarPackageWizard)getWizard()).isInitializingFromEglarPackage())
			initializeEglarPackage();

		fExportEGLSrcFilesCheckbox.setSelection(fEglarPackage.areEGLSrcFilesExported());
		fExportEGLWithErrorsCheckbox.setSelection(fEglarPackage.areErrorsExported());
		fExportEGLWithWarningCheckbox.setSelection(fEglarPackage.exportWarnings());
		restoreLocation();

	}

	/**
	 *	Initializes the Eglar package from last used wizard page values.
	 */
	protected void initializeEglarPackage() {
		super.initializeEglarPackage();

		IDialogSettings settings= getDialogSettings();
		if (settings != null) {
			// source
			fEglarPackage.setElements(getSelectedElements());
			fEglarPackage.setExportEGLIRFiles(settings.getBoolean(STORE_EXPORT_IR_FILES));
			
			// options
			fEglarPackage.setCompress(settings.getBoolean(STORE_COMPRESS));
		}
	}

	/**
	 *	Stores the widget values in the Eglar package.
	 */
	protected void updateModel() {
		if (getControl() == null)
			return;

		// source
		fEglarPackage.setExportEGLSrcFiles(fExportEGLSrcFilesCheckbox.getSelection());
		fEglarPackage.setExportErrors(fExportEGLWithErrorsCheckbox.getSelection());
		fEglarPackage.setExportWarnings(fExportEGLWithWarningCheckbox.getSelection());
		fEglarPackage.setElements(getSelectedElements());

		super.updateModel();
		// options
	}

	/**
	 * Returns the resource for the specified path.
	 *
	 * @param path	the path for which the resource should be returned
	 * @return the resource specified by the path or <code>null</code>
	 */
	protected IResource findResource(IPath path) {
		IWorkspace workspace= ResourcesPlugin.getWorkspace();
		IStatus result= workspace.validatePath(
							path.toString(),
							IResource.ROOT | IResource.PROJECT | IResource.FOLDER | IResource.FILE);
		if (result.isOK() && workspace.getRoot().exists(path))
			return workspace.getRoot().findMember(path);
		return null;
	}

	/**
	 * Creates the checkbox tree and list for selecting resources.
	 *
	 * @param parent the parent control
	 */
	protected void createInputGroup(Composite parent) {
		int labelFlags= EGLElementLabelProvider.SHOW_BASICS
						| EGLElementLabelProvider.SHOW_OVERLAY_ICONS
						| EGLElementLabelProvider.SHOW_SMALL_ICONS;
		ITreeContentProvider treeContentProvider=
			new StandardEGLElementContentProvider() {
				public boolean hasChildren(Object element) {
					if(element instanceof IEGLProject) {
						return super.hasChildren(element);
					} else {
						return false;
					}
				}
				public Object[] getChildren(Object element) {
					if (element instanceof IEGLModel){
						IEGLProject[] projects = (IEGLProject[]) super.getChildren(element);
						ArrayList<IEGLProject> projectsList = new ArrayList<IEGLProject>();
						for(IEGLProject project : projects) {
							if(!project.isBinary()) {
								projectsList.add(project);
							}
						}
						projects = new IEGLProject[projectsList.size()];
						projectsList.toArray(projects);
						return projects;
					} else {
						return NO_CHILDREN;
					}
				}
			};
		final DecoratingLabelProvider provider= new DecoratingLabelProvider(new EGLElementLabelProvider(labelFlags), new ProblemsLabelDecorator(null));
		fInputGroup= new CheckboxTreeAndListGroup(
					parent,
					EGLCore.create(ResourcesPlugin.getWorkspace().getRoot()),
					treeContentProvider,
					provider,
					provider,
					SWT.NONE,
					SIZING_SELECTION_WIDGET_WIDTH,
					SIZING_SELECTION_WIDGET_HEIGHT);
		ICheckStateListener listener = new ICheckStateListener() {
            public void checkStateChanged(CheckStateChangedEvent event) {
                update();
            }
        };
        fInputGroup.addCheckStateListener(listener);
		SWTUtil.setAccessibilityText(fInputGroup.getTree(), EglarPackagerMessages.EglarPackageWizardPage_tree_accessibility_message);
	}

	/**
	 * Creates the export type controls.
	 *
	 * @param parent
	 *            the parent control
	 */
	protected void createExportTypeGroup(Composite parent) {
		Composite optionsGroup= new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.marginHeight= 0;
		optionsGroup.setLayout(layout);

		fExportEGLWithErrorsCheckbox = new Button(optionsGroup, SWT.CHECK | SWT.LEFT);
		fExportEGLWithErrorsCheckbox.setText(EglarPackagerMessages.EglarPackageWizardPage_exportEGLWithErrors_text);
		fExportEGLWithErrorsCheckbox.addListener(SWT.Selection, this);
		
		fExportEGLWithWarningCheckbox = new Button(optionsGroup, SWT.CHECK | SWT.LEFT);
		fExportEGLWithWarningCheckbox.setText(EglarPackagerMessages.EglarPackageWizardPage_exportEGLWithWarnings_text);
		fExportEGLWithWarningCheckbox.addListener(SWT.Selection, this);
		
		fExportEGLSrcFilesCheckbox= new Button(optionsGroup, SWT.CHECK | SWT.LEFT);
		fExportEGLSrcFilesCheckbox.setText(EglarPackagerMessages.EglarPackageWizardPage_exportEGLSourceFiles_text);
		fExportEGLSrcFilesCheckbox.addListener(SWT.Selection, this);
		//69755: Remove some of UI for EGLAR & binary project for tech-preview in 8011
		fExportEGLSrcFilesCheckbox.setVisible(false);
		fExportEGLSrcFilesCheckbox.setEnabled(false);
		fExportEGLSrcFilesCheckbox.setSelection(false);	//do not export source file for eglar
		//69755
		
	}


	/*
	 * Overrides method from IEglarPackageWizardPage
	 */
	public boolean isPageComplete() {
		boolean complete= validateSourceGroup();
		complete= validateDestinationGroup() && complete;
		complete= validateOptionsGroup() && complete;
		if (complete)
			setErrorMessage(null);
		return complete;
	}

	protected void updatePageCompletion() {
		boolean pageComplete= isPageComplete();
		setPageComplete(pageComplete);
		if (pageComplete)
			setErrorMessage(null);
		updateRefactoringMessage();
	}

	protected void updateRefactoringMessage() {
		String currentMessage= getMessage();
		if (fEglarPackage.isRefactoringAware() && fEglarPackage.getRefactoringDescriptors().length == 0) {
			if (currentMessage == null)
				setMessage(EglarPackagerMessages.EglarPackageWizardPage_no_refactorings_selected, IMessageProvider.INFORMATION);
		} else if (EglarPackagerMessages.EglarPackageWizardPage_no_refactorings_selected.equals(currentMessage))
			setMessage(null);
	}

	/*
	 * Overrides method from WizardDataTransferPage
	 */
	protected boolean validateOptionsGroup() {
		Object[] selProjects = getSelectedElements();
		boolean ret = true;
		for (Object obj : selProjects) {
			IEGLProject eglProject = (IEGLProject) obj;
			IProject project = eglProject.getProject();
			try {
				IMarker[] eglMarkers = project.findMarkers(IMarker.MARKER, true, IResource.DEPTH_INFINITE);
				if (eglMarkers.length > 0) {
					for (IMarker marker : eglMarkers) {
						switch (marker.getAttribute(IMarker.SEVERITY, 0)) {
						case IMarker.SEVERITY_ERROR:
							ret = ret && fEglarPackage.areErrorsExported();
							break;
						case IMarker.SEVERITY_WARNING:
							ret = ret && fEglarPackage.exportWarnings();
							break;
						}
					}
				}
			} catch (CoreException e) {
			}
		}
		return ret;
	}

	/*
	 * Overrides method from WizardDataTransferPage
	 */
	protected boolean validateSourceGroup() {
		
		if (getSelectedResources().size() == 0) {
			if (getErrorMessage() != null)
				setErrorMessage(null);
			return false;
		}
		
		if (getErrorMessage() != null) {
			setErrorMessage(null);
			return false;
		}
		return true;
	}

	/**
	 * Creates a file resource handle for the file with the given workspace path.
	 * This method does not create the file resource; this is the responsibility
	 * of <code>createFile</code>.
	 *
	 * @param filePath the path of the file resource to create a handle for
	 * @return the new file resource handle
	 */
	protected IFile createFileHandle(IPath filePath) {
		if (filePath.isValidPath(filePath.toString()) && filePath.segmentCount() >= 2)
			return ResourcesPlugin.getWorkspace().getRoot().getFile(filePath);
		else
			return null;
	}

	/*
	 * Overrides method from WizardExportResourcePage
	 */
	protected void setupBasedOnInitialSelections() {
		Iterator iterator= fInitialSelection.iterator();
		while (iterator.hasNext()) {
			Object selectedElement= iterator.next();

				try {
					fInputGroup.initialCheckTreeItem(selectedElement);
				} finally {
				}
		}

	}

	/*
	 * Method declared on IWizardPage.
	 */
	public void setPreviousPage(IWizardPage page) {
		super.setPreviousPage(page);
		if (getControl() != null)
			updatePageCompletion();
	}

	Object[] getSelectedElementsWithoutContainedChildren() {
		Set closure= removeContainedChildren(fInputGroup.getWhiteCheckedTreeItems());
		closure.addAll(getExportedNonContainers());
		return closure.toArray();
	}

	private Set removeContainedChildren(Set elements) {
		Set newList= new HashSet(elements.size());
		Set javaElementResources= getCorrespondingContainers(elements);
		Iterator iter= elements.iterator();
		boolean removedOne= false;
		while (iter.hasNext()) {
			Object element= iter.next();
			Object parent;
			if (element instanceof IResource)
				parent= ((IResource)element).getParent();
			else if (element instanceof IEGLElement) {
				parent= ((IEGLElement)element).getParent();
				if (parent instanceof IPackageFragmentRoot) {
					IPackageFragmentRoot pkgRoot= (IPackageFragmentRoot)parent;
					try {
						if (pkgRoot.getCorrespondingResource() instanceof IProject)
							parent= pkgRoot.getEGLProject();
					} catch (EGLModelException ex) {
						// leave parent as is
					}
				}
			}
			else {
				// unknown type
				newList.add(element);
				continue;
			}
			if (element instanceof IEGLModel || ((!(parent instanceof IEGLModel)) && (elements.contains(parent) || javaElementResources.contains(parent))))
				removedOne= true;
			else
				newList.add(element);
		}
		if (removedOne)
			return removeContainedChildren(newList);
		else
			return newList;
	}

	private Set getExportedNonContainers() {
		Set whiteCheckedTreeItems= fInputGroup.getWhiteCheckedTreeItems();
		Set exportedNonContainers= new HashSet(whiteCheckedTreeItems.size());
		Set javaElementResources= getCorrespondingContainers(whiteCheckedTreeItems);
		Iterator iter= fInputGroup.getAllCheckedListItems();
		while (iter.hasNext()) {
			Object element= iter.next();
			Object parent= null;
			if (element instanceof IResource)
				parent= ((IResource)element).getParent();
			else if (element instanceof IEGLElement)
				parent= ((IEGLElement)element).getParent();
			if (!whiteCheckedTreeItems.contains(parent) && !javaElementResources.contains(parent))
				exportedNonContainers.add(element);
		}
		return exportedNonContainers;
	}

	/*
	 * Create a list with the folders / projects that correspond
	 * to the egl elements (egl project, package, package root)
	 */
	private Set getCorrespondingContainers(Set elements) {
		Set EGLElementResources= new HashSet(elements.size());
		Iterator iter= elements.iterator();
		while (iter.hasNext()) {
			Object element= iter.next();
			if (element instanceof IEGLElement) {
				IEGLElement je= (IEGLElement)element;
				int type= je.getElementType();
				if (type == IEGLElement.EGL_PROJECT || type == IEGLElement.PACKAGE_FRAGMENT || type == IEGLElement.PACKAGE_FRAGMENT_ROOT) {
					// exclude default package since it is covered by the root
					if (!(type == IEGLElement.PACKAGE_FRAGMENT && ((IPackageFragment)element).isDefaultPackage())) {
						Object resource;
						try {
							resource= je.getCorrespondingResource();
						} catch (EGLModelException ex) {
							resource= null;
						}
						if (resource != null)
							EGLElementResources.add(resource);
					}
				}
			}
		}
		return EGLElementResources;
	}

	protected Object[] getSelectedElements() {
		return getSelectedResources().toArray();
	}
	
	protected String getBrowseDialogTitle() {
		return EglarPackagerMessages.EglarPackageWizardPage_SelectDialogTitle;
	}
}
