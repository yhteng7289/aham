package com.pivot.aham.common.core.util;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;

import java.io.File;
import java.io.InputStream;

public class AwsUtil {

    private static String AWS_ACCESS_KEY = PropertiesUtil.getString("AWS_ACCESS_KEY");
    private static String AWS_SECRET_KEY = PropertiesUtil.getString("AWS_SECRET_KEY");
    private static String bucketName = PropertiesUtil.getString("BUCKET_NAME");
    private static AmazonS3 s3;

    public static void initAws() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setSocketTimeout(50000);
        s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion(Regions.AP_SOUTHEAST_1)
                .withPathStyleAccessEnabled(true)
                .withClientConfiguration(clientConfiguration)
                .build();

    }

    public static void uploadFile(String uploadFilePath, File tempFile) {
        initAws();
        s3.putObject(bucketName, uploadFilePath, tempFile);
    }

    public static S3Object downloadFile(String uploadFilePath) {
        initAws();
        return s3.getObject(bucketName, uploadFilePath);
    }

    public static void uploadFile(String uploadFilePath, InputStream inputStream) {
        initAws();
        s3.putObject(bucketName, uploadFilePath, inputStream,null);
    }
}
