# kafka-tests

Small project to test Kafka resiliency with Akka Streams exposed in this pres :
https://talend365-my.sharepoint.com/personal/achever_talend_com/_layouts/15/onedrive.aspx?id=%2Fpersonal%2Fachever_talend_com%2FDocuments%2Fkafka-resiliency

## 2 Endpoints exposed
curl -X POST http://localhost:8000/send
Send a message to Kafka through akka streams

curl -X POST http://localhost:8000/close
End the akka streams pipeline

## Launch
sbt kafka-tests-svc/run
