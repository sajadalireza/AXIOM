package com.axiom.app.ui;

import com.axiom.app.data.local.AxiomPreferences;
import com.axiom.app.domain.repository.VitalsRepository;
import com.axiom.app.domain.usecase.BurnoutMonitorUseCase;
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
public final class VitalsViewModel_Factory implements Factory<VitalsViewModel> {
  private final Provider<VitalsRepository> vitalsRepositoryProvider;

  private final Provider<AxiomPreferences> preferencesProvider;

  private final Provider<BurnoutMonitorUseCase> burnoutMonitorUseCaseProvider;

  private VitalsViewModel_Factory(Provider<VitalsRepository> vitalsRepositoryProvider,
      Provider<AxiomPreferences> preferencesProvider,
      Provider<BurnoutMonitorUseCase> burnoutMonitorUseCaseProvider) {
    this.vitalsRepositoryProvider = vitalsRepositoryProvider;
    this.preferencesProvider = preferencesProvider;
    this.burnoutMonitorUseCaseProvider = burnoutMonitorUseCaseProvider;
  }

  @Override
  public VitalsViewModel get() {
    return newInstance(vitalsRepositoryProvider.get(), preferencesProvider.get(), burnoutMonitorUseCaseProvider.get());
  }

  public static VitalsViewModel_Factory create(Provider<VitalsRepository> vitalsRepositoryProvider,
      Provider<AxiomPreferences> preferencesProvider,
      Provider<BurnoutMonitorUseCase> burnoutMonitorUseCaseProvider) {
    return new VitalsViewModel_Factory(vitalsRepositoryProvider, preferencesProvider, burnoutMonitorUseCaseProvider);
  }

  public static VitalsViewModel newInstance(VitalsRepository vitalsRepository,
      AxiomPreferences preferences, BurnoutMonitorUseCase burnoutMonitorUseCase) {
    return new VitalsViewModel(vitalsRepository, preferences, burnoutMonitorUseCase);
  }
}
