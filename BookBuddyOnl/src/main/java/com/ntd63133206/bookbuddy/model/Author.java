package com.ntd63133206.bookbuddy.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;
    
    @Column(name = "author_image")
    private String authorImage;

    @ManyToMany(mappedBy = "authors")
    private Set<Book> books = new HashSet<>();
    
    @Column(name = "description", length = 1000)
    private String description;
    
    public Author() {
        // Default constructor
    }


    public Author(String name, String description) {
		super();
		this.name = name;
		this.description = description;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getAuthorImage() {
        return authorImage;
    }

    public void setAuthorImage(String authorImage) {
        this.authorImage = authorImage;
    }
    
    public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }

    public void addBook(Book book) {
        this.books.add(book);
        book.getAuthors().add(this);
    }

    public void removeBook(Book book) {
        this.books.remove(book);
        book.getAuthors().remove(this);
    }
    
}
