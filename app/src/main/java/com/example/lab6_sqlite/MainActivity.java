package com.example.lab6_sqlite;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText edtNote;
    Button btnSave;
    ListView lvNotes;

    DatabaseHandler db;
    List<Note> noteList;
    ArrayAdapter<Note> adapter;

    // Biến để lưu trữ Ghi chú đang được chọn để SỬA
    Note editingNote = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtNote = findViewById(R.id.edtNote);
        btnSave = findViewById(R.id.btnSave);
        lvNotes = findViewById(R.id.lvNotes);

        db = new DatabaseHandler(this);
        noteList = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, noteList);
        lvNotes.setAdapter(adapter);

        loadNotes();

        // 1. SỰ KIỆN BẤM NÚT LƯU HOẶC CẬP NHẬT
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = edtNote.getText().toString().trim();

                if (content.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập nội dung!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (editingNote == null) {
                    // TRƯỜNG HỢP 1: THÊM MỚI (Biến editingNote đang rỗng)
                    db.addNote(new Note(content));
                    Toast.makeText(MainActivity.this, "Đã lưu ghi chú mới!", Toast.LENGTH_SHORT).show();
                } else {
                    // TRƯỜNG HỢP 2: LƯU CẬP NHẬT (Đang sửa một ghi chú cũ)
                    editingNote.setContent(content); // Gắn nội dung mới vào
                    db.updateNote(editingNote); // Lưu vào CSDL
                    Toast.makeText(MainActivity.this, "Đã cập nhật thành công!", Toast.LENGTH_SHORT).show();

                    // Sửa xong thì phải Reset trạng thái về như ban đầu
                    editingNote = null;
                    btnSave.setText("Lưu Ghi Chú");
                    btnSave.setBackgroundTintList(getResources().getColorStateList(android.R.color.holo_green_light));
                }

                // Dọn dẹp ô chữ và tải lại danh sách
                edtNote.setText("");
                loadNotes();
            }
        });

        // 2. SỰ KIỆN NHẤN 1 LẦN ĐỂ SỬA (MỚI THÊM)
        lvNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Lấy ghi chú đang được bấm
                editingNote = noteList.get(position);

                // Bắn chữ ngược lên ô nhập liệu
                edtNote.setText(editingNote.getContent());

                // Đổi tên nút thành "Cập Nhật" và đổi màu cho dễ phân biệt
                btnSave.setText("Cập Nhật Ghi Chú");
                btnSave.setBackgroundTintList(getResources().getColorStateList(android.R.color.holo_orange_light)); // Đổi nút sang màu cam

                // Kéo con trỏ chuột về cuối đoạn text để tiện gõ tiếp
                edtNote.setSelection(edtNote.getText().length());
            }
        });

        // 3. SỰ KIỆN NHẤN GIỮ ĐỂ XÓA (Giữ nguyên như cũ)
        lvNotes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Note selectedNote = noteList.get(position);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Xóa ghi chú")
                        .setMessage("Bạn có muốn xóa ghi chú này không?")
                        .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.deleteNote(selectedNote.getId());
                                Toast.makeText(MainActivity.this, "Đã xóa!", Toast.LENGTH_SHORT).show();

                                // Nếu lỡ đang bấm Sửa cái ghi chú đó mà lại bấm Xóa thì reset luôn form
                                if (editingNote != null && editingNote.getId() == selectedNote.getId()) {
                                    editingNote = null;
                                    edtNote.setText("");
                                    btnSave.setText("Lưu Ghi Chú");
                                    btnSave.setBackgroundTintList(getResources().getColorStateList(android.R.color.holo_green_light));
                                }

                                loadNotes();
                            }
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
                return true;
            }
        });
    }

    private void loadNotes() {
        noteList.clear();
        noteList.addAll(db.getAllNotes());
        adapter.notifyDataSetChanged();
    }
}