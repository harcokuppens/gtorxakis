package model;

public class Transition {
	
	private String channel,
				   condition,
				   action;
	
	public Transition (String channel, String condition, String action){
		this.channel = channel;
		this.condition = condition;
		this.action = action;
	}
	
	public String getChannel(){
		return channel;
	}
	
	public String getCondition(){
		return condition;
	}
	
	public String getAction(){
		return action;
	}
	
	public void setChannel(String channel){
		this.channel = channel;
	}
	
	public void setCondition(String condition){
		this.condition = condition;
	}
	
	public void setAction(String action){
		this.action = action;
	}
	
	public static Transition getDefaultTransition(){
		return new Transition("Opt ? x ? y","", "statevar := x + y");
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Transition){
			Transition t = (Transition) o;
			if(channel.equals(t.getChannel())){
				if(condition.equals(t.getCondition())){
					if(action.equals(t.getAction())){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public String toString(){
		return channel + ", " + condition + ", " + action;
	}

}