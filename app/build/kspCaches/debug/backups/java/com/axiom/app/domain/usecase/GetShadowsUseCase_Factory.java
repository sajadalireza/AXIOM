package com.axiom.app.domain.usecase;

import com.axiom.app.domain.repository.ShadowRepository;
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
public final class GetShadowsUseCase_Factory implements Factory<GetShadowsUseCase> {
  private final Provider<ShadowRepository> repositoryProvider;

  private GetShadowsUseCase_Factory(Provider<ShadowRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetShadowsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetShadowsUseCase_Factory create(Provider<ShadowRepository> repositoryProvider) {
    return new GetShadowsUseCase_Factory(repositoryProvider);
  }

  public static GetShadowsUseCase newInstance(ShadowRepository repository) {
    return new GetShadowsUseCase(repository);
  }
}
