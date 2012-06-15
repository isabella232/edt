/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.core.java.variables;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.edt.debug.core.EDTDebugCorePlugin;
import org.eclipse.edt.debug.core.java.IEGLJavaStackFrame;
import org.eclipse.edt.debug.core.java.IEGLJavaThread;
import org.eclipse.edt.debug.core.java.IEGLJavaValue;
import org.eclipse.edt.debug.core.java.IEGLJavaVariable;
import org.eclipse.edt.debug.core.java.SMAPVariableInfo;
import org.eclipse.edt.debug.internal.core.java.EGLJavaFunctionContainerVariable;
import org.eclipse.edt.debug.internal.core.java.EGLJavaVariable;
import org.eclipse.edt.debug.internal.core.java.variables.DefaultVariableAdapter;
import org.eclipse.jdt.debug.core.IEvaluationRunnable;
import org.eclipse.jdt.debug.core.IJavaClassObject;
import org.eclipse.jdt.debug.core.IJavaClassType;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaFieldVariable;
import org.eclipse.jdt.debug.core.IJavaInterfaceType;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaReferenceType;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.core.IJavaVariable;

/**
 * Utility class for EGL variables.
 */
public class VariableUtil
{
	/**
	 * Use this when an empty variable array would otherwise be created.
	 */
	public static final IVariable[] EMPTY_VARIABLES = {};
	
	/**
	 * The ID for the variable adapters extension point.
	 */
	public static final String EXTENSION_POINT_VARIABLE_ADAPTERS = "javaVariableAdapters"; //$NON-NLS-1$
	
	/**
	 * The variable adapters.
	 */
	private static IVariableAdapter[] variableAdapters;
	
	private VariableUtil()
	{
		// No instances.
	}
	
	/**
	 * Given a set of Java variables, and SMAP variable information, this will determine which Java variables should be displayed. Any variables to be
	 * displayed will be wrapped inside an EGLJavaVariable.
	 * 
	 * @param javaVariables The Java variables, not null.
	 * @param currVariables The currently cached EGL variables, not null.
	 * @param prevVariables The EGL variables from the last time we processed variables, possibly null.
	 * @param infos The variable information from the SMAP, not null.
	 * @param frame The active EGL-wrapped stack frame, not null.
	 * @param skipLocals True if local variables should be omitted.
	 * @param parent The parent of the variables being created, possibly null.
	 * @return the filtered, EGL-wrapped variables.
	 * @throws DebugException
	 */
	public static List<IEGLJavaVariable> filterAndWrapVariables( IVariable[] javaVariables, IEGLJavaStackFrame frame, boolean skipLocals,
			IEGLJavaValue parent ) throws DebugException
	{
		List<IEGLJavaVariable> newEGLVariables = new ArrayList<IEGLJavaVariable>( javaVariables.length );
		
		SMAPVariableInfo[] infos = parent == null
				? frame.getSMAPVariableInfos()
				: parent.getSMAPVariableInfos();
		String javaFrameSignature = frame.getJavaStackFrame().getMethodName() + ";" + frame.getJavaStackFrame().getSignature(); //$NON-NLS-1$
		int currentLine = frame.getLineNumber();
		int frameStartLine = frame.getSMAPFunctionInfo() == null
				? -1
				: frame.getSMAPFunctionInfo().lineDeclared;
		
		for ( int i = 0; i < javaVariables.length; i++ )
		{
			IJavaVariable javaVar = (IJavaVariable)javaVariables[ i ];
			String javaName = javaVar.getName();
			
			if ( javaVar.isLocal() )
			{
				if ( !skipLocals )
				{
					SMAPVariableInfo matchingInfo = null;
					for ( int j = 0; j < infos.length; j++ )
					{
						SMAPVariableInfo info = infos[ j ];
						if ( info.javaName.equals( javaName ) )
						{
							// Validate the signature and make sure the info is in scope. There could be multiple
							// local variables of different types with the same name, in different scopes within the
							// function.
							if ( info.javaMethodSignature != null && info.javaMethodSignature.equals( javaFrameSignature )
									&& (currentLine >= info.lineDeclared || currentLine == frameStartLine) )
							{
								if ( matchingInfo == null || matchingInfo.lineDeclared < info.lineDeclared )
								{
									matchingInfo = info;
								}
								
								// Don't break - keep looping to see if there's a better match. We want the info
								// with the highest line number that's within scope.
							}
						}
					}
					
					if ( matchingInfo != null )
					{
						IEGLJavaVariable var = frame.getCorrespondingVariable( createEGLVariable( javaVar, matchingInfo, frame, parent ), parent );
						newEGLVariables.add( var );
					}
				}
			}
			else
			{
				if ( "this".equals( javaName ) ) //$NON-NLS-1$
				{
					IEGLJavaVariable var = frame.getCorrespondingVariable( new EGLJavaFunctionContainerVariable( frame.getDebugTarget(), javaVar,
							frame ), parent );
					newEGLVariables.add( var );
				}
				else
				{
					SMAPVariableInfo matchingInfo = null;
					for ( int j = 0; j < infos.length; j++ )
					{
						SMAPVariableInfo info = infos[ j ];
						if ( info.javaName.equals( javaName ) )
						{
							// Make sure it has no signature
							if ( info.javaMethodSignature == null )
							{
								matchingInfo = info;
								break;
							}
						}
					}
					
					if ( matchingInfo != null )
					{
						IEGLJavaVariable var = frame.getCorrespondingVariable( createEGLVariable( javaVar, matchingInfo, frame, parent ), parent );
						newEGLVariables.add( var );
					}
				}
			}
		}
		return newEGLVariables;
	}
	
	/**
	 * Creates an EGL wrapper around the given Java variable. Contributed IVariableAdapters are consulted before doing basic wrapping.
	 * 
	 * @param javaVariable The Java variable.
	 * @param info The variable information from the SMAP.
	 * @param frame The EGL frame.
	 * @param parentVariable The parent of this variable, possibly null.
	 * @return
	 */
	public static IEGLJavaVariable createEGLVariable( IJavaVariable javaVariable, SMAPVariableInfo info, IEGLJavaStackFrame frame,
			IEGLJavaValue parent )
	{
		// Consult the adapters.
		for ( IVariableAdapter adapter : getVariableAdapters() )
		{
			IEGLJavaVariable variable = adapter.adapt( javaVariable, frame, info, parent );
			if ( variable != null )
			{
				return variable;
			}
		}
		
		return new EGLJavaVariable( frame.getDebugTarget(), javaVariable, info, frame, parent );
	}
	
	/**
	 * @return the qualified name of the variable with '|' as the delimeter, e.g. parentVarName|childVarName
	 * @throws DebugException
	 */
	public static String getQualifiedName( IEGLJavaVariable var ) throws DebugException
	{
		if ( var == null )
		{
			return null;
		}
		
		if ( var instanceof EGLJavaFunctionContainerVariable )
		{
			return "this"; //$NON-NLS-1$
		}
		
		IEGLJavaValue parent = var.getParentValue();
		if ( parent == null || parent.getParentVariable() == null )
		{
			return var.getName();
		}
		
		StringBuilder buf = new StringBuilder( 100 );
		buf.append( getQualifiedName( parent.getParentVariable() ) );
		buf.append( '|' );
		buf.append( var.getName() );
		return buf.toString();
	}
	
	/**
	 * @return true if the variable is an instance of <code>typeName</code>
	 */
	public static boolean isInstanceOf( IJavaVariable variable, String typeName )
	{
		IJavaValue javaValue = null;
		try
		{
			IValue value = variable.getValue();
			if ( value instanceof IJavaValue )
			{
				javaValue = (IJavaValue)value;
				IJavaType javaType = javaValue.isNull()
						? variable.getJavaType()
						: javaValue.getJavaType();
				if ( javaType instanceof IJavaClassType )
				{
					return isInstanceOf( (IJavaClassType)javaType, typeName, false );
				}
			}
		}
		catch ( DebugException e )
		{
		}
		
		// Maybe the type matches.
		try
		{
			String type = javaValue == null || javaValue.isNull()
					? variable.getReferenceTypeName()
					: javaValue.getReferenceTypeName();
			return typeName.equals( type );
		}
		catch ( DebugException e )
		{
		}
		
		return false;
	}
	
	public static boolean isInstanceOf( IJavaClassType type, String typeName, boolean skipInterfaces ) throws DebugException
	{
		if ( typeName.equals( type.getName() ) )
		{
			return true;
		}
		
		if ( !skipInterfaces )
		{
			for ( IJavaInterfaceType iface : type.getAllInterfaces() )
			{
				if ( typeName.equals( iface.getName() ) )
				{
					return true;
				}
			}
		}
		
		IJavaClassType superType = type.getSuperclass();
		if ( superType != null )
		{
			if ( isInstanceOf( superType, typeName, true ) )
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Invokes a static method in the remote VM.
	 * 
	 * @param thread The EGL thread.
	 * @param frame The Java frame.
	 * @param className The class name contaning the static method.
	 * @param methodName The name of the static method.
	 * @param methodSignature The signature of the static method.
	 * @param args Arguments to be passed to the method, possibly null.
	 * @return the resulting value, or null if the evaluation failed.
	 */
	public static IJavaValue invokeStaticMethod( IEGLJavaThread thread, IJavaStackFrame frame, String className, String methodName,
			String methodSignature, IJavaValue[] args )
	{
		synchronized ( thread.getEvaluationLock() )
		{
			waitForEvaluation( thread.getJavaThread() );
			
			IJavaDebugTarget target = (IJavaDebugTarget)frame.getDebugTarget();
			
			try
			{
				IJavaReferenceType context;
				IJavaObject thisObj = frame.getThis();
				if ( thisObj == null )
				{
					context = frame.getReferenceType();
				}
				else
				{
					context = (IJavaReferenceType)thisObj.getJavaType();
				}
				
				IJavaObject classLoader = context.getClassLoaderObject();
				IJavaClassObject classObj = null;
				IJavaType[] types = target.getJavaTypes( className );
				if ( types != null && types.length > 0 )
				{
					for ( int i = 0; i < types.length; i++ )
					{
						if ( types[ i ] instanceof IJavaReferenceType
								&& isClassLoaderCompatible( classLoader, ((IJavaReferenceType)types[ i ]).getClassLoaderObject(), thread ) )
						{
							classObj = (IJavaClassObject)((IJavaReferenceType)types[ i ]).getClassObject();
							break;
						}
					}
				}
				
				if ( classObj == null )
				{
					// Class not yet loaded.
					final IJavaValue name = target.newValue( className );
					((IJavaObject)name).disableCollection();
					try
					{
						IJavaClassType javaLangClass = getJavaLangClass( target );
						if ( javaLangClass != null )
						{
							IJavaValue[] forNameArgs = new IJavaValue[] { name, target.newValue( true ), classLoader == null
									? target.nullValue()
									: classLoader };
							classObj = (IJavaClassObject)runSendMessage( thread, javaLangClass, forNameArgs, "forName", //$NON-NLS-1$
									"(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;" ); //$NON-NLS-1$
						}
					}
					catch ( CoreException e )
					{
					}
					finally
					{
						((IJavaObject)name).enableCollection();
					}
				}
				
				if ( classObj != null )
				{
					IJavaType instType = classObj.getInstanceType();
					if ( instType instanceof IJavaClassType )
					{
						return runSendMessage( thread, (IJavaClassType)instType, args, methodName, methodSignature );
					}
				}
			}
			catch ( CoreException e )
			{
				EDTDebugCorePlugin.log( e );
			}
		}
		
		return null;
	}
	
	/**
	 * @return the IJavaClassType for java.lang.Class, or null if it hasn't been loaded yet.
	 * @throws CoreException
	 */
	public static IJavaClassType getJavaLangClass( IJavaDebugTarget target ) throws CoreException
	{
		IJavaType[] types = target.getJavaTypes( "java.lang.Class" ); //$NON-NLS-1$
		if ( types == null || types.length != 1 )
		{
			return null;
		}
		return (IJavaClassType)types[ 0 ];
	}
	
	/**
	 * @return true if cl2 is the same as, or a parent of, cl1, or if either is null.
	 */
	public static boolean isClassLoaderCompatible( IJavaObject cl1, IJavaObject cl2, IEGLJavaThread thread ) throws CoreException
	{
		if ( cl1 == null || cl2 == null )
		{
			return true;
		}
		
		IJavaObject toTry = cl1;
		while ( toTry != null )
		{
			if ( toTry.equals( cl2 ) )
			{
				return true;
			}
			toTry = getParentLoader( toTry, thread );
		}
		
		return false;
	}
	
	/**
	 * Returns the parent class loader of the given class loader object or <code>null</code> if none.
	 * 
	 * @param loader class loader object
	 * @return parent class loader or <code>null</code>
	 * @throws CoreException
	 */
	public static IJavaObject getParentLoader( IJavaObject cl, IEGLJavaThread thread ) throws CoreException
	{
		// Some classloaders have their parent in a 'parent' field. This is much faster than sending a message.
		IJavaFieldVariable parent = cl.getField( "parent", false ); //$NON-NLS-1$
		if ( parent != null )
		{
			IJavaValue value = (IJavaValue)parent.getValue();
			return value.isNull()
					? null
					: (IJavaObject)value;
		}
		
		IJavaValue value = runSendMessage( thread, cl, null, "getParent", "()Ljava/lang/ClassLoader;", false ); //$NON-NLS-1$ //$NON-NLS-2$
		return value.isNull()
				? null
				: (IJavaObject)value;
	}
	
	/**
	 * Runs "sendMessage" on the receiver in a synchronized manner. This should be used instead of directly invoking sendMessage because only one
	 * request may be done at a time.
	 * 
	 * @param thread The EGL thread.
	 * @param receiver The object on which to send the message.
	 * @param args The arguments to be passed to the method, possibly null.
	 * @param methodName The name of the method to invoke.
	 * @param methodSignature The signature of the method to invoke.
	 * @param superSend <code>true</code> if the method lookup should begin in this object's superclass
	 * @return the result of invoking the method.
	 * @throws DebugException
	 */
	public static IJavaValue runSendMessage( IEGLJavaThread thread, final IJavaObject receiver, final IJavaValue[] args, final String methodName,
			final String methodSignature, final boolean superSend ) throws DebugException
	{
		// Each thread can only run one evaluation at a time. If we try to run a second, JDT throws an error.
		final IJavaValue[] result = new IJavaValue[ 1 ];
		synchronized ( thread.getEvaluationLock() )
		{
			final IJavaThread javaThread = thread.getJavaThread();
			IEvaluationRunnable eval = new IEvaluationRunnable() {
				public void run( IJavaThread thread, IProgressMonitor monitor ) throws DebugException
				{
					result[ 0 ] = receiver.sendMessage( methodName, methodSignature, args, thread, superSend );
				}
			};
			
			waitForEvaluation( javaThread );
			javaThread.runEvaluation( eval, null, DebugEvent.EVALUATION_IMPLICIT, false );
		}
		return result[ 0 ];
	}
	
	/**
	 * Runs "sendMessage" on the receiver in a synchronized manner. This should be used instead of directly invoking sendMessage because only one
	 * request may be done at a time.
	 * 
	 * @param thread The EGL thread.
	 * @param receiver The type on which to send the message.
	 * @param args The arguments to be passed to the method, possibly null.
	 * @param methodName The name of the method to invoke.
	 * @param methodSignature The signature of the method to invoke.
	 * @return the result of invoking the method.
	 * @throws DebugException
	 */
	public static IJavaValue runSendMessage( IEGLJavaThread thread, final IJavaClassType receiver, final IJavaValue[] args, final String methodName,
			final String methodSignature ) throws DebugException
	{
		// Each thread can only run one evaluation at a time. If we try to run a second, JDT throws an error.
		final IJavaValue[] result = new IJavaValue[ 1 ];
		synchronized ( thread.getEvaluationLock() )
		{
			final IJavaThread javaThread = thread.getJavaThread();
			IEvaluationRunnable eval = new IEvaluationRunnable() {
				public void run( IJavaThread thread, IProgressMonitor monitor ) throws DebugException
				{
					result[ 0 ] = receiver.sendMessage( methodName, methodSignature, args, thread );
				}
			};
			
			waitForEvaluation( javaThread );
			javaThread.runEvaluation( eval, null, DebugEvent.EVALUATION_IMPLICIT, false );
		}
		return result[ 0 ];
	}
	
	/**
	 * Waits until it's safe to run an evaluation.
	 * 
	 * @param thread The Java thread.
	 */
	private static void waitForEvaluation( IJavaThread thread )
	{
		while ( thread.isPerformingEvaluation() || !thread.isSuspended() )
		{
			try
			{
				Thread.sleep( 1 );
			}
			catch ( InterruptedException e )
			{
				return;
			}
		}
	}
	
	/**
	 * @return the variable adapter extensions.
	 */
	public static synchronized IVariableAdapter[] getVariableAdapters()
	{
		if ( variableAdapters == null )
		{
			IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor( EDTDebugCorePlugin.PLUGIN_ID,
					EXTENSION_POINT_VARIABLE_ADAPTERS );
			List<IVariableAdapter> adapters = new ArrayList<IVariableAdapter>( elements.length );
			for ( IConfigurationElement element : elements )
			{
				try
				{
					Object o = element.createExecutableExtension( "class" ); //$NON-NLS-1$
					if ( o instanceof IVariableAdapter )
					{
						adapters.add( (IVariableAdapter)o );
					}
				}
				catch ( CoreException e )
				{
					EDTDebugCorePlugin.log( e );
				}
			}
			
			variableAdapters = new IVariableAdapter[ adapters.size() + 1 ];
			adapters.toArray( variableAdapters );
			variableAdapters[ variableAdapters.length - 1 ] = new DefaultVariableAdapter();
		}
		return variableAdapters;
	}
	
	public static void dispose()
	{
		if ( variableAdapters != null )
		{
			for ( IVariableAdapter adapter : variableAdapters )
			{
				adapter.dispose();
			}
			variableAdapters = null;
		}
	}
}
