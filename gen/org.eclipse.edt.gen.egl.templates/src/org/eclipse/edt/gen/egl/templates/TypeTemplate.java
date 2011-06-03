package org.eclipse.edt.gen.egl.templates;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

import org.eclipse.edt.gen.egl.Context;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;

public class TypeTemplate extends EglTemplate{
	public void genType(Type type, Context ctx, Member member) throws MofObjectNotFoundException, DeserializationException {
		member.setType(convertToEglType(ctx, type));
		member.setIsNullable(CommonUtilities.isNullable(type));
	}
	private org.eclipse.edt.mof.egl.Type convertToEglType(Context ctx, Type type) throws MofObjectNotFoundException, DeserializationException{
		org.eclipse.edt.mof.egl.Type eType = null;
		//FIXME handle arrays
		while((type instanceof Class && ((Class<?>)type).isArray())
				|| type instanceof ParameterizedType
				|| type instanceof GenericArrayType){
			eType = ctx.getFactory().createArrayType();
			if(type instanceof Class){
				type = ((Class<?>)type).getComponentType();
			}
			else if(type instanceof Type){
				type = getJavaType(type);
			}
		}
		
		String className = "";
		if( type instanceof Class ){
			className = ((Class<?>)type).getName();
		}
		eType = CommonUtilities.findType(ctx, className);
		
		return eType;
	}
	
	private Type getJavaType(Type type){
		 if(type instanceof ParameterizedType){
			return ((ParameterizedType)type).getActualTypeArguments()[0];
		 }
		 else if(type instanceof TypeVariable){
		 }
		 else if(type instanceof GenericArrayType){	 
			 return ((GenericArrayType)type).getGenericComponentType();
		 }
		 else if(type instanceof WildcardType){ 
		 }
		 else if(type instanceof Class){
			 return (Class<?>)type;
		 }
		 return null;
	}
}
