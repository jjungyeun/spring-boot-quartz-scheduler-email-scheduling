# Spring Boot Quartz Scheduler Example: Building an Email Scheduling app

**Complete Tutorial:** https://www.callicoder.com/spring-boot-quartz-scheduler-email-scheduling-example/

## Requirements

1. Java - 11

2. Maven - 3.x.x

3. MySQL - 5.x.x

## Steps to Setup

**1. Clone the application**

```bash
git clone https://github.com/callicoder/spring-boot-mysql-rest-api-tutorial.git
```

**2. Create MySQL database**

```bash
create database quartz_demo
```

**3. Change MySQL username and password as per your MySQL installation**

open `src/main/resources/application.properties`, and change `spring.datasource.username` and `spring.datasource.password` properties as per your mysql installation


**4. Setup Spring Mail**

The project is using Gmail's SMTP server for sending emails. Whether you use Gmail or any other SMTP server, you'll need to configure the following mail properties accordingly -

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=rajeevc217@gmail.com
spring.mail.password=
```

If you're using Gmail, you need to allow the third party apps to send emails by following the instructions below -

+ Go to https://myaccount.google.com/security?pli=1#connectedapps
+ Set ‘Allow less secure apps’ to YES

**5. Create Quartz Tables**

The project stores all the scheduled Jobs in MySQL database. You'll need to create the tables that Quartz uses to store Jobs and other job-related data. Please create Quartz specific tables by executing the `quartz_tables.sql` script located inside `src/main/resources` directory.

```bash
mysql> source <PATH_TO_QUARTZ_TABLES.sql>
```

**6. Build and run the app using maven**

Finally, You can run the app by typing the following command from the root directory of the project -

```bash
mvn spring-boot:run
```

## Scheduling an Email using the /scheduleEmail API

```bash
curl -i -H "Content-Type: application/json" -X POST \
-d '{ "message": "Hello World!", "dateTime": "2022-02-18T15:46:00", "timeZone": "Asia/Seoul" }' \
http://localhost:8080/scheduleMessage

# Output
{ "success": true, "jobId": "a5f04ad0-dc45-4d29-bec4-2eac5b7c6310", "jobGroup": "test-jobs", "message": "Message Scheduled Successfully!" }
```