package com.neoflies.mystackoverflowapi.controllers;

import com.neoflies.mystackoverflowapi.domains.File;
import com.neoflies.mystackoverflowapi.exceptions.BadRequestException;
import com.neoflies.mystackoverflowapi.utils.AwsS3Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/uploads")
public class UploadController {
  public static final long MAX_IMAGE_SIZE = 2000000; // 2MB
  public static final String IMAGE_FILE_REGEX = "\\.(png|jpg|gif|bmp|jpeg|PNG|JPG|GIF|BMP|JPEG)$";

  @Autowired
  AwsS3Utils awsS3Utils;

  @PostMapping("/images/questions")
  public ResponseEntity<File> uploadImage(@RequestParam("file") MultipartFile file) {
    this.validateImageFile(file);

    String filename = UUID.randomUUID().toString() + this.getFileExt(file.getOriginalFilename());
    String url = "";
    try {
      url = this.awsS3Utils.uploadFile(file, "images/questions/" + filename);
    } catch (IOException ex) {
      throw new BadRequestException("uploads-image/file-error", "File error");
    }

    File uploadedFile = new File();
    uploadedFile.setFilename(filename);
    uploadedFile.setUrl(url);
    uploadedFile.setSize(file.getSize());
    return new ResponseEntity<>(uploadedFile, HttpStatus.OK);
  }

  // Get file ext including ".". E.g. .jpg/.png
  private String getFileExt(String filename) {
    Integer lastDot = filename.lastIndexOf(".");
    return filename.substring(lastDot);
  }

  private void validateImageFile(MultipartFile file) {
    if (file.getSize() > MAX_IMAGE_SIZE) {
      throw new BadRequestException("uploads-image/file-too-large", "Image file size must be less than 2MB");
    }

    if (file.getOriginalFilename().matches(IMAGE_FILE_REGEX)) {
      throw new BadRequestException("uploads-image/invalid-file", "Only .jpg, .jpeg, .png, .gif, .bmp, .tiff image files are allowed");
    }
  }
}
