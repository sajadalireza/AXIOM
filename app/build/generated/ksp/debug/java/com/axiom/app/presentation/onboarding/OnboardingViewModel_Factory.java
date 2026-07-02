package com.axiom.app.presentation.onboarding;

import com.axiom.app.data.SeedDataHelper;
import com.axiom.app.data.local.AxiomPreferences;
import com.axiom.app.domain.usecase.InitializeAxiomUseCase;
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
public final class OnboardingViewModel_Factory implements Factory<OnboardingViewModel> {
  private final Provider<InitializeAxiomUseCase> initializeAxiomUseCaseProvider;

  private final Provider<SeedDataHelper> seedDataHelperProvider;

  private final Provider<AxiomPreferences> preferencesProvider;

  private OnboardingViewModel_Factory(
      Provider<InitializeAxiomUseCase> initializeAxiomUseCaseProvider,
      Provider<SeedDataHelper> seedDataHelperProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    this.initializeAxiomUseCaseProvider = initializeAxiomUseCaseProvider;
    this.seedDataHelperProvider = seedDataHelperProvider;
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public OnboardingViewModel get() {
    return newInstance(initializeAxiomUseCaseProvider.get(), seedDataHelperProvider.get(), preferencesProvider.get());
  }

  public static OnboardingViewModel_Factory create(
      Provider<InitializeAxiomUseCase> initializeAxiomUseCaseProvider,
      Provider<SeedDataHelper> seedDataHelperProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    return new OnboardingViewModel_Factory(initializeAxiomUseCaseProvider, seedDataHelperProvider, preferencesProvider);
  }

  public static OnboardingViewModel newInstance(InitializeAxiomUseCase initializeAxiomUseCase,
      SeedDataHelper seedDataHelper, AxiomPreferences preferences) {
    return new OnboardingViewModel(initializeAxiomUseCase, seedDataHelper, preferences);
  }
}
