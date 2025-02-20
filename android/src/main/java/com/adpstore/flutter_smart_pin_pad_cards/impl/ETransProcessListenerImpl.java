package com.adpstore.flutter_smart_pin_pad_cards.impl;

import static android.content.ContentValues.TAG;

import com.adpstore.flutter_smart_pin_pad_cards.AppLog;
import com.adpstore.flutter_smart_pin_pad_cards.ITransProcessListener;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvAidParam;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvOnlineResp;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvPinEnter;
import com.adpstore.flutter_smart_pin_pad_cards.enums.EKernelType;
import com.adpstore.flutter_smart_pin_pad_cards.enums.EOnlineResult;
import com.adpstore.flutter_smart_pin_pad_cards.enums.EPinType;
import com.adpstore.flutter_smart_pin_pad_cards.param.AidParam;
import com.topwise.cloudpos.aidl.emv.level2.Combination;
import com.topwise.cloudpos.aidl.emv.level2.EmvCandidateItem;
import com.topwise.cloudpos.aidl.emv.level2.EmvCapk;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建日期：2021/6/10 on 16:42
 * 描述:
 * 作者:wangweicheng
 */
public class ETransProcessListenerImpl implements ITransProcessListener {

    @Override
    public int onReqAppAidSelect(String[] aids) {
        return 0;
    }

    @Override
    public void onUpToAppEmvCandidateItem(EmvCandidateItem emvCandidateItem) {

    }

    @Override
    public void onUpToAppKernelType(EKernelType eKernelType) {

    }

    @Override
    public boolean onReqFinalAidSelect() {
        return true;
    }

    @Override
    public boolean onConfirmCardInfo(String cardNo) {
        return false;
    }

    @Override
    public EmvPinEnter onReqGetPinProc(EPinType pinType, int leftTimes) {
        return new EmvPinEnter();
    }

    @Override
    public boolean onDisplayPinVerifyStatus(final int PinTryCounter) {
        return false;
    }


    @Override
    public boolean onReqUserAuthProc(int certype, String certnumber) {
        return false;
    }

    //    @Override
//    public EmvOnlineResp onReqOnlineProc()  {
//        return new EmvOnlineResp();
//    }
//    @Override
//    public EmvOnlineResp onReqOnlineProc() {
//        EmvOnlineResp resp = new EmvOnlineResp();
//        resp.seteOnlineResult(EOnlineResult.ONLINE_APPROVE);
//
//        // Set auth response code "00" (approved)
//        byte[] authRespCode = new byte[]{0x30, 0x30}; // "00" in ASCII
//        resp.setAuthRespCode(authRespCode);
//        resp.setExistAuthRespCode(true);
//
//        return resp;
//    }

    @Override
    public EmvOnlineResp onReqOnlineProc() {
        EmvOnlineResp resp = new EmvOnlineResp();
        try {
            // Set basic response parameters
            resp.seteOnlineResult(EOnlineResult.ONLINE_APPROVE);
            // Set other required parameters
            return resp;
        } catch (Exception e) {
            AppLog.e(TAG, "Error creating online response: " + e.getMessage());
            return new EmvOnlineResp(); // Return default response instead of null
        }
    }


    @Override
    public boolean onSecCheckCardProc() {
        return false;
    }

//    @Override
//    public List<Combination> onLoadCombinationParam() {
//        return null;
//    }

    @Override
    public List<Combination> onLoadCombinationParam() {
        List<Combination> combinations = new ArrayList<>();
        try {
            // Add Visa combination
            Combination visa = new Combination();
            visa.setUcAidLen((byte) 7);
            visa.setAucAID(new byte[]{(byte) 0xA0, 0x00, 0x00, 0x00, 0x03, 0x10, 0x10});
            visa.setUcKernIDLen((byte) 1);
            visa.setAucKernelID(new byte[]{(byte) 0x03});
            combinations.add(visa);

            // Add Mastercard combination
            Combination mc = new Combination();
            mc.setUcAidLen((byte) 7);
            mc.setAucAID(new byte[]{(byte) 0xA0, 0x00, 0x00, 0x00, 0x04, 0x10, 0x10});
            mc.setUcKernIDLen((byte) 1);
            mc.setAucKernelID(new byte[]{(byte) 0x02});
            combinations.add(mc);

        } catch (Exception e) {
            AppLog.e(TAG, "Error creating combinations: " + e.getMessage());
        }
        return combinations;
    }

    //    @Override
//    public EmvAidParam onFindCurAidParamProc(String sAid) {
//        return null;
//    }
    @Override
    public EmvAidParam onFindCurAidParamProc(String sAid) {
        // Return AID param from already initialized list
        return AidParam.getCurrentAidParam(sAid);
    }

    @Override
    public void onRemoveCardProc() {

    }

    @Override
    public EmvCapk onFindIssCapkParamProc(String sAid, byte bCapkIndex) {
        return null;
    }
}
