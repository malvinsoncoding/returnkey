# returnkeyapp

## Details

- This application uses Postgresql as database
  - For more information regarding the database connection, username, etc. can refer to file application-dev.yml or application-prod.yml
  - database name: r_key
- The database tables will be automatically created while running the application
- Port of the application is :8080

## Execute API

To create pending task and get the generated token from Postman, run:

```
http://localhost:8080/api/pending/returns

Method: POST
Params: orderId, emailAddress
```

To create pending task and get the generated token from cURL, run:

```
curl --location -g --request POST 'http://localhost:8080/api/pending/returns?orderId={orderId}&emailAddress={emailAddress}'
```

To add the Return from Postman, run:

```
http://localhost:8080/api/returns

Method: POST
Params: generatedToken
```

To add the Return from cURL, run:

```
curl --location -g --request POST 'http://localhost:8080/api/returns?generatedToken={generatedToken}'
```

To get the Return from Postman, run:

```
http://localhost:8080/api/returns/:id

Method: GET
Params: id
```

To get the Return from cURL, run:

```
curl --location -g --request GET 'http://localhost:8080/api/returns/{id}'
```

To update QC Status from Postman, run:

```
http://localhost:8080/api/returns/:id/items/:itemId/qc/status

Method: PUT
PathVariable: id, itemId
Param: qcStatus (ACCEPTED, REJECTED)
```

To update QC Status from cURL, run:

```
curl --location -g --request PUT 'http://localhost:8080/api/returns/{id}/items/{itemId}/qc/status?qcStatus={qcStatus}'
```

## Development

To start your application in the dev profile, run:

```
./mvnw
```

## Building for production

### Packaging as jar

To build the final jar and optimize the returnkeyapp application for production, run:

```
./mvnw -Pprod clean verify
```

To ensure everything worked, run:

```
java -jar target/*.jar
```

### Packaging as war

To package your application as a war in order to deploy it to an application server, run:

```
./mvnw -Pprod,war clean verify
```

## Testing

To launch your application's tests, run:

```
./mvnw verify
```

For more information, refer to the [Running tests page][].

### Code quality

Sonar is used to analyse code quality. You can start a local Sonar server (accessible on http://localhost:9001) with:

```
docker-compose -f src/main/docker/sonar.yml up -d
```

run a Sonar analysis:

```
./mvnw -Pprod clean verify sonar:sonar
```

If you need to re-run the Sonar phase, please be sure to specify at least the `initialize` phase since Sonar properties are loaded from the sonar-project.properties file.

```
./mvnw initialize sonar:sonar
```

## Using Docker to simplify development (optional)

```
docker-compose -f src/main/docker/postgresql.yml up -d
```

To stop it and remove the container, run:

```
docker-compose -f src/main/docker/postgresql.yml down
```

You can also fully dockerize your application and all the services that it depends on.
To achieve this, first build a docker image of your app by running:

```
./mvnw -Pprod verify jib:dockerBuild
```

Then run:

```
docker-compose -f src/main/docker/app.yml up -d
```
