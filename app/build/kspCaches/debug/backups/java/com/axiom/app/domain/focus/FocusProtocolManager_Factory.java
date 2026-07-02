package com.axiom.app.domain.focus;

import android.content.Context;
import com.axiom.app.data.local.AxiomPreferences;
import com.axiom.app.domain.repository.LeagueRepository;
import com.axiom.app.domain.repository.MissionRepository;
import com.axiom.app.domain.repository.MuscleGroupRepository;
import com.axiom.app.domain.usecase.CompleteDungeonStageUseCase;
import com.axiom.app.domain.usecase.CompleteMissionUseCase;
import com.axiom.app.domain.usecase.DefeatBossUseCase;
import com.axiom.app.domain.usecase.GetHunterProfileUseCase;
import com.axiom.app.presentation.ceremony.CeremonyEngine;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class FocusProtocolManager_Factory implements Factory<FocusProtocolManager> {
  private final Provider<AxiomPreferences> preferencesProvider;

  private final Provider<CompleteMissionUseCase> completeMissionUseCaseProvider;

  private final Provider<CompleteDungeonStageUseCase> completeDungeonStageUseCaseProvider;

  private final Provider<DefeatBossUseCase> defeatBossUseCaseProvider;

  private final Provider<GetHunterProfileUseCase> getHunterProfileUseCaseProvider;

  private final Provider<CeremonyEngine> ceremonyEngineProvider;

  private final Provider<LeagueRepository> leagueRepositoryProvider;

  private final Provider<MissionRepository> missionRepositoryProvider;

  private final Provider<MuscleGroupRepository> muscleGroupRepositoryProvider;

  private final Provider<Context> contextProvider;

  private FocusProtocolManager_Factory(Provider<AxiomPreferences> preferencesProvider,
      Provider<CompleteMissionUseCase> completeMissionUseCaseProvider,
      Provider<CompleteDungeonStageUseCase> completeDungeonStageUseCaseProvider,
      Provider<DefeatBossUseCase> defeatBossUseCaseProvider,
      Provider<GetHunterProfileUseCase> getHunterProfileUseCaseProvider,
      Provider<CeremonyEngine> ceremonyEngineProvider,
      Provider<LeagueRepository> leagueRepositoryProvider,
      Provider<MissionRepository> missionRepositoryProvider,
      Provider<MuscleGroupRepository> muscleGroupRepositoryProvider,
      Provider<Context> contextProvider) {
    this.preferencesProvider = preferencesProvider;
    this.completeMissionUseCaseProvider = completeMissionUseCaseProvider;
    this.completeDungeonStageUseCaseProvider = completeDungeonStageUseCaseProvider;
    this.defeatBossUseCaseProvider = defeatBossUseCaseProvider;
    this.getHunterProfileUseCaseProvider = getHunterProfileUseCaseProvider;
    this.ceremonyEngineProvider = ceremonyEngineProvider;
    this.leagueRepositoryProvider = leagueRepositoryProvider;
    this.missionRepositoryProvider = missionRepositoryProvider;
    this.muscleGroupRepositoryProvider = muscleGroupRepositoryProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public FocusProtocolManager get() {
    return newInstance(preferencesProvider.get(), completeMissionUseCaseProvider.get(), completeDungeonStageUseCaseProvider.get(), defeatBossUseCaseProvider.get(), getHunterProfileUseCaseProvider.get(), ceremonyEngineProvider.get(), leagueRepositoryProvider.get(), missionRepositoryProvider.get(), muscleGroupRepositoryProvider.get(), contextProvider.get());
  }

  public static FocusProtocolManager_Factory create(Provider<AxiomPreferences> preferencesProvider,
      Provider<CompleteMissionUseCase> completeMissionUseCaseProvider,
      Provider<CompleteDungeonStageUseCase> completeDungeonStageUseCaseProvider,
      Provider<DefeatBossUseCase> defeatBossUseCaseProvider,
      Provider<GetHunterProfileUseCase> getHunterProfileUseCaseProvider,
      Provider<CeremonyEngine> ceremonyEngineProvider,
      Provider<LeagueRepository> leagueRepositoryProvider,
      Provider<MissionRepository> missionRepositoryProvider,
      Provider<MuscleGroupRepository> muscleGroupRepositoryProvider,
      Provider<Context> contextProvider) {
    return new FocusProtocolManager_Factory(preferencesProvider, completeMissionUseCaseProvider, completeDungeonStageUseCaseProvider, defeatBossUseCaseProvider, getHunterProfileUseCaseProvider, ceremonyEngineProvider, leagueRepositoryProvider, missionRepositoryProvider, muscleGroupRepositoryProvider, contextProvider);
  }

  public static FocusProtocolManager newInstance(AxiomPreferences preferences,
      CompleteMissionUseCase completeMissionUseCase,
      CompleteDungeonStageUseCase completeDungeonStageUseCase, DefeatBossUseCase defeatBossUseCase,
      GetHunterProfileUseCase getHunterProfileUseCase, CeremonyEngine ceremonyEngine,
      LeagueRepository leagueRepository, MissionRepository missionRepository,
      MuscleGroupRepository muscleGroupRepository, Context context) {
    return new FocusProtocolManager(preferences, completeMissionUseCase, completeDungeonStageUseCase, defeatBossUseCase, getHunterProfileUseCase, ceremonyEngine, leagueRepository, missionRepository, muscleGroupRepository, context);
  }
}
