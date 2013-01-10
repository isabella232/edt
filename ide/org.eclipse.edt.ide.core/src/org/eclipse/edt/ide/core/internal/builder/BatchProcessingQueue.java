/*******************************************************************************
 * Copyright © 2005, 2013 IBM Corporation and others.
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



/**
 * @author svihovec
 *
 */
public class BatchProcessingQueue extends AbstractProcessingQueue {

	public BatchProcessingQueue(IProject project, IBuildNotifier notifier) {
		super(project, notifier);
	}

	@Override
	protected void addDependents(String packageName, String partName) {
		// do nothing		
	}
	
	@Override
	protected void addDependents(String qualifiedName){
		// do nothing
	}
	
	/**
	 * Safety net - should never exceed max loop in batch processing
	 */
	@Override
	protected boolean hasExceededMaxLoop(){
   		return false;
   }

	
}
