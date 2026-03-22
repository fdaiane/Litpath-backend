package com.litpath.litpath.service;

import com.litpath.litpath.dto.openlibrary.*;
import com.litpath.litpath.model.Author;
import com.litpath.litpath.model.Book;
import com.litpath.litpath.model.Genre;
import com.litpath.litpath.repository.AuthorRepository;
import com.litpath.litpath.repository.BookRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Service
public class OpenLibraryService {

    private final WebClient openLibraryClient;
    private final WebClient googleBooksClient;
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final GenreService genreService;

    @Value("${google.books.api.key}")
    private String googleBooksApiKey;

    public OpenLibraryService(AuthorRepository authorRepository,
                               BookRepository bookRepository,
                               GenreService genreService) {
        this.openLibraryClient = WebClient.builder()
                .baseUrl("https://openlibrary.org")
                .build();
        this.googleBooksClient = WebClient.builder()
                .baseUrl("https://www.googleapis.com/books/v1")
                .build();
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.genreService = genreService;
    }

    
    @Transactional
    public Author importAuthorByName(String name) {

        
        OLAuthorSearchResult searchResult = searchAuthor(name);

        if (searchResult == null || searchResult.getDocs() == null || searchResult.getDocs().isEmpty()) {
            throw new RuntimeException("Autor não encontrado na OpenLibrary: " + name);
        }

        OLAuthorDoc doc = searchResult.getDocs().get(0);
        String authorKey = doc.getKey();

        
        OLAuthorDetail detail = getAuthorDetail(authorKey);

        
        Author author = new Author();
        author.setName(detail.getName() != null ? detail.getName() : doc.getName());
        author.setPhotoUrl(getPhotoUrl(authorKey));

        
        if (detail.getBio() instanceof String) {
            author.setBiography((String) detail.getBio());
        } else if (detail.getBio() instanceof java.util.Map) {
            Object value = ((java.util.Map<?, ?>) detail.getBio()).get("value");
            if (value != null) author.setBiography(value.toString());
        }

        
        if (detail.getBirthDate() != null) {
            author.setBirthDate(parseDateSafely(detail.getBirthDate()));
        }

        author = authorRepository.save(author);

        
        Set<String> titulosJaSalvos = new HashSet<>();
        importGoogleBooks(author, name, "pt", titulosJaSalvos);
        importGoogleBooks(author, name, "en", titulosJaSalvos);

        
        return authorRepository.findById(author.getId()).orElse(author);
    }

    
    private void importGoogleBooks(Author author, String authorName,
                                    String langRestrict, Set<String> titulosJaSalvos) {

        GoogleBooksResult result = googleBooksClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/volumes")
                        .queryParam("q", "inauthor:\"" + authorName + "\"")
                        .queryParam("langRestrict", langRestrict)
                        .queryParam("maxResults", 40)
                        .queryParam("orderBy", "relevance")
                        .queryParam("key", googleBooksApiKey)
                        .build())
                .retrieve()
                .bodyToMono(GoogleBooksResult.class)
                .block();

        if (result == null || result.getItems() == null) return;

        for (GoogleBookItem item : result.getItems()) {

            GoogleVolumeInfo info = item.getVolumeInfo();
            if (info == null || info.getTitle() == null) continue;

            
            if (!isLatinTitle(info.getTitle())) continue;

            
            String tituloNormalizado = info.getTitle().trim().toLowerCase();
            if (titulosJaSalvos.contains(tituloNormalizado)) continue;
            titulosJaSalvos.add(tituloNormalizado);

            Book book = new Book();
            book.setTitle(info.getTitle());
            book.setAuthor(author);

            
            if (info.getDescription() != null) {
                book.setSynopsis(info.getDescription());
            }

            
            if (info.getPublishedDate() != null) {
                book.setPublicationYear(parseYearSafely(info.getPublishedDate()));
            }

            
            if (info.getImageLinks() != null && info.getImageLinks().getThumbnail() != null) {
                String cover = info.getImageLinks().getThumbnail()
                        .replace("http://", "https://")
                        .replace("zoom=1", "zoom=3");
                book.setCoverUrl(cover);
            }

            
            Set<Genre> genres = new HashSet<>();
            if (info.getCategories() != null) {
                info.getCategories().stream()
                        .limit(3)
                        .forEach(category -> {
                            String normalized = capitalize(category);
                            Genre genre = genreService.findOrCreate(normalized);
                            genres.add(genre);
                        });
            }
            book.setGenres(genres);

            bookRepository.save(book);
        }
    }

    
    public OLAuthorSearchResult searchAuthor(String name) {
        return openLibraryClient.get()
                .uri("/search/authors.json?q={name}&limit=5", name)
                .retrieve()
                .bodyToMono(OLAuthorSearchResult.class)
                .block();
    }

    
    public OLAuthorDetail getAuthorDetail(String authorKey) {
        String path = authorKey.startsWith("/authors/") ? authorKey : "/authors/" + authorKey;
        return openLibraryClient.get()
                .uri(path + ".json")
                .retrieve()
                .bodyToMono(OLAuthorDetail.class)
                .block();
    }

    
    public String getPhotoUrl(String authorKey) {
        String id = authorKey.replace("/authors/", "");
        return "https://covers.openlibrary.org/a/olid/" + id + "-L.jpg";
    }

    private LocalDate parseDateSafely(String dateStr) {
        String[] patterns = {"d MMMM yyyy", "MMMM d, yyyy", "yyyy", "d MMM yyyy"};
        for (String pattern : patterns) {
            try {
                if (pattern.equals("yyyy")) {
                    return LocalDate.of(Integer.parseInt(dateStr.trim()), 1, 1);
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
                return LocalDate.parse(dateStr.trim(), formatter);
            } catch (DateTimeParseException | NumberFormatException ignored) {
            }
        }
        return null;
    }

    private Integer parseYearSafely(String dateStr) {
        if (dateStr == null) return null;
        String cleaned = dateStr.replaceAll(".*?(\\d{4}).*", "$1");
        try {
            return Integer.parseInt(cleaned);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private boolean isLatinTitle(String title) {
        return title.chars().allMatch(c ->
                Character.UnicodeBlock.of(c) == Character.UnicodeBlock.BASIC_LATIN ||
                Character.UnicodeBlock.of(c) == Character.UnicodeBlock.LATIN_1_SUPPLEMENT ||
                Character.UnicodeBlock.of(c) == Character.UnicodeBlock.LATIN_EXTENDED_A ||
                Character.UnicodeBlock.of(c) == Character.UnicodeBlock.LATIN_EXTENDED_B
        );
    }
}