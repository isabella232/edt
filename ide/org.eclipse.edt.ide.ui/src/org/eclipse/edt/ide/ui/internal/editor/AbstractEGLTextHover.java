/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
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

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;

public abstract class AbstractEGLTextHover implements ITextHover
{
	private IEditorPart editor;
	
	public void setEditor( IEditorPart editor )
	{
		this.editor = editor;
	}
	
	public IEditorPart getEditor()
	{
		return editor;
	}
	
	public IRegion getHoverRegion( ITextViewer textViewer, int offset )
	{
		Point selection= textViewer.getSelectedRange();
		if ( selection.x <= offset && offset < selection.x + selection.y )
		{
			return new Region( selection.x, selection.y );
		}
		return new Region( offset, 0 );
	}
}
