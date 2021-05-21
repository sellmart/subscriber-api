# Netflix Subscriber API
Responsible for creating new subscribers and saving their payment methods.
Additionally, provides business intelligence into the number of subscribers entered using this API.

# Build & Run locally using Docker
## Build
**This API is built with JDK 16, if you do not have JDK 16 on your local machine please follow
the next steps to build & run using docker.**
* From your terminal window (Mac / Linux) or Docker CLI (for windows) navigate to the root of this project (i.e. subscriber-api/)
* If you do not have Docker, please navigate [here](https://www.docker.com/get-started) to download and install it for your system.
* Once you have Docker installed (or if you already have Docker installed) please run the below command
  in your terminal window (Mac/Linux) or if you are on Windows you can use the Docker CLI for Windows.

`docker build -t subscriber-api .`

This will pull required images (i.e. maven & jdk-16) to build and run the service. During the first time this process can take up to 5 minutes or more depending on your system.

## Run
Once the build is complete, you should see a message like `Successfully tagged subscriber-api:latest`, at this time
you can run the service using the below command. Please remember, if you need to change the default NETFLIX URL, just update
the value in the command below prior to executing `i.e. just replace the x values with your desired domain; NETFLIX_URL=xxxxxxxx`.

`docker run -it -p 8080:8080 -e "SPRING_PROFILES_ACTIVE=local" -e "NETFLIX_URL=nf-code-is-fun-1192605829.us-east-2.elb.amazonaws.com" subscriber-api`

Once the server is started, you should see a message like this in your terminal window (Tomcat started on port(s): 8080 (http) with context path '')
this indicates the service is ready and can be executed against.

# Build & Run locally using Maven
## Build
**If you already have JDK 16 on your machine OR if you would like to download and install it to run this service
please follow the below steps**
* If you DO NOT have JDK 16, you can download it from [here](https://www.azul.com/downloads/?os=macos&architecture=arm-64-bit&package=jdk)
  if you are on an M1 Mac or if you are on Windows or non M1 Mac you can download the jdk 16 version from
  [here](https://www.oracle.com/java/technologies/javase-jdk16-downloads.html).
* Once you have this version downloaded, please make sure you update your JAVA_HOME variable to the new version
* Once the environment variable is updated, please make sure you have Maven v3 installed on your machine, if you don't
  you can download and install it from [here](https://maven.apache.org/download.cgi).
* Lastly, please make sure you update your MAVEN_HOME environment variable to the new version (location) of the new install.
* You can now navigate to the root folder of this project (i.e. subscriber-api/).
* Once at the root of the project, please open your terminal (Mac/Linux) or command prompt (Windows) and
  execute the below command.

`mvn clean install`

This will build and download any dependencies the project needs to run.

## Run
Once the install process is complete you can proceed to starting the service to consume its resources.
Please execute the below command from your terminal window (Mac/Linux) or command prompt (Windows).
Please remember, if you need to change the default NETFLIX URL, just update
the value in the command below prior to executing `i.e. just replace the x values with your designed URL; NETFLIX_URL=xxxxxxxx`.

`mvn spring-boot:run -Dspring-boot.run.profiles=local -Dspring-boot.run.arguments=--NETFLIX_URL=nf-code-is-fun-1192605829.us-east-2.elb.amazonaws.com`

# NETFLIX_URL Parameter
The `NETFLIX_URL` property can be replaced in the commands above to any valid domain excluding
the protocol (i.e. remove http:// or https://).

# Assumptions when building this service
The instructions mentioned "You don’t need to use any persistent storage or worry about encryption.", while
I did use an in memory DB (H2), anytime the service is stopped, all data that was created during that session will
be lost on the subsequent start up.
Additionally, I did not implement encryption, however, I feel impelled to mention that if this was a real world
service, there are many PCI-compliant standards that has to be adhered to when dealing with sensitive data such as
credit card information. To stay somewhat true to those standards, anytime I saved CC info, I masked all but the last
4 numbers of the card. Also, if this were a real world solution, I would set up a service to pass only the PAN (no additional data)
and exchange for an OTP, then use that OTP with the other related data when exchanging because it is extremely sensitive.
Lastly, when the PAN is exchanged for an OTP, the assumption is that it would be encrypted at rest (in storage).

Another assumption made during implementation was regarding authentication and authorization. This was not discussed
in the instructions but as an Engineer it lives on the forefront of my mind. One can surmise that this service could be
consumed by an internal team to the company. Depending on the consuming apps trust level, we can either implement implicit / Auth code
grant auth flow (using an Auth server with this API acting as the resource server) or client credentials if the consuming
app has resides in a secure realm.

Next, another assumption I made is regarding the Luhn check on the card number. The instructions states
"The /api/v1/addPayment endpoint validates and adds the user’s payment method.", so reading this, I am assuming since
it does not validate the card network, the only other validation that can be done is the Luhn check. I was not completely
sure on this (as it is just an assumption) however, I still wrote the Luhn validation logic, however, I do not halt the flow
if the Luhn check fail, instead I log it, and allow the process to continue with the hope that the addPayment resource will
fail the request if the Luhn check failed (I did this because I noticed the addPayment service failed some valid card numbers
but passed others). Additionally, I assumed the addPayment resource validates the card expiry year is a valid year
( i.e. provided year >= current year ).

Lastly, in addition to checking that the requested card network is available, I also performed a validation to check that the
card network matched the card number that was provided in the request. For example, if a request is made with
VISA as the network, but a card number that starts with 6011, a validation rule will fire and fail the request with a 400
BAD REQUEST error as that is not a valid network/card number combination.
I know the instructions didn't directly specify this, but in the payment world that is a pretty standard check, and I know the
addPayment resource didn't perform the check because card network is not a constraint to this resource.
