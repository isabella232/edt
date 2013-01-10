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
package org.eclipse.edt.ide.rui.visualeditor.internal.preferences;

import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvConstants;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.BidiFormat;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.BidiUtils;
import org.eclipse.edt.ide.rui.visualeditor.plugin.Activator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;



public class EvBidiSettingsDialog extends TitleAreaDialog {
		BidiFormat bidiFormat;
		
	  /**
	   * MyTitleAreaDialog constructor
	   * 
	   * @param shell the parent shell
	   */
	  public EvBidiSettingsDialog(Shell shell, BidiFormat bidiFormat) {
	    super(shell);
	    this.bidiFormat = bidiFormat;
	  }

	  /**
	   * Closes the dialog box Override so we can dispose the image we created
	   */
	  public boolean close() {	    
	    return super.close();
	  }

	  /**
	   * Creates the dialog's contents
	   * 
	   * @param parent the parent composite
	   * @return Control
	   */
	  protected Control createContents(Composite parent) {
	    Control contents = super.createContents(parent);

	    // Set the title
	    getShell().setText(Messages.NL_Bidirectional_options);
	    
	    // Set the image
	    Image image = Activator.getImage( EvConstants.ICON_BIDI_SETTINGS );
	    getShell().setImage( image );

	    // Set the message
	    setMessage(Messages.NL_BIDI_Page_Description, IMessageProvider.INFORMATION);	    

	    return contents;
	  }

	  /**
	   * Creates the gray area
	   * 
	   * @param parent the parent composite
	   * @return Control
	   */
	  protected Control createDialogArea(Composite parent) {
	    Composite composite = new Composite(parent, SWT.NONE);
	    BidiUtils.createBidiControls(composite, bidiFormat);

	    return composite;
	  }

	  /**
	   * Creates the buttons for the button bar
	   * 
	   * @param parent the parent composite
	   */
	  protected void createButtonsForButtonBar(Composite parent) {
	    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);	
	  }
	  
	  protected void okPressed() {
		  bidiFormat = BidiUtils.getBidiFormat();
		  super.okPressed();
	  }
	  
	  public BidiFormat getBidiFormat(){
		  return bidiFormat;
	  }
	  	
	}

