package com.neoflies.mystackoverflowapi.utils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class AwsS3Utils {
  @Autowired
  ApplicationProperties applicationProperties;

  private AmazonS3 s3Client;

  @PostConstruct
  private void initializeAwsClient() {
    AWSCredentials awsCredentials = new BasicAWSCredentials(
      this.applicationProperties.getAws().getAccessKey(),
      this.applicationProperties.getAws().getSecretKey()
    );

    this.s3Client = AmazonS3ClientBuilder
      .standard()
      .withRegion(Regions.AP_SOUTHEAST_1)
      .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
      .build();
  }

  public String uploadFile(MultipartFile file, String filename) throws IOException {
    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentType(file.getContentType());
    objectMetadata.setContentLength(file.getSize());

    PutObjectRequest putObjectRequest = new PutObjectRequest(
      this.applicationProperties.getAws().getBucketName(),
      filename,
      file.getInputStream(),
      objectMetadata
    ).withCannedAcl(CannedAccessControlList.PublicRead);
    this.s3Client.putObject(putObjectRequest);

    String url = this.s3Client.getUrl(this.applicationProperties.getAws().getBucketName(), filename).toExternalForm();
    return url;
  }
}
