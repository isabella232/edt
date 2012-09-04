/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.tests.validation.junit.ibmi;

import java.util.List;

import org.eclipse.edt.tests.validation.junit.ValidationTestCase;

/*
 * A JUnit test case for the file EGLSource/ibmi/IBMiTest1.egl
 */
public class IBMiCallTest1Test extends ValidationTestCase {

	public IBMiCallTest1Test() {
		super( "EGLSource/ibmi/IBMiCallTests.egl", false );
	}

	/*
	 * call f10(i1) using conn;//target function has no ibmiProgram;
	 * 1 validation message is expected.
	 * It is expected to contain "The function {0} must be defined with the IBMiProgram annotation.".
	 */
	public void testLine9() {
		List messages = getMessagesAtLine( 9 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "must be defined with the IBMiProgram annotation" );
		if( messageWithSubstring == null ) fail( "No message with substring \"must be defined with the IBMiProgram annotation\" was issued." );
	}

	/*
	 * call f9(i1);//can't resolve connection
	 * 1 validation message is expected.
	 * It is expected to contain "An IBM i call statement requires a connection, the using clause and the target function @Resource are missing.".
	 */
	public void testLine14() {
		List messages = getMessagesAtLine( 14 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "An IBM i call statement requires a connection, the using clause and the target function @Resource are missing." );
		if( messageWithSubstring == null ) fail( "No message with substring \"An IBM i call statement requires a connection, the using clause and the target function @Resource are missing.\" was issued." );
	}

	/*
	 * call f9(i1);//wrong data type
	 * 1 validation message is expected.
	 * It is expected to contain "The argument i1 cannot be passed to the inOut or Out parameter p1 of the function f9".
	 */
	public void testLine19() {
		List messages = getMessagesAtLine( 19 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The types boolean and int are not reference compatible" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The types boolean and int are not reference compatible\" was issued." );
	}

	/*
	 * call f9(i1) using conn returning to f9;//no callback
	 * 1 validation message is expected.
	 * It is expected to contain "A "returning to" or "onexception" expression is not allowed for a call to a local function.".
	 */
	public void testLine24() {
		List messages = getMessagesAtLine( 24 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "A \"returning to\" or \"onexception\" expression is not allowed for a call to a local function." );
		if( messageWithSubstring == null ) fail( "No message with substring \"A \"returning to\" or \"onexception\" expression is not allowed for a call to a local function.\" was issued." );
	}

	/*
	 * call f9(i1) using conn onexception f9;//no onexception
	 * 1 validation message is expected.
	 * It is expected to contain "A "returning to" or "onexception" expression is not allowed for a call to a local function.".
	 */
	public void testLine29() {
		List messages = getMessagesAtLine( 29 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "A \"returning to\" or \"onexception\" expression is not allowed for a call to a local function." );
		if( messageWithSubstring == null ) fail( "No message with substring \"A \"returning to\" or \"onexception\" expression is not allowed for a call to a local function.\" was issued." );
	}

	/*
	 * call fp9(i1) using conn returns(i2);//fp9 doesn't return anything
	 * 1 validation message is expected.
	 * It is expected to contain "The call statement cannot specify a returns value, because the function fp9 does not return a value".
	 */
	public void testLine34() {
		List messages = getMessagesAtLine( 34 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The call statement cannot specify a returns value, because the function fp9 does not return a value" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The call statement cannot specify a returns value, because the function fp9 does not return a value\" was issued." );
	}

	/*
	 * call f9(i1) using conn return i2;//f9 doesn't return anything
	 * 1 validation message is expected.
	 * It is expected to contain "A "returning to" or "onexception" expression is not allowed for a call to a local function.".
	 */
	public void testLine40() {
		List messages = getMessagesAtLine( 40 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The return type int of the function fp8 is not compatible with the type time of the returns expression i2 in the Call statement" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The return type int of the function fp8 is not compatible with the type time of the returns expression i2 in the Call statement\" was issued." );
	}

	/*
	 * call f9(i1) using conn return i2;//f9 doesn't return anything
	 * 1 validation message is expected.
	 * It is expected to contain "A "returning to" or "onexception" expression is not allowed for a call to a local function.".
	 */
	public void testLine46() {
		List messages = getMessagesAtLine( 46 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "The call statement must specify a returns expression" );
		if( messageWithSubstring == null ) fail( "No message with substring \"The call statement must specify a returns expression\" was issued." );
	}

	/*
	 * call Service1.fp9(i1) using conn;//target function is a service function
	 * 1 validation message is expected.
	 * It is expected to contain "A service cannot be use as a qualifier.".
	 */
	public void testLine51() {
		List messages = getMessagesAtLine( 51 );
		assertEquals( 1, messages.size() );
		
		Object messageWithSubstring = messageWithSubstring( messages, "A service cannot be use as a qualifier." );
		if( messageWithSubstring == null ) fail( "No message with substring \"A service cannot be use as a qualifier.\" was issued." );
	}

}
