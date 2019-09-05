# Keycloak Admin-Job Executor

Simple tool to run/execute admin jobs against a Keycloak instance.

## Configuration

|Param |Location |Note |Default |
|---|---|---|---|
|`authServerUrl` |`application.properties` |The URL of the Keycloak server to run the jobs. |`http://localhost:8080/auth` |
|`adminRealm` |`application.properties` | |`master` |
|`adminClientId` |`application.properties` | |`admin-cli` |
|`realm` |`application.properties` |The name of the realm the jobs should run in. | |
|`proxyHost`* |`application.properties` |The hostname of an optional proxy. | |
|`proxyPort`* |`application.properties` |The port of an optional proxy. | |
|`kcAdminUsr` |System properties / prompt |The admin username. | |
|`kcAdminPwd` |System properties / prompt |The password of the admin user. | |
|`kcEnv`* |System properties / prompt |If given, the appropriate `application-{env}.properties` file of the environment is used. _(see below)_ | |
|`kcJobId` |System properties / prompt |The ID (name) of the job to run/execute. | |

_\* = optional_

### Environment dependent configuration

If you have more than one environment to support, you can use/create multiple `application-{env}.properties` files, containing the environment name.
When the config value `kcEnv` is provided, the application tries to load the properties from this file.
Be aware, that _only this_ properties file is being use, it's no hierarchical approach like the Spring Boot configuration.
You _always_ have to provide _all_ config values in the environment specific file.

## Run

To run this tool, simply execute

    $ mvn exec:java

at the project root in your CLI.
Provide optional system properties with `-DmySystemPropertyName=...`, if desired.

It's also possible to run the `main()` method of [`KeycloakJobExecutor`](./src/main/java/dasniko/keycloak/KeycloakJobExecutor.java) class directly from your IDE.
Due to lack of the `Console` object in IDEs, the password prompt will not be hidden, but visible in cleartext in your IDE console!

## Creating Jobs

A Job is simply an instance of the [`KeycloakJob`](./src/main/java/dasniko/keycloak/KeycloakJob.java) interface.

This tool "finds" its jobs/tasks via the [Service Provider Interfaces](https://docs.oracle.com/javase/tutorial/sound/SPI-intro.html) mechanism with the [ServiceLoader](https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html).
To be able to use your created job, you have to enter its fully qualified class names into the [`META-INF/services/dasniko.keycloak.KeycloakJob`](./src/main/resources/META-INF/services/dasniko.keycloak.KeycloakJob) file.

The returned string of the `getId()` method will be used to find the job with the `kcJobId` config value _(see above)_.
