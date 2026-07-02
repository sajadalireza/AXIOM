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
public final class StreakRepositoryImpl_Factory implements Factory<StreakRepositoryImpl> {
  private final Provider<AxiomPreferences> preferencesProvider;

  private StreakRepositoryImpl_Factory(Provider<AxiomPreferences> preferencesProvider) {
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public StreakRepositoryImpl get() {
    return newInstance(preferencesProvider.get());
  }

  public static StreakRepositoryImpl_Factory create(
      Provider<AxiomPreferences> preferencesProvider) {
    return new StreakRepositoryImpl_Factory(preferencesProvider);
  }

  public static StreakRepositoryImpl newInstance(AxiomPreferences preferences) {
    return new StreakRepositoryImpl(preferences);
  }
}
