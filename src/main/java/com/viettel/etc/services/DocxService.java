package com.viettel.etc.services;

import com.viettel.etc.dto.FileReportDTO;
import com.viettel.etc.dto.VehicleDTO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.core.Authentication;

public interface DocxService {
    String createFileTemplate(VehicleDTO params);

    ByteArrayResource getFileSigned(byte[] byteImage, FileReportDTO params);

    ByteArrayResource getFileTemplate(FileReportDTO params);

    void saveReportFiles(FileReportDTO params, Authentication authentication);
}
