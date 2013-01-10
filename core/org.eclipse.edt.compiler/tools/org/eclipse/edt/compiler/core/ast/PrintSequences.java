/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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

/**
 * @author demurray
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PrintSequences {

	public static String[][] SEPARATORS = new String[][] {
		new String[] {"addOption",           "",      "star"},
		new String[] {"classContent",        "",      "star"},
		new String[] {"deleteOption",        "",      "star"},
		new String[] {"enumerationField",    "COMMA", "star"},
		new String[] {"eventBlock",          "",      "star"},
		new String[] {"executeOption",       "",      "star"},
		new String[] {"expr",                "COMMA", "star"},
		new String[] {"externalTypeContent", "",      "star"},
		new String[] {"formContent",         "",      "star"},
		new String[] {"formGroupContent",    "",      "star"},
		new String[] {"forwardOption",       "",      "star"},
		new String[] {"functionParameter",   "COMMA", "star"},
		new String[] {"getByKeyOption",      "",      "star"},
		new String[] {"getByPositionOption", "",      "star"},
		new String[] {"importDecl",          "",      "star"},
		new String[] {"interfaceContent",    "",      "star"},
		new String[] {"setTarget",           "COMMA", "plus"},
		new String[] {"moveModifier",        "",      "star"},
		new String[] {"name",                "COMMA", "plus"},
		new String[] {"onException",         "",      "star"},
		new String[] {"openTarget",          "",      "star"},
		new String[] {"part",                "",      "star"},
		new String[] {"prepareOption",       "",      "star"},
		new String[] {"programParameter",    "COMMA", "star"},
		new String[] {"replaceOption",       "",      "star"},
		new String[] {"serviceReference",    "COMMA", "star"},
		new String[] {"setting",             "COMMA", "star"},
		new String[] {"settingsBlock",       "",      "star"},
		new String[] {"showOption",          "",      "star"},
		new String[] {"stmt",                "",      "star"},
		new String[] {"structureContent",    "",      "star"},
		new String[] {"whenClause",          "",      "star"},
 	};
	
	private String lower(String name) {
		if( name.equals( "ID" ) ) {
			return name;
		}
		return Character.toLowerCase(name.charAt(0)) + name.substring(1);
	}
	
	private void printSymbols() {
		for (int i = 0; i < SEPARATORS.length; i++) {
			String[] separator = SEPARATORS[i];
			String name = separator[0];
			if(separator[2].equals("star")) {
				System.out.println("nonterminal List " + lower(name) + "_star, " + lower(name) + "_plus;");
			}
			else {
				System.out.println("nonterminal List " + lower(name) + "_plus;");
			}
		}
	}
	
	public void printRules() {
		for (int i = 0; i < SEPARATORS.length; i++) {
			String[] separator = SEPARATORS[i];
			String name = separator[0];
			if(separator[2].equals("star")) {
				printStarRule(name);
			}
			printPlusRule(name);
		}
	}
	
	private String getSeparator(String name) {
		for (int i = 0; i < SEPARATORS.length; i++) {
			if(SEPARATORS[i][0].equalsIgnoreCase(name)) {
				String separator = SEPARATORS[i][1];
				if(separator.length() > 0) {
					return " " + SEPARATORS[i][1] + " ";
				}
				else {
					return " ";
				}
			}
		}
		
		// Shouldn't come here
		return "";
	}
	
	private void printPlusRule(String symbol) {
		System.out.println(lower(symbol) + "_plus");
		System.out.println("\t::=\t" + lower(symbol) + ":" + lower(symbol));
		System.out.println("\t{: RESULT = new ArrayList(); RESULT.add(" + lower(symbol) + "); :}");
		System.out.println("\t|\t" + lower(symbol) + "_plus:" + lower(symbol) + "s" + getSeparator(symbol) + lower(symbol) + ":" + lower(symbol));
		System.out.println("\t{: if(" + lower(symbol) + " != null) " + lower(symbol) + "s.add(" + lower(symbol) + "); RESULT = " + lower(symbol) + "s; :}");
		System.out.println("\t;");
		System.out.println();
	}
	
	private void printStarRule(String symbol) {
		System.out.println(lower(symbol) + "_star");
		System.out.println("\t::=");
		System.out.println("\t{: RESULT = Collections.EMPTY_LIST; :}");
		System.out.println("\t|\t" + lower(symbol) + "_plus:" + lower(symbol) + "s" );
		System.out.println("\t{: RESULT = " + lower(symbol) + "s; :}" );
		System.out.println("\t;");
		System.out.println();
	}
	
	private void print() {
		printSymbols();
		printRules();
	}
	
	public static void main(String[] args) {
		PrintSequences tool = new PrintSequences();
		tool.print();
	}

}
