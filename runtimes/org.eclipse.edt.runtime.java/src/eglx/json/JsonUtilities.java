/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package eglx.json;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.json.ArrayNode;
import org.eclipse.edt.javart.json.NameValuePairNode;
import org.eclipse.edt.javart.json.ObjectNode;
import org.eclipse.edt.javart.json.StringNode;
import org.eclipse.edt.javart.json.ValueNode;
import org.eclipse.edt.javart.services.*;
import org.eclipse.edt.runtime.java.eglx.lang.EAny;

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
		for(int idx = 0; idx < signature.parameters().length; idx++ ){
			FunctionParameter functionParameter = signature.parameters()[idx];
			if(functionParameter.kind().equals(FunctionParameterKind.OUT)){
				parameters.add(EAny.ezeWrap(null));
			}
			else{
				Object obj = JsonLib.convertToEgl(functionParameter.parameterType(), functionParameter.asOptions(), null, (ValueNode)jsonParameters.getValues().get(jsonIdx++));
				if(method.getParameterTypes()[idx].equals(AnyBoxedObject.class)){
					obj = EAny.ezeWrap(obj);
				}
				parameters.add(obj);
			}
		}

		return parameters.toArray(new Object[parameters.size()]);
	}
}
