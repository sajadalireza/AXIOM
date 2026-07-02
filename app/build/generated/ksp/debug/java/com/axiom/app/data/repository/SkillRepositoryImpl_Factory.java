package com.axiom.app.data.repository;

import com.axiom.app.data.local.dao.SkillDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class SkillRepositoryImpl_Factory implements Factory<SkillRepositoryImpl> {
  private final Provider<SkillDao> skillDaoProvider;

  private SkillRepositoryImpl_Factory(Provider<SkillDao> skillDaoProvider) {
    this.skillDaoProvider = skillDaoProvider;
  }

  @Override
  public SkillRepositoryImpl get() {
    return newInstance(skillDaoProvider.get());
  }

  public static SkillRepositoryImpl_Factory create(Provider<SkillDao> skillDaoProvider) {
    return new SkillRepositoryImpl_Factory(skillDaoProvider);
  }

  public static SkillRepositoryImpl newInstance(SkillDao skillDao) {
    return new SkillRepositoryImpl(skillDao);
  }
}
