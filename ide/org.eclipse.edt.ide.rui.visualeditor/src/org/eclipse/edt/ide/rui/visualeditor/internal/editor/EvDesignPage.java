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
package org.eclipse.edt.ide.rui.visualeditor.internal.editor;

import java.awt.Dimension;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.ide.rui.utils.Util;
import org.eclipse.edt.ide.rui.utils.WorkingCopyGenerationResult;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Tooltips;
import org.eclipse.edt.ide.rui.visualeditor.internal.palette.EvPaletteRoot;
import org.eclipse.edt.ide.rui.visualeditor.internal.preferences.EvPreferences;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.AnimatedBusyPainter;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.BidiFormat;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.BidiUtils;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.ScrolledCompositeScroller;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetDescriptor;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetDescriptorRegistry;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetManager;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPart;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPropertyDescriptor;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPropertyValue;
import org.eclipse.edt.ide.rui.visualeditor.plugin.Activator;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerImpl;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;


public class EvDesignPage extends GraphicalEditorWithFlyoutPalette implements ControlListener, DisposeListener, IPropertyChangeListener, PaintListener, ProgressListener, SelectionListener {

	/**
	 * First part of two parts which do a screen capture of the web browser.
	 * In the first part, the browser is made visible.  Then the second part which
	 * does the screen capture is called asynchronously.
	 */
	protected class CapturePartA implements Runnable {
		
		public void run() {
			if( _captureInfo.bCaptureRunning == true )
				return;
			
			_captureInfo.bCaptureRunning = true;

			// Workaround for Linux
			// Put the browser in the correct location
			// It has been moved out of the way for Linux drag/drop to work
			//-------------------------------------------------------------
			if( _bGraphicsTransparencyAvailable == false )
				_compositeBrowser.setLocation( ptBROWSER_LOCATION.x, ptBROWSER_LOCATION.y );

			// Show the web browser
			//---------------------
			setDesignMode( false );

			// Initiate a paint of the web browser
			//------------------------------------
			Rectangle rectB = _compositeDesignArea.getBounds();
			_compositeDesignArea.redraw( 0, 0, rectB.width, rectB.height, true );
			
			// Wait for the paint to complete and do the screen capture
			//---------------------------------------------------------
			_compositeDesignArea.getDisplay().asyncExec( new CapturePartB() );
		}
	}

	/**
	 * Second part of two parts which do a screen capture of the web browser.
	 * The first part has made the web browser visible.  This part
	 * does the screen capture, and then makes the overlay visible.
	 */
	protected class CapturePartB implements Runnable {
		public void run(){
			// Create an image the same size as the browser
			//---------------------------------------------
			Rectangle rectBrowser = _compositeDesignArea.getBounds();
			if( _captureInfo.imageBrowser != null ){
				Rectangle rectBrowserImage = _captureInfo.imageBrowser.getBounds();
				if( rectBrowser.width != rectBrowserImage.width || rectBrowser.height != rectBrowserImage.height ){
					_captureInfo.imageBrowser.dispose();
					_captureInfo.imageBrowser = null;
				}
			}

			if( _captureInfo.imageBrowser == null )
				_captureInfo.imageBrowser = new Image( _compositeDesignArea.getDisplay(), rectBrowser.width, rectBrowser.height );
						
			// Copy from the browser screen to the browser image
			// The x and y are the location where the capture will be copied to
			//-----------------------------------------------------------------
			GC gcBrowserWidget = new GC( _browser );
			
			// Copy the screen from the browser origin to the image
			//-----------------------------------------------------
			gcBrowserWidget.copyArea( _captureInfo.imageBrowser, 0, 0 );
			
			// Remember the valid area
			//------------------------
			Point ptOrigin = _scrolledComposite.getOrigin();
			Rectangle rectClient = _scrolledComposite.getClientArea();
			_captureInfo.rectCapture.x = ptOrigin.x;
			_captureInfo.rectCapture.y = ptOrigin.y;
			_captureInfo.rectCapture.width = rectClient.width;
			_captureInfo.rectCapture.height = rectClient.height;
			
			gcBrowserWidget.dispose();
			
			// Workaround for Linux
			// Move the browser out of the way of the overlay
			// so the overlay can receive drag/drop events
			//-----------------------------------------------
			if( _bGraphicsTransparencyAvailable == false ){
				Rectangle rectBounds = _compositeBrowser.getBounds();
				_compositeBrowser.setLocation( rectBounds.x, -rectBounds.height );
			}

			// Place the overlay on top of the browser
			//----------------------------------------
			setDesignMode( true );
			
			// Indicate we are done
			//---------------------
			_captureInfo.bCaptureRunning = false;
			
			// Set the focus back to the control that had
			// focus before the capture
			//-------------------------------------------
			if( _captureInfo.controlFocusBeforeCapture != null )
				if( _captureInfo.controlFocusBeforeCapture.isDisposed() == false )
					_captureInfo.controlFocusBeforeCapture.setFocus();
			
			// If this capture is for an alignment,
			// complete the alignment
			//-------------------------------------
			if( _bAlignmentInProgress == true )
				alignBrowserAndOverlayEnd();
		}	
	}
	
	/**
	 * A layout for the scrolled composite, two sliders , two separators and the 'reset to default size' button.
	 * This layout is required in order to size the 'reset to default size' button correctly, and to
	 * place separators between adjacent sliders and scroll bars of the scrolled composite.
	 */
	protected class DesignAreaLayout extends Layout {
		/**
		 * 
		 */
		protected Point computeSize( Composite composite, int hint, int hint2, boolean flushCache ) {
			return new Point( 0, 0 );
		}

		/**
		 * Arranges the controls on the design area composite.
		 */
		protected void layout( Composite composite, boolean flushCache ) {

			if( _sliderHorizontal == null )
				return;

			// Packing will compute the preferred sizes
			//-----------------------------------------
			_sliderHorizontal.pack();
			_sliderVertical.pack();
			_labelSeparatorHorz.pack();
			_labelSeparatorVert.pack();

			Point ptSliderHorz = null;
			Point ptSliderVert = null;
			Point ptSeparatorHorz = null;
			Point ptSeparatorVert = null;

			if( _bBrowserSizeControlsVisible == true ) {
				ptSliderHorz = _sliderHorizontal.getSize();
				ptSliderVert = _sliderVertical.getSize();
				
				ptSeparatorHorz = _labelSeparatorHorz.getSize();
				ptSeparatorVert = _labelSeparatorVert.getSize();
			}
			
			else
				ptSliderHorz = ptSliderVert = ptSeparatorHorz = ptSeparatorVert = new Point( 0, 0 );

			_sliderHorizontal.setVisible( _bBrowserSizeControlsVisible );
			_sliderVertical.setVisible( _bBrowserSizeControlsVisible );
			_labelSeparatorHorz.setVisible( _bBrowserSizeControlsVisible );
			_labelSeparatorVert.setVisible( _bBrowserSizeControlsVisible );			
			_buttonResetToDefaultSize.setVisible( _bBrowserSizeControlsVisible );

			// The bounds of this composite will already be set
			//-------------------------------------------------
			Rectangle rectBounds = composite.getBounds();

			Rectangle rect = new Rectangle( 0, 0, rectBounds.width - ptSliderVert.x - ptSeparatorVert.x, rectBounds.height - ptSliderHorz.y - ptSeparatorHorz.y );
			_scrolledComposite.setBounds( rect );

			if( _bBrowserSizeControlsVisible == true ) {
				rect = new Rectangle( 0, rectBounds.height - ptSliderHorz.y, rectBounds.width - ptSliderVert.x - ptSeparatorVert.x, ptSliderHorz.y );
				_sliderHorizontal.setBounds( rect );

				rect = new Rectangle( rectBounds.width - ptSliderVert.x, 0, ptSliderVert.x, rectBounds.height - ptSliderHorz.y - ptSeparatorHorz.y );
				_sliderVertical.setBounds( rect );

				rect = new Rectangle( 0, rectBounds.height - ptSliderHorz.y - ptSeparatorHorz.y, rectBounds.width - ptSliderVert.x - ptSeparatorVert.x, ptSeparatorHorz.y );
				_labelSeparatorHorz.setBounds( rect );

				rect = new Rectangle( rectBounds.width - ptSliderVert.x - ptSeparatorVert.x, 0, ptSeparatorVert.x, rectBounds.height - ptSliderHorz.y - ptSeparatorHorz.y );
				_labelSeparatorVert.setBounds( rect );

				rect = new Rectangle( rectBounds.width - ptSliderVert.x, rectBounds.height - ptSliderHorz.y, ptSliderVert.x, ptSliderHorz.y );
				_buttonResetToDefaultSize.setBounds( rect );
			}
		}
	}

	/**
	 * We have our own graphical viewer so we can create our design area composite rather than a drawing canvas.
	 * This is constructed by the createGraphicalViewer method.
	 */
	protected class EvDesignGraphicalViewer extends GraphicalViewerImpl {

		/**
		 * Pretend to be a real graphical viewer.
		 */
		public EvDesignGraphicalViewer() {
			setRootEditPart( new ScalableRootEditPart() );
			setEditDomain( getEditDomain() );
			setEditPartFactory( new EvEditPartFactory() );
			setContents( new String() );
		}
	}

	protected static boolean				bALIGNMENT_DONE					= false;
	protected static Point					ptBROWSER_LOCATION				= new Point( 0, 0 );

	protected static int					SLIDER_PAGE						= 10;
	protected static int					SLIDER_THUMB					= 100;
	
	protected boolean						_bAlignmentInProgress			= false;
	protected boolean						_bBrowserSizeControlsVisible	= false;
	protected boolean						_bGraphicsTransparencyAvailable	= false;
	
	// IBMBIDI Append Start
	protected BidiFormat					_bidiFormat						= null;
	// IBMBIDI Append End
	
	protected AnimatedBusyPainter			_animatedBusyPainter			= null;
	protected Browser						_browser						= null;
	protected EvDesignBrowserManager		_browserManager					= null;
	protected boolean						_bUpdateRequired				= true;
	protected boolean						_bFullRefresh					= true;
	protected Button						_buttonResetToDefaultSize		= null;
	protected EvDesignCaptureInformation	_captureInfo					= null;
	protected Composite						_compositeBrowser				= null;
	protected Composite						_compositeDesign				= null;
	protected Composite						_compositeDesignArea			= null;
	protected Composite						_compositeFocus					= null;
	protected Composite						_compositeLayout				= null;
	protected EvDesignOutlinePage			_contentOutline					= null;
	protected Dimension						_dimSize						= null;
	protected EvEditor						_editor							= null;
	protected WorkingCopyGenerationResult	_generationResult				= null;
	protected Label							_labelSeparatorHorz				= null;
	protected Label							_labelSeparatorVert				= null;
	protected Label							_labelSizeVertical				= null;
	protected EvDesignOverlay				_overlay						= null;
	protected ScrolledComposite				_scrolledComposite				= null;
	protected ScrolledCompositeScroller		_scroller						= null;
	protected Slider						_sliderHorizontal				= null;
	protected Slider						_sliderVertical					= null;
	protected CTabFolder					_tabFolder						= null;
	protected EvDesignToolbar				_toolbar						= null;
	protected ScrollingGraphicalViewer		_viewer							= null;
	protected WidgetManager					_widgetManager					= new WidgetManager();

	/**
	 * Constructor.
	 */
	public EvDesignPage( EvEditor editor ) {
		_editor = editor;
		
		// Check for availability of graphics transparency
		// This check is also performed in the general preference page
		//------------------------------------------------------------
		String strOperatingSystem = Platform.getOS();
		_bGraphicsTransparencyAvailable = strOperatingSystem.equals( Platform.OS_WIN32 );

		_captureInfo = new EvDesignCaptureInformation();
			
		// GEF
		//----
		setEditDomain( new DefaultEditDomain( this ) );
		
		// The flyout palette doesn't have a way to set its initial state.
		// The palette's preference's initial state is hidden.
		// The state is remembered whenever it changes in any editor.
		// The state is restored whenever a flyout palette is created.
		// We set the preference when the editor is opened to fool the
		// palette into thinking its last state was the pinned open state.
		// Therefore, the flyout palette will be shown opened first.
		//----------------------------------------------------------------
		getPalettePreferences().setPaletteState( FlyoutPaletteComposite.STATE_PINNED_OPEN );
	}
	
	/**
	 * Begins the alignment test.
	 * Determines the origin offset required for alignment of the overlay with the browser.
	 * This is called when the design page's browser is created and an alignment has not been done.
	 * The method returns true if the alignment test has started successfully.
	 */
	protected boolean alignBrowserAndOverlayBegin(){
		if( _browserManager == null )
			return false;
		
		_bAlignmentInProgress = true;
		
		_browserManager.setAlignmentTestMode( true );
		_browserManager.refreshBrowser( true );
		capture();
		
		return true;
	}

	/**
	 * Ends the alignment test.
	 * This is called when a screen capture of the web browser has been completed during an alignment test.
	 * The screen capture contains an image of the web browser widget which may, or may not have a border around it.
	 * At the time of the screen capture, the web browser was displaying alignmenttest.html, which has
	 * as its background, alignmenttest.gif.  The gif is shown in the top left corner of the web page.  If
	 * the web browser widget has no borders, then the gif is also at the top left corner of the screen capture.
	 * We look for the pixels of alignmenttest.gif in the screen captured image.  If we find it, its location
	 * tells us the width and height of the border around the browser widget.  We can then set the top/left location
	 * of the browser widget to negative values to ensure proper alignment of the browser widget with the overlay that sits
	 * on top of it.  This test is only good for borders which are 16 pixels or less in width and/or height.
	 * This can easily be increased in the y direction, but increasing in the x direction requires
	 * changing the integer iBits variable to a long and reading in more bits for each line.
	 */
	protected void alignBrowserAndOverlayEnd(){
		// Indicate that we are no longer in alignment test mode
		//------------------------------------------------------
		_browserManager.setAlignmentTestMode( false );
		_bAlignmentInProgress = false;
		bALIGNMENT_DONE = true;
		
		// The browser is displaying web page alignmenttest.html which
		// specifies the background image alignmenttest.gif.  The image
		// consists of one line of sixteen pixels which has pixels:
		// 1 black, 1 white, 2 black, 2 white, 3 black, 3 white, 4 black
		// which is also binary:0100110001110000 and hexadecimal:4C70
		//--------------------------------------------------------------
		Point ptPattern = new Point( -1, -1 );
		
		ImageData imageData = _captureInfo.imageBrowser.getImageData();

		// The pattern that we are looking for
		//------------------------------------
		int iPattern = 0x4C700000;
		
		// Look for the pattern in the first 16 lines of the screen capture image
		//-----------------------------------------------------------------------
		int[] iaPixels = new int[ 32 ];
		
		for( int y=0; y<16; y++ ){
			// Read one line into our array
			//-----------------------------
			imageData.getPixels( 0, y, iaPixels.length, iaPixels, 0 );
			
			// Change all non-black pixel values to 1
			//---------------------------------------
			for( int x=0; x<iaPixels.length; x++ ){
				if( iaPixels[ x ] != 0 )
					iaPixels[ x ] = 1;
			}

			// Transfer from the array of 32 integers into bits of a single 32 bit integer
			// Start with the left most pixel, and shift it into the the rightmost bit.
			//----------------------------------------------------------------------------
			int iBits = 0;

			for( int x=0; x<32; x++ ){
				iBits <<= 1;
				
				if( iaPixels[ x ] == 1 )
					iBits |= 0x0001;
			}
			
			// Compare the upper half of the pattern to the reference
			// If there is no match, shift the pattern left by 1
			// This shifts the web browser border out of the way one pixel at a time
			//----------------------------------------------------------------------
			boolean bFound = false;
			
			for( int x=0; x<16; x++ ){
				int iTest = iBits & 0xffff0000;
				
				if( iTest == iPattern ){
					ptPattern.x = x;
					bFound = true;
					break;
				}
				
				// Shift the pattern to the left
				// to try to get rid of a left border
				//-----------------------------------
				iBits <<= 1;
			}
			
			if( bFound == true ){
				ptPattern.y = y;
				break;
			}
		}
		
		// Shift the web browser location up/left to hide
		// the browser widget borders and align with the
		// overlay sitting on top of it
		//-----------------------------------------------
		if( ptPattern.x > -1 && ptPattern.y > - 1){
			ptBROWSER_LOCATION.x = -ptPattern.x;
			ptBROWSER_LOCATION.y = -ptPattern.y;
		}
		
		// If the pattern was not found, do not
		// shift the browser widget location
		//-------------------------------------
		else{
			ptBROWSER_LOCATION.x = 0;
			ptBROWSER_LOCATION.y = 0;
		}
		
		_compositeBrowser.setLocation( ptBROWSER_LOCATION );
		
		// Dispose of the capture image if it is
		// no longer required for faking transparency
		//-------------------------------------------
		if( _bGraphicsTransparencyAvailable == true ){
			_captureInfo.imageBrowser.dispose();
			_captureInfo.imageBrowser = null;
		}

		// The displaying of the generated web page
		// was interrupted to do the alignment test.
		// Complete displaying the generated web page
		//-------------------------------------------
		updateBrowserFullPartB();
	}

	/**
	 * Begins the process of screen capturing the web browser
	 */
	protected void capture() {
		if( _browser == null )
			return;
		
		if( _bAlignmentInProgress == true || _bGraphicsTransparencyAvailable == false ){
			_captureInfo.controlFocusBeforeCapture = _compositeDesignArea.getDisplay().getFocusControl();
			_compositeDesignArea.getDisplay().asyncExec( new CapturePartA() );
		}
	}
	
	/**
	 * Declared in ProgressMonitor.  This method does nothing.
	 */
	public void changed( ProgressEvent event ) {
	}

	/**
	 * Declared in ProgressMonitor.  This method is called by the browser
	 * after it has completed updating.  The busy indicator is turned off.
	 */
	public void completed( ProgressEvent event ) {
		_animatedBusyPainter.animationStop();
	}

	/**
	 * Called when the widget manager receives a new set of widgets from the browser.
	 * The widgets have a package name and type name, but no project name.
	 * A type ID for the widget part is assembled and given to the widget part.  The type ID can then be
	 * used to find a corresponding widget descriptor.
	 */
	protected void computeWidgetIDs(){
		// Use a hash table so we only ask the
		// editor provider once for each widget type
		//------------------------------------------
		Hashtable hashPackageAndTypeToProject = new Hashtable();
		
		// Obtain the list of all widgets
		//-------------------------------
		List listWidgetParts = _widgetManager.getWidgetList();
		Iterator iterWidgetParts = listWidgetParts.iterator();
		StringBuffer strb = new StringBuffer();
		
		while( iterWidgetParts.hasNext() ){
			WidgetPart widgetPart = (WidgetPart)iterWidgetParts.next();
			
			// Assemble a package name and type name string
			//---------------------------------------------
			String strTypeName = widgetPart.getTypeName();
			String strPackageName = widgetPart.getPackageName();

			strb.setLength( 0 );
			strb.append( strPackageName );
			strb.append( Util.WIDGET_ID_SEPARATOR );
			strb.append( strTypeName );

			String strPackageAndType = strb.toString();
			
			// Obtain the project name either from the hash table or the editor provider
			//--------------------------------------------------------------------------
			String strProjectName = null;
			if( hashPackageAndTypeToProject.containsKey( strPackageAndType ) == true )
				strProjectName = (String)hashPackageAndTypeToProject.get( strPackageAndType );

			else{
				// A widget will be in the registry if it will have the VEWidget annotation
				WidgetDescriptorRegistry registry = WidgetDescriptorRegistry.getInstance(getEditor().getProject());
				WidgetDescriptor widgetDescriptor = registry.getDescriptor( strPackageName, strTypeName );

				if(widgetDescriptor != null){
					strProjectName = widgetDescriptor.getProjectName();
				}else{
					strProjectName = _editor.getWidgetProjectName( strPackageName, strTypeName );
				}
				
				// Store it even if it has zero length
				//------------------------------------
				if( strProjectName != null )
					hashPackageAndTypeToProject.put( strPackageAndType, strProjectName );
			}

			// Assemble widget type ID from project name, package name and type name
			//----------------------------------------------------------------------
			strb.setLength( 0 );
			
			if( strProjectName != null && strProjectName.length() > 0 ){
				strb.append( strProjectName );
				strb.append( Util.WIDGET_ID_SEPARATOR );
			}
				
			strb.append( strPackageAndType );
			
			widgetPart.setTypeID( strb.toString() );
		}
	}
	
	/**
	 * Called when the design area is resized.
	 */
	public void controlMoved( ControlEvent event ) {
		if( event.widget == _compositeDesignArea )
			capture();
	}

	/**
	 * Called when the scrolled composite or design area is resized.
	 * The scrolled composite is resized if the user changes the editor size
	 * The design area is resized if the user changes the browser size using the browser size controls
	 */
	public void controlResized( ControlEvent event ) {
		if( event.widget == _scrolledComposite || event.widget == _compositeDesignArea )
			capture();
	}

	/**
	 * Creates the browser as the second child of the design area composite, and browser manager. 
	 */
	protected void createBrowser() {
		_browser = _editor.createBrowser( _compositeBrowser );
		_compositeBrowser.layout();

		if( _browser == null )
			return;

		// Prevent adding ourselves as a listener 
		// more than once in shared browser scenario
		//------------------------------------------
		_browser.removeProgressListener( this );
		_browser.addProgressListener( this );

		// Only create the browser manager once
		// in shared browser scenario
		//-------------------------------------
		if( _browserManager == null )
			_browserManager = new EvDesignBrowserManager( _browser, _editor.getURL(), this, _editor.getEditorProvider() );
	}

	/**
	 * Is called to create the page's user interface content
	 */
	protected Control createDesignControl( Composite compositeParent ) {
		_bBrowserSizeControlsVisible = EvPreferences.getBoolean( EvConstants.PREFERENCE_BROWSER_SIZE_CONTROLS_VISIBLE );
		//@bd1a Start
		if(Locale.getDefault().toString().toLowerCase().indexOf("ar") != -1) 
			_compositeDesign = new Composite( compositeParent, SWT.BORDER | SWT.LEFT_TO_RIGHT); 
		else
		//@bd1a End
		_compositeDesign = new Composite( compositeParent, SWT.BORDER );
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.numColumns = 2;
		_compositeDesign.setLayout( gridLayout );
		_compositeDesign.addDisposeListener( this );

		// Animated busy indicator
		//------------------------
		_animatedBusyPainter = new AnimatedBusyPainter( _compositeDesign );
		GridData gridData = new GridData();
		gridData.widthHint = 16;
		gridData.heightHint = 16;
		gridData.horizontalIndent = 4;
		_animatedBusyPainter.setLayoutData( gridData );
		
		// Toolbar
		//--------
		_toolbar = new EvDesignToolbar( _compositeDesign, SWT.NULL, this );
		gridData = new GridData( GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL );
		_toolbar.setLayoutData( gridData );

		// Focus indicator which is a single line above the design area
		//-------------------------------------------------------------
		_compositeFocus = new Composite( _compositeDesign, SWT.NULL );
		gridLayout = new GridLayout();
		_compositeFocus.setLayout( gridLayout );
		gridData = new GridData( GridData.FILL_HORIZONTAL );
		gridData.horizontalSpan = 2;
		gridData.heightHint = 2;
		_compositeFocus.setLayoutData( gridData );
		_compositeFocus.addPaintListener( this );
		
		// We have our own layout
		//-----------------------
		_compositeLayout = new Composite( _compositeDesign, SWT.NULL );
		_compositeLayout.setLayout( new DesignAreaLayout() );
		gridData = new GridData( GridData.FILL_BOTH );
		_compositeLayout.setLayoutData( gridData );

		gridData = new GridData( GridData.FILL_BOTH );
		gridData.horizontalSpan = 2;
		_compositeLayout.setLayoutData( gridData );

		// Scrolled composite so that the browser/design canvas can be scrolled
		//---------------------------------------------------------------------
		_scrolledComposite = new ScrolledComposite( _compositeLayout, SWT.H_SCROLL | SWT.V_SCROLL );
		_scrolledComposite.setAlwaysShowScrollBars( true );
		if( _bGraphicsTransparencyAvailable == false )
			_scrolledComposite.addControlListener( this );
		
		ScrollBar scrollbar = _scrolledComposite.getHorizontalBar();
		scrollbar.setPageIncrement( 200 );
		scrollbar.setIncrement( 16 );

		scrollbar = _scrolledComposite.getVerticalBar();
		scrollbar.setPageIncrement( 200 );
		scrollbar.setIncrement( 16 );		

		// Composite that contains overlayed browser and design canvas
		//------------------------------------------------------------
		_dimSize = new Dimension( EvPreferences.getInt( EvConstants.PREFERENCE_BROWSER_SIZE_DEFAULT_WIDTH ), EvPreferences.getInt( EvConstants.PREFERENCE_BROWSER_SIZE_DEFAULT_HEIGHT ) );

		_compositeDesignArea = new Composite( _scrolledComposite, SWT.NO_BACKGROUND );
		_compositeDesignArea.setBounds( new Rectangle( 0, 0, _dimSize.width, _dimSize.height ) );
		if( _bGraphicsTransparencyAvailable == false )
			_compositeDesignArea.addControlListener( this );

		// Design canvas
		//--------------
		_overlay = new EvDesignOverlay( _compositeDesignArea, this );
		_overlay.setBounds( new Rectangle( 0, 0, _dimSize.width, _dimSize.height ) );
		
		// Browser composite
		//------------------
		_compositeBrowser = new Composite( _compositeDesignArea, SWT.NULL );
		_compositeBrowser.setLayout( new FillLayout() );
		_compositeBrowser.setBounds( new Rectangle( 0, 0, _dimSize.width, _dimSize.height ) );

		// The browser is created when updateBrowser is called
		//----------------------------------------------------
		_scrolledComposite.setContent( _compositeDesignArea );

		// Automatic scrolling for the scrolled composite
		// Remembered for dragging a palette item
		//-----------------------------------------------
		_scroller = new ScrolledCompositeScroller( _scrolledComposite, _overlay );

		// Vertical size control
		//----------------------
		_sliderVertical = new Slider( _compositeLayout, SWT.VERTICAL );
		_sliderVertical.setValues( EvPreferences.getInt( EvConstants.PREFERENCE_BROWSER_SIZE_DEFAULT_HEIGHT ), EvPreferences.getInt( EvConstants.PREFERENCE_BROWSER_SIZE_MINIMUM_HEIGHT ), EvPreferences.getInt( EvConstants.PREFERENCE_BROWSER_SIZE_MAXIMUM_HEIGHT ) + SLIDER_THUMB, SLIDER_THUMB, 1, SLIDER_PAGE );
		_sliderVertical.addSelectionListener( this );

		// Horizontal size control
		//------------------------
		_sliderHorizontal = new Slider( _compositeLayout, SWT.HORIZONTAL | SWT.BORDER );
		_sliderHorizontal.setValues( EvPreferences.getInt( EvConstants.PREFERENCE_BROWSER_SIZE_DEFAULT_WIDTH ), EvPreferences.getInt( EvConstants.PREFERENCE_BROWSER_SIZE_MINIMUM_WIDTH ), EvPreferences.getInt( EvConstants.PREFERENCE_BROWSER_SIZE_MAXIMUM_WIDTH ) + SLIDER_THUMB, SLIDER_THUMB, 1, SLIDER_PAGE );
		_sliderHorizontal.addSelectionListener( this );

		_labelSeparatorHorz = new Label( _compositeLayout, SWT.SEPARATOR | SWT.HORIZONTAL );
		_labelSeparatorVert = new Label( _compositeLayout, SWT.SEPARATOR | SWT.VERTICAL );

		_buttonResetToDefaultSize = new Button( _compositeLayout, SWT.PUSH );
		_buttonResetToDefaultSize.setToolTipText( Tooltips.NL_Reset_browser_to_default_size );
		_buttonResetToDefaultSize.addSelectionListener( this );

		// Content outline view
		//---------------------
		_contentOutline = new EvDesignOutlinePage( this );

		// Listen to preference changes
		//-----------------------------
		EvPreferences.getPreferenceStore().addPropertyChangeListener( this );

		initializeGraphicalViewer();

		// Listen for the first selection of this page
		// for lazy creation of the browser
		//--------------------------------------------
		_editor.getPageFolder().addSelectionListener( this );
		
		return _compositeDesign;
	}

	/**
	 * Overrides the GraphicalEditor so we can create our own graphical viewer.
	 */
	protected void createGraphicalViewer( Composite compositeParent ) {
		GraphicalViewer viewer = new EvDesignGraphicalViewer();
		setGraphicalViewer( viewer );

		// Create the toolbar and design area
		//-----------------------------------
		createDesignControl( compositeParent );
	}

	/**
	 * This method overrides the super class to allow us to listen to drop events from the palette
	 * and to listen to an enter key press to create a new widget.
	 */
	protected PaletteViewerProvider createPaletteViewerProvider() {
		return new EvPaletteViewerProvider( getEditDomain(), this );
	}

	/**
	 * Called by the overlay to create a widget at the specified drop location.
	 * If model changes are received, the browser is not updated until the operation is complete.
	 */
	public void doOperationWidgetCreate( WidgetDescriptor descriptor, EvDesignOverlayDropLocation location ) {
		_editor.doSourceOperationWidgetCreate( descriptor, location );
	}

	/**
	 * Called by the overlay to delete a widget.
	 * If model changes are received, the browser is not updated until the operation is complete.
	 */
	public void doOperationWidgetDelete( WidgetPart widget ) {
		_editor.doSourceOperationWidgetDelete( widget );
	}

	/**
	 * Called by the overlay to move the widget to the specified drop location.
	 * If model changes are received, the browser is not updated until the operation is complete.
	 */
	public void doOperationWidgetMove( WidgetPart widget, EvDesignOverlayDropLocation location ) {
		_editor.doSourceOperationWidgetMove( widget, location );
	}

	/**
	 * Called by the overlay to change the value of widget properties
	 * If model changes are received, the browser is not updated until the operation is complete.
	 */
	public void doOperationWidgetPropertyValueChanges( List listPropertyChanges ) {
		_editor.doSourceOperationWidgetPropertyValueChanges( listPropertyChanges );
	}

	/**
	 * Called by the overlay to dispatch the click event
	 */
	public void doOperationWidgetOnclick( WidgetPart widget, String property, String value ) {
		_editor.doSourceOperationWidgetPropertyValueChange( widget, property, null, null, new WidgetPropertyValue( value ), WidgetPropertyDescriptor.WIDGET_PROPERTY  );
	}

	public void doSourceOperation( EvSourceOperation operation ) {
		_editor.doSourceOperation( operation );
	}
	
	/**
	 * 
	 */
	public void doSave( IProgressMonitor arg0 ) {
	}

	/**
	 * 
	 */
	public void doSaveAs() {
	}

	/**
	 * Called by the design page's action bar contributor.
	 */
	public IAction getAction( String strActionId ) {
		if( strActionId.equals( ActionFactory.DELETE.getId() ) == true )
			return _overlay.getAction( strActionId );

		if( strActionId.equals( ActionFactory.PROPERTIES.getId() ) == true )
			return _overlay.getAction( strActionId );

		return null;
	}

	// IBMBIDI Append Start
	public BidiFormat getBidiFormat() {
		if( _bidiFormat == null )
			return BidiUtils.getBidiFormatFromPreferences();
		return _bidiFormat;
	}

	// IBMBIDI Append End

	/**
	 * Called by the editor if the design and preview
	 * pages share a common web browser.
	 */
	public Browser getBrowser() {
		return _browser;
	}

	/**
	 * Returns information involving the screen capture of the web browser.
	 */
	public EvDesignCaptureInformation getCaptureInformation(){
		return _captureInfo;
	}
	
	/**
	 * Returns the content outline page for this editor. 
	 */
	public EvDesignOutlinePage getContentOutline() {
		return _contentOutline;
	}

	/**
	 * Returns the visual editor.
	 */
	public EvEditor getEditor() {
		return _editor;
	}

	/**
	 * Return the design so that the internal GEF palette is beside it.
	 * Returning the design area composite doesn't work since the flyout palette composite
	 * will ensure that the composite returned is parented by the flyout palette composite.
	 */
	public Control getGraphicalControl() {
		return _compositeDesign;
	}

	/**
	 * Required by GraphicalEditorWithFlyoutPalette
	 */
	protected PaletteRoot getPaletteRoot() {
		EvPaletteRoot paletteRoot = EvPaletteRoot.getInstance(this.getEditor().getProject());
		return paletteRoot;
	}

	/**
	 * Called by the overlay to control the scroller during a palette item drag.
	 */
	public ScrolledCompositeScroller getScroller() {
		return _scroller;
	}

	/**
	 * Called by the editor to obtain a shell.
	 */
	public Shell getShell() {
		return _browser.getShell();
	}

	/**
	 * Returns the widget part given a statement offset and length.
	 */
	public WidgetPart getWidget( int iOffset, int iLength ) {
		return _widgetManager.getWidget( iOffset, iLength );
	}

	/**
	 * 
	 */
	public WidgetManager getWidgetManager() {
		return _widgetManager;
	}

	/**
	 * 
	 */
	public WidgetPart getWidgetSelected() {
		return _overlay.getWidgetSelected();
	}

	/**
	 * 
	 */
	public void init( IEditorSite site, IEditorInput input ) throws PartInitException {
		super.setSite( site );
		super.setInput( input );
	}

	/**
	 * Called by the editor when its setInput is called.
	 * This happens when the filename has changed. 
	 */
	public void inputChanged( IEditorInput input ){
		if( _browserManager != null )
			_browserManager = new EvDesignBrowserManager( _browser, _editor.getURL(), this, _editor.getEditorProvider() );
	}
	
	/**
	 * This method is defers to the EGL editor, so returns false.
	 */
	public boolean isDirty() {
		return false;
	}

	/**
	 * Returns whether the operating system supports graphics transparency.
	 * This is called by the toolbar to hide/show the variable transparency controls.
	 */
	public boolean isGraphicsTransparencyAvailable(){
		return _bGraphicsTransparencyAvailable;
	}
	/**
	 * This method is defers to the EGL editor, so returns false.
	 */
	public boolean isSaveAsAllowed() {
		return false;
	}

	/**
	 * Called by the overlay whenever its focus changes.
	 * The focus composite is redrawn to indicate whether the design area has focus.
	 */
	public void overlayFocusChanged( boolean bOverlayHasFocus ){
		_compositeFocus.redraw();
	}

	/**
	 * Called whenever a person presses the enter key on a selected palette item.
	 * A new widget is created as the last child of the RUI handler
	 */
	public void paletteItemSelected( String strWidgetTypeID ){
		if( strWidgetTypeID == null )
			return;
		
		boolean bAllowDrop = _editor.isRuiHandler() == true;
		if( bAllowDrop == false )
			return;

		WidgetDescriptor descriptor = WidgetDescriptorRegistry.getInstance(this.getEditor().getProject()).getDescriptor( strWidgetTypeID );
		if( descriptor == null )
			return;
		
		// The new widget is created as the last child of the RUI handler
		//---------------------------------------------------------------
		EvDesignOverlayDropLocation location = new EvDesignOverlayDropLocation();
		location.widgetParent = _widgetManager.getWidgetRoot();
		location.iIndex = _widgetManager.getWidgetRoot().getChildren().size();
		
		_overlay.rememberSelectionHierarchyForOperation( location.widgetParent, location.iIndex );
		doOperationWidgetCreate( descriptor, location );
	}
	
	/**
	 * The preferences for the browser size is monitored in order to update the sliders and browser.
	 */
	public void propertyChange( PropertyChangeEvent event ) {

		String strProperty = event.getProperty();

		try {
			if( strProperty.indexOf( "BrowserSize" ) >= 0 ) {
				// Update the browser size controls
				//---------------------------------
				boolean bChanged = updateBrowserSizeControls();

				// Only update the browser if the preferences did not match the sliders
				//---------------------------------------------------------------------
				if( bChanged == true ) {
					_compositeDesignArea.setBounds( new Rectangle( 0, 0, _dimSize.width, _dimSize.height ) );
					_overlay.setBounds( new Rectangle( 0, 0, _dimSize.width, _dimSize.height ) );
					_compositeBrowser.setBounds( new Rectangle( ptBROWSER_LOCATION.x, ptBROWSER_LOCATION.y, _dimSize.width, _dimSize.height ) );
				}
			}

			else if( strProperty.equals( EvConstants.PREFERENCE_COLOR_SELECTION                ) == true ||
					 strProperty.equals( EvConstants.PREFERENCE_COLOR_DROP_LOCATION_POTENTIAL  ) == true ||
					 strProperty.equals( EvConstants.PREFERENCE_COLOR_DROP_LOCATION_SELECTED   ) == true ||
					 strProperty.equals( EvConstants.PREFERENCE_PATTERN_DROP_LOCATION_SELECTED ) == true ||
					 strProperty.equals( EvConstants.PREFERENCE_PATTERN_SELECTION              ) == true    ) {
				_overlay.updateColors();
			}
			
			// IBM BIDI Append Start
			else if( strProperty.equals( Activator.IS_BIDI ) ) {
				_toolbar._itemBidiPreferences.setEnabled( ( (Boolean)event.getNewValue() ).booleanValue() );
			}
			// IBM BIDI Append End

		}

		// Catch widget is disposed exceptions
		//------------------------------------
		catch( SWTException ex ) {
		}
	}

	/**
	 * Called by the design tool bar item to refresh the palette.
	 * The descriptor registry will notify the palette root whenever it has completed an initialization. 
	 */
	public void refreshPalette() {
		this.getEditor().getEditorProvider().eglpathUpdated();
		WidgetDescriptorRegistry.getInstance(this.getEditor().getProject()).reinitialize();
		
	}

	/**
	 * Called by the editor whenever the web browser is shared
	 * between the design and preview pages during a web browser
	 * re-parenting operation.
	 */
	public void resetBrowserToNull() {
		_browser = null;
	}

	/**
	 * Called by the editor to select a widget.  The overlay is asked to select the widget.
	 */
	public void selectWidget( WidgetPart widget ) {
		_overlay.selectWidget( widget );
		_overlay.redraw();
	}

	// IBMBIDI Append Start
	/**
	 * 
	 */
	public void setBidiFormat( BidiFormat bidiFormat ) {
		this._bidiFormat = bidiFormat;
	}

	// IBMBIDI Append End

	/**
	 * Called to hide and show the overlay.
	 */
	public void setDesignMode( boolean bDesign ) {
		if( _compositeBrowser == null || _overlay == null || _compositeBrowser.isDisposed() || _overlay.isDisposed() )
			return;

		if( bDesign == true )
			_compositeBrowser.moveBelow( _overlay );
		else
			_compositeBrowser.moveAbove( _overlay );
	}

	/**
	 * Gives the overlay focus
	 */
	public void setFocus() {
		_overlay.setFocus();
	}

	/**
	 * Called by the toolbar
	 */
	public void setTransparency( int iTransparencyMode, int iTransparencyValue ) {
		_overlay.setTransparency( iTransparencyMode, iTransparencyValue );
	}

	/**
	 * Called by the editor when it is being disposed.
	 */
	public void terminate() {
		if( _browserManager != null )
			_browserManager.terminate();
	}

	/**
	 * Updates the browser if the design page is visible, and the model has changed.
	 */
	protected void updateBrowser( WorkingCopyGenerationResult result ) {
		updateBrowserFullPartA( result );
	}
	
	/**
	 * 
	 */
	protected void updateBrowserFullPartA( WorkingCopyGenerationResult result ){
		// Save the generation results in case
		// the browser will be created later
		//------------------------------------
		_generationResult = result;

		// Do nothing if this page is not visible
		//---------------------------------------
		if( getEditor().getPageIndex() != 0 ) {
			_bUpdateRequired = true;
			return;
		}

		// Create the browser and browser manager if not yet created
		//----------------------------------------------------------
		if( _browser == null )
			createBrowser();
		
		// Do an alignment test if no editor has done so
		//----------------------------------------------
		if( bALIGNMENT_DONE == false ){
			// Attempt to start the alignment test
			// If unsuccessful, cancel the alignment test and continue to part B
			//------------------------------------------------------------------
			boolean bStarted = alignBrowserAndOverlayBegin();

			if( bStarted == false ){
				bALIGNMENT_DONE = true;
				updateBrowserFullPartB();
			}
		} else {
			if ( result == null || result.hasError() ) {
				_bFullRefresh = true;
			}
			updateBrowserFullPartB();
		}
	}
	
	/**
	 * 
	 */
	protected void updateBrowserFullPartB(){
		if( _browser != null ) {
			// Turn on the busy indicator
			// It is turned off in the completed method
			//-----------------------------------------
			_animatedBusyPainter.animationStart();

			// Remove all the widgets
			//-----------------------
			_widgetManager.removeAllWidgets();

			// If there are no generation errors, set the design mode on
			// This will show the overlay, otherwise the overlay is hidden
			//------------------------------------------------------------
			if( _generationResult != null ) {
				int iNumberOfGenerationErrors = _generationResult.getNumGenErrors();
				setDesignMode( iNumberOfGenerationErrors == 0 );
			}

			// Update the browser
			//-------------------
			if( _browserManager != null ) {
				_browserManager.refreshBrowser(_bFullRefresh);
				_bFullRefresh = false;
			}
		}

		_bUpdateRequired = false;
	}

	/**
	 * Updates the browser if the preview page is visible, and a property value has changed.
	 */
	protected void updateBrowserIncremental( WorkingCopyGenerationResult result ) {

		// Save the generation results in case
		// the browser will be created later
		//------------------------------------
		_generationResult = result;

		// Do nothing if there is no RUI handler
		//--------------------------------------
		if( _editor.isRuiHandler() == false )
			return;

		// Do nothing if this page is not visible
		//---------------------------------------
		if( getEditor().getPageIndex() != 0 ) {
			_bUpdateRequired = true;
			return;
		}
		
		

		// Create the browser and browser manager if not yet created
		//----------------------------------------------------------
		if( _browser == null )
			createBrowser();

		// Remove all the widgets
		//-----------------------
		_widgetManager.removeAllWidgets();

		// If there are no generation errors, set the design mode on
		// This will show the overlay, otherwise the overlay is hidden
		//------------------------------------------------------------
		int iNumberOfGenerationErrors = result.getNumGenErrors();
		setDesignMode( iNumberOfGenerationErrors == 0 );

		// Update the browser
		//-------------------
		if( _browserManager != null )
			_browserManager.refreshBrowserIncremental();

		_bUpdateRequired = false;
		
		// If has error, go to source page
		if( result.hasError() ){
			updateBrowser(result);
			getEditor().showSourcePage();
		}
	}
	
	/**
	 * Updates the browser if the preview page is visible, and a property value has changed.
	 */
	protected void changeProperty( WorkingCopyGenerationResult result, WidgetPart widget, String property, String value, int totalCharactersChanged ) {

		// Save the generation results in case
		// the browser will be created later
		//------------------------------------
		_generationResult = result;

		// Do nothing if there is no RUI handler
		//--------------------------------------
		if( _editor.isRuiHandler() == false )
			return;

		// Do nothing if this page is not visible
		//---------------------------------------
		if( getEditor().getPageIndex() != 0 ) {
			_bUpdateRequired = true;
			return;
		}

		// Create the browser and browser manager if not yet created
		//----------------------------------------------------------
		if( _browser == null )
			createBrowser();

		// Remove all the widgets
		//-----------------------
		_widgetManager.removeAllWidgets();

		// If there are no generation errors, set the design mode on
		// This will show the overlay, otherwise the overlay is hidden
		//------------------------------------------------------------
		int iNumberOfGenerationErrors = result.getNumGenErrors();
		setDesignMode( iNumberOfGenerationErrors == 0 );

		// Update the browser
		//-------------------
		if( _browserManager != null )
			_browserManager.changeProperty(widget, property, value, totalCharactersChanged);

		_bUpdateRequired = false;
	}
	
	/**
	 * Updates the browser if the preview page is visible, and a widget has been moved.
	 */
	protected void moveWidget( WorkingCopyGenerationResult result, WidgetPart widget, WidgetPart targetParent, int oldIndex, int newIndex, int[] charactersChanged ) {

		// Save the generation results in case
		// the browser will be created later
		//------------------------------------
		_generationResult = result;

		// Do nothing if there is no RUI handler
		//--------------------------------------
		if( _editor.isRuiHandler() == false )
			return;

		// Do nothing if this page is not visible
		//---------------------------------------
		if( getEditor().getPageIndex() != 0 ) {
			return;
		}

		// Create the browser and browser manager if not yet created
		//----------------------------------------------------------
		if( _browser == null )
			createBrowser();

		// Remove all the widgets
		//-----------------------
		_widgetManager.removeAllWidgets();

		// If there are no generation errors, set the design mode on
		// This will show the overlay, otherwise the overlay is hidden
		//------------------------------------------------------------
		int iNumberOfGenerationErrors = result.getNumGenErrors();
		setDesignMode( iNumberOfGenerationErrors == 0 );

		// Update the browser
		//-------------------
		if( _browserManager != null )
			_browserManager.moveWidget(widget, targetParent, oldIndex, newIndex, charactersChanged);

		_bUpdateRequired = false;
	}
	
	/**
	 * Updates the browser if the preview page is visible, and a widget has been deleted.
	 */
	protected void deleteWidget( WorkingCopyGenerationResult result, WidgetPart widget, int totalCharactersRemoved ) {

		// Save the generation results in case
		// the browser will be created later
		//------------------------------------
		_generationResult = result;

		// Do nothing if there is no RUI handler
		//--------------------------------------
		if( _editor.isRuiHandler() == false )
			return;

		// Do nothing if this page is not visible
		//---------------------------------------
		if( getEditor().getPageIndex() != 0 ) {
			_bUpdateRequired = true;
			return;
		}

		// Create the browser and browser manager if not yet created
		//----------------------------------------------------------
		if( _browser == null )
			createBrowser();

		// Remove all the widgets
		//-----------------------
		_widgetManager.removeAllWidgets();

		// If there are no generation errors, set the design mode on
		// This will show the overlay, otherwise the overlay is hidden
		//------------------------------------------------------------
		int iNumberOfGenerationErrors = result.getNumGenErrors();
		setDesignMode( iNumberOfGenerationErrors == 0 );

		// Update the browser
		//-------------------
		if( _browserManager != null )
			_browserManager.deleteWidget( widget, totalCharactersRemoved );

		_bUpdateRequired = false;
	}

	/**
	 * Sets the values of the browser size controls to the preference values.
	 * The values of the sliders are set to the browser default sizes.
	 * Whenever a user modifies a browser size preference, several preference values may change,
	 * so this may be called several times, once for each change.  Therefore, the preference values are compared to
	 * the values in the sliders first.  The value returned indicates whether the preference values
	 * are different from the slider values.  This allows a caller to update the browser widget only
	 * if necessary.
	 */
	protected boolean updateBrowserSizeControls() {
		// Check for equality, preference values are the same as the slider values
		//------------------------------------------------------------------------
		int iXMin = EvPreferences.getInt( EvConstants.PREFERENCE_BROWSER_SIZE_MINIMUM_WIDTH );
		boolean bXMin = iXMin == _sliderHorizontal.getMinimum();

		int iXDef = EvPreferences.getInt( EvConstants.PREFERENCE_BROWSER_SIZE_DEFAULT_WIDTH );
		boolean bXDef = iXDef == _sliderHorizontal.getSelection();

		int iXMax = EvPreferences.getInt( EvConstants.PREFERENCE_BROWSER_SIZE_MAXIMUM_WIDTH );
		boolean bXMax = iXMax == _sliderHorizontal.getMaximum() - SLIDER_THUMB;
		
		int iYMin = EvPreferences.getInt( EvConstants.PREFERENCE_BROWSER_SIZE_MINIMUM_HEIGHT );
		boolean bYMin = iYMin == _sliderVertical.getMinimum();

		int iYDef = EvPreferences.getInt( EvConstants.PREFERENCE_BROWSER_SIZE_DEFAULT_HEIGHT );
		boolean bYDef = iYDef == _sliderVertical.getSelection();

		int iYMax = EvPreferences.getInt( EvConstants.PREFERENCE_BROWSER_SIZE_MAXIMUM_HEIGHT );
		boolean bYMax = iYMax == _sliderVertical.getMaximum() - SLIDER_THUMB;

		// All equal, return nothing changed
		//----------------------------------
		if( bXMin && bXDef && bXMax && bYMin && bYDef && bYMax )
			return false;

		_dimSize.width = EvPreferences.getInt( EvConstants.PREFERENCE_BROWSER_SIZE_DEFAULT_WIDTH );
		_dimSize.height = EvPreferences.getInt( EvConstants.PREFERENCE_BROWSER_SIZE_DEFAULT_HEIGHT );

		_sliderHorizontal.setValues( _dimSize.width, EvPreferences.getInt( EvConstants.PREFERENCE_BROWSER_SIZE_MINIMUM_WIDTH ), EvPreferences.getInt( EvConstants.PREFERENCE_BROWSER_SIZE_MAXIMUM_WIDTH ) + SLIDER_THUMB, SLIDER_THUMB, 1, SLIDER_PAGE );
		_sliderVertical.setValues( _dimSize.height, EvPreferences.getInt( EvConstants.PREFERENCE_BROWSER_SIZE_MINIMUM_HEIGHT ), EvPreferences.getInt( EvConstants.PREFERENCE_BROWSER_SIZE_MAXIMUM_HEIGHT ) + SLIDER_THUMB, SLIDER_THUMB, 1, SLIDER_PAGE );

		_buttonResetToDefaultSize.setEnabled( false );

		updateBrowserSizeControlsTooltips();

		return true;
	}

	/**
	 * Enables or disables the reset to default size button.
	 */
	protected void updateBrowserSizeControlsDefaultButton() {
		boolean bNotDefaultX = _dimSize.width != EvPreferences.getInt( EvConstants.PREFERENCE_BROWSER_SIZE_DEFAULT_WIDTH );
		boolean bNotDefaultY = _dimSize.height != EvPreferences.getInt( EvConstants.PREFERENCE_BROWSER_SIZE_DEFAULT_HEIGHT );

		_buttonResetToDefaultSize.setEnabled( bNotDefaultX || bNotDefaultY );
	}

	/**
	 * Sets the tooltip text for the browser size sliders to the current browser width and height.
	 */
	protected void updateBrowserSizeControlsTooltips() {
		//@bd2a Start
		if(Locale.getDefault().toString().toLowerCase().indexOf("ar") != -1) {
			_sliderHorizontal.setToolTipText( Tooltips.NL_Browser_width + " " + Integer.toString( _dimSize.width ) );
			_sliderVertical.setToolTipText( Tooltips.NL_Browser_height + " " + Integer.toString( _dimSize.height ) );
		}else{ //@bd2a End
		_sliderHorizontal.setToolTipText( Tooltips.NL_Browser_width + "\n" + Integer.toString( _dimSize.width ) );
		_sliderVertical.setToolTipText( Tooltips.NL_Browser_height + "\n" + Integer.toString( _dimSize.height ) );
		} //@bd2a
	}

	/**
	 * Called whenever the 'Hide browser size controls' design page toolbar item is toggled.
	 */
	protected void updateBrowserSizeControlsVisible( boolean bVisible ) {
		_bBrowserSizeControlsVisible = bVisible;
		_compositeLayout.layout();
	}

	/**
	 * Declared in SelectionListener
	 */
	public void widgetDefaultSelected( SelectionEvent e ) {
	}

	/**
	 * Declared in DisposeListener.
	 * Disposes of the browser screen capture image.
	 */
	public void widgetDisposed( DisposeEvent e ) {
		if( _captureInfo != null ) {
			if( _captureInfo.imageBrowser != null && _captureInfo.imageBrowser.isDisposed() == false ) {
				_captureInfo.imageBrowser.dispose();
				_captureInfo.imageBrowser = null;
			}
		}
	}

	/**
	 * The widget manager has a new widget set.  The overlay and content outline are notified.
	 */
	public void widgetsChanged() {
		computeWidgetIDs();
		
		_animatedBusyPainter.getDisplay().asyncExec( new Runnable() {
			public void run() {
				_animatedBusyPainter.animationStop();
				_overlay.widgetsChanged();
				_contentOutline.update();
			}
		} );
	}

	/**
	 * Declared in SelectionListener
	 */
	public void widgetSelected( SelectionEvent event ) {
		if( event.widget == _sliderHorizontal ) {
			_dimSize.width = _sliderHorizontal.getSelection();
			_compositeDesignArea.setBounds( new Rectangle( 0, 0, _dimSize.width, _dimSize.height ) );
			_overlay.setBounds( new Rectangle( 0, 0, _dimSize.width, _dimSize.height ) );
			_compositeBrowser.setBounds( new Rectangle( ptBROWSER_LOCATION.x, ptBROWSER_LOCATION.y, _dimSize.width, _dimSize.height ) );

			updateBrowserSizeControlsDefaultButton();
			updateBrowserSizeControlsTooltips();
		}

		else if( event.widget == _sliderVertical ) {
			_dimSize.height = _sliderVertical.getSelection();
			_compositeDesignArea.setBounds( new Rectangle( 0, 0, _dimSize.width, _dimSize.height ) );
			_overlay.setBounds( new Rectangle( 0, 0, _dimSize.width, _dimSize.height ) );
			_compositeBrowser.setBounds( new Rectangle( ptBROWSER_LOCATION.x, ptBROWSER_LOCATION.y, _dimSize.width, _dimSize.height ) );

			updateBrowserSizeControlsDefaultButton();
			updateBrowserSizeControlsTooltips();
		}

		// The default button is only enabled if the current slider position is not the default
		//-------------------------------------------------------------------------------------
		else if( event.widget == _buttonResetToDefaultSize ) {
			_dimSize.width = EvPreferences.getInt( EvConstants.PREFERENCE_BROWSER_SIZE_DEFAULT_WIDTH );
			_dimSize.height = EvPreferences.getInt( EvConstants.PREFERENCE_BROWSER_SIZE_DEFAULT_HEIGHT );
			_sliderHorizontal.setSelection( _dimSize.width );
			_sliderVertical.setSelection( _dimSize.height );
			_buttonResetToDefaultSize.setEnabled( false );

			updateBrowserSizeControlsTooltips();

			_compositeDesignArea.setBounds( new Rectangle( 0, 0, _dimSize.width, _dimSize.height ) );
			_overlay.setBounds( new Rectangle( 0, 0, _dimSize.width, _dimSize.height ) );
			_compositeBrowser.setBounds( new Rectangle( ptBROWSER_LOCATION.x, ptBROWSER_LOCATION.y, _dimSize.width, _dimSize.height ) );
		}

		// Lazy creation and lazy update of the browser
		//---------------------------------------------
		else if( event.widget instanceof CTabFolder ) {
			CTabFolder tabFolder = (CTabFolder)event.widget;
			if( tabFolder.getSelectionIndex() != 0 || _editor.isRuiHandler() == false  )
				return;

			// If the browser has not been created, create the browser
			// and update it with the last generation results
			//--------------------------------------------------------
			if( _browser == null || _bUpdateRequired == true )
				updateBrowser( _generationResult );
		}		
	}

	/**
	 * 
	 */
	public void widgetSelectedFromDesignCanvas( WidgetPart widget ) {
		_editor.widgetSelectedFromDesignPage( widget );
	}

	/**
	 * Paints the two pixel high focus composite to indicate that the design area has focus.
	 */
	public void paintControl( PaintEvent e ) {
		Rectangle rectBounds = _compositeFocus.getBounds();
		e.gc.fillRectangle( rectBounds );
		
		if( _overlay.isFocusControl() == true ) {
			Point ptSize = _compositeFocus.getSize();
			e.gc.setLineWidth( 1 );
			e.gc.setForeground( _compositeFocus.getDisplay().getSystemColor( SWT.COLOR_DARK_GRAY ) );
			e.gc.drawLine( 0, 0, ptSize.x, 0 );
		}
	}
}
