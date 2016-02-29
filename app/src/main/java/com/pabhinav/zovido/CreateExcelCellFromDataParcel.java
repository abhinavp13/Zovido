package com.pabhinav.zovido;

import java.util.ArrayList;

import jxl.format.Alignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;

/**
 * @author pabhinav
 */
public class CreateExcelCellFromDataParcel {

    private ArrayList<DataParcel> dataParcelArrayList;

    public CreateExcelCellFromDataParcel(ArrayList<DataParcel> dataParcels){
        dataParcelArrayList = dataParcels;
    }

    public Label[][] createExcel() throws Exception{

        Label[][] labels = new Label[9][dataParcelArrayList.size()+1];

        for(int i = 0; i<dataParcelArrayList.size()+1; i++){

                /** Header Font **/
                if(i == 0) {

                    WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
                    WritableCellFormat headerFormat = new WritableCellFormat(headerFont);

                    //center align the cells' contents
                    headerFormat.setAlignment(Alignment.CENTRE);

                    labels[0][i] = new Label(0,i, "Name");
                    labels[1][i] = new Label(1,i, "Phone Number");
                    labels[2][i] = new Label(2,i, "Agent Name");
                    labels[3][i] = new Label(3,i, "Call Time");
                    labels[4][i] = new Label(4,i, "Call Duration");
                    labels[5][i] = new Label(5,i, "Purpose");
                    labels[6][i] = new Label(6,i, "Product");
                    labels[7][i] = new Label(7,i, "Sport");
                    labels[8][i] = new Label(8,i, "Call Remarks");

                    for(int j = 0; j < 9; j++) {
                        labels[j][i].setCellFormat(headerFormat);
                    }

                } else {

                    WritableFont cellFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD);
                    WritableCellFormat cellFormat = new WritableCellFormat(cellFont);

                    //center align the cells' contents
                    cellFormat.setAlignment(Alignment.CENTRE);

                    labels[0][i] = new Label(0,i, dataParcelArrayList.get(i-1).getName());
                    labels[1][i] = new Label(1,i, dataParcelArrayList.get(i-1).getUserMobileNumber());
                    labels[2][i] = new Label(2,i, dataParcelArrayList.get(i-1).getAgentName());
                    labels[3][i] = new Label(3,i, dataParcelArrayList.get(i-1).getTimestamp());
                    labels[4][i] = new Label(4,i, dataParcelArrayList.get(i-1).getCallDuration());
                    labels[5][i] = new Label(5,i, dataParcelArrayList.get(i-1).getPurpose());
                    labels[6][i] = new Label(6,i, dataParcelArrayList.get(i-1).getProduct());
                    labels[7][i] = new Label(7,i, dataParcelArrayList.get(i-1).getSport());
                    labels[8][i] = new Label(8,i, dataParcelArrayList.get(i-1).getCallRemarks());

                    for(int j = 0; j < 9; j++){
                        labels[j][i].setCellFormat(cellFormat);
                    }
                }
        }

        return labels;
    }
}
