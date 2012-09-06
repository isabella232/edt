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

package org.eclipse.edt.tests.validation.junit;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.edt.tests.validation.junit.annotations.EglpropertyTest;
import org.eclipse.edt.tests.validation.junit.annotations.Javaobject2Test;
import org.eclipse.edt.tests.validation.junit.annotations.JavaobjectTest;
import org.eclipse.edt.tests.validation.junit.annotations.Javascriptobject2Test;
import org.eclipse.edt.tests.validation.junit.annotations.JavascriptobjectTest;
import org.eclipse.edt.tests.validation.junit.annotations.PropertyTest;
import org.eclipse.edt.tests.validation.junit.bugs.Bug376417Test;
import org.eclipse.edt.tests.validation.junit.bugs.Bug379176Test;
import org.eclipse.edt.tests.validation.junit.expressions.Comparison1Test;
import org.eclipse.edt.tests.validation.junit.expressions.DynamicAccess1Test;
import org.eclipse.edt.tests.validation.junit.ibmi.IBMiCallTests1;
import org.eclipse.edt.tests.validation.junit.ibmi.IBMiProxyFunctionTests;
import org.eclipse.edt.tests.validation.junit.ibmi.IBMiCallTests2;
import org.eclipse.edt.tests.validation.junit.services.ServiceCallTests1;
import org.eclipse.edt.tests.validation.junit.services.ServiceCallTests2;
import org.eclipse.edt.tests.validation.junit.services.ServiceProxyFunctionTests;


public class AllValidationTests {

	public static Test suite() {		
		ValidationTestCase.PRINT_ERRORS_TO_CONSOLE = false;
		
		TestSuite suite = new TestSuite("Validation Tests");
		//$JUnit-BEGIN$
	 
		suite.addTestSuite(EglpropertyTest.class);
		suite.addTestSuite(PropertyTest.class);
		suite.addTestSuite(Comparison1Test.class);
		suite.addTestSuite(DynamicAccess1Test.class);
		suite.addTestSuite(JavaobjectTest.class);
		suite.addTestSuite(JavascriptobjectTest.class);
		suite.addTestSuite(Javaobject2Test.class);
		suite.addTestSuite(Javascriptobject2Test.class);
		suite.addTestSuite(Bug376417Test.class);
		suite.addTestSuite(Bug379176Test.class);
		suite.addTestSuite(IBMiProxyFunctionTests.class);
		suite.addTestSuite(IBMiCallTests1.class);
		suite.addTestSuite(IBMiCallTests2.class);
		suite.addTestSuite(ServiceProxyFunctionTests.class);
		suite.addTestSuite(ServiceCallTests1.class);
		suite.addTestSuite(ServiceCallTests2.class);
///		suite.addTestSuite(SqlTest1Test.class);

		//$JUnit-END$
		return suite;
	}
}
