[ ![Annotations](https://api.bintray.com/packages/stcarolas/maven/enriched-beans-annotations/images/download.svg) ](https://bintray.com/stcarolas/maven/enriched-beans-annotations/_latestVersion)

# enriched-beans

A source code generator for JSR-330-compatible factories (for Spring, Micronaut and others). Like AutoFactory but usable with lombok.

### Example

Say you have:

```java
public class SomeClass {
  private final String notInjectableField;
  @Enrich private final String depA;
  @Enrich @Named("myDep") private final String depB;

  // …

  public void sayHello(){}
}
```

Produced factory would be like:
```java
@Singleton
public class SomeClassFactory {
  private final String depA;
  private final String depB;

  @Inject
  public SomeClassFactory(
    String depA,
    @Named("myDep") String depB
  ){

  }

  public SomeClass from(String notInjectableField){
    return new SomeClass(notInjectableField, depA, depB);
  }
}
```

And factory usage:
```java
@Component
public class AnotherBean{
  @Autowired SomeClassFactory factory;

  ...

  factory.from("notInjectableFieldValue").sayHello();
}
```

### Using with Gradle, Lombok, Spring (Lombok must be before enriched)
```
  implementation 'javax.inject:javax.inject:1'
  implementation 'org.projectlombok:lombok:1.18.12'
  implementation 'com.github.stcarolas.enriched-beans:enriched-beans-annotations:0.1.6'
  ...
  annotationProcessor 'org.projectlombok:lombok:1.18.12'
  annotationProcessor 'com.github.stcarolas.enriched-beans:enriched-beans-processor:0.1.6'
```

### Using with Maven, Lombok, Micronaut  (Lombok must be before enriched)
```
    <dependency>
      <groupId>com.github.stcarolas.enriched-beans</groupId>
      <artifactId>enriched-beans-annotations</artifactId>
      <version>0.1.6</version>
    </dependency>
    ...
    <plugin>
      <artifactId>maven-compiler-plugin</artifactId>
      <version>3.8.1</version>
      <configuration>
        <compilerArguments>
          <AaddGeneratedAnnotation>false</AaddGeneratedAnnotation>
          <Adebug>true</Adebug>
        </compilerArguments>
        <dependencies>
          <dependency>
            <groupId>org.ow2.asm</groupId>
            <artifactId>asm</artifactId>
            <version>7.3.1</version>
            <type>jar</type>
          </dependency>
        </dependencies>
        <annotationProcessorPaths>
          <annotationProcessorPath>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.12</version>
          </annotationProcessorPath>
          <annotationProcessorPath>
            <groupId>com.github.stcarolas.enriched-beans</groupId>
            <artifactId>enriched-beans-processor</artifactId>
            <version>0.1.6</version>
          </annotationProcessorPath>
          <annotationProcessorPath>
            <groupId>io.micronaut</groupId>
            <artifactId>micronaut-inject-java</artifactId>
            <version>1.3.4</version>
          </annotationProcessorPath>
        </annotationProcessorPaths>
      </configuration>
    </plugin>
```
