package com.example.dormrepair.controller;

import com.example.dormrepair.common.result.Result;
import com.example.dormrepair.service.OssStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final Logger log = LoggerFactory.getLogger(UploadController.class);
    private static final int MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final String[] ALLOWED_TYPES = {"image/jpeg", "image/png", "image/gif", "image/webp"};

    @Autowired
    private OssStorageService ossStorageService;

    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        log.info("调用接口: POST /api/upload/image, fileName={}", file.getOriginalFilename());

        if (file.getSize() > MAX_FILE_SIZE) {
            return Result.error("文件大小不能超过5MB");
        }

        boolean allowed = false;
        for (String type : ALLOWED_TYPES) {
            if (type.equalsIgnoreCase(file.getContentType())) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            return Result.error("只支持 JPG/PNG/GIF/WEBP 格式");
        }

        try {
            String imageUrl = ossStorageService.uploadImage(file);
            log.info("图片上传成功: {}", imageUrl);
            return Result.success(imageUrl);
        } catch (Exception e) {
            log.error("图片上传失败", e);
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/image")
    public Result<?> deleteImage(@RequestParam("path") String path) {
        log.info("调用接口: DELETE /api/upload/image, path={}", path);
        try {
            ossStorageService.deleteByPath(path);
            log.info("图片删除成功: {}", path);
            return Result.success();
        } catch (Exception e) {
            log.error("图片删除失败", e);
            return Result.error("删除失败: " + e.getMessage());
        }
    }
}
