/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.core;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.core.model.IWatchExpressionDelegate;
import org.eclipse.debug.core.model.IWatchExpressionListener;
import org.eclipse.debug.core.model.IWatchExpressionResult;
import org.eclipse.edt.debug.core.java.IEGLJavaDebugElement;
import org.eclipse.edt.debug.core.java.IEGLJavaDebugTarget;
import org.eclipse.edt.debug.core.java.SMAPUtil;
import org.eclipse.jdt.debug.core.IJavaStackFrame;

public class EGLWatchExpressionDelegate implements IWatchExpressionDelegate
{
	@Override
	public void evaluateExpression( final String expression, IDebugElement context, IWatchExpressionListener listener )
	{
		// We don't actually support watch expressions yet, we just need to allow Java frames that don't have the EGL stratum to still use them.
		if ( context instanceof IEGLJavaDebugElement )
		{
			Object javaContext = ((IEGLJavaDebugElement)context).getJavaDebugElement();
			if ( javaContext instanceof IDebugElement )
			{
				boolean skipJavaDelegate = false;
				IJavaStackFrame frame = (IJavaStackFrame)((IDebugElement)javaContext).getAdapter( IJavaStackFrame.class );
				if ( frame != null )
				{
					// If it's an EGL stratum, do not run the Java delegate.
					skipJavaDelegate = SMAPUtil.isEGLStratum( frame, (IEGLJavaDebugTarget)((IEGLJavaDebugElement)context).getDebugTarget() );
				}
				
				if ( !skipJavaDelegate )
				{
					IWatchExpressionDelegate delegate = DebugPlugin.getDefault().getExpressionManager()
							.newWatchExpressionDelegate( ((IDebugElement)javaContext).getModelIdentifier() );
					if ( delegate != null )
					{
						delegate.evaluateExpression( expression, (IDebugElement)javaContext, listener );
						return;
					}
				}
			}
		}
		
		if ( expression.trim().length() == 0 )
		{
			listener.watchEvaluationFinished( null );
		}
		else
		{
			IDebugElement element = (IEGLDebugElement)context.getAdapter( IEGLDebugElement.class );
			if ( element == null )
			{
				listener.watchEvaluationFinished( null );
			}
			else
			{
				listener.watchEvaluationFinished( new IWatchExpressionResult() {
					@Override
					public boolean hasErrors()
					{
						return false;
					}
					
					@Override
					public IValue getValue()
					{
						return new IValue() {
							@Override
							public String getReferenceTypeName() throws DebugException
							{
								return ""; //$NON-NLS-1$
							}
							
							@Override
							public String getValueString() throws DebugException
							{
								return EDTDebugCoreMessages.WatchExprsUnsupported;
							}
							
							@Override
							public IVariable[] getVariables() throws DebugException
							{
								return new IVariable[ 0 ];
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
					}
					
					@Override
					public String getExpressionText()
					{
						return expression;
					}
					
					@Override
					public DebugException getException()
					{
						return null;
					}
					
					@Override
					public String[] getErrorMessages()
					{
						return null;
					}
				} );
			}
		}
	}
}
