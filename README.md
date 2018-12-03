# keycloak-sms-authenticator

To install the SMS Authenticator one has to:

    Add the jar to the Keycloak server:
        $ cp target/sms-authenticator.jar _KEYCLOAK_HOME_/providers/

    Add two templates to the Keycloak server:
        $ cp sms-validation.ftl _KEYCLOAK_HOME_/themes/base/login/
        $ cp sms-validation-error.ftl _KEYCLOAK_HOME_/standalone/deployments/

Configure your REALM to use the SMS Authentication. First create a new REALM (or select a previously created REALM).

Under Authentication > Flows:

    Copy 'Browse' flow to 'Browser with SMS' flow
    Click on 'Actions > Add execution on the 'Browser with SMS Forms' line and add the 'SMS Authentication'
    Set 'SMS Authentication' to 'REQUIRED'
    To configure the SMS Authernticator, click on Actions Config and fill in the attributes.

Under Authentication > Bindings:

    Select 'Browser with SMS' as the 'Browser Flow' for the REALM.
