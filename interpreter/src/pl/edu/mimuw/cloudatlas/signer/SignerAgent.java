package pl.edu.mimuw.cloudatlas.signer;

import pl.edu.mimuw.cloudatlas.agent.utility.Logger;
import pl.edu.mimuw.cloudatlas.cloudAtlasAPI.SignerAPI;
import pl.edu.mimuw.cloudatlas.interpreter.Interpreter;
import pl.edu.mimuw.cloudatlas.interpreter.QueryResult;
import pl.edu.mimuw.cloudatlas.interpreter.query.Absyn.Program;
import pl.edu.mimuw.cloudatlas.interpreter.query.Yylex;
import pl.edu.mimuw.cloudatlas.interpreter.query.parser;
import pl.edu.mimuw.cloudatlas.model.ValueNull;
import pl.edu.mimuw.cloudatlas.model.ZMI;
import pl.edu.mimuw.cloudatlas.signer.signerExceptions.*;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignerAgent implements SignerAPI {
    private final PrivateKey key;
    private long newQueryID = 0;

    private final String queriesFile;

    private final Map<Long, SignedQueryRequest> installedQueries = new HashMap<>();
    private final Map<String, Long> queryToId = new HashMap<>();

    private final Set<String> queryNames = new HashSet<>();
    private final Set<String> columnNames = new HashSet<>();

    private final List<String> restrictedColumns = new LinkedList<>();

    private final Logger logger = new Logger("SIGNER");

    private final String LEFT_MARKER = "&";
    private final String RIGHT_MARKER = "#";

    public SignerAgent(PrivateKey key, String queriesFile) {
        this.key = key;
        this.queriesFile = queriesFile;
        setRestrictedColumns();
        readQueriesFile();
    }

    private void readQueriesFile() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(queriesFile)));

            String[] fragments = content.split(LEFT_MARKER);

            for (int i = 1; i < fragments.length; i++) {
                logger.log("Next record: " + fragments[i]);

                String[] typeQuery = fragments[i].split(RIGHT_MARKER);
                try {
                    String type = typeQuery[0];
                    logger.log(type);
                    String query = typeQuery[1];
                    logger.log(query);

                    switch (type) {
                        case "INSTALL":
                            try {
                                installQueries(query, false);
                            } catch (SignerException e) {
                                logger.errLog("Exception while installing from record: " + e.getMessage());
                            }
                            break;
                        case "UNINSTALL":
                            try {
                                uninstallQueries(query, false);
                            } catch (SignerException e) {
                                logger.errLog("Exception while uninstalling from record: " + e.getMessage());
                            }
                            break;
                        default:
                            logger.errLog("Incorrect type of record: " + type);
                    }
                } catch (IndexOutOfBoundsException e) {
                    logger.errLog("Index out of bounds exception, malformed query: " + fragments[i]);
                }

            }

        } catch (IOException e) {
            logger.errLog("IOException while trying to read: " + queriesFile);

        }
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
        logger.errLog(exception.getMessage());
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

    private void tryRecord(String toRecord) {
        try(FileWriter fw = new FileWriter(queriesFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(toRecord);
            logger.log("Recorded: " + toRecord);
        } catch (IOException e) {
            logger.errLog("Failed to record: " + toRecord);
        }
    }

    public SignedQueryRequest installQueries(String query) {
        return installQueries(query, true);
    }

    private SignedQueryRequest installQueries(String query, boolean record) {
        logger.log("Trying to install query: " + query);

        String queryName = "";
        String select = "";
        Program program;
        List<String> columns = new LinkedList<>();

        try {
            String[] nameSelects = query.split(":");
            queryName = nameSelects[0];
            if (queryNames.contains(queryName)) {
                printThrow(new QueryNameException(queryName));
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

                logger.log("Possible attribute names: " + attributes.toString());

                Interpreter interpreter = new Interpreter(zmi);
                List<QueryResult> results = interpreter.interpretProgram(program);
                for (QueryResult result : results) {
                    String column = result.getName().toString();
                    if (restrictedColumns.contains(column)) {
                        printThrow(new RestrictedColumn(column));
                    }
                    if (columnNames.contains(column)) {
                        printThrow(new ColumnException(column));
                    }
                    columns.add(column);
                }
            } catch (SignerException e) {
                throw e;
            } catch (Exception e) {
                printThrow(new ParserException(select));
            }

        } catch (IndexOutOfBoundsException e) {
            printThrow(new QueryFormatException());
        }

        SignedQueryRequest sqr = SignedQueryRequest.createNew(key, newQueryID, queryName, select, columns);

        logger.log("Created SignedQueryRequest");

        queryNames.add(queryName);
        columnNames.addAll(columns);
        installedQueries.put(newQueryID, sqr);
        queryToId.put(queryName, newQueryID);

        if (record) {
            String toRecord = LEFT_MARKER + "INSTALL" + RIGHT_MARKER + query;
            tryRecord(toRecord);
        }

        logger.log("Query accepted to install: " + query);
        logger.log("Columns of the query: " + columns);

        newQueryID++;

        return sqr;
    }

    public SignedQueryRequest uninstallQueries(String queryName) {
        return uninstallQueries(queryName, true);
    }

    public SignedQueryRequest uninstallQueries(String queryName, boolean record) {
        logger.log("Trying to uninstall query: " + queryName);

        if (!queryToId.containsKey(queryName)) {
            printThrow(new UninstallException(queryName));
        }

        long id = queryToId.remove(queryName);

        SignedQueryRequest sqr = installedQueries.remove(id);

        queryNames.remove(sqr.queryName);
        columnNames.removeAll(sqr.columns);

        if (record) {
            String toRecord = LEFT_MARKER + "UNINSTALL" + RIGHT_MARKER + queryName;
            tryRecord(toRecord);
        }

        logger.log("Query accepted to uninstall: " + queryName);

        return sqr;
    }

    public Collection<SignedQueryRequest> getInstalledQueries() {
        logger.log("Get installed queries request");

        List<SignedQueryRequest> quries = new LinkedList<>();

        for (SignedQueryRequest sqr : installedQueries.values()) {
            quries.add(sqr);
        }

        return quries;
    }
}