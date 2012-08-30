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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.TreeSet;

import java_cup.runtime.Scanner;
import junit.framework.TestCase;

import org.eclipse.edt.compiler.Processor;
import org.eclipse.edt.compiler.core.ast.AccumulatingSyntaxErrorRequestor;
import org.eclipse.edt.compiler.core.ast.ErrorCorrectingParser;
import org.eclipse.edt.compiler.core.ast.ISyntaxErrorRequestor;
import org.eclipse.edt.compiler.core.ast.Lexer;
import org.eclipse.edt.compiler.core.ast.SyntaxError;
import org.eclipse.edt.compiler.internal.core.builder.DefaultProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.sdk.compile.ISDKProblemRequestorFactory;
import org.eclipse.edt.compiler.internal.sdk.utils.SDKLineTracker;
import org.eclipse.edt.compiler.tools.EGL2IR;
import org.eclipse.edt.tests.validation.util.ProblemCollectingProblemRequestor;


/**
 * @author Dave Murray
 */
public class ValidationTestCase extends TestCase {
//PROFILE 	public static boolean PREPARSE_FOR_SYNTAX_ERRORS = true;
//PROFILE 	public static boolean PROFILE = false;
	
	private Map lineNumbersToMessageLists = new HashMap();
	private boolean exceptionOccured = false;
	private boolean syntaxErrorsExistInTest = false;
	private String testEGLFileName = null;
	private File file = null;
	private boolean isVAGCompatible = false;
	
	public static boolean PRINT_ERRORS_TO_CONSOLE = true;
	
	private static Map initialized = new TreeMap();
	
	static File stagingDir = new File("stagingDir");
	static {
		stagingDir.mkdir();
		stagingDir.deleteOnExit();
	}
	
	private static class TestResults {
		Map lineNumbersToMessageLists = null;
		boolean exceptionOccured = false;
		boolean syntaxErrorsInTest = false;
		
		TestResults( Map m, boolean exceptionOccured, boolean syntaxErrorsInTest ) {
			lineNumbersToMessageLists = m;
			this.exceptionOccured = exceptionOccured;
			this.syntaxErrorsInTest = syntaxErrorsInTest;
		}
	}
	
	protected ValidationTestCase( String testEGLFileName ) {
		this( testEGLFileName, false );
	}

	protected ValidationTestCase( String testEGLFileName, boolean isVAGCompatible ) {
		super( "ValidationTestCase" );		
		this.testEGLFileName = testEGLFileName;
		this.isVAGCompatible = isVAGCompatible;
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		TestResults results = (TestResults) initialized.get( testEGLFileName );
		boolean wasInitialized = false;
		
		if( results == null ) {
			Map tempLineNumbersToMessageLists = new HashMap();
			boolean tempExceptionOccured = false;
			boolean tempSyntaxErrorsExistInTest = false;
			
			file = new File( testEGLFileName );
			
			SDKLineTracker lineTracker = new SDKLineTracker();
			String contents = getContents(file);
			lineTracker.set(contents);
			
			File stagingDirForTest = new File(stagingDir, stagingDir.getName() + testEGLFileName.replaceAll("/", "__"));
			stagingDirForTest.mkdir();
			File testFile = new File(stagingDirForTest.getPath(), file.getName());
			testFile.createNewFile();
			File tempDir = new File(stagingDirForTest.getPath(), "irs");
			tempDir.mkdir();
			File EGLCOut = new File(stagingDirForTest.getPath(), "eglc.out");
			EGLCOut.createNewFile();
			
			PrintStream systemOut = System.out;
			
			try {
				copyContents(file, testFile);
				
				final ProblemCollectingProblemRequestor pRequestor = new ProblemCollectingProblemRequestor();
				
//PROFILE 				if(PREPARSE_FOR_SYNTAX_ERRORS) {
					/*
					 * Remove the following block when EGLC reports syntax errors
					 */
					AccumulatingSyntaxErrorRequestor syntaxProblemRequestor = new AccumulatingSyntaxErrorRequestor();
					Scanner lexer =  new Lexer(new FileInputStream(file));
					((org.eclipse.edt.compiler.core.ast.File) new ErrorCorrectingParser(lexer).parse().value).accept(syntaxProblemRequestor);
					for(Iterator iter = syntaxProblemRequestor.getSyntaxErrors().iterator(); iter.hasNext();) {
						SyntaxError synError = (SyntaxError) iter.next();
						
						// useful for debugging
						@SuppressWarnings("unused")
						String fromErrorToEnd = contents.substring(synError.startOffset);
						@SuppressWarnings("unused")
						String justErrorNode = contents.substring(synError.startOffset, synError.endOffset);
						
						pRequestor.acceptProblem(synError.startOffset, synError.endOffset, IMarker.SEVERITY_ERROR, -42, new String[] {"syntax error"});
					}
					tempSyntaxErrorsExistInTest = !pRequestor.getProblems().isEmpty();
//PROFILE 				}
				
				List arguments = new ArrayList();
				if(isVAGCompatible) {
					arguments.add("-isVAGCompatible");
				}
				arguments.add("-eglPath");
				arguments.add(stagingDirForTest.getPath());
				arguments.add("-irDestination");
				arguments.add(tempDir.getPath());
				arguments.add("-xmlout");
				arguments.add(testFile.getPath());
				arguments.add("-extensions");
				arguments.add("org.eclipse.edt.mof.eglx.jtopen.IBMiExtension,org.eclipse.edt.mof.eglx.persistence.sql.SQLExtension,org.eclipse.edt.mof.eglx.services.ServicesExtension");
				
				System.setOut(new PrintStream(new FileOutputStream(EGLCOut.getPath())));
				
//PROFILE 				Controller controller = null;
//PROFILE 				if(PROFILE) {
//PROFILE 					ValidationTestCase.PREPARSE_FOR_SYNTAX_ERRORS = false;
//PROFILE 					try {
//PROFILE 						controller = new Controller();
//PROFILE 						controller.startCPUTimesMeasuring();
//PROFILE 					}
//PROFILE 					catch (Exception e) {
//PROFILE 						e.printStackTrace();
//PROFILE 					}
//PROFILE 				}
				
				Processor.skipSerialization = true; // tons of serialization still needs to be ported. disable otherwise we always get a build error
				EGL2IR.main((String[]) arguments.toArray(new String[0]), getProblemRequestorFactory(pRequestor));
				
//PROFILE 				if(PROFILE) {				
//PROFILE 					try {
//PROFILE 						controller.captureCPUSnapshot(false);
//PROFILE 					} catch (Exception e) {
//PROFILE 						e.printStackTrace();
//PROFILE 					}
//PROFILE 				}
				
				System.out.close();
				System.setOut(systemOut);
				
				List messages = pRequestor.getProblems();				
				for( Iterator iter = messages.iterator(); iter.hasNext(); ) {
					ProblemCollectingProblemRequestor.Problem problem = (ProblemCollectingProblemRequestor.Problem) iter.next();
					Object key = new Integer(lineTracker.getLineNumberOfOffset(problem.startOffset));
					List messageListForLine = (List) tempLineNumbersToMessageLists.get( key );
					if( messageListForLine == null ) {
						messageListForLine = new ArrayList();
						tempLineNumbersToMessageLists.put( key, messageListForLine );	
					}
					messageListForLine.add( problem );
					
					if( problem.problemKind == IProblemRequestor.COMPILATION_EXCEPTION ) {
						tempExceptionOccured = true;
					}
					
					if(DefaultProblemRequestor.messagesWithLineNumberInserts.contains(new Integer(problem.problemKind))) {
						if(problem.inserts.length == 0 || problem.inserts[problem.inserts.length-1] != null) {
							problem.inserts = DefaultProblemRequestor.shiftInsertsIfNeccesary(problem.problemKind, problem.inserts);
						}
						problem.inserts[problem.inserts.length-2] = key.toString();
						problem.inserts[problem.inserts.length-1] = file.getName();
					}
				}
				
				results = new TestResults( tempLineNumbersToMessageLists, tempExceptionOccured, tempSyntaxErrorsExistInTest );
				initialized.put( testEGLFileName, results );
			}
			catch(Exception e) {
				results = new TestResults( tempLineNumbersToMessageLists, true, false);
				initialized.put( testEGLFileName, results );
				e.printStackTrace();
			}
			finally {
				delete(stagingDirForTest);
			}
		}
		else {
			wasInitialized = true;
		}
		
		lineNumbersToMessageLists = results.lineNumbersToMessageLists;
		exceptionOccured = results.exceptionOccured;
		syntaxErrorsExistInTest = results.syntaxErrorsInTest;
		
		if( !wasInitialized && PRINT_ERRORS_TO_CONSOLE ) {
			printErrors();
		}
	}
	
	private ISDKProblemRequestorFactory getProblemRequestorFactory(final ProblemCollectingProblemRequestor pRequestor) {
		return new ISDKProblemRequestorFactory() {
			public IProblemRequestor getProblemRequestor(File file, final String partName) {
				return new DefaultProblemRequestor() {
					@Override
					public void acceptProblem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts, ResourceBundle bundle) {
						if(messagesWithLineNumberInserts.contains(new Integer(problemKind))) {
							inserts = shiftInsertsIfNeccesary(problemKind, inserts);
							inserts[0] = partName;				
						}
				 		if (severity == IMarker.SEVERITY_ERROR) {
				 			setHasError(true);
				 		}
						pRequestor.acceptProblem(startOffset, endOffset, severity, problemKind, inserts, bundle);
					}
				};
			}
	
			public IProblemRequestor getGenericTopLevelFunctionProblemRequestor(File file, String partName, final boolean containerContextDependent) {
				return new DefaultProblemRequestor() {
					@Override
					public void acceptProblem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts, ResourceBundle bundle) {
						if(containerContextDependent && problemKind == IProblemRequestor.TYPE_CANNOT_BE_RESOLVED){
							return;
						}
						
						if(!messagesWithLineNumberInserts.contains(new Integer(problemKind))) {
					 		if (severity == IMarker.SEVERITY_ERROR) {
					 			setHasError(true);
					 		}
							pRequestor.acceptProblem(startOffset, endOffset, severity, problemKind, inserts, bundle);
						}								
					}							
				};
			}
	
			public IProblemRequestor getContainerContextTopLevelProblemRequestor(File file, final String containerContextName, final boolean containerContextDependent) {
				return new DefaultProblemRequestor() {
					@Override
					public void acceptProblem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts, ResourceBundle bundle) {
				 		if (severity == IMarker.SEVERITY_ERROR) {
				 			setHasError(true);
				 		}
						if(containerContextDependent && problemKind == TYPE_CANNOT_BE_RESOLVED ||
						   messagesWithLineNumberInserts.contains(new Integer(problemKind))) {
							int insertsLength = inserts.length;
							inserts = shiftInsertsIfNeccesary(problemKind, inserts);
							if(insertsLength != inserts.length) {
								inserts[0] = containerContextName;
							}
							pRequestor.acceptProblem(startOffset, endOffset, severity, problemKind, inserts, bundle);
						}								
					}
				};
			}

			public ISyntaxErrorRequestor getSyntaxErrorRequestor(File file) {
				 return new AccumulatingSyntaxErrorRequestor();
			}	
		};
	}

	private static boolean delete(File file) {
		if(file.isDirectory()) {
			File[] children = file.listFiles();
			for(int i = 0; i < children.length; i++) {
				delete(children[i]);
			}
		}
		file.deleteOnExit();
		boolean result = file.delete();
		return result;
	}
	
	private static String getContents(File file) {
		if (file == null) {
			return new String();
		} else {
			
			StringBuffer buffer = new StringBuffer();
			
			Reader reader = null;

	        try {
	            reader = new BufferedReader(new FileReader(file));

	            int cur;
	            while((cur = reader.read()) != -1) {
	                buffer.append((char) cur);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	        	if(reader != null) {
	        		try {
	        			reader.close();
	        		}
	        		catch(IOException e) {}
	        	}
	        }
	        
	        return buffer.toString();

		}
	}
	
	private static void copyContents(File srcFile, File destFile) {
		BufferedReader reader = null;
		BufferedWriter writer = null;
		
		try {
			reader = new BufferedReader(new FileReader(srcFile));			
			writer = new BufferedWriter(new FileWriter(destFile));
			
			int ch = reader.read();
			while(ch != -1) {
				writer.write(ch);
				ch = reader.read();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			if(reader != null) {
				try {
					reader.close();
				}
				catch(Exception e) {
					e.printStackTrace();					
				}
			}
			if(writer != null) {
				try {
					writer.close();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	protected List getMessagesAtLine( int lineNum ) {
		List result = (List) lineNumbersToMessageLists.get( new Integer( lineNum ) );
		return result != null ? result : new ArrayList();
	}
	
	protected Object messageWithSubstring( List messages, String substring) {
		return messageWithSubstring(messages, substring, false);
	}
	
	protected Object messageWithSubstring( List messages, String substring, boolean ignoreCase ) {
		for( Iterator iter = messages.iterator(); iter.hasNext(); ) {
			ProblemCollectingProblemRequestor.Problem next = (ProblemCollectingProblemRequestor.Problem) iter.next();
			if (ignoreCase) {
				if( next.getErrorMessage().toUpperCase().indexOf( substring.toUpperCase() ) != -1 ) {
					return next;
				}
			}
			else {
				if( next.getErrorMessage().indexOf( substring ) != -1 ) {
					return next;
				}
			}
		}
		return null;
	}
	
	public void testNoExceptionsThrownDuringValidation() {
		assertFalse( exceptionOccured );
	}
	
	public void testNoSyntaxErrorsInTest() {
		assertFalse( syntaxErrorsExistInTest );
	}
	
	protected boolean validationErrorsInTest() {
		List errorLists = new ArrayList( lineNumbersToMessageLists.values() );
		for( Iterator iter = errorLists.iterator(); iter.hasNext(); ) {
			List errors = (List) iter.next();
			for( Iterator iter2 = errors.iterator(); iter2.hasNext(); ) {
				ProblemCollectingProblemRequestor.Problem problem  = (ProblemCollectingProblemRequestor.Problem) iter2.next();
				if( problem.problemKind != IProblemRequestor.GENERATABLE_PART_NAME_MUST_MATCH_FILE_NAME &&
					problem.problemKind != IProblemRequestor.ONLY_ONE_GENERATABLE_PART_PER_FILE ) {
					return true;
				}
			}
		}
		return false;
	}
	
	protected void printErrors() {
		Integer[] lineNums = (Integer[]) new TreeSet( lineNumbersToMessageLists.keySet() ).toArray( new Integer[0] );
		BufferedReader reader = null;
		RandomAccessFile raFile = null;
		try {
			reader = new BufferedReader( new FileReader( file ) );
			raFile = new RandomAccessFile( file, "r" );
		}
		catch( FileNotFoundException e ) {
			System.err.println( "File not found: " + file.toString() );
			return;
		}
		
		int numErrors = 0;
		
		try {
			int currentReaderLine = 0;
			String readerLine = new String();//reader.readLine();			
			
			for( int i = 0; i < lineNums.length; i++ ) {
				int currentLineNum = lineNums[i].intValue();
				for( ; currentReaderLine != currentLineNum; currentReaderLine++ ) {
					readerLine = reader.readLine();
				}				
				System.out.println( currentLineNum + ": " + readerLine.trim() );
				List errors = (List) lineNumbersToMessageLists.get( lineNums[i] );
				for( Iterator iter = errors.iterator(); iter.hasNext(); ) {
					numErrors += 1;
					ProblemCollectingProblemRequestor.Problem problem = (ProblemCollectingProblemRequestor.Problem) iter.next();
					int startOffset = problem.startOffset;
					int length = problem.endOffset-problem.startOffset;
					//byte[] bytes = new byte[ length ];
					//raFile.read( bytes, startOffset, length );
					raFile.seek( startOffset );
					StringBuffer sb = new StringBuffer();
					for( int j = 0; j < length; j++ ) {
						sb.append( (char) raFile.readByte() );
					}
					System.out.print( "[[" + sb.toString() + "]]: " );
					String str;
					try {
						str = "{" + problem.problemKind + (problem.severity == IMarker.SEVERITY_WARNING ? " (warning)" : "") + "} " + problem.getErrorMessage();
					}
					catch(MissingResourceException e) {
						//TODO: hack for now
						if(problem.problemKind == -42) {
							str = "Syntax error on highlighted input.";
						}
						else {
							str = "!! Message #" + problem.problemKind + " not found in bundle !!";
						}
					}
					System.out.println(str);
				}
				if( i != lineNums.length - 1 ) {
					System.out.println();
				}
			}
		}
		catch( Exception e ) {
			System.err.println( "Exception in printErrors(): " );
			e.printStackTrace();
		}
		finally {
			try {
				if( reader != null ) reader.close();
				if( raFile != null ) raFile.close();
			}
			catch( Exception e ) {}
		}
		
		if(numErrors == 0) {
			System.out.println( "No errors." );
		}
		else {
			System.out.println();
			
			if(numErrors == 1) {
				System.out.println( "1 error total." );
			}
			else {
				System.out.println( numErrors + " errors total.");
			}
		}
	}
	
	public static void clearResultsCache() {
		initialized.clear();
	}
}
