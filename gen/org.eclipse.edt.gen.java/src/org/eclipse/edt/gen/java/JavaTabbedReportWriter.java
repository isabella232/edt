package org.eclipse.edt.gen.java;

import java.io.IOException;
import java.io.Writer;

import org.eclipse.edt.mof.codegen.api.TabbedReportWriter;

public class JavaTabbedReportWriter extends TabbedReportWriter {

	private Context ctx;
	
	public JavaTabbedReportWriter(Context ctx, String templateQualifier, Writer writer) {
		super(templateQualifier, writer);
		this.ctx = ctx;
	}

	/**
	 * Prints a newline character.
	 */
	public void println() {
		super.println();
		// a new line with the above println method, it throws off the smap ending line number by 1
		ctx.setSmapLastJavaLineNumber(getLineNumber() - 1);
	}

	/**
	 * Prints the given String followed by a newline character.
	 * @param str the String to be printed.
	 */
	public void println(String str) {
		super.println(str);
		// a new line with the above println method, it throws off the smap ending line number by 1
		ctx.setSmapLastJavaLineNumber(getLineNumber() - 1);
	}

	/**
	 * Prints each character in an array followed by a newline character.
	 * @param chars the characters to be printed.
	 */
	public void println(char[] chars) {
		super.println(chars);
		// a new line with the above println method, it throws off the smap ending line number by 1
		ctx.setSmapLastJavaLineNumber(getLineNumber() - 1);
	}

	/**
	 * Prints the given char followed by a newline character.
	 * @param c the char to be printed.
	 * @exception IOException if there's a problem.
	 */
	public void println(char c) {
		super.println(c);
		// a new line with the above println method, it throws off the smap ending line number by 1
		ctx.setSmapLastJavaLineNumber(getLineNumber() - 1);
	}

	/**
	 * Prints the given int followed by a newline character.
	 * @param i the int to be printed.
	 */
	public void println(int i) {
		super.println(i);
		// a new line with the above println method, it throws off the smap ending line number by 1
		ctx.setSmapLastJavaLineNumber(getLineNumber() - 1);
	}

	/**
	 * Prints the given boolean followed by a newline character.
	 * @param b the boolean to be printed.
	 */
	public void println(boolean b) {
		super.println(b);
		// a new line with the above println method, it throws off the smap ending line number by 1
		ctx.setSmapLastJavaLineNumber(getLineNumber() - 1);
	}
}
