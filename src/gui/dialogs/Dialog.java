package gui.dialogs;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.GridBagConstraints;
import java.awt.Container;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;

import javax.swing.JComponent;
import javax.swing.JLabel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JTextField;
import java.awt.Cursor;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public abstract class Dialog extends JDialog {
	public Dialog(JFrame parent) {
		super(parent);
	}
	
	public Dialog(JDialog parent) {
		super(parent);
	}

	public Dialog() {
		
	}
	
	protected void setSizeByScreenSize(double scale) {
		setSizeByScreenSize(scale, scale);
	}
	
	protected void setSizeByScreenSize(double scaleWidth, double scaleHeight) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int w = (int) (scaleWidth * screenSize.width), h = (int) (scaleHeight * screenSize.height);
		setSize(w, h);		
	}
	
	protected void centerOnScreen() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Point screenCenter = new Point(screenSize.width / 2, screenSize.height / 2);
		setLocation(screenCenter.x - getSize().width / 2, screenCenter.y - getSize().height / 2);
	}

	protected static JComponent getLabel(String string) {
		return getLabel(new String[] {string})[0];
	}

	protected static JComponent[] getLabel(String... strings) {
		JLabel[] labels = new JLabel[strings.length];
		for(int i = 0; i < strings.length; i++) {
			labels[i] = new JLabel(strings[i]);
		}
		return labels;
	}

	protected static JTextField getPassiveField(String string) {
		JTextField field = new JTextField(string);
		field.setBorder(null);
		field.setOpaque(false);
		field.setEditable(false);
		return field;
	}

	/**
	 * Adds a row of components to the given container.
	 * @param container     The container that the components will be added to.
	 * @param gbc   		The GridBagConstraints to be used.
	 * @param components 	The components to be added.
	 */
	protected static void addRow(Container container, GridBagConstraints gbc, JComponent... components) {
		if(components.length == 0) {
			return;
		}
		for(int c = 0; c < components.length; c++) {
			if(c == components.length - 1) {
				gbc.gridwidth = GridBagConstraints.REMAINDER;
			}
			container.add(components[c], gbc);
			gbc.gridx++;
		}
			
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy++;
	}

	protected class Hyperlink extends JLabel{
		
		public Hyperlink(String text, final String url){
			super("<html><a href=\"\">"+text+"</a></html>");
			this.setCursor(new Cursor(Cursor.HAND_CURSOR));
			this.setToolTipText("Click to open \""+url+"\" in your browser");
			this.addMouseListener(new MouseListener() {
				@Override
				public void mouseReleased(MouseEvent arg0) {
				}
				@Override
				public void mousePressed(MouseEvent arg0) {
				}
				@Override
				public void mouseExited(MouseEvent arg0) {
				}
				@Override
				public void mouseEntered(MouseEvent arg0) {
				}
				@Override
				public void mouseClicked(MouseEvent arg0) {
					try {
                        Desktop.getDesktop().browse(new URI(url));
					} catch (URISyntaxException | IOException ex) {
                        //It looks like there's a problem
					}
				}
			});
		}
	}

}
