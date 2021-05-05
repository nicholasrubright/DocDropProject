package com.example.docdropproject.db;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface CandidateDAO {

    @Query("SELECT * FROM candidates ORDER BY name COLLATE NOCASE, rowid")
    LiveData<List<Candidate>> getAll();

    @Query("SELECT * FROM candidates WHERE rowid = :id")
    Candidate getById(int id);

    @Insert
    void insert(Candidate... candidates);

    @Update
    void update(Candidate... candidates);

    @Delete
    void delete(Candidate... candidates);

    @Query("DELETE FROM candidates WHERE rowid = :id")
    void delete(int id);
}
