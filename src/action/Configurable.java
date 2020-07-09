package action;

import gui.control.DrawController;

public interface Configurable {
	public void setAttribute(String cmd, Object value);
	public Object getAttribute(String cmd);
	public void updateConfigs(DrawController dc);
}

