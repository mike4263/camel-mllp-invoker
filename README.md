# camel-mllp-invoker

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -Dmllp.ip=<WORKER NODE> -Dmllp.port=<NODE PORT> -jar target/*-runner.jar`.
