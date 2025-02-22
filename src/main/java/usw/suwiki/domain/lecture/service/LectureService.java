package usw.suwiki.domain.lecture.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import usw.suwiki.domain.evaluation.service.dto.EvaluatePostsToLecture;
import usw.suwiki.domain.lecture.controller.dto.LectureDetailResponseDto;
import usw.suwiki.domain.lecture.domain.repository.dao.LecturesAndCountDao;
import usw.suwiki.domain.lecture.controller.dto.LectureResponseDto;
import usw.suwiki.domain.lecture.controller.dto.LectureAndCountResponseForm;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.lecture.controller.dto.LectureFindOption;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LectureService {

    private final LectureCRUDService lectureCRUDService;

    @Transactional(readOnly = true)
    public LectureAndCountResponseForm readLectureByKeyword(String keyword, LectureFindOption option) {
        if (option.passMajorFiltering()) {
            return readLectureByKeywordAndOption(keyword, option);
        }
        return readLectureByKeywordAndMajor(keyword, option);
    }

    @Transactional(readOnly = true)
    public LectureAndCountResponseForm readAllLecture(LectureFindOption option) {
        if (option.passMajorFiltering()) {
            return readAllLectureByOption(option);
        }
        return readAllLectureByMajorType(option);
    }

    @Transactional(readOnly = true)
    public LectureDetailResponseDto readLectureDetail(Long id) {
        Lecture lecture = lectureCRUDService.loadLectureFromId(id);
        return new LectureDetailResponseDto(lecture);
    }

    private LectureAndCountResponseForm readLectureByKeywordAndOption(String keyword, LectureFindOption option) {
        LecturesAndCountDao lectureInfo = lectureCRUDService.loadLectureByKeywordAndOption(keyword, option);
        return createLectureResponseForm(lectureInfo);
    }

    private LectureAndCountResponseForm readLectureByKeywordAndMajor(String searchValue, LectureFindOption option) {
        LecturesAndCountDao lectureInfo = lectureCRUDService.loadLectureByKeywordAndMajor(searchValue, option);
        return createLectureResponseForm(lectureInfo);
    }

    private LectureAndCountResponseForm readAllLectureByOption(LectureFindOption option) {
        LecturesAndCountDao lectureInfo = lectureCRUDService.loadLecturesByOption(option);
        return createLectureResponseForm(lectureInfo);
    }

    private LectureAndCountResponseForm readAllLectureByMajorType(LectureFindOption option) {
        LecturesAndCountDao lectureInfo = lectureCRUDService.loadLecturesByMajor(option);
        return createLectureResponseForm(lectureInfo);
    }

    private LectureAndCountResponseForm createLectureResponseForm(LecturesAndCountDao lectureInfo) {
        List<LectureResponseDto> dtoList = new ArrayList<>();
        for (Lecture lecture : lectureInfo.getLectureList()) {
            dtoList.add(new LectureResponseDto(lecture));
        }
        return new LectureAndCountResponseForm(dtoList, lectureInfo.getCount());
    }
}
