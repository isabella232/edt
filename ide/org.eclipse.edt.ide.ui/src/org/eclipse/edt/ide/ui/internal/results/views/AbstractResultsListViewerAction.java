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
package org.eclipse.edt.ide.ui.internal.results.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.ui.part.ViewPart;

public abstract class AbstractResultsListViewerAction extends SelectionListenerAction {
	private int type = 0;
	private ViewPart viewPart = null;
	private Clipboard clipboard = null;

	public static final int SELECT_ALL = 1;
	public static final int DESELECT_ALL = 2;
	public static final int COPY = 3;

	/**
	 * Constructor for EGLUtilitiesResultListViewerAction.
	 * @param text
	 */
	protected AbstractResultsListViewerAction(String text) {
		super(text);
	}
	
	public AbstractResultsListViewerAction(
		String text,
		ViewPart viewPart,
		int type) {
		this(text);
		this.type = type;
		this.viewPart = viewPart;
		clipboard = new Clipboard(Display.getCurrent());
	}
	
    public Object getInput() {
        return getCurrentViewer().getInput();
    }
    
	public void run() {
		if (getCurrentViewer() == null) {
			return;
		}
		switch (type) {
			case SELECT_ALL :
				{
					getCurrentViewer().setSelection(
						new StructuredSelection((List)getInput()),
						true);
					break;
				}
			case DESELECT_ALL :
				{
					getCurrentViewer().setSelection(new StructuredSelection(new ArrayList()), true);
					break;
				}
			case COPY :
				{
					copyToClipboard();
					break;
				}
			default :
				break;
		}
	}
	
	public void copyToClipboard() {
		// Get the selected markers
		String[] selection = getCurrentViewer().getList().getSelection();
		if (selection.length == 0) {
			return;
		}
		Object[] data = new Object[] { stringFromArray(selection)};
		Transfer[] transferTypes = new Transfer[] { TextTransfer.getInstance()};
		clipboard.setContents(data, transferTypes);
	}
	
	public String stringFromArray(String[] strings) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < strings.length; i++) {
			buffer.append(strings[i]);
			if (i < strings.length - 1) {
				buffer.append("\n"); //$NON-NLS-1$
			}
		}
		return buffer.toString();
	}

	protected boolean updateSelection(IStructuredSelection selection) {
		return !selection.isEmpty();
	}
	
	public abstract ListViewer getCurrentViewer();
	
	public ViewPart getViewPart() {
		return viewPart;
	}
}
