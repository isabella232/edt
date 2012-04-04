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
package org.eclipse.edt.ide.ui.internal.deployment.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.deployment.provider.DeploymentItemProviderAdapterFactory;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class EGLDeploymentDescriptorEditor extends FormEditor implements IEditingDomainProvider, IResourceChangeListener{

	public static final String PAGEID_OVERVIEW = "page.eglbind.overview"; //$NON-NLS-1$
	public static final String PAGEID_BINDINGS = "page.eglbind.bindings"; //$NON-NLS-1$
	public static final String PAGEID_WEBSERVICES = "page.eglbind.webservices"; //$NON-NLS-1$
	public static final String PAGEID_PROTOCOLS = "page.eglbind.protocols"; //$NON-NLS-1$
	public static final String PAGEID_TARGETS = "page.eglbind.targets"; //$NON-NLS-1$
	public static final String PAGEID_IMPORTS = "page.eglbind.imports"; //$NON-NLS-1$
	public static final String PAGEID_RESOURCES = "page.eglbind.resources"; //$NON-NLS-1$
	private static final String EXTENSIONPOINT_DD_EDITOR_PAGE_CONTRIBUTIONS = EDTUIPlugin.PLUGIN_ID + ".eglDDEditorPageContributions"; //$NON-NLS-1$
	private static final String EXTENSIONPOINT_DD_EDITOR_TOOLBAR_CONTRIBUTIONS = EDTUIPlugin.PLUGIN_ID + ".eglDDEditorToolbarContributions"; //$NON-NLS-1$
	private static IEGLDDContributionPageProvider[] editorProviders;
	private static IEGLDDContributionToolbarProvider[] toolbarProviders;
	
	private AdapterFactoryEditingDomain editingDomain;
	private EGLDeploymentRoot fServiceBindingRoot;
	private boolean fModelChanged;
	private EGLServiceBindResourceObserver fServiceBindResourceObserver;
	public static final Point SMALL_SIZE = new Point(16, 16);
	private EGLDDOverviewFormPage overviewFormPage;	

	protected class EGLServiceBindResourceObserver implements Adapter{
		public void notifyChanged(Notification notification) {
			// set dirty
			fModelChanged = true;			
			Display display = getSite().getShell().getDisplay();			
			display.asyncExec
			 (new Runnable() {
				  public void run() {
					  firePropertyChange(IEditorPart.PROP_DIRTY);
				  }			
			 });
		}

		public Notifier getTarget() {
			return null;
		}

		public void setTarget(Notifier newTarget) {
			
		}

		public boolean isAdapterForType(Object type) {
			return false;
		}
		
	}
	public EGLDeploymentDescriptorEditor(){
		super();
		initEditingDomain();
	}
		
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		setPartName(input.getName());
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}
	
	private void initEditingDomain() {
		// Create an adapter factory that yields item providers.
		//
		List factories = new ArrayList();
		factories.add(new ResourceItemProviderAdapterFactory());
		factories.add(new DeploymentItemProviderAdapterFactory());
		factories.add(new ReflectiveItemProviderAdapterFactory());

		ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory(factories);

		// Create the command stack that will notify this editor as commands are executed.
		//
		BasicCommandStack commandStack = new BasicCommandStack();

		// Add a listener to set the most recent command's affected objects to be the selection of the viewer with focus.
		//
		commandStack.addCommandStackListener
			(new CommandStackListener() {
				 public void commandStackChanged(final EventObject event) {
					 getContainer().getDisplay().asyncExec
						 (new Runnable() {
							  public void run() {
								  editorDirtyStateChanged();
							  }
						  });
				 }
			 });

		// Create the editing domain with a special command stack.
		//
		editingDomain = new AdapterFactoryEditingDomain(adapterFactory, commandStack, new HashMap());				
	}
	
	protected void addPages() {
		try {
			overviewFormPage = new EGLDDOverviewFormPage(this, PAGEID_OVERVIEW, SOAMessages.OverviewPageLabel);
			addPage(overviewFormPage);			
			addPageContributions();
			addPage(new EGLDDWebsServicesFormPage(this, PAGEID_WEBSERVICES, SOAMessages.WebServicePageLabel));
			addPage(new EGLDDBindingFormPage(this, PAGEID_BINDINGS, SOAMessages.BindingPageLabel));
//			addPage(new EGLDDProtocolFormPage(this, PAGEID_PROTOCOLS, SOAMessages.ProtocolsTitle));
			addPage(new EGLDDImportsFormPage(this, PAGEID_IMPORTS, SOAMessages.ImportPageLabel));		
			addPage(new EGLDDResourcesFormPage(this, PAGEID_RESOURCES, SOAMessages.ArtifactsTitle));		
		} catch (PartInitException e) {
			EDTUIPlugin.log( e );
		}
	}
	
	private void addPageContributions() {
		IEGLDDContributionPageProvider[] providers = getEditorProviders();
		for (int i = 0; i < providers.length; i++) {
			try {
				addPage(providers[i].createPage(this));
			} catch (PartInitException pie) {
				EDTUIPlugin.log( pie );
			}
		}
	}
	
	public static IEGLDDContributionPageProvider[] getEditorProviders() {
		if (editorProviders == null) {
			List providers = new ArrayList();
			IConfigurationElement[] editorContributions = Platform.getExtensionRegistry().getConfigurationElementsFor( EXTENSIONPOINT_DD_EDITOR_PAGE_CONTRIBUTIONS );
			if (editorContributions != null) {
				for (int i = 0; i < editorContributions.length; i++) {
					try {
						Object o = editorContributions[i].createExecutableExtension(EDTUIPlugin.CLASS);
						if (o instanceof IEGLDDContributionPageProvider) {
							providers.add(o);
						}
					} catch (CoreException ce) {
						EDTUIPlugin.log( ce );
					}
				}
			}
			editorProviders = (IEGLDDContributionPageProvider[])providers.toArray(new IEGLDDContributionPageProvider[providers.size()]);
		}
		return editorProviders;
	}
	
	public static IEGLDDContributionToolbarProvider[] getToolbarProviders() {
		if (toolbarProviders == null) {
			List providers = new ArrayList();
			IConfigurationElement[] editorContributions = Platform.getExtensionRegistry().getConfigurationElementsFor( EXTENSIONPOINT_DD_EDITOR_TOOLBAR_CONTRIBUTIONS );
			if (editorContributions != null) {
				for (int i = 0; i < editorContributions.length; i++) {
					try {
						Object o = editorContributions[i].createExecutableExtension(EDTUIPlugin.CLASS);
						if (o instanceof IEGLDDContributionToolbarProvider) {
							providers.add(o);
						}
					} catch (CoreException ce) {
						EDTUIPlugin.log( ce );
					}
				}
			}
			toolbarProviders = (IEGLDDContributionToolbarProvider[])providers.toArray(new IEGLDDContributionToolbarProvider[providers.size()]);
		}
		return toolbarProviders;
	}

	public void doSave(IProgressMonitor monitor) {
		try {
			Resource savedResource = getModelRoot().eResource();
			savedResource.save(Collections.EMPTY_MAP);			

			// Refresh the necessary state.
			//
			((BasicCommandStack)editingDomain.getCommandStack()).saveIsDone();
			/*
			 * Now that the model is saved set the modelChanged idicator to false and
			 * notify the editor that the dirty state has changed to update the dirty indicator.
			 */
			fModelChanged = false;
			editorDirtyStateChanged();			
		}
		catch (Exception exception) {
			// Something went wrong that shouldn't.
			//
			EDTUIPlugin.log( exception );
		}
	}

	public boolean isDirty() {
		return fModelChanged || super.isDirty();
	}

	public void doSaveAs() {
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public EditingDomain getEditingDomain() {
		return editingDomain;
	}
	
	public EGLDeploymentRoot getModelRoot()
	{
		if(fServiceBindingRoot == null){
			ResourceSet resourceSet = editingDomain.getResourceSet();
			IEditorInput editorInput = getEditorInput();
			if(editorInput instanceof IFileEditorInput)
			{
				IFileEditorInput fileInput = (IFileEditorInput)editorInput;
				IFile file = fileInput.getFile();
				fServiceBindingRoot = EGLDDRootHelper.getEGLDDFileSharedWorkingModel(file, resourceSet, true);
				fServiceBindResourceObserver = new EGLServiceBindResourceObserver();
				fServiceBindingRoot.eResource().eAdapters().add(fServiceBindResourceObserver);
				//fServiceBindingRoot.getDeployment().eAdapters().add(fServiceBindResourceObserver);
			}
		}
		return fServiceBindingRoot;
	}
		
	public void dispose() {
		IFile file = getEditorInputFile();
		if(file != null){
			fServiceBindingRoot.eResource().eAdapters().remove(fServiceBindResourceObserver);
			EGLDDRootHelper.releaseSharedWorkingModel(file, true);
		}

		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		
		super.dispose();
	}
	
	protected FormToolkit createToolkit(Display display) {
		return new FormToolkit(EDTUIPlugin.getDefault().getFormColors(display));		
	}

	protected IProject getProject()
	{
		IFile file = getEditorInputFile();
		if(file != null)
			return file.getProject();
		
		return null;
	}
	
	protected IFile getEditorInputFile()
	{
		IEditorInput editorInput = getEditorInput();
		if(editorInput instanceof IFileEditorInput)
		{
			IFileEditorInput fileInput = (IFileEditorInput)editorInput;
			IFile file = fileInput.getFile();
			return file;
		}
		return null;
	}
	

	public void resourceChanged(IResourceChangeEvent event) {		
	    IResource eventRes = event.getResource();
		switch (event.getType()) {
		case IResourceChangeEvent.PRE_CLOSE:
			if (((IProject) eventRes).findMember(getEditorInputFile().getProjectRelativePath()) != null)
				close(false);
			break;
		case IResourceChangeEvent.PRE_DELETE:
			if (((IProject) eventRes).findMember(getEditorInputFile().getProjectRelativePath()) != null)
				close(false);
			break;
		case IResourceChangeEvent.PRE_BUILD:
			break;
		case IResourceChangeEvent.POST_BUILD:
			break;
		case IResourceChangeEvent.POST_CHANGE:
			try {
				event.getDelta().accept(new IResourceDeltaVisitor() {
					public boolean visit(IResourceDelta delta)
							throws CoreException {
						IResource res = delta.getResource();
						switch (delta.getKind()) {
						case IResourceDelta.ADDED:
							if(res.getType() == IResource.PROJECT){
								updateTargetList((IProject)res);
							}
							break;
						case IResourceDelta.CHANGED:
							break;
						case IResourceDelta.REMOVED:
							if (res.equals(getEditorInputFile())){
								close(false);
							}else if(res.getType() == IResource.PROJECT){
								updateTargetList((IProject)res);
							}
							break;
						}
						return true;
					}
				});
			} catch (CoreException e) {
				EDTUIPlugin.log( e );
			}
			break;
		}  
	}
	
	private void updateTargetList(IProject project){
		if(overviewFormPage != null){
			Display display = getSite().getShell().getDisplay();
			if (display != null) {
				display.asyncExec(new Runnable() {
					public void run() {
						overviewFormPage.initTargets();
					}
				});
			}
		}
	}
	
	public void setFocus() {
		// Override super - that will force focus on the toolbar, which we don't want.
		// Let each page decide its own focus.
		IFormPage page = getActivePageInstance();
		if ( page != null ) {
			page.setFocus();
		}
	}
}
