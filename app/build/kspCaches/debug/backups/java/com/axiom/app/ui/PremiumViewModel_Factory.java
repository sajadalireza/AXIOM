package com.axiom.app.ui;

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
public final class PremiumViewModel_Factory implements Factory<PremiumViewModel> {
  private final Provider<AxiomPreferences> preferencesProvider;

  private PremiumViewModel_Factory(Provider<AxiomPreferences> preferencesProvider) {
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public PremiumViewModel get() {
    return newInstance(preferencesProvider.get());
  }

  public static PremiumViewModel_Factory create(Provider<AxiomPreferences> preferencesProvider) {
    return new PremiumViewModel_Factory(preferencesProvider);
  }

  public static PremiumViewModel newInstance(AxiomPreferences preferences) {
    return new PremiumViewModel(preferences);
  }
}
