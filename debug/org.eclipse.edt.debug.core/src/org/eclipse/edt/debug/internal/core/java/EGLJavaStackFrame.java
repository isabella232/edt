/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDropToFrame;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.edt.debug.core.IEGLStackFrame;
import org.eclipse.edt.debug.core.IEGLThread;
import org.eclipse.jdt.debug.core.IJavaStackFrame;

/**
 * Wraps an IJavaStackFrame.
 */
public class EGLJavaStackFrame extends EGLJavaDebugElement implements IEGLStackFrame, IDropToFrame
{
	/**
	 * The underlying Java stack frame.
	 */
	private final IJavaStackFrame javaFrame;
	
	/**
	 * The EGL-wrapped thread.
	 */
	private final EGLJavaThread eglThread;
	
	/**
	 * The EGL-wrapped variables.
	 */
	private EGLJavaVariable[] eglVariables;
	
	/**
	 * The previous Java variables that were wrapped.
	 */
	private IVariable[] previousJavaVariables;
	
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
	 * Constructor.
	 * 
	 * @param frame The Java stack frame.
	 * @param thread The EGL-wrapped thread.
	 */
	public EGLJavaStackFrame( IJavaStackFrame frame, EGLJavaThread thread )
	{
		super( thread.getDebugTarget() );
		javaFrame = frame;
		eglThread = thread;
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
		javaFrame.stepInto();
	}
	
	@Override
	public void stepOver() throws DebugException
	{
		javaFrame.stepOver();
	}
	
	@Override
	public void stepReturn() throws DebugException
	{
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
	public IVariable[] getVariables() throws DebugException
	{
		if ( getSMAP().length() == 0 )
		{
			// Couldn't get the variable info from the SMAP...just return the Java variables.
			return javaFrame.getVariables();
		}
		
		boolean recompute = true;
		IVariable[] javaVariables = javaFrame.getVariables();
		if ( previousJavaVariables != null )
		{
			if ( javaVariables.length == previousJavaVariables.length )
			{
				recompute = false;
				for ( int i = 0; i < javaVariables.length; i++ )
				{
					if ( javaVariables[ i ] != previousJavaVariables[ i ] )
					{
						recompute = true;
						break;
					}
				}
			}
		}
		
		if ( recompute )
		{
			// We could just manually add a "this" variable and a "function" variable, but this way if the language gets
			// extended by some client, e.g. concept of 'static' added to EGL, this would support that.
			List<EGLJavaVariable> newEGLVariables = SMAPUtil.filterAndWrapVariables( javaVariables, eglVariables,
					getSMAPVariableInfos(), this, getEGLJavaDebugTarget(), true );
			previousJavaVariables = javaVariables;
			eglVariables = new EGLJavaVariable[ newEGLVariables.size() + 1 ];
			newEGLVariables.toArray( eglVariables );
			eglVariables[ eglVariables.length - 1 ] = new EGLJavaFunctionVariable( this );
		}
		
		return eglVariables;
	}
	
	@Override
	public boolean hasVariables() throws DebugException
	{
		return getVariables().length != 0;
	}
	
	@Override
	public int getLineNumber() throws DebugException
	{
		return javaFrame.getLineNumber();
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
		if ( adapter == IStackFrame.class || adapter == EGLJavaStackFrame.class || adapter == IEGLStackFrame.class
				|| adapter == IDropToFrame.class )
		{
			return this;
		}
		if ( adapter == IThread.class || adapter == EGLJavaThread.class || adapter == IEGLThread.class )
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
	 * @return the underlying Java element.
	 */
	@Override
	public Object getJavaElement()
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
		javaFrame.dropToFrame();
	}
	
	/**
	 * Returns the SMAP information for the stack frame. It will never be null. If there is no SMAP information, or the
	 * Java type was not a type that we recognize, then this will return blank.
	 * 
	 * @return the SMAP information.
	 * @throws DebugException
	 */
	public String getSMAP() throws DebugException
	{
		if ( smap == null )
		{
			smap = SMAPUtil.getSMAP( javaFrame.getReferenceType() );
		}
		return smap;
	}
	
	/**
	 * Returns the variable information from the SMAP for the stack frame. It will never be null. If there is no SMAP
	 * information, or the Java type was not a type that we recognize, then this will return empty.
	 * 
	 * @return the variable information
	 * @throws DebugException
	 */
	public SMAPVariableInfo[] getSMAPVariableInfos() throws DebugException
	{
		if ( smapVariableInfos == null )
		{
			smapVariableInfos = SMAPUtil.parseVariables( getSMAP(), this );
		}
		return smapVariableInfos;
	}
	
	public SMAPFunctionInfo getSMAPFunctionInfo() throws DebugException
	{
		return smapFunctionInfo;
	}
	
	public void setSMAPFunctionInfo( SMAPFunctionInfo info )
	{
		this.smapFunctionInfo = info;
	}
}
