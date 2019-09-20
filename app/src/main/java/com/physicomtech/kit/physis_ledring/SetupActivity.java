package com.physicomtech.kit.physis_ledring;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.physicomtech.kit.physis_ledring.customize.LightOptionView;
import com.physicomtech.kit.physis_ledring.customize.OnSingleClickListener;
import com.physicomtech.kit.physis_ledring.customize.SerialNumberView;
import com.physicomtech.kit.physis_ledring.dialog.LoadingDialog;
import com.physicomtech.kit.physis_ledring.helper.PHYSIsPreferences;
import com.physicomtech.kit.physislibrary.PHYSIsBLEActivity;

import java.util.Arrays;

public class SetupActivity extends PHYSIsBLEActivity {

    private static final String TAG = "SetupActivity";

    SerialNumberView snvSetup;
    Button btnConnect, btnLedOn, btnLedOff;
    LightOptionView lovPattern, lovColorType, lovColor, lovInterval;

    // LED 설정 메시지 프로토콜 STX/ETX
    private static final String LED_SETUP_STX = "$";
    private static final String LED_SETUP_ETX = "#";
    // LED 출력 옵션 리스트
    private final String[] LED_PATTERNs = {"Dot Cycle", "Cycle", "Dot"};
    private final String[] LED_COLOR_TYPEs = {"Single", "Rotation", "Random"};
    private final String[] LED_COLORs = {"Red", "Green", "Blue", "Purple", "Sky Blue", "Yellow"};
    private final String[] LED_INTERVALs = {"50", "100", "250", "500", "1000", "2000"};

    private PHYSIsPreferences preferences;

    private String serialNumber = null;
    private boolean isConnected = false;

    private int ledPattern = 0;
    private int ledColorType =0;
    private int ledColor = 0;
    private int ledInterval = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        init();
    }

    @Override
    protected void onBLEConnectedStatus(int result) {
        super.onBLEConnectedStatus(result);
        LoadingDialog.dismiss();
        setConnectedResult(result);
    }


    /*
            Event
     */
    final SerialNumberView.OnSetSerialNumberListener onSetSerialNumberListener = new SerialNumberView.OnSetSerialNumberListener() {
        @Override
        public void onSetSerialNumber(String serialNum) {
            preferences.setPhysisSerialNumber(serialNumber = serialNum);
            Log.e(TAG, "# Set Serial Number : " + serialNumber);
        }
    };

    final LightOptionView.OnSelectedItemListener onSelectedItemListener = new LightOptionView.OnSelectedItemListener() {
        @Override
        public void onSelectedIndex(int groupTag, int itemPosition) {
            setLedAttribute(groupTag, itemPosition);
        }
    };

    final OnSingleClickListener onClickListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            switch (v.getId()){
                case R.id.btn_connect:
                    if(serialNumber == null){
                        Toast.makeText(getApplicationContext(), "PHYSIs Kit의 시리얼 넘버를 설정하세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(isConnected){
                        disconnectDevice();
                    }else{
                        LoadingDialog.show(SetupActivity.this, "Connecting..");
                        connectDevice(serialNumber);
                    }
                    break;
                case R.id.btn_led_on:
                    if(!isConnected)
                        return;
                    String ledOn = LED_SETUP_STX + "1" + ledPattern + ledColorType +
                            ledColor + LED_INTERVALs[ledInterval] + LED_SETUP_ETX;
                    sendMessage(ledOn);
                    break;
                case R.id.btn_led_off:
                    if(!isConnected)
                        return;
                    String ledOff = LED_SETUP_STX + "0" + LED_SETUP_ETX;
                    sendMessage(ledOff);
                    break;
            }
        }
    };

    /*
            Helper Method
     */
    private void setLedAttribute(int viewTag, int itemPosition){
        Log.e(TAG, "# Set Led Option : " + viewTag + " = " + itemPosition);
        switch (viewTag){
            case 0:
                lovPattern.setSelectedText(LED_PATTERNs[ledPattern = itemPosition]);
                break;
            case 1:
                lovColorType.setSelectedText(LED_COLOR_TYPEs[ledColorType = itemPosition]);
                lovColor.setEnable(ledColorType == 0);
                break;
            case 2:
                lovColor.setSelectedText(LED_COLORs[ledColor = itemPosition]);
                break;
            default:
                lovInterval.setSelectedText(LED_INTERVALs[ledInterval = itemPosition]);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setConnectedResult(int state){
        // set button
        if(isConnected = state == CONNECTED){
            btnConnect.setText("Disconnect");
        }else{
            btnConnect.setText("Connect");
        }
        // show toast
        String toastMsg;
        if(state == CONNECTED){
            toastMsg = "Physi Kit와 연결되었습니다.";
        }else if(state == DISCONNECTED){
            toastMsg = "Physi Kit 연결이 실패/종료되었습니다.";
        }else{
            toastMsg = "연결할 Physi Kit가 존재하지 않습니다.";
        }
        Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT).show();
    }

    private void init() {
        preferences = new PHYSIsPreferences(getApplicationContext());
        serialNumber = preferences.getPhysisSerialNumber();

        snvSetup = findViewById(R.id.snv_setup);
        snvSetup.setSerialNumber(serialNumber);
        snvSetup.showEditView(serialNumber == null);
        snvSetup.setOnSetSerialNumberListener(onSetSerialNumberListener);

        btnConnect = findViewById(R.id.btn_connect);
        btnConnect.setOnClickListener(onClickListener);
        btnLedOn = findViewById(R.id.btn_led_on);
        btnLedOn.setOnClickListener(onClickListener);
        btnLedOff = findViewById(R.id.btn_led_off);
        btnLedOff.setOnClickListener(onClickListener);

        lovPattern = findViewById(R.id.lov_pattern);
        lovColorType = findViewById(R.id.lov_color_type);
        lovColor = findViewById(R.id.lov_color);
        lovInterval = findViewById(R.id.lov_interval);
        lovPattern.setOnSelectedItemListener(onSelectedItemListener);
        lovColorType.setOnSelectedItemListener(onSelectedItemListener);
        lovColor.setOnSelectedItemListener(onSelectedItemListener);
        lovInterval.setOnSelectedItemListener(onSelectedItemListener);

        lovPattern.setOptionItems(Arrays.asList(LED_PATTERNs));
        lovColorType.setOptionItems(Arrays.asList(LED_COLOR_TYPEs));
        lovColor.setOptionItems(Arrays.asList(LED_COLORs));
        lovInterval.setOptionItems(Arrays.asList(LED_INTERVALs));

        setLedAttribute(lovPattern.getOptionTag(), ledPattern);
        setLedAttribute(lovColorType.getOptionTag(), ledColorType);
        setLedAttribute(lovColor.getOptionTag(), ledColor);
        setLedAttribute(lovInterval.getOptionTag(), ledInterval);
    }
}
