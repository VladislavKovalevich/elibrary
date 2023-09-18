package by.vlad.elibrary.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.Year;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"id"})
@Builder
@Entity
@Table(name = "book")
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

    @OneToOne
    @JoinColumn(name = "genre_id", unique = false)
    private Genre genre;

    @OneToOne
    @JoinColumn(name = "author_id", unique = false)
    private Author author;

    @OneToOne
    @JoinColumn(name = "publisher_id", unique = false)
    private Publisher publisher;

}
