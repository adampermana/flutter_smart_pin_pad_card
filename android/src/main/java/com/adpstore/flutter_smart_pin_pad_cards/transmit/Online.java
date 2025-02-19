package com.adpstore.flutter_smart_pin_pad_cards.transmit;

import android.text.TextUtils;
import android.util.Log;

import com.adpstore.flutter_smart_pin_pad_cards.entity.TransData;
import com.adpstore.flutter_smart_pin_pad_cards.entity.TransResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Creation date：2021/9/1 on 15:09
 * Describe:
 * Author:wangweicheng
 */
public class Online {
    private static final String TAG = Online.class.getSimpleName();
    private static Online online;
    protected Gson jsonGson;

    private Online() {
        jsonGson = new GsonBuilder().create();
    }

    public synchronized static Online getInstance() {
        if (online == null) {
            online = new Online();
        }
        return online;
    }

    public int transMit(TransData transData) {

        //  int ret = comm.onInitPath();
        //like this
//        SaleRequest initialiseRequest = new SaleRequest();
//        initialiseRequest.setF003("000000");//hardcode keep it zero zero in pos
//        initialiseRequest.setF004(formatted);///AMOUNTWHICHU HAVE ENTRED IN AMOUNT PAGE
//        initialiseRequest.setF011(stan);///STAN INCREMENTAL
//        initialiseRequest.setF022(posentrymode+"1");//posentrymode
//        initialiseRequest.setF025("00");
////        initialiseRequest.setF023("11");//hardcode
////        initialiseRequest.setF025("00");//pontofservice
//        initialiseRequest.setF035(f035);//track2data replace D
////        initialiseRequest.setF036("10001001767500A00137");
//        initialiseRequest.setF041("10020611");//tid hard code  database
//        initialiseRequest.setF042("107113000078456");//database  hard code
//        initialiseRequest.setF047("30");//ENCRPTIONDATA WHAT KIND OF?????
//        if (transData.isHasPin()){
//                    initialiseRequest.setF052(pinblockdata);//PINBLOCKDATA
//             initialiseRequest.setF053("10001001767500A00137");
//        }

//        initialiseRequest.setF055(emv_data);
//        initialiseRequest.setF057("20210826");//YYYYMMDDbatchnuber
//        initialiseRequest.setF062(stan);//reciptnumber
//        initialiseRequest.setMsgType("0200");//response
//        Gson gson = new Gson();
        String testData = "{\"F003\":\"000000\",\"F004\":\"000000250000\",\"F011\":\"002130\"}";

        // Gunakan CustomResponse untuk menyimpan data
        CustomResponse testResponse = new CustomResponse(0, "");
        testResponse.setRetCode(0);
        testResponse.setData("{\"F062\":\"002130\",\"F041\":\"10025029\",\"F011\":\"002130\",\"F022\":\"071\"}");

        // Unpack data ke TransData
        return unpack(testResponse.getData(), transData);
    }

    public static class CustomResponse {
        private int retCode;
        private String data;

        public CustomResponse(int retCode, String data) {
            this.retCode = retCode;
            this.data = data;
        }

        public int getRetCode() {
            return retCode;
        }

        public void setRetCode(int retCode) {
            this.retCode = retCode;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }


    private int unpack(String jsonRecv, TransData transData) {
        JsonMessage jRecv = jsonGson.fromJson(jsonRecv, JsonMessage.class);
        String temp = jRecv.getF039();
        if (TextUtils.isEmpty(temp)) {
            return TransResult.ERR_BAG;
        }
        transData.setResponseCode(temp);
        // field 22
        temp = jRecv.getF022();
        ;
        if (!TextUtils.isEmpty(temp)) {
            transData.setField22(temp);
            Log.d(TAG, "unpack F022 = " + temp);
        }
        // field 23 CardSerialNo
        temp = jRecv.getF023();
        ;
        if (!TextUtils.isEmpty(temp)) {
            transData.setCardSerialNo(temp);
            Log.d(TAG, "unpack F023 = " + temp);
        }
        // field 25
        // field 26

        // field 32 AcqCenterCode
        temp = jRecv.getF032();
        if (!TextUtils.isEmpty(temp)) {
            transData.setAcqCenterCode(temp);
            Log.d(TAG, "unpack F032 = " + temp);
        }

        // field 35
        // field 36

        // field 37 RefNo
        temp = jRecv.getF037();
        if (!TextUtils.isEmpty(temp)) {
            transData.setRefNo(temp);
            Log.d(TAG, "unpack F037 = " + temp);
        }

        // field 38 AuthCode
        temp = jRecv.getF038();
        if (!TextUtils.isEmpty(temp)) {
            transData.setAuthCode(temp);

            Log.d(TAG, "unpack F038 = " + temp);
        }

        // field 41 check Terminal no
        temp = jRecv.getF041();
        if (!TextUtils.isEmpty(temp)) {
            transData.setTermID(temp);
            Log.d(TAG, "unpack F041 = " + temp);
        }

        // field 42 check Merchants
        temp = jRecv.getF042();
        if (!TextUtils.isEmpty(temp)) {
            transData.setMerchID(temp);
            Log.d(TAG, "unpack F042 = " + temp);
        }

        // field 43

        // field 44
        temp = jRecv.getF044();
        if (!TextUtils.isEmpty(temp)) {
            Log.d(TAG, "unpack F044 = " + temp);

        }
        //46
        temp = jRecv.getF046();
        if (!TextUtils.isEmpty(temp)) {
            Log.d(TAG, "unpack F046 = " + temp);
            //  transData.setXXX
        }
        // field 48

        // field 52
        temp = jRecv.getF052();
        if (!TextUtils.isEmpty(temp)) {
            transData.setField52(temp);
            Log.d(TAG, "unpack F052 = " + temp);
        }

        // field 53

        // field 54
        temp = jRecv.getF054();
        if (!TextUtils.isEmpty(temp)) {
//            transData.setBalanceFlag(temp.substring(7, 8));
//            transData.setBalance(temp.substring(temp.length() - 12, temp.length()));
            Log.d(TAG, "unpack F054 = " + temp);
        }

        // field 55
        temp = jRecv.getF055();
        if (!TextUtils.isEmpty(temp)) {
            transData.setRecvIccData(temp);
            Log.d(TAG, "unpack F055 = " + temp);
        }
        //....
        return 0;
    }
}
