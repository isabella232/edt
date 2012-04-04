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
package org.eclipse.edt.debug.javascript.internal.model;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.edt.debug.core.IEGLStackFrame;
import org.eclipse.edt.debug.core.IEGLThread;
import org.eclipse.edt.debug.core.IEGLVariable;
import org.eclipse.osgi.util.NLS;

public class RUIStackFrame extends RUIDebugElement implements IEGLStackFrame
{
	
	private static final IEGLVariable[] EMPTY_VARIABLES = {};
	private static final IRegisterGroup[] EMPTY_REGISTERS = {};
	
	private RUIThread fThread;
	private String fFileName;
	private int fLineNumber;
	private int fId;
	private String fFunctionName;
	private String fProgramName;
	
	/**
	 * Variables that belong to this stack frame.
	 */
	private IVariable[] fVariables;
	
	/**
	 * The variables processed from the client. These are used to look up and reinitialize existing variables for the frame, otherwise they are used
	 * when no old variable is found.
	 */
	private List fCurrentVariables;
	
	/**
	 * Table containing all the variables in this frame, including child variables. The key is the variable's fully qualified name (e.g.
	 * "myFunc.myRec.myField").
	 */
	private Hashtable fVariablesHash;
	
	/**
	 * Values that were stored in fVariablesHash are moved to fVariablesHashOld every time the thread is run (including stepping). Used to find old
	 * values of variables.
	 */
	private Hashtable fVariablesHashOld;
	
	/**
	 * Constructor.
	 * 
	 * @param thread The thread this frame belongs to.
	 * @param id The frame's id.
	 */
	public RUIStackFrame( RUIThread thread, int id, String programName, String fileName, String functionName, int lineNumber )
	{
		super( (RUIDebugTarget)thread.getDebugTarget() );
		
		initialize( thread, id, programName, fileName, functionName, lineNumber );
	}
	
	public RUIStackFrame( RUIDebugTarget target )
	{
		super( target );
	}
	
	@Override
	public Object getAdapter( Class adapter )
	{
		if ( adapter == RUIStackFrame.class || adapter == IEGLStackFrame.class || adapter == IStackFrame.class )
		{
			return this;
		}
		
		if ( adapter == IThread.class || adapter == IEGLThread.class || adapter == RUIThread.class )
		{
			return getThread();
		}
		
		return super.getAdapter( adapter );
	}
	
	/**
	 * Initializes this frame with information from <code>other</code>.
	 */
	public void initialize( RUIStackFrame other )
	{
		fLineNumber = other.fLineNumber;
		fCurrentVariables = other.fCurrentVariables;
		initialize( other.fThread, other.fId, other.fProgramName, other.fFileName, other.fFunctionName, other.fLineNumber );
	}
	
	private void initialize( RUIThread thread, int id, String programName, String fileName, String functionName, int lineNumber )
	{
		fThread = thread;
		fId = id;
		fProgramName = programName;
		fFileName = fileName;
		fFunctionName = functionName;
		fLineNumber = lineNumber;
		
		// Reset the variables.
		fVariablesHashOld = fVariablesHash;
		
		int hashSize = 10;
		if ( fVariablesHashOld != null )
		{
			int oldSize = fVariablesHashOld.size();
			if ( oldSize > hashSize )
			{
				hashSize = oldSize;
			}
		}
		
		fVariablesHash = new Hashtable( hashSize );
		fVariables = null;
	}
	
	/**
	 * Get the RUIVariable that corresponds to <code>newVar</code>. If we can find the variable in the hash of old variables then reuse it. Otherwise
	 * use <code>newVar</code>.
	 */
	public RUIVariable getCorrespondingVariable( RUIVariable newVar, RUIVariable parent )
	{
		RUIVariable answer;
		String qualifiedName;
		if ( parent != null )
		{
			qualifiedName = parent.getQualifiedName() + "." + newVar.getName(); //$NON-NLS-1$
			newVar.setQualifiedName( qualifiedName );
		}
		else
		{
			qualifiedName = newVar.getQualifiedName();
		}
		
		answer = findVariableInOldHash( qualifiedName );
		if ( answer == null )
		{
			answer = newVar;
		}
		else
		{
			answer.initialize( this, parent, newVar );
		}
		addVariableToHash( answer );
		
		return answer;
	}
	
	private void addVariableToHash( RUIVariable variable )
	{
		if ( variable != null )
		{
			fVariablesHash.put( variable.getQualifiedName(), variable );
		}
	}
	
	private RUIVariable findVariableInOldHash( String qualifiedName )
	{
		if ( fVariablesHashOld != null )
		{
			return (RUIVariable)fVariablesHashOld.get( qualifiedName );
		}
		return null;
	}
	
	@Override
	public int getCharEnd() throws DebugException
	{
		return -1;
	}
	
	@Override
	public int getCharStart() throws DebugException
	{
		return -1;
	}
	
	@Override
	public int getLineNumber() throws DebugException
	{
		return fLineNumber;
	}
	
	@Override
	public String getName() throws DebugException
	{
		return NLS.bind( RUIDebugMessages.rui_stack_frame_label_basic, new String[] { fFunctionName, Integer.toString( fLineNumber ), fProgramName } );
	}
	
	@Override
	public IRegisterGroup[] getRegisterGroups() throws DebugException
	{
		return EMPTY_REGISTERS;
	}
	
	@Override
	public IThread getThread()
	{
		return fThread;
	}
	
	@Override
	public IVariable[] getVariables() throws DebugException
	{
		if ( fVariables != null )
		{
			return fVariables;
		}
		
		// Any variables from the browser will be in fCurrentVariables.
		if ( fCurrentVariables == null || fCurrentVariables.size() == 0 )
		{
			fVariables = EMPTY_VARIABLES;
			return fVariables;
		}
		
		int size = fCurrentVariables.size();
		fVariables = new IVariable[ size ];
		for ( int i = 0; i < size; i++ )
		{
			fVariables[ i ] = getCorrespondingVariable( (RUIVariable)fCurrentVariables.get( i ), null );
		}
		return fVariables;
	}
	
	@Override
	public boolean hasRegisterGroups() throws DebugException
	{
		return false;
	}
	
	@Override
	public boolean hasVariables() throws DebugException
	{
		return isSuspended() && !isTerminated() && fCurrentVariables != null && fCurrentVariables.size() > 0;
	}
	
	@Override
	public boolean canStepInto()
	{
		return getThread().canStepInto();
	}
	
	@Override
	public boolean canStepOver()
	{
		return getThread().canStepOver();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#canStepReturn()
	 */
	@Override
	public boolean canStepReturn()
	{
		return getThread().canStepReturn();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#isStepping()
	 */
	@Override
	public boolean isStepping()
	{
		return getThread().isStepping();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#stepInto()
	 */
	@Override
	public void stepInto() throws DebugException
	{
		getThread().stepInto();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#stepOver()
	 */
	@Override
	public void stepOver() throws DebugException
	{
		((RUIThread)getThread()).stepOver( this );
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#stepReturn()
	 */
	@Override
	public void stepReturn() throws DebugException
	{
		((RUIThread)getThread()).stepReturn( this );
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#canResume()
	 */
	@Override
	public boolean canResume()
	{
		return getThread().canResume();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#canSuspend()
	 */
	@Override
	public boolean canSuspend()
	{
		return getThread().canSuspend();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#isSuspended()
	 */
	@Override
	public boolean isSuspended()
	{
		return getThread().isSuspended();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#resume()
	 */
	@Override
	public void resume() throws DebugException
	{
		getThread().resume();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#suspend()
	 */
	@Override
	public void suspend() throws DebugException
	{
		getThread().suspend();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	@Override
	public boolean canTerminate()
	{
		return getThread().canTerminate();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
	 */
	@Override
	public boolean isTerminated()
	{
		return getThread().isTerminated();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#terminate()
	 */
	@Override
	public void terminate() throws DebugException
	{
		getThread().terminate();
	}
	
	public String getSourceName()
	{
		return fFileName;
	}
	
	@Override
	public boolean equals( Object obj )
	{
		if ( obj instanceof RUIStackFrame )
		{
			RUIStackFrame sf = (RUIStackFrame)obj;
			return sf.fId == fId && sf.fFileName.equals( fFileName ) && sf.fFunctionName.equals( fFunctionName )
					&& sf.fProgramName.equals( fProgramName );
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return getSourceName().hashCode() + fId;
	}
	
	public String getProgramName()
	{
		return fProgramName;
	}
	
	public String getFunctionName()
	{
		return fFunctionName;
	}
	
	public void addVariable( IVariable var )
	{
		if ( fCurrentVariables == null )
		{
			fCurrentVariables = new ArrayList();
		}
		fCurrentVariables.add( var );
	}
	
	public int getId()
	{
		return fId;
	}
	
	public String getProgramFile()
	{
		return fFileName;
	}
	
	public IVariable evaluateWatchExpression( String expression ) throws DebugException
	{
		return WATCH_EXPRS_NOT_SUPPORTED_VAR;
	}
	
	/**
	 * Rather than throwing an error for watch expressions, we display the error message as the value so that users don't have to expand the
	 * expression to see the message.
	 */
	private static final IVariable WATCH_EXPRS_NOT_SUPPORTED_VAR = new IVariable() {
		private final IValue value = new IValue() {
			@Override
			public String getReferenceTypeName() throws DebugException
			{
				return ""; //$NON-NLS-1$
			}
			
			@Override
			public String getValueString() throws DebugException
			{
				return RUIDebugMessages.rui_stack_frame_watch_exprs_unsupported;
			}
			
			@Override
			public IVariable[] getVariables() throws DebugException
			{
				return EMPTY_VARIABLES;
			}
			
			@Override
			public boolean hasVariables() throws DebugException
			{
				return false;
			}
			
			@Override
			public boolean isAllocated() throws DebugException
			{
				return false;
			}
			
			@Override
			public IDebugTarget getDebugTarget()
			{
				return null;
			}
			
			@Override
			public ILaunch getLaunch()
			{
				return null;
			}
			
			@Override
			public String getModelIdentifier()
			{
				return null;
			}
			
			@Override
			public Object getAdapter( Class adapter )
			{
				return null;
			}
		};
		
		@Override
		public String getName() throws DebugException
		{
			return null;
		}
		
		@Override
		public String getReferenceTypeName() throws DebugException
		{
			return null;
		}
		
		@Override
		public IValue getValue() throws DebugException
		{
			return value;
		}
		
		@Override
		public boolean hasValueChanged() throws DebugException
		{
			return false;
		}
		
		@Override
		public IDebugTarget getDebugTarget()
		{
			return null;
		}
		
		@Override
		public ILaunch getLaunch()
		{
			return null;
		}
		
		@Override
		public String getModelIdentifier()
		{
			return null;
		}
		
		@Override
		public Object getAdapter( Class adapter )
		{
			return null;
		}
		
		@Override
		public void setValue( String expression ) throws DebugException
		{
		}
		
		@Override
		public void setValue( IValue value ) throws DebugException
		{
		}
		
		@Override
		public boolean supportsValueModification()
		{
			return false;
		}
		
		@Override
		public boolean verifyValue( String expression ) throws DebugException
		{
			return false;
		}
		
		@Override
		public boolean verifyValue( IValue value ) throws DebugException
		{
			return false;
		}
	};
}
