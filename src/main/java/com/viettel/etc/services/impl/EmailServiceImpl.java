package com.viettel.etc.services.impl;

import com.viettel.etc.repositories.tables.WsAuditRepositoryJPA;
import com.viettel.etc.repositories.tables.entities.WsAuditEntity;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.Map;
import java.util.Properties;

@Service
public class EmailServiceImpl {
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(EmailServiceImpl.class.getName());

    @Value("${crm.email.server}")
    private String server;

    @Value("${crm.email.port}")
    private String port;

    @Value("${crm.email.local}")
    private String local;

    @Value("${crm.email.user}")
    private String username;

    @Value("${crm.email.pass}")
    private String password;

    @Value("${crm.email.from}")
    private String from;

    @Autowired
    WsAuditRepositoryJPA wsAuditRepositoryJPA;

    public void sendMail(String subject, String mailReceive, String filePath, Map<String, String> parameter, Authentication authentication) {

//        String content = "<!DOCTYPE HTML>\n"
//                + "<meta charset=\"UTF-8\">\n"
//                + "<html>\n"
//                + "<body>\n"
//                + "<p><b>THÔNG BÁO THÔNG TIN TÀI KHOẢN</b></p>\n"
//                + "<br>\n"
//                + "<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <b><i> Kính gửi quý khách hàng: " + customerName + "</b></i></p>\n"
//                + "<p>ePass xin gửi tới quý khách hàng thông tin tài khoản : </p>\n"
//                + "<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- Tên tài khoản:<b> " + user + "</b></p>\n"
//                + "<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- Mật khẩu:<b> " + pass + "</b></p>		\n"
//                + "<br>\n"
//                + "<p>Truy cập https://epass-vdtc.com.vn/tai-ung-dung-cpt để cài đặt ứng dụng và đổi mật khẩu. Chi tiết liên hệ 19009080 (1000 d/p)</p>\n"
//                + "<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <b><i>Trân trọng cảm ơn quý khách đã tin tưởng sử dụng ePass và đồng hành cùng chúng tôi!</b></i></p>\n"
//                + "</body>\n"
//                + "</html>";
        String content = getContent(filePath, parameter);
        long startTime = System.currentTimeMillis();
        boolean result = sendMailSSL(server, port, local, username, password, from, mailReceive, subject, content, 0);
        writeLog(content, String.valueOf(result), server, "", System.currentTimeMillis() - startTime, Constants.ACT_TYPE.SEND_EMAIL, authentication, "SMTP", String.valueOf(result));
    }

    private String getContent(String filePath, Map<String, String> parameter) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(filePath)) {
            StringBuilder content = new StringBuilder();
            if (in != null) {
                try (Reader reader = new BufferedReader(new InputStreamReader(in, Charset.forName(StandardCharsets.UTF_8.name())))) {
                    int c = 0;
                    while ((c = reader.read()) != -1) {
                        content.append((char) c);
                    }

                    String result = content.toString();
                    for (Map.Entry<String, String> entry : parameter.entrySet()) {
                        result = result.replace(entry.getKey(), entry.getValue());
                    }
                    return result;
                } catch (IOException e) {
                    LOGGER.error(e);
                }
            }
        } catch (IOException e) {
            LOGGER.error(e);
        }
        return "";
    }

    /***
     * Hàm ghi log gọi WS OCS vào bảng WS_AUDIT
     * @param req
     * @param resp
     * @param url
     * @param ip
     * @param time
     * @param actTypeId
     * @param authentication
     * @param wsCallType
     * @param status
     */
    private WsAuditEntity writeLog(String req, String resp, String url, String ip, long time, long actTypeId,
                                   Authentication authentication, String wsCallType, String status) {
        WsAuditEntity wsAuditEntity = new WsAuditEntity();
        try {
            String userLogin = "system", clientId = "crm";
            if (authentication != null) {
                userLogin = FnCommon.getUserLogin(authentication);
                clientId = FnCommon.getClientId(authentication);
            }
            wsAuditEntity.setWsCallType(wsCallType);
            wsAuditEntity.setActTypeId(actTypeId);
            wsAuditEntity.setSourceAppId(clientId);
            wsAuditEntity.setFinishTime(time);
            wsAuditEntity.setActionUserName(userLogin);
            wsAuditEntity.setRequestTime(new Date(System.currentTimeMillis()));
            wsAuditEntity.setStatus(status);
            wsAuditEntity.setMsgRequest(req.getBytes());
            wsAuditEntity.setWsUri(url);
            wsAuditEntity.setIpPc(ip);
            if (resp != null) {
                wsAuditEntity.setMsgReponse(resp.getBytes());
            }
            wsAuditEntity.setDestinationAppId("EMAIL");
            wsAuditEntity.setRequestTimeMiliSecond(System.currentTimeMillis());
            wsAuditRepositoryJPA.save(wsAuditEntity);
        } catch (Exception e) {
            LOGGER.error("write log ws_audit failed", e);
        }
        return wsAuditEntity;
    }

    /**
     * Thong tin cau hinh mail
     *
     * @param ipHost
     * @param port
     * @param ipLocal
     * @return
     * @throws Exception
     */
    private Properties getHostMail(String ipHost, String port, String ipLocal) {
        Properties props = new Properties();
        props.put("mail.smtp.host", ipHost);
        props.put("mail.smtp.localhost", ipLocal);
        props.put("mail.smtp.socketFactory.port", port);
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", port);
        return props;

    }

    /**
     * Kiem tra user Pass lay session de gui mail
     *
     * @param props
     * @param userMail
     * @param passMail
     * @return
     * @throws Exception
     */
    private Session getSessionMail(Properties props, final String userMail, final String passMail) throws Exception {
        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(userMail, passMail);
                    }
                });
        return session;
    }

    /**
     * Set noi dung mail gui di
     *
     * @param session
     * @param mailFrom
     * @param mailReceive
     * @param subject
     * @param content
     * @return
     * @throws Exception
     */
    private Message getMessageForMail(Session session, String mailFrom, String mailReceive, String subject, String content) throws Exception {
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailFrom));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailReceive));
            message.setSubject(subject);
            message.setContent(content, "text/html;charset=utf-8");
            message.saveChanges();
            return message;
        } catch (Exception e) {
            LOGGER.error("getMessageForMail :", e);
        }
        return null;
    }

    /**
     * Ham xu ly gui mail
     *
     * @param ipServerMail
     * @param port
     * @param ipLocal
     * @param user
     * @param pass
     * @param mailFrom
     * @param mailReceive
     * @param subject
     * @param content
     * @param callId
     * @return
     */
    private boolean sendMailSSL(String ipServerMail, String port, String ipLocal, String user, String pass, String mailFrom,
                                String mailReceive, String subject, String content, long callId) {
        boolean result = false;
        try {
            Properties props = getHostMail(ipServerMail, port, ipLocal);
            Session session = getSessionMail(props, user, pass);
            Message message = getMessageForMail(session, mailFrom, mailReceive, subject, content);
            if (message != null) Transport.send(message);
            result = true;
        } catch (Exception e) {
            LOGGER.error("sendMailSSL :", e);
        }
        return result;
    }
}
