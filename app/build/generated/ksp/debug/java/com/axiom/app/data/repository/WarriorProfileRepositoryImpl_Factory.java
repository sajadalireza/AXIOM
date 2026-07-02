package com.axiom.app.data.repository;

import com.axiom.app.data.local.AxiomPreferences;
import com.axiom.app.data.local.dao.WarriorBlueprintDao;
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
public final class WarriorProfileRepositoryImpl_Factory implements Factory<WarriorProfileRepositoryImpl> {
  private final Provider<WarriorBlueprintDao> blueprintDaoProvider;

  private final Provider<AxiomPreferences> preferencesProvider;

  private WarriorProfileRepositoryImpl_Factory(Provider<WarriorBlueprintDao> blueprintDaoProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    this.blueprintDaoProvider = blueprintDaoProvider;
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public WarriorProfileRepositoryImpl get() {
    return newInstance(blueprintDaoProvider.get(), preferencesProvider.get());
  }

  public static WarriorProfileRepositoryImpl_Factory create(
      Provider<WarriorBlueprintDao> blueprintDaoProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    return new WarriorProfileRepositoryImpl_Factory(blueprintDaoProvider, preferencesProvider);
  }

  public static WarriorProfileRepositoryImpl newInstance(WarriorBlueprintDao blueprintDao,
      AxiomPreferences preferences) {
    return new WarriorProfileRepositoryImpl(blueprintDao, preferences);
  }
}
