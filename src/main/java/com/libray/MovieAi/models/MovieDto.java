package com.libray.MovieAi.models;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class MovieDto {
    private int id; // Add the id field

    @NotEmpty(message = "The title is required")
    private String originalTitle; // Match with original_title column

    @NotEmpty(message = "The director is required")
    private String director;

    @Min(value = 1888, message = "The release year should not be before 1888")
    private int releaseYear; // Match with release_date column

    @Min(0)
    private double voteAverage; // Match with vote_average column

    @Size(min = 10, message = "The description should be at least 10 characters")
    @Size(max = 1000, message = "The description cannot be more than 1000 characters") // Match with overview column
    private String overview;

    @NotEmpty(message = "The genre is required")
    private String genres; // Match with genres column

    private MultipartFile imageFile; // You may want to handle this separately as it's not directly related to database columns

    private String posterPath; // Match with poster_path column

    // Getter and setter methods for id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter and setter methods for other fields
    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
}
