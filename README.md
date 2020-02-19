# spring-k8s-demo

A simple Spring Kubernetes app for demo using gradle.  Adapted from these [Examples](https://github.com/spring-cloud/spring-cloud-kubernetes/tree/master/spring-cloud-kubernetes-examples).  This demo is intended to walk through some of Spring Boot and Spring Cloud Kubernetes' capabilites when running in a Kubernetes Cluster.  Note that many of the steps taken in this demo are best performed by your CICD tool of choice.

## Preparing My Spring Boot App for Kubernetes
In order to take advantage of Spring Cloud Kubernetes, there are some configuration changes that must be made to your application.  It is important to note that these changes will not prevent your application from running outside of Kubernetes in keeping with [12 Factor](https://12factor.net/) principles.  These configurations just make it possible for your application to consume configuration from the environment in a Kubernetes context.

First, bootstrap.yaml

```yaml

spring:
  application:
    name: spring-k8s-demo
  cloud:
    kubernetes:
      reload:
        enabled: true
        mode: polling
        period: 5000
      config:
        namespace: spring-k8s-demo
        sources:
          - name: app-config
      secrets:
        namespace: spring-k8s-demo
        paths:
        - /etc/secrets


```

### Reading Config from a k8s ConfigMaps
A particularly useful feature of Spring Cloud Kubernetes is making it possible to read application configuration out of a ConfigMap.  ConfigMaps decouple configuration from an image and provide a great abastraction for the environemnt to expose configuration.

This configuration tells our application to look for a config map named *app-config* in the namespace *spring-k8s-demo*.  Note that we are not explicitly binding the value of any particular property to the config map, just configuring the app to consume this configuration if it is present.  Within the application, our properties may be sourced from a hard coded default, an environment variable, or a properties file.  Just emphasizing that we do not need to make our application depenendant on Kubernetes as there are other contexts that we may run in with alternate mechanisms for exposing configuration.

```yaml
config:
        namespace: spring-k8s-demo
        sources:
          - name: app-config

```

In our cluster we will create a config map as follows `kubectl create -f app-config.yaml -n spring-k8s-demo`

```yaml

apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
data:
  application.properties: |-
    api.data=Elliott is a boy
    api.version=v2
    api.format=json

```

When our application starts up in our cluster with these configurations in place we can inject them into a configuration object.  Note that defaulted values will be overwrriten, so sensible defaults can be used for local dev.

```java 

@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = "api")
public class ApiConfig {

    private String version = "v1";
    private String config = "default";
    private String format = "json";
    private String data;


```

A config object can be wired into any component accross the application.

```java

    @Autowired
    private ApiConfig apiConfig;

```

### Reading Config from a k8s Secrets
Another approach to storing configuration in Kubernetes is to use secrets. Secrets store sensitive values like username or password, and connection strings.  Similar to ConfigMaps, we want a way of consuming secrets so that our application can use them.

In this case we take a slightly different approach to consuming configuration exposed by secrets.  We will choose to mount our secrets as a volume within our running container and point our application at the mounted path.

```yaml

secrets:
        namespace: spring-k8s-demo
        paths:
        - /etc/secrets
```

If we use this location (/etc/secrets) as a convention within our applications we can configure our containers to mount secrets and still maintain independence from Kubernetes, as only our Deployment/Pod definitions will need to be aware of specific secrets.  We are mounting a secret named *app-secret* in a volume named *app-secrets*, in this case.
 
```yaml

...
volumes:
      - name: app-secrets
        secret:
          secretName: app-secret
      serviceAccountName: spring-app
      imagePullSecrets:
        - name: tsfrt-pivotal
      containers:
        - image: harbor.tsfrt-pivotal.info/k8s-demo/spring-k8s-demo:0.0.1-SNAPSHOT
          name: spring-k8s-demo-cont
          volumeMounts:
          - name: app-secrets
            mountPath: "/etc/secrets"
            readOnly: true
...
```
In the example above we are mounting *app-secrets* at /etc/secrets.

```yaml

apiVersion: v1
kind: Secret
metadata:
  name: app-secret
type: Opaque
data:
  username: c2VjcmV0
  password: Y29tcGxleC1wYXNzd29yZA==
  connection: aHR0cDovL215LXNlcnZpY2UvdXJs
```
This will make the username, password, and connection properties available within our application.  We are now free to inject these values as such.

```java

...
@Configuration(proxyBeanMethods = false)
@ConfigurationProperties()
public class ConnectionConfig {

    private String username;
    private String password;
    private String connection;

...

```

This configuration object can then be wired into our application whenever needed with the populated values.

```java

   @Autowired
   private ConnectionConfig connectionConfig;

```
### Configuration Reloading

Once an application is consuming configuration from the environment, it can easily be configured to monitor for changes.  This is particularly useful if there is a dynamic configuration item that will require real-time changes without the need for a deployment.

```yaml

...
cloud:
    kubernetes:
      reload:
        enabled: true
        mode: polling
        period: 5000
...

```
This configuration directs Spring to poll the kubernetes configuration every 5 seconds for changes.  Changes made through editing a ConfigMap or Secret will be reflected in near real-time.

See [Spring Cloud Kubernetes Documentation](https://cloud.spring.io/spring-cloud-kubernetes/reference/html/#why-do-you-need-spring-cloud-kubernetes) for detailed information on these and more paramaters.

## Building Docker Images with Spring Boot and Cloud Native Build Packs
[For Reference](https://spring.io/blog/2020/01/27/creating-docker-images-with-spring-boot-2-3-0-m1)

With Spring Boot 2.3.0.M1 we gain the ability to build docker images with clould native build packs from Spring Boot.  

In order to build a docker image from this project run the following command.

`./gradlew bootBuildImage`

Cloud Native Buildpacks take care of providing the dependencies such as the JDK or web container (like Tomcat or Jetty).  Learn more about Buildpacks [here](https://buildpacks.io/).

Additional configuration can be applied to the image creation process in gradle or maven build scripts.  For instance, I am customizing the image name based on my project name and version.
[Docs](https://docs.spring.io/spring-boot/docs/current-SNAPSHOT/gradle-plugin/reference/html/#build-image)


```groovy
bootBuildImage {
	imageName = "harbor.tsfrt-pivotal.info/k8s-demo/${rootProject.name}:${project.version}"
	//environment = ["BP_JAVA_VERSION" : "13.0.1"]
	cleanCache = true
	verboseLogging = true
}
```

Once my image has been built and tested I want to deploy it to a registry that my kubernetes cluster can access.  Ideally my image will be scanned to ensure that no known vulnerabilities are present and that best practices have been followed.  In order to achieve this, I will push my tested image to a [Harbor Repository](https://goharbor.io/) using the following command.

`docker push harbor.tsfrt-pivotal.info/k8s-demo/spring-k8s-demo:0.0.11-SNAPSHOT`

## Monitoring Containers for Readiness and Liveness with Actuator
Kubernetes has a concept of Liveness and Readiness for containers running within a cluster.  Liveness is a determination of a container's health; in the case of a Spring Boot Web app this may mean responding to HTTP Requests.  Related to Liveness is Readiness; Readiness is a determination when a container is marked as ready.  There is a (hopefully brief) startup time where a container is running, but the application workload is not ready to accept work and a Readiness check is used to evaluate this state.  

Spring Boot Actuator has several built HTTP resources that can be used to determine the state of the running container to ensure that it is ready and healthy.  Actuator does a lot more that this, check it here [Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html)

In the following Kubernetes Deployment, we have configured the pods to check actuator's HTTP endpoints for Readiness and Liveness.

```yaml

apiVersion: apps/v1
kind: Deployment
metadata:
  name: spring-k8s-demo
  labels:
    app: spring-k8s-demo-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: spring-k8s-demo-app
  template:
    metadata:
      labels:
        app: spring-k8s-demo-app
    spec:
      volumes:
      - name: app-secrets
        secret:
          secretName: app-secret
      serviceAccountName: spring-app
      imagePullSecrets:
        - name: tsfrt-pivotal
      containers:
        - image: harbor.tsfrt-pivotal.info/k8s-demo/spring-k8s-demo:0.0.1-SNAPSHOT
          name: spring-k8s-demo-cont
          volumeMounts:
          - name: app-secrets
            mountPath: "/etc/secrets"
            readOnly: true
          imagePullPolicy: Always
          ports:
            - containerPort: 8080
              name: http
              protocol: TCP
            - containerPort: 9779
              name: prometheus
              protocol: TCP
          livenessProbe:
            failureThreshold: 3
            httpGet:
              path: /actuator/health
              port: 8080
              scheme: HTTP
            initialDelaySeconds: 180
            successThreshold: 1
          readinessProbe:
            failureThreshold: 3
            httpGet:
              path: /actuator/health
              port: 8080
              scheme: HTTP
            initialDelaySeconds: 10
            successThreshold: 1
          securityContext:
            privileged: false
          resources:
            limits:
              memory: "257Mi"
              cpu: "500m"

```

In order to accomplish these checks we expose the server port 8080 and configure the web resource `path: /actuator/health`, which is a standard Actuator endpoint.  The Spring Boot Application will have to of started in order for `/actuator/health` to return a 200 status code.  These prevents the cluster from directing traffic to a containter prematurely and thus avoiding any end user down time.


## References

[Spring Cloud Kubernetes Documentation](https://cloud.spring.io/spring-cloud-kubernetes/reference/html/#why-do-you-need-spring-cloud-kubernetes)

[Creating Docker Images with Spring Boot](https://spring.io/blog/2020/01/27/creating-docker-images-with-spring-boot-2-3-0-m1)

[Spring Cloud Kubernetes Repo - check out the examples](https://github.com/spring-cloud/spring-cloud-kubernetes)

[Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html)


