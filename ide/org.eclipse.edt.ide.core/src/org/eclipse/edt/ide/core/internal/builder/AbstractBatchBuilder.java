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
package org.eclipse.edt.ide.core.internal.builder;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier;
import org.eclipse.edt.ide.core.internal.lookup.ProjectBuildPathManager;
import org.eclipse.edt.mof.egl.utils.InternUtil;

public abstract class AbstractBatchBuilder extends AbstractBuilder {


	protected AbstractBatchBuilder(Builder builder, IBuildNotifier notifier) {
		super(builder, notifier);
		this.processingQueue = new BatchProcessingQueue(builder.getProject(), notifier);
	}

	protected void build(){
		notifier.subTask(BuilderResources.buildAnalyzingAllEGLFiles);
	    
		addAllEGLFiles();
	}

	protected void addAllEGLFiles()  {
		try{
			IContainer[] sourceLocations = ProjectBuildPathManager.getInstance().getProjectBuildPath(builder.getProject()).getSourceLocations();
			for (int i = 0, l = sourceLocations.length; i < l; i++) {
				final IContainer sourceLocation = sourceLocations[i];
				final int segmentCount = sourceLocation.getFullPath().segmentCount();
				IResource[] children = sourceLocation.members();
				
				for (int j = 0; j < children.length; j++) {
					children[j].accept(
						new IResourceProxyVisitor() {
							public boolean visit(IResourceProxy proxy) throws CoreException {
								IResource resource = proxy.requestResource();
			
								switch(proxy.getType()) {
									case IResource.FILE :
										if (!processedFiles.contains(resource)){
											IFile file = (IFile)resource;
											String[] packageName = InternUtil.intern(org.eclipse.edt.ide.core.internal.utils.Util.pathToStringArray(resource.getFullPath().removeFirstSegments(segmentCount).removeLastSegments(1)));
											if (org.eclipse.edt.ide.core.internal.model.Util.isEGLFileName(proxy.getName())) {
												addEGLFile(file, packageName);											
											}
											
											if (isOKToCopy(proxy.getName())){
												copyFileToOutputLocation(file,packageName);
											}
										}
										return false;
									case IResource.FOLDER :
										addEGLPackage(InternUtil.intern(org.eclipse.edt.ide.core.internal.utils.Util.pathToStringArray(resource.getFullPath().removeFirstSegments(segmentCount))));
										break;
								}
								return true;
							}
						},
						IResource.NONE
					);
				}
			}
		}catch(CoreException e){
			throw new BuildException(e);
		}
	}

	protected abstract void addEGLPackage(String[] packageName);
	protected abstract void addEGLFile(IFile file, String[] packageName);
}
