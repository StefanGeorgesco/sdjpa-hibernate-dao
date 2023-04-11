package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;
import jakarta.persistence.Query;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;

/**
 * Created by jt on 8/28/21.
 */
@Component
public class AuthorDaoImpl implements AuthorDao {

    private final EntityManagerFactory emf;

    public AuthorDaoImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public List<Author> listAuthorByLastNameLike(String searchString) {
        EntityManager em = getEntityManager();
        List<Author> authors;

        try {
            Query query = em.createQuery("select a from Author a where a.lastName like :likeSearchString");
            query.setParameter("likeSearchString", "%" + searchString + "%");
            authors = query.getResultList();
        } finally {
            em.close();
        }
        return authors;
    }

    @Override
    public Author getById(Long id) {
        EntityManager em = getEntityManager();
        Author author;
        try {
            author = getEntityManager().find(Author.class, id);
        } finally {
            em.close();
        }
        return author;
    }

    @Override
    public Author findAuthorByName(String firstName, String lastName) {
        EntityManager em = getEntityManager();
        Author author;
        try {
            TypedQuery<Author> query = em.createQuery("SELECT a FROM Author a " +
                    "WHERE a.firstName = :firstName and a.lastName = :lastName", Author.class);
            query.setParameter("firstName", firstName);
            query.setParameter("lastName", lastName);

            author = query.getSingleResult();
        } finally {
            em.close();
        }
        return author;
    }

    @Override
    public Author saveNewAuthor(Author author) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(author);
            em.flush();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return author;
    }

    @Override
    public Author updateAuthor(Author author) {
        EntityManager em = getEntityManager();
        Author savedAuthor;
        try {
            em.joinTransaction();
            em.merge(author);
            em.flush();
            em.clear();
            savedAuthor = em.find(Author.class, author.getId());
        } finally {
            em.close();
        }
        return savedAuthor;
    }

    @Override
    public void deleteAuthorById(Long id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Author author = em.find(Author.class, id);
            em.remove(author);
            em.flush();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    private EntityManager getEntityManager(){
        return emf.createEntityManager();
    }
}















