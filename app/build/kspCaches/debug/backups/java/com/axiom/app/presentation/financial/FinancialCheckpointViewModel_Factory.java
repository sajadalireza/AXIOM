package com.axiom.app.presentation.financial;

import com.axiom.app.domain.usecase.GetFinancialDataUseCase;
import com.axiom.app.domain.usecase.SaveFinancialCheckpointUseCase;
import com.axiom.app.domain.usecase.SaveMonthlyIncomeEntryUseCase;
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
public final class FinancialCheckpointViewModel_Factory implements Factory<FinancialCheckpointViewModel> {
  private final Provider<GetFinancialDataUseCase> getFinancialDataUseCaseProvider;

  private final Provider<SaveFinancialCheckpointUseCase> saveFinancialCheckpointUseCaseProvider;

  private final Provider<SaveMonthlyIncomeEntryUseCase> saveMonthlyIncomeEntryUseCaseProvider;

  private FinancialCheckpointViewModel_Factory(
      Provider<GetFinancialDataUseCase> getFinancialDataUseCaseProvider,
      Provider<SaveFinancialCheckpointUseCase> saveFinancialCheckpointUseCaseProvider,
      Provider<SaveMonthlyIncomeEntryUseCase> saveMonthlyIncomeEntryUseCaseProvider) {
    this.getFinancialDataUseCaseProvider = getFinancialDataUseCaseProvider;
    this.saveFinancialCheckpointUseCaseProvider = saveFinancialCheckpointUseCaseProvider;
    this.saveMonthlyIncomeEntryUseCaseProvider = saveMonthlyIncomeEntryUseCaseProvider;
  }

  @Override
  public FinancialCheckpointViewModel get() {
    return newInstance(getFinancialDataUseCaseProvider.get(), saveFinancialCheckpointUseCaseProvider.get(), saveMonthlyIncomeEntryUseCaseProvider.get());
  }

  public static FinancialCheckpointViewModel_Factory create(
      Provider<GetFinancialDataUseCase> getFinancialDataUseCaseProvider,
      Provider<SaveFinancialCheckpointUseCase> saveFinancialCheckpointUseCaseProvider,
      Provider<SaveMonthlyIncomeEntryUseCase> saveMonthlyIncomeEntryUseCaseProvider) {
    return new FinancialCheckpointViewModel_Factory(getFinancialDataUseCaseProvider, saveFinancialCheckpointUseCaseProvider, saveMonthlyIncomeEntryUseCaseProvider);
  }

  public static FinancialCheckpointViewModel newInstance(
      GetFinancialDataUseCase getFinancialDataUseCase,
      SaveFinancialCheckpointUseCase saveFinancialCheckpointUseCase,
      SaveMonthlyIncomeEntryUseCase saveMonthlyIncomeEntryUseCase) {
    return new FinancialCheckpointViewModel(getFinancialDataUseCase, saveFinancialCheckpointUseCase, saveMonthlyIncomeEntryUseCase);
  }
}
