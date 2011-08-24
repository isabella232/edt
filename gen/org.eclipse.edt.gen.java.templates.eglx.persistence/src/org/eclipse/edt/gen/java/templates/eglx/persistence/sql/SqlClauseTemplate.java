package org.eclipse.edt.gen.java.templates.eglx.persistence.sql;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.GenericType;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.sql.SqlClause;
import org.eclipse.edt.mof.egl.sql.SqlHostVariableToken;
import org.eclipse.edt.mof.egl.sql.SqlIOStatement;
import org.eclipse.edt.mof.egl.sql.SqlStringToken;
import org.eclipse.edt.mof.egl.sql.SqlTableNameHostVariableToken;
import org.eclipse.edt.mof.egl.sql.SqlToken;

public class SqlClauseTemplate extends JavaTemplate {

	public void genSelectClause(SqlClause select, Context context, TabbedWriter out, SqlIOStatement sqlstmt, Boolean useRowid) {
		/*
		 * Generate from the clause, storing the output in a Writer
		 * instead of putting it into a file. We have to modify the SQL
		 * when addRowId is true, and it's easiest to do it after the
		 * clause is processed.
		 */
		StringWriter sw = new StringWriter();
		TabbedWriter tw = new TabbedWriter(sw);
		genClause(select, context, tw, sqlstmt);
		String selectStr = tw.getCurrentLine();
		// Check for boolean value specifying that ROWID should be used
		if ( useRowid )
		{
			// Add ROWID as the first column.
			int insertIndex = divideSelect( selectStr );
			String beforeFirstCol = selectStr.substring( 0, insertIndex - 1 );
			String afterFirstCol = selectStr.substring( insertIndex - 1 );
			out.print( beforeFirstCol + " ROWID, " + afterFirstCol );
		}
		else
		{
			out.print( selectStr );
		}

	}
	
	
	public void genClause(SqlClause clause, Context context, TabbedWriter out, SqlIOStatement sqlStmt) {
		List<SqlToken> tokens = clause.getTokens();
		if ( tokens == null || tokens.size() == 0 )
		{
			out.print( "\"\"" );
			return;
		}
		out.print('"');
		for ( SqlToken token : tokens )
		{
			context.invoke("genToken", token, context, out, sqlStmt);
		}
		out.print(" \"");

	}


	/**
	 * Returns the index of the character in a SELECT clause before the name of
	 * the first column.
	 *
	 * @param selectStr		the SELECT statement
	 * @return				the index
	 */
	private int divideSelect( String selectStr )
	{
		/*
		 * Find the word SELECT. SQL isn't case sensitive, so we do the search
		 * in a lowercase copy of the real string.
		 */
		String str = selectStr.toLowerCase();
		int insertIndex = str.indexOf( "select" );
		// Move past the SELECT keyword.
		insertIndex += 6;
	
		/*
		 * The keywords ALL, DISTINCT, or UNIQUE may appear after SELECT
		 * and before the first column. Start by skipping any whitespace
		 * after SELECT.
		 */
		for ( char c = str.charAt( insertIndex ); insertIndex < str.length()
				&& Character.isWhitespace( c ); insertIndex++ )
		{
			c = str.charAt( insertIndex );
		}
	
		// Now see if we need to skip one of the keywords.
		if ( str.regionMatches( insertIndex - 1, "all", 0, 3 ) )
		{
			insertIndex += 3;
		}
		else if ( str.regionMatches( insertIndex - 1, "distinct", 0, 8 ) )
		{
			insertIndex += 8;
		}
		else if ( str.regionMatches( insertIndex - 1, "unique", 0, 6 ) )
		{
			insertIndex += 6;
		}
	
		return insertIndex;
	}
	
	public void genToken(SqlStringToken token, Context ctx, TabbedWriter out) {
		out.print('"');
		out.print(token.getSqlString());
		out.print('"');
	}

	public void genToken(SqlTableNameHostVariableToken token, Context ctx, TabbedWriter out, SqlIOStatement stmt) {
		List<String> sqlTableNames = findSqlTableNames(stmt.getTarget());
		for(int i=0; i<sqlTableNames.size(); i++) {
			out.print(sqlTableNames.get(i));
			if (i<sqlTableNames.size()) {
				out.print(", ");
			}
		}
	}
	public void genToken(SqlHostVariableToken token, Context ctx, TabbedWriter out, int hostVarIndex) {
		out.print('?');
		out.print(hostVarIndex);
	}

	/**
	 * Sets the sqlTableNames field from the tableNames annotation on 
	 * recordOrArray's Part (from ioObjectPart).
	 */
	private List<String> findSqlTableNames( Expression recordOrArray )
	{
		Type part = recordOrArray.getType();
		Object namesAnnotObj = part instanceof GenericType
			? ((GenericType)part).getTypeArguments().get(0)
			: part.getAnnotation("tableNames" ).getValue();
		List<String> names = new ArrayList<String>();
		if ( namesAnnotObj instanceof String[][] )
		{
			String[][] namesAnnot = (String[][])namesAnnotObj;
			for ( int i = 0; i < namesAnnot.length; i++ )
			{
				names.add( namesAnnot[ i ][ 0 ].toUpperCase() );
			}
		}
		else if ( part.getAnnotation("tableNameVariables" ) == null )
		{
			// If the record has neither property, that's like having tableNames
			// set to the name of the record type.
			names = new ArrayList<String>( 1 );
			names.add( ((Part)part).getName().toUpperCase() );
		}
		return names;
	}

}
