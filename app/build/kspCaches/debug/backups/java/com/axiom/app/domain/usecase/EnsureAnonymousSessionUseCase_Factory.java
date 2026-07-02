package com.axiom.app.domain.usecase;

import com.axiom.app.domain.repository.ActivationRepository;
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
public final class EnsureAnonymousSessionUseCase_Factory implements Factory<EnsureAnonymousSessionUseCase> {
  private final Provider<ActivationRepository> activationRepositoryProvider;

  private EnsureAnonymousSessionUseCase_Factory(
      Provider<ActivationRepository> activationRepositoryProvider) {
    this.activationRepositoryProvider = activationRepositoryProvider;
  }

  @Override
  public EnsureAnonymousSessionUseCase get() {
    return newInstance(activationRepositoryProvider.get());
  }

  public static EnsureAnonymousSessionUseCase_Factory create(
      Provider<ActivationRepository> activationRepositoryProvider) {
    return new EnsureAnonymousSessionUseCase_Factory(activationRepositoryProvider);
  }

  public static EnsureAnonymousSessionUseCase newInstance(
      ActivationRepository activationRepository) {
    return new EnsureAnonymousSessionUseCase(activationRepository);
  }
}
