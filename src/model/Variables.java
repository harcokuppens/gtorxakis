package model;

import java.util.ArrayList;

import action.Configurable;
import gui.control.DrawController;
/**
 * A container for all variables that will be specified in a stautdef as VARs.
 * @author Tobias
 *
 */
public class Variables implements Configurable {
	
	private ArrayList<Variable> variables;

	
	public static final String ATTRIBUTE_VARIABLES = "variables",
			   				   ATTRIBUTE_NAME = "name",
			   				   ATTRIBUTE_TYPE = "type",
			   				   ATTRIBUTE_INIT = "init";
	
	public Variables(){
		this(new ArrayList<Variable>());
	}

	public Variables(ArrayList<Variable> variables) {
		this.variables = variables;
	}
	
	public ArrayList<Variable> getVariables(){
		return variables;
	}
	
	public void addVariables(Variable variable){
		variables.add(variable);
	}

	@Override
	public void setAttribute(String cmd, Object value) {
		switch(cmd){
		case ATTRIBUTE_VARIABLES:
			variables = new ArrayList<Variable>((ArrayList<Variable>) value);
			break;
		default:
			System.err.println("Set Attribute in variables that was not recognized: "+cmd);
			break;
		}
	}

	@Override
	public Object getAttribute(String cmd) {
		switch(cmd){
		case ATTRIBUTE_VARIABLES:
			return variables;
		default:
			System.err.println("get Attribute in variables that was not recognized: "+cmd);
			return "";
		}
	}

	@Override
	public void updateConfigs(DrawController dc) {
	}
	
	public static class Variable {
		private String name, type, initValue;
		
		public Variable(String name, String type, String initValue){
			this.name = name;
			this.type = type;
			this.initValue = initValue;
		}
		
		public String getName(){
			return name;
		}
		
		public String getType(){
			return type;
		}
		
		public static Variable getDefaultVariable(){
			return new Variable("x", "Int", "0");
		}
		
		public String getInitValue(){
			return initValue;
		}
		
		public void setAttribute(String command, String newValue) {
			switch (command){
			case ATTRIBUTE_NAME:
					this.name = newValue;
				break;
			case ATTRIBUTE_TYPE:
					this.type = newValue;
				break;
			case ATTRIBUTE_INIT:
				this.initValue = newValue;
			}
		}
		
	}

}
