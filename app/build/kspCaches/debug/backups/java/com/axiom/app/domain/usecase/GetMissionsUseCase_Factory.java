package com.axiom.app.domain.usecase;

import com.axiom.app.domain.repository.MissionRepository;
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
public final class GetMissionsUseCase_Factory implements Factory<GetMissionsUseCase> {
  private final Provider<MissionRepository> repositoryProvider;

  private GetMissionsUseCase_Factory(Provider<MissionRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetMissionsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetMissionsUseCase_Factory create(Provider<MissionRepository> repositoryProvider) {
    return new GetMissionsUseCase_Factory(repositoryProvider);
  }

  public static GetMissionsUseCase newInstance(MissionRepository repository) {
    return new GetMissionsUseCase(repository);
  }
}
