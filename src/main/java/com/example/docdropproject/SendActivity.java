package com.example.docdropproject;


import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.ArrayList;

public class SendActivity extends AppCompatActivity implements NfcAdapter.OnNdefPushCompleteCallback, NfcAdapter.CreateNdefMessageCallback {

    private NfcAdapter mNfcAdapter;

    private ArrayList<String> informationToSend = new ArrayList<>();

    private EditText editText_Name;
    private EditText editText_Email;
    private EditText editText_Phone;

    private TextView textView_Name;
    private TextView textView_Email;
    private TextView textView_Phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(mNfcAdapter != null) {
            mNfcAdapter.setNdefPushMessageCallback(this, this);
            mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
        } else {
            Toast.makeText(this, "NFC not available on this device", Toast.LENGTH_SHORT).show();
        }

        editText_Name = (EditText) findViewById(R.id.editTextTextPersonName);
        editText_Email = (EditText) findViewById(R.id.editTextTextEmailAddress);
        editText_Phone = (EditText) findViewById(R.id.editTextPhone);

        textView_Name = (TextView) findViewById(R.id.textView_name_preview);
        textView_Email = (TextView) findViewById(R.id.textView_email_preview);
        textView_Phone = (TextView) findViewById(R.id.textView_phone_preview);

        Button btnUpdateInfo = (Button) findViewById(R.id.update_button);

        updateTextViews();

    }

    public void updateInformation(View view) {

        informationToSend.clear();

        String nameInfo = editText_Name.getText().toString();
        String emailInfo = editText_Email.getText().toString();
        String phoneInfo = editText_Phone.getText().toString();

        informationToSend.add(nameInfo);
        informationToSend.add(emailInfo);
        informationToSend.add(phoneInfo);

        editText_Name.setText(null);
        editText_Email.setText(null);
        editText_Phone.setText(null);

        updateTextViews();

        Toast.makeText(this, "Updated Information", Toast.LENGTH_LONG).show();
//        Snackbar mySnackbar = Snackbar.make(R.layout.activity_send, "Updated Information", Snackbar.LENGTH_LONG);
    }

    private void updateTextViews() {

        textView_Name.setText(null);
        textView_Email.setText(null);
        textView_Phone.setText(null);

        if(informationToSend.size() > 0) {
            textView_Name.setText(informationToSend.get(0));
            textView_Email.setText(informationToSend.get(1));
            textView_Phone.setText(informationToSend.get(2));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putStringArrayList("informationToSend", informationToSend);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        informationToSend = savedInstanceState.getStringArrayList("informationToSend");
    }


    @Override
    public void onNdefPushComplete(NfcEvent event) {
        Toast.makeText(this, "Information has been Successfully Sent", Toast.LENGTH_LONG).show();
        informationToSend.clear();
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        //This will be called when another NFC capable device is detected.
        if (informationToSend.size() == 0) {
            return null;
        }
        NdefRecord[] recordsToAttach = createRecords();
        return new NdefMessage(recordsToAttach);
    }

    public NdefRecord[] createRecords() {
        NdefRecord[] records = new NdefRecord[informationToSend.size() + 1];
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            for (int i = 0; i < informationToSend.size(); i++){
                byte[] payload = informationToSend.get(i).
                        getBytes(Charset.forName("UTF-8"));
                NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);

                records[i] = record;
            }
        }
        else {
            for (int i = 0; i < informationToSend.size(); i++){
                byte[] payload = informationToSend.get(i).
                        getBytes(Charset.forName("UTF-8"));

                NdefRecord record = NdefRecord.createMime("text/plain",payload);
                records[i] = record;
            }
        }
        records[informationToSend.size()] =
                NdefRecord.createApplicationRecord(getPackageName());
        return records;
    }


}