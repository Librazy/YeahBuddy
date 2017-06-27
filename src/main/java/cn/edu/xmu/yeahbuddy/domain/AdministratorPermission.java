package cn.edu.xmu.yeahbuddy.domain;

import org.springframework.security.core.GrantedAuthority;

public enum AdministratorPermission implements GrantedAuthority {
    CreateTask,
    CloseSubmit,
    CreateLink,
    CloseTutorReview,
    ViewReport,
    ViewReview,
    FinalReview;

    @Override
    public String getAuthority() {
        return name();
    }
}