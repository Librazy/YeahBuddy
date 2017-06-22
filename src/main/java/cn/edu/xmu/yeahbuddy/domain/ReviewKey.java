package cn.edu.xmu.yeahbuddy.domain;

import java.io.Serializable;

public class ReviewKey implements Serializable {

    private int teamId;
    private int stage;
    private int viewer;
    private boolean viewerIsAdmin;

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public int getViewer() {
        return viewer;
    }

    public void setViewer(int viewer) {
        this.viewer = viewer;
    }

    public boolean isViewerIsAdmin() {
        return viewerIsAdmin;
    }

    public void setViewerIsAdmin(boolean viewerIsAdmin) {
        this.viewerIsAdmin = viewerIsAdmin;
    }

    @Override
    public boolean equals(Object t) {
        return t instanceof ReviewKey && ((ReviewKey) t).getTeamId() == this.getTeamId() && ((ReviewKey) t).getStage() == this.getStage() && ((ReviewKey) t).getViewer() == this.getViewer() && ((ReviewKey) t).isViewerIsAdmin() == this.isViewerIsAdmin();
    }

    @Override
    public int hashCode(){
        return (isViewerIsAdmin() ? Integer.MIN_VALUE : 0) + (teamId << 20) + (stage << 10) + viewer;
    }

}
