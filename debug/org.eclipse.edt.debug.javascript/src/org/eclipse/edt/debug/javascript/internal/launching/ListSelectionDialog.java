/*******************************************************************************
 * Copyright © 2006, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.javascript.internal.launching;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * Dialog for selecting an item from a list. A text box is provided so the user can filter the list of items.
 */
public class ListSelectionDialog extends Dialog
{
	protected String fTitle;
	protected String fMessage;
	protected Text fText;
	protected Table fTable;
	protected ILabelProvider fLabelProvider;
	protected ListElement[] fListElements;
	protected ListElement[] fFilteredListElements;
	protected int fSelectionIndex = -1;
	
	// Help id to use for F1 help request for this dialog instance
	private String fHelpId;
	
	/**
	 * Constructor for ListSelectionDialog
	 * 
	 * @param parent The parent shell.
	 * @param labelProvider The class that will supply the labels for the selection elements.
	 */
	public ListSelectionDialog( Shell parent, ILabelProvider labelProvider, String helpId )
	{
		super( parent );
		fLabelProvider = labelProvider;
		fHelpId = helpId;
	}
	
	/**
	 * @see Window#open()
	 */
	@Override
	public int open()
	{
		BusyIndicator.showWhile( null, new Runnable() {
			@Override
			public void run()
			{
				access$superOpen();
			}
		} );
		
		return getReturnCode();
	}
	
	protected void access$superOpen()
	{
		super.open();
	}
	
	/**
	 * Set the selection list elements.
	 * 
	 * @param elements The set of selection elements for the user to choose from.
	 */
	public void setElements( Object[] elements )
	{
		fListElements = new ListElement[ elements.length ];
		
		for ( int i = 0; i < elements.length; i++ )
		{
			Object element = elements[ i ];
			String text = fLabelProvider.getText( element );
			Image image = fLabelProvider.getImage( element );
			
			ListElement listElement = new ListElement( text, image, element );
			fListElements[ i ] = listElement;
		}
		
		Arrays.sort( fListElements, new ListElementComparator() );
		fFilteredListElements = fListElements;
	}
	
	/**
	 * Set the dialog title.
	 * 
	 * @param title The String to be used for the dialog title.
	 */
	public void setTitle( String title )
	{
		fTitle = title;
	}
	
	/**
	 * Set the dialog message.
	 * 
	 * @param message The String to be used for the dialog message.
	 */
	public void setMessage( String message )
	{
		fMessage = message;
	}
	
	/**
	 * @see Window#configureShell(Shell)
	 */
	@Override
	protected void configureShell( Shell shell )
	{
		super.configureShell( shell );
		if ( fTitle != null )
		{
			shell.setText( fTitle );
		}
	}
	
	/**
	 * @see Dialog#createButtonBar(Composite)
	 */
	@Override
	protected Control createButtonBar( Composite parent )
	{
		Composite composite = (Composite)super.createButtonBar( parent );
		if ( fFilteredListElements.length == 0 )
		{
			this.getButton( IDialogConstants.OK_ID ).setEnabled( false );
		}
		return composite;
	}
	
	/**
	 * @see Dialog#createDialogArea(Composite)
	 */
	@Override
	protected Control createDialogArea( Composite parent )
	{
		Composite composite = (Composite)super.createDialogArea( parent );
		((GridLayout)composite.getLayout()).numColumns = 1;
		
		createMessageArea( composite );
		createText( composite );
		createTable( composite );
		
		createTableEntries();
		Dialog.applyDialogFont( composite );
		PlatformUI.getWorkbench().getHelpSystem().setHelp( getShell(), fHelpId );
		
		return composite;
	}
	
	/**
	 * @see Window#create(Shell)
	 */
	@Override
	public void create()
	{
		super.create();
		setView();
	}
	
	/**
	 * Create the area for the dialog message.
	 * 
	 * @param parent The parent composite.
	 */
	protected void createMessageArea( Composite parent )
	{
		Label label = new Label( parent, SWT.NONE );
		label.setText( fMessage );
		
		GridData gridData = new GridData();
		gridData.grabExcessVerticalSpace = false;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.BEGINNING;
		label.setLayoutData( gridData );
	}
	
	/**
	 * Create the text area. This is where the user can enter text which will be used to filter the list of elements.
	 * 
	 * @param parent The parent composite.
	 */
	protected void createText( Composite parent )
	{
		fText = new Text( parent, SWT.BORDER );
		
		GridData gridData = new GridData();
		gridData.grabExcessVerticalSpace = false;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.BEGINNING;
		fText.setLayoutData( gridData );
		
		fText.addModifyListener( new ModifyListener() {
			@Override
			public void modifyText( ModifyEvent e )
			{
				handleTextModified();
			}
		} );
	}
	
	/**
	 * Create the table of elements.
	 * 
	 * @param parent The parent composite.
	 */
	protected void createTable( Composite parent )
	{
		fTable = new Table( parent, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION );
		fTable.setEnabled( true );
		fTable.addSelectionListener( new SelectionListener() {
			@Override
			public void widgetSelected( SelectionEvent e )
			{
				fSelectionIndex = fTable.getSelectionIndex();
			}
			
			@Override
			public void widgetDefaultSelected( SelectionEvent e )
			{
				fSelectionIndex = fTable.getSelectionIndex();
			}
		} );
		
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 1;
		gridData.widthHint = convertWidthInCharsToPixels( 60 );
		gridData.heightHint = convertHeightInCharsToPixels( 18 );
		fTable.setLayoutData( gridData );
		
		fTable.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseDoubleClick( MouseEvent event )
			{
				Point coordinates = new Point( event.x, event.y );
				TableItem item = fTable.getItem( coordinates );
				if ( item != null )
				{
					fSelectionIndex = fTable.indexOf( item );
					okPressed();
				}
			}
		} );
	}
	
	/**
	 * Fill in the table with the list of elements.
	 */
	protected void createTableEntries()
	{
		for ( int i = 0; i < fFilteredListElements.length; i++ )
		{
			TableItem item = new TableItem( fTable, SWT.NONE );
			ListElement element = fFilteredListElements[ i ];
			item.setText( element.getText() );
			item.setImage( element.getImage() );
		}
	}
	
	/**
	 * Filter the list of elements based on the new text.
	 */
	protected void handleTextModified()
	{
		String text = fText.getText();
		
		fTable.setRedraw( false );
		fTable.removeAll();
		
		if ( text == null || text.length() < 1 )
		{
			fFilteredListElements = fListElements;
		}
		else
		{
			Vector<ListElement> vector = new Vector<ListSelectionDialog.ListElement>();
			for ( int i = 0; i < fListElements.length; i++ )
			{
				ListElement element = fListElements[ i ];
				if ( element.getText().startsWith( text ) )
				{
					vector.add( element );
				}
			}
			
			fFilteredListElements = new ListElement[ vector.size() ];
			vector.copyInto( fFilteredListElements );
		}
		if ( fFilteredListElements.length == 0 )
		{
			this.getButton( IDialogConstants.OK_ID ).setEnabled( false );
		}
		else
		{
			this.getButton( IDialogConstants.OK_ID ).setEnabled( true );
		}
		
		createTableEntries();
		fTable.setRedraw( true );
		setView();
	}
	
	/**
	 * Set the selection and focus.
	 */
	protected void setView()
	{
		if ( fFilteredListElements.length > 0 )
		{
			fTable.setSelection( 0 );
			fSelectionIndex = 0;
		}
		else
		{
			fSelectionIndex = -1;
		}
		fText.setFocus();
	}
	
	/**
	 * Get the user's selection.
	 * 
	 * @return The result will be one of the elements that was passed in in setElements(Object[]).
	 * @see #setElements(Object[])
	 */
	public Object getResult()
	{
		if ( fSelectionIndex >= 0 && fSelectionIndex < fFilteredListElements.length )
		{
			return fFilteredListElements[ fSelectionIndex ].getElementObject();
		}
		
		return null;
	}
	
	/**
	 * Get the user's selection.
	 * 
	 * @return The result will be one of the elements that was passed in in setElements(Object[]).
	 * @see #setElements(Object[])
	 */
	public int getResultIndex()
	{
		return fSelectionIndex;
	}
	
	/**
	 * Class which contains the element along with its text and image.
	 */
	protected class ListElement
	{
		protected String fText;
		protected Image fImage;
		protected Object fElementObject;
		
		/**
		 * ListElement constructor.
		 * 
		 * @param text The element text.
		 * @param image The element image.
		 * @param elementObject The original element.
		 */
		public ListElement( String text, Image image, Object elementObject )
		{
			fText = text;
			fImage = image;
			fElementObject = elementObject;
		}
		
		/**
		 * Get the element text.
		 * 
		 * @return Returns a <code>String</code>.
		 */
		public String getText()
		{
			return fText;
		}
		
		/**
		 * Get the element image.
		 * 
		 * @return Returns an <code>Image</code>.
		 */
		public Image getImage()
		{
			return fImage;
		}
		
		/**
		 * Get the element object.
		 * 
		 * @return Returns an <code>Object</code>.
		 */
		public Object getElementObject()
		{
			return fElementObject;
		}
	}
	
	/**
	 * Class for comparing list elements. Used to sort the element list.
	 */
	protected class ListElementComparator implements Comparator<ListElement>
	{
		/**
		 * @see Comparator#compare(Object, Object)
		 */
		@Override
		public int compare( ListElement element1, ListElement element2 )
		{
			return element1.getText().compareToIgnoreCase( element2.getText() );
		}
	}
}
