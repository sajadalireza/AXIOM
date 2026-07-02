package com.axiom.app.ui;

import com.axiom.app.data.local.AxiomPreferences;
import com.axiom.app.domain.focus.FocusProtocolManager;
import com.axiom.app.domain.repository.MissionRepository;
import com.axiom.app.domain.repository.SystemFeedRepository;
import com.axiom.app.domain.usecase.CompleteMissionUseCase;
import com.axiom.app.domain.usecase.CreateMissionUseCase;
import com.axiom.app.domain.usecase.GetDungeonsUseCase;
import com.axiom.app.domain.usecase.GetHunterProfileUseCase;
import com.axiom.app.domain.usecase.GetMissionsUseCase;
import com.axiom.app.domain.usecase.GetSkillsUseCase;
import com.axiom.app.presentation.ceremony.CeremonyEngine;
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
public final class MissionsViewModel_Factory implements Factory<MissionsViewModel> {
  private final Provider<GetMissionsUseCase> getMissionsUseCaseProvider;

  private final Provider<GetSkillsUseCase> getSkillsUseCaseProvider;

  private final Provider<GetDungeonsUseCase> getDungeonsUseCaseProvider;

  private final Provider<GetHunterProfileUseCase> getHunterProfileUseCaseProvider;

  private final Provider<CompleteMissionUseCase> completeMissionUseCaseProvider;

  private final Provider<CreateMissionUseCase> createMissionUseCaseProvider;

  private final Provider<MissionRepository> repositoryProvider;

  private final Provider<CeremonyEngine> ceremonyEngineProvider;

  private final Provider<AxiomPreferences> preferencesProvider;

  private final Provider<SystemFeedRepository> feedRepositoryProvider;

  private final Provider<FocusProtocolManager> focusProtocolManagerProvider;

  private MissionsViewModel_Factory(Provider<GetMissionsUseCase> getMissionsUseCaseProvider,
      Provider<GetSkillsUseCase> getSkillsUseCaseProvider,
      Provider<GetDungeonsUseCase> getDungeonsUseCaseProvider,
      Provider<GetHunterProfileUseCase> getHunterProfileUseCaseProvider,
      Provider<CompleteMissionUseCase> completeMissionUseCaseProvider,
      Provider<CreateMissionUseCase> createMissionUseCaseProvider,
      Provider<MissionRepository> repositoryProvider,
      Provider<CeremonyEngine> ceremonyEngineProvider,
      Provider<AxiomPreferences> preferencesProvider,
      Provider<SystemFeedRepository> feedRepositoryProvider,
      Provider<FocusProtocolManager> focusProtocolManagerProvider) {
    this.getMissionsUseCaseProvider = getMissionsUseCaseProvider;
    this.getSkillsUseCaseProvider = getSkillsUseCaseProvider;
    this.getDungeonsUseCaseProvider = getDungeonsUseCaseProvider;
    this.getHunterProfileUseCaseProvider = getHunterProfileUseCaseProvider;
    this.completeMissionUseCaseProvider = completeMissionUseCaseProvider;
    this.createMissionUseCaseProvider = createMissionUseCaseProvider;
    this.repositoryProvider = repositoryProvider;
    this.ceremonyEngineProvider = ceremonyEngineProvider;
    this.preferencesProvider = preferencesProvider;
    this.feedRepositoryProvider = feedRepositoryProvider;
    this.focusProtocolManagerProvider = focusProtocolManagerProvider;
  }

  @Override
  public MissionsViewModel get() {
    return newInstance(getMissionsUseCaseProvider.get(), getSkillsUseCaseProvider.get(), getDungeonsUseCaseProvider.get(), getHunterProfileUseCaseProvider.get(), completeMissionUseCaseProvider.get(), createMissionUseCaseProvider.get(), repositoryProvider.get(), ceremonyEngineProvider.get(), preferencesProvider.get(), feedRepositoryProvider.get(), focusProtocolManagerProvider.get());
  }

  public static MissionsViewModel_Factory create(
      Provider<GetMissionsUseCase> getMissionsUseCaseProvider,
      Provider<GetSkillsUseCase> getSkillsUseCaseProvider,
      Provider<GetDungeonsUseCase> getDungeonsUseCaseProvider,
      Provider<GetHunterProfileUseCase> getHunterProfileUseCaseProvider,
      Provider<CompleteMissionUseCase> completeMissionUseCaseProvider,
      Provider<CreateMissionUseCase> createMissionUseCaseProvider,
      Provider<MissionRepository> repositoryProvider,
      Provider<CeremonyEngine> ceremonyEngineProvider,
      Provider<AxiomPreferences> preferencesProvider,
      Provider<SystemFeedRepository> feedRepositoryProvider,
      Provider<FocusProtocolManager> focusProtocolManagerProvider) {
    return new MissionsViewModel_Factory(getMissionsUseCaseProvider, getSkillsUseCaseProvider, getDungeonsUseCaseProvider, getHunterProfileUseCaseProvider, completeMissionUseCaseProvider, createMissionUseCaseProvider, repositoryProvider, ceremonyEngineProvider, preferencesProvider, feedRepositoryProvider, focusProtocolManagerProvider);
  }

  public static MissionsViewModel newInstance(GetMissionsUseCase getMissionsUseCase,
      GetSkillsUseCase getSkillsUseCase, GetDungeonsUseCase getDungeonsUseCase,
      GetHunterProfileUseCase getHunterProfileUseCase,
      CompleteMissionUseCase completeMissionUseCase, CreateMissionUseCase createMissionUseCase,
      MissionRepository repository, CeremonyEngine ceremonyEngine, AxiomPreferences preferences,
      SystemFeedRepository feedRepository, FocusProtocolManager focusProtocolManager) {
    return new MissionsViewModel(getMissionsUseCase, getSkillsUseCase, getDungeonsUseCase, getHunterProfileUseCase, completeMissionUseCase, createMissionUseCase, repository, ceremonyEngine, preferences, feedRepository, focusProtocolManager);
  }
}
