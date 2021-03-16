package com.viettel.etc.services;

import com.google.zxing.WriterException;

import java.io.IOException;

public interface QrCodeService {

    String getQrCodeOfCust(String contractNo) throws WriterException, IOException;
}
