package org.eclipse.edt.compiler.binding;

import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.mof.EEnum;
import org.eclipse.edt.mof.EEnumLiteral;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EGenericType;
import org.eclipse.edt.mof.EType;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.utils.NameUtile;

public class AnnotationTypeValueGatherer extends AnnotationValueGatherer {

	EField field;
	
	public AnnotationTypeValueGatherer(Expression expr, EField field, Scope currentScope,
			Part currentBinding, IDependencyRequestor dependencyRequestor,
			IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		super(expr, currentScope, currentBinding, dependencyRequestor,
				problemRequestor, compilerOptions);
		this.field = field;
	}
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.SimpleName simpleName) {
		EEnum eenum = getEEnumType();
		if (eenum != null) {
			EEnumLiteral lit = eenum.getEEnumLiteral(simpleName.getIdentifier());
			if (lit != null) {
				simpleName.setElement(lit);
				simpleName.setBindAttempted(true);
				return false;
			}
		}
		return super.visit(simpleName);
	}
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.QualifiedName qualifiedName) {
		EEnum eenum = getEEnumType();
		if (eenum != null) {
			String eeName = NameUtile.getAsName(eenum.getName());
			String eeQualName = NameUtile.getAsName(eenum.getETypeSignature());
			if (NameUtile.equals(eeName, qualifiedName.getQualifier().getNameComponents()) || NameUtile.equals(eeQualName, qualifiedName.getQualifier().getNameComponents())) {
				EEnumLiteral lit = eenum.getEEnumLiteral(qualifiedName.getIdentifier());
				if (lit != null) {
					qualifiedName.setElement(lit);
					qualifiedName.setBindAttempted(true);
					return false;
				}
			}
		}
		return super.visit(qualifiedName);
	}
	
    private EEnum getEEnumType() {
    	if (field != null) {
    		EType type = field.getEType();
    		return getEEnumType(type);
    	}
    	return null;
    }
    
    private EEnum getEEnumType(EType type) {
    	if (type instanceof EEnum) {
    		return (EEnum)type;
    	}
    	if (type instanceof EGenericType) {
    		return getEEnumType(((EGenericType)type).getETypeArguments().get(0));
    	}
    	return null;
    }

	AnnotationValueGatherer getGatherer(Expression expr) {
		return new AnnotationTypeValueGatherer(expr, field, currentScope, currentBinding, dependencyRequestor, problemRequestor, compilerOptions);
	}    

}
