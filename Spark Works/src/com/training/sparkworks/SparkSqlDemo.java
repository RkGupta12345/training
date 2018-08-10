package com.training.sparkworks;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import static org.apache.spark.sql.functions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.training.commons.SparkConnection;

public class SparkSqlDemo {

	public static void main(String[] args) {
		Logger.getLogger("org").setLevel(Level.ERROR);
		Logger.getLogger("akka").setLevel(Level.ERROR);
		JavaSparkContext sparkContext= SparkConnection.getContext();
		
		SparkSession spSession=SparkConnection.getSession();
		Dataset<Row> empDataFields=spSession.read().json("./data/customerData.json");
		
		empDataFields.show();
		empDataFields.printSchema();
		
		// data queries
		
		System.out.println("Select Demo::::");
		
		empDataFields.select(col("name"),col("salary")).show();
		
		// data 
		
		System.out.println("Select Demo With Condition::::");
		empDataFields.filter(col("gender").equalTo("male")).show();
		
		System.out.println("Aggerate - group BY Gender:::::");
		empDataFields.groupBy(col("gender")).count().show();
		
		System.out.println("GroupBy deptId, average sal , Max Age:::::");
		
		Dataset<Row> summaryData=empDataFields
				.groupBy(col("deptid"))
				.agg(avg(empDataFields.col("salary")),max(empDataFields.col("age")));
		         summaryData.show();
		         
		         
//////////////////=================try with bean class------------//////////////////////////////////////
		         
		         Department dept1=new Department(100, "Development");
		         
		         Department dept2=new Department(200, "Testing");
		         
		         List<Department> deptList=new ArrayList<Department>();
		         
		         deptList.add(dept1);
		         deptList.add(dept2);
		         
		         Dataset<Row> deptDataFields=spSession.createDataFrame(deptList, Department.class);
		         
		         System.out.println("Department Contents Are::=>>>>");
		         deptDataFields.show();
		         /////=================join===========/////
		         System.out.println("Join Employee with department:::::");
		         
		        Dataset<Row> empDeptJoin= empDataFields.join(deptDataFields,col("deptid").equalTo(col("departmentId")));
		        empDeptJoin.show();
		        
		        /// gt 30 years, join employee with deptDataSet, groupby id, avg of salary and max of age
		        
		        System.out.println("=====Join With aggerate (Emp and Dept)=====");
		        
		        empDataFields.filter(col("age").gt(30)).join(deptDataFields, col("deptid").equalTo(col("departmentId"))).groupBy(col("deptid")).agg(avg(empDataFields.col("salary")),max(empDataFields.col("age"))).show();
		        
		        ///// to load data from csv/////////////////
		        System.out.println("::::::Read CSV Data:::::");
		        Dataset<Row> autoData=spSession.read().option("header","true").csv("./data/auto-data.csv");
		        autoData.show(5);
		        
		        /// Creating RDD with row objects//////
		        
		        System.out.println("Creating RDD with row objects::::");
		        
		        Row row1=RowFactory.create(1,"India","Bangalore");
		        Row row2=RowFactory.create(2,"USA","Reston");
		        Row row3=RowFactory.create(3,"UK","Steevenscreek");
		        
		        List<Row> rList=new ArrayList<Row>();
		        
		        rList.add(row1);
		        rList.add(row2);
		        rList.add(row3);
		        
		        JavaRDD<Row> rowRDD=sparkContext.parallelize(rList);
		        
		        StructType schema=DataTypes.createStructType(new StructField[] {
		        		
		        DataTypes.createStructField("id", DataTypes.IntegerType, false),
		        DataTypes.createStructField("country", DataTypes.StringType, false),
		        DataTypes.createStructField("city", DataTypes.StringType, false)
		        });
		        
		        Dataset<Row> tempDataFields=spSession.createDataFrame(rowRDD, schema);
		        tempDataFields.show();
		        
		        /// Working with csv data, with sql statement
		        //provided the data is kept in table like format
		        //the persistence will be only till the end if program
		        //meaning temporary
		        
		        autoData.createOrReplaceTempView("autos");
		        System.out.println("Temp table Contents::");
		        
		        System.out.println("Showing all fields with hp greater than 200:::");
		        spSession.sql("select * from autos where hp > 200").show();
		        
		        // To find make, maximum RPM from autos group by make
		        
		        System.out.println("To find make, maximum RPM from autos group by make");
		        
		        spSession.sql("select make, max(rpm) from autos"+" group by make order by 2 desc").show();
		        
		        // convert Datafram to JavaRDD
		        
		        JavaRDD<Row> autoRDD=autoData.rdd().toJavaRDD();
		        
		        // reading the data from MySql DB
		        
		        Map<String, String> jdbcConnectParams=new HashMap<String, String>();
		        
		        jdbcConnectParams.put("url","jdbc:mysql://localhost:3306/emp");
		        jdbcConnectParams.put("driver","com.mysql.jdbc.Driver");
		        jdbcConnectParams.put("dbtable","employee");
		        jdbcConnectParams.put("user", "root");
		       // jdbcConnectParams.put("password","SumanGupta@123");
		       System.out.println("Create a datafram froma DB table (EMP):::");
		       
		       Dataset<Row> sqldataFields=spSession.read().format("jdbc").options(jdbcConnectParams).load();
		       sqldataFields.show();
		        
		        
	}

}
