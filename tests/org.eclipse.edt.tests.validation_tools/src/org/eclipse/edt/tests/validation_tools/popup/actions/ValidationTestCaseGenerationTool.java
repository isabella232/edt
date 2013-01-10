/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
/*
 * Created on Feb 23, 2004
 */
package org.eclipse.edt.tests.validation_tools.popup.actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Dave Murray, Albert Ho
 */
public class ValidationTestCaseGenerationTool {

	static final String NEWLINE = System.getProperty( "line.separator" );
	
	private static boolean isVAGCompatible = false;
	private static boolean isNoError = false;
	private static boolean isIgnoreCase = false;

	public static String generateFile(String testTag, File file, String projectName) {
		isVAGCompatible = false;
		isNoError = false;
		
		if( (testTag.indexOf( projectName + File.separator + "EGLSource" + File.separator ) == -1)
			 || !testTag.toLowerCase().endsWith( ".egl" )) {
			return( "The file \"" + testTag + "\" is not valid. Select a .egl file that lives somewhere under the " + projectName + "/EGLSource directory.");
		}
		
		String EGLSOURCE_DIR;
		String TEST_FOLDER_AND_EGLSOURCE_DIR;
		String PACKAGE;
		String VALIDATION_TEST_CASE_NAME;
		String PROJECT_SLASH_EGLSOURCE;
		String PROJECT_SLASH_JUNIT;
		
		EGLSOURCE_DIR = projectName + File.separator + "EGLSource" + File.separator;
		TEST_FOLDER_AND_EGLSOURCE_DIR = "EGLSource/";
		PACKAGE = "org.eclipse.edt.tests.validation.junit";
		VALIDATION_TEST_CASE_NAME = "org.eclipse.edt.tests.validation.junit.ValidationTestCase";
		PROJECT_SLASH_EGLSOURCE = projectName + "/EGLSource/";
		PROJECT_SLASH_JUNIT = projectName + "/src/org/eclipse/edt/tests/validation/junit";
		
        String testEGLFileName = testTag.substring( testTag.indexOf( EGLSOURCE_DIR ) + EGLSOURCE_DIR.length() );
        String packageName = testEGLFileName.indexOf( File.separator ) == -1 ?
        	null :
        	testEGLFileName.substring( 0, testEGLFileName.lastIndexOf( File.separatorChar ) ).replace( File.separatorChar, '.' );
        testEGLFileName = TEST_FOLDER_AND_EGLSOURCE_DIR + (File.separatorChar == '/' ? testEGLFileName : testEGLFileName.replace( File.separatorChar, '/' ));
        testTag = testEGLFileName.substring( testEGLFileName.lastIndexOf( '/' ) + 1 );
        
//        System.out.println( testEGLFileName + ", " + testTag + ", " + packageName );
//        System.exit( 0 );
        
		if( !file.exists() ) {
			return( "The file \"" + testTag + "\" is not valid, since the file \"" + testEGLFileName + "\" does not exist.");
		}
		
		List thingsToCheck = getChecks( file );
		
		String outputClassName = Character.toUpperCase( testTag.charAt( 0 ) ) + testTag.substring( 1, testTag.indexOf( '.' ) ) + "Test";
		String outputFilename = file.getAbsolutePath().replace( File.separatorChar, '/' ).substring( 0, file.getAbsolutePath().replace( File.separatorChar, '/' ).indexOf(PROJECT_SLASH_EGLSOURCE) ) + PROJECT_SLASH_JUNIT + (packageName == null ? "" : "/" + packageName.replace( '.', '/' )) + "/" + outputClassName + ".java";
		File outputFile = new File( outputFilename );
		boolean fileExists = outputFile.exists();
	
		StringBuffer contents = new StringBuffer();
		appendLine( contents, "package " + PACKAGE + (packageName == null ? "" : "." + packageName) + ";" );
		appendLine( contents, "" );
		if( !thingsToCheck.isEmpty() ) {
			appendLine( contents, "import java.util.List;" );
			appendLine( contents, "" );
		}
		if( packageName != null ) {
			appendLine( contents, "import " + VALIDATION_TEST_CASE_NAME + ";" );
			appendLine( contents, "" );
		}
		appendLine( contents, "/*" );
		appendLine( contents, " * A JUnit test case for the file " + testEGLFileName );
		appendLine( contents, " */" );
		appendLine( contents, "public class " + outputClassName + " extends ValidationTestCase {" );
		appendLine( contents, "" );
		appendLine( contents, "\tpublic " + outputClassName + "() {" );
		
		String parms =  "\"" + testEGLFileName + "\"";
		if (isVAGCompatible) {
		    parms = parms + ", true";
		}
		else {
		    parms = parms + ", false";
		}
		appendLine(contents, "\t\tsuper( " + parms + " );");
		
		appendLine( contents, "\t}" );
		if( isNoError ) {
			appendLine( contents, "" );
			appendLine( contents, "\t/*" );
			appendLine( contents, "\t * This file is expected to contain no real* validation errors." );
			appendLine( contents, "\t *" );
			appendLine( contents, "\t * * Errors dealing with multiple generatable parts or part name not matching filename are allowed." );
			appendLine( contents, "\t */" );
			appendLine( contents, "\tpublic void testNoValidationErrorsInTest() {" );
			appendLine( contents, "\t\tassertFalse( validationErrorsInTest() );" );
			appendLine( contents, "\t}" );
		}
		for( Iterator iter = thingsToCheck.iterator(); iter.hasNext(); ) {
			Check chk = (Check) iter.next();
			appendLine( contents, "" );
			appendLine( contents, "\t/*" );
			appendLine( contents, "\t * " + chk.lineContents );
			appendLine( contents, "\t * " + chk.expectedMessages + " validation message" + (chk.expectedMessages == 1 ? " is" : "s are") + " expected." );
			for( Iterator substringsIter = chk.expectedSubstrings.iterator(); substringsIter.hasNext(); ) {
				String ss = (String) substringsIter.next();
				appendLine( contents, "\t * " + (chk.expectedMessages == 1 ? "It" : "One message") + " is expected to contain \"" + ss + "\"." ); 
			}			 
			appendLine( contents, "\t */" );
			appendLine( contents, "\tpublic void testLine" + chk.lineNumber + "() {" );
			appendLine( contents, "\t\tList messages = getMessagesAtLine( " + chk.lineNumber + " );" );
			appendLine( contents, "\t\tassertEquals( " + chk.expectedMessages + ", messages.size() );" );
			boolean firstTime = true;
			for( Iterator substringsIter = chk.expectedSubstrings.iterator(); substringsIter.hasNext(); ) {
				appendLine( contents, "\t\t" );

				if( !firstTime ) {
					appendLine( contents, "\t\tmessages.remove( messageWithSubstring );" );
				}
				String ss = (String) substringsIter.next();
				if(isIgnoreCase) {
					appendLine( contents, "\t\t" + (firstTime ? "Object " : "" ) + "messageWithSubstring = messageWithSubstring( messages, \"" + ss + "\", true );" );
				}
				else {
					appendLine( contents, "\t\t" + (firstTime ? "Object " : "" ) + "messageWithSubstring = messageWithSubstring( messages, \"" + ss + "\" );" );
				}
				appendLine( contents, "\t\tif( messageWithSubstring == null ) fail( \"No message with substring \\\"" + ss + "\\\" was issued.\" );" );				
				firstTime = false;
			}
			appendLine( contents, "\t}" );				
		}
		appendLine( contents, "}" ); 
		
		Writer writer = null;
		try {
			if( !fileExists ) {
				outputFile.getParentFile().mkdirs();
				outputFile.createNewFile();
			}
			writer = new BufferedWriter( new FileWriter( outputFile ) );
			writer.write( contents.toString() );
			//System.out.println( "The file " + outputFilename + " has been successfully " + (fileExists ? "updated" : "created") + "." );
			writer.close();			
		}
		catch( IOException e ) {
			System.err.println( "An IO exception was thrown while writing contents of file " + outputFilename + ":" );
			e.printStackTrace();
		}
		finally {
			try {
				writer.close();
			}
			catch( IOException e ) {}
		}
		
		return null;
    }

    private static void appendLine( StringBuffer sb, String line ) {
		sb.append( line + NEWLINE );	
	}
	
	private static class Check {
		int lineNumber;
		String lineContents;
		int expectedMessages;
		List expectedSubstrings;
		
		Check( int lineNumber, String lineContents, int expectedMessages, List expectedSubstrings ) {
			this.lineNumber = lineNumber;
			this.lineContents = lineContents;
			this.expectedMessages = expectedMessages;
			this.expectedSubstrings = expectedSubstrings;
		}
	}
	
	public static String removeCurlyContentsInComments( String s ) {
		if( s == null ) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		boolean copyChars = true;
		boolean foundOneSlash = false;
		boolean foundTwoSlashes = false;
		for( int i = 0; i < s.length(); i++ ) {
			char nextChar = s.charAt( i );
			if( nextChar == '/' ) {
				if( foundOneSlash ) {
					foundTwoSlashes = true;
				}
				else {
					foundOneSlash = true;
				}
			}
			else {
				foundOneSlash = false;
			}
			if( nextChar == '{' && foundTwoSlashes ) {
				copyChars = false;
			}
			else if( nextChar == '}' && foundTwoSlashes ) {
				copyChars = true;
			}			
			else if( copyChars ) {
				sb.append( nextChar == '\\' ? "\\\\" : Character.toString( nextChar ) );	
			}	
		}
		return sb.toString();
	}
	
	private static List getChecks( File file ) {
		List checks = new ArrayList();
		BufferedReader reader = null;

		try {
			reader = new BufferedReader( new FileReader( file ) );
			String line = removeCurlyContentsInComments( reader.readLine() );
			
			if( line.trim().startsWith( "//" ) ) {
				while( line.startsWith( "/" ) ) {
					line = line.substring( 1 );
				}
				StringTokenizer st = new StringTokenizer( line );
				while( st.hasMoreTokens() ) {
					String next = st.nextToken();
					if( next.equalsIgnoreCase( "vag" ) ) {
						isVAGCompatible = true;
					}
					else if( next.equalsIgnoreCase( "no_error" ) ) {
						isNoError = true;
					}
					else if( next.equalsIgnoreCase( "ignore_case" ) ) {
						isIgnoreCase = true;
					}
				}								
			}
			 
			int lineNum = 1;
			while( line != null ) {

				
				if( line.indexOf( "//" ) != -1 && !line.trim().startsWith( "//" ) && !line.trim().endsWith( "//" ) ) {
					int lastIndexOfSlashes = line.lastIndexOf( "//" );
					String rest = line.substring( lastIndexOfSlashes + 2 ).trim();
					try {
						int expectedMessages = -1;
						List expectedSubstrings = new ArrayList();
						boolean skipCheck = false;
						
						StringBuffer sb = new StringBuffer();
						int i = 1;
						char ch = 0;
						if( rest.length() == 0 ) {
							sb.append( "0" );
						}
						else if( rest.length() == 1 ) {
							sb.append( rest.charAt( 0 ) );
						}
						else {
							skipCheck = true;
							for( i = 0; i < rest.length() && Character.isDigit( rest.charAt( i ) ); i++ ) {
								skipCheck = false;
								sb.append( rest.charAt( i ) );
							}
						}
						
						if (!skipCheck) {
							if( !sb.toString().equals( "" ) ) {
								expectedMessages = Integer.parseInt( sb.toString() );
							}
							
							if (rest.length() > 0) {
								rest = ((Character.isDigit( ch ) ? "" : Character.toString( ch ) ) + rest.substring( i )).trim();
							}
							StringTokenizer st = new StringTokenizer( rest, "|" );
							while( st.hasMoreTokens() ) {
								String s = st.nextToken();
								StringBuffer sb2 = new StringBuffer();
								for( int j = 0; j < s.length(); j++ ) {
									sb2.append( s.charAt( j ) == '"' ? "\\\"" : Character.toString( s.charAt( j )));
								}
								expectedSubstrings.add( sb2.toString().trim() );
							}
							
							String lineContents = line.substring( 0, lastIndexOfSlashes );
							
							checks.add( new Check(
								lineNum,
								lineContents.trim(),
								expectedMessages != -1 ? expectedMessages : expectedSubstrings.size(),
								expectedSubstrings ) );
						}
					}
					catch( NumberFormatException e ) {
					}
				}
				line = removeCurlyContentsInComments( reader.readLine() );
				lineNum += 1;	
			}			
		}
		catch( Exception e ) {
			System.err.println( "An exception was thrown while reading the file " + file.getPath() + ":" );
			e.printStackTrace();
		}	
		finally {
			try {
				reader.close();
			}
			catch (IOException e) {}
		}
		
		return checks;		
	}
}
