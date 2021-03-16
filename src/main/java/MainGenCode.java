/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.viettel.gencode.*;
import com.viettel.gencode.constant.FunctionCommon;
import com.viettel.gencode.entity.ObjectEntity;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author datnv5
 */
public class MainGenCode {

    public static void main(String[] args) throws IOException {
            // create Gson instance
            String strPath = System.getProperty("user.dir") + "\\src\\main\\resources\\";
            // create a reader
            Reader reader = Files.newBufferedReader(Paths.get(strPath + "template_gencode.json"));
            gencodeAll(reader);
    }

    /**
     * thuc hien gencode
     * @param reader
     */
    private static void gencodeAll(Reader reader) {
        try {
        // convert JSON array to list of users
        ObjectEntity itemObject = (ObjectEntity) FunctionCommon.convertJsonToObject(reader, ObjectEntity.class);
        // close reader
        reader.close();
        //thuc hien gen class controller
        GencodeController.writeClassController(itemObject);
        //thuc hien gen class dto
        GencodeDTO.writeClassDTO(itemObject);

        //thuc hien gen class service,serviceImpl
        GencodeService.writeClassService(itemObject);
        GencodeServiceImpl.writeClassService(itemObject);


        //thuc hien gen class Repositorys
        GencodeRepositorys.writeClassRepositorys(itemObject);
        GencodeRepositorysImpl.writeClassRepositorys(itemObject);

        //==================gen service table==============================
        GencodeTableService.writeClassService(itemObject);
        GencodeTableRepositorys.writeClassRepositorys(itemObject);
        GencodeTableEntity.writeClassEntity(itemObject);
        } catch (IOException ex) {
            Logger.getLogger(GencodeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
