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

import pl.edu.mimuw.cloudatlas.model.Type.PrimaryType;
import pl.edu.mimuw.cloudatlas.model.Value;
import pl.edu.mimuw.cloudatlas.model.ValueInt;

/**
 * A class that wraps a Java <code>Long</code> object.
 */
public class ValueInt extends ValueSimple<Long> {
	/**
	 * Constructs a new <code>ValueInt</code> object wrapping the specified <code>value</code>.
	 * 
	 * @param value the value to wrap
	 */
	public ValueInt(Long value) {
		super(value);
	}
	
	@Override
	public Type getType() {
		return TypePrimitive.INTEGER;
	}
	
	@Override
	public Value getDefaultValue() {
		return new ValueInt(0l);
	}
	
	@Override
	public ValueBoolean isLowerThan(Value value) {
		sameTypesOrThrow(value, Operation.COMPARE);
		if(isNull() || value.isNull())
			return new ValueBoolean(null);
		return new ValueBoolean(getValue() < ((ValueInt)value).getValue());
	}
	
	@Override
	public ValueInt addValue(Value value) {
		sameTypesOrThrow(value, Operation.ADD);
		if(isNull() || value.isNull())
			return new ValueInt(null);
		return new ValueInt(getValue() + ((ValueInt)value).getValue());
	}
	
	@Override
	public ValueInt subtract(Value value) {
		sameTypesOrThrow(value, Operation.SUBTRACT);
		if(isNull() || value.isNull())
			return new ValueInt(null);
		return new ValueInt(getValue() - ((ValueInt)value).getValue());
	}
	
	@Override
	public Value multiply(Value value) {
		if(value.getType().getPrimaryType() == PrimaryType.DURATION)
			return value.multiply(this);
		sameTypesOrThrow(value, Operation.MULTIPLY);
		if(isNull() || value.isNull())
			return new ValueInt(null);
		return new ValueInt(getValue() * ((ValueInt)value).getValue());
	}
	
	@Override
	public ValueDouble divide(Value value) {
		sameTypesOrThrow(value, Operation.DIVIDE);
		if(value.isNull())
			return new ValueDouble(null);
		if(((ValueInt)value).getValue() == 0l)
			throw new ArithmeticException("Division by zero.");
		if(isNull())
			return new ValueDouble(null);
		return new ValueDouble((double)getValue() / ((ValueInt)value).getValue());
	}
	
	@Override
	public ValueInt modulo(Value value) {
		sameTypesOrThrow(value, Operation.MODULO);
		if(value.isNull())
			return new ValueInt(null);
		if(((ValueInt)value).getValue() == 0l)
			throw new ArithmeticException("Division by zero.");
		if(isNull())
			return new ValueInt(null);
		return new ValueInt(getValue() % ((ValueInt)value).getValue());
	}
	
	@Override
	public ValueInt negate() {
		return new ValueInt(isNull()? null : -getValue());
	}
	
	@Override
	public Value convertTo(Type type) {
		switch(type.getPrimaryType()) {
			case DOUBLE:
				return new ValueDouble(getValue() == null? null : getValue().doubleValue());
			case DURATION:
				return new ValueDuration(getValue());
			case INT:
				return this;
			case STRING:
				return getValue() == null? ValueString.NULL_STRING : new ValueString(Long.toString(getValue()
						.longValue()));
			default:
				throw new UnsupportedConversionException(getType(), type);
		}
	}
}
