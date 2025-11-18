package com.fabio.psatime.ui.dashboard;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u0012\u0012\u0004\u0012\u00020\u0002\u0012\b\u0012\u00060\u0003R\u00020\u00000\u0001:\u0001\u000eB\u0005\u00a2\u0006\u0002\u0010\u0004J\u001c\u0010\u0005\u001a\u00020\u00062\n\u0010\u0007\u001a\u00060\u0003R\u00020\u00002\u0006\u0010\b\u001a\u00020\tH\u0016J\u001c\u0010\n\u001a\u00060\u0003R\u00020\u00002\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\tH\u0016\u00a8\u0006\u000f"}, d2 = {"Lcom/fabio/psatime/ui/dashboard/PsaHistoryAdapter;", "Landroidx/recyclerview/widget/ListAdapter;", "Lcom/fabio/psatime/data/PsaResult;", "Lcom/fabio/psatime/ui/dashboard/PsaHistoryAdapter$PsaViewHolder;", "()V", "onBindViewHolder", "", "holder", "position", "", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "PsaViewHolder", "app_debug"})
public final class PsaHistoryAdapter extends androidx.recyclerview.widget.ListAdapter<com.fabio.psatime.data.PsaResult, com.fabio.psatime.ui.dashboard.PsaHistoryAdapter.PsaViewHolder> {
    
    public PsaHistoryAdapter() {
        super(null);
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public com.fabio.psatime.ui.dashboard.PsaHistoryAdapter.PsaViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    com.fabio.psatime.ui.dashboard.PsaHistoryAdapter.PsaViewHolder holder, int position) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0018\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\nR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/fabio/psatime/ui/dashboard/PsaHistoryAdapter$PsaViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "binding", "Lcom/fabio/psatime/databinding/ItemPsaResultBinding;", "(Lcom/fabio/psatime/ui/dashboard/PsaHistoryAdapter;Lcom/fabio/psatime/databinding/ItemPsaResultBinding;)V", "dateFormat", "Ljava/text/SimpleDateFormat;", "bind", "", "result", "Lcom/fabio/psatime/data/PsaResult;", "previous", "app_debug"})
    public final class PsaViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        @org.jetbrains.annotations.NotNull()
        private final com.fabio.psatime.databinding.ItemPsaResultBinding binding = null;
        @org.jetbrains.annotations.NotNull()
        private final java.text.SimpleDateFormat dateFormat = null;
        
        public PsaViewHolder(@org.jetbrains.annotations.NotNull()
        com.fabio.psatime.databinding.ItemPsaResultBinding binding) {
            super(null);
        }
        
        public final void bind(@org.jetbrains.annotations.NotNull()
        com.fabio.psatime.data.PsaResult result, @org.jetbrains.annotations.Nullable()
        com.fabio.psatime.data.PsaResult previous) {
        }
    }
}