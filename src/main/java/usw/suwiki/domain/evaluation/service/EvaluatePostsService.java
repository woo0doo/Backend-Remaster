package usw.suwiki.domain.evaluation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.evaluation.EvaluatePostsToLecture;
import usw.suwiki.domain.evaluation.dto.EvaluatePostsSaveDto;
import usw.suwiki.domain.evaluation.dto.EvaluatePostsUpdateDto;
import usw.suwiki.domain.evaluation.dto.EvaluateResponseByLectureIdDto;
import usw.suwiki.domain.evaluation.dto.EvaluateResponseByUserIdxDto;
import usw.suwiki.domain.evaluation.entity.EvaluatePosts;
import usw.suwiki.domain.evaluation.repository.EvaluatePostsRepository;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.lecture.service.LectureCRUDService;
import usw.suwiki.domain.lecture.service.LectureService;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.service.UserCRUDService;
import usw.suwiki.global.PageOption;
import usw.suwiki.global.exception.ExceptionType;
import usw.suwiki.global.exception.errortype.AccountException;

import java.util.ArrayList;
import java.util.List;

import static usw.suwiki.global.exception.ExceptionType.USER_POINT_LACK;

@Transactional
@RequiredArgsConstructor
@Service
public class EvaluatePostsService {

    private final EvaluatePostsRepository evaluatePostsRepository;
    private final LectureCRUDService lectureCRUDService;
    private final LectureService lectureService;
    private final UserCRUDService userCRUDService;

    public void save(EvaluatePostsSaveDto evaluatePostsSaveDto, Long userIdx, Long lectureId) {
        EvaluatePosts posts = new EvaluatePosts(evaluatePostsSaveDto);
        Lecture lecture = lectureCRUDService.loadLectureFromId(lectureId);
        User user = userCRUDService.loadUserFromUserIdx(userIdx);

        if (lecture == null) {
            throw new AccountException(ExceptionType.NOT_EXISTS_LECTURE);
        }

        posts.setLecture(lecture);
        posts.setUser(user);
        user.updateWritingEvaluatePost();

        EvaluatePostsToLecture lectureEvaluation = new EvaluatePostsToLecture(posts);
        lectureService.updateLectureEvaluationIfCreateNewPost(lectureEvaluation);
        evaluatePostsRepository.save(posts);
    }

    public EvaluatePosts findById(Long evaluateIdx) {
        return evaluatePostsRepository.findById(evaluateIdx);
    }

    public void update(Long evaluateIdx, EvaluatePostsUpdateDto dto) {
        EvaluatePosts post = evaluatePostsRepository.findById(evaluateIdx);
        EvaluatePostsToLecture beforeUpdated = new EvaluatePostsToLecture(post);
        post.update(dto);
        EvaluatePostsToLecture updated = new EvaluatePostsToLecture(post);
        lectureService.updateLectureEvaluationIfUpdatePost(beforeUpdated, updated);
    }

    public List<EvaluateResponseByLectureIdDto> findEvaluatePostsByLectureId(
            PageOption option,
            Long lectureId
    ) {
        List<EvaluateResponseByLectureIdDto> dtoList = new ArrayList<>();
        List<EvaluatePosts> list = evaluatePostsRepository.findByLectureId(option, lectureId);
        for (EvaluatePosts post : list) {
            dtoList.add(new EvaluateResponseByLectureIdDto(post));
        }
        return dtoList;
    }

    public List<EvaluateResponseByUserIdxDto> findEvaluatePostsByUserId(
            PageOption option,
            Long userId
    ) {
        List<EvaluateResponseByUserIdxDto> dtoList = new ArrayList<>();
        List<EvaluatePosts> list = evaluatePostsRepository.findByUserId(option, userId);
        for (EvaluatePosts post : list) {
            EvaluateResponseByUserIdxDto dto = new EvaluateResponseByUserIdxDto(post);
            dto.setSemesterList(post.getLecture().getSemester());
            dtoList.add(dto);
        }
        return dtoList;
    }

    public boolean verifyIsUserWriteEvaluatePost(Long userIdx, Long lectureId) {
        Lecture lecture = lectureCRUDService.loadLectureFromId(lectureId);
        User user = userCRUDService.loadUserFromUserIdx(userIdx);
        return evaluatePostsRepository.verifyPostsByIdx(user, lecture);
    }

    public void deleteFromUserIdx(Long userIdx) {
        List<EvaluatePosts> list = evaluatePostsRepository.findAllByUserId(userIdx);
        if (!list.isEmpty()) {
            for (EvaluatePosts evaluatePosts : list) {
                EvaluatePostsToLecture lectureEvaluation = new EvaluatePostsToLecture(evaluatePosts);
                lectureService.updateLectureEvaluationIfDeletePost(lectureEvaluation);
                evaluatePostsRepository.delete(evaluatePosts);
            }
        }
    }

    public void executeDeleteEvaluatePost(Long evaluateIdx, Long userIdx) {
        EvaluatePosts post = evaluatePostsRepository.findById(evaluateIdx);
        User user = userCRUDService.loadUserFromUserIdx(userIdx);
        user.decreasePointAndWrittenEvaluationByDeleteEvaluatePosts();
        evaluatePostsRepository.delete(post);
        throw new AccountException(USER_POINT_LACK);
    }

    public EvaluatePosts loadEvaluatePostsFromEvaluatePostsIdx(Long evaluateIdx) {
        return evaluatePostsRepository.findById(evaluateIdx);
    }
}
