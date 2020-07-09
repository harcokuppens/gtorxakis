package action;

import gui.draw.DrawableGraphState;
import gui.draw.GraphInterface;
import gui.control.DrawController;

public class SetConfigAction extends Action {
	public final Configurable[] objects;
	public final String[] attributes;
	public final Object[][] values;
	
	
	public SetConfigAction(Configurable[] objects, String[] attributes, Object[][] values) {
		super(true);
		this.objects = objects;
		this.attributes = attributes;
		this.values = values;
	}
	
	@Override
	public boolean needsConfirm(){
		boolean needsConfirmSetConfig = false;
		for(String attribute : attributes){
			if(!attribute.equals(DrawableGraphState.ATTRIBUTE_VIEW_INDICATORS)){
				needsConfirmSetConfig = true;
			}
		}
		return needsConfirmSetConfig;
	}
	
	@Override
	public void run(DrawController dc) {
		GraphInterface gi = dc.getGraphInterface();
		for(int c = 0; c < objects.length; c++) {
			for(int i = 0; i < attributes.length; i++){
				objects[c].setAttribute(attributes[i], values[i][c]);
				objects[c].updateConfigs(dc);
			}
		}
		gi.propertiesChanged();
		gi.notifyObservers();
	}
}
