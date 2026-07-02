package com.axiom.app.data.repository;

import com.axiom.app.data.local.AxiomDatabase;
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
public final class CloudSyncRepositoryImpl_Factory implements Factory<CloudSyncRepositoryImpl> {
  private final Provider<AxiomDatabase> databaseProvider;

  private final Provider<AxiomPreferences> preferencesProvider;

  private CloudSyncRepositoryImpl_Factory(Provider<AxiomDatabase> databaseProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    this.databaseProvider = databaseProvider;
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public CloudSyncRepositoryImpl get() {
    return newInstance(databaseProvider.get(), preferencesProvider.get());
  }

  public static CloudSyncRepositoryImpl_Factory create(Provider<AxiomDatabase> databaseProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    return new CloudSyncRepositoryImpl_Factory(databaseProvider, preferencesProvider);
  }

  public static CloudSyncRepositoryImpl newInstance(AxiomDatabase database,
      AxiomPreferences preferences) {
    return new CloudSyncRepositoryImpl(database, preferences);
  }
}
