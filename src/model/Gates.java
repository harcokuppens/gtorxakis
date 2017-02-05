package model;

import java.util.ArrayList;

import action.Configurable;
import gui.control.DrawController;

/**
 * Gates that will be specified in TorXakis as channel.
 * @author Tobias
 *
 */
public class Gates implements Configurable {

	private ArrayList<Gate> gates;
	
	public static final String ATTRIBUTE_GATES = "gates",
							   ATTRIBUTE_NAME = "name",
							   ATTRIBUTE_TYPE = "type";
	
	public Gates(){
		this(new ArrayList<Gate>());
	}

	public Gates(ArrayList<Gate> gates) {
		this.gates = gates;
	}
	
	public ArrayList<Gate> getGates(){
		return gates;
	}
	
	public void addGate(Gate g){
		gates.add(g);
	}
	
	@Override
	public void setAttribute(String cmd, Object value) {
		switch(cmd){
		case ATTRIBUTE_GATES:
			gates = new ArrayList<Gate>((ArrayList<Gate>) value);
			break;
		default:
			System.err.println("Set Attribute in Gates that was not recognized: "+cmd);
			break;
		}
	}

	@Override
	public Object getAttribute(String cmd) {
		switch(cmd){
		case ATTRIBUTE_GATES:
			return gates;
		default:
			System.err.println("get Attribute in Gates that was not recognized: "+cmd);
			return "";
		}
	}

	@Override
	public void updateConfigs(DrawController dc) {
	}
	
	
	public static class Gate {
		private String name, type;
		
		public Gate(String name, String type){
			this.name = name;
			this.type = type;
		}
		
		public String getName(){
			return name;
		}
		
		public String getType(){
			return type;
		}
		
		public static Gate getDefaultGate(){
			return new Gate("Gate", "Int # Int");
		}

		public void setAttribute(String command, String newValue) {
			switch (command){
			case ATTRIBUTE_NAME:
					this.name = newValue;
				break;
			case ATTRIBUTE_TYPE:
					this.type = newValue;
				break;
			}
			
		}
	}

}
