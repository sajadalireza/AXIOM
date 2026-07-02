package com.axiom.app.ui;

import com.axiom.app.data.local.AxiomPreferences;
import com.axiom.app.domain.focus.FocusProtocolManager;
import com.axiom.app.domain.repository.SystemFeedRepository;
import com.axiom.app.domain.usecase.CompleteDungeonStageUseCase;
import com.axiom.app.domain.usecase.CreateDungeonUseCase;
import com.axiom.app.domain.usecase.DefeatBossUseCase;
import com.axiom.app.domain.usecase.GetDungeonsUseCase;
import com.axiom.app.domain.usecase.GetHunterProfileUseCase;
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
public final class DungeonViewModel_Factory implements Factory<DungeonViewModel> {
  private final Provider<GetDungeonsUseCase> getDungeonsUseCaseProvider;

  private final Provider<GetHunterProfileUseCase> getHunterProfileUseCaseProvider;

  private final Provider<CompleteDungeonStageUseCase> completeDungeonStageUseCaseProvider;

  private final Provider<DefeatBossUseCase> defeatBossUseCaseProvider;

  private final Provider<CreateDungeonUseCase> createDungeonUseCaseProvider;

  private final Provider<CeremonyEngine> ceremonyEngineProvider;

  private final Provider<AxiomPreferences> preferencesProvider;

  private final Provider<SystemFeedRepository> feedRepositoryProvider;

  private final Provider<FocusProtocolManager> focusProtocolManagerProvider;

  private DungeonViewModel_Factory(Provider<GetDungeonsUseCase> getDungeonsUseCaseProvider,
      Provider<GetHunterProfileUseCase> getHunterProfileUseCaseProvider,
      Provider<CompleteDungeonStageUseCase> completeDungeonStageUseCaseProvider,
      Provider<DefeatBossUseCase> defeatBossUseCaseProvider,
      Provider<CreateDungeonUseCase> createDungeonUseCaseProvider,
      Provider<CeremonyEngine> ceremonyEngineProvider,
      Provider<AxiomPreferences> preferencesProvider,
      Provider<SystemFeedRepository> feedRepositoryProvider,
      Provider<FocusProtocolManager> focusProtocolManagerProvider) {
    this.getDungeonsUseCaseProvider = getDungeonsUseCaseProvider;
    this.getHunterProfileUseCaseProvider = getHunterProfileUseCaseProvider;
    this.completeDungeonStageUseCaseProvider = completeDungeonStageUseCaseProvider;
    this.defeatBossUseCaseProvider = defeatBossUseCaseProvider;
    this.createDungeonUseCaseProvider = createDungeonUseCaseProvider;
    this.ceremonyEngineProvider = ceremonyEngineProvider;
    this.preferencesProvider = preferencesProvider;
    this.feedRepositoryProvider = feedRepositoryProvider;
    this.focusProtocolManagerProvider = focusProtocolManagerProvider;
  }

  @Override
  public DungeonViewModel get() {
    return newInstance(getDungeonsUseCaseProvider.get(), getHunterProfileUseCaseProvider.get(), completeDungeonStageUseCaseProvider.get(), defeatBossUseCaseProvider.get(), createDungeonUseCaseProvider.get(), ceremonyEngineProvider.get(), preferencesProvider.get(), feedRepositoryProvider.get(), focusProtocolManagerProvider.get());
  }

  public static DungeonViewModel_Factory create(
      Provider<GetDungeonsUseCase> getDungeonsUseCaseProvider,
      Provider<GetHunterProfileUseCase> getHunterProfileUseCaseProvider,
      Provider<CompleteDungeonStageUseCase> completeDungeonStageUseCaseProvider,
      Provider<DefeatBossUseCase> defeatBossUseCaseProvider,
      Provider<CreateDungeonUseCase> createDungeonUseCaseProvider,
      Provider<CeremonyEngine> ceremonyEngineProvider,
      Provider<AxiomPreferences> preferencesProvider,
      Provider<SystemFeedRepository> feedRepositoryProvider,
      Provider<FocusProtocolManager> focusProtocolManagerProvider) {
    return new DungeonViewModel_Factory(getDungeonsUseCaseProvider, getHunterProfileUseCaseProvider, completeDungeonStageUseCaseProvider, defeatBossUseCaseProvider, createDungeonUseCaseProvider, ceremonyEngineProvider, preferencesProvider, feedRepositoryProvider, focusProtocolManagerProvider);
  }

  public static DungeonViewModel newInstance(GetDungeonsUseCase getDungeonsUseCase,
      GetHunterProfileUseCase getHunterProfileUseCase,
      CompleteDungeonStageUseCase completeDungeonStageUseCase, DefeatBossUseCase defeatBossUseCase,
      CreateDungeonUseCase createDungeonUseCase, CeremonyEngine ceremonyEngine,
      AxiomPreferences preferences, SystemFeedRepository feedRepository,
      FocusProtocolManager focusProtocolManager) {
    return new DungeonViewModel(getDungeonsUseCase, getHunterProfileUseCase, completeDungeonStageUseCase, defeatBossUseCase, createDungeonUseCase, ceremonyEngine, preferences, feedRepository, focusProtocolManager);
  }
}
