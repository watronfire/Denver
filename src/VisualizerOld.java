import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.util.HashMap;

public class VisualizerOld extends Frame {
    Genome genome;
    int inputNodes = 0;
    int outputNodes = 0;

    public VisualizerOld() {
        super( "Java2D Example" );

        setSize( 800, 800 );

        setVisible( true );

        addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing( WindowEvent e ) {
                dispose();
                System.exit( 0 );
            }
        } );
    }

    public void paint( Graphics g ) {
        g.setColor(Color.red);
        NeuralNet nn = genome.getPhenotype();
        HashMap<Node, int[]> nodeLocations = new HashMap<>();

        int x = 100;
        int y = 100;
        int inputsSeen = 0;
        int outputsSeen = 0;

        for( Node n : nn.getNodes() ) {
            switch ( n.getNodeType() ) {
                case INPUT:
                    inputsSeen += 1;
                    x = ( 800 ) / ( inputNodes + 1 ) * inputsSeen - 25;
                    y = 800 - 100;
                    break;
                case BIAS:
                    inputsSeen += 1;
                    x = ( 800 ) / ( inputNodes + 1 ) * inputsSeen - 25;
                    y = 800 - 100;
                    break;
                case HIDDEN:
                    g.fillOval( x, y, 50, 50 );
                    x += 100;
                    break;
                case OUTPUT:
                    outputsSeen += 1;
                    x = ( 800 ) / ( outputsSeen + 1 ) * outputsSeen - 25;
                    y = 100;
                    break;
            }

            nodeLocations.put( n, new int[]{ x, y } );
        }

        for( Node n : nodeLocations.keySet() ) {
            int[] location1 = nodeLocations.get( n );

            for( Connection c : n.getConnectionsIn() ) {
                int[] location2 = nodeLocations.get( c.inNode );
                g.setColor( Color.BLACK );
                g.drawLine( location1[0] + 25, location1[1] + 25, location2[0] + 25, location2[1] + 25 );
            }

            switch ( n.getNodeType() ) {
                case INPUT:
                    g.setColor( Color.BLUE );

                    break;
                case BIAS:
                    g.setColor( Color.BLACK );
                    break;
                case HIDDEN:
                    g.setColor( Color.YELLOW );
                    break;
                case OUTPUT:
                    g.setColor( Color.RED );
                    break;
            }
            g.fillOval( location1[0], location1[1], 50, 50 );
        }


    }

    public void setGenome( Genome genome, boolean isBest ) {
        this.genome = genome;
        for( Node node : genome.getPhenotype().getNodes() ) {
            switch ( node.getNodeType() ) {
                case INPUT:
                    inputNodes += 1;
                    break;
                case OUTPUT:
                    outputNodes += 1;
                    break;
                case BIAS:
                    inputNodes += 1;
                    break;
            }
        }
    }
}
