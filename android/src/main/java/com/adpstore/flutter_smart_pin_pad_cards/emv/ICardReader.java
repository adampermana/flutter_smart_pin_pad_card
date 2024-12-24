/*============================================================
 Module Name       : ICardReader.java
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

public interface ICardReader {
    /**
     * Start searching card
     *
     * @param isMag   support magnetic card or not
     * @param isIcc   support contact ic card or not
     * @param isRf    support contactless ic card or not
     * @param outTime 单位秒/Unit second
     *                //     * @param onReadCardListener
     */


    void startFindCard(boolean isMag, boolean isIcc, boolean isRf, int outTime,
                       CardReader.onReadCardListener onReadCardListener);

    /**
     * Cancel search card
     */
    void cancel();
}
