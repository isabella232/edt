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
// Generated at Fri Aug 27 15:34:58 EDT 2010 by EGL 8.0.0.v20100804_2330
// No generator APARs installed.
package test.gen;

import java.math.BigDecimal;
import java.util.List;

import org.eclipse.edt.javart.JavartException;
import org.eclipse.edt.javart.RunUnit;
import org.eclipse.edt.javart.resources.ProgramBase;
import org.eclipse.edt.javart.resources.RunUnitBase;
import org.eclipse.edt.javart.resources.StartupInfo;
import org.eclipse.edt.runtime.java.egl.lang.EList;

public class Rec1 extends ProgramBase
{
	private static final long serialVersionUID = 80L;
	
	public static StartupInfo _startupInfo()
	{
		return new StartupInfo( "Rec1", "test/gen/Rec1.properties", false );
	}
	public static void main(String[] args) {
		try {
			StartupInfo info = _startupInfo();
			info.setArgs( args );
			RunUnit ru = new RunUnitBase( info );
			ru.start( new Rec1( ru ), args );
			ru.exit();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// f1_int int;
	public int f1_int;
	
	// f2_int_n int?;
	public Integer f2_int_n;
	
	// f3_string string;
	public String f3_string;
	
	// f4_dec_10_2 decimal(10,2);
	public java.math.BigDecimal f4_dec_10_2;
	
	// f5_list_dec_10_2 decimal(10,2)[1];
	public egl.lang.EList<java.math.BigDecimal> f5_list_dec_10_2;
	
	// f6_list_dec_5_1_n decimal(5,1)?[1];
	public egl.lang.EList<java.math.BigDecimal> f6_list_dec_5_1_n;
	
	public Rec1(RunUnit ru) throws JavartException {
		super(ru);
		ezeInitialize();
	}
	
	protected void ezeInitialize() throws JavartException
	{
		f1_int = 0;
		f2_int_n = null;
		f3_string = "";
		f4_dec_10_2 = BigDecimal.ZERO;
		f5_list_dec_10_2 = new EList<BigDecimal>();
		f5_list_dec_10_2.add(BigDecimal.ZERO);
		f6_list_dec_5_1_n = new EList<BigDecimal>();
		f6_list_dec_5_1_n.add(null);
		
		// RETURN
	}
	
	public Object clone() throws java.lang.CloneNotSupportedException
	{
		Rec1 ezeClone = (Rec1)super.clone();
		ezeClone.f1_int = f1_int;
		ezeClone.f2_int_n = f2_int_n;
		return ezeClone;
	}
	
	public int getf1()
	{
		// Standard form for access to primitive
		return this.f1_int;
	}
	public void setf1( int ezeValue ) throws org.eclipse.edt.javart.JavartException
	{
		// Standard form for assigning primitive to primitive
		this.f1_int = ezeValue;
	}
	
	// Removed AsInteger stuff as Java boxing/unboxing handles this
	public Integer getf2()
	{
		return this.f2_int_n;
	}

	public void setf2( Integer ezeValue ) throws org.eclipse.edt.javart.JavartException
	{
		// Standard form for assigning to nullable field
		f2_int_n = ezeValue;
	}

	// Removed AsInteger stuff as Java boxing/unboxing handles this
	public java.lang.String getf3_string()
	{
		return this.f3_string;
	}
	
	public void setf3_string( java.lang.String ezeValue ) throws org.eclipse.edt.javart.JavartException
	{
		this.f3_string = ezeValue;
	}
	
	public void main(List<String> args) throws Exception
	{
		assignmentTest();
		return;
	}

	
	void assignmentTest() throws Exception {
		// EGL: f1_int = 5;
		// LHS && RHS are primitve so this is a straight assignment
		// RHS expressions always go to the primitive type - Literals follow this principle
		f1_int = 5;
		
		// EGL: f1_int = 123.45;
		// RHS is a BigDecimal literal; will cause an AS operation in the model to be added
		f1_int = org.eclipse.edt.runtime.java.egl.lang.EInt32.asInt32(this, new java.math.BigDecimal( new java.math.BigInteger( new byte[] { 0x30, 0x39 } ), 2 ));
		
		// EGL: f1_int = f1_int_n;
		// RHS is nullable int so will by typed to java.lang.Integer
		// must check for null value.  Also note that the RHS expression does
		// not have side effects so no need for creating temp var.
		f1_int = f2_int_n == null ? 0 : f2_int_n;
		
		// EGL: f1_int_n = f1_int;
		// LHS is nullable int so will by typed to java.lang.Integer
		// RHS will return primitive int and normal java boxing will take over
		f2_int_n = f1_int;

		// EGL: f1_int_n = "123";
		f2_int_n = org.eclipse.edt.runtime.java.egl.lang.EInt32.asInt32(this, org.eclipse.edt.runtime.java.egl.lang.EString.asNumber(this, "123"));
		
		// EGL: f2_string = "123"
		f3_string = "123";
		
		// EGL: f1_int = f3_string;
		// Implicit conversion of string to int will cause AS operation to be added in the model
		f1_int = org.eclipse.edt.runtime.java.egl.lang.EInt32.asInt32(this, org.eclipse.edt.runtime.java.egl.lang.EString.asNumber(this, f3_string));
		
		// EGL: f4_dec_10_2 = f6_list_dec_5_1_n[1];
		// RHS nullable assignment to non-nullable LHS
		BigDecimal $ezetemp1 = org.eclipse.edt.runtime.java.egl.lang.EDecimal.asDecimal(this, f6_list_dec_5_1_n.get(0), 10, 2);
		f4_dec_10_2 =  $ezetemp1 == null ? BigDecimal.ZERO.setScale(2) : $ezetemp1;
		
		// EGL: f4_dec_10_2 = "123.456";
		// Implicit conversion of string to decimal(10, 2) will cause AS operation to be added in the model
		f4_dec_10_2 = org.eclipse.edt.runtime.java.egl.lang.EDecimal.asDecimal(this, "123.456", 10, 2);
		f4_dec_10_2 = org.eclipse.edt.runtime.java.egl.lang.EDecimal.asDecimal(this, 123, 10, 2);
		
		f6_list_dec_5_1_n.set(0, org.eclipse.edt.runtime.java.egl.lang.EDecimal.asDecimal(this, org.eclipse.edt.runtime.java.egl.lang.EDecimal.asDecimal(this, "123.456"), 5, 1));
		
		f4_dec_10_2 = BigDecimal.valueOf(123456789, 2);
		// EGL: f6_list_dec_5_1_n[1] = f4_dec_10_2;
		f6_list_dec_5_1_n.set(0, org.eclipse.edt.runtime.java.egl.lang.EDecimal.asDecimal(this, f4_dec_10_2, 5, 1));
	}
}
