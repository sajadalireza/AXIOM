package com.axiom.app.ui;

import com.axiom.app.core.ai.SystemVoiceEngine;
import com.axiom.app.data.local.AxiomPreferences;
import com.axiom.app.domain.usecase.GetHunterProfileUseCase;
import com.axiom.app.domain.usecase.GetMissionsUseCase;
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
public final class SystemVoiceViewModel_Factory implements Factory<SystemVoiceViewModel> {
  private final Provider<SystemVoiceEngine> engineProvider;

  private final Provider<AxiomPreferences> preferencesProvider;

  private final Provider<GetHunterProfileUseCase> getHunterProfileProvider;

  private final Provider<GetMissionsUseCase> getMissionsProvider;

  private SystemVoiceViewModel_Factory(Provider<SystemVoiceEngine> engineProvider,
      Provider<AxiomPreferences> preferencesProvider,
      Provider<GetHunterProfileUseCase> getHunterProfileProvider,
      Provider<GetMissionsUseCase> getMissionsProvider) {
    this.engineProvider = engineProvider;
    this.preferencesProvider = preferencesProvider;
    this.getHunterProfileProvider = getHunterProfileProvider;
    this.getMissionsProvider = getMissionsProvider;
  }

  @Override
  public SystemVoiceViewModel get() {
    return newInstance(engineProvider.get(), preferencesProvider.get(), getHunterProfileProvider.get(), getMissionsProvider.get());
  }

  public static SystemVoiceViewModel_Factory create(Provider<SystemVoiceEngine> engineProvider,
      Provider<AxiomPreferences> preferencesProvider,
      Provider<GetHunterProfileUseCase> getHunterProfileProvider,
      Provider<GetMissionsUseCase> getMissionsProvider) {
    return new SystemVoiceViewModel_Factory(engineProvider, preferencesProvider, getHunterProfileProvider, getMissionsProvider);
  }

  public static SystemVoiceViewModel newInstance(SystemVoiceEngine engine,
      AxiomPreferences preferences, GetHunterProfileUseCase getHunterProfile,
      GetMissionsUseCase getMissions) {
    return new SystemVoiceViewModel(engine, preferences, getHunterProfile, getMissions);
  }
}
