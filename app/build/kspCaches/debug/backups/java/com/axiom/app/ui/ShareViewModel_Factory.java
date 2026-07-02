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
public final class ShareViewModel_Factory implements Factory<ShareViewModel> {
  private final Provider<GetHunterProfileUseCase> getHunterProfileUseCaseProvider;

  private final Provider<AxiomPreferences> preferencesProvider;

  private ShareViewModel_Factory(Provider<GetHunterProfileUseCase> getHunterProfileUseCaseProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    this.getHunterProfileUseCaseProvider = getHunterProfileUseCaseProvider;
    this.preferencesProvider = preferencesProvider;
  }

  @Override
  public ShareViewModel get() {
    return newInstance(getHunterProfileUseCaseProvider.get(), preferencesProvider.get());
  }

  public static ShareViewModel_Factory create(
      Provider<GetHunterProfileUseCase> getHunterProfileUseCaseProvider,
      Provider<AxiomPreferences> preferencesProvider) {
    return new ShareViewModel_Factory(getHunterProfileUseCaseProvider, preferencesProvider);
  }

  public static ShareViewModel newInstance(GetHunterProfileUseCase getHunterProfileUseCase,
      AxiomPreferences preferences) {
    return new ShareViewModel(getHunterProfileUseCase, preferences);
  }
}
