package dasniko.keycloak;

import org.keycloak.admin.client.Keycloak;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
public interface KeycloakJob {

    String getId();

    void execute(Keycloak kc, String realmName);

}
