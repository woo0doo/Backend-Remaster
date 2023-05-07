package usw.suwiki.domain.postreport.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.postreport.entity.EvaluatePostReport;
import usw.suwiki.domain.postreport.entity.ExamPostReport;
import usw.suwiki.domain.postreport.repository.EvaluateReportRepository;
import usw.suwiki.domain.postreport.repository.ExamReportRepository;
import usw.suwiki.global.exception.errortype.AccountException;

import java.util.List;

import static usw.suwiki.global.exception.ExceptionType.SERVER_ERROR;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportPostService {

    private final EvaluateReportRepository evaluateReportRepository;
    private final ExamReportRepository examReportRepository;

    public List<EvaluatePostReport> loadAllEvaluateReports() {
        return evaluateReportRepository.findAll();
    }

    public List<ExamPostReport> loadAllExamReports() {
        return examReportRepository.findAll();
    }

    public EvaluatePostReport loadDetailEvaluateReportFromReportingEvaluatePostId(Long reportingEvaluatePostId) {
        return evaluateReportRepository.findById(reportingEvaluatePostId)
                .orElseThrow(() -> new AccountException(SERVER_ERROR));
    }

    public ExamPostReport loadDetailEvaluateReportFromReportingExamPostId(Long reportingExamPostId) {
        return examReportRepository.findById(reportingExamPostId)
                .orElseThrow(() -> new AccountException(SERVER_ERROR));
    }

    public void deleteFromUserIdx(Long userId) {
        examReportRepository.deleteByReportedUserIdx(userId);
        examReportRepository.deleteByReportingUserIdx(userId);
        evaluateReportRepository.deleteByReportedUserIdx(userId);
        evaluateReportRepository.deleteByReportingUserIdx(userId);
    }


    public void deleteByEvaluateIdx(Long evaluateIdx) {
        evaluateReportRepository.deleteByEvaluateIdx(evaluateIdx);
    }

    public void deleteByExamIdx(Long examIdx) {
        examReportRepository.deleteByExamIdx(examIdx);
    }


    public void saveEvaluatePostReport(EvaluatePostReport evaluatePostReport) {
        evaluateReportRepository.save(evaluatePostReport);
    }

    public void saveExamPostReport(ExamPostReport examPostReport) {
        examReportRepository.save(examPostReport);
    }

    public Long whoIsEvaluateReporting(Long evaluateIdx) {
        return loadDetailEvaluateReportFromReportingEvaluatePostId(evaluateIdx).getReportingUserIdx();
    }

    public Long whoIsExamReporting(Long examPostIdx) {
        return loadDetailEvaluateReportFromReportingExamPostId(examPostIdx).getReportingUserIdx();
    }
}
