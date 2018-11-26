/**
 * Copyright (c) 2014, University of Warsaw
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package pl.edu.mimuw.cloudatlas.model;

import java.text.ParseException;

import pl.edu.mimuw.cloudatlas.model.Value;
import pl.edu.mimuw.cloudatlas.model.ValueString;

/**
 * A class that wraps a Java <code>String</code> object.
 */
public class ValueString extends ValueSimple<String> {
	/**
	 * A result of conversion values representing null to <code>ValueString</code>.
	 */
	protected static final ValueString NULL_STRING = new ValueString("NULL");
	
	/**
	 * Constructs a new <code>ValueString</code> object wrapping the specified <code>value</code>.
	 * 
	 * @param value the value to wrap
	 */
	public ValueString(String value) {
		super(value);
	}
	
	@Override
	public Type getType() {
		return TypePrimitive.STRING;
	}
	
	@Override
	public Value getDefaultValue() {
		return new ValueString("");
	}
	
	@Override
	public ValueBoolean isLowerThan(Value value) {
		sameTypesOrThrow(value, Operation.COMPARE);
		if(isNull() || value.isNull())
			return new ValueBoolean(null);
		return new ValueBoolean(getValue().compareTo(((ValueString)value).getValue()) < 0);
	}
	
	@Override
	public ValueString addValue(Value value) {
		sameTypesOrThrow(value, Operation.ADD);
		if(isNull() || value.isNull())
			return new ValueString(null);
		return new ValueString(getValue().concat(((ValueString)value).getValue()));
	}
	
	@Override
	public ValueBoolean regExpr(Value value) {
		sameTypesOrThrow(value, Operation.REG_EXPR);
		if(isNull() || value.isNull())
			return new ValueBoolean(null);
		return new ValueBoolean(getValue().matches(((ValueString)value).getValue()));
	}
	
	@Override
	public Value convertTo(Type type) {
		switch(type.getPrimaryType()) {
			case BOOLEAN:
				return new ValueBoolean(Boolean.parseBoolean(getValue()));
			case DOUBLE:
				try {
					return new ValueDouble(Double.parseDouble(getValue()));
				} catch(NumberFormatException exception) {
					return new ValueDouble(null);
				}
			case DURATION:
				return new ValueDuration(getValue());
			case INT:
				try {
					return new ValueInt(Long.parseLong(getValue()));
				} catch(NumberFormatException exception) {
					return new ValueInt(null);
				}
			case STRING:
				return getValue() == null? ValueString.NULL_STRING : this;
			case TIME:
				try {
					return new ValueTime(getValue());
				} catch(ParseException exception) {
					return new ValueTime((Long)null);
				}
			default:
				throw new UnsupportedConversionException(getType(), type);
		}
	}
	
	@Override
	public ValueInt valueSize() {
		return new ValueInt(getValue() == null? null : (long)getValue().length());
	}
}
