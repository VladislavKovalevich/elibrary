package by.vlad.elibrary.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Year;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"id"})
@Builder
@Entity
@Table(name = "books", schema = "e_library")
public class Book implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "copies_number")
    private Integer copiesNumber;

    @Column(name = "release_year")
    private Year releaseYear;

    @Column(name = "number_of_pages")
    private Integer numberOfPages;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "genre_id", unique = false)
    private Genre genre;

    @ManyToOne
    @JoinColumn(name = "author_id", unique = false)
    private Author author;

    @ManyToOne
    @JoinColumn(name = "publisher_id", unique = false)
    private Publisher publisher;


    public Book(Long id, Integer copiesNumber) {
        this.id = id;
        this.copiesNumber = copiesNumber;
    }
}
