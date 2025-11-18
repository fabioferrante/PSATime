package com.fabio.psatime.ui.dashboard;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0010\u001a\u00020\f2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\b0\u0007H\u0002J\u000e\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\bJ\u0016\u0010\u0015\u001a\u00020\u00132\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019J\u000e\u0010\u001a\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\bR\u001d\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\nR\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001b"}, d2 = {"Lcom/fabio/psatime/ui/dashboard/PsaViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "(Landroid/app/Application;)V", "allResults", "Landroidx/lifecycle/LiveData;", "", "Lcom/fabio/psatime/data/PsaResult;", "getAllResults", "()Landroidx/lifecycle/LiveData;", "currentPsaStatus", "Lcom/fabio/psatime/ui/dashboard/PsaStatus;", "getCurrentPsaStatus", "psaDao", "Lcom/fabio/psatime/data/PsaDao;", "calculateStatus", "results", "deleteResult", "", "result", "insertResult", "year", "", "value", "", "updateResult", "app_debug"})
public final class PsaViewModel extends androidx.lifecycle.AndroidViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.fabio.psatime.data.PsaDao psaDao = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.util.List<com.fabio.psatime.data.PsaResult>> allResults = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<com.fabio.psatime.ui.dashboard.PsaStatus> currentPsaStatus = null;
    
    public PsaViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.fabio.psatime.data.PsaResult>> getAllResults() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<com.fabio.psatime.ui.dashboard.PsaStatus> getCurrentPsaStatus() {
        return null;
    }
    
    private final com.fabio.psatime.ui.dashboard.PsaStatus calculateStatus(java.util.List<com.fabio.psatime.data.PsaResult> results) {
        return null;
    }
    
    public final void insertResult(int year, float value) {
    }
    
    public final void updateResult(@org.jetbrains.annotations.NotNull()
    com.fabio.psatime.data.PsaResult result) {
    }
    
    public final void deleteResult(@org.jetbrains.annotations.NotNull()
    com.fabio.psatime.data.PsaResult result) {
    }
}