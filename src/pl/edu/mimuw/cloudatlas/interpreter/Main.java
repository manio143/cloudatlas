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

import java.io.ByteArrayInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import pl.edu.mimuw.cloudatlas.interpreter.query.Yylex;
import pl.edu.mimuw.cloudatlas.interpreter.query.parser;
import pl.edu.mimuw.cloudatlas.model.PathName;
import pl.edu.mimuw.cloudatlas.model.TypePrimitive;
import pl.edu.mimuw.cloudatlas.model.Value;
import pl.edu.mimuw.cloudatlas.model.ValueBoolean;
import pl.edu.mimuw.cloudatlas.model.ValueContact;
import pl.edu.mimuw.cloudatlas.model.ValueDouble;
import pl.edu.mimuw.cloudatlas.model.ValueDuration;
import pl.edu.mimuw.cloudatlas.model.ValueInt;
import pl.edu.mimuw.cloudatlas.model.ValueList;
import pl.edu.mimuw.cloudatlas.model.ValueSet;
import pl.edu.mimuw.cloudatlas.model.ValueString;
import pl.edu.mimuw.cloudatlas.model.ValueTime;
import pl.edu.mimuw.cloudatlas.model.ZMI;

public class Main {
	private static ZMI root;
	
	public static void main(String[] args) throws Exception {
		root = createTestHierarchy();
		Scanner scanner = new Scanner(System.in);
		scanner.useDelimiter("\\n");
		while(scanner.hasNext())
			executeQueries(root, scanner.next());
		scanner.close();
	}
	
	private static PathName getPathName(ZMI zmi) {
		String name = ((ValueString)zmi.getAttributes().get("name")).getValue();
		return zmi.getFather() == null? PathName.ROOT : getPathName(zmi.getFather()).levelDown(name);
	}
	
	private static void executeQueries(ZMI zmi, String query) throws Exception {
		if(!zmi.getSons().isEmpty()) {
			for(ZMI son : zmi.getSons())
				executeQueries(son, query);
			Interpreter interpreter = new Interpreter(zmi);
			Yylex lex = new Yylex(new ByteArrayInputStream(query.getBytes()));
			try {
				List<QueryResult> result = interpreter.interpretProgram((new parser(lex)).pProgram());
				PathName zone = getPathName(zmi);
				for(QueryResult r : result) {
					System.out.println(zone + ": " + r);
					zmi.getAttributes().addOrChange(r.getName(), r.getValue());
				}
			} catch(InterpreterException exception) {}
		}
	}
	
	private static ValueContact createContact(String path, byte ip1, byte ip2, byte ip3, byte ip4)
			throws UnknownHostException {
		return new ValueContact(new PathName(path), InetAddress.getByAddress(new byte[] {
			ip1, ip2, ip3, ip4
		}));
	}
	
	private static ZMI createTestHierarchy() throws ParseException, UnknownHostException {
		ValueContact violet07Contact = createContact("/uw/violet07", (byte)10, (byte)1, (byte)1, (byte)10);
		ValueContact khaki13Contact = createContact("/uw/khaki13", (byte)10, (byte)1, (byte)1, (byte)38);
		ValueContact khaki31Contact = createContact("/uw/khaki31", (byte)10, (byte)1, (byte)1, (byte)39);
		ValueContact whatever01Contact = createContact("/uw/whatever01", (byte)82, (byte)111, (byte)52, (byte)56);
		ValueContact whatever02Contact = createContact("/uw/whatever02", (byte)82, (byte)111, (byte)52, (byte)57);
		
		List<Value> list;
		
		root = new ZMI();
		root.getAttributes().add("level", new ValueInt(0l));
		root.getAttributes().add("name", new ValueString(null));
		root.getAttributes().add("owner", new ValueString("/uw/violet07"));
		root.getAttributes().add("timestamp", new ValueTime("2012/11/09 20:10:17.342"));
		root.getAttributes().add("contacts", new ValueSet(TypePrimitive.CONTACT));
		root.getAttributes().add("cardinality", new ValueInt(0l));
		
		ZMI uw = new ZMI(root);
		root.addSon(uw);
		uw.getAttributes().add("level", new ValueInt(1l));
		uw.getAttributes().add("name", new ValueString("uw"));
		uw.getAttributes().add("owner", new ValueString("/uw/violet07"));
		uw.getAttributes().add("timestamp", new ValueTime("2012/11/09 20:8:13.123"));
		uw.getAttributes().add("contacts", new ValueSet(TypePrimitive.CONTACT));
		uw.getAttributes().add("cardinality", new ValueInt(0l));
		
		ZMI pjwstk = new ZMI(root);
		root.addSon(pjwstk);
		pjwstk.getAttributes().add("level", new ValueInt(1l));
		pjwstk.getAttributes().add("name", new ValueString("pjwstk"));
		pjwstk.getAttributes().add("owner", new ValueString("/pjwstk/whatever01"));
		pjwstk.getAttributes().add("timestamp", new ValueTime("2012/11/09 20:8:13.123"));
		pjwstk.getAttributes().add("contacts", new ValueSet(TypePrimitive.CONTACT));
		pjwstk.getAttributes().add("cardinality", new ValueInt(0l));
		
		ZMI violet07 = new ZMI(uw);
		uw.addSon(violet07);
		violet07.getAttributes().add("level", new ValueInt(2l));
		violet07.getAttributes().add("name", new ValueString("violet07"));
		violet07.getAttributes().add("owner", new ValueString("/uw/violet07"));
		violet07.getAttributes().add("timestamp", new ValueTime("2012/11/09 18:00:00.000"));
		list = Arrays.asList(new Value[] {
			khaki31Contact, whatever01Contact
		});
		violet07.getAttributes().add("contacts", new ValueSet(new HashSet<Value>(list), TypePrimitive.CONTACT));
		violet07.getAttributes().add("cardinality", new ValueInt(1l));
		list = Arrays.asList(new Value[] {
			violet07Contact,
		});
		violet07.getAttributes().add("members", new ValueSet(new HashSet<Value>(list), TypePrimitive.CONTACT));
		violet07.getAttributes().add("creation", new ValueTime("2011/11/09 20:8:13.123"));
		violet07.getAttributes().add("cpu_usage", new ValueDouble(0.9));
		violet07.getAttributes().add("num_cores", new ValueInt(3l));
		violet07.getAttributes().add("has_ups", new ValueBoolean(null));
		list = Arrays.asList(new Value[] {
			new ValueString("tola"), new ValueString("tosia"),
		});
		violet07.getAttributes().add("some_names", new ValueList(list, TypePrimitive.STRING));
		violet07.getAttributes().add("expiry", new ValueDuration(13l, 12l, 0l, 0l, 0l));
		
		ZMI khaki31 = new ZMI(uw);
		uw.addSon(khaki31);
		khaki31.getAttributes().add("level", new ValueInt(2l));
		khaki31.getAttributes().add("name", new ValueString("khaki31"));
		khaki31.getAttributes().add("owner", new ValueString("/uw/khaki31"));
		khaki31.getAttributes().add("timestamp", new ValueTime("2012/11/09 20:03:00.000"));
		list = Arrays.asList(new Value[] {
			violet07Contact, whatever02Contact,
		});
		khaki31.getAttributes().add("contacts", new ValueSet(new HashSet<Value>(list), TypePrimitive.CONTACT));
		khaki31.getAttributes().add("cardinality", new ValueInt(1l));
		list = Arrays.asList(new Value[] {
			khaki31Contact
		});
		khaki31.getAttributes().add("members", new ValueSet(new HashSet<Value>(list), TypePrimitive.CONTACT));
		khaki31.getAttributes().add("creation", new ValueTime("2011/11/09 20:12:13.123"));
		khaki31.getAttributes().add("cpu_usage", new ValueDouble(null));
		khaki31.getAttributes().add("num_cores", new ValueInt(3l));
		khaki31.getAttributes().add("has_ups", new ValueBoolean(false));
		list = Arrays.asList(new Value[] {
			new ValueString("agatka"), new ValueString("beatka"), new ValueString("celina"),
		});
		khaki31.getAttributes().add("some_names", new ValueList(list, TypePrimitive.STRING));
		khaki31.getAttributes().add("expiry", new ValueDuration(-13l, -11l, 0l, 0l, 0l));
		
		ZMI khaki13 = new ZMI(uw);
		uw.addSon(khaki13);
		khaki13.getAttributes().add("level", new ValueInt(2l));
		khaki13.getAttributes().add("name", new ValueString("khaki13"));
		khaki13.getAttributes().add("owner", new ValueString("/uw/khaki13"));
		khaki13.getAttributes().add("timestamp", new ValueTime("2012/11/09 21:03:00.000"));
		list = Arrays.asList(new Value[] {});
		khaki13.getAttributes().add("contacts", new ValueSet(new HashSet<Value>(list), TypePrimitive.CONTACT));
		khaki13.getAttributes().add("cardinality", new ValueInt(1l));
		list = Arrays.asList(new Value[] {
			khaki13Contact,
		});
		khaki13.getAttributes().add("members", new ValueSet(new HashSet<Value>(list), TypePrimitive.CONTACT));
		khaki13.getAttributes().add("creation", new ValueTime((Long)null));
		khaki13.getAttributes().add("cpu_usage", new ValueDouble(0.1));
		khaki13.getAttributes().add("num_cores", new ValueInt(null));
		khaki13.getAttributes().add("has_ups", new ValueBoolean(true));
		list = Arrays.asList(new Value[] {});
		khaki13.getAttributes().add("some_names", new ValueList(list, TypePrimitive.STRING));
		khaki13.getAttributes().add("expiry", new ValueDuration((Long)null));
		
		ZMI whatever01 = new ZMI(pjwstk);
		pjwstk.addSon(whatever01);
		whatever01.getAttributes().add("level", new ValueInt(2l));
		whatever01.getAttributes().add("name", new ValueString("whatever01"));
		whatever01.getAttributes().add("owner", new ValueString("/uw/whatever01"));
		whatever01.getAttributes().add("timestamp", new ValueTime("2012/11/09 21:12:00.000"));
		list = Arrays.asList(new Value[] {
			violet07Contact, whatever02Contact,
		});
		whatever01.getAttributes().add("contacts", new ValueSet(new HashSet<Value>(list), TypePrimitive.CONTACT));
		whatever01.getAttributes().add("cardinality", new ValueInt(1l));
		list = Arrays.asList(new Value[] {
			whatever01Contact,
		});
		whatever01.getAttributes().add("members", new ValueSet(new HashSet<Value>(list), TypePrimitive.CONTACT));
		whatever01.getAttributes().add("creation", new ValueTime("2012/10/18 07:03:00.000"));
		whatever01.getAttributes().add("cpu_usage", new ValueDouble(0.1));
		whatever01.getAttributes().add("num_cores", new ValueInt(7l));
		list = Arrays.asList(new Value[] {
			new ValueString("rewrite")
		});
		whatever01.getAttributes().add("php_modules", new ValueList(list, TypePrimitive.STRING));
		
		ZMI whatever02 = new ZMI(pjwstk);
		pjwstk.addSon(whatever02);
		whatever02.getAttributes().add("level", new ValueInt(2l));
		whatever02.getAttributes().add("name", new ValueString("whatever02"));
		whatever02.getAttributes().add("owner", new ValueString("/uw/whatever02"));
		whatever02.getAttributes().add("timestamp", new ValueTime("2012/11/09 21:13:00.000"));
		list = Arrays.asList(new Value[] {
			khaki31Contact, whatever01Contact,
		});
		whatever02.getAttributes().add("contacts", new ValueSet(new HashSet<Value>(list), TypePrimitive.CONTACT));
		whatever02.getAttributes().add("cardinality", new ValueInt(1l));
		list = Arrays.asList(new Value[] {
			whatever02Contact,
		});
		whatever02.getAttributes().add("members", new ValueSet(new HashSet<Value>(list), TypePrimitive.CONTACT));
		whatever02.getAttributes().add("creation", new ValueTime("2012/10/18 07:04:00.000"));
		whatever02.getAttributes().add("cpu_usage", new ValueDouble(0.4));
		whatever02.getAttributes().add("num_cores", new ValueInt(13l));
		list = Arrays.asList(new Value[] {
			new ValueString("odbc")
		});
		whatever02.getAttributes().add("php_modules", new ValueList(list, TypePrimitive.STRING));
		
		return root;
	}
}
