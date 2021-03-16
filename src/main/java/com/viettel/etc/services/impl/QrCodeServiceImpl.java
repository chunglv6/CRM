package com.viettel.etc.services.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.viettel.etc.repositories.tables.ContractRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.ContractEntity;
import com.viettel.etc.repositories.tables.entities.CustQrCode;
import com.viettel.etc.services.ContractService;
import com.viettel.etc.services.QrCodeService;
import com.viettel.etc.services.tables.QrCodeServiceJPA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;
import java.util.Hashtable;
import java.util.Optional;

/**
 * Autogen class: Lop thao tac danh sach ca nhan
 *
 * @author ToolGen
 * @date Tue Jul 07 14:31:18 ICT 2020
 */

@Service
public class QrCodeServiceImpl implements QrCodeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QrCodeServiceImpl.class);

    @Autowired
    QrCodeServiceJPA qrCodeServiceJpa;

    @Autowired
    ContractRepositoryJPA contractRepositoryJPA;

    @Autowired
    ContractService contractService;


    /**
     * Lay qrcode cua khach hang
     *
     * @return chuoi base64 hinh anh qrcode
     */
    @Override
    public String getQrCodeOfCust(String contractNo) throws WriterException, IOException {
        String pathQrCode = "";
        ContractEntity contract = contractRepositoryJPA.findByContractNo(contractNo);
        if (contract != null) {
            Optional<CustQrCode> data = qrCodeServiceJpa.findByCustId(contract.getContractId());
            if (data.isPresent()) {
                pathQrCode = data.get().getQrCodePath();
            } else {
                pathQrCode = createQrCode(System.getProperty("user.dir") + File.separator + "qrcode" + File.separator, contractNo, contract);
            }
        }
        return encodeBase64QrCode(pathQrCode);
    }

    /**
     * Tao moi qrcode neu khach hang chua co qrcode
     *
     * @param path
     * @param contractNo
     * @param contract
     * @return
     * @throws WriterException
     * @throws IOException
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public String createQrCode(String path, String contractNo, ContractEntity contract)
            throws WriterException, IOException {
        contractNo = contractNo.replaceAll("[\\\\/]", "_");
        String result = "";
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdir();
        }
        File qrFile = new File(path + contractNo + ".png");
        Hashtable<EncodeHintType, Comparable> hintMap = new Hashtable<>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        StringBuilder qrCodeContent = new StringBuilder();
        qrCodeContent.append("Mã hợp đồng: ").append(contract.getContractNo()).append("\n");
        qrCodeContent.append("Tên chủ phương tiện: ").append(contract.getNoticeName()).append("\n");
        qrCodeContent.append("Email chủ phương tiện: ").append(contract.getNoticeEmail()).append("\n");
        qrCodeContent.append("SDT chủ phương tiện: ").append(contract.getNoticePhoneNumber());
        BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeContent.toString(), BarcodeFormat.QR_CODE, 200, 200, hintMap);

        int matrixWidth = byteMatrix.getWidth();
        BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
        image.createGraphics();

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, matrixWidth, matrixWidth);

        graphics.setColor(Color.BLACK);

        for (int i = 0; i < matrixWidth; i++) {
            for (int j = 0; j < matrixWidth; j++) {
                if (byteMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }
        if (ImageIO.write(image, "png", qrFile)) {
            result = path + contractNo + ".png";
            CustQrCode qrCodeEntity = new CustQrCode();
            qrCodeEntity.setContractId(contract.getContractId());
            qrCodeEntity.setQrCodePath(result);
            qrCodeEntity.setCreateDate(new Date());
            qrCodeServiceJpa.saveQrCode(qrCodeEntity);
        }
        return result;
    }

    /**
     * Thuc hien encode base64 QRCode
     *
     * @param pathQrCode
     * @return
     */
    public String encodeBase64QrCode(String pathQrCode) {
        String base64QrCode = "";
        try {
            Path path = Paths.get(pathQrCode);
            byte[] data = Files.readAllBytes(path);
            base64QrCode = Base64.getEncoder().encodeToString(data);
        } catch (Exception e) {
            LOGGER.error(String.format("%s%s", "[QrCodeServiceImpl][encodeBase64QrCode]", e));
        }
        return base64QrCode;
    }

}
