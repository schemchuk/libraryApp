package com.telran.libraryapp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class LibraryController {
    private List<Book> library;

    public LibraryController() {
        library = new ArrayList<>();
        library.add(new Book("Java in action", "author", "Java", 2,"1"));
        library.add(new Book("Algoritms", "author", "Java", 1,"5"));
        library.add(new Book("Desing Patterns", "author", "Java", 4,"2"));
        library.add(new Book("Did Pachom i traktor v nochnom", "author", "Detectives", 3,"3"));
        library.add(new Book("Harry Potter and the Philosopher's stone", "author", "Fantasy", 4,"4"));
        library.add(new Book("Tralya-lya", "" , "CategoryUps" , 5, "6"));
        library.add(new Book("Tralya-lya-lya-lya", "" , "CategoryUps" , 5, "7"));
        library.add(new Book("", "" , "CategoryUps" , 5, "8"));
        library.add(new Book("", "" , "CategoryUps" , 5, "9"));




    }

    @GetMapping("/home")
    public String  helloMessage() {
        return  "Hello from my excellent website";
    }

    @GetMapping("/all")
    public List<Book> getAll() {
        return library;
    }

    @GetMapping("/all/{category}")
    public List<Book> getAllByCategory(@PathVariable String category) {
        List<Book> result = library.stream()
                .filter(book -> book.getCategory().equals(category))
                .toList();
        if (result.isEmpty()) {
            throw new RuntimeException("No books by category " + category + " found. Try search by another category");
        }
        return result;
    }

    @GetMapping("/searchByTitle")
    public List<Book> getAllByTitle(@RequestParam String title, @RequestParam(required = false) Integer amount) {

        return  library.stream()
                .filter(book -> book.getTitle().startsWith(title))
                .filter(book -> amount == null ? true : book.getAvailableAmount() >= amount)
                .toList();
    }

    @PostMapping("/all")
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        library.add(book);
       return new ResponseEntity<>(book, HttpStatus.CREATED);
    }

    @PutMapping("/all")
    public ResponseEntity<Book> updateBook(@RequestBody Book book) {

        if (library.contains(book)) {

            int index = library.indexOf(book);
            library.set(index, book);
            return new ResponseEntity<>(book, HttpStatus.OK);
        } else {
            library.add(book);
            return new ResponseEntity<>(book, HttpStatus.CREATED);
        }
    }

    @PatchMapping("/all")
    public ResponseEntity<Book> updateAmountOfBook(@RequestParam String isbn, @RequestParam Integer amount) {

        Optional<Book> book = library.stream()
                .filter(b -> b.getIsbn().equals(isbn))
                .peek(b -> b.setAvailableAmount(amount))
                .findAny();

        if (book.isPresent()) {
            return new ResponseEntity<>(book.get(), HttpStatus.OK);
        } else {
           return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/all")
    public ResponseEntity<?> deleteByISBN(@RequestParam String isbn) {
        library.removeIf(book -> book.getIsbn().equals(isbn));
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
//REST запрос на вывод одной книги по ее isbn.

    @GetMapping("/searchByISBN")
    public List<Book> getByISBN(@RequestParam  String isbn) {

        return  library.stream()
                .filter(book -> book.getIsbn().equals(isbn))
                .toList();
    }
//
//REST запрос на вывод общего числа книг в библиотеке (с учетом копий).

    @GetMapping("/numbersExempl")
    public int getBooksNumber() {
        return library.stream()
                .mapToInt(b -> b.getAvailableAmount())
                .sum();
    }
//
//REST запрос на вывод общего числа книг в отдельной категории.

    @GetMapping("/numbersExempl/{category}")
    public int getBooksNumberInCategorie( @PathVariable String category) {
        return library.stream()
                .filter(b -> b.getCategory().equals(category))
                .mapToInt(b -> b.getAvailableAmount())
                .sum();
    }
//
//REST запрос на заполнение всех пустых полей author значением "Unknown".
//

    @PatchMapping("/updateAuthors")
    public ResponseEntity<List<Book>> updateEmptyAuthors() {
        library.stream()
                .forEach(book -> {
                    if (book.getAuthor() == null || book.getAuthor().isEmpty() ){
                        book.setAuthor("Unknown");
                    }
                });
        return new ResponseEntity<>(library, HttpStatus.OK);
    }
//REST запрос на удаление из списка книг всех книг, у которых не указан title.

    @DeleteMapping("/deleteWithoutTitle")
    public ResponseEntity<?> deleteTitleIsEmpty() {
        library.removeIf(book -> book.getTitle() == null || book.getTitle().isEmpty());
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
//Проверить работу запросов через Postman.


}

