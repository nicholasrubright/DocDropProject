package com.example.docdropproject.db;

import android.app.Application;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


public class CandidateViewModel extends AndroidViewModel {

    private LiveData<List<Candidate>> candidates;

    public CandidateViewModel (Application application) {
        super(application);
        candidates = CandidateDatabase.getDatabase(getApplication()).candidateDAO().getAll();
    }

    public LiveData<List<Candidate>> getAllCandidates() {
        return candidates;
    }

}
