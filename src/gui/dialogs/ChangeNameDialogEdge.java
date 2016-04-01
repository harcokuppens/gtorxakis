package gui.dialogs;

import javax.swing.JFrame;

import action.Action;
import action.SetConfigAction;
import gui.control.Selectable;
import gui.draw.DrawableGraphEdge;
import model.Model;
import model.graph.GraphEdge;

public class ChangeNameDialogEdge extends ChangeNameDialog{

	private DrawableGraphEdge dge;
	
	public ChangeNameDialogEdge(Model model, Selectable selectable, JFrame parent) {
		super(model, selectable, parent, ChangeNameDialog.EDGE);
		this.dge = (DrawableGraphEdge) selectable;
		this.init((String) dge.getEdge().getAttribute(GraphEdge.ATTRIBUTE_NAME));
	}

	@Override
	public void success() {
		GraphEdge[] objects = new GraphEdge[] {dge.getEdge()};
		String[] cmd = new String[]{GraphEdge.ATTRIBUTE_NAME};
		Object[][] oldValues = new String[1][objects.length];
		for(int i = 0; i < objects.length; i++) {
			oldValues[0][i] = objects[i].getAttribute(GraphEdge.ATTRIBUTE_NAME);
		}
		Object[][] newValues = new String[1][objects.length];
		for(int i = 0; i < objects.length; i++) {
			newValues[0][i] = nameTXT.getText();
		}
		Action a1 = new SetConfigAction(objects, cmd, oldValues);
		Action a2 = new SetConfigAction(objects, cmd, newValues);
		ChangeNameDialogEdge.this.model.performAction(a2, a1);
		dispose();
	}
	
}
