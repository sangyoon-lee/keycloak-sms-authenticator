package jp.openstandia.keycloak.authenticator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import org.jboss.logging.Logger;

public class SMSSendVerify {

	private static final Logger logger = Logger.getLogger(SMSSendVerify.class.getPackage().getName());

	private final boolean isProxy;
	private final String proxyUrl;
	private final String proxyPort;
	private final String apiKey;

	public SMSSendVerify(String apiKey, String isProxy, String proxyUrl, String proxyPort) {
		this.apiKey = apiKey;
		this.isProxy = Boolean.parseBoolean(isProxy);
		this.proxyUrl = proxyUrl;
		this.proxyPort = proxyPort;
	}

	public boolean sendSMS(String telnum) {

		logger.debug("send SMS start..... proxy is " + isProxy);
		boolean result = false;
		try {
			URL url = new URL(Contstants.TWILIO_URL + "start");

			HttpURLConnection conn = null;
			if (isProxy) {
				Proxy proxy = new Proxy(Proxy.Type.HTTP,
						new InetSocketAddress(this.proxyUrl, Integer.parseInt(this.proxyPort)));
				conn = (HttpURLConnection) url.openConnection(proxy);
			} else {
				conn = (HttpURLConnection) url.openConnection();
			}

			conn.setRequestMethod("POST");
			conn.setRequestProperty("api_key", this.apiKey);
			conn.setRequestProperty("via", "sms");
			conn.setRequestProperty("phone_number", telnum);
			conn.setRequestProperty("country_code", "81");

			conn.connect();

			int resStatus = conn.getResponseCode();
			logger.infov("RESPONSE STATUS : {0}", resStatus);

			if (resStatus == HttpURLConnection.HTTP_OK) {
				InputStream in = conn.getInputStream();

				BufferedReader reader = new BufferedReader(new InputStreamReader(in));

				StringBuilder output = new StringBuilder();
				String line;

				while ((line = reader.readLine()) != null) {
					logger.infov("RESPONSE DETAIL : {0}", line);
					output.append(line);
				}
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public boolean verifySMS(String telnum, String code) {
		logger.debug("checkCode start..... proxy is " + isProxy);
		Boolean result = false;

		try {
			URL url = new URL(Contstants.TWILIO_URL + "check");

			HttpURLConnection conn = null;
			if (isProxy) {
				Proxy proxy = new Proxy(Proxy.Type.HTTP,
						new InetSocketAddress(this.proxyUrl, Integer.parseInt(this.proxyPort)));
				conn = (HttpURLConnection) url.openConnection(proxy);
			} else {
				conn = (HttpURLConnection) url.openConnection();
			}

			conn.setRequestMethod("GET");
			conn.setRequestProperty("api_key", this.apiKey);
			conn.setRequestProperty("verification_code", code);
			conn.setRequestProperty("phone_number", telnum);
			conn.setRequestProperty("country_code", "81");

			conn.connect();

			int resStatus = conn.getResponseCode();
			logger.infov("RESPONSE STATUS : {0}", resStatus);

			if (resStatus == HttpURLConnection.HTTP_OK) {
				InputStream in = conn.getInputStream();

				BufferedReader reader = new BufferedReader(new InputStreamReader(in));

				StringBuilder output = new StringBuilder();
				String line;

				while ((line = reader.readLine()) != null) {
					logger.infov("RESPONSE DETAIL : {0}", line);
					output.append(line);
				}
				result = true;
			}

			logger.infov("checkCode result:" + result.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
