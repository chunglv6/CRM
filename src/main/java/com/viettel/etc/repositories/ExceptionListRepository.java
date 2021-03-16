package com.viettel.etc.repositories;

import com.viettel.etc.dto.StageBooDTO;
import com.viettel.etc.dto.StationBooDTO;
import com.viettel.etc.dto.boo.ReqCalculatorTicketDTO;
import com.viettel.etc.repositories.tables.entities.ExceptionListEntity;
import com.viettel.etc.xlibrary.core.entities.ResultSelectEntity;

import java.util.List;

/**
 * Autogen class Repository Interface: Lop lay danh sach ngoai le
 *
 * @author toolGen
 * @date Fri Jul 03 10:13:51 ICT 2020
 */
public interface ExceptionListRepository {

    List<ExceptionListEntity> findAllExceptionEffective(ReqCalculatorTicketDTO reqCalculatorTicketDTO, Long  stageId, Long stationId, String licensePlateType);
}
