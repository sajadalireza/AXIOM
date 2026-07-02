package com.axiom.app.ui;

import com.axiom.app.data.local.AxiomPreferences;
import com.axiom.app.domain.repository.HunterRepository;
import com.axiom.app.domain.repository.SkillRepository;
import com.axiom.app.domain.repository.SystemFeedRepository;
import com.axiom.app.domain.usecase.AriseShadowUseCase;
import com.axiom.app.domain.usecase.CreateSkillUseCase;
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
public final class SkillTreeViewModel_Factory implements Factory<SkillTreeViewModel> {
  private final Provider<GetSkillsUseCase> getSkillsUseCaseProvider;

  private final Provider<CreateSkillUseCase> createSkillUseCaseProvider;

  private final Provider<AriseShadowUseCase> ariseShadowUseCaseProvider;

  private final Provider<GetMissionsUseCase> getMissionsUseCaseProvider;

  private final Provider<CeremonyEngine> ceremonyEngineProvider;

  private final Provider<AxiomPreferences> preferencesProvider;

  private final Provider<SystemFeedRepository> feedRepositoryProvider;

  private final Provider<SkillRepository> skillRepositoryProvider;

  private final Provider<HunterRepository> hunterRepositoryProvider;

  private SkillTreeViewModel_Factory(Provider<GetSkillsUseCase> getSkillsUseCaseProvider,
      Provider<CreateSkillUseCase> createSkillUseCaseProvider,
      Provider<AriseShadowUseCase> ariseShadowUseCaseProvider,
      Provider<GetMissionsUseCase> getMissionsUseCaseProvider,
      Provider<CeremonyEngine> ceremonyEngineProvider,
      Provider<AxiomPreferences> preferencesProvider,
      Provider<SystemFeedRepository> feedRepositoryProvider,
      Provider<SkillRepository> skillRepositoryProvider,
      Provider<HunterRepository> hunterRepositoryProvider) {
    this.getSkillsUseCaseProvider = getSkillsUseCaseProvider;
    this.createSkillUseCaseProvider = createSkillUseCaseProvider;
    this.ariseShadowUseCaseProvider = ariseShadowUseCaseProvider;
    this.getMissionsUseCaseProvider = getMissionsUseCaseProvider;
    this.ceremonyEngineProvider = ceremonyEngineProvider;
    this.preferencesProvider = preferencesProvider;
    this.feedRepositoryProvider = feedRepositoryProvider;
    this.skillRepositoryProvider = skillRepositoryProvider;
    this.hunterRepositoryProvider = hunterRepositoryProvider;
  }

  @Override
  public SkillTreeViewModel get() {
    return newInstance(getSkillsUseCaseProvider.get(), createSkillUseCaseProvider.get(), ariseShadowUseCaseProvider.get(), getMissionsUseCaseProvider.get(), ceremonyEngineProvider.get(), preferencesProvider.get(), feedRepositoryProvider.get(), skillRepositoryProvider.get(), hunterRepositoryProvider.get());
  }

  public static SkillTreeViewModel_Factory create(
      Provider<GetSkillsUseCase> getSkillsUseCaseProvider,
      Provider<CreateSkillUseCase> createSkillUseCaseProvider,
      Provider<AriseShadowUseCase> ariseShadowUseCaseProvider,
      Provider<GetMissionsUseCase> getMissionsUseCaseProvider,
      Provider<CeremonyEngine> ceremonyEngineProvider,
      Provider<AxiomPreferences> preferencesProvider,
      Provider<SystemFeedRepository> feedRepositoryProvider,
      Provider<SkillRepository> skillRepositoryProvider,
      Provider<HunterRepository> hunterRepositoryProvider) {
    return new SkillTreeViewModel_Factory(getSkillsUseCaseProvider, createSkillUseCaseProvider, ariseShadowUseCaseProvider, getMissionsUseCaseProvider, ceremonyEngineProvider, preferencesProvider, feedRepositoryProvider, skillRepositoryProvider, hunterRepositoryProvider);
  }

  public static SkillTreeViewModel newInstance(GetSkillsUseCase getSkillsUseCase,
      CreateSkillUseCase createSkillUseCase, AriseShadowUseCase ariseShadowUseCase,
      GetMissionsUseCase getMissionsUseCase, CeremonyEngine ceremonyEngine,
      AxiomPreferences preferences, SystemFeedRepository feedRepository,
      SkillRepository skillRepository, HunterRepository hunterRepository) {
    return new SkillTreeViewModel(getSkillsUseCase, createSkillUseCase, ariseShadowUseCase, getMissionsUseCase, ceremonyEngine, preferences, feedRepository, skillRepository, hunterRepository);
  }
}
