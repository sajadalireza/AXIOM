package com.axiom.app.presentation.ceremony;

import com.axiom.app.data.local.AxiomPreferences;
import com.axiom.app.domain.repository.HunterRepository;
import com.axiom.app.domain.usecase.CreateMissionUseCase;
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
public final class CeremonyViewModel_Factory implements Factory<CeremonyViewModel> {
  private final Provider<CeremonyEngine> ceremonyEngineProvider;

  private final Provider<CreateMissionUseCase> createMissionUseCaseProvider;

  private final Provider<HunterRepository> hunterRepositoryProvider;

  private final Provider<AxiomPreferences> preferencesProvider;

  private CeremonyViewModel_Factory(Provider<CeremonyEngine> ceremonyEngineProvider,
      Provider<CreateMissionUseCase> createMissionUseCaseProvider,
      Provider<HunterRepository> hunterRepositoryProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    this.ceremonyEngineProvider = ceremonyEngineProvider;
    this.createMissionUseCaseProvider = createMissionUseCaseProvider;
    this.hunterRepositoryProvider = hunterRepositoryProvider;
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public CeremonyViewModel get() {
    return newInstance(ceremonyEngineProvider.get(), createMissionUseCaseProvider.get(), hunterRepositoryProvider.get(), preferencesProvider.get());
  }

  public static CeremonyViewModel_Factory create(Provider<CeremonyEngine> ceremonyEngineProvider,
      Provider<CreateMissionUseCase> createMissionUseCaseProvider,
      Provider<HunterRepository> hunterRepositoryProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    return new CeremonyViewModel_Factory(ceremonyEngineProvider, createMissionUseCaseProvider, hunterRepositoryProvider, preferencesProvider);
  }

  public static CeremonyViewModel newInstance(CeremonyEngine ceremonyEngine,
      CreateMissionUseCase createMissionUseCase, HunterRepository hunterRepository,
      AxiomPreferences preferences) {
    return new CeremonyViewModel(ceremonyEngine, createMissionUseCase, hunterRepository, preferences);
  }
}
