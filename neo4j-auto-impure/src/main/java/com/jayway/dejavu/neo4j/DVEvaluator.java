package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;

public class DVEvaluator {

    transient Evaluator evaluator;

    DVEvaluator( Evaluator evaluator ) {
        this.evaluator = evaluator;
    }

    @Impure( integrationPoint = "neo4j" )
    public Evaluation evaluate( DVPath path ) {
        return evaluator.evaluate( path.path );
    }

}
