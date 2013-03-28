package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.PathEvaluator;

public class DVPathEvaluator extends DVEvaluator {

    transient PathEvaluator evaluator;

    DVPathEvaluator(PathEvaluator evaluator) {
        super( evaluator );
        this.evaluator = evaluator;
    }

    @Impure( integrationPoint = "neo4j" )
    public Evaluation evaluate( DVPath path ) {
        return evaluator.evaluate( path.path );
    }

}
