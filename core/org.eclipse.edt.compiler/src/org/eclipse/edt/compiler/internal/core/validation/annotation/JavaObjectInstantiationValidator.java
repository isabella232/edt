package org.eclipse.edt.compiler.internal.core.validation.annotation;

import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ConstructorBinding;
import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.ArrayType;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.DefaultBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;

public class JavaObjectInstantiationValidator implements
		IInstantiationValidationRule {

	@Override
	public void validate(Node node, final ITypeBinding typeBinding,
			IPartBinding declaringPart, final IProblemRequestor problemRequestor,
			ICompilerOptions compilerOptions) {
		
		if (!Binding.isValidBinding(typeBinding) || typeBinding.isNullable()) {
			return;
		}
		
		node.accept(new DefaultASTVisitor() {
			public boolean visit(StructureItem structureItem) {
				if (!hasDefaultConstructor(typeBinding)) {
					errorNoDefaultConstructor(structureItem.getType());
				}
				return false;
			}
			
			public boolean visit(ClassDataDeclaration classDataDeclaration) {
				if (!hasDefaultConstructor(typeBinding)) {
					errorNoDefaultConstructor(classDataDeclaration.getType());
				}
				return false;
			}
			
			public boolean visit(FunctionDataDeclaration functionDataDeclaration) {
				if (!hasDefaultConstructor(typeBinding)) {
					errorNoDefaultConstructor(functionDataDeclaration.getType());
				}
				return false;
			}
			
			public boolean visit(NewExpression newExpression) {
				if (newExpression.getType().resolveTypeBinding().getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
					ArrayType arrType = (ArrayType)newExpression.getType();
					if (arrType.hasInitialSize() && !DefaultBinder.isZeroLiteral(arrType.getInitialSize())) {
						if (!hasDefaultConstructor(typeBinding)) {
							errorNoDefaultConstructor(arrType.getBaseType());
						}
					}
					
				}
				else {
					if (!newExpression.hasArgumentList() || newExpression.getArguments().size() == 0) {
						if (!hasDefaultConstructor(typeBinding)) {
							errorNoDefaultConstructor(newExpression.getType());
						}
					}
				}
				
				return false;
			}
			
			private void errorNoDefaultConstructor(Node errorNode) {
				problemRequestor.acceptProblem(
						errorNode, 
						IProblemRequestor.NO_DEFAULT_CONSTRUCTOR, 
						new String[] {typeBinding.getCaseSensitiveName()});
			}
		});

	}

	
	private boolean hasDefaultConstructor(ITypeBinding typeBinding) {
		if (typeBinding.getKind() != ITypeBinding.EXTERNALTYPE_BINDING) {
			return false;
		}
		
		List list = ((ExternalTypeBinding)typeBinding).getConstructors();
		if (list.size() == 0) {
			return true;
		}
		
		Iterator i = list.iterator();
		while (i.hasNext()) {
			ConstructorBinding constructor = (ConstructorBinding)i.next();
			if (constructor.getParameters().size() == 0) {
				return true;
			}
		}
		
		return false;
	}
	
}
