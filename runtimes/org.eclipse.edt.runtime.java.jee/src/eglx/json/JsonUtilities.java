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
package eglx.json;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.json.ArrayNode;
import org.eclipse.edt.javart.json.NameValuePairNode;
import org.eclipse.edt.javart.json.ObjectNode;
import org.eclipse.edt.javart.json.StringNode;
import org.eclipse.edt.javart.json.ValueNode;
import org.eclipse.edt.javart.resources.ExecutableBase;
import org.eclipse.edt.javart.services.FunctionParameter;
import org.eclipse.edt.javart.services.FunctionParameterKind;
import org.eclipse.edt.javart.services.FunctionSignature;
import org.eclipse.edt.javart.services.ServiceUtilities;
import org.eclipse.edt.javart.util.DateTimeUtil;
import org.eclipse.edt.javart.util.JavartUtil;
import org.eclipse.edt.runtime.java.eglx.lang.AnyValue;
import org.eclipse.edt.runtime.java.eglx.lang.EAny;
import org.eclipse.edt.runtime.java.eglx.lang.EBigint;
import org.eclipse.edt.runtime.java.eglx.lang.EBoolean;
import org.eclipse.edt.runtime.java.eglx.lang.EDate;
import org.eclipse.edt.runtime.java.eglx.lang.EDecimal;
import org.eclipse.edt.runtime.java.eglx.lang.EFloat;
import org.eclipse.edt.runtime.java.eglx.lang.EInt;
import org.eclipse.edt.runtime.java.eglx.lang.EList;
import org.eclipse.edt.runtime.java.eglx.lang.ESmallfloat;
import org.eclipse.edt.runtime.java.eglx.lang.ESmallint;
import org.eclipse.edt.runtime.java.eglx.lang.EString;
import org.eclipse.edt.runtime.java.eglx.lang.ETime;
import org.eclipse.edt.runtime.java.eglx.lang.ETimestamp;

import eglx.lang.AnyException;


public class JsonUtilities {
	private static String JSON_RPC_ERROR_ID = "error";
	private static String JSON_RPC_ERROR_NAME_ID = "name";
	public static String JSON_RPC_ERROR_NAME_VALUE = "JSONRPCError";
	private static String JSON_RPC_ERROR_MESSAGE_ID = "message";
	private static String JSON_RPC_ERROR_CODE_ID = "code";

	private JsonUtilities() {
	}
	
	public static String trimJSON(String json) {
		if ( json == null || json.equals( "" ) ) {
			//this was causing a defect: RATLC01169730, part 2
			//charAt() was called without checking if the string were null first
			//used to throw a StringIndexOutOfBoundsException
			//don't know if it's better to return "" or null
			//right now, setting it to return itself; garbage in, garbage out
			return json;
		}
		else if (json.charAt(0) == '{') {
			int curlyCount = 1;
			//don't look at { or } if it's inside "'s because 
			//that is data and it's legal to have them in quotes, but that will throw off the actual {} counting
			boolean ignoreCurlyBraces = false;
			for (int n=1; n<json.length(); n++) {
				switch (json.charAt(n)) {
				case '"':
					if( !ignoreCurlyBraces ){
						ignoreCurlyBraces = true;
					}
					else if( json.charAt(n-1) != '\\' ){
						// if it's a \ then it's a quote within the data
						// so this is the end of a data block start looking at the {} again
						ignoreCurlyBraces = false;
					}
					break;
				case '{': 
					if( !ignoreCurlyBraces ){
						curlyCount++; 
					}
					break;
				case '}': 
					if( !ignoreCurlyBraces ){
						curlyCount--; 
					}
					break;
				}
				if (curlyCount == 0)
					json = json.substring(0,n+1);
			}
		}
		return json;
	}
	public static String createJsonAnyException(AnyException jrte )
	{
		/*
		{
			"error" : {
				"name" : "JSONRPCError"
			    "code" : ""egl message ID"",
			    "message" : "egl exception message"
				"error" : {
				    "name" : "fully qualified egl exception type",  i.e. "egl.core.ServiceInvocationException"
				    "messageID" : "egl message ID",
				    "message" : "egl exception message",
				    //other fields defined by the individual exception record
				    //ie service invocation exception
				    "source"  : "egl" or "web",
				    "detail1" : "detail1 text", 
				    "detail2" : "detail2 text", 
				    "detail3" : "detail3 text" 
				}
			}
		}
		 */
		ObjectNode wrapper = new ObjectNode();
		ObjectNode error = new ObjectNode();
		error.addPair( new NameValuePairNode( new StringNode( JSON_RPC_ERROR_NAME_ID, true ), new StringNode(JSON_RPC_ERROR_NAME_VALUE, false ) ) );
		error.addPair( new NameValuePairNode( new StringNode( JSON_RPC_ERROR_CODE_ID, true ), new StringNode(jrte.getMessageID(), false ) ) );
		error.addPair( new NameValuePairNode( new StringNode( JSON_RPC_ERROR_MESSAGE_ID, true ), new StringNode( ServiceUtilities.getMessage( jrte ), false ) ) );
		wrapper.addPair( new NameValuePairNode( new StringNode( JSON_RPC_ERROR_ID, true ), error ) );
		if( jrte instanceof AnyException )
		{
			AnyException record = (AnyException)jrte;
			ObjectNode eglException;
			try
			{
				eglException = (ObjectNode)JsonLib.convertToJsonNode(record);
			}
			catch( AnyException j )
			{
				eglException = new ObjectNode();
			}

			error.addPair( new NameValuePairNode( new StringNode( JSON_RPC_ERROR_ID, true ), eglException ) );
			eglException.addPair( new NameValuePairNode( new StringNode( JSON_RPC_ERROR_NAME_ID, true ), new StringNode( record.getClass().getName(), false ) ) );
		}
		return wrapper.toJson();
	}
	
	public static Object[] getParameters(Method method, ArrayNode jsonParameters){
		FunctionSignature signature = method.getAnnotation(FunctionSignature.class);
		List<Object> parameters = new ArrayList<Object>();
		int jsonIdx = 0;
		for(int idx = 0; idx < method.getParameterTypes().length; idx++ ){
			FunctionParameter functionParameter = signature.parameters()[idx];
			if(functionParameter.kind().equals(FunctionParameterKind.OUT)){
				try {
					Class<?> fieldType = functionParameter.jsonInfo().clazz();
					Object newValue = null;
					if(functionParameter.arrayDimensions() > 0){
						newValue = EList.ezeNew(fieldType);
					}
					else if(fieldType.equals(EBigint.class)){
						newValue = EBigint.asBigint(0);
			        }
					else if(fieldType.equals(EBoolean.class)){
			        	newValue = EBoolean.asBoolean(false);
			        }
					else if(fieldType.equals(EDate.class)){
			    		Calendar cal = DateTimeUtil.getBaseCalendar();
			    		cal.get(Calendar.YEAR);
			    		newValue = EDate.asDate(cal);
			        }
					else if(fieldType.equals(EDecimal.class)){
			        	if(functionParameter.jsonInfo().asOptions() != null && functionParameter.jsonInfo().asOptions().length > 1){
				        	int length = Integer.parseInt(functionParameter.jsonInfo().asOptions()[0]);
				        	int decimal = Integer.parseInt(functionParameter.jsonInfo().asOptions()[1]);
				        	newValue = EDecimal.asDecimal(0, length, decimal);
			        	}
			        	else{
			        		newValue = EDecimal.asDecimal(0);
			        	}
			        }
					else if(fieldType.equals(EFloat.class)){
			        	newValue = EFloat.asFloat(0);
			        }
					else if(fieldType.equals(EInt.class)){
			        	newValue = EInt.asInt(0);
			        }
			        else if(fieldType.equals(ESmallfloat.class)){
			        	newValue = ESmallfloat.asSmallfloat(0);
			        }
			        else if(fieldType.equals(ESmallint.class)){
			        	newValue = ESmallint.asSmallint(0);
			        }
			        else if(fieldType.equals(EString.class)){
			        	newValue = "";
			        }
					else if(fieldType.equals(ETime.class)){
			    		Calendar cal = DateTimeUtil.getBaseCalendar();
			    		cal.get(Calendar.YEAR);
			    		newValue = ETime.asTime(cal);
			        }
			        else if(fieldType.equals(ETimestamp.class)){
		        		int start = ETimestamp.YEAR_CODE;
		        		int end = ETimestamp.SECOND_CODE;
			        	if(functionParameter.jsonInfo().asOptions() != null && functionParameter.jsonInfo().asOptions().length > 1){
			        		start = getETimestampStaticField(functionParameter.jsonInfo().asOptions()[0]);
			        		end = getETimestampStaticField(functionParameter.jsonInfo().asOptions()[1]);
			        	}
			    		Calendar cal = DateTimeUtil.getBaseCalendar();
			    		cal.get(Calendar.YEAR);
			        	newValue = ETimestamp.asTimestamp(cal, start, end);
			        }
			        else if(AnyValue.class.isAssignableFrom(fieldType)){
		        		newValue =  fieldType.newInstance();
			        }
			        else if(ExecutableBase.class.isAssignableFrom(fieldType)){
			        	newValue =  fieldType.newInstance();
			        }
			        else if(fieldType.equals(Enum.class)){
			        	for(Object enumConstant : fieldType.getEnumConstants()){
			        		if(enumConstant instanceof Enum){
			        			newValue = enumConstant;
			        			break;
			        		}
			        	}
			        }

					if(method.getParameterTypes()[idx].equals(AnyBoxedObject.class)){
						newValue = EAny.ezeWrap(newValue);
					}
					parameters.add(newValue);
				} catch (Exception e) {
					JavartUtil.makeEglException(e);
				}
					
			}
			else{
				Object obj = JsonLib.convertToEgl(functionParameter.jsonInfo().clazz(), functionParameter.jsonInfo().asOptions(), null, (ValueNode)jsonParameters.getValues().get(jsonIdx++));
				if(method.getParameterTypes()[idx].equals(AnyBoxedObject.class)){
					obj = EAny.ezeWrap(obj);
				}
				parameters.add(obj);
			}
		}

		return parameters.toArray(new Object[parameters.size()]);
	}
	public static int getETimestampStaticField(String fieldName){
		if("YEAR_CODE".equalsIgnoreCase(fieldName)){
			return ETimestamp.YEAR_CODE;
		}
		else if("SECOND_CODE".equalsIgnoreCase(fieldName)){
			return ETimestamp.SECOND_CODE;
		}
		else if("MONTH_CODE".equalsIgnoreCase(fieldName)){
			return ETimestamp.MONTH_CODE;
		}
		else if("MINUTE_CODE".equalsIgnoreCase(fieldName)){
			return ETimestamp.MINUTE_CODE;
		}
		else if("HOUR_CODE".equalsIgnoreCase(fieldName)){
			return ETimestamp.HOUR_CODE;
		}
		else if("FRACTION6_CODE".equalsIgnoreCase(fieldName)){
			return ETimestamp.FRACTION6_CODE;
		}
		else if("FRACTION5_CODE".equalsIgnoreCase(fieldName)){
			return ETimestamp.FRACTION5_CODE;
		}
		else if("FRACTION4_CODE".equalsIgnoreCase(fieldName)){
			return ETimestamp.FRACTION4_CODE;
		}
		else if("FRACTION3_CODE".equalsIgnoreCase(fieldName)){
			return ETimestamp.FRACTION3_CODE;
		}
		else if("FRACTION2_CODE".equalsIgnoreCase(fieldName)){
			return ETimestamp.FRACTION2_CODE;
		}
		else if("FRACTION1_CODE".equalsIgnoreCase(fieldName)){
			return ETimestamp.FRACTION1_CODE;
		}
		else if("DAY_CODE".equalsIgnoreCase(fieldName)){
			return ETimestamp.DAY_CODE;
		}
		return 0;
	}
	public static Object wrapCalendar(Object field, Class<?> type, String[] asOptions){
		if(field instanceof AnyBoxedObject<?>){
			field = ((AnyBoxedObject<?>)field).ezeUnbox();
		}
		if(field instanceof List){
			List<Object> wrappedList = new ArrayList<Object>();
			for(Object obj : ((List<?>)field)){
				wrappedList.add(wrapCalendar(obj, type, asOptions));
			}
			return wrappedList;
		}
		else if(type != null && type.equals(EDate.class)){
			return new EDate((Calendar)field);
		}
		else if(type != null && type.equals(ETime.class)){
			return new ETime((Calendar)field);
		}
		else if(type != null && type.equals(ETimestamp.class)){
    		int start = ETimestamp.YEAR_CODE;
    		int end = ETimestamp.SECOND_CODE;
        	if(asOptions != null && asOptions.length > 1){
        		start = eglx.json.JsonUtilities.getETimestampStaticField(asOptions[0]);
        		end = eglx.json.JsonUtilities.getETimestampStaticField(asOptions[1]);
        	}
			return new ETimestamp((Calendar)field, start, end);
		}
		else{
			return field;
		}

	}
	
}
