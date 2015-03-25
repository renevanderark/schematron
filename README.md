# Schematron


This is a simple schematron implementation in java.

## Install

Installing through maven from the project root

	$ mvn clean install

## Run

Run the standalone jar with test file from the resources directory

```
	$ export REPO_HOME="/path/to/.m2/repository
	$ java -cp $REPO_HOME/nl/kb/schematron/1.0-SNAPSHOT/schematron-1.0-SNAPSHOT-jar-with-dependencies.jar nl.kb.schematron.SchematronRunner src/test/resources/jp2-gvn-schema.sch src/test/resources/jp2-failed-test.xml
```

Or use the shell script

```
	$ export REPO_HOME="/path/to/.m2/repository
	$ ./run.sh  src/test/resources/jp2-gvn-schema.sch src/test/resources/jp2-failed-test.xml
```

## Use

Initialize the SchematronValidator with a StreamSource to the schematron schema

```java
	SchematronValidator validator = new SchematronValidator(new StreamSource(new FileInputStream("path/to/schema.sch")));
```

Validate an xml as StreamSource against the schematron schema

```java
	SchematronResult result = validator.validate(new StreamSource(new FileInputStream("path/to/file.xml")));
```

Check if the result is valid

```java
	result.isValid();
```

Print the xml result

```java
	System.out.println(result);
```

Print any failed assertions

```java
	for(FailedAssertion failedAssertion : result.getFailedAssertions()) {
		System.out.println(failedAssertion);
	}
```
Print the schematron rules which have been fired

```java
	for(FiredRule firedRule : result.getFiredRules()) {
		System.out.println(firedRule);
	}
```
