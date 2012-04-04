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
package org.eclipse.edt.ide.rui.visualeditor.internal.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

/**
 * This class draws a set of sequential images stored in a single gif file.
 * A timer thread is used to trigger the drawing of each image. 
 */
public class AnimatedBusyPainter extends Composite implements DisposeListener {

	/**
	 * A simple thread that calls a tick method every 10th of a second
	 */
	protected class AnimationTimer extends Thread {
		public boolean	_bRun	= true;

		public AnimationTimer() {
			super( "Image Animator" );
		}

		public void run() {
			while( _bRun == true ) {
				try {
					tick();
					Thread.sleep( 250 );
				}
				catch( InterruptedException ex ) {
					break;
				}
			}
		}
	}

	// There are 16 images in the busy image file.
	// They are loaded once and used by all instances.
	// an instance count is incremented during instance construction
	// and decremented when instance disposal occurs.
	// The images are disposed of when the count reaches zero
	//--------------------------------------------------------------
	protected GC				_gc				= null;
	protected int				_iCurrentIndex	= 0;
	protected AnimationTimer	_timer			= null;

	protected static Color		_color1			= null;
	protected static Color		_color2			= null;
	protected static Color		_color3			= null;
	protected static Point[]	_ptLOCATIONS	= null; // Circle locations

	/**
	 * This class is a composite that paints the multiple images onto itself.
	 */
	public AnimatedBusyPainter( Composite compositeParent ) {
		super( compositeParent, SWT.NULL );
		
		// Clockwise from top
		//-------------------
		if( _ptLOCATIONS == null ){
			_ptLOCATIONS = new Point[8];
			
			_ptLOCATIONS[ 0] = new Point(  6, 0 );
			_ptLOCATIONS[ 1] = new Point( 10, 2 );
			_ptLOCATIONS[ 2] = new Point( 12, 6 );
			_ptLOCATIONS[ 3] = new Point( 10, 10 );
			_ptLOCATIONS[ 4] = new Point(  6, 12 );
			_ptLOCATIONS[ 5] = new Point(  2, 10 );
			_ptLOCATIONS[ 6] = new Point(  0, 6 );
			_ptLOCATIONS[ 7] = new Point(  2, 2 );
		}

		// Lightest to darkest
		//--------------------
		if( _color1 == null )
			_color1 = getDisplay().getSystemColor( SWT.COLOR_GRAY );

		if( _color2 == null )
			_color2 = getDisplay().getSystemColor( SWT.COLOR_DARK_GRAY );

		if( _color3 == null )
			_color3 = getDisplay().getSystemColor( SWT.COLOR_BLACK );

		// Create a graphics context to paint onto this composite
		//-------------------------------------------------------
		_gc = new GC( this );

		// Dispose of the graphics context when finished
		//----------------------------------------------
		addDisposeListener( this );
	}

	/**
	 * This is called by the design page when the browser is about to be fully (not partially) refreshed, and by the preview page when its browser is refreshing.
	 */
	public void animationStart() {
		if( _gc == null || _gc.isDisposed() )
			return;

		if( _timer == null ) {
			setVisible( true );
			
			_timer = new AnimationTimer();
			_timer.start();
		}
	}

	/**
	 * Stops the timer.
	 * This is called by the design and preview pages when the browser has completed its refresh.
	 */
	public void animationStop() {
		if( _gc == null || _gc.isDisposed() )
			return;

		setVisible( false );
		stopTimer();
	}

	/**
	 * 
	 */
	protected void paintCircle( int iIndex, Color colorCircle, Color colorCenter ){
		
		Point ptLocation = _ptLOCATIONS[ iIndex ];
		
		// Top to bottom, left to right
		//-----------------------------
		_gc.setForeground( colorCircle );
		_gc.drawPoint( ptLocation.x + 1, ptLocation.y + 0 );
		_gc.drawPoint( ptLocation.x + 2, ptLocation.y + 0 );
		_gc.drawPoint( ptLocation.x + 0, ptLocation.y + 1 );
		_gc.drawPoint( ptLocation.x + 3, ptLocation.y + 1 );
		_gc.drawPoint( ptLocation.x + 0, ptLocation.y + 2 );
		_gc.drawPoint( ptLocation.x + 3, ptLocation.y + 2 );
		_gc.drawPoint( ptLocation.x + 1, ptLocation.y + 3 );
		_gc.drawPoint( ptLocation.x + 2, ptLocation.y + 3 );

		_gc.setForeground( colorCenter );
		_gc.drawPoint( ptLocation.x + 1, ptLocation.y + 1 );
		_gc.drawPoint( ptLocation.x + 2, ptLocation.y + 1 );
		_gc.drawPoint( ptLocation.x + 1, ptLocation.y + 2 );
		_gc.drawPoint( ptLocation.x + 2, ptLocation.y + 2 );
	}
	
	/**
	 * The timer is stopped.
	 */
	protected void stopTimer() {
		if( _timer != null )
			_timer._bRun = false;

		_timer = null;
	}

	/**
	 * Called by the timer thread to call an equivalent asynchronous method in the UI thread.
	 */
	protected void tick() {
		if ( _gc == null || _gc.isDisposed() )
			return;
		
		getDisplay().asyncExec( new Runnable() {
			public void run() {
				tickAsync();
			}
		} );
	}

	/**
	 * This method is called after the tick method has been called.
	 * The graphics context is painted with the next image.
	 */
	protected void tickAsync() {
		if ( _gc == null || _gc.isDisposed() )
			return;
		
		_gc.setBackground( getBackground() );
		_gc.fillRectangle( 0, 0, 16, 16 );
		
		// Clockwise: darkest
		//-------------------
		for( int i=0; i<_ptLOCATIONS.length; ++i )
			if( i != _iCurrentIndex )
				paintCircle( i, _color2, _color3 );
		
		// Lightest
		//---------
		paintCircle( _iCurrentIndex, _color1, _color1 );

		// Increment the index
		//--------------------
		if( ++_iCurrentIndex >= _ptLOCATIONS.length )
			_iCurrentIndex = 0;

		// Between lightest and darkest
		//-----------------------------
		paintCircle( _iCurrentIndex, _color1, _color2 );
	}

	/**
	 * Declared in DisposeListener.
	 */
	public synchronized void widgetDisposed( DisposeEvent e ) {
		// Dispose the graphics context that paints this composite
		//--------------------------------------------------------
		if( _gc != null && _gc.isDisposed() == false )
			_gc.dispose();
	}
}
