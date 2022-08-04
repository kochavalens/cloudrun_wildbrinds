package com.ms.consume.api.controller.services;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageClass;
import com.google.cloud.storage.StorageOptions;

@Service
public class UploadBucketServiceImpl implements IUploadBucketService {
	
	private static Logger log = LoggerFactory.getLogger(UploadBucketServiceImpl.class);
	
	public Object uploadBucket(String nameFile, String nameBucket, String csv) {
		
		Credentials credentials = null;
		//credentials = GoogleCredentials.fromStream(new FileInputStream("c:/bbdd-wild-lama-b1759cfc195e.json"));
		Storage storage = StorageOptions.newBuilder().setProjectId("bbdd-wild-lama").build().getService();
		//Storage storage = StorageOptions.newBuilder().setCredentials(credentials).setProjectId("bbdd-wild-lama").build().getService();
		
		String contentType = "application/csv";
		StorageClass storageClass = StorageClass.STANDARD;
		storage = StorageOptions.getDefaultInstance().getService();
		Bucket bucket = storage.create(BucketInfo.of(nameBucket));
		String value = csv;
		byte[] bytes = value.getBytes();
		Blob blob = bucket.create(nameFile, bytes, contentType);
		return nameFile;
		
	}
}
