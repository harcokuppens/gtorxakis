package gui.dialogs;

import javax.swing.JFrame;

import action.Action;
import action.SetConfigAction;
import gui.control.Selectable;
import gui.draw.DrawableGraphState;
import model.Model;
import model.graph.GraphState;

public class ChangeNameDialogState extends ChangeNameDialog{

	private DrawableGraphState dgn;
	
	public ChangeNameDialogState(Model model, Selectable selectable, JFrame parent) {
		super(model, selectable, parent, ChangeNameDialog.NODE);
		this.dgn = (DrawableGraphState) selectable;
		this.init(dgn.getState().getName());
	}

	@Override
	public void success() {
		GraphState[] objects = new GraphState[] {dgn.getState()};
		String[] cmd = new String[]{GraphState.ATTRIBUTE_NAME};
		Object[][] oldValues = new String[1][objects.length];
		for(int i = 0; i < objects.length; i++) {
			oldValues[0][i] = objects[i].getAttribute(GraphState.ATTRIBUTE_NAME);
		}
		Object[][] newValues = new String[1][objects.length];
		for(int i = 0; i < objects.length; i++) {
			newValues[0][i] = nameTXT.getText();
		}
		Action a1 = new SetConfigAction(objects, cmd, oldValues);
		Action a2 = new SetConfigAction(objects, cmd, newValues);
		ChangeNameDialogState.this.model.performAction(a2, a1);
		dispose();
	}
}
