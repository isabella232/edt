/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.palette;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.tools.CreationTool;

public class EvPaletteCreationTool extends CreationTool {

	private Command	newCommand;
	private boolean	commandChanged		= false;
	private boolean	unlockingEditPart	= false;

	/**
	 * Default Constructor
	 */
	public EvPaletteCreationTool() {
		super();
	}

	protected void unlockTargetEditPart() {
		unlockingEditPart = true;
		super.unlockTargetEditPart();
		unlockingEditPart = false;
	}

	protected boolean updateTargetUnderMouse() {
		if( getCurrentViewer() == null || unlockingEditPart )
			return false;
		else
			return super.updateTargetUnderMouse();
	}

	/**
	 * Constructor
	 * 
	 * @param aFactory
	 */
	public EvPaletteCreationTool( CreationFactory aFactory ) {
		super( aFactory );
	}

	protected boolean handleMove() {
		super.handleMove();
		return true;
	}

	public Command getCurrentCommand() {
		if( commandChanged )
			return newCommand;
		else
			return super.getCurrentCommand();
	}

	public void setCurrentCommand( Command command ) {
		super.setCurrentCommand( command );
		newCommand = command;
	}
}
