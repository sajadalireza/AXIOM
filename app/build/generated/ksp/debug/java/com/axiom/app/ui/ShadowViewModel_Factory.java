package com.axiom.app.ui;

import com.axiom.app.data.local.AxiomPreferences;
import com.axiom.app.domain.repository.SystemFeedRepository;
import com.axiom.app.domain.usecase.AriseShadowUseCase;
import com.axiom.app.domain.usecase.GetHunterProfileUseCase;
import com.axiom.app.domain.usecase.GetMissionsUseCase;
import com.axiom.app.domain.usecase.GetShadowsUseCase;
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
public final class ShadowViewModel_Factory implements Factory<ShadowViewModel> {
  private final Provider<GetShadowsUseCase> getShadowsUseCaseProvider;

  private final Provider<GetSkillsUseCase> getSkillsUseCaseProvider;

  private final Provider<AriseShadowUseCase> ariseShadowUseCaseProvider;

  private final Provider<GetHunterProfileUseCase> getHunterProfileUseCaseProvider;

  private final Provider<GetMissionsUseCase> getMissionsUseCaseProvider;

  private final Provider<AxiomPreferences> preferencesProvider;

  private final Provider<SystemFeedRepository> feedRepositoryProvider;

  private ShadowViewModel_Factory(Provider<GetShadowsUseCase> getShadowsUseCaseProvider,
      Provider<GetSkillsUseCase> getSkillsUseCaseProvider,
      Provider<AriseShadowUseCase> ariseShadowUseCaseProvider,
      Provider<GetHunterProfileUseCase> getHunterProfileUseCaseProvider,
      Provider<GetMissionsUseCase> getMissionsUseCaseProvider,
      Provider<AxiomPreferences> preferencesProvider,
      Provider<SystemFeedRepository> feedRepositoryProvider) {
    this.getShadowsUseCaseProvider = getShadowsUseCaseProvider;
    this.getSkillsUseCaseProvider = getSkillsUseCaseProvider;
    this.ariseShadowUseCaseProvider = ariseShadowUseCaseProvider;
    this.getHunterProfileUseCaseProvider = getHunterProfileUseCaseProvider;
    this.getMissionsUseCaseProvider = getMissionsUseCaseProvider;
    this.preferencesProvider = preferencesProvider;
    this.feedRepositoryProvider = feedRepositoryProvider;
  }

  @Override
  public ShadowViewModel get() {
    return newInstance(getShadowsUseCaseProvider.get(), getSkillsUseCaseProvider.get(), ariseShadowUseCaseProvider.get(), getHunterProfileUseCaseProvider.get(), getMissionsUseCaseProvider.get(), preferencesProvider.get(), feedRepositoryProvider.get());
  }

  public static ShadowViewModel_Factory create(
      Provider<GetShadowsUseCase> getShadowsUseCaseProvider,
      Provider<GetSkillsUseCase> getSkillsUseCaseProvider,
      Provider<AriseShadowUseCase> ariseShadowUseCaseProvider,
      Provider<GetHunterProfileUseCase> getHunterProfileUseCaseProvider,
      Provider<GetMissionsUseCase> getMissionsUseCaseProvider,
      Provider<AxiomPreferences> preferencesProvider,
      Provider<SystemFeedRepository> feedRepositoryProvider) {
    return new ShadowViewModel_Factory(getShadowsUseCaseProvider, getSkillsUseCaseProvider, ariseShadowUseCaseProvider, getHunterProfileUseCaseProvider, getMissionsUseCaseProvider, preferencesProvider, feedRepositoryProvider);
  }

  public static ShadowViewModel newInstance(GetShadowsUseCase getShadowsUseCase,
      GetSkillsUseCase getSkillsUseCase, AriseShadowUseCase ariseShadowUseCase,
      GetHunterProfileUseCase getHunterProfileUseCase, GetMissionsUseCase getMissionsUseCase,
      AxiomPreferences preferences, SystemFeedRepository feedRepository) {
    return new ShadowViewModel(getShadowsUseCase, getSkillsUseCase, ariseShadowUseCase, getHunterProfileUseCase, getMissionsUseCase, preferences, feedRepository);
  }
}
