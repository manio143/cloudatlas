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

import pl.edu.mimuw.cloudatlas.model.Value.Operation;

/**
 * An exception caused by calling an unsupported unary operation on value.
 * 
 * @see IncompatibleTypesException
 * @see UnsupportedConversionException
 */
@SuppressWarnings("serial")
public class UnsupportedValueOperationException extends UnsupportedOperationException {
	private final Type left;
	private final Operation operation;
	
	/**
	 * Creates a new object representing this exception.
	 * 
	 * @param left type of a value that was an argument of an operation that caused this exception
	 * @param operation the operation that caused this exception
	 */
	protected UnsupportedValueOperationException(Type left, Operation operation) {
		super("Type: " + left + " does not provide operation " + operation + ".");
		this.left = left;
		this.operation = operation;
	}
	
	/**
	 * Gets a type of value that was an argument to an operation that caused this exception.
	 * 
	 * @return first argument of the operation
	 */
	public Type getLeft() {
		return left;
	}
	
	/**
	 * Gets an operation that caused this exception.
	 * 
	 * @return the operation
	 */
	public Operation getOperation() {
		return operation;
	}
}
