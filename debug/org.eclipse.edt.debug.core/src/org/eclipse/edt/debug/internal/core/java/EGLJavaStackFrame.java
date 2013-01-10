/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.internal.core.java;

import java.util.Hashtable;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDropToFrame;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.edt.debug.core.DebugUtil;
import org.eclipse.edt.debug.core.EDTDebugCoreMessages;
import org.eclipse.edt.debug.core.IEGLStackFrame;
import org.eclipse.edt.debug.core.IEGLThread;
import org.eclipse.edt.debug.core.java.IEGLJavaStackFrame;
import org.eclipse.edt.debug.core.java.IEGLJavaThread;
import org.eclipse.edt.debug.core.java.IEGLJavaValue;
import org.eclipse.edt.debug.core.java.IEGLJavaVariable;
import org.eclipse.edt.debug.core.java.SMAPFunctionInfo;
import org.eclipse.edt.debug.core.java.SMAPLineInfo;
import org.eclipse.edt.debug.core.java.SMAPUtil;
import org.eclipse.edt.debug.core.java.SMAPVariableInfo;
import org.eclipse.edt.debug.core.java.variables.VariableUtil;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.osgi.util.NLS;

/**
 * Wraps an IJavaStackFrame.
 */
public class EGLJavaStackFrame extends EGLJavaDebugElement implements IEGLJavaStackFrame, IDropToFrame
{
	/**
	 * The underlying Java stack frame.
	 */
	private IJavaStackFrame javaFrame;
	
	/**
	 * The EGL-wrapped thread.
	 */
	private final EGLJavaThread eglThread;
	
	/**
	 * The EGL-wrapped variables.
	 */
	private IVariable[] variables;
	
	/**
	 * The current cached EGL variables.
	 */
	private Hashtable<String, IEGLJavaVariable> currentEGLVariables;
	
	/**
	 * The cached EGL variables from the last time we suspended.
	 */
	private Hashtable<String, IEGLJavaVariable> previousEGLVariables;
	
	/**
	 * The SMAP data from the source debug extension of the class file.
	 */
	private String smap;
	
	/**
	 * The variable information retrieved from the SMAP.
	 */
	private SMAPVariableInfo[] smapVariableInfos;
	
	/**
	 * The function information retrieved from the SMAP, for this stack frame.
	 */
	private SMAPFunctionInfo smapFunctionInfo;
	
	/**
	 * The line number this frame was suspended at before a stepInto was performed.
	 */
	private int lineBeforeStepInto;
	
	/**
	 * Constructor.
	 * 
	 * @param frame The Java stack frame.
	 * @param thread The EGL-wrapped thread.
	 */
	public EGLJavaStackFrame( IJavaStackFrame frame, EGLJavaThread thread )
	{
		super( thread.getDebugTarget() );
		this.eglThread = thread;
		bind( frame );
	}
	
	/**
	 * Binds to the given Java frame.
	 * 
	 * @param javaFrame The underlying Java stack frame.
	 */
	protected void bind( IJavaStackFrame javaFrame )
	{
		this.javaFrame = javaFrame;
		previousEGLVariables = currentEGLVariables;
		
		int hashSize = 10;
		if ( previousEGLVariables != null )
		{
			int oldSize = previousEGLVariables.size();
			if ( oldSize > hashSize )
			{
				hashSize = oldSize;
			}
		}
		currentEGLVariables = new Hashtable<String, IEGLJavaVariable>( hashSize );
		
		this.variables = null;
		this.smap = null;
	}
	
	@Override
	public boolean canStepInto()
	{
		return javaFrame.canStepInto();
	}
	
	@Override
	public boolean canStepOver()
	{
		return javaFrame.canStepOver();
	}
	
	@Override
	public boolean canStepReturn()
	{
		return javaFrame.canStepReturn();
	}
	
	@Override
	public boolean isStepping()
	{
		return javaFrame.isStepping();
	}
	
	@Override
	public void stepInto() throws DebugException
	{
		eglThread.setStepStart( javaFrame );
		javaFrame.stepInto();
	}
	
	@Override
	public void stepOver() throws DebugException
	{
		eglThread.setStepStart( javaFrame );
		javaFrame.stepOver();
	}
	
	@Override
	public void stepReturn() throws DebugException
	{
		eglThread.setStepStart( javaFrame );
		javaFrame.stepReturn();
	}
	
	@Override
	public boolean canResume()
	{
		return javaFrame.canResume();
	}
	
	@Override
	public boolean canSuspend()
	{
		return javaFrame.canSuspend();
	}
	
	@Override
	public boolean isSuspended()
	{
		return javaFrame.isSuspended();
	}
	
	@Override
	public void resume() throws DebugException
	{
		javaFrame.resume();
	}
	
	@Override
	public void suspend() throws DebugException
	{
		javaFrame.suspend();
	}
	
	@Override
	public boolean canTerminate()
	{
		return javaFrame.canTerminate();
	}
	
	@Override
	public boolean isTerminated()
	{
		return javaFrame.isTerminated();
	}
	
	@Override
	public void terminate() throws DebugException
	{
		javaFrame.terminate();
	}
	
	@Override
	public IThread getThread()
	{
		return eglThread;
	}
	
	@Override
	public IEGLJavaThread getEGLThread()
	{
		return eglThread;
	}
	
	@Override
	public synchronized IVariable[] getVariables() throws DebugException
	{
		if ( variables != null )
		{
			return variables;
		}
		
		if ( getSMAP().length() == 0 )
		{
			// Couldn't get the variable info from the SMAP...just return the Java variables.
			variables = javaFrame.getVariables();
		}
		else
		{
			// We could just manually add a "this" variable and a "function" variable, but this way if the language gets
			// extended by some client, e.g. concept of 'static' added to EGL, this would support that.
			List<IEGLJavaVariable> newEGLVariables = VariableUtil.filterAndWrapVariables( javaFrame.getVariables(), this, true, null );
			variables = new EGLJavaVariable[ newEGLVariables.size() + 1 ];
			newEGLVariables.toArray( variables );
			variables[ variables.length - 1 ] = getCorrespondingVariable( new EGLJavaFunctionVariable( this ), null );
		}
		
		return variables;
	}
	
	@Override
	public boolean hasVariables() throws DebugException
	{
		return getVariables().length != 0;
	}
	
	@Override
	public int getLineNumber() throws DebugException
	{
		int line = javaFrame.getLineNumber();
		if ( !getEGLJavaDebugTarget().supportsSourceDebugExtension() && isEGLStratum() )
		{
			SMAPLineInfo lineInfo = SMAPUtil.getSMAPLineInfo( getSMAP(), getEGLJavaDebugTarget().getSMAPLineCache() );
			if ( lineInfo != null )
			{
				line = lineInfo.getEGLLine( line );
			}
			else
			{
				line = -1;
			}
		}
		return line;
	}
	
	@Override
	public int getCharStart() throws DebugException
	{
		return -1;
	}
	
	@Override
	public int getCharEnd() throws DebugException
	{
		return -1;
	}
	
	public String getLabel() throws DebugException
	{
		String sourceName = null;
		for ( IVariable v : getVariables() )
		{
			if ( v instanceof EGLJavaFunctionContainerVariable )
			{
				sourceName = v.getName();
				break;
			}
		}
		
		if ( sourceName == null || sourceName.trim().length() == 0 )
		{
			sourceName = SMAPUtil.getFileName( getSMAP() );
			
			if ( sourceName != null && DebugUtil.isEGLFileName( sourceName ) )
			{
				// Strip off '.egl'
				sourceName = sourceName.substring( 0, sourceName.length() - 4 );
			}
		}
		
		String lineStr;
		int lineNumber = getLineNumber();
		if ( lineNumber >= 0 )
		{
			lineStr = String.valueOf( lineNumber );
		}
		else
		{
			lineStr = String.valueOf( EDTDebugCoreMessages.StackFrameLineUnknown );
		}
		
		return NLS.bind( EDTDebugCoreMessages.StackFrameLabelBasic, new Object[] { javaFrame.getName(), lineStr, sourceName } );
	}
	
	@Override
	public String getName() throws DebugException
	{
		return javaFrame.getName();
	}
	
	@Override
	public IRegisterGroup[] getRegisterGroups() throws DebugException
	{
		return javaFrame.getRegisterGroups();
	}
	
	@Override
	public boolean hasRegisterGroups() throws DebugException
	{
		return javaFrame.hasRegisterGroups();
	}
	
	@Override
	public Object getAdapter( Class adapter )
	{
		if ( adapter == IStackFrame.class || adapter == EGLJavaStackFrame.class || adapter == IEGLStackFrame.class || adapter == IDropToFrame.class
				|| adapter == IEGLJavaStackFrame.class )
		{
			return this;
		}
		if ( adapter == IThread.class || adapter == EGLJavaThread.class || adapter == IEGLThread.class || adapter == IEGLJavaThread.class )
		{
			return getThread();
		}
		if ( adapter == IJavaStackFrame.class )
		{
			return javaFrame;
		}
		return super.getAdapter( adapter );
	}
	
	/**
	 * @return the underlying stack frame.
	 */
	public IJavaStackFrame getJavaStackFrame()
	{
		return javaFrame;
	}
	
	/**
	 * @return the underlying Java debug element.
	 */
	@Override
	public Object getJavaDebugElement()
	{
		return getJavaStackFrame();
	}
	
	@Override
	public boolean canDropToFrame()
	{
		return javaFrame.canDropToFrame();
	}
	
	@Override
	public void dropToFrame() throws DebugException
	{
		eglThread.setStepStart( javaFrame );
		javaFrame.dropToFrame();
	}
	
	/**
	 * Returns the SMAP information for the stack frame. It will never be null. If there is no SMAP information, or the Java type was not a type that
	 * we recognize, then this will return blank.
	 * 
	 * @return the SMAP information.
	 * @throws DebugException
	 */
	public String getSMAP() throws DebugException
	{
		if ( smap == null )
		{
			smap = SMAPUtil.getSMAP( getEGLJavaDebugTarget(), javaFrame.getReferenceType() );
		}
		return smap;
	}
	
	@Override
	public SMAPVariableInfo[] getSMAPVariableInfos() throws DebugException
	{
		if ( smapVariableInfos == null )
		{
			smapVariableInfos = SMAPUtil.parseVariables( getSMAP(), this );
		}
		return smapVariableInfos;
	}
	
	@Override
	public void setSMAPVariableInfos( SMAPVariableInfo[] infos )
	{
		this.smapVariableInfos = infos;
	}
	
	@Override
	public SMAPFunctionInfo getSMAPFunctionInfo() throws DebugException
	{
		getSMAPVariableInfos(); // Make sure we've parsed the SMAP data
		return smapFunctionInfo;
	}
	
	@Override
	public void setSMAPFunctionInfo( SMAPFunctionInfo info )
	{
		this.smapFunctionInfo = info;
	}
	
	public int getLineBeforeStepInto()
	{
		return this.lineBeforeStepInto;
	}
	
	public void setLineBeforeStepInto( int line )
	{
		this.lineBeforeStepInto = line;
	}
	
	@Override
	public IEGLJavaVariable getCorrespondingVariable( IEGLJavaVariable newVariable, IEGLJavaValue parent ) throws DebugException
	{
		IEGLJavaVariable eglVar = null;
		
		String qualifiedName = VariableUtil.getQualifiedName( newVariable );
		if ( previousEGLVariables != null )
		{
			eglVar = previousEGLVariables.get( qualifiedName );
		}
		
		// If not the same class, e.g. previously wasn't adapted, but now its value caused it to get adapted to some other implementation
		// of IEGLJavaVariable, then throw the old variable away instead of reusing it.
		if ( eglVar != null && eglVar.getClass() == newVariable.getClass() )
		{
			eglVar.initialize( newVariable, parent );
		}
		else
		{
			eglVar = newVariable;
		}
		
		if ( currentEGLVariables != null )
		{
			currentEGLVariables.put( qualifiedName, eglVar );
		}
		
		return eglVar;
	}
	
	@Override
	public String getSourcePath() throws DebugException
	{
		if ( !getEGLJavaDebugTarget().supportsSourceDebugExtension() && isEGLStratum() )
		{
			String path = SMAPUtil.getFilePath( getSMAP() );
			if ( path != null )
			{
				return path;
			}
		}
		return javaFrame.getSourcePath();
	}
	
	@Override
	protected boolean shouldCheckJavaElementAdapter()
	{
		try
		{
			return !isEGLStratum();
		}
		catch ( DebugException e )
		{
			return true;
		}
	}
	
	@Override
	public boolean isEGLStratum() throws DebugException
	{
		return SMAPUtil.isEGLStratum( getSMAP() );
	}
}
