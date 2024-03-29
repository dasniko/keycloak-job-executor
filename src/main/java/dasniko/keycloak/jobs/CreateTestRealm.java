package dasniko.keycloak.jobs;

import dasniko.keycloak.KeycloakJob;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RealmRepresentation;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
public class CreateTestRealm implements KeycloakJob {

    @Override
    public String getId() {
        return "create-test-realm";
    }

    @Override
    public void execute(Keycloak kc, String realmName) {
        createRealm(kc, realmName);
    }

    private static void createRealm(Keycloak kc, String realmName) {
        RealmRepresentation realm = new RealmRepresentation();
        realm.setRealm(realmName + "_" + System.currentTimeMillis());
        realm.setEnabled(Boolean.TRUE);
        kc.realms().create(realm);
    }

}
