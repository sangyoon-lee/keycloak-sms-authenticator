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

import jp.openstandia.keycloak.authenticator.api.SMSSendVerify;

public class SMSAuthenticator implements Authenticator {

	private static final Logger logger = Logger.getLogger(SMSAuthenticator.class.getPackage().getName());

	public void authenticate(AuthenticationFlowContext context) {
		logger.debug("Method [authenticate]");

		AuthenticatorConfigModel config = context.getAuthenticatorConfig();

		UserModel user = context.getUser();
		String phoneNumber = getPhoneNumber(user);
		logger.debugv("phoneNumber : {0}", phoneNumber);

		if (phoneNumber != null) {

			// SendSMS
			SMSSendVerify sendVerify = new SMSSendVerify(getConfigString(config, SMSAuthContstants.CONFIG_SMS_API_KEY),
					getConfigString(config, SMSAuthContstants.CONFIG_PROXY_FLAG),
					getConfigString(config, SMSAuthContstants.CONFIG_PROXY_URL),
					getConfigString(config, SMSAuthContstants.CONFIG_PROXY_PORT),
					getConfigString(config, SMSAuthContstants.CONFIG_CODE_LENGTH));

			if (sendVerify.sendSMS(phoneNumber)) {
				Response challenge = context.form().createForm("sms-validation.ftl");
				context.challenge(challenge);

			} else {
				Response challenge = context.form().setError("認証コードの送信に失敗しました。")
						.createForm("sms-validation-error.ftl");
				context.challenge(challenge);
			}

		} else {
			Response challenge = context.form().setError("電話番号が設定されていません。")
					.createForm("sms-validation-error.ftl");
			context.challenge(challenge);
		}

	}

	public void action(AuthenticationFlowContext context) {
		logger.debug("Method [action]");

		MultivaluedMap<String, String> inputData = context.getHttpRequest().getDecodedFormParameters();
		String enteredCode = inputData.getFirst("smsCode");

		UserModel user = context.getUser();
		String phoneNumber = getPhoneNumber(user);
		logger.debugv("phoneNumber : {0}", phoneNumber);

		// SendSMS
		AuthenticatorConfigModel config = context.getAuthenticatorConfig();
		SMSSendVerify sendVerify = new SMSSendVerify(getConfigString(config, SMSAuthContstants.CONFIG_SMS_API_KEY),
				getConfigString(config, SMSAuthContstants.CONFIG_PROXY_FLAG),
				getConfigString(config, SMSAuthContstants.CONFIG_PROXY_URL),
				getConfigString(config, SMSAuthContstants.CONFIG_PROXY_PORT),
				getConfigString(config, SMSAuthContstants.CONFIG_CODE_LENGTH));

		if (sendVerify.verifySMS(phoneNumber, enteredCode)) {
			logger.info("verify code check : OK");
			context.success();

		} else {
			Response challenge = context.form()
					.setAttribute("username", context.getAuthenticationSession().getAuthenticatedUser().getUsername())
					.setError("認証コードが一致しません。").createForm("sms-validation-error.ftl");
			context.challenge(challenge);
		}

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
		List<String> phoneNumberList = user.getAttribute(SMSAuthContstants.ATTR_PHONE_NUMBER);
		if (phoneNumberList != null && !phoneNumberList.isEmpty()) {
			return phoneNumberList.get(0);
		}
		return null;
	}

	private String getConfigString(AuthenticatorConfigModel config, String configName) {
		String value = null;
		if (config.getConfig() != null) {
			// Get value
			value = config.getConfig().get(configName);
		}
		return value;
	}
}