package com.octal.actorPay.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;

@Component
public class UploadService {

	@Value("${S3Upload.access-key}")
	private String accessKey;

	@Value("${S3Upload.secret-key}")
	private String secretKey;

	@Value("${S3base.url}")
	protected String s3BaseURL;

	public String uploadFileToS3(MultipartFile filePart, String store) throws IllegalStateException, IOException {
		AWSCredentials credentials = new BasicAWSCredentials(accessKey,secretKey);
		AmazonS3 s3client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(Regions.AP_SOUTH_1).build();
		String bucketName = "s3-sbucket1";
		File file =  new File(System.getProperty("java.io.tmpdir")+"/"+filePart.getOriginalFilename());
		filePart.transferTo(file);
		PutObjectResult response = s3client.putObject(
				bucketName, 
				store+"/"+file.getName(), 
				file
				);
		String s3Path = s3BaseURL + store+"/"+file.getName();
		System.out.println("S3 Path ###  " + s3Path);
		System.out.println("Response from S3 Bucket " + response.getMetadata().getContentMD5());
		return s3Path;
	}

	public void deleteFile(String key) throws IllegalStateException, IOException {
		try {
			AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
			AmazonS3 s3client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
					.withRegion(Regions.AP_SOUTH_1).build();
			String bucketName = "s3-sbucket1";
			s3client.deleteObject(bucketName, key);
		}catch (Exception e) {
			System.out.println(e.getMessage() + "Unable to delete product");
		}
	}

	public String uploadSaasFileToS3(MultipartFile filePart, String store) throws IllegalStateException, IOException {
		AWSCredentials credentials = new BasicAWSCredentials(accessKey,secretKey);
		AmazonS3 s3client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(Regions.AP_SOUTH_1).build();
		String bucketName = "s3-sbucket1";
		File file =  new File(System.getProperty("java.io.tmpdir")+"/"+filePart.getOriginalFilename());
		filePart.transferTo(file);
		PutObjectResult response = s3client.putObject(
				bucketName,
				store+"/"+file.getName(),
				file
		);
		String s3Path = s3BaseURL + store+"/"+file.getName();
		System.out.println("S3 Path ###  " + s3Path);
		System.out.println("Response from S3 Bucket " + response.getMetadata().getContentMD5());

		// Set the presigned URL to expire after one hour.
		java.util.Date expiration = new java.util.Date();
		long expTimeMillis = Instant.now().toEpochMilli();
		expTimeMillis += 10000 * 60;
		expiration.setTime(expTimeMillis);

		// Generate the presigned URL.
		System.out.println("Generating pre-signed URL.");
		GeneratePresignedUrlRequest generatePresignedUrlRequest =
				new GeneratePresignedUrlRequest(bucketName, store+"/"+file.getName())
						.withMethod(HttpMethod.GET)
						.withExpiration(expiration);
		URL url = s3client.generatePresignedUrl(generatePresignedUrlRequest);
		return url.toExternalForm();
	}

}
