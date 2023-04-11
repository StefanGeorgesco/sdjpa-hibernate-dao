package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Book;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;

/**
 * Created by jt on 8/29/21.
 */
@Component
public class BookDaoImpl implements BookDao {
    private final EntityManagerFactory emf;

    public BookDaoImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public List<Book> findAll() {
        EntityManager em = getEntityManager();
        List<Book> books;
        try {
            TypedQuery<Book> typedQuery = em.createNamedQuery("book_find_all", Book.class);
            books = typedQuery.getResultList();
        } finally {
            em.close();
        }
        return books;
    }

    @Override
    public Book findByISBN(String isbn) {
        EntityManager em = getEntityManager();
        Book book;

        try {
            TypedQuery<Book> query = em.createQuery("select b from Book b where b.isbn = :isbn", Book.class);
            query.setParameter("isbn", isbn);
            book = query.getSingleResult();
        } finally {
            em.close();
        }
        return book;
    }

    @Override
    public Book getById(Long id) {
        EntityManager em = getEntityManager();
        Book book;
        try {
            book = getEntityManager().find(Book.class, id);
        } finally {
            em.close();
        }
        return book;
    }

    @Override
    public Book findBookByTitle(String title) {
        EntityManager em = getEntityManager();
        Book book;
        try {
            TypedQuery<Book> query = em.createNamedQuery("book_find_by_title", Book.class);
            query.setParameter("title", title);
            book = query.getResultList().get(0);
        } finally {
            em.close();
        }
        return book;
    }

    @Override
    public Book saveNewBook(Book book) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(book);
            em.flush();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return book;
    }

    @Override
    public Book updateBook(Book book) {
        EntityManager em = getEntityManager();
        Book savedBook;
        try {
            em.getTransaction().begin();
            em.merge(book);
            em.flush();
            em.clear();
            savedBook = em.find(Book.class, book.getId());
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return savedBook;
    }

    @Override
    public void deleteBookById(Long id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Book book = em.find(Book.class, id);
            em.remove(book);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    private EntityManager getEntityManager(){
        return emf.createEntityManager();
    }
}