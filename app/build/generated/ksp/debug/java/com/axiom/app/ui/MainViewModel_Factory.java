package com.axiom.app.ui;

import com.axiom.app.data.local.AxiomPreferences;
import com.axiom.app.domain.focus.FocusProtocolManager;
import com.axiom.app.domain.repository.DailyHabitLogRepository;
import com.axiom.app.domain.repository.SystemFeedRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class MainViewModel_Factory implements Factory<MainViewModel> {
  private final Provider<FocusProtocolManager> focusProtocolManagerProvider;

  private final Provider<DailyHabitLogRepository> dailyHabitLogRepositoryProvider;

  private final Provider<AxiomPreferences> preferencesProvider;

  private final Provider<SystemFeedRepository> feedRepositoryProvider;

  private MainViewModel_Factory(Provider<FocusProtocolManager> focusProtocolManagerProvider,
      Provider<DailyHabitLogRepository> dailyHabitLogRepositoryProvider,
      Provider<AxiomPreferences> preferencesProvider,
      Provider<SystemFeedRepository> feedRepositoryProvider) {
    this.focusProtocolManagerProvider = focusProtocolManagerProvider;
    this.dailyHabitLogRepositoryProvider = dailyHabitLogRepositoryProvider;
    this.preferencesProvider = preferencesProvider;
    this.feedRepositoryProvider = feedRepositoryProvider;
  }

  @Override
  public MainViewModel get() {
    return newInstance(focusProtocolManagerProvider.get(), dailyHabitLogRepositoryProvider.get(), preferencesProvider.get(), feedRepositoryProvider.get());
  }

  public static MainViewModel_Factory create(
      Provider<FocusProtocolManager> focusProtocolManagerProvider,
      Provider<DailyHabitLogRepository> dailyHabitLogRepositoryProvider,
      Provider<AxiomPreferences> preferencesProvider,
      Provider<SystemFeedRepository> feedRepositoryProvider) {
    return new MainViewModel_Factory(focusProtocolManagerProvider, dailyHabitLogRepositoryProvider, preferencesProvider, feedRepositoryProvider);
  }

  public static MainViewModel newInstance(FocusProtocolManager focusProtocolManager,
      DailyHabitLogRepository dailyHabitLogRepository, AxiomPreferences preferences,
      SystemFeedRepository feedRepository) {
    return new MainViewModel(focusProtocolManager, dailyHabitLogRepository, preferences, feedRepository);
  }
}
