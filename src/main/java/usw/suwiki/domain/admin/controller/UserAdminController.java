package usw.suwiki.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.domain.admin.dto.UserAdminRequestDto;
import usw.suwiki.domain.admin.dto.UserAdminRequestDto.EvaluatePostRestrictForm;
import usw.suwiki.domain.admin.dto.UserAdminResponseDto;
import usw.suwiki.domain.admin.service.UserAdminBusinessService;
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
public class UserAdminController {

    private final UserAdminBusinessService userAdminBusinessService;

    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/login")
    public Map<String, String> administratorLogin(
            @Valid @RequestBody LoginForm loginForm
    ) {
        return userAdminBusinessService.executeAdminLogin(loginForm);
    }

    @JWTVerify(option = "ADMIN")
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/evaluate-posts/restrict")
    public Map<String, Boolean> restrictEvaluatePost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody EvaluatePostRestrictForm evaluatePostRestrictForm
    ) {
        return userAdminBusinessService.executeRestrictEvaluatePost(Authorization, evaluatePostRestrictForm);
    }

    @JWTVerify(option = "ADMIN")
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/exam-post/restrict")
    public Map<String, Boolean> restrictExamPost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody UserAdminRequestDto.ExamPostRestrictForm examPostRestrictForm
    ) {
        return userAdminBusinessService.executeRestrictExamPost(Authorization, examPostRestrictForm);
    }


    @JWTVerify(option = "ADMIN")
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/evaluate-post/blacklist")
    public Map<String, Boolean> banEvaluatePost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody UserAdminRequestDto.EvaluatePostBlacklistForm evaluatePostBlacklistForm
    ) {
        return userAdminBusinessService.executeBlackListEvaluatePost(Authorization, evaluatePostBlacklistForm);
    }

    @JWTVerify(option = "ADMIN")
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @PostMapping("/exam-post/blacklist")
    public Map<String, Boolean> banExamPost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody UserAdminRequestDto.ExamPostBlacklistForm examPostBlacklistForm
    ) {
        return userAdminBusinessService.executeBlackListExamPost(Authorization, examPostBlacklistForm);
    }

    @JWTVerify(option = "ADMIN")
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @DeleteMapping("/evaluate-post")
    public Map<String, Boolean> noProblemEvaluatePost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody UserAdminRequestDto.EvaluatePostNoProblemForm evaluatePostNoProblemForm
    ) {
        return userAdminBusinessService.executeNoProblemEvaluatePost(Authorization, evaluatePostNoProblemForm);
    }

    @JWTVerify(option = "ADMIN")
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @DeleteMapping("/exam-post")
    public Map<String, Boolean> noProblemExamPost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestBody UserAdminRequestDto.ExamPostNoProblemForm examPostNoProblemForm
    ) {
        return userAdminBusinessService.executeNoProblemExamPost(Authorization, examPostNoProblemForm);
    }

    @JWTVerify(option = "ADMIN")
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @GetMapping("/reported-posts")
    public UserAdminResponseDto.LoadAllReportedPostForm loadReportedPost(
            @Valid @RequestHeader String Authorization
    ) {
        return userAdminBusinessService.executeLoadAllReportedPosts(Authorization);
    }


    @JWTVerify(option = "ADMIN")
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @GetMapping("/reported-evaluate/")
    public EvaluatePostReport loadDetailReportedEvaluatePost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestParam Long target
    ) {
        return userAdminBusinessService.executeLoadDetailReportedEvaluatePost(Authorization, target);
    }

    // 시험정보에 관련된 신고 게시글 자세히 보기
    @ResponseStatus(OK)
    @ApiLogger(option = "admin")
    @GetMapping("/reported-exam/")
    public ExamPostReport loadDetailReportedExamPost(
            @Valid @RequestHeader String Authorization,
            @Valid @RequestParam Long target
    ) {
        return userAdminBusinessService.executeLoadDetailReportedExamPost(Authorization, target);
    }
}
