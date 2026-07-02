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
public final class SaveMonthlyIncomeEntryUseCase_Factory implements Factory<SaveMonthlyIncomeEntryUseCase> {
  private final Provider<WarriorProfileRepository> repositoryProvider;

  private SaveMonthlyIncomeEntryUseCase_Factory(
      Provider<WarriorProfileRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public SaveMonthlyIncomeEntryUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static SaveMonthlyIncomeEntryUseCase_Factory create(
      Provider<WarriorProfileRepository> repositoryProvider) {
    return new SaveMonthlyIncomeEntryUseCase_Factory(repositoryProvider);
  }

  public static SaveMonthlyIncomeEntryUseCase newInstance(WarriorProfileRepository repository) {
    return new SaveMonthlyIncomeEntryUseCase(repository);
  }
}
