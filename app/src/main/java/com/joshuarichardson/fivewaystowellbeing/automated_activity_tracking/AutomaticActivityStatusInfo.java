package com.joshuarichardson.fivewaystowellbeing.automated_activity_tracking;

/**
 * Status information required to create pending intents
 */
public class AutomaticActivityStatusInfo {
    int acceptCode;
    int rejectCode;
    String appName;

    public AutomaticActivityStatusInfo(int acceptCode, int rejectCode, String appName) {
        this.acceptCode = acceptCode;
        this.rejectCode = rejectCode;
        this.appName = appName;
    }

    public String getAppName() {
        return this.appName;
    }

    public int getAcceptCode() {
        return this.acceptCode;
    }

    public int getRejectCode() {
        return this.rejectCode;
    }
}
