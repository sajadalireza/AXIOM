package com.axiom.app.ui;

import com.axiom.app.core.ai.SystemVoiceEngine;
import com.axiom.app.data.local.AxiomPreferences;
import com.axiom.app.domain.usecase.CreateMissionUseCase;
import com.axiom.app.domain.usecase.GetHunterProfileUseCase;
import com.axiom.app.domain.usecase.GetSkillsUseCase;
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
public final class AIMissionGeneratorViewModel_Factory implements Factory<AIMissionGeneratorViewModel> {
  private final Provider<SystemVoiceEngine> engineProvider;

  private final Provider<AxiomPreferences> preferencesProvider;

  private final Provider<GetHunterProfileUseCase> getHunterProfileProvider;

  private final Provider<GetSkillsUseCase> getSkillsProvider;

  private final Provider<CreateMissionUseCase> createMissionProvider;

  private AIMissionGeneratorViewModel_Factory(Provider<SystemVoiceEngine> engineProvider,
      Provider<AxiomPreferences> preferencesProvider,
      Provider<GetHunterProfileUseCase> getHunterProfileProvider,
      Provider<GetSkillsUseCase> getSkillsProvider,
      Provider<CreateMissionUseCase> createMissionProvider) {
    this.engineProvider = engineProvider;
    this.preferencesProvider = preferencesProvider;
    this.getHunterProfileProvider = getHunterProfileProvider;
    this.getSkillsProvider = getSkillsProvider;
    this.createMissionProvider = createMissionProvider;
  }

  @Override
  public AIMissionGeneratorViewModel get() {
    return newInstance(engineProvider.get(), preferencesProvider.get(), getHunterProfileProvider.get(), getSkillsProvider.get(), createMissionProvider.get());
  }

  public static AIMissionGeneratorViewModel_Factory create(
      Provider<SystemVoiceEngine> engineProvider, Provider<AxiomPreferences> preferencesProvider,
      Provider<GetHunterProfileUseCase> getHunterProfileProvider,
      Provider<GetSkillsUseCase> getSkillsProvider,
      Provider<CreateMissionUseCase> createMissionProvider) {
    return new AIMissionGeneratorViewModel_Factory(engineProvider, preferencesProvider, getHunterProfileProvider, getSkillsProvider, createMissionProvider);
  }

  public static AIMissionGeneratorViewModel newInstance(SystemVoiceEngine engine,
      AxiomPreferences preferences, GetHunterProfileUseCase getHunterProfile,
      GetSkillsUseCase getSkills, CreateMissionUseCase createMission) {
    return new AIMissionGeneratorViewModel(engine, preferences, getHunterProfile, getSkills, createMission);
  }
}
