package com.training.sparkworks;

import java.util.Arrays;

import org.apache.spark.api.java.function.Function;

public class MPGWorks implements Function<String, Integer>{
	
	int totalMPGCity;
	int totalMPGHwy;

	@Override
	public Integer call(String v1) throws Exception {
		String[] attributeList=v1.split(",");
		 totalMPGCity=Integer.parseInt(attributeList[9]);
		 //totalMPGHwy=Integer.parseInt(attributeList[10]);
		return totalMPGCity;
		
	}
	public double getAverageMPGCity(int count) {
		return totalMPGCity/count;
		
	}

}
