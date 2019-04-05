## Sypht

Sypht is an service which extracts key fields from documents. For example, you can upload an image or pdf of a bill or invoice and extract the amount due, due date, invoice number and biller information.

Pixels in, json out.

Checkout [sypht.com](https://sypht.com) for more details.

### API

Sypht provides a REST api for interaction with the service. Full documentation is available at: [docs.sypht.com](https://docs.sypht.com/).
This repository is an open-source Java reference client implementation for working with the API.

### Getting started

To get started you'll need some API credentials, i.e. a `client_id` and `client_secret`.
You need to register for an account at https://www.sypht.com/signup/developer to generate these first.


### Prerequisites
JDK8 and upwards are supported.


### Installation
Sypht Java Client is available on maven central:

```
<dependency>
  <groupId>com.sypht</groupId>
  <artifactId>sypht-java-client</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

### Usage
Populate these system environment variables with the credentials generated above:

```
OAUTH_CLIENT_ID="client_id"
OAUTH_CLIENT_SECRET="client_secret"
```

then invoke the client with a file of your choice:
```Java
SyphtClient client = new SyphtClient();
String uuid = client.upload(new File("mytaxireceipt.pdf"));
System.out.println(client.result(uuid));
```
