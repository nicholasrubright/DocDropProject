package com.example.docdropproject.db;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Candidate.class}, version = 1, exportSchema = false)
public abstract class CandidateDatabase extends RoomDatabase {
    public interface CandidateListener {
        void onJokeReturned(Candidate candidate);
    }

    public abstract CandidateDAO candidateDAO();

    private static CandidateDatabase INSTANCE;

    public static CandidateDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CandidateDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            CandidateDatabase.class, "candidate_database")
                            .addCallback(createCandidateDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Note this call back will be run
    private static RoomDatabase.Callback createCandidateDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            createCandidateTable();
        }
    };

    private static void createCandidateTable() {
        for (int i = 0; i < DefaultContent.NAME.length; i++) {
            insert(new Candidate(0, DefaultContent.NAME[i], DefaultContent.EMAIL[i], DefaultContent.PHONE[i]));
        }
    }

    public static void getCandidate(int id, CandidateListener listener) {
        new AsyncTask<Integer, Void, Candidate> () {
            protected Candidate doInBackground(Integer... ids) {
                return INSTANCE.candidateDAO().getById(ids[0]);
            }

            protected void onPostExecute(Candidate candidate) {
                super.onPostExecute(candidate);
                listener.onJokeReturned(candidate);
            }
        }.execute(id);
    }

    public static void insert(Candidate candidate) {
        new AsyncTask<Candidate, Void, Void> () {
            protected Void doInBackground(Candidate... candidates) {
                INSTANCE.candidateDAO().insert(candidates);
                return null;
            }
        }.execute(candidate);
    }

    public static void delete(int candidateId) {
        new AsyncTask<Integer, Void, Void> () {
            protected Void doInBackground(Integer... ids) {
                INSTANCE.candidateDAO().delete(ids[0]);
                return null;
            }
        }.execute(candidateId);
    }


    public static void update(Candidate candidate) {
        new AsyncTask<Candidate, Void, Void> () {
            protected Void doInBackground(Candidate... candidates) {
                INSTANCE.candidateDAO().update(candidates);
                return null;
            }
        }.execute(candidate);
    }
}
