import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

public class Visualizer extends Frame {
    Genome genome;

    public Visualizer() {
        super( "Java2D Example" );

        setSize( 800, 400 );

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
        g.drawRect(50,50,200,200);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor(Color.blue);
        g2d.drawRect(75,75,300,200);
        //Now, let's draw another rectangle, but this time, let's
        //use a GeneralPath to specify it segment by segment.
        //Furthermore, we're going to translate and rotate this
        //rectangle relative to the Device Space (and thus, to
        //the first two quadrilaterals) using an AffineTransform.
        //We also will change its color.
        GeneralPath path = new GeneralPath( GeneralPath.WIND_EVEN_ODD );
        path.moveTo(0.0f,0.0f);
        path.lineTo(0.0f,125.0f);
        path.lineTo(225.0f,125.0f);
        path.lineTo(225.0f,0.0f);
        path.closePath();
        AffineTransform at = new AffineTransform();
        at.setToRotation(-Math.PI/8.0);
        g2d.transform(at);
        at.setToTranslation(50.0f,200.0f);
        g2d.transform(at);
        g2d.setColor(Color.green);
        g2d.fill(path);
    }

    public void setGenome( Genome genome, boolean isBest ) {
        this.genome = genome;
    }
}
