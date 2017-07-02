package com.gulbalasalamov.smsdemo3;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SmsActivity extends AppCompatActivity {

    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";

    PendingIntent sentPI, deliveredPI;
    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;

    private static final int SMS_REQUEST = 1;
    Button btn_gonder;
    EditText mesaj, numara;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        btn_gonder = (Button) findViewById(R.id.button_gonder);
        numara = (EditText) findViewById(R.id.numara);
        mesaj = (EditText) findViewById(R.id.mesaj);

        sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);
        deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);


        if (!izinVarMi()) {
            kullaniciIzni();
        }

        try {
            btn_gonder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gonderSMS();
                    //Toast.makeText(SmsActivity.this, "Mesajınız başarıyla gönderildi. ", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Mesaj gönderilemedi. ", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void gonderSMS() {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(numara.getText().toString(), null, mesaj.getText().toString(), sentPI, deliveredPI);
    }

    private void kullaniciIzni() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_REQUEST);
    }

    private boolean izinVarMi() {
        int resultSms = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if (resultSms == PackageManager.PERMISSION_GRANTED)
            return true;
        return false;
    }

    public void onResume() {
        super.onResume();
        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS yollandı", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Genel hata", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "Servis Yok", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Boş PDU değeri", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Sinyal Yok", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        smsDeliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS iletildi",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS iletilmedi",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        registerReceiver(smsSentReceiver, new IntentFilter(SENT));
        registerReceiver(smsDeliveredReceiver, new IntentFilter(DELIVERED));
        btn_gonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gonderSMS();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(smsSentReceiver);
        unregisterReceiver(smsDeliveredReceiver);
    }

}

