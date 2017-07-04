package cn.edu.xmu.yeahbuddy.domain;

import org.jetbrains.annotations.Contract;
import org.springframework.security.core.GrantedAuthority;

public enum AdministratorPermission implements GrantedAuthority {
    CreateTask,
    CloseSubmit,
    CreateLink,
    CloseTutorReview,
    ViewReport,
    ViewReview,
    FinalReview,
    ResetPassword,
    ManageAdministrator,
    ManageTeam,
    ManageTutor;

    @Contract(pure = true)
    @Override
    public String getAuthority() {
        return name();
    }
}