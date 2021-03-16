package com.viettel.etc.services.impl;

import com.viettel.etc.services.FileService;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.utils.exceptions.GlobalExceptionHandler;
import io.minio.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
public class FileServiceImpl implements FileService {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Override
    public void uploadFile(String filePath, byte[] file) {
        try {
            if (filePath.startsWith("/")) {
                filePath = filePath.substring(1);
            }
            //String filePath = String.format("%s/%s/%s", customerId, contractId, fileName);
            createBucketIfNotExist(bucket, false);
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucket).object(filePath).stream(new ByteArrayInputStream(file), file.length, -1).build());
        } catch (Exception e) {
            LOG.error("HAS ERROR", e);
            throw new EtcException("crm.file.upload.error");
        }
    }

    @Override
    public void removeFile(String filePath) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucket).object(filePath).build());
        } catch (Exception e) {
            LOG.error("HAS ERROR", e);
            throw new EtcException("crm.file.remove.error");
        }
    }

    @Override
    public byte[] getFile(String filePath) {
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(filePath)
                        .build())) {
            return IOUtils.toByteArray(stream);
        } catch (Exception e) {
            LOG.error("HAS ERROR", e);
            throw new EtcException("crm.file.get.error");
        }
    }


    private void createBucketIfNotExist(String bucketName, boolean objectLock) {
        try {
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!isExist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).objectLock(objectLock).build());
            }
        } catch (Exception e) {
            LOG.error("HAS ERROR", e);
            throw new EtcException("crm.file.bucket.error");
        }
    }
}
