I. Huong dan lap trinh su dung gencode va cac ham co san
        
1. Import libs
	Thực hiện coppy Thư mục com ở Trong thư mục Libs vào MAVEN\Repository

2. Thuc hien gencode
    File config.properties: dung de cau hinh gen code
        - cau hinh database ung dung
            spring.datasource.url=jdbc:oracle:thin:@10.60.157.9:1521:dbpt
            spring.datasource.username=app
            spring.datasource.password=app#123
            (Thay doi thong tin database tuong ung cho ung dung dang code)
        - cau hinh thu muc gencode
            src.url.createcode: cau hinh duong dan tro den thu muc dang de code cua project

    - Đinh nghia cac class va  phuong thuc gen trong file template_gencode.json
        className: la ten cua lop muon tao
        desc: mo ta chi tiet thong tin cua lop muon tao
        listMethod: danh sach phuong thuc trong class can tao
            "type":"post",: bo sung them loai phuong thuc gen code
            "name": Tên phuong thuc dinh nghia,
            "value":"/customers/{customerId}/contracts", bo sung them duong dan value cho phuong thuc
            "sql": dinh nghia sql lay du lieu (neu co), cac dieu kien duoc viet theo mau ":tenparams"
                    cac bien nay co the truyen tu client de lay du lieu,

                    example: select u.name startdate from USERS u, DEPART d where u.depart_id = d.depart_id and (d.depart_id = :depart_id or :depart_id is null)
                     HashMap<String, Object> hmapParams = new HashMap<>();
                     hmapParams.put("depart_id", itemParamsEntity.getDepart_id());

            "desc": mo ta chuc nang cua phuong thuc,
            "params":danh sach params hoac ten bien muon tao trong DTO

    - Thuc hien gencode
        chạy file MainGenCode se tao duoc code can gen


3. thuc hien phuong thuc chung tai class FnCommon
    cac phuong thuc chung thuc hien o class nay
    - neu muon lay thong tin nguoi dang nhap thi su dung
    String userLogin =  FnCommon.getUserLogin(authentication);
    - neu muon lay chuoi token
    String strToken =  FnCommon.getStringToken(authentication);
    -neu muon convert 1 doi tuong sang 1 doi tuong khac
        FnCommon.convertObjectToObject(object1,classname.class);
        example:
            UsersDTO usersDTO = new UsersDTO();
                    usersDTO.setAddr("12321");
                    usersDTO.setName("nguyen dat");
                    UsersEntity item = (UsersEntity) FnCommon.convertObjectToObject(usersDTO, UsersEntity.class);
    
4. Thuc hien khai bao ma loi va tra  ve ma loi
    Neu muon tra ve ma loi tu custom thi su dung ham nhu sau
    return new ResponseEntity<>(FnCommon.responseToClient(ErrorApp.ERR_DATA,resultObj), HttpStatus.OK);
    class dinh nghĩa mã Loi:
    com.viettel.etc.utils.ErrorApp
    -  thuc hien gan  thong tin mo ta ma loi nhu sau:
    FnCommon.getValueFileMess("err.data")
    - bo sung them key cau hinh duong dan api
    public static final String REQUEST_MAPPING_V1 = "/api/v1";

5. Chú ý:
    package dùng chung cho he thong: com.viettel.etc.utils
    cac phuong thuc dung chung va khai bao o trong package nay.
    example:
        {
          "className": "Test",
          "desc": "Lớp thao tác danh sách cá nhân",
          "listMethod": [{
            "type":"post",
            "name": "getDataUser",
            "value":"/customers/{customerId}/contracts",
            "sql": "select * from USERS",
            "desc": "Lay du lieu giay to ca nhan",
            "params":["testParams1","testParams2","testParams3"]
          }]
        }

6. Hướng dẫn xuất fie excell
   Hàm dùng chung xuất file excell bao gồm 2 loại
   Loại 1: xuất file báo cáo chỉ có 1 sheet trong 1 file
   hàm gọi chung: FunctionCommon.exportFileExcell(sheetExprort, "D://temp1.xlsx");
            
            example: 
                ExcellSheet sheetExprort = new ExcellSheet();
                //set header
                List<ExcellHeaderEntity> listHeader = new ArrayList<>();
                ExcellHeaderEntity item = new ExcellHeaderEntity();
                item.setHeaderName("test 1");
                item.setWidth(4000);
                listHeader.add(item);
                item = new ExcellHeaderEntity();
                item.setHeaderName("test 2");
                listHeader.add(item);
                sheetExprort.setListHeader(listHeader);
                //set data
                ExcellDataEntity excellDataEntity = new ExcellDataEntity();
                List<List<Object>> listData = new ArrayList<>();
                //data row
                List<Object> listOb =  new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    listOb =  new ArrayList<>();
                    listOb.add("ha noi aaaa" + i);
                    listOb.add(2+i);
                    listData.add(listOb);
                }
                excellDataEntity.setListData(listData);
                sheetExprort.setExcellDataEntity(excellDataEntity);
                //xuat file khong co tieu de trong excell
                FunctionCommon.exportFileExcell(sheetExprort, "D://temp1.xlsx");
                Xuat file co tieu de trong excell
                FunctionCommon.exportFileExcell(sheetExprort, "D://temp1.xlsx","Tieu de excell");
   Loại 2: xuất file báo cáo bao gồm nhiều sheet trong 1 file
            hàm gọi chung: FunctionCommon.exportFileExcellMultiSheet(listSheetExprort, "D://temp1.xlsx");
	                        List<ExcellSheet> listSheetExprort = new ArrayList<>();
	        Trong từng item của sheet tương tự như xuất báo cái 1 sheet

7. Thông báo mã lỗi
   Su dung ham sau de thong bao ma loi cho nguoi dung neu dau vao sai
   if(params input error){
        FnCommon.throwsErrorApp(ErrorApp.ERROR_INPUTPARAMS);
   }
   Trong ham Controller thuc hien tra thong bao loi
   try {
       resultObj = usersService.getDataUser(dataParams);
   } catch (EtcException e) {
       return new ResponseEntity<>(FnCommon.responseToClient(e), HttpStatus.BAD_REQUEST);
   }
    
8.Thuc hien ky chung thu so tu usb, Gom 2 buoc
  Thuc hien add khai bao thong tin ma loi
    - file bo sung: com.viettel.etc.utils.Constants
        /**
         * Ma loi tra ve cua ki tren di dong
         */
        public interface SIGN_CODE {
            //Thanh cong
            public static final Integer SUCCESS = 1;
            //Loi cert het han
            public static final Integer ERR_CODE_CEREXPIRE = 2;
            //loi cert khong ton tai
            public static final Integer ERR_CODE_NOTFOUND = 3;
    
        }
    - file bo sung com.viettel.etc.utils.FnCommon:
        /**
         * loai bo gio trong date
         *
         * @param dateCv
         * @return
         */
        public static Date dateSort(Date dateCv) {
            Date date = null;
            String strDate = dateShow(dateCv, false);
            try {
    
                date = new SimpleDateFormat("dd/MM/yyyy").parse(strDate);
            } catch (ParseException ex) {
                LOGGER.error("==========datnv5: dateSort null", ex);
            }
            return date;
        }
    
        /**
         * kiem tra chung thu so
         * @param strCertificate
         * @return
         */
        public static Integer checkCer(String strCertificate) {
            Date now = new Date();
            now = dateSort(now);
            if (strCertificate != null && strCertificate.trim().length() > 0) {
                // Tao doi tuong ky mem
                X509Certificate x509Cert = CertUtils.getX509Cert(strCertificate);
                if (now.compareTo(dateSort(x509Cert.getNotAfter())) > 0
                        || now.compareTo(dateSort(x509Cert.getNotBefore())) < 0) {
                    return Constants.SIGN_CODE.ERR_CODE_CEREXPIRE;
                } else {
                    return Constants.SIGN_CODE.SUCCESS;
                }
            } else {
                return Constants.SIGN_CODE.ERR_CODE_NOTFOUND;
            }
        }
      

  Thuc hien khai bao them file pom
        <dependency>
              <groupId>com.itextpdf</groupId>
              <artifactId>itextpdf</artifactId>
              <version>5.5.5</version>
         </dependency>
        <dependency>
              <groupId>com.viettel.digital</groupId>
              <artifactId>viettelsign</artifactId>
              <version>1.0-RELEASE</version>
        </dependency>
        
  Buoc 1: thuc hien lay hash file va khai bao khoi tao he chu ky so
        Code mau thuc hien nhu sau:
        Example:
        
            //1. thuc hien khoi tao ky file pdf ngay ban dau lay hash file
            Integer statusCheckCert = FnCommon.checkCer("chuoi base cert");
            if(statusCheckCert == 1){
                SignerInfo signerInfo = new SignerInfo();
                signerInfo.setUserName("Test Sign");
                signerInfo.setUserId(1L);
                String strFilePdf = "duong dan file pdf can ky";
                String strFilePdfSigned = "duong dan file pdf da ky";
                UsbSign usbSign = new UsbSign(signerInfo, strFilePdf, strFilePdfSigned, false);
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("location", "ETC GROUP");
                String strLocation = jsonObj.toString();
                try {
                    //danh chuoi hash cua file pdf gom  2 phan tu: so 1 la hash, so 2 la serial number
                    List<String> listHashInfo = usbSign.getDigest("strCert", "", strLocation);
                    //TODO: thuc hien luu lai softSign vao session key
    
                    String sessionId = "Chuoi sessionid lay tu HttpServletRequest";
                    HttpSession session = req.getSession(true);
                    session.setAttribute("signObject", usbSign);
                    // Dat session vao bo quan ly
                    HttpSessionCollector.putSession(sessionId, session);
    
                } catch (Exception ex) {
                    //"Khong co hash file:"
                    System.out.println("signFileExtentCa");
                    System.out.println(ex);
                }
            }else{
                //thong bao ma loi cho client
    
            }
  Buoc 2: Thuc hien attach chu ky so vao file pdf
        Example:
        
            //2. thuc hien attach chu ky so  vao file
            String sessionId = "Chuoi sessionid luc luu lai";
            HttpSession session = HttpSessionCollector.find(sessionId);
            SoftSign softSign = (SoftSign) session.getAttribute("signObject");
            try {
                softSign.appendSignature("Chuoi chu ky so sau khu thuc hien ky");
            } catch (Exception ex) {
                //"Khong co hash file:"
                System.out.println("Append signature");
                System.out.println(ex);
            }
        
9. Su dung Redis cache trong project
   b1: Them thu vien vao pom file
    <dependency>
        <groupId>org.springframework.data</groupId>
        <artifactId>spring-data-redis</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-pool2</artifactId>
    </dependency>
    <dependency>
        <groupId>redis.clients</groupId>
        <artifactId>jedis</artifactId>
    </dependency>
   
   b2: Cau hinh server redis
   # ===============================
   # thuc hien cau hinh trong file application.properties
   # ===============================
   - dia chi cau hinh danh sach server redis
   redis.server.address = localhost:8011,localhost:8012
   - thoi gian timeout
   redis.server.timeout = 3000

   b3: sua lai file serviceApplication
   
    private static final Logger LOGGER = Logger.getLogger(ServiceApplication.class);

    public static void main(String[] args) {
        //thuc hien doc cau hinh file lay thong tin cau hinh redis
        String strRedisAddress = FnCommon.getPropertiesValue("redis.server.address").trim();
        String strIntTimeout = FnCommon.getPropertiesValue("redis.server.timeout");
        int timeoutRedit = 3000;
        try {
            timeoutRedit = Integer.valueOf(strIntTimeout);
        } catch (Exception ex) {
            LOGGER.error(ex);
        }
        if (strRedisAddress.length() > 0) {
            RedisUtil redis = new RedisUtil(strRedisAddress, timeoutRedit);
            redis.setup();
        }
        SpringApplication.run(ServiceApplication.class, args);
    }
   
   b4: Set gia tri vao redis su dung ham
        RedisUtil.getInstance().save("keytest","value");
   Neu muon gia tri co thoi gian timeout thi su dung ham
        RedisUtil.getInstance().save("keytest","value","thoi gian timeout");
   lay gia tri trong redis su dung
        RedisUtil.getInstance().get("keytest");
10. cau hinh bo qua link url ko can xac thuc
    permission.ignore.url = controllerRequest1/methodrequest1;controllerRequest2/methodrequest2
    Neu muon cau hinh de khong check authen cua 1 class thi tien hanh khai bao urlClassController/**

II. Quy dinh bat buoc trong su dung core nen
    1. quy dinh luong code 