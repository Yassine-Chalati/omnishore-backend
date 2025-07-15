package com.omnishore.cvtech.domain.services;

import java.io.File;

public interface MinioStorageService {
    String upload(File file, String objectName) throws Exception;

    File download(String objectName, File destFile) throws Exception;
}
