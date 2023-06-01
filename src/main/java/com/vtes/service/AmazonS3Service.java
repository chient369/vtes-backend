package com.vtes.service;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
/*
 * Author : Chien@vti
 * Date : 2023/05/20
 * - Define method access to aws s3
 * */
public interface AmazonS3Service {
	public PutObjectResult upload(
            String path,
            String fileName,
            Optional<Map<String, String>> optionalMetaData,
            InputStream inputStream);

    public S3Object download(String path, String fileName);
}
