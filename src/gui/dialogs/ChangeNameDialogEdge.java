package gui.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;

import action.Action;
import action.SetConfigAction;
import gui.control.Selectable;
import gui.draw.DrawableGraphEdge;
import model.Model;
import model.Transition;
import model.graph.GraphEdge;

public class ChangeNameDialogEdge extends Dialog {
	
	public enum TextFieldType {
		CHANNEL,
		CONDITION,
		ACTION;
	}
	
	private final ChangeNameDialogEdge dialog;
	private Model model;

	private DrawableGraphEdge drawableEdge;
	private JScrollPane scrollPane;
	private JPanel centerPanel,
				   bottomPanel,
				   topPanel;
	private ArrayList<Transition> transitions;
	
	public ChangeNameDialogEdge(Model model, Selectable selectable, JFrame parent) {
		this.model = model;
//		this.setSizeByScreenSize(0.4);
		dialog = this;
		this.drawableEdge = (DrawableGraphEdge) selectable;
		transitions = new ArrayList<Transition>((ArrayList<Transition>) drawableEdge.getEdge().getAttribute(GraphEdge.ATTRIBUTE_TRANSITIONS));
		init();
	}
	
	public void init(){
		this.setLayout(new BorderLayout());
		
		
		topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton addTransition = new JButton("Add Transition");
		addTransition.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				transitions.add(Transition.getDefaultTransition());
				refreshList();
			}
			
		});
		topPanel.add(addTransition);
		
		centerPanel = new JPanel(new BorderLayout());
		
		refreshList();
		
		bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
		bottomPanel.add(cancel);
		JButton save = new JButton("Save");
		save.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				GraphEdge[] edges = new GraphEdge[]{drawableEdge.getEdge()};
				String[] cmd = new String[]{GraphEdge.ATTRIBUTE_TRANSITIONS};
				Object[][] oldValues = new Object[1][1];
				Object[][] newValues = new Object[1][1];
				
				oldValues[0][0] = edges[0].getAttribute(GraphEdge.ATTRIBUTE_TRANSITIONS);
				newValues[0][0] = transitions;
				
				if(!transitionsChanged(oldValues[0][0], newValues[0][0])){
					Action a1 = new SetConfigAction(edges, cmd, oldValues);
					Action a2 = new SetConfigAction(edges, cmd, newValues);
					model.performAction(a2, a1);
				}
				dispose();
			}

			private boolean transitionsChanged(Object oldValues, Object newValues) {
				ArrayList<Transition> newTransitions = (ArrayList<Transition>) newValues, 
									  oldTransitions = (ArrayList<Transition>) oldValues;
				System.out.println("New Transitions:"+newTransitions);
				System.out.println("Old Transitions:"+oldTransitions);
				System.out.println((newTransitions.equals(oldTransitions)));
				return newTransitions.equals(oldTransitions);
			}
		});
		Object c = new ArrayList<Transition>();
		bottomPanel.add(save);
		
		scrollPane = new JScrollPane();
		scrollPane.setViewportView(centerPanel);
		add(topPanel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
		
		pack();
		this.setSize(this.getWidth(), 350);
		centerOnScreen();
	}
	
	private void refreshList(){
		centerPanel.removeAll();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5,10,5,10);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.5;
		gbc.weighty = 0.5;
		JPanel container = new JPanel(new GridBagLayout());
		for(int i = 0; i < transitions.size(); i++){
			Transition t = transitions.get(i);
			final int index = i;
			JButton delete = new JButton("Delete");
			delete.addActionListener(new ActionListener(){
				private final int transitionID = index;
				@Override
				public void actionPerformed(ActionEvent e) {
					transitions.remove(transitionID);
					refreshList();
				}
			});
			container.add(delete,gbc);
			gbc.gridx++;
			container.add(new JLabel("Gate:"), gbc);
			gbc.gridx++;
			JTextField channelText = new JTextField(t.getChannel(), 13);
			channelText.addFocusListener(new TextFocusListener(TextFieldType.CHANNEL, i));
			container.add(channelText, gbc);
			gbc.gridx++;
			container.add(new JLabel("Condition:"), gbc);
			gbc.gridx++;
			JTextField conditionText = new JTextField(t.getCondition(), 13);
			conditionText.addFocusListener(new TextFocusListener(TextFieldType.CONDITION, i));
			container.add(conditionText, gbc);
			gbc.gridx++;
			container.add(new JLabel("Action:"), gbc);
			gbc.gridx++;
			JTextField actionText = new JTextField(t.getAction(), 13);
			actionText.addFocusListener(new TextFocusListener(TextFieldType.ACTION, i));
			container.add(actionText, gbc);
			gbc.gridy++;
			gbc.gridx=0;
		}
		centerPanel.add(container, BorderLayout.NORTH);
		centerPanel.revalidate();
		centerPanel.repaint();
	}


	public class TextFocusListener implements FocusListener{
		
		private TextFieldType type;
		private int transitionID;
		
		public TextFocusListener(TextFieldType type, int transitionID){
			this.type = type;
			this.transitionID = transitionID;
		}

		@Override
		public void focusGained(FocusEvent e) {
		}

		@Override
		public void focusLost(FocusEvent e) {
			JTextField textField = (JTextField) e.getSource();
			Transition t = transitions.get(transitionID);
			switch (type) {
			case CHANNEL:
				transitions.set(transitionID, new Transition(textField.getText(), t.getCondition(), t.getAction()));
				break;
			case CONDITION:
				transitions.set(transitionID, new Transition(t.getChannel(), textField.getText(), t.getAction()));
				break;
			case ACTION:
				transitions.set(transitionID, new Transition(t.getChannel(), t.getCondition(), textField.getText()));
				break;
			default:
				System.err.println("Major failure focuslost");
				break;
			}
		}
	}
	
}
