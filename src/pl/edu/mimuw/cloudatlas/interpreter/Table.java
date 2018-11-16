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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import pl.edu.mimuw.cloudatlas.model.Attribute;
import pl.edu.mimuw.cloudatlas.model.Type;
import pl.edu.mimuw.cloudatlas.model.TypeCollection;
import pl.edu.mimuw.cloudatlas.model.Value;
import pl.edu.mimuw.cloudatlas.model.ValueList;
import pl.edu.mimuw.cloudatlas.model.ValueNull;
import pl.edu.mimuw.cloudatlas.model.ZMI;

public class Table implements Iterable<TableRow> {
	private final List<String> columns = new ArrayList<String>();
	private final Map<String, Integer> headersMap = new HashMap<String, Integer>();
	private final List<TableRow> rows = new ArrayList<TableRow>();

	// creates whole table based on a given ZMI
	public Table(ZMI zmi) {
		Set<String> allColumns = new HashSet<String>();
		for(ZMI z : zmi.getSons())
			for(Entry<Attribute, Value> e : z.getAttributes())
				allColumns.add(e.getKey().getName());

		columns.addAll(allColumns);
		int i = 0;
		for(String c : columns)
			headersMap.put(c, i++);
		for(ZMI z : zmi.getSons()) {
			Value[] row = new Value[columns.size()];
			for(int j = 0; j < row.length; ++j)
				row[j] = ValueNull.getInstance();
			for(Entry<Attribute, Value> e : z.getAttributes())
				row[getColumnIndex(e.getKey().getName())] = e.getValue();
			appendRow(new TableRow(row));
		}
	}

	// creates an empty table with same columns as given
	public Table(Table table) {
		this.columns.addAll(table.columns);
		this.headersMap.putAll(table.headersMap);
	}

	public List<String> getColumns() {
		return Collections.unmodifiableList(columns);
	}

	public void appendRow(TableRow row) {
		if(row.getSize() != columns.size())
			throw new InternalInterpreterException("Cannot append row. Length expected: " + columns.size() + ", got: "
					+ row.getSize() + ".");
		rows.add(row);
	}

	public int getColumnIndex(String column) {
		try {
			return headersMap.get(column).intValue();
		} catch(NullPointerException exception) {
			throw new NoSuchAttributeException(column);
		}
	}

	public ValueList getColumn(String column) {
		if(column.startsWith("&")) {
			throw new NoSuchAttributeException(column);
		}
		try {
			int position = headersMap.get(column);
			List<Value> result = new ArrayList<Value>();
			for(TableRow row : rows) {
				Value v = row.getIth(position);
				result.add(v);
			}
			Type elementType = TypeCollection.computeElementType(result);
			return new ValueList(result, elementType);
		} catch(NullPointerException exception) {
			throw new NoSuchAttributeException(column);
		}
	}

	@Override
	public Iterator<TableRow> iterator() {
		return rows.iterator();
	}

	public void sort(Comparator<TableRow> comparator) {
		Collections.sort(rows, comparator);
	}
}
