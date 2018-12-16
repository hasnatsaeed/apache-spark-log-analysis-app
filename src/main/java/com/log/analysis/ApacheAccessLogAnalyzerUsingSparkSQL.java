package com.log.analysis;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;

import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import com.esotericsoftware.minlog.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.log.analysis.model.LogAnalysisResultModel;
import com.log.analysis.model.LogLineModel;
import com.log.analysis.model.ServerEnpointAccessCountModel;
import com.log.analysis.model.HttpResponseCodeCountModel;

import io.netty.handler.codec.http2.Http2FrameReader.Configuration;
import scala.Tuple2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ApacheAccessLogAnalyzerUsingSparkSQL {

	private static Properties configuration = loadConfigraiton();

	private static LogAnalysisResultModel analysisResults = new LogAnalysisResultModel();

	public static void main(String[] args) {

		// Create Spark SQL session which is an instance of 'SparkSession'
		SparkSession spark = buildSqlSession();

		buildSqlView(spark);

		analyzeHttpResponseCodes(spark, analysisResults);

		analyzeTopServerEnpointsAccessed(spark, analysisResults);

		writeToFile(analysisResults);

		spark.stop();
	}

	private static void analyzeTopServerEnpointsAccessed(SparkSession spark, LogAnalysisResultModel logAnalysisResult) {
		Dataset<Row> results = spark
				.sql("SELECT ipAddress, COUNT(*) AS total FROM logs GROUP BY ipAddress ORDER BY total desc LIMIT 100");

		Dataset<ServerEnpointAccessCountModel> serverEnpointAccessCountAnalysis = results.map(
				row -> new ServerEnpointAccessCountModel(row.getString(0), row.getLong(1)),
				Encoders.bean(ServerEnpointAccessCountModel.class));

		logAnalysisResult.setServerEndpointAccessCountAnalysis(serverEnpointAccessCountAnalysis.collectAsList());

	}

	private static void analyzeHttpResponseCodes(SparkSession spark, LogAnalysisResultModel logAnalysisResult) {
		// Normal SQL can be run over our temporary view 'logs' that was created using
		// DataFrames
		Dataset<Row> results = spark.sql("SELECT responseCode, COUNT(*) FROM logs GROUP BY responseCode LIMIT 100");

		// The results of SQL queries are DataFrames and support all the normal RDD
		// operations
		// The columns of a row in the result can be accessed by field index or by field
		// name

		Dataset<HttpResponseCodeCountModel> httpResponseCodeCountAnalysis = results.map(
				row -> new HttpResponseCodeCountModel(row.getString(0), row.getLong(1)),
				Encoders.bean(HttpResponseCodeCountModel.class));

		logAnalysisResult.setHttpResponseCodeCountAnalysis(httpResponseCodeCountAnalysis.collectAsList());

	}

	private static SparkSession buildSqlSession() {
		return SparkSession.builder().appName(configuration.getProperty("spark.sql.appname"))
				.master(configuration.getProperty("spark.sql.master.url")).getOrCreate();
	}

	private static void writeToFile(LogAnalysisResultModel analysisResults) {
		ObjectMapper mapper = new ObjectMapper();

		try {

			mapper.writerFor(LogAnalysisResultModel.class)
					.writeValue(new File(configuration.getProperty("spark.sql.output.file.path")), analysisResults);
		} catch (JsonProcessingException ex) {
			Log.error("Error while writing to output file", ex);
		} catch (IOException ex) {
			Log.error("Error while writing to output file", ex);
		}
	}

	private static void buildSqlView(SparkSession spark) {
		// Load apache access log file as JavaRDD
		JavaRDD<String> accessLogsRDD = spark.sparkContext()
				.textFile(configuration.getProperty("spark.sql.accesslog.file.path"), 1).toJavaRDD().cache();

		StructType schema = buildSqlSchema();

		// Convert records of the access logs to Spark SQL Rows

		JavaRDD<Row> rowRDD = transformToSqlRows(accessLogsRDD);

		// Combine schema and data rows to create a DataSet
		Dataset<Row> logDF = spark.createDataFrame(rowRDD, schema);

		// Create temporary SQL view named 'logs' using the DataSet
		logDF.createOrReplaceTempView("logs");
	}

	private static JavaRDD<Row> transformToSqlRows(JavaRDD<String> accessLogsRDD) {
		return accessLogsRDD.map(record -> {
			LogLineModel logLine = LogLineModel.parseFromLogLine(record);
			return RowFactory.create(logLine.getClientIpAddress(), logLine.getClientId(), logLine.getUserId(),
					logLine.getDateTime(), logLine.getHttpMethod(), logLine.getServerEndpoint(),
					logLine.getHttpProtocol(), logLine.getHttpResponseCode(), logLine.getHttpContentSize());
		});
	}

	private static StructType buildSqlSchema() {
		// The column schema of the in memory table that will be generated. These are
		// the names of the columns separated by spaces.
		String schemaString = LogLineModel.sqlTableColumnSchema();

		// Convert each column name to an instance of StructField that describes a
		// column/field in the language of Spark SQL
		List<StructField> fields = new ArrayList<>();
		for (String fieldName : schemaString.split(" ")) {
			StructField field = DataTypes.createStructField(fieldName, DataTypes.StringType, true);
			fields.add(field);
		}

		// Generate schema definition using the instances of 'StructField'
		StructType schema = DataTypes.createStructType(fields);
		return schema;
	}

	private static Properties loadConfigraiton() {
		Properties configuration = new Properties();
		String configurationFileName = "configuration.properties";

		try {

			InputStream inputStream = ApacheAccessLogAnalyzerUsingSparkSQL.class.getClassLoader()
					.getResourceAsStream(configurationFileName);

			if (inputStream != null) {
				configuration.load(inputStream);
			} else {
				throw new FileNotFoundException(
						"property file '" + configurationFileName + "' not found in the classpath");
			}

			return configuration;
		} catch (FileNotFoundException ex) {
			throw new RuntimeException("property file '" + configurationFileName + "' not found in the classpath");
		} catch (IOException ex) {
			throw new RuntimeException("property file '" + configurationFileName + "' not found in the classpath");
		}

	}
}