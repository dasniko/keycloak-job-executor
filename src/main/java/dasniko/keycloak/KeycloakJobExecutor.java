package dasniko.keycloak;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Properties;
import java.util.ServiceLoader;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
public class KeycloakJobExecutor {

    private Properties props;

    public static void main(String[] args) {
        new KeycloakJobExecutor().run();
    }

    private void run() {
        String username = System.getProperty("kcAdminUsr");
        String password = System.getProperty("kcAdminPwd");
        String environment = System.getProperty("kcEnv", "");

        String jobId = System.getProperty("kcJobId");

        readApplicationProperties(environment);

        Keycloak kc = createKeycloakClient(username, password);

        ServiceLoader<KeycloakJob> serviceLoader = ServiceLoader.load(KeycloakJob.class);
        for (KeycloakJob job : serviceLoader) {
            if (null != job.getId() && job.getId().equalsIgnoreCase(jobId)) {
                System.out.printf("Found job %s in %s\n", jobId, job.getClass().getCanonicalName());
                System.out.printf("Executing %s...\n", jobId);
                job.execute(kc, props.getProperty("realm"));
                System.out.printf("Finished job %s\n", jobId);
            }
        }
    }

    private Keycloak createKeycloakClient(String username, String password) {
        String authServerUrl = props.getProperty("authServerUrl", "http://localhost:8080/auth");
        String realm = props.getProperty("adminRealm", "master");
        String clientId = props.getProperty("adminClientId", "admin-cli");

        System.out.printf("Creating KeycloakClient with authServerUr=%s, realm=%s, clientId=%s\n" +
                "This may take some seconds...\n",
            authServerUrl, realm, clientId);

        ResteasyClientBuilder resteasyClientBuilder = new ResteasyClientBuilder();
        if (null != props.getProperty("proxyHost")) {
            resteasyClientBuilder.defaultProxy(
                props.getProperty("proxyHost"),
                Integer.parseInt(props.getProperty("proxyPort"))
            );
        }
        ResteasyClient resteasyClient = resteasyClientBuilder.build();

        return KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm(realm)
                .username(username)
                .password(password)
                .clientId(clientId)
                .resteasyClient(resteasyClient)
                .build();
    }

    private void readApplicationProperties(String environment) {
        String propsFileName = "application";
        if (null != environment && !environment.isEmpty()) {
            propsFileName += "-" + environment;
        }
        propsFileName += ".properties";

        System.out.printf("Using properties file %s\n", propsFileName);

        props = new Properties();
        try {
            props.load(this.getClass().getClassLoader().getResourceAsStream(propsFileName));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
