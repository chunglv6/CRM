package com.viettel.etc.services.impl;

import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.BaseFont;
import com.viettel.etc.dto.ContractDTO;
import com.viettel.etc.dto.CustomerDTO;
import com.viettel.etc.dto.FileReportDTO;
import com.viettel.etc.dto.VehicleDTO;
import com.viettel.etc.repositories.tables.entities.*;
import com.viettel.etc.services.DocxService;
import com.viettel.etc.services.FileService;
import com.viettel.etc.services.VehicleService;
import com.viettel.etc.services.tables.*;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.ErrorApp;
import com.viettel.etc.utils.FnCommon;
import com.viettel.etc.utils.exceptions.EtcException;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.ConverterTypeVia;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.images.ByteArrayImageProvider;
import fr.opensagres.xdocreport.document.images.IImageProvider;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.itext.extension.font.IFontProvider;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

@Service
@Log4j
public class DocxServiceImpl implements DocxService {

    @Autowired
    FileService fileService;

    @Autowired
    VehicleServiceJPA vehicleServiceJPA;

    @Autowired
    ContractServiceJPA contractServiceJPA;

    @Autowired
    CustomerServiceJPA customerServiceJPA;

    @Autowired
    private OtpServiceJPA otpServiceJPA;

    @Autowired
    VehicleProfileServiceJPA vehicleProfileServiceJPA;

    @Autowired
    DocumentTypeServiceJPA documentTypeServiceJPA;

    @Autowired
    VehicleService vehicleService;

    @Override
    public ByteArrayResource getFileSigned(byte[] byteImage, FileReportDTO params) {
        ContractEntity contractEntity = contractServiceJPA.getByContractNo(params.getContractNo());
        if (contractEntity == null) {
            throw new EtcException("crm.contract.not.exist");
        }

        //validate otp
        OtpIdentify otpId = new OtpIdentify();
        otpId.setPhone(contractEntity.getNoticePhoneNumber());
        otpId.setConfirmType(OtpIdentify.SIGN);
        if (!otpServiceJPA.existsById(otpId)) {
            throw new EtcException("validate.otp.not.exist");
        }
        OtpEntity otpEntity = otpServiceJPA.getById(otpId);
        if (!otpEntity.getOtp().equals(params.getOtp())) {
            throw new EtcException("validate.otp.wrong");
        }
        long diff = new Date().getTime() - otpEntity.getSignDate().getTime();
        if (diff > otpEntity.getDuration() * 1000 * 60) {   // diff in minute
            throw new EtcException("validate.opt.expired");
        }

        String filePath = params.getContractNo() + File.separator + params.getFileName() + ".docx";
        IImageProvider photo = new ByteArrayImageProvider(byteImage);
        try (ByteArrayInputStream file = new ByteArrayInputStream(fileService.getFile(filePath))) {
            IXDocReport report = XDocReportRegistry.getRegistry().loadReport(file, TemplateEngineKind.Velocity);
            IContext context = report.createContext();
            photo.setSize(100f, 100f);
            FieldsMetadata metadata = report.createFieldsMetadata();
            metadata.addFieldAsImage("photo");
            context.put("photo", photo);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Options options = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.XWPF);
            PdfOptions pdfOptions = PdfOptions.create();
//            pdfOptions.fontEncoding("iso-8859-16");
//            windows-1258
            pdfOptions.fontProvider((familyName, encoding, size, style, color) -> {
                try {
                    if ("Arial".equalsIgnoreCase(familyName)) {
                        BaseFont baseFont =
                                BaseFont.createFont("fonts" + File.separator + "arial.ttf", encoding, BaseFont.EMBEDDED);
                        return new Font(baseFont, size, style, color);
                    } else {
                        BaseFont baseFont =
                                BaseFont.createFont("fonts" + File.separator + "seguisym.ttf", encoding, BaseFont.EMBEDDED);
                        return new Font(baseFont, size, style, color);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
//                return FontFactory.getFont(familyName, encoding, size, style, color);
            });
            options.subOptions(pdfOptions);
            report.convert(context, options, out);
            filePath = filePath.replace(".docx", "_signed.pdf");
            fileService.uploadFile(filePath, out.toByteArray());
            return new ByteArrayResource(out.toByteArray());
        } catch (XDocReportException | IOException e) {
            log.error(e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ByteArrayResource getFileTemplate(FileReportDTO params) {
        String filePath = params.getContractNo() + File.separator + params.getFileName() + ".docx";
        try (ByteArrayInputStream file = new ByteArrayInputStream(fileService.getFile(filePath))) {
            IXDocReport report = XDocReportRegistry.getRegistry().loadReport(file, TemplateEngineKind.Velocity);
            IContext context = report.createContext();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Options options = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.XWPF);
            PdfOptions pdfOptions = PdfOptions.create();
//            pdfOptions.fontEncoding("iso-8859-16");
//            windows-1258
            pdfOptions.fontProvider((familyName, encoding, size, style, color) -> {
                try {
                    if ("Arial".equalsIgnoreCase(familyName)) {
                        BaseFont baseFont =
                                BaseFont.createFont("fonts" + File.separator + "arial.ttf", encoding, BaseFont.EMBEDDED);
                        return new Font(baseFont, size, style, color);
                    } else {
                        BaseFont baseFont =
                                BaseFont.createFont("fonts" + File.separator + "seguisym.ttf", encoding, BaseFont.EMBEDDED);
                        return new Font(baseFont, size, style, color);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
//                return FontFactory.getFont(familyName, encoding, size, style, color);
            });
            options.subOptions(pdfOptions);
            report.convert(context, options, out);
            return new ByteArrayResource(out.toByteArray());
        } catch (XDocReportException | IOException e) {
            log.error(e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveReportFiles(FileReportDTO params, Authentication authentication) {
        ContractEntity contractEntity = contractServiceJPA.getByContractNo(params.getContractNo());
        if (contractEntity == null) {
            throw new EtcException("crm.contract.not.exist");
        }
        VehicleProfileEntity vehicleProfileEntity = new VehicleProfileEntity();
        vehicleProfileEntity.setContractId(contractEntity.getContractId());
        vehicleProfileEntity.setVehicleId(params.getVehicleId());
        vehicleProfileEntity.setDocumentTypeId(documentTypeServiceJPA.findDocumentTypeIdByCode("DVYC"));
        vehicleProfileEntity.setFileName(params.getFileName());
        vehicleProfileEntity.setFilePath(params.getContractNo() + File.separator + params.getFileName());
        vehicleProfileEntity.setCreateDate(new java.sql.Date(System.currentTimeMillis()));
        vehicleProfileEntity.setCreateUser(FnCommon.getUserLogin(authentication));
        vehicleProfileEntity.setStatus(Constants.ATTACHMENT_FILE_STATUS.ACTIVE);
        vehicleProfileServiceJPA.save(vehicleProfileEntity);
    }

    @Override
    public String createFileTemplate(VehicleDTO params) {
        if (params.getContractId() == null || params.getPlateNumber() == null || params.getOwner() == null || params.getSeatNumber() == null) {
            throw new EtcException(ErrorApp.ERR_DATA);
        }
        ContractEntity contract = contractServiceJPA.getOne(params.getContractId());
        if (contract == null) {
            throw new EtcException("crm.contract.not.exist");
        }
        CustomerEntity customer = customerServiceJPA.getOne(contract.getCustId());
        String fileName = null;
        String filePath;
        if (Arrays.asList(Constants.CUSTOMER_PERSONAL).contains(customer.getCustTypeId())) {
            filePath = "template" + File.separator + "EPass_giay_de_nghi_personal.docx";
        } else {
            filePath = "template" + File.separator + "EPass_giay_de_nghi_enterprise.docx";
            if (customer.getRepName() == null) {
                customer.setRepName(customer.getAuthName());
                customer.setRepIdentityNumber(customer.getAuthIdentityNumber());
                customer.setRepDateOfIssue(customer.getAuthDateOfIssue());
            }
        }
        try(InputStream in = getClass().getClassLoader().getResourceAsStream(filePath)) {

            IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Velocity);
            SimpleDateFormat dateFormatter = new SimpleDateFormat(Constants.COMMON_DATE_FORMAT);

            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setCustName(customer.getCustName().toUpperCase());
            if (customer.getBirthDate() != null) {
                customerDTO.setDateOfBirth(dateFormatter.format(customer.getBirthDate()));
            }
            customerDTO.setDocumentNumber(customer.getDocumentNumber());
            customerDTO.setPlaceOfIssue(customer.getPlaceOfIssue());
            if (customer.getDateOfIssue() != null) {
                customerDTO.setDateIssue(dateFormatter.format(customer.getDateOfIssue()));
            }
            customerDTO.setRepName(customer.getRepName());
            customerDTO.setRepIdentityNumber(customer.getRepIdentityNumber());
            if (customer.getRepDateOfIssue() != null) {
                customerDTO.setRepDateIssue(dateFormatter.format(customer.getRepDateOfIssue()));
            }

            ContractDTO contractDTO = new ContractDTO();
            contractDTO.setNoticeStreet(contract.getNoticeStreet());
            contractDTO.setNoticeAreaName(contract.getNoticeAreaName());
            contractDTO.setNoticePhoneNumber(contract.getNoticePhoneNumber());
            contractDTO.setNoticeEmail(contract.getNoticeEmail());

            VehicleDTO vehicleDTO = new VehicleDTO();
            vehicleDTO.setPlateNumber(params.getPlateNumber());
            vehicleDTO.setOwner(params.getOwner());
            vehicleDTO.setSeatNumber(params.getSeatNumber());
            vehicleDTO.setRfidSerial(params.getRfidSerial());

            String vehicleCheck = vehicleService.checkVehicle(params.getPlateNumber(), params.getPlateTypeCode());

            IContext context = report.createContext();
            context.put("customer", customerDTO);
            context.put("contract", contractDTO);
            context.put("vehicle", vehicleDTO);

            Calendar calendar = Calendar.getInstance();
            context.put("day", calendar.get(Calendar.DAY_OF_MONTH));
            context.put("month", calendar.get(Calendar.MONTH) + 1);
            context.put("years", calendar.get(Calendar.YEAR));
            if (Constants.BOO_STATUS.DESTROY.equals(vehicleCheck) || Constants.BOO_STATUS.ACTIVE.equals(vehicleCheck)) {
                context.put("new", "☐");
                context.put("old", "☑");
            } else {
                context.put("new", "☑");
                context.put("old", "☐");
            }
            fileName = "Giay_de_nghi_" + System.currentTimeMillis();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            report.process(context, out);
            fileService.uploadFile(contract.getContractNo() + File.separator + fileName + ".docx", out.toByteArray());

        } catch ( IOException | XDocReportException e ) {
            log.error(e);
            e.printStackTrace();
        }
        return fileName;
    }
}
