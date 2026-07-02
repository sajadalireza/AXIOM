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
public final class GetFinancialDataUseCase_Factory implements Factory<GetFinancialDataUseCase> {
  private final Provider<WarriorProfileRepository> repositoryProvider;

  private GetFinancialDataUseCase_Factory(Provider<WarriorProfileRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetFinancialDataUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetFinancialDataUseCase_Factory create(
      Provider<WarriorProfileRepository> repositoryProvider) {
    return new GetFinancialDataUseCase_Factory(repositoryProvider);
  }

  public static GetFinancialDataUseCase newInstance(WarriorProfileRepository repository) {
    return new GetFinancialDataUseCase(repository);
  }
}
