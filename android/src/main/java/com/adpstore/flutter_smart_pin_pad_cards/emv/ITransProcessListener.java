/*============================================================
 Module Name       : ITransProcessListener.java
 Date of Creation  : 18/12/2024
 Name of Creator   : Adam Permana
 History of Modifications:
 18/12/2024- Lorem Ipsum

 Summary           :


 Functions         :
 -

 Variables         :
 -

 ============================================================*/

package com.adpstore.flutter_smart_pin_pad_cards.emv;

import com.adpstore.flutter_smart_pin_pad_cards.emv.entity.EmvAidParam;
import com.adpstore.flutter_smart_pin_pad_cards.emv.entity.EmvOnlineResp;
import com.adpstore.flutter_smart_pin_pad_cards.emv.entity.EmvPinEnter;
import com.adpstore.flutter_smart_pin_pad_cards.emv.enums.EKernelType;
import com.adpstore.flutter_smart_pin_pad_cards.emv.enums.EPinType;
import com.topwise.cloudpos.aidl.emv.level2.Combination;
import com.topwise.cloudpos.aidl.emv.level2.EmvCandidateItem;
import com.topwise.cloudpos.aidl.emv.level2.EmvCapk;

import java.util.List;

public interface ITransProcessListener {
    /**
     * Request card holder to select an AID from candidate list.
     *
     * @param aids candidate AID list
     */
    int onReqAppAidSelect(String[] aids);

    /**
     * Inform payment app of selected candidate item.
     *
     * @param emvCandidateItem selected candidate item
     */
    void onUpToAppEmvCandidateItem(EmvCandidateItem emvCandidateItem);

    /**
     * Inform payment app of kernel type.
     *
     * @param eKernelType kernel type of current card
     */
    void onUpToAppKernelType(EKernelType eKernelType);

    /**
     * Notice payment app after final select step.
     */
    boolean onReqFinalAidSelect();

    /**
     * Request card holder to confirm card number.
     *
     * @param cardNo card number
     * @return true-proceed false-abort
     */
    boolean onConfirmCardInfo(String cardNo) ;

    /**
     * Request card holder to input PIN.
     *
     * @param pinType PIN type
     * @param leftTimes Remaining time of PIN entry
     * @return PinEnter PIN result
     */
    EmvPinEnter onReqGetPinProc(EPinType pinType, int leftTimes);

    /**
     * Request payment app to display pin try count.
     * @param PinTryCounter
     * @return
     */
    boolean onDisplayPinVerifyStatus(final int PinTryCounter);

    /**
     * Request card holder to authorize certificate.
     *
     * @param certype certificate type
     * @param certnumber certificate number
     * @return true-proceed false-abort
     */
    boolean onReqUserAuthProc(int certype, String certnumber);

    /**
     * Request for online processing.
     *
     * @return Online response data and script(optional) from issuer bank
     */
    EmvOnlineResp onReqOnlineProc();

    /**
     * Request second check card
     *
     *  0 find card success
     * -1 failed
     * @return
     */
    boolean onSecCheckCardProc();

    /**
     * Load all combinations from AID params stored by payment app.
     *
     * @return Combination list
     */
    List<Combination> onLoadCombinationParam();

    /**
     * Search for the specific AID param
     *
     * @param sAid specific AID
     * @return Target AID param
     */
    EmvAidParam onFindCurAidParamProc(String sAid);

    /**
     * Notice payment app about card removed event.
     */
    void onRemoveCardProc();

    /**
     * Search for the specific CA public key
     *
     * @param sAid specific AID
     * @param bCapkIndex specific public key index
     * @return Target CA public key
     */
    EmvCapk onFindIssCapkParamProc(String sAid, byte bCapkIndex);
}