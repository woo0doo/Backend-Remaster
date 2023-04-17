package usw.suwiki.domain.user.entity;

import static usw.suwiki.global.exception.ErrorType.USER_POINT_LACK;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.global.exception.errortype.AccountException;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String loginId;

    @Column
    private String password;

    @Column
    private String email;

    @Column
    private Boolean restricted;

    @Column
    private Integer restrictedCount;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column
    private Integer writtenEvaluation;

    @Column
    private Integer writtenExam;

    @Column
    private Integer viewExamCount;

    @Column
    private Integer point;

    @Column
    private LocalDateTime lastLogin;

    @Column
    private LocalDateTime requestedQuitDate;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    public static User makeUser(String loginId, String password, String email) {
        return User.builder()
            .loginId(loginId)
            .password(password)
            .email(email)
            .restricted(true)
            .restrictedCount(0)
            .writtenEvaluation(0)
            .writtenExam(0)
            .point(0)
            .viewExamCount(0)
            .build();
    }

    public void editRestricted(boolean restricted) {
        this.restricted = restricted;
    }

    public void disable() {
        this.restricted = true;
        this.restrictedCount = null;
        this.role = null;
        this.writtenExam = null;
        this.writtenEvaluation = null;
        this.viewExamCount = null;
        this.point = null;
        this.lastLogin = null;
        this.createdAt = null;
        this.updatedAt = null;
        this.requestedQuitDate = LocalDateTime.now();
    }

    public void increasePointByWritingEvaluatePost() {
        this.point += 10;
    }

    public void increasePointByWritingExamPost() {
        this.point += 20;
    }

    public void decreasePointByPurchaseExamPost() {
        final int examPostRequiringPoint = 20;
        if (this.point < examPostRequiringPoint) {
            throw new AccountException(USER_POINT_LACK);
        }
        this.point -= examPostRequiringPoint;
    }

    public void decreasePointByDeletePosts() {
        final int deletePostRequiringPoint = 30;
        if (this.point < deletePostRequiringPoint) {
            throw new AccountException(USER_POINT_LACK);
        }
        this.point -= deletePostRequiringPoint;
    }
}
