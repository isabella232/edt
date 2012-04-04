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
package org.eclipse.edt.compiler.core.ast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;

/**
 * Scans the contents of file ${PARSER_NAME}.java (assumed to be in working directory)
 * and prints each line to standard out. Everything but the method CUP$Parser$do_action()
 * is printed verbatim. The contents of that method (one large switch statement) are
 * split between two methods.
 * 
 * This technique is a workaround for the 65535-byte limit for methods in Java.
 * 
 * @author Dave Murray
 */
public class ActionMethodSplitter {
	
	public static final String PARSER_NAME = "Parser";
	
	public static final String NL = System.getProperty( "line.separator" );
	
	public static final String DO_ACTION_SIG_BEGIN1 = "  public final java_cup.runtime.Symbol CUP$";
	public static final String DO_ACTION_SIG_BEGIN2 = "$do_action(";
	
	public static void main( String[] args ) {
		BufferedReader reader = null;
		StringBuffer activeBuffer = new StringBuffer();		
		String doActionSig = null;
		int numCases = -1;
		String parserName = args.length == 1 ? args[0] : PARSER_NAME;
		String doActionSigBegin = DO_ACTION_SIG_BEGIN1 + parserName + DO_ACTION_SIG_BEGIN2;
				
		try {
			reader = new BufferedReader( new FileReader( parserName + ".java" ) );
			String line = reader.readLine();
			
			while( line != null ) {
				if( line.equals( doActionSigBegin ) ) {
					System.out.println( activeBuffer );
					
					activeBuffer = new StringBuffer();
					while(!line.equals( "        {" ) ) {
						activeBuffer.append( line + NL );
						line = reader.readLine();
					}
					activeBuffer.append( line + NL );
					
					doActionSig = activeBuffer.toString();
					doActionSig = doActionSig.replaceAll( "do_action", "do_action2" );
					
					line = reader.readLine();
					
					// Now line is "          /*. . . . . . . . . . . . . . . . . . . .*/"
					activeBuffer.append( line + NL );
					
					line = reader.readLine();

					// Now line is "          case ###: // whenClause_plus ::= whenClause_plus whenClause"
					StringTokenizer st = new StringTokenizer( line );
					st.nextToken();
					numCases = Integer.parseInt( st.nextToken().replaceAll(":", "") );
				}
				if( numCases != -1 && line.trim().startsWith( "case " + Integer.toString(numCases/2))) {
					activeBuffer.append( "				default:" + NL );
					activeBuffer.append( "		            return( CUP$" + parserName + "$do_action2(CUP$" + parserName + "$act_num,CUP$" + parserName + "$parser,CUP$" + parserName + "$stack,CUP$" + parserName + "$top) ); " + NL );
					activeBuffer.append( "		        }" + NL );
					activeBuffer.append( "		    }" + NL );
					System.out.println( activeBuffer );
					System.out.println();
					
					System.out.println( doActionSig );	
					numCases = -1;
					activeBuffer = new StringBuffer();					
				}
				
				activeBuffer.append( line + NL );				
				line = reader.readLine();				
			}
			
			System.out.println( activeBuffer );
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
		finally {
			try {
				if( reader != null ) reader.close();
			}
			catch( Exception e ) {}
		}
		
		//public final java_cup.runtime.Symbol CUP$Parser$do_action(
	}
}
