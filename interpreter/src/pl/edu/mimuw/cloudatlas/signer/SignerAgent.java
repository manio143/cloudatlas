package pl.edu.mimuw.cloudatlas.signer;

import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.SignerAPI;
import pl.edu.mimuw.cloudatlas.interpreter.Interpreter;
import pl.edu.mimuw.cloudatlas.interpreter.QueryResult;
import pl.edu.mimuw.cloudatlas.interpreter.query.Absyn.Program;
import pl.edu.mimuw.cloudatlas.interpreter.query.Yylex;
import pl.edu.mimuw.cloudatlas.interpreter.query.parser;
import pl.edu.mimuw.cloudatlas.model.ValueNull;
import pl.edu.mimuw.cloudatlas.model.ZMI;
import pl.edu.mimuw.cloudatlas.signer.signerExceptions.*;

import java.io.ByteArrayInputStream;
import java.rmi.RemoteException;
import java.security.PrivateKey;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignerAgent implements SignerAPI {
    private final PrivateKey key;
    private long newQueryID = 0;

    private final Map<Long, SignedQueryRequest> installedQueries = new HashMap<>();

    private final List<String> restrictedColumns = new LinkedList<>();

    public SignerAgent(PrivateKey key) {
        this.key = key;
        setRestrictedColumns();
    }

    private void setRestrictedColumns() {
        restrictedColumns.add("name");
        restrictedColumns.add("level");
        restrictedColumns.add("timestamp");
        restrictedColumns.add("owner");
        restrictedColumns.add("contacts");
        restrictedColumns.add("cardinality");
        restrictedColumns.add("freshness");
    }

    private void printThrow(SignerException exception) {
        System.out.println(exception.getMessage());
        throw exception;
    }

    private List<String> possibleAttributeNames(String select) {
        List<String> attributes = new LinkedList<>();
        Pattern pattern = Pattern.compile("[a-zA-Z][a-zA-Z0-9_]*");
        Matcher matcher = pattern.matcher(select) ;
        while (matcher.find()) {
            attributes.add(matcher.group());
        }
        return attributes;
    }

    public SignedQueryRequest installQueries(String query) throws RemoteException {
        String queryName = "";
        String select = "";
        Program program;
        List<String> columns = new LinkedList<>();

        try {
            String[] nameSelects = query.split(":");
            queryName = nameSelects[0];
            for (Map.Entry<Long, SignedQueryRequest> entry : installedQueries.entrySet()) {
                SignedQueryRequest sqr = entry.getValue();
                if (queryName.compareTo(sqr.queryName) == 0) {
                    printThrow(new QueryNameException());
                }
            }
            select = nameSelects[1];
            Yylex lex = new Yylex(new ByteArrayInputStream(select.getBytes()));
            try {
                program = (new parser(lex)).pProgram();
                ZMI zmi = new ZMI();
                ZMI son = new ZMI(zmi);
                zmi.addSon(son);

                List<String> attributes = possibleAttributeNames(select);
                for (String attribute : attributes) {
                    son.getAttributes().addOrChange(attribute, ValueNull.getInstance());
                }

                System.out.println(attributes);

                Interpreter interpreter = new Interpreter(zmi);
                List<QueryResult> results = interpreter.interpretProgram(program);
                for (QueryResult result : results) {
                    String column = result.getName().toString();
                    if (restrictedColumns.contains(column)) {
                        printThrow(new RestrictedColumn(column));
                    }
                    for (Map.Entry<Long, SignedQueryRequest> entry : installedQueries.entrySet()) {
                        SignedQueryRequest sqr = entry.getValue();
                        for (String col : sqr.columns) {
                            if (column.compareTo(col) == 0) {
                                printThrow(new ColumnException(column));
                            }
                        }
                    }
                    columns.add(column);
                }
            } catch (SignerException e) {
                printThrow(e);
            } catch (Exception e) {
                e.printStackTrace();
                printThrow(new ParserException(select));
            }

        } catch (IndexOutOfBoundsException e) {
            printThrow(new QueryFormatException());
        }

        System.out.println("Query accepted to install: " + query);
        System.out.println("Columns of the query: " + columns);

        SignedQueryRequest sqr = SignedQueryRequest.createNew(key, newQueryID, queryName, select, columns);

        System.out.println("Created SignedQueryRequest");

        installedQueries.put(newQueryID, sqr);

        newQueryID++;

        return sqr;
    }

    public SignedQueryRequest uninstallQueries(String queryName) throws RemoteException {
        SignedQueryRequest sqr = installedQueries.get(queryName);

        if (sqr == null) {
            printThrow(new UninstallException(queryName));
        }

        System.out.println("Query accepted to uninstall: " + queryName);

        return sqr;
    }
}