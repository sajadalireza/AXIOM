package com.axiom.app.domain.usecase;

import com.axiom.app.domain.repository.ShadowRepository;
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
public final class AriseShadowUseCase_Factory implements Factory<AriseShadowUseCase> {
  private final Provider<SkillRepository> skillRepositoryProvider;

  private final Provider<ShadowRepository> shadowRepositoryProvider;

  private AriseShadowUseCase_Factory(Provider<SkillRepository> skillRepositoryProvider,
      Provider<ShadowRepository> shadowRepositoryProvider) {
    this.skillRepositoryProvider = skillRepositoryProvider;
    this.shadowRepositoryProvider = shadowRepositoryProvider;
  }

  @Override
  public AriseShadowUseCase get() {
    return newInstance(skillRepositoryProvider.get(), shadowRepositoryProvider.get());
  }

  public static AriseShadowUseCase_Factory create(Provider<SkillRepository> skillRepositoryProvider,
      Provider<ShadowRepository> shadowRepositoryProvider) {
    return new AriseShadowUseCase_Factory(skillRepositoryProvider, shadowRepositoryProvider);
  }

  public static AriseShadowUseCase newInstance(SkillRepository skillRepository,
      ShadowRepository shadowRepository) {
    return new AriseShadowUseCase(skillRepository, shadowRepository);
  }
}
