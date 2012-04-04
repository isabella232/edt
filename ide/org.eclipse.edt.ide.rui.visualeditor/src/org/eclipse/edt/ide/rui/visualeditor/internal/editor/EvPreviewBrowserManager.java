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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.edt.ide.rui.server.AbstractPreviewContext;
import org.eclipse.edt.ide.rui.server.EvEditorProvider;
import org.eclipse.edt.ide.rui.server.EvServer;
import org.eclipse.edt.ide.rui.server.IServerListener;
import org.eclipse.edt.ide.rui.server.PreviewContext;
import org.eclipse.edt.ide.rui.server.PreviewPaneContentProvider;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPart;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;


public class EvPreviewBrowserManager implements IServerListener, ProgressListener {

	protected boolean			_bRefreshing	= false;
	protected Browser			_browser		= null;
	protected AbstractPreviewContext	_context		= null;
	protected Integer			_intKey			= null;
	protected EvPreviewPage		_pagePreview	= null;
	protected String			_strURL			= null;

	/**
	 * Constructor used by subclasses.
	 */
	public EvPreviewBrowserManager( Browser browser, String strURL, EvEditorProvider editorProvider ) {
		_browser = browser;
		_intKey = getContextKey();
		_strURL = strURL;

		appendContextKey();

		_context = createContext( editorProvider );
		EvServer.getInstance().addContext( _context );
	}

	/**
	 * Constructor used by the preview page.
	 */
	public EvPreviewBrowserManager( Browser browser, String strURL, EvPreviewPage pagePreview, EvEditorProvider editorProvider ) {

		_browser = browser;
		_intKey = getContextKey();
		_strURL = strURL;
		_pagePreview = pagePreview;

		appendContextKey();

		_context = createContext( editorProvider );
		EvServer.getInstance().addContext( _context );
	}

	/**
	 * Obsolete. Does nothing.
	 */
	public void acceptWidgetPositions( String positionInfo ) {
	}

	/**
	 * Appends a context key to the URL string.
	 */
	protected void appendContextKey() {
		if( _strURL.indexOf( "?" ) == -1 )
			_strURL = _strURL + "?contextKey=" + _intKey;

		else
			_strURL = _strURL + "&contextKey=" + _intKey;
	}

	/**
	 * Declared in ProgressListener.  Does nothing.
	 */
	public void changed( ProgressEvent event ) {
	}

	/**
	 * Declared in ProgressListener.
	 * Called by the browser when the browser has completed refreshing its web page.
	 * Redraws the browser.
	 */
	public void completed( ProgressEvent event ) {
		if( _bRefreshing == false )
			return;

		_browser.removeProgressListener( this );
		_browser.setRedraw( true );
		_bRefreshing = false;
	}

	/**
	 * Creates a preview context.
	 */
	protected AbstractPreviewContext createContext( EvEditorProvider editorProvider ) {
		return new PreviewContext( _strURL, _intKey, new PreviewPaneContentProvider( editorProvider ), this );
	}

	/**
	 * Creates and returns a new context key.
	 */
	protected Integer getContextKey() {
		return new Integer( EvServer.getInstance().generateContextKey() );
	}

	/**
	 * Called by setBrowserUrl.
	 */
	protected Object getField( Object object, String fieldName ) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field field = object.getClass().getDeclaredField( fieldName );
		field.setAccessible( true );
		return field.get( object );
	}

	/**
	 * Returns the URL string. 
	 */
	public String getURL() {
		return _strURL;
	}

	/**
	 * Asynchronously refreshes the browser with the current url.
	 * If no generated file exists, then a blank page is displayed.
	 */
	public synchronized void refreshBrowser(boolean fullRefresh) {
		if( _bRefreshing == true || _browser == null )
			return;

		_bRefreshing = true;

		final String strURLFinal = _strURL;

		Display display = _browser.getDisplay();

		if( !display.isDisposed() ) {
			if ( fullRefresh ) {
				_browser.addProgressListener( this );
	
				display.asyncExec( new Runnable() {
					public void run() {
						// Prevent the browser from redrawing until the
						// (design or preview) page's completed method is called
						//------------------------------------------------------
						_browser.setRedraw( false );
						setBrowserUrl( strURLFinal );
					}
				} );
			} else {
				_browser.execute( "if (window.egl) { egl.evTerminateReloadHandler(); } else { document.location = \"" + strURLFinal + "\";}" );
				_bRefreshing = false;
			}
		}
	}

	/**
	 * Asynchronously refreshes a part of the browser page.
	 */
	public synchronized void refreshBrowserIncremental() {
		if( _browser == null )
			return;
		
		final String strURLFinal = _strURL;

		Display display = _browser.getDisplay();

		if( !display.isDisposed() ) {
			_browser.addProgressListener( this );

			display.asyncExec( new Runnable() {
				public void run() {
					// Give incremental content to the browser
					//----------------------------------------
//					EvServer.getInstance().refreshBrowserIncremental(_context);
					
					//execute JavaScript directly instead of putting it in the event loop
					_browser.execute( "if (window.egl) { egl.evTerminateReloadHandler(); } else { document.location = \"" + strURLFinal + "\";}" );

				}
			} );
		}
	}

	/**
	 * dispatch the click event.
	 */
	public void doWidgetClick(final Point point) {
		if( _browser == null )
			return;
		
		EvServer.getInstance().doWidgetClick(_context, point.x, point.y);
	}
	
	/**
	 * Asynchronously refreshes a part of the browser page for property change
	 */
	public synchronized void changeProperty( final WidgetPart widget, final String property, final String value, final int totalCharactersChanged ) {
		if( _browser == null )
			return;
		
		Display display = _browser.getDisplay();

		if( !display.isDisposed() ) {

			display.asyncExec( new Runnable() {
				public void run() {
					// Give incremental content to the browser
					//----------------------------------------
					EvServer.getInstance().changeProperty(_context, widget.getBounds().x, widget.getBounds().y, widget.getBounds().width, widget.getBounds().height, property, value, totalCharactersChanged);
				}
			} );
		}
	}
	
	/**
	 * Asynchronously refreshes a part of the browser page for widget move
	 */
	public synchronized void moveWidget( final WidgetPart widget, final WidgetPart targetParent, final int oldIndex, final int newIndex, final int[] charactersChanged ) {
		if( _browser == null )
			return;
		
		Display display = _browser.getDisplay();

		if( !display.isDisposed() ) {

			display.asyncExec( new Runnable() {
				public void run() {
					// Give incremental content to the browser
					//----------------------------------------
					EvServer.getInstance().moveWidget(_context, widget.getBounds().x, widget.getBounds().y, widget.getBounds().width, 
							widget.getBounds().height, targetParent.getBounds().x, targetParent.getBounds().y, 
							targetParent.getBounds().width, targetParent.getBounds().height, oldIndex, newIndex, charactersChanged);
				}
			} );
		}
	}
	
	/**
	 * Asynchronously refreshes a part of the browser page for widget delete
	 */
	public synchronized void deleteWidget( final WidgetPart widget, final int totalCharactersRemoved ) {
		if( _browser == null )
			return;
		
		Display display = _browser.getDisplay();

		if( !display.isDisposed() ) {

			display.asyncExec( new Runnable() {
				public void run() {
					// Give incremental content to the browser
					//----------------------------------------
					EvServer.getInstance().deleteWidget(_context, widget.getBounds().x, widget.getBounds().y, widget.getBounds().width,	widget.getBounds().height, totalCharactersRemoved);
				}
			} );
		}
	}
	
	/**
	 * Asynchronously refreshes a part of the browser page.
	 */
	public synchronized void refreshBrowserProperty(WidgetPart widget) {
		if( _browser == null )
			return;
		
		Display display = _browser.getDisplay();

		if( !display.isDisposed() ) {

			display.asyncExec( new Runnable() {
				public void run() {
					// Give incremental content to the browser
					//----------------------------------------
					EvServer.getInstance().refreshBrowserIncremental(_context);
				}
			} );
		}
	}

	/**
	 * Called when a person selects a link to a source line on a browser page with error messages. 
	 */
	public void selectTextInEditor( int offset, int length ) {
		if( _pagePreview == null )
			return;

		_pagePreview.getEditor().getPageSource().selectAndReveal( offset, length );
		_pagePreview.getEditor().showPage( 1 );
	}

	/**
	 * Gives the web page URL to the browser to display.
	 */
	protected void setBrowserUrl( String url ) {
//		if ( EvEditor._iRenderEngine != EvConstants.PREFERENCE_RENDERENGINE_XULRUNNER ) {
//			try {
//				Class oleAutomationClass = Class.forName( "org.eclipse.swt.ole.win32.OleAutomation" );
//				Class variantClass = Class.forName( "org.eclipse.swt.ole.win32.Variant" );
//	
//				_browser.execute( "try { if (window.egl) egl.terminateSession(); } catch (e) { }" );
//				
//				Object webBrowser = getField( _browser, "webBrowser" );
//				Object auto = getField( webBrowser, "auto" );
//				Method getIDsOfNamesMethod = oleAutomationClass.getMethod( "getIDsOfNames", new Class[] { String[].class } );
//				int[] iaNameIds = (int[])getIDsOfNamesMethod.invoke( auto, new Object[] { new String[] { "Navigate", "Flags", "URL" } } );
//	
//				Object variants = Array.newInstance( variantClass, 2 );
//				Array.set( variants, 0, variantClass.getConstructor( new Class[] { int.class } ).newInstance( new Object[] { new Integer( 0x2 ) } ) ); // 0x2 = NavNoHistory
//				Array.set( variants, 1, variantClass.getConstructor( new Class[] { String.class } ).newInstance( new Object[] { url } ) );
//	
//				Method invokeMethod = oleAutomationClass.getMethod( "invoke", new Class[] { int.class, variants.getClass(), int[].class } );
//				invokeMethod.invoke( auto, new Object[] { new Integer( iaNameIds[ 0 ] ), variants, new int[] { iaNameIds[ 1 ], iaNameIds[ 2 ] } } );
//			} catch( Exception e ) {
//				_browser.setUrl( url );
//			}
//		} else {
			_browser.setUrl( url );
//		}
	}

	/**
	 * Called by the owning page when the editor is being disposed.
	 */
	public void terminate() {
		EvServer.getInstance().removeContext( _context );
	}
}
