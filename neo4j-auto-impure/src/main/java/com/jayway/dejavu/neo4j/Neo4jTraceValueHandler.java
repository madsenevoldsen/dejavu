package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.RunningTrace;
import com.jayway.dejavu.core.TraceValueHandler;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.helpers.collection.PagingIterator;

import static org.easymock.EasyMock.createMock;

public class Neo4jTraceValueHandler implements TraceValueHandler {

    public static void initialize() {
        RunningTrace.addTraceHandler(new Neo4jTraceValueHandler());
    }


    @Override
    public Object record(Object value) {
        if ( value instanceof GraphDatabaseService) {
            return Neo4jPure.Neo4jGraphDatabaseService;
        } else if ( value instanceof Index) {
            return Neo4jPure.Neo4jIndex;
        } else if ( value instanceof IndexHits) {
            return Neo4jPure.Neo4jIndexHits;
        } else if ( value instanceof IndexManager) {
            return Neo4jPure.Neo4jIndexManager;
        } else if ( value instanceof Node) {
            return Neo4jPure.Neo4jNode;
        } else if (value instanceof PagingIterator) {
            return Neo4jPure.Neo4jPagingIterator;
        } else if ( value instanceof Transaction) {
            return Neo4jPure.Neo4jTransaction;
        }

        return value;
    }

    @Override
    public Object replay(Object value) {
        if ( value instanceof Neo4jPure) {
            Neo4jPure pure = (Neo4jPure) value;
            switch (pure) {
                case Neo4jGraphDatabaseService:
                    return createMock(GraphDatabaseService.class);
                case Neo4jIndex:
                    return createMock(Index.class);
                case Neo4jIndexHits:
                    return createMock(IndexHits.class);
                case Neo4jIndexManager:
                    return createMock(IndexManager.class);
                case Neo4jTransaction:
                    return createMock(Transaction.class);
                case Neo4jPagingIterator:
                    return createMock(PagingIterator.class);
                case Neo4jNode:
                    return createMock(Node.class);
            }
        }

        return value;
    }
}
