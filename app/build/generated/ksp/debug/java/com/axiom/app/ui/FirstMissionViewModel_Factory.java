package com.axiom.app.ui;

import com.axiom.app.data.local.AxiomPreferences;
import com.axiom.app.domain.usecase.CompleteMissionUseCase;
import com.axiom.app.domain.usecase.CreateMissionUseCase;
import com.axiom.app.domain.usecase.GetSkillsUseCase;
import com.axiom.app.presentation.ceremony.CeremonyEngine;
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
public final class FirstMissionViewModel_Factory implements Factory<FirstMissionViewModel> {
  private final Provider<CreateMissionUseCase> createMissionUseCaseProvider;

  private final Provider<CompleteMissionUseCase> completeMissionUseCaseProvider;

  private final Provider<AxiomPreferences> preferencesProvider;

  private final Provider<GetSkillsUseCase> getSkillsUseCaseProvider;

  private final Provider<CeremonyEngine> ceremonyEngineProvider;

  private FirstMissionViewModel_Factory(Provider<CreateMissionUseCase> createMissionUseCaseProvider,
      Provider<CompleteMissionUseCase> completeMissionUseCaseProvider,
      Provider<AxiomPreferences> preferencesProvider,
      Provider<GetSkillsUseCase> getSkillsUseCaseProvider,
      Provider<CeremonyEngine> ceremonyEngineProvider) {
    this.createMissionUseCaseProvider = createMissionUseCaseProvider;
    this.completeMissionUseCaseProvider = completeMissionUseCaseProvider;
    this.preferencesProvider = preferencesProvider;
    this.getSkillsUseCaseProvider = getSkillsUseCaseProvider;
    this.ceremonyEngineProvider = ceremonyEngineProvider;
  }

  @Override
  public FirstMissionViewModel get() {
    return newInstance(createMissionUseCaseProvider.get(), completeMissionUseCaseProvider.get(), preferencesProvider.get(), getSkillsUseCaseProvider.get(), ceremonyEngineProvider.get());
  }

  public static FirstMissionViewModel_Factory create(
      Provider<CreateMissionUseCase> createMissionUseCaseProvider,
      Provider<CompleteMissionUseCase> completeMissionUseCaseProvider,
      Provider<AxiomPreferences> preferencesProvider,
      Provider<GetSkillsUseCase> getSkillsUseCaseProvider,
      Provider<CeremonyEngine> ceremonyEngineProvider) {
    return new FirstMissionViewModel_Factory(createMissionUseCaseProvider, completeMissionUseCaseProvider, preferencesProvider, getSkillsUseCaseProvider, ceremonyEngineProvider);
  }

  public static FirstMissionViewModel newInstance(CreateMissionUseCase createMissionUseCase,
      CompleteMissionUseCase completeMissionUseCase, AxiomPreferences preferences,
      GetSkillsUseCase getSkillsUseCase, CeremonyEngine ceremonyEngine) {
    return new FirstMissionViewModel(createMissionUseCase, completeMissionUseCase, preferences, getSkillsUseCase, ceremonyEngine);
  }
}
