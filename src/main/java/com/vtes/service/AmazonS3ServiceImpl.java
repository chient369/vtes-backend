package com.vtes.service;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;

import lombok.extern.slf4j.Slf4j;

/*
 * Author : Chien@vti
 * Date : 2023/05/20
 * */

@Service
@Slf4j
public class AmazonS3ServiceImpl implements AmazonS3Service {

	@Autowired
	private AmazonS3 amazonS3;

	@Override
	public PutObjectResult upload(String bucketName, String fileName, Optional<Map<String, String>> optionalMetaData,
			InputStream inputStream) {
		ObjectMetadata objectMetadata = new ObjectMetadata();
		optionalMetaData.ifPresent(map -> {
			if (!map.isEmpty()) {
				map.forEach(objectMetadata::addUserMetadata);
			}
		});
		log.info("Path: {}, FileName: {} has uploaded successfully",bucketName,fileName );
		return amazonS3.putObject(bucketName, fileName, inputStream, objectMetadata);
	}

	public S3Object download(String bucketName, String fileName) {
		return amazonS3.getObject(bucketName, fileName);
	}
}
