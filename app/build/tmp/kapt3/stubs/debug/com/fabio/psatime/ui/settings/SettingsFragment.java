package com.fabio.psatime.ui.settings;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000d\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0018\u001a\u00020\u0019H\u0002J\u0010\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u001b\u001a\u00020\u001cH\u0002J\u0010\u0010\u001d\u001a\u00020\u00192\u0006\u0010\u001b\u001a\u00020\u001cH\u0002J\b\u0010\u001e\u001a\u00020\u0019H\u0002J$\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\"2\b\u0010#\u001a\u0004\u0018\u00010$2\b\u0010%\u001a\u0004\u0018\u00010&H\u0016J\b\u0010\'\u001a\u00020\u0019H\u0016J\u001a\u0010(\u001a\u00020\u00192\u0006\u0010)\u001a\u00020 2\b\u0010%\u001a\u0004\u0018\u00010&H\u0016J\u0010\u0010*\u001a\u00020\u00192\u0006\u0010+\u001a\u00020\u0004H\u0002J\u0018\u0010,\u001a\u00020\u00192\u0006\u0010-\u001a\u00020.2\u0006\u0010/\u001a\u00020\u0004H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082D\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\u00020\t8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u000b\u0010\fR\u001c\u0010\r\u001a\u0010\u0012\f\u0012\n \u000f*\u0004\u0018\u00010\u00040\u00040\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0010\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\u00110\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0012\u001a\u00020\u00138BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0016\u0010\u0017\u001a\u0004\b\u0014\u0010\u0015\u00a8\u00060"}, d2 = {"Lcom/fabio/psatime/ui/settings/SettingsFragment;", "Landroidx/fragment/app/Fragment;", "()V", "BTC_ADDRESS", "", "BTC_URI", "PREFS_NAME", "PREF_KEY_THEME", "_binding", "Lcom/fabio/psatime/databinding/FragmentSettingsBinding;", "binding", "getBinding", "()Lcom/fabio/psatime/databinding/FragmentSettingsBinding;", "exportLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "kotlin.jvm.PlatformType", "importLauncher", "", "viewModel", "Lcom/fabio/psatime/ui/dashboard/PsaViewModel;", "getViewModel", "()Lcom/fabio/psatime/ui/dashboard/PsaViewModel;", "viewModel$delegate", "Lkotlin/Lazy;", "copyBtcAndOpenWallet", "", "exportDataToUri", "uri", "Landroid/net/Uri;", "importDataFromUri", "loadThemePreference", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onDestroyView", "onViewCreated", "view", "openUrl", "url", "saveAndApplyTheme", "themeMode", "", "themeName", "app_debug"})
public final class SettingsFragment extends androidx.fragment.app.Fragment {
    @org.jetbrains.annotations.Nullable()
    private com.fabio.psatime.databinding.FragmentSettingsBinding _binding;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy viewModel$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String PREFS_NAME = "psa_theme_prefs";
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String PREF_KEY_THEME = "theme_preference";
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String BTC_ADDRESS = "BC1QCGGEYAWVVSG5N8UYUPXU93HAPS8NH9Q79SPY0V";
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String BTC_URI = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.activity.result.ActivityResultLauncher<java.lang.String> exportLauncher = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.activity.result.ActivityResultLauncher<java.lang.String[]> importLauncher = null;
    
    public SettingsFragment() {
        super();
    }
    
    private final com.fabio.psatime.databinding.FragmentSettingsBinding getBinding() {
        return null;
    }
    
    private final com.fabio.psatime.ui.dashboard.PsaViewModel getViewModel() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public android.view.View onCreateView(@org.jetbrains.annotations.NotNull()
    android.view.LayoutInflater inflater, @org.jetbrains.annotations.Nullable()
    android.view.ViewGroup container, @org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
        return null;
    }
    
    @java.lang.Override()
    public void onViewCreated(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void copyBtcAndOpenWallet() {
    }
    
    private final void openUrl(java.lang.String url) {
    }
    
    private final void exportDataToUri(android.net.Uri uri) {
    }
    
    private final void importDataFromUri(android.net.Uri uri) {
    }
    
    private final void loadThemePreference() {
    }
    
    private final void saveAndApplyTheme(int themeMode, java.lang.String themeName) {
    }
    
    @java.lang.Override()
    public void onDestroyView() {
    }
}