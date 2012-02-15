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

package org.eclipse.edt.tests.validation.junit;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.edt.tests.validation.junit.callStatement.LocalFunctionCallTestsTest;
import org.eclipse.edt.tests.validation.junit.callStatement.RemoteFunctionCallTestsTest;
import org.eclipse.edt.tests.validation.junit.ibmi.IBMiTest1Test;


public class AllValidationTests {

	public static Test suite() {		
		ValidationTestCase.PRINT_ERRORS_TO_CONSOLE = false;
		
		TestSuite suite = new TestSuite("Validation Tests");
		//$JUnit-BEGIN$
	 
//No error for reference to ambiguous part		suite.addTestSuite(AbstractFunctionTest.class);
		suite.addTestSuite(LocalFunctionCallTestsTest.class);
		suite.addTestSuite(RemoteFunctionCallTestsTest.class);
		suite.addTestSuite(IBMiTest1Test.class);

		//$JUnit-END$
		return suite;
	}
}
