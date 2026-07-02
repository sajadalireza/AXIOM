package com.axiom.app.ui;

import com.axiom.app.core.XionEventBus;
import com.axiom.app.core.ai.SystemVoiceEngine;
import com.axiom.app.data.local.AxiomPreferences;
import com.axiom.app.domain.repository.HunterRepository;
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
public final class XionViewModel_Factory implements Factory<XionViewModel> {
  private final Provider<GetHunterProfileUseCase> getHunterProfileProvider;

  private final Provider<GetMissionsUseCase> getMissionsProvider;

  private final Provider<XionEventBus> eventBusProvider;

  private final Provider<SystemVoiceEngine> engineProvider;

  private final Provider<HunterRepository> hunterRepositoryProvider;

  private final Provider<AxiomPreferences> preferencesProvider;

  private XionViewModel_Factory(Provider<GetHunterProfileUseCase> getHunterProfileProvider,
      Provider<GetMissionsUseCase> getMissionsProvider, Provider<XionEventBus> eventBusProvider,
      Provider<SystemVoiceEngine> engineProvider,
      Provider<HunterRepository> hunterRepositoryProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    this.getHunterProfileProvider = getHunterProfileProvider;
    this.getMissionsProvider = getMissionsProvider;
    this.eventBusProvider = eventBusProvider;
    this.engineProvider = engineProvider;
    this.hunterRepositoryProvider = hunterRepositoryProvider;
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public XionViewModel get() {
    return newInstance(getHunterProfileProvider.get(), getMissionsProvider.get(), eventBusProvider.get(), engineProvider.get(), hunterRepositoryProvider.get(), preferencesProvider.get());
  }

  public static XionViewModel_Factory create(
      Provider<GetHunterProfileUseCase> getHunterProfileProvider,
      Provider<GetMissionsUseCase> getMissionsProvider, Provider<XionEventBus> eventBusProvider,
      Provider<SystemVoiceEngine> engineProvider,
      Provider<HunterRepository> hunterRepositoryProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    return new XionViewModel_Factory(getHunterProfileProvider, getMissionsProvider, eventBusProvider, engineProvider, hunterRepositoryProvider, preferencesProvider);
  }

  public static XionViewModel newInstance(GetHunterProfileUseCase getHunterProfile,
      GetMissionsUseCase getMissions, XionEventBus eventBus, SystemVoiceEngine engine,
      HunterRepository hunterRepository, AxiomPreferences preferences) {
    return new XionViewModel(getHunterProfile, getMissions, eventBus, engine, hunterRepository, preferences);
  }
}
