package gui.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import action.Action;
import action.SetConfigAction;
import model.Definition;
import model.Gates;
import model.Gates.Gate;
import model.graph.GraphEdge;
import model.Model;
import model.Project;
import model.Transition;
import model.Variables;
import model.Variables.Variable;

public class SiteBar extends JPanel{
	
	private Project project;
	private GateBar gateBar;
	private VariableBar variableBar;
	private SiteBar siteBar;
	
	public SiteBar (Project project){
		siteBar = this;
		this.project = project;
		this.setLayout(new BorderLayout());
		this.init();
	}
	
	public void init(){
		JPanel container = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.insets = new Insets(5,10,5,10);
		gateBar = new GateBar();
		container.add(gateBar, gbc);
		gbc.gridy++;
		variableBar = new VariableBar();
		container.add(variableBar, gbc);
		this.add(container, BorderLayout.NORTH);
	}
	
	public void changeView(Definition newDefinition) {
		if(newDefinition instanceof Model){
			gateBar.setEnabled(true);
			variableBar.setEnabled(true);
//			gateBar.setBackground(Color.cyan);
//			variableBar.setBackground(Color.cyan);
			gateBar.initView((Model) newDefinition);
			variableBar.initView((Model) newDefinition);
		}else{
//			gateBar.setBackground(Color.green);
//			variableBar.setBackground(Color.green);
			gateBar.setEnabled(false);
			variableBar.setEnabled(false);
		}
	}

	public class GateBar extends JPanel{
		private Model model;
		private JButton addGate;
		private JPanel centerPanel, container;
		private GateBar gateBar;
		
		public GateBar(){
			gateBar = this;
//			setBackground(Color.green);
			this.setBorder(BorderFactory.createTitledBorder("Gates"));
			setMinimumSize(new Dimension(350, 300));
			setPreferredSize(new Dimension(350, 300));
			setLayout(new BorderLayout());
			addGate = new JButton("Add");
			addGate.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					gateBar.addGateAction();
				}
			});
			JPanel dummy = new JPanel(new FlowLayout(FlowLayout.LEFT));
			dummy.add(addGate);
			add(dummy, BorderLayout.NORTH);
			centerPanel = new JPanel(new BorderLayout());
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setViewportView(centerPanel);
			scrollPane.setBorder(BorderFactory.createEmptyBorder());
			add(scrollPane, BorderLayout.CENTER);
		}
		
		private void addGateAction(){
			Gates[] gates = new Gates[]{model.getGates()};
			String[] cmd = new String[]{Gates.ATTRIBUTE_GATES};
			Object[][] oldValues = new Object[1][1];
			Object[][] newValues = new Object[1][1];
			
			oldValues[0][0] = gates[0].getAttribute(Gates.ATTRIBUTE_GATES);
			ArrayList<Gate> newGates = new ArrayList<Gate>(model.getGates().getGates());
			newGates.add(Gate.getDefaultGate());
			newValues[0][0] = newGates;
			
			Action a1 = new SetConfigAction(gates, cmd, oldValues);
			Action a2 = new SetConfigAction(gates, cmd, newValues);
			model.performAction(a2, a1);
			gateBar.initView(model);
		}
		
		private void removeGateAction(Gate g){
			Gates[] gates = new Gates[]{model.getGates()};
			String[] cmd = new String[]{Gates.ATTRIBUTE_GATES};
			Object[][] oldValues = new Object[1][1];
			Object[][] newValues = new Object[1][1];
			
			oldValues[0][0] = gates[0].getAttribute(Gates.ATTRIBUTE_GATES);
			ArrayList<Gate> newGates = new ArrayList<Gate>(model.getGates().getGates());
			newGates.remove(g);
			newValues[0][0] = newGates;
			
			Action a1 = new SetConfigAction(gates, cmd, oldValues);
			Action a2 = new SetConfigAction(gates, cmd, newValues);
			model.performAction(a2, a1);
			gateBar.initView(model);
		}
		
		public void changeGateAction(int gateID, String command, String newValue){
			Gates[] gates = new Gates[]{model.getGates()};
			String[] cmd = new String[]{Gates.ATTRIBUTE_GATES};
			Object[][] oldValues = new Object[1][1];
			Object[][] newValues = new Object[1][1];
			
			oldValues[0][0] = gates[0].getAttribute(Gates.ATTRIBUTE_GATES);
			ArrayList<Gate> newGates = new ArrayList<Gate>(model.getGates().getGates());
			Gate g = newGates.get(gateID);
			g.setAttribute(command, newValue);
			newGates.set(gateID, g);
			newValues[0][0] = newGates;
			
			Action a1 = new SetConfigAction(gates, cmd, oldValues);
			Action a2 = new SetConfigAction(gates, cmd, newValues);
			model.performAction(a2, a1);
			gateBar.initView(model);
		}
		
		private void refreshList(ArrayList<Gate> gates){
			centerPanel.removeAll();
			container = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.weightx = 0.5;
//			gbc.weighty = 1.0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.FIRST_LINE_START;
			gbc.insets = new Insets(1,1,1,1);
			container.add(new JLabel("Name:"), gbc);
			gbc.gridx++;
			container.add(new JLabel("Type:"), gbc);
			gbc.gridx = 0;
			gbc.gridy++;
			int id = 0;
			for(Gate g : gates){
				JButton delete = new JButton("");
				delete.setIcon(new ImageIcon(Window.class.getResource("/icons/delete.png")));
				delete.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						gateBar.removeGateAction(g);
					}
				});
				container.add(delete, gbc);
				gbc.gridx++;
				JTextField nameText = new JTextField(g.getName(), 10);
				nameText.addFocusListener(new GateBarFocusListener(gateBar, id, Gates.ATTRIBUTE_NAME));
				container.add(nameText,gbc);
				gbc.gridx++;
				JTextField typeText = new JTextField(g.getType(), 10);
				typeText.addFocusListener(new GateBarFocusListener(gateBar, id, Gates.ATTRIBUTE_TYPE));
				container.add(typeText,gbc);
				gbc.gridy++;
				gbc.gridx = 0;
				id++;
			}
			centerPanel.add(container, BorderLayout.NORTH);
			centerPanel.revalidate();
			centerPanel.repaint();
		}
		
		public void initView(Model model){
			this.model = model;
			ArrayList<Gate> gates = (ArrayList<Gate>) model.getGates().getGates();
			refreshList(gates);
		}
		
		@Override
		public void setEnabled(boolean b){
			System.out.println("setEnabled");
			centerPanel.setEnabled(b);
			if(container != null){
				for(Component c : container.getComponents()){
					c.setEnabled(b);
				}
			}
		}
		
	}
	
	public class VariableBar extends JPanel{
		private Model model;
		private VariableBar variableBar;
		private JButton addVariable;
		private JPanel centerPanel, container;
		
		public VariableBar(){
			variableBar = this;
			this.setBorder(BorderFactory.createTitledBorder("Variables"));
			setMinimumSize(new Dimension(350, 300));
			setPreferredSize(new Dimension(350, 300));
			setLayout(new BorderLayout());
			addVariable = new JButton("Add");
			addVariable.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					variableBar.addVariableAction();
				}
			});
			JPanel dummy = new JPanel(new FlowLayout(FlowLayout.LEFT));
			dummy.add(addVariable);
			add(dummy, BorderLayout.NORTH);
			centerPanel = new JPanel(new BorderLayout());
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setViewportView(centerPanel);
			scrollPane.setBorder(BorderFactory.createEmptyBorder());
			add(scrollPane, BorderLayout.CENTER);
		}

		private void addVariableAction(){
			Variables[] variables = new Variables[]{model.getVariables()};
			String[] cmd = new String[]{Variables.ATTRIBUTE_VARIABLES};
			Object[][] oldValues = new Object[1][1];
			Object[][] newValues = new Object[1][1];
			
			oldValues[0][0] = variables[0].getAttribute(Variables.ATTRIBUTE_VARIABLES);
			ArrayList<Variable> newVariables = new ArrayList<Variable>(model.getVariables().getVariables());
			newVariables.add(Variable.getDefaultVariable());
			newValues[0][0] = newVariables;
			
			Action a1 = new SetConfigAction(variables, cmd, oldValues);
			Action a2 = new SetConfigAction(variables, cmd, newValues);
			model.performAction(a2, a1);
			variableBar.initView(model);
		}
		
		private void removeVariableAction(Variable v){
			Variables[] variables = new Variables[]{model.getVariables()};
			String[] cmd = new String[]{Variables.ATTRIBUTE_VARIABLES};
			Object[][] oldValues = new Object[1][1];
			Object[][] newValues = new Object[1][1];
			
			oldValues[0][0] = variables[0].getAttribute(Variables.ATTRIBUTE_VARIABLES);
			ArrayList<Variable> newVariables = new ArrayList<Variable>(model.getVariables().getVariables());
			newVariables.remove(v);
			newValues[0][0] = newVariables;
			
			Action a1 = new SetConfigAction(variables, cmd, oldValues);
			Action a2 = new SetConfigAction(variables, cmd, newValues);
			model.performAction(a2, a1);
			variableBar.initView(model);
		}
		
		public void changeVariableAction(int variableID, String command, String newValue){
			Variables[] variables = new Variables[]{model.getVariables()};
			String[] cmd = new String[]{Variables.ATTRIBUTE_VARIABLES};
			Object[][] oldValues = new Object[1][1];
			Object[][] newValues = new Object[1][1];
			
			oldValues[0][0] = variables[0].getAttribute(Variables.ATTRIBUTE_VARIABLES);
			ArrayList<Variable> newVariables = new ArrayList<Variable>(model.getVariables().getVariables());
			Variable v = newVariables.get(variableID);
			v.setAttribute(command, newValue);
			newVariables.set(variableID, v);
			newValues[0][0] = newVariables;
			
			Action a1 = new SetConfigAction(variables, cmd, oldValues);
			Action a2 = new SetConfigAction(variables, cmd, newValues);
			model.performAction(a2, a1);
			variableBar.initView(model);
		}
		
		private void refreshList(ArrayList<Variable> variables){
			centerPanel.removeAll();
			container = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.weightx = 0.5;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.FIRST_LINE_START;
			gbc.insets = new Insets(1,1,1,1);
			container.add(new JLabel("Name:"), gbc);
			gbc.gridx++;
			container.add(new JLabel("Type:"), gbc);
			gbc.gridx++;
			container.add(new JLabel("Init value:"), gbc);
			gbc.gridx = 0;
			gbc.gridy++;
			int id = 0;
			for(Variable v : variables){
				JButton delete = new JButton("");
				delete.setIcon(new ImageIcon(Window.class.getResource("/icons/delete.png")));
				delete.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						variableBar.removeVariableAction(v);
					}
				});
				container.add(delete, gbc);
				gbc.gridx++;
				JTextField nameText = new JTextField(v.getName(), 5);
				nameText.addFocusListener(new VariableBarFocusListener(variableBar, id, Variables.ATTRIBUTE_NAME));
				container.add(nameText,gbc);
				gbc.gridx++;
				JTextField typeText = new JTextField(v.getType(), 5);
				typeText.addFocusListener(new VariableBarFocusListener(variableBar, id, Variables.ATTRIBUTE_TYPE));
				container.add(typeText,gbc);
				gbc.gridx++;
				JTextField initText = new JTextField(v.getInitValue(), 5);
				initText.addFocusListener(new VariableBarFocusListener(variableBar, id, Variables.ATTRIBUTE_INIT));
				container.add(initText,gbc);
				gbc.gridy++;
				gbc.gridx = 0;
				id++;
			}
			centerPanel.add(container, BorderLayout.NORTH);
			centerPanel.revalidate();
			centerPanel.repaint();
		}

		public void initView(Model newDefinition) {
			this.model = newDefinition;
			ArrayList<Variable> variables = (ArrayList<Variable>) model.getVariables().getVariables();
			refreshList(variables);
		}
		
		@Override
		public void setEnabled(boolean b){
//			System.out.println("setEnabled");
			centerPanel.setEnabled(b);
			if(container != null){
				for(Component c : container.getComponents()){
					c.setEnabled(b);
				}
			}
		}
		
	}
	
	public class GateBarFocusListener implements FocusListener{
		
		private int id;
		private String command;
		private GateBar gateBar;
		
		public GateBarFocusListener(GateBar gateBar, int id, String command){
			this.id = id;
			this.command = command;
			this.gateBar = gateBar;
		}

		@Override
		public void focusGained(FocusEvent e) {
		}

		@Override
		public void focusLost(FocusEvent e) {
			JTextField textField = (JTextField) e.getSource();
			gateBar.changeGateAction(id, command, textField.getText());
		}
	}
	
	public class VariableBarFocusListener implements FocusListener{
		
		private int id;
		private String command;
		private VariableBar variableBar;
		
		public VariableBarFocusListener(VariableBar variableBar, int id, String command){
			this.id = id;
			this.command = command;
			this.variableBar = variableBar;
		}

		@Override
		public void focusGained(FocusEvent e) {
		}

		@Override
		public void focusLost(FocusEvent e) {
			JTextField textField = (JTextField) e.getSource();
			variableBar.changeVariableAction(id, command, textField.getText());
		}
	}
	

}

