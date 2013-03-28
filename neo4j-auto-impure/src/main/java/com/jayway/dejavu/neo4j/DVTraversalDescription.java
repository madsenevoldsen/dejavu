package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.traversal.TraversalDescription;

import java.util.Comparator;

public class DVTraversalDescription {
    private transient TraversalDescription traversalDescription;

    DVTraversalDescription( TraversalDescription traversalDescription ) {
        this.traversalDescription = traversalDescription;
    }

    @Impure( integrationPoint = "neo4j" )
    public DVTraversalDescription breadthFirst() {
        traversalDescription = traversalDescription.breadthFirst();
        return new DVTraversalDescription( traversalDescription );
    }

    @Impure( integrationPoint = "neo4j" )
    public DVTraversalDescription depthFirst() {
        traversalDescription = traversalDescription.depthFirst();
        return new DVTraversalDescription( traversalDescription );
    }

    @Impure( integrationPoint = "neo4j" )
    public DVTraversalDescription evaluator( DVEvaluator evaluator ) {
        traversalDescription = traversalDescription.evaluator( evaluator.evaluator );
        return new DVTraversalDescription( traversalDescription );
    }


    @Impure( integrationPoint = "neo4j" )
    public DVTraversalDescription evaluator( DVPathEvaluator evaluator ) {
        traversalDescription = traversalDescription.evaluator( evaluator.evaluator );
        return new DVTraversalDescription( traversalDescription );
    }

    @Impure( integrationPoint = "neo4j" )
    public DVTraversalDescription expand(DVPathExpander<?> expander ) {
        traversalDescription = traversalDescription.expand( expander.pathExpander );
        return new DVTraversalDescription( traversalDescription );
    }

    @Impure( integrationPoint = "neo4j" )
    public <STATE> DVTraversalDescription expand( DVPathExpander<STATE> expander, DVInitialBranchState<STATE> initialState ) {
        traversalDescription = traversalDescription.expand( expander.pathExpander, initialState.initialBranchState );
        return new DVTraversalDescription( traversalDescription );
    }

    @Impure( integrationPoint = "neo4j" )
    public DVTraversalDescription order( DVBranchOrderingPolicy branchOrderingPolicy ) {
        traversalDescription = traversalDescription.order(branchOrderingPolicy.branchOrderingPolicy);
        return new DVTraversalDescription( traversalDescription );
    }

    @Impure( integrationPoint = "neo4j" )
    public DVTraversalDescription relationships( RelationshipType type ) {
        traversalDescription = traversalDescription.relationships( type );
        return new DVTraversalDescription( traversalDescription );
    }

    @Impure( integrationPoint = "neo4j" )
    public DVTraversalDescription relationships(RelationshipType type, Direction direction) {
        traversalDescription = traversalDescription.relationships( type, direction );
        return new DVTraversalDescription( traversalDescription );
    }

    @Impure( integrationPoint = "neo4j" )
    public DVTraversalDescription reverse() {
        traversalDescription = traversalDescription.reverse();
        return new DVTraversalDescription( traversalDescription );
    }

    // Is this ok???
    @Impure( integrationPoint = "neo4j" )
    public DVTraversalDescription sort( Comparator<? super Path> comparator ) {
        traversalDescription = traversalDescription.sort( comparator );
        return new DVTraversalDescription( traversalDescription );
    }

    @Impure( integrationPoint = "neo4j" )
    public DVTraverser traverse( DVNode... startNodes ) {
        Node[] nodes = new Node[ startNodes.length ];
        for (int i=0; i<startNodes.length; i++ ) {
            nodes[i] = startNodes[i].node;
        }
        return new DVTraverser( traversalDescription.traverse( nodes ) );
    }

    @Impure( integrationPoint = "neo4j" )
    public DVTraverser traverse( DVNode node ) {
        return new DVTraverser( traversalDescription.traverse( node.node ));
    }

    @Impure( integrationPoint = "neo4j" )
    public DVTraversalDescription uniqueness( DVUniquenessFactory uniqueness ) {
        traversalDescription = traversalDescription.uniqueness( uniqueness.uniquenessFactory );
        return new DVTraversalDescription( traversalDescription );
    }

    @Impure( integrationPoint = "neo4j" )
    public DVTraversalDescription uniqueness( DVUniquenessFactory uniqueness, Object parameter ) {
        traversalDescription = traversalDescription.uniqueness( uniqueness.uniquenessFactory, parameter );
        return new DVTraversalDescription( traversalDescription );
    }

}
