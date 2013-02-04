package com.jayway.dejavu.neo4j;

import com.jayway.dejavu.core.annotation.Impure;
import org.neo4j.graphdb.traversal.TraversalDescription;

public class DVTraversalDescription {
    private TraversalDescription traversalDescription;

    DVTraversalDescription( TraversalDescription traversalDescription ) {
        this.traversalDescription = traversalDescription;
    }

    @Impure
    public DVTraversalDescription breadthFirst() {
        traversalDescription = traversalDescription.breadthFirst();
        return new DVTraversalDescription( traversalDescription );
    }

    @Impure
    public DVTraversalDescription depthFirst() {
        traversalDescription = traversalDescription.depthFirst();
        return new DVTraversalDescription( traversalDescription );
    }

    @Impure
    public DVTraversalDescription evaluator( DVEvaluator evaluator ) {
        traversalDescription = traversalDescription.evaluator( evaluator.evaluator );
        return new DVTraversalDescription( traversalDescription );
    }



    /*
 TraversalDescription	evaluator(PathEvaluator evaluator)
          Adds evaluator to the list of evaluators which will control the behaviour of the traversal.
 TraversalDescription	expand(PathExpander<?> expander)
          Sets the PathExpander as the expander of relationships, discarding all previous calls to relationships(RelationshipType) and relationships(RelationshipType, Direction) or any other expand method.
<STATE> TraversalDescription
expand(PathExpander<STATE> expander, InitialBranchState<STATE> initialState)
          Sets the PathExpander as the expander of relationships, discarding all previous calls to relationships(RelationshipType) and relationships(RelationshipType, Direction) or any other expand method.
<STATE> TraversalDescription
expand(PathExpander<STATE> expander, InitialStateFactory<STATE> initialState)
          Sets the PathExpander as the expander of relationships, discarding all previous calls to relationships(RelationshipType) and relationships(RelationshipType, Direction) or any other expand method.
 TraversalDescription	expand(RelationshipExpander expander)
          Sets the RelationshipExpander as the expander of relationships, discarding all previous calls to relationships(RelationshipType) and relationships(RelationshipType, Direction) or any other expand method.
 TraversalDescription	order(BranchOrderingPolicy selector)
          Sets the BranchOrderingPolicy to use.
 TraversalDescription	relationships(RelationshipType type)
          Adds type to the list of relationship types to traverse.
 TraversalDescription	relationships(RelationshipType type, Direction direction)
          Adds type to the list of relationship types to traverse in the given direction.
 TraversalDescription	reverse()
          Creates an identical TraversalDescription, although reversed in how it traverses the graph.
 TraversalDescription	sort(Comparator<? super Path> comparator)

 Traverser	traverse(Node... startNodes)
          Traverse from a set of start nodes based on all the rules and behavior in this description.
 Traverser	traverse(Node startNode)
          Traverse from a single start node based on all the rules and behavior in this description.
 TraversalDescription	uniqueness(UniquenessFactory uniqueness)
          Sets the UniquenessFactory for creating the UniquenessFilter to use.
 TraversalDescription	uniqueness(UniquenessFactory uniqueness, Object parameter)
          Sets the UniquenessFactory for creating the UniquenessFilter to use.

     */

}
