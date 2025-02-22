package usw.suwiki.domain.evaluation.domain.repository;

import org.springframework.stereotype.Repository;
import usw.suwiki.domain.evaluation.domain.EvaluatePosts;
import usw.suwiki.domain.evaluation.domain.repository.EvaluatePostsRepository;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.global.PageOption;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaEvaluatePostsRepository implements EvaluatePostsRepository {

    private final EntityManager em;

    public JpaEvaluatePostsRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(EvaluatePosts EvaluatePosts) {
        em.persist(EvaluatePosts);
    }

    @Override
    public EvaluatePosts findById(Long id) {
        return em.find(EvaluatePosts.class, id);
    }

    @Override
    public List<EvaluatePosts> findByLectureId(PageOption option, Long lectureId) {
        Optional<Integer> page = option.getPageNumber();
        if (page.isEmpty()) {
            page = Optional.of(1);
        }

        List resultList = em.createQuery("SELECT p from EvaluatePosts p join p.lecture l WHERE l.id = :lectureId ORDER BY p.modifiedDate DESC")
                .setParameter("lectureId", lectureId)
                .setFirstResult((page.get() - 1) * 10)
                .setMaxResults(10)
                .getResultList();

        return resultList;
    }

    @Override
    public List<EvaluatePosts> findByUserId(PageOption option, Long userId) {
        Optional<Integer> page = option.getPageNumber();
        if (page.isEmpty()) {
            page = Optional.of(1);
        }

        List resultList = em.createQuery(
                "SELECT p from EvaluatePosts p join p.user u WHERE u.id = :id ORDER BY p.modifiedDate DESC")
            .setParameter("id", userId)
            .setFirstResult((page.get() - 1) * 10)
            .setMaxResults(10)
            .getResultList();

        return resultList;
    }

    @Override
    public boolean isExistPostsByIdx(User user, Lecture lecture) {
        List resultList = em.createQuery(
            "SELECT p from EvaluatePosts p WHERE p.user = :user AND p.lecture = :lecture")
                .setParameter("user", user)
                .setParameter("lecture", lecture)
                .getResultList();
        if (resultList.isEmpty()) {
            return false;
        } else return true;
    }

    @Override
    public void delete(EvaluatePosts evaluatePosts) {
        em.remove(evaluatePosts);
    }

    @Override
    public List<EvaluatePosts> findAllByUserId(Long userId) {

        List resultList = em.createQuery(
            "SELECT p from EvaluatePosts p join p.user u WHERE u.id = :id ORDER BY p.modifiedDate DESC")
                .setParameter("id", userId)
                .getResultList();

        return resultList;
    }
}
