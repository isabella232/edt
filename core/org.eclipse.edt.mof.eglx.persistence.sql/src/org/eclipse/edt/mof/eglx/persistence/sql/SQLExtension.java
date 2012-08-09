package org.eclipse.edt.mof.eglx.persistence.sql;

import org.eclipse.edt.compiler.ICompilerExtension;
import org.eclipse.edt.compiler.PartValidator;
import org.eclipse.edt.compiler.StatementValidator;
import org.eclipse.edt.compiler.SystemEnvironmentUtil;
import org.eclipse.edt.compiler.TypeValidator;
import org.eclipse.edt.compiler.core.ast.AddStatement;
import org.eclipse.edt.compiler.core.ast.CloseStatement;
import org.eclipse.edt.compiler.core.ast.DeleteStatement;
import org.eclipse.edt.compiler.core.ast.ExecuteStatement;
import org.eclipse.edt.compiler.core.ast.ForEachStatement;
import org.eclipse.edt.compiler.core.ast.GetByKeyStatement;
import org.eclipse.edt.compiler.core.ast.GetByPositionStatement;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.OpenStatement;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.PrepareStatement;
import org.eclipse.edt.compiler.core.ast.ReplaceStatement;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.internal.egl2mof.ElementGenerator;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.eglx.persistence.sql.gen.SQLActionStatementGenerator;
import org.eclipse.edt.mof.eglx.persistence.sql.gen.SqlActionStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.validation.SQLActionStatementValidator;

public class SQLExtension implements ICompilerExtension {
	
	@Override
	public String[] getSystemEnvironmentPaths() {
		return new String[]{SystemEnvironmentUtil.getSystemLibraryPath(SqlActionStatement.class, "egllib")};
	}
	
	@Override
	public Class[] getExtendedTypes() {
		return new Class[]{AddStatement.class, CloseStatement.class, DeleteStatement.class, ExecuteStatement.class, ForEachStatement.class,
				GetByKeyStatement.class, GetByPositionStatement.class, OpenStatement.class, PrepareStatement.class, ReplaceStatement.class};
	}
	
	@Override
	public ElementGenerator getElementGeneratorFor(Node node) {
		if (shouldExtend(node)) {
			return new SQLActionStatementGenerator();
		}
		return null;
	}
	
	@Override
	public PartValidator getValidatorFor(Part part) {
		// No special validators.
		return null;
	}
	
	@Override
	public StatementValidator getValidatorFor(Statement stmt) {
		if (shouldExtend(stmt)) {
			return new SQLActionStatementValidator();
		}
		return null;
	}
	
	@Override
	public TypeValidator getValidatorFor(Type type) {
		// No special validators.
		return null;
	}
	
	private boolean shouldExtend(Node node) {
		//TODO should be based on the data source type
		return false;
	}
}
