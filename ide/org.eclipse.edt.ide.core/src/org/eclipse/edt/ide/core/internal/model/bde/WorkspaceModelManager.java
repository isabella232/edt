/*******************************************************************************
 * Copyright © 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.model.bde;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.model.bde.IModel;
import org.eclipse.edt.ide.core.model.bde.IModelProviderEvent;
import org.eclipse.edt.ide.core.model.bde.IPluginModelBase;

import org.eclipse.edt.ide.core.model.EGLCore;

public abstract class WorkspaceModelManager extends AbstractModelManager implements IResourceChangeListener, IResourceDeltaVisitor {

	public static boolean isEGLProject(IProject project) {
//		try {
			return true;
//			return project.hasNature(EGLCore.NATURE_ID);
//		} catch (CoreException e) {
//		}
//		return false;
	}

	

	class ModelChange {
		IModel model;
		int type;

		public ModelChange(IModel model, int type) {
			this.model = model;
			this.type = type;
		}

		public boolean equals(Object obj) {
			if (obj instanceof ModelChange) {
				ModelChange change = (ModelChange) obj;
				IProject project = change.model.getUnderlyingResource().getProject();
				int type = change.type;
				return model.getUnderlyingResource().getProject().equals(project) && this.type == type;
			}
			return false;
		}
	}

	protected Map<IProject,IPluginModelBase> fModels = null;
	private ArrayList<ModelChange> fChangedModels;

	protected synchronized void initialize() {
		if (fModels != null)
			return;
		fModels = Collections.synchronizedMap(new HashMap());
		IProject[] projects = EGLCore.getWorkspace().getRoot().getProjects();
		for (int i = 0; i < projects.length; i++) {
			if (isInterestingProject(projects[i]))
				createModel(projects[i], false);
		}
		addListeners();
	}

	protected abstract boolean isInterestingProject(IProject project);

	protected abstract void createModel(IProject project, boolean notify);

	protected abstract void addListeners();

	protected Object getModel(IProject project) {
		initialize();
		return fModels.get(project);
	}

	protected Object[] getModels() {
		initialize();
		return fModels.values().toArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		switch (event.getType()) {
			case IResourceChangeEvent.PRE_BUILD :
				handleResourceDelta(event.getDelta());
				processModelChanges();
				break;
			case IResourceChangeEvent.PRE_CLOSE :
				removeModel((IProject) event.getResource());
				processModelChanges();
				break;
		}
	}

	private void handleResourceDelta(IResourceDelta delta) {
		try {
			delta.accept(this);
		} catch (CoreException e) {
			EDTCoreIDEPlugin.logException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
	 */
	public boolean visit(IResourceDelta delta) throws CoreException {
		if (delta != null) {

			final IResource resource = delta.getResource();
			if (!resource.isDerived()) {
				switch (resource.getType()) {

					case IResource.ROOT :
						return true;
					case IResource.PROJECT : {
						IProject project = (IProject) resource;
						if (isInterestingProject(project) && (delta.getKind() == IResourceDelta.ADDED || (delta.getFlags() & IResourceDelta.OPEN) != 0)) {
							createModel(project, true);
							return false;
						} else if (delta.getKind() == IResourceDelta.REMOVED) {
							removeModel(project);
							return false;
						}
						return true;
					}
					case IResource.FOLDER :
						return isInterestingFolder((IFolder) resource);
					case IResource.FILE :
						// do not process 
						if (isContentChange(delta)) {
							handleFileDelta(delta);
							return false;
						}
				}
			}
		}
		return false;
	}

	private boolean isContentChange(IResourceDelta delta) {
		int kind = delta.getKind();
		return (kind == IResourceDelta.ADDED || kind == IResourceDelta.REMOVED || (kind == IResourceDelta.CHANGED && (delta.getFlags() & IResourceDelta.CONTENT) != 0));
	}

	protected boolean isInterestingFolder(IFolder folder) {
		return false;
	}

	protected abstract void handleFileDelta(IResourceDelta delta);

	protected Object removeModel(IProject project) {
		Object model = fModels != null ? fModels.remove(project) : null;
		addChange(model, IModelProviderEvent.MODELS_REMOVED);
		return model;
	}

	protected void addChange(Object model, int eventType) {
		if (model instanceof IModel) {
			if (fChangedModels == null)
				fChangedModels = new ArrayList<ModelChange>();
			ModelChange change = new ModelChange((IModel) model, eventType);
			if (!fChangedModels.contains(change))
				fChangedModels.add(change);
		}
	}

	protected void processModelChanges() {
		processModelChanges("org.eclipse.pde.core.IModelProviderEvent", fChangedModels); //$NON-NLS-1$
		fChangedModels = null;
	}

	protected void processModelChanges(String changeId, ArrayList<ModelChange> changedModels) {
		if (changedModels == null)
			return;

		if (changedModels.size() == 0) {
			return;
		}

		ArrayList<IModel> added = new ArrayList<IModel>();
		ArrayList<IModel> removed = new ArrayList<IModel>();
		ArrayList<IModel> changed = new ArrayList<IModel>();
		for (ListIterator<ModelChange> li = changedModels.listIterator(); li.hasNext();) {
			ModelChange change = (ModelChange) li.next();
			switch (change.type) {
				case IModelProviderEvent.MODELS_ADDED :
					added.add(change.model);
					break;
				case IModelProviderEvent.MODELS_REMOVED :
					removed.add(change.model);
					break;
				case IModelProviderEvent.MODELS_CHANGED :
					changed.add(change.model);
			}
		}

		int type = 0;
		if (added.size() > 0)
			type |= IModelProviderEvent.MODELS_ADDED;
		if (removed.size() > 0)
			type |= IModelProviderEvent.MODELS_REMOVED;
		if (changed.size() > 0)
			type |= IModelProviderEvent.MODELS_CHANGED;

		if (type != 0) {
			createAndFireEvent(changeId, type, added, removed, changed);
		}
	}

	protected void loadModel(IModel model, boolean reload) {
		try {
				model.load(null, false);
		} catch (CoreException e) {
			EDTCoreIDEPlugin.logException(e);
		} 
	}

	protected void createAndFireEvent(String eventId, int type, Collection<IModel> added, Collection<IModel> removed, Collection<IModel> changed) {
		if (eventId.equals("org.eclipse.pde.core.IModelProviderEvent")) { //$NON-NLS-1$
			final ModelProviderEvent event = new ModelProviderEvent(this, type, (IModel[]) added.toArray(new IModel[added.size()]), (IModel[]) removed.toArray(new IModel[removed.size()]), (IModel[]) changed.toArray(new IModel[changed.size()]));
			fireModelProviderEvent(event);
		}
	}
}
