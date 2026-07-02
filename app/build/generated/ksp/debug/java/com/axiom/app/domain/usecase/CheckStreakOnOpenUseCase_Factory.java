package com.axiom.app.domain.usecase;

import com.axiom.app.data.local.AxiomPreferences;
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
public final class CheckStreakOnOpenUseCase_Factory implements Factory<CheckStreakOnOpenUseCase> {
  private final Provider<AxiomPreferences> preferencesProvider;

  private final Provider<CeremonyEngine> ceremonyEngineProvider;

  private final Provider<SystemFeedRepository> feedRepositoryProvider;

  private CheckStreakOnOpenUseCase_Factory(Provider<AxiomPreferences> preferencesProvider,
      Provider<CeremonyEngine> ceremonyEngineProvider,
      Provider<SystemFeedRepository> feedRepositoryProvider) {
    this.preferencesProvider = preferencesProvider;
    this.ceremonyEngineProvider = ceremonyEngineProvider;
    this.feedRepositoryProvider = feedRepositoryProvider;
  }

  @Override
  public CheckStreakOnOpenUseCase get() {
    return newInstance(preferencesProvider.get(), ceremonyEngineProvider.get(), feedRepositoryProvider.get());
  }

  public static CheckStreakOnOpenUseCase_Factory create(
      Provider<AxiomPreferences> preferencesProvider,
      Provider<CeremonyEngine> ceremonyEngineProvider,
      Provider<SystemFeedRepository> feedRepositoryProvider) {
    return new CheckStreakOnOpenUseCase_Factory(preferencesProvider, ceremonyEngineProvider, feedRepositoryProvider);
  }

  public static CheckStreakOnOpenUseCase newInstance(AxiomPreferences preferences,
      CeremonyEngine ceremonyEngine, SystemFeedRepository feedRepository) {
    return new CheckStreakOnOpenUseCase(preferences, ceremonyEngine, feedRepository);
  }
}
