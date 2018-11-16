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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pl.edu.mimuw.cloudatlas.interpreter.Result.AggregationOperation;
import pl.edu.mimuw.cloudatlas.interpreter.Result.TransformOperation;
import pl.edu.mimuw.cloudatlas.interpreter.Result.UnaryOperation;
import pl.edu.mimuw.cloudatlas.model.Type;
import pl.edu.mimuw.cloudatlas.model.Type.PrimaryType;
import pl.edu.mimuw.cloudatlas.model.TypeCollection;
import pl.edu.mimuw.cloudatlas.model.TypePrimitive;
import pl.edu.mimuw.cloudatlas.model.Value;
import pl.edu.mimuw.cloudatlas.model.ValueBoolean;
import pl.edu.mimuw.cloudatlas.model.ValueDouble;
import pl.edu.mimuw.cloudatlas.model.ValueInt;
import pl.edu.mimuw.cloudatlas.model.ValueList;
import pl.edu.mimuw.cloudatlas.model.ValueNull;
import pl.edu.mimuw.cloudatlas.model.ValueSet;
import pl.edu.mimuw.cloudatlas.model.ValueTime;

class Functions {
	private static Functions instance = null;

	private static final UnaryOperation ROUND = new UnaryOperation() {
		@Override
		public Value perform(Value v) {
			if(v.getType().isCompatible(TypePrimitive.DOUBLE)) {
				if(v.isNull())
					return new ValueDouble(null);
				return new ValueDouble((double)Math.round(((ValueDouble)v).getValue()));
			}
			throw new IllegalArgumentException("Value must have type " + TypePrimitive.DOUBLE + ".");
		}
	};

	private static final UnaryOperation FLOOR = new UnaryOperation() {
		@Override
		public Value perform(Value v) {
			if(v.getType().isCompatible(TypePrimitive.DOUBLE)) {
				if(v.isNull())
					return new ValueDouble(null);
				return new ValueDouble((double)Math.floor(((ValueDouble)v).getValue()));
			}
			throw new IllegalArgumentException("Value must have type " + TypePrimitive.DOUBLE + ".");
		}
	};

	private static final UnaryOperation CEIL = new UnaryOperation() {
		@Override
		public Value perform(Value v) {
			// TODO
			throw new UnsupportedOperationException("Not yet implemented");
		}
	};

	private static final AggregationOperation COUNT = new AggregationOperation() {
		@Override
		public ValueInt perform(ValueList values) {
			ValueList nlist = Result.filterNullsList(values);
			if(nlist.getValue() == null) {
				return new ValueInt(null);
			}
			return new ValueInt((long)nlist.size());
		}
	};

	private static final AggregationOperation SUM = new AggregationOperation() {
		@Override
		public Value perform(ValueList values) {
			// TODO
			throw new UnsupportedOperationException("Not yet implemented");
		}
	};

	private static final AggregationOperation AVERAGE = new AggregationOperation() {
		@Override
		public Value perform(ValueList values) {
			Type elementType = ((TypeCollection)values.getType()).getElementType();
			PrimaryType primaryType = elementType.getPrimaryType();

			if(primaryType != PrimaryType.INT && primaryType != PrimaryType.DOUBLE && primaryType != PrimaryType.DURATION
					&& primaryType != PrimaryType.NULL) {
				throw new IllegalArgumentException("Aggregation doesn't support type: " + elementType + ".");
			}

			ValueList nlist = Result.filterNullsList(values);
			if(nlist.getValue() == null || nlist.isEmpty()) {
				return ValueNull.getInstance();
			}

			Value result = nlist.get(0).getDefaultValue();

			for(Value v : nlist) {
				result = result.addValue(v);
			}
			Value size = primaryType == PrimaryType.DOUBLE? new ValueDouble((double)nlist.size()) : new ValueInt(
					(long)nlist.size());
			return result.divide(size);
		}
	};

	private static final AggregationOperation AND = new AggregationOperation() {
		@Override
		public ValueBoolean perform(ValueList values) { // lazy
			ValueList nlist = Result.filterNullsList(values);
			if(nlist.getValue() == null) {
				return new ValueBoolean(null);
			} else if(values.isEmpty()) {
				return new ValueBoolean(true);
			}
			for(Value v : nlist) {
				if(v.getType().isCompatible(TypePrimitive.BOOLEAN)) {
					if(v.isNull() || !((ValueBoolean)v).getValue())
						return new ValueBoolean(false);
				} else
					throw new IllegalArgumentException("Aggregation doesn't support type: " + v.getType() + ".");
			}
			return new ValueBoolean(true);
		}
	};

	private static final AggregationOperation OR = new AggregationOperation() {
		@Override
		public ValueBoolean perform(ValueList values) { // lazy
			// TODO
			throw new UnsupportedOperationException("Not yet implemented");
		}
	};

	private static final AggregationOperation MIN = new AggregationOperation() {
		@Override
		public Value perform(ValueList values) {
			ValueList nlist = Result.filterNullsList(values);
			if(nlist.getValue() == null || nlist.isEmpty()) {
				return ValueNull.getInstance();
			}
			Value result = nlist.get(0);
			for(Value v : nlist) {
				if(((ValueBoolean)v.isLowerThan(result)).getValue()) {
					result = v;
				}
			}
			return result;
		}
	};

	private static final AggregationOperation MAX = new AggregationOperation() {
		@Override
		public Value perform(ValueList values) {
			ValueList nlist = Result.filterNullsList(values);
			if(nlist.getValue() == null || nlist.isEmpty()) {
				return ValueNull.getInstance();
			}
			Value result = nlist.get(0);
			for(Value v : nlist) {
				if(((ValueBoolean)v.isLowerThan(result)).negate().and(v.isEqual(result).negate()).getValue()) {
					result = v;
				}
			}
			return result;
		}
	};

	private static final TransformOperation UNFOLD = new TransformOperation() {
		@Override
		public ValueList perform(ValueList values) {
			if(!((TypeCollection)values.getType()).getElementType().isCollection()) {
				throw new IllegalArgumentException("All elements must have a collection compatible type.");
			}
			ValueList nlist = Result.filterNullsList(values);
			if(nlist.getValue() == null) {
				return new ValueList(null,
						((TypeCollection)((TypeCollection)values.getType()).getElementType()).getElementType());
			} else if(nlist.isEmpty()) {
				return new ValueList(
						((TypeCollection)((TypeCollection)values.getType()).getElementType()).getElementType());
			}
			List<Value> ret = new ArrayList<Value>();
			for(Value v : nlist) {
				if(v.getType().getPrimaryType() == Type.PrimaryType.SET) {
					ret.addAll((ValueSet)v);
				} else if(v.getType().getPrimaryType() == Type.PrimaryType.LIST) {
					ret.addAll((ValueList)v);
				}
			}
			return new ValueList(ret,
					((TypeCollection)((TypeCollection)values.getType()).getElementType()).getElementType());
		}
	};

	private static final TransformOperation DISTINCT = new TransformOperation() {
		@Override
		public ValueList perform(ValueList values) {
			if(values.isEmpty())
				return new ValueList(((TypeCollection)values.getType()).getElementType());
			List<Value> ret = new ArrayList<Value>();
			for(Value v : values) {
				if(!ret.contains(v)) {
					ret.add(v);
				}
			}
			return new ValueList(ret, ((TypeCollection)values.getType()).getElementType());
		}
	};

	private static final TransformOperation SORT = new TransformOperation() {
		@Override
		public ValueList perform(ValueList values) {
			if(values.isEmpty())
				return new ValueList(((TypeCollection)values.getType()).getElementType());
			List<Value> ret = new ArrayList<Value>();
			ret.addAll(values);
			Collections.sort(ret, new Comparator<Value>() {
				public int compare(Value v1, Value v2) {
					if(((ValueBoolean)v1.isLowerThan(v2)).getValue()) {
						return -1;
					} else if(((ValueBoolean)v1.isEqual(v2)).getValue()) {
						return 0;
					} else {
						return 1;
					}
				}
			});
			return new ValueList(ret, ((TypeCollection)values.getType()).getElementType());
		}
	};

	private final ValueTime EPOCH;

	private Functions() {
		try {
			EPOCH = new ValueTime("2000/01/01 00:00:00.000");
		} catch(ParseException exception) {
			throw new InternalInterpreterException("Cannot parse time when creating an EPOCH object.\n"
					+ exception.getMessage());
		}
	}

	public static Functions getInstance() {
		if(instance == null)
			instance = new Functions();
		return instance;
	}

	public Result evaluate(String name, List<Result> arguments) {
		switch(name) {
			case "round":
				if(arguments.size() == 1)
					return arguments.get(0).unaryOperation(ROUND);
				break;
			case "floor":
				if(arguments.size() == 1)
					return arguments.get(0).unaryOperation(FLOOR);
				break;
			case "ceil":
				if(arguments.size() == 1)
					return arguments.get(0).unaryOperation(CEIL);
				break;
			case "now":
				if(arguments.size() == 0)
					return new ResultSingle(new ValueTime(Calendar.getInstance().getTimeInMillis()));
				break;
			case "epoch":
				if(arguments.size() == 0)
					return new ResultSingle(EPOCH);
				break;
			case "count":
				if(arguments.size() == 1)
					return arguments.get(0).aggregationOperation(COUNT);
				break;
			case "size":
				if(arguments.size() == 1)
					return arguments.get(0).valueSize();
				break;
			case "sum":
				if(arguments.size() == 1)
					return arguments.get(0).aggregationOperation(SUM);
				break;
			case "avg":
				if(arguments.size() == 1)
					return arguments.get(0).aggregationOperation(AVERAGE);
				break;
			case "land":
				if(arguments.size() == 1)
					return arguments.get(0).aggregationOperation(AND);
				break;
			case "lor":
				if(arguments.size() == 1)
					return arguments.get(0).aggregationOperation(OR);
				break;
			case "min":
				if(arguments.size() == 1)
					return arguments.get(0).aggregationOperation(MIN);
				break;
			case "max":
				if(arguments.size() == 1)
					return arguments.get(0).aggregationOperation(MAX);
				break;
			case "unfold":
				if(arguments.size() == 1)
					return arguments.get(0).transformOperation(UNFOLD);
				break;
			case "distinct":
				if(arguments.size() == 1)
					return arguments.get(0).transformOperation(DISTINCT);
				break;
			case "sort":
				if(arguments.size() == 1)
					return arguments.get(0).transformOperation(SORT);
				break;
			case "filterNulls":
				if(arguments.size() == 1)
					return arguments.get(0).filterNulls();
				break;
			case "first":
				if(arguments.size() == 2) {
					Value size = arguments.get(0).getValue();
					if(size.getType().isCompatible(TypePrimitive.INTEGER) && ((ValueInt)size).getValue().intValue() >= 0)
						return arguments.get(1).first(((ValueInt)size).getValue().intValue());
					throw new IllegalArgumentException("First argument must have type " + TypePrimitive.INTEGER
							+ " and be >= 0.");
				}
				break;
			case "last":
				if(arguments.size() == 2) {
					Value size = arguments.get(0).getValue();
					if(size.getType().isCompatible(TypePrimitive.INTEGER) && ((ValueInt)size).getValue().intValue() >= 0)
						return arguments.get(1).last(((ValueInt)size).getValue().intValue());
					throw new IllegalArgumentException("First argument must have type " + TypePrimitive.INTEGER
							+ " and be >= 0.");
				}
				break;
			case "random":
				if(arguments.size() == 2) {
					Value size = arguments.get(0).getValue();
					if(size.getType().isCompatible(TypePrimitive.INTEGER) && ((ValueInt)size).getValue().intValue() >= 0)
						return arguments.get(1).random(((ValueInt)size).getValue().intValue());
					throw new IllegalArgumentException("First argument must have type " + TypePrimitive.INTEGER
							+ " and be >= 0.");
				}
				break;
			case "to_boolean":
				if(arguments.size() == 1)
					return arguments.get(0).convertTo(TypePrimitive.BOOLEAN);
				break;
			case "to_contact":
				if(arguments.size() == 1)
					return arguments.get(0).convertTo(TypePrimitive.CONTACT);
				break;
			case "to_double":
				if(arguments.size() == 1)
					return arguments.get(0).convertTo(TypePrimitive.DOUBLE);
				break;
			case "to_duration":
				if(arguments.size() == 1)
					return arguments.get(0).convertTo(TypePrimitive.DURATION);
				break;
			case "to_integer":
				if(arguments.size() == 1)
					return arguments.get(0).convertTo(TypePrimitive.INTEGER);
				break;
			case "to_string":
				if(arguments.size() == 1)
					return arguments.get(0).convertTo(TypePrimitive.STRING);
				break;
			case "to_time":
				if(arguments.size() == 1)
					return arguments.get(0).convertTo(TypePrimitive.TIME);
				break;
			case "to_set":
				if(arguments.size() == 1) {
					Type t = arguments.get(0).getType();
					if(t.isCollection()) {
						Type elementType = ((TypeCollection)t).getElementType();
						return arguments.get(0).convertTo(new TypeCollection(Type.PrimaryType.SET, elementType));
					}
					throw new IllegalArgumentException("First argument must be a collection.");
				}
				break;
			case "to_list":
				if(arguments.size() == 1) {
					Type t = arguments.get(0).getType();
					if(t.isCollection()) {
						Type elementType = ((TypeCollection)t).getElementType();
						return arguments.get(0).convertTo(new TypeCollection(Type.PrimaryType.LIST, elementType));
					}
					throw new IllegalArgumentException("First argument must be a collection.");
				}
				break;
			case "isNull":
				if(arguments.size() == 1)
					return arguments.get(0).isNull();
				break;
			default:
				throw new IllegalArgumentException("Illegal function name.");
		}
		throw new IllegalArgumentException("Illegal number of arguments.");
	}
}
