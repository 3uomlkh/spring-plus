package org.example.expert.client.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3Client;
    private final String bucketName = "my-gom-bucket";

    public String uploadFile(MultipartFile file, String dirName) throws IOException {
        String fileName = dirName + "/" + UUID.randomUUID() + extractExt(file.getOriginalFilename());

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .contentType(file.getContentType())
                        .build(),
                RequestBody.fromBytes(file.getBytes())
        );

        return getFileUrl(fileName);
    }

    public void deleteFile(String fileUrl) {
        String key = extractKey(fileUrl);

        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build()
        );
    }

    private String extractKey(String fileUrl) {
        return fileUrl.substring(fileUrl.indexOf("profile/"));
    }

    private String extractExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.'));
    }

    private String getFileUrl(String fileName) {
        return "https://" + bucketName + ".s3.ap-northeast-2.amazonaws.com/" + fileName;
    }
}
