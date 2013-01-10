/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.refactor;

import java.util.HashSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.prefs.BackingStoreException;

public class SettingUpdateResourceChangeListener implements IResourceChangeListener {

	private static SettingUpdateResourceChangeListener INSTANCE = new SettingUpdateResourceChangeListener();

	private SettingUpdateResourceChangeListener() {
	}

	public static SettingUpdateResourceChangeListener getInstance() {
		return INSTANCE;
	}

	private class ResourceConfigurationChecker implements IResourceDeltaVisitor {
//		private IResourceDelta fRootDelta;
		private HashSet fValidatedFilesSet = new HashSet();

		public ResourceConfigurationChecker(IResourceDelta rootDelta) {
//			fRootDelta = rootDelta;
		}

		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource dResource = delta.getResource();
			int rcType = dResource.getType();

			if (rcType == IResource.PROJECT || rcType == IResource.FOLDER) {
				int flags = delta.getFlags();
				switch (delta.getKind()) {
				case IResourceDelta.REMOVED:
					if ((flags & IResourceDelta.MOVED_TO) == 0) {
						handleDeleteFolder(dResource.getProject(), dResource.getFullPath());
						break;
					}
				case IResourceDelta.CHANGED:
					if ((flags & IResourceDelta.MOVED_TO) != 0) {
						IPath path = delta.getMovedToPath();
						if (path != null) {
							handleRenamedFolder(dResource.getProject(), dResource.getFullPath(), path);
						}
					} else if ((flags & IResourceDelta.MOVED_FROM) != 0) {
						IPath path = delta.getMovedFromPath();
						if (path != null) {
							handleRenamedFolder(dResource.getProject(), path, dResource.getFullPath());
						}
					} else {
						return true;
					}
				default:
					
					break;
				}
				return false;
			} else if (rcType == IResource.FILE && !dResource.isDerived()) {
				int flags = delta.getFlags();
				switch (delta.getKind()) {
				case IResourceDelta.REMOVED:
					if ((flags & IResourceDelta.MOVED_TO) == 0) {
						handleDeleteFile(dResource.getProject(), dResource.getFullPath());
						break;
					}
				case IResourceDelta.ADDED:
				case IResourceDelta.CHANGED:
					if ((flags & IResourceDelta.MOVED_TO) != 0) {
						IPath path = delta.getMovedToPath();
						if (path != null) {
							handleRenamedFile(dResource.getProject(), dResource.getFullPath(), path);
						}
					} else if ((flags & IResourceDelta.MOVED_FROM) != 0) {
						IPath path = delta.getMovedFromPath();
						if (path != null) {
							handleRenamedFile(dResource.getProject(), path, dResource.getFullPath());
						}
					}
					break;

				default:
					break;
				}
				return false;
			}
			return true; // visit the children
		}

		private void handleRenamedFile(final IProject project, final IPath fromPath, final IPath toPath) {
			if(!fValidatedFilesSet.add(fromPath))
                return;
			Runnable op = new Runnable() {
				@Override
				public void run() {
					try {
						ProjectSettingsUtility.replaceWorkspaceSettings( fromPath, toPath );
					} catch (BackingStoreException e) {
					}
				}
			};
			PlatformUI.getWorkbench().getDisplay().asyncExec( op );
		}
		
		private void handleRenamedFolder(final IProject project, final IPath fromPath, final IPath toPath) {
			if(!fValidatedFilesSet.add(fromPath))
                return;
			Runnable op = new Runnable() {
				@Override
				public void run() {
					try {
						ProjectSettingsUtility.replaceWorkspaceSettings( fromPath, toPath );
						handleRenamedProject(project, fromPath, toPath);
					} catch (BackingStoreException e) {
					}
				}
			};
			PlatformUI.getWorkbench().getDisplay().asyncExec( op );
		}
		
		private void handleRenamedProject(final IProject project, final IPath fromPath, final IPath toPath) {
			try {
				IEGLProject eglProject = EGLCore.create(project);
				IEGLProject[] referencingProjects = eglProject.getReferencingProjects();
				
				if(referencingProjects != null) {
					for(IEGLProject referencingProject : referencingProjects) {
						renameEntryInClasspath(referencingProject,fromPath,toPath);
					}
				}
			} catch (EGLModelException e) {
			}
		}
		
		//Referring to MovePackageFragmentRootOperation.renameEntryInClasspath()
		private void renameEntryInClasspath(IEGLProject eglproject, IPath fromPath, IPath toPath) throws EGLModelException {
			IEGLPathEntry[] classpath = eglproject.getRawEGLPath();
			IEGLPathEntry[] newClasspath = null;
			int cpLength = classpath.length;
			int newCPIndex = -1;
			
			for (int i = 0; i < cpLength; i++) {
				IEGLPathEntry entry = classpath[i];
				IPath entryPath = entry.getPath();
				if (fromPath.equals(entryPath)) {
					// rename entry
					if (newClasspath == null) {
						newClasspath = new IEGLPathEntry[cpLength];
						System.arraycopy(classpath, 0, newClasspath, 0, i);
						newCPIndex = i;
					}
					if(entry.getEntryKind() == IEGLPathEntry.CPE_PROJECT) {
						newClasspath[newCPIndex++] = EGLCore.newProjectEntry(toPath, entry.isExported());
					}
					
				} else if (newClasspath != null) {
					newClasspath[newCPIndex++] = entry;
				}
			}
			
			if (newClasspath != null) {
				if (newCPIndex < newClasspath.length) {
					System.arraycopy(newClasspath, 0, newClasspath = new IEGLPathEntry[newCPIndex], 0, newCPIndex);
				}
				eglproject.setRawEGLPath(newClasspath, null);
			}
		}

		private void handleDeleteFile(final IProject project, final IPath path) {
			if(!fValidatedFilesSet.add(path))
                return;
			Runnable op = new Runnable() {
				@Override
				public void run() {
					try {
						ProjectSettingsUtility.removeWorkspaceSettings( path );
					} catch (BackingStoreException e) {
					}
				}
			};
			PlatformUI.getWorkbench().getDisplay().asyncExec( op );
		}
		
		private void handleDeleteFolder(final IProject project, final IPath path) {
			if(!fValidatedFilesSet.add(path))
                return;
			Runnable op = new Runnable() {
				@Override
				public void run() {
					try {
						ProjectSettingsUtility.removeWorkspaceSettings( path );
						//handleDeletedProject(project, path);
					} catch (BackingStoreException e) {
					}
				}
			};
			PlatformUI.getWorkbench().getDisplay().asyncExec( op );

		}

//		// finds the project geven the initial project name
//		// That is:
//		// if the project of a given name was renamed returns the renamed
//		// project
//		// if the project of a given name was removed returns null
//		// if the project of a given name was neither renamed or removed
//		// returns the project of that name or null if the project does not
//		// exist
//		//
//		private IProject findModifiedProject(final String oldProjectName) {
//			IResourceDelta projectDelta = fRootDelta.findMember(new Path(
//					oldProjectName));
//			boolean replaced = false;
//			if (projectDelta != null) {
//				switch (projectDelta.getKind()) {
//				case IResourceDelta.REMOVED:
//					if ((projectDelta.getFlags() & IResourceDelta.MOVED_TO) == 0) {
//						return null;
//					}
//				case IResourceDelta.CHANGED:
//					if ((projectDelta.getFlags() & IResourceDelta.MOVED_TO) != 0) {
//						IPath path = projectDelta.getMovedToPath();
//						if (path != null)
//							return ResourcesPlugin.getWorkspace().getRoot()
//									.findMember(path).getProject();
//					}
//					break;
//				}
//			}
//
//			final IProject project[] = new IProject[1];
//			try {
//				fRootDelta.accept(new IResourceDeltaVisitor() {
//					public boolean visit(IResourceDelta delta)
//							throws CoreException {
//						IResource dResource = delta.getResource();
//						int rcType = dResource.getType();
//						if (rcType == IResource.ROOT) {
//							return true;
//						} else if (rcType == IResource.PROJECT) {
//							switch (delta.getKind()) {
//							case IResourceDelta.ADDED:
//							case IResourceDelta.CHANGED:
//								if ((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0) {
//									IPath path = delta.getMovedFromPath();
//									if (path != null
//											&& path.segment(0).equals(
//													oldProjectName)) {
//										project[0] = dResource.getProject();
//									}
//								}
//								break;
//							default:
//								break;
//							}
//						}
//						return false;
//					}
//				});
//			} catch (CoreException e) {
//			}
//
//			if (project[0] == null && !replaced)
//				project[0] = ResourcesPlugin.getWorkspace().getRoot()
//						.findMember(oldProjectName).getProject();
//			return project[0];
//		}
	}

	public void resourceChanged(IResourceChangeEvent event) {
		if (event.getSource() instanceof IWorkspace) {

			switch (event.getType()) {
			case IResourceChangeEvent.POST_CHANGE:
			case IResourceChangeEvent.POST_BUILD:
			case IResourceChangeEvent.PRE_DELETE:
				IResourceDelta resDelta = event.getDelta();
				if (resDelta == null) {
					break;
				}
				try {
					ResourceConfigurationChecker rcChecker = new ResourceConfigurationChecker(
							resDelta);
					resDelta.accept(rcChecker);

				} catch (CoreException e) {
				}
				break;
			default:
				break;
			}
		}
	}

}
