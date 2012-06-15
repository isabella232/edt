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
package org.eclipse.edt.debug.internal.core.java.variables;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.edt.debug.core.EDTDebugCoreMessages;
import org.eclipse.edt.debug.core.java.IEGLJavaStackFrame;
import org.eclipse.edt.debug.core.java.IEGLJavaValue;
import org.eclipse.edt.debug.core.java.IEGLJavaVariable;
import org.eclipse.edt.debug.core.java.SMAPVariableInfo;
import org.eclipse.edt.debug.core.java.variables.VariableUtil;
import org.eclipse.edt.debug.internal.core.java.EGLJavaValue;
import org.eclipse.edt.debug.internal.core.java.EGLJavaVariable;
import org.eclipse.edt.runtime.java.eglx.lang.ETimestamp;
import org.eclipse.jdt.debug.core.IJavaClassType;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaFieldVariable;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.core.IJavaVariable;

/**
 * Variable that shows the calendar formatted nicely, using the EDT Java runtime's EString class.
 */
public class CalendarVariable extends EGLJavaVariable
{
	private static final String ESTRING_CLASS = "org.eclipse.edt.runtime.java.eglx.lang.EString"; //$NON-NLS-1$
	private static final String DATE_METHOD = "asStringDate"; //$NON-NLS-1$
	private static final String TIME_METHOD = "asStringTime"; //$NON-NLS-1$
	private static final String TIMESTAMP_METHOD = "asStringTimestamp"; //$NON-NLS-1$
	
	private static final String TIMESTAMP_FORMAT_YEAR = "yyyy"; //$NON-NLS-1$
	private static final String TIMESTAMP_FORMAT_MONTH = "MM"; //$NON-NLS-1$
	private static final String TIMESTAMP_FORMAT_DAY = "dd"; //$NON-NLS-1$
	private static final String TIMESTAMP_FORMAT_HOUR = "HH"; //$NON-NLS-1$
	private static final String TIMESTAMP_FORMAT_MINUTE = "mm"; //$NON-NLS-1$
	private static final String TIMESTAMP_FORMAT_SECOND = "ss"; //$NON-NLS-1$
	private static final String TIMESTAMP_FORMAT_FRACTION1 = "f"; //$NON-NLS-1$
	private static final String TIMESTAMP_FORMAT_FRACTION2 = "ff"; //$NON-NLS-1$
	private static final String TIMESTAMP_FORMAT_FRACTION3 = "fff"; //$NON-NLS-1$
	private static final String TIMESTAMP_FORMAT_FRACTION4 = "ffff"; //$NON-NLS-1$
	private static final String TIMESTAMP_FORMAT_FRACTION5 = "fffff"; //$NON-NLS-1$
	private static final String TIMESTAMP_FORMAT_FRACTION6 = "ffffff"; //$NON-NLS-1$
	
	public CalendarVariable( IDebugTarget target, IJavaVariable javaVariable, SMAPVariableInfo variableInfo, IEGLJavaStackFrame frame,
			IEGLJavaValue parent )
	{
		super( target, javaVariable, variableInfo, frame, parent );
	}
	
	@Override
	protected IEGLJavaValue createEGLValue( IJavaValue javaValue )
	{
		return new EGLJavaValue( getDebugTarget(), (IJavaValue)javaValue, this ) {
			@Override
			public String getValueString() throws DebugException
			{
				if ( javaValue.isNull() )
				{
					return "null"; //$NON-NLS-1$
				}
				
				if ( javaValue instanceof IJavaObject )
				{
					IJavaValue result = null;
					if ( "eglx.lang.EDate".equals( variableInfo.type ) ) //$NON-NLS-1$
					{
						result = formatDate();
					}
					else if ( "eglx.lang.ETime".equals( variableInfo.type ) ) //$NON-NLS-1$
					{
						result = formatTime();
					}
					else
					{
						if ( "eglx.lang.EAny".equals( variableInfo.type ) ) //$NON-NLS-1$
						{
							// Need to find an IJavaFieldVariable, which is what boxed this one.
							IEGLJavaVariable temp = parentVariable;
							while ( temp != null && !(temp.getJavaVariable() instanceof IJavaFieldVariable) )
							{
								if ( temp.getParentValue() == null )
								{
									temp = null;
								}
								else
								{
									temp = temp.getParentValue().getParentVariable();
								}
							}
							
							if ( temp != null && temp.getJavaVariable() instanceof IJavaFieldVariable )
							{
								// Resolve the runtime type so that we know how to format it (date, time, timestamp).
								IJavaObject boxingValue = ((org.eclipse.jdt.debug.core.IJavaFieldVariable)temp.getJavaVariable()).getReceiver();
								String boxingType = boxingValue.getReferenceTypeName();
								if ( boxingValue.getJavaType() instanceof IJavaClassType
										&& VariableUtil.isInstanceOf( (IJavaClassType)boxingValue.getJavaType(),
												"org.eclipse.edt.runtime.java.eglx.lang.EList", true ) ) //$NON-NLS-1$
								{
									// Look for 'signature' field.
									IJavaFieldVariable signature = boxingValue.getField( "signature", false ); //$NON-NLS-1$
									if ( signature != null )
									{
										boxingType = signature.getValue().getValueString();
									}
								}
								
								if ( "org.eclipse.edt.runtime.java.eglx.lang.ETime".equals( boxingType ) //$NON-NLS-1$
										|| "eglx.lang.EList<eglx.lang.ETime>".equals( boxingType ) ) //$NON-NLS-1$
								{
									result = formatTime();
								}
								else if ( "org.eclipse.edt.runtime.java.eglx.lang.EDate".equals( boxingType ) //$NON-NLS-1$
										|| "eglx.lang.EList<eglx.lang.EDate>".equals( boxingType ) ) //$NON-NLS-1$
								{
									result = formatDate();
								}
								else if ( "org.eclipse.edt.runtime.java.eglx.lang.ETimestamp".equals( boxingType ) //$NON-NLS-1$
										|| boxingType.startsWith( "eglx.lang.EList<eglx.lang.ETimestamp" ) ) //$NON-NLS-1$
								{
									// Use the start and end code from the boxing variable, if available. Otherwise use the type
									// to find a pattern.
									IJavaFieldVariable startCode = boxingValue.getField( "startCode", false ); //$NON-NLS-1$
									IJavaFieldVariable endCode = boxingValue.getField( "endCode", false ); //$NON-NLS-1$
									
									if ( startCode != null && endCode != null )
									{
										result = formatTimestamp( (IJavaValue)startCode.getValue(), (IJavaValue)endCode.getValue() );
									}
									else
									{
										result = formatTimestamp( boxingType );
									}
								}
							}
						}
						
						if ( result == null )
						{
							result = formatTimestamp( variableInfo.type );
						}
					}
					
					if ( result == null )
					{
						return EDTDebugCoreMessages.ErrorRetrievingValue;
					}
					return result.getValueString();
				}
				
				return super.getValueString();
			}
			
			protected IJavaValue formatDate()
			{
				return VariableUtil.invokeStaticMethod( getEGLStackFrame().getEGLThread(), getEGLStackFrame().getJavaStackFrame(), ESTRING_CLASS,
						DATE_METHOD, "(Ljava/util/Calendar;)Ljava/lang/String;", new IJavaValue[] { javaValue } ); //$NON-NLS-1$
			}
			
			protected IJavaValue formatTime()
			{
				return VariableUtil.invokeStaticMethod( getEGLStackFrame().getEGLThread(), getEGLStackFrame().getJavaStackFrame(), ESTRING_CLASS,
						TIME_METHOD, "(Ljava/util/Calendar;)Ljava/lang/String;", new IJavaValue[] { javaValue } ); //$NON-NLS-1$
			}
			
			protected IJavaValue formatTimestamp( String type )
			{
				// To format a timestamp we need to mimick the generated code for determining the parameters to pass to the runtime method.
				int startCode;
				int endCode;
				String pattern = null;
				
				int idx = type.lastIndexOf( "eglx.lang.ETimestamp(" ); //$NON-NLS-1$
				if ( idx != -1 )
				{
					int idx2 = type.indexOf( ")", idx ); //$NON-NLS-1$
					if ( idx2 != -1 )
					{
						pattern = type.substring( idx + "eglx.lang.ETimestamp(".length(), idx2 ); //$NON-NLS-1$
					}
				}
				
				if ( pattern == null )
				{
					startCode = ETimestamp.YEAR_CODE;
					endCode = ETimestamp.SECOND_CODE;
				}
				else
				{
					// Start code.
					if ( pattern.startsWith( TIMESTAMP_FORMAT_YEAR ) )
					{
						startCode = ETimestamp.YEAR_CODE;
					}
					else if ( pattern.startsWith( TIMESTAMP_FORMAT_MONTH ) )
					{
						startCode = ETimestamp.MONTH_CODE;
					}
					else if ( pattern.startsWith( TIMESTAMP_FORMAT_DAY ) )
					{
						startCode = ETimestamp.DAY_CODE;
					}
					else if ( pattern.startsWith( TIMESTAMP_FORMAT_HOUR ) )
					{
						startCode = ETimestamp.HOUR_CODE;
					}
					else if ( pattern.startsWith( TIMESTAMP_FORMAT_MINUTE ) )
					{
						startCode = ETimestamp.MINUTE_CODE;
					}
					else if ( pattern.startsWith( TIMESTAMP_FORMAT_SECOND ) )
					{
						startCode = ETimestamp.SECOND_CODE;
					}
					else if ( pattern.startsWith( TIMESTAMP_FORMAT_FRACTION1 ) )
					{
						startCode = ETimestamp.FRACTION1_CODE;
					}
					else
					{
						startCode = ETimestamp.YEAR_CODE;
					}
					
					// End code.
					if ( pattern.endsWith( TIMESTAMP_FORMAT_YEAR ) )
					{
						endCode = ETimestamp.YEAR_CODE;
					}
					else if ( pattern.endsWith( TIMESTAMP_FORMAT_MONTH ) )
					{
						endCode = ETimestamp.MONTH_CODE;
					}
					else if ( pattern.endsWith( TIMESTAMP_FORMAT_DAY ) )
					{
						endCode = ETimestamp.DAY_CODE;
					}
					else if ( pattern.endsWith( TIMESTAMP_FORMAT_HOUR ) )
					{
						endCode = ETimestamp.HOUR_CODE;
					}
					else if ( pattern.endsWith( TIMESTAMP_FORMAT_MINUTE ) )
					{
						endCode = ETimestamp.MINUTE_CODE;
					}
					else if ( pattern.endsWith( TIMESTAMP_FORMAT_SECOND ) )
					{
						endCode = ETimestamp.SECOND_CODE;
					}
					else if ( pattern.endsWith( TIMESTAMP_FORMAT_FRACTION6 ) )
					{
						endCode = ETimestamp.FRACTION6_CODE;
					}
					else if ( pattern.endsWith( TIMESTAMP_FORMAT_FRACTION5 ) )
					{
						endCode = ETimestamp.FRACTION5_CODE;
					}
					else if ( pattern.endsWith( TIMESTAMP_FORMAT_FRACTION4 ) )
					{
						endCode = ETimestamp.FRACTION4_CODE;
					}
					else if ( pattern.endsWith( TIMESTAMP_FORMAT_FRACTION3 ) )
					{
						endCode = ETimestamp.FRACTION3_CODE;
					}
					else if ( pattern.endsWith( TIMESTAMP_FORMAT_FRACTION2 ) )
					{
						endCode = ETimestamp.FRACTION2_CODE;
					}
					else if ( pattern.endsWith( TIMESTAMP_FORMAT_FRACTION1 ) )
					{
						endCode = ETimestamp.FRACTION1_CODE;
					}
					else
					{
						endCode = ETimestamp.SECOND_CODE;
					}
				}
				
				IJavaDebugTarget javaTarget = getEGLJavaDebugTarget().getJavaDebugTarget();
				return formatTimestamp( javaTarget.newValue( startCode ), javaTarget.newValue( endCode ) );
			}
			
			protected IJavaValue formatTimestamp( IJavaValue startCode, IJavaValue endCode )
			{
				return VariableUtil.invokeStaticMethod( getEGLStackFrame().getEGLThread(), getEGLStackFrame().getJavaStackFrame(), ESTRING_CLASS,
						TIMESTAMP_METHOD, "(Ljava/util/Calendar;II)Ljava/lang/String;", new IJavaValue[] { javaValue, startCode, endCode } ); //$NON-NLS-1$
			}
			
			@Override
			public IVariable[] getVariables()
			{
				return VariableUtil.EMPTY_VARIABLES;
			}
			
			@Override
			public boolean hasVariables()
			{
				return false;
			}
			
			@Override
			public String computeDetail()
			{
				try
				{
					return getValueString();
				}
				catch ( DebugException e )
				{
					return e.getLocalizedMessage();
				}
			}
		};
	}
	
	@Override
	protected boolean shouldCheckJavaElementAdapter()
	{
		return false;
	}
}
