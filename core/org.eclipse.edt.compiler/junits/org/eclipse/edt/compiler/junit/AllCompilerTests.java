package org.eclipse.edt.compiler.junit;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllCompilerTests
{
	public static Test suite() throws Exception
	{
		TestSuite suite = new TestSuite( "Tests for the EDT Compiler" );
		
		// $JUnit-BEGIN$
		suite.addTestSuite( DummyTest.class );
		// $JUnit-END$
		
		return suite;
	}
}
