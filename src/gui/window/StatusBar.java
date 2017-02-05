package gui.window;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

/**
 * A statusbar that is shown at the bottomn of the window. For example a message is shown, that defines how to add states.
 * @author Tobias
 *
 */
public class StatusBar extends JPanel {
	public static final String DEFAULT_MESSAGE = "";
	
	private JLabel message;
	private JLabel zoom;
	
	public StatusBar(JFrame parent) {
		setLayout(new BorderLayout());
		setBorder(new BevelBorder(BevelBorder.LOWERED));
		setPreferredSize(new Dimension(parent.getWidth(), 22));
		message = new JLabel();
		add(message, BorderLayout.WEST);
		
		zoom = new JLabel();
		setZoomFactor(1.0);
		add(zoom, BorderLayout.EAST);
	}
	
	public void setMessage(String message) {
		this.message.setText(message);
	}
	
	public void setZoomFactor(double zoom) {
		this.zoom.setText("Zoom: " + (int) (zoom * 100) + "%");
	}
}
