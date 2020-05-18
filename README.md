# big-data-hadoop-project

Hadoop does distributed processing for huge data sets across the cluster of commodity servers and works on multiple machines simultaneously. To process any data, the client submits data and program to Hadoop. HDFS stores the data while MapReduce process the data.

In this project, we will set up a single node Hadoop cluster and run the implementations of the algorithms below:
- WordCount Algorithm which counts the number of times a word appears in a file
- Average Computation Algorithm which calculates the average of a specific parameter
- Relative Frequency Computation Algorithm using three different approaches: Pair, Stripe and Hybrid

## Dependencies
- Docker version: v18
- Eclipse (for Scala) as IDE
- Java JDK 1.7

## Setup the project
Clone the repository to your machine and open the project in the IDE preferrably Eclipse IDE.

Download Hadoop Common and Hadoop MapReduce Client Core jar files from below links and add them to the project
[org.apache.hadoop/hadoop-common/2.7.3](org.apache.hadoop/hadoop-common/2.7.3)\
[org.apache.hadoop/hadoop-mapreduce-client-core/2.7.3](org.apache.hadoop/hadoop-mapreduce-client-core/2.7.3)

To be able to run the Jar file inside Hadoop, make sure you change your Java Environment to JDK 1.7. Thereafter export the project as a .war file. In the next steps, I will use the Jar file name as `wordcount.jar`


Install Hadoop Image on Docker
Ensure that you have docker installed using by running `docker -v` command. Else install Docker from official [website](https://docs.docker.com/install/)

Install Hadoop Image on Docker using `docker pull sequenceiq/hadoop-docker` command
After pulling the image, try to start the container using the following command:\
```
docker run -it sequenceiq/hadoop-docker:2.7.0 /etc/bootstrap.sh -bash
```

To check if everything is OK, open another terminal window and type:
```
docker ps
```

## Sharing files (JAR and Input Files) between your machine and the Docker container
For you the be able to run Hadoop commands, like hadoop jar, you need to copy some files from your local machine to that Docker Container containing Hadoop. To do this, you have to know its Container ID as it is randomly generated each time you run it. To see it, run`docker ps` in the new terminal different from the docker container terminal

The input files to be used for this project are in the files folder in the project. Download them to your local machine and copy them one by one to the hadoop container.
```
docker cp access_log.txt docker-container-id:/usr/local/hadoop
docker cp testData1.txt docker-container-id:/usr/local/hadoop
docker cp testData2.txt docker-container-id:/usr/local/hadoop
docker cp words.txt docker-container-id:/usr/local/hadoop
```

We also copy the project Jar file from wherever you saved it to the hadoop docker container
```
docker cp wordcount.jar docker-container-id:/usr/local/hadoop
```

## Executing Hadoop commands using Docker
After copying the required files to the container, now we can run the Hadoop commands inside the docker container terminal. We can close the new terminal we had opened and switch back to the docker terminal. First we change our current directory to the Hadoop container root directory where the files were copied to. 
```
cd /usr/local/hadoop
```

### Create the input folders from where the input files will be read for processing
Then we can execute hadoop commands using 'bin/hadoop' to create the project structure inside the Hadoop Distributed File System (HDFS). Please note that each time you terminate the Docker Container all your modifications to the HDFS are lost and you have to repeat all those steps!
```
bin/hadoop fs -ls  /
bin/hadoop fs -mkdir /input /input/apache
bin/hadoop fs -mkdir /input/relFrequency
bin/hadoop fs -mkdir /input/wordCount
```

Then copy the input files to the created directory. Remember that we have already copied them from the local directory to the hadoop container root directory
```
bin/hadoop fs -put testData* /input/relFrequency
bin/hadoop fs -put access_log.txt /input/apache
bin/hadoop fs -put words.txt /input/wordCount
```
You do not need to define output folder since it will be created by the JAR Application automatically when you run it with Hadoop.

## Running the Hadoop Application using the JAR file
We run different MapReduce programs using the same JAR file by specifying which input file, package name and class. 

### WordCount
The class `WordCount` within the package Part1 is an implementation the Word Count Algorithm which calculates the number of times a word appears in a file. The input for this algorithm is the words.txt file. Feel free to add more words. To obtain the word count 
```
bin/hadoop jar wordcount.jar Part1.InMapperWordCount /input/wordCount/words.txt /output/output0
``` 
To view the output of the Word Count Algorithm, run 
```
bin/hadoop fs -cat /output/output0/*
```

### InMapperWordCount
The class `InMapperWordCount` within the package Part1 implements the in-mapper combining version of the Word Count Algorithm. The In-Mapper basically ensures local aggregation of the hadoop iterables, which optimizes the running time since it has less write operations. To obtain the word count using the in-mapper combining version, run 
```
bin/hadoop jar wordcount.jar Part1.InMapperWordCount /input/wordCount/words.txt /output/output1
```
To view the output, run
```
bin/hadoop fs -cat /output/output1/*
```
The output will be the same as that without the in-mapper but with a lesser processing time and mapper output records

### Average
The class `Average` within the package Part1 implements an Average Computation Algorithm to compute the average of the "last quantity" in an Apache access log file for each IP Address ("the first quantity) using Hadoop MapReduce. The input file used for this algorithm is the access_log.txt file.
To obtain the average of the "last quantity" for each IP address, run 
```
bin/hadoop jar wordcount.jar Part1.Average /input/apache/*  /output/output2
```
To view of the  Average Computation Algorithm, run 
```
bin/hadoop fs -cat /output/output2/*
```

### InMapperAverage
The class `InMapperAverag` within the package Part1 implements the in-mapper combining version of the Average Computation Algorithm with the same input.
To obtain the average using the in-mapper combining version, run 
```
bin/hadoop jar wordcount.jar Part1.InMapperAverage /input/apache/*  /output/output3
```
View the output by running 
```bin/hadoop fs -cat /output/output3/*
```
The output will be the same as that without the in-mapper but with less processing time.


## Relative Frequency Computation
Next you will create a crystal ball to predict events that may happen once a certain event happened. For this project we assume testData1.txt and testData1.txt contain historical customer data of two different customers. Each record contains the productIDs of all the product bought by the customer. We want to predict the likelihood of buying a product with product ID B after they have bought a product with productID A. 

We can predict this from the relative frequencies of productID B in the window of productID A. Let the window of X, N(X) be set of all productIDs after X and before the next X. In the following Relative Frequency Algorithms, we will use two files: testData1.txt and testData2.txt as our input data. Each file is a record containing the product IDs of all the product bought by a single customer. The prodcutIDs are listed in the order the customer bought the products.

### Pair Approach
In package Part2_PairApproach there is an implementation of the `Pair` approach to calculate the relative frequencies. 
To to generate the relative frequencies for each productID using the Pair approach, run
```
bin/hadoop jar wordcount.jar Part2_PairApproach.Main /input/relFrequency/* /output/output4
```
View the output by running
```
bin/hadoop fs -cat /output/output4/*
```
   
### Stripe Approach
In the package Part2_StripeApproach we have an implementation of the `Stripe` approach to calculate the relative frequencies.
To generate the relative frequencies for each productID using the Stripe approach, run 
```
bin/hadoop jar wordcount.jar Part2_StripeApproach.Main /input/relFrequency/* /output/output5
```
View the output by running 
```
bin/hadoop fs -cat /output/output5/*
```

### Hybrid Approach
In the package Part2_HybridApproach we implement a `Hybrid` approach that calculates the relative frequencies by combining the Pair approach in the Mapper class with the Stripe approach in the Reducer class. To calculate the relative frequency using the Hybrid Approach, run
```
bin/hadoop jar wordcount.jar Part2_HybridApproach.Main /input/relFrequency/* /output/output6
```
View the output by running
```
bin/hadoop fs -cat /output/output6/*
```

On the whole, the Hybrid approach performs best followed by the Stripe approach
