/*******************************************************************************
 * Copyright © 2006, 2011 IBM Corporation and others.
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

import egl.lang.AnyException;
import org.eclipse.edt.javart.json.ValueNode;
import org.eclipse.edt.javart.resources.ExecutableBase;



public class JSONToEGLConverter
{
    
    private JSONToEGLConverter()
    {
    }


	public static void convertToEgl(final ExecutableBase program, final Object eglType, ValueNode jsonValue) throws AnyException{
		
/*		if( jsonValue instanceof NullNode )
		{
			Assign.run(program, (Object)eglType, (Object)null );
		}
		else if(eglType instanceof Container){
			if( ((Container)eglType).nullStatus() != Value.SQL_NOT_NULLABLE )
			{
				((Container)eglType).nullStatus(Value.SQL_NOT_NULL);
			}
			if (eglType instanceof OverlayContainer)
			{
				((OverlayContainer) eglType).helper().fromJSON(jsonValue, (Container) eglType);
			}
			else
			{
		    	ArrayList<Storage> contents = ((Container)eglType).contents();
		    	Storage field = null;
		    	for( int idx = 0; idx < contents.size(); idx++ )
		    	{
		    		field = contents.get(idx);
		    		String jsonName = ServiceUtilities.getJSONFieldName(program, (Container)eglType, field);
		    		ValueNode value = ((ObjectNode)jsonValue).getValue(jsonName);
		    		if(value != null){
		    			convertToEgl(program, field, value);
		    		}
		    		else{
		    			//can't find the data for the field throw exception
						JavartUtil.throwRuntimeException( Message.SOA_E_WS_PROXY_CONVERT_FROM_JSON, 	
								JavartUtil.errorMessage( program, Message.SOA_E_WS_PROXY_CONVERT_FROM_JSON, 
										new Object[] { ((Container)eglType).signature().substring(1,((Container)eglType).signature().length()-1).replace('\\', '/').replace('/','.'),field.name(), jsonName } ), program );
		    		}
		    	}
			}
		}else if(eglType instanceof DynamicArray){
			List arrayValues = ((ArrayNode)jsonValue).getValues();
			((DynamicArray)eglType).resize(program, arrayValues.size());
			for (int idx = 0; idx < arrayValues.size(); idx++) {
				ValueNode jsonObject = (ValueNode) arrayValues.get(idx);
				Object element = ((DynamicArray)eglType).get(idx);
				convertToEgl(program, element, jsonObject);			
			}
		}else if(eglType instanceof Dictionary){
			convertToDictionary(program, (Dictionary)eglType, jsonValue);
		}else if(eglType instanceof ArrayDictionary){
			convertToArrayDictionary(program, (ArrayDictionary)eglType, jsonValue);
		}else if(eglType instanceof Reference){
			((Reference)eglType).createNewValue(program);
			convertToEgl(program, ((Reference)eglType).valueObject(), jsonValue);
		}else if(eglType instanceof Value){
			if( eglType instanceof BlobValue ||
					eglType instanceof HexValue ){
				JavartUtil.throwRuntimeException( Message.SOA_E_JSON_TYPE_EXCEPTION, 	
						JavartUtil.errorMessage( program, Message.SOA_E_JSON_TYPE_EXCEPTION, 
								new Object[] { ((Value)eglType).name() } ), program );
			}
			jsonValue.accept(new DefaultJsonVisitor(){
				public boolean visit(BooleanNode bool) throws AnyException {
					Assign.run(program, eglType, Boolean.valueOf(bool.toJava()));
					return false;
				}
				
				public boolean visit(DecimalNode dec) throws AnyException {
					Assign.run(program, eglType, dec.getDecimalValue());
					return false;
				}
				
				public boolean visit(IntegerNode i) throws AnyException {
					Assign.run(program, eglType, i.getBigIntegerValue());
					return false;
				}
				
				public boolean visit(FloatingPointNode fp) throws AnyException {
					Assign.run(program, eglType, fp.getDoubleValue());
					return false;
				}
				
				public boolean visit(StringNode string) throws AnyException {
					if( eglType instanceof DateValue )
					{
						try
						{
							Assign.run(program, eglType, DateValue.convert( string.getJavaValue(), program.egl__core__StrLib.isoDateFormat.getValueAsString() ));
						}
						catch ( ParseException pe )
						{
							Assign.run(program, eglType, string.getJavaValue());
						}
					}
					else if (eglType instanceof TimeValue)
					{
						try
						{
							((TimeValue)eglType).setValue( TimeValue.convert( string.getJavaValue(), "HH:mm:ss" ));
						}
						catch ( ParseException pe )
						{
							Assign.run(program, eglType, string.getJavaValue());
						}
					}
					else if (eglType instanceof ETimestamp)
					{
						ETimestamp timestampValue = ItemFactory.createTimestamp("timestampValue", "yyyyMMddHHmmss", true);
						Assign.run(program, timestampValue, string.getJavaValue());
						Assign.run(program, eglType, timestampValue);
					}
					else
					{
						Assign.run(program, eglType, string.getJavaValue());
					}
					return false;
				}
			});
		}
		else
		{
			String field;
			if(eglType instanceof Storage)
			{
				field = ((Storage)eglType).name();
			}
			else
			{
				field = eglType.getClass().toString();
			}
			JavartUtil.throwRuntimeException( Message.SOA_E_JSON_TYPE_EXCEPTION, 	
					JavartUtil.errorMessage( program, Message.SOA_E_JSON_TYPE_EXCEPTION, 
							new Object[] { field } ), program );
		}
	}

	private static void convertToDictionary(ExecutableBase program, Dictionary dictionary, ValueNode jsonValue) throws AnyException
	{
		ObjectNode jsonObject = (ObjectNode)jsonValue;
		List pairs = jsonObject.getPairs();
		for (Iterator iter = pairs.iterator(); iter.hasNext();) {
			final NameValuePairNode pair = (NameValuePairNode) iter.next();
			AnyRef field = dictionary.lookup(pair.getName().getJavaValue(), program);
			assignDictionaryField(program, field, pair.getName().getJavaValue(), pair.getValue());
		}
	}
		
	private static void convertToArrayDictionary(ExecutableBase program, ArrayDictionary dictionary, ValueNode jsonValue) throws AnyException
	{
		ObjectNode jsonObject = (ObjectNode)jsonValue;
		List pairs = jsonObject.getPairs();
		for (Iterator iter = pairs.iterator(); iter.hasNext();) {
			final NameValuePairNode pair = (NameValuePairNode) iter.next();
			AnyRef field = dictionary.lookup(pair.getName().getJavaValue());
			assignDictionaryField(program, field, pair.getName().getJavaValue(),pair.getValue());
		}
	}
		
	private static boolean isArrayDictionary(ObjectNode jsonValue)
	    {
	    	// You are an array dictionary if:
	    	// - you have multiple children
	    	// - the children are arrays
	    	// - the children are arrays of the same length
	    	boolean isArrayDict = false;
	    	
	    	if (jsonValue.getPairs().size() > 1)
	    	{
	        	isArrayDict = true;
	        	
	    		int count = -1;
	    		for (Iterator iter = jsonValue.getPairs().iterator(); iter.hasNext(); ) 
	    		{ 
	    			ValueNode child = ((NameValuePairNode)iter.next()).getValue();
	    			int childsize = (child instanceof ArrayNode)?((ArrayNode)child).getValues().size():1;
	    			if (count==-1) count = childsize;
	    			isArrayDict &= (childsize>1 && childsize==count);
	    		}
	    	}
			return isArrayDict;
	    }
	    
    private static void assignDictionaryField( final ExecutableBase program, final AnyRef field, final String valueName, final ValueNode value ) throws AnyException
    {
		value.accept(new DefaultJsonVisitor(){
			public boolean visit(NullNode nul) throws AnyException {
				field.update( null );
				return false;
			}
			
			public boolean visit(BooleanNode bool) throws AnyException {
				field.update( new BooleanItem( "", Value.SQL_NOT_NULLABLE, Constants.SIGNATURE_BOOLEAN ));
		    	convertToEgl(program, field.valueObject(), value);
				return false;
			}
			
			public boolean visit(IntegerNode i) throws AnyException {
				field.update( new BigNumericItem( "", Value.SQL_NOT_NULLABLE, 32, Constants.EGL_TYPE_NUM, Type.NUM +  "32:0;" ));
		    	convertToEgl(program, field.valueObject(), value);
				return false;
			}
			
			public boolean visit(StringNode string) throws AnyException {
				field.update( new StringItem( "", Value.SQL_NOT_NULLABLE, Constants.SIGNATURE_STRING ));
		    	convertToEgl(program, field.valueObject(), value);
				return false;
			}
			
			public boolean visit(FloatingPointNode fp) throws AnyException {
				field.update( new FloatItem( "", Value.SQL_NOT_NULLABLE, Constants.SIGNATURE_FLOAT ));
		    	convertToEgl(program, field.valueObject(), value);
				return false;
			}					
			public boolean visit(DecimalNode dec) throws AnyException {
				BigDecimal bd = dec.getDecimalValue();
				int decimals = bd.scale() < 0? 0: bd.scale();
				field.update(  new NumericDecItem( "", Value.SQL_NOT_NULLABLE, 32, decimals, Constants.EGL_TYPE_DECIMAL, Type.DECIMAL + "32:" + String.valueOf( decimals ) + ';' ));
		    	convertToEgl(program, field.valueObject(), value);
				return false;
			}
			
			public boolean visit(ObjectNode obj) throws AnyException {
	    		if (isArrayDictionary(obj))
	    			field.update( new ArrayDictionary( valueName ));
	    		else
					field.update( new Dictionary( valueName, false, Dictionary.NONE) );
		    	convertToEgl(program, field.valueObject(), value);
				return false;
			}
			
			public boolean visit(ArrayNode array) throws AnyException {
		    	ReferenceArrayRef ary = createArray(program, valueName, array);
				field.update(ary);
				if( ary.value() != null )
				{
		    		int idx = 0;
		    		for( Iterator itr = array.getValues().iterator(); itr.hasNext(); idx++)
		    		{
		    			assignDictionaryField(program, (AnyRef)ary.value().get(idx), "eze$" + valueName + "2", (ValueNode)itr.next());
		    		}
				}
				return false;
			}
			
		});
     }

    private static ReferenceArrayRef createArray(ExecutableBase program, final String name, ArrayNode array) throws AnyException
    {
    	if( array.getValues() == null )
    	{
    		return new ReferenceArrayRef( name, null, Constants.SIGNATURE_ANY_ARRAY ){
    			private static final long serialVersionUID = 70L;
    			
    			public void createNewValue( ExecutableBase ezeProgram ) throws AnyException
    			{
    				value = new ReferenceArray( name, ezeProgram, 0, 10, Integer.MAX_VALUE, Constants.SIGNATURE_ANY_ARRAY )
    				{
    					private static final long serialVersionUID = 70L;
    					
    					public Reference makeNewElement( ExecutableBase ezeProgram ) throws AnyException
    					{
    						return new AnyRef( "eze$Temp1", null );
    					};
    				}
    				;
    			}
    		};
    	}
    	else
    	{
    		return new ReferenceArrayRef( name, new ReferenceArray( name, program, array.getValues().size(), (array.getValues().size() > 10 ? array.getValues().size() : 10), Integer.MAX_VALUE, Constants.SIGNATURE_ANY_ARRAY )
    		{
    			private static final long serialVersionUID = 70L;
    			
    			public Reference makeNewElement( ExecutableBase ezeProgram ) throws AnyException
    			{
    				return new AnyRef( "eze$" + name + "1", null );
    			};
    		}
    		, Constants.SIGNATURE_ANY_ARRAY ){
    			private static final long serialVersionUID = 70L;
    			
    			public void createNewValue( ExecutableBase ezeProgram ) throws AnyException
    			{
    				value = new ReferenceArray( name, ezeProgram, 0, 10, Integer.MAX_VALUE, Constants.SIGNATURE_ANY_ARRAY )
    				{
    					private static final long serialVersionUID = 70L;
    					
    					public Reference makeNewElement( ExecutableBase ezeProgram ) throws AnyException
    					{
    						return new AnyRef( "eze$" + name + "2", null );
    					};
    				}
    				;
    			}
    		};
    	}*/
    }
}
