package org.eclipse.edt.compiler.internal.core.validation.annotation;

import java.util.Iterator;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.OnExceptionBlock;
import org.eclipse.edt.compiler.core.ast.TryStatement;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;

public class ThrowsInvocationValidator implements
		IInvocationValidationRule {
	
	private final static String ANYEXCEPTION = InternUtil.intern("anyException");
	private final static String[] EGLXLAN = InternUtil.intern(new String[] {"eglx", "lang"});

	@Override
	public void validate(Node node, final IBinding binding,
			IPartBinding declaringPart, final IProblemRequestor problemRequestor,
			ICompilerOptions compilerOptions) {
		
		if (!Binding.isValidBinding(binding)) {
			return;
		}
		
		if (!isInTryCatchAnyException(node)) {
			problemRequestor.acceptProblem(
					node, 
					IProblemRequestor.INVOCATION_MUST_BE_IN_TRY, 
					new String[] {});
		}
	}
	
	private boolean isInTryCatchAnyException(Node node) {

		TryStatement tryStmt = getTryStatement(node);
		if (tryStmt == null) {
			return false;
		}
		if (hasCatchAnyException(tryStmt)) {
			return true;
		}
		return isInTryCatchAnyException(tryStmt.getParent());		
	}
	
	private boolean hasCatchAnyException(TryStatement tryStmt) {
		Iterator i = tryStmt.getOnExceptionBlocks().iterator();
		while (i.hasNext()) {
			OnExceptionBlock exBlock = (OnExceptionBlock)i.next();
			Type type = exBlock.getExceptionType();
			if (type != null && Binding.isValidBinding(type.resolveTypeBinding())) {
				if (type.resolveTypeBinding().getKind() == ITypeBinding.EXTERNALTYPE_BINDING) {
					ExternalTypeBinding et = (ExternalTypeBinding)type.resolveTypeBinding();
					return et.getName() == ANYEXCEPTION && et.getPackageName() == EGLXLAN;
				}
			}
		}
		return false;
	}

	
	private TryStatement getTryStatement(Node node) {
		if (node == null) {
			return null;
		}
		if (node instanceof TryStatement) {
			return (TryStatement) node;
		}
		
		if (node instanceof OnExceptionBlock) {
			if (node.getParent() != null) {
				return getTryStatement(node.getParent().getParent());
			}
			return null;
		}
		
		return getTryStatement(node.getParent());
		
	}
}