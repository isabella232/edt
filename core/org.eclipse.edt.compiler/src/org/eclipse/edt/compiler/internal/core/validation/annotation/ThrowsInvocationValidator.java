package org.eclipse.edt.compiler.internal.core.validation.annotation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PartBinding;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.OnExceptionBlock;
import org.eclipse.edt.compiler.core.ast.TryStatement;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;

public class ThrowsInvocationValidator implements
		IInvocationValidationRule {
	
	private final static String JAVAOBJECTECXPTION = InternUtil.intern("JavaObjectException");
	private final static String[] EGLXJAVA = InternUtil.intern(new String[] {"eglx", "java"});
	private List<IPartBinding> validExceptions = new ArrayList<IPartBinding>();

	@Override
	public void validate(Node node, final IBinding binding,
			IPartBinding declaringPart, final IProblemRequestor problemRequestor,
			ICompilerOptions compilerOptions) {
		
		if (!Binding.isValidBinding(binding) || !Binding.isValidBinding(declaringPart)) {
			return;
		}
		
		IPartBinding joe = declaringPart.getEnvironment().getPartBinding(EGLXJAVA, JAVAOBJECTECXPTION);
		buildAllValidExceptions(validExceptions, joe);
		
		if (!isInTryCatchException(node)) {
			problemRequestor.acceptProblem(
					node, 
					IProblemRequestor.INVOCATION_MUST_BE_IN_TRY, 
					new String[] {});
		}
	}
	
	private boolean isInTryCatchException(Node node) {

		TryStatement tryStmt = getTryStatement(node);
		if (tryStmt == null) {
			return false;
		}
		if (hasCatchException(tryStmt)) {
			return true;
		}
		return isInTryCatchException(tryStmt.getParent());		
	}
	
	private boolean hasCatchException(TryStatement tryStmt) {
		Iterator i = tryStmt.getOnExceptionBlocks().iterator();
		while (i.hasNext()) {
			OnExceptionBlock exBlock = (OnExceptionBlock)i.next();
			Type type = exBlock.getExceptionType();
			if (type != null && validExceptions.contains(type.resolveTypeBinding())) {
				return true;
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
	
	private void buildAllValidExceptions(List<IPartBinding> list, IPartBinding baseException) {
		
		if (!Binding.isValidBinding(baseException) || list.contains(baseException)) {
			return;
		}
		
		list.add(baseException);
		
		if (baseException.getKind() == ITypeBinding.EXTERNALTYPE_BINDING) {
			ExternalTypeBinding et = (ExternalTypeBinding) baseException;
			list.addAll(et.getExtendedTypes());
		}
		
		buildAllValidExceptions(list, ((PartBinding)baseException).getDefaultSuperType());
		
	}

}