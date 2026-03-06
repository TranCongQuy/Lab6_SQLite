package com.example.lab6_sqlite;

public class Note {
    private int id;
    private String content;

    // Constructor đầy đủ
    public Note(int id, String content) {
        this.id = id;
        this.content = content;
    }

    // Constructor dùng khi thêm mới (không cần ID vì ID tự tăng)
    public Note(String content) {
        this.content = content;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    // Ghi đè hàm này để ListView biết cách in nội dung chữ ra màn hình
    @Override
    public String toString() {
        return content;
    }
}