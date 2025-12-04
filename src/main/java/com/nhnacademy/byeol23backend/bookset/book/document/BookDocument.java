package com.nhnacademy.byeol23backend.bookset.book.document;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Document(indexName = "byeol23-books")
public class BookDocument {
    @Id
    private String id;

    @Setter
    @Field(type = FieldType.Text)
    private String title;

    @Setter
    @Field(type = FieldType.Text)
    private String description;

    @Setter
    @Field(type = FieldType.Text)
    private List<String> author;

    @Setter
    @Field(type = FieldType.Keyword)
    private List<String> translator;

    @Setter
    @Field(type = FieldType.Keyword)
    private String isbn;

    @Setter
    @Field(type = FieldType.Integer)
    private int regularPrice;

    @Setter
    @Field(type = FieldType.Integer)
    private int salePrice;

    @Setter
    @Field(type = FieldType.Keyword)
    private String publisher;

    @Setter
    @Field(type = FieldType.Date)
    private LocalDate publishedAt;

    @Setter
    @Field(type = FieldType.Text)
    private List<String> tagNames;

    @Field(type = FieldType.Text)
    private List<String> pathIds;

    @Setter
    @Field(type = FieldType.Text)
    private List<String> pathNames;

    @Setter
    @Field(type = FieldType.Long)
    private long viewCount;

    @Setter
    @Field(type = FieldType.Integer)
    private int reviewCount;

    @Setter
    @Field(type = FieldType.Float)
    private float ratingAverage;

    @Setter
    @Field(type = FieldType.Keyword)
    private String bookStatus;

    @Setter
    @Field(type = FieldType.Keyword, index = false)
    private String imageUrl;

    @Setter
    @Field(type = FieldType.Dense_Vector, dims = 1024)
    private float[] embedding;
}
