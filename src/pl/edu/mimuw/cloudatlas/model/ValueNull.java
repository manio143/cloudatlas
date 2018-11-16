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
import pl.edu.mimuw.cloudatlas.model.ValueNull;

/**
 * A special null value of an unknown type introduced to deal with nulls. This class is a singleton.
 * 
 * @see TypePrimitve#NULL
 * @see Type#isCompatible(Value)
 */
public class ValueNull extends Value {
	private static ValueNull instance = null;
	
	private ValueNull() {}
	
	/**
	 * Gets a singleton instance of a <code>ValueNull</code> class. Every call to this method returns the same
	 * reference.
	 * 
	 * @return an instance of <code>ValueNull</code>
	 */
	public static ValueNull getInstance() {
		if(instance == null)
			instance = new ValueNull();
		return instance;
	}
	
	@Override
	public Value getDefaultValue() {
		return instance;
	}
	
	@Override
	public Value convertTo(Type type) {
		switch(type.getPrimaryType()) {
			case STRING:
				return ValueString.NULL_STRING;
			default:
				return this;
		}
	}
	
	@Override
	public Type getType() {
		return TypePrimitive.NULL;
	}
	
	@Override
	public boolean isNull() {
		return true;
	}
	
	@Override
	public Value isEqual(Value value) {
		return new ValueBoolean(isNull() && value.isNull());
	}
	
	@Override
	public Value isLowerThan(Value value) {
		if(value == getInstance())
			return this;
		return value.isLowerThan(this);
	}
	
	@Override
	public Value addValue(Value value) {
		if(value == getInstance())
			return this;
		return value.addValue(this);
	}
	
	@Override
	public Value subtract(Value value) {
		if(value == getInstance())
			return this;
		return value.subtract(this);
	}
	
	@Override
	public Value multiply(Value value) {
		if(value == getInstance())
			return this;
		return value.multiply(this);
	}
	
	@Override
	public Value divide(Value value) {
		if(value == getInstance())
			return this;
		return value.divide(this);
	}
	
	@Override
	public Value modulo(Value value) {
		if(value == getInstance())
			return this;
		return value.modulo(this);
	}
	
	@Override
	public Value and(Value value) {
		if(value == getInstance())
			return this;
		return value.and(this);
	}
	
	@Override
	public Value or(Value value) {
		if(value == getInstance())
			return this;
		return value.or(this);
	}
	
	@Override
	public Value regExpr(Value value) {
		if(value == getInstance())
			return this;
		return value.regExpr(this);
	}
	
	@Override
	public Value negate() {
		return this;
	}
	
	@Override
	public Value valueSize() {
		return this;
	}
}
