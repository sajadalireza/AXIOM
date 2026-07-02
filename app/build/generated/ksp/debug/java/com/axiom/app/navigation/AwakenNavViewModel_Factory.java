package com.axiom.app.navigation;

import com.axiom.app.data.local.AxiomPreferences;
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
public final class AwakenNavViewModel_Factory implements Factory<AwakenNavViewModel> {
  private final Provider<AxiomPreferences> preferencesProvider;

  private AwakenNavViewModel_Factory(Provider<AxiomPreferences> preferencesProvider) {
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public AwakenNavViewModel get() {
    return newInstance(preferencesProvider.get());
  }

  public static AwakenNavViewModel_Factory create(Provider<AxiomPreferences> preferencesProvider) {
    return new AwakenNavViewModel_Factory(preferencesProvider);
  }

  public static AwakenNavViewModel newInstance(AxiomPreferences preferences) {
    return new AwakenNavViewModel(preferences);
  }
}
