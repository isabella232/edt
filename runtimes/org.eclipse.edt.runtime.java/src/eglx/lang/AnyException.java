/*******************************************************************************
 * Copyright © 2006, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package eglx.lang;

import java.lang.reflect.Field;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.TypeConstraints;
import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.util.JavartUtil;

public class AnyException extends RuntimeException implements eglx.lang.EAny {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	protected String message;

	/**
	 * The message ID.
	 */
	protected String messageID;

	public AnyException() {
		this.message = "";
		this.messageID = "";
		ezeInitialize();
	}

	public AnyException(String id, String message) {
		super( message );
		this.message = message;
		this.messageID = id;
		ezeInitialize();
	}

	public Object clone() throws CloneNotSupportedException {
		AnyException ezeClone = (AnyException) super.clone();
		return ezeClone;
	}

	@Override
	public void ezeInitialize() {}

	@Override
	public Object ezeGet(String name) throws AnyException {
		try {
			Field field = this.getClass().getField(name);
			Object value = field.get(this);
			return value;
		}
		catch (Exception e) {
			DynamicAccessException dax = new DynamicAccessException();
			dax.key = name;
			dax.initCause( e );
			throw dax.fillInMessage( Message.DYNAMIC_ACCESS_FAILED, name, ezeName() );
		}
	}

	@Override
	public Object ezeGet(int index) throws AnyException {
		TypeCastException tcx = new TypeCastException();
		tcx.castToName = "list";
		Object unboxed = ezeUnbox();
		tcx.actualTypeName = unboxed.getClass().getName();
		throw tcx.fillInMessage( Message.CONVERSION_ERROR, unboxed, tcx.actualTypeName,
				tcx.castToName );
	}

	@Override
	public String ezeName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void ezeSet(String name, Object value) throws AnyException {
		try {
			Field field = this.getClass().getField(name);
			field.set(this, value);
		}
		catch (Exception e) {
			DynamicAccessException dax = new DynamicAccessException();
			dax.key = name;
			dax.initCause( e );
			throw dax.fillInMessage( Message.DYNAMIC_ACCESS_FAILED, name, ezeName() );
		}
	}

	@Override
	public TypeConstraints ezeTypeConstraints(String fieldName) {
		return null;
	}

	@Override
	public String ezeTypeSignature() {
		return this.getClass().getName();
	}

	public String toString() {
		String msg = getMessage();
		if (msg.length() == 0) {
			return ezeTypeSignature();
		} else {
			String typeSig = ezeTypeSignature();
			return new StringBuilder(typeSig.length() + 1 + msg.length()).append(typeSig).append(' ').append(msg).toString();
		}
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Because the message is a non-nullable field of AnyException, this method
	 * never returns null.  If there's no message, it returns the empty string. 
	 */
	@Override
	public String getMessage() {
		if ( message != null && message.length() > 0 )
			return message;

		String superMessage = super.getMessage();
		if ( superMessage != null && superMessage.length() > 0 )
			return superMessage;
		
		return "";
	}
	
	/**
	 * @return The ID of the error message.
	 */
	public String getMessageID()
	{
		return messageID;
	}
	
	public void setMessageID(String id) {
		this.messageID = id;
	}

	@Override
	public AnyException ezeUnbox() {
		return this;
	}

	/**
	 * This method sets the ID and message of this exception.  It's normally used
	 * after an exception is created and values are assigned to its fields, other 
	 * than message and messageID.  This exception is returned so that it may easily
	 * be thrown, as shown in the following example. 
	 * <pre><code>
	 * DynamicAccessException dax = new DynamicAccessException();
	 * dax.key = name;
	 * throw dax.fillInMessage( Message.DYNAMIC_ACCESS_FAILED, name, ezeName() );
	 * </code></pre>
	 * 
	 * @param id       the message ID.
	 * @param inserts  the inserts for the message, may be null.
	 * @return this exception.
	 */
	public AnyException fillInMessage( String id, Object... inserts )
	{
		setMessageID( id );
		setMessage( JavartUtil.errorMessage( id, inserts ) );
		return this;
	}
	
	/**
	 * The usual implementation of this method is VERY expensive.  To improve
	 * performance, at the cost of losing stack traces from our exceptions, set 
	 * the system property org.eclipse.edt.javart.StackTraces to false.  
	 *
	 * @return this object.
	 */
	public Throwable fillInStackTrace()
	{
		if ( STACK_TRACES )
		{
			return super.fillInStackTrace();
		}
		return this;
	}
	
	/**
	 * Determines if fillInStackTrace is optimized or not.
	 */
	public static final boolean STACK_TRACES = !"false".equalsIgnoreCase( System.getProperty( "org.eclipse.edt.javart.StackTraces" ) );
}
