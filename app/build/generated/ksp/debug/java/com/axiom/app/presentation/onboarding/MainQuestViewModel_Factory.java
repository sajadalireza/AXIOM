package com.axiom.app.presentation.onboarding;

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
public final class MainQuestViewModel_Factory implements Factory<MainQuestViewModel> {
  private final Provider<HunterRepository> hunterRepositoryProvider;

  private MainQuestViewModel_Factory(Provider<HunterRepository> hunterRepositoryProvider) {
    this.hunterRepositoryProvider = hunterRepositoryProvider;
  }

  @Override
  public MainQuestViewModel get() {
    return newInstance(hunterRepositoryProvider.get());
  }

  public static MainQuestViewModel_Factory create(
      Provider<HunterRepository> hunterRepositoryProvider) {
    return new MainQuestViewModel_Factory(hunterRepositoryProvider);
  }

  public static MainQuestViewModel newInstance(HunterRepository hunterRepository) {
    return new MainQuestViewModel(hunterRepository);
  }
}
