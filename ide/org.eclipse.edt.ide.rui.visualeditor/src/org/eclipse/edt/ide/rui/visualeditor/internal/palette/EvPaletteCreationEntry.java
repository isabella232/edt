/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
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

import org.eclipse.gef.Tool;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.jface.resource.ImageDescriptor;

public class EvPaletteCreationEntry extends CombinedTemplateCreationEntry {

	protected boolean	_bUnloadWhenFinished	= false;

	/**
	 * 
	 */
	public EvPaletteCreationEntry( String strLabel, String shortDesc, Object template, CreationFactory factory, ImageDescriptor iconSmall, ImageDescriptor iconLarge, boolean unloadWhenFinished ) {
		super( strLabel, shortDesc, template, factory, iconSmall, iconLarge );

		// Set the ID to the template
		//---------------------------
		if( strLabel instanceof String )
			super.setId( (String)strLabel );

		_bUnloadWhenFinished = unloadWhenFinished;
	}

	/**
	 * 
	 */
	public Tool createTool() {
		EvPaletteCreationTool tool = new EvPaletteCreationTool( factory );
		tool.setUnloadWhenFinished( _bUnloadWhenFinished );
		return tool;
	}
}
