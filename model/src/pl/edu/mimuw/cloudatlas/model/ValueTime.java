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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import pl.edu.mimuw.cloudatlas.model.Value;
import pl.edu.mimuw.cloudatlas.model.ValueTime;

/**
 * A class representing the POSIX time in milliseconds. This is a simple wrapper of a Java <code>Long</code> object.
 */
public class ValueTime extends ValueSimple<Long> {
	/**
	 * A format of string representing <code>ValueTime</code> when constructing from or converting to a
	 * <code>String</code> object.
	 */
	public static final DateFormat TIME_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
	
	/**
	 * Constructs a new <code>ValueTime</code> object wrapping the specified value.
	 * 
	 * @param value the POSIX time
	 */
	public ValueTime(Long value) {
		super(value);
	}
	
	@Override
	public Type getType() {
		return TypePrimitive.TIME;
	}
	
	@Override
	public Value getDefaultValue() {
		return new ValueTime(0l);
	}
	
	/**
	 * Constructs a new <code>ValueTime</code> object from its textual representation.
	 * 
	 * @param time a time formatted according to {@link #TIME_FORMAT}
	 * @throws ParseException if the <code>time</code> is incorrect
	 * @see #TIME_FORMAT
	 */
	public ValueTime(String time) throws ParseException {
		this(TIME_FORMAT.parse(time).getTime());
	}
	
	@Override
	public ValueBoolean isLowerThan(Value value) {
		sameTypesOrThrow(value, Operation.COMPARE);
		if(isNull() || value.isNull())
			return new ValueBoolean(null);
		return new ValueBoolean(getValue() < ((ValueTime)value).getValue());
	}
	
	@Override
	public ValueTime addValue(Value value) {
		if(!value.getType().isCompatible(TypePrimitive.DURATION))
			throw new IncompatibleTypesException(getType(), value.getType(), Operation.ADD);
		if(isNull() || value.isNull())
			return new ValueTime((Long)null);
		return new ValueTime(getValue() + ((ValueDuration)value).getValue());
	}
	
	@Override
	public Value subtract(Value value) {
		if(value.getType().isCompatible(TypePrimitive.DURATION)) {
			if(isNull() || value.isNull())
				return new ValueTime((Long)null);
			return new ValueTime(getValue() - ((ValueDuration)value).getValue());
		} else if(value.getType().isCompatible(TypePrimitive.TIME)) {
			if(isNull() || value.isNull())
				return new ValueTime((Long)null);
			return new ValueDuration(getValue() - ((ValueTime)value).getValue());
		}
		throw new IncompatibleTypesException(getType(), value.getType(), Operation.SUBTRACT);
		
	}
	
	@Override
	public Value convertTo(Type type) {
		switch(type.getPrimaryType()) {
			case STRING:
				return getValue() == null? ValueString.NULL_STRING : new ValueString(TIME_FORMAT.format(getValue()));
			case TIME:
				return this;
			default:
				throw new UnsupportedConversionException(getType(), type);
		}
	}
}
