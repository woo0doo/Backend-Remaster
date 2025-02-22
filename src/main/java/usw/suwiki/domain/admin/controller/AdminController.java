package usw.suwiki.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.domain.admin.controller.dto.UserAdminRequestDto.*;
import usw.suwiki.domain.admin.controller.dto.UserAdminResponseDto.LoadAllReportedPostForm;
import usw.suwiki.domain.admin.service.AdminBusinessService;
import usw.suwiki.domain.postreport.EvaluatePostReport;
import usw.suwiki.domain.postreport.ExamPostReport;
import usw.suwiki.domain.user.user.controller.dto.UserRequestDto.LoginForm;
import usw.suwiki.global.annotation.ApiLogger;
import usw.suwiki.global.annotation.JWTVerify;

import javax.validation.Valid;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;


@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AdminController {

    private final AdminBusinessService adminBusinessService;

    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/login")
    public Map<String, String> administratorLogin(
            @Valid @RequestBody LoginForm loginForm
    ) {
        return adminBusinessService.executeAdminLogin(loginForm);
    }

    @JWTVerify(option = "ADMIN")
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/restrict/evaluate-posts")
    public Map<String, Boolean> restrictEvaluatePost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody EvaluatePostRestrictForm evaluatePostRestrictForm
    ) {
        return adminBusinessService.executeRestrictEvaluatePost(evaluatePostRestrictForm);
    }

    @JWTVerify(option = "ADMIN")
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/restrict/exam-post")
    public Map<String, Boolean> restrictExamPost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody ExamPostRestrictForm examPostRestrictForm
    ) {
        return adminBusinessService.executeRestrictExamPost(examPostRestrictForm);
    }


    @JWTVerify(option = "ADMIN")
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/blacklist/evaluate-post")
    public Map<String, Boolean> banEvaluatePost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody EvaluatePostBlacklistForm evaluatePostBlacklistForm
    ) {
        return adminBusinessService.executeBlackListEvaluatePost(evaluatePostBlacklistForm);
    }

    @JWTVerify(option = "ADMIN")
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/blacklist/exam-post")
    public Map<String, Boolean> banExamPost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody ExamPostBlacklistForm examPostBlacklistForm
    ) {
        return adminBusinessService.executeBlackListExamPost(examPostBlacklistForm);
    }

    @JWTVerify(option = "ADMIN")
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @DeleteMapping("/no-problem/evaluate-post")
    public Map<String, Boolean> noProblemEvaluatePost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody EvaluatePostNoProblemForm evaluatePostNoProblemForm
    ) {
        return adminBusinessService.executeNoProblemEvaluatePost(evaluatePostNoProblemForm);
    }

    @JWTVerify(option = "ADMIN")
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @DeleteMapping("/no-problem/exam-post")
    public Map<String, Boolean> noProblemExamPost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody ExamPostNoProblemForm examPostNoProblemForm
    ) {
        return adminBusinessService.executeNoProblemExamPost(examPostNoProblemForm);
    }

    @JWTVerify(option = "ADMIN")
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @GetMapping("/report/list")
    public LoadAllReportedPostForm loadReportedPost(
            @Valid @RequestHeader String Authorization
    ) {
        return adminBusinessService.executeLoadAllReportedPosts();
    }


    @JWTVerify(option = "ADMIN")
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @GetMapping("/report/evaluate/")
    public EvaluatePostReport loadDetailReportedEvaluatePost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestParam Long target
    ) {
        return adminBusinessService.executeLoadDetailReportedEvaluatePost(target);
    }

    // 시험정보에 관련된 신고 게시글 자세히 보기
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @GetMapping("/report/exam/")
    public ExamPostReport loadDetailReportedExamPost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestParam Long target
    ) {
        return adminBusinessService.executeLoadDetailReportedExamPost(target);
    }
}
