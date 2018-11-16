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

import pl.edu.mimuw.cloudatlas.model.Value;
import pl.edu.mimuw.cloudatlas.model.ValueDouble;

/**
 * A class that wraps a Java <code>Double</code> object.
 */
public class ValueDouble extends ValueSimple<Double> {
	/**
	 * Constructs a new <code>ValueDouble</code> object wrapping the specified <code>value</code>.
	 * 
	 * @param value the value to wrap
	 */
	public ValueDouble(Double value) {
		super(value);
	}
	
	@Override
	public Type getType() {
		return TypePrimitive.DOUBLE;
	}
	
	@Override
	public Value getDefaultValue() {
		return new ValueDouble(0.0);
	}
	
	@Override
	public ValueBoolean isLowerThan(Value value) {
		sameTypesOrThrow(value, Operation.COMPARE);
		if(isNull() || value.isNull())
			return new ValueBoolean(null);
		return new ValueBoolean(getValue() < ((ValueDouble)value).getValue());
	}
	
	@Override
	public ValueDouble addValue(Value value) {
		sameTypesOrThrow(value, Operation.ADD);
		if(isNull() || value.isNull())
			return new ValueDouble(null);
		return new ValueDouble(getValue() + ((ValueDouble)value).getValue());
	}
	
	@Override
	public ValueDouble subtract(Value value) {
		sameTypesOrThrow(value, Operation.SUBTRACT);
		if(isNull() || value.isNull())
			return new ValueDouble(null);
		return new ValueDouble(getValue() - ((ValueDouble)value).getValue());
	}
	
	@Override
	public ValueDouble multiply(Value value) {
		sameTypesOrThrow(value, Operation.MULTIPLY);
		if(isNull() || value.isNull())
			return new ValueDouble(null);
		return new ValueDouble(getValue() * ((ValueDouble)value).getValue());
	}
	
	@Override
	public ValueDouble divide(Value value) {
		sameTypesOrThrow(value, Operation.DIVIDE);
		if(isNull() || value.isNull())
			return new ValueDouble(null);
		return new ValueDouble(getValue() / ((ValueDouble)value).getValue());
	}
	
	@Override
	public ValueDouble negate() {
		return new ValueDouble(isNull()? null : -getValue());
	}
	
	@Override
	public Value convertTo(Type type) {
		switch(type.getPrimaryType()) {
			case DOUBLE:
				return this;
			case INT:
				return new ValueInt(getValue() == null? null : getValue().longValue());
			case STRING:
				return getValue() == null? ValueString.NULL_STRING : new ValueString(getValue().toString());
			default:
				throw new UnsupportedConversionException(getType(), type);
		}
	}
}
