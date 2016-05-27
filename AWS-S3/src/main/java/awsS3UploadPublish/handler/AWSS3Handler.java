/**
 * 
 */
package awsS3UploadPublish.handler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
/**
 * @author sumon_m
 *
 */
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class AWSS3Handler {

	/*@Value("${SUFFIX}")
	//@NotNull
	private String SUFFIX;
	
	@Value("${bucketName:sumonbucketpoc1}")
	//@NotNull
	private String bucketName;
	
	@Value("${folderName:testfolder}")
	//@NotNull
	private String folderName;
	
	@Value("${fileName_key:folderName/ToDo.txt}")
	//@NotNull
	private String fileName_key;
	
	@Value("${fileTouse}")
	//@NotNull
	String fileTouse="C:\\D_Drive\\temp\\ToDo.txt";
	
	private File file = new File(fileTouse);
	
	@Value("${accessKeyID:AKIAI7XHTAFKSO5BWHKA}")
	//@NotNull
	private String accessKeyID;
	
	@Value("${secretAccessKey:qmSEJJMSVWWd6OFaU+6uXDEj7a33qQ7CqhBdaMKm}")
	//@NotNull
	private String secretAccessKey;
	*/
	
	
	private static final String SUFFIX = "/";
	String bucketName = "sumonbucketpoc1";
	String folderName = "testfolder";
	String fileName_key = folderName + SUFFIX + "ToDo.txt";
	File file = new File("C:\\D_Drive\\temp\\ToDo.txt");
	String accessKeyID = "AKIAI7XHTAFKSO5BWHKA";
	String secretAccessKey = "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";
	private java.util.Date validfor = new Date(System.currentTimeMillis() + 6 * 60 * 1000);

	
	
	
	public void process() {
		
		System.out.println(accessKeyID+"--------"+ secretAccessKey);
		
		
		
		
		// credentials object identifying user for authentication
		// user must have AWSConnector and AmazonS3FullAccess for this
		AWSCredentials credentials = new BasicAWSCredentials(accessKeyID, secretAccessKey);

		// create a client connection based on credentials
		AmazonS3 s3client = new AmazonS3Client(credentials);

		// create bucket - name must be unique for all S3 users

		System.out.println("Creating a bucket, named: " + bucketName);
		s3client.createBucket(bucketName);

		// list buckets
		System.out.println("List of Buckets:");
		for (Bucket bucket : s3client.listBuckets()) {
			System.out.println(" - " + bucket.getName());
		}

		// create folder into bucket
		System.out.println("Creating folder into the bucket.");
		createFolder(bucketName, folderName, s3client);

		// upload file to folder and set it to public
		System.out.println("Uploading file to the specific folder.");
		// s3client.putObject(new PutObjectRequest(bucketName, fileName_key,
		// file).withCannedAcl(CannedAccessControlList.PublicRead));
		PutObjectRequest putObjetRequest = new PutObjectRequest(bucketName, fileName_key, file);
		ObjectMetadata objectMetaData = new ObjectMetadata();
		objectMetaData.setContentLength(file.length());
		putObjetRequest.withMetadata(objectMetaData);
		putObjetRequest.withCannedAcl(CannedAccessControlList.PublicRead);
		s3client.putObject(putObjetRequest);

		// publish to use the file
		System.out.println("Publish the url for the file.");
		URL url = s3client.generatePresignedUrl(bucketName, fileName_key, validfor);
		System.out.println("Access the file: " + url);

		// download the file
		System.out.println("Downloading the file");
		S3Object object = s3client.getObject(bucketName, fileName_key);
		try {
			downloadFile(object);
		} catch (IOException e) {
			System.out.println("Error in Download" + e.getMessage());
		}

		// delete folder
		System.out.println("Deleting folder recursively.");
		deleteFolder(bucketName, folderName, s3client);

		// deletes bucket
		System.out.println("Deleating Bucket.");
		s3client.deleteBucket(bucketName);

	}

	private void createFolder(String bucketName, String folderName, AmazonS3 client) {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);
		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
		// create a PutObjectRequest passing the folder name suffixed by /
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, folderName + SUFFIX, emptyContent,
				metadata);
		// send request to S3 to create folder
		client.putObject(putObjectRequest);
	}

	/*
	 * This method first deletes all the files in given folder and than the
	 * folder itself
	 */
	private void deleteFolder(String bucketName, String folderName, AmazonS3 client) {
		List fileList = client.listObjects(bucketName, folderName).getObjectSummaries();
		for (int i = 0; i < fileList.size(); i++) {
			S3ObjectSummary objectSummary = (S3ObjectSummary) fileList.get(i);
			client.deleteObject(bucketName, objectSummary.getKey());
		}
		client.deleteObject(bucketName, folderName);
	}

	private void downloadFile(S3Object object) throws IOException {
		InputStream reader = new BufferedInputStream(object.getObjectContent());
		File file = new File("C:\\D_Drive\\temp\\ToDo_Downloaded.txt");
		OutputStream writer = new BufferedOutputStream(new FileOutputStream(file));

		int read = -1;

		while ((read = reader.read()) != -1) {
			writer.write(read);
		}

		writer.flush();
		writer.close();
		reader.close();
	}

}