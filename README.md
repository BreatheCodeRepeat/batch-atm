# BATCH ATM OPERATIONS PROCESSING PIPELINE

The pipeline is taking an input file and outputing the results of transactions . The code is written in Java with Spring Boot,
Spring Batch for reading unstructured text files and Spring State Machine for managing state.

## Installation

Clone the repo and run
```java
mvn spring-boot:run 
or
mvn clean install
```
in the root of the project.

## Usage

There are two files in the root of the project called transactions.txt and output.txt.
transactions.txt is used as an input file.
output.txt is the outputed transactions.
Both names of the files can be customized from application.yaml

Example transactions.txt
```
8000

12345678 1234 1234
500 100
B
W 105

87654321 4321 4321
100 0
W 10

87654321 4321 4321
0 0
W 10
B
```

Example output.txt

```
500
395
90
FUNDS_ERR
0

```

## Logic

The transactions can output 3 errors: 
ATM_ERR when ATM is empty,
FUNDS_ERR when the account doesn't have funds,
ACCOUNT_ERR when wrong pin was inserted

Using Spring Batch transaction.txt is read with a custom reader. This reader reads one session at a time where a session is bound by 2 blank lines.
State of the app is managed using Spring State Machine.
