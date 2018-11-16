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
import pl.edu.mimuw.cloudatlas.model.ValueBoolean;

/**
 * A class that wraps a Java <code>Boolean</code> object.
 */
public class ValueBoolean extends ValueSimple<Boolean> {
	/**
	 * Constructs a new <code>ValueBoolean</code> object wrapping the specified <code>value</code>.
	 * 
	 * @param value the value to wrap
	 */
	public ValueBoolean(Boolean value) {
		super(value);
	}
	
	@Override
	public Type getType() {
		return TypePrimitive.BOOLEAN;
	}
	
	@Override
	public Value getDefaultValue() {
		return new ValueBoolean(false);
	}
	
	@Override
	public ValueBoolean isLowerThan(Value value) {
		sameTypesOrThrow(value, Operation.COMPARE);
		if(isNull() || value.isNull())
			return new ValueBoolean(null);
		return new ValueBoolean(!getValue() && ((ValueBoolean)value).getValue());
	}
	
	@Override
	public ValueBoolean and(Value value) {
		sameTypesOrThrow(value, Operation.AND);
		if(isNull() || value.isNull())
			return new ValueBoolean(null);
		return new ValueBoolean(getValue() && ((ValueBoolean)value).getValue());
	}
	
	@Override
	public ValueBoolean or(Value value) { // -
		sameTypesOrThrow(value, Operation.OR);
		if(isNull() || value.isNull())
			return new ValueBoolean(null);
		return new ValueBoolean(getValue() || ((ValueBoolean)value).getValue());
	}
	
	@Override
	public ValueBoolean negate() { // !
		return new ValueBoolean(isNull()? null : !getValue());
	}
	
	@Override
	public Value convertTo(Type type) {
		switch(type.getPrimaryType()) {
			case BOOLEAN:
				return this;
			case STRING:
				return getValue() == null? ValueString.NULL_STRING : new ValueString(getValue().toString());
			default:
				throw new UnsupportedConversionException(getType(), type);
		}
	}
}
