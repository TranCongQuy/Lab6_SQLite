package com.example.lab6_sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "notesManager";
    private static final int DATABASE_VERSION = 1;

    // Tên bảng và các cột theo đúng yêu cầu đề bài
    private static final String TABLE_NAME = "TBL_NOTES";
    private static final String KEY_ID = "Id";
    private static final String KEY_CONTENT = "Content";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng TBL_NOTES(Id, Content)
        String create_notes_table = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT)",
                TABLE_NAME, KEY_ID, KEY_CONTENT);
        db.execSQL(create_notes_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // 1. THÊM GHI CHÚ (addNote)
    public void addNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CONTENT, note.getContent());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // 2. LẤY TẤT CẢ GHI CHÚ (getAllNotes)
    public List<Note> getAllNotes() {
        List<Note> noteList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Note note = new Note(
                        cursor.getInt(0),   // Id
                        cursor.getString(1) // Content
                );
                noteList.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return noteList;
    }

    // 3. XÓA GHI CHÚ (deleteNote)
    public void deleteNote(int noteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + " = ?", new String[]{String.valueOf(noteId)});
        db.close();
    }
    // 4. SỬA GHI CHÚ (updateNote)
    public int updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CONTENT, note.getContent());

        // Cập nhật dòng có ID tương ứng
        return db.update(TABLE_NAME, values, KEY_ID + " = ?", new String[]{String.valueOf(note.getId())});
    }
}