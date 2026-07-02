package com.axiom.app.domain.usecase;

import com.axiom.app.data.local.AxiomPreferences;
import com.axiom.app.domain.repository.HunterRepository;
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
public final class GrantDailyLoginBonusUseCase_Factory implements Factory<GrantDailyLoginBonusUseCase> {
  private final Provider<AxiomPreferences> preferencesProvider;

  private final Provider<HunterRepository> hunterRepositoryProvider;

  private GrantDailyLoginBonusUseCase_Factory(Provider<AxiomPreferences> preferencesProvider,
      Provider<HunterRepository> hunterRepositoryProvider) {
    this.preferencesProvider = preferencesProvider;
    this.hunterRepositoryProvider = hunterRepositoryProvider;
  }

  @Override
  public GrantDailyLoginBonusUseCase get() {
    return newInstance(preferencesProvider.get(), hunterRepositoryProvider.get());
  }

  public static GrantDailyLoginBonusUseCase_Factory create(
      Provider<AxiomPreferences> preferencesProvider,
      Provider<HunterRepository> hunterRepositoryProvider) {
    return new GrantDailyLoginBonusUseCase_Factory(preferencesProvider, hunterRepositoryProvider);
  }

  public static GrantDailyLoginBonusUseCase newInstance(AxiomPreferences preferences,
      HunterRepository hunterRepository) {
    return new GrantDailyLoginBonusUseCase(preferences, hunterRepository);
  }
}
