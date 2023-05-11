package usw.suwiki.domain.evaluation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.evaluation.service.dto.EvaluatePostsToLecture;
import usw.suwiki.domain.evaluation.controller.dto.EvaluatePostsSaveDto;
import usw.suwiki.domain.evaluation.controller.dto.EvaluatePostsUpdateDto;
import usw.suwiki.domain.evaluation.controller.dto.EvaluateResponseByLectureIdDto;
import usw.suwiki.domain.evaluation.controller.dto.EvaluateResponseByUserIdxDto;
import usw.suwiki.domain.evaluation.domain.EvaluatePosts;
import usw.suwiki.domain.evaluation.domain.repository.EvaluatePostsRepository;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.lecture.service.LectureCRUDService;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.service.UserCRUDService;
import usw.suwiki.global.PageOption;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.exception.errortype.EvaluatePostException;

import java.util.ArrayList;
import java.util.List;

import static usw.suwiki.global.exception.ExceptionType.USER_POINT_LACK;

@Service
@RequiredArgsConstructor
public class EvaluatePostService {

    private final EvaluatePostCRUDService evaluatePostCRUDService;
    private final LectureCRUDService lectureCRUDService;
    private final UserCRUDService userCRUDService;

    @Transactional
    public void save(EvaluatePostsSaveDto evaluatePostData, Long userIdx, Long lectureId) {
        Lecture lecture = lectureCRUDService.loadLectureFromId(lectureId);
        User user = userCRUDService.loadUserFromUserIdx(userIdx);
        EvaluatePosts evaluatePost = createEvaluatePost(evaluatePostData, user, lecture);

        user.updateWritingEvaluatePost();
        EvaluatePostsToLecture lectureEvaluation = new EvaluatePostsToLecture(evaluatePost);
        updateLectureEvaluationIfCreateNewPost(lectureEvaluation);

        evaluatePostCRUDService.save(evaluatePost);
    }

    @Transactional
    public void update(Long evaluateIdx, EvaluatePostsUpdateDto evaluatePostUpdateData) {
        EvaluatePosts post = evaluatePostCRUDService.loadEvaluatePostFromEvaluatePostIdx(evaluateIdx);
        EvaluatePostsToLecture beforeUpdated = new EvaluatePostsToLecture(post);
        post.update(evaluatePostUpdateData);

        EvaluatePostsToLecture updated = new EvaluatePostsToLecture(post);
        updateLectureEvaluationIfUpdatePost(beforeUpdated, updated);
    }

    @Transactional(readOnly = true)
    public List<EvaluateResponseByLectureIdDto> readEvaluatePostsByLectureId(
            PageOption option, Long lectureId) {
        List<EvaluateResponseByLectureIdDto> response = new ArrayList<>();
        List<EvaluatePosts> evaluatePosts = evaluatePostCRUDService.loadEvaluatePostsFromLectureIdx(option, lectureId);
        for (EvaluatePosts post : evaluatePosts) {
            response.add(new EvaluateResponseByLectureIdDto(post));
        }
        return response;
    }

    @Transactional(readOnly = true)
    public List<EvaluateResponseByUserIdxDto> readEvaluatePostsByUserId(
        PageOption option, Long userId) {

        List<EvaluateResponseByUserIdxDto> response = new ArrayList<>();
        List<EvaluatePosts> evaluatePosts = evaluatePostCRUDService.loadEvaluatePostsFromUserIdxAndOption(option, userId);
        for (EvaluatePosts post : evaluatePosts) {
            EvaluateResponseByUserIdxDto data = new EvaluateResponseByUserIdxDto(post);
            data.setSemesterList(post.getLecture().getSemester());
            response.add(data);
        }
        return response;
    }

    public boolean verifyIsUserWriteEvaluatePost(Long userIdx, Long lectureId) {
        Lecture lecture = lectureCRUDService.loadLectureFromId(lectureId);
        User user = userCRUDService.loadUserFromUserIdx(userIdx);
        return evaluatePostCRUDService.verifyIsUserWriteEvaluatePost(user, lecture);
    }

    public void executeDeleteEvaluatePost(Long evaluateIdx, Long userIdx) {
        EvaluatePosts evaluatePost = evaluatePostCRUDService.loadEvaluatePostFromEvaluatePostIdx(evaluateIdx);
        User user = userCRUDService.loadUserFromUserIdx(userIdx);
        user.decreasePointAndWrittenEvaluationByDeleteEvaluatePosts();

        evaluatePostCRUDService.delete(evaluatePost);
    }

    public void updateLectureEvaluationIfCreateNewPost(EvaluatePostsToLecture post) {
        Lecture lecture = lectureCRUDService.loadLectureFromIdPessimisticLock(post.getLectureId());
        lecture.handleLectureEvaluationIfNewPost(post);
    }

    public void updateLectureEvaluationIfUpdatePost(EvaluatePostsToLecture beforeUpdatePost, EvaluatePostsToLecture post) {
        Lecture lecture = lectureCRUDService.loadLectureFromIdPessimisticLock(post.getLectureId());
        lecture.handleLectureEvaluationIfUpdatePost(beforeUpdatePost, post);
    }

    public void updateLectureEvaluationIfDeletePost(EvaluatePostsToLecture post) {
        Lecture lecture = lectureCRUDService.loadLectureFromIdPessimisticLock(post.getLectureId());
        lecture.handleLectureEvaluationIfDeletePost(post);
    }

    private EvaluatePosts createEvaluatePost(EvaluatePostsSaveDto evaluatePostData, User user, Lecture lecture) {
        EvaluatePosts evaluatePost = new EvaluatePosts(evaluatePostData);
        evaluatePost.setUser(user);
        evaluatePost.setLecture(lecture);

        return evaluatePost;
    }
}
