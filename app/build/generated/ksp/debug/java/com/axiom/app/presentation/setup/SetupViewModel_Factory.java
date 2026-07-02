package com.axiom.app.presentation.setup;

import android.app.Application;
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
public final class SetupViewModel_Factory implements Factory<SetupViewModel> {
  private final Provider<Application> appProvider;

  private final Provider<AxiomPreferences> preferencesProvider;

  private SetupViewModel_Factory(Provider<Application> appProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    this.appProvider = appProvider;
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public SetupViewModel get() {
    return newInstance(appProvider.get(), preferencesProvider.get());
  }

  public static SetupViewModel_Factory create(Provider<Application> appProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    return new SetupViewModel_Factory(appProvider, preferencesProvider);
  }

  public static SetupViewModel newInstance(Application app, AxiomPreferences preferences) {
    return new SetupViewModel(app, preferences);
  }
}
