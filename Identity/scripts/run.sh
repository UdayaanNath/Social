#!/bin/sh

exec java -Xms128m -Xmx128m -Xss256k -XX:+UseSerialGC -XX:MaxMetaspaceSize=96m -jar target/identity-1.0-SNAPSHOT.jar server base.conf
