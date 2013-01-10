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
package org.eclipse.edt.ide.ui.internal.editor;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlExtension;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SourceViewerInformationControl implements IInformationControl,
		IInformationControlExtension, DisposeListener {
	
	/** The control's shell */
	private Shell fShell;
	
	/** Border thickness in pixels. */
	private static final int BORDER= 1;
	
	/** The control's source viewer */
	private SourceViewer fViewer;
	
	/** The control's text widget */
	private StyledText fText;
	

	public SourceViewerInformationControl(Shell parent) {
		GridLayout layout;
		GridData gd;

		fShell= new Shell(parent, SWT.NO_FOCUS | SWT.ON_TOP | SWT.NO_TRIM);
		Display display= fShell.getDisplay();		
		fShell.setBackground(display.getSystemColor(SWT.COLOR_BLACK));

		Composite composite= fShell;
		layout= new GridLayout(1, false);
		int border= ((SWT.NO_TRIM & SWT.NO_TRIM) == 0) ? 0 : BORDER;
		layout.marginHeight= border;
		layout.marginWidth= border;
		composite.setLayout(layout);
		gd= new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gd);
		fViewer= createViewer(composite);
		
		fText= fViewer.getTextWidget();
		gd= new GridData(GridData.BEGINNING | GridData.FILL_BOTH);
		fText.setLayoutData(gd);
		fText.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		fText.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
			
		fText.addKeyListener(new KeyListener() {
				
			public void keyPressed(KeyEvent e)  {
				if (e.character == 0x1B) // ESC
					fShell.dispose();
			}
				
			public void keyReleased(KeyEvent e) {}
		});
	}

    private SourceViewer createViewer(Composite parent) {
    	SourceViewer viewer = new SourceViewer(parent, null, SWT.NONE);
          
		SourceViewerConfiguration configuration = new EGLSourceViewerConfiguration();
		viewer.configure(configuration);
		viewer.setEditable(false);	
		Font font= JFaceResources.getFont(JFaceResources.TEXT_FONT);
		viewer.getTextWidget().setFont(font);    
		        
		return viewer;
    }
	
	public void setInformation(String content) {
		if (content == null) {
			fViewer.setInput(null);
			return;
		}
		IDocument document = new Document(content);       
		new DocumentSetupParticipant().setup(document);
		fViewer.setDocument(document);
	}

	public void setSizeConstraints(int maxWidth, int maxHeight) {
	}

	public Point computeSizeHint() {
		return fShell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
	}

	public void setVisible(boolean visible) {
		fShell.setVisible(visible);
	}

	public void setSize(int width, int height) {
		fShell.setSize(width, height);
	}

	public void setLocation(Point location) {
		Rectangle trim= fShell.computeTrim(0, 0, 0, 0);
		Point textLocation= fText.getLocation();				
		location.x += trim.x - textLocation.x;		
		location.y += trim.y - textLocation.y;		
		fShell.setLocation(location);		
	}

	public void dispose() {
		if (fShell != null && !fShell.isDisposed()) {
			fShell.dispose();
		} else {
			widgetDisposed(null);
		}
	}

	public void addDisposeListener(DisposeListener listener) {
		fShell.addDisposeListener(listener);
	}

	public void removeDisposeListener(DisposeListener listener) {
		fShell.removeDisposeListener(listener);
	}

	public void setForegroundColor(Color foreground) {
		fText.setForeground(foreground);
	}

	public void setBackgroundColor(Color background) {
		fText.setBackground(background);
	}

	public boolean isFocusControl() {
		return fText.isFocusControl();
	}

	public void setFocus() {
		fShell.forceFocus();
		fText.setFocus();
	}

	public void addFocusListener(FocusListener listener) {
		fText.addFocusListener(listener);
	}

	public void removeFocusListener(FocusListener listener) {
		fText.removeFocusListener(listener);
	}

	public boolean hasContents() {
		return fText.getCharCount() > 0;
	}

	public void widgetDisposed(DisposeEvent e) {
		fShell= null;
		fText= null;
	}

}
