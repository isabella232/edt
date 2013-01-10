/*******************************************************************************
 * Copyright © 2010, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvDesignOverlay;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvDesignOverlayDropLocation;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetDescriptor;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.GenManager;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.GenModel;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.GenModelBuilder;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.engine.GenEngine;
import org.eclipse.jface.operation.IRunnableWithProgress;


public class GenerationProgress implements IRunnableWithProgress {
	private EvDesignOverlay evDesignOverlay;
	private EvDesignOverlayDropLocation	dropLocation;
	private InsertDataNode rootInsertDataNode;
	
	public GenerationProgress(InsertDataNode rootInsertDataNode, EvDesignOverlay evDesignOverlay, EvDesignOverlayDropLocation	dropLocation){
		this.rootInsertDataNode = rootInsertDataNode;
		this.evDesignOverlay = evDesignOverlay;
		this.dropLocation = dropLocation;
	}
	
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		GenModel genModel = GenModelBuilder.getInstance().create(rootInsertDataNode);
		int taskTime = genModel.getRoot().getChildren().size() * 200;
		monitor.beginTask(Messages.NL_IWWP_Gen_Task_Compose_Generation_Model, taskTime);
		WidgetDescriptor widgetDescriptor = GenEngine.getInstance().gen(genModel);
		
		monitor.worked(taskTime/5);
		monitor.setTaskName(Messages.NL_IWWP_Gen_Task_Write_Code);
		
		GenManager.getInstance().setGenFromDataView(true);
		evDesignOverlay.doOperationWidgetCreate( widgetDescriptor, dropLocation );		
		GenManager.getInstance().setGenFromDataView(false);
		
		monitor.done();
	}
}
