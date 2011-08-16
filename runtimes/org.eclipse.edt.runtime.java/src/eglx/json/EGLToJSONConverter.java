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


public class EGLToJSONConverter
{
    
    private EGLToJSONConverter()
    {
    }
    
    public static ValueNode convertToJson(ExecutableBase program, Object storage)
	    throws AnyException
	{
//FIXME   		return processStorage(program, storage); 
   		return null;
	}

/*    private static NameValuePairNode getPair(ExecutableBase program, Container container, Storage storage)
        throws AnyException
    {
    	String name;
    	if( container != null )
    	{
    		name = ServiceUtilities.getJSONFieldName(program, container, storage);
    	}
    	else
    	{
    		name = storage.name();
    	}
        StringNode nameNode = new StringNode(name, false);
        ValueNode valueNode = processStorage(program, storage);
        return new NameValuePairNode(nameNode, valueNode);
    }

    private static ValueNode processStorage(ExecutableBase program, Object storage)
        throws AnyException
    {
        if(storage instanceof Value)
            return convertValue( program, (Value)storage);
        if(storage instanceof Container)
            return convertContainer(program, (Container)storage);
        if(storage instanceof Dictionary)
        	return convertDictionary(program, (Dictionary)storage);
        if(storage instanceof DynamicArray)
        	return convertArray(program, (DynamicArray)storage);
        if(storage instanceof Reference)
        {
            Reference reference = (Reference)storage;
            Object valueObject = reference.valueObject();
            if(valueObject == null)
                return NullNode.NULL;
            else
            	return processStorage( program,(Storage)valueObject);
        }
        if(storage instanceof ArrayDictionary)
        	return convertArrayDictionary(program, (ArrayDictionary)storage);
		String field;
		if(storage instanceof Storage)
		{
			field = ((Storage)storage).name();
		}
		else
		{
			field = storage.getClass().toString();
		}
		JavartUtil.throwRuntimeException( Message.SOA_E_JSON_TYPE_EXCEPTION, 	
				JavartUtil.errorMessage( program, Message.SOA_E_JSON_TYPE_EXCEPTION, 
						new Object[] { field } ), program );
        return NullNode.NULL;
    }

    private static ValueNode convertArray(ExecutableBase program, DynamicArray array)
        throws AnyException
    {
        ArrayNode node = new ArrayNode();
        Storage storage;
        for(Iterator iterator = array.iterator(); iterator.hasNext(); )
        {
            storage = (Storage)iterator.next();
            node.addValue(processStorage(program, storage));
        }

        return node;
    }
    
    private static ValueNode convertDictionary(ExecutableBase program, Dictionary dictionary)
	    throws AnyException
	{
	    ValueNode valueNode = null;
	    valueNode = new ObjectNode();
        String[] contents = dictionary.getKeyArray();
        for(int i = 0; i < contents.length; i++)
            ((ObjectNode)valueNode).addPair(getPair(program, null, (Storage)dictionary.get(contents[i])));
	
	    return valueNode;
	}

    private static ValueNode convertArrayDictionary(ExecutableBase program, ArrayDictionary dictionary)
    	throws AnyException
    {
    	ObjectNode valueNode = new ObjectNode();
    	Iterator keys = dictionary.getMap().keySet().iterator();
    	while ( keys.hasNext() )
    	{
    		String key = (String)keys.next();
    		valueNode.addPair(getPair(program, null, (Storage)dictionary.lookup(key)));
    	}

    	return valueNode;
    }
    
    private static ValueNode convertContainer(ExecutableBase program, Container container)
        throws AnyException
    {
        ValueNode valueNode = null;
        if(container.nullStatus() == Value.SQL_NULL)
        {
            valueNode = NullNode.NULL;
        } 
        else if (container instanceof OverlayContainer)
        {
        	valueNode = container.helper().toJSON(container);
        }
        else
        {
            valueNode = new ObjectNode();
            ArrayList contents = container.contents();
            int size = contents.size();
            for(int i = 0; i < size; i++)
                ((ObjectNode)valueNode).addPair(getPair(program, container, (Storage)contents.get(i)));

        }
        return valueNode;
    }

    private static ValueNode convertValue(ExecutableBase program, Value value)
        throws AnyException
    {
        ValueNode valueNode = null;
        if(value.getNullStatus() == Value.SQL_NULL)
            valueNode = NullNode.NULL;
        else
            switch(value.getValueType())
            {
            case Constants.VALUE_TYPE_LIMITED_STRING:
            case Constants.VALUE_TYPE_DBCHAR: 
            case Constants.VALUE_TYPE_MBCHAR:
            case Constants.VALUE_TYPE_CHAR:
            case Constants.VALUE_TYPE_STRING:
            case Constants.VALUE_TYPE_UNICODE:
            case Constants.VALUE_TYPE_CLOB:
                valueNode = new StringNode( ConvertToString.run( program, value ) , false);
                break;

            case Constants.VALUE_TYPE_HEX:
            case Constants.VALUE_TYPE_BLOB:
    			JavartUtil.throwRuntimeException( Message.SOA_E_JSON_TYPE_EXCEPTION, 	
    					JavartUtil.errorMessage( program, Message.SOA_E_JSON_TYPE_EXCEPTION, 
    							new Object[] { value.name() } ), program );
                break;

            case Constants.VALUE_TYPE_DATE:
            	StringValue format = new StringItem("format", Value.SQL_NOT_NULLABLE, Constants.SIGNATURE_STRING );
            	format.setValue( program.egl__core__StrLib.isoDateFormat.getValueAsString() );
                StringValue str = program.egl__core__StrLib.formatDate( program, (DateValue)value, format );
                valueNode = new StringNode(str.getValue(), false);
                break;

            case Constants.VALUE_TYPE_TIME:
            	format = new StringItem("format", Value.SQL_NOT_NULLABLE, Constants.SIGNATURE_STRING );
            	format.setValue( "HH:mm:ss" );
                str = program.egl__core__StrLib.formatTime( program, (TimeValue)value, format );
                valueNode = new StringNode(str.getValue(), false);
                break;

            case Constants.VALUE_TYPE_TIMESTAMP:
            	format = new StringItem("format", Value.SQL_NOT_NULLABLE, Constants.SIGNATURE_STRING );
            	format.setValue( "yyyyMMddHHmmss" );
                str = program.egl__core__StrLib.formatTimeStamp( program, (TimestampValue)value, format );
                valueNode = new StringNode(str.getValue(), false);
                break;

            case Constants.VALUE_TYPE_MONTH_INTERVAL:
                valueNode = new StringNode(((MonthIntervalValue)value).toConcatString(program), false);
                break;

            case Constants.VALUE_TYPE_SECOND_INTERVAL:
                valueNode = new StringNode(((SecondIntervalValue)value).toConcatString(program), false);
                break;

            case Constants.VALUE_TYPE_SMALLINT:
            case Constants.VALUE_TYPE_INT:
            case Constants.VALUE_TYPE_BIGINT:
            case Constants.VALUE_TYPE_SMALL_NUMERIC:
            case Constants.VALUE_TYPE_NUMERIC:
            case Constants.VALUE_TYPE_BIG_NUMERIC:
                valueNode = new IntegerNode(value.toConcatString(program));
                break;

            case Constants.VALUE_TYPE_BIN_DEC:
            case Constants.VALUE_TYPE_NUMERIC_DEC:
                valueNode = new DecimalNode( ConvertToBigDecimal.run( program, value ).toPlainString() );
                break;

            case Constants.VALUE_TYPE_BOOLEAN:
                valueNode = ((BooleanValue)value).getValue() ? ((ValueNode) (BooleanNode.TRUE)) : ((ValueNode) (BooleanNode.FALSE));
                break;

            case Constants.VALUE_TYPE_FLOAT:
            case Constants.VALUE_TYPE_SMALLFLOAT:
                valueNode = new FloatingPointNode(value.toConcatString(program));
                break;
            }
        
        return valueNode;
    }*/
}


