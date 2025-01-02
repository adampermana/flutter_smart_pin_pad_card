package com.adpstore.flutter_smart_pin_pad_cards;

import android.os.RemoteException;

import com.adpstore.flutter_smart_pin_pad_cards.entity.ClssOutComeData;
import com.adpstore.flutter_smart_pin_pad_cards.entity.ClssTransParam;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvOnlineResp;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvOutCome;
import com.adpstore.flutter_smart_pin_pad_cards.entity.EmvPinEnter;
import com.adpstore.flutter_smart_pin_pad_cards.enums.EKernelType;
import com.adpstore.flutter_smart_pin_pad_cards.enums.EPinType;
import com.topwise.cloudpos.aidl.emv.level2.AidlEntry;
import com.topwise.cloudpos.aidl.emv.level2.EmvCapk;
import com.topwise.cloudpos.struct.TlvList;
import com.topwise.toptool.api.convert.IConvert;
import com.topwise.toptool.impl.TopTool;

/**
 * Creation date: 2021/6/10 on 16:36
 * describe:
 * Author: Adam Permana
 */
public abstract class AClssKernelBaseTrans {
    private static final String TAG = AClssKernelBaseTrans.class.getSimpleName();
    protected AidlEntry entryL2 = DeviceServiceManagers.getInstance().getL2Entry();
    private kernekTransProcessListener kernekTransProcessListener;
    protected ClssTransParam clssTransParam;
    protected IConvert convert = TopTool.getInstance().getConvert();
    protected ClssOutComeData clssOutComeData;
    protected EmvOnlineResp emvOnlineResp;;
    protected EmvPinEnter emvPinEnter;

    /**
     * Set transaction param
     * @param clssTransParam
     */
    public void setInputParam(ClssTransParam clssTransParam) {
        this.clssTransParam = clssTransParam;
    }

    /**
     * Set contactless transaction process listener
     * @param processListener
     */
    public void setProcessListener(kernekTransProcessListener processListener) {
        this.kernekTransProcessListener = processListener;
    }

    /**
     * Get current AID param
     * @return
     */
    protected abstract String getCurrentAid();

    /**
     * Get outcome data
     * @return
     */
    protected abstract ClssOutComeData getOutcomeData();

    /**
     * Add CA public key by aid
     * @param aid
     * @return
     */
    protected abstract boolean addCapk(String aid);

    /**
     * Start kernel transaction process
     * @return
     */
    public abstract EmvOutCome StartKernelTransProc();

    /**
     * Get TLV data by TAG
     *
     * @param tag TAG
     * @return
     */
    public abstract byte [] getTLV(int tag);

    /**
     * Set TAG data into kernel
     *
     * @param tag TAG
     * @param datas Value
     * @return
     */
    public abstract boolean setTLV(int tag, byte [] datas);

    /**
     * Get debug info from kernel
     */
    public abstract void getDebugInfo();

    /**
     * Get PAN by track data
     * @param track track data
     * @return
     */
    protected String getPan(String track) {
        if (track == null)
            return null;

        int len = track.indexOf('=');
        if (len < 0) {
            len = track.indexOf('D');
            if (len < 0)
                return null;
        }

        if ((len < 10) || (len > 19))
            return null;
        return track.substring(0, len);
    }

    /**
     * Pass kernel type to payment app
     *
     * @param eKernelType
     */
    protected void AppUpdateKernelType(EKernelType eKernelType) {
        if (kernekTransProcessListener != null) {
            kernekTransProcessListener.onUpdateKernelType(eKernelType);
        }
    }

    /**
     * Notice payment app about final select step
     * @return
     */
    protected boolean AppFinalSelectAid() {
        if (kernekTransProcessListener != null) {
            return kernekTransProcessListener.onFinalSelectAid();
        }
        return false;
    }

    /**
     * Load AID TLV list by aid
     *
     * @return
     */
    protected TlvList AppGetKernalDataFromAidParam(String Aid) {
        if (kernekTransProcessListener != null) {
            return kernekTransProcessListener.onGetKernalDataFromAidParams(Aid);
        }
        return null;
    }

    /**
     * Load CA public key by rid and index
     *
     * @param rid RID
     * @param capkIndex index of public key
     * @return
     */
    protected EmvCapk AppFindCapkParamsProc(byte [] rid, byte capkIndex) {
        if (kernekTransProcessListener != null) {
            return kernekTransProcessListener.onFindCapkProc(rid,(byte) (capkIndex&0xFF));
        }
        return null;
    }

    /**
     * Notice payment app about card removed event, close RF reader.
     */
    protected boolean AppRemovrCard() {
        AppLog.d(TAG," AppRemovrCard =====");
        if (kernekTransProcessListener != null) {
            kernekTransProcessListener.onRemoveCardProc();
        }
        try {
            boolean RFclose = DeviceServiceManagers.getInstance().getRfCardReader().close();
            AppLog.d(TAG," AppRemovrCard Rf Close =====" +RFclose);
            return RFclose;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Request card holder to input PIN
     * @param ePinType
     * @return
     */
    protected EmvPinEnter AppReqImportPin(EPinType ePinType) {
        if (kernekTransProcessListener != null) {
            return kernekTransProcessListener.onReqImportPin(ePinType);
        }
        return null;
    }

    /**
     * Request card holder to confirm card number
     *
     * @param pan Card number
     * @return
     */
    protected boolean AppConfirmPan(String pan) {
        if (kernekTransProcessListener != null) {
            return kernekTransProcessListener.onConfirmPan(pan);
        }
        return false;
    }

    /**
     * Request for online processing and get online response data from issue bank
     * @return
     */
    protected EmvOnlineResp AppReqOnlineProc() {
        if (kernekTransProcessListener != null) {
            return kernekTransProcessListener.onReqOnlineProc();
        }
        return new EmvOnlineResp();
    }

    /**
     * Request for searching card again
     * @return
     */
    protected boolean AppSearchCardbySecond() {
        if (kernekTransProcessListener != null) {
            return kernekTransProcessListener.onSecondSearchCardProc();
        }
        return false;
    }

    /**
     * kernek trans proc listener
     */
    public interface kernekTransProcessListener {

        /**
         * Pass kernel type to payment app
         *
         * @param eKernelType
         */
        void onUpdateKernelType(EKernelType eKernelType);

        /**
         * Pass specific aid to payment app and search for AID param
         * @return
         */
        TlvList onGetKernalDataFromAidParams(String Aid);

        /**
         * Notice payment app about final select step
         * @return
         */
        boolean onFinalSelectAid();

        /**
         * Request card holder to confirm card number
         * @param pan
         * @return
         */
        boolean onConfirmPan(String pan);

        /**
         * Request card holder to input PIN
         *
         * @param ePinType ONLINE_PIN_REQ
         * @return
         */
        EmvPinEnter onReqImportPin(EPinType ePinType);

        /**
         * Search for the specific CA public key
         *
         * @param rid 9F06
         * @param index 8F
         * @return EmvCapk
         */
        EmvCapk onFindCapkProc(byte[] rid, byte index);

        /**
         * Notece payment app about card removed event
         */
        void onRemoveCardProc();

        /**
         * Request for online processing
         * @return
         */
        EmvOnlineResp onReqOnlineProc();

        /**
         * Request for searching card again
         * @return
         */
        boolean onSecondSearchCardProc();
    }
}
