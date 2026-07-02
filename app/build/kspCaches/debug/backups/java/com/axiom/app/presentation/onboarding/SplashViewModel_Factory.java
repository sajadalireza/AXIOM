package com.axiom.app.presentation.onboarding;

import com.axiom.app.data.local.AxiomPreferences;
import com.axiom.app.domain.repository.HunterRepository;
import com.axiom.app.domain.usecase.EnsureAnonymousSessionUseCase;
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
public final class SplashViewModel_Factory implements Factory<SplashViewModel> {
  private final Provider<HunterRepository> hunterRepositoryProvider;

  private final Provider<EnsureAnonymousSessionUseCase> ensureAnonymousSessionUseCaseProvider;

  private final Provider<AxiomPreferences> preferencesProvider;

  private SplashViewModel_Factory(Provider<HunterRepository> hunterRepositoryProvider,
      Provider<EnsureAnonymousSessionUseCase> ensureAnonymousSessionUseCaseProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    this.hunterRepositoryProvider = hunterRepositoryProvider;
    this.ensureAnonymousSessionUseCaseProvider = ensureAnonymousSessionUseCaseProvider;
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public SplashViewModel get() {
    return newInstance(hunterRepositoryProvider.get(), ensureAnonymousSessionUseCaseProvider.get(), preferencesProvider.get());
  }

  public static SplashViewModel_Factory create(Provider<HunterRepository> hunterRepositoryProvider,
      Provider<EnsureAnonymousSessionUseCase> ensureAnonymousSessionUseCaseProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    return new SplashViewModel_Factory(hunterRepositoryProvider, ensureAnonymousSessionUseCaseProvider, preferencesProvider);
  }

  public static SplashViewModel newInstance(HunterRepository hunterRepository,
      EnsureAnonymousSessionUseCase ensureAnonymousSessionUseCase, AxiomPreferences preferences) {
    return new SplashViewModel(hunterRepository, ensureAnonymousSessionUseCase, preferences);
  }
}
