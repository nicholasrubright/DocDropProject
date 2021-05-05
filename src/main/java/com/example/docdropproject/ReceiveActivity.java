package com.example.docdropproject;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.docdropproject.db.Candidate;
import com.example.docdropproject.db.CandidateDatabase;
import com.example.docdropproject.db.CandidateViewModel;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class ReceiveActivity extends AppCompatActivity {

    private NfcAdapter mNfcAdapter;
    private String[] information;
    private ArrayList<String> informationReceived = new ArrayList<>();

    private CandidateViewModel candidateViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);



        RecyclerView recyclerView = findViewById(R.id.lstCandidates);
        CandidateListAdapter adapter = new CandidateListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        candidateViewModel = new ViewModelProvider(this).get(CandidateViewModel.class);

        candidateViewModel.getAllCandidates().observe(this, adapter::setCandidates);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        information = getResources().getStringArray(R.array.receiverItems);


        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(mNfcAdapter != null) {
        } else {
            Toast.makeText(this, "NFC not available on this device",
                    Toast.LENGTH_SHORT).show();
        }

        for(int i = 0; i < information.length; i++) {
            informationReceived.add(information[i]);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void displayCandidate(int id) {
        CandidateDatabase.getCandidate(id, candidate -> {
            Bundle args = new Bundle();
            args.putInt("candidate_id", candidate.id);
            args.putString("name", candidate.name);
            args.putString("email", candidate.email);
            args.putString("phone", candidate.phone);

            DisplayCandidateDialog candidateDialog = new DisplayCandidateDialog();
            candidateDialog.setArguments(args);
            candidateDialog.show(getSupportFragmentManager(), "candidateDialog");
        });
    }

    public class CandidateListAdapter extends RecyclerView.Adapter<CandidateListAdapter.CandidateViewHolder> {
        class CandidateViewHolder extends RecyclerView.ViewHolder {
            private final TextView nameView;
            private Candidate candidate;

            private CandidateViewHolder(View itemView) {
                super(itemView);
                nameView = itemView.findViewById(R.id.txtName);
            }
        }
        private final LayoutInflater layoutInflater;
        private List<Candidate> candidates;

        CandidateListAdapter(Context context) {layoutInflater = LayoutInflater.from(context); }

        @Override
        public CandidateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.list_item, parent, false);
            return new CandidateViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(CandidateViewHolder holder, int position) {
            if (candidates != null) {
                Candidate current = candidates.get(position);
                holder.candidate = current;
                holder.nameView.setText(current.name);
            } else {
                holder.nameView.setText("...intializing...");
            }
        }

        void setCandidates(List<Candidate> candidates){
            this.candidates = candidates;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            if (candidates != null)
                return candidates.size();
            else return 0;
        }


    }

    public static class DisplayCandidateDialog extends DialogFragment {
        int candidate_id;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            candidate_id = getArguments().getInt("candidate_id");
            final String name = getArguments().getString("name");
            final String email = getArguments().getString("email");
            final String phone = getArguments().getString("phone");
            builder.setTitle(name)
                    .setMessage(email);
            return builder.create();
        }

        @Override
        public void onSaveInstanceState(@NonNull Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putString("JJB", "tester");
        }
    }


    private void addCandidate(String name, String email, String phone) {
        Candidate candidate = new Candidate(candidateViewModel.getAllCandidates().getValue().size() + 1, name, email, phone);
        CandidateDatabase.insert(candidate);
    }

    private void handleNfcIntent(Intent NfcIntent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(NfcIntent.getAction())) {
            Parcelable[] receivedArray =
                    NfcIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if(receivedArray != null) {
                informationReceived.clear();
                NdefMessage receivedMessage = (NdefMessage) receivedArray[0];
                NdefRecord[] attachedRecords = receivedMessage.getRecords();

                for (NdefRecord record:attachedRecords) {
                    String string = new String(record.getPayload());
                    if (string.equals(getPackageName())) { continue; }
                    informationReceived.add(string);
                }
                Toast.makeText(this, "Received Candidate Information", Toast.LENGTH_LONG).show();
                addCandidate(informationReceived.get(0), informationReceived.get(1), informationReceived.get(2));
                //updateTextViews();
            }
            else {
                Toast.makeText(this, "Received Empty Info", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleNfcIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        //updateTextViews();
        handleNfcIntent(getIntent());
    }
}