package com.fabio.psatime.ui.dashboard;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0004\u0003\u0004\u0005\u0006B\u0007\b\u0004\u00a2\u0006\u0002\u0010\u0002\u0082\u0001\u0004\u0007\b\t\n\u00a8\u0006\u000b"}, d2 = {"Lcom/fabio/psatime/ui/dashboard/PsaStatus;", "", "()V", "Danger", "Empty", "Normal", "Warning", "Lcom/fabio/psatime/ui/dashboard/PsaStatus$Danger;", "Lcom/fabio/psatime/ui/dashboard/PsaStatus$Empty;", "Lcom/fabio/psatime/ui/dashboard/PsaStatus$Normal;", "Lcom/fabio/psatime/ui/dashboard/PsaStatus$Warning;", "app_debug"})
public abstract class PsaStatus {
    
    private PsaStatus() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/fabio/psatime/ui/dashboard/PsaStatus$Danger;", "Lcom/fabio/psatime/ui/dashboard/PsaStatus;", "()V", "app_debug"})
    public static final class Danger extends com.fabio.psatime.ui.dashboard.PsaStatus {
        @org.jetbrains.annotations.NotNull()
        public static final com.fabio.psatime.ui.dashboard.PsaStatus.Danger INSTANCE = null;
        
        private Danger() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/fabio/psatime/ui/dashboard/PsaStatus$Empty;", "Lcom/fabio/psatime/ui/dashboard/PsaStatus;", "()V", "app_debug"})
    public static final class Empty extends com.fabio.psatime.ui.dashboard.PsaStatus {
        @org.jetbrains.annotations.NotNull()
        public static final com.fabio.psatime.ui.dashboard.PsaStatus.Empty INSTANCE = null;
        
        private Empty() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/fabio/psatime/ui/dashboard/PsaStatus$Normal;", "Lcom/fabio/psatime/ui/dashboard/PsaStatus;", "()V", "app_debug"})
    public static final class Normal extends com.fabio.psatime.ui.dashboard.PsaStatus {
        @org.jetbrains.annotations.NotNull()
        public static final com.fabio.psatime.ui.dashboard.PsaStatus.Normal INSTANCE = null;
        
        private Normal() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/fabio/psatime/ui/dashboard/PsaStatus$Warning;", "Lcom/fabio/psatime/ui/dashboard/PsaStatus;", "()V", "app_debug"})
    public static final class Warning extends com.fabio.psatime.ui.dashboard.PsaStatus {
        @org.jetbrains.annotations.NotNull()
        public static final com.fabio.psatime.ui.dashboard.PsaStatus.Warning INSTANCE = null;
        
        private Warning() {
        }
    }
}