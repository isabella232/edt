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


import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.model.bde.IModelProviderEvent;
import org.eclipse.edt.ide.core.model.bde.IPluginModelBase;
import org.eclipse.edt.ide.core.model.bde.WorkspacePluginModel;

import org.eclipse.edt.ide.core.model.EGLCore;

public class WorkspacePluginModelManager extends WorkspaceModelManager {

	/**
	 * The workspace plug-in model manager is only interested
	 * in changes to plug-in projects.
	 */
	protected boolean isInterestingProject(IProject project) {
		return isEGLProject(project);
	}

	/**
	 * Creates a plug-in model based on the project structure.
	 * <p>
	 * A bundle model is created if the project has a MANIFEST.MF file and optionally 
	 * a plugin.xml/fragment.xml file.
	 * </p>
	 * <p>
	 * An old-style plugin model is created if the project only has a plugin.xml/fragment.xml
	 * file.
	 * </p>
	 */
	protected void createModel(IProject project, boolean notify) {
		IPluginModelBase model = null;
		model = new WorkspacePluginModel(project);
		loadModel(model, false);
		if (model != null) {
			if (fModels == null)
				fModels = new HashMap<IProject,IPluginModelBase>();
			fModels.put(project, model);
			if (notify)
				addChange(model, IModelProviderEvent.MODELS_ADDED);
		}
	}

	/**
	 * Reacts to changes in files of interest to PDE
	 */
	protected void handleFileDelta(IResourceDelta delta) {
		IFile file = (IFile) delta.getResource();
		IProject project = file.getProject();
		if (file.equals(BDEProject.getEGLPath((project)))) {
			handleExtensionFileDelta(file, delta);
		}
	}


	/**
	 * Reacts to changes in the plugin.xml or fragment.xml file.
	 * <ul>
	 * <li>If the file has been deleted and the project has a MANIFEST.MF file,
	 * then this deletion only affects extensions and extension points.</li>
	 * <li>If the file has been deleted and the project does not have a MANIFEST.MF file,
	 * then it's an old-style plug-in and the entire model must be removed from the table.</li>
	 * <li>If the file has been added and the project already has a MANIFEST.MF, then
	 * this file only contributes extensions and extensions.  No need to send a notification
	 * to trigger update classpath of dependent plug-ins</li>
	 * <li>If the file has been added and the project does not have a MANIFEST.MF, then
	 * an old-style plug-in has been created.</li>
	 * <li>If the file has been modified and the project already has a MANIFEST.MF,
	 * then reload the extensions model but do not send out notifications</li>
	 * </li>If the file has been modified and the project has no MANIFEST.MF, then
	 * it's an old-style plug-in, reload and send out notifications to trigger a classpath update
	 * for dependent plug-ins</li>
	 * </ul>
	 * @param file the manifest file
	 * @param delta the resource delta
	 */
	private void handleExtensionFileDelta(IFile file, IResourceDelta delta) {
		int kind = delta.getKind();
		IPluginModelBase model = (IPluginModelBase) getModel(file.getProject());
//		System.out.println(model.getClass().getName());
		if (kind == IResourceDelta.REMOVED) {
//			if (model instanceof IBundlePluginModelBase) {
//				((IBundlePluginModelBase) model).setExtensionsModel(null);
//				addExtensionChange(model, IModelProviderEvent.MODELS_REMOVED);
//			} else {
//				removeModel(file.getProject());
//			}
		} else if (kind == IResourceDelta.ADDED) {
//			if (model instanceof IBundlePluginModelBase) {
//				WorkspaceExtensionsModel extensions = new WorkspaceExtensionsModel(file);
//				extensions.setEditable(false);
//				((IBundlePluginModelBase) model).setExtensionsModel(extensions);
//				extensions.setBundleModel((IBundlePluginModelBase) model);
//				loadModel(extensions, false);
//				addExtensionChange(model, IModelProviderEvent.MODELS_ADDED);
//			} else {
//				createModel(file.getProject(), true);
//			}
		} else if (kind == IResourceDelta.CHANGED && (IResourceDelta.CONTENT & delta.getFlags()) != 0) {
//			if (model instanceof IBundlePluginModelBase) {
//				ISharedExtensionsModel extensions = ((IBundlePluginModelBase) model).getExtensionsModel();
//				boolean reload = extensions != null;
//				if (extensions == null) {
//					extensions = new WorkspaceExtensionsModel(file);
//					((WorkspaceExtensionsModel) extensions).setEditable(false);
//					((IBundlePluginModelBase) model).setExtensionsModel(extensions);
//					((WorkspaceExtensionsModel) extensions).setBundleModel((IBundlePluginModelBase) model);
//				}
//				loadModel(extensions, reload);
//			} else if (model != null) {
//				loadModel(model, true);
//				addChange(model, IModelProviderEvent.MODELS_CHANGED);
//			}
//			addExtensionChange(model, IModelProviderEvent.MODELS_CHANGED);
		}
	}


	/**
	 * Returns a plug-in model associated with the given project, or <code>null</code>
	 * if the project is not a plug-in project or the manifest file is missing vital data
	 * such as a symbolic name or version
	 * 
	 * @param project the given project
	 * 
	 * @return a plug-in model associated with the given project or <code>null</code>
	 * if no such valid model exists
	 */
	protected IPluginModelBase getPluginModel(IProject project) {
		return (IPluginModelBase) getModel(project);
	}

	/**
	 * Returns a list of all workspace plug-in models
	 * 
	 * @return an array of workspace plug-in models
	 */
	protected IPluginModelBase[] getPluginModels() {
		initialize();
		return (IPluginModelBase[]) fModels.values().toArray(new IPluginModelBase[fModels.size()]);
	}

	/**
	 * Adds listeners to the workspace and to the java model
	 * to be notified of PRE_CLOSE events and POST_CHANGE events.
	 */
	protected void addListeners() {
		IWorkspace workspace = EGLCore.getWorkspace();
		workspace.addResourceChangeListener(this, IResourceChangeEvent.PRE_CLOSE|IResourceChangeEvent.PRE_BUILD);
	}

	/**
	 * Removes listeners that the model manager attached on others, 
	 * as well as listeners attached on the model manager
	 */
	protected void removeListeners() {
		EGLCore.getWorkspace().removeResourceChangeListener(this);
	}


	/**
	 * This method is called when workspace models are read and initialized
	 * from the cache.  No need to read the workspace plug-ins from scratch.
	 * 
	 * @param models  the workspace plug-in models
	 */
	protected void initializeModels(IPluginModelBase[] models) {
		fModels = Collections.synchronizedMap(new HashMap());
		for (int i = 0; i < models.length; i++) {
			IProject project = models[i].getUnderlyingResource().getProject();
			fModels.put(project, models[i]);
		}
		IProject[] projects = EGLCore.getWorkspace().getRoot().getProjects();
		for (int i = 0; i < projects.length; i++) {
			// if any projects contained Manifest files and were not included in the PDEState,
			// we should create models for them now
			if (!fModels.containsKey(projects[i]) && isInterestingProject(projects[i]))
				createModel(projects[i], false);
		}
		addListeners();
	}

	/**
	 * Return URLs to projects in the workspace that have a manifest file (MANIFEST.MF
	 * or plugin.xml)
	 * 
	 * @return an array of URLs to workspace plug-ins
	 */
	protected URL[] getPluginPaths() {
		ArrayList list = new ArrayList();
		IProject[] projects = EGLCore.getWorkspace().getRoot().getProjects();
		for (int i = 0; i < projects.length; i++) {
			if (isEGLProject(projects[i])) {
				try {
					IPath path = projects[i].getLocation();
					if (path != null) {
						list.add(path.toFile().toURL());
					}
				} catch (MalformedURLException e) {
				}
			}
		}
		return (URL[]) list.toArray(new URL[list.size()]);
	}
}

