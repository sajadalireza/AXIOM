package com.axiom.app.domain.usecase;

import com.axiom.app.data.local.AxiomPreferences;
import com.axiom.app.domain.repository.VitalsRepository;
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
public final class BurnoutMonitorUseCase_Factory implements Factory<BurnoutMonitorUseCase> {
  private final Provider<VitalsRepository> vitalsRepositoryProvider;

  private final Provider<AxiomPreferences> preferencesProvider;

  private BurnoutMonitorUseCase_Factory(Provider<VitalsRepository> vitalsRepositoryProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    this.vitalsRepositoryProvider = vitalsRepositoryProvider;
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public BurnoutMonitorUseCase get() {
    return newInstance(vitalsRepositoryProvider.get(), preferencesProvider.get());
  }

  public static BurnoutMonitorUseCase_Factory create(
      Provider<VitalsRepository> vitalsRepositoryProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    return new BurnoutMonitorUseCase_Factory(vitalsRepositoryProvider, preferencesProvider);
  }

  public static BurnoutMonitorUseCase newInstance(VitalsRepository vitalsRepository,
      AxiomPreferences preferences) {
    return new BurnoutMonitorUseCase(vitalsRepository, preferences);
  }
}
