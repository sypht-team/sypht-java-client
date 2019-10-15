[![Build Status](https://travis-ci.com/sypht-team/sypht-java-client.svg?branch=master)](https://travis-ci.com/sypht-team/sypht-java-client.svg?branch=master) [![codecov](https://codecov.io/gh/sypht-team/sypht-java-client/branch/master/graph/badge.svg)](https://codecov.io/gh/sypht-team/sypht-java-client)

# Sypht Java Client
This repository is a Java reference client implementation for working with the Sypht API. [![Docs](https://img.shields.io/badge/API%20Docs-site-lightgrey.svg?style=flat-square)](https://docs.sypht.com)

## About Sypht
[Sypht](https://sypht.com) is a SaaS [API]((https://docs.sypht.com/)) which extracts key fields from documents. For
example, you can upload an image or pdf of a bill or invoice and extract the amount due, due date, invoice number
and biller information.

## Getting started
To get started you'll need API credentials, i.e. a `<client_id>` and `<client_secret>`, which can be obtained by registering
for an [account](https://www.sypht.com/signup/developer)

## Prerequisites
JDK8 and upwards are supported.

```Bash
brew tap AdoptOpenJDK/openjdk
brew cask install adoptopenjdk8
```

## Installation
Sypht Java Client is available on maven central

### Maven
```Xml
<dependency>
  <groupId>com.sypht</groupId>
  <artifactId>sypht-java-client</artifactId>
  <version>1.3</version>
</dependency>
```

### Gradle
```Gradle
// https://mvnrepository.com/artifact/com.sypht/sypht-java-client
compile group: 'com.sypht', name: 'sypht-java-client', version: '1.3'
```

### SBT
```SBT
// https://mvnrepository.com/artifact/com.sypht/sypht-java-client
libraryDependencies += "com.sypht" % "sypht-java-client" % "1.3"
```

### Ivy
```Ivy
<!-- https://mvnrepository.com/artifact/com.sypht/sypht-java-client -->
<dependency org="com.sypht" name="sypht-java-client" rev="1.3"/>
```


## Usage
Populate these system environment variables with the credentials generated above:

```Bash
SYPHT_API_KEY="<client_id>:<client_secret>"
```

or

```Bash
OAUTH_CLIENT_ID="<client_id>"
OAUTH_CLIENT_SECRET="<client_secret>"
```

then invoke the client with a file of your choice:
```Java
SyphtClient client = new SyphtClient();
System.out.println(
        client.result(
                client.upload(
                        new File("receipt.pdf"))));
```

## License
The software in this repository is available as open source under the terms of the [Apache License](https://github.com/sypht-team/sypht-java-client/blob/master/LICENSE).

## Code of Conduct
Everyone interacting in the project’s codebases, issue trackers, chat rooms and mailing lists is expected to follow the [code of conduct](https://github.com/sypht-team/sypht-java-client/blob/master/CODE_OF_CONDUCT.md).
