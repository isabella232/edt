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
package org.eclipse.edt.debug.internal.ui.actions;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.ElseBlock;
import org.eclipse.edt.compiler.core.ast.EmptyStatement;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.OnEventBlock;
import org.eclipse.edt.compiler.core.ast.OnExceptionBlock;
import org.eclipse.edt.compiler.core.ast.OtherwiseClause;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.core.ast.TopLevelFunction;
import org.eclipse.edt.compiler.core.ast.WhenClause;
import org.eclipse.edt.debug.internal.ui.EDTDebugUIPlugin;
import org.eclipse.edt.gen.JavaAliaser;
import org.eclipse.edt.ide.core.internal.model.document.EGLDocument;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPackageDeclaration;
import org.eclipse.edt.ide.ui.editor.IEGLEditor;
import org.eclipse.jdt.debug.core.IJavaStratumLineBreakpoint;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Utility methods for working with breakpoints.
 */
public class BreakpointUtils
{
	private BreakpointUtils()
	{
		// No instances.
	}
	
	// Would be nice if JDT made this public so we didn't have to duplicate it.
	public static final String STRATUM_BREAKPOINT = "org.eclipse.jdt.debug.javaStratumLineBreakpointMarker"; //$NON-NLS-1$
	
	/**
	 * @return the text editor for the part, possibly null.
	 */
	public static ITextEditor getEditor( IWorkbenchPart part )
	{
		if ( part instanceof ITextEditor )
		{
			return (ITextEditor)part;
		}
		return (ITextEditor)part.getAdapter( ITextEditor.class );
	}
	
	/**
	 * @return the fully-qualified class name of the generated file.
	 */
	public static String getGeneratedClassName( ITextSelection selection, IFile file )
	{
		IEGLFile eglFile = (IEGLFile)EGLCore.create( file );
		if ( eglFile != null && eglFile.exists() )
		{
			try
			{
				StringBuilder buf = new StringBuilder( 50 );
				IPackageDeclaration[] pkg = eglFile.getPackageDeclarations();
				if ( pkg != null && pkg.length > 0 )
				{
					buf.append( JavaAliaser.packageNameAlias( pkg[ 0 ].getElementName() ) );
					buf.append( '.' );
				}
				
				String name = eglFile.getElementName();
				int idx = name.lastIndexOf( '.' );
				if ( idx != -1 )
				{
					name = name.substring( 0, idx );
				}
				
				buf.append( JavaAliaser.getAlias( name ) );
				return buf.toString();
			}
			catch ( EGLModelException e )
			{
			}
		}
		return null;
	}
	
	/**
	 * Returns true if a stratum breakpoint exists on the resource at the given line number.
	 * 
	 * @param resource The resource.
	 * @param lineNumber The line number.
	 * @param stratum The stratum type (e.g. "egl").
	 * @return true if a stratum breakpoint exists on the resource at the given line number.
	 * @throws CoreException
	 */
	public static IJavaStratumLineBreakpoint stratumBreakpointExists( IResource resource, int lineNumber, String stratum ) throws CoreException
	{
		String modelId = JDIDebugModel.getPluginIdentifier();
		IBreakpointManager manager = DebugPlugin.getDefault().getBreakpointManager();
		IBreakpoint[] breakpoints = manager.getBreakpoints( modelId );
		for ( int i = 0; i < breakpoints.length; i++ )
		{
			if ( !(breakpoints[ i ] instanceof IJavaStratumLineBreakpoint) )
			{
				continue;
			}
			IJavaStratumLineBreakpoint breakpoint = (IJavaStratumLineBreakpoint)breakpoints[ i ];
			IMarker marker = breakpoint.getMarker();
			if ( marker != null && marker.exists() && STRATUM_BREAKPOINT.equals( marker.getType() ) )
			{
				if ( breakpoint.getLineNumber() == lineNumber && resource.equals( marker.getResource() ) && breakpoint.getStratum().equals( stratum ) )
				{
					return breakpoint;
				}
			}
		}
		return null;
	}
	
	/**
	 * @return true if a breakpoint can be placed on a line containing the statement. For example you can't put a breakpoint on a local variable
	 *         declaration unless it has an initializer.
	 */
	public static boolean isBreakpointValidForStatement( Statement statement )
	{
		if ( statement instanceof EmptyStatement )
		{
			return false;
		}
		
		// check if function data declaration has an initializer or not
		if ( statement instanceof FunctionDataDeclaration )
		{
			return ((FunctionDataDeclaration)statement).getInitializer() != null;
		}
		
		return true;
	}
	
	/**
	 * @return true if the line of the editor is a valid location for a breakpoint.
	 */
	public static boolean isBreakpointValid( IEGLEditor editor, int line )
	{
		Statement statement = getStatementNode( editor, line );
		if ( statement != null )
		{
			return isBreakpointValidForStatement( statement );
		}
		
		return false;
	}
	
	/**
	 * @return the AST statement node at the line of the editor.
	 */
	public static Statement getStatementNode( IEGLEditor editor, int line )
	{
		EGLDocument document = (EGLDocument)editor.getViewer().getDocument();
		if ( document == null )
		{
			return null;
		}
		
		int docOffset;
		try
		{
			// use the 1st non space character of the line as the document offset to avoid boundary conditions
			docOffset = document.getLineOffset( line );
			int lineEndOffset = docOffset + document.getLineLength( line );
			// get the 1st non space character
			char thechar = document.getChar( docOffset );
			while ( (thechar == ' ' || thechar == '\t') && docOffset <= lineEndOffset )
			{
				docOffset++;
				thechar = document.getChar( docOffset );
			}
		}
		catch ( BadLocationException e )
		{
			e.printStackTrace();
			EDTDebugUIPlugin.log( e );
			return null;
		}
		
		Node node = document.getNewModelNodeAtOffset( docOffset );
		if ( node == null )
		{
			return null;
		}
		
		// If we found a statement that contains statements, need to get the list of
		// statements and loop thru them to find the right one
		final Node[] statementContainerNode = new Node[ 1 ];
		final List[] statements = new List[ 1 ];
		node.accept( new DefaultASTVisitor() {
			public boolean visit( ElseBlock visitedNode )
			{
				statementContainerNode[ 0 ] = visitedNode;
				statements[ 0 ] = visitedNode.getStmts();
				return false;
			}
			
			public boolean visit( NestedFunction visitedNode )
			{
				statementContainerNode[ 0 ] = visitedNode;
				statements[ 0 ] = visitedNode.getStmts();
				return false;
			}
			
			public boolean visit( OnExceptionBlock visitedNode )
			{
				statementContainerNode[ 0 ] = visitedNode;
				statements[ 0 ] = visitedNode.getStmts();
				return false;
			}
			
			public boolean visit( OnEventBlock visitedNode )
			{
				statementContainerNode[ 0 ] = visitedNode;
				statements[ 0 ] = visitedNode.getStatements();
				return false;
			}
			
			public boolean visit( TopLevelFunction visitedNode )
			{
				statementContainerNode[ 0 ] = visitedNode;
				statements[ 0 ] = visitedNode.getStmts();
				return false;
			}
			
			public boolean visit( WhenClause visitedNode )
			{
				statementContainerNode[ 0 ] = visitedNode;
				statements[ 0 ] = visitedNode.getStmts();
				return false;
			}
			
			public boolean visit( OtherwiseClause visitedNode )
			{
				statementContainerNode[ 0 ] = visitedNode;
				statements[ 0 ] = visitedNode.getStatements();
				return false;
			}
		} );
		
		// If we did not find a statement that contains statements, look up the node parent chain
		// until a statement is found and return it
		if ( statementContainerNode[ 0 ] == null )
		{
			while ( node != null && !(node instanceof Statement) )
			{
				node = node.getParent();
			}
			return (Statement)node;
		}
		// If we found a statement that contains statements, need to get the list of
		// statements and loop thru them to find the right one
		else
		{
			Statement statement = null;
			for ( Iterator iter = statements[ 0 ].iterator(); iter.hasNext(); )
			{
				Node nodex = (Node)iter.next();
				if ( nodex instanceof Statement )
				{
					statement = (Statement)nodex;
					try
					{
						int statementLine = document.getLineOfOffset( statement.getOffset() ) + 1;
						if ( statementLine > line && isBreakpointValidForStatement( statement ) )
							return statement;
					}
					catch ( BadLocationException e )
					{
						e.printStackTrace();
						EDTDebugUIPlugin.log( e );
					}
				}
			}
		}
		
		return null;
	}
}
