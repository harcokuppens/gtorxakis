package gui.dialogs;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import model.TextualDefinition;
import core.Session;
import io.net.SocketIO;

public class RunDialog extends Dialog{
	
	private RunDialog runDialog;
	private JSpinner portNumber;
	private JTextField programField;
	private JComboBox<String> connectDefinitions,
							  modelDefinitions;
	
	public RunDialog(){
		runDialog = this;
		init();
	}

	public void init(){
		setTitle("Run project with TorXakis");
		setSizeByScreenSize(0.3, 0.5);
		centerOnScreen();
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		this.setModal(true);
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.4;
		gbc.weighty = 0.05;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.insets = new Insets(5,5,5,5);
		
		add(this.getConConfigPanel(), gbc);
		gbc.gridy++;
		
		add(this.getProjectConfigPanel(), gbc);
		gbc.gridy++;
		
		add(this.getRunConfigPanel(), gbc);
		gbc.gridy++;

		add(this.getButtonPanel(), gbc);
		gbc.gridy++;
		
		gbc.gridy++;
		gbc.gridx = 0;
		this.pack();
		this.setMinimumSize(new Dimension(300,200));
		this.setPreferredSize(new Dimension(300,200));
		this.setResizable(false);
		this.centerOnScreen();
	}
	
	public JPanel getConConfigPanel(){
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		panel.setBorder(BorderFactory.createTitledBorder("Connection configurations"));
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.4;
		gbc.weighty = 0.05;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.insets = new Insets(5,5,5,5);
				
		JLabel programLabel = new JLabel("Host");
		panel.add(programLabel, gbc);
		gbc.gridx++;
		gbc.weightx = 0.6;
		
		programField = new JTextField("localhost");
		programField.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(programField, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.weightx = 0.4;
		
		JLabel portLabel = new JLabel("Port");
		panel.add(portLabel, gbc);
		gbc.gridx++;
		gbc.weightx = 0.6;
		
		
		portNumber = new JSpinner(new SpinnerNumberModel(7220, 0, 250000, 1));
		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(portNumber, "#"); 
		portNumber.setEditor(editor);
		panel.add(portNumber, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.weightx = 0.4;
		return panel;
	}
	
	public JPanel getProjectConfigPanel(){
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		panel.setBorder(BorderFactory.createTitledBorder("Project configurations"));
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.4;
		gbc.weighty = 0.05;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.insets = new Insets(5,5,5,5);
		
		JLabel specLabel = new JLabel("Model");
		panel.add(specLabel, gbc);
		gbc.gridx++;
		gbc.weightx = 0.6;
		
		modelDefinitions = new JComboBox<String>();
		System.out.println(Session.getSession().getProject().getName());
		for(String s : Session.getSession().getProject().getDefinitionsByTypeDef(TextualDefinition.DefType.MODEL)){
			modelDefinitions.addItem(s);
		}
		panel.add(modelDefinitions, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.weightx = 0.4;
		
//		JLabel adapLabel = new JLabel("Adapter");
//		panel.add(adapLabel, gbc);
//		gbc.gridx++;
//		gbc.weightx = 0.6;
//		
//		adapDefinitions = new JComboBox<String>();
//		for(String s : Session.getSession().getProject().getDefinitionsByTypeDef(TextualDefinition.DefType.ADAP)){
//			adapDefinitions.addItem(s);
//		}
//		panel.add(adapDefinitions, gbc);
//		
//		gbc.gridy++;
//		gbc.gridx = 0;
//		gbc.weightx = 0.4;
		
		JLabel connectLabel = new JLabel("Connect definition");
		panel.add(connectLabel, gbc);
		gbc.gridx++;
		gbc.weightx = 0.6;
		
		connectDefinitions = new JComboBox<String>();
		for(String s : Session.getSession().getProject().getDefinitionsByTypeDef(TextualDefinition.DefType.CNECTDEF)){
			connectDefinitions.addItem(s);
		}
		panel.add(connectDefinitions, gbc);
		
		return panel;
	}
	
	public JPanel getRunConfigPanel(){
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		panel.setBorder(BorderFactory.createTitledBorder("Run configurations"));
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.4;
		gbc.weighty = 0.05;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.insets = new Insets(5,5,5,5);
		
		JLabel specLabel = new JLabel("Number of steps");
		panel.add(specLabel, gbc);
		gbc.gridx++;
		gbc.weightx = 0.6;
		
		
		JSpinner testNumber = new JSpinner(new SpinnerNumberModel(1000, 0, null, 1));
		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(testNumber, "#"); 
		testNumber.setEditor(editor);
		panel.add(testNumber, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.weightx = 0.4;
		return panel;
	}
	
	public JPanel getButtonPanel(){
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton save = new JButton("Run");
		save.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				//TODO connect to torxakis
				int port = (int) portNumber.getValue();
				String host = programField.getText();
				SocketIO socketIO = new SocketIO(port, host);
				socketIO.startTorXakis("filename");
			}
		});
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				runDialog.dispose();
			}
		});
		panel.add(cancel);
		panel.add(save);
		return panel;
	}
}
