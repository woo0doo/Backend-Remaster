package usw.suwiki.domain.exam.repository;

import usw.suwiki.domain.exam.entity.ExamPosts;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.user.user.entity.User;
import usw.suwiki.global.PageOption;

import java.util.List;

public interface ExamPostsRepository {
    void save(ExamPosts examPosts);

    ExamPosts findById(Long id);

    List<ExamPosts> findByLectureId(PageOption option, Long lectureId);

    List<ExamPosts> findByUserId(PageOption option, Long userId);

    boolean verifyPostsByIdx(User user, Lecture lecture);

    void delete(ExamPosts examPosts);

    List<ExamPosts> findAllByUserId(Long userId);
}
