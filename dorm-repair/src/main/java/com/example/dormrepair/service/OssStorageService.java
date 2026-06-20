package com.example.dormrepair.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.example.dormrepair.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class OssStorageService {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    @Value("${aliyun.oss.dir-prefix:uploads/}")
    private String dirPrefix;

    public String uploadImage(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String safeFileName = StringUtils.hasText(originalFilename)
                ? originalFilename.replace("\\", "_").replace("/", "_")
                : "image";
        String objectKey = normalizePrefix(dirPrefix)
                + UUID.randomUUID().toString().replace("-", "")
                + "_"
                + safeFileName;

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try (InputStream inputStream = file.getInputStream()) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            if (StringUtils.hasText(file.getContentType())) {
                metadata.setContentType(file.getContentType());
            }
            metadata.setContentDisposition("inline;filename*=UTF-8''" + encodeFileName(safeFileName));
            ossClient.putObject(bucketName, objectKey, inputStream, metadata);
            return buildPublicUrl(objectKey);
        } catch (IOException e) {
            throw new BusinessException("图片上传失败");
        } finally {
            ossClient.shutdown();
        }
    }

    public void deleteByPath(String path) {
        String objectKey = extractObjectKey(path);
        if (!StringUtils.hasText(objectKey)) {
            throw new BusinessException("非法文件路径");
        }

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            if (ossClient.doesObjectExist(bucketName, objectKey)) {
                ossClient.deleteObject(bucketName, objectKey);
            }
        } finally {
            ossClient.shutdown();
        }
    }

    private String buildPublicUrl(String objectKey) {
        return "https://" + bucketName + "." + endpoint + "/" + objectKey;
    }

    private String extractObjectKey(String path) {
        if (!StringUtils.hasText(path)) {
            return null;
        }
        String normalizedPrefix = normalizePrefix(dirPrefix);
        if (path.startsWith("http://") || path.startsWith("https://")) {
            URI uri = URI.create(path);
            String uriPath = uri.getPath();
            if (!StringUtils.hasText(uriPath) || "/".equals(uriPath)) {
                return null;
            }
            String objectKey = uriPath.startsWith("/") ? uriPath.substring(1) : uriPath;
            return objectKey.startsWith(normalizedPrefix) ? objectKey : null;
        }
        return path.startsWith(normalizedPrefix) ? path : null;
    }

    private String normalizePrefix(String prefix) {
        if (!StringUtils.hasText(prefix)) {
            return "";
        }
        return prefix.endsWith("/") ? prefix : prefix + "/";
    }

    private String encodeFileName(String fileName) {
        return java.net.URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replace("+", "%20");
    }
}
