package com.example.dormrepair.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.example.dormrepair.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class OssStorageService {

    private static final long SIGNED_URL_EXPIRE_MILLIS = 24 * 60 * 60 * 1000L;

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

    public String resolveImageUrl(String path) {
        if (!StringUtils.hasText(path)) {
            return path;
        }

        String objectKey = extractObjectKey(path);
        if (StringUtils.hasText(objectKey)) {
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            try {
                if (ossClient.doesObjectExist(bucketName, objectKey)) {
                    Date expiration = new Date(System.currentTimeMillis() + SIGNED_URL_EXPIRE_MILLIS);
                    URL signedUrl = ossClient.generatePresignedUrl(bucketName, objectKey, expiration);
                    return signedUrl.toString();
                }
            } finally {
                ossClient.shutdown();
            }
        }

        if (isLocalUploadPath(path)) {
            String normalizedPath = path.startsWith("/") ? path : "/" + path;
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(normalizedPath)
                    .toUriString();
        }

        return encodePublicUrl(path);
    }

    private String buildPublicUrl(String objectKey) {
        return "https://" + bucketName + "." + endpoint + "/" + encodeObjectKey(objectKey);
    }

    private String extractObjectKey(String path) {
        if (!StringUtils.hasText(path)) {
            return null;
        }
        String normalizedPrefix = normalizePrefix(dirPrefix);
        if (path.startsWith("http://") || path.startsWith("https://")) {
            URI uri = URI.create(path);
            String uriPath = uri.getRawPath();
            if (!StringUtils.hasText(uriPath) || "/".equals(uriPath)) {
                return null;
            }
            String objectKey = uriPath.startsWith("/") ? uriPath.substring(1) : uriPath;
            objectKey = URLDecoder.decode(objectKey, StandardCharsets.UTF_8);
            return objectKey.startsWith(normalizedPrefix) ? objectKey : null;
        }
        return path.startsWith(normalizedPrefix) ? path : null;
    }

    private boolean isLocalUploadPath(String path) {
        return StringUtils.hasText(path)
                && (path.startsWith("uploads/") || path.startsWith("/uploads/"));
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

    private String encodeObjectKey(String objectKey) {
        String[] segments = objectKey.split("/");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < segments.length; i++) {
            if (i > 0) {
                builder.append('/');
            }
            builder.append(encodeFileName(segments[i]));
        }
        return builder.toString();
    }

    private String encodePublicUrl(String path) {
        if (!StringUtils.hasText(path)) {
            return path;
        }
        try {
            URL url = new URL(path);
            String decodedPath = URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8);
            String normalizedPath = decodedPath.startsWith("/") ? decodedPath.substring(1) : decodedPath;
            String encodedPath = encodeObjectKey(normalizedPath);
            StringBuilder builder = new StringBuilder()
                    .append(url.getProtocol())
                    .append("://")
                    .append(url.getAuthority())
                    .append("/")
                    .append(encodedPath);
            if (StringUtils.hasText(url.getQuery())) {
                builder.append("?").append(url.getQuery());
            }
            return builder.toString();
        } catch (MalformedURLException e) {
            return path;
        }
    }
}
