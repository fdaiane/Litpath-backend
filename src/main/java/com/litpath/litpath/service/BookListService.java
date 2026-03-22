package com.litpath.litpath.service;

import com.litpath.litpath.dto.BookResponseDTO;
import com.litpath.litpath.dto.UserBookListResponseDTO;
import com.litpath.litpath.exception.BusinessException;
import com.litpath.litpath.exception.ResourceNotFoundException;
import com.litpath.litpath.model.*;
import com.litpath.litpath.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookListService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final UserBookListRepository bookListRepository;
    private final UserBookListItemRepository bookListItemRepository;
    private final BookService bookService;

    public BookListService(UserRepository userRepository,
                            BookRepository bookRepository,
                            UserBookListRepository bookListRepository,
                            UserBookListItemRepository bookListItemRepository,
                            BookService bookService) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.bookListRepository = bookListRepository;
        this.bookListItemRepository = bookListItemRepository;
        this.bookService = bookService;
    }

    
    public List<UserBookListResponseDTO> getAllLists(String email) {
        User user = findUserByEmail(email);
        return bookListRepository.findByUserId(user.getId())
                .stream()
                .map(this::toListDTO)
                .collect(Collectors.toList());
    }

    
    public UserBookListResponseDTO getList(String email, ListType listType) {
        User user = findUserByEmail(email);
        UserBookList list = bookListRepository.findByUserIdAndListType(user.getId(), listType)
                .orElseGet(() -> {
                    UserBookList emptyList = new UserBookList();
                    emptyList.setUser(user);
                    emptyList.setListType(listType);
                    return emptyList;
                });
        return toListDTO(list);
    }

    
    public UserBookListResponseDTO getListByUserId(Long userId, ListType listType) {
        return bookListRepository.findByUserIdAndListType(userId, listType)
                .map(this::toListDTO)
                .orElseGet(() -> {
                    UserBookListResponseDTO dto = new UserBookListResponseDTO();
                    dto.setListType(listType);
                    dto.setBooks(new ArrayList<>());
                    return dto;
                });
    }

    
    @Transactional
    public UserBookListResponseDTO addBookToList(String email, ListType listType, Long bookId) {
        User user = findUserByEmail(email);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Livro não encontrado!"));

        UserBookList list = bookListRepository.findByUserIdAndListType(user.getId(), listType)
                .orElseGet(() -> {
                    UserBookList newList = new UserBookList();
                    newList.setUser(user);
                    newList.setListType(listType);
                    return bookListRepository.save(newList);
                });

        if (bookListItemRepository.existsByUserBookListIdAndBookId(list.getId(), bookId)) {
            throw new BusinessException("Livro já está nessa lista!");
        }

        UserBookListItem item = new UserBookListItem();
        item.setUserBookList(list);
        item.setBook(book);
        bookListItemRepository.save(item);

        return toListDTO(list);
    }

    
    @Transactional
    public void removeBookFromList(String email, ListType listType, Long bookId) {
        User user = findUserByEmail(email);

        UserBookList list = bookListRepository.findByUserIdAndListType(user.getId(), listType)
                .orElseThrow(() -> new ResourceNotFoundException("Lista não encontrada!"));

        if (!bookListItemRepository.existsByUserBookListIdAndBookId(list.getId(), bookId)) {
            throw new ResourceNotFoundException("Livro não está nessa lista!");
        }

        bookListItemRepository.deleteByUserBookListIdAndBookId(list.getId(), bookId);
    }

    
    private UserBookListResponseDTO toListDTO(UserBookList list) {
        UserBookListResponseDTO dto = new UserBookListResponseDTO();
        dto.setId(list.getId());
        dto.setListType(list.getListType());

        List<BookResponseDTO> books = list.getItems() != null
                ? list.getItems().stream()
                        .map(item -> bookService.getBookById(item.getBook().getId()))
                        .collect(Collectors.toList())
                : List.of();

        dto.setBooks(books);
        return dto;
    }

   
    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado!"));
    }
}