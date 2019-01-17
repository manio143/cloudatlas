package pl.edu.mimuw.cloudatlas.signer;

import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.SignerAPI;
import pl.edu.mimuw.cloudatlas.interpreter.Interpreter;
import pl.edu.mimuw.cloudatlas.interpreter.QueryResult;
import pl.edu.mimuw.cloudatlas.interpreter.query.Absyn.Program;
import pl.edu.mimuw.cloudatlas.interpreter.query.Yylex;
import pl.edu.mimuw.cloudatlas.interpreter.query.parser;
import pl.edu.mimuw.cloudatlas.model.ZMI;
import pl.edu.mimuw.cloudatlas.signer.signerExceptions.*;

import java.io.ByteArrayInputStream;
import java.rmi.RemoteException;
import java.security.PrivateKey;
import java.util.*;

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

    public SignedQueryRequest installQueries(String query) throws RemoteException {
        String queryName;
        String select;
        Program program;
        List<String> columns = new LinkedList<>();

        try {
            String[] nameSelects = query.split(":");
            queryName = nameSelects[0];
            for (Map.Entry<Long, SignedQueryRequest> entry : installedQueries.entrySet()) {
                SignedQueryRequest sqr = entry.getValue();
                if (queryName.compareTo(sqr.queryName) == 0) {
                    throw new QueryNameException();
                }
            }
            select = nameSelects[1];
            Yylex lex = new Yylex(new ByteArrayInputStream(select.getBytes()));
            try {
                program = (new parser(lex)).pProgram();
            } catch (Exception e) {
                throw new ParserException(select);
            }
            if (program != null) {
                ZMI zmi = new ZMI();
                Interpreter interpreter = new Interpreter(zmi);
                List<QueryResult> results = interpreter.interpretProgram(program);
                for (QueryResult result : results) {
                    String column = result.getName().toString();
                    if (restrictedColumns.contains(column)) {
                        throw new RestrictedColumn(column);
                    }
                    for (Map.Entry<Long, SignedQueryRequest> entry : installedQueries.entrySet()) {
                        SignedQueryRequest sqr = entry.getValue();
                        for (String col : sqr.columns) {
                            if (column.compareTo(col) == 0) {
                                throw new ColumnException(column);
                            }
                        }
                    }
                    columns.add(column);
                }
            }

        } catch (IndexOutOfBoundsException e) {
            throw new QueryFormatException();
        }

        SignedQueryRequest sqr = SignedQueryRequest.createNew(key, newQueryID, queryName, select, columns);

        newQueryID++;

        return sqr;
    }

    public SignedQueryRequest uninstallQueries(String queryName) throws RemoteException {
        SignedQueryRequest sqr = installedQueries.get(queryName);

        if (sqr == null) {
            throw new UninstallException(queryName);
        }

        return sqr;
    }
}