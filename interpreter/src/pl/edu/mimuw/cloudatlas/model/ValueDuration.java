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

import static java.lang.Math.abs;

/**
 * A class representing duration in milliseconds. The duration can be negative. This is a simple wrapper of a Java
 * <code>Long</code> object.
 */
public class ValueDuration extends ValueSimple<Long> {
	/**
	 * Constructs a new <code>ValueDuration</code> object wrapping the specified <code>value</code>.
	 * 
	 * @param value the value to wrap
	 */
	public ValueDuration(Long value) {
		super(value);
	}
	
	@Override
	public Type getType() {
		return TypePrimitive.DURATION;
	}
	
	@Override
	public Value getDefaultValue() {
		return new ValueDuration(0l);
	}
	
	/**
	 * Constructs a new <code>ValueDuration</code> object from the specified amounts of different time units.
	 * 
	 * @param seconds a number of full seconds
	 * @param milliseconds a number of milliseconds (an absolute value does not have to be lower than 1000)
	 */
	public ValueDuration(long seconds, long milliseconds) {
		this(seconds * 1000l + milliseconds);
	}
	
	/**
	 * Constructs a new <code>ValueDuration</code> object from the specified amounts of different time units.
	 * 
	 * @param minutes a number of full minutes
	 * @param seconds a number of full seconds (an absolute value does not have to be lower than 60)
	 * @param milliseconds a number of milliseconds (an absolute value does not have to be lower than 1000)
	 */
	public ValueDuration(long minutes, long seconds, long milliseconds) {
		this(minutes * 60l + seconds, milliseconds);
	}
	
	/**
	 * Constructs a new <code>ValueDuration</code> object from the specified amounts of different time units.
	 * 
	 * @param hours a number of full hours
	 * @param minutes a number of full minutes (an absolute value does not have to be lower than 60)
	 * @param seconds a number of full seconds (an absolute value does not have to be lower than 60)
	 * @param milliseconds a number of milliseconds (an absolute value does not have to be lower than 1000)
	 */
	public ValueDuration(long hours, long minutes, long seconds, long milliseconds) {
		this(hours * 60l + minutes, seconds, milliseconds);
	}
	
	/**
	 * Constructs a new <code>ValueDuration</code> object from the specified amounts of different time units.
	 * 
	 * @param days a number of full days
	 * @param hours a number of full hours (an absolute value does not have to be lower than 24)
	 * @param minutes a number of full minutes (an absolute value does not have to be lower than 60)
	 * @param seconds a number of full seconds (an absolute value does not have to be lower than 60)
	 * @param milliseconds a number of milliseconds (an absolute value does not have to be lower than 1000)
	 */
	public ValueDuration(long days, long hours, long minutes, long seconds, long milliseconds) {
		this(days * 24l + hours, minutes, seconds, milliseconds);
	}
	
	/**
	 * Constructs a new <code>ValueDuration</code> object from its textual representation. The representation has
	 * format: <code>sd hh:mm:ss.lll</code> where:
	 * <ul>
	 * <li><code>s</code> is a sign (<code>+</code> or <code>-</code>),</li>
	 * <li><code>d</code> is a number of days,</li>
	 * <li><code>hh</code> is a number of hours (between <code>00</code> and <code>23</code>),</li>
	 * <li><code>mm</code> is a number of minutes (between <code>00</code> and <code>59</code>),</li>
	 * <li><code>ss</code> is a number of seconds (between <code>00</code> and <code>59</code>),</li>
	 * <li><code>lll</code> is a number of milliseconds (between <code>000</code> and <code>999</code>).</li>
	 * </ul>
	 * <p>
	 * All fields are obligatory.
	 * 
	 * @param value a textual representation of a duration
	 * @throws IllegalArgumentException if <code>value</code> does not meet described rules
	 */
	public ValueDuration(String value) {
		this(parseDuration(value));
	}
	
	private static long parseDuration(String value) {
		if(value == null) {
			illegalParseFormat();
		}
		value = value.trim();
		int base = value.charAt(0) == '+' ? 1 : (value.charAt(0) == '-' ? -1 : illegalParseFormat("sign"));
		String[] daysRest = value.substring(1, value.length()).split(" ");
		int days = 0;
		int hours = 0;
		int minutes = 0;
		int seconds = 0;
		int miliseconds = 0;
		try {
			days = Integer.parseInt(daysRest[0]);
			if (0 > days) {
				illegalParseFormat("days");
			}
		} catch (Exception e) {
			illegalParseFormat("days");
		}
		String rest = value.substring(daysRest[0].length() + 2, value.length());
		String hoursMinutesRest[] = rest.split(":");
		try {
			hours = Integer.parseInt(hoursMinutesRest[0]);
			if (0 > hours || hours > 23) {
				illegalParseFormat("hours");
			}
		} catch (Exception e) {
			illegalParseFormat("hours");
		}
		try {
			minutes = Integer.parseInt(hoursMinutesRest[1]);
			if (0 > minutes || minutes > 59) {
				illegalParseFormat("minutes");
			}
		} catch (Exception e) {
			illegalParseFormat("minutes");
		}
		rest = rest.substring(hoursMinutesRest[0].length() + hoursMinutesRest[1].length() + 2, rest.length());
		String secondsRest[] = rest.split("\\.");
		try {
			seconds = Integer.parseInt(secondsRest[0]);
			if (0 > seconds || seconds > 59) {
				illegalParseFormat("seconds");
			}
		} catch (Exception e) {
			illegalParseFormat("seconds");
		}
		rest = rest.substring(secondsRest[0].length() + 1, rest.length());
		try {
			miliseconds = Integer.parseInt(rest);
			if (0 > miliseconds || miliseconds > 999) {
				illegalParseFormat("miliseconds");
			}
		} catch (Exception e) {
			illegalParseFormat("miliseconds");
		}
		return base * ((((24l * days + hours) * 60l + minutes) * 60l + seconds) * 1000 + miliseconds);
	}

	private static int parseDigit(String value, int index) {
		return value.charAt(index) >= '0' && value.charAt(index) <= '9' ? value.charAt(index) - '0' : illegalParseFormat();
	}

	private static int illegalParseFormat() {
		throw new IllegalArgumentException("Value has to abide the following format: \"sd hh:mm:ss.lll\"");
	}

	private static int illegalParseFormat(String illegalPart) {
		throw new IllegalArgumentException("Failed to parse: " + illegalPart
				+ ". Value has to abide the following format: \"sd hh:mm:ss.lll\"");
	}

	@Override
	public String toString() {
		if (getValue() == null)
			return ValueString.NULL_STRING.getValue();
		if(getValue() == 0)
			return "+0";
		long remaining = getValue();
		boolean positive = remaining > 0;
		remaining = abs(remaining);
		int days = (int) Math.floor(remaining / (1000 * 60 * 60 * 24));
		remaining -= days * 24 * 60 * 60 * 1000;
		int hours = (int) Math.floor(remaining / (1000 * 60 * 60));
		remaining -= hours * 60 * 60 * 1000;
		int minutes = (int) Math.floor(remaining / (1000 * 60));
		remaining -= minutes * 60 * 1000;
		int seconds = (int) Math.floor(remaining / 1000);
		remaining -= seconds * 1000;
		int milliseconds = (int) remaining;
		return String.format("%s%d %02d:%02d:%02d.%03d"
				, positive ? "+" : "-", days, hours, minutes, seconds, milliseconds);
	}

	@Override
	public ValueBoolean isLowerThan(Value value) {
		sameTypesOrThrow(value, Operation.COMPARE);
		if(isNull() || value.isNull())
			return new ValueBoolean(null);
		return new ValueBoolean(getValue() < ((ValueDuration)value.convertTo(getType())).getValue());
	}
	
	@Override
	public ValueDuration addValue(Value value) {
		sameTypesOrThrow(value, Operation.ADD);
		if(isNull() || value.isNull())
			return new ValueDuration((Long)null);
		return new ValueDuration(getValue() + ((ValueDuration)value.convertTo(getType())).getValue());
	}
	
	@Override
	public ValueDuration subtract(Value value) {
		sameTypesOrThrow(value, Operation.SUBTRACT);
		if(isNull() || value.isNull())
			return new ValueDuration((Long)null);
		return new ValueDuration(getValue() - ((ValueDuration)value.convertTo(getType())).getValue());
	}
	
	@Override
	public ValueDuration multiply(Value value) {
		sameTypesOrThrow(value, Operation.MULTIPLY);
		if(isNull() || value.isNull())
			return new ValueDuration((Long)null);
		return new ValueDuration(getValue() * ((ValueDuration)value.convertTo(getType())).getValue());
	}
	
	@Override
	public Value divide(Value value) {
		sameTypesOrThrow(value, Operation.DIVIDE);
		if(isNull() || value.isNull())
			return new ValueDuration((Long)null);
		return new ValueDuration(getValue() / ((ValueDuration)value.convertTo(getType())).getValue());
	}
	
	@Override
	public ValueDuration modulo(Value value) {
		sameTypesOrThrow(value, Operation.SUBTRACT);
		if(isNull() || value.isNull())
			return new ValueDuration((Long)null);
		return new ValueDuration(getValue() % ((ValueDuration)value.convertTo(getType())).getValue());
	}
	
	@Override
	public ValueDuration negate() {
		if(isNull()) return new ValueDuration((Long)null);
		return new ValueDuration(-1 * getValue());
	}
	
	@Override
	public Value convertTo(Type type) {
		switch(type.getPrimaryType()) {
			case DOUBLE:
				return new ValueDouble(getValue() == null? null : getValue().doubleValue());
			case DURATION:
				return this;
			case INT:
				return new ValueInt(getValue());
			case STRING:
				return getValue() == null? ValueString.NULL_STRING : new ValueString(Long.toString(getValue()
						.longValue()));
			default:
				throw new UnsupportedConversionException(getType(), type);
		}
	}
}
