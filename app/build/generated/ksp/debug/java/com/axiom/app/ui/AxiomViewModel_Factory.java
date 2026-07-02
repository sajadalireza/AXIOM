package com.axiom.app.ui;

import com.axiom.app.core.XionEventBus;
import com.axiom.app.data.local.AxiomPreferences;
import com.axiom.app.domain.focus.FocusProtocolManager;
import com.axiom.app.domain.repository.CloudSyncRepository;
import com.axiom.app.domain.repository.HunterRepository;
import com.axiom.app.domain.usecase.AriseShadowUseCase;
import com.axiom.app.domain.usecase.CompleteMissionUseCase;
import com.axiom.app.domain.usecase.CreateDungeonUseCase;
import com.axiom.app.domain.usecase.CreateMissionUseCase;
import com.axiom.app.domain.usecase.CreateSkillUseCase;
import com.axiom.app.domain.usecase.GetDungeonsUseCase;
import com.axiom.app.domain.usecase.GetHunterProfileUseCase;
import com.axiom.app.domain.usecase.GetMissionsUseCase;
import com.axiom.app.domain.usecase.GetShadowsUseCase;
import com.axiom.app.domain.usecase.GetSkillsUseCase;
import com.axiom.app.domain.usecase.InitializeAxiomUseCase;
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
public final class AxiomViewModel_Factory implements Factory<AxiomViewModel> {
  private final Provider<HunterRepository> repositoryProvider;

  private final Provider<InitializeAxiomUseCase> initializeAxiomUseCaseProvider;

  private final Provider<GetHunterProfileUseCase> getHunterProfileUseCaseProvider;

  private final Provider<GetMissionsUseCase> getMissionsUseCaseProvider;

  private final Provider<GetSkillsUseCase> getSkillsUseCaseProvider;

  private final Provider<GetDungeonsUseCase> getDungeonsUseCaseProvider;

  private final Provider<GetShadowsUseCase> getShadowsUseCaseProvider;

  private final Provider<CompleteMissionUseCase> completeMissionUseCaseProvider;

  private final Provider<CreateMissionUseCase> createMissionUseCaseProvider;

  private final Provider<CreateSkillUseCase> createSkillUseCaseProvider;

  private final Provider<CreateDungeonUseCase> createDungeonUseCaseProvider;

  private final Provider<AriseShadowUseCase> ariseShadowUseCaseProvider;

  private final Provider<AxiomPreferences> preferencesProvider;

  private final Provider<XionEventBus> xionEventBusProvider;

  private final Provider<FocusProtocolManager> focusProtocolManagerProvider;

  private final Provider<CloudSyncRepository> cloudSyncRepositoryProvider;

  private AxiomViewModel_Factory(Provider<HunterRepository> repositoryProvider,
      Provider<InitializeAxiomUseCase> initializeAxiomUseCaseProvider,
      Provider<GetHunterProfileUseCase> getHunterProfileUseCaseProvider,
      Provider<GetMissionsUseCase> getMissionsUseCaseProvider,
      Provider<GetSkillsUseCase> getSkillsUseCaseProvider,
      Provider<GetDungeonsUseCase> getDungeonsUseCaseProvider,
      Provider<GetShadowsUseCase> getShadowsUseCaseProvider,
      Provider<CompleteMissionUseCase> completeMissionUseCaseProvider,
      Provider<CreateMissionUseCase> createMissionUseCaseProvider,
      Provider<CreateSkillUseCase> createSkillUseCaseProvider,
      Provider<CreateDungeonUseCase> createDungeonUseCaseProvider,
      Provider<AriseShadowUseCase> ariseShadowUseCaseProvider,
      Provider<AxiomPreferences> preferencesProvider, Provider<XionEventBus> xionEventBusProvider,
      Provider<FocusProtocolManager> focusProtocolManagerProvider,
      Provider<CloudSyncRepository> cloudSyncRepositoryProvider) {
    this.repositoryProvider = repositoryProvider;
    this.initializeAxiomUseCaseProvider = initializeAxiomUseCaseProvider;
    this.getHunterProfileUseCaseProvider = getHunterProfileUseCaseProvider;
    this.getMissionsUseCaseProvider = getMissionsUseCaseProvider;
    this.getSkillsUseCaseProvider = getSkillsUseCaseProvider;
    this.getDungeonsUseCaseProvider = getDungeonsUseCaseProvider;
    this.getShadowsUseCaseProvider = getShadowsUseCaseProvider;
    this.completeMissionUseCaseProvider = completeMissionUseCaseProvider;
    this.createMissionUseCaseProvider = createMissionUseCaseProvider;
    this.createSkillUseCaseProvider = createSkillUseCaseProvider;
    this.createDungeonUseCaseProvider = createDungeonUseCaseProvider;
    this.ariseShadowUseCaseProvider = ariseShadowUseCaseProvider;
    this.preferencesProvider = preferencesProvider;
    this.xionEventBusProvider = xionEventBusProvider;
    this.focusProtocolManagerProvider = focusProtocolManagerProvider;
    this.cloudSyncRepositoryProvider = cloudSyncRepositoryProvider;
  }

  @Override
  public AxiomViewModel get() {
    return newInstance(repositoryProvider.get(), initializeAxiomUseCaseProvider.get(), getHunterProfileUseCaseProvider.get(), getMissionsUseCaseProvider.get(), getSkillsUseCaseProvider.get(), getDungeonsUseCaseProvider.get(), getShadowsUseCaseProvider.get(), completeMissionUseCaseProvider.get(), createMissionUseCaseProvider.get(), createSkillUseCaseProvider.get(), createDungeonUseCaseProvider.get(), ariseShadowUseCaseProvider.get(), preferencesProvider.get(), xionEventBusProvider.get(), focusProtocolManagerProvider.get(), cloudSyncRepositoryProvider.get());
  }

  public static AxiomViewModel_Factory create(Provider<HunterRepository> repositoryProvider,
      Provider<InitializeAxiomUseCase> initializeAxiomUseCaseProvider,
      Provider<GetHunterProfileUseCase> getHunterProfileUseCaseProvider,
      Provider<GetMissionsUseCase> getMissionsUseCaseProvider,
      Provider<GetSkillsUseCase> getSkillsUseCaseProvider,
      Provider<GetDungeonsUseCase> getDungeonsUseCaseProvider,
      Provider<GetShadowsUseCase> getShadowsUseCaseProvider,
      Provider<CompleteMissionUseCase> completeMissionUseCaseProvider,
      Provider<CreateMissionUseCase> createMissionUseCaseProvider,
      Provider<CreateSkillUseCase> createSkillUseCaseProvider,
      Provider<CreateDungeonUseCase> createDungeonUseCaseProvider,
      Provider<AriseShadowUseCase> ariseShadowUseCaseProvider,
      Provider<AxiomPreferences> preferencesProvider, Provider<XionEventBus> xionEventBusProvider,
      Provider<FocusProtocolManager> focusProtocolManagerProvider,
      Provider<CloudSyncRepository> cloudSyncRepositoryProvider) {
    return new AxiomViewModel_Factory(repositoryProvider, initializeAxiomUseCaseProvider, getHunterProfileUseCaseProvider, getMissionsUseCaseProvider, getSkillsUseCaseProvider, getDungeonsUseCaseProvider, getShadowsUseCaseProvider, completeMissionUseCaseProvider, createMissionUseCaseProvider, createSkillUseCaseProvider, createDungeonUseCaseProvider, ariseShadowUseCaseProvider, preferencesProvider, xionEventBusProvider, focusProtocolManagerProvider, cloudSyncRepositoryProvider);
  }

  public static AxiomViewModel newInstance(HunterRepository repository,
      InitializeAxiomUseCase initializeAxiomUseCase,
      GetHunterProfileUseCase getHunterProfileUseCase, GetMissionsUseCase getMissionsUseCase,
      GetSkillsUseCase getSkillsUseCase, GetDungeonsUseCase getDungeonsUseCase,
      GetShadowsUseCase getShadowsUseCase, CompleteMissionUseCase completeMissionUseCase,
      CreateMissionUseCase createMissionUseCase, CreateSkillUseCase createSkillUseCase,
      CreateDungeonUseCase createDungeonUseCase, AriseShadowUseCase ariseShadowUseCase,
      AxiomPreferences preferences, XionEventBus xionEventBus,
      FocusProtocolManager focusProtocolManager, CloudSyncRepository cloudSyncRepository) {
    return new AxiomViewModel(repository, initializeAxiomUseCase, getHunterProfileUseCase, getMissionsUseCase, getSkillsUseCase, getDungeonsUseCase, getShadowsUseCase, completeMissionUseCase, createMissionUseCase, createSkillUseCase, createDungeonUseCase, ariseShadowUseCase, preferences, xionEventBus, focusProtocolManager, cloudSyncRepository);
  }
}
