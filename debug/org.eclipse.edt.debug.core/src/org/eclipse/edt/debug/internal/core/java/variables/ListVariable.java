package org.eclipse.edt.debug.internal.core.java.variables;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.edt.debug.core.java.IEGLJavaStackFrame;
import org.eclipse.edt.debug.core.java.IEGLJavaValue;
import org.eclipse.edt.debug.core.java.IEGLJavaVariable;
import org.eclipse.edt.debug.core.java.SMAPVariableInfo;
import org.eclipse.edt.debug.internal.core.java.EGLJavaValue;
import org.eclipse.edt.debug.internal.core.java.EGLJavaVariable;
import org.eclipse.edt.debug.internal.core.java.VariableUtil;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.core.IJavaVariable;

/**
 * Represents a java.lang.List.
 */
public class ListVariable extends EGLJavaVariable
{
	
	public ListVariable( IDebugTarget target, IJavaVariable javaVariable, SMAPVariableInfo variableInfo, IEGLJavaStackFrame frame,
			IEGLJavaValue parent )
	{
		super( target, javaVariable, variableInfo, frame, parent );
	}
	
	@Override
	protected IEGLJavaValue createEGLValue( IJavaValue javaValue )
	{
		return new ListValue( getDebugTarget(), (IJavaValue)javaValue, this );
	}
	
	private class ListValue extends EGLJavaValue
	{
		private IVariable[] variables;
		
		public ListValue( IDebugTarget target, IJavaValue value, EGLJavaVariable parent )
		{
			super( target, value, parent );
		}
		
		@Override
		public IVariable[] getVariables() throws DebugException
		{
			boolean recompute = true;
			IVariable[] javaVariables = javaValue.getVariables();
			if ( previousJavaVariables != null )
			{
				if ( javaVariables.length == previousJavaVariables.length )
				{
					recompute = false;
					for ( int i = 0; i < javaVariables.length; i++ )
					{
						if ( javaVariables[ i ] != previousJavaVariables[ i ] || javaVariables[ i ].hasValueChanged() )
						{
							recompute = true;
							break;
						}
					}
				}
			}
			
			if ( recompute )
			{
				if ( javaValue instanceof IJavaObject )
				{
					IJavaValue toArrayValue = ((IJavaObject)javaValue).sendMessage( "toArray", "()[Ljava/lang/Object;", null, parentVariable //$NON-NLS-1$ //$NON-NLS-2$
							.getEGLStackFrame().getEGLThread().getJavaThread(), false );
					if ( toArrayValue != null )
					{
						IVariable[] vars = toArrayValue.getVariables();
						List<IEGLJavaVariable> list = new ArrayList<IEGLJavaVariable>( vars.length );
						
						SMAPVariableInfo parentInfo = parentVariable.getVariableInfo();
						
						String elementType = parentInfo.type;
						int idx = parentInfo.type.indexOf( '<' );
						if ( idx != -1 )
						{
							int idx2 = parentInfo.type.indexOf( '>', idx );
							if ( idx2 != -1 )
							{
								elementType = parentInfo.type.substring( idx + 1, idx2 );
							}
						}
						
						for ( IVariable var : vars )
						{
							if ( var instanceof IJavaVariable )
							{
								SMAPVariableInfo info = new SMAPVariableInfo( var.getName(), var.getName(), elementType, parentInfo.lineDeclared,
										parentInfo.smapEntry );
								list.add( VariableUtil.createEGLVariable( (IJavaVariable)var, info, parentVariable.getEGLStackFrame(), this ) );
							}
						}
						
						variables = list.toArray( new IEGLJavaVariable[ list.size() ] );
					}
				}
				
				if ( variables == null )
				{
					variables = super.getVariables();
				}
				previousJavaVariables = javaVariables;
			}
			return variables;
		}
		
		@Override
		public String getValueString() throws DebugException
		{
			return ""; //$NON-NLS-1$
		}
		
		@Override
		public String computeDetail()
		{
			return ""; //$NON-NLS-1$
		}
	}
}