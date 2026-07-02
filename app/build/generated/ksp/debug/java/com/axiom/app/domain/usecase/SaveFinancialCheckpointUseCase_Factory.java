package com.axiom.app.domain.usecase;

import com.axiom.app.domain.repository.WarriorProfileRepository;
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
public final class SaveFinancialCheckpointUseCase_Factory implements Factory<SaveFinancialCheckpointUseCase> {
  private final Provider<WarriorProfileRepository> repositoryProvider;

  private SaveFinancialCheckpointUseCase_Factory(
      Provider<WarriorProfileRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public SaveFinancialCheckpointUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static SaveFinancialCheckpointUseCase_Factory create(
      Provider<WarriorProfileRepository> repositoryProvider) {
    return new SaveFinancialCheckpointUseCase_Factory(repositoryProvider);
  }

  public static SaveFinancialCheckpointUseCase newInstance(WarriorProfileRepository repository) {
    return new SaveFinancialCheckpointUseCase(repository);
  }
}
