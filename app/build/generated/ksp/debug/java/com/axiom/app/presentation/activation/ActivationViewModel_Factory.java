package com.axiom.app.presentation.activation;

import com.axiom.app.data.local.AxiomPreferences;
import com.axiom.app.domain.repository.ActivationRepository;
import com.axiom.app.domain.repository.CloudSyncRepository;
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
public final class ActivationViewModel_Factory implements Factory<ActivationViewModel> {
  private final Provider<ActivationRepository> activationRepositoryProvider;

  private final Provider<AxiomPreferences> preferencesProvider;

  private final Provider<CloudSyncRepository> cloudSyncRepositoryProvider;

  private ActivationViewModel_Factory(Provider<ActivationRepository> activationRepositoryProvider,
      Provider<AxiomPreferences> preferencesProvider,
      Provider<CloudSyncRepository> cloudSyncRepositoryProvider) {
    this.activationRepositoryProvider = activationRepositoryProvider;
    this.preferencesProvider = preferencesProvider;
    this.cloudSyncRepositoryProvider = cloudSyncRepositoryProvider;
  }

  @Override
  public ActivationViewModel get() {
    return newInstance(activationRepositoryProvider.get(), preferencesProvider.get(), cloudSyncRepositoryProvider.get());
  }

  public static ActivationViewModel_Factory create(
      Provider<ActivationRepository> activationRepositoryProvider,
      Provider<AxiomPreferences> preferencesProvider,
      Provider<CloudSyncRepository> cloudSyncRepositoryProvider) {
    return new ActivationViewModel_Factory(activationRepositoryProvider, preferencesProvider, cloudSyncRepositoryProvider);
  }

  public static ActivationViewModel newInstance(ActivationRepository activationRepository,
      AxiomPreferences preferences, CloudSyncRepository cloudSyncRepository) {
    return new ActivationViewModel(activationRepository, preferences, cloudSyncRepository);
  }
}
