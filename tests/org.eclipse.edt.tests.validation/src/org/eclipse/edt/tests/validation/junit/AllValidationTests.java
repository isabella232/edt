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

import org.eclipse.edt.tests.validation.junit.annotations.EglpropertyTest;
import org.eclipse.edt.tests.validation.junit.annotations.PropertyTest;
import org.eclipse.edt.tests.validation.junit.callStatement.LocalFunctionCallTestsTest;
import org.eclipse.edt.tests.validation.junit.callStatement.RemoteFunctionCallTestsTest;
import org.eclipse.edt.tests.validation.junit.expressions.Comparison1Test;
import org.eclipse.edt.tests.validation.junit.expressions.DynamicAccess1Test;
import org.eclipse.edt.tests.validation.junit.ibmi.IBMiTest1Test;
import org.eclipse.edt.tests.validation.junit.statements.sql.SqlTest1Test;


public class AllValidationTests {

	public static Test suite() {		
		ValidationTestCase.PRINT_ERRORS_TO_CONSOLE = false;
		
		TestSuite suite = new TestSuite("Validation Tests");
		//$JUnit-BEGIN$
	 
		suite.addTestSuite(LocalFunctionCallTestsTest.class);
		suite.addTestSuite(RemoteFunctionCallTestsTest.class);
		suite.addTestSuite(IBMiTest1Test.class);
		suite.addTestSuite(EglpropertyTest.class);
		suite.addTestSuite(PropertyTest.class);
		suite.addTestSuite(Comparison1Test.class);
		suite.addTestSuite(DynamicAccess1Test.class);
//		suite.addTestSuite(SqlTest1Test.class);

		//$JUnit-END$
		return suite;
	}
}
