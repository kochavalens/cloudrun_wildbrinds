package com.ms.consume.api.controller.services;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.springframework.stereotype.Service;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryException;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.CsvOptions;
import com.google.cloud.bigquery.Dataset;
import com.google.cloud.bigquery.DatasetInfo;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.LoadJobConfiguration;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardSQLTypeName;
import com.google.cloud.bigquery.TableId;

@Service
public class LoadBigQueryServiceImpl implements ILoadBigQueryService {

	public Object loadBigQuery(JSONArray filter, String nameFile, String nameBucket, String nameDataset, String nameTable) throws Exception {
		// TODO(developer): Replace these variables before running the sample.
		String datasetName = nameDataset;
		String tableName = nameTable;
		String sourceUri = "https://storage.cloud.google.com/" + nameBucket + "/" + nameFile;

		ArrayList<Field> field = new ArrayList<Field>();	
		for (int i = 0; i < filter.size(); i++) {
			field.add(Field.of(filter.get(i).toString().replace("#","_"), StandardSQLTypeName.STRING));			
		}
		Schema schema = Schema.of(field);
		loadCsvFromGcs(datasetName, tableName, sourceUri, schema);
		
		return schema;
	}

	public Object loadCsvFromGcs(String datasetName, String tableName, String sourceUri, Schema schema) {
		
		try {
			// Initialize client that will be used to send requests. This client only needs
			// to be created
			// once, and can be reused for multiple requests.
			BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();

			// Skip header row in the file.
			CsvOptions csvOptions = CsvOptions.newBuilder().setSkipLeadingRows(1).build();

			TableId tableId = TableId.of(datasetName, tableName);
			LoadJobConfiguration loadConfig = LoadJobConfiguration.newBuilder(tableId, sourceUri, csvOptions)
					.setSchema(schema).build();
			
			Dataset dataset = null;
			DatasetInfo datasetInfo = DatasetInfo.newBuilder(datasetName).build();
			
			dataset = bigquery.create(datasetInfo);
			// Load data from a GCS CSV file into the table
			Job job = bigquery.create(JobInfo.of(loadConfig));
			// Blocks until this load table job completes its execution, either failing or
			// succeeding.
			job = job.waitFor();
			if (job.isDone()) {
				System.out.println("CSV from GCS successfully added during load append job");
			} else {
				System.out.println(
						"BigQuery was unable to load into the table due to an error:" + job.getStatus().getError());
			}
		} catch (BigQueryException | InterruptedException e) {
			System.out.println("Column not added during load append \n" + e.toString());
		}
		
		return schema;
	}

}
