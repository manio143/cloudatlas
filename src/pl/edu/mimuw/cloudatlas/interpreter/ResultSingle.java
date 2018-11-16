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

package pl.edu.mimuw.cloudatlas.interpreter;

import pl.edu.mimuw.cloudatlas.model.Type;
import pl.edu.mimuw.cloudatlas.model.Value;
import pl.edu.mimuw.cloudatlas.model.ValueBoolean;
import pl.edu.mimuw.cloudatlas.model.ValueList;

class ResultSingle extends Result {
	private final Value value;

	public ResultSingle(Value value) {
		this.value = value;
	}
	
	@Override
	protected ResultSingle binaryOperationTyped(BinaryOperation operation, ResultSingle right) {
		return new ResultSingle(operation.perform(value, right.value));
	}

	@Override
	public ResultSingle unaryOperation(UnaryOperation operation) {
		return new ResultSingle(operation.perform(value));
	}

	@Override
	protected Result callMe(BinaryOperation operation, Result left) {
		return left.binaryOperationTyped(operation, this);
	}

	@Override
	public Value getValue() {
		return value;
	}

	@Override
	public ValueList getList() {
		throw new UnsupportedOperationException("Not a ResultList.");
	}

	@Override
	public ValueList getColumn() {
		throw new UnsupportedOperationException("Not a ResultColumn.");
	}

	@Override
	public Result filterNulls() {
		throw new UnsupportedOperationException("Operation filterNulls not supported on ResultSingle.");
	}

	@Override
	public Result first(int size) {
		throw new UnsupportedOperationException("Operation first not supported on ResultSingle.");
	}

	@Override
	public Result last(int size) {
		throw new UnsupportedOperationException("Operation last not supported on ResultSingle.");
	}

	@Override
	public Result random(int size) {
		throw new UnsupportedOperationException("Operation random not supported on ResultSingle.");
	}

	@Override
	public ResultSingle convertTo(Type to) {
		return new ResultSingle(value.convertTo(to));
	}

	@Override
	public ResultSingle isNull() {
		return new ResultSingle(new ValueBoolean(value.isNull()));
	}

	@Override
	public Type getType() {
		return value.getType();
	}
}
