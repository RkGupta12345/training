package com.training.sparkworks;

import java.util.Arrays;
import java.util.Iterator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;

import com.training.commons.DataSource;
import com.training.commons.SparkConnection;
import com.training.commons.Utilities;

public class SparkOperationsClient {

	public static void main(String[] args) {
		Logger.getLogger("org").setLevel(Level.ERROR);
		//Logger.getLogger("akka").setLevel(Level.ERROR);
		JavaSparkContext sparkContext= SparkConnection.getContext();
		
		//start loading the data
		//1. load the collection and cache it
		JavaRDD<Integer> collData=DataSource.getCollData();
		
		System.out.println("Total No Of Records::=>"+collData.count());
		
		//2. load the file and cache it
		
		JavaRDD<String> autoDataContent=sparkContext.textFile("./data/auto-data.csv");
		
		
		System.out.println("No oF Records=>"+autoDataContent.count());
		
		//printing 5 lines of data
		
		System.out.println("Loading data from file");
		System.out.println("===========================");
		
		//autoDataContent.take(5).forEach(System.out::println);
		Utilities.printStringRDD(autoDataContent, 10);
		
		// storing RDD's
		
		
		//autoDataContent.saveAsTextFile("data/auto-data-modified.csv");
		
		// Spark Transformation
		
		JavaRDD<String> tsvData=autoDataContent.map(str->str.replace(",", "\t"));
		Utilities.printStringRDD(tsvData, 5);
		
		////////////////////Filter Example/////////////////////
		// to remove header
		
		String header=autoDataContent.first();
		
		JavaRDD<String> autoDataWithOutHeader=autoDataContent.filter(s->!s.equals(header));
		
		Utilities.printStringRDD(autoDataWithOutHeader, 5);
		
		// filter those record which has only Toyota Car
		
		JavaRDD<String> toyotaData=autoDataContent.filter(str->str.contains("toyota"));
		
		System.out.println("=====Only Toyota Car=====");
		
		Utilities.printStringRDD(toyotaData, 5);
		
		// unique record
		
		System.out.println("=====Distinct Record======");
		
		JavaRDD<String> distinctRecord=autoDataContent.distinct();
		
		Utilities.printStringRDD(distinctRecord,(int) distinctRecord.count());
		
		// To count no of words in RDD
		
		System.out.println("----Using FlatMap-----------");
		
		JavaRDD<String> words=toyotaData.flatMap(new FlatMapFunction<String, String>() {

			@Override
			public Iterator<String> call(String t) throws Exception {
				
				return Arrays.asList(t.split(",")).iterator();
			}
		});
		
		System.out.println("ToyotaRDD Words Count=>"+words.count());
		
		// after cleansing the data
		
		System.out.println("=====after cleansing the data=====");
		
		JavaRDD<String> cleanseRDD=autoDataContent.map(new ClenseRDDCars());
		
		Utilities.printStringRDD(cleanseRDD, 5);
		
		// set Operations
		
		JavaRDD<String> words1=sparkContext.parallelize(Arrays.asList("hello","how","are","you","today"));
		
		JavaRDD<String> words2=sparkContext.parallelize(Arrays.asList("hello","how","were","yesterday"));
		
		System.out.println(":::::::::Union Operation Set::::::::");
		Utilities.printStringRDD(words1.union(words2), 9);
		
		System.out.println(":::::Intersaction Operation Set::::::");
		
		Utilities.printStringRDD(words1.intersection(words2), 9);
		
		// Find Sum of number in the given RDD
		
		System.out.println("Sum::::");
		
		Integer collDataCount=collData.reduce((x,y)->x+y);
		
		System.out.println("Sum Of Given Integers::=>"+collDataCount);
		
		//////////////////Average////////////////////////////
		//JavaRDD<Integer> totalMPG=autoDataWithOutHeader.map(new MPGWorks());
	}

}
