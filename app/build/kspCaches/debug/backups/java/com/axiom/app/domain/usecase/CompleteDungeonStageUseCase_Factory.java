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
public final class CompleteDungeonStageUseCase_Factory implements Factory<CompleteDungeonStageUseCase> {
  private final Provider<DungeonRepository> repositoryProvider;

  private CompleteDungeonStageUseCase_Factory(Provider<DungeonRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public CompleteDungeonStageUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static CompleteDungeonStageUseCase_Factory create(
      Provider<DungeonRepository> repositoryProvider) {
    return new CompleteDungeonStageUseCase_Factory(repositoryProvider);
  }

  public static CompleteDungeonStageUseCase newInstance(DungeonRepository repository) {
    return new CompleteDungeonStageUseCase(repository);
  }
}
