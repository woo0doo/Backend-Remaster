package usw.suwiki.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.admin.controller.dto.UserAdminRequestDto;
import usw.suwiki.domain.admin.controller.dto.UserAdminRequestDto.*;
import usw.suwiki.domain.admin.controller.dto.UserAdminResponseDto.LoadAllReportedPostForm;
import usw.suwiki.domain.blacklistdomain.service.BlacklistDomainCRUDService;
import usw.suwiki.domain.evaluation.domain.EvaluatePosts;
import usw.suwiki.domain.evaluation.service.EvaluatePostCRUDService;
import usw.suwiki.domain.exam.domain.ExamPosts;
import usw.suwiki.domain.exam.service.ExamPostCRUDService;
import usw.suwiki.domain.postreport.EvaluatePostReport;
import usw.suwiki.domain.postreport.ExamPostReport;
import usw.suwiki.domain.postreport.service.ReportPostService;
import usw.suwiki.domain.restrictinguser.service.RestrictingUserService;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.domain.user.user.controller.dto.UserRequestDto.LoginForm;
import usw.suwiki.domain.user.user.service.UserCRUDService;
import usw.suwiki.global.exception.errortype.AccountException;
import usw.suwiki.global.jwt.JwtAgent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static usw.suwiki.global.exception.ExceptionType.PASSWORD_ERROR;
import static usw.suwiki.global.exception.ExceptionType.USER_RESTRICTED;
import static usw.suwiki.global.util.apiresponse.ApiResponseFactory.successCapitalFlag;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminBusinessService {

    private final BlacklistDomainCRUDService blacklistDomainCRUDService;
    private final UserCRUDService userCRUDService;
    private final ReportPostService reportPostService;
    private final EvaluatePostCRUDService evaluatePostCRUDService;
    private final ExamPostCRUDService examPostCRUDService;
    private final RestrictingUserService restrictingUserService;
    private final JwtAgent jwtAgent;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 관리자 로그인
     */
    public Map<String, String> executeAdminLogin(LoginForm loginForm) {
        User user = userCRUDService.loadUserFromLoginId(loginForm.loginId());
        if (user.validatePassword(bCryptPasswordEncoder, loginForm.password())) {
            if (user.getRole().getKey().equals("ADMIN")) {
                return new HashMap<>() {{
                    put("AccessToken", jwtAgent.createAccessToken(user));
                    put("UserCount", String.valueOf(userCRUDService.findAllUsersSize()));
                }};
            }
            throw new AccountException(USER_RESTRICTED);
        }
        throw new AccountException(PASSWORD_ERROR);
    }

    /**
     * 신고된 모든 게시글 조회
     */
    public LoadAllReportedPostForm executeLoadAllReportedPosts() {
        List<EvaluatePostReport> evaluatePostReports = reportPostService.loadAllEvaluateReports();
        List<ExamPostReport> examPostReports = reportPostService.loadAllExamReports();

        return LoadAllReportedPostForm
                .builder()
                .evaluatePostReports(evaluatePostReports)
                .examPostReports(examPostReports)
                .build();
    }

    /**
     * 신고된 강의평가 게시물 자세히 보기
     */
    public EvaluatePostReport executeLoadDetailReportedEvaluatePost(Long evaluatePostReportId) {
        return reportPostService.loadDetailEvaluateReportFromReportingEvaluatePostId(evaluatePostReportId);
    }

    /**
     * 신고된 시험정보 게시물 자세히 보기
     */
    public ExamPostReport executeLoadDetailReportedExamPost(Long examPostReportId) {
        return reportPostService.loadDetailEvaluateReportFromReportingExamPostId(examPostReportId);
    }

    /**
     * 신고된 강의평가 게시물 삭제
     */
    public Map<String, Boolean> executeNoProblemEvaluatePost(EvaluatePostNoProblemForm evaluatePostNoProblemForm) {
        reportPostService.deleteByEvaluateIdx(evaluatePostNoProblemForm.evaluateIdx());
        return successCapitalFlag();
    }

    /**
     * 신고된 시험정보 게시물 삭제
     */
    public Map<String, Boolean> executeNoProblemExamPost(ExamPostNoProblemForm examPostRestrictForm) {
        reportPostService.deleteByExamIdx(examPostRestrictForm.examIdx());
        return successCapitalFlag();
    }

    /**
     * 신고된 강의평가 게시물 작성자 이용 정지 처리
     */
    public Map<String, Boolean> executeRestrictEvaluatePost(EvaluatePostRestrictForm evaluatePostRestrictForm) {
        plusReportingUserPoint(reportPostService.whoIsEvaluateReporting(evaluatePostRestrictForm.evaluateIdx()));
        plusRestrictCount(deleteReportedEvaluatePostFromEvaluateIdx(evaluatePostRestrictForm.evaluateIdx()));
        restrictingUserService.executeRestrictUserFromEvaluatePost(evaluatePostRestrictForm);

        return successCapitalFlag();
    }

    /**
     * 신고된 시험정보 게시물 작성자 이용 정지 처리
     */
    public Map<String, Boolean> executeRestrictExamPost(UserAdminRequestDto.ExamPostRestrictForm examPostRestrictForm) {
        plusReportingUserPoint(reportPostService.whoIsExamReporting(examPostRestrictForm.examIdx()));
        plusRestrictCount(deleteReportedExamPostFromEvaluateIdx(examPostRestrictForm.examIdx()));
        restrictingUserService.executeRestrictUserFromExamPost(examPostRestrictForm);

        return successCapitalFlag();
    }

    /**
     * 신고된 강의평가 게시물 작성자 블랙리스트 처리
     */
    public Map<String, Boolean> executeBlackListEvaluatePost(EvaluatePostBlacklistForm evaluatePostBlacklistForm) {
        Long userIdx = evaluatePostCRUDService
                .loadEvaluatePostFromEvaluatePostIdx(evaluatePostBlacklistForm.evaluateIdx())
                .getUser()
                .getId();

        deleteReportedEvaluatePostFromEvaluateIdx(evaluatePostBlacklistForm.evaluateIdx());
        blacklistDomainCRUDService.saveBlackListDomain(
                userIdx,
                365L,
                evaluatePostBlacklistForm.bannedReason(),
                evaluatePostBlacklistForm.judgement()
        );
        plusRestrictCount(userIdx);

        return successCapitalFlag();
    }

    /**
     * 신고된 시험정보 게시물 작성자 블랙리스트 처리
     */
    public Map<String, Boolean> executeBlackListExamPost(ExamPostBlacklistForm examPostBlacklistForm) {
        Long userIdx = examPostCRUDService.loadExamPostFromExamPostIdx(examPostBlacklistForm.examIdx()).getUser().getId();

        deleteReportedExamPostFromEvaluateIdx(examPostBlacklistForm.examIdx());
        blacklistDomainCRUDService.saveBlackListDomain(
                userIdx,
                365L,
                examPostBlacklistForm.bannedReason(),
                examPostBlacklistForm.judgement()
        );
        plusRestrictCount(userIdx);

        return successCapitalFlag();
    }

    private Long deleteReportedEvaluatePostFromEvaluateIdx(Long evaluateIdx) {
        EvaluatePosts evaluatePost = evaluatePostCRUDService.loadEvaluatePostFromEvaluatePostIdx(evaluateIdx);
        reportPostService.deleteByEvaluateIdx(evaluateIdx);
        evaluatePostCRUDService.delete(evaluatePost);
        return evaluatePost.getUser().getId();
    }

    private Long deleteReportedExamPostFromEvaluateIdx(Long examPostIdx) {
        ExamPosts examPost = examPostCRUDService.loadExamPostFromExamPostIdx(examPostIdx);
        reportPostService.deleteByEvaluateIdx(examPostIdx);
        examPostCRUDService.delete(examPost);
        return examPost.getUser().getId();
    }

    private void plusRestrictCount(Long userIdx) {
        User user = userCRUDService.loadUserFromUserIdx(userIdx);
        user.increaseRestrictedCountByReportedPost();
    }

    private void plusReportingUserPoint(Long reportingUserIdx) {
        User user = userCRUDService.loadUserFromUserIdx(reportingUserIdx);
        user.increasePointByReporting();
    }
}