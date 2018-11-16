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
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@Override
	public Value getDefaultValue() {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
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
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@Override
	public ValueBoolean isLowerThan(Value value) {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@Override
	public ValueDuration addValue(Value value) {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@Override
	public ValueDuration subtract(Value value) {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@Override
	public ValueDuration multiply(Value value) {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@Override
	public Value divide(Value value) {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@Override
	public ValueDuration modulo(Value value) {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@Override
	public ValueDuration negate() {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@Override
	public Value convertTo(Type type) {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
