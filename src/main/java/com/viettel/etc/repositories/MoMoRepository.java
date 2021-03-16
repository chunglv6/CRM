package com.viettel.etc.repositories;

public interface MoMoRepository {
    Object findByPlateOrContract(String searchType, String contractNo, String plateType, String plateNumber);
}
