package controllers;

import org.json.simple.JSONObject;

public interface PortalViewInterface {
	public void handleMsg(Object json);

	public void init();

	public ComController getClientController();
}
