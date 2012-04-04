/*******************************************************************************
 * Copyright © 2005, 2012 IBM Corporation and others.
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

import org.eclipse.core.resources.IProject;
import org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier;
import org.eclipse.edt.ide.core.internal.dependency.DependencyGraph;
import org.eclipse.edt.ide.core.internal.dependency.DependencyGraphManager;
import org.eclipse.edt.ide.core.internal.dependency.IPartRequestor;

/**
 * @author svihovec
 *
 */
public class IncrementalProcessingQueue extends AbstractProcessingQueue{

	private DependencyGraph dependencyGraph;

	public IncrementalProcessingQueue(IProject project, IBuildNotifier notifier) {
		super(project, notifier);
		
		this.dependencyGraph = DependencyGraphManager.getInstance().getDependencyGraph(project);
	}

	public void addDependents(String[] packageName, String partName){
		dependencyGraph.findDependents(packageName, partName, new IPartRequestor(){

			public void acceptPart(String[] packageName, String partName) {
				doAddPart(packageName, partName);
			}});		
	}
	
	protected void addDependents(String[] qualifiedName){
		dependencyGraph.findDependents(qualifiedName, new IPartRequestor(){

			public void acceptPart(String[] packageName, String partName) {
				doAddPart(packageName, partName);
			}});		
	}
	
	protected void addDependents(String topLevelFunctionName){
		dependencyGraph.findDependents(topLevelFunctionName, new IPartRequestor(){

			public void acceptPart(String[] packageName, String partName) {
				doAddPart(packageName, partName);
			}});
	}
	
	protected void addPartFromCompiledFile(String[] packageName, String partName){
		// When incremental building, add all parts from this file
		// We don't need to do this when batch building because the parts have already been added
		addPart(packageName, partName);	
		
		super.addPartFromCompiledFile(packageName, partName);
	}
}
