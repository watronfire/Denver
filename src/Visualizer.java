import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.GraphParseException;

import java.io.IOException;
import java.util.HashMap;

// Class will deal with creating and visualizing a graph of a neural net.

public class Visualizer {

    private int minimumSize = 20;
    private int maximumSize = 35;
    private Graph graph;

    public Visualizer() {
        System.setProperty( "org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer" );
        graph = new SingleGraph( "Neural Net" );
        graph.setStrict( false );
        graph.setAutoCreate( true );
        graph.addAttribute( "ui.quality" );
        graph.addAttribute( "ui.antialias" );
        graph.addAttribute( "ui.stylesheet", "url('file:///home/nate/IdeaProjects/Denver/src/graphStuff.css')" );
    }

    public void Display( String netFile ) {
        try {
            graph.read( netFile );
        } catch ( IOException | GraphParseException e ) {
            e.printStackTrace();
        }
        graph.display();
        setNodeSize( minimumSize, maximumSize, graph );
    }

    private void setNodeSize( int minimumSize, int maximumSize, Graph graph) {
        int smaller = -1;
        int greater = -1;
        for( org.graphstream.graph.Node n : graph.getEachNode() ) {
            if( n.getDegree() > greater || smaller == -1 )
                greater = n.getDegree();
            if( n.getDegree() < smaller || greater == -1 )
                smaller = n.getDegree();
        }
        for ( org.graphstream.graph.Node n : graph.getEachNode() ) {
            double scale = (double)( n.getDegree()  - smaller ) / (double)( greater - smaller );
            if( null != n.getAttribute( "ui.style" ) ) {
                n.setAttribute( "ui.style", n.getAttribute( "ui.style" ) + " size:" + Math.round( ( scale * maximumSize ) + minimumSize ) + "px;" );
            } else {
                n.addAttribute("ui.style", " size:"+ Math.round( ( scale * maximumSize ) + minimumSize ) + "px;" );
            }
        }

    }

    // Refactor so that this is the modification method.
    // TODO: color edges based on weight. Also adjust stroke based on weight.
    public void setNodeColors( HashMap<Integer, String> typeArray ) {
        int outputsSeen = 0;
        int inputsSeen = 0;


        for( Integer i : typeArray.keySet() ) {
            graph.getNode( String.valueOf( i ) ).setAttribute( "ui.class", typeArray.get( i ) );
            if( typeArray.get( i ).equals( "INPUT" ) || typeArray.get( i ).equals( "BIAS" ) ) {
                graph.getNode( String.valueOf( i ) ).addAttribute( "layout.frozen" );
                graph.getNode( String.valueOf( i ) ).addAttribute( "xy", inputsSeen, 0 );
                inputsSeen += 1;
            }
            if( typeArray.get( i ).equals( "OUTPUT" ) ) {
                graph.getNode( String.valueOf( i ) ).addAttribute( "layout.frozen" );
                graph.getNode( String.valueOf( i ) ).addAttribute( "xy", outputsSeen + 1, 2 );
                outputsSeen += 1;
            }
        }
    }

}
