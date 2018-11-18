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
import pl.edu.mimuw.cloudatlas.model.TypeCollection;
import pl.edu.mimuw.cloudatlas.model.Value;
import pl.edu.mimuw.cloudatlas.model.ValueBoolean;
import pl.edu.mimuw.cloudatlas.model.ValueList;
import pl.edu.mimuw.cloudatlas.model.ValueNull;

class ResultList extends Result {
	private final ValueList value;

	public ResultList(ValueList value) {
		this.value = value;
	}
	
	@Override
	protected ResultList binaryOperationTyped(BinaryOperation operation, ResultSingle right) {
		ValueList r = new ValueList(((TypeCollection)value.getType()).getElementType());
		for (Value v : value.getValue()) {
			r.add(operation.perform(v, right.getValue()));
		}
		return new ResultList(r);
	}

	@Override
	public ResultList unaryOperation(UnaryOperation operation) {
		ValueList r = new ValueList(((TypeCollection)value.getType()).getElementType());
		for (Value v : value.getValue()) {
			r.add(operation.perform(v));
		}
		return new ResultList(r);
	}

	@Override
	public Result binaryOperation(BinaryOperation operation, Result right) {
		if(right.getValue().getType().isCollection())
			return new ResultList((ValueList)operation.perform(value, right.getValue()));
		return binaryOperationTyped(operation, (ResultSingle)right);
	}

	@Override
	protected Result callMe(BinaryOperation operation, Result left) {
		throw new UnsupportedOperationException("Not a ResultSingle");
	}

	@Override
	public Value getValue() {
		throw new UnsupportedOperationException("Not a ResultSingle");
	}

	@Override
	public ValueList getList() {
		return value;
	}

	@Override
	public ValueList getColumn() {
		throw new UnsupportedOperationException("Not a ResultColumn.");
	}

	@Override
	public Result filterNulls() {
		return new ResultList(filterNullsList(value));
	}

	@Override
	public Result first(int size) {
		for(Value v : value.getValue())
			return new ResultSingle(v);
		return new ResultSingle(ValueNull.getInstance());
	}

	@Override
	public Result last(int size) {
		Value r = ValueNull.getInstance();
		for(Value v : value.getValue())
			r = v;
		return new ResultSingle(r);
	}

	@Override
	public Result random(int size) {
		return new ResultList(randomList(value, size));
	}

	@Override
	public Result convertTo(Type to) {
		Value v = value.convertTo(to);
		if(v.getType().getPrimaryType().equals(Type.PrimaryType.LIST))
			return new ResultList((ValueList)v);
		else return new ResultSingle(v);
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
