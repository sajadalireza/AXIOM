package com.axiom.app.domain.usecase;

import com.axiom.app.data.local.AxiomPreferences;
import com.axiom.app.domain.repository.DungeonRepository;
import com.axiom.app.domain.repository.HunterRepository;
import com.axiom.app.domain.repository.MissionRepository;
import com.axiom.app.domain.repository.SkillRepository;
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
public final class InitializeAxiomUseCase_Factory implements Factory<InitializeAxiomUseCase> {
  private final Provider<HunterRepository> hunterRepositoryProvider;

  private final Provider<SkillRepository> skillRepositoryProvider;

  private final Provider<DungeonRepository> dungeonRepositoryProvider;

  private final Provider<MissionRepository> missionRepositoryProvider;

  private final Provider<AxiomPreferences> preferencesProvider;

  private InitializeAxiomUseCase_Factory(Provider<HunterRepository> hunterRepositoryProvider,
      Provider<SkillRepository> skillRepositoryProvider,
      Provider<DungeonRepository> dungeonRepositoryProvider,
      Provider<MissionRepository> missionRepositoryProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    this.hunterRepositoryProvider = hunterRepositoryProvider;
    this.skillRepositoryProvider = skillRepositoryProvider;
    this.dungeonRepositoryProvider = dungeonRepositoryProvider;
    this.missionRepositoryProvider = missionRepositoryProvider;
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public InitializeAxiomUseCase get() {
    return newInstance(hunterRepositoryProvider.get(), skillRepositoryProvider.get(), dungeonRepositoryProvider.get(), missionRepositoryProvider.get(), preferencesProvider.get());
  }

  public static InitializeAxiomUseCase_Factory create(
      Provider<HunterRepository> hunterRepositoryProvider,
      Provider<SkillRepository> skillRepositoryProvider,
      Provider<DungeonRepository> dungeonRepositoryProvider,
      Provider<MissionRepository> missionRepositoryProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    return new InitializeAxiomUseCase_Factory(hunterRepositoryProvider, skillRepositoryProvider, dungeonRepositoryProvider, missionRepositoryProvider, preferencesProvider);
  }

  public static InitializeAxiomUseCase newInstance(HunterRepository hunterRepository,
      SkillRepository skillRepository, DungeonRepository dungeonRepository,
      MissionRepository missionRepository, AxiomPreferences preferences) {
    return new InitializeAxiomUseCase(hunterRepository, skillRepository, dungeonRepository, missionRepository, preferences);
  }
}
