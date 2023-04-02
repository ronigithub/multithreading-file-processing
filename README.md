# Multithreading file processing (More than 1 million data)

Boost the performance of the program where the file has more than 1 Million data and is stored in a database by batch processing and exporting the data into separate files (100K each)

## Deployment guideline

#### Step 1: Create a database (mysql) and replace the credentials (databaseName, username, password) application.properties files

#### Step 2: Install jdk 17 and maven

#### Step 3: Build jar file by run command ```mvn clean package```

#### Step 3: Navigate target folder and run command ```java -jar file-process-1.0.jar ```

Now will see the application is running on port http://localhost:8080 and database tables will be created

## API Documentation

### Upload Api 

URL Endpoint : http://localhost:8080/file/upload <br>
Request Type : POST <br>
Request Param: file <br>

Download sample file from here https://drive.google.com/file/d/1ODHlgplC5lhZorncmswz8HFsKBwLuTmf/view



### Export Api

URL Endpoint : http://localhost:8080/file/export <br>
Request Type : GET <br>

Result: Two valid customers text file will be created root directory with prefix "valid_customers_"

## Implementation Techniques
Upload Api: 
1. Used synchronized multithreading for process every line from content
2. Jpa batch process for bulk insert

Export Api:
1. Used multithreading for exported data to write into file
2. Total Execution Time: 14.62 second (1.2 Million data)