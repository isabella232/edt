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
package org.eclipse.edt.compiler.core.ast;

import org.eclipse.edt.compiler.internal.core.validation.type.PrimitiveTypeValidator.DateTimePattern;

/**
 * TimestampIntervalSpecPrimitiveType AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class TimestampIntervalSpecPrimitiveType extends PrimitiveType {

	private String timestampIntervalPrimitiveSpecOpt;

	public TimestampIntervalSpecPrimitiveType(Primitive prim, String timestampIntervalPrimitiveSpecOpt, int startOffset, int endOffset) {
		super(prim, startOffset, endOffset);
		
		if(timestampIntervalPrimitiveSpecOpt != null) {
			//strip quotes
			this.timestampIntervalPrimitiveSpecOpt = timestampIntervalPrimitiveSpecOpt.substring(1, timestampIntervalPrimitiveSpecOpt.length()-1);
		}
		
		if(prim == null) {
			if(timestampIntervalPrimitiveSpecOpt == null) {
				this.prim = Primitive.INTERVAL;
			}
			else {
				DateTimePattern dtPat = new DateTimePattern(timestampIntervalPrimitiveSpecOpt);
		  		if( dtPat.isValidIntervalPattern() ) {
		  			this.prim = dtPat.isMonthSpanInterval() ?
		  				Primitive.MONTHSPAN_INTERVAL : Primitive.SECONDSPAN_INTERVAL;
		  		}
		  		else {
		  			this.prim = Primitive.MONTHSPAN_INTERVAL;
		  		}
			}
		}
	}
	
	public boolean hasPrimLength() {
		return false;
	}

	public boolean hasPrimDecimals() {
		return false;
	}

	public boolean hasPrimPattern() {
		return timestampIntervalPrimitiveSpecOpt != null;
	}

	public String getPrimLength() {
		return null;
	}

	public String getPrimDecimals() {
		return null;
	}

	public String getPrimPattern() {
		return timestampIntervalPrimitiveSpecOpt;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		String newTimestampIntervalPrimitiveSpecOpt = timestampIntervalPrimitiveSpecOpt != null ? new String("\"" + timestampIntervalPrimitiveSpecOpt + "\"") : null;
		
		return new TimestampIntervalSpecPrimitiveType(getPrimitive(), newTimestampIntervalPrimitiveSpecOpt, getOffset(), getOffset() + getLength());
	}
}
