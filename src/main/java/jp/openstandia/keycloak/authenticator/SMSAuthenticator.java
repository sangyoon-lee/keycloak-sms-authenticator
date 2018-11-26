package jp.openstandia.keycloak.authenticator;

import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

public class SMSAuthenticator implements Authenticator {

	private static final Logger logger = Logger.getLogger(SMSAuthenticator.class.getPackage().getName());

	public void authenticate(AuthenticationFlowContext context) {
		logger.debug("Method [authenticate]");

		AuthenticatorConfigModel config = context.getAuthenticatorConfig();
		if (config != null) {
			logger.debugv("send : {0}", Contstants.CONFIG_SMS_SEND_URL);
			logger.debugv("verify : {0}", Contstants.CONFIG_SMS_VERIFY_URL);
			logger.debugv("apikey : {0}", Contstants.CONFIG_SMS_API_KEY);
			logger.debugv("code : {0}", Contstants.CONFIG_SMS_COUNTRY_CODE);
		}

		// 電話番号取得
		UserModel user = context.getUser();
		String phoneNumber = getPhoneNumber(user);
		logger.debugv("phoneNumber : {0}", phoneNumber);

		if (phoneNumber != null) {
			Response challenge = context.form().createForm("sms-validation.ftl");
			context.challenge(challenge);
		} else {
			Response challenge = context.form().setError("電話番号が設定されていません")
					.createForm("sms-validation-error.ftl");
			context.challenge(challenge);
		}

	}

	public void action(AuthenticationFlowContext context) {
		logger.debug("Method [action]");
		MultivaluedMap<String, String> inputData = context.getHttpRequest().getDecodedFormParameters();
		String enteredCode = inputData.getFirst("smsCode");

		logger.debug("smsCode : " + enteredCode);
		context.success();
	}

	public boolean requiresUser() {
		logger.debug("Method [requiresUser]");
		return false;
	}

	public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
		logger.debug("Method [configuredFor]");
		return false;
	}

	public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {

	}

	public void close() {
		logger.debug("<<<<<<<<<<<<<<< SMSAuthenticator close");
	}

	private String getPhoneNumber(UserModel user) {
		List<String> phoneNumberList = user.getAttribute(Contstants.ATTR_PHONE_NUMBER);
		if (phoneNumberList != null && !phoneNumberList.isEmpty()) {
			return phoneNumberList.get(0);
		}
		return null;
	}
}