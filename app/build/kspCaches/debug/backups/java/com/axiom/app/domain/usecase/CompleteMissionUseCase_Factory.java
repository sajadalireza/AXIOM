package com.axiom.app.domain.usecase;

import com.axiom.app.core.ai.SystemVoiceEngine;
import com.axiom.app.data.local.AxiomPreferences;
import com.axiom.app.domain.repository.DungeonRepository;
import com.axiom.app.domain.repository.HunterRepository;
import com.axiom.app.domain.repository.LeagueRepository;
import com.axiom.app.domain.repository.MissionRepository;
import com.axiom.app.domain.repository.ShadowRepository;
import com.axiom.app.domain.repository.SkillRepository;
import com.axiom.app.domain.repository.SystemFeedRepository;
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
public final class CompleteMissionUseCase_Factory implements Factory<CompleteMissionUseCase> {
  private final Provider<MissionRepository> missionRepositoryProvider;

  private final Provider<SkillRepository> skillRepositoryProvider;

  private final Provider<HunterRepository> hunterRepositoryProvider;

  private final Provider<ShadowRepository> shadowRepositoryProvider;

  private final Provider<DungeonRepository> dungeonRepositoryProvider;

  private final Provider<AxiomPreferences> preferencesProvider;

  private final Provider<SystemVoiceEngine> systemVoiceEngineProvider;

  private final Provider<SystemFeedRepository> systemFeedRepositoryProvider;

  private final Provider<LeagueRepository> leagueRepositoryProvider;

  private final Provider<CeremonyEngine> ceremonyEngineProvider;

  private CompleteMissionUseCase_Factory(Provider<MissionRepository> missionRepositoryProvider,
      Provider<SkillRepository> skillRepositoryProvider,
      Provider<HunterRepository> hunterRepositoryProvider,
      Provider<ShadowRepository> shadowRepositoryProvider,
      Provider<DungeonRepository> dungeonRepositoryProvider,
      Provider<AxiomPreferences> preferencesProvider,
      Provider<SystemVoiceEngine> systemVoiceEngineProvider,
      Provider<SystemFeedRepository> systemFeedRepositoryProvider,
      Provider<LeagueRepository> leagueRepositoryProvider,
      Provider<CeremonyEngine> ceremonyEngineProvider) {
    this.missionRepositoryProvider = missionRepositoryProvider;
    this.skillRepositoryProvider = skillRepositoryProvider;
    this.hunterRepositoryProvider = hunterRepositoryProvider;
    this.shadowRepositoryProvider = shadowRepositoryProvider;
    this.dungeonRepositoryProvider = dungeonRepositoryProvider;
    this.preferencesProvider = preferencesProvider;
    this.systemVoiceEngineProvider = systemVoiceEngineProvider;
    this.systemFeedRepositoryProvider = systemFeedRepositoryProvider;
    this.leagueRepositoryProvider = leagueRepositoryProvider;
    this.ceremonyEngineProvider = ceremonyEngineProvider;
  }

  @Override
  public CompleteMissionUseCase get() {
    return newInstance(missionRepositoryProvider.get(), skillRepositoryProvider.get(), hunterRepositoryProvider.get(), shadowRepositoryProvider.get(), dungeonRepositoryProvider.get(), preferencesProvider.get(), systemVoiceEngineProvider.get(), systemFeedRepositoryProvider.get(), leagueRepositoryProvider.get(), ceremonyEngineProvider.get());
  }

  public static CompleteMissionUseCase_Factory create(
      Provider<MissionRepository> missionRepositoryProvider,
      Provider<SkillRepository> skillRepositoryProvider,
      Provider<HunterRepository> hunterRepositoryProvider,
      Provider<ShadowRepository> shadowRepositoryProvider,
      Provider<DungeonRepository> dungeonRepositoryProvider,
      Provider<AxiomPreferences> preferencesProvider,
      Provider<SystemVoiceEngine> systemVoiceEngineProvider,
      Provider<SystemFeedRepository> systemFeedRepositoryProvider,
      Provider<LeagueRepository> leagueRepositoryProvider,
      Provider<CeremonyEngine> ceremonyEngineProvider) {
    return new CompleteMissionUseCase_Factory(missionRepositoryProvider, skillRepositoryProvider, hunterRepositoryProvider, shadowRepositoryProvider, dungeonRepositoryProvider, preferencesProvider, systemVoiceEngineProvider, systemFeedRepositoryProvider, leagueRepositoryProvider, ceremonyEngineProvider);
  }

  public static CompleteMissionUseCase newInstance(MissionRepository missionRepository,
      SkillRepository skillRepository, HunterRepository hunterRepository,
      ShadowRepository shadowRepository, DungeonRepository dungeonRepository,
      AxiomPreferences preferences, SystemVoiceEngine systemVoiceEngine,
      SystemFeedRepository systemFeedRepository, LeagueRepository leagueRepository,
      CeremonyEngine ceremonyEngine) {
    return new CompleteMissionUseCase(missionRepository, skillRepository, hunterRepository, shadowRepository, dungeonRepository, preferences, systemVoiceEngine, systemFeedRepository, leagueRepository, ceremonyEngine);
  }
}
