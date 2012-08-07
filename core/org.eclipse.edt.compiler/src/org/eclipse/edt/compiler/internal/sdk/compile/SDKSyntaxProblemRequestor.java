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
package org.eclipse.edt.compiler.internal.sdk.compile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.NumberFormat;
import java.util.ResourceBundle;

import org.eclipse.edt.compiler.internal.core.builder.DefaultProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.sdk.utils.SDKLineTracker;


public class SDKSyntaxProblemRequestor extends DefaultProblemRequestor {

    private String errorMsgCode;
    private String source;
    private SDKLineTracker lineTracker;
    private File file;
    private boolean processedError;
      
    public SDKSyntaxProblemRequestor(File file,String errorMsgCode) {
        super();
        this.errorMsgCode = errorMsgCode;
        this.file = file;
      }
    
    
    private SDKLineTracker getLineTracker() {
    	if (lineTracker == null) {
    		lineTracker = new SDKLineTracker();
    		lineTracker.set(getSource());
    	}
    	return lineTracker;
    }
    
    private String getSource() {
    	if (source == null) {
    		source = getFileContents(file);    		
    	}
    	return source;
    }
    
    @Override
	public void acceptProblem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts, ResourceBundle bundle) {
 		if (severity == IMarker.SEVERITY_ERROR) {
 			setHasError(true);
 		}
 		String message = createMessage(startOffset, endOffset, severity, problemKind, inserts, bundle);
 		if (severity == IMarker.SEVERITY_ERROR) {
 			setHasError(true);
 		}
 		this.displayProblem(startOffset,endOffset,message);
    }
    
    protected String createMessage(int startOffset, int endOffset, int severity, int problemKind, String[] inserts, ResourceBundle bundle) {
    	return createMessage(startOffset, endOffset, getLineNumberOfOffset(startOffset), severity, problemKind, inserts, bundle);
    }

	protected String createMessage(int startOffset, int endOffset, int lineNumber, int severity, int problemKind, String[] inserts, ResourceBundle bundle){
		NumberFormat numberFormat = NumberFormat.getIntegerInstance();
		numberFormat.setMaximumFractionDigits(0);
		numberFormat.setMinimumIntegerDigits(3);
		String message = ""; 
		if(problemKind != -1) {
            message = getErrorMessageText(problemKind, startOffset, lineNumber, severity, getMessageFromBundle(problemKind, inserts, bundle));
        }
        else {
            message = getErrorMessageText(problemKind, startOffset, lineNumber, severity, inserts[0]);
        }
	    
		return message;
	}
	
	
	private String getErrorMessageText(int problemKind, int startOffset, int lineNumber, int severity, String msgText) {
		StringBuffer result = new StringBuffer();
		result.append(getMessagePrefix(problemKind, startOffset, lineNumber, severity));
		result.append(" ");
		result.append(msgText);
		return result.toString();
	}
	
	private String getMessagePrefix(int problemKind, int startOffset, int lineNumber, int severity) {
		StringBuffer prefix = new StringBuffer();
		prefix.append("IWN.");
		prefix.append(errorMsgCode);
		if(problemKind != -1) {
			prefix.append(".");
			prefix.append(Integer.toString(problemKind));
		}
		if(IMarker.SEVERITY_ERROR == severity) {
			prefix.append(".e");
		}
		else if(IMarker.SEVERITY_WARNING == severity) {
			prefix.append(".w");
		}
		else if(IMarker.SEVERITY_INFO == severity) {
			prefix.append(".i");
		}
		prefix.append(" ");
		
		prefix.append(Integer.toString(lineNumber));
		
		prefix.append("/");
		
		int offsetOnLine;
		offsetOnLine = startOffset - getLineTracker().getLineOffset(lineNumber);

		prefix.append(Integer.toString(offsetOnLine+1));
		
		return prefix.toString();
	}
	
	protected int getLineNumberOfOffset(int offset) {
		return getLineTracker().getLineNumberOfOffset(offset);

	}
	
	private String getFileContents(File file)  {
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
    private void displayProblem(int startOffset, int endOffset, String message)  {
    	if (!processedError){
    		processedError = true;
    		println(file.getAbsolutePath());
    	}
    	if(startOffset == -1) startOffset = getSource().length();
    	if(endOffset == -1) endOffset = getSource().length();
        try {
            int startLine = getLineTracker().getLineNumberOfOffset(startOffset);
            int endLine = getLineTracker().getLineNumberOfOffset(endOffset);

            if(startLine == endLine) {
            	SDKLineTracker.LineInfo lineInformation = getLineTracker().getLineInfo(startLine);
                int lineStart = lineInformation.getOffset();
                int lineEnd = lineStart + lineInformation.getLength();

                // Print the source line
                println(getSource().substring(lineStart, lineEnd));

                // Print the annotation
                for(int i = lineStart; i < startOffset; i++) {
                    print(getSource().charAt(i) == '\t' ? "\t" : " ");
                }
                print(endOffset - startOffset <= 1 ? "*" : "<");
                for(int i = startOffset; i < endOffset - 2; i++) {
                    print(getSource().charAt(i) == '\t' ? "--------" : "-");
                }
                if(endOffset - startOffset > 1) {
                    print(">");
                }
                println();
            } else {
                // Print the first line
            	SDKLineTracker.LineInfo firstLineInformation = getLineTracker().getLineInfo(startLine);
                int firstLineStart = firstLineInformation.getOffset();
                int firstLineEnd = firstLineStart + firstLineInformation.getLength();

                println(getSource().substring(firstLineStart, firstLineEnd));

                for(int i = firstLineStart; i < startOffset; i++) {
                    print(getSource().charAt(i) == '\t' ? "\t" : " ");
                }
                print("<");
                for(int i = startOffset; i <= firstLineEnd; i++) {
                    print(getSource().charAt(i) == '\t' ? "--------" : "-");
                }
                println();

                // Print the line between first line and last line
                for(int i = startLine + 1; i < endLine; i++) {
                	SDKLineTracker.LineInfo lineInformation = getLineTracker().getLineInfo(i);
                    int lineStart = lineInformation.getOffset();
                    int lineEnd = lineStart + lineInformation.getLength();

                    println(getSource().substring(lineStart, lineEnd));

                    for(int j = lineStart; j <= lineEnd; j++) {
                        print(getSource().charAt(i) == '\t' ? "--------" : "-");
                    }
                    println();
                }

                // Print the last line
                SDKLineTracker.LineInfo endLineInformation = getLineTracker().getLineInfo(endLine);
                int endLineStart = endLineInformation.getOffset();
                int endLineEnd = endLineStart + endLineInformation.getLength();

                println(getSource().substring(endLineStart, endLineEnd));

                for(int i = endLineStart; i < endOffset - 1; i++) {
                    print(getSource().charAt(i) == '\t' ? "--------" : "-");
                }
                print(">");
                println();
            }

            println(message);
            println("-------------------------------------------------------------------------------");
            println();
            println();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void println(String string) {
    	System.out.println(string);
    }
    public void print(String string) {
    	System.out.print(string);
    }
    public void println() {
    	System.out.println();
    }

}
