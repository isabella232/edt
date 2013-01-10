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
package org.eclipse.edt.ide.rui.visualeditor.internal.util;

import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvDesignOverlay;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;


/**
 * This class scrolls the scrolled composite if the first mouse button is down, and the mouse
 * is moved, and the mouse is just within the borders, or is outside of the overlay area.  This happens whether something is
 * being dragged or not.  The speed of the scroll is proportional to how far away the mouse is
 * from the overlay area.  When the scrolling occurs, the overlay is notified since the mouse
 * position has moved relative to the overlay even though the mouse may not have physically moved.
 * This allows the overlay to draw the widget being dragged at the right location relative to overlay.
 * Scrolling can also happen in two different directions simultaneously.  This class has public methods
 * that are called by the design area overlay, to control scrolling whenever a palette item is being dragged
 * over the design area overlay.  Scrolling in this case only occurs if the mouse is within the outside
 * margin of the design overlay.
 */
public class ScrolledCompositeScroller implements MouseListener, MouseMoveListener {

	protected class ScrollTimer extends Thread {
		public boolean	_bRun	= true;

		public ScrollTimer() {
			super( "Visual Editor Scroll Timer" );
		}

		public void run() {
			while( _bRun == true ) {
				try {
					tick();
					Thread.sleep( 10 );
					//					Thread.yield();
				}
				catch( InterruptedException ex ) {
					break;
				}
			}
		}
	}

	protected final static int	MARGIN					= 32;
	protected final static int	SCROLL_INCREMENT_FACTOR	= 10;

	protected boolean			_bButtonDown			= false;
	protected EvDesignOverlay	_overlay				= null;
	protected Point				_ptContentSize			= null;
	protected Point				_ptMouse				= new Point( 0, 0 );
	protected Point				_ptOrigin				= null;
	protected Rectangle			_rectClient				= null;
	protected ScrolledComposite	_scrolledComposite		= null;
	protected ScrollTimer		_timer					= null;

	/**
	 * Takes the scrolled composite to control, and the overlay to notify when we scroll the scrolled composite.
	 */
	public ScrolledCompositeScroller( ScrolledComposite scrolledComposite, EvDesignOverlay overlay ) {
		_scrolledComposite = scrolledComposite;
		_overlay = overlay;
		_timer = new ScrollTimer();

		// Add listeners to the content
		//-----------------------------
		_overlay.addMouseListener( this );
		_overlay.addMouseMoveListener( this );
	}

	/**
	 * This is either called when the mouse is down, or a drag of a palette item enters the design area.
	 */
	public void activate() {
		_bButtonDown = true;
		_ptOrigin = _scrolledComposite.getOrigin();
		_ptContentSize = _overlay.getSize();
		_rectClient = _scrolledComposite.getClientArea();
	}

	/**
	 * Starts the timer if the mouse is down and outside of the content area.
	 * This is either called when the mouse is moved, or a palette item is being dragged over the design area.
	 */
	public void autoScroll( int iMouseX, int iMouseY ) {
		if( _bButtonDown == false )
			return;

		// Remember the mouse position for the tick method
		//------------------------------------------------
		_ptMouse.x = iMouseX;
		_ptMouse.y = iMouseY;

		// Determine if scrolling should start by determining
		// whether the mouse is outside, or just within the
		// outer margins of the overlay area.
		// Treat the left/right and top/bottom separately
		// so that we can scroll in both directions simultaneously
		//--------------------------------------------------------
		boolean bStartTimer = false;

		// Left
		//-----
		if( _ptMouse.x < _ptOrigin.x + MARGIN ) {
			if( _ptOrigin.x > 0 ) {
				bStartTimer = true;
			}
		}

		// Right
		//------
		else if( _ptMouse.x > _ptOrigin.x + _rectClient.width - MARGIN ) {
			if( _ptOrigin.x + _rectClient.width < _ptContentSize.x ) {
				bStartTimer = true;
			}
		}

		// Top
		//----
		if( _ptMouse.y < _ptOrigin.y + MARGIN ) {
			if( _ptOrigin.y > 0 ) {
				bStartTimer = true;
			}
		}

		// Bottom
		//-------
		else if( _ptMouse.y > _ptOrigin.y + _rectClient.height - MARGIN ) {
			if( _ptOrigin.y + _rectClient.height < _ptContentSize.y ) {
				bStartTimer = true;
			}
		}

		// Start the timer if the mouse
		// is not within the overlay area
		//-------------------------------
		if( bStartTimer == true && _timer == null ) {
			_timer = new ScrollTimer();
			_timer.start();
		}

		// Stop the timer if the mouse
		// is within of the overlay area
		//-----------------------------------
		else if( bStartTimer == false && _timer != null ) {
			stopTimer();
		}
	}

	/**
	 * Stops the timer.
	 * This is either called when the mouse is released, or a drag of a palette item exits the design area, or is dropped.
	 */
	public void deactivate() {
		stopTimer();
		_bButtonDown = false;
	}

	/**
	 * Declared in MouseListener.  Does nothing.
	 */
	public void mouseDoubleClick( MouseEvent event ) {
	}

	/**
	 * Declared in MouseListener.  Remembers the positions of things for the mouseMove and tick methods.
	 */
	public void mouseDown( MouseEvent event ) {
		if( event.button != 1 )
			return;

		activate();
	}

	/**
	 * Declared in MouseMoveListener.  
	 * The mouse has moved.  Do the automatic scrolling.
	 */
	public void mouseMove( MouseEvent event ) {
		autoScroll( event.x, event.y );
	}

	/**
	 * Declared in MouseListener.  
	 * The timer is stopped if button 1 is is released.
	 */
	public void mouseUp( MouseEvent event ) {
		if( event.button != 1 )
			return;

		deactivate();
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
	 * Called by the timer thread.  We in turn call the equivalent method in the UI thread.
	 */
	protected void tick() {
		_scrolledComposite.getDisplay().asyncExec( new Runnable() {
			public void run() {
				tickAsync();
			}
		} );
	}

	/**
	 * This method is called in the UI thread so that it can access the scrolled composite.
	 * This method computes the amount of scrolling to be done in the x and y directions.
	 * If the scrolled composite is scrolled, the amount is dependent on how far the mouse
	 * is away from the four sides of the scrolled composite's client area.
	 * Scrolling can be in two directions simultaneously.
	 */
	protected void tickAsync() {
		// Prevent scrolling after the mouse has been released
		// Sometimes scrolling continues to happen because it is asynchronous
		//-------------------------------------------------------------------
		if( _bButtonDown == false )
			return;

		_ptOrigin = _scrolledComposite.getOrigin();

		// Compute a new origin for the overlay within the scrolled composite.
		// Treat left/right separately from top/bottom
		// to allow for scrolling in two directions simultaneously
		//--------------------------------------------------------------------
		Point ptOriginLeftRight = null;

		// Left
		//-----
		if( _ptMouse.x - MARGIN < _ptOrigin.x ) {
			if( _ptOrigin.x > 0 ) {
				int iScrollAmount = ( _ptOrigin.x - ( _ptMouse.x - MARGIN ) ) / SCROLL_INCREMENT_FACTOR;
				ptOriginLeftRight = new Point( Math.max( _ptOrigin.x - iScrollAmount, 0 ), _ptOrigin.y );
			}
		}

		// Right
		//------
		else if( _ptMouse.x + MARGIN > _ptOrigin.x + _rectClient.width ) {
			if( _ptOrigin.x + _rectClient.width < _ptContentSize.x ) {
				int iScrollAmount = ( _ptMouse.x + MARGIN - ( _ptOrigin.x + _rectClient.width ) ) / SCROLL_INCREMENT_FACTOR;
				ptOriginLeftRight = new Point( Math.max( _ptOrigin.x + iScrollAmount, 0 ), _ptOrigin.y );
			}
		}

		Point ptOriginTopBottom = null;

		// Top
		//----
		if( _ptMouse.y - MARGIN < _ptOrigin.y ) {
			if( _ptOrigin.y > 0 ) {
				int iScrollAmount = ( _ptOrigin.y - ( _ptMouse.y - MARGIN ) ) / SCROLL_INCREMENT_FACTOR;
				ptOriginTopBottom = new Point( _ptOrigin.x, Math.max( _ptOrigin.y - iScrollAmount, 0 ) );
			}
		}

		// Bottom
		//-------
		else if( _ptMouse.y + MARGIN > _ptOrigin.y + _rectClient.height ) {
			if( _ptOrigin.y + _rectClient.height < _ptContentSize.y ) {
				int iScrollAmount = ( _ptMouse.y + MARGIN - ( _ptOrigin.y + _rectClient.height ) ) / SCROLL_INCREMENT_FACTOR;
				ptOriginTopBottom = new Point( _ptOrigin.x, Math.max( _ptOrigin.y + iScrollAmount, 0 ) );
			}
		}

		// Combine left/right with top/bottom
		//-----------------------------------
		Point ptOriginNew = null;

		if( ptOriginLeftRight != null && ptOriginTopBottom != null )
			ptOriginNew = new Point( ptOriginLeftRight.x, ptOriginTopBottom.y );

		else if( ptOriginLeftRight != null )
			ptOriginNew = ptOriginLeftRight;

		else if( ptOriginTopBottom != null )
			ptOriginNew = ptOriginTopBottom;

		// Scroll the scrolled composite to the new position
		//--------------------------------------------------
		if( ptOriginNew != null ) {
			_scrolledComposite.setOrigin( ptOriginNew );

			// As the origin moves, the mouse position relative to
			// it changes even though the mouse is stationary
			//----------------------------------------------------
			_ptMouse.x += ( ptOriginNew.x - _ptOrigin.x );
			_ptMouse.y += ( ptOriginNew.y - _ptOrigin.y );

			// Notify the overlay that the mouse has moved
			// relative to the overlay
			//--------------------------------------------
			_overlay.mouseMoved( _ptMouse.x, _ptMouse.y );
		}

		else {
			stopTimer();
		}
	}
}
