# spring-k8s-demo

A simple Spring Kubernetes app for demo using gradle.  Adapted from these [Examples.](https://github.com/spring-cloud/spring-cloud-kubernetes/tree/master/spring-cloud-kubernetes-examples)

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

## References

[Spring Cloud Kubernetes Documentation](https://cloud.spring.io/spring-cloud-kubernetes/reference/html/#why-do-you-need-spring-cloud-kubernetes)

[Creating Docker Images with Spring Boot](https://spring.io/blog/2020/01/27/creating-docker-images-with-spring-boot-2-3-0-m1)

[Spring Cloud Kubernetes Repo - check out the examples](https://github.com/spring-cloud/spring-cloud-kubernetes)
