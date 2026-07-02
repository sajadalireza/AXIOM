package com.axiom.app.domain.usecase;

import com.axiom.app.domain.repository.SkillRepository;
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
public final class CreateSkillUseCase_Factory implements Factory<CreateSkillUseCase> {
  private final Provider<SkillRepository> repositoryProvider;

  private CreateSkillUseCase_Factory(Provider<SkillRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public CreateSkillUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static CreateSkillUseCase_Factory create(Provider<SkillRepository> repositoryProvider) {
    return new CreateSkillUseCase_Factory(repositoryProvider);
  }

  public static CreateSkillUseCase newInstance(SkillRepository repository) {
    return new CreateSkillUseCase(repository);
  }
}
