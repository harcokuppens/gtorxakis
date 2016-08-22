package gui.dialogs;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import model.TextualDefinition;
import core.Session;

public class RunDialog extends Dialog{
	
	public RunDialog(){
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
		
		JLabel specLabel = new JLabel("Port");
		panel.add(specLabel, gbc);
		gbc.gridx++;
		gbc.weightx = 0.6;
		
		
		JSpinner portNumber = new JSpinner(new SpinnerNumberModel(7220, 0, 250000, 1));
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
		
		JLabel specLabel = new JLabel("Specification");
		panel.add(specLabel, gbc);
		gbc.gridx++;
		gbc.weightx = 0.6;
		
		JComboBox<String> specDefinitions = new JComboBox<String>();
		for(String s : Session.getSession().getProject().getDefinitionsByTypeDef(TextualDefinition.DefType.SPEC)){
			specDefinitions.addItem(s);
		}
		panel.add(specDefinitions, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.weightx = 0.4;
		
		JLabel adapLabel = new JLabel("Adapter");
		panel.add(adapLabel, gbc);
		gbc.gridx++;
		gbc.weightx = 0.6;
		
		JComboBox<String> adapDefinitions = new JComboBox<String>();
		for(String s : Session.getSession().getProject().getDefinitionsByTypeDef(TextualDefinition.DefType.ADAP)){
			adapDefinitions.addItem(s);
		}
		panel.add(adapDefinitions, gbc);
		
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.weightx = 0.4;
		
		JLabel sutLabel = new JLabel("System Under Test");
		panel.add(sutLabel, gbc);
		gbc.gridx++;
		gbc.weightx = 0.6;
		
		JComboBox<String> sutDefinitions = new JComboBox<String>();
		for(String s : Session.getSession().getProject().getDefinitionsByTypeDef(TextualDefinition.DefType.SUT)){
			sutDefinitions.addItem(s);
		}
		panel.add(sutDefinitions, gbc);
		
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
		JButton cancel = new JButton("Cancel");
		panel.add(cancel);
		panel.add(save);
		return panel;
	}
}
