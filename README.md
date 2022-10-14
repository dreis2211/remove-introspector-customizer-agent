# Summary
This project serves a little Java agent that removes the support for looking up Bean `Customizer`s inside `java.beans.Introspector`.
It does so by essentially making the internal `findCustomizerClass` method a no-op and returning null.

This is inspired by the findings in Spring-Framework:
- https://github.com/spring-projects/spring-framework/issues/29320
- https://github.com/spring-projects/spring-framework/issues/26884#issuecomment-1276425257

Where it was revealed that a reasonable amount of time is spent in `Introspector`.
Especially on finding `Customizer` classes that 99% of Spring applications don't have.

<img src="https://user-images.githubusercontent.com/6304496/195392297-a23f2820-f434-480b-9fa4-d78b84686d68.jpg" alt="CPU profile" />
<img src="https://user-images.githubusercontent.com/6304496/195426215-6d3499ab-42fb-43f0-93d3-78f174dd9db1.png" alt="Allocation profile" />


# Usage

**NOTE**: This agent is not meant to run in production usages and
I don't claim this is safe to use and only do so at your own risk.

Before using the agent, you need to build the agent and put it somewhere where you need it:

```
./gradlew build
```

Depending on your use-case you might need to put this in place then:

## Plain Java

```text
java -javaagent:/path/to/the/agent/remove-introspector-customizer-agent.jar ...
```

## Maven Surefire Tests
E.g. in Maven Surefire tests
```xml
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-surefire-plugin</artifactId>
	<configuration>
		<argLine>-javaagent:/path/to/the/agent/remove-introspector-customizer-agent.jar</argLine>
	</configuration>
</plugin>
```

## Gradle Tests
Or for Gradle tests
```groovy
test {
    jvmArgs '-javaagent:/path/to/the/agent/remove-introspector-customizer-agent.jar'
}
```

Happy experimenting with measuring eventual impacts.
