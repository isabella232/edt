/*******************************************************************************
 * Copyright © 2006, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.javart;


import eglx.lang.AnyValue;

/**
 * Exception to be thrown when one program transfers to another.
 */
public class Transfer extends ControlFlow
{
	/**
	 * The version ID used in serialization.
	 */
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	/**
	 * Which program to transfer to.
	 */
	public String name;
	
	/**
	 * Optional data to initialize the new program's input record.
	 */
	public AnyValue input;
	
	/**
	 * True/false for toTransaction/toProgram.
	 */
	public boolean toTransaction;
	
	/**
	 * Value for the new program's transactionId, or null.
	 */
	public String transactionId;
	
	/**
	 * VGUIRecord or TextForm to be displayed when transferring for a Show statement.
	 */
	public AnyValue ui;
	
	/**
	 * Create a Transfer object for a transfer statement.
	 * 
	 * @param name   which program to transfer to.
	 * @param input  optional data to initialize the new program's input record.
	 * @param toTransaction  true/false for toTransaction/toProgram.
	 * @param transactionId  value for the new program's transactionId, or null.  
	 */
	public Transfer( String name, AnyValue input, boolean toTransaction,
			String transactionId )
	{
		this.name = name;
		this.input = input;
		this.toTransaction = toTransaction;
		this.transactionId = transactionId;
	}

	/**
	 * Create a Transfer object for a show statement.
	 * 
	 * @param name   which program to transfer to.
	 * @param input  optional data to initialize the new program's input record.
	 * @param toTransaction  true/false for toTransaction/toProgram.
	 * @param ui     the VGUIRecord or TextForm to send to the screen.   
	 * 
	 */
    public Transfer( String name, AnyValue input, AnyValue ui, boolean toTransaction )
	{
		this.name = name;
		this.input = input;
		this.toTransaction = toTransaction;
		this.ui = ui;
	}
}
