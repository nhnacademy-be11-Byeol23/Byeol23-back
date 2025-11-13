package com.nhnacademy.byeol23backend.bookset.book.document;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@Document(indexName = "byeol23-books")
public class BookDocument {
    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Keyword)
    private List<String> author;

    @Field(type = FieldType.Keyword)
    private List<String> translator;

    @Field(type = FieldType.Keyword)
    private String isbn;

    @Field(type = FieldType.Integer)
    private int regularPrice;

    @Field(type = FieldType.Integer)
    private int salePrice;

    @Field(type = FieldType.Keyword)
    private String publisher;

    @Field(type = FieldType.Date)
    private LocalDate publishedAt;

    @Field(type = FieldType.Text)
    private List<String> tagNames;

    @Field(type = FieldType.Text)
    private List<String> pathIds;

    @Field(type = FieldType.Text)
    private List<String> pathNames;

    @Field(type = FieldType.Long)
    private long viewCount;

    @Field(type = FieldType.Integer)
    private int reviewCount;

    @Field(type = FieldType.Float)
    private float ratingAverage;

    @Field(type = FieldType.Boolean)
    private boolean isSoldOut;

    @Setter
    @Field(type = FieldType.Dense_Vector, dims = 1024)
    private float[] embedding;
}
