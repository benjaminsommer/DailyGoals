package com.benjaminsommer.dailygoals.entities;

import android.arch.persistence.room.ColumnInfo;

/**
 * Created by SOMMER on 02.12.2017.
 */

public class LocalRemoteId {

    @ColumnInfo(name = "remoteId")
    private String remoteId;

    @ColumnInfo(name = "localId")
    private int localId;

    public LocalRemoteId(String remoteId, int localId) {
        this.remoteId = remoteId;
        this.localId = localId;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }
}
