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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author winghong
 * @author demurray
 */
public abstract class Part extends Node {
	
	private class SyntaxErrorRequestorSWrapper implements ISyntaxErrorRequestor {

		int thisStart = getOffset();
		int thisEnd = getOffset() + getLength() - 1;
		
		ISyntaxErrorRequestor requestor;
		
		public SyntaxErrorRequestorSWrapper(ISyntaxErrorRequestor requestor) {
			super();
			this.requestor = requestor;
		}

		public void incorrectNonTerminal(int nonTerminalType, int startOffset, int endOffset) {
			if (startOffset >= thisStart && endOffset <= thisEnd) {
				requestor.incorrectNonTerminal(nonTerminalType, startOffset, endOffset);
			}
			
		}

		public void incorrectPhrase(int nonTerminalType, int startOffset, int endOffset) {
			if (startOffset >= thisStart && endOffset <= thisEnd) {
				requestor.incorrectPhrase(nonTerminalType, startOffset, endOffset);
			}
		}

		public void incorrectPreviousNonTerminal(int nonTerminalType, int startOffset, int endOffset) {
			if (startOffset >= thisStart && endOffset <= thisEnd) {
				requestor.incorrectPreviousNonTerminal(nonTerminalType, startOffset, endOffset);
			}
			
		}

		public void incorrectPreviousTerminal(int terminalType, int startOffset, int endOffset) {
			if (startOffset >= thisStart && endOffset <= thisEnd) {
				requestor.incorrectPreviousTerminal(terminalType, startOffset, endOffset);
			}
		}

		public void incorrectTerminal(int terminalType, int startOffset, int endOffset) {
			if (startOffset >= thisStart && endOffset <= thisEnd) {
				requestor.incorrectTerminal(terminalType, startOffset, endOffset);
			}
		}

		public void invalidCharacterInHexLiteral(int startOffset, int endOffset) {
			if (startOffset >= thisStart && endOffset <= thisEnd) {
				requestor.invalidCharacterInHexLiteral(startOffset, endOffset);
			}
		}

		public void invalidEscapeSequence(int startOffset, int endOffset) {
			if (startOffset >= thisStart && endOffset <= thisEnd) {
				requestor.invalidEscapeSequence(startOffset, endOffset);
			}
		}

		public void keywordAsName(int terminalType, int startOffset, int endOffset) {
			if (startOffset >= thisStart && endOffset <= thisEnd) {
				requestor.keywordAsName(terminalType, startOffset, endOffset);
			}
		}

		public void missingEndForPart(int startOffset, int endOffset) {

			//ignore the missing end if it is on the first byte of this part...
			//that means that the missing end belongs to the previous part
			if ((startOffset > thisStart && endOffset <= thisEnd) || startOffset == thisEnd + 1) {
				requestor.missingEndForPart(startOffset, endOffset);
			}
		}

		public void missingNonTerminal(int nonTerminalType, int startOffset, int endOffset) {
			if (startOffset >= thisStart && endOffset <= thisEnd) {
				requestor.missingNonTerminal(nonTerminalType, startOffset, endOffset);
			}
		}

		public void missingPreviousNonTerminal(int nonTerminalType, int startOffset, int endOffset) {
			if (startOffset >= thisStart && endOffset <= thisEnd) {
				requestor.missingPreviousNonTerminal(nonTerminalType, startOffset, endOffset);
			}
		}

		public void missingPreviousTerminal(int terminalType, int startOffset, int endOffset) {
			if (startOffset >= thisStart && endOffset <= thisEnd) {
				requestor.missingPreviousTerminal(terminalType, startOffset, endOffset);
			}
		}

		public void missingScopeCloser(int terminalType, int startOffset, int endOffset) {
			if ((startOffset > thisStart && endOffset <= thisEnd) || startOffset == thisEnd + 1) {
				requestor.missingScopeCloser(terminalType, startOffset, endOffset);
			}
		}

		public void missingTerminal(int terminalType, int startOffset, int endOffset) {
			if (startOffset >= thisStart && endOffset <= thisEnd) {
				requestor.missingTerminal(terminalType, startOffset, endOffset);
			}
		}

		public void panicPhrase(int startOffset, int endOffset) {
			if (startOffset >= thisStart && endOffset <= thisEnd) {
				requestor.panicPhrase(startOffset, endOffset);
			}
		}

		public void tooManyErrors() {
			// no way to determine where the errors are
		}

		public void unclosedBlockComment(int startOffset, int endOffset) {
			if (startOffset >= thisStart && endOffset <= thisEnd) {
				requestor.unclosedBlockComment(startOffset, endOffset);
			}
		}

		public void unclosedSQL(int startOffset, int endOffset) {
			if (startOffset >= thisStart && endOffset <= thisEnd) {
				requestor.unclosedSQL(startOffset, endOffset);
			}
		}

		public void unclosedSQLCondition(int startOffset, int endOffset) {
			if (startOffset >= thisStart && endOffset <= thisEnd) {
				requestor.unclosedSQLCondition(startOffset, endOffset);
			}
		}

		public void unclosedString(int startOffset, int endOffset) {
			if (startOffset >= thisStart && endOffset <= thisEnd) {
				requestor.unclosedString(startOffset, endOffset);
			}
		}

		public void unexpectedPhrase(int startOffset, int endOffset) {
			if (startOffset >= thisStart && endOffset <= thisEnd) {
				requestor.unexpectedPhrase(startOffset, endOffset);
			}
		}

		public void unexpectedPreviousTerminal(int startOffset, int endOffset) {
			if (startOffset >= thisStart && endOffset <= thisEnd) {
				requestor.unexpectedPreviousTerminal(startOffset, endOffset);
			}
		}

		public void unexpectedTerminal(int startOffset, int endOffset) {
			if (startOffset >= thisStart && endOffset <= thisEnd) {
				requestor.unexpectedTerminal(startOffset, endOffset);
			}
		}

		public void whitespaceInSQL(int startOffset, int endOffset) {
			if (startOffset >= thisStart && endOffset <= thisEnd) {
				requestor.whitespaceInSQL(startOffset, endOffset);
			}
		}

		public void whitespaceInSQLCondition(int startOffset, int endOffset) {
			if (startOffset >= thisStart && endOffset <= thisEnd) {
				requestor.whitespaceInSQLCondition(startOffset, endOffset);
			}
		}
		
	}
	
	//part type constants
	public static final int PROGRAM = 0;
	public static final int RECORD = 1;
	public static final int FUNCTION = 2;
	public static final int DATAITEM = 3;
	public static final int LIBRARY = 8;
	public static final int HANDLER = 9;
	public static final int SERVICE = 10;
	public static final int INTERFACE = 11;
	public static final int DELEGATE = 12;
	public static final int EXTERNALTYPE = 13;
	public static final int ENUMERATION = 14;
	public static final int CLASS = 15;

    protected boolean isPrivate;
	protected SimpleName name;
    protected List<Node> contents;
    private String identifier;

    public Part(Boolean isPrivate, SimpleName name, List contents, int startOffset, int endOffset) {
        super(startOffset, endOffset);

        this.name = name;
        name.setParent(this);
        this.contents = setParent(contents);
        
        this.isPrivate = isPrivate.booleanValue();
    }

    public Name getName() {
        return name;
    }

    public List<Node> getContents() {
        return contents;
    }
    
    public boolean isPrivate() {
    	return isPrivate;
    }
    
    protected abstract Object clone() throws CloneNotSupportedException;

	protected ArrayList cloneContents() throws CloneNotSupportedException {
		return cloneList(contents);
	}
	
	public Part clonePart(){
		try {
			Part newPart = (Part)this.clone();
			File newFile = new File(null, Collections.EMPTY_LIST, Collections.EMPTY_LIST, -1, -1);
			File oldFile = getFile();
			if (oldFile != null) {
				newFile.setSyntaxErrors(oldFile.getSyntaxErrors());
			}
	    	newPart.setParent(newFile);
	    	
	    	return newPart;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}		
	}
	
	private File getFile() {
		if (getParent() instanceof File) {
			return (File)getParent();
		}
		return null;
	}
	
	public abstract String getPartTypeName();
	public abstract int getPartType();
	
	public abstract boolean hasSubType();	
	public abstract Name getSubType();
	
    public void accept(ISyntaxErrorRequestor syntaxErrorRequestor) {
    	File file = getFile();
    	if (file == null) {
    		return;
    	}
    	file.accept(new SyntaxErrorRequestorSWrapper(syntaxErrorRequestor));
    	
    }
    
    public String getIdentifier() {
    	if (identifier == null) {
    		identifier = getName().getIdentifier();
    	}
    	return identifier;
    }
    
    @Override
	public String toString() {
		return getPartTypeName() + " " + getName();
	}
}
