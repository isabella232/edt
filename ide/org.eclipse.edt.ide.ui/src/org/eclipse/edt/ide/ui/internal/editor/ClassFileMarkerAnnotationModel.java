/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.editor;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IClassFile;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel;
import org.eclipse.ui.texteditor.MarkerAnnotation;

public class ClassFileMarkerAnnotationModel extends AbstractMarkerAnnotationModel implements IResourceChangeListener {
	
	private final IResource resource;
	private final IClassFile classFile;
	
	public ClassFileMarkerAnnotationModel(IResource resource, IClassFile classFile) {
		this.resource = resource;
		this.classFile = classFile;
	}
	
	@Override
	protected IMarker[] retrieveMarkers() throws CoreException {
		// BinaryReadOnlyFile doesn't support markers right now, but we'll check anyway in case it does in the future.
		IMarker[] rootMarkers = resource.getWorkspace().getRoot().findMarkers(IMarker.MARKER, true, IResource.DEPTH_ZERO);
		IMarker[] resourceMarkers = resource.findMarkers(IMarker.MARKER, true, IResource.DEPTH_ZERO);
		if (rootMarkers.length == 0) {
			return resourceMarkers;
		}

		if (resourceMarkers.length == 0) {
			return rootMarkers;
		}

		IMarker[] all = new IMarker[resourceMarkers.length + rootMarkers.length];
		System.arraycopy(resourceMarkers, 0, all, 0, resourceMarkers.length);
		System.arraycopy(rootMarkers, 0, all, resourceMarkers.length, rootMarkers.length);
		return all;
	}
	
	@Override
	protected void deleteMarkers(final IMarker[] markers) throws CoreException {
		resource.getWorkspace().run(new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				for (int i = 0; i < markers.length; i++) {
					markers[i].delete();
				}
			}
		}, null, IWorkspace.AVOID_UPDATE, null);
	}
	
	@Override
	protected void listenToMarkerChanges(boolean listen) {
		if (listen) {
			resource.getWorkspace().addResourceChangeListener(this);
		}
		else {
			resource.getWorkspace().removeResourceChangeListener(this);
		}
	}
	
	@Override
	protected boolean isAcceptable(IMarker marker) {
		try {
			return EGLCore.isReferencedBy(classFile, marker);
		}
		catch (CoreException ce) {
			EDTUIPlugin.log(ce);
			return false;
		}
	}
	
	/**
	 * @see AbstractMarkerAnnotationModel#createMarkerAnnotation(IMarker)
	 */
	protected MarkerAnnotation createMarkerAnnotation(IMarker marker) {
		return new EGLMarkerAnnotation(marker);
	}
	
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		IMarkerDelta[] deltas = event.findMarkerDeltas(null, true);
		if (deltas != null && deltas.length > 0) {
			boolean changed = false;
			for (IMarkerDelta delta : deltas) {
				try {
					if (EGLCore.isReferencedBy(classFile, delta)) {
						switch (delta.getKind()) {
							case IResourceDelta.ADDED :
								addMarkerAnnotation(delta.getMarker());
								changed = true;
								break;
							case IResourceDelta.REMOVED :
								removeMarkerAnnotation(delta.getMarker());
								changed = true;
								break;
							case IResourceDelta.CHANGED:
								modifyMarkerAnnotation(delta.getMarker());
								changed = true;
								break;
						}
					}
				}
				catch (CoreException ce) {
					EDTUIPlugin.log(ce);
				}
			}
			
			if (changed) {
				fireModelChanged();
			}
		}
	}
}
