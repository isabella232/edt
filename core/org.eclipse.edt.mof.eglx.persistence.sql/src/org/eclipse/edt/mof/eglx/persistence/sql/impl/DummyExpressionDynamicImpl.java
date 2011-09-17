package org.eclipse.edt.mof.eglx.persistence.sql.impl;

import java.util.List;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.StringLiteral;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.eglx.persistence.sql.DummyExpression;
import org.eclipse.edt.mof.impl.DynamicEClass;
import org.eclipse.edt.mof.impl.DynamicEObject;

public class DummyExpressionDynamicImpl extends DynamicEObject implements DummyExpression {
	static EClass eClass;
	
	static {
		eClass = new DynamicEClass();
		eClass.setName("DummyExpression");
		eClass.setPackageName("org.eclipse.edt.mof.eglx.persistence.sql");
		eClass.getSuperTypes().add(IrFactory.INSTANCE.getLHSExprEClass());
	}
	
	public static DummyExpression newInstance() {
		return (DummyExpression)eClass.newInstance();
	}
	
	@Override
	public Type getType() {
		return (Type)eGet("type");
	}
	
	public void setType(Type type) {
		eSet("type", type);
	}

	public String getExpr() {
		return (String)eGet("expr");
	}

	public void setExpr(String value) {
		eSet("expr", value);
		
	}

	@Override
	public Expression getQualifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isNullable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Annotation> getAnnotations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Annotation getAnnotation(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Annotation getAnnotation(AnnotationType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addAnnotation(Annotation ann) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAnnotation(Annotation ann) {
		// TODO Auto-generated method stub
		
	}

}
