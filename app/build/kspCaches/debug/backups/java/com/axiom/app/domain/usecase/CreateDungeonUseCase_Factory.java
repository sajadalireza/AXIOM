package com.axiom.app.domain.usecase;

import com.axiom.app.domain.repository.DungeonRepository;
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
public final class CreateDungeonUseCase_Factory implements Factory<CreateDungeonUseCase> {
  private final Provider<DungeonRepository> repositoryProvider;

  private CreateDungeonUseCase_Factory(Provider<DungeonRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public CreateDungeonUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static CreateDungeonUseCase_Factory create(
      Provider<DungeonRepository> repositoryProvider) {
    return new CreateDungeonUseCase_Factory(repositoryProvider);
  }

  public static CreateDungeonUseCase newInstance(DungeonRepository repository) {
    return new CreateDungeonUseCase(repository);
  }
}
