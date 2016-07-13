### Sample code from the Advanced Java Concurrency training

The code showcases different scenarios for multithreaded processing of some simple logic using synchronizations, locks, and different classes from java.util.concurrent..
The video for the training in Russian language can be found here: https://www.youtube.com/playlist?list=PL6jg6AGdCNaXo06LjCBmRao-qJdf38oKp

The code has some unit tests that validate the logic.

There are also some classes with `main` function in them that output information to console. You can call them in the following way:
- `mvn exec:java -Dexec.mainClass="ua.yet.adv.java.concurrency.OperationsDeadlock"`
- `mvn exec:java -Dexec.mainClass="ua.yet.adv.java.concurrency.OperationsService"`
- `mvn exec:java -Dexec.mainClass="ua.yet.adv.java.concurrency.OperationsCompletableFuture"`
- `mvn exec:java -Dexec.mainClass="ua.yet.adv.java.concurrency.OperationsSemaphore"`
