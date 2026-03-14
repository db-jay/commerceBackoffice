package com.commerce.backoffice.domain.member;

/*
 * Member 컨텍스트의 회원 애그리거트 초안.
 * - 등급/상태 변경 규칙을 한 객체로 모은다.
 */
public class Member {

    private final Long id;
    private final String email;
    private String name;
    private MemberGrade grade;
    private MemberStatus status;

    public Member(Long id, String email, String name, MemberGrade grade, MemberStatus status) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("id는 양수여야 합니다.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email은 비어 있을 수 없습니다.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name은 비어 있을 수 없습니다.");
        }
        this.id = id;
        this.email = email;
        this.name = name;
        this.grade = grade == null ? MemberGrade.BASIC : grade;
        this.status = status == null ? MemberStatus.ACTIVE : status;
    }

    public void upgradeGrade(MemberGrade newGrade) {
        if (newGrade == null) {
            throw new IllegalArgumentException("newGrade는 null일 수 없습니다.");
        }
        this.grade = newGrade;
    }

    public void changeName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("newName은 비어 있을 수 없습니다.");
        }
        this.name = newName;
    }

    public void deactivate() {
        this.status = MemberStatus.DORMANT;
    }

    public void withdraw() {
        this.status = MemberStatus.WITHDRAWN;
    }

    public void activate() {
        this.status = MemberStatus.ACTIVE;
    }

    public Long id() {
        return id;
    }

    public String email() {
        return email;
    }

    public String name() {
        return name;
    }

    public MemberGrade grade() {
        return grade;
    }

    public MemberStatus status() {
        return status;
    }
}
