package com.axiom.app.ui;

import com.axiom.app.data.local.AxiomPreferences;
import com.axiom.app.domain.repository.DailyHabitLogRepository;
import com.axiom.app.domain.repository.MuscleGroupRepository;
import com.axiom.app.domain.repository.SystemFeedRepository;
import com.axiom.app.domain.usecase.GetDungeonsUseCase;
import com.axiom.app.domain.usecase.GetHunterProfileUseCase;
import com.axiom.app.domain.usecase.GetMissionsUseCase;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<GetHunterProfileUseCase> getHunterProfileUseCaseProvider;

  private final Provider<GetMissionsUseCase> getMissionsUseCaseProvider;

  private final Provider<GetDungeonsUseCase> getDungeonsUseCaseProvider;

  private final Provider<SystemFeedRepository> systemFeedRepositoryProvider;

  private final Provider<MuscleGroupRepository> muscleRepositoryProvider;

  private final Provider<DailyHabitLogRepository> habitRepositoryProvider;

  private final Provider<AxiomPreferences> preferencesProvider;

  private HomeViewModel_Factory(Provider<GetHunterProfileUseCase> getHunterProfileUseCaseProvider,
      Provider<GetMissionsUseCase> getMissionsUseCaseProvider,
      Provider<GetDungeonsUseCase> getDungeonsUseCaseProvider,
      Provider<SystemFeedRepository> systemFeedRepositoryProvider,
      Provider<MuscleGroupRepository> muscleRepositoryProvider,
      Provider<DailyHabitLogRepository> habitRepositoryProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    this.getHunterProfileUseCaseProvider = getHunterProfileUseCaseProvider;
    this.getMissionsUseCaseProvider = getMissionsUseCaseProvider;
    this.getDungeonsUseCaseProvider = getDungeonsUseCaseProvider;
    this.systemFeedRepositoryProvider = systemFeedRepositoryProvider;
    this.muscleRepositoryProvider = muscleRepositoryProvider;
    this.habitRepositoryProvider = habitRepositoryProvider;
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(getHunterProfileUseCaseProvider.get(), getMissionsUseCaseProvider.get(), getDungeonsUseCaseProvider.get(), systemFeedRepositoryProvider.get(), muscleRepositoryProvider.get(), habitRepositoryProvider.get(), preferencesProvider.get());
  }

  public static HomeViewModel_Factory create(
      Provider<GetHunterProfileUseCase> getHunterProfileUseCaseProvider,
      Provider<GetMissionsUseCase> getMissionsUseCaseProvider,
      Provider<GetDungeonsUseCase> getDungeonsUseCaseProvider,
      Provider<SystemFeedRepository> systemFeedRepositoryProvider,
      Provider<MuscleGroupRepository> muscleRepositoryProvider,
      Provider<DailyHabitLogRepository> habitRepositoryProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    return new HomeViewModel_Factory(getHunterProfileUseCaseProvider, getMissionsUseCaseProvider, getDungeonsUseCaseProvider, systemFeedRepositoryProvider, muscleRepositoryProvider, habitRepositoryProvider, preferencesProvider);
  }

  public static HomeViewModel newInstance(GetHunterProfileUseCase getHunterProfileUseCase,
      GetMissionsUseCase getMissionsUseCase, GetDungeonsUseCase getDungeonsUseCase,
      SystemFeedRepository systemFeedRepository, MuscleGroupRepository muscleRepository,
      DailyHabitLogRepository habitRepository, AxiomPreferences preferences) {
    return new HomeViewModel(getHunterProfileUseCase, getMissionsUseCase, getDungeonsUseCase, systemFeedRepository, muscleRepository, habitRepository, preferences);
  }
}
