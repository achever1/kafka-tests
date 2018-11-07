# kafka-tests

Small project to test Kafka resiliency with Akka Streams

## 2 Endpoints exposed
curl -X POST http://localhost:8000/send
Send a message to Kafka through akka streams

curl -X POST http://localhost:8000/close
End the akka streams pipeline

## Launch
sbt kafka-tests-svc/run
