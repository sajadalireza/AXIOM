package com.axiom.app.data.repository;

import com.axiom.app.data.local.AxiomPreferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class ActivationRepositoryImpl_Factory implements Factory<ActivationRepositoryImpl> {
  private final Provider<AxiomPreferences> preferencesProvider;

  private ActivationRepositoryImpl_Factory(Provider<AxiomPreferences> preferencesProvider) {
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public ActivationRepositoryImpl get() {
    return newInstance(preferencesProvider.get());
  }

  public static ActivationRepositoryImpl_Factory create(
      Provider<AxiomPreferences> preferencesProvider) {
    return new ActivationRepositoryImpl_Factory(preferencesProvider);
  }

  public static ActivationRepositoryImpl newInstance(AxiomPreferences preferences) {
    return new ActivationRepositoryImpl(preferences);
  }
}
