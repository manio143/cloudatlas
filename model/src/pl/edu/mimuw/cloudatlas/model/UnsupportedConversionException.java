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
 * An exception describing unsupported conversion of a value to another type.
 * 
 * @see IncompatibleTypesException
 * @see UnsupportedValueOperationException
 */
@SuppressWarnings("serial")
public class UnsupportedConversionException extends UnsupportedOperationException {
	private final Type from;
	private final Type to;
	
	/**
	 * Creates a new instance of this this exception.
	 * 
	 * @param from source type of an unsupported conversion
	 * @param to destination type of an unsupported conversion
	 */
	protected UnsupportedConversionException(Type from, Type to) {
		super("Type " + from + " cannot be converted to " + to + ".");
		this.from = from;
		this.to = to;
	}
	
	/**
	 * Gets a source type of an unsupported conversion that caused this exception.
	 * 
	 * @return source type
	 */
	public Type getFrom() {
		return from;
	}
	
	/**
	 * Gets a destination type of an unsupported conversion that caused this exception.
	 * 
	 * @return destination type
	 */
	public Type getTo() {
		return to;
	}
}
