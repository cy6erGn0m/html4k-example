This is example Kotlin Web project that 
demonstrates [html4k](https://github.com/cy6erGn0m/html4k) and [RxKotlin](https://github.com/ReactiveX/RxKotlin). 

tags: Kotlin, Web, html4k, Kotlin programming

# Requirements
 - Java 8
 - Maven 3.1.0+
 - Apache Tomcat 8.0.20+
 
# Build
```bash
$ mvn clean package 
```

Result war should appear in **assembly/target**

If you use IDEA you shouldn't use "Make" step, use Maven Goal instead.
To skip tests use Maven Goal "package -DskipTests=true"

# Running
You can create Tomcat configuration in IDEA to run, just don't forget to replace Make with Maven Goal. Otherwise 
you always can deploy war to tomcat instance via UI or by hands by copying. 
 
