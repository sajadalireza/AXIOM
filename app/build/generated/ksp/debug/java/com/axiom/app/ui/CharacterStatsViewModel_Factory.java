package com.axiom.app.ui;

import com.axiom.app.data.local.AxiomPreferences;
import com.axiom.app.domain.usecase.GetHunterProfileUseCase;
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
public final class CharacterStatsViewModel_Factory implements Factory<CharacterStatsViewModel> {
  private final Provider<GetHunterProfileUseCase> getHunterProfileUseCaseProvider;

  private final Provider<AxiomPreferences> preferencesProvider;

  private CharacterStatsViewModel_Factory(
      Provider<GetHunterProfileUseCase> getHunterProfileUseCaseProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    this.getHunterProfileUseCaseProvider = getHunterProfileUseCaseProvider;
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public CharacterStatsViewModel get() {
    return newInstance(getHunterProfileUseCaseProvider.get(), preferencesProvider.get());
  }

  public static CharacterStatsViewModel_Factory create(
      Provider<GetHunterProfileUseCase> getHunterProfileUseCaseProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    return new CharacterStatsViewModel_Factory(getHunterProfileUseCaseProvider, preferencesProvider);
  }

  public static CharacterStatsViewModel newInstance(GetHunterProfileUseCase getHunterProfileUseCase,
      AxiomPreferences preferences) {
    return new CharacterStatsViewModel(getHunterProfileUseCase, preferences);
  }
}
