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
package org.eclipse.edt.ide.core.internal.generation;

import java.util.HashSet;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.builder.CancelledException;
import org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier;
import org.eclipse.edt.ide.core.CoreIDEPluginStrings;
import org.eclipse.edt.ide.core.internal.lookup.ProjectBuildPathManager;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.mof.utils.NameUtile;

/**
 * Base generator that processes deltas and kicks off the generation queue.
 */
public abstract class AbstractGenerator {
	
	protected GenerationBuilder builder;
	protected IBuildNotifier notifier;
	protected IContainer outputLocation;
	protected HashSet processedFiles;
	protected GenerationQueue generationQueue;
	
	protected AbstractGenerator(GenerationBuilder builder,IBuildNotifier notifier) {
		this.builder = builder;
		this.notifier = notifier;
		this.outputLocation = ProjectBuildPathManager.getInstance().getProjectBuildPath(builder.getProject()).getOutputLocation();
		this.processedFiles = new HashSet();
		this.generationQueue = new GenerationQueue(notifier, builder.getProject());
	}
	
	public boolean build(IResourceDelta sourceDelta) {
		boolean abortedBuild = false;
		
		try {
			beginBuilding();
			
			if (sourceDelta != null) {
				if (!processDeltas(sourceDelta)) {
					abortedBuild = true;
				}
			}
			
			if (!abortedBuild) {
				addAdditionalParts();
				generationQueue.generate();
				abortedBuild = notifier.isAborted();
			}
		} catch(CancelledException e) {
		    throw e;
		} catch(BuildException e) {
		    throw e;
		} catch(RuntimeException e) {
		    throw new BuildException(e);
        }
		
		
		return abortedBuild;
	}
	
	protected boolean processDeltas(IResourceDelta delta) {
		notifier.updateProgressDelta(0.20f);
		notifier.subTask(CoreIDEPluginStrings.analyzingChangedIRs);
		
		IResourceDelta sourceDelta = delta.findMember(outputLocation.getProjectRelativePath());
		if (sourceDelta != null) {
			// TODO Remove this check, or throw exception, since we should never get here...
			if (sourceDelta.getKind() == IResourceDelta.REMOVED) {
				System.out.println("ABORTING incremental build... found removed bin folder"); //$NON-NLS-1$
				return false; // removed source folder should not make it here, but handle anyways (ADDED is supported)
			}
			
			int segmentCount = sourceDelta.getFullPath().segmentCount();
			IResourceDelta[] children = sourceDelta.getAffectedChildren();
			for (int j = 0, m = children.length; j < m; j++) {
				processDeltas(children[j], segmentCount);
			}
		}
		
		notifier.checkCancel();
		return true;
	}
	
	private void processDeltas(IResourceDelta sourceDelta, int segmentCount) {
		IResource resource = sourceDelta.getResource();
		switch(resource.getType()) {
			case IResource.FOLDER :
				switch (sourceDelta.getKind()) {
					case IResourceDelta.ADDED:
					case IResourceDelta.CHANGED:
						IResourceDelta[] children = sourceDelta.getAffectedChildren();
						for (int i = 0, l = children.length; i < l; i++){
							processDeltas(children[i], segmentCount);
						}
						break;
					case IResourceDelta.REMOVED:
						//TODO cleanup generated artifacts?
						break;
				}
				break;
			case IResource.FILE:
				String resourceName = resource.getName();
				processedFiles.add(resource);
				if (org.eclipse.edt.compiler.tools.IRUtils.isEGLIRFileName(resourceName)) {
					switch (sourceDelta.getKind()) {
						case IResourceDelta.CHANGED: // generate even if the content hasn't changed; could be the generator for a file changed
						case IResourceDelta.ADDED:
							IPath fullPath = resource.getFullPath();
							IPath packagePath = fullPath.removeFirstSegments(segmentCount).removeLastSegments(1);
							generationQueue.addPart(NameUtile.getAsName(Util.pathToQualifiedName(packagePath)), NameUtile.getAsName(fullPath.removeFileExtension().lastSegment()));
							break;
						case IResourceDelta.REMOVED:
							//TODO cleanup generated artifacts?
							break;
					}
				}
				break;
		}
	}
	
	/**
	 * Populates the queue with additional parts to generate.
	 */
	protected abstract void addAdditionalParts();
	
	protected void beginBuilding() {
		notifier.setAborted(false); // we may be re-using the notifier, so set this back to false
	}
}
