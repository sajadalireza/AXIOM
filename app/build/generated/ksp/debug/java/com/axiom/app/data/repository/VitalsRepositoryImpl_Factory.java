package com.axiom.app.data.repository;

import com.axiom.app.data.local.AxiomPreferences;
import com.axiom.app.data.local.dao.VitalLogDao;
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
public final class VitalsRepositoryImpl_Factory implements Factory<VitalsRepositoryImpl> {
  private final Provider<VitalLogDao> vitalLogDaoProvider;

  private final Provider<AxiomPreferences> preferencesProvider;

  private VitalsRepositoryImpl_Factory(Provider<VitalLogDao> vitalLogDaoProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    this.vitalLogDaoProvider = vitalLogDaoProvider;
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public VitalsRepositoryImpl get() {
    return newInstance(vitalLogDaoProvider.get(), preferencesProvider.get());
  }

  public static VitalsRepositoryImpl_Factory create(Provider<VitalLogDao> vitalLogDaoProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    return new VitalsRepositoryImpl_Factory(vitalLogDaoProvider, preferencesProvider);
  }

  public static VitalsRepositoryImpl newInstance(VitalLogDao vitalLogDao,
      AxiomPreferences preferences) {
    return new VitalsRepositoryImpl(vitalLogDao, preferences);
  }
}
