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

import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier;





/**
 * @author cduval
 *
 * 
 */
public class IncrementalBuilder extends AbstractBuilder {
	
	/**
	 * @param builder
	 */
	public IncrementalBuilder(Builder builder,IBuildNotifier notifier) {
		super(builder,notifier);
		this.processingQueue = new IncrementalProcessingQueue(builder.getProject(), notifier);
	}

	protected void build(){
		processRequiredProjectChanges();
	}

	protected void processRequiredProjectChanges(){	    
	    notifier.subTask(BuilderResources.buildProcessingDependentChanges);
		BuildManagerChange[] changes = buildManager.getChanges(builder.getProject());
		
		for (int i = 0; i < changes.length; i++) {
			if(changes[i].isPackage()){
				addDependents(((BuildManagerPackageChange)changes[i]).getPackageName());
			}else{
				BuildManagerPartChange partChange = (BuildManagerPartChange)changes[i];
				
				if(partChange.getPartType() == ITypeBinding.FUNCTION_BINDING){
					addDependents(partChange.getPartName());
				}else{
					addDependents(partChange.getPackageName(), partChange.getPartName());
				}
			}
		}
	}
}
