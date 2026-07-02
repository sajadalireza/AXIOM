package com.axiom.app.presentation.onboarding.blueprint;

import com.axiom.app.data.local.AxiomPreferences;
import com.axiom.app.domain.repository.HunterRepository;
import com.axiom.app.domain.repository.MissionRepository;
import com.axiom.app.domain.repository.WarriorProfileRepository;
import com.axiom.app.domain.usecase.GenerateDailyMissionsFromScheduleUseCase;
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
public final class BlueprintWizardViewModel_Factory implements Factory<BlueprintWizardViewModel> {
  private final Provider<WarriorProfileRepository> repositoryProvider;

  private final Provider<HunterRepository> hunterRepositoryProvider;

  private final Provider<AxiomPreferences> preferencesProvider;

  private final Provider<GenerateDailyMissionsFromScheduleUseCase> generateDailyMissionsUseCaseProvider;

  private final Provider<MissionRepository> missionRepositoryProvider;

  private BlueprintWizardViewModel_Factory(Provider<WarriorProfileRepository> repositoryProvider,
      Provider<HunterRepository> hunterRepositoryProvider,
      Provider<AxiomPreferences> preferencesProvider,
      Provider<GenerateDailyMissionsFromScheduleUseCase> generateDailyMissionsUseCaseProvider,
      Provider<MissionRepository> missionRepositoryProvider) {
    this.repositoryProvider = repositoryProvider;
    this.hunterRepositoryProvider = hunterRepositoryProvider;
    this.preferencesProvider = preferencesProvider;
    this.generateDailyMissionsUseCaseProvider = generateDailyMissionsUseCaseProvider;
    this.missionRepositoryProvider = missionRepositoryProvider;
  }

  @Override
  public BlueprintWizardViewModel get() {
    return newInstance(repositoryProvider.get(), hunterRepositoryProvider.get(), preferencesProvider.get(), generateDailyMissionsUseCaseProvider.get(), missionRepositoryProvider.get());
  }

  public static BlueprintWizardViewModel_Factory create(
      Provider<WarriorProfileRepository> repositoryProvider,
      Provider<HunterRepository> hunterRepositoryProvider,
      Provider<AxiomPreferences> preferencesProvider,
      Provider<GenerateDailyMissionsFromScheduleUseCase> generateDailyMissionsUseCaseProvider,
      Provider<MissionRepository> missionRepositoryProvider) {
    return new BlueprintWizardViewModel_Factory(repositoryProvider, hunterRepositoryProvider, preferencesProvider, generateDailyMissionsUseCaseProvider, missionRepositoryProvider);
  }

  public static BlueprintWizardViewModel newInstance(WarriorProfileRepository repository,
      HunterRepository hunterRepository, AxiomPreferences preferences,
      GenerateDailyMissionsFromScheduleUseCase generateDailyMissionsUseCase,
      MissionRepository missionRepository) {
    return new BlueprintWizardViewModel(repository, hunterRepository, preferences, generateDailyMissionsUseCase, missionRepository);
  }
}
