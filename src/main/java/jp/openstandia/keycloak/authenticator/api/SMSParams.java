package jp.openstandia.keycloak.authenticator.api;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public class SMSParams {

	private Map<String, String> data;

	public SMSParams() {
		data = new HashMap<String, String>();
	}

	public void setAttribute(String key, String value) {
		this.data.put(key, value);
	}

	public Map<String, String> toMap() {
		return this.data;
	}

	public String toJSON() {
		JSONObject json = new JSONObject();
		for (Map.Entry<String, String> entry : toMap().entrySet()) {
			json.put(entry.getKey(), entry.getValue());
		}
		return json.toString();
	}
}
