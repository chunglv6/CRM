package com.viettel.etc.utils.exports;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Itsol_TungPV
 */
@Getter
@Setter
public class ConfigFileExport {

    private List lstData;
    private String sheetName;
    private String title;
    private List<ConfigSubtitleExport> suptitle;
    private int startRow;
    private int cellTitleIndex;
    private int mergeTitleEndIndex;
    private boolean creatHeader;
    private List<ConfigHeaderExport> header;
    private List<ConfigSubheaderExport> supheader;
    private boolean autoGenNo;

    public ConfigFileExport(List lstData, String sheetName, String title, List<ConfigSubtitleExport> suptitle, int startRow, int cellTitleIndex, int mergeTitleEndIndex, boolean creatHeader, List<ConfigHeaderExport> header, List<ConfigSubheaderExport> supheader, boolean autoGenNo) {
        this.lstData = lstData;
        this.sheetName = sheetName;
        this.title = title;
        this.suptitle = suptitle;
        this.startRow = startRow;
        this.cellTitleIndex = cellTitleIndex;
        this.mergeTitleEndIndex = mergeTitleEndIndex;
        this.creatHeader = creatHeader;
        this.header = header;
        this.supheader = supheader;
        this.autoGenNo = autoGenNo;
    }
}
