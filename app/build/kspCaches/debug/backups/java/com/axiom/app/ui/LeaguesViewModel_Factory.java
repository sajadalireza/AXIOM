package com.axiom.app.ui;

import com.axiom.app.data.local.AxiomPreferences;
import com.axiom.app.domain.focus.FocusProtocolManager;
import com.axiom.app.domain.repository.ActivationRepository;
import com.axiom.app.domain.repository.LeagueRepository;
import com.axiom.app.domain.usecase.CompleteMissionUseCase;
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
public final class LeaguesViewModel_Factory implements Factory<LeaguesViewModel> {
  private final Provider<GetMissionsUseCase> getMissionsUseCaseProvider;

  private final Provider<GetHunterProfileUseCase> getHunterProfileUseCaseProvider;

  private final Provider<CompleteMissionUseCase> completeMissionUseCaseProvider;

  private final Provider<AxiomPreferences> preferencesProvider;

  private final Provider<LeagueRepository> leagueRepositoryProvider;

  private final Provider<FocusProtocolManager> focusProtocolManagerProvider;

  private final Provider<ActivationRepository> activationRepositoryProvider;

  private LeaguesViewModel_Factory(Provider<GetMissionsUseCase> getMissionsUseCaseProvider,
      Provider<GetHunterProfileUseCase> getHunterProfileUseCaseProvider,
      Provider<CompleteMissionUseCase> completeMissionUseCaseProvider,
      Provider<AxiomPreferences> preferencesProvider,
      Provider<LeagueRepository> leagueRepositoryProvider,
      Provider<FocusProtocolManager> focusProtocolManagerProvider,
      Provider<ActivationRepository> activationRepositoryProvider) {
    this.getMissionsUseCaseProvider = getMissionsUseCaseProvider;
    this.getHunterProfileUseCaseProvider = getHunterProfileUseCaseProvider;
    this.completeMissionUseCaseProvider = completeMissionUseCaseProvider;
    this.preferencesProvider = preferencesProvider;
    this.leagueRepositoryProvider = leagueRepositoryProvider;
    this.focusProtocolManagerProvider = focusProtocolManagerProvider;
    this.activationRepositoryProvider = activationRepositoryProvider;
  }

  @Override
  public LeaguesViewModel get() {
    return newInstance(getMissionsUseCaseProvider.get(), getHunterProfileUseCaseProvider.get(), completeMissionUseCaseProvider.get(), preferencesProvider.get(), leagueRepositoryProvider.get(), focusProtocolManagerProvider.get(), activationRepositoryProvider.get());
  }

  public static LeaguesViewModel_Factory create(
      Provider<GetMissionsUseCase> getMissionsUseCaseProvider,
      Provider<GetHunterProfileUseCase> getHunterProfileUseCaseProvider,
      Provider<CompleteMissionUseCase> completeMissionUseCaseProvider,
      Provider<AxiomPreferences> preferencesProvider,
      Provider<LeagueRepository> leagueRepositoryProvider,
      Provider<FocusProtocolManager> focusProtocolManagerProvider,
      Provider<ActivationRepository> activationRepositoryProvider) {
    return new LeaguesViewModel_Factory(getMissionsUseCaseProvider, getHunterProfileUseCaseProvider, completeMissionUseCaseProvider, preferencesProvider, leagueRepositoryProvider, focusProtocolManagerProvider, activationRepositoryProvider);
  }

  public static LeaguesViewModel newInstance(GetMissionsUseCase getMissionsUseCase,
      GetHunterProfileUseCase getHunterProfileUseCase,
      CompleteMissionUseCase completeMissionUseCase, AxiomPreferences preferences,
      LeagueRepository leagueRepository, FocusProtocolManager focusProtocolManager,
      ActivationRepository activationRepository) {
    return new LeaguesViewModel(getMissionsUseCase, getHunterProfileUseCase, completeMissionUseCase, preferences, leagueRepository, focusProtocolManager, activationRepository);
  }
}
