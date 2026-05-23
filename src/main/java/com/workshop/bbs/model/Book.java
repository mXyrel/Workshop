package com.workshop.bbs.model;

/**
 * Represents a book in the library.
 */
public class Book {
    private int id;
    private String title;
    private String author;
    private String isbn;
    private int totalCopies;
    private int availableCopies;
    private String category;

    public Book() {}

    public Book(int id, String title, String author, String isbn,
                int totalCopies, int availableCopies, String category) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.category = category;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }

    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isAvailable() { return availableCopies > 0; }

    @Override
    public String toString() { return title + " by " + author; }
}
