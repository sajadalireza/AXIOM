package com.axiom.app.domain.usecase;

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
public final class GetHunterProfileUseCase_Factory implements Factory<GetHunterProfileUseCase> {
  private final Provider<HunterRepository> repositoryProvider;

  private GetHunterProfileUseCase_Factory(Provider<HunterRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetHunterProfileUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetHunterProfileUseCase_Factory create(
      Provider<HunterRepository> repositoryProvider) {
    return new GetHunterProfileUseCase_Factory(repositoryProvider);
  }

  public static GetHunterProfileUseCase newInstance(HunterRepository repository) {
    return new GetHunterProfileUseCase(repository);
  }
}
